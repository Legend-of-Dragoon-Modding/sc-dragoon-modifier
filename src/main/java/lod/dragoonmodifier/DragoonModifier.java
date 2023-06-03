package lod.dragoonmodifier;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import legend.core.GameEngine;
import legend.game.SItem;
import legend.game.SMap;
import legend.game.Scus94491BpeSegment_8004;
import legend.game.Scus94491BpeSegment_8006;
import legend.game.characters.Element;
import legend.game.characters.ElementSet;
import legend.game.characters.VitalsStat;
import legend.game.combat.Bttl_800c;
import legend.game.combat.bobj.AttackEvent;
import legend.game.combat.bobj.BattleObject27c;
import legend.game.combat.bobj.PlayerBattleObject;
import legend.game.combat.environment.BattlePreloadedEntities_18cb0;
import legend.game.combat.types.AttackType;
import legend.game.combat.types.CombatantStruct1a8;
import legend.game.input.InputAction;
import legend.game.modding.Mod;
import legend.game.modding.coremod.CoreMod;
import legend.game.modding.events.EventListener;
import legend.game.modding.events.battle.*;
import legend.game.modding.events.characters.*;
import legend.game.modding.events.config.ConfigLoadedEvent;
import legend.game.modding.events.gamestate.GameLoadedEvent;
import legend.game.modding.events.input.InputPressedEvent;
import legend.game.modding.events.input.InputReleasedEvent;
import legend.game.modding.events.inventory.RepeatItemReturnEvent;
import legend.game.modding.events.inventory.ShopItemEvent;
import legend.game.modding.events.inventory.ShopSellPriceEvent;
import legend.game.modding.registries.Registrar;
import legend.game.modding.registries.RegistryDelegate;
import legend.game.saves.ConfigEntry;
import legend.game.saves.ConfigRegistryEvent;
import legend.game.scripting.ScriptState;
import legend.game.types.*;
import org.apache.commons.lang3.ArrayUtils;

import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static legend.game.SMap.FUN_800e5534;
import static legend.game.SMap.smapLoadingStage_800cb430;
import static legend.game.Scus94491BpeSegment_8004.mainCallbackIndex_8004dd20;
import static legend.game.Scus94491BpeSegment_8005.submapCut_80052c30;
import static legend.game.Scus94491BpeSegment_8006.battleState_8006e398;
import static legend.game.Scus94491BpeSegment_800b.*;
import static legend.game.WMap.*;
import static legend.game.combat.Bttl_800c.allBobjCount_800c66d0;
import static legend.game.combat.Bttl_800c.dragoonSpaceElement_800c6b64;

@Mod(id = DragoonModifier.MOD_ID)
public class DragoonModifier {
    public static final String MOD_ID = "dragoon-modifier";

    private GameState52c gameState;
    public final List<String[]> monsterStats = new ArrayList<>();
    public final List<String[]> monstersRewardsStats = new ArrayList<>();
    public final List<String[]> additionStats = new ArrayList<>();
    public final List<String[]> additionMultiStats = new ArrayList<>();
    public final List<String[]> additionUnlockStats = new ArrayList<>();
    public final List<String[]> characterStats = new ArrayList<>();
    public final List<String[]> dragoonStats = new ArrayList<>();
    public final List<String[]> xpNextStats = new ArrayList<>();
    public final List<String[]> spellStats = new ArrayList<>();
    public final List<String[]> equipStats = new ArrayList<>();
    public final List<String[]> itemStats = new ArrayList<>();
    public final List<String[]> shopItems = new ArrayList<>();
    public final List<String[]> shopPrices = new ArrayList<>();

    public final Registrar<ConfigEntry<?>, ConfigRegistryEvent> CSV_CONFIG_REGISTRAR = new Registrar<>(GameEngine.REGISTRIES.config, MOD_ID);
    public final RegistryDelegate<ConfigDifficultyEntry> DIFFICULTY = CSV_CONFIG_REGISTRAR.register("difficulty", ConfigDifficultyEntry::new);
    public final RegistryDelegate<ConfigFaustDefeated> FAUST_DEFEATED = CSV_CONFIG_REGISTRAR.register("faust_defeated", ConfigFaustDefeated::new);

    /*public final RegistryDelegate<ConfigEnrageMode> ENRAGE_MODE = CSV_CONFIG_REGISTRAR.register("enrage_mode", ConfigEnrageMode::new);
    public final RegistryDelegate<ConfigElementalBomb> ELEMENTAL_BOMB = CSV_CONFIG_REGISTRAR.register("elemental_bomb", ConfigElementalBomb::new);
    public final RegistryDelegate<ConfigNeverGuard> NEVER_GUARD = CSV_CONFIG_REGISTRAR.register("never_guard", ConfigNeverGuard::new);
    public final RegistryDelegate<ConfigTurnBattleMode> TURN_BATTLE = CSV_CONFIG_REGISTRAR.register("turn_battle", ConfigTurnBattleMode::new);
    public final RegistryDelegate<ConfigUltimateBoss> ULTIMATE_BOSS = CSV_CONFIG_REGISTRAR.register("ultimate_boss", ConfigUltimateBoss::new);
    public inal RegistryDelegate<ConfigUltimateBossDefeated> ULTIMATE_BOSS_DEFEATED = CSV_CONFIG_REGISTRAR.register("ultimate_boss_defeated", ConfigUltimateBossDefeated::new);
    */

    public Set<InputAction> hotkey = new HashSet<>();

    public boolean burnStackMode = false;
    public int burnStacks = 0;
    public double dmgPerBurn = 0.1;
    public int burnStacksMax = 0;
    public double maxBurnAddition = 1;
    public final int burnStackFlameShot = 1;
    public final int burnStackExplosion = 2;
    public final int burnStackFinalBurst = 3;
    public final int burnStackRedEye = 4;
    public final int burnStackAddition = 1;
    public boolean burnAdded = false;
    public boolean faustBattle = false;
    public int armorOfLegendTurns = 0;
    public int legendCasqueTurns = 0;


    public DragoonModifier() {
        GameEngine.EVENTS.register(this);
    }

    @EventListener
    public void registerConfig(final ConfigRegistryEvent event) {
        CSV_CONFIG_REGISTRAR.registryEvent(event);
    }

    @EventListener
    public void configLoaded(final ConfigLoadedEvent event) {
        if(event.storageLocation == DIFFICULTY.get().storageLocation) {
            System.out.println("[Dragoon Modifier] Config Loaded Event");
            loadAllCsvs(event.configCollection.getConfig(DIFFICULTY.get()));
        }
    }

    @EventListener
    public void difficultyChanged(final DifficultyChangedEvent event) {
        this.loadAllCsvs(event.difficulty);

        if(this.gameState != null) {
            this.overwriteTables();
        }
    }

    public List<String[]> loadCSV(String path) {
        try (FileReader fr = new FileReader(path, StandardCharsets.UTF_8);
             CSVReader csv = new CSVReader(fr)) {
            List<String[]> list = csv.readAll();
            list.remove(0);
            return list;
        } catch (IOException | CsvException e) {
            throw new RuntimeException(e);
        }
    }

    private void loadCsvIntoList(final String difficulty, final List<String[]> list, final String file) {
        list.clear();
        list.addAll(loadCSV("./mods/csvstat/" + difficulty + '/' + file));
    }

    private void loadAllCsvs(final String difficulty) {
        this.loadCsvIntoList(difficulty, monsterStats, "scdk-monster-stats.csv");
        this.loadCsvIntoList(difficulty, monstersRewardsStats, "scdk-monster-rewards.csv");
        this.loadCsvIntoList(difficulty, additionStats, "scdk-addition-stats.csv");
        this.loadCsvIntoList(difficulty, additionUnlockStats, "scdk-addition-unlock-levels.csv");
        this.loadCsvIntoList(difficulty, additionMultiStats, "scdk-addition-multiplier-stats.csv");
        this.loadCsvIntoList(difficulty, characterStats, "scdk-character-stats.csv");
        this.loadCsvIntoList(difficulty, dragoonStats, "scdk-dragoon-stats.csv");
        this.loadCsvIntoList(difficulty, xpNextStats, "scdk-exp-table.csv");
        this.loadCsvIntoList(difficulty, spellStats, "scdk-spell-stats.csv");
        this.loadCsvIntoList(difficulty, equipStats, "scdk-equip-stats.csv");
        this.loadCsvIntoList(difficulty, itemStats, "scdk-thrown-item-stats.csv");
        this.loadCsvIntoList(difficulty, shopItems, "scdk-shop-items.csv");
        this.loadCsvIntoList(difficulty, shopPrices, "scdk-shop-prices.csv");

        System.out.println("[Dragoon Modifier] Loaded using directory: " + difficulty);
    }

    @EventListener
    public void enemyRewards(final EnemyRewardsEvent enemyRewards) {
        int enemyId = enemyRewards.enemyId;
        enemyRewards.clear();
        enemyRewards.xp = Integer.parseInt(monstersRewardsStats.get(enemyId)[0]);
        enemyRewards.gold = Integer.parseInt(monstersRewardsStats.get(enemyId)[1]);
        enemyRewards.add(new CombatantStruct1a8.ItemDrop(Integer.parseInt(monstersRewardsStats.get(enemyId)[2]), Integer.parseInt(monstersRewardsStats.get(enemyId)[3])));
        if (faustBattle && enemyRewards.enemyId == 344) {
            enemyRewards.clear();
            enemyRewards.xp = 30000;
            enemyRewards.gold = 250;
            if (Integer.parseInt(GameEngine.CONFIG.getConfig(FAUST_DEFEATED.get())) == 39) {
                enemyRewards.add(new CombatantStruct1a8.ItemDrop(100, 74));
                enemyRewards.add(new CombatantStruct1a8.ItemDrop(100, 89));
            }
        }
    }

    @EventListener
    public void enemyStats(final MonsterStatsEvent enemyStats) {
        int ovrId = enemyStats.enemyId;
        enemyStats.hp = Integer.parseInt(monsterStats.get(ovrId)[1]);
        enemyStats.maxHp = Integer.parseInt(monsterStats.get(ovrId)[1]);
        enemyStats.attack = Integer.parseInt(monsterStats.get(ovrId)[3]);
        enemyStats.magicAttack = Integer.parseInt(monsterStats.get(ovrId)[4]);
        enemyStats.speed = Integer.parseInt(monsterStats.get(ovrId)[5]);
        enemyStats.defence = Integer.parseInt(monsterStats.get(ovrId)[6]);
        enemyStats.magicDefence = Integer.parseInt(monsterStats.get(ovrId)[7]);
        enemyStats.attackAvoid = Integer.parseInt(monsterStats.get(ovrId)[8]);
        enemyStats.magicAvoid = Integer.parseInt(monsterStats.get(ovrId)[9]);
        enemyStats.specialEffectFlag = Integer.parseInt(monsterStats.get(ovrId)[10]);
        enemyStats.elementFlag = Element.fromFlag(Integer.parseInt(monsterStats.get(ovrId)[12]));
        enemyStats.elementalImmunityFlag.clear();
        if (Integer.parseInt(monsterStats.get(ovrId)[13]) > 0)
            enemyStats.elementalImmunityFlag.add(Element.fromFlag(Integer.parseInt(monsterStats.get(ovrId)[13])));
        enemyStats.statusResistFlag = Integer.parseInt(monsterStats.get(ovrId)[14]);
    }

    @EventListener
    public void additionStats(final BattleMapActiveAdditionHitPropertiesEvent addition) {
        int additionId = addition.additionHits.hits_00[0].audioFile_0c;
        for (int i = 0; i < 8; i++) {
            final BattlePreloadedEntities_18cb0.AdditionHitProperties20 hit = addition.additionHits.hits_00[i];
            //hit.flags_00 = Short.parseShort(additionStats.get(additionId * 8 + i)[0]);
            //hit.totalFrames_02 = Short.parseShort(additionStats.get(additionId * 8 + i)[1]);
            //hit.overlayHitFrameOffset_04 = Short.parseShort(additionStats.get(additionId * 8 + i)[2]);
            //hit.totalSuccessFrames_06 = Short.parseShort(additionStats.get(additionId * 8 + i)[3]);
            hit.damageMultiplier_08 = Short.parseShort(additionStats.get(additionId * 8 + i)[4]);
            hit.spValue_0a = Short.parseShort(additionStats.get(additionId * 8 + i)[5]);
            //hit.audioFile_0c = Short.parseShort(additionStats.get(additionId * 8 + i)[6]);
            //hit.isFinalHit_0e = Short.parseShort(additionStats.get(additionId * 8 + i)[7]);
            //hit._10 = Short.parseShort(additionStats.get(additionId * 8 + i)[8]);
            //hit._12 = Short.parseShort(additionStats.get(additionId * 8 + i)[9]);
            //hit._14 = Short.parseShort(additionStats.get(additionId * 8 + i)[10]);
            //hit.hitDistanceFromTarget_16 = Short.parseShort(additionStats.get(additionId * 8 + i)[11]);
            //hit.framesToHitPosition_18 = Short.parseShort(additionStats.get(additionId * 8 + i)[12]);
            //hit._1a = Short.parseShort(additionStats.get(additionId * 8 + i)[13]);
            //hit.framesPostFailure_1c = Short.parseShort(additionStats.get(additionId * 8 + i)[14]);
            //hit.overlayStartingFrameOffset_1e = Short.parseShort(additionStats.get(additionId * 8 + i)[15]);
        }
    }

    @EventListener
    public void additionMulti(final AdditionHitMultiplierEvent multiplier) {
        multiplier.additionSpMulti = Integer.parseInt(additionMultiStats.get(multiplier.additionId)[(multiplier.additionLevel - 1) * 4]);
        multiplier.additionDmgMulti = Integer.parseInt(additionMultiStats.get(multiplier.additionId)[(multiplier.additionLevel - 1) * 4 + 1]);
    }

    @EventListener
    public void additionUnlock(final AdditionUnlockEvent unlock) {
        unlock.additionLevel = Integer.parseInt(additionUnlockStats.get(unlock.additionId)[0]);
    }

    @EventListener
    public void characterStats(final CharacterStatsEvent character) {
        character.maxHp = Short.parseShort(characterStats.get(character.characterId * 61 + character.level)[5]);
        character.bodySpeed = Short.parseShort(characterStats.get(character.characterId * 61 + character.level)[0]);
        character.bodyAttack = Short.parseShort(characterStats.get(character.characterId * 61 + character.level)[1]);
        character.bodyMagicAttack = Short.parseShort(characterStats.get(character.characterId * 61 + character.level)[2]);
        character.bodyDefence = Short.parseShort(characterStats.get(character.characterId * 61 + character.level)[3]);
        character.bodyMagicDefence = Short.parseShort(characterStats.get(character.characterId * 61 + character.level)[4]);

        if (character.dlevel > 0) {
            character.maxMp = Integer.parseInt(dragoonStats.get(character.characterId * 6 + character.dlevel)[0]);
            character.dragoonAttack = Integer.parseInt(dragoonStats.get(character.characterId * 6 + character.dlevel)[3]);
            character.dragoonMagicAttack = Integer.parseInt(dragoonStats.get(character.characterId * 6 + character.dlevel)[4]);
            character.dragoonDefence = Integer.parseInt(dragoonStats.get(character.characterId * 6 + character.dlevel)[5]);
            character.dragoonMagicDefence = Integer.parseInt(dragoonStats.get(character.characterId * 6 + character.dlevel)[6]);
        }
    }

    @EventListener
    public void xpNext(final XpToLevelEvent exp) {
        exp.xp = Integer.parseInt(xpNextStats.get(exp.charId * 61 + exp.level)[0]);
    }

    @EventListener
    public void spellStats(final SpellStatsEvent spell) {
        int spellId = spell.spellId;

        final String difficulty = GameEngine.CONFIG.getConfig(DIFFICULTY.get());

        if (difficulty.equals("Hard Mode") || difficulty.equals("US + Hard Mode")) {
            dramodBurnStacks(spellId);
        }
    }

    @EventListener
    public void shopItem(final ShopItemEvent shopItem) {
        shopItem.itemId = Integer.parseInt(shopItems.get(shopItem.shopId)[shopItem.slotId]);
        shopItem.price = Integer.parseInt(shopPrices.get(shopItem.itemId)[0]) * 2;
    }

    @EventListener
    public void shopSell(final ShopSellPriceEvent shopItem) {
        shopItem.price = Integer.parseInt(shopPrices.get(shopItem.itemId)[0]);
    }

    @EventListener
    public void repeatItems(final RepeatItemReturnEvent item) {
        final String difficulty = GameEngine.CONFIG.getConfig(DIFFICULTY.get());

        if (difficulty.equals("Japan Demo")) {
            item.returnItem = item.itemId == 250;
        }
    }

    @EventListener
    public void attack(final AttackEvent attack) {
        if (attack.attacker instanceof PlayerBattleObject && attack.attackType == AttackType.DRAGOON_MAGIC_STATUS_ITEMS) {
            if (!ArrayUtils.contains(new int[]{1, 2, 4, 8, 16, 32, 64, 128}, Integer.parseInt(spellStats.get(attack.attacker.spellId_4e)[3])) && Integer.parseInt(spellStats.get(attack.attacker.spellId_4e)[4]) == 0) {
                attack.damage *= (Integer.parseInt(spellStats.get(attack.attacker.spellId_4e)[3]) / 100d);
            }
        }

        final String difficulty = GameEngine.CONFIG.getConfig(DIFFICULTY.get());

        if (difficulty.equals("Hard Mode") || difficulty.equals("US + Hard Mode")) {
            if (attack.attacker instanceof PlayerBattleObject player) {
                if (player.isDragoon() && attack.attackType.isPhysical() && player.element == dragoonSpaceElement_800c6b64) {
                    attack.damage *= 1.5;
                }
            }

            if (attack.attacker instanceof PlayerBattleObject player && attack.attacker.charId_272 == 0) {
                if (burnStackMode) {
                    if (burnStacks == burnStacksMax) {
                        if (player.spellId_4e == 0) {
                            attack.damage *= (1 + (burnStacks * dmgPerBurn)) * Integer.parseInt(spellStats.get(2)[3]) / Integer.parseInt(spellStats.get(0)[3]);
                        } else if (player.spellId_4e == 1)  {
                            attack.damage *= (1 + (burnStacks * dmgPerBurn)) * Integer.parseInt(spellStats.get(3)[3]) / Integer.parseInt(spellStats.get(1)[3]);
                        } else {
                            attack.damage *= 1 + (burnStacks * dmgPerBurn);
                        }
                    } else {
                        attack.damage *= 1 + (burnStacks * dmgPerBurn);
                    }
                    burnStacks = 0;
                    burnStackMode = false;
                } else {
                    if (attack.attackType == AttackType.DRAGOON_MAGIC_STATUS_ITEMS && !burnAdded) {
                        if (player.spellId_4e == 0) {
                            addBurnStacks(player, burnStackFlameShot);
                        } else if (player.spellId_4e == 1) {
                            addBurnStacks(player, burnStackExplosion);
                        } else if (player.spellId_4e == 2) {
                            addBurnStacks(player, burnStackFinalBurst);
                        } else if (player.spellId_4e == 3) {
                            addBurnStacks(player, burnStackRedEye);
                        }
                        burnAdded = true;
                    } else if (attack.attackType == AttackType.PHYSICAL && player.isDragoon()) {
                        addBurnStacks(player, burnStackAddition);
                        burnAdded = true;
                    }
                }
            }

            if (attack.attacker instanceof PlayerBattleObject player && attack.attacker.charId_272 == 3) {
                if (player.spellId_4e == 15) {
                    for(int i = 0; i < allBobjCount_800c66d0.get(); i++) {
                        final ScriptState<? extends BattleObject27c> state = battleState_8006e398.allBobjs_e0c[i];
                        final BattleObject27c bobj = state.innerStruct_00;
                        if (bobj instanceof PlayerBattleObject) {
                            PlayerBattleObject playerHealed = (PlayerBattleObject) bobj;
                            int playerHealedHP = bobj.stats.getStat(CoreMod.HP_STAT.get()).getCurrent();
                            int roseMaxHP = player.stats.getStat(CoreMod.HP_STAT.get()).getMax();
                            if (playerHealedHP > 0) {
                                bobj.stats.getStat(CoreMod.HP_STAT.get()).setCurrent((int) Math.min(bobj.stats.getStat(CoreMod.HP_STAT.get()).getMax(), (playerHealedHP + Math.round(roseMaxHP * player.dlevel_06 * 0.05d))));
                            }
                        }
                    }
                } else if (player.spellId_4e == 19) {
                    player.stats.getStat(CoreMod.HP_STAT.get()).setCurrent((int) Math.min(player.stats.getStat(CoreMod.HP_STAT.get()).getMax(),  player.stats.getStat(CoreMod.HP_STAT.get()).getCurrent() + attack.damage * 0.1d));
                }
            }
        }
    }

    @EventListener
    public void battleStarted(final BattleStartedEvent battleStarted) {
        if (faustBattle) {
            final ScriptState<? extends BattleObject27c> state = battleState_8006e398.allBobjs_e0c[0];
            final BattleObject27c bobj = state.innerStruct_00;
            final VitalsStat hp = bobj.stats.getStat(CoreMod.HP_STAT.get());
            hp.setCurrent(25600);
            hp.setMaxRaw(25600);
            bobj.attack_34 = 125;
            bobj.magicAttack_36 = 125;
            bobj.defence_38 = 75;
            bobj.magicDefence_3a = 200;
        }

        final String difficulty = GameEngine.CONFIG.getConfig(DIFFICULTY.get());

        if (difficulty.equals("Hard Mode") || difficulty.equals("US + Hard Mode")) {
            for(int i = 0; i < allBobjCount_800c66d0.get(); i++) {
                final ScriptState<? extends BattleObject27c> state = battleState_8006e398.allBobjs_e0c[i];
                final BattleObject27c bobj = state.innerStruct_00;
                if (bobj instanceof PlayerBattleObject) {
                    PlayerBattleObject player = (PlayerBattleObject) bobj;
                    player.equipmentElementalImmunity_22.clear();

                    int level = player.level_04;
                    if (player.charId_272 == 2 || player.charId_272 == 8) { //Shana AT Boost

                        double boost = 1;
                        if (player.equipment0_11e == 32) {
                            boost = 1.4;
                        } else if (level >= 28) {
                            boost = 2.15;
                        } else if (level >= 20) {
                            boost = 1.9;
                        } else if (level >= 10) {
                            boost = 1.6;
                        }

                        player.attack_34 = (int) Math.round(player.attack_34 * boost);

                        if (level >= 30) {
                            player.defence_38 = (int) Math.round(player.defence_38 * 1.12d);
                        }
                    }

                    if (player.charId_272 == 3 && level >= 30) { //Rose
                        player.defence_38 = (int) Math.round(player.defence_38 * 1.1d);
                    }

                    if (player.charId_272 == 6 && level >= 30) { //Meru
                        player.defence_38 = (int) Math.round(player.defence_38 * 1.26d);
                    }

                    if (player.charId_272 == 7) { //Kongol
                        final ActiveStatsa0 stats = stats_800be5f8[player.charId_272];
                        player.stats.getStat(CoreMod.SPEED_STAT.get()).setRaw(stats.bodySpeed_69 + (int) Math.round(stats.equipmentSpeed_86 / 2d));
                    }

                    if (player.equipment4_126 == 149) { //Phantom Shield
                        player.defence_38 = (int) Math.round(player.defence_38 * 0.6d);
                        player.magicDefence_3a = (int) Math.round(player.magicDefence_3a * 0.6d);
                    }

                    if (player.equipment4_126 == 150) { // Dragon Shield
                        player.defence_38 = (int) Math.round(player.defence_38 * 0.6d);
                    }

                    if (player.equipment4_126 == 150) { // Angel Scarf
                        player.magicDefence_3a = (int) Math.round(player.magicDefence_3a * 0.6d);
                    }
                }
            }
        }

        burnStacks = 0;
        armorOfLegendTurns = 0;
        legendCasqueTurns = 0;
        burnStackMode = false;
    }

    @EventListener
    public void battleEnded(final BattleEndedEvent battleEnded) {
        if (faustBattle) {
            faustBattle = false;
            try {
                GameEngine.CONFIG.setConfig(FAUST_DEFEATED.get(), String.valueOf(Integer.parseInt(GameEngine.CONFIG.getConfig(FAUST_DEFEATED.get())) + 1));
            } catch (NumberFormatException ex) {
                GameEngine.CONFIG.setConfig(FAUST_DEFEATED.get(), String.valueOf(1));
            }
            System.out.println("[Dragoon Modifier] Faust Defeated: " + GameEngine.CONFIG.getConfig(FAUST_DEFEATED.get()));
        }
    }

    @EventListener
    public void inputPressed(final InputPressedEvent input) {
        hotkey.add(input.inputAction);

        dramodHotkeys();
    }

    @EventListener
    public void inputReleased(final InputReleasedEvent input) {
        hotkey.remove(input.inputAction);
    }

    @EventListener
    public void gameLoaded(final GameLoadedEvent game) {
        this.gameState = game.gameState;
        this.loadAllCsvs(GameEngine.CONFIG.getConfig(DIFFICULTY.get()));
        this.overwriteTables();
    }

    private void overwriteTables() {
        System.out.println("[Dragoon Modifier] [Game Loaded] ...");
        for(int i = 0; i < spellStats.size(); i++) {
            Bttl_800c.spellStats_800fa0b8[i] = new SpellStats0c(spellStats.get(i)[12],
              spellStats.get(i)[13],
              Integer.parseInt(spellStats.get(i)[0]),
              Integer.parseInt(spellStats.get(i)[1]),
              Integer.parseInt(spellStats.get(i)[2]),
              Integer.parseInt(spellStats.get(i)[3]),
              Integer.parseInt(spellStats.get(i)[4]),
              Integer.parseInt(spellStats.get(i)[5]),
              Integer.parseInt(spellStats.get(i)[6]),
              Integer.parseInt(spellStats.get(i)[7]),
              Element.fromFlag(Integer.parseInt(spellStats.get(i)[8])),
              Integer.parseInt(spellStats.get(i)[9]),
              Integer.parseInt(spellStats.get(i)[10]),
              Integer.parseInt(spellStats.get(i)[11]));
        }

        for (int i = 0; i < 192; i++) {
            ElementSet elementalResistance = new ElementSet();
            ElementSet elementalImmunity = new ElementSet();
            int special1 = Integer.parseInt(equipStats.get(i)[11]);
            int special2 = Integer.parseInt(equipStats.get(i)[12]);
            int specialAmount = Integer.parseInt(equipStats.get(i)[13]);
            int mpPerMagicalHit = (special1 & 0x1) != 0 ? specialAmount : 0;
            int spPerMagicalHit = (special1 & 0x2) != 0 ? specialAmount : 0;
            int mpPerPhysicalHit = (special1 & 0x4) != 0 ? specialAmount : 0;
            int spPerPhysicalHit = (special1 & 0x8) != 0 ? specialAmount : 0;
            int spMultiplier = (special1 & 0x10) != 0 ? specialAmount : 0;
            boolean physicalResistance = (special1 & 0x20) != 0;
            boolean magicalImmunity = (special1 & 0x40) != 0;
            boolean physicalImmunity = (special1 & 0x80) != 0;
            int mpMultiplier = (special2 & 0x1) != 0 ? specialAmount : 0;
            int hpMultiplier = (special2 & 0x2) != 0 ? specialAmount : 0;
            boolean magicalResistance = (special2 & 0x4) != 0;
            int revive = (special2 & 0x8) != 0 ? specialAmount : 0;
            int spRegen = (special2 & 0x10) != 0 ? specialAmount : 0;
            int mpRegen = (special2 & 0x20) != 0 ? specialAmount : 0;
            int hpRegen = (special2 & 0x40) != 0 ? specialAmount : 0;
            int special2Flag80 = (special2 & 0x80) != 0 ? specialAmount : 0;

            if (Integer.parseInt(equipStats.get(i)[6]) > 0)
                elementalResistance.add(Element.fromFlag(Integer.parseInt(equipStats.get(i)[6])));
            if (Integer.parseInt(equipStats.get(i)[7]) > 0)
                elementalImmunity.add(Element.fromFlag(Integer.parseInt(equipStats.get(i)[7])));

            SItem.equipmentStats_80111ff0[i] = new EquipmentStats1c(
                    equipStats.get(i)[28],
                    equipStats.get(i)[29].replace('\u00A7', '\n'),
                    Integer.parseInt(equipStats.get(i)[0]),
                    Integer.parseInt(equipStats.get(i)[1]),
                    Integer.parseInt(equipStats.get(i)[2]),
                    Integer.parseInt(equipStats.get(i)[3]),
                    Element.fromFlag(Integer.parseInt(equipStats.get(i)[4])),
                    Integer.parseInt(equipStats.get(i)[5]),
                    elementalResistance,
                    elementalImmunity,
                    Integer.parseInt(equipStats.get(i)[8]),
                    Integer.parseInt(equipStats.get(i)[9]),
                    0,
                    mpPerPhysicalHit,
                    spPerPhysicalHit,
                    mpPerMagicalHit,
                    spPerMagicalHit,
                    hpMultiplier,
                    mpMultiplier,
                    spMultiplier,
                    SItem.equipmentStats_80111ff0[i].magicalResistance,
                    SItem.equipmentStats_80111ff0[i].physicalResistance,
                    SItem.equipmentStats_80111ff0[i].magicalImmunity,
                    SItem.equipmentStats_80111ff0[i].physicalImmunity,
                    revive,
                    hpRegen,
                    mpRegen,
                    spRegen,
                    SItem.equipmentStats_80111ff0[i].special2Flag80,
                    Integer.parseInt(equipStats.get(i)[14]),
                    Integer.parseInt(equipStats.get(i)[15]),
                    Integer.parseInt(equipStats.get(i)[16]) + Integer.parseInt(equipStats.get(i)[10]),
                    Integer.parseInt(equipStats.get(i)[17]),
                    Integer.parseInt(equipStats.get(i)[18]),
                    Integer.parseInt(equipStats.get(i)[19]),
                    Integer.parseInt(equipStats.get(i)[20]),
                    Integer.parseInt(equipStats.get(i)[21]),
                    Integer.parseInt(equipStats.get(i)[22]),
                    Integer.parseInt(equipStats.get(i)[23]),
                    Integer.parseInt(equipStats.get(i)[24]),
                    Integer.parseInt(equipStats.get(i)[25]),
                    Integer.parseInt(equipStats.get(i)[26]),
                    Integer.parseInt(equipStats.get(i)[27])
            );
        }

        for (int i = 0; i < 64; i++) {
            int special1 = Integer.parseInt(itemStats.get(i)[3]);
            int special2 = Integer.parseInt(itemStats.get(i)[4]);
            int specialAmount = Integer.parseInt(itemStats.get(i)[6]);
            int powerDefence = (special1 & 0x80) != 0 ? specialAmount : 0;
            int powerMagicDefence = (special1 & 0x40) != 0 ? specialAmount : 0;
            int powerAttack = (special1 & 0x20) != 0 ? specialAmount : 0;
            int powerMagicAttack = (special1 & 0x10) != 0 ? specialAmount : 0;
            int powerAttackHit = (special1 & 0x8) != 0 ? specialAmount : 0;
            int powerMagicAttackHit = (special1 & 0x4) != 0 ? specialAmount : 0;
            int powerAttackAvoid = (special1 & 0x2) != 0 ? specialAmount : 0;
            int powerMagicAttackAvoid = (special1 & 0x1) != 0 ? specialAmount : 0;
            boolean physicalImmunity = (special2 & 0x80) != 0;
            boolean magicalImmunity = (special2 & 0x40) != 0;
            int speedUp = (special2 & 0x20) != 0 ? 100 : 0;
            int speedDown = (special2 & 0x10) != 0 ? -50 : 0;
            int spPerPhysicalHit = (special2 & 0x8) != 0 ? specialAmount : 0;
            int mpPerPhysicalHit = (special2 & 0x4) != 0 ? specialAmount : 0;
            int spPerMagicalHit = (special2 & 0x2) != 0 ? specialAmount : 0;
            int mpPerMagicalHit = (special2 & 0x1) != 0 ? specialAmount : 0;

            Scus94491BpeSegment_8004.itemStats_8004f2ac[i] = new ItemStats0c(
                    itemStats.get(i)[12],
                    itemStats.get(i)[13].replace('\u00A7', '\n'),
                    itemStats.get(i)[14],
                    Integer.parseInt(itemStats.get(i)[0]),
                    Scus94491BpeSegment_8004.itemStats_8004f2ac[i].element_01,
                    Integer.parseInt(itemStats.get(i)[2]),
                    powerDefence,
                    powerMagicDefence,
                    powerAttack,
                    powerMagicAttack,
                    powerAttackHit,
                    powerMagicAttackHit,
                    powerAttackAvoid,
                    powerMagicAttackAvoid,
                    Scus94491BpeSegment_8004.itemStats_8004f2ac[i].physicalImmunity,
                    Scus94491BpeSegment_8004.itemStats_8004f2ac[i].magicalImmunity,
                    speedUp,
                    speedDown,
                    spPerPhysicalHit,
                    mpPerPhysicalHit,
                    spPerMagicalHit,
                    mpPerMagicalHit,
                    Integer.parseInt(itemStats.get(i)[5]),
                    Integer.parseInt(itemStats.get(i)[7]),
                    Integer.parseInt(itemStats.get(i)[8]),
                    Integer.parseInt(itemStats.get(i)[9]),
                    Integer.parseInt(itemStats.get(i)[10]),
                    Integer.parseInt(itemStats.get(i)[11])
            );
        }

        System.out.println("[Dragoon Modifier] [Game Loaded] Done");
    }

    @EventListener
    public void bobjTurn(final BattleObjectTurnEvent<?> turn) {
        final String difficulty = GameEngine.CONFIG.getConfig(DIFFICULTY.get());

        if (difficulty.equals("Hard Mode") || difficulty.equals("US + Hard Mode")) {
            if (turn.bobj instanceof PlayerBattleObject player) {
                if (player.equipment2_122 == 74) {
                    armorOfLegendTurns += 1;
                    if (armorOfLegendTurns % 2 == 0 && armorOfLegendTurns <= 80) {
                        player.defence_38 += 1;
                    }
                }

                if (player.equipment1_120 == 89) {
                    legendCasqueTurns += 1;
                    if (legendCasqueTurns % 2 == 0 && legendCasqueTurns <= 80) {
                        player.magicDefence_3a += 1;
                    }
                }

                if (player.charId_272 == 0) {
                    burnAdded = false;
                }
            }
        }
    }

    @EventListener
    public void dragonBlockStaffOn(final DragonBlockStaffOnEvent event) {
        final String difficulty = GameEngine.CONFIG.getConfig(DIFFICULTY.get());
        if (difficulty.equals("Hard Mode") || difficulty.equals("US + Hard Mode")) {
            for(int i = 0; i < allBobjCount_800c66d0.get(); i++) {
                final ScriptState<? extends BattleObject27c> state = battleState_8006e398.allBobjs_e0c[i];
                final BattleObject27c bobj = state.innerStruct_00;
                if (bobj instanceof PlayerBattleObject) {
                    PlayerBattleObject player = (PlayerBattleObject) bobj;
                    player.dragoonAttack_ac = Integer.parseInt(dragoonStats.get(player.charId_272 * 6 + player.dlevel_06)[3]) * 8;
                    player.dragoonMagic_ae = Integer.parseInt(dragoonStats.get(player.charId_272 * 6 + player.dlevel_06)[4]) * 8;
                    player.dragoonDefence_b0 = Integer.parseInt(dragoonStats.get(player.charId_272 * 6 + player.dlevel_06)[5]) * 8;
                    player.dragoonMagicDefence_b2 = Integer.parseInt(dragoonStats.get(player.charId_272 * 6 + player.dlevel_06)[6]) * 8;
                }
            }
        }
    }

    @EventListener
    public void dragonBlockStaffOff(final DragonBlockStaffOffEvent event) {
        final String difficulty = GameEngine.CONFIG.getConfig(DIFFICULTY.get());
        if (difficulty.equals("Hard Mode") || difficulty.equals("US + Hard Mode")) {
            for(int i = 0; i < allBobjCount_800c66d0.get(); i++) {
                final ScriptState<? extends BattleObject27c> state = battleState_8006e398.allBobjs_e0c[i];
                final BattleObject27c bobj = state.innerStruct_00;
                if (bobj instanceof PlayerBattleObject) {
                    PlayerBattleObject player = (PlayerBattleObject) bobj;
                    player.dragoonAttack_ac = Integer.parseInt(dragoonStats.get(player.charId_272 * 6 + player.dlevel_06)[3]);
                    player.dragoonMagic_ae = Integer.parseInt(dragoonStats.get(player.charId_272 * 6 + player.dlevel_06)[4]);
                    player.dragoonDefence_b0 = Integer.parseInt(dragoonStats.get(player.charId_272 * 6 + player.dlevel_06)[5]);
                    player.dragoonMagicDefence_b2 = Integer.parseInt(dragoonStats.get(player.charId_272 * 6 + player.dlevel_06)[6]);
                }
            }
        }
    }

    public void dramodBurnStacks(int spellId) {
        if (spellId >= 0 && spellId <= 3) {
            if (burnStackMode && burnStacks > 0) {
                int damage = Integer.parseInt(spellStats.get(spellId)[3]);
                String newDescription = spellStats.get(spellId)[13].replace("1.00", String.format("%.2f", (1 + (burnStacks * dmgPerBurn))));

                if (burnStacks == burnStacksMax) {
                    if (spellId == 0) {
                        damage *= (1 + (burnStacks * dmgPerBurn)) * Integer.parseInt(spellStats.get(2)[3]) / Integer.parseInt(spellStats.get(0)[3]);
                        newDescription = spellStats.get(spellId)[13].replace("1.00", String.format("%.2f", (1 + (burnStacks * dmgPerBurn)) * Integer.parseInt(spellStats.get(2)[3]) / Integer.parseInt(spellStats.get(0)[3])));
                    } else if (spellId == 1)  {
                        damage *= (1 + (burnStacks * dmgPerBurn)) * Integer.parseInt(spellStats.get(3)[3]) / Integer.parseInt(spellStats.get(1)[3]);
                        newDescription = spellStats.get(spellId)[13].replace("1.00", String.format("%.2f", (1 + (burnStacks * dmgPerBurn)) * Integer.parseInt(spellStats.get(3)[3]) / Integer.parseInt(spellStats.get(1)[3])));
                    } else {
                        damage *= 1 + (burnStacks * dmgPerBurn);
                    }
                } else {
                    damage *= 1 + (burnStacks * dmgPerBurn);
                }

                Bttl_800c.spellStats_800fa0b8[spellId] = new SpellStats0c(
                        spellStats.get(spellId)[12],
                        newDescription,
                        Integer.parseInt(spellStats.get(spellId)[0]),
                        Integer.parseInt(spellStats.get(spellId)[1]),
                        Integer.parseInt(spellStats.get(spellId)[2]),
                        damage,
                        Integer.parseInt(spellStats.get(spellId)[4]),
                        Integer.parseInt(spellStats.get(spellId)[5]),
                        burnStacks == burnStacksMax ? 0 : Integer.parseInt(spellStats.get(spellId)[6]),
                        Integer.parseInt(spellStats.get(spellId)[7]),
                        Element.fromFlag(Integer.parseInt(spellStats.get(spellId)[8])),
                        Integer.parseInt(spellStats.get(spellId)[9]),
                        Integer.parseInt(spellStats.get(spellId)[10]),
                        Integer.parseInt(spellStats.get(spellId)[11])
                );
            } else {
                Bttl_800c.spellStats_800fa0b8[spellId] = new SpellStats0c(
                        spellStats.get(spellId)[12],
                        spellStats.get(spellId)[13],
                        Integer.parseInt(spellStats.get(spellId)[0]),
                        Integer.parseInt(spellStats.get(spellId)[1]),
                        Integer.parseInt(spellStats.get(spellId)[2]),
                        Integer.parseInt(spellStats.get(spellId)[3]),
                        Integer.parseInt(spellStats.get(spellId)[4]),
                        Integer.parseInt(spellStats.get(spellId)[5]),
                        Integer.parseInt(spellStats.get(spellId)[6]),
                        Integer.parseInt(spellStats.get(spellId)[7]),
                        Element.fromFlag(Integer.parseInt(spellStats.get(spellId)[8])),
                        Integer.parseInt(spellStats.get(spellId)[9]),
                        Integer.parseInt(spellStats.get(spellId)[10]),
                        Integer.parseInt(spellStats.get(spellId)[11])
                );
            }
        }
    }

    public void addBurnStacks(PlayerBattleObject dart, int stacks) {
        int dlv = dart.dlevel_06;
        burnStacksMax = dlv == 1 ? 3 : dlv == 2 ? 6 : dlv == 3 ? 9 : dlv == 7 ? 15 : 12;
        burnStacks = Math.min(burnStacksMax, burnStacks + stacks);
    }

    public void dramodHotkeys() {
        final String difficulty = GameEngine.CONFIG.getConfig(DIFFICULTY.get());

        if (SMap.encounterAccumulator_800c6ae8.get() < 0) {
            if (hotkey.contains(InputAction.BUTTON_SHOULDER_LEFT_1) && hotkey.contains(InputAction.DPAD_UP)) {
                if (Scus94491BpeSegment_8006.battleState_8006e398._294 > 0) {
                    Scus94491BpeSegment_8006.battleState_8006e398._294 = 1;
                }
            } else if (hotkey.contains(InputAction.BUTTON_SHOULDER_LEFT_1) && hotkey.contains(InputAction.DPAD_RIGHT)) {
                if (Scus94491BpeSegment_8006.battleState_8006e398._298 > 0) {
                    Scus94491BpeSegment_8006.battleState_8006e398._298 = 1;
                }
            } else if (hotkey.contains(InputAction.BUTTON_SHOULDER_LEFT_1) && hotkey.contains(InputAction.DPAD_LEFT)) {
                if (Scus94491BpeSegment_8006.battleState_8006e398._29c > 0) {
                    Scus94491BpeSegment_8006.battleState_8006e398._29c = 1;
                }
            } else if (hotkey.contains(InputAction.BUTTON_NORTH) && hotkey.contains(InputAction.BUTTON_WEST) && (difficulty.equals("Hard Mode") || difficulty.equals("US + Hard Mode"))) {
                if (burnStacks > 0) {
                    burnStackMode = !burnStackMode;
                }
            }
        } else {
            if (hotkey.contains(InputAction.BUTTON_CENTER_1) && hotkey.contains(InputAction.BUTTON_THUMB_1)) {
                gameState_800babc8.charData_32c[2].partyFlags_04 = 3;
            } else if (hotkey.contains(InputAction.BUTTON_CENTER_1) && hotkey.contains(InputAction.BUTTON_THUMB_2)) {
                gameState_800babc8.charData_32c[1].partyFlags_04 = 3;
            } else if (hotkey.contains(InputAction.BUTTON_SOUTH) && hotkey.contains(InputAction.BUTTON_SHOULDER_LEFT_1)) {
                int mapId = submapCut_80052c30.get();
                if (mapId == 10) {
                    for (int i = 0; i < 9; i++) {
                        gameState_800babc8.charData_32c[i].partyFlags_04 = 3;
                        gameState_800babc8.charData_32c[i].dlevel_13 = 1;
                        gameState_800babc8.charData_32c[i].level_12 = 1;
                        gameState_800babc8.charData_32c[i].xp_00 = 0;
                        gameState_800babc8.charData_32c[i].equipment_14[0] = 0;
                        gameState_800babc8.charData_32c[i].equipment_14[1] = 76;
                        gameState_800babc8.charData_32c[i].equipment_14[2] = 46;
                        gameState_800babc8.charData_32c[i].equipment_14[3] = 93;
                    }
                    gameState_800babc8.goods_19c[0] ^= 1 << 0;
                    gameState_800babc8.goods_19c[0] ^= 1 << 1;
                    gameState_800babc8.goods_19c[0] ^= 1 << 2;
                    gameState_800babc8.goods_19c[0] ^= 1 << 3;
                    gameState_800babc8.goods_19c[0] ^= 1 << 4;
                    gameState_800babc8.goods_19c[0] ^= 1 << 5;
                    gameState_800babc8.goods_19c[0] ^= 1 << 6;
                } else if (mapId == 232) {
                    gameState_800babc8.goods_19c[0] ^= 1 << 0;
                } else if (mapId == 424 || mapId == 736) {
                    gameState_800babc8.goods_19c[0] ^= 1 << 7;
                } else if (mapId == 729) {
                    submapCut_80052c30.set(527);
                    smapLoadingStage_800cb430.set(0x4);
                } else if (mapId == 526 || mapId == 527) { // TODO: Story flag check here
                    submapCut_80052c30.set(730);
                    smapLoadingStage_800cb430.set(0x4);
                } else if (mapId == 732) {
                    encounterId_800bb0f8.set(420);

                    if(mainCallbackIndex_8004dd20.get() == 5) {
                        combatStage_800bb0f4.set(78);
                        FUN_800e5534(-1, 0);
                    } else if(mainCallbackIndex_8004dd20.get() == 8) {
                        combatStage_800bb0f4.set(78);
                        gameState_800babc8.areaIndex_4de = areaIndex_800c67aa.get();
                        gameState_800babc8.pathIndex_4d8 = pathIndex_800c67ac.get();
                        gameState_800babc8.dotIndex_4da = dotIndex_800c67ae.get();
                        gameState_800babc8.dotOffset_4dc = dotOffset_800c67b0.get();
                        gameState_800babc8.facing_4dd = facing_800c67b4.get();
                        pregameLoadingStage_800bb10c.set(8);
                    }

                    faustBattle = true;
                }
            }
        }
    }
}
