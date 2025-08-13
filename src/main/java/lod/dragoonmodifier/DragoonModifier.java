package lod.dragoonmodifier;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import legend.core.GameEngine;
import legend.core.QueuedModelStandard;
import legend.core.gte.MV;
import legend.core.platform.input.ButtonInputActivation;
import legend.core.platform.input.InputAction;
import legend.core.platform.input.InputActionRegistryEvent;
import legend.core.platform.input.InputButton;
import legend.core.platform.input.InputKey;
import legend.core.platform.input.ScancodeInputActivation;
import legend.game.EngineStateEnum;
import legend.game.Scus94491BpeSegment_8002;
import legend.game.Scus94491BpeSegment_800b;
import legend.game.characters.Addition04;
import legend.game.characters.Element;
import legend.game.characters.ElementSet;
import legend.game.characters.UnaryStatModConfig;
import legend.game.characters.VitalsStat;
import legend.game.combat.Battle;
import legend.game.combat.bent.AttackEvent;
import legend.game.combat.bent.BattleEntity27c;
import legend.game.combat.bent.BattleEntityStat;
import legend.game.combat.bent.MonsterBattleEntity;
import legend.game.combat.bent.PlayerBattleEntity;
import legend.game.combat.deff.DeffPackage;
import legend.game.combat.deff.RegisterDeffsEvent;
import legend.game.combat.types.AdditionHitProperties10;
import legend.game.combat.types.AdditionHits80;
import legend.game.combat.types.AttackType;
import legend.game.combat.types.CombatantStruct1a8;
import legend.game.combat.ui.BattleHud;
import legend.game.combat.ui.ListMenu;
import legend.game.combat.ui.ListPosition;
import legend.game.combat.ui.ModMenu;
import legend.game.combat.ui.RegisterModMenuEvent;
import legend.game.i18n.I18n;
import legend.game.inventory.*;
import legend.game.inventory.screens.FontOptions;
import legend.game.inventory.screens.MenuStack;
import legend.game.inventory.screens.ShopScreen;
import legend.game.inventory.screens.TextColour;
import legend.game.modding.coremod.CoreMod;
import legend.game.modding.events.RenderEvent;
import legend.game.modding.events.battle.*;
import legend.game.modding.events.characters.AdditionUnlockEvent;
import legend.game.modding.events.characters.XpToLevelEvent;
import legend.game.modding.events.config.ConfigLoadedEvent;
import legend.game.modding.events.gamestate.DeleteSaveEvent;
import legend.game.modding.events.gamestate.LoadGameEvent;
import legend.game.modding.events.gamestate.NewGameEvent;
import legend.game.modding.events.gamestate.SaveGameEvent;
import legend.game.modding.events.input.InputPressedEvent;
import legend.game.modding.events.input.InputReleasedEvent;
import legend.game.modding.events.input.RegisterDefaultInputBindingsEvent;
import legend.game.modding.events.inventory.AddGoldEvent;
import legend.game.modding.events.inventory.DescriptionEvent;
import legend.game.modding.events.inventory.EquipmentCanEquipEvent;
import legend.game.modding.events.inventory.EquipmentStatsEvent;
import legend.game.modding.events.inventory.GiveEquipmentEvent;
import legend.game.modding.events.inventory.GiveItemEvent;
import legend.game.modding.events.inventory.IconDisplayEvent;
import legend.game.modding.events.inventory.RepeatItemReturnEvent;
import legend.game.modding.events.inventory.ShopContentsEvent;
import legend.game.modding.events.scripting.DrgnFileEvent;
import legend.game.modding.events.submap.SubmapLoadEvent;
import legend.game.modding.events.submap.SubmapWarpEvent;
import legend.game.saves.BoolConfigEntry;
import legend.game.saves.ConfigCategory;
import legend.game.saves.ConfigEntry;
import legend.game.saves.ConfigRegistryEvent;
import legend.game.saves.ConfigStorageLocation;
import legend.game.scripting.ScriptFile;
import legend.game.scripting.ScriptState;
import legend.game.submap.SMap;
import legend.game.submap.SubmapState;
import legend.game.types.ActiveStatsa0;
import legend.game.types.EquipmentSlot;
import legend.game.types.LevelStuff08;
import legend.game.types.MagicStuff08;
import legend.game.types.SpellStats0c;
import legend.game.types.Translucency;
import legend.game.unpacker.Loader;
import legend.lodmod.LodItems;
import legend.lodmod.LodMod;
import legend.lodmod.RetailDeffPackage;
import legend.lodmod.items.AngelsPrayerItem;
import legend.lodmod.items.AttackBallItem;
import legend.lodmod.items.AttackItem;
import legend.lodmod.items.BodyPurifierItem;
import legend.lodmod.items.BuffItem;
import legend.lodmod.items.CauseStatusItem;
import legend.lodmod.items.CharmPotionItem;
import legend.lodmod.items.DepetrifierItem;
import legend.lodmod.items.HealingBreezeItem;
import legend.lodmod.items.HealingFogItem;
import legend.lodmod.items.HealingPotionItem;
import legend.lodmod.items.HealingRainItem;
import legend.lodmod.items.MindPurifierItem;
import legend.lodmod.items.MoonSerenadeItem;
import legend.lodmod.items.PandemoniumItem;
import legend.lodmod.items.PsycheBombXItem;
import legend.lodmod.items.RecoveryBallItem;
import legend.lodmod.items.ShieldItem;
import legend.lodmod.items.SignetStoneItem;
import legend.lodmod.items.SmokeBallItem;
import legend.lodmod.items.SpiritPotionItem;
import legend.lodmod.items.SunRhapsodyItem;
import legend.lodmod.items.TotalVanishingItem;
import lod.dragoonmodifier.configs.*;
import lod.dragoonmodifier.events.DifficultyChangedEvent;
import lod.dragoonmodifier.events.ShanaGetArrowCountEvent;
import lod.dragoonmodifier.events.ShanaSwapArrowEvent;
import lod.dragoonmodifier.items.DraModItemDeffPackage;
import lod.dragoonmodifier.events.HellModeAdjustmentEvent;
import lod.dragoonmodifier.items.DraModShieldItem;
import lod.dragoonmodifier.items.DraModSpiritPotion;
import lod.dragoonmodifier.saves.DraModAchievements;
import lod.dragoonmodifier.saves.DraModSaveFile;
import lod.dragoonmodifier.saves.SaveFile;
import lod.dragoonmodifier.screens.DraMenu;
import lod.dragoonmodifier.screens.DraModAchievementsScreen;
import lod.dragoonmodifier.screens.DraModNotification;
import lod.dragoonmodifier.screens.ElementalQuiver;
import lod.dragoonmodifier.values.DamageTracker;
import lod.dragoonmodifier.values.ElementalBomb;
import lod.dragoonmodifier.values.EnrageMode;
import org.apache.commons.lang3.ArrayUtils;
import org.legendofdragoon.modloader.Mod;
import org.legendofdragoon.modloader.events.EventListener;
import org.legendofdragoon.modloader.registries.DuplicateRegistryIdException;
import org.legendofdragoon.modloader.registries.Registrar;
import org.legendofdragoon.modloader.registries.RegistryDelegate;
import org.legendofdragoon.modloader.registries.RegistryId;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.stream.IntStream;

import static legend.core.GameEngine.*;
import static legend.game.SItem.getXpToNextLevel;
import static legend.game.Scus94491BpeSegment_8002.playMenuSound;
import static legend.game.Scus94491BpeSegment_8004.currentEngineState_8004dd04;
import static legend.game.Scus94491BpeSegment_8004.engineState_8004dd20;
import static legend.game.Scus94491BpeSegment_8005.submapCut_80052c30;
import static legend.game.Scus94491BpeSegment_8006.battleState_8006e398;
import static legend.game.Scus94491BpeSegment_8007.shopId_8007a3b4;
import static legend.game.Scus94491BpeSegment_800b.*;
import static legend.game.combat.Battle.spellStats_800fa0b8_Monster;
import static legend.game.combat.Battle.spellStats_800fa0b8_Player;
import static legend.game.combat.ui.BattleMenuStruct58.battleMenuIconMetrics_800fb674;
import static legend.lodmod.LodMod.DARK_ELEMENT;
import static legend.lodmod.LodMod.DIVINE_ELEMENT;
import static legend.lodmod.LodMod.EARTH_ELEMENT;
import static legend.lodmod.LodMod.FIRE_ELEMENT;
import static legend.lodmod.LodMod.HP_STAT;
import static legend.lodmod.LodMod.LIGHT_ELEMENT;
import static legend.lodmod.LodMod.MP_STAT;
import static legend.lodmod.LodMod.SPEED_STAT;
import static legend.lodmod.LodMod.SP_STAT;
import static legend.lodmod.LodMod.THUNDER_ELEMENT;
import static legend.lodmod.LodMod.WATER_ELEMENT;
import static legend.lodmod.LodMod.WIND_ELEMENT;

@Mod(id = DragoonModifier.MOD_ID, version = "^3.0.0")
public class DragoonModifier {
  public static final String MOD_ID = "dragoon_modifier";
  public static boolean DEBUG_MODE = true;
  public static final String[] charNames = {"Dart", "Lavitz", "Shana", "Rose", "Haschel", "Albert", "Meru", "Kongol", "???"};

  public static final List<String[]> monsterStats = new ArrayList<>();
  public static final List<String[]> monstersRewardsStats = new ArrayList<>();
  public static final List<String[]> additionStats = new ArrayList<>();
  public static final List<String[]> additionMultiStats = new ArrayList<>();
  public static final List<String[]> additionUnlockStats = new ArrayList<>();
  public static final List<String[]> characterStatsTable = new ArrayList<>();
  public static final List<String[]> dragoonStatsTable = new ArrayList<>();
  public static final List<String[]> xpNextStats = new ArrayList<>();
  public static final List<String[]> dxpNextStats = new ArrayList<>();
  public static final List<String[]> spellStatsPlayer = new ArrayList<>();
  public static final List<String[]> spellStatsMonster = new ArrayList<>();
  public static final List<String[]> equipStats = new ArrayList<>();
  public static final List<String[]> itemStats = new ArrayList<>();
  public static final List<String[]> shopItems = new ArrayList<>();
  public static final List<String[]> levelCaps = new ArrayList<>();
  public static final List<String[]> spBarColours = new ArrayList<>();
  public static final List<String[]> shanaSpGain = new ArrayList<>();
  public static final List<String[]> ultimateData = new ArrayList<>();
  public static final Map<RegistryId, Equipment> registryEquipment = new HashMap<>();
  public static final Map<RegistryId, Item> registryItems = new HashMap<>();

  public static int maxCharacterLevel = 60;
  public static int maxDragoonLevel = 5;
  public static int maxAdditionLevel = 5;
  public static int additionsPerLevel = 20;
  public int currentPlayerSlot;
  public int currentPlayerId;
  public PlayerBattleEntity currentPlayer;
  public boolean dragonBlockStaff;
  public int[] enrageMode = new int[10];
  public int[] enrageModeProtection = new int[10];
  public Element[] previousElement = new Element[3];
  public String[][] damageTrackerEquips = new String[3][5];
  public int[][] damageTracker = new int[3][5];
  public int[] damageTrackerPreviousHP = new int[10];
  public int damageTrackerPreviousCharacter;
  public int damageTrackerPreviousCharacterID;
  public int damageTrackerPreviousAttackType;
  public ArrayList<String> damageTrackerLog = new ArrayList<>();
  public boolean damageTrackerPrinted;
  public boolean[] elementalAttack = new boolean[3];
  public int[] windMark = new int[10];
  public int[] thunderCharge = new int[10];
  public int[] staticCharge = new int[3];
  public boolean flowerStormOverride;
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
  public boolean swappedEXP;
  public int[] swapEXPParty = new int[3];
  public static int[][] ultimateEncounter = {{487, 10}, {386, 3}, {414, 8},
    {461, 21}, {412, 16}, {413, 70}, {387, 5}, {415, 12},
    /*{449, 68}, {402, 23}, {403, 29}, {417, 31}, {418, 41}, {448, 68}, {416, 38}, {422, 42}, {423, 47}, {432, 69}, {430, 67}, {433, 56}, {431, 54}, {447, 68}*/
  };
  public static boolean ultimateBattle;
  public static int ultimateLevelCap = 30;
  public double[][] ultimatePenality = new double[3][2];
  public boolean[] bonusItemSP = new boolean[3];
  public boolean[] ouroboros = new boolean[3];
  public int[] ringOfElements = new int[3];
  public Element[] ringOfElementsElement = new Element[3];
  public ArrayList<BattleEntity27c> attacked = new ArrayList<>();

  public static int selectedItemId = -1;
  public static int selectedFakeItemId;
  //public static ItemStats0c selectedItemStats = null;
  public static int lastSelectedMenuType;

  public Set<InputAction> hotkey = new HashSet<>();

  public boolean[] burnStackMode = new boolean[3];
  public int[] burnStacks = new int[3];
  public int[] previousBurnStacks = new int[3];
  public double dmgPerBurn = 0.1;
  public int[] burnStacksMax = new int[3];
  public double maxBurnAddition = 1;
  public final int burnStackFlameshot = 1;
  public final int burnStackExplosion = 2;
  public final int burnStackFinalBurst = 3;
  public final int burnStackRedEye = 4;
  public final int burnStackAddition = 1;
  public boolean[] burnAdded = new boolean[3];
  public static boolean faustBattle;
  public int armorOfLegendTurns;
  public int legendCasqueTurns;
  public int[] protectionShield = new int[3];
  public boolean[] spiritBottle = new boolean[3];
  public boolean[] speedBottle = new boolean[3];
  public boolean[] healingBottle = new boolean[3];
  public boolean[] sunBottle = new boolean[3];
  public boolean shanaDeffArrow = true;
  public boolean haschelInParty;
  public boolean[] windBarrier = new boolean[3];
  public int[] roseSiphon = new int[3];
  public int roseSiphonMax;
  public boolean[] roseSiphonActivated = new boolean[3];
  public int[] meruIceShield = new int[3];
  public int meruIceShieldMax;
  public boolean[] meruWinglyMagic = new boolean[3];
  public boolean[] kongolCounterStance = new boolean[3];
  public int[] kongolCounterStanceTurns = new int[3];
  public int kongolCounterSpeed = 0;
  public int currentMenuBlock;
  public RegistryId[] shanaArrow = new RegistryId[3];
  public int[] shanaArrowCount = new int[3];
  public int[] shanaMaxArrowCount = new int[3];
  public RegistryId[] shanaPreviousArrow = new RegistryId[3];
  public boolean isItemArrow;
  public int trackGold = 0;

  private final FontOptions fontOptions = new FontOptions().colour(TextColour.WHITE);

  public static final Registrar<ConfigEntry<?>, ConfigRegistryEvent> DRAMOD_CONFIG_REGISTRAR = new Registrar<>(REGISTRIES.config, MOD_ID);
  public static final RegistryDelegate<DifficultyEntryConfig> DIFFICULTY = DRAMOD_CONFIG_REGISTRAR.register("difficulty", DifficultyEntryConfig::new);
  public static final RegistryDelegate<BoolConfigEntry> MONSTER_HP_BAR = DRAMOD_CONFIG_REGISTRAR.register("hp_bar", MonsterHPBarConfig::new);
  public static final RegistryDelegate<EnrageModeConfig> ENRAGE_MODE = DRAMOD_CONFIG_REGISTRAR.register("enrage_mode", EnrageModeConfig::new);
  public static final RegistryDelegate<HellFlowerStormConfig> FLOWER_STORM = DRAMOD_CONFIG_REGISTRAR.register("flower_storm", HellFlowerStormConfig::new);
  public static final RegistryDelegate<UltimateBossConfig> ULTIMATE_BOSS = DRAMOD_CONFIG_REGISTRAR.register("ultimate_boss", UltimateBossConfig::new);
  public static final RegistryDelegate<ElementalBombConfig> ELEMENTAL_BOMB = DRAMOD_CONFIG_REGISTRAR.register("elemental_bomb", ElementalBombConfig::new);
  public static final RegistryDelegate<DamageTrackerConfig> DAMAGE_TRACKER = DRAMOD_CONFIG_REGISTRAR.register("damage_tracker", DamageTrackerConfig::new);
  public static final RegistryDelegate<BoolConfigEntry> SHOW_ACHIEVEMENTS = DRAMOD_CONFIG_REGISTRAR.register("show_achievements", () -> new BoolConfigEntry(false, ConfigStorageLocation.CAMPAIGN, ConfigCategory.GAMEPLAY));

  public static final Registrar<Item, ItemRegistryEvent> DRAMOD_ITEM_REGISTRAR = new Registrar<>(REGISTRIES.items, MOD_ID);
  public static final Registrar<DeffPackage, RegisterDeffsEvent> DRAMOD_ITEM_DEFF_REGISTRAR = new Registrar<>(REGISTRIES.deff, MOD_ID);
  public static final Registrar<InputAction, InputActionRegistryEvent> DRAMOD_INPUT_REGISTRAR = new Registrar<>(REGISTRIES.inputActions, MOD_ID);

  public static final RegistryDelegate<InputAction> INPUT_ACTION_DRAMENU = DRAMOD_INPUT_REGISTRAR.register("dramenu_open", InputAction::fixed);
  public static final RegistryDelegate<InputAction> INPUT_ACTION_DRAACHIEVEMENTS = DRAMOD_INPUT_REGISTRAR.register("draachievements_open", InputAction::fixed);

  public static DraModSaveFile draModSave;

  public static String draMenuMessage = "";

  public final int[] bossEncounters = {
    384, //Commander
    386, //Fruegel I
    414, //Urobolus
    385, //Sandora Elite
    388, //Kongol I
    408, //Virage I
    415, //Fire Bird
    393, //Greham + Feyrbrand
    412, //Drake the Bandit
    413, //Jiango
    387, //Fruegel II
    461, //Sandora Elite II
    389, //Kongol II
    390, //Emperor Doel
    402, //Mappi
    409, //Virage II
    403, //Gehrich + Mappi
    396, //Lenus
    417, //Ghost Commander
    397, //Lenus + Regole
    418, //Kamuy
    410, //S Virage
    416, //Grand Jewel
    394, //Divine Dragon
    422, //Windigo
    392, //Lloyd
    423, //Polter Set
    398, //Damia
    399, //Syuveil
    400, //Belzac
    401, //Kanzas
    420, //Magician Faust
    432, //Last Kraken
    430, //Executioners
    449, //Spirit (Feyrbrand)
    448, //Spirit (Regole)
    447, //Spirit (Divine Dragon)
    431, //Zackwell
    433, //Imago
    411, //S Virage II
    442, //Zieg
    443 //Melbu Fraahma
  };

  /*private final Texture[] burnStacksGfx = new Texture[4];
  private final Texture[] windMarksGfx = new Texture[4];
  private final Texture[] thunderChargesGfx = new Texture[11];
  private Obj battleHudOverlay;*/

  private final static MenuStack menuStack = new MenuStack();
  private static boolean draMenuOpen;
  private static boolean draAchievementsOpen;
  private static boolean draNotificationOpen;

  private boolean startThreads;

  //region Startup
  public DragoonModifier() {
    GameEngine.EVENTS.register(this);

    if(!this.startThreads) {
      //new Thread(new DRP()).start();
      //new Thread(new scriptFlagTracker()).start();
      this.startThreads = true;
    }
  }

  public RegistryId id(final String entryId) {
    return new RegistryId(MOD_ID, entryId);
  }

  public RegistryId idCore(final String entryId) {
    return new RegistryId("lod", entryId);
  }

  public void print(final String text) {
    if(DEBUG_MODE) {
      System.out.println("[DRAGOON MODIFIER] " + text);
    }
  }

  @EventListener
  public void configRegistry(final ConfigRegistryEvent event) {
    this.print("Config Registry Event");
    DRAMOD_CONFIG_REGISTRAR.registryEvent(event);
    this.loadAllCsvs(GameEngine.CONFIG.getConfig(DIFFICULTY.get()));
  }

  @EventListener
  public void configLoaded(final ConfigLoadedEvent event) {
    if(event.storageLocation == DIFFICULTY.get().storageLocation) {
      this.print("Config Loaded Event" + equipStats.size());
      this.loadAllCsvs(event.configCollection.getConfig(DIFFICULTY.get()));
    }
  }

  @EventListener
  public void difficultyChanged(final DifficultyChangedEvent event) {
    this.loadAllCsvs(GameEngine.CONFIG.getConfig(DIFFICULTY.get()));
  }

  public List<String[]> loadCSV(final String path) {
    try(final FileReader fr = new FileReader(path, StandardCharsets.UTF_8);
        final CSVReader csv = new CSVReader(fr)) {
      final List<String[]> list = csv.readAll();
      list.removeFirst();
      return list;
    } catch(final IOException | CsvException e) {
      throw new RuntimeException(e);
    }
  }

  private void loadCsvIntoList(final String difficulty, final List<String[]> list, final String file) {
    list.clear();
    list.addAll(this.loadCSV("./mods/dragoon_modifier/" + difficulty + '/' + file));
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
    this.loadCsvIntoList(difficulty, spellStatsPlayer, "scdk-player-spell-stats.csv");
    this.loadCsvIntoList(difficulty, spellStatsMonster, "scdk-monster-spell-stats.csv");
    this.loadCsvIntoList(difficulty, equipStats, "scdk-equip-stats.csv");
    this.loadCsvIntoList(difficulty, itemStats, "scdk-thrown-item-stats.csv");
    this.loadCsvIntoList(difficulty, shopItems, "scdk-shop-items.csv");
    this.loadCsvIntoList(difficulty, levelCaps, "scdk-level-caps.csv");
    this.loadCsvIntoList(difficulty, spBarColours, "scdk-sp-bar-colours.csv");
    this.loadCsvIntoList(difficulty, shanaSpGain, "scdk-shana-sp-gain.csv");
    this.loadCsvIntoList("Ultimate", ultimateData, "scdk-ultimate.csv");
    maxCharacterLevel = Integer.parseInt(levelCaps.getFirst()[0]);
    maxDragoonLevel = Integer.parseInt(levelCaps.getFirst()[1]);
    maxAdditionLevel = Integer.parseInt(levelCaps.getFirst()[2]);
    additionsPerLevel = Integer.parseInt(levelCaps.getFirst()[3]);

    this.print("Loaded using directory: " + difficulty + equipStats.size());
    this.configSwapped();
  }

  @EventListener
  public void newGame(final NewGameEvent event) {
    final String difficulty = GameEngine.CONFIG.getConfig(DIFFICULTY.get());
    if("Hell Mode".equals(difficulty) || "Hard + Hell Bosses".equals(difficulty)) {
      event.gameState.gold_94 = 200;
    } else {
      event.gameState.gold_94 = 20;
    }
  }

  @EventListener
  public void submapWarp(final SubmapWarpEvent event) {
    final String difficulty = GameEngine.CONFIG.getConfig(DIFFICULTY.get());
    if(submapCut_80052c30 == 676 && gameState_800babc8.charData_32c[0].level_12 == 1) {
      if("Hell Mode".equals(difficulty) || "Hard + Hell Bosses".equals(difficulty)) {
        event.gameState.gold_94 = 200;
        event.gameState.items_2e9.add(REGISTRIES.items.getEntry("lod:spark_net").get());
        event.gameState.items_2e9.add(REGISTRIES.items.getEntry("lod:trans_light").get());
        event.gameState.items_2e9.add(REGISTRIES.items.getEntry("lod:healing_potion").get());
        event.gameState.items_2e9.add(REGISTRIES.items.getEntry("lod:healing_potion").get());
      } else {
        event.gameState.gold_94 = 20;
      }
    } else if(submapCut_80052c30 == 10) {
      if(("Hell Mode".equals(difficulty) || "Hard + Hell Bosses".equals(difficulty)) && gameState_800babc8.charData_32c[0].level_12 == 1) {
        gameState_800babc8.goods_19c[0] ^= 1;
        gameState_800babc8.goods_19c[0] ^= 1 << 1;
        gameState_800babc8.goods_19c[0] ^= 1 << 2;
        gameState_800babc8.goods_19c[0] ^= 1 << 3;
        gameState_800babc8.goods_19c[0] ^= 1 << 4;
        gameState_800babc8.goods_19c[0] ^= 1 << 5;
        gameState_800babc8.goods_19c[0] ^= 1 << 6;
      }
    }

    if(gameState_800babc8.stardust_9c >= 50) {
      addAchievement(75);
    } 
    if(gameState_800babc8.stardust_9c >= 40) {
      addAchievement(74);
    } 
    if(gameState_800babc8.stardust_9c >= 30) {
      addAchievement(73);
    } 
    if(gameState_800babc8.stardust_9c >= 20) {
      addAchievement(72);
    } 
    if(gameState_800babc8.stardust_9c >= 10) {
      addAchievement(71);
    }
    if(gameState_800babc8.gold_94 >= 20000) {
      addAchievement(66);
    }

    if(submapCut_80052c30 == 190) {
      if(isBitSet(3, 0)) {
        addAchievement(28);
      }
    }

    if(submapCut_80052c30 == 195) {
      if(isBitSet(3, 0)) {
        this.trackGold = gameState_800babc8.gold_94;
      }
    }

    if(this.trackGold != gameState_800babc8.gold_94 && (submapCut_80052c30 == 189 || submapCut_80052c30 == 196)) {
      addAchievement(70);
    }

    for(int i = 0; i < 9; i++) {
      if(gameState_800babc8.charData_32c[i].dlevel_13 >= 6) {
        addAchievement(55);
      } else if(gameState_800babc8.charData_32c[i].dlevel_13 >= 4) {
        addAchievement(54);
      }
    }

    if(gameState_800babc8.charData_32c[0].additionLevels_1a[0] >= 5 &&
      gameState_800babc8.charData_32c[0].additionLevels_1a[1] >= 5 &&
      gameState_800babc8.charData_32c[0].additionLevels_1a[2] >= 5 &&
      gameState_800babc8.charData_32c[0].additionLevels_1a[3] >= 5 &&
      gameState_800babc8.charData_32c[0].additionLevels_1a[4] >= 5 &&
      gameState_800babc8.charData_32c[0].additionLevels_1a[5] >= 5 &&
      gameState_800babc8.charData_32c[0].additionLevels_1a[6] >= 5 &&
      gameState_800babc8.charData_32c[0].dlevel_13 == 7) {
      addAchievement(56);
    }

    if(gameState_800babc8.charData_32c[1].additionLevels_1a[0] >= 5 &&
      gameState_800babc8.charData_32c[1].additionLevels_1a[1] >= 5 &&
      gameState_800babc8.charData_32c[1].additionLevels_1a[2] >= 5 &&
      gameState_800babc8.charData_32c[1].additionLevels_1a[3] >= 5 &&
      gameState_800babc8.charData_32c[1].additionLevels_1a[4] >= 5 &&
      gameState_800babc8.charData_32c[1].dlevel_13 == 7) {
      addAchievement(57);
    }

    if(gameState_800babc8.charData_32c[2].dlevel_13 == 7) {
      addAchievement(58);
    }

    if(gameState_800babc8.charData_32c[3].additionLevels_1a[0] >= 5 &&
      gameState_800babc8.charData_32c[3].additionLevels_1a[1] >= 5 &&
      gameState_800babc8.charData_32c[3].additionLevels_1a[2] >= 5 &&
      gameState_800babc8.charData_32c[3].additionLevels_1a[3] >= 5 &&
      gameState_800babc8.charData_32c[3].dlevel_13 == 7) {
      addAchievement(59);
    }

    if(gameState_800babc8.charData_32c[4].additionLevels_1a[0] >= 5 &&
      gameState_800babc8.charData_32c[4].additionLevels_1a[1] >= 5 &&
      gameState_800babc8.charData_32c[4].additionLevels_1a[2] >= 5 &&
      gameState_800babc8.charData_32c[4].additionLevels_1a[3] >= 5 &&
      gameState_800babc8.charData_32c[4].additionLevels_1a[4] >= 5 &&
      gameState_800babc8.charData_32c[4].additionLevels_1a[5] >= 5 &&
      gameState_800babc8.charData_32c[4].dlevel_13 == 7) {
      addAchievement(60);
    }

    if(gameState_800babc8.charData_32c[5].additionLevels_1a[0] >= 5 &&
      gameState_800babc8.charData_32c[5].additionLevels_1a[1] >= 5 &&
      gameState_800babc8.charData_32c[5].additionLevels_1a[2] >= 5 &&
      gameState_800babc8.charData_32c[5].additionLevels_1a[3] >= 5 &&
      gameState_800babc8.charData_32c[5].additionLevels_1a[4] >= 5 &&
      gameState_800babc8.charData_32c[5].dlevel_13 == 7) {
      addAchievement(61);
    }

    if(gameState_800babc8.charData_32c[6].additionLevels_1a[0] >= 5 &&
      gameState_800babc8.charData_32c[6].additionLevels_1a[1] >= 5 &&
      gameState_800babc8.charData_32c[6].additionLevels_1a[2] >= 5 &&
      gameState_800babc8.charData_32c[6].additionLevels_1a[3] >= 5 &&
      gameState_800babc8.charData_32c[6].additionLevels_1a[4] >= 5 &&
      gameState_800babc8.charData_32c[6].dlevel_13 == 7) {
      addAchievement(62);
    }

    if(gameState_800babc8.charData_32c[7].additionLevels_1a[0] >= 5 &&
      gameState_800babc8.charData_32c[7].additionLevels_1a[1] >= 5 &&
      gameState_800babc8.charData_32c[7].additionLevels_1a[2] >= 5 &&
      gameState_800babc8.charData_32c[7].dlevel_13 == 7) {
      addAchievement(63);
    }

    if(gameState_800babc8.charData_32c[8].dlevel_13 == 7) {
      addAchievement(64);
    }
  }

  public void configSwapped() {
    new Thread(() -> {
      while(engineState_8004dd20 == EngineStateEnum.PRELOAD_00) {
        try {
          this.print("Waiting for preload state to exit...");
          Thread.sleep(500);
        } catch(final InterruptedException e) {
          throw new RuntimeException(e);
        }
      }

      CoreMod.MAX_CHARACTER_LEVEL = maxCharacterLevel;
      CoreMod.MAX_DRAGOON_LEVEL = maxDragoonLevel;
      CoreMod.MAX_ADDITION_LEVEL = maxAdditionLevel;
      CoreMod.ADDITIONS_PER_LEVEL = additionsPerLevel;
      CoreMod.MAX_DRAGOON_XP = 999999;
      CoreMod.MAX_ADDITION_XP = 999;

      for(int i = 0; i < 9; i++) {
        CoreMod.CHARACTER_DATA[i].xpTable = new int[maxCharacterLevel + 1];
        CoreMod.CHARACTER_DATA[i].statsTable = new LevelStuff08[maxCharacterLevel + 1];
        CoreMod.CHARACTER_DATA[i].dxpTable = new int[CoreMod.MAX_DRAGOON_LEVEL + 1];
        CoreMod.CHARACTER_DATA[i].dragoonStatsTable = new MagicStuff08[CoreMod.MAX_DRAGOON_LEVEL + 1];
        CoreMod.CHARACTER_DATA[i].additions = new ArrayList<>();
        CoreMod.CHARACTER_DATA[i].additionsMultiplier = new ArrayList<>();
        CoreMod.CHARACTER_DATA[i].dragoonAddition = new ArrayList<>();
      }

      for(int i = 0; i < 9; i++) {
        for(int x = 0; x < maxCharacterLevel + 1; x++) {
          CoreMod.CHARACTER_DATA[i].xpTable[x] = Integer.parseInt(xpNextStats.get((maxCharacterLevel + 1) * i + x)[0]);
          CoreMod.CHARACTER_DATA[i].statsTable[x] = new LevelStuff08(Integer.parseInt(characterStatsTable.get((maxCharacterLevel + 1) * i + x)[5]), Integer.parseInt(characterStatsTable.get((maxCharacterLevel + 1) * i + x)[6]),
            Integer.parseInt(characterStatsTable.get((maxCharacterLevel + 1) * i + x)[0]), Integer.parseInt(characterStatsTable.get((maxCharacterLevel + 1) * i + x)[1]),
            Integer.parseInt(characterStatsTable.get((maxCharacterLevel + 1) * i + x)[2]), Integer.parseInt(characterStatsTable.get((maxCharacterLevel + 1) * i + x)[3]),
            Integer.parseInt(characterStatsTable.get((maxCharacterLevel + 1) * i + x)[4]));
        }
      }

      for(int i = 0; i < 9; i++) {
        CoreMod.CHARACTER_DATA[i].spBarColours = new int[maxDragoonLevel + 2][6];
        for(int x = 0; x < maxDragoonLevel + 1; x++) {
          CoreMod.CHARACTER_DATA[i].dxpTable[x] = Integer.parseInt(dxpNextStats.get(i)[x]);
        }
        for(int x = 0; x < maxDragoonLevel + 1; x++) {
          final int spellIndex = Integer.parseInt(dragoonStatsTable.get((maxDragoonLevel + 1) * i + x)[1]);
          CoreMod.CHARACTER_DATA[i].dragoonStatsTable[x] = new MagicStuff08(Integer.parseInt(dragoonStatsTable.get((maxDragoonLevel + 1) * i + x)[0]), spellIndex == 255 ? (byte)-1 : (byte)spellIndex,
            Integer.parseInt(dragoonStatsTable.get((maxDragoonLevel + 1) * i + x)[2]), Integer.parseInt(dragoonStatsTable.get((maxDragoonLevel + 1) * i + x)[3]),
            Integer.parseInt(dragoonStatsTable.get((maxDragoonLevel + 1) * i + x)[4]), Integer.parseInt(dragoonStatsTable.get((maxDragoonLevel + 1) * i + x)[5]),
            Integer.parseInt(dragoonStatsTable.get((maxDragoonLevel + 1) * i + x)[6]));
        }
      }

      for(int i = 0; i < 9; i++) {
        for(int x = 0; x < maxDragoonLevel + 2; x++) {
          final int top = Integer.decode(spBarColours.get(i * 2)[x].replace("#", "0x"));
          final int btm = Integer.decode(spBarColours.get(2 + 1)[x].replace("#", "0x"));

          final int[] topArray = {
            ((top >> 24) & 0xff),
            ((top >> 16) & 0xff),
            ((top >> 8) & 0xff),
            (top & 0xff)
          };

          final int[] btmArray = {
            ((btm >> 24) & 0xff),
            ((btm >> 16) & 0xff),
            ((btm >> 8) & 0xff),
            (btm & 0xff)
          };

          final int[] rgbArray = {topArray[1], topArray[2], topArray[3], btmArray[1], btmArray[2], btmArray[3]};
          CoreMod.CHARACTER_DATA[i].spBarColours[x] = rgbArray;
        }
      }

      this.loadCharacterAdditions(0, 0, 7);
      this.loadCharacterAdditions(1, 8, 13);
      this.loadCharacterAdditions(3, 14, 18);
      this.loadCharacterAdditions(7, 19, 22);
      this.loadCharacterAdditions(6, 23, 28);
      this.loadCharacterAdditions(4, 29, 35);
      this.loadCharacterAdditions(5, 36, 41);
      this.loadAdditionMultiplier(0, 0, 7);
      this.loadAdditionMultiplier(1, 8, 13);
      this.loadAdditionMultiplier(3, 14, 18);
      this.loadAdditionMultiplier(7, 19, 22);
      this.loadAdditionMultiplier(6, 23, 28);
      this.loadAdditionMultiplier(4, 29, 35);
      this.loadAdditionMultiplier(5, 36, 41);
      this.loadDragoonAddition(0, 7, false);
      this.loadDragoonAddition(1, 13, false);
      this.loadDragoonAddition(3, 18, false);
      this.loadDragoonAddition(7, 22, false);
      this.loadDragoonAddition(6, 28, false);
      this.loadDragoonAddition(4, 35, false);
      this.loadDragoonAddition(5, 41, false);
      this.loadDragoonAddition(0, 42, true);
      this.loadShanaAdditions();
      this.loadAdditionMultiplier(2, 7, 8);
      this.loadAdditionMultiplier(8, 7, 8);

      this.print("MOD loaded. Config swapped." + equipStats.size());
    }).start();
  }

  public Equipment getEquipFromRegistry(final RegistryId id) {
    for(final var entry : registryEquipment.entrySet()) {
      if(entry.getKey().equals(id)) {
        return entry.getValue();
      }
    }
    return null;
  }

  public Equipment getEquipFromRegistry(final String id) {
    for(final var entry : registryEquipment.entrySet()) {
      if(entry.getKey().toString().equals(id)) {
        return entry.getValue();
      }
    }
    return null;
  }

  public String getEquipDescription(final String key) {
    final String[] keySplit = key.split("\\.");
    final String registryId = keySplit[0] + ':' + keySplit[2];
    for(final String[] equip : equipStats) {
      if(equip[44].equals(registryId)) {
        return equip[43].replaceAll("§", "\n");
      }
    }
    return "";
  }

  public Item getItemFromRegistry(final RegistryId id) {
    for(final var entry : registryItems.entrySet()) {
      if(entry.getKey().equals(id)) {
        return entry.getValue();
      }
    }
    return null;
  }

  public Item getItemFromRegistry(final String id) {
    for(final var entry : registryItems.entrySet()) {
      if(entry.getKey().toString().equals(id)) {
        return entry.getValue();
      }
    }
    return null;
  }

  public String getItemDescription(final String key) {
    final String[] keySplit = key.split("\\.");
    final String registryId = keySplit[0] + ':' + keySplit[2];
    for(final String[] item : itemStats) {
      if(item[36].equals(registryId)) {
        if(engineState_8004dd20 == EngineStateEnum.COMBAT_06) {
          return item[29];
        } else {
          return item[28].replaceAll("§", "\n");
        }
      }
    }
    return "";
  }

  public int getItemRowFromTable(final String id) {
    final int row = 0;
    for(final String[] item : itemStats) {
      if(item[36].equals(id)) {
        return row;
      }
    }
    return row;
  }

  public ItemIcon getIconFromId(final int id) {
    for(final ItemIcon i : ItemIcon.values()) {
      if(i.icon == id) {
        return i;
      }
    }
    return ItemIcon.SWORD;
  }
  //endregion

  //region Additions
  public void loadCharacterAdditions(final int charIndex, final int additionStart, final int additionEnd) {
    CoreMod.CHARACTER_DATA[charIndex].additions = new ArrayList<>();

    for(int i = additionStart; i < additionEnd; i++) {
      final AdditionHitProperties10[] hits = new AdditionHitProperties10[8];

      for(int x = 0; x < 8; x++) {
        final int panDistance = Short.parseShort(additionStats.get(i * 8 + x)[8]);
        hits[x] = new AdditionHitProperties10(
          Short.parseShort(additionStats.get(i * 8 + x)[0]),
          Short.parseShort(additionStats.get(i * 8 + x)[1]),
          Short.parseShort(additionStats.get(i * 8 + x)[2]),
          Short.parseShort(additionStats.get(i * 8 + x)[3]),
          Short.parseShort(additionStats.get(i * 8 + x)[4]),
          Short.parseShort(additionStats.get(i * 8 + x)[5]),
          Short.parseShort(additionStats.get(i * 8 + x)[6]),
          Short.parseShort(additionStats.get(i * 8 + x)[7]),
          panDistance > 127 ? panDistance - 255 : panDistance,
          Short.parseShort(additionStats.get(i * 8 + x)[9]),
          Short.parseShort(additionStats.get(i * 8 + x)[10]),
          Short.parseShort(additionStats.get(i * 8 + x)[11]),
          Short.parseShort(additionStats.get(i * 8 + x)[12]),
          Short.parseShort(additionStats.get(i * 8 + x)[13]),
          Short.parseShort(additionStats.get(i * 8 + x)[14]),
          Short.parseShort(additionStats.get(i * 8 + x)[15])
        );
      }

      CoreMod.CHARACTER_DATA[charIndex].additions.add(new AdditionHits80(hits));
    }
  }

  public void loadAdditionMultiplier(final int charIndex, final int additionStart, final int additionEnd) {
    CoreMod.CHARACTER_DATA[charIndex].additionsMultiplier = new ArrayList<>();

    for(int i = additionStart; i < additionEnd; i++) {
      final Addition04[] multipliers = new Addition04[maxAdditionLevel + 1];

      for(int x = 0; x < maxAdditionLevel + 1; x++) {
        multipliers[x] = new Addition04();
        multipliers[x]._00 = Integer.parseInt(additionMultiStats.get(i)[x * 4]);
        multipliers[x].spMultiplier_02 = Integer.parseInt(additionMultiStats.get(i)[x * 4 + 2]);
        multipliers[x].damageMultiplier_03 = Integer.parseInt(additionMultiStats.get(i)[x * 4 + 3]);
      }

      CoreMod.CHARACTER_DATA[charIndex].additionsMultiplier.add(multipliers);
    }
  }

  public void loadDragoonAddition(final int charIndex, final int dragoonIndex, final boolean divine) {
    CoreMod.CHARACTER_DATA[charIndex].dragoonAddition = new ArrayList<>();
    final AdditionHitProperties10[] hits = new AdditionHitProperties10[8];

    for(int x = 0; x < 8; x++) {
      final int panDistance = Short.parseShort(additionStats.get(x * 8 + x)[8]);
      hits[x] = new AdditionHitProperties10(
        Short.parseShort(additionStats.get(dragoonIndex * 8 + x)[0]),
        Short.parseShort(additionStats.get(dragoonIndex * 8 + x)[1]),
        Short.parseShort(additionStats.get(dragoonIndex * 8 + x)[2]),
        Short.parseShort(additionStats.get(dragoonIndex * 8 + x)[3]),
        Short.parseShort(additionStats.get(dragoonIndex * 8 + x)[4]),
        Short.parseShort(additionStats.get(dragoonIndex * 8 + x)[5]),
        Short.parseShort(additionStats.get(dragoonIndex * 8 + x)[6]),
        Short.parseShort(additionStats.get(dragoonIndex * 8 + x)[7]),
        panDistance > 127 ? panDistance - 255 : panDistance,
        Short.parseShort(additionStats.get(dragoonIndex * 8 + x)[9]),
        Short.parseShort(additionStats.get(dragoonIndex * 8 + x)[10]),
        Short.parseShort(additionStats.get(dragoonIndex * 8 + x)[11]),
        Short.parseShort(additionStats.get(dragoonIndex * 8 + x)[12]),
        Short.parseShort(additionStats.get(dragoonIndex * 8 + x)[13]),
        Short.parseShort(additionStats.get(dragoonIndex * 8 + x)[14]),
        Short.parseShort(additionStats.get(dragoonIndex * 8 + x)[15])
      );
    }

    CoreMod.CHARACTER_DATA[charIndex].dragoonAddition.add(new AdditionHits80(hits));
  }

  public void loadShanaAdditions() {
    CoreMod.CHARACTER_DATA[2].additions = new ArrayList<>();
    CoreMod.CHARACTER_DATA[8].additions = new ArrayList<>();
    CoreMod.CHARACTER_DATA[2].dragoonAddition = new ArrayList<>();
    CoreMod.CHARACTER_DATA[8].dragoonAddition = new ArrayList<>();

    final AdditionHitProperties10[] hits = new AdditionHitProperties10[8];
    for(int x = 0; x < 8; x++) {
      hits[x] = new AdditionHitProperties10(
        0,
        0,
        0,
        0,
        0,
        0,
        0,
        0,
        0,
        0,
        0,
        0,
        0,
        0,
        0,
        0
      );
    }

    CoreMod.CHARACTER_DATA[2].additions.add(new AdditionHits80(hits));
    CoreMod.CHARACTER_DATA[2].dragoonAddition.add(new AdditionHits80(hits));
    CoreMod.CHARACTER_DATA[8].additions.add(new AdditionHits80(hits));
    CoreMod.CHARACTER_DATA[8].dragoonAddition.add(new AdditionHits80(hits));
  }

  @EventListener
  public void additionUnlock(final AdditionUnlockEvent unlock) {
    unlock.additionLevel = Integer.parseInt(additionUnlockStats.get(unlock.additionId)[0]);
  }

  /*@EventListener public void additionMulti(final AdditionHitMultiplierEvent multiplier) { //Is this needed? It's not needed keeping it here.
    multiplier.additionSpMulti = Integer.parseInt(additionMultiStats.get(multiplier.additionId)[(multiplier.additionLevel) * 4 + 2]);
    multiplier.additionDmgMulti = Integer.parseInt(additionMultiStats.get(multiplier.additionId)[(multiplier.additionLevel) * 4 + 3]);
  }*/
  //endregion

  //region Inventory
  @EventListener
  public void equipmentRegistry(final EquipmentRegistryEvent event) {
    this.print("Equipment Registry Event");
    //registryEquipment.clear();

    final File[] baseDirectory = new File("./mods/dragoon_modifier").listFiles();
    if(baseDirectory != null) {
      for(final File file : baseDirectory) {
        if(file.isDirectory() && (!"Ultimate".equals(file.getName()) && !"Damage Tracker".equals(file.getName()) && !"scripts".equals(file.getName()) && !"patches".equals(file.getName()))) {
          try(final FileReader fr = new FileReader(file.getAbsolutePath() + "/scdk-equip-stats.csv", StandardCharsets.UTF_8);
              final CSVReader csv = new CSVReader(fr)) {
            final List<String[]> list = csv.readAll();
            list.removeFirst();

            for(final String[] equip : list) {
              //this.print("Registering: " + file.getName() + '/' + equip[44]);
              final ElementSet elementalResistance = new ElementSet();
              final ElementSet elementalImmunity = new ElementSet();
              final EquipmentSlot equipmentSlot = Integer.parseInt(equip[1]) == 0x80 ? EquipmentSlot.WEAPON : Integer.parseInt(equip[1]) == 0x20 ? EquipmentSlot.ARMOUR : Integer.parseInt(equip[1]) == 0x40 ? EquipmentSlot.HELMET : Integer.parseInt(equip[1]) == 0x10 ? EquipmentSlot.BOOTS : EquipmentSlot.ACCESSORY;

              elementalResistance.add(Element.fromFlag(Integer.parseInt(equip[6])).get());
              elementalImmunity.add(Element.fromFlag(Integer.parseInt(equip[7])).get());

              final Equipment dmEquip = new Equipment(
                Integer.parseInt(equip[41]), //Price
                Integer.parseInt(equip[0]), //Flags
                equipmentSlot, //type
                Integer.parseInt(equip[2]), //_02
                Integer.parseInt(equip[3]), //equipable
                Element.fromFlag(Integer.parseInt(equip[4])).get(), //Element
                Integer.parseInt(equip[5]), //_05
                elementalResistance, //elementalResistance
                elementalImmunity, //elementalImmunity
                Integer.parseInt(equip[8]), //Status Resist
                Integer.parseInt(equip[9]), //_09
                Integer.parseInt(equip[10]), //AT
                Integer.parseInt(equip[11]), //mpPerPhysicalHit
                Integer.parseInt(equip[12]), //spPerPhysicalHit
                Integer.parseInt(equip[13]), //mpPerMagicalHit
                Integer.parseInt(equip[14]), //spPerMagicalHit
                Integer.parseInt(equip[15]), //hpMultiplier
                Integer.parseInt(equip[16]), //mpMultiplier
                Integer.parseInt(equip[17]), //spMultiplier
                Boolean.parseBoolean(equip[18]), //magicalResistance
                Boolean.parseBoolean(equip[19]), //physicalResistance
                Boolean.parseBoolean(equip[20]), //magicalImmunity
                Boolean.parseBoolean(equip[21]), //physicalImmunity
                Integer.parseInt(equip[22]), //revive
                Integer.parseInt(equip[23]), //hpRegen
                Integer.parseInt(equip[24]), //mpRegen
                Integer.parseInt(equip[25]), //spRegen
                Integer.parseInt(equip[26]), //escapeBonus
                this.getIconFromId(Integer.parseInt(equip[27])), //icon
                Integer.parseInt(equip[28]), //spd
                Integer.parseInt(equip[29]), //atkHi
                Integer.parseInt(equip[30]), //matk
                Integer.parseInt(equip[31]), //def
                Integer.parseInt(equip[32]), //mdef
                Integer.parseInt(equip[33]), //aHit
                Integer.parseInt(equip[34]), //mHit
                Integer.parseInt(equip[35]), //aAv
                Integer.parseInt(equip[36]), //mAv
                Integer.parseInt(equip[37]), //onStatusChance
                Integer.parseInt(equip[38]), //_19
                Integer.parseInt(equip[39]), //_1a
                Integer.parseInt(equip[40]) //On Hit Status
              );

              if(file.getName().equals(GameEngine.CONFIG.getConfig(DIFFICULTY.get()))) {
                if(equip[44].split(":")[1].length() >= 3) {
                  registryEquipment.put(equip[44].startsWith("lod") ? (this.idCore(equip[44].split(":")[1])) : (this.id(equip[44].split(":")[1])), dmEquip);
                }
                if(event != null && !equip[44].startsWith("lod") && equip[44].split(":")[1].length() >= 3) {
                  try {
                    event.register(this.id(equip[44].split(":")[1]), dmEquip);
                  } catch(final DuplicateRegistryIdException ignored) {
                  }
                }
              }
            }


            this.print("Registered " + registryEquipment.size() + " equips.");
          } catch(final IOException | CsvException e) {
            throw new RuntimeException(e);
          }
        }
      }
    }

    for(final var entry : registryEquipment.entrySet()) {
      this.print("Equip Registry: " + entry.getKey());
    }
  }

  @EventListener
  public void itemRegistry(final ItemRegistryEvent event) {
    this.print("Item Registry Event");
    //registryItems.clear();

    final File[] baseDirectory = new File("./mods/dragoon_modifier").listFiles();
    if(baseDirectory != null) {
      for(final File file : baseDirectory) {
        if(file.isDirectory() && (!"Ultimate".equals(file.getName()) && !"Damage Tracker".equals(file.getName()) && !"scripts".equals(file.getName()) && !"patches".equals(file.getName()))) {
          try(final FileReader fr = new FileReader(file.getAbsolutePath() + "/scdk-thrown-item-stats.csv", StandardCharsets.UTF_8);
              final CSVReader csv = new CSVReader(fr)) {
            final List<String[]> list = csv.readAll();
            list.removeFirst();

            for(final String[] item : list) {
              if(file.getName().equals(GameEngine.CONFIG.getConfig(DIFFICULTY.get()))) {
                if(item[36].split(":")[1].length() >= 3) {
                  try {
                    final boolean targetAll = (Integer.parseInt(item[0]) & 0x2) != 0;
                    final Item.TargetType targetType = ((Integer.parseInt(item[0]) & 0x4) != 0) ? Item.TargetType.ENEMIES : Item.TargetType.ALLIES;

                    if(item[36].split(":")[1].length() >= 3) {
                      switch(item[30]) {
                        case "AttackItem":
                          DRAMOD_ITEM_REGISTRAR.register(item[36].split(":")[1], () -> new AttackItem(this.getIconFromId(Integer.parseInt(item[20])), Integer.parseInt(item[26]), targetAll, Element.fromFlag(Integer.parseInt(item[1])).get(), Integer.parseInt(item[2])));
                          registryItems.put(item[36].startsWith("lod") ? (this.idCore(item[36].split(":")[1])) : (this.id(item[36].split(":")[1])), new AttackItem(this.getIconFromId(Integer.parseInt(item[20])), Integer.parseInt(item[26]), targetAll, Element.fromFlag(Integer.parseInt(item[1])).get(), Integer.parseInt(item[2])));
                          break;
                        case "AttackBallItem":
                          DRAMOD_ITEM_REGISTRAR.register(item[36].split(":")[1], () -> new AttackBallItem(this.getIconFromId(Integer.parseInt(item[20])), Integer.parseInt(item[26])));
                          registryItems.put(item[36].startsWith("lod") ? (this.idCore(item[36].split(":")[1])) : (this.id(item[36].split(":")[1])), new AttackBallItem(this.getIconFromId(Integer.parseInt(item[20])), Integer.parseInt(item[26])));
                          break;
                        case "HealingPotionItem":
                          DRAMOD_ITEM_REGISTRAR.register(item[36].split(":")[1], () -> new HealingPotionItem(this.getIconFromId(Integer.parseInt(item[20])), Integer.parseInt(item[26]), targetAll, Integer.parseInt(item[22])));
                          registryItems.put(item[36].startsWith("lod") ? (this.idCore(item[36].split(":")[1])) : (this.id(item[36].split(":")[1])), new HealingPotionItem(this.getIconFromId(Integer.parseInt(item[20])), Integer.parseInt(item[26]), targetAll, Integer.parseInt(item[22])));
                          break;
                        case "DepetrifierItem":
                          DRAMOD_ITEM_REGISTRAR.register(item[36].split(":")[1], () -> new DepetrifierItem(this.getIconFromId(Integer.parseInt(item[20])), Integer.parseInt(item[26]), Integer.parseInt(item[21])));
                          registryItems.put(item[36].startsWith("lod") ? (this.idCore(item[36].split(":")[1])) : (this.id(item[36].split(":")[1])), new DepetrifierItem(this.getIconFromId(Integer.parseInt(item[20])), Integer.parseInt(item[26]), Integer.parseInt(item[21])));
                          break;
                        case "MindPurifierItem":
                          DRAMOD_ITEM_REGISTRAR.register(item[36].split(":")[1], () -> new MindPurifierItem(this.getIconFromId(Integer.parseInt(item[20])), Integer.parseInt(item[26]), Integer.parseInt(item[21])));
                          registryItems.put(item[36].startsWith("lod") ? (this.idCore(item[36].split(":")[1])) : (this.id(item[36].split(":")[1])), new MindPurifierItem(this.getIconFromId(Integer.parseInt(item[20])), Integer.parseInt(item[26]), Integer.parseInt(item[21])));
                          break;
                        case "BodyPurifierItem":
                          DRAMOD_ITEM_REGISTRAR.register(item[36].split(":")[1], () -> new BodyPurifierItem(this.getIconFromId(Integer.parseInt(item[20])), Integer.parseInt(item[26]), Integer.parseInt(item[21])));
                          registryItems.put(item[36].startsWith("lod") ? (this.idCore(item[36].split(":")[1])) : (this.id(item[36].split(":")[1])), new BodyPurifierItem(this.getIconFromId(Integer.parseInt(item[20])), Integer.parseInt(item[26]), Integer.parseInt(item[21])));
                          break;
                        case "SpiritPotionItem":
                          DRAMOD_ITEM_REGISTRAR.register(item[36].split(":")[1], () -> new SpiritPotionItem(this.getIconFromId(Integer.parseInt(item[20])), Integer.parseInt(item[26]), targetAll, Integer.parseInt(item[22])));
                          registryItems.put(item[36].startsWith("lod") ? (this.idCore(item[36].split(":")[1])) : (this.id(item[36].split(":")[1])), new SpiritPotionItem(this.getIconFromId(Integer.parseInt(item[20])), Integer.parseInt(item[26]), targetAll, Integer.parseInt(item[22])));
                          break;
                        case "CauseStatusItem":
                          DRAMOD_ITEM_REGISTRAR.register(item[36].split(":")[1], () -> new CauseStatusItem(Integer.parseInt(item[33]), this.getIconFromId(Integer.parseInt(item[20])), Integer.parseInt(item[26]), Integer.parseInt(item[22])));
                          registryItems.put(item[36].startsWith("lod") ? (this.idCore(item[36].split(":")[1])) : (this.id(item[36].split(":")[1])), new CauseStatusItem(Integer.parseInt(item[33]), this.getIconFromId(Integer.parseInt(item[20])), Integer.parseInt(item[26]), Integer.parseInt(item[22])));
                          break;
                        case "TotalVanishingItem":
                          DRAMOD_ITEM_REGISTRAR.register(item[36].split(":")[1], () -> new TotalVanishingItem(this.getIconFromId(Integer.parseInt(item[20])), Integer.parseInt(item[26])));
                          registryItems.put(item[36].startsWith("lod") ? (this.idCore(item[36].split(":")[1])) : (this.id(item[36].split(":")[1])), new TotalVanishingItem(this.getIconFromId(Integer.parseInt(item[20])), Integer.parseInt(item[26])));
                          break;
                        case "AngelsPrayerItem":
                          DRAMOD_ITEM_REGISTRAR.register(item[36].split(":")[1], () -> new AngelsPrayerItem(this.getIconFromId(Integer.parseInt(item[20])), Integer.parseInt(item[26])));
                          registryItems.put(item[36].startsWith("lod") ? (this.idCore(item[36].split(":")[1])) : (this.id(item[36].split(":")[1])), new AngelsPrayerItem(this.getIconFromId(Integer.parseInt(item[20])), Integer.parseInt(item[26])));
                          break;
                        case "CharmPotionItem":
                          DRAMOD_ITEM_REGISTRAR.register(item[36].split(":")[1], () -> new CharmPotionItem(this.getIconFromId(Integer.parseInt(item[20])), Integer.parseInt(item[26])));
                          registryItems.put(item[36].startsWith("lod") ? (this.idCore(item[36].split(":")[1])) : (this.id(item[36].split(":")[1])), new CharmPotionItem(this.getIconFromId(Integer.parseInt(item[20])), Integer.parseInt(item[26])));
                          break;
                        case "PandemoniumItem":
                          DRAMOD_ITEM_REGISTRAR.register(item[36].split(":")[1], () -> new PandemoniumItem(this.getIconFromId(Integer.parseInt(item[20])), Integer.parseInt(item[26])));
                          registryItems.put(item[36].startsWith("lod") ? (this.idCore(item[36].split(":")[1])) : (this.id(item[36].split(":")[1])), new PandemoniumItem(this.getIconFromId(Integer.parseInt(item[20])), Integer.parseInt(item[26])));
                          break;
                        case "RecoveryBallItem":
                          DRAMOD_ITEM_REGISTRAR.register(item[36].split(":")[1], () -> new RecoveryBallItem(this.getIconFromId(Integer.parseInt(item[20])), Integer.parseInt(item[26])));
                          registryItems.put(item[36].startsWith("lod") ? (this.idCore(item[36].split(":")[1])) : (this.id(item[36].split(":")[1])), new RecoveryBallItem(this.getIconFromId(Integer.parseInt(item[20])), Integer.parseInt(item[26])));
                          break;
                        case "ShieldItem":
                          DRAMOD_ITEM_REGISTRAR.register(item[36].split(":")[1], () -> new ShieldItem(this.getIconFromId(Integer.parseInt(item[20])), Integer.parseInt(item[26]), Integer.parseInt(item[34]), Boolean.parseBoolean(item[11]), Boolean.parseBoolean(item[12])));
                          registryItems.put(item[36].startsWith("lod") ? (this.idCore(item[36].split(":")[1])) : (this.id(item[36].split(":")[1])), new ShieldItem(this.getIconFromId(Integer.parseInt(item[20])), Integer.parseInt(item[26]), Integer.parseInt(item[34]), Boolean.parseBoolean(item[11]), Boolean.parseBoolean(item[12])));
                          break;
                        case "SunRhapsodyItem":
                          DRAMOD_ITEM_REGISTRAR.register(item[36].split(":")[1], () -> new SunRhapsodyItem(this.getIconFromId(Integer.parseInt(item[20])), Integer.parseInt(item[26]), targetAll, Integer.parseInt(item[22])));
                          registryItems.put(item[36].startsWith("lod") ? (this.idCore(item[36].split(":")[1])) : (this.id(item[36].split(":")[1])), new SunRhapsodyItem(this.getIconFromId(Integer.parseInt(item[20])), Integer.parseInt(item[26]), targetAll, Integer.parseInt(item[22])));
                          break;
                        case "SmokeBallItem":
                          DRAMOD_ITEM_REGISTRAR.register(item[36].split(":")[1], () -> new SmokeBallItem(this.getIconFromId(Integer.parseInt(item[20])), Integer.parseInt(item[26])));
                          registryItems.put(item[36].startsWith("lod") ? (this.idCore(item[36].split(":")[1])) : (this.id(item[36].split(":")[1])), new SmokeBallItem(this.getIconFromId(Integer.parseInt(item[20])), Integer.parseInt(item[26])));
                          break;
                        case "HealingFogItem":
                          DRAMOD_ITEM_REGISTRAR.register(item[36].split(":")[1], () -> new HealingFogItem(this.getIconFromId(Integer.parseInt(item[20])), Integer.parseInt(item[26]), targetAll, Integer.parseInt(item[22])));
                          registryItems.put(item[36].startsWith("lod") ? (this.idCore(item[36].split(":")[1])) : (this.id(item[36].split(":")[1])), new HealingFogItem(this.getIconFromId(Integer.parseInt(item[20])), Integer.parseInt(item[26]), targetAll, Integer.parseInt(item[22])));
                          break;
                        case "SignetStoneItem":
                          DRAMOD_ITEM_REGISTRAR.register(item[36].split(":")[1], () -> new SignetStoneItem(this.getIconFromId(Integer.parseInt(item[20])), Integer.parseInt(item[26])));
                          registryItems.put(item[36].startsWith("lod") ? (this.idCore(item[36].split(":")[1])) : (this.id(item[36].split(":")[1])), new SignetStoneItem(this.getIconFromId(Integer.parseInt(item[20])), Integer.parseInt(item[26])));
                          break;
                        case "HealingRainItem":
                          DRAMOD_ITEM_REGISTRAR.register(item[36].split(":")[1], () -> new HealingRainItem(this.getIconFromId(Integer.parseInt(item[20])), Integer.parseInt(item[26]), targetAll, Integer.parseInt(item[22])));
                          registryItems.put(item[36].startsWith("lod") ? (this.idCore(item[36].split(":")[1])) : (this.id(item[36].split(":")[1])), new HealingRainItem(this.getIconFromId(Integer.parseInt(item[20])), Integer.parseInt(item[26]), targetAll, Integer.parseInt(item[22])));
                          break;
                        case "MoonSerenadeItem":
                          DRAMOD_ITEM_REGISTRAR.register(item[36].split(":")[1], () -> new MoonSerenadeItem(this.getIconFromId(Integer.parseInt(item[20])), Integer.parseInt(item[26]), targetAll, Integer.parseInt(item[22])));
                          registryItems.put(item[36].startsWith("lod") ? (this.idCore(item[36].split(":")[1])) : (this.id(item[36].split(":")[1])), new MoonSerenadeItem(this.getIconFromId(Integer.parseInt(item[20])), Integer.parseInt(item[26]), targetAll, Integer.parseInt(item[22])));
                          break;
                        case "BuffItem":
                          DRAMOD_ITEM_REGISTRAR.register(item[36].split(":")[1], () -> new BuffItem(Integer.parseInt(item[34]), this.getIconFromId(Integer.parseInt(item[20])), Integer.parseInt(item[26]), targetType, Integer.parseInt(item[3]), Integer.parseInt(item[4]), Integer.parseInt(item[5]), Integer.parseInt(item[6]), Integer.parseInt(item[7]), Integer.parseInt(item[8]), Integer.parseInt(item[9]), Integer.parseInt(item[10]), Boolean.parseBoolean(item[11]), Boolean.parseBoolean(item[12]), Integer.parseInt(item[13]), Integer.parseInt(item[14]), Integer.parseInt(item[15]), Integer.parseInt(item[16]), Integer.parseInt(item[17]), Integer.parseInt(item[18])));
                          registryItems.put(item[36].startsWith("lod") ? (this.idCore(item[36].split(":")[1])) : (this.id(item[36].split(":")[1])), new BuffItem(Integer.parseInt(item[34]), this.getIconFromId(Integer.parseInt(item[20])), Integer.parseInt(item[26]), targetType, Integer.parseInt(item[3]), Integer.parseInt(item[4]), Integer.parseInt(item[5]), Integer.parseInt(item[6]), Integer.parseInt(item[7]), Integer.parseInt(item[8]), Integer.parseInt(item[9]), Integer.parseInt(item[10]), Boolean.parseBoolean(item[11]), Boolean.parseBoolean(item[12]), Integer.parseInt(item[13]), Integer.parseInt(item[14]), Integer.parseInt(item[15]), Integer.parseInt(item[16]), Integer.parseInt(item[17]), Integer.parseInt(item[18])));
                          break;
                        case "SachetItem":
                          DRAMOD_ITEM_REGISTRAR.register(item[36].split(":")[1], () -> new SmokeBallItem(this.getIconFromId(Integer.parseInt(item[20])), Integer.parseInt(item[26])));
                          registryItems.put(item[36].startsWith("lod") ? (this.idCore(item[36].split(":")[1])) : (this.id(item[36].split(":")[1])), new SmokeBallItem(this.getIconFromId(Integer.parseInt(item[20])), Integer.parseInt(item[26])));
                          break;
                        case "HealingBreezeItem":
                          DRAMOD_ITEM_REGISTRAR.register(item[36].split(":")[1], () -> new HealingBreezeItem(this.getIconFromId(Integer.parseInt(item[20])), Integer.parseInt(item[26]), targetAll, Integer.parseInt(item[22])));
                          registryItems.put(item[36].startsWith("lod") ? (this.idCore(item[36].split(":")[1])) : (this.id(item[36].split(":")[1])), new HealingBreezeItem(this.getIconFromId(Integer.parseInt(item[20])), Integer.parseInt(item[26]), targetAll, Integer.parseInt(item[22])));
                          break;
                        case "PsycheBombXItem":
                          DRAMOD_ITEM_REGISTRAR.register(item[36].split(":")[1], () -> new PsycheBombXItem(this.getIconFromId(Integer.parseInt(item[20])), Integer.parseInt(item[26]), targetAll, Element.fromFlag(Integer.parseInt(item[1])).get(), Integer.parseInt(item[22])));
                          registryItems.put(item[36].startsWith("lod") ? (this.idCore(item[36].split(":")[1])) : (this.id(item[36].split(":")[1])), new PsycheBombXItem(this.getIconFromId(Integer.parseInt(item[20])), Integer.parseInt(item[26]), targetAll, Element.fromFlag(Integer.parseInt(item[1])).get(), Integer.parseInt(item[22])));
                          break;
                        case "DraModShieldItem":
                          DRAMOD_ITEM_REGISTRAR.register(item[36].split(":")[1], () -> new DraModShieldItem(this.getIconFromId(Integer.parseInt(item[20])), Integer.parseInt(item[26]), Integer.parseInt(item[34]), Boolean.parseBoolean(item[11]), Boolean.parseBoolean(item[12]), item[31]));
                          registryItems.put(item[36].startsWith("lod") ? (this.idCore(item[36].split(":")[1])) : (this.id(item[36].split(":")[1])), new ShieldItem(this.getIconFromId(Integer.parseInt(item[20])), Integer.parseInt(item[26]), Integer.parseInt(item[34]), Boolean.parseBoolean(item[11]), Boolean.parseBoolean(item[12])));
                          break;
                        case "DraModSpiritPotionItem":
                          DRAMOD_ITEM_REGISTRAR.register(item[36].split(":")[1], () -> new DraModSpiritPotion(this.getIconFromId(Integer.parseInt(item[20])), Integer.parseInt(item[26]), targetAll, Integer.parseInt(item[22]), item[31]));
                          registryItems.put(item[36].startsWith("lod") ? (this.idCore(item[36].split(":")[1])) : (this.id(item[36].split(":")[1])), new DraModSpiritPotion(this.getIconFromId(Integer.parseInt(item[20])), Integer.parseInt(item[26]), targetAll, Integer.parseInt(item[22]), item[31]));
                          break;
                        default:
                          throw new Exception("Invalid item type found: " + item[30]);
                      }
                    }
                  } catch(final DuplicateRegistryIdException ignored) {
                  } catch(final Exception e) {
                    throw new RuntimeException(e);
                  }
                }
              }
            }


            this.print("Registered " + registryItems.size() + " items.");
          } catch(final IOException | CsvException e) {
            throw new RuntimeException(e);
          }
        }
      }
    }

    DRAMOD_ITEM_REGISTRAR.registryEvent(event);

    for(final var entry : registryItems.entrySet()) {
      this.print("Item Registry: " + entry.getKey());
    }
  }

  @EventListener
  public void deffRegistry(final RegisterDeffsEvent event) {
    this.print("Item DEFF Registry Event");

    final File[] baseDirectory = new File("./mods/dragoon_modifier").listFiles();
    if(baseDirectory != null) {
      for(final File file : baseDirectory) {
        if(file.isDirectory() && (!"Ultimate".equals(file.getName()) && !"Damage Tracker".equals(file.getName()) && !"scripts".equals(file.getName()) && !"patches".equals(file.getName()))) {
          try(final FileReader fr = new FileReader(file.getAbsolutePath() + "/scdk-thrown-item-stats.csv", StandardCharsets.UTF_8);
              final CSVReader csv = new CSVReader(fr)) {
            final List<String[]> list = csv.readAll();
            list.removeFirst();

            for(final String[] item : list) {
              if(file.getName().equals(GameEngine.CONFIG.getConfig(DIFFICULTY.get()))) {
                if(item[36].split(":")[1].length() >= 3) {
                  try {
                    if(item[36].startsWith("dragoon_modifier") && item[36].split(":")[1].length() >= 3) {
                      try {
                        final int deff = Integer.parseInt(item[31]);
                        DRAMOD_ITEM_DEFF_REGISTRAR.register(item[36].split(":")[1], () -> new RetailDeffPackage(deff));
                      } catch(final NumberFormatException nfe) {
                        DRAMOD_ITEM_DEFF_REGISTRAR.register(item[36].split(":")[1], () -> new DraModItemDeffPackage(item[31]));
                      }
                    }
                  } catch(final DuplicateRegistryIdException ignored) {
                  } catch(final Exception e) {
                    throw new RuntimeException(e);
                  }
                }
              }
            }
          } catch(final IOException | CsvException e) {
            throw new RuntimeException(e);
          }
        }
      }
    }

    DRAMOD_ITEM_DEFF_REGISTRAR.registryEvent(event);
  }

  @EventListener
  public void spellRegistry(final SpellRegistryEvent event) {
    for(int i = 0; i < spellStatsPlayer.size(); i++) {
      spellStats_800fa0b8_Player[i] = new SpellStats0c(spellStatsPlayer.get(i)[12],
        spellStatsPlayer.get(i)[13],
        Integer.parseInt(spellStatsPlayer.get(i)[0]),
        Integer.parseInt(spellStatsPlayer.get(i)[1]),
        Integer.parseInt(spellStatsPlayer.get(i)[2]),
        Integer.parseInt(spellStatsPlayer.get(i)[3]),
        Integer.parseInt(spellStatsPlayer.get(i)[4]),
        Integer.parseInt(spellStatsPlayer.get(i)[5]),
        Integer.parseInt(spellStatsPlayer.get(i)[6]),
        Integer.parseInt(spellStatsPlayer.get(i)[7]),
        Element.fromFlag(Integer.parseInt(spellStatsPlayer.get(i)[8])),
        Integer.parseInt(spellStatsPlayer.get(i)[9]),
        Integer.parseInt(spellStatsPlayer.get(i)[10]),
        Integer.parseInt(spellStatsPlayer.get(i)[11]));
    }
    for(int i = 0; i < spellStatsMonster.size(); i++) {
      spellStats_800fa0b8_Monster[i] = new SpellStats0c(spellStatsMonster.get(i)[12],
        spellStatsMonster.get(i)[13],
        Integer.parseInt(spellStatsMonster.get(i)[0]),
        Integer.parseInt(spellStatsMonster.get(i)[1]),
        Integer.parseInt(spellStatsMonster.get(i)[2]),
        Integer.parseInt(spellStatsMonster.get(i)[3]),
        Integer.parseInt(spellStatsMonster.get(i)[4]),
        Integer.parseInt(spellStatsMonster.get(i)[5]),
        Integer.parseInt(spellStatsMonster.get(i)[6]),
        Integer.parseInt(spellStatsMonster.get(i)[7]),
        Element.fromFlag(Integer.parseInt(spellStatsMonster.get(i)[8])),
        Integer.parseInt(spellStatsMonster.get(i)[9]),
        Integer.parseInt(spellStatsMonster.get(i)[10]),
        Integer.parseInt(spellStatsMonster.get(i)[11]));
    }
  }

  @EventListener
  public void modMenuRegistry(final RegisterModMenuEvent event) {
    event.register(this.id("burn_stacks"), new ModMenu("Burn Stacks"));
    event.register(this.id("wind_barrier"), new ModMenu("Wind Barrier"));
    event.register(this.id("arrow_swap"), new ModMenu("Arrow Swap"));
    event.register(this.id("siphon_overflow"), new ModMenu("Siphon Overflow"));
    event.register(this.id("static_charge"), new ModMenu("Static Charge"));
    event.register(this.id("magic_state"), new ModMenu("Magic State"));
    event.register(this.id("counter_stance"), new ModMenu("Counter Stance"));
  }

  @EventListener
  public void giveItem(final GiveItemEvent event) {
  }

  @EventListener
  public void takeItem(final GiveEquipmentEvent event) {
  }

  @EventListener
  public void equipStats(final EquipmentStatsEvent event) {
    final Equipment update = this.getEquipFromRegistry(event.equipment.getRegistryId());

    if(update != null) {
      event.flags_00 = update.flags_00;
      event.slot = update.slot;
      event._02 = update._02;
      event.equipableFlags_03 = update.equipableFlags_03;
      event.attackElement_04 = update.attackElement_04;
      event._05 = update._05;
      event.elementalResistance_06 = update.elementalResistance_06;
      event.elementalImmunity_07 = update.elementalImmunity_07;
      event.statusResist_08 = update.statusResist_08;
      event._09 = update._09;
      event.attack1_0a = update.attack1_0a;
      event.mpPerPhysicalHit = update.mpPerPhysicalHit;
      event.spPerPhysicalHit = update.spPerPhysicalHit;
      event.mpPerMagicalHit = update.mpPerMagicalHit;
      event.spPerMagicalHit = update.spPerMagicalHit;
      event.hpMultiplier = update.hpMultiplier;
      event.mpMultiplier = update.mpMultiplier;
      event.spMultiplier = update.spMultiplier;
      event.magicalResistance = update.magicalResistance;
      event.physicalResistance = update.physicalResistance;
      event.magicalImmunity = update.magicalImmunity;
      event.physicalImmunity = update.physicalImmunity;
      event.revive = update.revive;
      event.hpRegen = update.hpRegen;
      event.mpRegen = update.mpRegen;
      event.spRegen = update.spRegen;
      event.escapeBonus = update.escapeBonus;
      event.icon_0e = update.icon_0e.icon;
      event.speed_0f = update.speed_0f;
      event.attack2_10 = update.attack2_10;
      event.magicAttack_11 = update.magicAttack_11;
      event.defence_12 = update.defence_12;
      event.magicDefence_13 = update.magicDefence_13;
      event.attackHit_14 = update.attackHit_14;
      event.magicHit_15 = update.magicHit_15;
      event.attackAvoid_16 = update.attackAvoid_16;
      event.magicAvoid_17 = update.magicAvoid_17;
      event.onHitStatusChance_18 = update.onHitStatusChance_18;
      event._19 = update._19;
      event._1a = update._1a;
      event.onHitStatus_1b = update.onHitStatus_1b;
    } else {
      this.print("NULL EQUIPMENT FOUND DOES NOT EXIST IN DRAMOD REGISTRY: " + event.equipment.getRegistryId());
    }
  }

  @EventListener
  public void equipCanEquip(final EquipmentCanEquipEvent event) {
    final Equipment update = this.getEquipFromRegistry(event.equipment.getRegistryId());
    if(update != null) {
      event.equipableFlags_03 = update.equipableFlags_03;
    }
  }

  @EventListener
  public void iconDisplayEvent(final IconDisplayEvent event) {
    final String[] key = event.key.toString().split(" ");
    if("Equipment".equals(key[0]) || "UltimateWargodEquipment".equals(key[0]) || "WargodCallingEquipment".equals(key[0]) || "DestroyerMaceEquipment".equals(key[0]) || "DetonateArrowEquipment".equals(key[0]) ) {
      event.icon = this.getEquipFromRegistry(key[1]).icon_0e;
    } else {
      event.icon = this.getItemFromRegistry(key[1]).icon;
    }
  }

  @EventListener
  public void shopContentsEvent(final ShopContentsEvent event) {
    final String[] shop = shopItems.get(shopId_8007a3b4);
    event.contents.clear();
    for(int i = 0; i < shop.length - 1; i++) {
      final Equipment equip = this.getEquipFromRegistry(shop[i]);
      final Item item = this.getItemFromRegistry(shop[i]);
      if(equip != null) {
        if(shopId_8007a3b4 == 26) {
          if(shop[i].contains("arrow")) {
            event.contents.add(new ShopScreen.ShopEntry<>(REGISTRIES.equipment.getEntry(shop[i]).get(), 100));
          } else {
            event.contents.add(new ShopScreen.ShopEntry<>(REGISTRIES.equipment.getEntry(shop[i]).get(), equip.getPrice() * 2));
          }
        } else {
          event.contents.add(new ShopScreen.ShopEntry<>(REGISTRIES.equipment.getEntry(shop[i]).get(), equip.getPrice() * 2));
        }
      }
      if(item != null) {
        if(shopId_8007a3b4 == 40) {
          if("lod:healing_rain".equals(shop[i])) {
            event.contents.add(new ShopScreen.ShopEntry<>(REGISTRIES.items.getEntry(shop[i]).get(), 600));
          } else if("lod:total_vanishing".equals(shop[i])) {
            event.contents.add(new ShopScreen.ShopEntry<>(REGISTRIES.items.getEntry(shop[i]).get(), 400));
          } else if("lod:spirit_potion".equals(shop[i])) {
            event.contents.add(new ShopScreen.ShopEntry<>(REGISTRIES.items.getEntry(shop[i]).get(), 200));
          }
        } else if(shopId_8007a3b4 == 41) {
          event.contents.add(new ShopScreen.ShopEntry<>(REGISTRIES.items.getEntry(shop[i]).get(), 800));
        } else {
          event.contents.add(new ShopScreen.ShopEntry<>(REGISTRIES.items.getEntry(shop[i]).get(), item.getPrice() * 2));
        }
      }
    }
  }

  @EventListener
  public void addGoldEvent(final AddGoldEvent event) {
    if(submapCut_80052c30 == 333 && this.isBitSet(28, 26)) {
      event.gold *= 5;
    }
  }


  @EventListener
  public void descriptionEvent(final DescriptionEvent event) {
    final String type = event.translationKey.split("\\.")[1];
    if("lod".equals(event.translationKey.split("\\.")[0])) {
      if("equipment".equals(type)) {
        event.description = this.getEquipDescription(event.translationKey);
      } else {
        event.description = this.getItemDescription(event.translationKey);
      }
    }
  }
  //endregion

  //region Save Data
  @EventListener
  public void saveGame(final SaveGameEvent event) {
    final String saveLocation = event.state.campaign.path.resolve(event.fileName) + ".dragoon_modifier";
    SaveFile.save(draModSave, saveLocation);
    this.print("[SAVE] " + saveLocation);
  }

  @EventListener
  public void loadGame(final LoadGameEvent event) {
    final String saveLocation = event.save.state.campaign.path.resolve(event.save.fileName).toString() + ".dragoon_modifier";
    this.print("[LOAD] " + saveLocation);
    draModSave = SaveFile.load(saveLocation);
  }

  @EventListener
  public void deleteGame(final DeleteSaveEvent event) {
    final String saveLocation = event.campaign.path.resolve(event.fileName).toString() + ".dragoon_modifier";
    this.print("[DELETE] " + saveLocation);
    SaveFile.delete(event.campaign.path, event.fileName);
  }
  //endregion

  //region Inventory Battle
  /*@EventListener public void itemId(final ItemIdEvent event) { // Not needed anymore
  }/*

/*
  @EventListener public void temporaryItemStats(final TemporaryItemStatsEvent event) {
    if(event.bent instanceof PlayerBattleEntity) {
      if(event.attackType == 5) {
        event.itemStats = inventoryItemStats.get(event.itemId);
        selectedItemStats = event.itemStats;
      } else {
        if(selectedItemStats != null) {
          event.itemStats = selectedItemStats;
        }
      }
      print("Temporary Item Stats Percentage %: " + event.itemStats.percentage_09 + "/type: " + event.attackType + " ID: " + event.itemId);
    }
  }
*/

  @EventListener
  public void selectedItem(final SelectedItemEvent event) {
    final String item = event.item.getRegistryId().toString().split(":")[1];
    final int fakeItemId = Integer.parseInt(itemStats.get(event.itemId)[17]);
    event.itemId = (short)fakeItemId;
  }

  @EventListener
  public void spellItemDeff(final SpellItemDeffEvent event) {
  }

  @EventListener
  public void dragoonDeff(final DragoonDeffEvent event) {
    this.print("Dragoon DEFF: " + event.scriptId);
    switch(event.scriptId) {
      case 4205: //Transform?
      case 4235: //Dart Attack
      case 4237: //Lavitz Attack
      case 4241: //Rose Attack
      case 4243: //Haschel Attack
      case 4245: //Albert Attack
      case 4247: //Meru Attack
      case 4249: //Kongol Attack
      case 4253: //Divine Attack
      case 4307: //Burn Out
      case 4311: //Spark Net
      case 4315: //???
      case 4317: //Pellet
      case 4319: //Spear Frost
      case 4321: //Spinning Gale
      case 4325: //Trans Light
      case 4327: //Dark Mist
        new Thread(() -> {
          for(int i = 0; i < 80; i++) {
            try {
              fullScreenEffect_800bb140.type_00 = 0;
              Thread.sleep(125);
            } catch(final InterruptedException e) {
              throw new RuntimeException(e);
            }
          }
        }).start();
        break;
      case 4208: //Blossom Storm
      case 4234: //Rose Storm
        final String difficulty = GameEngine.CONFIG.getConfig(DIFFICULTY.get());
        if("Hell Mode".equals(difficulty) || "Hard + Hell Bosses".equals(difficulty)) {
          this.flowerStormOverride = true;
        }
        break;
    }
  }

  @EventListener
  public void shanaGetArrowCount(final ShanaGetArrowCountEvent event) {
    final String arrow;
    int amount = 0;

    if(event.arrowIndex == 0) {
      arrow = "dragoon_modifier:fire_arrow";
    } else if(event.arrowIndex == 1) {
      arrow = "dragoon_modifier:water_arrow";
    } else if(event.arrowIndex == 2) {
      arrow = "dragoon_modifier:wind_arrow";
    } else if(event.arrowIndex == 3) {
      arrow = "dragoon_modifier:earth_arrow";
    } else if(event.arrowIndex == 4) {
      arrow = "dragoon_modifier:dark_arrow";
    } else if(event.arrowIndex == 5) {
      arrow = "dragoon_modifier:light_arrow";
    } else if(event.arrowIndex == 6) {
      arrow = "dragoon_modifier:thunder_arrow";
    } else {
      arrow = "";
    }

    for(int i = 0; i < gameState_800babc8.equipment_1e8.size(); i++) {
      final Equipment equipment = gameState_800babc8.equipment_1e8.get(i);
      if(arrow.equals(equipment.getRegistryId().toString())) {
        amount++;
      }
    }

    if("dragoon_modifier:elemental_arrow".equals(this.currentPlayer.equipment_11e.get(EquipmentSlot.WEAPON).getRegistryId().toString()) || "dragoon_modifier:elemental_arrow".equals(this.shanaPreviousArrow[this.currentPlayerSlot].toString())) {
      event.arrowCount = 99;
    } else {
      event.arrowCount = amount;
    }
  }

  @EventListener
  public void shanaSwapArrow(final ShanaSwapArrowEvent event) {
    this.shanaArrowCount[this.currentPlayerSlot] = EVENTS.postEvent(new ShanaGetArrowCountEvent(event.arrowIndex)).arrowCount;
    if(this.shanaArrowCount[this.currentPlayerSlot] > 0) {
      final String arrow;

      if(event.arrowIndex == 0) {
        arrow = "dragoon_modifier:fire_arrow";
      } else if(event.arrowIndex == 1) {
        arrow = "dragoon_modifier:water_arrow";
      } else if(event.arrowIndex == 2) {
        arrow = "dragoon_modifier:wind_arrow";
      } else if(event.arrowIndex == 3) {
        arrow = "dragoon_modifier:earth_arrow";
      } else if(event.arrowIndex == 4) {
        arrow = "dragoon_modifier:dark_arrow";
      } else if(event.arrowIndex == 5) {
        arrow = "dragoon_modifier:light_arrow";
      } else if(event.arrowIndex == 6) {
        arrow = "dragoon_modifier:thunder_arrow";
      } else {
        this.shanaArrowCount[this.currentPlayerSlot] = 0;
        this.shanaMaxArrowCount[this.currentPlayerSlot] = 0;
        this.currentPlayer.equipment_11e.put(EquipmentSlot.WEAPON, REGISTRIES.equipment.getEntry(this.shanaPreviousArrow[this.currentPlayerSlot]).get());
        return;
      }

      addAchievement(49);
      this.shanaMaxArrowCount[this.currentPlayerSlot] = this.shanaArrowCount[this.currentPlayerSlot];
      this.currentPlayer.equipment_11e.put(EquipmentSlot.WEAPON, this.getEquipFromRegistry(arrow));
    } else {
      this.shanaArrowCount[this.currentPlayerSlot] = 0;
      this.shanaMaxArrowCount[this.currentPlayerSlot] = 0;
      this.currentPlayer.equipment_11e.put(EquipmentSlot.WEAPON, REGISTRIES.equipment.getEntry(this.shanaPreviousArrow[this.currentPlayerSlot]).get());
    }
  }

  public void removeArrow() {
    if("dragoon_modifier:elemental_arrow".equals(this.currentPlayer.equipment_11e.get(EquipmentSlot.WEAPON).getRegistryId().toString()) || "dragoon_modifier:elemental_arrow".equals(this.shanaPreviousArrow[this.currentPlayerSlot].toString())) {
      return;
    }
    for(int i = 0; i < gameState_800babc8.equipment_1e8.size(); i++) {
      final Equipment equipment = gameState_800babc8.equipment_1e8.get(i);
      if(equipment.getRegistryId().equals(this.currentPlayer.equipment_11e.get(EquipmentSlot.WEAPON).getRegistryId())) {
        gameState_800babc8.equipment_1e8.remove(i);
        this.shanaArrowCount[this.currentPlayerSlot]--;
        if(this.shanaArrowCount[this.currentPlayerSlot] == 0) {
          this.shanaMaxArrowCount[this.currentPlayerSlot] = 0;
          this.currentPlayer.equipment_11e.put(EquipmentSlot.WEAPON, REGISTRIES.equipment.getEntry(this.shanaPreviousArrow[this.currentPlayerSlot]).get());
        }
        return;
      }
    }
  }
  //endregion

  //region Battle Monster
  @EventListener
  public void monsterStats(final MonsterStatsEvent event) {
    if(ultimateBattle) {
      final int ovrId = event.enemyId;
      for(int x = 0; x < 86; x++) {
        if(ovrId == Integer.parseInt(ultimateData.get(x)[0])) {
          event.hp = Integer.parseInt(ultimateData.get(x)[1]);
          event.maxHp = Integer.parseInt(ultimateData.get(x)[1]);
          event.attack = Integer.parseInt(ultimateData.get(x)[3]);
          event.magicAttack = Integer.parseInt(ultimateData.get(x)[4]);
          event.speed = Integer.parseInt(ultimateData.get(x)[5]);
          event.defence = Integer.parseInt(ultimateData.get(x)[6]);
          event.magicDefence = Integer.parseInt(ultimateData.get(x)[7]);
          event.attackAvoid = Integer.parseInt(ultimateData.get(x)[8]);
          event.magicAvoid = Integer.parseInt(ultimateData.get(x)[9]);
          event.specialEffectFlag = Integer.parseInt(ultimateData.get(x)[10]);
          event.elementFlag = Element.fromFlag(Integer.parseInt(ultimateData.get(x)[12])).get();
          event.elementalImmunityFlag.clear();
          if(Integer.parseInt(ultimateData.get(x)[13]) > 0) {
            event.elementalImmunityFlag.add(Element.fromFlag(Integer.parseInt(ultimateData.get(x)[13])).get());
          }
          event.statusResistFlag = Integer.parseInt(ultimateData.get(x)[14]);
          break;
        }
      }
    } else {
      final int ovrId = event.enemyId;
      event.hp = Integer.parseInt(monsterStats.get(ovrId)[1]);
      event.maxHp = Integer.parseInt(monsterStats.get(ovrId)[1]);
      event.attack = Integer.parseInt(monsterStats.get(ovrId)[3]);
      event.magicAttack = Integer.parseInt(monsterStats.get(ovrId)[4]);
      event.speed = Integer.parseInt(monsterStats.get(ovrId)[5]);
      event.defence = Integer.parseInt(monsterStats.get(ovrId)[6]);
      event.magicDefence = Integer.parseInt(monsterStats.get(ovrId)[7]);
      event.attackAvoid = Integer.parseInt(monsterStats.get(ovrId)[8]);
      event.magicAvoid = Integer.parseInt(monsterStats.get(ovrId)[9]);
      event.specialEffectFlag = Integer.parseInt(monsterStats.get(ovrId)[10]);
      event.elementFlag = Element.fromFlag(Integer.parseInt(monsterStats.get(ovrId)[12])).get();
      event.elementalImmunityFlag.clear();
      if(Integer.parseInt(monsterStats.get(ovrId)[13]) > 0) {
        event.elementalImmunityFlag.add(Element.fromFlag(Integer.parseInt(monsterStats.get(ovrId)[13])).get());
      }
      event.statusResistFlag = Integer.parseInt(monsterStats.get(ovrId)[14]);
    }
  }

  @EventListener
  public void enemyRewards(final EnemyRewardsEvent event) {
    final int enemyId = event.enemyId;
    final String difficulty = GameEngine.CONFIG.getConfig(DIFFICULTY.get());

    event.clear();
    if(ultimateBattle) {
      event.xp = 0;
      event.gold = 0;

      if(draModSave.ultimateBossStage + 1 == CONFIG.getConfig(ULTIMATE_BOSS.get())) {
        for(int i = 0; i < 86; i++) {
          if(enemyId == Integer.parseInt(ultimateData.get(i)[0])) {
            final String item = ultimateData.get(i)[27];

            event.xp = Integer.parseInt(ultimateData.get(i)[25]);
            event.gold = Integer.parseInt(ultimateData.get(i)[26]);
            if(!item.startsWith("lod:_") && !item.startsWith("lod:None")) {
              try {
                event.add(new CombatantStruct1a8.ItemDrop(Integer.parseInt(ultimateData.get(i)[28]), REGISTRIES.equipment.getEntry(item).get()));
              } catch(final Exception ignored) {
              }

              try {
                event.add(new CombatantStruct1a8.ItemDrop(Integer.parseInt(ultimateData.get(i)[28]), REGISTRIES.items.getEntry(item).get()));
              } catch(final Exception ignored) {
              }
            }
            break;
          }
        }
      }
    } else {
      final String item = monstersRewardsStats.get(enemyId)[3];
      final int exp = Integer.parseInt(monstersRewardsStats.get(enemyId)[0]);
      event.xp = exp;
      if("Hell Mode".equals(difficulty) || "Hard + Hell Bosses".equals(difficulty)) {
        if(ArrayUtils.contains(this.bossEncounters, encounterId_800bb0f8)) {
          int activePartyMembers = 0;
          for(int x = 0; x < 3; x++) {
            if(gameState_800babc8.charIds_88[x] != -1) {
              activePartyMembers++;
            }
          }

          if(activePartyMembers == 3) {
            event.xp = (int)(exp * 1.5); //TODO: increase or decrease this
          }
        }
      }
      event.gold = Integer.parseInt(monstersRewardsStats.get(enemyId)[1]);
      if(!item.startsWith("lod:_") && !item.startsWith("lod:None")) {
        try {
          event.add(new CombatantStruct1a8.ItemDrop(Integer.parseInt(monstersRewardsStats.get(enemyId)[2]), REGISTRIES.equipment.getEntry(item).get()));
        } catch(final Exception ignored) {
        }

        try {
          event.add(new CombatantStruct1a8.ItemDrop(Integer.parseInt(monstersRewardsStats.get(enemyId)[2]), REGISTRIES.items.getEntry(item).get()));
        } catch(final Exception ignored) {
        }
      }

      if("Hard Mode".equals(difficulty) || "US + Hard Bosses".equals(difficulty) || "Hell Mode".equals(difficulty) || "Hard + Hell Bosses".equals(difficulty)) {
        if(submapCut_80052c30 >= 114 && submapCut_80052c30 <= 123) { //Volcano Villude
          event.add(new CombatantStruct1a8.ItemDrop(12, REGISTRIES.equipment.getEntry("dragoon_modifier:fire_arrow").get()));
        } else if((submapCut_80052c30 >= 301 && submapCut_80052c30 <= 305)) { //Undersea Cavern
          event.add(new CombatantStruct1a8.ItemDrop(12, REGISTRIES.equipment.getEntry("dragoon_modifier:water_arrow").get()));
        } else if((submapCut_80052c30 >= 433 && submapCut_80052c30 <= 438)) { //Kashua Glacier
          event.add(new CombatantStruct1a8.ItemDrop(12, REGISTRIES.equipment.getEntry("dragoon_modifier:water_arrow").get()));
        } else if((submapCut_80052c30 >= 130 && submapCut_80052c30 <= 139)) { //Nest of Dragon
          event.add(new CombatantStruct1a8.ItemDrop(12, REGISTRIES.equipment.getEntry("dragoon_modifier:wind_arrow").get()));
        } else if((submapCut_80052c30 >= 339 && submapCut_80052c30 <= 346)) { //Evergreen Forest
          event.add(new CombatantStruct1a8.ItemDrop(12, REGISTRIES.equipment.getEntry("dragoon_modifier:wind_arrow").get()));
        } else if((submapCut_80052c30 >= 261 && submapCut_80052c30 <= 268)) { //Home of Giganto
          event.add(new CombatantStruct1a8.ItemDrop(12, REGISTRIES.equipment.getEntry("dragoon_modifier:earth_arrow").get()));
        } else if((submapCut_80052c30 >= 393 && submapCut_80052c30 <= 405)) { //Kadessa
          event.add(new CombatantStruct1a8.ItemDrop(12, REGISTRIES.equipment.getEntry("dragoon_modifier:earth_arrow").get()));
        } else if((submapCut_80052c30 >= 261 && submapCut_80052c30 <= 296)) { //Phantom Ship
          event.add(new CombatantStruct1a8.ItemDrop(12, REGISTRIES.equipment.getEntry("dragoon_modifier:dark_arrow").get()));
        } else if((submapCut_80052c30 >= 539 && submapCut_80052c30 <= 553)) { //Death City Mayfil
          event.add(new CombatantStruct1a8.ItemDrop(12, REGISTRIES.equipment.getEntry("dragoon_modifier:dark_arrow").get()));
        } else if((submapCut_80052c30 >= 153 && submapCut_80052c30 <= 166)) { //Shrine of Shirley
          event.add(new CombatantStruct1a8.ItemDrop(12, REGISTRIES.equipment.getEntry("dragoon_modifier:light_arrow").get()));
        } else if((submapCut_80052c30 >= 597 && submapCut_80052c30 <= 622)) { //Moon
          event.add(new CombatantStruct1a8.ItemDrop(12, REGISTRIES.equipment.getEntry("dragoon_modifier:light_arrow").get()));
        } else if((submapCut_80052c30 >= 477 && submapCut_80052c30 <= 502)) { //Vellweb
          event.add(new CombatantStruct1a8.ItemDrop(12, REGISTRIES.equipment.getEntry("dragoon_modifier:thunder_arrow").get()));
        } else if((submapCut_80052c30 >= 441 && submapCut_80052c30 <= 452)) { //Flanvel Tower
          event.add(new CombatantStruct1a8.ItemDrop(12, REGISTRIES.equipment.getEntry("dragoon_modifier:thunder_arrow").get()));
        }

        if("Hell Mode".equals(difficulty) || "Hard + Hell Bosses".equals(difficulty)) {
          if(encounterId_800bb0f8 == 403 && enemyId == 301) {
            event.add(new CombatantStruct1a8.ItemDrop(100, REGISTRIES.items.getEntry("dragoon_modifier:weak_shield").get()));
            event.add(new CombatantStruct1a8.ItemDrop(100, REGISTRIES.items.getEntry("dragoon_modifier:weak_shield").get()));
            event.add(new CombatantStruct1a8.ItemDrop(100, REGISTRIES.items.getEntry("dragoon_modifier:super_spirit_pot").get()));
          }
        }
      }

      if(faustBattle && event.enemyId == 344) {
        event.clear();
        event.xp = 30000;
        event.gold = 250;

        for(int i = 0; i < 9; i++) {
          if(gameState_800babc8.charData_32c[i].level_12 == 60) {
            draModSave.faustDefeated = 39;
          }
        }

        if(draModSave.faustDefeated == 39) {
          event.add(new CombatantStruct1a8.ItemDrop(100, REGISTRIES.equipment.getEntry("lod:armor_of_legend").get()));
          event.add(new CombatantStruct1a8.ItemDrop(100, REGISTRIES.equipment.getEntry("lod:legend_casque").get()));
          draModSave.faustDefeated = 999;
        }

        if(draModSave.faustDefeated > 999) {
          draModSave.faustDefeated = 999;
        }
      }
    }
  }
  //endregion

  //region Battle
  @EventListener
  public void battleStarted(final BattleStartedEvent event) {
    /*if(battleHudOverlay == null) {
      this.burnStacksGfx[0] = Texture.png(Path.of("mods", "dragoon_modifier", "burnstacks-25.png"));
      this.burnStacksGfx[1] = Texture.png(Path.of("mods", "dragoon_modifier", "burnstacks-50.png"));
      this.burnStacksGfx[2] = Texture.png(Path.of("mods", "dragoon_modifier", "burnstacks-75.png"));
      this.burnStacksGfx[3] = Texture.png(Path.of("mods", "dragoon_modifier", "burnstacks-100.png"));
      this.windMarksGfx[0] = Texture.png(Path.of("mods", "dragoon_modifier", "windmark-0.png"));
      this.windMarksGfx[1] = Texture.png(Path.of("mods", "dragoon_modifier", "windmark-1.png"));
      this.windMarksGfx[2] = Texture.png(Path.of("mods", "dragoon_modifier", "windmark-2.png"));
      this.windMarksGfx[3] = Texture.png(Path.of("mods", "dragoon_modifier", "windmark-3.png"));
      this.thunderChargesGfx[0] = Texture.png(Path.of("mods", "dragoon_modifier", "thundercharge-0.png"));
      this.thunderChargesGfx[1] = Texture.png(Path.of("mods", "dragoon_modifier", "thundercharge-1.png"));
      this.thunderChargesGfx[2] = Texture.png(Path.of("mods", "dragoon_modifier", "thundercharge-2.png"));
      this.thunderChargesGfx[3] = Texture.png(Path.of("mods", "dragoon_modifier", "thundercharge-3.png"));
      this.thunderChargesGfx[4] = Texture.png(Path.of("mods", "dragoon_modifier", "thundercharge-4.png"));
      this.thunderChargesGfx[5] = Texture.png(Path.of("mods", "dragoon_modifier", "thundercharge-5.png"));
      this.thunderChargesGfx[6] = Texture.png(Path.of("mods", "dragoon_modifier", "thundercharge-6.png"));
      this.thunderChargesGfx[7] = Texture.png(Path.of("mods", "dragoon_modifier", "thundercharge-7.png"));
      this.thunderChargesGfx[8] = Texture.png(Path.of("mods", "dragoon_modifier", "thundercharge-8.png"));
      this.thunderChargesGfx[9] = Texture.png(Path.of("mods", "dragoon_modifier", "thundercharge-9.png"));
      this.thunderChargesGfx[10] = Texture.png(Path.of("mods", "dragoon_modifier", "thundercharge-10.png"));
      this.battleHudOverlay = new QuadBuilder("HUD Profile Overlay")
        .bpp(Bpp.BITS_24)
        .posSize(28.0f, 36.0f)
        .uvSize(1.0f, 1.0f)
        .build();
      print("GFX Loaded." + (battleHudOverlay == null));
    }*/

    if(faustBattle) {
      final ScriptState<? extends BattleEntity27c> state = battleState_8006e398.allBents_e0c[0];
      final BattleEntity27c bobj = state.innerStruct_00;
      final VitalsStat hp = bobj.stats.getStat(HP_STAT.get());
      hp.setCurrent(25600);
      hp.setMaxRaw(25600);
      bobj.attack_34 = 125;
      bobj.magicAttack_36 = 125;
      bobj.defence_38 = 75;
      bobj.magicDefence_3a = 200;
    }

    this.armorOfLegendTurns = 0;
    this.legendCasqueTurns = 0;
    this.dragonBlockStaff = false;
    this.flowerStormOverride = false;
    this.damageTrackerPrinted = false;
    this.shanaDeffArrow = false;
    this.haschelInParty = false;
    Arrays.fill(this.burnStacks, 0);
    Arrays.fill(this.previousBurnStacks, 0);
    Arrays.fill(this.enrageMode, 0);
    Arrays.fill(this.enrageModeProtection, 0);
    Arrays.fill(this.elementalBombTurns, 0);
    Arrays.fill(this.windMark, 0);
    Arrays.fill(this.thunderCharge, 0);
    Arrays.fill(this.burnStackMode, false);
    Arrays.fill(this.burnAdded, false);
    Arrays.fill(this.elementalAttack, false);
    Arrays.fill(this.shanaStarChildrenHeal, false);
    Arrays.fill(this.shanaRapidFireContinue, false);
    Arrays.fill(this.shanaRapidFire, false);
    Arrays.fill(this.shanaRapidFireCount, 0);
    Arrays.fill(this.meruBoost, false);
    Arrays.fill(this.bonusItemSP, false);
    Arrays.fill(this.ouroboros, false);
    Arrays.fill(this.meruBoostTurns, 0);
    Arrays.fill(this.meruMaxHpSave, 0);
    Arrays.fill(this.meruMDFSave, 0);
    Arrays.fill(this.damageTracker[0], 0);
    Arrays.fill(this.damageTracker[1], 0);
    Arrays.fill(this.damageTracker[2], 0);
    Arrays.fill(this.ringOfElements, 0);
    Arrays.fill(this.ringOfElementsElement, null);
    Arrays.fill(this.protectionShield, 0);
    Arrays.fill(this.spiritBottle, false);
    Arrays.fill(this.speedBottle, false);
    Arrays.fill(this.healingBottle, false);
    Arrays.fill(this.sunBottle, false);
    Arrays.fill(this.windBarrier, false);
    Arrays.fill(this.roseSiphon, 0);
    Arrays.fill(this.roseSiphonActivated, false);
    Arrays.fill(this.meruIceShield, 0);
    Arrays.fill(this.meruWinglyMagic, false);
    Arrays.fill(this.kongolCounterStance, false);
    Arrays.fill(this.kongolCounterStanceTurns, 0);
    Arrays.fill(this.shanaArrow, null);
    Arrays.fill(this.shanaArrowCount, -1);
    Arrays.fill(this.shanaMaxArrowCount, -1);
    Arrays.fill(this.shanaPreviousArrow, null);
    this.damageTrackerLog.clear();

    for(int i = 0; i < battleState_8006e398.getAllBentCount(); i++) {
      final ScriptState<? extends BattleEntity27c> state = battleState_8006e398.allBents_e0c[i];
      final BattleEntity27c bobj = state.innerStruct_00;
      if(bobj instanceof final PlayerBattleEntity player) {
        if(!this.haschelInParty && player.charId_272 == 4) {
          this.haschelInParty = true;
        }
        if(player.charId_272 == 3) {
          this.roseSiphonMax = player.getStat(BattleEntityStat.MAX_HP);
        } else if(player.charId_272 == 6) {
          this.meruIceShieldMax = player.getStat(BattleEntityStat.MAX_HP);
        }
      }
    }

    final String difficulty = GameEngine.CONFIG.getConfig(DIFFICULTY.get());
    boolean windDragoonPresent = false;
    boolean whiteSilverDragoonPresent = false;

    if("Hard Mode".equals(difficulty) || "US + Hard Bosses".equals(difficulty) || "Hell Mode".equals(difficulty) || "Hard + Hell Bosses".equals(difficulty)) {
      for(int i = 0; i < battleState_8006e398.getAllBentCount(); i++) {
        final ScriptState<? extends BattleEntity27c> state = battleState_8006e398.allBents_e0c[i];
        final BattleEntity27c bobj = state.innerStruct_00;
        if(bobj instanceof final PlayerBattleEntity player) {
          if(windDragoonPresent && (player.charId_272 == 1 || player.charId_272 == 5)) {
            final int newHP = (int)Math.round(player.getStat(BattleEntityStat.MAX_HP) * 0.7);
            final int newAT = (int)Math.round(player.magicAttack_36 * 0.5);
            final int newMAT = (int)Math.round(player.attack_34 * 0.5);
            final int newDF = player.magicDefence_3a;
            final int newMDF = player.defence_38;
            final VitalsStat hp = player.stats.getStat(LodMod.HP_STAT.get());

            hp.setMaxRaw(newHP);
            player.attack_34 = newAT;
            player.magicAttack_36 = newMAT;
            player.defence_38 = newDF;
            player.magicDefence_3a = newMDF;
          }

          if(whiteSilverDragoonPresent && (player.charId_272 == 2 || player.charId_272 == 8)) {
            final int newHP = (int)Math.round(player.getStat(BattleEntityStat.MAX_HP) * 0.7);
            final int newAT = (int)Math.round(player.magicAttack_36 * 0.5);
            final int newMAT = (int)Math.round(player.attack_34 * 0.5);
            final int newDF = player.magicDefence_3a;
            final int newMDF = player.defence_38;
            final VitalsStat hp = player.stats.getStat(LodMod.HP_STAT.get());

            hp.setMaxRaw(newHP);
            player.attack_34 = newAT;
            player.magicAttack_36 = newMAT;
            player.defence_38 = newDF;
            player.magicDefence_3a = newMDF;
          }

          if(player.charId_272 == 1 || player.charId_272 == 5) {
            windDragoonPresent = true;
          }

          if(player.charId_272 == 2 || player.charId_272 == 8) {
            whiteSilverDragoonPresent = true;
          }

          if(player.charId_272 == 0) {
            this.burnStacksMax[player.charSlot_276] = player.dlevel_06 == 0 ? 0 : player.dlevel_06 == 1 ? 3 : player.dlevel_06 == 2 ? 6 : player.dlevel_06 == 3 ? 9 : player.dlevel_06 == 7 ? 15 : 12;
          }

          player.equipmentElementalImmunity_22.clear();

          if(player.charId_272 == 1 || player.charId_272 == 5 || player.charId_272 == 7) { //SPD reduction
            final ActiveStatsa0 stats = Scus94491BpeSegment_800b.stats_800be5f8[player.charId_272];
            double speedReduction = player.charId_272 == 7 ? 2d : 1.5d;
            player.stats.getStat(SPEED_STAT.get()).setRaw(stats.bodySpeed_69 + (int)Math.round(stats.equipmentSpeed_86 / speedReduction));
          }

          if("lod:phantom_shield".equals(player.equipment_11e.get(EquipmentSlot.ACCESSORY).getRegistryId().toString())) {
            player.defence_38 = (int)Math.round(player.defence_38 * 0.6d);
            player.magicDefence_3a = (int)Math.round(player.magicDefence_3a * 0.6d);
          }

          if("lod:dragon_shield".equals(player.equipment_11e.get(EquipmentSlot.ACCESSORY).getRegistryId().toString())) {
            player.defence_38 = (int)Math.round(player.defence_38 * 0.6d);
          }

          if("lod:angel_scarf".equals(player.equipment_11e.get(EquipmentSlot.ACCESSORY).getRegistryId().toString())) {
            player.magicDefence_3a = (int)Math.round(player.magicDefence_3a * 0.6d);
          }

          if("lod:holy_ankh".equals(player.equipment_11e.get(EquipmentSlot.ACCESSORY).getRegistryId().toString()) && "lod:angel_robe".equals(player.equipment_11e.get(EquipmentSlot.ARMOUR).getRegistryId().toString())) {
            player.revive_13a -= 20;
          }

          int crystalItems = 0;
          if("dragoon_modifier:crystal_armor".equals(player.equipment_11e.get(EquipmentSlot.ARMOUR).getRegistryId().toString())) {
            crystalItems++;
          }

          if("dragoon_modifier:crystal_hat".equals(player.equipment_11e.get(EquipmentSlot.HELMET).getRegistryId().toString())) {
            crystalItems++;
          }

          if("dragoon_modifier:crystal_boots".equals(player.equipment_11e.get(EquipmentSlot.BOOTS).getRegistryId().toString())) {
            crystalItems++;
          }

          if("dragoon_modifier:crystal_ring".equals(player.equipment_11e.get(EquipmentSlot.ACCESSORY).getRegistryId().toString())) {
            crystalItems++;
          }

          if(crystalItems > 3) {
            player.attack_34 += 60;
            player.magicAttack_36 += 60;
            player.defence_38 += 60;
            player.magicDefence_3a += 60;
            player.attackHit_3c += 60;
            player.magicHit_3e += 60;
            player.attackAvoid_40 += 12;
            player.magicAvoid_42 += 12;
            if(player.charId_272 != 7) {
              player.stats.getStat(SPEED_STAT.get()).setRaw(player.stats.getStat(SPEED_STAT.get()).get() + 12);
            } else {
              player.stats.getStat(SPEED_STAT.get()).setRaw(player.stats.getStat(SPEED_STAT.get()).get() + 6);
            }
            player.stats.getStat(HP_STAT.get()).setMaxRaw((int)Math.round(player.stats.getStat(HP_STAT.get()).getMax() * 1.3d));
            player.stats.getStat(MP_STAT.get()).setMaxRaw((int)Math.round(player.stats.getStat(MP_STAT.get()).getMax() * 1.3d));
            player.hpRegen_134 = 10;
            player.mpRegen_136 = 10;
            player.spRegen_138 = 100;
          } else if(crystalItems > 2) {
            player.attack_34 += 30;
            player.magicAttack_36 += 30;
            player.defence_38 += 30;
            player.magicDefence_3a += 30;
            player.attackHit_3c += 30;
            player.magicHit_3e += 30;
            player.attackAvoid_40 += 6;
            player.magicAvoid_42 += 6;
            if(player.charId_272 != 7) {
              player.stats.getStat(SPEED_STAT.get()).setRaw(player.stats.getStat(SPEED_STAT.get()).get() + 6);
            } else {
              player.stats.getStat(SPEED_STAT.get()).setRaw(player.stats.getStat(SPEED_STAT.get()).get() + 3);
            }
            player.hpRegen_134 = 4;
            player.mpRegen_136 = 4;
            player.spRegen_138 = 40;
          } else if(crystalItems > 1) {
            player.attack_34 += 5;
            player.magicAttack_36 += 5;
            player.defence_38 += 5;
            player.magicDefence_3a += 5;
            player.attackHit_3c += 5;
            player.magicHit_3e += 5;
            player.attackAvoid_40 += 1;
            player.magicAvoid_42 += 1;
            if(player.charId_272 != 7) {
              player.stats.getStat(SPEED_STAT.get()).setRaw(player.stats.getStat(SPEED_STAT.get()).get() + 1);
            }
          }

          if("dragoon_modifier:ring_of_reversal".equals(player.equipment_11e.get(EquipmentSlot.ACCESSORY).getRegistryId().toString())) {
            final int df = player.defence_38;
            final int mdf = player.magicDefence_3a;
            player.magicDefence_3a = df;
            player.defence_38 = mdf;
            if(player.defence_38 > player.magicDefence_3a) {
              player.stats.getStat(HP_STAT.get()).setMaxRaw((int)Math.round(player.stats.getStat(HP_STAT.get()).getMax() * 1.5d));
            } else {
              player.stats.getStat(HP_STAT.get()).setMaxRaw((int)Math.round(player.stats.getStat(HP_STAT.get()).getMax() / 1.5d));
              player.spMultiplier_128 += 35;
            }
          }

          if("dragoon_modifier:the_one_ring".equals(player.equipment_11e.get(EquipmentSlot.ACCESSORY).getRegistryId().toString())) {
            player.stats.getStat(HP_STAT.get()).setCurrent(1);
            player.stats.getStat(HP_STAT.get()).setMaxRaw(1);
            player.attackAvoid_40 = 80;
            player.magicAvoid_42 = 80;
          }

          if("dragoon_modifier:divine_dg_armor".equals(player.equipment_11e.get(EquipmentSlot.ARMOUR).getRegistryId().toString())) {
            player.spPerPhysicalHit_12a += 10;
            player.spPerMagicalHit_12e += 10;
            stats_800be5f8[player.charId_272].equipmentSpPerPhysicalHit_4e += 10;
            stats_800be5f8[player.charId_272].equipmentSpPerMagicalHit_52 += 10;
          }

          if("dragoon_modifier:halo_of_balance".equals(player.equipment_11e.get(EquipmentSlot.HELMET).getRegistryId().toString())) {
            player.stats.getStat(HP_STAT.get()).setMaxRaw((int)Math.round(player.stats.getStat(HP_STAT.get()).getMax() * 1.3d));
            player.stats.getStat(MP_STAT.get()).setMaxRaw((int)Math.round(player.stats.getStat(MP_STAT.get()).getMax() * 1.3d));
          }

          if("dragoon_modifier:firebrand".equals(player.equipment_11e.get(EquipmentSlot.WEAPON).getRegistryId().toString())) {
            player.equipmentAttackElements_1c.clear();
            player.equipmentAttackElements_1c.add(DIVINE_ELEMENT.get());
            player.equipmentAttackElements_1c.add(FIRE_ELEMENT.get());
          }

          if("dragoon_modifier:super_spirit_ring".equals(player.equipment_11e.get(EquipmentSlot.ACCESSORY).getRegistryId().toString())) {
            player.spMultiplier_128 = -100;
          }

          if("Hell Mode".equals(difficulty) || "Hard + Hell Bosses".equals(difficulty)) {
            final int flowerStormTurns = GameEngine.CONFIG.getConfig(FLOWER_STORM.get());
            int flowerStorm = -1;
            if(player.charId_272 == 1) {
              flowerStorm = 7;
            } else if(player.charId_272 == 5) {
              flowerStorm = 26;
            }
            if(flowerStorm > 0) {
              spellStats_800fa0b8_Player[flowerStorm] = new SpellStats0c(spellStatsPlayer.get(flowerStorm)[12],
                spellStatsPlayer.get(flowerStorm)[13].substring(0, spellStatsPlayer.get(flowerStorm)[13].length() - 1) + GameEngine.CONFIG.getConfig(FLOWER_STORM.get()),
                Integer.parseInt(spellStatsPlayer.get(flowerStorm)[0]),
                Integer.parseInt(spellStatsPlayer.get(flowerStorm)[1]),
                Integer.parseInt(spellStatsPlayer.get(flowerStorm)[2]),
                Integer.parseInt(spellStatsPlayer.get(flowerStorm)[3]),
                Integer.parseInt(spellStatsPlayer.get(flowerStorm)[4]),
                Integer.parseInt(spellStatsPlayer.get(flowerStorm)[5]),
                GameEngine.CONFIG.getConfig(FLOWER_STORM.get()) * 20,
                Integer.parseInt(spellStatsPlayer.get(flowerStorm)[7]),
                Element.fromFlag(Integer.parseInt(spellStatsPlayer.get(flowerStorm)[8])),
                Integer.parseInt(spellStatsPlayer.get(flowerStorm)[9]),
                Integer.parseInt(spellStatsPlayer.get(flowerStorm)[10]),
                Integer.parseInt(spellStatsPlayer.get(flowerStorm)[11]));
            }
          }

          if("dragoon_modifier:protection_shield".equals(player.equipment_11e.get(EquipmentSlot.ACCESSORY).getRegistryId().toString())) {
            this.protectionShield[player.charSlot_276] += player.stats.getStat(HP_STAT.get()).getMax() / 2;
          }

          if("dragoon_modifier:protection_shoes".equals(player.equipment_11e.get(EquipmentSlot.BOOTS).getRegistryId().toString())) {
            this.protectionShield[player.charSlot_276] += player.stats.getStat(HP_STAT.get()).getMax() / 4;
          }

          if("dragoon_modifier:protection_helmet".equals(player.equipment_11e.get(EquipmentSlot.HELMET).getRegistryId().toString())) {
            this.protectionShield[player.charSlot_276] += player.stats.getStat(HP_STAT.get()).getMax() / 4;
          }

          if("dragoon_modifier:protection_armor".equals(player.equipment_11e.get(EquipmentSlot.ARMOUR).getRegistryId().toString())) {
            this.protectionShield[player.charSlot_276] += player.stats.getStat(HP_STAT.get()).getMax();
          }

          if("dragoon_modifier:spirit_bottle".equals(player.equipment_11e.get(EquipmentSlot.ACCESSORY).getRegistryId().toString())) {
            this.spiritBottle[player.charSlot_276] = true;
          }

          if("dragoon_modifier:speed_bottle".equals(player.equipment_11e.get(EquipmentSlot.ACCESSORY).getRegistryId().toString())) {
            this.speedBottle[player.charSlot_276] = true;
          }

          if("dragoon_modifier:healing_bottle".equals(player.equipment_11e.get(EquipmentSlot.ACCESSORY).getRegistryId().toString())) {
            this.healingBottle[player.charSlot_276] = true;
          }

          if("dragoon_modifier:sun_bottle".equals(player.equipment_11e.get(EquipmentSlot.ACCESSORY).getRegistryId().toString())) {
            this.sunBottle[player.charSlot_276] = true;
          }

          if("dragoon_modifier:fake_solitary_ring".equals(player.equipment_11e.get(EquipmentSlot.ACCESSORY).getRegistryId().toString())) {
            int activePartyMembers = 0;
            for(int x = 0; x < 3; x++) {
              if(gameState_800babc8.charIds_88[x] != -1) {
                activePartyMembers++;
              }
            }

            if(activePartyMembers == 1) {
              player.hpRegen_134 += 5;
              player.mpRegen_136 += 5;
              player.spRegen_138 += 10;
              player.spPerMagicalHit_12e += 4;
              player.spPerPhysicalHit_12a += 4;
              player.mpPerMagicalHit_130 += 4;
              player.mpPerPhysicalHit_12c += 4;
              stats_800be5f8[player.charId_272].equipmentSpPerPhysicalHit_4e += 4;
              stats_800be5f8[player.charId_272].equipmentSpPerMagicalHit_52 += 4;
              stats_800be5f8[player.charId_272].equipmentMpPerPhysicalHit_50 += 4;
              stats_800be5f8[player.charId_272].equipmentMpPerMagicalHit_54 += 4;
              player.stats.getStat(HP_STAT.get()).setMaxRaw((int)Math.round(player.stats.getStat(HP_STAT.get()).getMax() * 1.1));
              player.stats.getStat(MP_STAT.get()).setMaxRaw((int)Math.round(player.stats.getStat(MP_STAT.get()).getMax() * 1.1));
              player.stats.getStat(SPEED_STAT.get()).setRaw((int)Math.round(player.stats.getStat(SPEED_STAT.get()).get() * 1.1));
            }
          }

          if("dragoon_modifier:solitary_ring".equals(player.equipment_11e.get(EquipmentSlot.ACCESSORY).getRegistryId().toString())) {
            int activePartyMembers = 0;
            for(int x = 0; x < 3; x++) {
              if(gameState_800babc8.charIds_88[x] != -1) {
                activePartyMembers++;
              }
            }

            if(activePartyMembers == 1) {
              player.attack_34 = (int)Math.round(player.attack_34 * 1.2);
              player.magicAttack_36 = (int)Math.round(player.magicAttack_36 * 1.2);
              player.defence_38 = (int)Math.round(player.defence_38 * 1.2);
              player.magicDefence_3a = (int)Math.round(player.magicDefence_3a * 1.2);
              player.hpRegen_134 += 10;
              player.mpRegen_136 += 10;
              player.spRegen_138 += 20;
              player.spPerMagicalHit_12e += 8;
              player.spPerPhysicalHit_12a += 8;
              player.mpPerMagicalHit_130 += 8;
              player.mpPerPhysicalHit_12c += 8;
              stats_800be5f8[player.charId_272].equipmentSpPerPhysicalHit_4e += 8;
              stats_800be5f8[player.charId_272].equipmentSpPerMagicalHit_52 += 8;
              stats_800be5f8[player.charId_272].equipmentMpPerPhysicalHit_50 += 8;
              stats_800be5f8[player.charId_272].equipmentMpPerMagicalHit_54 += 8;
              player.stats.getStat(HP_STAT.get()).setMaxRaw((int)Math.round(player.stats.getStat(HP_STAT.get()).getMax() * 1.3333));
              player.stats.getStat(MP_STAT.get()).setMaxRaw((int)Math.round(player.stats.getStat(MP_STAT.get()).getMax() * 1.3333));
              player.stats.getStat(SPEED_STAT.get()).setRaw((int)Math.round(player.stats.getStat(SPEED_STAT.get()).get() * 1.3333));
            }
          }

          if("dragoon_modifier:fake_plague_boots".equals(player.equipment_11e.get(EquipmentSlot.BOOTS).getRegistryId().toString())) {
            if(player.onHitStatusChance_44 > 0) {
              player.onHitStatusChance_44 += 20;
            }
          }

          if("dragoon_modifier:plague_boots".equals(player.equipment_11e.get(EquipmentSlot.BOOTS).getRegistryId().toString())) {
            if(player.onHitStatusChance_44 > 0) {
              player.onHitStatusChance_44 += 40;
            }
          }

          if(player.charId_272 == 2 || player.charId_272 == 8) {
            if(player.dlevel_06 >= 2) {
              final int moonLight;
              final int gatesOfHeaven;
              if(player.charId_272 == 2) {
                moonLight = 11;
                gatesOfHeaven = 12;
              } else {
                moonLight = 66;
                gatesOfHeaven = 67;
              }

              spellStats_800fa0b8_Player[moonLight] = new SpellStats0c(spellStatsPlayer.get(moonLight)[12],
                spellStatsPlayer.get(moonLight)[13],
                Integer.parseInt(spellStatsPlayer.get(moonLight)[0]),
                Integer.parseInt(spellStatsPlayer.get(moonLight)[1]),
                Integer.parseInt(spellStatsPlayer.get(moonLight)[2]),
                Integer.parseInt(spellStatsPlayer.get(moonLight)[3]),
                Integer.parseInt(spellStatsPlayer.get(moonLight)[4]),
                Integer.parseInt(spellStatsPlayer.get(moonLight)[5]),
                20,
                Integer.parseInt(spellStatsPlayer.get(moonLight)[7]),
                Element.fromFlag(Integer.parseInt(spellStatsPlayer.get(moonLight)[8])),
                Integer.parseInt(spellStatsPlayer.get(moonLight)[9]),
                Integer.parseInt(spellStatsPlayer.get(moonLight)[10]),
                Integer.parseInt(spellStatsPlayer.get(moonLight)[11]));
              if(player.dlevel_06 >= 4 && player.stats.getStat(MP_STAT.get()).getMax() >= 120) {
                spellStats_800fa0b8_Player[gatesOfHeaven] = new SpellStats0c(spellStatsPlayer.get(gatesOfHeaven)[12],
                  spellStatsPlayer.get(gatesOfHeaven)[13],
                  Integer.parseInt(spellStatsPlayer.get(gatesOfHeaven)[0]),
                  Integer.parseInt(spellStatsPlayer.get(gatesOfHeaven)[1]),
                  Integer.parseInt(spellStatsPlayer.get(gatesOfHeaven)[2]),
                  Integer.parseInt(spellStatsPlayer.get(gatesOfHeaven)[3]),
                  Integer.parseInt(spellStatsPlayer.get(gatesOfHeaven)[4]),
                  Integer.parseInt(spellStatsPlayer.get(gatesOfHeaven)[5]),
                  player.stats.getStat(MP_STAT.get()).getMax() / 3,
                  Integer.parseInt(spellStatsPlayer.get(gatesOfHeaven)[7]),
                  Element.fromFlag(Integer.parseInt(spellStatsPlayer.get(gatesOfHeaven)[8])),
                  Integer.parseInt(spellStatsPlayer.get(gatesOfHeaven)[9]),
                  Integer.parseInt(spellStatsPlayer.get(gatesOfHeaven)[10]),
                  Integer.parseInt(spellStatsPlayer.get(gatesOfHeaven)[11]));
              }
            }

            this.shanaPreviousArrow[player.charSlot_276] = player.equipment_11e.get(EquipmentSlot.WEAPON).getRegistryId();
          }
        }
      }
    }

    if("Hell Mode".equals(difficulty) || "Hard + Hell Bosses".equals(difficulty)) {
      GameEngine.EVENTS.postEvent(new HellModeAdjustmentEvent());
    }

    for(int i = 0; i < battleState_8006e398.getAllBentCount(); i++) {
      final ScriptState<? extends BattleEntity27c> state = battleState_8006e398.allBents_e0c[i];
      final BattleEntity27c bobj = state.innerStruct_00;
      if(bobj instanceof final PlayerBattleEntity player) {
        this.damageTrackerEquips[player.charSlot_276][0] = I18n.translate(player.equipment_11e.get(EquipmentSlot.WEAPON));
        this.damageTrackerEquips[player.charSlot_276][1] = I18n.translate(player.equipment_11e.get(EquipmentSlot.HELMET));
        this.damageTrackerEquips[player.charSlot_276][2] = I18n.translate(player.equipment_11e.get(EquipmentSlot.ARMOUR));
        this.damageTrackerEquips[player.charSlot_276][3] = I18n.translate(player.equipment_11e.get(EquipmentSlot.BOOTS));
        this.damageTrackerEquips[player.charSlot_276][4] = I18n.translate(player.equipment_11e.get(EquipmentSlot.ACCESSORY));
      }
    }

    if(ultimateBattle) {
      for(int i = 0; i < battleState_8006e398.getAllBentCount(); i++) {
        final ScriptState<? extends BattleEntity27c> state = battleState_8006e398.allBents_e0c[i];
        final BattleEntity27c bobj = state.innerStruct_00;
        if(bobj instanceof final PlayerBattleEntity player) {
          this.ultimatePenality[player.charSlot_276][0] = 1;
          this.ultimatePenality[player.charSlot_276][1] = 1;

          if(player.level_04 > ultimateLevelCap) {
            final int levelDifference = player.level_04 - this.ultimateLevelCap;

            if(this.ultimateLevelCap == 30) {
              if(Math.round(levelDifference / 10d) == 1) { //Level 40
                this.ultimatePenality[player.charSlot_276][0] = 1.5;
                this.ultimatePenality[player.charSlot_276][1] = 1.26;
              } else if(Math.round(levelDifference / 10d) == 2) { //Level 50
                this.ultimatePenality[player.charSlot_276][0] = 2.6;
                this.ultimatePenality[player.charSlot_276][1] = 1.53;
              } else if(Math.round(levelDifference / 10d) == 3) { //Level 60
                this.ultimatePenality[player.charSlot_276][0] = 3.4;
                this.ultimatePenality[player.charSlot_276][1] = 1.89;
              }
            } else if(this.ultimateLevelCap == 40) {
              if(Math.round(levelDifference / 10d) == 1) { //Level 50
                this.ultimatePenality[player.charSlot_276][0] = 1.7;
                this.ultimatePenality[player.charSlot_276][1] = 1.17;
              } else if(Math.round(levelDifference / 10d) == 2) { //Level 60
                this.ultimatePenality[player.charSlot_276][0] = 2.2;
                this.ultimatePenality[player.charSlot_276][1] = 1.35;
              }
            } else if(this.ultimateLevelCap == 50) {
              if(Math.round(levelDifference / 10d) == 1) { //Level 60
                this.ultimatePenality[player.charSlot_276][0] = 1.3;
                this.ultimatePenality[player.charSlot_276][1] = 1.08;
              }
            }
          }

          if(this.ultimatePenality[player.charSlot_276][0] > 1) {
            final int currentMax = player.stats.getStat(HP_STAT.get()).getMaxRaw();
            player.stats.getStat(HP_STAT.get()).setMaxRaw(Math.round(Math.round((double)currentMax / this.ultimatePenality[player.charSlot_276][0])));
          }

          this.ultimateZeroSPStart(player);
          this.ultimateStatusChanceResist(player);
        }
      }
    }

    for(int i = 0; i < battleState_8006e398.getMonsterCount(); i++) {
      final MonsterBattleEntity monster = battleState_8006e398.monsterBents_e50[i].innerStruct_00;
      final int hp = monster.stats.getStat(HP_STAT.get()).getCurrent();
      this.damageTrackerPreviousHP[monster.charSlot_276] = hp;
    }

    if("Hell Mode".equals(difficulty) || "Hard + Hell Bosses".equals(difficulty)) {
      GameEngine.EVENTS.postEvent(new HellModeAdjustmentEvent());

      if(encounterId_800bb0f8 == 416 || encounterId_800bb0f8 == 394 || encounterId_800bb0f8 == 443) {//TODO add this to hard mode
        battleNotification(0, "Dragon Block Staff is 20% reduction to Dragoons.");
      }
    }
  }

  @EventListener
  public void battleEntityTurn(final BattleEntityTurnEvent<?> event) {
    selectedItemId = -1;
    this.isItemArrow = false;
    this.attacked.clear();

    final String difficulty = GameEngine.CONFIG.getConfig(DIFFICULTY.get());
    this.updateItemMagicDamage();
    this.updateEnrageMode(null);

    if(event.bent instanceof final PlayerBattleEntity player) {
      this.damageTrackerLog.add(charNames[player.charId_272] + " Turn Started");
      this.currentPlayerSlot = player.charSlot_276;
      this.currentPlayerId = player.charId_272;
      this.currentPlayer = player;

      if("Hard Mode".equals(difficulty) || "US + Hard Bosses".equals(difficulty) || "Hell Mode".equals(difficulty) || "Hard + Hell Bosses".equals(difficulty)) {
        for(final RegistryId entry : GameEngine.REGISTRIES.modMenu) {
          if(entry.toString().contains("dragoon_modifier")) {
            final RegistryDelegate<ModMenu> menuEntry = GameEngine.REGISTRIES.modMenu.getEntry(entry);
            final String text = entry.toString();
            if(player.charId_272 == 0) {
              if(text.contains("burn_stacks")) {
                menuEntry.get().enable();
              } else {
                menuEntry.get().disable();
              }
            } else if(player.charId_272 == 1 || player.charId_272 == 5) {
              if(text.contains("wind_barrier")) {
                menuEntry.get().enable();
              } else {
                menuEntry.get().disable();
              }
            } else if(player.charId_272 == 2 || player.charId_272 == 8) {
              if(text.contains("arrow_swap")) {
                menuEntry.get().enable();
              } else {
                menuEntry.get().disable();
              }
            } else if(player.charId_272 == 3) {
              if(text.contains("siphon_overflow")) {
                menuEntry.get().enable();
              } else {
                menuEntry.get().disable();
              }
            } else if(player.charId_272 == 4) {
              if(text.contains("static_charge")) {
                menuEntry.get().enable();
              } else {
                menuEntry.get().disable();
              }
            } else if(player.charId_272 == 6) {
              if(text.contains("magic_state")) {
                menuEntry.get().enable();
              } else {
                menuEntry.get().disable();
              }
            } else if(player.charId_272 == 7) {
              if(text.contains("counter_stance")) {
                menuEntry.get().enable();
              } else {
                menuEntry.get().disable();
              }
            }
          }
        }

        if(player.isDragoon()) {
          spGained_800bc950[player.charSlot_276] += 100;
        }

        if(this.bonusItemSP[player.charSlot_276]) {
          this.bonusItemSP[player.charSlot_276] = false;
          if(player.isDragoon()) {
            player.stats.getStat(SP_STAT.get()).setCurrent(player.stats.getStat(SP_STAT.get()).getCurrent() + 50);
            final int newSP = player.stats.getStat(SP_STAT.get()).getCurrent();
            if(player.charSlot_276 == 0) {
              battleState_8006e398.dragoonTurnsRemaining_294[0] = newSP / 100;
            } else if(player.charSlot_276 == 1) {
              battleState_8006e398.dragoonTurnsRemaining_294[1] = newSP / 100;
            } else if(player.charSlot_276 == 2) {
              battleState_8006e398.dragoonTurnsRemaining_294[2] = newSP / 100;
            }
          }
        }

        if("dragoon_modifier:spirit_eater".equals(player.equipment_11e.get(EquipmentSlot.WEAPON).getRegistryId().toString())) {
          final int sp = player.stats.getStat(SP_STAT.get()).getCurrent();
          if(!player.isDragoon() && sp != player.stats.getStat(SP_STAT.get()).getMax()) {
            player.stats.getStat(SP_STAT.get()).setCurrent(sp - 20);
            spGained_800bc950[player.charSlot_276] += 20;
          }
        }

        if(this.ouroboros[player.charSlot_276] && !player.isDragoon()) { //Ouroboros
          player.stats.getStat(SPEED_STAT.get()).addMod(LodMod.id("speed_down"), LodMod.UNARY_STAT_MOD_TYPE.get().make(new UnaryStatModConfig().percent(-50).turns(3)));
          this.ouroboros[player.charSlot_276] = false;
        }

        if("dragoon_modifier:ring_of_elements".equals(player.equipment_11e.get(EquipmentSlot.ACCESSORY).getRegistryId().toString())) {
          if(((Battle)currentEngineState_8004dd04).dragoonSpaceElement_800c6b64 == player.element) {
            this.ringOfElements[player.charSlot_276]++;
            this.ringOfElementsElement[player.charSlot_276] = ((Battle)currentEngineState_8004dd04).dragoonSpaceElement_800c6b64;
          } else {
            if(player.element == FIRE_ELEMENT.get() && ((Battle)currentEngineState_8004dd04).dragoonSpaceElement_800c6b64 == DIVINE_ELEMENT.get()) {
              this.ringOfElements[player.charSlot_276]++;
              this.ringOfElementsElement[player.charSlot_276] = ((Battle)currentEngineState_8004dd04).dragoonSpaceElement_800c6b64;
            }

            if(((Battle)currentEngineState_8004dd04).dragoonSpaceElement_800c6b64 == null) {
              this.ringOfElements[player.charSlot_276]--;
            }
          }
        }

        if(this.windBarrier[player.charSlot_276]) {
          final int currentMP = player.getStat(BattleEntityStat.CURRENT_MP);
          if(currentMP >= 10) {
            player.setStat(BattleEntityStat.CURRENT_MP, currentMP - 10);
          } else if(currentMP == 0) {
            this.windBarrier[player.charSlot_276] = false;
          }
        }

        if(this.meruWinglyMagic[player.charSlot_276]) {
          final int currentMP = player.getStat(BattleEntityStat.CURRENT_MP);
          if(currentMP >= 10) {
            player.setStat(BattleEntityStat.CURRENT_MP, currentMP - 10);
          } else {
            this.meruWinglyMagic[player.charSlot_276] = false;
          }
        }

        if(("Hell Mode".equals(difficulty) || "Hard + Hell Bosses".equals(difficulty)) && this.flowerStormOverride) {
          this.flowerStormOverride = false;
          for(int i = 0; i < battleState_8006e398.getAllBentCount(); i++) {
            final ScriptState<? extends BattleEntity27c> state = battleState_8006e398.allBents_e0c[i];
            final BattleEntity27c bobj = state.innerStruct_00;
            player.powerDefenceTurns_b9 = GameEngine.CONFIG.getConfig(FLOWER_STORM.get());
            player.powerMagicDefenceTurns_bb = GameEngine.CONFIG.getConfig(FLOWER_STORM.get());
          }
        }

        if(player.item_d4 != null) {
          player.item_d4 = null;
        }

        if("lod:armor_of_legend".equals(player.equipment_11e.get(EquipmentSlot.ARMOUR).getRegistryId().toString())) {
          this.armorOfLegendTurns += 1;
          if(this.armorOfLegendTurns <= 40) {
            player.defence_38 += 1;
          }
        }

        if("lod:legend_casque".equals(player.equipment_11e.get(EquipmentSlot.HELMET).getRegistryId().toString())) {
          this.legendCasqueTurns += 1;
          if(this.legendCasqueTurns <= 40) {
            player.magicDefence_3a += 1;
          }
        }

        if(player.charId_272 == 0) {
          this.burnAdded[player.charSlot_276] = false;

          if(this.burnStackMode[player.charSlot_276]) {
            this.burnStacks[player.charSlot_276] = 0;
            this.previousBurnStacks[player.charSlot_276] = 0;
            this.burnStackMode[player.charSlot_276] = false;
          }
        }

        if(player.charId_272 == 2 || player.charId_272 == 8) {
          this.isItemArrow = false;
          if(this.shanaStarChildrenHeal[player.charSlot_276] && !player.isDragoon()) {
            this.shanaStarChildrenHeal[player.charSlot_276] = false;
            this.displayNumbers(6 + player.charSlot_276, player.stats.getStat(HP_STAT.get()).getMax() - player.stats.getStat(HP_STAT.get()).getCurrent(), 1, 1000);
            player.stats.getStat(HP_STAT.get()).setCurrent(player.stats.getStat(HP_STAT.get()).getMax());
          }

          if(this.shanaRapidFire[player.charSlot_276]) {
            this.shanaRapidFire[player.charSlot_276] = false;
            player.dragoonAttack_ac = this.dragonBlockStaff ? 365 * 8 : 365;
          }
        }

        if(this.elementalAttack[player.charSlot_276]) {
          player.element = this.previousElement[player.charSlot_276];
          this.elementalAttack[player.charSlot_276] = false;
          if(player.charId_272 == 2 || player.charId_272 == 8) {
            player.dragoonAttack_ac = this.dragonBlockStaff ? 365 * 8 : 365;
          }
        }

        if(player.charId_272 == 6 && this.meruBoost[player.charSlot_276]) {
          this.meruBoostTurns[player.charSlot_276] -= 1;
          if(this.meruBoostTurns[player.charSlot_276] == 0) {
            this.meruBoost[player.charSlot_276] = false;
            player.stats.getStat(HP_STAT.get()).setMaxRaw(this.meruMaxHpSave[player.charSlot_276]);
            player.magicDefence_3a = this.meruMDFSave[player.charSlot_276];
          }
        }

        if(this.spiritBottle[player.charSlot_276]) {
          final int sp = player.stats.getStat(SP_STAT.get()).getCurrent();
          player.stats.getStat(SP_STAT.get()).setCurrent(sp + 80);
          spGained_800bc950[player.charSlot_276] += 80;
          this.spiritBottle[player.charSlot_276] = false;
          this.displayNumbers(6 + player.charSlot_276, 80, 11, 1000);
        }

        if(this.speedBottle[player.charSlot_276]) {
          player.turnValue_4c += 255;
          this.speedBottle[player.charSlot_276] = false;
        }

        if(this.healingBottle[player.charSlot_276]) {
          final int hp = player.stats.getStat(HP_STAT.get()).getCurrent();
          if(hp <= player.stats.getStat(HP_STAT.get()).getMax() * 0.25) {
            this.displayNumbers(6 + player.charSlot_276, (int)Math.round(player.stats.getStat(HP_STAT.get()).getMax() * 0.7), 3, 1000);
            player.stats.getStat(HP_STAT.get()).setCurrent(hp + (int)Math.round(player.stats.getStat(HP_STAT.get()).getMax() * 0.7));
            this.healingBottle[player.charSlot_276] = false;
          }
        }

        if(this.sunBottle[player.charSlot_276]) {
          final int mp = player.stats.getStat(MP_STAT.get()).getCurrent();
          if(mp <= player.stats.getStat(MP_STAT.get()).getMax() - 20) {
            player.stats.getStat(MP_STAT.get()).setCurrent(mp + 20);
            this.sunBottle[player.charSlot_276] = false;
            this.displayNumbers(6 + player.charSlot_276, 20, 10, 1000);
          }
        }

        if("Hell Mode".equals(difficulty) || "Hard + Hell Bosses".equals(difficulty)) {
          if(player.isDragoon()) {
            spGained_800bc950[player.charSlot_276] += 50;
          }
        }

        if(player.charId_272 == 6) {
          this.meruIceShieldMax = player.getStat(BattleEntityStat.MAX_HP);
          if("Hard Mode".equals(difficulty) || "US + Hard Bosses".equals(difficulty) || "Hell Mode".equals(difficulty) || "Hard + Hell Bosses".equals(difficulty)) {
            if("dragoon_modifier:magic_hammer".equals(player.equipment_11e.get(EquipmentSlot.WEAPON).getRegistryId().toString()) && player.isDragoon()) {
              if(!(CONFIG.getConfig(CoreMod.DRAGOON_EQUIP_EFFECTS_CONFIG.get()))) {
                player.setStat(BattleEntityStat.CURRENT_MP, player.getStat(BattleEntityStat.CURRENT_MP) + 20);
              }
            }
          }
        }

        if(player.charId_272 == 7) {
          if(this.kongolCounterStance[player.charSlot_276] && this.kongolCounterStanceTurns[player.charSlot_276] == 0) {
            this.kongolCounterStance[player.charSlot_276] = false;
            this.currentPlayer.stats.getStat(LodMod.SPEED_STAT.get()).setRaw(this.kongolCounterSpeed);
          }

          if(this.kongolCounterStance[player.charSlot_276]) {
            this.kongolCounterStanceTurns[this.currentPlayerSlot] -= 1;
            player.setStat(BattleEntityStat.CURRENT_HP, (int)(player.getStat(BattleEntityStat.CURRENT_HP) + player.getStat(BattleEntityStat.MAX_HP) * 0.02));
          }
        }
      } else {
        for(final RegistryId entry : GameEngine.REGISTRIES.modMenu) {
          if("dragoon_modifier".contains(entry.toString())) {
            final RegistryDelegate<ModMenu> menuEntry = GameEngine.REGISTRIES.modMenu.getEntry(entry);
            menuEntry.get().disable();
          }
        }
      }
    }


    if(event.bent instanceof final MonsterBattleEntity monster) {
      if(this.elementalBombTurns[monster.charSlot_276] > 0) {
        this.elementalBombTurns[monster.charSlot_276] -= 1;

        if(this.elementalBombTurns[monster.charSlot_276] == 0) {
          monster.element = this.elementalBombPreviousElement[monster.charSlot_276];
        }
      }
    }
  }

  @EventListener
  public void attack(final AttackEvent event) {
    if(this.attacked.contains(event.defender)) {
      return;
    } else {
      this.attacked.add(event.defender);
    }

    if(event.attacker instanceof PlayerBattleEntity) {
      if(event.attackType == AttackType.DRAGOON_MAGIC_STATUS_ITEMS) {
        if(Integer.parseInt(spellStatsPlayer.get(event.attacker.spellId_4e)[4]) == 0) {
          switch(Integer.parseInt(spellStatsPlayer.get(event.attacker.spellId_4e)[3])) {
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
              event.damage *= (int)(Integer.parseInt(spellStatsPlayer.get(event.attacker.spellId_4e)[3]) / 100d);
          }
        }
      }
    }

    final String difficulty = CONFIG.getConfig(DIFFICULTY.get());

    if("Hard Mode".equals(difficulty) || "US + Hard Bosses".equals(difficulty) || "Hell Mode".equals(difficulty) || "Hard + Hell Bosses".equals(difficulty)) {
            /*
                ATTACKING PLAYER
             */
      if(event.attacker instanceof final PlayerBattleEntity player) {
        if(player.isDragoon() && event.attackType.isPhysical()) {
          if(player.element == ((Battle)currentEngineState_8004dd04).dragoonSpaceElement_800c6b64) { //Dragoon Space physical boost
            if(player.charId_272 == 7) {
              event.damage *= 1.2;
            } else {
              event.damage *= 1.5;
            }
          } else {
            if(player.element == Element.fromFlag(0x80).get() && ((Battle)currentEngineState_8004dd04).dragoonSpaceElement_800c6b64 == Element.fromFlag(0x8).get()) { //Divine Dart special physical boost
              if("dragoon_modifier:firebrand".equals(player.equipment_11e.get(EquipmentSlot.WEAPON).getRegistryId().toString())) {
                event.damage *= 1.1; //TODO this doesn't seem right
              } else {
                event.damage *= 1.5;
              }
            }
          }
        }

        if(event.defender instanceof MonsterBattleEntity) {
          final int level = player.level_04;
          if(event.attackType.isPhysical() && (player.charId_272 == 2 || player.charId_272 == 8)) { //Shana AT Boost
            double boost = 1;
            if("lod:detonate_arrow".equals(player.equipment_11e.get(EquipmentSlot.WEAPON).getRegistryId().toString())) {
              boost = 1.4;
            } else if(level >= 28) {
              boost = 2.15;
            } else if(level >= 20) {
              boost = 1.9;
            } else if(level >= 10) {
              boost = 1.6;
            }
            event.damage = (int)Math.round(event.damage * boost);
          }

          if(this.isItemArrow) {
            if("dragoon_modifier:elemental_arrow".equals(this.shanaPreviousArrow[player.charSlot_276].toString())) {
              if(!player.isDragoon()) {
                event.damage = (int)Math.round(event.damage * 0.8d);
              }
            } else {
              if(!player.isDragoon()) {
                event.damage = (int)Math.round(event.damage * 0.4d);
              } else {
                event.damage = (int)Math.round(event.damage * 0.55d);
              }
            }
            this.removeArrow();
          }
        }

        if(player.spellId_4e >= 84 && player.spellId_4e <= 94) { //Item Spells In Dragoon
          if(player.charId_272 != 4) {
            this.bonusItemSP[player.charSlot_276] = true;
          }

          if(player.charId_272 == 3) {
            event.damage *= 1.7;
          } else if(player.charId_272 == 1 || player.charId_272 == 5) {
            event.damage *= 1.5;
          } else if(player.charId_272 == 7) {
            event.damage *= 2.2;
          }

          if(this.dragonBlockStaff) {
            event.damage /= 8;
          }
        }

        if(player.charId_272 == 2 || player.charId_272 == 8) {
          if(player.spellId_4e == 10 || player.spellId_4e == 65) { //Star Children full heal on exit
            this.shanaStarChildrenHeal[player.charSlot_276] = true;
          }
        }

        if(event.defender instanceof final MonsterBattleEntity monster) {
          if(this.windMark[event.defender.charSlot_276] > 0) { //Wind mark turn value reduction
            monster.turnValue_4c = Math.max(0, monster.turnValue_4c - 30);
            this.windMark[event.defender.charSlot_276] -= 1;
          }
        }

        if(event.attacker.charId_272 == 0) {
          if(this.burnStackMode[event.attacker.charSlot_276]) {
            if(this.burnStacks[event.attacker.charSlot_276] == this.burnStacksMax[event.attacker.charSlot_276]) {
              if(player.spellId_4e == 0) {
                event.damage *= (1 + (this.burnStacks[event.attacker.charSlot_276] * this.dmgPerBurn)) * (Integer.parseInt(spellStatsPlayer.get(2)[3]) / Integer.parseInt(spellStatsPlayer.get(0)[3])) * 1.5;
              } else if(player.spellId_4e == 1) {
                event.damage *= (1 + (this.burnStacks[event.attacker.charSlot_276] * this.dmgPerBurn)) * Integer.parseInt(spellStatsPlayer.get(3)[3]) / Integer.parseInt(spellStatsPlayer.get(1)[3]);
              } else if(player.spellId_4e == 2) {
                event.damage *= (1 + (this.burnStacks[event.attacker.charSlot_276] * this.dmgPerBurn)) * 1.5;
              } else {
                event.damage *= 1 + (this.burnStacks[event.attacker.charSlot_276] * this.dmgPerBurn);
              }
            } else {
              event.damage *= 1 + (this.burnStacks[event.attacker.charSlot_276] * this.dmgPerBurn);
            }
          } else {
            if(event.attackType == AttackType.DRAGOON_MAGIC_STATUS_ITEMS && !this.burnAdded[event.attacker.charSlot_276]) {
              if(player.spellId_4e == 0 || player.spellId_4e == 84) {
                this.addBurnStacks(player, this.burnStackFlameshot);
              } else if(player.spellId_4e == 1) {
                this.addBurnStacks(player, this.burnStackExplosion);
              } else if(player.spellId_4e == 2) {
                this.addBurnStacks(player, this.burnStackFinalBurst);
              } else if(player.spellId_4e == 3) {
                this.addBurnStacks(player, this.burnStackRedEye);
              }
              this.burnAdded[event.attacker.charSlot_276] = true;
            } else if(event.attackType == AttackType.PHYSICAL && player.isDragoon()) {
              this.addBurnStacks(player, this.burnStackAddition);
              this.burnAdded[event.attacker.charSlot_276] = true;
            }
          }
        }

        if(event.attacker.charId_272 == 3) {
          if(player.isDragoon()) {
            if(player.spellId_4e == 15) {
              for(int i = 0; i < battleState_8006e398.getAllBentCount(); i++) {
                final ScriptState<? extends BattleEntity27c> state = battleState_8006e398.allBents_e0c[i];
                final BattleEntity27c bobj = state.innerStruct_00;
                if(bobj instanceof PlayerBattleEntity) {
                  final int playerHealedHP = bobj.stats.getStat(HP_STAT.get()).getCurrent();
                  final int roseMaxHP = player.stats.getStat(HP_STAT.get()).getMax();
                  if(playerHealedHP > 0) {
                    final int heal = (int)Math.round(roseMaxHP * player.dlevel_06 * 0.0425d);
                    bobj.stats.getStat(HP_STAT.get()).setCurrent((int)Math.min(bobj.stats.getStat(HP_STAT.get()).getMax(), (playerHealedHP + heal)));
                    this.displayNumbers(6 + player.charSlot_276, heal, 3, 1500);
                  }
                }
              }
            } else if(player.spellId_4e == 19) {
              final int heal = (int)Math.round(event.damage * 0.1d);
              player.stats.getStat(HP_STAT.get()).setCurrent((int)Math.min(player.stats.getStat(HP_STAT.get()).getMax(), player.stats.getStat(HP_STAT.get()).getCurrent() + heal));
              this.displayNumbers(6 + player.charSlot_276, heal, 3, 1500);
            }

            if(event.damage > 0 && !this.roseSiphonActivated[player.charSlot_276]) {
              this.roseSiphon[player.charSlot_276] = (int)Math.min(this.roseSiphonMax, Math.min(this.roseSiphonMax * 0.2 + this.roseSiphon[player.charSlot_276], this.roseSiphon[player.charSlot_276] + event.damage));
              this.displayNumbers(6 + player.charSlot_276, this.roseSiphon[player.charSlot_276], 3, 1500);
              addAchievement(50);
            }
          }

          if(this.roseSiphonActivated[player.charSlot_276]) {
            event.damage += this.roseSiphonMax;
            this.roseSiphon[player.charSlot_276] = 0;
            this.roseSiphonActivated[player.charSlot_276] = false;
          }
        }

        if(event.attacker.charId_272 == 1 || event.attacker.charId_272 == 5) {
          if(this.windMark[event.defender.charSlot_276] == 0 && event.attackType.isMagical() && player.isDragoon()) { //Add wind marks
            if(player.spellId_4e == 5 || player.spellId_4e == 91) {
              this.windMark[event.defender.charSlot_276] = 1;
              this.displayNumbers(11 + event.defender.charSlot_276, 1, 11, 1500);
              addAchievement(48);
            } else if(player.spellId_4e == 6 || player.spellId_4e == 17) {
              this.windMark[event.defender.charSlot_276] = 2;
              this.displayNumbers(11 + event.defender.charSlot_276, 2, 11, 1500);
              addAchievement(48);
            } else if(player.spellId_4e == 8) {
              this.windMark[event.defender.charSlot_276] = 3;
              this.displayNumbers(11 + event.defender.charSlot_276, 3, 11, 1500);
              addAchievement(48);
            }
          }
        }

        if("dragoon_modifier:giant_axe".equals(player.equipment_11e.get(EquipmentSlot.WEAPON).getRegistryId().toString()) && event.attackType.isPhysical()) {
          if(new Random().nextInt(0, 99) < 20) {
            player.guard_54 = 1;
          }
        }

        if("dragoon_modifier:dragon_beater".equals(player.equipment_11e.get(EquipmentSlot.WEAPON).getRegistryId().toString()) && event.attackType.isPhysical()) {
          final int heal = (int)Math.ceil(event.damage * 0.01d);
          final int hp = player.stats.getStat(HP_STAT.get()).getCurrent();
          final int sp = player.stats.getStat(SP_STAT.get()).getCurrent();
          player.stats.getStat(HP_STAT.get()).setCurrent(hp + Math.min(1000, heal));
          player.stats.getStat(SP_STAT.get()).setCurrent(sp + Math.min(100, heal));
          this.displayNumbers(6 + player.charSlot_276, heal, 11, 1000);
          this.displayNumbers(6 + player.charSlot_276, heal, 3, 1500);
          this.roseSiphon[player.charSlot_276] = (int)Math.min(this.roseSiphonMax, Math.min(this.roseSiphonMax * 0.2 + this.roseSiphon[player.charSlot_276], this.roseSiphon[player.charSlot_276] + event.damage));
        }

        if("dragoon_modifier:ouroboros".equals(player.equipment_11e.get(EquipmentSlot.WEAPON).getRegistryId().toString()) && player.isDragoon()) {
          final int dragoonTurns = battleState_8006e398.dragoonTurnsRemaining_294[player.charSlot_276];
          final int sp = player.stats.getStat(SP_STAT.get()).getCurrent();
          if(player.isDragoon() && dragoonTurns >= 2 && sp >= 200) {
            player.stats.getStat(SP_STAT.get()).setCurrent(sp - 100);
            battleState_8006e398.dragoonTurnsRemaining_294[player.charSlot_276] = player.stats.getStat(SP_STAT.get()).getCurrent() / 100;
            event.damage *= 2;
            this.ouroboros[player.charSlot_276] = true;
          }
        }

        /*if("dragoon_modifier:elemental_arrow".equals(player.equipment_11e.get(EquipmentSlot.WEAPON).getRegistryId().toString())) { //Elemental Arrow
          if(event.defender instanceof final MonsterBattleEntity monster && event.attackType.isPhysical()) {
            final ArrayList<Element> elementsCalculated = new ArrayList<>();
            for(final Element elementArrowsElement : this.elementArrowsElements) {
              if(elementArrowsElement != null) {
                if(!elementsCalculated.contains(elementArrowsElement)) {
                  elementsCalculated.add(elementArrowsElement);
                  if(((Battle)currentEngineState_8004dd04).dragoonSpaceElement_800c6b64 != null) {
                    int damage = ((Battle)currentEngineState_8004dd04).dragoonSpaceElement_800c6b64.adjustDragoonSpaceDamage(event.attackType, event.damage, elementArrowsElement);
                    if(damage > event.damage) {
                      event.damage = damage;

                      damage = monster.getElement().adjustAttackingElementalDamage(event.attackType, event.damage, elementArrowsElement);
                      if(damage != event.damage) {
                        event.damage = damage;
                      }
                    }
                  } else {
                    final int damage = monster.getElement().adjustAttackingElementalDamage(event.attackType, event.damage, elementArrowsElement);
                    if(damage > event.damage) {
                      event.damage = damage;
                    }
                  }
                }
              }
            }

            if(new Random().nextInt(0, 99) < 40 && gameState_800babc8.items_2e9.size() < CONFIG.getConfig(CoreMod.INVENTORY_SIZE_CONFIG.get())) {
              Scus94491BpeSegment_8002.giveItem(REGISTRIES.items.getEntry("lod:trans_light").get());
            }
          }

          if(player.item_d4 != null) {
            player.stats.getStat(SP_STAT.get()).setCurrent(player.stats.getStat(SP_STAT.get()).getCurrent() + 100);
          }
        }*/

        if("dragoon_modifier:magic_hammer".equals(player.equipment_11e.get(EquipmentSlot.WEAPON).getRegistryId().toString())) {
          if(event.attackType.isPhysical()) {
            event.damage = 0;
          }
        }

        if("dragoon_modifier:overcharge_glove".equals(player.equipment_11e.get(EquipmentSlot.WEAPON).getRegistryId().toString())) {
          if(event.defender instanceof final MonsterBattleEntity monster) {
            if(monster.getElement() == THUNDER_ELEMENT.get()) {
              event.damage *= 3;
            }
          }
        }

        for(int i = 0; i < 3; i++) {
          if(this.ringOfElements[i] > 0 && ((Battle)currentEngineState_8004dd04).dragoonSpaceElement_800c6b64 == null) { //Ring of Elements
            if(event.defender instanceof final MonsterBattleEntity monster) {
              if(event.attackType.isPhysical()) {
                for(final Element e : player.equipmentAttackElements_1c) {
                  final int damage = this.ringOfElementsElement[i].adjustDragoonSpaceDamage(event.attackType, event.damage, e);
                  if(damage != event.damage) {
                    event.damage = damage;
                  }
                }
              } else {
                try {
                  final int damage = event.attacker.spell_94.element_08.get().adjustDragoonSpaceDamage(event.attackType, event.damage, this.ringOfElementsElement[i]);
                  if(damage != event.damage) {
                    event.damage = damage;
                  }
                } catch(final Exception ignored) {
                }

                try {
                  final int damage = event.attacker.item_d4.getAttackElement().adjustDragoonSpaceDamage(event.attackType, event.damage, this.ringOfElementsElement[i]);
                  if(damage != event.damage) {
                    event.damage = damage;
                  }
                } catch(final Exception ignored) {
                }
              }
            }
          }
        }

        if(this.haschelInParty) {
          if(event.defender instanceof final MonsterBattleEntity monster) { //Haschel in party thunder charge
            try {
              if(event.attacker.spell_94.element_08.get() == THUNDER_ELEMENT.get() && new Random().nextBoolean()) {
                if(this.staticCharge[event.attacker.charSlot_276] == 0) {
                  this.thunderCharge[monster.charSlot_276] = Math.min(10, this.thunderCharge[monster.charSlot_276] + 1);
                  this.displayNumbers(11 + monster.charSlot_276, 1, 10, 1500);
                }
              }
            } catch(final Exception ignored) {
            }

            try {
              if(event.attacker.item_d4.getAttackElement() == THUNDER_ELEMENT.get() && new Random().nextBoolean()) {
                if(this.staticCharge[event.attacker.charSlot_276] == 0) {
                  this.thunderCharge[monster.charSlot_276] = Math.min(10, this.thunderCharge[monster.charSlot_276] + 1);
                  this.displayNumbers(11 + monster.charSlot_276, 1, 10, 1500);
                }
              }
            } catch(final Exception ignored) {
            }

            if(event.attackType.isPhysical() && player.equipmentAttackElements_1c.contains(THUNDER_ELEMENT.get()) && new Random().nextBoolean()) {
              if(this.staticCharge[event.attacker.charSlot_276] == 0) {
                this.thunderCharge[monster.charSlot_276] = Math.min(10, this.thunderCharge[monster.charSlot_276] + 1);
                this.displayNumbers(11 + monster.charSlot_276, 1, 10, 1500);
              }
            }
          }

          if(player.charId_272 == 4) { //Haschel thunder charge on physical and spark net boost on max stacks and thunder element
            if(event.defender instanceof final MonsterBattleEntity monster) {
              if(player.dlevel_06 > 0) {
                if(event.attackType.isPhysical()) {
                  if(player.isDragoon()) {
                    if(new Random().nextBoolean() || new Random().nextBoolean()) {
                      if(this.staticCharge[event.attacker.charSlot_276] == 0) {
                        this.thunderCharge[monster.charSlot_276] = Math.min(10, this.thunderCharge[monster.charSlot_276] + 1);
                        this.displayNumbers(11 + monster.charSlot_276, 1, 10, 1500);
                      }
                    }
                  } else {
                    if(new Random().nextBoolean()) {
                      if(this.staticCharge[event.attacker.charSlot_276] == 0) {
                        this.thunderCharge[monster.charSlot_276] = Math.min(10, this.thunderCharge[monster.charSlot_276] + 1);
                        this.displayNumbers(11 + monster.charSlot_276, 1, 10, 1500);
                      }
                    }
                  }
                } else {
                  if(player.isDragoon() && player.spellId_4e == 86) {
                    if(this.thunderCharge[monster.charSlot_276] == 10) {
                      final double thunderDamage = 7.07 + ((player.dlevel_06 - 1) * 1.01);
                      this.thunderCharge[monster.charSlot_276] = 0;
                      event.damage *= (int)(monster.getElement() == THUNDER_ELEMENT.get() ? thunderDamage : 3.5);
                      addAchievement(51);
                    }
                  }
                }
              }

              if(this.staticCharge[player.charSlot_276] > 0) {
                final int transferCharge = Math.min(10, this.staticCharge[player.charSlot_276]) - this.thunderCharge[monster.charSlot_276];
                this.thunderCharge[monster.charSlot_276] += transferCharge;
                this.staticCharge[player.charSlot_276] -= transferCharge;
                this.displayNumbers(11 + monster.charSlot_276, transferCharge, 10, 1500);
              }
            }
          }
        }

        if(event.defender instanceof final PlayerBattleEntity defender) { //ifMeru's in Wingly Boost Mode all healing is 0, the only player vs player damage should be physical confusion or magic
          if(this.meruBoost[defender.charSlot_276]) {
            if(event.attackType.isMagical()) {
              event.damage = 0;
            }
          }
        }

        if("dragoon_modifier:firebrand".equals(player.equipment_11e.get(EquipmentSlot.WEAPON).getRegistryId().toString())) {
          event.damage *= 1.2;
        }

        if(player.charId_272 == 6) {
          if(event.damage > 0 && this.meruWinglyMagic[player.charSlot_276]) {
            this.meruIceShield[player.charSlot_276] = (int)Math.min(this.meruIceShieldMax, Math.min(this.meruIceShieldMax * 0.1 + this.meruIceShield[player.charSlot_276], this.meruIceShield[player.charSlot_276] + event.damage * 0.1));
            this.displayNumbers(6 + player.charSlot_276, this.meruIceShield[player.charSlot_276], 1, 2500);
            addAchievement(52);
          }
        }

        if(player.charId_272 == 7) {
          if(this.kongolCounterStance[player.charSlot_276]) {
            player.guard_54 = 1;

            if(this.kongolCounterStance[player.charSlot_276] && this.kongolCounterStanceTurns[player.charSlot_276] == 0) {
              player.guard_54 = 0;
              this.kongolCounterStance[player.charSlot_276] = false;
              this.currentPlayer.stats.getStat(LodMod.SPEED_STAT.get()).setRaw(this.kongolCounterSpeed);
            }
          }
        }
      }

            /*
                DEFENDING PLAYER
             */

      if(event.defender instanceof final PlayerBattleEntity defender) {
        if(this.haschelInParty) {
          try {
            if(event.attacker.spell_94.element_08.get() == THUNDER_ELEMENT.get() && new Random().nextBoolean()) {
              this.thunderCharge[event.attacker.charSlot_276] = Math.min(10, this.thunderCharge[event.attacker.charSlot_276] + 1);
              this.displayNumbers(11 + event.attacker.charSlot_276, 1, 10, 1500);
            }
          } catch(final Exception ignored) {
          }

          try {
            if(event.attacker.item_d4.getAttackElement() == THUNDER_ELEMENT.get() && new Random().nextBoolean()) {
              this.thunderCharge[event.attacker.charSlot_276] = Math.min(10, this.thunderCharge[event.attacker.charSlot_276] + 1);
              this.displayNumbers(11 + event.attacker.charSlot_276, 1, 10, 1500);
            }
          } catch(final Exception ignored) {
          }
        }

        if("dragoon_modifier:ring_of_shielding".equals(defender.equipment_11e.get(EquipmentSlot.ACCESSORY).getRegistryId().toString())) { //Ring of Shielding
          final int hp = defender.stats.getStat(HP_STAT.get()).getCurrent();
          if((hp - event.damage) <= 0 && new Random().nextInt(0, 99) < 35) {
            event.damage = 0;
            //defender.stats.getStat(null).addMod(LodMod.id("material_shield"), LodMod.UNARY_STAT_MOD_TYPE.get().make(new UnaryStatModConfig().percent(100).turns(5)));
            //defender.stats.getStat(SPEED_STAT.get()).addMod(LodMod.id("magic_shield"), LodMod.UNARY_STAT_MOD_TYPE.get().make(new UnaryStatModConfig().percent(100).turns(5)));
          }
        }

        if(defender.charId_272 == 6) { //ifMeru dies in Wingly Boost turn it off
          final int hp = defender.stats.getStat(HP_STAT.get()).getCurrent();
          if(this.meruBoost[defender.charSlot_276] && hp - event.damage <= 0) {
            this.meruBoostTurns[defender.charSlot_276] = 0;
            this.meruBoost[defender.charSlot_276] = false;
            defender.stats.getStat(HP_STAT.get()).setMaxRaw(this.meruMaxHpSave[defender.charSlot_276]);
            defender.magicDefence_3a = this.meruMDFSave[defender.charSlot_276];
          }
        }

        if(this.ringOfElements[defender.charSlot_276] > 0) {
          final int hp = defender.stats.getStat(HP_STAT.get()).getCurrent();
          if(event.damage <= 0) {
            this.ringOfElements[defender.charSlot_276] = 0;
          }
        }

        final int level = defender.level_04;

        if(event.attackType.isPhysical()) { //DF Boost
          if(defender.charId_272 == 2 || defender.charId_272 == 8) { //Shana
            if(level >= 30) {
              event.damage = (int)Math.round(event.damage / 1.12d);
            }
          }

          if(defender.charId_272 == 3 && level >= 30) { //Rose
            event.damage = (int)Math.round(event.damage / 1.11d);
          }

          if(defender.charId_272 == 6 && level >= 30) { //Meru
            event.damage = (int)Math.round(event.damage / 1.26d);
          }
        }

        if(event.attackType.isMagical()) {
          Element attackElement = null;
          final String armorEquipped = defender.equipment_11e.get(EquipmentSlot.ARMOUR).getRegistryId().toString();

          try {
            attackElement = event.attacker.item_d4.getAttackElement();
          } catch(final Exception ignored) {
          }

          try {
            if(attackElement == null) {
              attackElement = event.attacker.spell_94.element_08.get();
            }
          } catch(final Exception ignored) {
          }

          if(attackElement != null) {
            //Divine Dragon Armor 15% elemental reduction instead of half
            if(attackElement == FIRE_ELEMENT.get() && "lod:red_dg_armor".equals(armorEquipped)) {
              event.damage = (int)Math.round(event.damage / 1.15d);
            } else if(attackElement == WIND_ELEMENT.get() && "lod:jade_dg_armor".equals(armorEquipped)) {
              event.damage = (int)Math.round(event.damage / 1.15d);
            } else if(attackElement == EARTH_ELEMENT.get() && "lod:gold_dg_armor".equals(armorEquipped)) {
              event.damage = (int)Math.round(event.damage / 1.15d);
            } else if(attackElement == THUNDER_ELEMENT.get() && "lod:violet_dg_armor".equals(armorEquipped)) {
              event.damage = (int)Math.round(event.damage / 1.15d);
            } else if(attackElement == LIGHT_ELEMENT.get() && "lod:silver_dg_armor".equals(armorEquipped)) {
              event.damage = (int)Math.round(event.damage / 1.15d);
            } else if(attackElement == DARK_ELEMENT.get() && "lod:dark_dg_armor".equals(armorEquipped)) {
              event.damage = (int)Math.round(event.damage / 1.15d);
            } else if(attackElement == WATER_ELEMENT.get() && "lod:blue_dg_armor".equals(armorEquipped)) {
              event.damage = (int)Math.round(event.damage / 1.15d);
            }
          }
        }

        try {
          if(("dragoon_modifier:weak_shield".equals(event.attacker.item_d4.getRegistryId().toString()) || "dragoon_modifier:shield_generator".equals(event.attacker.item_d4.getRegistryId().toString())) && event.attacker instanceof PlayerBattleEntity) {
            final int shield;
            if("dragoon_modifier:weak_shield".equals(event.attacker.item_d4.getRegistryId().toString())) {
              shield = (int)Math.floor(event.defender.getStat(BattleEntityStat.MAX_HP) / 2d);
            } else {
              shield = event.defender.getStat(BattleEntityStat.MAX_HP) * 2;
            }
            this.protectionShield[defender.charSlot_276] += shield;
            event.damage = 0;
          }
        } catch(final Exception ignored) {
        }

        if(event.damage > 0 && event.attacker instanceof MonsterBattleEntity) {
          if(this.protectionShield[defender.charSlot_276] > 0) {
            if(event.damage <= this.protectionShield[defender.charSlot_276]) {
              this.displayNumbers(6 + defender.charSlot_276, event.damage, 0, 1500);
              this.protectionShield[defender.charSlot_276] -= event.damage;
              event.damage = 0;
            } else {
              this.displayNumbers(6 + defender.charSlot_276, this.protectionShield[defender.charSlot_276], 0, 1500);
              event.damage -= this.protectionShield[defender.charSlot_276];
              this.protectionShield[defender.charSlot_276] = 0;
            }
          }
        }

        if(this.windBarrier[event.defender.charSlot_276]) {
          event.attacker.turnValue_4c = Math.max(0, event.attacker.turnValue_4c - 30);
        }

        if(defender.charId_272 == 6) {
          if(event.damage > 0 && this.meruIceShield[defender.charSlot_276] > 0) {
            if(event.damage <= this.meruIceShield[defender.charSlot_276]) {
              this.displayNumbers(6 + defender.charSlot_276, event.damage, 1, 2500);
              this.meruIceShield[defender.charSlot_276] -= event.damage;
              event.damage = 0;
            } else {
              this.displayNumbers(6 + defender.charSlot_276, this.meruIceShield[defender.charSlot_276], 1, 2500);
              event.damage -= this.meruIceShield[defender.charSlot_276];
              this.meruIceShield[defender.charSlot_276] = 0;
            }
          }
        }

        if(defender.charId_272 == 7) {
          if(this.kongolCounterStance[defender.charSlot_276]) {
            defender.guard_54 = 1;
            defender.turnValue_4c += 218;
            event.damage *= 1.8;
            addAchievement(53);
          }
        }
      }
    }

        /*if(event.attacker instanceof MonsterBattleEntity monster && event.defender instanceof PlayerBattleEntity player) {
            try {
                System.out.println("-------------------------------");
                if(event.attackType.isPhysical()) {
                    System.out.println("[DRAMODTEST] ID:  " + monster.spellId_4e);
                    System.out.println("[DRAMODTEST] DMG: " + spellStats_800fa0b8_Player[monster.spellId_4e].multi_04);
                } else {
                    System.out.println("[DRAMODTEST] ID:  " + monster.spellId_4e);
                    System.out.println("[DRAMODTEST] DMG: " + spellStats_800fa0b8_Player[monster.spellId_4e].multi_04);
                    System.out.println("[DRAMODTEST] IID: " + monster.itemId_52);
                    System.out.println("[DRAMODTEST] ITM: " + monster.item_d4.damage_05);
                }
            } catch (Exception ignored) {}
        }*/

    if(ultimateBattle) { //Ultimate Boss effects per attack
      if(event.attacker instanceof final PlayerBattleEntity player && event.defender instanceof MonsterBattleEntity) {
        if(this.ultimatePenality[player.charSlot_276][1] > 1) { //Damage penalty for over leveled ultimate boss
          event.damage /= this.ultimatePenality[player.charSlot_276][1];
        }
      }

      if(event.attacker instanceof MonsterBattleEntity && event.defender instanceof final PlayerBattleEntity player) {
        if(this.ultimatePenality[player.charSlot_276][1] > 1) { //Damage penalty for over leveled ultimate boss
          event.damage *= this.ultimatePenality[player.charSlot_276][1];
        }
      }

      if(event.attacker instanceof final MonsterBattleEntity monster) {
        this.ultimateGuardBreak((PlayerBattleEntity)event.defender, monster, event);
        this.ultimateMPAttack((PlayerBattleEntity)event.defender, monster, event);
      }
    }

    this.updateEnrageMode(event);
    this.updateElementalBomb(event);
    this.updateDamageTracker(event);

    if(event.attacker instanceof PlayerBattleEntity) {
      int damage = event.damage;
      if(event.attackType == AttackType.DRAGOON_MAGIC_STATUS_ITEMS) {
        if(Integer.parseInt(spellStatsPlayer.get(event.attacker.spellId_4e)[4]) == 0) {
          switch(Integer.parseInt(spellStatsPlayer.get(event.attacker.spellId_4e)[3])) {
            case 0:
              break;
            case 1:
              damage *= 8;
              break;
            case 2:
              damage *= 6;
              break;
            case 4:
              damage *= 5;
              break;
            case 8:
              damage *= 4;
              break;
            case 16:
              damage *= 3;
              break;
            case 32:
              damage *= 2;
              break;
            case 64:
              damage = damage + damage / 2;
              break;
            case 128:
              damage /= 2;
              break;
            default:
              break;
          }
        }
      }

      if(damage > 9999) {
        addAchievement(65);
      }
    }
  }

  @EventListener
  public void setBentStatEvent(final SetBentStatEvent event) {
    updateEnrageMode(null);
    if(event.registryValue != null) {
      if(event.bent instanceof PlayerBattleEntity) {
        if(this.shanaDeffArrow && event.stat == BattleEntityStat.ITEM_ID) {
          if("dragoon_modifier:fire_arrow".equals(((PlayerBattleEntity)event.bent).equipment_11e.get(EquipmentSlot.WEAPON).getRegistryId().toString())) {
            event.registryValue = LodItems.BURN_OUT.getId();
          } else if("dragoon_modifier:water_arrow".equals(((PlayerBattleEntity)event.bent).equipment_11e.get(EquipmentSlot.WEAPON).getRegistryId().toString())) {
            event.registryValue = LodItems.SPEAR_FROST.getId();
          } else if("dragoon_modifier:wind_arrow".equals(((PlayerBattleEntity)event.bent).equipment_11e.get(EquipmentSlot.WEAPON).getRegistryId().toString())) {
            event.registryValue = LodItems.SPINNING_GALE.getId();
          } else if("dragoon_modifier:earth_arrow".equals(((PlayerBattleEntity)event.bent).equipment_11e.get(EquipmentSlot.WEAPON).getRegistryId().toString())) {
            event.registryValue = LodItems.PELLET.getId();
          } else if("dragoon_modifier:dark_arrow".equals(((PlayerBattleEntity)event.bent).equipment_11e.get(EquipmentSlot.WEAPON).getRegistryId().toString())) {
            event.registryValue = LodItems.DARK_MIST.getId();
          } else if("dragoon_modifier:light_arrow".equals(((PlayerBattleEntity)event.bent).equipment_11e.get(EquipmentSlot.WEAPON).getRegistryId().toString())) {
            event.registryValue = LodItems.TRANS_LIGHT.getId();
          } else if("dragoon_modifier:thunder_arrow".equals(((PlayerBattleEntity)event.bent).equipment_11e.get(EquipmentSlot.WEAPON).getRegistryId().toString())) {
            event.registryValue = LodItems.SPARK_NET.getId();
          }
        }
      }
    } else {
      if(event.bent instanceof MonsterBattleEntity) {
        if(event.stat == BattleEntityStat.CURRENT_HP && event.value > 0) {
          final int damage = event.bent.getStat(BattleEntityStat.CURRENT_HP) - event.value;
          if(this.enrageModeProtection[event.bent.charSlot_276] > 0 && damage > 0) {
            if(damage <= this.enrageModeProtection[event.bent.charSlot_276]) {
              event.value = event.bent.getStat(BattleEntityStat.CURRENT_HP);
              this.enrageModeProtection[event.bent.charSlot_276] -= damage;
              this.displayNumbers(11 + event.bent.charSlot_276, damage, 0, 1500);
            } else {
              event.value += this.enrageModeProtection[event.bent.charSlot_276];
              this.displayNumbers(11 + event.bent.charSlot_276, this.enrageModeProtection[event.bent.charSlot_276], 0, 1500);
              this.enrageModeProtection[event.bent.charSlot_276] = 0;
            }
          } else {
            this.enrageModeProtection[event.bent.charSlot_276] = 0;
          }
        }
      }
    }
  }

  @EventListener
  public void archerSpEvent(final ArcherSpEvent event) {
    final PlayerBattleEntity bent = event.bent;

    if(bent.charId_272 == 2 || bent.charId_272 == 8) {
      event.sp = Integer.parseInt(shanaSpGain.getFirst()[bent.dlevel_06 - 1]);
    }
  }

  public void addBurnStacks(final PlayerBattleEntity dart, final int stacks) {
    if(!this.burnStackMode[dart.charSlot_276]) {
      this.previousBurnStacks[dart.charSlot_276] = this.burnStacks[dart.charSlot_276];
      final int dlv = dart.dlevel_06;
      this.burnStacksMax[dart.charSlot_276] = dlv == 0 ? 0 : dlv == 1 ? 3 : dlv == 2 ? 6 : dlv == 3 ? 9 : dlv == 7 ? 15 : 12;
      this.burnStacks[dart.charSlot_276] = Math.min(this.burnStacksMax[dart.charSlot_276], this.burnStacks[dart.charSlot_276] + stacks);

      if(this.burnStacks[dart.charSlot_276] >= 4 && this.previousBurnStacks[dart.charSlot_276] < 4) {
        dart.stats.getStat(MP_STAT.get()).setCurrent(dart.stats.getStat(MP_STAT.get()).getCurrent() + 10);
      } else if(this.burnStacks[dart.charSlot_276] >= 8 && this.previousBurnStacks[dart.charSlot_276] < 8) {
        dart.stats.getStat(MP_STAT.get()).setCurrent(dart.stats.getStat(MP_STAT.get()).getCurrent() + 20);
      } else if(this.burnStacks[dart.charSlot_276] >= 12 && this.previousBurnStacks[dart.charSlot_276] < 12) {
        dart.stats.getStat(MP_STAT.get()).setCurrent(dart.stats.getStat(MP_STAT.get()).getCurrent() + 30);
      }

      this.displayNumbers(6 + dart.charSlot_276, stacks, 5, 1500);
      addAchievement(47);
    }
  }

  public void dramodBurnStacks(final int spellId) {
    if(spellId >= 0 && spellId <= 3) {
      if(this.burnStackMode[this.currentPlayerSlot] && this.burnStacks[this.currentPlayerSlot] > 0) {
        int damage = Integer.parseInt(spellStatsPlayer.get(spellId)[3]);
        String newDescription = spellStatsPlayer.get(spellId)[13].replace("1.00", String.format("%.2f", (1 + (this.burnStacks[this.currentPlayerSlot] * this.dmgPerBurn))));

        if(this.burnStacks[this.currentPlayerSlot] == this.burnStacksMax[this.currentPlayerSlot]) {
          if(spellId == 0) {
            damage *= (1 + (this.burnStacks[this.currentPlayerSlot] * this.dmgPerBurn)) * (Integer.parseInt(spellStatsPlayer.get(2)[3]) / Integer.parseInt(spellStatsPlayer.get(0)[3])) * 1.5;
            newDescription = spellStatsPlayer.get(spellId)[13].replace("1.00", String.format("%.2f", ((1 + (this.burnStacks[this.currentPlayerSlot] * this.dmgPerBurn)) * (Integer.parseInt(spellStatsPlayer.get(2)[3]) / Integer.parseInt(spellStatsPlayer.get(0)[3])) * 1.5)));
          } else if(spellId == 1) {
            damage *= (1 + (this.burnStacks[this.currentPlayerSlot] * this.dmgPerBurn)) * Integer.parseInt(spellStatsPlayer.get(3)[3]) / Integer.parseInt(spellStatsPlayer.get(1)[3]);
            newDescription = spellStatsPlayer.get(spellId)[13].replace("1.00", String.format("%.2f", (1 + (this.burnStacks[this.currentPlayerSlot] * this.dmgPerBurn)) * Integer.parseInt(spellStatsPlayer.get(3)[3]) / Integer.parseInt(spellStatsPlayer.get(1)[3])));
          } else if(spellId == 2) {
            damage *= (1 + (this.burnStacks[this.currentPlayerSlot] * this.dmgPerBurn)) * 1.5;
            newDescription = spellStatsPlayer.get(spellId)[13].replace("1.00", String.format("%.2f", (1 + (this.burnStacks[this.currentPlayerSlot] * this.dmgPerBurn)) * 1.5));
          } else {
            damage *= 1 + (this.burnStacks[this.currentPlayerSlot] * this.dmgPerBurn);
          }
        } else {
          damage *= 1 + (this.burnStacks[this.currentPlayerSlot] * this.dmgPerBurn);
        }

        spellStats_800fa0b8_Player[spellId] = new SpellStats0c(
          spellStatsPlayer.get(spellId)[12],
          newDescription,
          Integer.parseInt(spellStatsPlayer.get(spellId)[0]),
          Integer.parseInt(spellStatsPlayer.get(spellId)[1]),
          Integer.parseInt(spellStatsPlayer.get(spellId)[2]),
          damage,
          Integer.parseInt(spellStatsPlayer.get(spellId)[4]),
          Integer.parseInt(spellStatsPlayer.get(spellId)[5]),
          this.burnStacks[this.currentPlayerSlot] == this.burnStacksMax[this.currentPlayerSlot] ? 0 : Integer.parseInt(spellStatsPlayer.get(spellId)[6]),
          Integer.parseInt(spellStatsPlayer.get(spellId)[7]),
          Element.fromFlag(Integer.parseInt(spellStatsPlayer.get(spellId)[8])),
          Integer.parseInt(spellStatsPlayer.get(spellId)[9]),
          Integer.parseInt(spellStatsPlayer.get(spellId)[10]),
          Integer.parseInt(spellStatsPlayer.get(spellId)[11])
        );
      } else {
        spellStats_800fa0b8_Player[spellId] = new SpellStats0c(
          spellStatsPlayer.get(spellId)[12],
          spellStatsPlayer.get(spellId)[13],
          Integer.parseInt(spellStatsPlayer.get(spellId)[0]),
          Integer.parseInt(spellStatsPlayer.get(spellId)[1]),
          Integer.parseInt(spellStatsPlayer.get(spellId)[2]),
          Integer.parseInt(spellStatsPlayer.get(spellId)[3]),
          Integer.parseInt(spellStatsPlayer.get(spellId)[4]),
          Integer.parseInt(spellStatsPlayer.get(spellId)[5]),
          Integer.parseInt(spellStatsPlayer.get(spellId)[6]),
          Integer.parseInt(spellStatsPlayer.get(spellId)[7]),
          Element.fromFlag(Integer.parseInt(spellStatsPlayer.get(spellId)[8])),
          Integer.parseInt(spellStatsPlayer.get(spellId)[9]),
          Integer.parseInt(spellStatsPlayer.get(spellId)[10]),
          Integer.parseInt(spellStatsPlayer.get(spellId)[11])
        );
      }
    }
  }

  @EventListener
  public void spellStats(final SpellStatsEvent spell) {
    final int spellId = spell.spellId;

    final String difficulty = CONFIG.getConfig(DIFFICULTY.get());

    if(spell.bent instanceof PlayerBattleEntity) {
      if("Hard Mode".equals(difficulty) || "US + Hard Bosses".equals(difficulty) || "Hell Mode".equals(difficulty) || "Hard + Hell Bosses".equals(difficulty)) {
        this.dramodBurnStacks(spellId);
      }
    }
  }

  @EventListener
  public void dragonBlockStaffOn(final DragonBlockStaffOnEvent event) {
    final String difficulty = CONFIG.getConfig(DIFFICULTY.get());
    if("Hard Mode".equals(difficulty) || "US + Hard Bosses".equals(difficulty) || "Hell Mode".equals(difficulty) || "Hard + Hell Bosses".equals(difficulty)) {
      this.dragonBlockStaff = true;
      for(int i = 0; i < battleState_8006e398.getAllBentCount(); i++) {
        final ScriptState<? extends BattleEntity27c> state = battleState_8006e398.allBents_e0c[i];
        final BattleEntity27c bobj = state.innerStruct_00;
        if(bobj instanceof final PlayerBattleEntity player) {
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
    final String difficulty = CONFIG.getConfig(DIFFICULTY.get());
    if("Hard Mode".equals(difficulty) || "US + Hard Bosses".equals(difficulty) || "Hell Mode".equals(difficulty) || "Hard + Hell Bosses".equals(difficulty)) {
      this.dragonBlockStaff = false;
      for(int i = 0; i < battleState_8006e398.getAllBentCount(); i++) {
        final ScriptState<? extends BattleEntity27c> state = battleState_8006e398.allBents_e0c[i];
        final BattleEntity27c bobj = state.innerStruct_00;
        if(bobj instanceof final PlayerBattleEntity player) {
          player.dragoonAttack_ac = Integer.parseInt(dragoonStatsTable.get(player.charId_272 * (maxDragoonLevel + 1) + player.dlevel_06)[3]);
          player.dragoonMagic_ae = Integer.parseInt(dragoonStatsTable.get(player.charId_272 * (maxDragoonLevel + 1) + player.dlevel_06)[4]);
          player.dragoonDefence_b0 = Integer.parseInt(dragoonStatsTable.get(player.charId_272 * (maxDragoonLevel + 1) + player.dlevel_06)[5]);
          player.dragoonMagicDefence_b2 = Integer.parseInt(dragoonStatsTable.get(player.charId_272 * (maxDragoonLevel + 1) + player.dlevel_06)[6]);
        }
      }
    }
  }

  @EventListener
  public void repeatItemReturn(final RepeatItemReturnEvent event) {
    if(Boolean.parseBoolean(itemStats.get(this.getItemRowFromTable(event.item.getRegistryId().toString()))[35])) {
      event.returnItem = true;
    }
  }

  public void updateEnrageMode(final AttackEvent event) {
    final int damage = event == null ? 0 : event.damage;
    if(GameEngine.CONFIG.getConfig(ENRAGE_MODE.get()) == EnrageMode.ON) {
      final String difficulty = GameEngine.CONFIG.getConfig(DIFFICULTY.get());
      for(int i = 0; i < battleState_8006e398.getMonsterCount(); i++) {
        final MonsterBattleEntity monster = battleState_8006e398.monsterBents_e50[i].innerStruct_00;
        final int hp = monster.stats.getStat(HP_STAT.get()).getCurrent();
        final int maxHp = monster.stats.getStat(HP_STAT.get()).getMax();
        final boolean hellMode = "Hell Mode".equals(difficulty) || "Hard + Hell Bosses".equals(difficulty) && hp - damage > 0;
        if(hp <= maxHp * 0.8 && this.enrageMode[monster.charSlot_276] == 0) {
          monster.attack_34 = (int)Math.round(monster.attack_34 * 1.1d);
          monster.magicAttack_36 = (int)Math.round(monster.magicAttack_36 * 1.1d);
          monster.defence_38 = (int)Math.round(monster.defence_38 * 1.1d);
          monster.magicDefence_3a = (int)Math.round(monster.magicDefence_3a * 1.1d);
          this.enrageMode[monster.charSlot_276] = 1;
          if(hellMode) {
            if(ArrayUtils.contains(this.bossEncounters, encounterId_800bb0f8)) {
              final int protection = (int)Math.ceil(maxHp * 0.075);
              this.enrageModeProtection[monster.charSlot_276] += protection;
            }
          }
        }
        if(hp <= maxHp * 0.4 && this.enrageMode[monster.charSlot_276] == 1) {
          monster.attack_34 = (int)Math.round(monster.attack_34 * 1.136365d);
          monster.magicAttack_36 = (int)Math.round(monster.magicAttack_36 * 1.136365d);
          monster.defence_38 = (int)Math.round(monster.defence_38 * 1.136365d);
          monster.magicDefence_3a = (int)Math.round(monster.magicDefence_3a * 1.136365d);
          this.enrageMode[monster.charSlot_276] = 2;
          if(hellMode) {
            if(ArrayUtils.contains(this.bossEncounters, encounterId_800bb0f8)) {
              final int protection = (int)Math.ceil(maxHp * 0.15);
              this.enrageModeProtection[monster.charSlot_276] += protection;
            }
          }
        }
      }
    }
  }

  public void updateItemMagicDamage() {
    if(GameEngine.CONFIG.getConfig(DAMAGE_TRACKER.get()) == DamageTracker.ON) {
      for(int i = 0; i < battleState_8006e398.getMonsterCount(); i++) {
        final MonsterBattleEntity monster = battleState_8006e398.monsterBents_e50[i].innerStruct_00;
        final int hp = monster.stats.getStat(HP_STAT.get()).getCurrent();
        if(hp < this.damageTrackerPreviousHP[monster.charSlot_276]) {
          final int difference = this.damageTrackerPreviousHP[monster.charSlot_276] - hp;
          this.damageTracker[this.damageTrackerPreviousCharacter][this.damageTrackerPreviousAttackType] += difference;
          this.damageTrackerLog.add(charNames[this.damageTrackerPreviousCharacterID] + " - Multiplier - " + difference);
          this.damageTrackerPreviousHP[monster.charSlot_276] = hp;
        }
      }
    }
  }

  private boolean isAttackItem(final Item item) {
    return item == LodItems.BURNING_WAVE.get() || item == LodItems.FROZEN_JET.get() || item == LodItems.DOWN_BURST.get() || item == LodItems.GRAVITY_GRABBER.get() || item == LodItems.SPECTRAL_FLASH.get() || item == LodItems.NIGHT_RAID.get() || item == LodItems.FLASH_HALL.get() || item == LodItems.PSYCHE_BOMB.get() || item == LodItems.PSYCHE_BOMB_X.get();
  }

  public void updateElementalBomb(final AttackEvent event) {
    if(GameEngine.CONFIG.getConfig(ELEMENTAL_BOMB.get()) == ElementalBomb.ON) {
      if(event.attacker instanceof final PlayerBattleEntity player) {
        try {
          if(this.isAttackItem(player.item_d4) && event.defender instanceof final MonsterBattleEntity monster) {
            //for(int i = 0; i < monsterCount_800c6768.get(); i++) {
            if(this.elementalBombTurns[monster.charSlot_276] == 0) {
              final Element swapTo = player.item_d4.getAttackElement();
              this.elementalBombPreviousElement[monster.charSlot_276] = monster.getElement();
              this.elementalBombTurns[monster.charSlot_276] = 5;
              monster.element = swapTo;
            }
          }
          //}
        } catch(final Exception ignored) {
        }
      }
    }
  }

  @EventListener
  public void shanaItemSpGain(final AttackEvent event) {
    final String difficulty = GameEngine.CONFIG.getConfig(DIFFICULTY.get());
    if("Hard Mode".equals(difficulty) || "US + Hard Bosses".equals(difficulty) || "Hell Mode".equals(difficulty) || "Hard + Hell Bosses".equals(difficulty)) {
      if(event.attacker instanceof final PlayerBattleEntity player && event.defender instanceof final MonsterBattleEntity monster) {
        if((player.charId_272 == 2 || player.charId_272 == 8) && player.item_d4 != null && !player.isDragoon()) {
          final int sp = player.getStat(BattleEntityStat.CURRENT_SP);
          int gain = !this.isItemArrow ? 50 : player.dlevel_06 > 0 ? Integer.parseInt(shanaSpGain.getFirst()[player.dlevel_06 - 1]) : 0;
          if(this.isItemArrow) {
            gain *= (1d + (player.spMultiplier_128 / 100d));
          }
          spGained_800bc950[player.charSlot_276] += gain;
          player.setStat(BattleEntityStat.CURRENT_SP, sp + gain);
          this.recaluteBentDragoonTurns(player);
        }
      }
    }
  }

  public void updateDamageTracker(final AttackEvent attack) {
    if(GameEngine.CONFIG.getConfig(DAMAGE_TRACKER.get()) == DamageTracker.ON) {
      if(attack.attacker instanceof final PlayerBattleEntity player && attack.defender instanceof final MonsterBattleEntity monster) {
        if(player.isDragoon()) {
          if(attack.attackType.isPhysical()) {
            this.damageTrackerPreviousAttackType = 0;
            this.damageTracker[player.charSlot_276][0] += attack.damage;
            this.damageTrackerLog.add(charNames[gameState_800babc8.charIds_88[player.charSlot_276]] + " - D.Physical - " + attack.damage);
          } else {
            this.damageTrackerPreviousAttackType = 1;
            this.damageTracker[player.charSlot_276][1] += attack.damage;
            this.damageTrackerLog.add(charNames[gameState_800babc8.charIds_88[player.charSlot_276]] + " - D.Magical - " + attack.damage);
          }
        } else {
          if(attack.attackType.isPhysical()) {
            this.damageTrackerPreviousAttackType = 2;
            this.damageTracker[player.charSlot_276][2] += attack.damage;
            this.damageTrackerLog.add(charNames[gameState_800babc8.charIds_88[player.charSlot_276]] + " - Physical - " + attack.damage);
          } else {
            this.damageTrackerPreviousAttackType = 3;
            this.damageTracker[player.charSlot_276][3] += attack.damage;
            this.damageTrackerLog.add(charNames[gameState_800babc8.charIds_88[player.charSlot_276]] + " - Magical - " + attack.damage);
          }
        }

        final int hp = monster.stats.getStat(HP_STAT.get()).getCurrent();
        if(attack.damage > hp && hp > 0 && hp != this.damageTrackerPreviousHP[monster.charSlot_276]) {
          this.damageTracker[player.charSlot_276][4] = attack.damage - hp;
        }

        this.damageTrackerPreviousCharacter = player.charSlot_276;
        this.damageTrackerPreviousCharacterID = player.charId_272;
        this.damageTrackerPreviousHP[monster.charSlot_276] = hp - attack.damage;
      }
    }
  }

  public void ultimateZeroSPStart(final PlayerBattleEntity player) {
    final int encounterId = encounterId_800bb0f8;

    if(encounterId == 413 || encounterId == 415 || encounterId == 403) {
      player.stats.getStat(SP_STAT.get()).setCurrent(0);
    }
  }

  public void ultimateStatusChanceResist(final PlayerBattleEntity player) {
    final int encounterId = encounterId_800bb0f8;
    if(encounterId == 387) {
      player.onHitStatusChance_44 /= 2;
    }
  }

  @EventListener
  public void statDisplay(final StatDisplayEvent event) {
    final String difficulty = GameEngine.CONFIG.getConfig(DIFFICULTY.get());
    if("Hard Mode".equals(difficulty) || "US + Hard Bosses".equals(difficulty) || "Hell Mode".equals(difficulty) || "Hard + Hell Bosses".equals(difficulty)) {
      if(event.player.charId_272 == 0 && this.burnStacksMax[event.player.charSlot_276] > 0 && this.burnStacks[event.player.charSlot_276] > 0) {
        final float burnPercent = (float)this.burnStacks[event.player.charSlot_276] / this.burnStacksMax[event.player.charSlot_276];
        this.renderCharacterBar(event.player, 1.0f, 0.0f, 0.0f, burnPercent, false);
        if(this.burnStackMode[event.player.charSlot_276]) {
          this.renderCharacterBar(event.player, 1.0f, 0.0f, 0.0f, 1.0f, true);
        }
      } else if(event.player.charId_272 == 1 || event.player.charId_272 == 5) {
        if(this.windBarrier[event.player.charSlot_276]) {
          this.renderCharacterBar(event.player, 0.0f, 1.0f, 0.0f, 1.0f, true);
        }
      } else if(event.player.charId_272 == 2 || event.player.charId_272 == 8) {
        final float r, g, b;
        if("dragoon_modifier:fire_arrow".equals(event.player.equipment_11e.get(EquipmentSlot.WEAPON).getRegistryId().toString())) {
          r = 1.0f;
          g = 0.0f;
          b = 0.0f;
        } else if("dragoon_modifier:water_arrow".equals(event.player.equipment_11e.get(EquipmentSlot.WEAPON).getRegistryId().toString())) {
          r = 0.0f;
          g = 0.5f;
          b = 1.0f;
        } else if("dragoon_modifier:wind_arrow".equals(event.player.equipment_11e.get(EquipmentSlot.WEAPON).getRegistryId().toString())) {
          r = 0.0f;
          g = 1.0f;
          b = 0.0f;
        } else if("dragoon_modifier:earth_arrow".equals(event.player.equipment_11e.get(EquipmentSlot.WEAPON).getRegistryId().toString())) {
          r = 0.5f;
          g = 0.35f;
          b = 0.25f;
        } else if("dragoon_modifier:dark_arrow".equals(event.player.equipment_11e.get(EquipmentSlot.WEAPON).getRegistryId().toString())) {
          r = 0.0f;
          g = 0.0f;
          b = 0.5f;
        } else if("dragoon_modifier:light_arrow".equals(event.player.equipment_11e.get(EquipmentSlot.WEAPON).getRegistryId().toString())) {
          r = 1.0f;
          g = 1.0f;
          b = 0.0f;
        } else if("dragoon_modifier:thunder_arrow".equals(event.player.equipment_11e.get(EquipmentSlot.WEAPON).getRegistryId().toString())) {
          r = 0.5f;
          g = 0.0f;
          b = 1.0f;
        } else {
          r = 0.0f;
          g = 0.0f;
          b = 0.0f;
        }
        if(r + g + b != 0.0f) {
          final float arrowPercent = (float)this.shanaArrowCount[event.player.charSlot_276] / this.shanaMaxArrowCount[event.player.charSlot_276];
          this.renderCharacterBar(event.player, r, g, b, 1.0f, true);
          this.renderCharacterBar(event.player, r, g, b, arrowPercent, false);
        }
      } else if(event.player.charId_272 == 3) {
        if(this.roseSiphon[event.player.charSlot_276] > 0) {
          final float siphonPercent = (float)this.roseSiphon[event.player.charSlot_276] / this.roseSiphonMax;
          this.renderCharacterBar(event.player, 0.0f, 0.0f, 0.5f, siphonPercent, false);
        }
        if(this.roseSiphonActivated[event.player.charSlot_276]) {
          this.renderCharacterBar(event.player, 0.0f, 0.0f, 0.5f, 1.0f, true);
        }
      } else if(event.player.charId_272 == 6) {
        if(this.meruIceShield[event.player.charSlot_276] > 0) {
          final float iceShieldPercent = (float)this.meruIceShield[event.player.charSlot_276] / this.meruIceShieldMax;
          this.renderCharacterBar(event.player, 0.0f, 0.5f, 1.0f, iceShieldPercent, false);
        }
        if(this.meruWinglyMagic[event.player.charSlot_276]) {
          this.renderCharacterBar(event.player, 0.0f, 0.5f, 1.0f, 1.0f, true);
        }
      } else if(event.player.charId_272 == 7) {
        if(this.kongolCounterStance[event.player.charSlot_276]) {
          final float counterMax = ("dragoon_modifier:giant_axe".equals(event.player.equipment_11e.get(EquipmentSlot.WEAPON).getRegistryId().toString())) ? 4 : 3;
          this.renderCharacterBar(event.player, 0.5f, 0.35f, 0.25f, this.kongolCounterStanceTurns[event.player.charSlot_276] / counterMax, false);
        }
      }

      if(this.protectionShield[event.player.charSlot_276] > 0) {
        final Battle battle = ((Battle)currentEngineState_8004dd04);
        final MV transforms = new MV();
        transforms.transfer.set(battle.hud.activePartyBattleHudCharacterDisplays_800c6c40[event.player.charSlot_276].x_08 - 49, battle.hud.activePartyBattleHudCharacterDisplays_800c6c40[event.player.charSlot_276].y_0a + 16, 1.0f);
        RENDERER.queueOrthoModel(battle.hud.battleMenu_800c6c34.menuObj, transforms, QueuedModelStandard.class)
          .vertices(battle.hud.battleMenu_800c6c34.actionIconObjOffset + 4, 4)
          .translucency(Translucency.of(battleMenuIconMetrics_800fb674[1].translucencyMode_06));

        final MV bar = new MV();
        bar.transfer.set(event.player.charSlot_276 * 94 + 30, 226.0, 999.0f);
        bar.scaling(41.0f, 13.0f, 999.0f);
        RENDERER
          .queueOrthoModel(RENDERER.opaqueQuad, bar, QueuedModelStandard.class)
          .monochrome(0.0f)
          .translucency(Translucency.HALF_B_PLUS_HALF_F);

        Scus94491BpeSegment_8002.renderText(String.valueOf(this.protectionShield[event.player.charSlot_276]), event.player.charSlot_276 * 94 + 30, 227.0f, this.fontOptions);
      }
    }
  }

  public void renderCharacterBar(final PlayerBattleEntity player, final float r, final float g, final float b, final float percent, final boolean top) {
    final MV transforms = new MV();
    final Battle battle = ((Battle)currentEngineState_8004dd04);
    final int x = battle.hud.activePartyBattleHudCharacterDisplays_800c6c40[player.charSlot_276].x_08 - 46;
    final int y = top ? (battle.hud.activePartyBattleHudCharacterDisplays_800c6c40[player.charSlot_276].y_0a - 24) : (battle.hud.activePartyBattleHudCharacterDisplays_800c6c40[player.charSlot_276].y_0a + 10);
    transforms.transfer.set(x, y, 1);
    transforms.scaling(28.0f, 2.0f, 999.0f);
    RENDERER
      .queueOrthoModel(RENDERER.opaqueQuad, transforms, QueuedModelStandard.class)
      .monochrome(0.0f)
      .translucency(Translucency.HALF_B_PLUS_HALF_F);

    final MV burnBar = new MV();
    burnBar.scaling(28.0f * percent, 2.0f, 999.0f);
    burnBar.transfer.set(x, y, 1);
    RENDERER.queueOrthoModel(RENDERER.opaqueQuad, burnBar, QueuedModelStandard.class)
      .colour(r, g, b)
      .translucency(Translucency.HALF_B_PLUS_HALF_F);
  }

  @EventListener
  public void selectedTarget(final SingleMonsterTargetEvent event) {
    if(GameEngine.CONFIG.getConfig(MONSTER_HP_BAR.get())) {
      final MV transforms = new MV();
      final VitalsStat stat = event.monster.stats.getStat(LodMod.HP_STAT.get());
      final float hp = (float)stat.getCurrent() / stat.getMax();
      final float r;
      final float g;
      final float b;
      final String text;

      if(hp <= 0.25f) {
        r = 0.85f;
        g = 0.0f;
        b = 0.0f;
      } else if(hp <= 0.5f) {
        r = 0.85f;
        g = 0.85f;
        b = 0.0f;
      } else {
        r = 0.0f;
        g = 0.0f;
        b = 0.85f;
      }

      transforms.transfer.set(41, 0, 999.0f);
      transforms.scaling(238.0f, 20.0f, 999.0f);

      if(this.enrageModeProtection[event.monster.charSlot_276] > 0) {
        text = String.valueOf(stat.getCurrent()) + " (" +  this.enrageModeProtection[event.monster.charSlot_276] + ')';
      } else {
        text = String.valueOf(stat.getCurrent());
      }

      RENDERER
        .queueOrthoModel(RENDERER.opaqueQuad, transforms, QueuedModelStandard.class)
        .monochrome(0.0f)
        .translucency(Translucency.HALF_B_PLUS_HALF_F);

      transforms.scaling(238.0f * hp, 20.0f, 1.0f);
      RENDERER
        .queueOrthoModel(RENDERER.opaqueQuad, transforms, QueuedModelStandard.class)
        .colour(r, g, b)
        .translucency(Translucency.B_PLUS_QUARTER_F);
      Scus94491BpeSegment_8002.renderText(text, 41, 5, this.fontOptions);
    }

    for(int i = 0; i < battleState_8006e398.getAllBentCount(); i++) {
      final ScriptState<? extends BattleEntity27c> state = battleState_8006e398.allBents_e0c[i];
      final BattleEntity27c bent = state.innerStruct_00;
      if(bent instanceof final PlayerBattleEntity player) {
        final Battle battle = ((Battle)currentEngineState_8004dd04);
        if(player.charId_272 == 1 || player.charId_272 == 5) {
          /*this.transforms.transfer.set(battle.hud.activePartyBattleHudCharacterDisplays_800c6c40[bent.charSlot_276].x_08 - 46, battle.hud.activePartyBattleHudCharacterDisplays_800c6c40[bent.charSlot_276].y_0a - 24, 1);
          RENDERER.queueOrthoModel(this.battleHudOverlay, this.transforms, QueuedModelStandard.class).texture(this.windMarksGfx[this.windMark[event.monster.charSlot_276]]);*/
          this.renderCharacterBar(player, 0.0f, 1.0f, 0.0f, this.windMark[event.monster.charSlot_276] == 3 ? 1.0f : this.windMark[event.monster.charSlot_276] / 3.0f, false);
        } else if(player.charId_272 == 4) {
          /*this.transforms.transfer.set(battle.hud.activePartyBattleHudCharacterDisplays_800c6c40[bent.charSlot_276].x_08 - 46, battle.hud.activePartyBattleHudCharacterDisplays_800c6c40[bent.charSlot_276].y_0a - 24, 1);
          RENDERER.queueOrthoModel(this.battleHudOverlay, this.transforms, QueuedModelStandard.class).texture(this.thunderChargesGfx[this.thunderCharge[event.monster.charSlot_276]]);*/
          this.renderCharacterBar(player, 0.5f, 0.0f, 1.0f, this.thunderCharge[event.monster.charSlot_276] == 10 ? 1.0f : this.thunderCharge[event.monster.charSlot_276] / 10.0f, false);
          if(this.staticCharge[player.charSlot_276] > 0) {
            this.renderCharacterBar(player, 0.0f, 0.8f, 1.0f, this.staticCharge[player.charSlot_276] == 20 ? 1.0f : this.staticCharge[player.charSlot_276] / 20.0f, true);
          }
        }
      }
    }
  }

  @EventListener
  public void battleEnded(final BattleEndedEvent event) {
    /*if(this.battleHudOverlay != null) {
      this.battleHudOverlay.delete();
    }*/

    final String difficulty = GameEngine.CONFIG.getConfig(DIFFICULTY.get());
    this.updateItemMagicDamage();

    if("Hell Mode".equals(difficulty) || "Hard + Hell Bosses".equals(difficulty)) {
      if(ArrayUtils.contains(this.bossEncounters, encounterId_800bb0f8)) {
        livingCharCount_800bc97c = 3;
        System.arraycopy(gameState_800babc8.charIds_88, 0, livingCharIds_800bc968, 0, 3);
      }

      //TODO: add hard
      if(encounterId_800bb0f8 == 384) {
        addAchievement(2);
      } else if(encounterId_800bb0f8 == 386) {
        addAchievement(3);
      } else if(encounterId_800bb0f8 == 414) {
        addAchievement(4);
      } else if(encounterId_800bb0f8 == 388) {
        addAchievement(5);
      } else if(encounterId_800bb0f8 == 408) {
        addAchievement(6);
      } else if(encounterId_800bb0f8 == 415) {
        addAchievement(7);
      } else if(encounterId_800bb0f8 == 487) {
        addAchievement(8);
      } else if(encounterId_800bb0f8 == 393) {
        addAchievement(9);
      } else if(encounterId_800bb0f8 == 412) {
        addAchievement(10);
      } else if(encounterId_800bb0f8 == 387) {
        addAchievement(11);
      } else if(encounterId_800bb0f8 == 389) {
        addAchievement(12);
      } else if(encounterId_800bb0f8 == 390) {
        addAchievement(13);
      } else if(encounterId_800bb0f8 == 402) {
        addAchievement(14);
      } else if(encounterId_800bb0f8 == 409) {
        addAchievement(15);
      } else if(encounterId_800bb0f8 == 403) {
        addAchievement(16);
      } else if(encounterId_800bb0f8 == 396) {
        addAchievement(17);
      } else if(encounterId_800bb0f8 == 417) {
        addAchievement(18);
      } else if(encounterId_800bb0f8 == 397) {
        addAchievement(19);
      } else if(encounterId_800bb0f8 == 410) {
        addAchievement(20);
      } else if(encounterId_800bb0f8 == 416) {
        addAchievement(21);
      } else if(encounterId_800bb0f8 == 394) {
        addAchievement(22);
      } else if(encounterId_800bb0f8 == 422) {
        addAchievement(23);
      } else if(encounterId_800bb0f8 == 418) {
        addAchievement(24);
      } else if(encounterId_800bb0f8 == 392) {
        addAchievement(25);
      } else if(encounterId_800bb0f8 == 432) {
        addAchievement(29);
      } else if(encounterId_800bb0f8 == 423) {
        addAchievement(30);
      } else if(encounterId_800bb0f8 == 430) {
        addAchievement(31);
      } else if(encounterId_800bb0f8 == 449) {
        addAchievement(33);
      } else if(encounterId_800bb0f8 == 448) {
        addAchievement(34);
      } else if(encounterId_800bb0f8 == 447) {
        addAchievement(35);
      } else if(encounterId_800bb0f8 == 431) {
        addAchievement(36);
      } else if(encounterId_800bb0f8 == 433) {
        addAchievement(37);
      } else if(encounterId_800bb0f8 == 411) {
        addAchievement(38);
      } else if(encounterId_800bb0f8 == 442) {
        addAchievement(39);
      } else if(encounterId_800bb0f8 == 400) {
        addAchievement(42);
      } else if(encounterId_800bb0f8 == 398) {
        addAchievement(43);
      } else if(encounterId_800bb0f8 == 399) {
        addAchievement(44);
      } else if(encounterId_800bb0f8 == 401) {
        addAchievement(45);
      }
    }

    if(faustBattle) {
      faustBattle = false;
      draModSave.faustDefeated += 1;
      addAchievement(40);
    }

    if(ultimateBattle) {
      ultimateBattle = false;

      if(draModSave.ultimateBossStage + 1 == GameEngine.CONFIG.getConfig(ULTIMATE_BOSS.get())) {
        draModSave.ultimateBossStage += 1;
      }

      if(draModSave.ultimateBossStage == 3) {
        GameEngine.CONFIG.setConfig(CoreMod.INVENTORY_SIZE_CONFIG.get(), 36);
      } else if(draModSave.ultimateBossStage == 8) {
        GameEngine.CONFIG.setConfig(CoreMod.INVENTORY_SIZE_CONFIG.get(), 40);
      }

      if(draModSave.ultimateBossStage == 1) {
        addAchievement(26);
      } else if(draModSave.ultimateBossStage == 3) {
        addAchievement(27);
      } else if(draModSave.ultimateBossStage == 8) {
        addAchievement(32);
      }
    }

    if(gameState_800babc8._b4 >= 400) {
      addAchievement(69);
    } else if(gameState_800babc8._b4 >= 150) {
      addAchievement(68);
    } else if(gameState_800babc8._b4 >= 50) {
      addAchievement(67);
    }

    if(GameEngine.CONFIG.getConfig(DAMAGE_TRACKER.get()) == DamageTracker.ON && !this.damageTrackerPrinted && gameState_800babc8.charIds_88[0] >= 0 && gameState_800babc8.charIds_88[1] >= 0 && gameState_800babc8.charIds_88[2] >= 0) {
      try {
        final double total = IntStream.of(this.damageTracker[0]).sum() + IntStream.of(this.damageTracker[1]).sum() + IntStream.of(this.damageTracker[2]).sum();
        final PrintWriter pw = new PrintWriter("./mods/dragoon_modifier/Damage Tracker/" + new SimpleDateFormat("yyyy-MMdd--hh-mm-ss").format(new Date()) + " - E" + encounterId_800bb0f8 + ".txt");
        pw.printf("======================================================================%n");
        pw.printf("=                           Damage Tracker                           =%n");
        pw.printf("======================================================================%n");
        pw.printf("| %-20s | %-20s | %-20s |%n", charNames[gameState_800babc8.charIds_88[0]], charNames[gameState_800babc8.charIds_88[1]], charNames[gameState_800babc8.charIds_88[2]]);
        pw.printf("----------------------------------------------------------------------%n");
        pw.printf("| %-10s %-9s | %-10s %-9s | %-10s %-9s |%n", "D.Physical", this.damageTracker[0][0], "D.Physical", this.damageTracker[1][0], "D.Physical", this.damageTracker[2][0]);
        pw.printf("| %-10s %-9s | %-10s %-9s | %-10s %-9s |%n", "D.Magical", this.damageTracker[0][1], "D.Magical", this.damageTracker[1][1], "D.Magical", this.damageTracker[2][1]);
        pw.printf("| %-10s %-9s | %-10s %-9s | %-10s %-9s |%n", "Physical", this.damageTracker[0][2], "Physical", this.damageTracker[1][2], "Physical", this.damageTracker[2][2]);
        pw.printf("| %-10s %-9s | %-10s %-9s | %-10s %-9s |%n", "Magical", this.damageTracker[0][3], "Magical", this.damageTracker[1][3], "Magical", this.damageTracker[2][3]);
        pw.printf("| %-10s %-9s | %-10s %-9s | %-10s %-9s |%n", "Total", IntStream.of(this.damageTracker[0]).sum(), "Total", IntStream.of(this.damageTracker[1]).sum(), "Total", IntStream.of(this.damageTracker[2]).sum());
        pw.printf("----------------------------------------------------------------------%n");
        pw.printf("%-13s %.2f%%%n", charNames[gameState_800babc8.charIds_88[0]], (IntStream.of(this.damageTracker[0]).sum() - this.damageTracker[0][4] * 2) / total * 100);
        pw.printf("%-13s %.2f%%%n", charNames[gameState_800babc8.charIds_88[1]], (IntStream.of(this.damageTracker[1]).sum() - this.damageTracker[1][4] * 2) / total * 100);
        pw.printf("%-13s %.2f%%%n", charNames[gameState_800babc8.charIds_88[2]], (IntStream.of(this.damageTracker[2]).sum() - this.damageTracker[2][4] * 2) / total * 100);
        pw.printf("Grand Total   " + total + "%n");
        pw.printf("Encounter     " + encounterId_800bb0f8 + "%n%n");
        pw.printf("===========================================================================================================%n");
        pw.printf("=                                                Equipment                                                =%n");
        pw.printf("===========================================================================================================%n");
        pw.printf("| Name     | Weapon           | Helmet           | Armor            | Shoes            | Accessory        |%n");
        pw.printf("-----------------------------------------------------------------------------------------------------------%n");
        for(int i = 0; i < this.damageTrackerEquips.length; i++) {
          pw.printf("| %-8s | %-16s | %-16s | %-16s | %-16s | %-16s |%n", charNames[gameState_800babc8.charIds_88[i]], I18n.translate(this.damageTrackerEquips[i][0]), I18n.translate(this.damageTrackerEquips[i][1]), I18n.translate(this.damageTrackerEquips[i][2]), I18n.translate(this.damageTrackerEquips[i][3]), I18n.translate(this.damageTrackerEquips[i][4]));
        }
        pw.printf("===========================================================================================================%n%n");
        for(final String s : this.damageTrackerLog) {
          pw.printf(s + "%n");
        }
        pw.flush();
        pw.close();
        this.damageTrackerPrinted = true;
      } catch(final FileNotFoundException e) {
        throw new RuntimeException(e);
      }
    }
  }

  public void displayNumbers(final int scriptIndex, final int damage, final int colour, final int delay) {
    new Thread(() -> {
      try {
        Thread.sleep(delay);
      } catch(final InterruptedException e) {
        throw new RuntimeException(e);
      }

      try {
        ((Battle)currentEngineState_8004dd04).hud.addFloatingNumberForBent(scriptIndex, damage, colour);
      } catch(final Exception ignored) {
      }
    }).start();
  }

  public void recaluteBentDragoonTurns(final PlayerBattleEntity bent) {
    if(bent.isDragoon()) {
      final int sp = bent.stats.getStat(SP_STAT.get()).getCurrent();
      battleState_8006e398.dragoonTurnsRemaining_294[bent.charSlot_276] = (int)Math.floor(sp / 100d);
    }
  }

  @EventListener
  public void combatBar(final CombatMenuEvent event) {
    if(event.bent.isDragoon() && event.bent.dlevel_06 >= 6) {
      event.combatBar += 2;
    }

    if(this.kongolCounterStance[event.bent.charSlot_276]) {
      event.combatBar = event.bent.isDragoon() ? (event.bent.dlevel_06 >= 6) ? 0x32 : 0x30 : 0x1;
    }
  }

  @EventListener
  public void combatBarBlocked(final CombatMenuBlockedEvent event) {
    this.currentMenuBlock = event.combatBarBlocked;
  }

  @EventListener
  public void isDeffArrow(final DeffArrowEvent event) {
    if("dragoon_modifier:fire_arrow".equals(event.registryId.toString()) ||
      "dragoon_modifier:water_arrow".equals(event.registryId.toString()) ||
      "dragoon_modifier:wind_arrow".equals(event.registryId.toString()) ||
      "dragoon_modifier:earth_arrow".equals(event.registryId.toString()) ||
      "dragoon_modifier:dark_arrow".equals(event.registryId.toString()) ||
      "dragoon_modifier:light_arrow".equals(event.registryId.toString()) ||
      "dragoon_modifier:thunder_arrow".equals(event.registryId.toString())) {
      event.isDeff = true;
      this.shanaDeffArrow = true;
      this.isItemArrow = true;
    }
  }

  @EventListener
  public void loadDeff(final ScriptLoadDeffEvent event) {
    if(this.shanaDeffArrow) {
      this.print("---SHANA DEFF ARROW---");
      this.print("FLAGS " + event.flagsAndIndex);
      this.print("BENT  " + event.bentIndex);
      this.print("P2    " + event.p2);
      this.print("SCREP " + event.scriptEntrypoint);
      this.print("TYPE  " + event.type);

      if(event.type == 0x2000000) {
        final PlayerBattleEntity bent = (PlayerBattleEntity)scriptStatePtrArr_800bc1c0[event.bentIndex].innerStruct_00;
        if(bent.charId_272 == 2 || bent.charId_272 == 8) {
          if("dragoon_modifier:fire_arrow".equals(bent.equipment_11e.get(EquipmentSlot.WEAPON).getRegistryId().toString()) ||
            "dragoon_modifier:water_arrow".equals(bent.equipment_11e.get(EquipmentSlot.WEAPON).getRegistryId().toString()) ||
            "dragoon_modifier:wind_arrow".equals(bent.equipment_11e.get(EquipmentSlot.WEAPON).getRegistryId().toString()) ||
            "dragoon_modifier:earth_arrow".equals(bent.equipment_11e.get(EquipmentSlot.WEAPON).getRegistryId().toString()) ||
            "dragoon_modifier:dark_arrow".equals(bent.equipment_11e.get(EquipmentSlot.WEAPON).getRegistryId().toString()) ||
            "dragoon_modifier:light_arrow".equals(bent.equipment_11e.get(EquipmentSlot.WEAPON).getRegistryId().toString()) ||
            "dragoon_modifier:thunder_arrow".equals(bent.equipment_11e.get(EquipmentSlot.WEAPON).getRegistryId().toString())) {
            event.scriptEntrypoint = 0x0;
            this.shanaDeffArrow = false;
            this.isItemArrow = true;

            if("dragoon_modifier:fire_arrow".equals(bent.equipment_11e.get(EquipmentSlot.WEAPON).getRegistryId().toString())) {
              event.flagsAndIndex = 0xC3;
            } else if("dragoon_modifier:water_arrow".equals(bent.equipment_11e.get(EquipmentSlot.WEAPON).getRegistryId().toString())) {
              event.flagsAndIndex = 0xC6;
            } else if("dragoon_modifier:wind_arrow".equals(bent.equipment_11e.get(EquipmentSlot.WEAPON).getRegistryId().toString())) {
              event.flagsAndIndex = 0xC7;
            } else if("dragoon_modifier:earth_arrow".equals(bent.equipment_11e.get(EquipmentSlot.WEAPON).getRegistryId().toString())) {
              event.flagsAndIndex = 0xC5;
            } else if("dragoon_modifier:dark_arrow".equals(bent.equipment_11e.get(EquipmentSlot.WEAPON).getRegistryId().toString())) {
              event.flagsAndIndex = 0xCA;
            } else if("dragoon_modifier:light_arrow".equals(bent.equipment_11e.get(EquipmentSlot.WEAPON).getRegistryId().toString())) {
              event.flagsAndIndex = 0xC9;
            } else if("dragoon_modifier:thunder_arrow".equals(bent.equipment_11e.get(EquipmentSlot.WEAPON).getRegistryId().toString())) {
              event.flagsAndIndex = 0xC2;
            }
          }
        }
      } else {
        this.shanaDeffArrow = false;
        this.isItemArrow = false;
      }
    }
  }

  @EventListener
  public void guardHealEvent(final GuardHealEvent event) {
    final String difficulty = GameEngine.CONFIG.getConfig(DIFFICULTY.get());
    if("Hell Mode".equals(difficulty) || "Hard + Hell Bosses".equals(difficulty)) {
      if(this.currentPlayerId == 6) {
        if(this.meruWinglyMagic[this.currentPlayerSlot]) {
          event.heal = (int) Math.round(event.heal * 1.5d);
        } else {
          event.heal = (int) Math.round(event.heal * 0.8d);
        }
      } else {
        event.heal = (int) Math.round(event.heal * 0.8d);
      }
    } else if("Hard Mode".equals(difficulty) || "US + Hard Bosses".equals(difficulty)) {
      if(this.currentPlayerId == 6) {
        if(this.meruWinglyMagic[this.currentPlayerSlot]) {
          event.heal = (int) Math.round(event.heal * 1.8d);
        }
      }
    }

    if("dragoon_modifier:fake_solitary_ring".equals(this.currentPlayer.equipment_11e.get(EquipmentSlot.ACCESSORY).getRegistryId().toString())) {
      event.heal = (int) Math.round(event.heal * 1.25d);
    }

    if("dragoon_modifier:solitary_ring".equals(this.currentPlayer.equipment_11e.get(EquipmentSlot.ACCESSORY).getRegistryId().toString())) {
      event.heal = (int) Math.round(event.heal * 1.5d);
    }
  }

  @EventListener
  public void modMenuEvent(final ModMenuEvent event) {
    final String menuAction = event.modMenuId.toString();
    if("dragoon_modifier:burn_stacks".equals(menuAction)) {
      this.burnStackMode[this.currentPlayerSlot] = this.burnStacks[this.currentPlayerSlot] > 0 && !this.burnStackMode[this.currentPlayerSlot];
    } else if("dragoon_modifier:wind_barrier".equals(menuAction)) {
      final int currentMP = this.currentPlayer.getStat(BattleEntityStat.CURRENT_MP);
      if(!this.windBarrier[this.currentPlayerSlot]) {
        if(currentMP >= 10) {
          this.currentPlayer.setStat(BattleEntityStat.CURRENT_MP, currentMP - 10);
          this.windBarrier[this.currentPlayerSlot] = true;
        }
      } else {
        this.currentPlayer.setStat(BattleEntityStat.CURRENT_MP, currentMP + 10);
        this.windBarrier[this.currentPlayerSlot] = false;
      }
    } else if("dragoon_modifier:arrow_swap".equals(menuAction)) {
      final ListMenu menu = ((Battle)currentEngineState_8004dd04).hud.listMenu_800c6b60;
      final BattleHud hud = ((Battle)currentEngineState_8004dd04).hud;
      if(menu == null) {
        hud.listMenu_800c6b60 = new ElementalQuiver(hud, this.currentPlayer, new ListPosition(), hud::onListClose);
      }
    } else if("dragoon_modifier:siphon_overflow".equals(menuAction)) {
      this.roseSiphonActivated[this.currentPlayerSlot] = this.roseSiphon[this.currentPlayerSlot] == this.roseSiphonMax;
    } else if("dragoon_modifier:static_charge".equals(menuAction)) {
      int allStaticCharges = 0;
      for(int i = 0; i < battleState_8006e398.getAllBentCount(); i++) {
        final ScriptState<? extends BattleEntity27c> state = battleState_8006e398.allBents_e0c[i];
        final BattleEntity27c bobj = state.innerStruct_00;
        if(bobj instanceof final MonsterBattleEntity monster) {
          if(this.thunderCharge[monster.charSlot_276] > 0) {
            allStaticCharges += this.thunderCharge[monster.charSlot_276];
            this.thunderCharge[monster.charSlot_276] = 0;
          }
        }
      }

      if(allStaticCharges > 20) {
        allStaticCharges = 20;
      }

      this.staticCharge[this.currentPlayerSlot] = allStaticCharges;
    } else if("dragoon_modifier:magic_state".equals(menuAction)) {
      final int currentMP = this.currentPlayer.getStat(BattleEntityStat.CURRENT_MP);
      if(!this.meruWinglyMagic[this.currentPlayerSlot]) {
        if(currentMP >= 10) {
          this.currentPlayer.setStat(BattleEntityStat.CURRENT_MP, currentMP - 10);
          this.meruWinglyMagic[this.currentPlayerSlot] = true;
        }
      } else {
        this.currentPlayer.setStat(BattleEntityStat.CURRENT_MP, currentMP + 10);
        this.meruWinglyMagic[this.currentPlayerSlot] = false;
      }
    } else if("dragoon_modifier:counter_stance".equals(menuAction)) {
      if((this.currentPlayer.status_0e == 0 || this.currentPlayer.status_0e == 8192) && (this.currentMenuBlock == 0 || this.currentMenuBlock == 8) && !this.kongolCounterStance[this.currentPlayerSlot]) {
        this.kongolCounterStance[this.currentPlayerSlot] = true;
        this.kongolCounterStanceTurns[this.currentPlayerSlot] = ("dragoon_modifier:giant_axe".equals(this.currentPlayer.equipment_11e.get(EquipmentSlot.WEAPON).getRegistryId().toString())) ? 4 : 3;
        this.kongolCounterSpeed = this.currentPlayer.stats.getStat(SPEED_STAT.get()).get();
        this.currentPlayer.stats.getStat(SPEED_STAT.get()).setRaw(0);
        this.currentPlayer.turnValue_4c = 0;
        ((Battle)currentEngineState_8004dd04).hud.initializeMenuIcons(battleState_8006e398.playerBents_e40[this.currentPlayerSlot], this.currentPlayer.isDragoon() ? this.currentPlayer.dlevel_06 >= 6 ? 0x32 : 0x30 : 0x2, 0);
      }
    }
  }
  //endregion

  //region Menu Events
  @EventListener
  public void xpToLevel(final XpToLevelEvent event) {
    //event.xp = Integer.parseInt(xpNextStats.get(event.charId * (maxCharacterLevel + 1) + event.level)[0]);
  }

  public boolean checkAchievements(final int id) {
    return draModSave.enhancedAchievements.containsKey(id);
  }

  public static void addAchievement(final int id) {
    if(!draModSave.enhancedAchievements.containsKey(id)) {
      draModSave.enhancedAchievements.put(id, true);
      notifyAchievement(id);
    }
  }

  public static void notifyAchievement(final int id) {
    final DraModAchievements.Enhanced achievement = DraModAchievements.Enhanced.getById(id);
    if(engineState_8004dd20 == EngineStateEnum.COMBAT_06) {
      battleNotification(achievement.getType().ordinal() + 1, achievement.getName() + " - " + achievement.getDesc());
    } else {
      notification(achievement.getType().ordinal() + 1, achievement.getName() + " - " + achievement.getDesc());
    }
  }

  public static void notification(final int type, final String message) {
    try {
      final DraModNotification notification = new DraModNotification(type, message);
      menuStack.pushScreen(notification);
      draNotificationOpen = true;

      new Thread(() -> {
        try {
          while(engineState_8004dd20 != EngineStateEnum.SUBMAP_05) {
            Thread.sleep(1000);
          }
          Thread.sleep(5000);
          draNotificationOpen = false;

          menuStack.reset();

        } catch(final Exception ignored) {
          draNotificationOpen = false;
          menuStack.reset();
        }
      }).start();
    } catch(final Exception ignored) {
      draNotificationOpen = false;
      menuStack.reset();
    }
  }

  public static void battleNotification(final int type, final String message) {
    try {
        final DraModNotification notification = new DraModNotification(type, message);
        menuStack.pushScreen(notification);
        draNotificationOpen = true;

        new Thread(() -> {
          try {
            Thread.sleep(5000);

            menuStack.reset();

            draNotificationOpen = false;
          } catch(final Exception ignored) {
            menuStack.reset();
            draNotificationOpen = false;
          }
        }).start();
    } catch(final Exception ignored) {
      menuStack.reset();
      draNotificationOpen = false;
    }
  }
  //endregion

  //region Ultimate
  public void ultimateGuardBreak(final PlayerBattleEntity player, final MonsterBattleEntity monster, final AttackEvent attack) {
    final int encounterId = encounterId_800bb0f8;

    if(encounterId == 415) {
      if(monster.spellId_4e == 117) {
        player.guard_54 = 0;
      }
    }
  }

  public void ultimateMPAttack(final PlayerBattleEntity player, final MonsterBattleEntity monster, final AttackEvent attack) {
    final int encounterId = encounterId_800bb0f8;

    if(attack.damage > 0) {
      if(encounterId == 415) {
        final SpellStats0c spell = spellStats_800fa0b8_Monster[monster.spellId_4e];
        if(monster.spellId_4e == 38) {
          player.stats.getStat(MP_STAT.get()).setCurrent(Math.max(0, player.stats.getStat(MP_STAT.get()).getCurrent() - 10));
        }
      }
    }
  }
  //endregion

  //region Scripting
  @EventListener
  public void DrgnFileEvent(final DrgnFileEvent event) {
    this.print("[FILE] " + event.location + " - " + event.deffPath);


    if(!event.loadDeff) {
      if(ultimateBattle && event.location.startsWith("SECT/DRGN1.BIN")) {
        final Path ultimateMonster = Loader.resolveMods("dragoon_modifier/scripts/mobs/" + event.location.split("/")[2]);

        if(Files.exists(ultimateMonster)) {
          this.print("[OVERRIDE] " + ultimateMonster);
          event.overrideLoad = true;
          Loader.loadFile(ultimateMonster, event.onCompletion);
        }
      }
    } else {
      if(event.deffPath.toString().contains("DRGN0.BIN\\5216\\1") || event.deffPath.toString().contains("DRGN0.BIN/5216/1") ) {
        final Path attack = Loader.resolveMods("dragoon_modifier/scripts/mobs/5216.1");
        if(Files.exists(attack)) {
          try {
            this.print("[OVERRIDE] 5216.1");
            event.fileData = Files.readAllBytes(attack);
          } catch(final IOException ignored) {}
        }
      }
    }
  }

  @EventListener
  public void SubmapLoadEvent(final SubmapLoadEvent event) {
    /*File file = new File("scriptDump\\" + submapCut_80052c30 + "mainScript");
    try {
      file.createNewFile();
    } catch(IOException ex) { System.err.println("Error: " + ex.getMessage()); ex.printStackTrace(); }

    try(FileOutputStream fos = new FileOutputStream(file)) {
      fos.write(event.submapScript.data);
    } catch (IOException ex) { System.err.println("Error: " + ex.getMessage()); ex.printStackTrace(); }

    for(int i = 0; i < event.submapObjects.size(); i++) {
      file = new File("scriptDump\\" + submapCut_80052c30 + "objectScript" +  "-" + i);
      try {
        file.createNewFile();
      } catch(IOException ex) { System.err.println("Error: " + ex.getMessage()); ex.printStackTrace(); }
      try(FileOutputStream fos = new FileOutputStream(file)) {
        fos.write(event.submapObjects.get(i).script.data);
      } catch (IOException ex) { System.err.println("Error: " + ex.getMessage()); ex.printStackTrace(); }
    }*/

    if(submapCut_80052c30 == 177) {
      if(isBitSet(3, 0)) {
        new Thread(() -> {
          try {
            Thread.sleep(50);
            ((SMap)currentEngineState_8004dd04).sobjs_800c6880[2].innerStruct_00.model_00.coord2_14.coord.transfer.y = 10000;
          } catch(InterruptedException e) {
            throw new RuntimeException(e);
          }
        }).start();
      }
    } else if(submapCut_80052c30 == 181) {
      if(isBitSet(3, 0)) {
        new Thread(() -> {
          try {
            Thread.sleep(50);
            ((SMap)currentEngineState_8004dd04).sobjs_800c6880[5].innerStruct_00.model_00.coord2_14.coord.transfer.y = 10000;
          } catch(InterruptedException e) {
            throw new RuntimeException(e);
          }
        }).start();
      }
    } else if(submapCut_80052c30 == 195) {
      if(isBitSet(3, 0)) {
        event.submapObjects.add(event.submapObjects.get(2));

        final Path player = Loader.resolveMods("dragoon_modifier/scripts/npcs/195.0");
        try {
          if(Files.exists(player)) {
            event.submapObjects.getFirst().script = new ScriptFile("Player", Files.readAllBytes(player));
          }
        } catch(IOException e) {
          throw new RuntimeException(e);
        }

        new Thread(() -> {
          try {
            Thread.sleep(50);
            ((SMap)currentEngineState_8004dd04).sobjs_800c6880[10].innerStruct_00.model_00.coord2_14.coord.transfer.x = -425;
            ((SMap)currentEngineState_8004dd04).sobjs_800c6880[10].innerStruct_00.model_00.coord2_14.coord.transfer.y = 209;
            ((SMap)currentEngineState_8004dd04).sobjs_800c6880[10].innerStruct_00.model_00.coord2_14.coord.transfer.z = 19;
            ((SMap)currentEngineState_8004dd04).sobjs_800c6880[10].innerStruct_00.model_00.coord2_14.transforms.rotate.x = 0;
            ((SMap)currentEngineState_8004dd04).sobjs_800c6880[10].innerStruct_00.model_00.coord2_14.transforms.rotate.y = -1.1f;
            ((SMap)currentEngineState_8004dd04).sobjs_800c6880[10].innerStruct_00.model_00.coord2_14.transforms.rotate.z = 0;
          } catch(InterruptedException e) {
            throw new RuntimeException(e);
          }
        }).start();
      }
    }



  }
  //endregion

  //region Hotkey
  @EventListener
  public void inputPressed(final InputPressedEvent event) {
    this.print("[HOTKEY] " + event.action.toString());
    this.hotkey.add(event.action);
    this.dramodHotkeys();
  }

  @EventListener
  public void inputReleased(final InputReleasedEvent event) {
    this.hotkey.remove(event.action);
  }

  public void dramodHotkeys() {
    if(this.hotkey.contains(INPUT_ACTION_DRAMENU.get())) {
      if(draAchievementsOpen) {
        return;
      }
      if(!draMenuOpen) {
        if(engineState_8004dd20 == EngineStateEnum.SUBMAP_05) {
          draMenuMessage = "";
          SCRIPTS.pause();
          final DraMenu menu = new DraMenu();


          int ultimateBossSelected = GameEngine.CONFIG.getConfig(ULTIMATE_BOSS.get());
          if(ultimateBossSelected > draModSave.ultimateBossStage + 1) {
            ultimateBossSelected = draModSave.ultimateBossStage + 1;
            GameEngine.CONFIG.setConfig(DragoonModifier.ULTIMATE_BOSS.get(), ultimateBossSelected);
          }

          menu.setUltimateBossDefeated(draModSave.ultimateBossStage);
          menu.setUltimateBossStage(ultimateBossSelected);
          this.menuStack.pushScreen(menu);
          playMenuSound(2);
          draMenuOpen = true;
        }
      } else {
        this.menuStack.reset();
        playMenuSound(3);
        draMenuOpen = false;
        SCRIPTS.resume();
        addAchievement(1);
      }
    } else if(this.hotkey.contains(INPUT_ACTION_DRAACHIEVEMENTS.get())) {
      if(draMenuOpen) {
        return;
      }
      if(!draAchievementsOpen) {
        if(engineState_8004dd20 == EngineStateEnum.SUBMAP_05) {
          SCRIPTS.pause();
          final DraModAchievementsScreen menu = new DraModAchievementsScreen(true); //TODO add difficulty switch here
          this.menuStack.pushScreen(menu);
          playMenuSound(2);
          draAchievementsOpen = true;
        }
      } else {
        this.menuStack.reset();
        playMenuSound(3);
        draAchievementsOpen = false;
        SCRIPTS.resume();
        addAchievement(0);
      }
    }
  }

  public static void levelSync() {
    final String difficulty = GameEngine.CONFIG.getConfig(DIFFICULTY.get());
    int highestInPartyEXP = 0;
    for(int i = 0; i < 9; i++) {
      if(gameState_800babc8.charData_32c[i].partyFlags_04 > 0 && gameState_800babc8.charData_32c[i].xp_00 > highestInPartyEXP) {
        highestInPartyEXP = gameState_800babc8.charData_32c[i].xp_00;
      }
    }

    if("Hard Mode".equals(difficulty) || "Us + Hard Bosses".equals(difficulty)) {
      if(highestInPartyEXP > 80000) {
        highestInPartyEXP = 80000;
      }
    }

    if("Hell Mode".equals(difficulty) || "Hard + Hell Bosses".equals(difficulty)) {
      if(highestInPartyEXP > 160000) {
        highestInPartyEXP = 160000;
      }
    }

    for(int i = 0; i < 9; i++) {
      if(gameState_800babc8.charData_32c[i].partyFlags_04 > 0) {
        while (highestInPartyEXP > getXpToNextLevel(i)) {
          gameState_800babc8.charData_32c[i].level_12++;
        }
      }
    }

    draMenuMessage = "Level synced up to " + highestInPartyEXP + " EXP";
  }

  public static void addLavitz() {
    gameState_800babc8.charData_32c[1].partyFlags_04 = gameState_800babc8.charData_32c[1].partyFlags_04 != 0 ? 0x43 : 0;
    if(gameState_800babc8.charData_32c[1].partyFlags_04 != 0) {
      draMenuMessage = "Lavitz added.";
    } else {
      draMenuMessage = "Lavitz removed.";
    }
  }

  public static void addShana() {
    gameState_800babc8.charData_32c[2].partyFlags_04 = gameState_800babc8.charData_32c[2].partyFlags_04 == 0 ? 3 : 0;
    if(gameState_800babc8.charData_32c[2].partyFlags_04 == 3) {
      draMenuMessage = "Shana added.";
    } else {
      draMenuMessage = "Shana removed.";
    }
  }

  public static void addLv1Characters() {
    for(int i = 0; i < 9; i++) {
      gameState_800babc8.charData_32c[i].partyFlags_04 = 3;
      gameState_800babc8.charData_32c[i].dlevel_13 = 1;
      gameState_800babc8.charData_32c[i].level_12 = 1;
      gameState_800babc8.charData_32c[i].xp_00 = 0;
      gameState_800babc8.charData_32c[i].equipment_14.put(EquipmentSlot.WEAPON, REGISTRIES.equipment.getEntry("lod:broad_sword").get());
      gameState_800babc8.charData_32c[i].equipment_14.put(EquipmentSlot.HELMET, REGISTRIES.equipment.getEntry("lod:bandana").get());
      gameState_800babc8.charData_32c[i].equipment_14.put(EquipmentSlot.ARMOUR, REGISTRIES.equipment.getEntry("lod:leather_armor").get());
      gameState_800babc8.charData_32c[i].equipment_14.put(EquipmentSlot.BOOTS, REGISTRIES.equipment.getEntry("lod:leather_boots").get());
    }
    draMenuMessage = "Characters set to level 1.";
  }

  public static void addDragoons() {
    gameState_800babc8.goods_19c[0] ^= 1 << 0;
    gameState_800babc8.goods_19c[0] ^= 1 << 1;
    gameState_800babc8.goods_19c[0] ^= 1 << 2;
    gameState_800babc8.goods_19c[0] ^= 1 << 3;
    gameState_800babc8.goods_19c[0] ^= 1 << 4;
    gameState_800babc8.goods_19c[0] ^= 1 << 5;
    gameState_800babc8.goods_19c[0] ^= 1 << 6;
    draMenuMessage = "Dragoons swapped.";
  }

  public static void addDartDragoon() {
    gameState_800babc8.goods_19c[0] ^= 1 << 0;
    draMenuMessage = "Red-Eyed spirit swapped.";
  }

  public static void swapRedEyedAndDivineSpirit() {
    final String difficulty = GameEngine.CONFIG.getConfig(DIFFICULTY.get());
    if("Hell Mode".equals(difficulty) || "Hard + Hell Bosses".equals(difficulty)) {
      //if(draModSave.ultimateBossStage >= 34) {
        gameState_800babc8.goods_19c[0] ^= 1 << 7;
        if(submapCut_80052c30 == 736) {
          gameState_800babc8.goods_19c[0] |= 1 << 0;
        }
        draMenuMessage = "Spirit swapped.";
      /*} else {
        draMenuMessage = "Ultimate Boss #34 not defeated.";
      }*/
    } else {
      gameState_800babc8.goods_19c[0] ^= 1 << 7;
      if(submapCut_80052c30 == 736) {
        gameState_800babc8.goods_19c[0] |= 1 << 0;
      }
      draMenuMessage = "Spirit swapped.";
    }
  }

  public static void warpToMoon() {
    submapCut_80052c30 = 730;
    ((SMap)currentEngineState_8004dd04).smapLoadingStage_800cb430 = SubmapState.CHANGE_SUBMAP_4;
  }

  public static void warpToUlara() {
    submapCut_80052c30 = 524;
    ((SMap)currentEngineState_8004dd04).smapLoadingStage_800cb430 = SubmapState.CHANGE_SUBMAP_4;
    addAchievement(41);
  }

  public static void startFaustBattle() {
    new Thread(() -> {
      while(draMenuOpen) {
        try {
          Thread.sleep(20);
        } catch(InterruptedException e) {
          throw new RuntimeException(e);
        }
      }

      if(currentEngineState_8004dd04 instanceof final SMap smap) {
        faustBattle = true;
        smap.submap.prepareEncounter(420, false);
        smap.mapTransition(-1, 0);
        battleStage_800bb0f4 = 78;
      }
    }).start();
  }

  public static void addAllPartyMembers() {
    for(int i = 0; i < 9; i++) {
      gameState_800babc8.charData_32c[i].partyFlags_04 = 0x43;
    }
  }

  public static void who() {
    gameState_800babc8.charData_32c[8].partyFlags_04 = 0;
    draMenuMessage = "???";
  }

  public static void removeSecondSlot() {
    gameState_800babc8.charIds_88[1] = -1;
  }

  public static void removeThirdSlot() {
    gameState_800babc8.charIds_88[2] = -1;
  }

  public static void forbiddenLandShop() {
    new Thread(() -> {
      while(draMenuOpen) {
        try {
          Thread.sleep(20);
        } catch(InterruptedException e) {
          throw new RuntimeException(e);
        }
      }

      if(currentEngineState_8004dd04 instanceof final SMap smap) {
        shopId_8007a3b4 = 19;
        smap.mapTransition(-1, 0x3fe);
      }
    }).start();
  }

  public static void rogueShop() {
    new Thread(() -> {
      while(draMenuOpen) {
        try {
          Thread.sleep(20);
        } catch(InterruptedException e) {
          throw new RuntimeException(e);
        }
      }

      if(currentEngineState_8004dd04 instanceof final SMap smap) {
        shopId_8007a3b4 = 25;
        smap.mapTransition(-1, 0x3fe);
      }
    }).start();
  }

  public static void heroCompetitionShop() {
    new Thread(() -> {
      while(draMenuOpen) {
        try {
          Thread.sleep(20);
        } catch(InterruptedException e) {
          throw new RuntimeException(e);
        }
      }

      if(currentEngineState_8004dd04 instanceof final SMap smap) {
        shopId_8007a3b4 = 40;
        smap.mapTransition(-1, 0x3fe);
      }
    }).start();
  }

  public static void elementalBombShop() {
    new Thread(() -> {
      while(draMenuOpen) {
        try {
          Thread.sleep(20);
        } catch(InterruptedException e) {
          throw new RuntimeException(e);
        }
      }

      if(currentEngineState_8004dd04 instanceof final SMap smap) {
        shopId_8007a3b4 = 41;
        smap.mapTransition(-1, 0x3fe);
      }
    }).start();
  }

  public static void nextUltimateBoss() {
    final String difficulty = GameEngine.CONFIG.getConfig(DIFFICULTY.get());
    if("Hard Mode".equals(difficulty) || "US + Hard Bosses".equals(difficulty) || "Hell Mode".equals(difficulty) || "Hard + Hell Bosses".equals(difficulty)) {
      new Thread(() -> {
        while(draMenuOpen) {
          try {
            Thread.sleep(20);
          } catch(InterruptedException e) {
            throw new RuntimeException(e);
          }
        }

        if(currentEngineState_8004dd04 instanceof final SMap smap) {
          ultimateBattle = true;
          CONFIG.setConfig(ULTIMATE_BOSS.get(), draModSave.ultimateBossStage + 1);
          setUltimateLevelCap(GameEngine.CONFIG.getConfig(ULTIMATE_BOSS.get()) - 1);
          smap.submap.prepareEncounter(ultimateEncounter[GameEngine.CONFIG.getConfig(ULTIMATE_BOSS.get()) - 1][0], false);
          battleStage_800bb0f4 = ultimateEncounter[GameEngine.CONFIG.getConfig(ULTIMATE_BOSS.get()) - 1][1];
          smap.mapTransition(-1, 0);
        }
      }).start();
    } else {
      draMenuMessage = "Ultimate Boss is only for Hard/Hell modes.";
    }
  }

  public static void startUltimateBoss() {
    final String difficulty = GameEngine.CONFIG.getConfig(DIFFICULTY.get());
    if("Hard Mode".equals(difficulty) || "US + Hard Bosses".equals(difficulty) || "Hell Mode".equals(difficulty) || "Hard + Hell Bosses".equals(difficulty)) {
      new Thread(() -> {
        while(draMenuOpen) {
          try {
            Thread.sleep(20);
          } catch(InterruptedException e) {
            throw new RuntimeException(e);
          }
        }

        if(currentEngineState_8004dd04 instanceof final SMap smap) {
          int bossSelected = GameEngine.CONFIG.getConfig(ULTIMATE_BOSS.get()) - 1;
          if(bossSelected < 0) {
            bossSelected = 0;
          }
          ultimateBattle = true;
          setUltimateLevelCap(bossSelected);
          smap.submap.prepareEncounter(ultimateEncounter[bossSelected][0], false);
          battleStage_800bb0f4 = ultimateEncounter[bossSelected][1];
          smap.mapTransition(-1, 0);
        }
      }).start();
    } else {
      draMenuMessage = "Ultimate Boss is only for Hard/Hell modes.";
    }
  }

  public static void setUltimateLevelCap(final int bossSelected) {
    if(bossSelected >= 22) {
      ultimateLevelCap = 60;
    } else if(bossSelected >= 8) {
      ultimateLevelCap = 50;
    } else if(bossSelected >= 3) {
      ultimateLevelCap = 40;
    } else {
      ultimateLevelCap = 30;
    }
  }

  @EventListener
  public void RenderMenu(final RenderEvent event) {
    if(draMenuOpen) {
      menuStack.render();
    } else {
      if(draAchievementsOpen) {
        menuStack.render();
      } else {
        if(draNotificationOpen) {
          menuStack.render();
        }
      }
    }
  }

  @EventListener
  public void registerInputActions(final InputActionRegistryEvent event) {
    DRAMOD_INPUT_REGISTRAR.registryEvent(event);
  }

  @EventListener
  public void registerInput(final RegisterDefaultInputBindingsEvent event) {
    event.add(INPUT_ACTION_DRAMENU.get(), new ScancodeInputActivation(InputKey.C));
    event.add(INPUT_ACTION_DRAMENU.get(), new ButtonInputActivation(InputButton.START));
    event.add(INPUT_ACTION_DRAACHIEVEMENTS.get(), new ScancodeInputActivation(InputKey.V));
    event.add(INPUT_ACTION_DRAACHIEVEMENTS.get(), new ButtonInputActivation(InputButton.SELECT));
  }
  //endregion

  public static boolean isBitSet(final int index, final int bit) {
    return (gameState_800babc8.scriptFlags2_bc.getRaw(index) & bit) != 1;
  }
}
