package lod.dragoonmodifier;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import legend.core.GameEngine;
import legend.core.gpu.ModelLoader;
import legend.core.gpu.Renderable;
import legend.core.gpu.VramTextureLoader;
import legend.game.*;
import legend.game.characters.Element;
import legend.game.characters.ElementSet;
import legend.game.characters.TurnBasedPercentileBuff;
import legend.game.characters.VitalsStat;
import legend.game.combat.Bttl_800c;
import legend.game.combat.bobj.AttackEvent;
import legend.game.combat.bobj.BattleObject27c;
import legend.game.combat.bobj.MonsterBattleObject;
import legend.game.combat.bobj.PlayerBattleObject;
import legend.game.combat.environment.BattlePreloadedEntities_18cb0;
import legend.game.combat.types.AttackType;
import legend.game.combat.types.CombatantStruct1a8;
import legend.game.input.InputAction;
import legend.game.inventory.WhichMenu;
import legend.game.modding.Mod;
import legend.game.modding.coremod.CoreMod;
import legend.game.modding.coremod.character.CharacterData;
import legend.game.modding.events.EventListener;
import legend.game.modding.events.battle.*;
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
import legend.game.scripting.GameVarArrayParam;
import legend.game.scripting.ScriptState;
import legend.game.types.*;
import lod.dragoonmodifier.configs.*;
import lod.dragoonmodifier.configs.events.HellModeAdjustmentEvent;
import lod.dragoonmodifier.configs.values.DamageTracker;
import lod.dragoonmodifier.configs.values.ElementalBomb;
import lod.dragoonmodifier.configs.values.EnrageMode;
import lod.dragoonmodifier.configs.values.MonsterHPNames;
import lod.dragoonmodifier.configs.events.DifficultyChangedEvent;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.IntStream;

import static legend.core.GameEngine.CONFIG;
import static legend.game.SItem.*;
import static legend.game.SMap.FUN_800e5534;
import static legend.game.SMap.smapLoadingStage_800cb430;
import static legend.game.Scus94491BpeSegment_8004.engineState_8004dd20;
import static legend.game.Scus94491BpeSegment_8005.submapCut_80052c30;
import static legend.game.Scus94491BpeSegment_8006.battleState_8006e398;
import static legend.game.Scus94491BpeSegment_8007.shopId_8007a3b4;
import static legend.game.Scus94491BpeSegment_800b.*;
import static legend.game.combat.Bttl_800c.*;
import static legend.game.wmap.WMap.mapState_800c6798;

@Mod(id = DragoonModifier.MOD_ID)
public class DragoonModifier {
    public static final String MOD_ID = "dragoon-modifier";
    public static final String[] charNames = {"Dart", "Lavitz", "Shana", "Rose", "Haschel", "Albert", "Meru", "Kongol", "???"};

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
    public final List<String[]> spBarColours = new ArrayList<>();
    public final List<String[]> ultimateData = new ArrayList<>();
    public int maxCharacterLevel = 60;
    public int maxDragoonLevel = 5;
    public int maxAdditionLevel = 5;
    public int additionsPerLevel = 20;
    public int currentPlayerSlot = 0;
    public boolean dragonBlockStaff = false;
    public int[] enrageMode = new int[10];
    public Element[] previousElement = new Element[3];
    public int[][] damageTrackerEquips = new int[3][5];
    public int[][] damageTracker = new int[3][5];
    public int[] damageTrackerPreviousHP = new int[10];
    public int damageTrackerPreviousCharacter = 0;
    public int damageTrackerPreviousCharacterID = 0;
    public int damageTrackerPreviousAttackType = 0;
    public ArrayList<String> damageTrackerLog = new ArrayList<>();
    public boolean damageTrackerPrinted = false;
    public boolean[] elementalAttack = new boolean[3];
    public int[] windMark = new int[10];
    public int[] thunderCharge = new int[10];
    public boolean flowerStormOverride = false;
    public boolean[] shanaStarChildrenHeal = new boolean[3];
    public boolean[] shanaRapidFireContinue = new boolean[3];
    public boolean[] shanaRapidFire = new boolean[3];
    public int[] shanaRapidFireCount = new int[3];
    public boolean[] meruBoost = new boolean[3];
    public int[] meruBoostTurns = new int[3];
    public int[] meruMDFSave = new int[3];
    public int[] meruMaxHpSave = new int[3];
    public Element[] elementalBombPreviousElement = new Element[10];
    public int[] elementalBombTurns = new int[10];
    public boolean swappedEXP = false;
    public int[] swapEXPParty = new int[3];
    public int[][] ultimateEncounter = {{487, 10}, {386, 3}, {414, 8},
            {461, 21}, {412, 16}, {413, 70}, {387, 5}, {415, 12},
            {449, 68}, {402, 23}, {403, 29}, {417, 31}, {418, 41}, {448, 68}, {416, 38}, {422, 42}, {423, 47}, {432, 69}, {430, 67}, {433, 56}, {431, 54}, {447, 68}
    };
    public boolean ultimateBattle = false;
    public int ultimateLevelCap = 30;
    public double[][] ultimatePenality = new double[3][2];
    public boolean[] bonusItemSP = new boolean[3];
    public boolean[] ouroboros = new boolean[3];
    public ArrayList<Element> elementArrowsElements = new ArrayList<>();
    public int[] ringOfElements = new int[3];
    public Element[] ringOfElementsElement = new Element[3];

    public static final Registrar<ConfigEntry<?>, ConfigRegistryEvent> DRAMOD_CONFIG_REGISTRAR = new Registrar<>(GameEngine.REGISTRIES.config, MOD_ID);
    public static final RegistryDelegate<ConfigDifficultyEntry> DIFFICULTY = DRAMOD_CONFIG_REGISTRAR.register("difficulty", ConfigDifficultyEntry::new);
    public static final RegistryDelegate<ConfigFaustDefeated> FAUST_DEFEATED = DRAMOD_CONFIG_REGISTRAR.register("faust_defeated", ConfigFaustDefeated::new);
    public static final RegistryDelegate<ConfigMonsterHPNames> MONSTER_HP_NAMES = DRAMOD_CONFIG_REGISTRAR.register("hp_names", ConfigMonsterHPNames::new);
    public static final RegistryDelegate<ConfigEnrageMode> ENRAGE_MODE = DRAMOD_CONFIG_REGISTRAR.register("enrage_mode", ConfigEnrageMode::new);
    public static final RegistryDelegate<ConfigHellFlowerStorm> FLOWER_STORM = DRAMOD_CONFIG_REGISTRAR.register("flower_storm", ConfigHellFlowerStorm::new);
    public static final RegistryDelegate<ConfigUltimateBoss> ULTIMATE_BOSS = DRAMOD_CONFIG_REGISTRAR.register("ultimate_boss", ConfigUltimateBoss::new);
    public static final RegistryDelegate<ConfigUltimateBossDefeated> ULTIMATE_BOSS_DEFEATED = DRAMOD_CONFIG_REGISTRAR.register("ultimate_boss_defeated", ConfigUltimateBossDefeated::new);
    public static final RegistryDelegate<ConfigElementalBomb> ELEMENTAL_BOMB = DRAMOD_CONFIG_REGISTRAR.register("elemental_bomb", ConfigElementalBomb::new);
    public static final RegistryDelegate<ConfigDamageTracker> DAMAGE_TRACKER = DRAMOD_CONFIG_REGISTRAR.register("damage_tracker", ConfigDamageTracker::new);
    /*
    public final RegistryDelegate<ConfigNeverGuard> NEVER_GUARD = CSV_CONFIG_REGISTRAR.register("never_guard", ConfigNeverGuard::new);
    public final RegistryDelegate<ConfigTurnBattleMode> TURN_BATTLE = CSV_CONFIG_REGISTRAR.register("turn_battle", ConfigTurnBattleMode::new);

    */

    public Set<InputAction> hotkey = new HashSet<>();

    public boolean burnStackMode = false;
    public int burnStacks = 0;
    public int previousBurnStacks = 0;
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
    private final Renderable[] thunderChargeGfx = new Renderable[11];
    private final Renderable[] windMarkGfx = new Renderable[4];


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
        for (int i = 0; i < 11; i++) {
            this.thunderChargeGfx[i] = ModelLoader.quad(
                            "thundercharge" + i,
                            0, 0, 0, 28, 36,
                            0, 0, 28, 36,
                            0, 0, 0,
                            0x80, 0x80, 0x80,
                            null
                    )
                    .texture(VramTextureLoader.textureFromPng(Path.of("mods", "csvstat", "thundercharge-" + i + ".png")))
                    .build();
        }
        for (int i = 0; i < 4; i++) {
            this.windMarkGfx[i] = ModelLoader.quad(
                            "windmark" + i,
                            0, 0, 0, 28, 36,
                            0, 0, 28, 36,
                            0, 0, 0,
                            0x80, 0x80, 0x80,
                            null
                    )
                    .texture(VramTextureLoader.textureFromPng(Path.of("mods", "csvstat", "windmark-" + i + ".png")))
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
        this.loadCsvIntoList(difficulty, spBarColours, "scdk-sp-bar-colours.csv");
        this.loadCsvIntoList("Ultimate", ultimateData, "scdk-ultimate.csv");
        maxCharacterLevel = Integer.parseInt(levelCaps.get(0)[0]);
        maxDragoonLevel = Integer.parseInt(levelCaps.get(0)[1]);
        maxAdditionLevel = Integer.parseInt(levelCaps.get(0)[2]);
        additionsPerLevel = Integer.parseInt(levelCaps.get(0)[3]);

        System.out.println("[Dragoon Modifier] Loaded using directory: " + difficulty);
        ConfigSwapped();
    }

    @EventListener
    public void enemyRewards(final EnemyRewardsEvent enemyRewards) {
        int enemyId = enemyRewards.enemyId;
        enemyRewards.clear();

        if (ultimateBattle) {
            for (int i = 0; i < 86; i++) {
                if (enemyId == Integer.parseInt(ultimateData.get(i)[0])) {
                    enemyRewards.xp = Integer.parseInt(ultimateData.get(i)[25]);
                    enemyRewards.gold = Integer.parseInt(ultimateData.get(i)[26]);
                    enemyRewards.add(new CombatantStruct1a8.ItemDrop(Integer.parseInt(ultimateData.get(i)[28]), Integer.parseInt(ultimateData.get(i)[27])));
                    break;
                }
            }
        } else {
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
            hit.flags_00 = Short.parseShort(additionStats.get(addition.additionIndex * 8 + i)[0]);
            hit.totalFrames_02 = Short.parseShort(additionStats.get(addition.additionIndex * 8 + i)[1]);
            hit.overlayHitFrameOffset_04 = Short.parseShort(additionStats.get(addition.additionIndex * 8 + i)[2]);
            hit.totalSuccessFrames_06 = Short.parseShort(additionStats.get(addition.additionIndex * 8 + i)[3]);
            hit.damageMultiplier_08 = Short.parseShort(additionStats.get(addition.additionIndex * 8 + i)[4]);
            hit.spValue_0a = Short.parseShort(additionStats.get(addition.additionIndex * 8 + i)[5]);
            hit.audioFile_0c = Short.parseShort(additionStats.get(addition.additionIndex * 8 + i)[6]);
            hit.isFinalHit_0e = Short.parseShort(additionStats.get(addition.additionIndex * 8 + i)[7]);
            //hit._10 = Short.parseShort(additionStats.get(addition.additionIndex * 8 + i)[8]);
            //hit._12 = Short.parseShort(additionStats.get(addition.additionIndex * 8 + i)[9]);
            //hit._14 = Short.parseShort(additionStats.get(addition.additionIndex * 8 + i)[10]);
            //hit.hitDistanceFromTarget_16 = Short.parseShort(additionStats.get(addition.additionIndex * 8 + i)[11]);
            hit.framesToHitPosition_18 = Short.parseShort(additionStats.get(addition.additionIndex * 8 + i)[12]);
            //hit._1a = Short.parseShort(additionStats.get(addition.additionIndex * 8 + i)[13]);
            hit.framesPostFailure_1c = Short.parseShort(additionStats.get(addition.additionIndex * 8 + i)[14]);
            hit.overlayStartingFrameOffset_1e = Short.parseShort(additionStats.get(addition.additionIndex * 8 + i)[15]);
        }
    }

    @EventListener
    public void additionMulti(final AdditionHitMultiplierEvent multiplier) {
        multiplier.additionSpMulti = Integer.parseInt(additionMultiStats.get(multiplier.additionId)[(multiplier.additionLevel) * 4 + 2]);
        multiplier.additionDmgMulti = Integer.parseInt(additionMultiStats.get(multiplier.additionId)[(multiplier.additionLevel) * 4 + 3]);
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

        if (difficulty.equals("Hard Mode") || difficulty.equals("US + Hard Bosses") || difficulty.equals("Hell Mode") || difficulty.equals("Hard + Hell Bosses")) {
            dramodBurnStacks(spellId);
        }
    }

    @EventListener
    public void shopItem(final ShopItemEvent shopItem) {
        shopItem.itemId = Integer.parseInt(shopItems.get(shopItem.shopId)[shopItem.slotId]);
        shopItem.price = Integer.parseInt(shopPrices.get(shopItem.itemId)[0]) * 2;

        if (shopItem.shopId == 40) {
            if (shopItem.itemId == 211) {
                shopItem.price = 300;
            } else if (shopItem.itemId == 221) {
                shopItem.price = 600;
            } else if (shopItem.itemId == 233) {
                shopItem.price = 900;
            }
        } else if (shopItem.shopId == 41) {
            shopItem.price = 1000;
        }
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

        if (difficulty.equals("Hard Mode") || difficulty.equals("US + Hard Bosses") || difficulty.equals("Hell Mode") || difficulty.equals("Hard + Hell Bosses")) {
            /*
                ATTACKING PLAYER
             */
            if (attack.attacker instanceof PlayerBattleObject player) {
                if (player.isDragoon() && attack.attackType.isPhysical()) {
                    if (player.element == dragoonSpaceElement_800c6b64) { //Dragoon Space physical boost
                        if (player.charId_272 == 7) {
                            attack.damage *= 1.2;
                        } else {
                            attack.damage *= 1.5;
                        }
                    } else {
                        if (player.element == Element.fromFlag(0x80) && dragoonSpaceElement_800c6b64 == Element.fromFlag(0x8)) { //Divine Dart special physical boost
                            if (player.equipment0_11e == 189) {
                                attack.damage *= 1.1;
                            } else {
                                attack.damage *= 1.5;
                            }
                        }
                    }
                }

                if (attack.defender instanceof MonsterBattleObject) {
                    int level = player.level_04;
                    if (attack.attackType.isPhysical() && (player.charId_272 == 2 || player.charId_272 == 8)) { //Shana AT Boost
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
                        attack.damage = (int) Math.round(attack.damage * boost);
                    }
                }

                if (player.spellId_4e >= 84) { //Item Spells In Dragoon
                    if (player.charId_272 != 4) {
                        bonusItemSP[player.charSlot_276] = true;
                    }

                    if (player.charId_272 == 3) {
                        attack.damage *= 1.7;
                    } else if (player.charId_272 == 5) {
                        attack.damage *= 1.5;
                    } else if (player.charId_272 == 7) {
                        attack.damage *= 2.2;
                    }

                    if (dragonBlockStaff) {
                        attack.damage /= 8;
                    }
                }

                if (player.charId_272 == 2 || player.charId_272 == 8) {
                    if (player.spellId_4e == 10 || player.spellId_4e == 65) { //Star Children full heal on exit
                        shanaStarChildrenHeal[player.charSlot_276] = true;
                    }
                }

                if (attack.attacker.charId_272 == 0) {
                    if (burnStackMode) {
                        if (burnStacks == burnStacksMax) {
                            if (player.spellId_4e == 0) {
                                attack.damage *= (1 + (burnStacks * dmgPerBurn)) * (Integer.parseInt(spellStats.get(2)[3]) / Integer.parseInt(spellStats.get(0)[3])) * 1.5;
                            } else if (player.spellId_4e == 1) {
                                attack.damage *= (1 + (burnStacks * dmgPerBurn)) * Integer.parseInt(spellStats.get(3)[3]) / Integer.parseInt(spellStats.get(1)[3]);
                            } else if (player.spellId_4e == 2) {
                                attack.damage *= (1 + (burnStacks * dmgPerBurn)) * 1.5;
                            } else {
                                attack.damage *= 1 + (burnStacks * dmgPerBurn);
                            }
                        } else {
                            attack.damage *= 1 + (burnStacks * dmgPerBurn) * 1.5;
                        }
                    } else {
                        if (attack.attackType == AttackType.DRAGOON_MAGIC_STATUS_ITEMS && !burnAdded) {
                            if (player.spellId_4e == 0 || player.spellId_4e == 84) {
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

                if (attack.attacker.charId_272 == 3) {
                    if (player.spellId_4e == 15) {
                        for (int i = 0; i < allBobjCount_800c66d0.get(); i++) {
                            final ScriptState<? extends BattleObject27c> state = battleState_8006e398.allBobjs_e0c[i];
                            final BattleObject27c bobj = state.innerStruct_00;
                            if (bobj instanceof PlayerBattleObject) {
                                final int playerHealedHP = bobj.stats.getStat(CoreMod.HP_STAT.get()).getCurrent();
                                final int roseMaxHP = player.stats.getStat(CoreMod.HP_STAT.get()).getMax();
                                if (playerHealedHP > 0) {
                                    bobj.stats.getStat(CoreMod.HP_STAT.get()).setCurrent((int) Math.min(bobj.stats.getStat(CoreMod.HP_STAT.get()).getMax(), (playerHealedHP + Math.round(roseMaxHP * player.dlevel_06 * 0.0425d))));
                                }
                            }
                        }
                    } else if (player.spellId_4e == 19) {
                        player.stats.getStat(CoreMod.HP_STAT.get()).setCurrent((int) Math.min(player.stats.getStat(CoreMod.HP_STAT.get()).getMax(), player.stats.getStat(CoreMod.HP_STAT.get()).getCurrent() + attack.damage * 0.1d));
                    }
                }

                if (attack.attacker.charId_272 == 1 || attack.attacker.charId_272 == 5) {
                    if (windMark[attack.defender.charSlot_276] == 0 && attack.attackType.isMagical() && player.isDragoon()) { //Add wind marks
                        if (player.spellId_4e == 5 || player.spellId_4e == 14 || player.spellId_4e == 91) {
                            windMark[attack.defender.charSlot_276] = 1;
                        } else if (player.spellId_4e == 7 || player.spellId_4e == 18) {
                            windMark[attack.defender.charSlot_276] = 2;
                        } else if (player.spellId_4e == 8) {
                            windMark[attack.defender.charSlot_276] = 3;
                        }
                    }
                }

                if (player.equipment0_11e == 167 && attack.attackType.isPhysical()) { //Giant Axe
                    if (new Random().nextInt(0, 99) < 20) {
                        player.guard_54 = 1;
                    }
                }

                if (player.equipment0_11e == 168 && attack.attackType.isPhysical()) { //Dragon Beater
                    final int heal = (int) Math.round(attack.damage * 0.01d);
                    final int hp = player.stats.getStat(CoreMod.HP_STAT.get()).getCurrent();
                    final int sp = player.stats.getStat(CoreMod.SP_STAT.get()).getCurrent();
                    player.stats.getStat(CoreMod.HP_STAT.get()).setCurrent(hp + Math.min(1000, heal));
                    player.stats.getStat(CoreMod.SP_STAT.get()).setCurrent(sp + Math.min(100, heal));
                }

                if (player.equipment0_11e == 169 && player.isDragoon()) { //Ouroboros
                    final int dragoonTurns = player.charSlot_276 == 0 ? battleState_8006e398.dragoonTurnsSlot1_294 : player.charSlot_276 == 1 ? battleState_8006e398.dragoonTurnsSlot2_298 : battleState_8006e398.dragoonTurnsSlot3_29c;
                    final int sp = player.stats.getStat(CoreMod.SP_STAT.get()).getCurrent();
                    if (player.isDragoon() && dragoonTurns >= 2 && sp >= 200) {
                        player.stats.getStat(CoreMod.SP_STAT.get()).setCurrent(sp - 100);
                        if (player.charSlot_276 == 0) {
                            battleState_8006e398.dragoonTurnsSlot1_294 = player.stats.getStat(CoreMod.SP_STAT.get()).getCurrent() / 100;
                        } else if (player.charSlot_276 == 1) {
                            battleState_8006e398.dragoonTurnsSlot2_298 = player.stats.getStat(CoreMod.SP_STAT.get()).getCurrent() / 100;
                        } else if (player.charSlot_276 == 2) {
                            battleState_8006e398.dragoonTurnsSlot3_29c = player.stats.getStat(CoreMod.SP_STAT.get()).getCurrent() / 100;
                        }
                        attack.damage *= 2;
                        ouroboros[player.charSlot_276] = true;
                    }
                }

                if (player.equipment0_11e == 170) { //Elemental Arrow
                    if (attack.defender instanceof MonsterBattleObject monster && attack.attackType.isPhysical()) {
                        ArrayList<Element> elementsCalculated = new ArrayList<>();
                        for (int i = 0; i < elementArrowsElements.size(); i++) {
                            if (elementArrowsElements.get(i) != null) {
                                if (!elementsCalculated.contains(elementArrowsElements.get(i))) {
                                    elementsCalculated.add(elementArrowsElements.get(i));
                                    if (dragoonSpaceElement_800c6b64 != null) {
                                        int damage = dragoonSpaceElement_800c6b64.adjustDragoonSpaceDamage(attack.attackType, attack.damage, elementArrowsElements.get(i));
                                        if (damage > attack.damage) {
                                            attack.damage = damage;

                                            damage = monster.getElement().adjustAttackingElementalDamage(attack.attackType, attack.damage, elementArrowsElements.get(i));
                                            if (damage != attack.damage) {
                                                attack.damage = damage;
                                            }
                                        }
                                    } else {
                                        final int damage = monster.getElement().adjustAttackingElementalDamage(attack.attackType, attack.damage, elementArrowsElements.get(i));
                                        if (damage > attack.damage) {
                                            attack.damage = damage;
                                        }
                                    }
                                }
                            }
                        }

                        if (new Random().nextInt(0, 99) < 40 && gameState_800babc8.items_2e9.size() < CONFIG.getConfig(CoreMod.INVENTORY_SIZE_CONFIG.get())) {
                            Scus94491BpeSegment_8002.giveItem(0xC9);
                        }
                    }

                    if (player.itemId_52 > 0) {
                        player.stats.getStat(CoreMod.SP_STAT.get()).setCurrent(player.stats.getStat(CoreMod.SP_STAT.get()).getCurrent() + 100);
                    }
                }

                if (player.equipment0_11e == 171) { //Magic Hammer
                    if (attack.attackType.isPhysical()) {
                        attack.damage = 0;
                    }
                    player.stats.getStat(CoreMod.MP_STAT.get()).setCurrent(player.stats.getStat(CoreMod.MP_STAT.get()).getCurrent() + 8);
                }

                if (player.equipment0_11e == 172) { //Overcharge Glove
                    if (attack.defender instanceof MonsterBattleObject monster) {
                        if (monster.getElement() == CoreMod.THUNDER_ELEMENT.get()) {
                            attack.damage *= 3;
                        }
                    }
                }

                for (int i = 0; i < 3; i++) {
                    if (ringOfElements[i] > 0 && dragoonSpaceElement_800c6b64 == null) { //Ring of Elements
                        if (attack.defender instanceof MonsterBattleObject monster) {
                            if (attack.attackType.isPhysical()) {
                                for (Element e : player.equipmentAttackElements_1c) {
                                    final int damage = ringOfElementsElement[i].adjustDragoonSpaceDamage(attack.attackType, attack.damage, e);
                                    if (damage != attack.damage) {
                                        attack.damage = damage;
                                    }
                                }
                            } else {
                                try {
                                    final int damage = attack.attacker.spell_94.element_08.adjustDragoonSpaceDamage(attack.attackType, attack.damage, ringOfElementsElement[i]);
                                    if (damage != attack.damage) {
                                        attack.damage = damage;
                                    }
                                } catch (Exception ignored) {}

                                try {
                                    final int damage = attack.attacker.item_d4.element_01.adjustDragoonSpaceDamage(attack.attackType, attack.damage, ringOfElementsElement[i]);
                                    if (damage != attack.damage) {
                                        attack.damage = damage;
                                    }
                                } catch (Exception ignored) {}
                            }
                        }
                    }
                }

                if (attack.defender instanceof MonsterBattleObject monster) { //Haschel in party thunder charge
                    try {
                        if (attack.attacker.spell_94.element_08 == CoreMod.THUNDER_ELEMENT.get() && new Random().nextBoolean()) {
                            thunderCharge[monster.charSlot_276] = Math.min(10, thunderCharge[monster.charSlot_276] + 1);
                        }
                    } catch (Exception ignored) {}

                    try {
                        if (attack.attacker.item_d4.element_01 == CoreMod.THUNDER_ELEMENT.get() && new Random().nextBoolean()) {
                            thunderCharge[monster.charSlot_276] = Math.min(10, thunderCharge[monster.charSlot_276] + 1);
                        }
                    } catch (Exception ignored) {}

                    if (attack.attackType.isPhysical() && player.equipmentAttackElements_1c.contains(CoreMod.THUNDER_ELEMENT.get()) && new Random().nextBoolean()) {
                        thunderCharge[monster.charSlot_276] = Math.min(10, thunderCharge[monster.charSlot_276] + 1);
                    }
                }

                if (player.charSlot_276 == 4) { //Haschel thunder charge on physical and spark net boost on max stacks and thunder element
                    if (attack.defender instanceof MonsterBattleObject monster) {
                        if (player.dlevel_06 > 0) {
                            if (attack.attackType.isPhysical() && new Random().nextBoolean()) {
                                thunderCharge[monster.charSlot_276] = Math.min(10, thunderCharge[monster.charSlot_276] + 1);
                            } else {
                                if (player.isDragoon() && player.spellId_4e == 86) {
                                    if (thunderCharge[monster.charSlot_276] == 10) {
                                        thunderCharge[monster.charSlot_276] = 0;
                                        attack.damage *= monster.getElement() == CoreMod.THUNDER_ELEMENT.get() ? 8.8 : 2.93333;
                                    }
                                }
                            }
                        }
                    }
                }

                if (attack.defender instanceof MonsterBattleObject monster) {
                    if (windMark[attack.defender.charSlot_276] > 0) { //Wind mark turn value reduction
                        monster.turnValue_4c = Math.max(0, monster.turnValue_4c - 10);
                        windMark[attack.defender.charSlot_276] -= 1;
                    }
                }

                if (attack.defender instanceof PlayerBattleObject defender) { //If Meru's in Wingly Boost Mode all healing is 0
                    if (meruBoost[defender.charSlot_276]) {
                        try {
                            if (Integer.parseInt(spellStats.get(attack.attacker.spellId_4e)[4]) > 0) {
                                attack.damage = 0;
                            }
                        } catch (Exception ignored) {}

                        try {
                            if (Integer.parseInt(itemStats.get(attack.attacker.itemId_52)[11]) == 128) {
                                attack.damage = 0;
                            }
                        } catch (Exception ignored) {}
                    }
                }

                if (bonusItemSP[player.charSlot_276]) {
                    player.itemId_52 = 0;
                }
            }

            /*
                DEFENDING MONSTER
             */

            if (attack.defender instanceof PlayerBattleObject defender) {
                if (defender.equipment4_126 == 183) { //Ring of Shielding
                    final int hp = defender.stats.getStat(CoreMod.HP_STAT.get()).getCurrent();
                    if ((hp - attack.damage) <= 0 && new Random().nextInt(0, 99) < 35) {
                        for (int i = 0; i < allBobjCount_800c66d0.get(); i++) {
                            final ScriptState<? extends BattleObject27c> state = battleState_8006e398.allBobjs_e0c[i];
                            final BattleObject27c bobj = state.innerStruct_00;
                            if (bobj == defender) {
                                battleState_8006e398.specialEffect_00[i].shieldsSigStoneCharmTurns_1c = 5;
                            }
                        }
                    }
                }

                if (defender.charId_272 == 6) { //If Meru dies in Wingly Boost turn it off
                    final int hp = defender.stats.getStat(CoreMod.HP_STAT.get()).getCurrent();
                    if (meruBoost[defender.charSlot_276] && hp - attack.damage <= 0) {
                        meruBoostTurns[defender.charSlot_276] = 0;
                        meruBoost[defender.charSlot_276] = false;
                        defender.stats.getStat(CoreMod.HP_STAT.get()).setMaxRaw(meruMaxHpSave[defender.charSlot_276]);
                        defender.magicDefence_3a = meruMDFSave[defender.charSlot_276];
                    }
                }

                if (ringOfElements[defender.charSlot_276] > 0) {
                    final int hp = defender.stats.getStat(CoreMod.HP_STAT.get()).getCurrent();
                    if (attack.damage <= 0) {
                        ringOfElements[defender.charSlot_276] = 0;
                    }
                }

                final int level = defender.level_04;

                if (attack.attackType.isPhysical()) { //DF Boost
                    if (defender.charId_272 == 2 || defender.charId_272 == 8) { //Shana
                        if (level >= 30) {
                            attack.damage = (int) Math.round(attack.damage / 1.12d);
                        }
                    }

                    if (defender.charId_272 == 3 && level >= 30) { //Rose
                        attack.damage = (int) Math.round(attack.damage / 1.11d);
                    }

                    if (defender.charId_272 == 6 && level >= 30) { //Meru
                        attack.damage = (int) Math.round(attack.damage / 1.26d);
                    }
                }

                if (attack.attackType.isMagical()) {
                    Element attackElement = null;
                    final int armorEquipped = defender.equipment2_122;

                    try {
                        attackElement = attack.attacker.item_d4.element_01;
                    } catch (Exception ignored) {}

                    if (attackElement == null) {
                        attackElement = attack.attacker.spell_94.element_08;
                    }

                    //Divine Dragon Armor 15% elemental reduction instead of half
                    if (attackElement == CoreMod.FIRE_ELEMENT.get() && armorEquipped == 51) {
                        attack.damage = (int) Math.round(attack.damage / 1.15d);;
                    } else if (attackElement == CoreMod.WIND_ELEMENT.get() && armorEquipped == 52) {
                        attack.damage = (int) Math.round(attack.damage / 1.15d);;
                    } else if (attackElement == CoreMod.EARTH_ELEMENT.get() && armorEquipped == 56) {
                        attack.damage = (int) Math.round(attack.damage / 1.15d);;
                    } else if (attackElement == CoreMod.THUNDER_ELEMENT.get() && armorEquipped == 61) {
                        attack.damage = (int) Math.round(attack.damage / 1.15d);;
                    } else if (attackElement == CoreMod.LIGHT_ELEMENT.get() && armorEquipped == 67) {
                        attack.damage = (int) Math.round(attack.damage / 1.15d);;
                    } else if (attackElement == CoreMod.DARK_ELEMENT.get() && armorEquipped == 68) {
                        attack.damage = (int) Math.round(attack.damage / 1.15d);;
                    } else if (attackElement == CoreMod.WATER_ELEMENT.get() && armorEquipped == 69) {
                        attack.damage = (int) Math.round(attack.damage / 1.15d);;
                    }
                }
            }
        }

        if(ultimateBattle) {
            if(attack.attacker instanceof PlayerBattleObject player && attack.defender instanceof MonsterBattleObject) {
                if(ultimatePenality[player.charSlot_276][1] > 1) { //Damage penalty for over leveled ultiamte boss
                    attack.damage /= ultimatePenality[player.charSlot_276][1];
                }
            }

            if(attack.attacker instanceof MonsterBattleObject && attack.defender instanceof PlayerBattleObject player) {
                if(ultimatePenality[player.charSlot_276][1] > 1) { //Damage penalty for over leveled ultiamte boss
                    attack.damage *= ultimatePenality[player.charSlot_276][1];
                }
            }
        }

        /*if (attack.attacker instanceof MonsterBattleObject monster && attack.defender instanceof PlayerBattleObject player) {
            try {
                System.out.println("-------------------------------");
                if (attack.attackType.isPhysical()) {
                    System.out.println("[DRAMODTEST] ID:  " + monster.spellId_4e);
                    System.out.println("[DRAMODTEST] DMG: " + spellStats_800fa0b8[monster.spellId_4e].multi_04);
                } else {
                    System.out.println("[DRAMODTEST] ID:  " + monster.spellId_4e);
                    System.out.println("[DRAMODTEST] DMG: " + spellStats_800fa0b8[monster.spellId_4e].multi_04);
                    System.out.println("[DRAMODTEST] IID: " + monster.itemId_52);
                    System.out.println("[DRAMODTEST] ITM: " + monster.item_d4.damage_05);
                }
            } catch (Exception ignored) {}
        }*/

        if (ultimateBattle) { //Ultimate Boss effects per attack
            if (attack.attacker instanceof MonsterBattleObject monster) {
                UltimateGuardBreak((PlayerBattleObject) attack.defender, monster, attack);
                UltimateMPAttack((PlayerBattleObject) attack.defender, monster, attack);
            }
        }

        UpdateEnrageMode(attack);
        UpdateElementalBomb(attack);
        UpdateDamageTracker(attack);
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

        burnStacks = 0;
        armorOfLegendTurns = 0;
        legendCasqueTurns = 0;
        dragonBlockStaff = false;
        burnStackMode = false;
        flowerStormOverride = false;
        damageTrackerPrinted = false;
        Arrays.fill(enrageMode, 0);
        Arrays.fill(windMark, 0);
        Arrays.fill(thunderCharge, 0);
        Arrays.fill(elementalAttack, false);
        Arrays.fill(shanaStarChildrenHeal, false);
        Arrays.fill(shanaRapidFireContinue, false);
        Arrays.fill(shanaRapidFire, false);
        Arrays.fill(shanaRapidFireCount, 0);
        Arrays.fill(meruBoost, false);
        Arrays.fill(bonusItemSP, false);
        Arrays.fill(ouroboros, false);
        Arrays.fill(meruBoostTurns, 0);
        Arrays.fill(meruMaxHpSave, 0);
        Arrays.fill(meruMDFSave, 0);
        Arrays.fill(damageTracker[0], 0);
        Arrays.fill(damageTracker[1], 0);
        Arrays.fill(damageTracker[2], 0);
        Arrays.fill(ringOfElements, 0);
        Arrays.fill(ringOfElementsElement, null);
        damageTrackerLog.clear();
        elementArrowsElements.clear();

        final String difficulty = GameEngine.CONFIG.getConfig(DIFFICULTY.get());

        if (difficulty.equals("Hard Mode") || difficulty.equals("US + Hard Bosses") || difficulty.equals("Hell Mode") || difficulty.equals("Hard + Hell Bosses")) {
            for (int i = 0; i < allBobjCount_800c66d0.get(); i++) {
                final ScriptState<? extends BattleObject27c> state = battleState_8006e398.allBobjs_e0c[i];
                final BattleObject27c bobj = state.innerStruct_00;
                if (bobj instanceof PlayerBattleObject player) {
                  player.equipmentElementalImmunity_22.clear();

                    if (player.charId_272 == 7) { //Kongol SPD reduction
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

                    int crystalItems = 0;
                    if (player.equipment2_122 == 173) { //Crystal Armor
                        crystalItems++;
                    }

                    if (player.equipment1_120 == 174) { //Crystal Hat
                        crystalItems++;
                    }

                    if (player.equipment3_124 == 175) { //Crystal Boots
                        crystalItems++;
                    }

                    if (player.equipment4_126 == 176) { //Crystal Ring
                        crystalItems++;
                    }

                    if (crystalItems > 3) {
                        player.attack_34 += 60;
                        player.magicAttack_36 += 60;
                        player.defence_38 += 60;
                        player.magicDefence_3a += 60;
                        player.attackHit_3c += 60;
                        player.magicHit_3e += 60;
                        player.attackAvoid_40 += 12;
                        player.magicAvoid_42 += 12;
                        if (player.charId_272 != 7) {
                            player.stats.getStat(CoreMod.SPEED_STAT.get()).setRaw(player.stats.getStat(CoreMod.SPEED_STAT.get()).get() + 12);
                        } else {
                            player.stats.getStat(CoreMod.SPEED_STAT.get()).setRaw(player.stats.getStat(CoreMod.SPEED_STAT.get()).get() + 6);
                        }
                        player.stats.getStat(CoreMod.HP_STAT.get()).setMaxRaw((int) Math.round(player.stats.getStat(CoreMod.HP_STAT.get()).getMax() * 1.3d));
                        player.stats.getStat(CoreMod.MP_STAT.get()).setMaxRaw((int) Math.round(player.stats.getStat(CoreMod.MP_STAT.get()).getMax() * 1.3d));
                        player.hpRegen_134 = 10;
                        player.mpRegen_136 = 10;
                        player.spRegen_138 = 100;
                    } else if (crystalItems > 2) {
                        player.attack_34 += 30;
                        player.magicAttack_36 += 30;
                        player.defence_38 += 30;
                        player.magicDefence_3a += 30;
                        player.attackHit_3c += 30;
                        player.magicHit_3e += 30;
                        player.attackAvoid_40 += 6;
                        player.magicAvoid_42 += 6;
                        if (player.charId_272 != 7) {
                            player.stats.getStat(CoreMod.SPEED_STAT.get()).setRaw(player.stats.getStat(CoreMod.SPEED_STAT.get()).get() + 6);
                        } else {
                            player.stats.getStat(CoreMod.SPEED_STAT.get()).setRaw(player.stats.getStat(CoreMod.SPEED_STAT.get()).get() + 3);
                        }
                        player.hpRegen_134 = 4;
                        player.mpRegen_136 = 4;
                        player.spRegen_138 = 40;
                    } else if (crystalItems > 1) {
                        player.attack_34 += 5;
                        player.magicAttack_36 += 5;
                        player.defence_38 += 5;
                        player.magicDefence_3a += 5;
                        player.attackHit_3c += 5;
                        player.magicHit_3e += 5;
                        player.attackAvoid_40 += 1;
                        player.magicAvoid_42 += 1;
                        if (player.charId_272 != 7) {
                            player.stats.getStat(CoreMod.SPEED_STAT.get()).setRaw(player.stats.getStat(CoreMod.SPEED_STAT.get()).get() + 1);
                        }
                    }

                    if (player.equipment4_126 == 177) { //Ring of Reversal
                        int df = player.defence_38;
                        int mdf = player.magicDefence_3a;
                        player.magicDefence_3a = df;
                        player.defence_38 = mdf;
                        if (player.defence_38 > player.magicDefence_3a) {
                            player.stats.getStat(CoreMod.HP_STAT.get()).setMaxRaw((int) Math.round(player.stats.getStat(CoreMod.HP_STAT.get()).getMax() * 1.5d));
                        } else {
                            player.stats.getStat(CoreMod.HP_STAT.get()).setMaxRaw((int) Math.round(player.stats.getStat(CoreMod.HP_STAT.get()).getMax() / 1.5d));
                            player.spMultiplier_128 += 35;
                        }
                    }

                    if (player.equipment4_126 == 185) { //The One Ring
                        player.stats.getStat(CoreMod.HP_STAT.get()).setCurrent(1);
                        player.stats.getStat(CoreMod.HP_STAT.get()).setMaxRaw(1);
                        player.attackAvoid_40 = 80;
                        player.magicAvoid_42 = 80;
                    }

                    if (player.equipment2_122 == 187) { //Divine DG Armor
                        player.spPerPhysicalHit_12a += 10;
                        player.spPerMagicalHit_12e += 10;
                    }

                    if (player.equipment1_120 == 188) { //Halo of Balance
                        player.stats.getStat(CoreMod.HP_STAT.get()).setMaxRaw((int) Math.round(player.stats.getStat(CoreMod.HP_STAT.get()).getMax() * 1.3d));
                        player.stats.getStat(CoreMod.MP_STAT.get()).setMaxRaw((int) Math.round(player.stats.getStat(CoreMod.MP_STAT.get()).getMax() * 1.3d));
                    }

                    if (player.equipment0_11e == 189) { //Firebrand
                        player.equipmentAttackElements_1c.add(CoreMod.FIRE_ELEMENT.get());
                    }

                    if (player.equipment4_126 == 190) { //Super Spirit Ring
                        player.spMultiplier_128 = -100;
                    }

                    if (difficulty.equals("Hell Mode") || difficulty.equals("Hard + Hell Bosses")) {
                        int flowerStormTurns = GameEngine.CONFIG.getConfig(FLOWER_STORM.get());
                        int flowerStorm = -1;
                        if (player.charId_272 == 1) {
                            flowerStorm = 7;
                        } else if (player.charId_272 == 5) {
                            flowerStorm = 26;
                        }
                        if (flowerStorm > 0) {
                            Bttl_800c.spellStats_800fa0b8[flowerStorm] = new SpellStats0c(spellStats.get(flowerStorm)[12],
                            spellStats.get(flowerStorm)[13].substring(0, spellStats.get(flowerStorm)[13].length() - 1) + GameEngine.CONFIG.getConfig(FLOWER_STORM.get()),
                            Integer.parseInt(spellStats.get(flowerStorm)[0]),
                            Integer.parseInt(spellStats.get(flowerStorm)[1]),
                            Integer.parseInt(spellStats.get(flowerStorm)[2]),
                            Integer.parseInt(spellStats.get(flowerStorm)[3]),
                            Integer.parseInt(spellStats.get(flowerStorm)[4]),
                            Integer.parseInt(spellStats.get(flowerStorm)[5]),
                            GameEngine.CONFIG.getConfig(FLOWER_STORM.get()) * 20,
                            Integer.parseInt(spellStats.get(flowerStorm)[7]),
                            Element.fromFlag(Integer.parseInt(spellStats.get(flowerStorm)[8])),
                            Integer.parseInt(spellStats.get(flowerStorm)[9]),
                            Integer.parseInt(spellStats.get(flowerStorm)[10]),
                            Integer.parseInt(spellStats.get(flowerStorm)[11]));
                        }

                        if (player.charId_272 == 2 || player.charId_272 == 8) {
                            if (player.dlevel_06 >= 2) {
                                int moonLight;
                                int gatesOfHeaven;
                                if (player.charId_272 == 2) {
                                    moonLight = 11;
                                    gatesOfHeaven = 12;
                                } else {
                                    moonLight = 66;
                                    gatesOfHeaven = 67;
                                }

                                Bttl_800c.spellStats_800fa0b8[moonLight] = new SpellStats0c(spellStats.get(moonLight)[12],
                                        spellStats.get(moonLight)[13],
                                        Integer.parseInt(spellStats.get(moonLight)[0]),
                                        Integer.parseInt(spellStats.get(moonLight)[1]),
                                        Integer.parseInt(spellStats.get(moonLight)[2]),
                                        Integer.parseInt(spellStats.get(moonLight)[3]),
                                        Integer.parseInt(spellStats.get(moonLight)[4]),
                                        Integer.parseInt(spellStats.get(moonLight)[5]),
                                        20,
                                        Integer.parseInt(spellStats.get(moonLight)[7]),
                                        Element.fromFlag(Integer.parseInt(spellStats.get(moonLight)[8])),
                                        Integer.parseInt(spellStats.get(moonLight)[9]),
                                        Integer.parseInt(spellStats.get(moonLight)[10]),
                                        Integer.parseInt(spellStats.get(moonLight)[11]));
                                if (player.dlevel_06 >= 4 && player.stats.getStat(CoreMod.MP_STAT.get()).getMax() >= 120) {
                                    Bttl_800c.spellStats_800fa0b8[gatesOfHeaven] = new SpellStats0c(spellStats.get(gatesOfHeaven)[12],
                                            spellStats.get(gatesOfHeaven)[13],
                                            Integer.parseInt(spellStats.get(gatesOfHeaven)[0]),
                                            Integer.parseInt(spellStats.get(gatesOfHeaven)[1]),
                                            Integer.parseInt(spellStats.get(gatesOfHeaven)[2]),
                                            Integer.parseInt(spellStats.get(gatesOfHeaven)[3]),
                                            Integer.parseInt(spellStats.get(gatesOfHeaven)[4]),
                                            Integer.parseInt(spellStats.get(gatesOfHeaven)[5]),
                                            player.stats.getStat(CoreMod.MP_STAT.get()).getMax() / 3,
                                            Integer.parseInt(spellStats.get(gatesOfHeaven)[7]),
                                            Element.fromFlag(Integer.parseInt(spellStats.get(gatesOfHeaven)[8])),
                                            Integer.parseInt(spellStats.get(gatesOfHeaven)[9]),
                                            Integer.parseInt(spellStats.get(gatesOfHeaven)[10]),
                                            Integer.parseInt(spellStats.get(gatesOfHeaven)[11]));
                                }
                            }
                        }
                    }
                }

                elementArrowsElements.add(bobj.getElement());
                if (bobj instanceof PlayerBattleObject && bobj.getElement() == CoreMod.FIRE_ELEMENT.get() && gameState_800babc8.goods_19c[0] << 7 == 1) {
                    elementArrowsElements.add(CoreMod.DIVINE_ELEMENT.get());
                }
            }
        }

        if (difficulty.equals("Hell Mode") || difficulty.equals("Hard + Hell Bosses")) {
            GameEngine.EVENTS.postEvent(new HellModeAdjustmentEvent());
        }

        for(int i = 0; i < allBobjCount_800c66d0.get(); i++) {
            final ScriptState<? extends BattleObject27c> state = battleState_8006e398.allBobjs_e0c[i];
            final BattleObject27c bobj = state.innerStruct_00;
            if (bobj instanceof PlayerBattleObject player) {
                damageTrackerEquips[player.charSlot_276][0] = player.equipment0_11e;
                damageTrackerEquips[player.charSlot_276][1] = player.equipment1_120;
                damageTrackerEquips[player.charSlot_276][2] = player.equipment2_122;
                damageTrackerEquips[player.charSlot_276][3] = player.equipment3_124;
                damageTrackerEquips[player.charSlot_276][4] = player.equipment4_126;
            }
        }

        if (ultimateBattle) {
            for(int i = 0; i < allBobjCount_800c66d0.get(); i++) {
                final ScriptState<? extends BattleObject27c> state = battleState_8006e398.allBobjs_e0c[i];
                final BattleObject27c bobj = state.innerStruct_00;
                if (bobj instanceof PlayerBattleObject player) {
                    ultimatePenality[player.charSlot_276][0] = 1;
                    ultimatePenality[player.charSlot_276][1] = 1;

                    if (player.level_04 > ultimateLevelCap) {
                        int levelDifference = player.level_04 - ultimateLevelCap;

                        if (ultimateLevelCap == 30) {
                            if (Math.round(levelDifference / 10d) == 1) { //Level 40
                                ultimatePenality[player.charSlot_276][0] = 1.5;
                                ultimatePenality[player.charSlot_276][1] = 1.26;
                            } else if (Math.round(levelDifference / 10d) == 2) { //Level 50
                                ultimatePenality[player.charSlot_276][0] = 2.6;
                                ultimatePenality[player.charSlot_276][1] = 1.53;
                            } else if (Math.round(levelDifference / 10d) == 3) { //Level 60
                                ultimatePenality[player.charSlot_276][0] = 3.4;
                                ultimatePenality[player.charSlot_276][1] = 1.89;
                            }
                        } else if (ultimateLevelCap == 40) {
                            if (Math.round(levelDifference / 10d) == 1) { //Level 50
                                ultimatePenality[player.charSlot_276][0] = 1.7;
                                ultimatePenality[player.charSlot_276][1] = 1.17;
                            } else if (Math.round(levelDifference / 10d) == 2) { //Level 60
                                ultimatePenality[player.charSlot_276][0] = 2.2;
                                ultimatePenality[player.charSlot_276][1] = 1.35;
                            }
                        } else if (ultimateLevelCap == 50) {
                            if (Math.round(levelDifference / 10d) == 1) { //Level 60
                                ultimatePenality[player.charSlot_276][0] = 1.3;
                                ultimatePenality[player.charSlot_276][1] = 1.08;
                            }
                        }
                    }

                    if (ultimatePenality[player.charSlot_276][0] > 1) {
                        int currentMax = player.stats.getStat(CoreMod.HP_STAT.get()).getMaxRaw();
                        player.stats.getStat(CoreMod.HP_STAT.get()).setMaxRaw(Math.round(Math.round((double) currentMax / ultimatePenality[player.charSlot_276][0])));
                    }

                    UltimateZeroSPStart(player);
                } else if (bobj instanceof MonsterBattleObject monster) {
                    int enemyId = monster.charId_272;
                    for (int x = 0; x < 86; x++) {
                        if (enemyId == Integer.parseInt(ultimateData.get(x)[0])) {
                            monster.stats.getStat(CoreMod.HP_STAT.get()).setMaxRaw(Integer.parseInt(ultimateData.get(x)[1]));
                            monster.stats.getStat(CoreMod.HP_STAT.get()).setCurrent(Integer.parseInt(ultimateData.get(x)[1]));
                            monster.attack_34 = Integer.parseInt(ultimateData.get(x)[3]);
                            monster.magicAttack_36 = Integer.parseInt(ultimateData.get(x)[4]);
                            monster.stats.getStat(CoreMod.SPEED_STAT.get()).setRaw(Integer.parseInt(ultimateData.get(x)[5]));
                            monster.defence_38 = Integer.parseInt(ultimateData.get(x)[6]);
                            monster.magicDefence_3a = Integer.parseInt(ultimateData.get(x)[7]);
                            monster.attackAvoid_40 = Integer.parseInt(ultimateData.get(x)[8]);
                            monster.magicAvoid_42 = Integer.parseInt(ultimateData.get(x)[9]);
                            monster.specialEffectFlag_14 = Integer.parseInt(ultimateData.get(x)[10]);
                            monster.monsterElement_72 = Element.fromFlag(Integer.parseInt(ultimateData.get(x)[12]));
                            monster.displayElement_1c = Element.fromFlag(Integer.parseInt(ultimateData.get(x)[12]));
                            monster.equipmentStatusResist_24 = Integer.parseInt(ultimateData.get(x)[13]);
                            monster.monsterElementalImmunity_74.clear();
                            if (Integer.parseInt(ultimateData.get(x)[13]) > 0)
                                monster.monsterElementalImmunity_74.add(Element.fromFlag(Integer.parseInt(ultimateData.get(x)[13])));
                            monster.monsterStatusResistFlag_76 = Integer.parseInt(ultimateData.get(x)[14]);
                            break;
                        }
                    }
                }
            }
        }

        UpdateMonsterHPNames();

        for (int i = 0; i < monsterCount_800c6768.get(); i++) {
            final MonsterBattleObject monster = battleState_8006e398.monsterBobjs_e50[i].innerStruct_00;
            int hp = monster.stats.getStat(CoreMod.HP_STAT.get()).getCurrent();
            damageTrackerPreviousHP[monster.charSlot_276] = hp;
        }

        if (difficulty.equals("Hell Mode") || difficulty.equals("Hard + Hell Bosses")) {
            GameEngine.EVENTS.postEvent(new HellModeAdjustmentEvent());
        }
    }

    public void UpdateMonsterHPNames() {
        if (GameEngine.CONFIG.getConfig(MONSTER_HP_NAMES.get()) == MonsterHPNames.ON) {
            for (int i = 0; i < 10; i++) {
                final ScriptState<? extends BattleObject27c> state = battleState_8006e398.allBobjs_e0c[i];
                if (state != null) {
                    final BattleObject27c bobj = state.innerStruct_00;
                    if (bobj instanceof MonsterBattleObject) {
                        int hp = bobj.stats.getStat(CoreMod.HP_STAT.get()).getCurrent();
                        currentEnemyNames_800c69d0.get(bobj.charSlot_276).set(String.valueOf(hp));
                    }
                }
            }
        }
    }

    public void UpdateEnrageMode(final AttackEvent attack) {
        if (GameEngine.CONFIG.getConfig(ENRAGE_MODE.get()) == EnrageMode.ON) {
            for (int i = 0; i < monsterCount_800c6768.get(); i++) {
                final MonsterBattleObject monster = battleState_8006e398.monsterBobjs_e50[i].innerStruct_00;
                int hp = monster.stats.getStat(CoreMod.HP_STAT.get()).getCurrent();
                int maxHp = monster.stats.getStat(CoreMod.HP_STAT.get()).getMax();
                if (hp <= maxHp / 2 && enrageMode[i] == 0) {
                    monster.attack_34 = (int) Math.round(monster.attack_34 * 1.1d);
                    monster.magicAttack_36 = (int) Math.round(monster.magicAttack_36 * 1.1d);
                    monster.defence_38 = (int) Math.round(monster.defence_38 * 1.1d);
                    monster.magicDefence_3a = (int) Math.round(monster.magicDefence_3a * 1.1d);
                    enrageMode[i] = 1;
                }
                if (hp <= maxHp / 4 && enrageMode[i] == 1) {
                    monster.attack_34 = (int) Math.round(monster.attack_34 * 1.136365d);
                    monster.magicAttack_36 = (int) Math.round(monster.magicAttack_36 * 1.136365d);
                    monster.defence_38 = (int) Math.round(monster.defence_38 * 1.136365d);
                    monster.magicDefence_3a = (int) Math.round(monster.magicDefence_3a * 1.136365d);
                    enrageMode[i] = 2;
                }
            }
        }
    }

    public void UpdateElementalBomb(final AttackEvent attack) {
        if (GameEngine.CONFIG.getConfig(ELEMENTAL_BOMB.get()) == ElementalBomb.ON) {
            if (attack.attacker instanceof PlayerBattleObject player) {
                try {
                    if (player.itemId_52 >= 49 && player.itemId_52 != 57 && attack.defender instanceof MonsterBattleObject monster) {
                        //for (int i = 0; i < monsterCount_800c6768.get(); i++) {
                            if (elementalBombTurns[monster.charSlot_276] == 0) {
                                Element swapTo;
                                if (player.itemId_52 == 50) { //Burning Wave
                                    swapTo = Element.fromFlag(0x80);
                                } else if (player.itemId_52 == 51) { //Frozen Jet
                                    swapTo = Element.fromFlag(0x1);
                                } else if (player.itemId_52 == 52) { //Down Burst
                                    swapTo = Element.fromFlag(0x40);
                                } else if (player.itemId_52 == 53) { //Gravity Grabber
                                    swapTo = Element.fromFlag(0x2);
                                } else if (player.itemId_52 == 54) { //Spectral Flash
                                    swapTo = Element.fromFlag(0x20);
                                } else if (player.itemId_52 == 55) { //Night Raid
                                    swapTo = Element.fromFlag(0x4);
                                } else if (player.itemId_52 == 56) { //Flash Hall
                                    swapTo = Element.fromFlag(0x10);
                                } else { //Psyche Bomb
                                    swapTo = Element.fromFlag(0x8);
                                }
                                elementalBombPreviousElement[monster.charSlot_276] = monster.getElement();
                                elementalBombTurns[monster.charSlot_276] = 5;
                                monster.monsterElement_72 = swapTo;
                                monster.displayElement_1c = swapTo;
                            }
                        }
                    //}
                } catch (Exception ignored) {}
            }
        }
    }

    public void UpdateDamageTracker(final AttackEvent attack) {
        if (GameEngine.CONFIG.getConfig(DAMAGE_TRACKER.get()) == DamageTracker.ON) {
            if (attack.attacker instanceof PlayerBattleObject player && attack.defender instanceof MonsterBattleObject monster) {
                if (player.isDragoon()) {
                    if (attack.attackType.isPhysical()) {
                        damageTrackerPreviousAttackType = 0;
                        damageTracker[player.charSlot_276][0] += attack.damage;
                        damageTrackerLog.add(charNames[gameState_800babc8.charIds_88[player.charSlot_276]] + " - D.Physical - " + attack.damage);
                    } else {
                        damageTrackerPreviousAttackType = 1;
                        damageTracker[player.charSlot_276][1] += attack.damage;
                        damageTrackerLog.add(charNames[gameState_800babc8.charIds_88[player.charSlot_276]] + " - D.Magical - " + attack.damage);
                    }
                } else {
                    if (attack.attackType.isPhysical()) {
                        damageTrackerPreviousAttackType = 2;
                        damageTracker[player.charSlot_276][2] += attack.damage;
                        damageTrackerLog.add(charNames[gameState_800babc8.charIds_88[player.charSlot_276]] + " - Physical - " + attack.damage);
                    } else {
                        damageTrackerPreviousAttackType = 3;
                        damageTracker[player.charSlot_276][3] += attack.damage;
                        damageTrackerLog.add(charNames[gameState_800babc8.charIds_88[player.charSlot_276]] + " - Magical - " + attack.damage);
                    }
                }

                int hp = monster.stats.getStat(CoreMod.HP_STAT.get()).getCurrent();
                if (attack.damage > hp && hp > 0 && hp != damageTrackerPreviousHP[monster.charSlot_276]) {
                    damageTracker[player.charSlot_276][4] = attack.damage - hp;
                }

                damageTrackerPreviousCharacter = player.charSlot_276;
                damageTrackerPreviousCharacterID = player.charId_272;
                damageTrackerPreviousHP[monster.charSlot_276] = hp - attack.damage;
            }
        }
    }

    public void UpdateItemMagicDamage() {
        if (GameEngine.CONFIG.getConfig(DAMAGE_TRACKER.get()) == DamageTracker.ON) {
            for (int i = 0; i < monsterCount_800c6768.get(); i++) {
                final MonsterBattleObject monster = battleState_8006e398.monsterBobjs_e50[i].innerStruct_00;
                int hp = monster.stats.getStat(CoreMod.HP_STAT.get()).getCurrent();
                if (hp < damageTrackerPreviousHP[monster.charSlot_276]) {
                    int difference = damageTrackerPreviousHP[monster.charSlot_276] - hp;
                    damageTracker[damageTrackerPreviousCharacter][damageTrackerPreviousAttackType] += difference;
                    damageTrackerLog.add(charNames[damageTrackerPreviousCharacterID] + " - Multiplier - " + difference);
                    damageTrackerPreviousHP[monster.charSlot_276] = hp;
                }
            }
        }
    }

    public void UltimateZeroSPStart(final PlayerBattleObject player) {
        int encounterId = encounterId_800bb0f8.get();

        if (encounterId == 413 || encounterId == 415 || encounterId == 403) {
            player.stats.getStat(CoreMod.SP_STAT.get()).setCurrent(0);
        }
    }

    public void UltimateGuardBreak(final PlayerBattleObject player, final MonsterBattleObject monster, final AttackEvent attack) {
        int encounterId = encounterId_800bb0f8.get();

        if (encounterId == 415) {
            if (!attack.attackType.isPhysical()) {
                if (monster.spellId_4e == 117) {
                    player.guard_54 = 0;
                }
            }
        }
    }

    public void UltimateMPAttack(final PlayerBattleObject player, final MonsterBattleObject monster, final AttackEvent attack) {
        int encounterId = encounterId_800bb0f8.get();

        if (attack.damage > 0) {
            if (encounterId == 415) {
                if (attack.attackType.isPhysical()) {
                    if (monster.spellId_4e == 33) {
                        player.stats.getStat(CoreMod.MP_STAT.get()).setCurrent(Math.max(0, player.stats.getStat(CoreMod.MP_STAT.get()).getCurrent() - 10));
                    }
                }
            }
        }
    }

    @EventListener
    public void battleEnded(final BattleEndedEvent battleEnded) {
        final String difficulty = GameEngine.CONFIG.getConfig(DIFFICULTY.get());
        UpdateItemMagicDamage();

        if (faustBattle) {
            faustBattle = false;
            try {
                GameEngine.CONFIG.setConfig(FAUST_DEFEATED.get(), String.valueOf(Integer.parseInt(GameEngine.CONFIG.getConfig(FAUST_DEFEATED.get())) + 1));
            } catch (NumberFormatException ex) {
                GameEngine.CONFIG.setConfig(FAUST_DEFEATED.get(), String.valueOf(1));
            }
        }

        if (ultimateBattle) {
            ultimateBattle = false;

            int ultimateBossesDefeated = Integer.parseInt(GameEngine.CONFIG.getConfig(ULTIMATE_BOSS_DEFEATED.get()));
            int ultimateBossSelected = GameEngine.CONFIG.getConfig(ULTIMATE_BOSS.get()) - 1;
            int mapId = submapCut_80052c30.get();

            if (mapId >= 393 && mapId <= 394) {
                if (ultimateBossSelected > 2 && ultimateBossesDefeated > 2) {
                    ultimateBossSelected = 2;
                } else {
                    if (ultimateBossSelected > ultimateBossesDefeated) {
                        ultimateBossSelected = ultimateBossesDefeated;
                    }
                }
            } else if (mapId >= 395 && mapId <= 397) {
                if (ultimateBossSelected > 7 && ultimateBossesDefeated > 7) {
                    ultimateBossSelected = 7;
                } else {
                    if (ultimateBossSelected > ultimateBossesDefeated) {
                        ultimateBossSelected = ultimateBossesDefeated;
                    }
                }
            } else if (mapId >= 398 && mapId <= 400) {
                if (ultimateBossSelected > 21 && ultimateBossesDefeated > 21) {
                    ultimateBossSelected = 21;
                } else {
                    if (ultimateBossSelected > ultimateBossesDefeated) {
                        ultimateBossSelected = ultimateBossesDefeated;
                    }
                }
                System.out.println("HERE: " + ultimateBossesDefeated + "/" + ultimateBossSelected);
            }

            if (ultimateBossesDefeated == ultimateBossSelected) {
                GameEngine.CONFIG.setConfig(ULTIMATE_BOSS_DEFEATED.get(), String.valueOf(Integer.parseInt(GameEngine.CONFIG.getConfig(ULTIMATE_BOSS_DEFEATED.get())) + 1));
            }

            if (Integer.parseInt(GameEngine.CONFIG.getConfig(ULTIMATE_BOSS_DEFEATED.get())) == 3) {
                GameEngine.CONFIG.setConfig(CoreMod.INVENTORY_SIZE_CONFIG.get(), 36);
            } else if (Integer.parseInt(GameEngine.CONFIG.getConfig(ULTIMATE_BOSS_DEFEATED.get())) == 8) {
                GameEngine.CONFIG.setConfig(CoreMod.INVENTORY_SIZE_CONFIG.get(), 40);
            }
        }

        if (GameEngine.CONFIG.getConfig(DAMAGE_TRACKER.get()) == DamageTracker.ON && !damageTrackerPrinted) {
            try {
                double total = IntStream.of(damageTracker[0]).sum() + IntStream.of(damageTracker[1]).sum() + IntStream.of(damageTracker[2]).sum();
                PrintWriter pw = new PrintWriter("./mods/Damage Tracker/" + new SimpleDateFormat("yyyy-MMdd--hh-mm-ss").format(new Date()) + " - E" + encounterId_800bb0f8.get() + ".txt");
                pw.printf("======================================================================%n");
                pw.printf("=                           Damage Tracker                           =%n");
                pw.printf("======================================================================%n");
                pw.printf("| %-20s | %-20s | %-20s |%n", charNames[gameState_800babc8.charIds_88[0]], charNames[gameState_800babc8.charIds_88[1]], charNames[gameState_800babc8.charIds_88[2]]);
                pw.printf("----------------------------------------------------------------------%n");
                pw.printf("| %-10s %-9s | %-10s %-9s | %-10s %-9s |%n", "D.Physical", damageTracker[0][0], "D.Physical", damageTracker[1][0], "D.Physical", damageTracker[2][0]);
                pw.printf("| %-10s %-9s | %-10s %-9s | %-10s %-9s |%n", "D.Magical", damageTracker[0][1], "D.Magical",  damageTracker[1][1],"D.Magical",  damageTracker[2][1]);
                pw.printf("| %-10s %-9s | %-10s %-9s | %-10s %-9s |%n", "Physical", damageTracker[0][2], "Physical",  damageTracker[1][2],"Physical",  damageTracker[2][2]);
                pw.printf("| %-10s %-9s | %-10s %-9s | %-10s %-9s |%n", "Magical", damageTracker[0][3], "Magical",  damageTracker[1][3],"Magical",  damageTracker[2][3]);
                pw.printf("| %-10s %-9s | %-10s %-9s | %-10s %-9s |%n", "Total", IntStream.of(damageTracker[0]).sum(), "Total",  IntStream.of(damageTracker[1]).sum(), "Total",  IntStream.of(damageTracker[2]).sum());
                pw.printf("----------------------------------------------------------------------%n");
                pw.printf("%-13s %.2f%%%n", charNames[gameState_800babc8.charIds_88[0]], (IntStream.of(damageTracker[0]).sum() - damageTracker[0][4] * 2) / total * 100);
                pw.printf("%-13s %.2f%%%n", charNames[gameState_800babc8.charIds_88[1]], (IntStream.of(damageTracker[1]).sum() - damageTracker[1][4] * 2) / total * 100);
                pw.printf("%-13s %.2f%%%n", charNames[gameState_800babc8.charIds_88[2]], (IntStream.of(damageTracker[2]).sum() - damageTracker[2][4] * 2) / total * 100);
                pw.printf("Grand Total   " + total + "%n");
                pw.printf("Encounter     " + encounterId_800bb0f8.get() + "%n%n");
                pw.printf("===========================================================================================================%n");
                pw.printf("=                                                Equipment                                                =%n");
                pw.printf("===========================================================================================================%n");
                pw.printf("| Name     | Weapon           | Helmet           | Armor            | Shoes            | Accessory        |%n");
                pw.printf("-----------------------------------------------------------------------------------------------------------%n");
                for (int i = 0; i < damageTrackerEquips.length; i++) {
                    pw.printf("| %-8s | %-16s | %-16s | %-16s | %-16s | %-16s |%n", charNames[gameState_800babc8.charIds_88[i]], equipStats.get(damageTrackerEquips[i][0])[28], equipStats.get(damageTrackerEquips[i][1])[28], equipStats.get(damageTrackerEquips[i][2])[28], equipStats.get(damageTrackerEquips[i][3])[28], equipStats.get(damageTrackerEquips[i][4])[28]);
                }
                pw.printf("===========================================================================================================%n%n");
                for (String s : damageTrackerLog) {
                    pw.printf(s + "%n");
                }
                pw.flush();
                pw.close();
                damageTrackerPrinted = true;
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
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
                            fullScreenEffect_800bb140.type_00 = 0;
                            Thread.sleep(125);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }).start();
                break;
            case 4208: //Blossom Storm
            case 4234: //Rose Storm
                final String difficulty = GameEngine.CONFIG.getConfig(DIFFICULTY.get());
                if (difficulty.equals("Hell Mode") || difficulty.equals("Hard + Hell Bosses")) {
                    flowerStormOverride = true;
                }
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
                    equipStats.get(i)[29].replace('\u00a7', '\n'),
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
                    itemStats.get(i)[13].replace('\u00a7', '\n'),
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

        ConfigSwapped();

        System.out.println("[Dragoon Modifier] [Game Loaded] Done");
    }

    public void ConfigSwapped() {
        System.out.println("[Dragoon Modifier] [Config Swapped]");
        CoreMod.MAX_CHARACTER_LEVEL = maxCharacterLevel;
        CoreMod.MAX_DRAGOON_LEVEL = maxDragoonLevel;
        CoreMod.MAX_ADDITION_LEVEL = maxAdditionLevel;
        CoreMod.ADDITIONS_PER_LEVEL = additionsPerLevel;

        for (int i = 0; i < 9; i++) {
            CoreMod.CHARACTER_DATA[i].xpTable = new int[maxCharacterLevel + 1];
            CoreMod.CHARACTER_DATA[i].statsTable = new LevelStuff08[maxCharacterLevel + 1];
            CoreMod.CHARACTER_DATA[i].dxpTable = new int[CoreMod.MAX_DRAGOON_LEVEL + 1];
            CoreMod.CHARACTER_DATA[i].dragoonStatsTable = new MagicStuff08[CoreMod.MAX_DRAGOON_LEVEL + 1];
            CoreMod.CHARACTER_DATA[i].additions = new ArrayList<>();
            CoreMod.CHARACTER_DATA[i].additionsMultiplier = new ArrayList<>();
            CoreMod.CHARACTER_DATA[i].dragoonAddition = new ArrayList<>();
            CoreMod.CHARACTER_DATA[i].spBarColour = new int[2][CoreMod.MAX_DRAGOON_LEVEL + 2];
        }

        for (int i = 0; i < 9; i++) {
            for (int x = 0; x < maxCharacterLevel + 1; x++) {
                CoreMod.CHARACTER_DATA[i].xpTable[x] = Integer.parseInt(xpNextStats.get((maxCharacterLevel + 1) * i + x)[0]);
                CoreMod.CHARACTER_DATA[i].statsTable[x] = new LevelStuff08(Integer.parseInt(characterStatsTable.get((maxCharacterLevel + 1) * i + x)[5]), Integer.parseInt(characterStatsTable.get((maxCharacterLevel + 1) * i + x)[6]),
                        Integer.parseInt(characterStatsTable.get((maxCharacterLevel + 1) * i + x)[0]), Integer.parseInt(characterStatsTable.get((maxCharacterLevel + 1) * i + x)[1]),
                        Integer.parseInt(characterStatsTable.get((maxCharacterLevel + 1) * i + x)[2]), Integer.parseInt(characterStatsTable.get((maxCharacterLevel + 1) * i + x)[3]),
                        Integer.parseInt(characterStatsTable.get((maxCharacterLevel + 1) * i + x)[4]));
            }
        }

        for (int i = 0; i < 9; i++) {
            for (int x = 0; x < maxDragoonLevel + 1; x++) {
                CoreMod.CHARACTER_DATA[i].dxpTable[x] = Integer.parseInt(dxpNextStats.get(i)[x]);
            }
            for (int x = 0; x < maxDragoonLevel + 1; x++) {
                int spellIndex = Integer.parseInt(dragoonStatsTable.get((maxDragoonLevel + 1) * i + x)[1]);
                CoreMod.CHARACTER_DATA[i].dragoonStatsTable[x] = new MagicStuff08(Integer.parseInt(dragoonStatsTable.get((maxDragoonLevel + 1) * i + x)[0]), spellIndex == 255 ? (byte) -1 : (byte) spellIndex,
                        Integer.parseInt(dragoonStatsTable.get((maxDragoonLevel + 1) * i + x)[2]), Integer.parseInt(dragoonStatsTable.get((maxDragoonLevel + 1) * i + x)[3]),
                        Integer.parseInt(dragoonStatsTable.get((maxDragoonLevel + 1) * i + x)[4]), Integer.parseInt(dragoonStatsTable.get((maxDragoonLevel + 1) * i + x)[5]),
                        Integer.parseInt(dragoonStatsTable.get((maxDragoonLevel + 1) * i + x)[6]));
            }
        }

        for (int i = 0; i < 9; i++) {
            for (int x = 0; x < 2; x++) {
                for (int y = 0; y < maxDragoonLevel + 2; y++) {
                    CoreMod.CHARACTER_DATA[i].spBarColour[x][y] = Integer.decode(spBarColours.get(x)[y].replace("#", "0x"));
                }
            }
        }


        loadCharacterAdditions(0, 0, 7);
        loadCharacterAdditions(1, 8, 13);
        loadCharacterAdditions(3, 14, 18);
        loadCharacterAdditions(7, 19, 22);
        loadCharacterAdditions(6, 23, 28);
        loadCharacterAdditions(4, 29, 35);
        loadCharacterAdditions(5, 36, 41);
        loadDragoonAddition(0, 7, false);
        loadDragoonAddition(1, 13, false);
        loadDragoonAddition(3, 18, false);
        loadDragoonAddition(7, 22, false);
        loadDragoonAddition(6, 28, false);
        loadDragoonAddition(4, 35, false);
        loadDragoonAddition(5, 41, false);
        loadDragoonAddition(0, 42, true);
        loadAdditionMultiplier(0, 0, 7);
        loadAdditionMultiplier(1, 8, 13);
        loadAdditionMultiplier(3, 14, 18);
        loadAdditionMultiplier(7, 19, 22);
        loadAdditionMultiplier(6, 23, 28);
        loadAdditionMultiplier(4, 29, 35);
        loadAdditionMultiplier(5, 36, 41);
        loadShanaAdditions();
        loadAdditionMultiplier(2, 7, 8);
        loadAdditionMultiplier(8, 7, 8);

        System.out.println("TEST-TEST");
        System.out.println("DRAMOD: " + maxDragoonLevel);
        System.out.println("SC    : " + CoreMod.MAX_DRAGOON_LEVEL);
        System.out.println("SC1   : " + CoreMod.CHARACTER_DATA[0].dragoonStatsTable.length);
    }

    public void loadCharacterAdditions(int charIndex, int additionStart, int additionEnd) {
        int additionIndex = 0;
        for (int i = additionStart; i < additionEnd; i++) {
            CoreMod.CHARACTER_DATA[charIndex].additions.add(new CharacterData.SingleAddition());
            CharacterData.SingleAddition addition = CoreMod.CHARACTER_DATA[charIndex].additions.get(additionIndex);
            for (int x = 0; x < 8; x++) {
                addition.hits[x] = new BattlePreloadedEntities_18cb0.AdditionHitProperties20();
                addition.hits[x].set(0, Short.parseShort(additionStats.get(i * 8 + x)[0]));
                addition.hits[x].set(1, Short.parseShort(additionStats.get(i * 8 + x)[1]));
                addition.hits[x].set(2, Short.parseShort(additionStats.get(i * 8 + x)[2]));
                addition.hits[x].set(3, Short.parseShort(additionStats.get(i * 8 + x)[3]));
                addition.hits[x].set(4, Short.parseShort(additionStats.get(i * 8 + x)[4]));
                addition.hits[x].set(5, Short.parseShort(additionStats.get(i * 8 + x)[5]));
                addition.hits[x].set(6, Short.parseShort(additionStats.get(i * 8 + x)[6]));
                addition.hits[x].set(7, Short.parseShort(additionStats.get(i * 8 + x)[7]));
                addition.hits[x].set(8, Short.parseShort(additionStats.get(i * 8 + x)[8]));
                addition.hits[x].set(9, Short.parseShort(additionStats.get(i * 8 + x)[9]));
                addition.hits[x].set(10, Short.parseShort(additionStats.get(i * 8 + x)[10]));
                addition.hits[x].set(11, Short.parseShort(additionStats.get(i * 8 + x)[11]));
                addition.hits[x].set(12, Short.parseShort(additionStats.get(i * 8 + x)[12]));
                addition.hits[x].set(13, Short.parseShort(additionStats.get(i * 8 + x)[13]));
                addition.hits[x].set(14, Short.parseShort(additionStats.get(i * 8 + x)[14]));
                addition.hits[x].set(15, Short.parseShort(additionStats.get(i * 8 + x)[15]));
            }
            additionIndex++;
        }
    }

    public void loadDragoonAddition(int charIndex, int dragoonIndex, boolean divine) {
        CoreMod.CHARACTER_DATA[charIndex].dragoonAddition.add(new CharacterData.SingleAddition());
        CharacterData.SingleAddition addition = CoreMod.CHARACTER_DATA[charIndex].dragoonAddition.get(divine ? 1 : 0);
        for (int x = 0; x < 8; x++) {
            addition.hits[x] = new BattlePreloadedEntities_18cb0.AdditionHitProperties20();
            addition.hits[x].set(0, Short.parseShort(additionStats.get(dragoonIndex * 8 + x)[0]));
            addition.hits[x].set(1, Short.parseShort(additionStats.get(dragoonIndex * 8 + x)[1]));
            addition.hits[x].set(2, Short.parseShort(additionStats.get(dragoonIndex * 8 + x)[2]));
            addition.hits[x].set(3, Short.parseShort(additionStats.get(dragoonIndex * 8 + x)[3]));
            addition.hits[x].set(4, Short.parseShort(additionStats.get(dragoonIndex * 8 + x)[4]));
            addition.hits[x].set(5, Short.parseShort(additionStats.get(dragoonIndex * 8 + x)[5]));
            addition.hits[x].set(6, Short.parseShort(additionStats.get(dragoonIndex * 8 + x)[6]));
            addition.hits[x].set(7, Short.parseShort(additionStats.get(dragoonIndex * 8 + x)[7]));
            addition.hits[x].set(8, Short.parseShort(additionStats.get(dragoonIndex * 8 + x)[8]));
            addition.hits[x].set(9, Short.parseShort(additionStats.get(dragoonIndex * 8 + x)[9]));
            addition.hits[x].set(10, Short.parseShort(additionStats.get(dragoonIndex * 8 + x)[10]));
            addition.hits[x].set(11, Short.parseShort(additionStats.get(dragoonIndex * 8 + x)[11]));
            addition.hits[x].set(12, Short.parseShort(additionStats.get(dragoonIndex * 8 + x)[12]));
            addition.hits[x].set(13, Short.parseShort(additionStats.get(dragoonIndex * 8 + x)[13]));
            addition.hits[x].set(14, Short.parseShort(additionStats.get(dragoonIndex * 8 + x)[14]));
            addition.hits[x].set(15, Short.parseShort(additionStats.get(dragoonIndex * 8 + x)[15]));
        }
    }

    public void loadAdditionMultiplier(int charIndex, int additionStart, int additionEnd) {
        int additionIndex = 0;
        for (int i = additionStart; i < additionEnd; i++) {
            CoreMod.CHARACTER_DATA[charIndex].additionsMultiplier.add(new CharacterData.AdditionMultiplier());
            for (int x = 0; x < maxAdditionLevel + 1; x++) {
                CoreMod.CHARACTER_DATA[charIndex].additionsMultiplier.get(additionIndex).levelMultiplier.add(new CharacterData.AdditionMultiplierLevel());
                CharacterData.AdditionMultiplierLevel levelData = CoreMod.CHARACTER_DATA[charIndex].additionsMultiplier.get(additionIndex).levelMultiplier.get(x);
                levelData._00 = Integer.parseInt(additionMultiStats.get(i)[x * 4]);
              levelData._01 = Integer.parseInt(additionMultiStats.get(i)[x * 4 + 1]);
              levelData.spMultiplier_02 = Integer.parseInt(additionMultiStats.get(i)[x * 4 + 2]);
                levelData.dmgMultiplier_03 = Integer.parseInt(additionMultiStats.get(i)[x * 4 + 3]);
            }
            additionIndex++;
        }
    }

    public void loadShanaAdditions() {
        CoreMod.CHARACTER_DATA[2].additions.add(new CharacterData.SingleAddition());
        CoreMod.CHARACTER_DATA[8].additions.add(new CharacterData.SingleAddition());
        CoreMod.CHARACTER_DATA[2].dragoonAddition.add(new CharacterData.SingleAddition());
        CoreMod.CHARACTER_DATA[8].dragoonAddition.add(new CharacterData.SingleAddition());
        CharacterData.SingleAddition addition = CoreMod.CHARACTER_DATA[2].additions.get(0);
        for (int x = 0; x < 8; x++) {
            addition.hits[x] = new BattlePreloadedEntities_18cb0.AdditionHitProperties20();
            addition.hits[x].set(0, (short) 0);
            addition.hits[x].set(1, (short) 0);
            addition.hits[x].set(2, (short) 0);
            addition.hits[x].set(3, (short) 0);
            addition.hits[x].set(4, (short) 0);
            addition.hits[x].set(5, (short) 0);
            addition.hits[x].set(6, (short) 0);
            addition.hits[x].set(7, (short) 0);
            addition.hits[x].set(8, (short) 0);
            addition.hits[x].set(9, (short) 0);
            addition.hits[x].set(10, (short) 0);
            addition.hits[x].set(11, (short) 0);
            addition.hits[x].set(12, (short) 0);
            addition.hits[x].set(13, (short) 0);
            addition.hits[x].set(14, (short) 0);
            addition.hits[x].set(15, (short) 0);
        }
        CoreMod.CHARACTER_DATA[2].dragoonAddition.get(0).hits = addition.hits;
        CoreMod.CHARACTER_DATA[8].additions.get(0).hits = addition.hits;
        CoreMod.CHARACTER_DATA[8].dragoonAddition.get(0).hits = addition.hits;
    }

    @EventListener
    public void bobjTurn(final BattleObjectTurnEvent<?> turn) {
        final String difficulty = GameEngine.CONFIG.getConfig(DIFFICULTY.get());
        UpdateMonsterHPNames();
        UpdateItemMagicDamage();

        if (turn.bobj instanceof PlayerBattleObject player) {
            damageTrackerLog.add(charNames[player.charId_272] + " Turn Started");

            if (difficulty.equals("Hard Mode") || difficulty.equals("US + Hard Bosses") || difficulty.equals("Hell Mode") || difficulty.equals("Hard + Hell Bosses")) {
                if (bonusItemSP[player.charSlot_276]) {
                    bonusItemSP[player.charSlot_276] = false;
                    if (player.isDragoon()) {
                        player.stats.getStat(CoreMod.SP_STAT.get()).setCurrent(player.stats.getStat(CoreMod.SP_STAT.get()).getCurrent() + 50);
                        int newSP = player.stats.getStat(CoreMod.SP_STAT.get()).getCurrent();
                        if (player.charSlot_276 == 0) {
                            battleState_8006e398.dragoonTurnsSlot1_294 = newSP / 100;
                        } else if (player.charSlot_276 == 1) {
                            battleState_8006e398.dragoonTurnsSlot2_298 = newSP / 100;
                        } else if (player.charSlot_276 == 2) {
                            battleState_8006e398.dragoonTurnsSlot3_29c = newSP / 100;
                        }
                    }
                }

                if (player.equipment0_11e == 166) { //Spirit Eater
                    int sp = player.stats.getStat(CoreMod.SP_STAT.get()).getCurrent();
                    if (!player.isDragoon() && sp != player.stats.getStat(CoreMod.SP_STAT.get()).getMax()) {
                        player.stats.getStat(CoreMod.SP_STAT.get()).setCurrent(sp - 20);
                    }
                    spGained_800bc950.get(player.charSlot_276).add(40);
                }

                if (ouroboros[player.charSlot_276] && !player.isDragoon()) { //Ouroboros
                    player.stats.getStat(CoreMod.SPEED_STAT.get()).addMod(new TurnBasedPercentileBuff(-50, 3));
                    ouroboros[player.charSlot_276] = false;
                }

                if (player.equipment4_126 == 184) { //Ring of Elements
                    if (dragoonSpaceElement_800c6b64 == player.element) {
                        ringOfElements[player.charSlot_276]++;
                        ringOfElementsElement[player.charSlot_276] = dragoonSpaceElement_800c6b64;
                    } else {
                        if (player.element == CoreMod.FIRE_ELEMENT.get() && dragoonSpaceElement_800c6b64 == CoreMod.DIVINE_ELEMENT.get()) {
                            ringOfElements[player.charSlot_276]++;
                            ringOfElementsElement[player.charSlot_276] = dragoonSpaceElement_800c6b64;
                        }

                        if (dragoonSpaceElement_800c6b64 == null) {
                            ringOfElements[player.charSlot_276]--;
                        }
                    }
                }
            }
        }

        if (difficulty.equals("Hard Mode") || difficulty.equals("US + Hard Bosses") || difficulty.equals("Hell Mode") || difficulty.equals("Hard + Hell Bosses")) {
            if ((difficulty.equals("Hell Mode") || difficulty.equals("Hard + Hell Bosses")) && flowerStormOverride) {
                flowerStormOverride = false;
                for(int i = 0; i < allBobjCount_800c66d0.get(); i++) {
                    final ScriptState<? extends BattleObject27c> state = battleState_8006e398.allBobjs_e0c[i];
                    final BattleObject27c bobj = state.innerStruct_00;
                    if (bobj instanceof PlayerBattleObject player) {
                        player.powerDefenceTurns_b9 = GameEngine.CONFIG.getConfig(FLOWER_STORM.get());
                        player.powerMagicDefenceTurns_bb = GameEngine.CONFIG.getConfig(FLOWER_STORM.get());
                    }
                }
            }

            if (turn.bobj instanceof PlayerBattleObject player) {
                currentPlayerSlot = player.charSlot_276;
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
                        previousBurnStacks = 0;
                        burnStackMode = false;
                    }
                }

                if (player.charId_272 == 2 || player.charId_272 == 8) {
                    if (shanaStarChildrenHeal[player.charSlot_276] && !player.isDragoon()) {
                        shanaStarChildrenHeal[player.charSlot_276] = false;
                        player.stats.getStat(CoreMod.HP_STAT.get()).setCurrent(player.stats.getStat(CoreMod.HP_STAT.get()).getMax());
                    }

                    if (shanaRapidFire[player.charSlot_276]) {
                        shanaRapidFire[player.charSlot_276] = false;
                        player.dragoonAttack_ac = dragonBlockStaff ? 365 * 8 : 365;
                    }
                }

                if (elementalAttack[player.charSlot_276]) {
                    player.element = previousElement[player.charSlot_276];
                    elementalAttack[player.charSlot_276] = false;
                    if (player.charId_272 == 2 || player.charId_272 == 8) {
                        player.dragoonAttack_ac = dragonBlockStaff ? 365 * 8 : 365;
                    }
                }

                if (player.charId_272 == 6 && meruBoost[player.charSlot_276]) {
                    meruBoostTurns[player.charSlot_276] -= 1;
                    if (meruBoostTurns[player.charSlot_276] == 0) {
                        meruBoost[player.charSlot_276] = false;
                        player.stats.getStat(CoreMod.HP_STAT.get()).setMaxRaw(meruMaxHpSave[player.charSlot_276]);
                        player.magicDefence_3a = meruMDFSave[player.charSlot_276];
                    }
                }

                if (difficulty.equals("Hell Mode") || difficulty.equals("Hard + Hell Bosses")) {
                    if (player.isDragoon()) {
                        spGained_800bc950.get(player.charSlot_276).add(50);
                    }
                }
            }
        }


        if (turn.bobj instanceof MonsterBattleObject monster) {
            if (elementalBombTurns[monster.charSlot_276] > 0) {
                elementalBombTurns[monster.charSlot_276] -= 1;

                if (elementalBombTurns[monster.charSlot_276] == 0) {
                    monster.displayElement_1c = elementalBombPreviousElement[monster.charSlot_276];
                    monster.monsterElement_72 = elementalBombPreviousElement[monster.charSlot_276];
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
                    burnStacksGfx[0].render(event.charSlot * 94 - 143, 64, z);
                } else if (currentBurnState <= 0.50) {
                    burnStacksGfx[1].render(event.charSlot * 94 - 143, 64, z);
                } else if (currentBurnState <= 0.99) {
                    burnStacksGfx[2].render(event.charSlot * 94 - 143, 64, z);
                } else {
                    burnStacksGfx[3].render(event.charSlot * 94 - 143, 64, z);
                }
            }
        }
    }

    @EventListener
    public void dragonBlockStaffOn(final DragonBlockStaffOnEvent event) {
        final String difficulty = GameEngine.CONFIG.getConfig(DIFFICULTY.get());
        if (difficulty.equals("Hard Mode") || difficulty.equals("US + Hard Bosses") || difficulty.equals("Hell Mode") || difficulty.equals("Hard + Hell Bosses")) {
            dragonBlockStaff = true;
            for(int i = 0; i < allBobjCount_800c66d0.get(); i++) {
                final ScriptState<? extends BattleObject27c> state = battleState_8006e398.allBobjs_e0c[i];
                final BattleObject27c bobj = state.innerStruct_00;
                if (bobj instanceof PlayerBattleObject player) {
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
        if (difficulty.equals("Hard Mode") || difficulty.equals("US + Hard Bosses") || difficulty.equals("Hell Mode") || difficulty.equals("Hard + Hell Bosses")) {
            dragonBlockStaff = false;
            for(int i = 0; i < allBobjCount_800c66d0.get(); i++) {
                final ScriptState<? extends BattleObject27c> state = battleState_8006e398.allBobjs_e0c[i];
                final BattleObject27c bobj = state.innerStruct_00;
                if (bobj instanceof PlayerBattleObject player) {
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
                        damage *= (1 + (burnStacks * dmgPerBurn)) * (Integer.parseInt(spellStats.get(2)[3]) / Integer.parseInt(spellStats.get(0)[3])) * 1.5;
                        newDescription = spellStats.get(spellId)[13].replace("1.00", String.format("%.2f", ((1 + (burnStacks * dmgPerBurn)) * (Integer.parseInt(spellStats.get(2)[3]) / Integer.parseInt(spellStats.get(0)[3])) * 1.5)));
                    } else if (spellId == 1)  {
                        damage *= (1 + (burnStacks * dmgPerBurn)) * Integer.parseInt(spellStats.get(3)[3]) / Integer.parseInt(spellStats.get(1)[3]);
                        newDescription = spellStats.get(spellId)[13].replace("1.00", String.format("%.2f", (1 + (burnStacks * dmgPerBurn)) * Integer.parseInt(spellStats.get(3)[3]) / Integer.parseInt(spellStats.get(1)[3])));
                    } else if (spellId == 2)  {
                        damage *= (1 + (burnStacks * dmgPerBurn)) * 1.5;
                        newDescription = spellStats.get(spellId)[13].replace("1.00", String.format("%.2f", (1 + (burnStacks * dmgPerBurn)) * 1.5));
                    } else {
                        damage *= 1 + (burnStacks * dmgPerBurn);
                    }
                } else {
                    damage *= 1 + (burnStacks * dmgPerBurn) * 1.5;
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
        if (!burnStackMode) {
            previousBurnStacks = burnStacks;
            int dlv = dart.dlevel_06;
            burnStacksMax = dlv == 1 ? 3 : dlv == 2 ? 6 : dlv == 3 ? 9 : dlv == 7 ? 15 : 12;
            burnStacks = Math.min(burnStacksMax, burnStacks + stacks);

            if (burnStacks >= 4 && previousBurnStacks < 4) {
                dart.stats.getStat(CoreMod.MP_STAT.get()).setCurrent(dart.stats.getStat(CoreMod.MP_STAT.get()).getCurrent() + 10);
            } else if (burnStacks >= 8 && previousBurnStacks < 8) {
                dart.stats.getStat(CoreMod.MP_STAT.get()).setCurrent(dart.stats.getStat(CoreMod.MP_STAT.get()).getCurrent() + 20);
            } else if (burnStacks >= 12 && previousBurnStacks < 12) {
                dart.stats.getStat(CoreMod.MP_STAT.get()).setCurrent(dart.stats.getStat(CoreMod.MP_STAT.get()).getCurrent() + 30);
            }
        }
    }

    public void dramodHotkeys() {
        final String difficulty = GameEngine.CONFIG.getConfig(DIFFICULTY.get());

        if (engineState_8004dd20 == EngineState.COMBAT_06) { // Combat
            if (hotkey.contains(InputAction.BUTTON_SHOULDER_LEFT_1) && hotkey.contains(InputAction.DPAD_UP)) { //Exit Dragoon Slot 1
                if (Scus94491BpeSegment_8006.battleState_8006e398.dragoonTurnsSlot1_294 > 0) {
                    Scus94491BpeSegment_8006.battleState_8006e398.dragoonTurnsSlot1_294 = 1;
                }
            } else if (hotkey.contains(InputAction.BUTTON_SHOULDER_LEFT_1) && hotkey.contains(InputAction.DPAD_RIGHT)) { //Exit Dragoon Slot 2
                if (Scus94491BpeSegment_8006.battleState_8006e398.dragoonTurnsSlot2_298 > 0) {
                    Scus94491BpeSegment_8006.battleState_8006e398.dragoonTurnsSlot2_298 = 1;
                }
            } else if (hotkey.contains(InputAction.BUTTON_SHOULDER_LEFT_1) && hotkey.contains(InputAction.DPAD_LEFT)) { //Exit Dragoon Slot 3
                if (Scus94491BpeSegment_8006.battleState_8006e398.dragoonTurnsSlot3_29c > 0) {
                    Scus94491BpeSegment_8006.battleState_8006e398.dragoonTurnsSlot3_29c = 1;
                }
            }

            if (difficulty.equals("Hard Mode") || difficulty.equals("US + Hard Bosses") || difficulty.equals("Hell Mode") || difficulty.equals("Hard + Hell Bosses")) {
                if (hotkey.contains(InputAction.BUTTON_NORTH) && hotkey.contains(InputAction.BUTTON_WEST)) { //Burn Stacks Mode
                    if (burnStacks > 0) {
                        burnStackMode = !burnStackMode;
                    }
                } else if (hotkey.contains(InputAction.BUTTON_SHOULDER_LEFT_2) && hotkey.contains(InputAction.DPAD_UP)) { //Dragoon Guard Slot 1
                    PlayerBattleObject player = battleState_8006e398.charBobjs_e40[0].innerStruct_00;
                    int dragoonTurns = Scus94491BpeSegment_8006.battleState_8006e398.dragoonTurnsSlot1_294;
                    int sp = player.stats.getStat(CoreMod.SP_STAT.get()).getCurrent();
                    if (player.isDragoon() && player.dlevel_06 >= 6 && dragoonTurns > 1 && sp >= 100) {
                        Scus94491BpeSegment_8006.battleState_8006e398.dragoonTurnsSlot1_294 -= 1;
                        player.stats.getStat(CoreMod.SP_STAT.get()).setCurrent(sp - 100);
                        player.guard_54 = 1;
                    }
                } else if (hotkey.contains(InputAction.BUTTON_SHOULDER_LEFT_2) && hotkey.contains(InputAction.DPAD_RIGHT)) { //Dragoon Guard Slot 2
                    PlayerBattleObject player = battleState_8006e398.charBobjs_e40[1].innerStruct_00;
                    int dragoonTurns = Scus94491BpeSegment_8006.battleState_8006e398.dragoonTurnsSlot2_298;
                    int sp = player.stats.getStat(CoreMod.SP_STAT.get()).getCurrent();
                    if (player.isDragoon() && player.dlevel_06 >= 6 && dragoonTurns > 1 && sp >= 100) {
                        Scus94491BpeSegment_8006.battleState_8006e398.dragoonTurnsSlot2_298 -= 1;
                        player.stats.getStat(CoreMod.SP_STAT.get()).setCurrent(sp - 100);
                        player.guard_54 = 1;
                    }
                } else if (hotkey.contains(InputAction.BUTTON_SHOULDER_LEFT_2) && hotkey.contains(InputAction.DPAD_LEFT)) { //Dragoon Guard Slot 3
                    PlayerBattleObject player = battleState_8006e398.charBobjs_e40[2].innerStruct_00;
                    int dragoonTurns = Scus94491BpeSegment_8006.battleState_8006e398.dragoonTurnsSlot3_29c;
                    int sp = player.stats.getStat(CoreMod.SP_STAT.get()).getCurrent();
                    if (player.isDragoon() && player.dlevel_06 >= 6 && dragoonTurns > 1 && sp >= 100) {
                        Scus94491BpeSegment_8006.battleState_8006e398.dragoonTurnsSlot3_29c -= 1;
                        player.stats.getStat(CoreMod.SP_STAT.get()).setCurrent(sp - 100);
                        player.guard_54 = 1;
                    }
                } else if (hotkey.contains(InputAction.BUTTON_SOUTH) && hotkey.contains(InputAction.BUTTON_SHOULDER_RIGHT_1)) { // Shana Rapid fire
                    for(int i = 0; i < 0x48; i++) {
                        try {
                            final ScriptState<?> state = scriptStatePtrArr_800bc1c0[i];
                            if ((state.name.contains("Char ID 2") || state.name.contains("Char Id 8"))) {
                                for(int x = 0; x < allBobjCount_800c66d0.get(); x++) {
                                    final ScriptState<? extends BattleObject27c> playerstate = battleState_8006e398.allBobjs_e0c[x];
                                    final BattleObject27c bobj = playerstate.innerStruct_00;
                                    if (bobj instanceof PlayerBattleObject player) {
                                      if (player.isDragoon() && shanaRapidFireContinue[player.charSlot_276]) {
                                            if (scriptStatePtrArr_800bc1c0[i].offset_18 == 0x1d2) {
                                                scriptStatePtrArr_800bc1c0[i].offset_18 = 0x2050;
                                                shanaRapidFireCount[player.charSlot_276]++;
                                                if (shanaRapidFireCount[player.charSlot_276] == 2) {
                                                    shanaRapidFireContinue[player.charSlot_276] = false;
                                                }
                                            }
                                            break;
                                        }
                                    }
                                }
                            }
                        } catch (Exception ignored) {}
                    }
                } else if (hotkey.contains(InputAction.BUTTON_SOUTH) && hotkey.contains(InputAction.BUTTON_SHOULDER_LEFT_1)) { //Shana Rapid Fire Activator
                    for(int i = 0; i < allBobjCount_800c66d0.get(); i++) {
                        final ScriptState<? extends BattleObject27c> state = battleState_8006e398.allBobjs_e0c[i];
                        final BattleObject27c bobj = state.innerStruct_00;
                        if (bobj instanceof PlayerBattleObject player) {
                            if ((player.charId_272 == 2 || player.charId_272 == 8) && player.charSlot_276 == currentPlayerSlot && !shanaRapidFire[player.charSlot_276] && player.isDragoon() && player.dlevel_06 >= 6) {
                                int mp = player.stats.getStat(CoreMod.MP_STAT.get()).getCurrent();
                                if (mp >= 20) {
                                    player.stats.getStat(CoreMod.MP_STAT.get()).setCurrent(mp - 20);
                                    shanaRapidFire[player.charSlot_276] = true;
                                    shanaRapidFireContinue[player.charSlot_276] = true;
                                    shanaRapidFireCount[player.charSlot_276] = 0;
                                    player.dragoonAttack_ac = dragonBlockStaff ? 165 * 8 : 165;
                                }
                            }
                        }
                    }
                /*} else if (hotkey.contains(InputAction.BUTTON_SOUTH) && hotkey.contains(InputAction.BUTTON_SHOULDER_RIGHT_2)) { //Shana Light Element Arrow
                    for(int i = 0; i < allBobjCount_800c66d0.get(); i++) {
                        final ScriptState<? extends BattleObject27c> state = battleState_8006e398.allBobjs_e0c[i];
                        final BattleObject27c bobj = state.innerStruct_00;
                        if (bobj instanceof PlayerBattleObject) {
                            PlayerBattleObject player = (PlayerBattleObject) bobj;
                            if ((player.charId_272 == 2 || player.charId_272 == 8) && player.charSlot_276 == currentPlayerSlot && player.isDragoon() && player.dlevel_06 >= 7) {
                                int mp = player.stats.getStat(CoreMod.MP_STAT.get()).getCurrent();
                                if (mp >= 100) {
                                    player.stats.getStat(CoreMod.MP_STAT.get()).setCurrent(mp - 100);
                                    previousElement[player.charSlot_276] = player.element;
                                    elementalAttack[player.charSlot_276] = true;
                                    player.element = Element.fromFlag(32);
                                    player.dragoonAttack_ac = dragonBlockStaff ? 550 * 8 : 550;
                                }
                            }
                        }
                    }*/
                } else if (hotkey.contains(InputAction.BUTTON_SHOULDER_LEFT_2) && hotkey.contains(InputAction.BUTTON_SHOULDER_RIGHT_2)) { //Meru Boost
                    for(int i = 0; i < allBobjCount_800c66d0.get(); i++) {
                        final ScriptState<? extends BattleObject27c> state = battleState_8006e398.allBobjs_e0c[i];
                        final BattleObject27c bobj = state.innerStruct_00;
                        if (bobj instanceof PlayerBattleObject) {
                            PlayerBattleObject player = (PlayerBattleObject) bobj;
                            if (player.charId_272 == 6 && player.charSlot_276 == currentPlayerSlot && player.isDragoon() && player.dlevel_06 >= 7) {
                                int mp = player.stats.getStat(CoreMod.MP_STAT.get()).getCurrent();
                                if (mp >= 100) {
                                    int maxHP = player.stats.getStat(CoreMod.HP_STAT.get()).getMax();
                                    player.stats.getStat(CoreMod.MP_STAT.get()).setCurrent(mp - 100);
                                    meruBoost[player.charSlot_276] = true;
                                    meruBoostTurns[player.charSlot_276] = 5;
                                    meruMDFSave[player.charSlot_276] = player.magicDefence_3a;
                                    meruMaxHpSave[player.charSlot_276] = maxHP;
                                    player.stats.getStat(CoreMod.HP_STAT.get()).setMaxRaw(maxHP * 3);
                                    player.stats.getStat(CoreMod.HP_STAT.get()).setCurrent(maxHP * 3);
                                }
                            }
                        }
                    }
                }
            }
        } else {
            if (hotkey.contains(InputAction.BUTTON_CENTER_1) && hotkey.contains(InputAction.BUTTON_THUMB_1)) { //Add Shana
                gameState_800babc8.charData_32c[2].partyFlags_04 = gameState_800babc8.charData_32c[2].partyFlags_04 == 0 ? 3 : 0;
            } else if (hotkey.contains(InputAction.BUTTON_CENTER_1) && hotkey.contains(InputAction.BUTTON_THUMB_2)) { //Add Lavitz
                gameState_800babc8.charData_32c[1].partyFlags_04 = gameState_800babc8.charData_32c[1].partyFlags_04 == 0 ? 3 : 0;
            } else if (hotkey.contains(InputAction.BUTTON_SOUTH) && hotkey.contains(InputAction.BUTTON_SHOULDER_RIGHT_1)) { //Add Dragoons Start
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
            } else if (hotkey.contains(InputAction.BUTTON_SOUTH) && hotkey.contains(InputAction.BUTTON_SHOULDER_LEFT_1)) { //Solo/All Character Start
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
                } else if (mapId == 232) { //Add Dart Dragoon Back
                    gameState_800babc8.goods_19c[0] ^= 1 << 0;
                } else if (mapId == 424 || mapId == 736) { //Divine Dragoon Swap
                    if ((difficulty.equals("Hell Mode") || difficulty.equals("Hard + Hell Bosses")) && Integer.parseInt(GameEngine.CONFIG.getConfig(ULTIMATE_BOSS_DEFEATED.get())) >= 34) {
                        gameState_800babc8.goods_19c[0] ^= 1 << 7;
                        if (mapId == 736) {
                            gameState_800babc8.goods_19c[0] |= 1 << 0;
                        }
                    } else {
                        gameState_800babc8.goods_19c[0] ^= 1 << 7;
                        if (mapId == 736) {
                            gameState_800babc8.goods_19c[0] |= 1 << 0;
                        }
                    }
                } else if (mapId == 729) { //Warp out of Moon
                    submapCut_80052c30.set(527);
                    smapLoadingStage_800cb430.set(0x4);
                } else if (mapId == 526 || mapId == 527) { // TODO: Story flag check here // Warp to Moon
                    submapCut_80052c30.set(730);
                    smapLoadingStage_800cb430.set(0x4);
                } else if (mapId == 732) { //Faust Battle
                    encounterId_800bb0f8.set(420);

                    if (engineState_8004dd20 == EngineState.SUBMAP_05) {
                        combatStage_800bb0f4.set(78);
                        FUN_800e5534(-1, 0);
                    } else if(engineState_8004dd20 == EngineState.WORLD_MAP_08) {
                        combatStage_800bb0f4.set(78);
                        gameState_800babc8.areaIndex_4de = mapState_800c6798.areaIndex_12;
                        gameState_800babc8.pathIndex_4d8 = mapState_800c6798.pathIndex_14;
                        gameState_800babc8.dotIndex_4da = mapState_800c6798.dotIndex_16;
                        gameState_800babc8.dotOffset_4dc = mapState_800c6798.dotOffset_18;
                        gameState_800babc8.facing_4dd = mapState_800c6798.facing_1c;
                        pregameLoadingStage_800bb10c.set(8);
                    }

                    faustBattle = true;
                }
            } else if (hotkey.contains(InputAction.BUTTON_SOUTH) && hotkey.contains(InputAction.BUTTON_THUMB_2)) { //Add all party members back
                for (int i = 0; i < 9; i++) {
                    gameState_800babc8.charData_32c[i].partyFlags_04 = 3;
                }
            } else if (hotkey.contains(InputAction.BUTTON_SOUTH) && hotkey.contains(InputAction.BUTTON_CENTER_2)) { //???
                gameState_800babc8.charData_32c[8].partyFlags_04 = 0;
            } else if (hotkey.contains(InputAction.BUTTON_NORTH) && hotkey.contains(InputAction.BUTTON_EAST)) { //Level Up Party
                int highestInPartyEXP = 0;
                boolean maxedSwapEXP = false;
                for (int i = 0; i < 9; i++) {
                    if (gameState_800babc8.charData_32c[i].partyFlags_04 > 0 && gameState_800babc8.charData_32c[i].xp_00 > highestInPartyEXP) {
                        highestInPartyEXP = gameState_800babc8.charData_32c[i].xp_00;
                    }
                }

                if (difficulty.equals("Hard Mode") || difficulty.equals("Us + Hard Bosses")) {
                    if (highestInPartyEXP > 80000) {
                        maxedSwapEXP = true;
                    }
                }

                if (difficulty.equals("Hell Mode") || difficulty.equals("Hard + Hell Bosses")) {
                    if (highestInPartyEXP > 160000) {
                        maxedSwapEXP = true;
                    }
                }

                if (!maxedSwapEXP) {
                    for (int i = 0; i < 9; i++) {
                        if (gameState_800babc8.charData_32c[i].partyFlags_04 > 0) {
                            while (highestInPartyEXP > getXpToNextLevel(i)) {
                                gameState_800babc8.charData_32c[i].level_12++;
                            }
                        }
                    }
                }
            } else if (hotkey.contains(InputAction.BUTTON_NORTH) && hotkey.contains(InputAction.BUTTON_WEST)) {
                if (!swappedEXP) {
                    swappedEXP = true;
                    System.out.println("[Dragoon Modifier] Preparing Switch EXP...");
                    System.arraycopy(gameState_800babc8.charIds_88, 0, swapEXPParty, 0, 3);
                } else {
                    swappedEXP = false;
                    int slot1 = -1;
                    int slot2 = -1;
                    for (int i = 0; i < 3; i++) {
                        if (swapEXPParty[i] != gameState_800babc8.charIds_88[i]) {
                            slot1 = i;
                        }
                    }

                    for (int i = 0; i < 3; i++) {
                        if (swapEXPParty[slot1] == gameState_800babc8.charIds_88[i]) {
                            slot2 = i;
                            int char1 = gameState_800babc8.charIds_88[slot1];
                            int char2 = gameState_800babc8.charIds_88[slot2];
                            int slot1EXP = gameState.charData_32c[char1].xp_00;
                            int slot2EXP = gameState.charData_32c[char2].xp_00;
                            boolean disableSwap = false;

                            if (difficulty.equals("Hard Mode") || difficulty.equals("Us + Hard Bosses")) {
                                if (slot1EXP > 80000 || slot2EXP > 80000) {
                                    disableSwap = true;
                                }
                            }

                            if (difficulty.equals("Hell Mode") || difficulty.equals("Hard + Hell Bosses")) {
                                if (slot1EXP > 160000 || slot2EXP > 160000) {
                                    disableSwap = true;
                                }
                            }

                            if (!disableSwap) {
                                gameState.charData_32c[char1].xp_00 = slot2EXP;
                                gameState.charData_32c[char2].xp_00 = slot1EXP;
                            }
                        }
                    }

                    if (slot1 >= 0 && slot2 >= 0) {
                        System.out.println("[Dragoon Modifier] EXP Switched.");
                    } else {
                        System.out.println("[Dragoon Modifier] Switch EXP character removed from party.");
                    }
                }
            } else if (hotkey.contains(InputAction.BUTTON_EAST) && hotkey.contains(InputAction.BUTTON_CENTER_2)) {
                if (difficulty.equals("Hard Mode") || difficulty.equals("US + Hard Bosses") || difficulty.equals("Hell Mode") || difficulty.equals("Hard + Hell Bosses")) {
                    int mapId = submapCut_80052c30.get();
                    if (mapId >= 393 && mapId <= 405) {
                        if (gameState_800babc8.chapterIndex_98 == 3) {
                            int ultimateBossesDefeated = Integer.parseInt(GameEngine.CONFIG.getConfig(ULTIMATE_BOSS_DEFEATED.get()));
                            int ultimateBossSelected = GameEngine.CONFIG.getConfig(ULTIMATE_BOSS.get()) - 1;

                            if (mapId >= 393 && mapId <= 394) {
                                if (ultimateBossSelected > 2 && ultimateBossesDefeated > 2) {
                                    ultimateBossSelected = 2;
                                } else {
                                    if (ultimateBossSelected > ultimateBossesDefeated) {
                                        ultimateBossSelected = ultimateBossesDefeated;
                                    }
                                }
                                ultimateLevelCap = 30;
                            } else if (mapId >= 395 && mapId <= 397) {
                                if (ultimateBossSelected > 7 && ultimateBossesDefeated > 7) {
                                    ultimateBossSelected = 7;
                                } else {
                                    if (ultimateBossSelected > ultimateBossesDefeated) {
                                        ultimateBossSelected = ultimateBossesDefeated;
                                    }
                                }
                                ultimateLevelCap = 40;
                            } else if (mapId >= 398 && mapId <= 400) {
                                if (ultimateBossSelected > 21 && ultimateBossesDefeated > 21) {
                                    ultimateBossSelected = 21;
                                } else {
                                    if (ultimateBossSelected > ultimateBossesDefeated) {
                                        ultimateBossSelected = ultimateBossesDefeated;
                                    }
                                }
                                ultimateLevelCap = 50;
                            }


                            if (ultimateBossSelected >= 0) {
                                ultimateBattle = true;

                                encounterId_800bb0f8.set(ultimateEncounter[ultimateBossSelected][0]);

                                if (engineState_8004dd20 == EngineState.SUBMAP_05) {
                                    combatStage_800bb0f4.set(ultimateEncounter[ultimateBossSelected][1]);
                                    FUN_800e5534(-1, 0);
                                } else if (engineState_8004dd20 == EngineState.WORLD_MAP_08) {
                                    combatStage_800bb0f4.set(ultimateEncounter[ultimateBossSelected][1]);
                                    gameState_800babc8.areaIndex_4de = mapState_800c6798.areaIndex_12;
                                    gameState_800babc8.pathIndex_4d8 = mapState_800c6798.pathIndex_14;
                                    gameState_800babc8.dotIndex_4da = mapState_800c6798.dotIndex_16;
                                    gameState_800babc8.dotOffset_4dc = mapState_800c6798.dotOffset_18;
                                    gameState_800babc8.facing_4dd = mapState_800c6798.facing_1c;
                                    pregameLoadingStage_800bb10c.set(8);
                                }
                            }
                        }
                    }
                }
            } else if (hotkey.contains(InputAction.BUTTON_NORTH) && hotkey.contains(InputAction.BUTTON_SHOULDER_RIGHT_1)) {
                if (difficulty.equals("Hard Mode") || difficulty.equals("US + Hard Bosses") || difficulty.equals("Hell Mode") || difficulty.equals("Hard + Hell Bosses")) {
                    if (gameState_800babc8.chapterIndex_98 == 3) {
                        shopId_8007a3b4.set(42);
                        whichMenu_800bdc38 = WhichMenu.INIT_SHOP_MENU_6;
                        inventoryMenuState_800bdc28.set(InventoryMenuState._9);
                    }
                }
            } else if (hotkey.contains(InputAction.BUTTON_NORTH) && hotkey.contains(InputAction.BUTTON_SHOULDER_LEFT_1)) {
                if (difficulty.equals("Hard Mode") || difficulty.equals("US + Hard Bosses") || difficulty.equals("Hell Mode") || difficulty.equals("Hard + Hell Bosses")) {
                    if (gameState_800babc8.chapterIndex_98 == 3) {
                        shopId_8007a3b4.set(43);
                        whichMenu_800bdc38 = WhichMenu.INIT_SHOP_MENU_6;
                        inventoryMenuState_800bdc28.set(InventoryMenuState._9);
                    }
                }
            } else if (hotkey.contains(InputAction.BUTTON_NORTH) && hotkey.contains(InputAction.BUTTON_SHOULDER_RIGHT_2)) {
                if (gameState_800babc8.chapterIndex_98 >= 1) {
                    shopId_8007a3b4.set(40);
                    whichMenu_800bdc38 = WhichMenu.INIT_SHOP_MENU_6;
                    inventoryMenuState_800bdc28.set(InventoryMenuState._9);
                }
            } else if (hotkey.contains(InputAction.BUTTON_NORTH) && hotkey.contains(InputAction.BUTTON_SHOULDER_LEFT_2)) {
                shopId_8007a3b4.set(41);
                whichMenu_800bdc38 = WhichMenu.INIT_SHOP_MENU_6;
                inventoryMenuState_800bdc28.set(InventoryMenuState._9);
            }
        }
    }

    @EventListener
    public void handleAttackSpGain(final AttackSpGainEvent event) {
        final PlayerBattleObject bobj = event.bobj;
        if(bobj.charId_272 == 2 || bobj.charId_272 == 8) {
            final String difficulty = GameEngine.CONFIG.getConfig(DIFFICULTY.get());

            if(difficulty.equals("Hard Mode") || difficulty.equals("Us + Hard Bosses")) {
                switch(bobj.dlevel_06) {
                    case 6 -> event.sp = 150;
                    case 7 -> event.sp = 175;
                }
            }

            if(difficulty.equals("Hell Mode") || difficulty.equals("Hard + Hell Bosses")) {
                switch(bobj.dlevel_06) {
                    case 1 -> event.sp = 15;
                    case 2 -> event.sp = 25;
                    case 3 -> event.sp = 40;
                    case 4 -> event.sp = 50;
                    case 5, 6 -> event.sp = 75;
                    case 7 -> event.sp = 90;
                }
            }
        }
    }

    @EventListener
    public void selectedTarget(final SingleMonsterTargetEvent event) {
        for(int i = 0; i < allBobjCount_800c66d0.get(); i++) {
            final ScriptState<? extends BattleObject27c> state = battleState_8006e398.allBobjs_e0c[i];
            final BattleObject27c bobj = state.innerStruct_00;
            if (bobj instanceof PlayerBattleObject player) {
              if (player.charId_272 == 1 || player.charId_272 == 5) {
                    int z = 99;
                    int charSlot = player.charSlot_276;
                    if (currentTurnBobj_800c66c8 == battleState_8006e398.charBobjs_e40[charSlot]) {
                        z = 0;
                    }
                    windMarkGfx[windMark[event.monster.charSlot_276]].render(charSlot * 94 - 143, 64, z);
                } else if (player.charId_272 == 4) {
                    int z = 99;
                    int charSlot = player.charSlot_276;
                    if (currentTurnBobj_800c66c8 == battleState_8006e398.charBobjs_e40[charSlot]) {
                        z = 0;
                    }
                    thunderChargeGfx[thunderCharge[event.monster.charSlot_276]].render(charSlot * 94 - 143, 64, z);
                }
            }
        }
    }

    @EventListener
    public void combatDescription(final BattleDescriptionEvent event) {
        if (event.textType == 5) {

        }
    }
}
