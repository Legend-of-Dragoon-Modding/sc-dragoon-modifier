package lod.dragoonmodifier;

import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvException;
import legend.core.Config;
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
import legend.game.combat.bobj.BattleEvent;
import legend.game.combat.bobj.BattleObject27c;
import legend.game.combat.bobj.PlayerBattleObject;
import legend.game.combat.environment.BattlePreloadedEntities_18cb0;
import legend.game.combat.types.AttackType;
import legend.game.combat.types.CombatantStruct1a8;
import legend.game.input.InputAction;
import legend.game.inventory.ItemRegistryEvent;
import legend.game.modding.Mod;
import legend.game.modding.coremod.CoreMod;
import legend.game.modding.coremod.elements.NoElement;
import legend.game.modding.events.EventListener;
import legend.game.modding.events.battle.*;
import legend.game.modding.events.characters.*;
import legend.game.modding.events.config.ConfigLoadedEvent;
import legend.game.modding.events.gamestate.GameLoadedEvent;
import legend.game.modding.events.input.InputPressedEvent;
import legend.game.modding.events.input.InputReleasedEvent;
import legend.game.modding.events.inventory.EquipmentStatsEvent;
import legend.game.modding.events.inventory.RepeatItemReturnEvent;
import legend.game.modding.events.inventory.ShopItemEvent;
import legend.game.modding.events.inventory.ShopSellPriceEvent;
import legend.game.modding.registries.Registrar;
import legend.game.modding.registries.RegistryDelegate;
import legend.game.saves.ConfigCollection;
import legend.game.saves.ConfigEntry;
import legend.game.saves.ConfigRegistryEvent;
import legend.game.scripting.ScriptState;
import legend.game.types.EquipmentStats1c;
import legend.game.types.ItemStats0c;
import legend.game.types.SpellStats0c;
import legend.game.types.WMapAreaData08;
import org.apache.commons.lang3.ArrayUtils;

import java.awt.event.InputEvent;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static legend.game.SMap.*;
import static legend.game.Scus94491BpeSegment_8004.mainCallbackIndex_8004dd20;
import static legend.game.Scus94491BpeSegment_8005.submapCut_80052c30;
import static legend.game.Scus94491BpeSegment_8006.battleState_8006e398;
import static legend.game.Scus94491BpeSegment_800b.*;
import static legend.game.WMap.*;
import static legend.game.WMap.facing_800c67b4;
import static legend.game.combat.Bttl_800c.allBobjCount_800c66d0;

@Mod(id = DragoonModifier.MOD_ID)
@EventListener
public class DragoonModifier {
    public static boolean loaded = false;
    public static final String MOD_ID = "dragoon-modifier";
    public static String modDirectory = "";
    public static List<String[]> monsterStats;
    public static List<String[]> monstersRewardsStats;
    public static List<String[]> additionStats;
    public static List<String[]> additionMultiStats;
    public static List<String[]> additionUnlockStats;
    public static List<String[]> characterStats;
    public static List<String[]> dragoonStats;
    public static List<String[]> xpNextStats;
    public static List<String[]> spellStats;
    public static List<String[]> equipStats;
    public static List<String[]> itemStats;
    public static List<String[]> shopItems;
    public static List<String[]> shopPrices;
    public static Registrar<ConfigEntry<?>, ConfigRegistryEvent> CSV_CONFIG_REGISTRAR = new Registrar<>(GameEngine.REGISTRIES.config, MOD_ID);
    public static final RegistryDelegate<ConfigDifficultyEntry> DIFFICULTY = CSV_CONFIG_REGISTRAR.register("difficulty", ConfigDifficultyEntry::new);
    public static final RegistryDelegate<ConfigFaustDefeated> FAUST_DEFEATED = CSV_CONFIG_REGISTRAR.register("faust_defeated", ConfigFaustDefeated::new);
    /*public static final RegistryDelegate<ConfigEnrageMode> ENRAGE_MODE = CSV_CONFIG_REGISTRAR.register("enrage_mode", ConfigEnrageMode::new);
    public static final RegistryDelegate<ConfigElementalBomb> ELEMENTAL_BOMB = CSV_CONFIG_REGISTRAR.register("elemental_bomb", ConfigElementalBomb::new);
    public static final RegistryDelegate<ConfigNeverGuard> NEVER_GUARD = CSV_CONFIG_REGISTRAR.register("never_guard", ConfigNeverGuard::new);
    public static final RegistryDelegate<ConfigTurnBattleMode> TURN_BATTLE = CSV_CONFIG_REGISTRAR.register("turn_battle", ConfigTurnBattleMode::new);
    public static final RegistryDelegate<ConfigUltimateBoss> ULTIMATE_BOSS = CSV_CONFIG_REGISTRAR.register("ultimate_boss", ConfigUltimateBoss::new);
    public static final RegistryDelegate<ConfigUltimateBossDefeated> ULTIMATE_BOSS_DEFEATED = CSV_CONFIG_REGISTRAR.register("ultimate_boss_defeated", ConfigUltimateBossDefeated::new);
    */

    public static Set<InputAction> hotkey = new HashSet<>();

    public static boolean burnStackMode = false;
    public static int burnStacks = 0;
    public static double dmgPerBurn = 0.1;
    public static int burnStacksMax = 0;
    public static double maxBurnAddition = 1;
    public static final int burnStackFlameShot = 1;
    public static final int burnStackExplosion = 2;
    public static final int burnStackFinalBurst = 3;
    public static final int burnStackRedEye = 4;
    public static final int burnStackAddition = 1;
    public static boolean faustBattle = false;
    public static int armorOfLegendTurns = 0;
    public static int legendCasqueTurns = 0;


    public DragoonModifier() {
        loaded = false;
    }

    @EventListener
    public static void registerConfig(final ConfigRegistryEvent event) {
        CSV_CONFIG_REGISTRAR.registryEvent(event);
    }

    @EventListener
    public static void configLoaded(final ConfigLoadedEvent event) {
        System.out.println("[Dragoon Modifier] Config Loaded Event");
        try {
            System.out.println("[Dragoon Modifier] Directory Test: " + GameEngine.CONFIG.getConfig(DIFFICULTY.get()).strip());
            modDirectory = GameEngine.CONFIG.getConfig(DIFFICULTY.get()).toString();
            System.out.println("[Dragoon Modifier] Mod already initialized...");
        } catch (Exception ex) {
            System.out.println("[Dragoon Modifier] Mod not initialized...");
            modDirectory = "US";
        }

        changeModDirectory(modDirectory);
    }

    public static void changeModDirectory(String newDirectory) {
        try {
            modDirectory = newDirectory;
            List<String[]> monstersCsv = new CSVReaderBuilder(new FileReader("./mods/csvstat/" + modDirectory + "/SCDK-MONSTER-STATS.CSV")).build().readAll();
            List<String[]> monstersRewardsCsv = new CSVReaderBuilder(new FileReader("./mods/csvstat/" + modDirectory + "/SCDK-MONSTER-REWARDS.CSV")).build().readAll();
            List<String[]> additionCsv = new CSVReaderBuilder(new FileReader("./mods/csvstat/" + modDirectory + "/SCDK-ADDITION-STATS.CSV")).build().readAll();
            List<String[]> additionUnlockCsv = new CSVReaderBuilder(new FileReader("./mods/csvstat/" + modDirectory + "/SCDK-ADDITION-UNLOCK-LEVELS.CSV")).build().readAll();
            List<String[]> additionMultiCsv = new CSVReaderBuilder(new FileReader("./mods/csvstat/" + modDirectory + "/SCDK-ADDITION-MULTIPLIER-STATS.CSV")).build().readAll();
            List<String[]> characterStatsCsv = new CSVReaderBuilder(new FileReader("./mods/csvstat/" + modDirectory + "/SCDK-CHARACTER-STATS.CSV")).build().readAll();
            List<String[]> dragoonStatsCsv = new CSVReaderBuilder(new FileReader("./mods/csvstat/" + modDirectory + "/SCDK-DRAGOON-STATS.CSV")).build().readAll();
            List<String[]> xpNextStatsCsv = new CSVReaderBuilder(new FileReader("./mods/csvstat/" + modDirectory + "/SCDK-EXP-TABLE.CSV")).build().readAll();
            List<String[]> spellStatsCsv = new CSVReaderBuilder(new FileReader("./mods/csvstat/" + modDirectory + "/SCDK-SPELL-STATS.CSV")).build().readAll();
            List<String[]> equipStatsCsv = new CSVReaderBuilder(new FileReader("./mods/csvstat/" + modDirectory + "/SCDK-EQUIP-STATS.CSV")).build().readAll();
            List<String[]> itemStatsCsv = new CSVReaderBuilder(new FileReader("./mods/csvstat/" + modDirectory + "/SCDK-THROWN-ITEM-STATS.CSV")).build().readAll();
            List<String[]> shopItemsCsv = new CSVReaderBuilder(new FileReader("./mods/csvstat/" + modDirectory + "/SCDK-SHOP-ITEMS.CSV")).build().readAll();
            List<String[]> shopPricesCsv = new CSVReaderBuilder(new FileReader("./mods/csvstat/" + modDirectory + "/SCDK-SHOP-PRICES.CSV")).build().readAll();

            monstersCsv.remove(0);
            monstersRewardsCsv.remove(0);
            additionCsv.remove(0);
            additionMultiCsv.remove(0);
            additionUnlockCsv.remove(0);
            characterStatsCsv.remove(0);
            dragoonStatsCsv.remove(0);
            xpNextStatsCsv.remove(0);
            spellStatsCsv.remove(0);
            equipStatsCsv.remove(0);
            itemStatsCsv.remove(0);
            shopItemsCsv.remove(0);
            shopPricesCsv.remove(0);

            monsterStats = monstersCsv;
            monstersRewardsStats = monstersRewardsCsv;
            additionStats = additionCsv;
            additionMultiStats = additionMultiCsv;
            additionUnlockStats = additionUnlockCsv;
            characterStats = characterStatsCsv;
            dragoonStats = dragoonStatsCsv;
            xpNextStats = xpNextStatsCsv;
            spellStats = spellStatsCsv;
            equipStats = equipStatsCsv;
            itemStats = itemStatsCsv;
            shopItems = shopItemsCsv;
            shopPrices = shopPricesCsv;

            System.out.println("[Dragoon Modifier] Loaded using directory: " + modDirectory);
            gameLoaded(null);
            loaded = true;
        } catch (FileNotFoundException e) {
            loaded = false;
            System.out.println(e.toString());
            throw new RuntimeException(e);
        } catch (IOException e) {
            loaded = false;
            System.out.println(e.toString());
            throw new RuntimeException(e);
        } catch (CsvException e) {
            loaded = false;
            System.out.println(e.toString());
            throw new RuntimeException(e);
        }
    }

    @EventListener
    public static void enemyRewards(final EnemyRewardsEvent enemyRewards) {
        if (!loaded) return;
        int enemyId = enemyRewards.enemyId;
        enemyRewards.clear();
        enemyRewards.xp = Integer.parseInt(monstersRewardsStats.get(enemyId)[0]);
        enemyRewards.gold = Integer.parseInt(monstersRewardsStats.get(enemyId)[1]);
        enemyRewards.add(new CombatantStruct1a8.ItemDrop(Integer.parseInt(monstersRewardsStats.get(enemyId)[2]), Integer.parseInt(monstersRewardsStats.get(enemyId)[3])));
        if (faustBattle == true && enemyRewards.enemyId == 344) {
            enemyRewards.clear();
            enemyRewards.xp = 60000;
            enemyRewards.gold = 250;
            if (GameEngine.CONFIG.getConfig(FAUST_DEFEATED.get()) == 40) {
                enemyRewards.add(new CombatantStruct1a8.ItemDrop(100, 74));
                enemyRewards.add(new CombatantStruct1a8.ItemDrop(100, 89));
            }
        }
    }

    @EventListener
    public static void enemyStats(final MonsterStatsEvent enemyStats) {
        if (!loaded) return;
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
    public static void additionStats(final BattleMapActiveAdditionHitPropertiesEvent addition) {
        if (!loaded) return;
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
    public static void additionMulti(final AdditionHitMultiplierEvent multiplier) {
        if (!loaded) return;
        multiplier.additionSpMulti = Integer.parseInt(additionMultiStats.get(multiplier.additionId)[(multiplier.additionLevel - 1) * 4]);
        multiplier.additionDmgMulti = Integer.parseInt(additionMultiStats.get(multiplier.additionId)[(multiplier.additionLevel - 1) * 4 + 1]);
    }

    @EventListener
    public static void additionUnlock(final AdditionUnlockEvent unlock) {
        if (!loaded) return;
        unlock.additionLevel = Integer.parseInt(additionUnlockStats.get(unlock.additionId)[0]);
    }

    @EventListener
    public static void characterStats(final CharacterStatsEvent character) {
        if (!loaded) return;
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
    public static void xpNext(final XpToLevelEvent exp) {
        if (!loaded) return;
        exp.xp = Integer.parseInt(xpNextStats.get(exp.charId * 61 + exp.level)[0]);
    }

    @EventListener
    public static void spellStats(final SpellStatsEvent spell) {
        if (!loaded) return;
        int spellId = spell.spellId;
        if (modDirectory.equals("Hard Mode") || modDirectory.equals("US + Hard Mode")) {
            dramodBurnStacks(spellId);
        }
    }

    @EventListener
    public static void shopItem(final ShopItemEvent shopItem) {
        if (!loaded) return;
        shopItem.itemId = Integer.parseInt(shopItems.get(shopItem.shopId)[shopItem.slotId]);
        shopItem.price = Integer.parseInt(shopPrices.get(shopItem.itemId)[0]) * 2;
    }

    @EventListener
    public static void shopSell(final ShopSellPriceEvent shopItem) {
        if (!loaded) return;
        shopItem.price = Integer.parseInt(shopPrices.get(shopItem.itemId)[0]);
    }

    @EventListener
    public static void repeatItems(final RepeatItemReturnEvent item) {
        if (!loaded) return;
        if (modDirectory.equals("Japan Demo")) {
            item.returnItem = item.itemId == 250 ? true : false;
        }
    }

    @EventListener
    public static void attack(final AttackEvent attack) {
        if (!loaded) return;
        if (attack.attacker instanceof PlayerBattleObject && attack.attackType == AttackType.DRAGOON_MAGIC_STATUS_ITEMS) {
            if (!ArrayUtils.contains(new int[]{1, 2, 4, 8, 16, 32, 64, 128}, attack.damage)) {
                attack.damage *= (Integer.parseInt(spellStats.get(attack.attacker.spellId_4e)[3]) / 100d);
            }
        }

        if (modDirectory.equals("Hard Mode") || modDirectory.equals("US + Hard Mode")) {
            if (attack.attacker instanceof PlayerBattleObject && attack.attacker.charId_272 == 0) {
                final PlayerBattleObject player = (PlayerBattleObject) attack.attacker;
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
                    if (attack.attackType == AttackType.DRAGOON_MAGIC_STATUS_ITEMS) {
                        if (player.spellId_4e == 0) {
                            addBurnStacks(player, burnStackFlameShot);
                        } else if (player.spellId_4e == 1) {
                            addBurnStacks(player, burnStackExplosion);
                        } else if (player.spellId_4e == 2) {
                            addBurnStacks(player, burnStackFinalBurst);
                        } else if (player.spellId_4e == 3) {
                            addBurnStacks(player, burnStackRedEye);
                        }
                    } else if (attack.attackType == AttackType.PHYSICAL && player.isDragoon()) {
                        addBurnStacks(player, burnStackAddition);
                    }
                }
            }
        }
    }

    @EventListener
    public static void battleStarted(final BattleStartedEvent battleStarted) {
        if (!loaded) return;
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

        if (modDirectory.equals("Hard Mode") || modDirectory.equals("US + Hard Mode")) {
            for(int i = 0; i < allBobjCount_800c66d0.get(); i++) {
                final ScriptState<? extends BattleObject27c> state = battleState_8006e398.allBobjs_e0c[i];
                final BattleObject27c bobj = state.innerStruct_00;
                if (bobj instanceof PlayerBattleObject) {
                    bobj.equipmentElementalImmunity_22.clear();
                }
            }
        }

        burnStacks = 0;
        armorOfLegendTurns = 0;
        legendCasqueTurns = 0;
        burnStackMode = false;
    }

    @EventListener
    public static void battleEnded(final BattleEndedEvent battleEnded) {
        if (!loaded) return;
        if (faustBattle) {
            faustBattle = false;
            GameEngine.CONFIG.setConfig(FAUST_DEFEATED.get(), GameEngine.CONFIG.getConfig(FAUST_DEFEATED.get()) + 1);
            System.out.println("[Dragoon Modifier] Faust Defeated: " + GameEngine.CONFIG.getConfig(FAUST_DEFEATED.get()));
        }
    }

    @EventListener
    public static void inputPressed(final InputPressedEvent input) {
        if (!loaded) return;
        hotkey.add(input.inputAction);

        if (modDirectory.equals("Hard Mode") || modDirectory.equals("US + Hard Mode")) {
            dramodHotkeys();
        }
    }

    @EventListener
    public static void inputReleased(final InputReleasedEvent input) {
        if (!loaded) return;
        hotkey.remove(input.inputAction);
    }

    @EventListener
    public static void gameLoaded(final GameLoadedEvent game) {
        System.out.println("[Dragoon Modifier] [Game Loaded]");
        for (int i = 0; i < spellStats.size(); i++) {
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

        /*for (int i = 0; i < 192; i++) {
            ElementSet elementalResistance = new ElementSet();
            ElementSet elementalImmunity = new ElementSet();
            ElementSet attackElement = new ElementSet();
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

            attackElement.add(Element.fromFlag(Integer.parseInt(equipStats.get(i)[4])));
            if (Integer.parseInt(equipStats.get(i)[6]) > 0)
                elementalResistance.add(Element.fromFlag(Integer.parseInt(equipStats.get(i)[6])));
            if (Integer.parseInt(equipStats.get(i)[7]) > 0)
                elementalImmunity.add(Element.fromFlag(Integer.parseInt(equipStats.get(i)[7])));

            SItem.equipmentStats_80111ff0[i].name = equipStats.get(i)[28];
            SItem.equipmentStats_80111ff0[i].description = equipStats.get(i)[29].replace('\u00A7', '\n');
            SItem.equipmentStats_80111ff0[i].flags_00 = Integer.parseInt(equipStats.get(i)[0]);
            SItem.equipmentStats_80111ff0[i].type_01 = Integer.parseInt(equipStats.get(i)[1]);
            SItem.equipmentStats_80111ff0[i]._02 = Integer.parseInt(equipStats.get(i)[2]);
            SItem.equipmentStats_80111ff0[i].equipableFlags_03 = Integer.parseInt(equipStats.get(i)[3]);
            SItem.equipmentStats_80111ff0[i].attackElement_04 = attackElement;
            SItem.equipmentStats_80111ff0[i]._05 = Integer.parseInt(equipStats.get(i)[5]);
            SItem.equipmentStats_80111ff0[i].mpPerPhysicalHit = mpPerPhysicalHit;
            SItem.equipmentStats_80111ff0[i].spPerPhysicalHit = spPerPhysicalHit;
            SItem.equipmentStats_80111ff0[i].mpPerMagicalHit = mpPerMagicalHit;
            SItem.equipmentStats_80111ff0[i].spPerMagicalHit = spPerMagicalHit;
            SItem.equipmentStats_80111ff0[i].hpMultiplier = hpMultiplier;
            SItem.equipmentStats_80111ff0[i].mpMultiplier = mpMultiplier;
            SItem.equipmentStats_80111ff0[i].spMultiplier = spMultiplier;
            SItem.equipmentStats_80111ff0[i].magicalResistance = magicalResistance;
            SItem.equipmentStats_80111ff0[i].physicalResistance = physicalResistance;
            SItem.equipmentStats_80111ff0[i].magicalImmunity = magicalImmunity;
            SItem.equipmentStats_80111ff0[i].physicalImmunity = physicalImmunity;
            SItem.equipmentStats_80111ff0[i].revive = revive;
            SItem.equipmentStats_80111ff0[i].hpRegen = hpRegen;
            SItem.equipmentStats_80111ff0[i].mpRegen = mpRegen;
            SItem.equipmentStats_80111ff0[i].spRegen = spRegen;
            SItem.equipmentStats_80111ff0[i].special2Flag80 = special2Flag80;
            SItem.equipmentStats_80111ff0[i].elementalResistance_06 = elementalResistance;
            SItem.equipmentStats_80111ff0[i].elementalImmunity_07 = elementalImmunity;
            SItem.equipmentStats_80111ff0[i].statusResist_08 = Integer.parseInt(equipStats.get(i)[8]);
            SItem.equipmentStats_80111ff0[i]._09 = Integer.parseInt(equipStats.get(i)[9]);
            SItem.equipmentStats_80111ff0[i].attack1_0a = Integer.parseInt(equipStats.get(i)[10]);
            SItem.equipmentStats_80111ff0[i].icon_0e = Integer.parseInt(equipStats.get(i)[14]);
            SItem.equipmentStats_80111ff0[i].speed_0f = Integer.parseInt(equipStats.get(i)[15]);
            SItem.equipmentStats_80111ff0[i].attack2_10 = Integer.parseInt(equipStats.get(i)[16]);
            SItem.equipmentStats_80111ff0[i].magicAttack_11 = Integer.parseInt(equipStats.get(i)[17]);
            SItem.equipmentStats_80111ff0[i].defence_12 = Integer.parseInt(equipStats.get(i)[18]);
            SItem.equipmentStats_80111ff0[i].magicDefence_13 = Integer.parseInt(equipStats.get(i)[19]);
            SItem.equipmentStats_80111ff0[i].attackHit_14 = Integer.parseInt(equipStats.get(i)[20]);
            SItem.equipmentStats_80111ff0[i].magicHit_15 = Integer.parseInt(equipStats.get(i)[21]);
            SItem.equipmentStats_80111ff0[i].attackAvoid_16 = Integer.parseInt(equipStats.get(i)[22]);
            SItem.equipmentStats_80111ff0[i].magicAvoid_17 = Integer.parseInt(equipStats.get(i)[23]);
            SItem.equipmentStats_80111ff0[i].onHitStatusChance_18 = Integer.parseInt(equipStats.get(i)[24]);
            SItem.equipmentStats_80111ff0[i]._19 = Integer.parseInt(equipStats.get(i)[25]);
            SItem.equipmentStats_80111ff0[i]._1a = Integer.parseInt(equipStats.get(i)[26]);
            SItem.equipmentStats_80111ff0[i].onHitStatus_1b = Integer.parseInt(equipStats.get(i)[27]);
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

            Scus94491BpeSegment_8004.itemStats_8004f2ac[i].name = itemStats.get(i)[12];
            Scus94491BpeSegment_8004.itemStats_8004f2ac[i].description = itemStats.get(i)[13].replace('\u00A7', '\n');
            Scus94491BpeSegment_8004.itemStats_8004f2ac[i].combatDescription = itemStats.get(i)[14];
            Scus94491BpeSegment_8004.itemStats_8004f2ac[i].target_00 = Integer.parseInt(itemStats.get(i)[0]);
            Scus94491BpeSegment_8004.itemStats_8004f2ac[i].element_01 = Element.fromFlag(Integer.parseInt(itemStats.get(i)[1]));
            Scus94491BpeSegment_8004.itemStats_8004f2ac[i].damageMultiplier_02 = Integer.parseInt(itemStats.get(i)[2]);
            Scus94491BpeSegment_8004.itemStats_8004f2ac[i].powerDefence = powerDefence;
            Scus94491BpeSegment_8004.itemStats_8004f2ac[i].powerMagicDefence = powerMagicDefence;
            Scus94491BpeSegment_8004.itemStats_8004f2ac[i].powerAttack = powerAttack;
            Scus94491BpeSegment_8004.itemStats_8004f2ac[i].powerMagicAttack = powerMagicAttack;
            Scus94491BpeSegment_8004.itemStats_8004f2ac[i].powerAttackHit = powerAttackHit;
            Scus94491BpeSegment_8004.itemStats_8004f2ac[i].powerMagicAttackHit = powerMagicAttackHit;
            Scus94491BpeSegment_8004.itemStats_8004f2ac[i].powerAttackAvoid = powerAttackAvoid;
            Scus94491BpeSegment_8004.itemStats_8004f2ac[i].powerMagicAttackAvoid = powerMagicAttackAvoid;
            Scus94491BpeSegment_8004.itemStats_8004f2ac[i].physicalImmunity = physicalImmunity;
            Scus94491BpeSegment_8004.itemStats_8004f2ac[i].magicalImmunity = magicalImmunity;
            Scus94491BpeSegment_8004.itemStats_8004f2ac[i].speedUp = speedUp;
            Scus94491BpeSegment_8004.itemStats_8004f2ac[i].speedDown = speedDown;
            Scus94491BpeSegment_8004.itemStats_8004f2ac[i].spPerPhysicalHit = spPerPhysicalHit;
            Scus94491BpeSegment_8004.itemStats_8004f2ac[i].mpPerPhysicalHit = mpPerPhysicalHit;
            Scus94491BpeSegment_8004.itemStats_8004f2ac[i].spPerMagicalHit = spPerMagicalHit;
            Scus94491BpeSegment_8004.itemStats_8004f2ac[i].mpPerMagicalHit = mpPerMagicalHit;
            Scus94491BpeSegment_8004.itemStats_8004f2ac[i].damage_05 = Integer.parseInt(itemStats.get(i)[5]);
            Scus94491BpeSegment_8004.itemStats_8004f2ac[i].icon_07 = Integer.parseInt(itemStats.get(i)[7]);
            Scus94491BpeSegment_8004.itemStats_8004f2ac[i].status_08 = Integer.parseInt(itemStats.get(i)[8]);
            Scus94491BpeSegment_8004.itemStats_8004f2ac[i].percentage_09 = Integer.parseInt(itemStats.get(i)[9]);
            Scus94491BpeSegment_8004.itemStats_8004f2ac[i].uu2_0a = Integer.parseInt(itemStats.get(i)[10]);
            Scus94491BpeSegment_8004.itemStats_8004f2ac[i].type_0b = Integer.parseInt(itemStats.get(i)[11]);
        }*/

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
    }

    @EventListener
    public static void bobjTurn(BattleObjectTurnEvent turn) {
        if (modDirectory.equals("Hard Mode") || modDirectory.equals("US + Hard Mode")) {
            if (turn.bobj instanceof PlayerBattleObject) {
                final PlayerBattleObject player = (PlayerBattleObject) turn.bobj;
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
            }
        }
    }

    public static void dramodBurnStacks(int spellId) {
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

    public static void addBurnStacks(PlayerBattleObject dart, int stacks) {
        int dlv = dart.dlevel_06;
        burnStacksMax = dlv == 1 ? 3 : dlv == 2 ? 6 : dlv == 3 ? 9 : dlv == 7 ? 15 : 12;
        burnStacks = Math.min(burnStacksMax, burnStacks + stacks);
    }

    public static void dramodHotkeys() {
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
            } else if (hotkey.contains(InputAction.BUTTON_NORTH) && hotkey.contains(InputAction.BUTTON_WEST)) {
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
                    gameState_800babc8.goods_19c[0] ^= 7 << 0;
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