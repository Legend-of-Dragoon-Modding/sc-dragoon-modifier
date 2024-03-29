package lod.dragoonmodifier;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import legend.core.GameEngine;
import legend.core.gpu.ModelLoader;
import legend.core.gpu.Renderable;
import legend.core.gpu.VramTextureLoader;
import legend.game.SItem;
import legend.game.SMap;
import legend.game.Scus94491BpeSegment_8004;
import legend.game.Scus94491BpeSegment_8006;
import legend.game.characters.Element;
import legend.game.characters.ElementSet;
import legend.game.characters.VitalsStat;
import legend.game.combat.Bttl_800c;
import legend.game.combat.bobj.BattleObject27c;
import legend.game.combat.bobj.MonsterBattleObject;
import legend.game.combat.bobj.PlayerBattleObject;
import legend.game.combat.environment.BattlePreloadedEntities_18cb0;
import legend.game.combat.types.AttackType;
import legend.game.combat.types.CombatantStruct1a8;
import legend.game.input.InputAction;
import legend.game.modding.Mod;
import legend.game.modding.coremod.CoreMod;
import legend.game.modding.events.EventListener;
import legend.game.modding.events.battle.AttackEvent;
import legend.game.modding.events.battle.AttackSpGainEvent;
import legend.game.modding.events.battle.BattleEndedEvent;
import legend.game.modding.events.battle.BattleObjectTurnEvent;
import legend.game.modding.events.battle.BattleStartedEvent;
import legend.game.modding.events.battle.DragonBlockStaffOffEvent;
import legend.game.modding.events.battle.DragonBlockStaffOnEvent;
import legend.game.modding.events.battle.DragoonDEFFLoadedEvent;
import legend.game.modding.events.battle.EnemyRewardsEvent;
import legend.game.modding.events.battle.MonsterStatsEvent;
import legend.game.modding.events.battle.SpellStatsEvent;
import legend.game.modding.events.battle.StatDisplayEvent;
import legend.game.modding.events.characters.AdditionHitMultiplierEvent;
import legend.game.modding.events.characters.AdditionUnlockEvent;
import legend.game.modding.events.characters.BattleMapActiveAdditionHitPropertiesEvent;
import legend.game.modding.events.characters.CharacterStatsEvent;
import legend.game.modding.events.characters.XpToLevelEvent;
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
import legend.game.types.ActiveStatsa0;
import legend.game.types.EquipmentStats1c;
import legend.game.types.GameState52c;
import legend.game.types.ItemStats0c;
import legend.game.types.LevelStuff08;
import legend.game.types.MagicStuff08;
import legend.game.types.SpellStats0c;
import lod.dragoonmodifier.configs.ConfigDifficultyEntry;
import lod.dragoonmodifier.configs.ConfigEnrageMode;
import lod.dragoonmodifier.configs.ConfigFaustDefeated;
import lod.dragoonmodifier.configs.ConfigMonsterHPNames;
import lod.dragoonmodifier.configs.values.MonsterHPNames;
import lod.dragoonmodifier.events.DifficultyChangedEvent;

import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static legend.game.SItem.characterStats;
import static legend.game.SItem.dragoonStats;
import static legend.game.SItem.dxpTables;
import static legend.game.SItem.xpTables;
import static legend.game.SMap.FUN_800e5534;
import static legend.game.SMap.smapLoadingStage_800cb430;
import static legend.game.Scus94491BpeSegment_8004.mainCallbackIndex_8004dd20;
import static legend.game.Scus94491BpeSegment_8005.submapCut_80052c30;
import static legend.game.Scus94491BpeSegment_8006.battleState_8006e398;
import static legend.game.Scus94491BpeSegment_800b.combatStage_800bb0f4;
import static legend.game.Scus94491BpeSegment_800b.encounterId_800bb0f8;
import static legend.game.Scus94491BpeSegment_800b.gameState_800babc8;
import static legend.game.Scus94491BpeSegment_800b.pregameLoadingStage_800bb10c;
import static legend.game.Scus94491BpeSegment_800b.scriptEffect_800bb140;
import static legend.game.Scus94491BpeSegment_800b.scriptStatePtrArr_800bc1c0;
import static legend.game.Scus94491BpeSegment_800b.stats_800be5f8;
import static legend.game.WMap.areaIndex_800c67aa;
import static legend.game.WMap.dotIndex_800c67ae;
import static legend.game.WMap.dotOffset_800c67b0;
import static legend.game.WMap.facing_800c67b4;
import static legend.game.WMap.pathIndex_800c67ac;
import static legend.game.combat.Bttl_800c.allBobjCount_800c66d0;
import static legend.game.combat.Bttl_800c.currentEnemyNames_800c69d0;
import static legend.game.combat.Bttl_800c.currentTurnBobj_800c66c8;
import static legend.game.combat.Bttl_800c.dragoonSpaceElement_800c6b64;
import static legend.game.combat.Bttl_800c.monsterCount_800c6768;

@Mod(id = DragoonModifier.MOD_ID)
public class DragoonModifier {
    public static final String MOD_ID = "dragoon-modifier";

    private GameState52c gameState;
    public final List<String[]> monsterStats = new ArrayList<>();
    public final List<String[]> monstersRewardsStats = new ArrayList<>();
    public final List<String[]> additionStats = new ArrayList<>();
    public final List<String[]> additionMultiStats = new ArrayList<>();
    public final List<String[]> additionUnlockStats = new ArrayList<>();
    public final List<String[]> characterStatsTable = new ArrayList<>();
    public final List<String[]> dragoonStatsTable = new ArrayList<>();
    public final List<String[]> xpNextStats = new ArrayList<>();
    public final List<String[]> dxpNextStats = new ArrayList<>();
    public final List<String[]> spellStats = new ArrayList<>();
    public final List<String[]> equipStats = new ArrayList<>();
    public final List<String[]> itemStats = new ArrayList<>();
    public final List<String[]> shopItems = new ArrayList<>();
    public final List<String[]> shopPrices = new ArrayList<>();
    public final List<String[]> levelCaps = new ArrayList<>();
    public int maxCharacterLevel = 60;
    public int maxDragoonLevel = 5;
    public int[] enrageMode = new int[10];

    public final Registrar<ConfigEntry<?>, ConfigRegistryEvent> DRAMOD_CONFIG_REGISTRAR = new Registrar<>(GameEngine.REGISTRIES.config, MOD_ID);
    public final RegistryDelegate<ConfigDifficultyEntry> DIFFICULTY = DRAMOD_CONFIG_REGISTRAR.register("difficulty", ConfigDifficultyEntry::new);
    public final RegistryDelegate<ConfigFaustDefeated> FAUST_DEFEATED = DRAMOD_CONFIG_REGISTRAR.register("faust_defeated", ConfigFaustDefeated::new);
    public final RegistryDelegate<ConfigMonsterHPNames> MONSTER_HP_NAMES = DRAMOD_CONFIG_REGISTRAR.register("hp_names", ConfigMonsterHPNames::new);
    public final RegistryDelegate<ConfigEnrageMode> ENRAGE_MODE = DRAMOD_CONFIG_REGISTRAR.register("enrage_mode", ConfigEnrageMode::new);

    /*public final RegistryDelegate<ConfigElementalBomb> ELEMENTAL_BOMB = CSV_CONFIG_REGISTRAR.register("elemental_bomb", ConfigElementalBomb::new);
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
    private final Renderable[] burnStacksGfx = new Renderable[4];


    public DragoonModifier() {
        GameEngine.EVENTS.register(this);
        for (int i = 0; i < 4; i++) {
            this.burnStacksGfx[i] = ModelLoader.quad(
                            "burnstacks" + i,
                            0, 0, 0, 28, 36,
                            0, 0, 28, 36,
                            0, 0, 0,
                            0x80, 0x80, 0x80,
                            null
                    )
                    .texture(VramTextureLoader.textureFromPng(Path.of("mods", "csvstat", "burnstacks-" + ((i + 1) * 25) + ".png")))
                    .build();
        }
    }

    @EventListener
    public void registerConfig(final ConfigRegistryEvent event) {
        DRAMOD_CONFIG_REGISTRAR.registryEvent(event);
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
        this.loadCsvIntoList(difficulty, characterStatsTable, "scdk-character-stats.csv");
        this.loadCsvIntoList(difficulty, dragoonStatsTable, "scdk-dragoon-stats.csv");
        this.loadCsvIntoList(difficulty, xpNextStats, "scdk-exp-table.csv");
        this.loadCsvIntoList(difficulty, dxpNextStats, "scdk-dragoon-exp-table.csv");
        this.loadCsvIntoList(difficulty, spellStats, "scdk-spell-stats.csv");
        this.loadCsvIntoList(difficulty, equipStats, "scdk-equip-stats.csv");
        this.loadCsvIntoList(difficulty, itemStats, "scdk-thrown-item-stats.csv");
        this.loadCsvIntoList(difficulty, shopItems, "scdk-shop-items.csv");
        this.loadCsvIntoList(difficulty, shopPrices, "scdk-shop-prices.csv");
        this.loadCsvIntoList(difficulty, levelCaps, "scdk-level-caps.csv");
        maxCharacterLevel = Integer.parseInt(levelCaps.get(0)[0]);
        maxDragoonLevel = Integer.parseInt(levelCaps.get(0)[1]);

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
        for (int i = 0; i < 8; i++) {
            final BattlePreloadedEntities_18cb0.AdditionHitProperties20 hit = addition.additionHits.hits_00[i];
            //hit.flags_00 = Short.parseShort(additionStats.get(additionId * 8 + i)[0]);
            //hit.totalFrames_02 = Short.parseShort(additionStats.get(additionId * 8 + i)[1]);
            //hit.overlayHitFrameOffset_04 = Short.parseShort(additionStats.get(additionId * 8 + i)[2]);
            //hit.totalSuccessFrames_06 = Short.parseShort(additionStats.get(additionId * 8 + i)[3]);
            hit.damageMultiplier_08 = Short.parseShort(additionStats.get(addition.additionIndex * 8 + i)[4]);
            hit.spValue_0a = Short.parseShort(additionStats.get(addition.additionIndex * 8 + i)[5]);
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
        /*character.maxHp = Short.parseShort(characterStats.get(character.characterId * (maxCharacterLevel + 1) + character.level)[5]);
        character.bodySpeed = Short.parseShort(characterStats.get(character.characterId * (maxCharacterLevel + 1) + character.level)[0]);
        character.bodyAttack = Short.parseShort(characterStats.get(character.characterId * (maxCharacterLevel + 1) + character.level)[1]);
        character.bodyMagicAttack = Short.parseShort(characterStats.get(character.characterId * (maxCharacterLevel + 1) + character.level)[2]);
        character.bodyDefence = Short.parseShort(characterStats.get(character.characterId * (maxCharacterLevel + 1) + character.level)[3]);
        character.bodyMagicDefence = Short.parseShort(characterStats.get(character.characterId * (maxCharacterLevel + 1) + character.level)[4]);

        if (character.dlevel > 0) {
            character.maxMp = Integer.parseInt(dragoonStats.get(character.characterId * (maxDragoonLevel + 1) + character.dlevel)[0]);
            character.dragoonAttack = Integer.parseInt(dragoonStats.get(character.characterId * (maxDragoonLevel + 1) + character.dlevel)[3]);
            character.dragoonMagicAttack = Integer.parseInt(dragoonStats.get(character.characterId * (maxDragoonLevel + 1) + character.dlevel)[4]);
            character.dragoonDefence = Integer.parseInt(dragoonStats.get(character.characterId * (maxDragoonLevel + 1) + character.dlevel)[5]);
            character.dragoonMagicDefence = Integer.parseInt(dragoonStats.get(character.characterId * (maxDragoonLevel + 1) + character.dlevel)[6]);
        }*/
    }

    @EventListener
    public void xpNext(final XpToLevelEvent exp) {
        exp.xp = Integer.parseInt(xpNextStats.get(exp.charId * (maxCharacterLevel + 1) + exp.level)[0]);
    }

    @EventListener
    public void spellStats(final SpellStatsEvent spell) {
        int spellId = spell.spellId;

        final String difficulty = GameEngine.CONFIG.getConfig(DIFFICULTY.get());

        if (difficulty.equals("Hard Mode") || difficulty.equals("US + Hard Bosses")) {
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
        if (attack.attacker instanceof PlayerBattleObject) {
            if (attack.attackType == AttackType.DRAGOON_MAGIC_STATUS_ITEMS) {
                if (Integer.parseInt(spellStats.get(attack.attacker.spellId_4e)[4]) == 0) {
                    switch (Integer.parseInt(spellStats.get(attack.attacker.spellId_4e)[3])) {
                        case 0:
                        case 1:
                        case 2:
                        case 4:
                        case 8:
                        case 16:
                        case 32:
                        case 64:
                        case 128:
                            break;
                        default:
                            attack.damage *= (Integer.parseInt(spellStats.get(attack.attacker.spellId_4e)[3]) / 100d);
                    }
                }
            }
        }

        final String difficulty = GameEngine.CONFIG.getConfig(DIFFICULTY.get());

        if (difficulty.equals("Hard Mode") || difficulty.equals("US + Hard Bosses")) {
            if (attack.attacker instanceof PlayerBattleObject player) {
                if (player.isDragoon() && attack.attackType.isPhysical()) {
                    if (player.element == dragoonSpaceElement_800c6b64) {
                        if (player.charId_272 == 7) {
                            attack.damage *= 1.2;
                        } else {
                            attack.damage *= 1.5;
                        }
                    } else {
                        if (player.element == Element.fromFlag(0x80) && dragoonSpaceElement_800c6b64 == Element.fromFlag(0x8)) {
                            attack.damage *= 1.5;
                        }
                    }
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

        UpdateMonsterHPNames(attack);
        UpdateEnrageMode(attack);
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

        if (difficulty.equals("Hard Mode") || difficulty.equals("US + Hard Bosses")) {
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

                    if (player.equipment4_126 == 130 && player.equipment2_122 == 73) { //Holy Ahnk + Angel Robe
                        player.revive_13a -= 20;
                    }
                }
            }
        }

        for(int i = 0; i < allBobjCount_800c66d0.get(); i++) {
            final ScriptState<? extends BattleObject27c> state = battleState_8006e398.allBobjs_e0c[i];
            final BattleObject27c bobj = state.innerStruct_00;
            if (bobj instanceof PlayerBattleObject) {
                PlayerBattleObject player = (PlayerBattleObject) bobj;
                int x;
                for (x = 0; x < player.dlevel_06; x++) {
                    int charIndex = player.charId_272;
                    if(player.charId_272 == 0 && (gameState_800babc8.goods_19c[0] & 0xff) >>> 7 != 0) {
                        charIndex = 9; // Divine dragoon
                    }
                    //System.out.println("Spell IndeX: " + dragoonSpells_800c6960.get(player.charId_272).spellIndex_01.get(x));
                    int spellId = Integer.parseInt(dragoonStatsTable.get(charIndex * (maxDragoonLevel + 1) + x + 1)[1]);
                    //dragoonSpells_800c6960.get(player.charId_272).spellIndex_01.get(x).set(spellId == 255 ? 0xFFFFFFFF : spellId);
                    //System.out.println("Spell IndeX: " + dragoonSpells_800c6960.get(player.charId_272).spellIndex_01.get(x));
                }
            }
        }

        UpdateMonsterHPNames(null);

        burnStacks = 0;
        armorOfLegendTurns = 0;
        legendCasqueTurns = 0;
        burnStackMode = false;
        Arrays.fill(enrageMode, 0);
    }

    public void UpdateMonsterHPNames(final AttackEvent attack) {
        if (GameEngine.CONFIG.getConfig(MONSTER_HP_NAMES.get()) == MonsterHPNames.ON) {
            for (int i = 0; i < 10; i++) {
                final ScriptState<? extends BattleObject27c> state = battleState_8006e398.allBobjs_e0c[i];
                if (state != null) {
                    final BattleObject27c bobj = state.innerStruct_00;
                    if (bobj instanceof MonsterBattleObject) {
                        int hp = bobj.stats.getStat(CoreMod.HP_STAT.get()).getCurrent();
                        if (attack != null) {
                            hp = bobj == attack.defender ? bobj.stats.getStat(CoreMod.HP_STAT.get()).getCurrent() - attack.damage : hp;
                        }
                        currentEnemyNames_800c69d0.get(bobj.charSlot_276).set(String.valueOf(hp));
                    }
                }
            }
        }
    }

    public void UpdateEnrageMode(final AttackEvent attack) {
        for(int i = 0; i < monsterCount_800c6768.get(); i++) {
            final MonsterBattleObject monster = battleState_8006e398.monsterBobjs_e50[i].innerStruct_00;
            int hp = monster.stats.getStat(CoreMod.HP_STAT.get()).getCurrent();
            int maxHp = monster.stats.getStat(CoreMod.HP_STAT.get()).getMax();
            if (hp <= maxHp / 2 && enrageMode[i] == 0) {
                monster.attack_34 = (int) Math.round(monster.attack_34 * 1.1d);
                monster.magicAttack_36 = (int) Math.round(monster.magicAttack_36 * 1.1d);
                monster.defence_38 = (int) Math.round(monster.defence_38 * 1.1d);
                monster.magicDefence_3a = (int) Math.round(monster.magicDefence_3a * 1.1d);
                enrageMode[i] = 1;
            } else if (hp <= maxHp / 4 && enrageMode[i] == 1) {
                monster.attack_34 = (int) Math.round(monster.attack_34 * 1.136365d);
                monster.magicAttack_36 = (int) Math.round(monster.magicAttack_36 * 1.136365d);
                monster.defence_38 = (int) Math.round(monster.defence_38 * 1.136365d);
                monster.magicDefence_3a = (int) Math.round(monster.magicDefence_3a * 1.136365d);
                enrageMode[2] = 1;
            }
        }
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
    public void DragoonDEFFEvent(final DragoonDEFFLoadedEvent event) {
        System.out.println("DEFF Event: " + event.scriptId);
        switch (event.scriptId) {
            case 4206: //Transform?
            case 4236: //Dart Attack
            case 4238: //Lavitz Attack
            case 4242: //Rose Attack
            case 4244: //Haschel Attack
            case 4246: //Albert Attack
            case 4248: //Meru Attack
            case 4250: //Kongol Attack
            case 4254: //Divine Attack
            case 4308: //Burn Out
            case 4312: //Spark Net
            case 4316: //???
            case 4318: //Pellet
            case 4320: //Spear Frost
            case 4322: //Spinning Gale
            case 4326: //Trans Light
            case 4328: //Dark Mist
                new Thread(() -> {
                    for (int i = 0; i < 80; i++) {
                        try {
                            scriptEffect_800bb140.type_00.set(0);
                            Thread.sleep(125);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }).start();
            break;
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
                    magicalResistance,
                    physicalResistance,
                    magicalImmunity,
                    physicalImmunity,
                    revive,
                    hpRegen,
                    mpRegen,
                    spRegen,
                    special2Flag80,
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
                    Element.fromFlag(Integer.parseInt(itemStats.get(i)[1])),
                    Integer.parseInt(itemStats.get(i)[2]),
                    powerDefence,
                    powerMagicDefence,
                    powerAttack,
                    powerMagicAttack,
                    powerAttackHit,
                    powerMagicAttackHit,
                    powerAttackAvoid,
                    powerMagicAttackAvoid,
                    physicalImmunity,
                    magicalImmunity,
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

        for (int i = 0; i < 9; i++) {
            xpTables[i] = new int[maxCharacterLevel + 1];
            characterStats[i] = new LevelStuff08[maxCharacterLevel + 1];
            for (int x = 0; x < xpTables[i].length; x++) {
                xpTables[i][x] = Integer.parseInt(xpNextStats.get((maxCharacterLevel + 1) * i + x)[0]);
                characterStats[i][x] = new LevelStuff08(Integer.parseInt(characterStatsTable.get((maxCharacterLevel + 1) * i + x)[5]), Integer.parseInt(characterStatsTable.get((maxCharacterLevel + 1) * i + x)[6]),
                        Integer.parseInt(characterStatsTable.get((maxCharacterLevel + 1) * i + x)[0]), Integer.parseInt(characterStatsTable.get((maxCharacterLevel + 1) * i + x)[1]),
                        Integer.parseInt(characterStatsTable.get((maxCharacterLevel + 1) * i + x)[2]), Integer.parseInt(characterStatsTable.get((maxCharacterLevel + 1) * i + x)[3]),
                        Integer.parseInt(characterStatsTable.get((maxCharacterLevel + 1) * i + x)[4]));
            }
        }

        for (int i = 0; i < 9; i++) {
            dxpTables[i] = new int[maxDragoonLevel + 1];
            dragoonStats[i] = new MagicStuff08[maxDragoonLevel + 1];
            for (int x = 0; x < dxpTables[i].length - 1; x++) {
                dxpTables[i][x] = Integer.parseInt(dxpNextStats.get(i)[x]);
            }
            for (int x = 0; x < dragoonStats[i].length; x++) {
                int spellIndex = Integer.parseInt(dragoonStatsTable.get((maxDragoonLevel + 1) * i + x)[1]);
                dragoonStats[i][x] = new MagicStuff08(Integer.parseInt(dragoonStatsTable.get((maxDragoonLevel + 1) * i + x)[0]), spellIndex == 255 ? (byte) -1 : (byte) spellIndex,
                        Integer.parseInt(dragoonStatsTable.get((maxDragoonLevel + 1) * i + x)[2]), Integer.parseInt(dragoonStatsTable.get((maxDragoonLevel + 1) * i + x)[3]),
                        Integer.parseInt(dragoonStatsTable.get((maxDragoonLevel + 1) * i + x)[4]), Integer.parseInt(dragoonStatsTable.get((maxDragoonLevel + 1) * i + x)[5]),
                        Integer.parseInt(dragoonStatsTable.get((maxDragoonLevel + 1) * i + x)[6]));
                if (i == 0) {
                    System.out.println("TEST2: " + dragoonStats[i][x].spellIndex_02);
                }
            }
        }

        System.out.println("[Dragoon Modifier] [Game Loaded] Done");
    }

    @EventListener
    public void bobjTurn(final BattleObjectTurnEvent<?> turn) {
        final String difficulty = GameEngine.CONFIG.getConfig(DIFFICULTY.get());

        if (difficulty.equals("Hard Mode") || difficulty.equals("US + Hard Bosses")) {
            if (turn.bobj instanceof PlayerBattleObject player) {
                if (player.equipment2_122 == 74) {
                    armorOfLegendTurns += 1;
                    if (armorOfLegendTurns <= 40) {
                        player.defence_38 += 1;
                    }
                }

                if (player.equipment1_120 == 89) {
                    legendCasqueTurns += 1;
                    if (legendCasqueTurns <= 40) {
                        player.magicDefence_3a += 1;
                    }
                }

                if (player.charId_272 == 0) {
                    burnAdded = false;

                    if (burnStackMode) {
                        burnStacks = 0;
                        burnStackMode = false;
                    }
                }
            }
        }
    }

    @EventListener
    public void statDisplay(final StatDisplayEvent event) {
        if (event.player.charId_272 == 0) {
            int z = 99;
            if (currentTurnBobj_800c66c8 == battleState_8006e398.charBobjs_e40[event.charSlot]) {
                z = 0;
            }
            double currentBurnState = (double) burnStacks / (double) burnStacksMax;
            if (burnStacksMax > 0 && currentBurnState > 0) {
                if (currentBurnState <= 0.25) {
                    burnStacksGfx[0].render(event.charSlot * 94 + -143, 64, z);
                } else if (currentBurnState <= 0.50) {
                    burnStacksGfx[1].render(event.charSlot * 94 + -143, 64, z);
                } else if (currentBurnState <= 0.99) {
                    burnStacksGfx[2].render(event.charSlot * 94 + -143, 64, z);
                } else {
                    burnStacksGfx[3].render(event.charSlot * 94 + -143, 64, z);
                }
            }
        }
    }

    @EventListener
    public void dragonBlockStaffOn(final DragonBlockStaffOnEvent event) {
        final String difficulty = GameEngine.CONFIG.getConfig(DIFFICULTY.get());
        if (difficulty.equals("Hard Mode") || difficulty.equals("US + Hard Bosses")) {
            for(int i = 0; i < allBobjCount_800c66d0.get(); i++) {
                final ScriptState<? extends BattleObject27c> state = battleState_8006e398.allBobjs_e0c[i];
                final BattleObject27c bobj = state.innerStruct_00;
                if (bobj instanceof PlayerBattleObject) {
                    PlayerBattleObject player = (PlayerBattleObject) bobj;
                    player.dragoonAttack_ac = Integer.parseInt(dragoonStatsTable.get(player.charId_272 * (maxDragoonLevel + 1) + player.dlevel_06)[3]) * 8;
                    player.dragoonMagic_ae = Integer.parseInt(dragoonStatsTable.get(player.charId_272 * (maxDragoonLevel + 1) + player.dlevel_06)[4]) * 8;
                    player.dragoonDefence_b0 = Integer.parseInt(dragoonStatsTable.get(player.charId_272 * (maxDragoonLevel + 1) + player.dlevel_06)[5]) * 8;
                    player.dragoonMagicDefence_b2 = Integer.parseInt(dragoonStatsTable.get(player.charId_272 * (maxDragoonLevel + 1) + player.dlevel_06)[6]) * 8;
                }
            }
        }
    }

    @EventListener
    public void dragonBlockStaffOff(final DragonBlockStaffOffEvent event) {
        final String difficulty = GameEngine.CONFIG.getConfig(DIFFICULTY.get());
        if (difficulty.equals("Hard Mode") || difficulty.equals("US + Hard Bosses")) {
            for(int i = 0; i < allBobjCount_800c66d0.get(); i++) {
                final ScriptState<? extends BattleObject27c> state = battleState_8006e398.allBobjs_e0c[i];
                final BattleObject27c bobj = state.innerStruct_00;
                if (bobj instanceof PlayerBattleObject) {
                    PlayerBattleObject player = (PlayerBattleObject) bobj;
                    player.dragoonAttack_ac = Integer.parseInt(dragoonStatsTable.get(player.charId_272 * (maxDragoonLevel + 1) + player.dlevel_06)[3]);
                    player.dragoonMagic_ae = Integer.parseInt(dragoonStatsTable.get(player.charId_272 * (maxDragoonLevel + 1) + player.dlevel_06)[4]);
                    player.dragoonDefence_b0 = Integer.parseInt(dragoonStatsTable.get(player.charId_272 * (maxDragoonLevel + 1) + player.dlevel_06)[5]);
                    player.dragoonMagicDefence_b2 = Integer.parseInt(dragoonStatsTable.get(player.charId_272 * (maxDragoonLevel + 1) + player.dlevel_06)[6]);
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

        if (mainCallbackIndex_8004dd20.get() == 6) { // Combat
            if (hotkey.contains(InputAction.BUTTON_SHOULDER_LEFT_1) && hotkey.contains(InputAction.DPAD_UP)) {
                if (Scus94491BpeSegment_8006.battleState_8006e398.dragoonTurnsSlot1_294 > 0) {
                    Scus94491BpeSegment_8006.battleState_8006e398.dragoonTurnsSlot1_294 = 1;
                }
            } else if (hotkey.contains(InputAction.BUTTON_SHOULDER_LEFT_1) && hotkey.contains(InputAction.DPAD_RIGHT)) {
                if (Scus94491BpeSegment_8006.battleState_8006e398.dragoonTurnsSlot2_298 > 0) {
                    Scus94491BpeSegment_8006.battleState_8006e398.dragoonTurnsSlot2_298 = 1;
                }
            } else if (hotkey.contains(InputAction.BUTTON_SHOULDER_LEFT_1) && hotkey.contains(InputAction.DPAD_LEFT)) {
                if (Scus94491BpeSegment_8006.battleState_8006e398.dragoonTurnsSlot3_29c > 0) {
                    Scus94491BpeSegment_8006.battleState_8006e398.dragoonTurnsSlot3_29c = 1;
                }
            } else if (hotkey.contains(InputAction.BUTTON_SHOULDER_LEFT_2) && hotkey.contains(InputAction.BUTTON_SHOULDER_RIGHT_2)) {
                scriptStatePtrArr_800bc1c0[7].offset_18 = 0x2050; //TODO NOT THIS
            }
            if ((difficulty.equals("Hard Mode") || difficulty.equals("US + Hard Bosses"))) {
                if (hotkey.contains(InputAction.BUTTON_NORTH) && hotkey.contains(InputAction.BUTTON_WEST)) {
                    if (burnStacks > 0) {
                        burnStackMode = !burnStackMode;
                    }
                } else if (hotkey.contains(InputAction.BUTTON_SHOULDER_LEFT_2) && hotkey.contains(InputAction.DPAD_UP)) {
                    PlayerBattleObject player = battleState_8006e398.charBobjs_e40[0].innerStruct_00;
                    int dragoonTurns = Scus94491BpeSegment_8006.battleState_8006e398.dragoonTurnsSlot1_294;
                    int sp = player.stats.getStat(CoreMod.SP_STAT.get()).getCurrent();
                    if (player.isDragoon() && player.dlevel_06 >= 6 && dragoonTurns > 1 && sp >= 100) {
                        Scus94491BpeSegment_8006.battleState_8006e398.dragoonTurnsSlot1_294 -= 1;
                        player.stats.getStat(CoreMod.SP_STAT.get()).setCurrent(sp - 100);
                        player.guard_54 = 1;
                    }
                } else if (hotkey.contains(InputAction.BUTTON_SHOULDER_LEFT_2) && hotkey.contains(InputAction.DPAD_RIGHT)) {
                    PlayerBattleObject player = battleState_8006e398.charBobjs_e40[1].innerStruct_00;
                    int dragoonTurns = Scus94491BpeSegment_8006.battleState_8006e398.dragoonTurnsSlot2_298;
                    int sp = player.stats.getStat(CoreMod.SP_STAT.get()).getCurrent();
                    if (player.isDragoon() && player.dlevel_06 >= 6 && dragoonTurns > 1 && sp >= 100) {
                        Scus94491BpeSegment_8006.battleState_8006e398.dragoonTurnsSlot2_298 -= 1;
                        player.stats.getStat(CoreMod.SP_STAT.get()).setCurrent(sp - 100);
                        player.guard_54 = 1;
                    }
                } else if (hotkey.contains(InputAction.BUTTON_SHOULDER_LEFT_2) && hotkey.contains(InputAction.DPAD_LEFT)) {
                    PlayerBattleObject player = battleState_8006e398.charBobjs_e40[2].innerStruct_00;
                    int dragoonTurns = Scus94491BpeSegment_8006.battleState_8006e398.dragoonTurnsSlot3_29c;
                    int sp = player.stats.getStat(CoreMod.SP_STAT.get()).getCurrent();
                    if (player.isDragoon() && player.dlevel_06 >= 6 && dragoonTurns > 1 && sp >= 100) {
                        Scus94491BpeSegment_8006.battleState_8006e398.dragoonTurnsSlot3_29c -= 1;
                        player.stats.getStat(CoreMod.SP_STAT.get()).setCurrent(sp - 100);
                        player.guard_54 = 1;
                    }
                } else if (hotkey.contains(InputAction.BUTTON_SHOULDER_RIGHT_2) && hotkey.contains(InputAction.BUTTON_NORTH)) {
                    //TODO
                }
            }
        } else {
            if (hotkey.contains(InputAction.BUTTON_CENTER_1) && hotkey.contains(InputAction.BUTTON_THUMB_1)) {
                gameState_800babc8.charData_32c[2].partyFlags_04 = 3;
            } else if (hotkey.contains(InputAction.BUTTON_CENTER_1) && hotkey.contains(InputAction.BUTTON_THUMB_2)) {
                gameState_800babc8.charData_32c[1].partyFlags_04 = 3;
            } else if (hotkey.contains(InputAction.BUTTON_SOUTH) && hotkey.contains(InputAction.BUTTON_SHOULDER_RIGHT_1)) {
                int mapId = submapCut_80052c30.get();
                if (mapId == 10) {
                    gameState_800babc8.goods_19c[0] ^= 1 << 0;
                    gameState_800babc8.goods_19c[0] ^= 1 << 1;
                    gameState_800babc8.goods_19c[0] ^= 1 << 2;
                    gameState_800babc8.goods_19c[0] ^= 1 << 3;
                    gameState_800babc8.goods_19c[0] ^= 1 << 4;
                    gameState_800babc8.goods_19c[0] ^= 1 << 5;
                    gameState_800babc8.goods_19c[0] ^= 1 << 6;
                }
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
                    if (mapId == 736) {
                        gameState_800babc8.goods_19c[0] |= 1 << 0;
                    }
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
            } else if (hotkey.contains(InputAction.BUTTON_SOUTH) && hotkey.contains(InputAction.BUTTON_THUMB_2)) {
                for (int i = 0; i < 9; i++) {
                    gameState_800babc8.charData_32c[i].partyFlags_04 = 3;
                }
            }
        }
    }

    @EventListener
    public void handleAttackSpGain(final AttackSpGainEvent event) {
        final PlayerBattleObject bobj = event.bobj;

        if(bobj.charId_272 == 2 || bobj.charId_272 == 8) {
            final String difficulty = GameEngine.CONFIG.getConfig(DIFFICULTY.get());

            if(difficulty.equals("Hard Mode") || difficulty.equals("Hell Mode")) {
                switch(bobj.dlevel_06) {
                    case 6 -> event.sp = 150;
                    case 7 -> event.sp = 175;
                }
            }

            if(difficulty.equals("Hell Mode")) {
                event.sp = (int)Math.ceil(event.sp / 2.0f);
            }
        }
    }
}
