package lod.dragoonmodifier;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import legend.core.GameEngine;
import legend.core.gte.MV;
import legend.core.lang.RawText;
import legend.core.renderer.Obj;
import legend.core.renderer.QueuedModelStandard;
import legend.core.renderer.Texture;
import legend.core.renderer.Translucency;
import legend.game.additions.AdditionRegistryEvent;
import legend.game.characters.CharacterData2c;
import legend.game.characters.CharacterTemplate;
import legend.game.characters.Element;
import legend.game.characters.ElementSet;
import legend.game.characters.FractionalStatModConfig;
import legend.game.characters.RegisterCharacterTemplatesEvent;
import legend.game.characters.StatType;
import legend.game.characters.StatTypeRegistryEvent;
import legend.game.characters.UnaryStat;
import legend.game.characters.UnaryStatModConfig;
import legend.game.characters.UnaryStatType;
import legend.game.characters.VitalsStat;
import legend.game.combat.Battle;
import legend.game.combat.MonsterSpellRegistryEvent;
import legend.game.combat.bent.AttackEvent;
import legend.game.combat.bent.BattleEntity27c;
import legend.game.combat.bent.BattleEntityStat;
import legend.game.combat.bent.MonsterBattleEntity;
import legend.game.combat.bent.PlayerBattleEntity;
import legend.game.combat.deff.RegisterDeffsEvent;
import legend.game.combat.types.AttackType;
import legend.game.combat.types.CombatantStruct1a8;
import legend.game.combat.ui.BattleAction;
import legend.game.combat.ui.BattleMenuStruct58;
import legend.game.combat.ui.GatherBattleActionsEvent;
import legend.game.combat.ui.RegisterBattleActionsEvent;
import legend.game.i18n.I18n;
import legend.game.inventory.Equipment;
import legend.game.inventory.EquipmentRegistryEvent;
import legend.game.inventory.EquipmentTypes;
import legend.game.inventory.GatherCharacterEquipmentTypesEvent;
import legend.game.inventory.GatherEquipmentTypesEvent;
import legend.game.inventory.Item;
import legend.game.inventory.ItemIcon;
import legend.game.inventory.ItemRegistryEvent;
import legend.game.inventory.ItemStack;
import legend.game.inventory.SpellRegistryEvent;
import legend.game.inventory.SpellStats0c;
import legend.game.inventory.screens.FontOptions;
import legend.game.inventory.screens.ShopScreen;
import legend.game.inventory.screens.TextColour;
import legend.game.modding.coremod.CoreMod;
import legend.game.modding.events.RenderEvent;
import legend.game.modding.events.battle.ActiveItemEvent;
import legend.game.modding.events.battle.ActiveSpellEvent;
import legend.game.modding.events.battle.BattleEndedEvent;
import legend.game.modding.events.battle.BattleEntityTurnEvent;
import legend.game.modding.events.battle.BattleStartedEvent;
import legend.game.modding.events.battle.DragonBlockStaffOffEvent;
import legend.game.modding.events.battle.DragonBlockStaffOnEvent;
import legend.game.modding.events.battle.EnemyRewardsEvent;
import legend.game.modding.events.battle.MonsterStatsEvent;
import legend.game.modding.events.battle.RegisterBattleEntityStatsEvent;
import legend.game.modding.events.battle.SpellStatsEvent;
import legend.game.modding.events.battle.StatDisplayEvent;
import legend.game.modding.events.config.ConfigLoadedEvent;
import legend.game.modding.events.gamestate.NewGameEvent;
import legend.game.modding.events.input.InputPressedEvent;
import legend.game.modding.events.inventory.GiveEquipmentEvent;
import legend.game.modding.events.inventory.GiveItemEvent;
import legend.game.modding.events.inventory.RepeatItemReturnEvent;
import legend.game.modding.events.inventory.ShopContentsEvent;
import legend.game.modding.events.inventory.ShopSellPriceEvent;
import legend.game.modding.events.scripting.DrgnFileEvent;
import legend.game.modding.events.submap.SubmapWarpEvent;
import legend.game.saves.BoolConfigEntry;
import legend.game.saves.ConfigEntry;
import legend.game.saves.ConfigRegistryEvent;
import legend.game.scripting.ScriptState;
import legend.game.submap.SMap;
import legend.game.types.EquipmentSlot;
import legend.game.ui.GameOverlay;
import legend.lodmod.LodBattleActions;
import legend.lodmod.LodConfig;
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
import legend.lodmod.items.SachetItem;
import legend.lodmod.items.ShieldItem;
import legend.lodmod.items.SignetStoneItem;
import legend.lodmod.items.SmokeBallItem;
import legend.lodmod.items.SpiritPotionItem;
import legend.lodmod.items.SunRhapsodyItem;
import legend.lodmod.items.TotalVanishingItem;
import lod.dragoonmodifier.character.Albert;
import lod.dragoonmodifier.character.Dart;
import lod.dragoonmodifier.character.Haschel;
import lod.dragoonmodifier.character.Kongol;
import lod.dragoonmodifier.character.Lavitz;
import lod.dragoonmodifier.character.Meru;
import lod.dragoonmodifier.character.Miranda;
import lod.dragoonmodifier.character.Rose;
import lod.dragoonmodifier.character.Shana;
import lod.dragoonmodifier.character.TemplateCommon;
import lod.dragoonmodifier.configs.DamageTrackerConfig;
import lod.dragoonmodifier.configs.DifficultyEntryConfig;
import lod.dragoonmodifier.configs.MonsterHPBarConfig;
import lod.dragoonmodifier.equips.DestroyerMaceEquipment;
import lod.dragoonmodifier.equips.ItemArrowEquipment;
import lod.dragoonmodifier.equips.UltimateWargodEquipment;
import lod.dragoonmodifier.equips.WargodCallingEquipment;
import lod.dragoonmodifier.events.DifficultyChangedEvent;
import lod.dragoonmodifier.events.ShanaElementArrowAttackEvent;
import lod.dragoonmodifier.events.ShanaGetArrowCountEvent;
import lod.dragoonmodifier.events.ShanaSwapArrowEvent;
import lod.dragoonmodifier.items.DraModItemDeffPackage;
import lod.dragoonmodifier.menu.DetransformAction;
import lod.dragoonmodifier.menu.EnhancementAction;
import org.legendofdragoon.modloader.Mod;
import org.legendofdragoon.modloader.events.EventListener;
import org.legendofdragoon.modloader.events.Priority;
import org.legendofdragoon.modloader.registries.Registrar;
import org.legendofdragoon.modloader.registries.RegistryDelegate;
import org.legendofdragoon.modloader.registries.RegistryId;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
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

import static legend.core.GameEngine.CONFIG;
import static legend.core.GameEngine.EVENTS;
import static legend.core.GameEngine.REGISTRIES;
import static legend.core.GameEngine.RENDERER;
import static legend.game.EngineStates.currentEngineState_8004dd04;
import static legend.game.Scus94491BpeSegment_8005.submapCut_80052c30;
import static legend.game.Scus94491BpeSegment_8006.battleState_8006e398;
import static legend.game.Scus94491BpeSegment_800b.encounterId_800bb0f8;
import static legend.game.Scus94491BpeSegment_800b.gameState_800babc8;
import static legend.game.Scus94491BpeSegment_800b.spGained_800bc950;
import static legend.game.Text.renderText;
import static legend.game.characters.CharacterData2c.CAN_BE_IN_PARTY;
import static legend.game.characters.CharacterData2c.IN_PARTY;
import static legend.game.combat.ui.BattleMenuStruct58.battleMenuIconMetrics_800fb674;
import static legend.lodmod.LodGoods.BLUE_DRAGOON_SPIRIT;
import static legend.lodmod.LodGoods.VIOLET_DRAGOON_SPIRIT;
import static legend.lodmod.LodMod.ATTACK_STAT;
import static legend.lodmod.LodMod.DARK_ELEMENT;
import static legend.lodmod.LodMod.DEFENSE_STAT;
import static legend.lodmod.LodMod.DIVINE_ELEMENT;
import static legend.lodmod.LodMod.EARTH_ELEMENT;
import static legend.lodmod.LodMod.FIRE_ELEMENT;
import static legend.lodmod.LodMod.FRACTIONAL_STAT_MOD_TYPE;
import static legend.lodmod.LodMod.GUARD_HEAL_STAT;
import static legend.lodmod.LodMod.HP_STAT;
import static legend.lodmod.LodMod.INPUT_ACTION_WMAP_QUEEN_FURY_COOLON;
import static legend.lodmod.LodMod.LIGHT_ELEMENT;
import static legend.lodmod.LodMod.MAGIC_ATTACK_STAT;
import static legend.lodmod.LodMod.MAGIC_DEFENSE_STAT;
import static legend.lodmod.LodMod.MP_STAT;
import static legend.lodmod.LodMod.NO_ELEMENT;
import static legend.lodmod.LodMod.PLAYER_TYPE;
import static legend.lodmod.LodMod.SP_STAT;
import static legend.lodmod.LodMod.THUNDER_ELEMENT;
import static legend.lodmod.LodMod.UNARY_STAT_MOD_TYPE;
import static legend.lodmod.LodMod.WATER_ELEMENT;
import static legend.lodmod.LodMod.WIND_ELEMENT;

@Mod(id = DragoonModifier.MOD_ID, version = "^3.0.0")
public class DragoonModifier {
  //region Vars
  public static final String MOD_ID = "dragoon_modifier";
  public boolean csvLoaded;
  public String difficultyLoaded;
  public PlayerBattleEntity currentPlayer;
  public boolean dragonBlockStaff;
  public boolean jadeDragoonPresent;
  public boolean whiteSilverDragoonPresent;
  public double damageOverride;
  public int[] protectionShield = new int[3];
  private final FontOptions fontOptions = new FontOptions().colour(TextColour.WHITE);
  public boolean[] recalcDragoonTurns = new boolean[3];
  public int[] preventDeathCount = new int[3];

  //CSVs
  public static final List<String[]> equipStats = new ArrayList<>();
  public static final List<String[]> itemStats = new ArrayList<>();
  public static final List<String[]> shopItems = new ArrayList<>();
  public static final List<String[]> monsterStats = new ArrayList<>();
  public static final List<String[]> monstersRewardsStats = new ArrayList<>();
  public static final List<String[]> spells = new ArrayList<>();
  public static final List<String[]> monsterSpells = new ArrayList<>();
  public static final List<String[]> shanaSpGain = new ArrayList<>();

  //Registars
  public static final Registrar<CharacterTemplate, RegisterCharacterTemplatesEvent> CHARACTER_REGISTRAR = new Registrar<>(REGISTRIES.characterTemplates, DragoonModifier.MOD_ID);
  public static final Registrar<BattleAction, RegisterBattleActionsEvent> ACTION_REGISTRAR = new Registrar<>(REGISTRIES.battleActions, DragoonModifier.MOD_ID);
  public static final Registrar<StatType<?>, StatTypeRegistryEvent> STAT_TYPE_REGISTRAR = new Registrar<>(REGISTRIES.statTypes, DragoonModifier.MOD_ID);
  public static final RegistryDelegate<Dart> DART = CHARACTER_REGISTRAR.register("dart", Dart::new);
  public static final RegistryDelegate<Lavitz> LAVITZ = CHARACTER_REGISTRAR.register("lavitz", Lavitz::new);
  public static final RegistryDelegate<Shana> SHANA = CHARACTER_REGISTRAR.register("shana", Shana::new);
  public static final RegistryDelegate<Rose> ROSE = CHARACTER_REGISTRAR.register("rose", Rose::new);
  public static final RegistryDelegate<Haschel> HASCHEL = CHARACTER_REGISTRAR.register("haschel", Haschel::new);
  public static final RegistryDelegate<Albert> ALBERT = CHARACTER_REGISTRAR.register("albert", Albert::new);
  public static final RegistryDelegate<Meru> MERU = CHARACTER_REGISTRAR.register("meru", Meru::new);
  public static final RegistryDelegate<Kongol> KONGOL = CHARACTER_REGISTRAR.register("kongol", Kongol::new);
  public static final RegistryDelegate<Miranda> MIRANDA = CHARACTER_REGISTRAR.register("miranda", Miranda::new);
  public static final RegistryDelegate<BattleAction> DETRANSFORM_ACTION = ACTION_REGISTRAR.register("detransform", DetransformAction::new);
  public static final RegistryDelegate<BattleAction> BURN_STACK_ACTION = ACTION_REGISTRAR.register("burn_stacks", EnhancementAction::new);
  public static final RegistryDelegate<BattleAction> WIND_BARRIER_ACTION = ACTION_REGISTRAR.register("wind_barrier", EnhancementAction::new);
  public static final RegistryDelegate<BattleAction> ELEMENTAL_QUIVER_ACTION = ACTION_REGISTRAR.register("elemental_quiver", EnhancementAction::new);
  public static final RegistryDelegate<BattleAction> SIPHON_ACTION = ACTION_REGISTRAR.register("siphon", EnhancementAction::new);
  public static final RegistryDelegate<BattleAction> STATIC_CHARGE = ACTION_REGISTRAR.register("static_charge", EnhancementAction::new);
  public static final RegistryDelegate<BattleAction> ALBERT_WIND_BARRIER_ACTION = ACTION_REGISTRAR.register("albert_wind_barrier", EnhancementAction::new);
  public static final RegistryDelegate<BattleAction> WINGLY_MAGIC = ACTION_REGISTRAR.register("wingly_magic", EnhancementAction::new);
  public static final RegistryDelegate<BattleAction> COUNTER_STANCE = ACTION_REGISTRAR.register("counter_stance", EnhancementAction::new);
  public static final RegistryDelegate<BattleAction> MIRANDA_ELEMENTAL_QUIVER_ACTION = ACTION_REGISTRAR.register("miranda_elemental_quiver", EnhancementAction::new);
  public static final RegistryDelegate<StatType<UnaryStat>> STORM_HP_REGEN = STAT_TYPE_REGISTRAR.register("storm_hp_regen", UnaryStatType::new);
  public static final RegistryDelegate<StatType<UnaryStat>> COUNTER_STANCE_SLOWDOWN = STAT_TYPE_REGISTRAR.register("counter_stance_slowdown", UnaryStatType::new);

  //Configs
  public static final Registrar<ConfigEntry<?>, ConfigRegistryEvent> DRAMOD_CONFIG_REGISTRAR = new Registrar<>(REGISTRIES.config, MOD_ID);
  public static final RegistryDelegate<DifficultyEntryConfig> DIFFICULTY = DRAMOD_CONFIG_REGISTRAR.register("difficulty", DifficultyEntryConfig::new);
  public static final RegistryDelegate<BoolConfigEntry> MONSTER_HP_BAR = DRAMOD_CONFIG_REGISTRAR.register("hp_bar", MonsterHPBarConfig::new);
  public static final RegistryDelegate<DamageTrackerConfig> DAMAGE_TRACKER = DRAMOD_CONFIG_REGISTRAR.register("damage_tracker", DamageTrackerConfig::new);

  //Constants
  public static Obj ENHANCEMENT_OBJ;
  public static Texture ENHANCEMENT_TEXTURE;
  public static Obj ELEMENTAL_OBJ;
  public static Map<String, Texture> ELEMENTAL_ICON_TEXTURE = new HashMap<>();
  private final FontOptions hpFont = new FontOptions().colour(TextColour.WHITE).size(0.67f);

  //Damage Tracker
  public String[][] damageTrackerEquips = new String[3][5];
  public int[][] damageTracker = new int[3][5];
  public int[] damageTrackerPreviousHP = new int[10];
  public PlayerBattleEntity damageTrackerPreviousCharacter;
  public int damageTrackerPreviousAttackType;
  public String[] damageTrackerMonsterNames = new String[5];
  public ArrayList<String> damageTrackerLog = new ArrayList<>();
  public boolean damageTrackerPrinted;

  //Dart Enhancements
  public static final int DART_BURN_STACKS_MAX = 15;
  public static boolean[] dartBurnStackMode = new boolean[3];
  public static int[] dartBurnStacks = new int[3];
  public static int[] dartPreviousMp = new int[3];
  public int[] dartPreviousBurnStacks = new int[3];
  public boolean[] dartBurnAdded = new boolean[3];
  public double dmgPerBurn = 0.1;
  public final int BURN_STACKS_FLAMESHOT = 1;
  public final int BURN_STACKS_EXPLOSION = 2;
  public final int BURN_STACKS_FINAL_BURST = 3;
  public final int BURN_STACKS_RED_EYED_DRAGON = 4;
  public final int BURN_STACKS_ADDITION = 1;

  //Lavitz & Albert Enhancements
  public static boolean[] windBarrier = new boolean[3];
  public int[] windMark = new int[10];
  public static int STORM_REGEN_AMOUNT = 10;
  public boolean[] stormHPRegenActive = new boolean[3];

  //Shana Enhancements
  public boolean[] shanaDeffArrow = new boolean[3];
  public int[] shanaArrowCount = new int[3];
  public int[] shanaMaxArrowCount = new int[3];
  public RegistryId[] shanaPreviousArrow = new RegistryId[3];
  public List<String>[] shanaUsedElementalArrowsField = new ArrayList[3];
  public List<String>[] shanaUsedElementalArrowsShift = new ArrayList[3];
  public boolean shanaElementalField;
  public int shanaElementalFieldTurns;
  public int shanaElementalFieldActivatorSlot;
  public int shanaElementalFieldMode;
  public Element shanaElementalFieldElement1;
  public Element shanaElementalFieldElement2;
  public boolean shanaElementalShift;
  public int shanaElementalShiftTurns;
  public int shanaElementalShiftActivatorSlot;
  public int shanaElementalShiftMode;
  public Element shanaElementalShiftElement1;
  public Element shanaElementalShiftElement2;
  public int shanaElementalCooldownTurns;

  //Rose Enhancements
  public static int[] roseSiphon = new int[3];
  public static int[] roseSiphonMax = new int[3];
  public static boolean[] roseSiphonActivated = new boolean[3];

  //Haschel Enhancements
  public static int[] thunderCharge = new int[10];
  public static int[] overchargedTurns = new int[10];
  public static int[] staticCharge = new int[3];
  public boolean haschelInPartyWithDragoon;
  public Element[] infusedChargedMonsterPreviousElement = new Element[10];
  public int[] infusedChargedMonsterTurns = new int[10];

  //Meru Enhancements
  public int[] meruIceShield = new int[3];
  public int[] meruIceShieldMax = new int[3];
  public static boolean[] meruWinglyMagic = new boolean[3];
  public boolean meruInPartyWithDragoon;

  //Kongol Enhancements
  public static boolean[] kongolCounterStance = new boolean[3];
  public boolean[] kongolHitInCounterStance = new boolean[3];
  public static int[] kongolCounterStanceTurns = new int[3];

  //endregion

  //region Mod ID
  public DragoonModifier() {
    EVENTS.register(this);
  }

  public static RegistryId id(final String entryId) {
    return new RegistryId(MOD_ID, entryId);
  }

  public static RegistryId idCore(final String entryId) {
    return new RegistryId("lod", entryId);
  }
  //endregion

  //region Helper Methods
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

  public ItemIcon getIconFromId(final int id) {
    return switch(id) {
      case 0 -> ItemIcon.SWORD;
      case 1 -> ItemIcon.AXE;
      case 2 -> ItemIcon.HAMMER;
      case 3 -> ItemIcon.SPEAR;
      case 4 -> ItemIcon.BOW;
      case 5 -> ItemIcon.MACE;
      case 6 -> ItemIcon.KNUCKLE;
      case 7 -> ItemIcon.BOXING_GLOVE;
      case 8 -> ItemIcon.CLOTHES;
      case 9 -> ItemIcon.ROBE;
      case 10 -> ItemIcon.ARMOR;
      case 11 -> ItemIcon.BREASTPLATE;
      case 12 -> ItemIcon.RED_DRESS;
      case 13 -> ItemIcon.LOINCLOTH;
      case 14 -> ItemIcon.WARRIOR_DRESS;
      case 15 -> ItemIcon.CROWN;
      case 16 -> ItemIcon.HAIRBAND;
      case 17 -> ItemIcon.HAT;
      case 18 -> ItemIcon.HELM;
      case 19 -> ItemIcon.SHOES;
      case 20 -> ItemIcon.KNEEPIECE;
      case 21 -> ItemIcon.BOOTS;
      case 22 -> ItemIcon.BRACELET;
      case 23 -> ItemIcon.RING;
      case 24 -> ItemIcon.AMULET;
      case 25 -> ItemIcon.STONE;
      case 26 -> ItemIcon.JEWELLERY;
      case 27 -> ItemIcon.PIN;
      case 28 -> ItemIcon.BELL;
      case 29 -> ItemIcon.BAG;
      case 30 -> ItemIcon.CAPE;
      case 31 -> ItemIcon.GLOVE;
      case 32 -> ItemIcon.HORN;
      case 33 -> ItemIcon.BLUE_POTION;
      case 34 -> ItemIcon.YELLOW_POTION;
      case 35 -> ItemIcon.RED_POTION;
      case 36 -> ItemIcon.ANGELS_PRAYER;
      case 37 -> ItemIcon.GREEN_POTION;
      case 38 -> ItemIcon.MAGIC;
      case 39 -> ItemIcon.SKULL;
      case 40 -> ItemIcon.UP;
      case 41 -> ItemIcon.DOWN;
      case 42 -> ItemIcon.SHIELD_ITEM;
      case 43 -> ItemIcon.SMOKE_BALL;
      case 44 -> ItemIcon.SIG_STONE;
      case 45 -> ItemIcon.CHARM;
      case 46 -> ItemIcon.SACK;
      case 57 -> ItemIcon.INVALID;
      case 58 -> ItemIcon.WARNING;
      case 64 -> ItemIcon.NONE;
      default -> ItemIcon.SWORD;
    };
  }

  public int getEquipPrice(final String id) {
    for(final String[] equip : equipStats) {
      if(equip[38].contains(id)) {
        return Integer.parseInt(equip[35]);
      }
    }
    return 0;
  }

  public int getItemPrice(final String id) {
    for(final String[] item : itemStats) {
      if(item[33].contains(id)) {
        return Integer.parseInt(item[24]);
      }
    }
    return 0;
  }

  @EventListener
  public void eventConfigRegistry(final ConfigRegistryEvent event) {
    DRAMOD_CONFIG_REGISTRAR.registryEvent(event);
  }

  @EventListener
  public void eventConfigLoaded(final ConfigLoadedEvent event) {
    //this.loadAllCsvs(event.configCollection.getConfig(DIFFICULTY.get()));
  }

  private void loadAllCsvs(final String difficulty) {
    if(!this.csvLoaded) {
      this.loadCsvIntoList(difficulty, equipStats, "equip_stats.csv");
      this.loadCsvIntoList(difficulty, itemStats, "thrown_item_stats.csv");
      this.loadCsvIntoList(difficulty, shopItems, "shop_items.csv");
      this.loadCsvIntoList(difficulty, monsterStats, "monster_stats.csv");
      this.loadCsvIntoList(difficulty, monstersRewardsStats, "monster_rewards.csv");
      this.loadCsvIntoList(difficulty, spells, "spells.csv");
      this.loadCsvIntoList(difficulty, shanaSpGain, "shana_sp_gain.csv");
      this.csvLoaded = true;
      this.difficultyLoaded = difficulty;
      this.lockConfigs();
    }
  }

  private void reloadCSV(final String difficulty) {
    this.loadCsvIntoList(difficulty, monsterStats, "monster_stats.csv");
    this.loadCsvIntoList(difficulty, monstersRewardsStats, "monster_rewards.csv");
    this.lockConfigs();
  }

  private void lockConfigs() {
    //TODO: Config CSV for all modes but it will throw array out of index anyways... but for future when a characters can have different max levels from each other.
    CONFIG.setConfig(LodConfig.MAX_LEVEL.get(), 60);
    CONFIG.setConfig(LodConfig.MAX_DRAGOON_LEVEL.get(), 5);
    CONFIG.setConfig(LodConfig.ITEM_STACK_SIZE.get(), 1);
    CONFIG.setConfig(CoreMod.INVENTORY_SIZE_CONFIG.get(), 32);
    CONFIG.setConfig(LodConfig.EXTENDED_DRAGOON_ACTIONS.get(), false);
    CONFIG.setConfig(CoreMod.EQUIP_EFFECTS_IN_DRAGOON.get(), true);
    CONFIG.lockConfig(LodConfig.MAX_LEVEL.get());
    CONFIG.lockConfig(LodConfig.MAX_DRAGOON_LEVEL.get());
    CONFIG.lockConfig(LodConfig.ITEM_STACK_SIZE.get());
    CONFIG.lockConfig(CoreMod.INVENTORY_SIZE_CONFIG.get());
    CONFIG.lockConfig(LodConfig.EXTENDED_DRAGOON_ACTIONS.get());
    CONFIG.lockConfig(CoreMod.EQUIP_EFFECTS_IN_DRAGOON.get());
  }

  private void levelUp(final CharacterData2c character, final int level) {
    while(character.level_12 < level - 1) {
      character.applyLevelUp(null);
    }

    character.xp_00 = character.getXpToNextLevel();
    character.applyLevelUp(null);

    character.stats.getStat(HP_STAT.get()).restore();
    character.stats.getStat(MP_STAT.get()).restore();
  }

  private boolean isHardMode() {
    final String difficulty = CONFIG.getConfig(DIFFICULTY.get());

    return "Retail NA + Hard Bosses".equals(difficulty) || "Hard Mode".equals(difficulty);
  }

  private boolean isHellMode() {
    final String difficulty = CONFIG.getConfig(DIFFICULTY.get());

    return "Hard + Hell Bosses".equals(difficulty) || "Hell Mode".equals(difficulty);
  }

  public boolean getEquipment(final String registryIdString, final PlayerBattleEntity player, final EquipmentSlot slot) {
    try {
      return registryIdString.equals(player.equipment_11e.get(slot).getRegistryId().toString());
    } catch(final Exception ignored) {
      return false;
    }
  }
  //endregion

  //region Characters
  @EventListener(priority = Priority.HIGHEST)
  public void newGame(final NewGameEvent event) {
    final CharacterData2c dart = event.gameState.addCharacter(DART.get().make(event.gameState));
    final CharacterData2c lavitz = event.gameState.addCharacter(LAVITZ.get().make(event.gameState));
    final CharacterData2c shana = event.gameState.addCharacter(SHANA.get().make(event.gameState));
    final CharacterData2c rose = event.gameState.addCharacter(ROSE.get().make(event.gameState));
    final CharacterData2c haschel = event.gameState.addCharacter(HASCHEL.get().make(event.gameState));
    final CharacterData2c albert = event.gameState.addCharacter(ALBERT.get().make(event.gameState));
    final CharacterData2c meru = event.gameState.addCharacter(MERU.get().make(event.gameState));
    final CharacterData2c kongol = event.gameState.addCharacter(KONGOL.get().make(event.gameState));
    final CharacterData2c miranda = event.gameState.addCharacter(MIRANDA.get().make(event.gameState));

    event.gameState.charData_32c.clear();

    event.gameState.charData_32c.add(dart);
    event.gameState.charData_32c.add(lavitz);
    event.gameState.charData_32c.add(shana);
    event.gameState.charData_32c.add(rose);
    event.gameState.charData_32c.add(haschel);
    event.gameState.charData_32c.add(albert);
    event.gameState.charData_32c.add(meru);
    event.gameState.charData_32c.add(kongol);
    event.gameState.charData_32c.add(miranda);

    event.gameState.charData_32c.get(0).partyFlags_04 = 0x3;

    this.levelUp(lavitz, 3);
    this.levelUp(shana, 4);
    this.levelUp(rose, 8);
    this.levelUp(haschel, 13);
    this.levelUp(albert, 15);
    this.levelUp(meru, 17);
    this.levelUp(kongol, 19);
    this.levelUp(miranda, 23);

    dart.equip(EquipmentSlot.WEAPON, REGISTRIES.equipment.getEntry(MOD_ID, "broad_sword").get());
    dart.equip(EquipmentSlot.HELMET, REGISTRIES.equipment.getEntry(MOD_ID, "bandana").get());
    dart.equip(EquipmentSlot.ARMOUR, REGISTRIES.equipment.getEntry(MOD_ID, "leather_armor").get());
    dart.equip(EquipmentSlot.BOOTS, REGISTRIES.equipment.getEntry(MOD_ID, "leather_boots").get());
    dart.equip(EquipmentSlot.ACCESSORY, REGISTRIES.equipment.getEntry(MOD_ID, "bracelet").get());

    lavitz.equip(EquipmentSlot.WEAPON, REGISTRIES.equipment.getEntry(MOD_ID, "spear").get());
    lavitz.equip(EquipmentSlot.HELMET, REGISTRIES.equipment.getEntry(MOD_ID, "sallet").get());
    lavitz.equip(EquipmentSlot.ARMOUR, REGISTRIES.equipment.getEntry(MOD_ID, "scale_armor").get());
    lavitz.equip(EquipmentSlot.BOOTS, REGISTRIES.equipment.getEntry(MOD_ID, "leather_boots").get());
    lavitz.equip(EquipmentSlot.ACCESSORY, REGISTRIES.equipment.getEntry(MOD_ID, "bracelet").get());

    shana.equip(EquipmentSlot.WEAPON, REGISTRIES.equipment.getEntry(MOD_ID, "short_bow").get());
    shana.equip(EquipmentSlot.HELMET, REGISTRIES.equipment.getEntry(MOD_ID, "felt_hat").get());
    shana.equip(EquipmentSlot.ARMOUR, REGISTRIES.equipment.getEntry(MOD_ID, "clothes").get());
    shana.equip(EquipmentSlot.BOOTS, REGISTRIES.equipment.getEntry(MOD_ID, "leather_shoes").get());
    shana.equip(EquipmentSlot.ACCESSORY, REGISTRIES.equipment.getEntry(MOD_ID, "bracelet").get());

    rose.equip(EquipmentSlot.WEAPON, REGISTRIES.equipment.getEntry(MOD_ID, "rapier").get());
    rose.equip(EquipmentSlot.HELMET, REGISTRIES.equipment.getEntry(MOD_ID, "felt_hat").get());
    rose.equip(EquipmentSlot.ARMOUR, REGISTRIES.equipment.getEntry(MOD_ID, "leather_jacket").get());
    rose.equip(EquipmentSlot.BOOTS, REGISTRIES.equipment.getEntry(MOD_ID, "leather_shoes").get());
    rose.equip(EquipmentSlot.ACCESSORY, REGISTRIES.equipment.getEntry(MOD_ID, "bracelet").get());

    haschel.equip(EquipmentSlot.WEAPON, REGISTRIES.equipment.getEntry(MOD_ID, "iron_knuckle").get());
    haschel.equip(EquipmentSlot.HELMET, REGISTRIES.equipment.getEntry(MOD_ID, "armet").get());
    haschel.equip(EquipmentSlot.ARMOUR, REGISTRIES.equipment.getEntry(MOD_ID, "disciple_vest").get());
    haschel.equip(EquipmentSlot.BOOTS, REGISTRIES.equipment.getEntry(MOD_ID, "iron_kneepiece").get());
    haschel.equip(EquipmentSlot.ACCESSORY, REGISTRIES.equipment.getEntry(MOD_ID, "bracelet").get());

    albert.equip(EquipmentSlot.WEAPON, REGISTRIES.equipment.getEntry(MOD_ID, "spear").get());
    albert.equip(EquipmentSlot.HELMET, REGISTRIES.equipment.getEntry(MOD_ID, "sallet").get());
    albert.equip(EquipmentSlot.ARMOUR, REGISTRIES.equipment.getEntry(MOD_ID, "scale_armor").get());
    albert.equip(EquipmentSlot.BOOTS, REGISTRIES.equipment.getEntry(MOD_ID, "leather_boots").get());
    albert.equip(EquipmentSlot.ACCESSORY, REGISTRIES.equipment.getEntry(MOD_ID, "bracelet").get());

    meru.equip(EquipmentSlot.WEAPON, REGISTRIES.equipment.getEntry(MOD_ID, "mace").get());
    meru.equip(EquipmentSlot.HELMET, REGISTRIES.equipment.getEntry(MOD_ID, "tiara").get());
    meru.equip(EquipmentSlot.ARMOUR, REGISTRIES.equipment.getEntry(MOD_ID, "silver_vest").get());
    meru.equip(EquipmentSlot.BOOTS, REGISTRIES.equipment.getEntry(MOD_ID, "soft_boots").get());
    meru.equip(EquipmentSlot.ACCESSORY, REGISTRIES.equipment.getEntry(MOD_ID, "bracelet").get());

    kongol.equip(EquipmentSlot.WEAPON, REGISTRIES.equipment.getEntry(MOD_ID, "axe").get());
    kongol.equip(EquipmentSlot.HELMET, REGISTRIES.equipment.getEntry(MOD_ID, "armet").get());
    kongol.equip(EquipmentSlot.ARMOUR, REGISTRIES.equipment.getEntry(MOD_ID, "lion_fur").get());
    kongol.equip(EquipmentSlot.BOOTS, REGISTRIES.equipment.getEntry(MOD_ID, "iron_kneepiece").get());
    kongol.equip(EquipmentSlot.ACCESSORY, REGISTRIES.equipment.getEntry(MOD_ID, "bracelet").get());

    miranda.equip(EquipmentSlot.WEAPON, REGISTRIES.equipment.getEntry(MOD_ID, "short_bow").get());
    miranda.equip(EquipmentSlot.HELMET, REGISTRIES.equipment.getEntry(MOD_ID, "felt_hat").get());
    miranda.equip(EquipmentSlot.ARMOUR, REGISTRIES.equipment.getEntry(MOD_ID, "clothes").get());
    miranda.equip(EquipmentSlot.BOOTS, REGISTRIES.equipment.getEntry(MOD_ID, "leather_shoes").get());
    miranda.equip(EquipmentSlot.ACCESSORY, REGISTRIES.equipment.getEntry(MOD_ID, "bracelet").get());
  }

  @EventListener(priority = Priority.HIGHEST)
  public void additionRegistry(final AdditionRegistryEvent event) {
    final List<String[]> additionList = new ArrayList<>();
    this.loadCsvIntoList(CONFIG.getConfig(DIFFICULTY.get()), additionList, "addition_list.csv");

    for(final String[] addition : additionList) {
      event.register(id(addition[1]), TemplateCommon.getAddition(addition[0], addition[1]));
    }
  }

  @EventListener(priority = Priority.HIGHEST)
  public void spellRegistry(final SpellRegistryEvent event) {
    final List<String[]> spells = new ArrayList<>();
    this.loadCsvIntoList(CONFIG.getConfig(DIFFICULTY.get()), spells, "player_spells.csv");

    for(final String[] spell : spells) {
      event.register(id(spell[14]), TemplateCommon.getSpell(spell, true));
    }

    GameEngine.loadLangOverrides(Path.of("mods", "dragoon_modifier", "lang", CONFIG.getConfig(DIFFICULTY.get())));
  }

  @EventListener(priority = Priority.HIGHEST)
  public void monsterSpellRegistry(final MonsterSpellRegistryEvent event) {
    this.loadCsvIntoList(CONFIG.getConfig(DIFFICULTY.get()), monsterSpells, "spells.csv");
    for(final String[] spell : monsterSpells) {
      event.register(id(spell[14]), TemplateCommon.getSpell(spell, false));
    }
  }

  @EventListener(priority = Priority.HIGHEST)
  public void registerCharacterTemplates(final RegisterCharacterTemplatesEvent event) {
    CHARACTER_REGISTRAR.registryEvent(event);
  }

  @EventListener(priority = Priority.HIGHEST)
  public void gatherCharacterEquipmentTypes(final GatherCharacterEquipmentTypesEvent event) {
    event.add(DART.get(), EquipmentTypes.DART, EquipmentTypes.LONGSWORD, EquipmentTypes.NEUTRAL, EquipmentTypes.MALE, EquipmentTypes.ADDITIONS, EquipmentTypes.HEAVY, EquipmentTypes.ARMOR_OF_YORE);
    event.add(LAVITZ.get(), EquipmentTypes.LAVITZ, EquipmentTypes.POLEARM, EquipmentTypes.NEUTRAL, EquipmentTypes.MALE, EquipmentTypes.ADDITIONS, EquipmentTypes.HEAVY, EquipmentTypes.ARMOR_OF_YORE);
    event.add(SHANA.get(), EquipmentTypes.SHANA, EquipmentTypes.BOW, EquipmentTypes.NEUTRAL, EquipmentTypes.FEMALE, EquipmentTypes.MEDIUM, EquipmentTypes.LIGHT);
    event.add(ROSE.get(), EquipmentTypes.ROSE, EquipmentTypes.SHORTSWORD, EquipmentTypes.NEUTRAL, EquipmentTypes.FEMALE, EquipmentTypes.ADDITIONS, EquipmentTypes.MEDIUM);
    event.add(HASCHEL.get(), EquipmentTypes.HASCHEL, EquipmentTypes.HAND, EquipmentTypes.NEUTRAL, EquipmentTypes.MALE, EquipmentTypes.ADDITIONS);
    event.add(ALBERT.get(), EquipmentTypes.ALBERT, EquipmentTypes.POLEARM, EquipmentTypes.NEUTRAL, EquipmentTypes.MALE, EquipmentTypes.ADDITIONS, EquipmentTypes.HEAVY, EquipmentTypes.ARMOR_OF_YORE);
    event.add(MERU.get(), EquipmentTypes.MERU, EquipmentTypes.HAMMER, EquipmentTypes.NEUTRAL, EquipmentTypes.FEMALE, EquipmentTypes.ADDITIONS, EquipmentTypes.LIGHT);
    event.add(KONGOL.get(), EquipmentTypes.KONGOL, EquipmentTypes.AXE, EquipmentTypes.NEUTRAL, EquipmentTypes.MALE, EquipmentTypes.ADDITIONS, EquipmentTypes.ARMOR_OF_YORE);
    event.add(MIRANDA.get(), EquipmentTypes.MIRANDA, EquipmentTypes.BOW, EquipmentTypes.NEUTRAL, EquipmentTypes.FEMALE, EquipmentTypes.MEDIUM, EquipmentTypes.LIGHT);
  }
  //endregion

  //region Inventory
  @EventListener(priority = Priority.HIGHEST)
  public void equipmentRegistry(final EquipmentRegistryEvent event) {
    this.loadAllCsvs(CONFIG.getConfig(DIFFICULTY.get()));

    for(final String[] equip : equipStats) {
      //this.print("Registering: " + file.getName() + '/' + equip[44]);
      final ElementSet elementalResistance = new ElementSet();
      final ElementSet elementalImmunity = new ElementSet();
      final EquipmentSlot equipmentSlot = Integer.parseInt(equip[1]) == 0x80 ? EquipmentSlot.WEAPON :
        Integer.parseInt(equip[1]) == 0x20 ? EquipmentSlot.ARMOUR :
          Integer.parseInt(equip[1]) == 0x40 ? EquipmentSlot.HELMET :
            Integer.parseInt(equip[1]) == 0x10 ? EquipmentSlot.BOOTS : EquipmentSlot.ACCESSORY;

      final int eleRes = Integer.parseInt(equip[4]);
      final int eleImmunity = Integer.parseInt(equip[5]);

      if(eleRes > 0) {
        elementalResistance.add(Element.fromFlag(Integer.parseInt(equip[4])).get());
      }

      if(eleImmunity > 0) {
        elementalImmunity.add(Element.fromFlag(Integer.parseInt(equip[5])).get());
      }

      final Equipment dmEquip = new Equipment(
        Integer.parseInt(equip[34]), //Price
        Integer.parseInt(equip[0]), //Flags
        equipmentSlot, //Slot
        Element.fromFlag(Integer.parseInt(equip[3])).get(), // Element
        elementalResistance, //elementalResistance
        elementalImmunity, //elementalImmunity
        Integer.parseInt(equip[6]), // Status Resist
        Integer.parseInt(equip[7]), //mpPerPhysicalHit
        Integer.parseInt(equip[8]), //spPerPhysicalHit
        Integer.parseInt(equip[9]), //mpPerMagicalHit
        Integer.parseInt(equip[10]), //spPerMagicalHit
        Integer.parseInt(equip[11]), //hpMultiplier
        Integer.parseInt(equip[12]), //mpMultiplier
        Integer.parseInt(equip[13]), //spMultiplier
        Boolean.parseBoolean(equip[14]), //magicalResistance
        Boolean.parseBoolean(equip[15]), //physicalResistance
        Boolean.parseBoolean(equip[16]), //magicalImmunity
        Boolean.parseBoolean(equip[17]), //physicalImmunity
        Integer.parseInt(equip[18]), //revive
        Integer.parseInt(equip[19]), //hpRegen
        Integer.parseInt(equip[20]), //mpRegen
        Integer.parseInt(equip[21]), //spRegen
        Integer.parseInt(equip[22]), //escapeBonus
        this.getIconFromId(Integer.parseInt(equip[23])), //icon
        Integer.parseInt(equip[24]), //spd
        Integer.parseInt(equip[25]), //atkHi
        Integer.parseInt(equip[26]), //matk
        Integer.parseInt(equip[27]), //def
        Integer.parseInt(equip[28]), //mdef
        Integer.parseInt(equip[29]), //aHit
        Integer.parseInt(equip[30]), //mHit
        Integer.parseInt(equip[31]), //aAv
        Integer.parseInt(equip[32]), //mAv
        Integer.parseInt(equip[33]), //onStatusChance
        Integer.parseInt(equip[34]) //On Hit Status
      );

      switch(equip[38].split(":")[1]) {
        case "ultimate_wargod":
          event.register(id(equip[38].split(":")[1]), new UltimateWargodEquipment(dmEquip));
          break;
        case "wargod_calling":
          event.register(id(equip[38].split(":")[1]), new WargodCallingEquipment(dmEquip));
          break;
        case "destoryer_mace":
          event.register(id(equip[38].split(":")[1]), new DestroyerMaceEquipment(dmEquip));
          break;
        case "detonate_arrow":
          event.register(id(equip[38].split(":")[1]), new ItemArrowEquipment(dmEquip, "dragoon_modifier:detonate_rock", 0xc1));
          break;
        case "fire_arrow":
          event.register(id(equip[38].split(":")[1]), new ItemArrowEquipment(dmEquip, "dragoon_modifier:burn_out", 0xc3));
          break;
        case "water_arrow":
          event.register(id(equip[38].split(":")[1]), new ItemArrowEquipment(dmEquip, "dragoon_modifier:spear_frost", 0xc6));
          break;
        case "wind_arrow":
          event.register(id(equip[38].split(":")[1]), new ItemArrowEquipment(dmEquip, "dragoon_modifier:spinning_gale", 0xc7));
          break;
        case "earth_arrow":
          event.register(id(equip[38].split(":")[1]), new ItemArrowEquipment(dmEquip, "dragoon_modifier:pellet", 0xc5));
          break;
        case "dark_arrow":
          event.register(id(equip[38].split(":")[1]), new ItemArrowEquipment(dmEquip, "dragoon_modifier:dark_mist", 0xca));
          break;
        case "light_arrow":
          event.register(id(equip[38].split(":")[1]), new ItemArrowEquipment(dmEquip, "dragoon_modifier:trans_light", 0xc9));
          break;
        case "thunder_arrow":
          event.register(id(equip[38].split(":")[1]), new ItemArrowEquipment(dmEquip, "dragoon_modifier:spark_net", 0xc2));
          break;
        default:
          event.register(id(equip[38].split(":")[1]), dmEquip);
          break;
      }
    }
  }

  @EventListener(priority = Priority.HIGHEST)
  public void gatherEquipmentTypes(final GatherEquipmentTypesEvent event) {
    for(final String[] equip : equipStats) {
      final String[] types = equip[2].split(",");
      if(types.length == 1) {
        event.add(id(equip[38].split(":")[1]), types[0]);
      } else if(types.length == 2) {
        event.add(id(equip[38].split(":")[1]), types[0], types[1]);
      }
    }
  }

  @EventListener(priority = Priority.HIGHEST)
  public void itemRegistry(final ItemRegistryEvent event) {
    this.loadAllCsvs(CONFIG.getConfig(DIFFICULTY.get()));

    for(final String[] item : itemStats) {
      try {
        final boolean targetAll = (Integer.parseInt(item[0]) & 0x2) != 0;
        final Item.TargetType targetType = ((Integer.parseInt(item[0]) & 0x4) != 0) ? Item.TargetType.ENEMIES : Item.TargetType.ALLIES;

        if(item[33].split(":")[1].length() >= 3) {
          switch(item[28]) {
            case "AttackItem":
              event.register(id(item[33].split(":")[1]), new AttackItem(this.getIconFromId(Integer.parseInt(item[19])), Integer.parseInt(item[24]), targetAll, Element.fromFlag(Integer.parseInt(item[1])).get(), Integer.parseInt(item[2])));
              break;
            case "AttackBallItem":
              event.register(id(item[33].split(":")[1]), new AttackBallItem());
              break;
            case "HealingPotionItem":
              event.register(id(item[33].split(":")[1]), new HealingPotionItem(this.getIconFromId(Integer.parseInt(item[19])), Integer.parseInt(item[24]), targetAll, Integer.parseInt(item[21])));
              break;
            case "DepetrifierItem":
              event.register(id(item[33].split(":")[1]), new DepetrifierItem());
              break;
            case "MindPurifierItem":
              event.register(id(item[33].split(":")[1]), new MindPurifierItem());
              break;
            case "BodyPurifierItem":
              event.register(id(item[33].split(":")[1]), new BodyPurifierItem());
              break;
            case "SpiritPotionItem":
              event.register(id(item[33].split(":")[1]), new SpiritPotionItem());
              break;
            case "CauseStatusItem":
              event.register(id(item[33].split(":")[1]), new CauseStatusItem(Integer.parseInt(item[30]), this.getIconFromId(Integer.parseInt(item[19])), Integer.parseInt(item[24]), Integer.parseInt(item[20])));
              break;
            case "TotalVanishingItem":
              event.register(id(item[33].split(":")[1]), new TotalVanishingItem());
              break;
            case "AngelsPrayerItem":
              event.register(id(item[33].split(":")[1]), new AngelsPrayerItem());
              break;
            case "CharmPotionItem":
              event.register(id(item[33].split(":")[1]), new CharmPotionItem());
              break;
            case "PandemoniumItem":
              event.register(id(item[33].split(":")[1]), new PandemoniumItem());
              break;
            case "RecoveryBallItem":
              event.register(id(item[33].split(":")[1]), new RecoveryBallItem());
              break;
            case "ShieldItem":
              event.register(id(item[33].split(":")[1]), new ShieldItem(Integer.parseInt(item[31]), Boolean.parseBoolean(item[11]), Boolean.parseBoolean(item[12])));
              break;
            case "SunRhapsodyItem":
              event.register(id(item[33].split(":")[1]), new SunRhapsodyItem());
              break;
            case "SmokeBallItem":
              event.register(id(item[33].split(":")[1]), new SmokeBallItem());
              break;
            case "HealingFogItem":
              event.register(id(item[33].split(":")[1]), new HealingFogItem());
              break;
            case "SignetStoneItem":
              event.register(id(item[33].split(":")[1]), new SignetStoneItem());
              break;
            case "HealingRainItem":
              event.register(id(item[33].split(":")[1]), new HealingRainItem());
              break;
            case "MoonSerenadeItem":
              event.register(id(item[33].split(":")[1]), new MoonSerenadeItem());
              break;
            case "BuffItem":
              event.register(id(item[33].split(":")[1]), new BuffItem(Integer.parseInt(item[31]), this.getIconFromId(Integer.parseInt(item[19])), Integer.parseInt(item[24]), targetType, Integer.parseInt(item[3]), Integer.parseInt(item[4]), Integer.parseInt(item[5]), Integer.parseInt(item[6]), Integer.parseInt(item[7]), Integer.parseInt(item[8]), Integer.parseInt(item[9]), Integer.parseInt(item[10]), Boolean.parseBoolean(item[11]), Boolean.parseBoolean(item[12]), Integer.parseInt(item[13]), Integer.parseInt(item[14]), Integer.parseInt(item[15]), Integer.parseInt(item[16]), Integer.parseInt(item[17]), Integer.parseInt(item[18])));
              break;
            case "SachetItem":
              event.register(id(item[33].split(":")[1]), new SachetItem());
              break;
            case "HealingBreezeItem":
              event.register(id(item[33].split(":")[1]), new HealingBreezeItem());
              break;
            case "PsycheBombXItem":
              event.register(id(item[33].split(":")[1]), new PsycheBombXItem());
              break;
            case "DraModShieldItem":
              //event.register(id(item[33].split(":")[1]), () -> new DraModShieldItem(this.getIconFromId(Integer.parseInt(item[19])), Integer.parseInt(item[24]), Integer.parseInt(item[34]), Boolean.parseBoolean(item[11]), Boolean.parseBoolean(item[12]), item[31]));
              break;
            case "DraModSpiritPotionItem":
              //event.register(id(item[33].split(":")[1]), () -> new DraModSpiritPotion(this.getIconFromId(Integer.parseInt(item[19])), Integer.parseInt(item[24]), targetAll, Integer.parseInt(item[22]), item[31]));
              break;
            default:
              throw new Exception("Invalid item type found: " + item[28]);
          }
        }
      } catch(final Exception e) {
        throw new RuntimeException(e);
      }
    }
  }

  @EventListener(priority = Priority.HIGHEST)
  public void deffRegistry(final RegisterDeffsEvent event) {
    for(final String[] item : itemStats) {
      if(item[33].split(":")[1].length() >= 3) {
        try {
          final int deff = Integer.parseInt(item[31]);
          event.register(id(item[33].split(":")[1]), new RetailDeffPackage(Integer.parseInt(item[29])));
        } catch(final NumberFormatException nfe) {
          event.register(id(item[33].split(":")[1]), new DraModItemDeffPackage(item[29]));
        }
      }
    }
  }

  @EventListener
  public void registerStatTypes(final StatTypeRegistryEvent event) {
    STAT_TYPE_REGISTRAR.registryEvent(event);
  }

  @EventListener
  public void registerBentStats(final RegisterBattleEntityStatsEvent event) {
    if(event.type == PLAYER_TYPE.get()) {
      event.addStat(STORM_HP_REGEN.get());
      event.addStat(COUNTER_STANCE_SLOWDOWN.get());
    }
  }

  @EventListener(priority = Priority.HIGHEST)
  public void shopContentsEvent(final ShopContentsEvent event) {
    String[] shop = null;

    for(final String[] shopContents : shopItems) {
      if(shopContents[32].equals(event.shop.getRegistryId().toString())) {
        shop = shopContents;
        break;
      }
    }

    if(shop == null) {
      return;
    }

    event.contents.clear();

    for(int i = 0; i < shop.length - 1; i++) {
      Equipment equip;
      Item item;
      try {
        equip = REGISTRIES.equipment.getEntry(id(shop[i].split(":")[1])).get();
      } catch(final Exception ignored) {
        equip = null;
      }

      try {
        item = REGISTRIES.items.getEntry(id(shop[i].split(":")[1])).get();
      } catch(final Exception ignored) {
        item = null;
      }

      if(equip != null) {
        /*if(LodMod.getShopIndex(SItem.shopId_8007a3b4) == 26) {
          if(shop[i].contains("arrow")) {
            event.contents.add(new ShopScreen.ShopEntry<>(equip, 100));
          } else {
            event.contents.add(new ShopScreen.ShopEntry<>(equip, this.getEquipPrice(shop[i].split(":")[1]) * 2));
          }
        } else {*/
          event.contents.add(new ShopScreen.ShopEntry<>(equip, this.getEquipPrice(shop[i].split(":")[1]) * 2));
        //}
      }

      if(item != null) {
        final ItemStack stack = new ItemStack(item, 1);
        /*if(LodMod.getShopIndex(SItem.shopId_8007a3b4) == 40) {
          if("lod:healing_rain".equals(shop[i])) {
            event.contents.add(new ShopScreen.ShopEntry<>(stack, 600));
          } else if("lod:total_vanishing".equals(shop[i])) {
            event.contents.add(new ShopScreen.ShopEntry<>(stack, 400));
          } else if("lod:spirit_potion".equals(shop[i])) {
            event.contents.add(new ShopScreen.ShopEntry<>(stack, 200));
          }
        } else if(LodMod.getShopIndex(SItem.shopId_8007a3b4) == 41) {
          event.contents.add(new ShopScreen.ShopEntry<>(stack, 800));
        } else {*/
          event.contents.add(new ShopScreen.ShopEntry<>(stack, this.getItemPrice(shop[i].split(":")[1]) * 2));
        //}
      }
    }

    if("lod:moon_equipment_shop".equals(shop[32]) && gameState_800babc8.scriptFlags2_bc.get(12, 12)) {
      event.contents.add(new ShopScreen.ShopEntry<>(REGISTRIES.equipment.getEntry(MOD_ID, "firebrand").get(), 5000));
      event.contents.add(new ShopScreen.ShopEntry<>(REGISTRIES.equipment.getEntry(MOD_ID, "jade_spear").get(), 5000));
      event.contents.add(new ShopScreen.ShopEntry<>(REGISTRIES.equipment.getEntry(MOD_ID, "elemental_arrow").get(), 5000));
      event.contents.add(new ShopScreen.ShopEntry<>(REGISTRIES.equipment.getEntry(MOD_ID, "overcharge_glove").get(), 5000));
      event.contents.add(new ShopScreen.ShopEntry<>(REGISTRIES.equipment.getEntry(MOD_ID, "magic_hammer").get(), 5000));
      event.contents.add(new ShopScreen.ShopEntry<>(REGISTRIES.equipment.getEntry(MOD_ID, "giant_axe").get(), 5000));
    }
  }

  @EventListener(priority = Priority.LOWEST)
  public void lastShopContentsEvent(final ShopContentsEvent event) {
  }

  @EventListener(priority = Priority.LOWEST)
  public void ShopSellPriceEvent(final ShopSellPriceEvent event) {
    if(REGISTRIES.equipment.hasEntry(event.inv.getRegistryId())) {
      event.price = this.getEquipPrice(event.inv.getRegistryId().toString());
    } else {
      event.price = this.getItemPrice(event.inv.getRegistryId().toString());
    }
  }

  @EventListener(priority = Priority.HIGHEST)
  public void giveItemEvent(final GiveItemEvent event) {
    final List<ItemStack> newItems = new ArrayList<>();
    for(final ItemStack item : event.givenItems) {
      if("lod".equals(item.getItem().getRegistryId().modId())) {
        final RegistryDelegate<Item> registry = REGISTRIES.items.getEntry(id(item.getItem().getRegistryId().entryId()));
        if(registry != null) {
          newItems.add(new ItemStack(registry.get(), item.getSize(), item.getCurrentDurability()));
        } else {
          newItems.add(item);
        }
      } else {
        newItems.add(item);
      }
    }

    event.givenItems.clear();
    event.givenItems.addAll(newItems);
  }

  @EventListener(priority = Priority.HIGHEST)
  public void giveEquipmentEvent(final GiveEquipmentEvent event) {
    final List<Equipment> newEquipment = new ArrayList<>();
    for(final Equipment equip : event.givenEquipment) {
      if("lod".equals(equip.getRegistryId().modId())) {
        final RegistryDelegate<Equipment> registry = REGISTRIES.equipment.getEntry(id(equip.getRegistryId().entryId()));
        if(registry != null) {
          newEquipment.add(registry.get());
        } else {
          newEquipment.add(equip);
        }
      } else {
        newEquipment.add(equip);
      }
    }

    event.givenEquipment.clear();
    event.givenEquipment.addAll(newEquipment);
  }

  @EventListener
  public void difficultyChanged(final DifficultyChangedEvent event) {
    this.reloadCSV(CONFIG.getConfig(DIFFICULTY.get()));
  }

  @EventListener
  public void renderEvent(final RenderEvent event) {
    if(currentEngineState_8004dd04 instanceof final Battle battle) {
      final BattleMenuStruct58 menu = battle.hud.battleMenu_800c6c34;
      if(menu.displayTargetArrowAndName_4c) {
        final int target = menu.combatantIndex_54;
        if(target != -1) {
          if(menu.targetType_50 == 1) {
            this.selectedTarget(battleState_8006e398.aliveMonsterBents_ebc.get(target).innerStruct_00);
          }
        }
      }
    }
  }
  //endregion

  //region Submap
  @EventListener
  public void submapWarp(final SubmapWarpEvent event) {
    if(this.isHardMode() || this.isHellMode()) {
      if(gameState_800babc8.scriptFlags2_bc.get(0, 7)) {
        for(int i = 0; i < gameState_800babc8.charData_32c.size(); i++) {
          if(gameState_800babc8.charData_32c.get(i).template instanceof Lavitz) {
            gameState_800babc8.charData_32c.get(i).partyFlags_04 = (IN_PARTY) | (CAN_BE_IN_PARTY);
          }
        }
      }

      if(gameState_800babc8.scriptFlags2_bc.get(0, 10)) {
        for(int i = 0; i < gameState_800babc8.charData_32c.size(); i++) {
          if(gameState_800babc8.charData_32c.get(i).template instanceof Shana) {
            gameState_800babc8.charData_32c.get(i).partyFlags_04 = (IN_PARTY) | (CAN_BE_IN_PARTY);
          }
        }
      }

      if(gameState_800babc8.scriptFlags2_bc.get(12, 8)) {
        if(event.submapCut == 729) {
          GameOverlay.addNotification(3, new RawText("PRESS COOLON BUTTON TO WARP TO ULARA."));
        } else if(event.submapCut == 526 || event.submapCut == 527) {
          GameOverlay.addNotification(3, new RawText("PRESS COOLON BUTTON TO WARP TO MOON."));
        }
      }
    }
  }

  @EventListener
  public void inputPressed(final InputPressedEvent event) {
    if(event.action == INPUT_ACTION_WMAP_QUEEN_FURY_COOLON.get()) {
      if(submapCut_80052c30 == 729) {
        ((SMap)currentEngineState_8004dd04).mapTransition(527, 0);
      } else if(submapCut_80052c30 == 526 || submapCut_80052c30 == 527) {
        ((SMap)currentEngineState_8004dd04).mapTransition(729, 0);
      }
    }
  }
  //endregion

  //region Battle
  @EventListener
  public void onRegisterBattleAction(final RegisterBattleActionsEvent event) {
    event.register(DETRANSFORM_ACTION.getId(), new DetransformAction());
    event.register(BURN_STACK_ACTION.getId(), new EnhancementAction(DART.getId()));
    event.register(WIND_BARRIER_ACTION.getId(), new EnhancementAction(LAVITZ.getId()));
    event.register(ELEMENTAL_QUIVER_ACTION.getId(), new EnhancementAction(SHANA.getId()));
    event.register(SIPHON_ACTION.getId(), new EnhancementAction(ROSE.getId()));
    event.register(ALBERT_WIND_BARRIER_ACTION.getId(), new EnhancementAction(ALBERT.getId()));
    event.register(STATIC_CHARGE.getId(), new EnhancementAction(HASCHEL.getId()));
    event.register(WINGLY_MAGIC.getId(), new EnhancementAction(MERU.getId()));
    event.register(COUNTER_STANCE.getId(), new EnhancementAction(KONGOL.getId()));
    event.register(MIRANDA_ELEMENTAL_QUIVER_ACTION.getId(), new EnhancementAction(MIRANDA.getId()));
  }

  @EventListener
  public void battleStarted(final BattleStartedEvent event) {
    Arrays.fill(dartBurnStacks, 0);
    Arrays.fill(dartPreviousMp, 0);
    Arrays.fill(this.dartPreviousBurnStacks, 0);
    Arrays.fill(dartBurnStackMode, false);
    Arrays.fill(this.windMark, 0);
    Arrays.fill(windBarrier, false);
    Arrays.fill(this.stormHPRegenActive, false);
    Arrays.fill(this.shanaPreviousArrow, null);
    Arrays.fill(roseSiphon, 0);
    Arrays.fill(roseSiphonActivated, false);
    Arrays.fill(thunderCharge, 0);
    Arrays.fill(overchargedTurns, 0);
    Arrays.fill(staticCharge, 0);
    Arrays.fill(this.infusedChargedMonsterTurns, 0);
    Arrays.fill(this.meruIceShield, 0);
    Arrays.fill(meruWinglyMagic, false);
    Arrays.fill(kongolCounterStance, false);
    Arrays.fill(this.kongolHitInCounterStance, false);
    Arrays.fill(this.protectionShield, 0);
    Arrays.fill(this.recalcDragoonTurns, false);
    Arrays.fill(this.preventDeathCount, 0);
    Arrays.fill(this.damageTracker[0], 0);
    Arrays.fill(this.damageTracker[1], 0);
    Arrays.fill(this.damageTracker[2], 0);
    Arrays.fill(this.damageTrackerMonsterNames, "");

    this.infusedChargedMonsterPreviousElement = new Element[10];

    this.damageTrackerPrinted = false;
    this.damageTrackerLog.clear();

    for(int i = 0; i < 3; i++) {
      this.shanaUsedElementalArrowsField[i] = new ArrayList<>();
      this.shanaUsedElementalArrowsShift[i] = new ArrayList<>();
    }
    this.shanaElementalField = false;
    this.shanaElementalShift = false;
    this.shanaElementalFieldTurns = 0;
    this.shanaElementalShiftTurns = 0;
    this.shanaElementalFieldActivatorSlot = -1;
    this.shanaElementalShiftActivatorSlot = -1;
    this.shanaElementalCooldownTurns = 0;
    this.haschelInPartyWithDragoon = false;
    this.jadeDragoonPresent = false;
    this.whiteSilverDragoonPresent = false;
    this.dragonBlockStaff = false;

    for(int i = 0; i < battleState_8006e398.getAllBentCount(); i++) {
      final ScriptState<? extends BattleEntity27c> state = battleState_8006e398.allBents_e0c.get(i);
      final BattleEntity27c bent = state.innerStruct_00;
      if(bent instanceof final PlayerBattleEntity player) {
        if(player.character.template instanceof Shana || player.character.template instanceof Miranda) {
          try {
            this.shanaPreviousArrow[player.typeBentSlot_276] = player.equipment_11e.get(EquipmentSlot.WEAPON).getRegistryId();
          } catch(final Exception ignored) {
            this.shanaPreviousArrow[player.typeBentSlot_276] = null;
          }
        } else if(player.character.template instanceof Rose) {
          roseSiphonMax[player.typeBentSlot_276] = player.stats.getStat(HP_STAT.get()).getMax();
        } else if(player.character.template instanceof Haschel) {
          if(gameState_800babc8.goods_19c.has(VIOLET_DRAGOON_SPIRIT.get())) {
            this.haschelInPartyWithDragoon = true;
          }
        } else if(player.character.template instanceof Meru) {
          this.meruIceShieldMax[player.typeBentSlot_276] = player.stats.getStat(HP_STAT.get()).getMax();
          if(gameState_800babc8.goods_19c.has(BLUE_DRAGOON_SPIRIT.get())) {
            this.meruInPartyWithDragoon = true;
          }
        }

        try {
          this.damageTrackerEquips[player.typeBentSlot_276][0] = I18n.translate(player.equipment_11e.get(EquipmentSlot.WEAPON));
          this.damageTrackerEquips[player.typeBentSlot_276][1] = I18n.translate(player.equipment_11e.get(EquipmentSlot.HELMET));
          this.damageTrackerEquips[player.typeBentSlot_276][2] = I18n.translate(player.equipment_11e.get(EquipmentSlot.ARMOUR));
          this.damageTrackerEquips[player.typeBentSlot_276][3] = I18n.translate(player.equipment_11e.get(EquipmentSlot.BOOTS));
          this.damageTrackerEquips[player.typeBentSlot_276][4] = I18n.translate(player.equipment_11e.get(EquipmentSlot.ACCESSORY));
        } catch(final Exception ignored) {
          this.damageTrackerEquips[player.typeBentSlot_276][0] = "";
          this.damageTrackerEquips[player.typeBentSlot_276][1] = "";
          this.damageTrackerEquips[player.typeBentSlot_276][2] = "";
          this.damageTrackerEquips[player.typeBentSlot_276][3] = "";
          this.damageTrackerEquips[player.typeBentSlot_276][4] = "";
        }

        if(player.character.template instanceof Lavitz || player.character.template instanceof Albert) {
          if(this.jadeDragoonPresent && this.isHardMode()) {
            final int newHP = (int)Math.round(player.stats.getStat(HP_STAT.get()).getMax() * 0.7);
            final int newAT = (int)Math.round(player.stats.getStat(MAGIC_ATTACK_STAT.get()).get() * 0.5);
            final int newMAT = (int)Math.round(player.stats.getStat(ATTACK_STAT.get()).get() * 0.5);
            final int newDF = player.stats.getStat(MAGIC_DEFENSE_STAT.get()).get();
            final int newMDF = player.stats.getStat(DEFENSE_STAT.get()).get();
            final VitalsStat hp = player.stats.getStat(HP_STAT.get());

            hp.setMaxRaw(newHP);
            player.stats.getStat(ATTACK_STAT.get()).setRaw(newAT);
            player.stats.getStat(MAGIC_ATTACK_STAT.get()).setRaw(newMAT);
            player.stats.getStat(DEFENSE_STAT.get()).setRaw(newDF);
            player.stats.getStat(MAGIC_DEFENSE_STAT.get()).setRaw(newMDF);
          } else {
            this.jadeDragoonPresent = true;
          }
        }

        if(player.character.template instanceof Shana || player.character.template instanceof Miranda) {
          if(this.whiteSilverDragoonPresent && this.isHardMode()) {
            final int newHP = (int)Math.round(player.stats.getStat(HP_STAT.get()).getMax() * 0.7);
            final int newAT = (int)Math.round(player.stats.getStat(MAGIC_ATTACK_STAT.get()).get() * 0.5);
            final int newMAT = (int)Math.round(player.stats.getStat(ATTACK_STAT.get()).get() * 0.5);
            final int newDF = player.stats.getStat(MAGIC_DEFENSE_STAT.get()).get();
            final int newMDF = player.stats.getStat(DEFENSE_STAT.get()).get();
            final VitalsStat hp = player.stats.getStat(HP_STAT.get());

            hp.setMaxRaw(newHP);
            player.stats.getStat(ATTACK_STAT.get()).setRaw(newAT);
            player.stats.getStat(MAGIC_ATTACK_STAT.get()).setRaw(newMAT);
            player.stats.getStat(DEFENSE_STAT.get()).setRaw(newDF);
            player.stats.getStat(MAGIC_DEFENSE_STAT.get()).setRaw(newMDF);
          } else {
            this.whiteSilverDragoonPresent = true;
          }
        }

        if(this.getEquipment("dragoon_modifier:phantom_shield", player, EquipmentSlot.ACCESSORY)) {
          player.stats.getStat(LodMod.DEFENSE_STAT.get()).setRaw((int)Math.round(player.stats.getStat(LodMod.DEFENSE_STAT.get()).get() * 0.75d));
          player.stats.getStat(LodMod.MAGIC_DEFENSE_STAT.get()).setRaw((int)Math.round(player.stats.getStat(LodMod.MAGIC_DEFENSE_STAT.get()).get() * 0.75d));
        }

        if(this.getEquipment("dragoon_modifier:dragon_shield", player, EquipmentSlot.ACCESSORY)) {
          player.stats.getStat(LodMod.DEFENSE_STAT.get()).setRaw((int)Math.round(player.stats.getStat(LodMod.DEFENSE_STAT.get()).get() * 0.75d));
        }

        if(this.getEquipment("dragoon_modifier:angel_scarf", player, EquipmentSlot.ACCESSORY)) {
          player.stats.getStat(LodMod.MAGIC_DEFENSE_STAT.get()).setRaw((int)Math.round(player.stats.getStat(LodMod.MAGIC_DEFENSE_STAT.get()).get() * 0.75d));
        }

        if(this.getEquipment("dragoon_modifier:armor_of_legend", player, EquipmentSlot.ARMOUR)) {
          this.preventDeathCount[player.typeBentSlot_276] += 1;
        }

        if(this.getEquipment("dragoon_modifier:legend_casque", player, EquipmentSlot.HELMET)) {
          this.preventDeathCount[player.typeBentSlot_276] += 1;
        }

        if(this.getEquipment("dragoon_modifier:firebrand", player, EquipmentSlot.WEAPON)) {
          player.equipmentAttackElements_1c.clear();
          player.equipmentAttackElements_1c.add(DIVINE_ELEMENT.get());
          player.equipmentAttackElements_1c.add(FIRE_ELEMENT.get());
        }
      } else if(bent instanceof MonsterBattleEntity) {
        final MonsterBattleEntity monster = battleState_8006e398.monsterBents_e50[i].innerStruct_00;
        final int hp = monster.stats.getStat(HP_STAT.get()).getCurrent();
        this.damageTrackerPreviousHP[monster.typeBentSlot_276] = hp;
        this.damageTrackerMonsterNames[monster.typeBentSlot_276] = monster.getName();
      }
    }
  }

  @EventListener(priority = Priority.LOWEST)
  public void gatherBattleActions(final GatherBattleActionsEvent event) {
    if(this.isHardMode() || this.isHellMode()) {
      if(event.player.isDragoon()) {
        event.actions.put(DETRANSFORM_ACTION.get(), (event.actions.size() + 1) * 100 - 1);
      }

      if(event.player.character.template instanceof Dart) {
        event.actions.put(BURN_STACK_ACTION.get(), (event.actions.size() + 1) * 100);
      } else if(event.player.character.template instanceof Lavitz || event.player.character.template instanceof Albert) {
        event.actions.put(WIND_BARRIER_ACTION.get(), (event.actions.size() + 1) * 100);
      } else if(event.player.character.template instanceof Shana) {
        if(this.shanaPreviousArrow[event.player.typeBentSlot_276] != null) {
          event.actions.put(ELEMENTAL_QUIVER_ACTION.get(), (event.actions.size() + 1) * 100);
        }
      } else if(event.player.character.template instanceof Rose) {
        event.actions.put(SIPHON_ACTION.get(), (event.actions.size() + 1) * 100);
      } else if(event.player.character.template instanceof Haschel) {
        if(this.haschelInPartyWithDragoon) {
          event.actions.put(STATIC_CHARGE.get(), (event.actions.size() + 1) * 100);
        }
      } else if(event.player.character.template instanceof Meru) {
        if(this.meruInPartyWithDragoon) {
          event.actions.put(WINGLY_MAGIC.get(), (event.actions.size() + 1) * 100);
        }
      } else if(event.player.character.template instanceof Kongol) {
        if(!kongolCounterStance[event.player.typeBentSlot_276]) {
          event.actions.put(COUNTER_STANCE.get(), (event.actions.size() + 1) * 100);
        } else {
          if(this.kongolHitInCounterStance[event.player.typeBentSlot_276]) {
            event.actions.clear();
            if(event.player.isDragoon()) {
              event.actions.put(LodBattleActions.D_ATTACK.get(), 0);
            } else {
              event.actions.put(LodBattleActions.ATTACK.get(), 0);
            }
            this.kongolHitInCounterStance[event.player.typeBentSlot_276] = false;
          } else {
            event.actions.clear();
            event.actions.put(LodBattleActions.GUARD.get(), 0);
          }
        }
      }
    }
  }

  @EventListener
  public void battleEntityTurn(final BattleEntityTurnEvent<?> event) {
    if(event.bent instanceof final PlayerBattleEntity player) {
      this.updateItemMagicDamageTracker();
      this.damageTrackerPreviousCharacter = player;
    }

    if(this.isHardMode() || this.isHellMode()) {
      this.damageOverride = 0;

      if(event.bent instanceof final PlayerBattleEntity player) {
        this.damageTrackerLog.add(player.getName() + " Turn Started");
        this.currentPlayer = player;

        if(player.isDragoon()) {
          this.recaluteBentDragoonTurns(player);
        }

        if(player.character.template instanceof Dart) {
          this.dartBurnAdded[player.typeBentSlot_276] = false;

          if(dartBurnStackMode[player.typeBentSlot_276]) {
            if(dartBurnStacks[player.typeBentSlot_276] == DART_BURN_STACKS_MAX) {
              player.stats.getStat(MP_STAT.get()).setCurrent(dartPreviousMp[player.typeBentSlot_276]);
            }
            dartBurnStacks[player.typeBentSlot_276] = 0;
            this.dartPreviousBurnStacks[player.typeBentSlot_276] = 0;
            dartBurnStackMode[player.typeBentSlot_276] = false;
          }
        }

        if(player.character.template instanceof Lavitz || player.character.template instanceof Albert) {
          if(windBarrier[player.typeBentSlot_276]) {
            final int currentMP = player.stats.getStat(MP_STAT.get()).getCurrent();
            if(currentMP >= 10) {
              player.setStat(BattleEntityStat.CURRENT_MP, currentMP - 10);
            } else if(currentMP == 0) {
              windBarrier[player.typeBentSlot_276] = false;
            }
          }
        }

        if(player.stats.getStat(STORM_HP_REGEN.get()).get() == 0 && this.stormHPRegenActive[player.typeBentSlot_276]) {
          player.setStat(BattleEntityStat.HP_REGEN, player.hpRegen_134 - STORM_REGEN_AMOUNT);
          this.stormHPRegenActive[player.typeBentSlot_276] = false;
        }

        if(player.character.template instanceof Shana || player.character.template instanceof Miranda) {
          this.shanaDeffArrow[player.typeBentSlot_276] = false;

          if(this.shanaElementalFieldTurns > 0 && this.shanaElementalField && this.shanaElementalFieldActivatorSlot == player.typeBentSlot_276) {
            this.shanaElementalFieldTurns--;

            GameOverlay.addNotification(3, new RawText("ELEMENTAL FIELD TURNS LEFT: " + this.shanaElementalFieldTurns));
            if(this.shanaElementalFieldTurns == 0) {
              this.shanaElementalField = false;
            }
          }

          if(this.shanaElementalShiftTurns > 0 && this.shanaElementalShift && this.shanaElementalShiftActivatorSlot == player.typeBentSlot_276) {
            this.shanaElementalShiftTurns--;
            GameOverlay.addNotification(3, new RawText("ELEMENTAL SHIFT TURNS LEFT: " + this.shanaElementalShiftTurns));

            if(this.shanaElementalShiftTurns == 0) {
              this.shanaElementalShift = false;
              this.shanaElementalCooldownTurns = 4;
            }
          }

          if(this.shanaElementalCooldownTurns > 0 && this.shanaElementalShiftActivatorSlot == player.typeBentSlot_276) {
            this.shanaElementalCooldownTurns--;

            GameOverlay.addNotification(3, new RawText("ELEMENTAL COOLDOWN TURNS LEFT: " + this.shanaElementalShiftTurns));

            if(this.shanaElementalCooldownTurns == 0) {
              for(int i = 0; i < 3; i++) {
                this.shanaUsedElementalArrowsField[i] = new ArrayList<>();
                this.shanaUsedElementalArrowsShift[i] = new ArrayList<>();
              }
            }
          }
        }

        if(player.character.template instanceof Meru) {
          if(meruWinglyMagic[player.typeBentSlot_276]) {
            final int currentMP = player.stats.getStat(MP_STAT.get()).getCurrent();
            if(currentMP >= 10) {
              player.setStat(BattleEntityStat.CURRENT_MP, currentMP - 10);
            } else {
              meruWinglyMagic[player.typeBentSlot_276] = false;
              player.stats.getStat(GUARD_HEAL_STAT.get()).setRaw(10);
            }
          }
        }

        if(player.character.template instanceof Kongol) {
          if(kongolCounterStance[player.typeBentSlot_276] && kongolCounterStanceTurns[player.typeBentSlot_276] == 0) {
            kongolCounterStance[player.typeBentSlot_276] = false;
            player.stats.getStat(LodMod.SPEED_STAT.get()).removeMod(DragoonModifier.COUNTER_STANCE_SLOWDOWN.getId());
          }

          if(kongolCounterStance[player.typeBentSlot_276]) {
            kongolCounterStanceTurns[player.typeBentSlot_276] -= 1;
            player.stats.getStat(HP_STAT.get()).setCurrent((int)(player.stats.getStat(HP_STAT.get()).getCurrent() * 1.05));
          }
        }
      }

      if(event.bent instanceof final MonsterBattleEntity monster) {
        if(overchargedTurns[monster.typeBentSlot_276] > 0) {
          overchargedTurns[monster.typeBentSlot_276] -= 1;
          GameOverlay.addNotification(3, new RawText("OVERCHARGED TURNS LEFT: " + overchargedTurns[monster.typeBentSlot_276]));
        }

        if(this.infusedChargedMonsterTurns[monster.typeBentSlot_276] > 0) {
          this.infusedChargedMonsterTurns[monster.typeBentSlot_276] -= 1;

          if(this.infusedChargedMonsterTurns[monster.typeBentSlot_276] == 0) {
            monster.element = this.infusedChargedMonsterPreviousElement[monster.typeBentSlot_276];
          }
        }
      }
    }
  }

  @EventListener(priority = Priority.HIGHEST)
  public void activeSpell(final ActiveSpellEvent event) {
    if(event.bent instanceof final MonsterBattleEntity monster) {
      if(!REGISTRIES.monsterSpells.getEntry(MOD_ID, event.registryId.entryId()).toString().isBlank()) {
        event.spell = REGISTRIES.monsterSpells.getEntry(MOD_ID, event.registryId.entryId()).get();

        if(this.isHardMode()) {
          if(monster.charId_272 == 283) { //Divine Dragon
            if("spell53".equals(event.registryId.entryId()))  { //Divine Dragon Ball
              event.spell = REGISTRIES.monsterSpells.getEntry(MOD_ID, "divine_dragon_ball").get();
            } else if("spell116".equals(event.registryId.entryId())) { //Divine Dragon Cannon
              event.spell = REGISTRIES.monsterSpells.getEntry(MOD_ID, "divine_dragon_cannon").get();
            }
          } else if(monster.charId_272 == 352) { //Divine Dragon Ghost
            if("spell53".equals(event.registryId.entryId()))  { //Divine Dragon Ball
              event.spell = REGISTRIES.monsterSpells.getEntry(MOD_ID, "divine_dragon_ghost_ball").get();
            } else if("spell55".equals(event.registryId.entryId())) { //Divine Dragon Cannon
              event.spell = REGISTRIES.monsterSpells.getEntry(MOD_ID, "divine_dragon_ghost_cannon").get();
            }
          } else if(monster.charId_272 == 363) { //Zackwell
            if("spell33".equals(event.registryId.entryId()))  { //Physical
              event.spell = REGISTRIES.monsterSpells.getEntry(MOD_ID, "zackwell_physical").get();
            } else if("spell69".equals(event.registryId.entryId())) { //Fireball
              event.spell = REGISTRIES.monsterSpells.getEntry(MOD_ID, "zackwell_fireball").get();
            } else if("spell124".equals(event.registryId.entryId())) { //Summon
              event.spell = REGISTRIES.monsterSpells.getEntry(MOD_ID, "zackwell_summon").get();
            }
          }
        }
      } else {
        throw new RuntimeException("UNKNOWN SPELL REGISTRY ID: " + event.registryId);
      }
    } else {
      event.spell = REGISTRIES.spells.getEntry(MOD_ID, event.spell.getRegistryId().entryId()).get();
    }
  }

  @EventListener(priority = Priority.HIGHEST)
  public void spellStats(final SpellStatsEvent event) {
    if(REGISTRIES.spells.hasEntry(id(event.spell.getRegistryId().entryId()))) {
      event.spell = REGISTRIES.spells.getEntry(MOD_ID, event.spell.getRegistryId().entryId()).get();
    } else {
      event.spell = REGISTRIES.monsterSpells.getEntry(MOD_ID, event.spell.getRegistryId().entryId()).get();
    }
  }

  @EventListener(priority = Priority.HIGHEST)
  public void activeItem(final ActiveItemEvent event) {
    if(event.bent instanceof MonsterBattleEntity) {
      if(!REGISTRIES.items.getEntry(MOD_ID, event.registryId.entryId()).toString().isBlank()) {
        event.item = new ItemStack(REGISTRIES.items.getEntry(MOD_ID, event.registryId.entryId()).get());
      } else {
        throw new RuntimeException("UNKNOWN ITEM REGISTRY ID: " + event.registryId);
      }
    } else {
      event.item = new ItemStack(REGISTRIES.items.getEntry(MOD_ID, event.item.getItem().getRegistryId().entryId()).get());
    }
  }

  @EventListener(priority = Priority.HIGHEST)
  public void attack(final AttackEvent attack) {
    Arrays.fill(this.recalcDragoonTurns, false);

    if(attack.damage > 0) { //GLOBAL MODIFIER
      //System.out.println("BEFORE ATTACK EVENT: " + attack.damage);
      //Damage multiplier for spells and items
      if(attack.attackType == AttackType.DRAGOON_MAGIC_STATUS_ITEMS) {
        final SpellStats0c spell = attack.attacker.spell_94;
        switch(spell.damageMultiplier_03) {
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
            attack.damage *= (int)(spell.damageMultiplier_03 / 100d);
        }
      } else if(attack.attackType == AttackType.ITEM_MAGIC) {
        final int itemDamageMultiplier = attack.attacker.item_d4.getAttackDamageMultiplier(attack.attacker, attack.defender);
        switch(itemDamageMultiplier) {
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
            attack.damage *= (int)(itemDamageMultiplier / 100d);
        }
      }

      if(attack.attacker instanceof MonsterBattleEntity) {
        if(this.damageOverride > 0) {
          attack.damage *= this.damageOverride;
        }
      }

      if(this.isHardMode() || this.isHellMode()) {
        if(this.shanaElementalField && this.shanaElementalFieldTurns < 4) {
          if(this.shanaElementalFieldMode == 1) {
            if(attack.attackType.isPhysical() && attack.attacker.getAttackElements().contains(this.shanaElementalFieldElement1)) {
              attack.damage *= 1.12;
            }
            if(attack.attackType == AttackType.DRAGOON_MAGIC_STATUS_ITEMS) {
              if(attack.attacker.spell_94.element_08.get() == this.shanaElementalFieldElement1) {
                attack.damage *= 1.12;
              }
            } else if(attack.attackType == AttackType.ITEM_MAGIC && attack.attacker.item_d4.getItem() instanceof AttackItem) {
              if(attack.attacker.item_d4.getAttackElement() == this.shanaElementalFieldElement1) {
                attack.damage *= 1.12;
              }
            }
          } else if(this.shanaElementalFieldMode == 2) {
            if(attack.attackType.isPhysical() && attack.attacker.getAttackElements().contains(this.shanaElementalFieldElement2)) {
              attack.damage *= 1.08;
            }
            if(attack.attackType == AttackType.DRAGOON_MAGIC_STATUS_ITEMS) {
              if(attack.attacker.spell_94.element_08.get() == this.shanaElementalFieldElement2) {
                attack.damage *= 1.08;
              }
            } else if(attack.attackType == AttackType.ITEM_MAGIC && attack.attacker.item_d4.getItem() instanceof AttackItem) {
              if(attack.attacker.item_d4.getAttackElement() == this.shanaElementalFieldElement2) {
                attack.damage *= 1.08;
              }
            }

            if(attack.attackType.isPhysical() && attack.attacker.getAttackElements().contains(this.shanaElementalFieldElement1)) {
              attack.damage *= 0.96;
            }
            if(attack.attackType == AttackType.DRAGOON_MAGIC_STATUS_ITEMS) {
              if(attack.attacker.spell_94.element_08.get() == this.shanaElementalFieldElement1) {
                attack.damage *= 0.96;
              }
            } else if(attack.attackType == AttackType.ITEM_MAGIC && attack.attacker.item_d4.getItem() instanceof AttackItem) {
              if(attack.attacker.item_d4.getAttackElement() == this.shanaElementalFieldElement1) {
                attack.damage *= 0.96;
              }
            }
          } else {
            final List<Element> arrows = new ArrayList<>();
            for(final String arrow : this.shanaUsedElementalArrowsField[this.shanaElementalFieldActivatorSlot]) {
              arrows.add(this.getElementFromArrow(arrow));
            }

            if(attack.attackType.isPhysical() && arrows.contains(attack.attacker.getAttackElements().iterator().next())) {
              attack.damage *= 0.85; //TODO loop?
            } else {
              if(attack.attackType == AttackType.DRAGOON_MAGIC_STATUS_ITEMS) {
                if(arrows.contains(attack.attacker.spell_94.element_08.get())) {
                  attack.damage *= 0.85;
                }
              } else if(attack.attackType == AttackType.ITEM_MAGIC) {
                if(arrows.contains(attack.attacker.item_d4.getAttackElement())) {
                  attack.damage *= 0.85;
                }
              }
            }
          }
        }


        //PLAYER ATTACKING MONSTER
        if(attack.attacker instanceof final PlayerBattleEntity player) {
          if(attack.defender instanceof final MonsterBattleEntity monster) {
            if(this.isHardMode() || this.isHellMode()) {
              if(player.isDragoon() && attack.attackType.isPhysical()) {
                if(player.element == attack.battle.dragoonSpaceElement_800c6b64) { //Dragoon Space and element matches the character.
                  if(player.character.template instanceof Kongol) {
                    attack.damage *= 1.2;
                  } else {
                    attack.damage *= 1.5;
                  }
                } else {
                  if(player.character.template instanceof Dart && attack.battle.dragoonSpaceElement_800c6b64 == Element.fromFlag(0x8).get()) { //If Divine Dart
                    attack.damage *= 1.5;
                  }
                }
              }

              if(player.character.template instanceof Dart) {
                if(attack.attackType == AttackType.DRAGOON_MAGIC_STATUS_ITEMS && !this.dartBurnAdded[attack.attacker.typeBentSlot_276] && !dartBurnStackMode[player.typeBentSlot_276]) {
                  if("flameshot".equals(player.spell_94.getRegistryId().entryId())) {
                    this.addBurnStacks(player, this.BURN_STACKS_FLAMESHOT);
                  } else if("explosion".equals(player.spell_94.getRegistryId().entryId())) {
                    this.addBurnStacks(player, this.BURN_STACKS_EXPLOSION);
                  } else if("final_burst".equals(player.spell_94.getRegistryId().entryId())) {
                    this.addBurnStacks(player, this.BURN_STACKS_FINAL_BURST);
                  } else if("red_eyed_dragon".equals(player.spell_94.getRegistryId().entryId())) {
                    this.addBurnStacks(player, this.BURN_STACKS_RED_EYED_DRAGON);
                  } else if("divine_dg_ball".equals(player.spell_94.getRegistryId().entryId())) {
                    this.addBurnStacks(player, this.BURN_STACKS_FINAL_BURST);
                  } else if("divine_dg_cannon".equals(player.spell_94.getRegistryId().entryId())) {
                    this.addBurnStacks(player, this.BURN_STACKS_FINAL_BURST);
                  }
                } else if(attack.attackType == AttackType.PHYSICAL && player.isDragoon()) {
                  this.addBurnStacks(player, this.BURN_STACKS_ADDITION);
                }

                if(dartBurnStackMode[player.typeBentSlot_276]) {
                  if(dartBurnStacks[player.typeBentSlot_276] == DART_BURN_STACKS_MAX) {
                    if(attack.attackType == AttackType.DRAGOON_MAGIC_STATUS_ITEMS) {
                      if("flameshot".equals(player.spell_94.getRegistryId().entryId())) {
                        attack.damage *= 1 + (dartBurnStacks[player.typeBentSlot_276] * this.dmgPerBurn) * 2.25;
                      } else if("explosion".equals(player.spell_94.getRegistryId().entryId())) {
                        attack.damage *= 1 + (dartBurnStacks[player.typeBentSlot_276] * this.dmgPerBurn) * 3;
                      } else if("final_burst".equals(player.spell_94.getRegistryId().entryId())) {
                        attack.damage *= 1 + (dartBurnStacks[player.typeBentSlot_276] * this.dmgPerBurn) * 1.5;
                      } else if("red_eyed_dragon".equals(player.spell_94.getRegistryId().entryId())) {
                        attack.damage *= 1 + (dartBurnStacks[player.typeBentSlot_276] * this.dmgPerBurn);
                      } else {
                        attack.damage *= 1 + (dartBurnStacks[player.typeBentSlot_276] * this.dmgPerBurn);
                      }
                    } else {
                      attack.damage *= 1 + (dartBurnStacks[player.typeBentSlot_276] * this.dmgPerBurn);
                    }
                  } else {
                    attack.damage *= 1 + (dartBurnStacks[player.typeBentSlot_276] * this.dmgPerBurn);
                  }
                }
              }

              if(this.windMark[monster.typeBentSlot_276] > 0) {
                monster.turnValue_4c = Math.max(0, monster.turnValue_4c - 30);
                this.windMark[monster.typeBentSlot_276] -= 1;
              }

              if(player.character.template instanceof Lavitz || player.character.template instanceof Albert) {
                if(player.isDragoon()) {
                  if("wing_blaster".equals(player.spell_94.getRegistryId().entryId()) || "albert_wing_blaster".equals(player.spell_94.getRegistryId().entryId())) {
                    this.windMark[monster.typeBentSlot_276] = 1;
                    this.displayNumbers(11 + monster.typeBentSlot_276, this.windMark[monster.typeBentSlot_276], 0, -12, 0f, 1f, 0f);
                  } else if("gaspless".equals(player.spell_94.getRegistryId().entryId()) || "albert_gaspless".equals(player.spell_94.getRegistryId().entryId())) {
                    this.windMark[monster.typeBentSlot_276] = 2;
                    this.displayNumbers(11 + monster.typeBentSlot_276, this.windMark[monster.typeBentSlot_276], 0, -12, 0f, 1f, 0f);
                  } else if("jade_dragon".equals(player.spell_94.getRegistryId().entryId())) {
                    this.windMark[monster.typeBentSlot_276] = 3;
                    this.displayNumbers(11 + monster.typeBentSlot_276, this.windMark[monster.typeBentSlot_276], 0, -12, 0f, 1f, 0f);
                  }
                }

                if(attack.attackType == AttackType.PHYSICAL && this.getEquipment("dragoon_modifier:jade_spear", player, EquipmentSlot.WEAPON)) {
                  this.windMark[monster.typeBentSlot_276] += 1;
                  if(this.windMark[monster.typeBentSlot_276] > 4) {
                    this.windMark[monster.typeBentSlot_276] = 4;
                  }
                }
              }

              if(player.character.template instanceof Shana || player.character.template instanceof Miranda) {
                if(attack.attackType.isPhysical()) {
                  final int level = player.level_04;
                  double boost = 1;
                  if(this.getEquipment("dragoon_modifier:detonate_arrow", player, EquipmentSlot.WEAPON)) {
                    boost = 1.4;
                  } else if(level >= 28) {
                    boost = 2.15;
                  } else if(level >= 20) {
                    boost = 1.9;
                  } else if(level >= 10) {
                    boost = 1.6;
                  }
                  attack.damage = (int)Math.round(attack.damage * boost);

                  if(this.shanaElementalShift && this.shanaElementalShiftMode == 3) {
                    attack.damage *= 3.25;
                  }
                }

                if(this.shanaDeffArrow[player.typeBentSlot_276]) {
                  if(this.getEquipment("dragoon_modifier:elemental_arrow", player, EquipmentSlot.WEAPON)) {
                    if(!player.isDragoon()) {
                      attack.damage = (int)Math.round(attack.damage * 0.8d);
                    }
                  } else {
                    if(!player.isDragoon()) {
                      attack.damage = (int)Math.round(attack.damage * 0.6d);
                    } else {
                      attack.damage = (int)Math.round(attack.damage * 0.8d);
                    }
                  }
                  this.removeArrow();

                  if(this.shanaElementalShift) {
                    if(this.shanaElementalShiftMode == 2) {
                      if(attack.attacker.item_d4.getAttackElement() == this.shanaElementalShiftElement1) {
                        attack.damage *= 1.75;
                        if(this.shanaElementalShiftElement1 == THUNDER_ELEMENT.get() && monster.getElement() == THUNDER_ELEMENT.get()) {
                          attack.damage *= 1.5;
                        }
                      }
                    } else if(this.shanaElementalShiftMode == 4) {
                      final ArrayList<Element> elementalReaction = new ArrayList<>();
                      elementalReaction.add(this.shanaElementalFieldElement1);
                      elementalReaction.add(this.shanaElementalShiftElement1);

                      if(elementalReaction.contains(attack.attacker.item_d4.getAttackElement())) {
                        if(elementalReaction.contains(FIRE_ELEMENT.get()) || elementalReaction.contains(WATER_ELEMENT.get())) {
                          for(int i = 0; i < battleState_8006e398.alivePlayerBents_eac.size(); i++) {
                            final ScriptState<? extends BattleEntity27c> state = battleState_8006e398.alivePlayerBents_eac.get(i);
                            final PlayerBattleEntity bent = (PlayerBattleEntity)state.innerStruct_00;
                            final int healMp = (int)Math.min(49, Math.round(attack.damage * 0.025)) + 1;
                            bent.stats.getStat(MP_STAT.get()).setCurrent(Math.min(bent.stats.getStat(MP_STAT.get()).getMax(), (bent.stats.getStat(MP_STAT.get()).getCurrent() + healMp)));
                            this.displayNumbers(6 + bent.typeBentSlot_276, healMp, 0, -12, 0.56666666f, 0.53333336f, 1.0333333f);
                          }
                        } else if(elementalReaction.contains(WIND_ELEMENT.get()) || elementalReaction.contains(EARTH_ELEMENT.get())) {
                          for(int i = 0; i < battleState_8006e398.alivePlayerBents_eac.size(); i++) {
                            final ScriptState<? extends BattleEntity27c> state = battleState_8006e398.alivePlayerBents_eac.get(i);
                            final PlayerBattleEntity bent = (PlayerBattleEntity)state.innerStruct_00;
                            final int healSp = (int)Math.min(100, Math.round(attack.damage * 0.04));
                            bent.stats.getStat(SP_STAT.get()).setCurrent(Math.min(bent.stats.getStat(SP_STAT.get()).getMax(), (bent.stats.getStat(SP_STAT.get()).getCurrent() + healSp)));
                            this.displayNumbers(6 + bent.typeBentSlot_276, healSp, 0, -12, 0.3f, 1.0333333f, 0.3f);
                            this.recaluteBentDragoonTurns(bent);
                          }
                        } else if(elementalReaction.contains(DARK_ELEMENT.get()) || elementalReaction.contains(LIGHT_ELEMENT.get())) {
                          for(int i = 0; i < battleState_8006e398.alivePlayerBents_eac.size(); i++) {
                            final ScriptState<? extends BattleEntity27c> state = battleState_8006e398.alivePlayerBents_eac.get(i);
                            final PlayerBattleEntity bent = (PlayerBattleEntity)state.innerStruct_00;
                            final int healHp = (int)Math.min(100, Math.round(attack.damage * 0.25));
                            bent.stats.getStat(HP_STAT.get()).setCurrent(Math.min(bent.stats.getStat(HP_STAT.get()).getMax(), (bent.stats.getStat(HP_STAT.get()).getCurrent() + healHp)));
                            this.displayNumbers(6 + bent.typeBentSlot_276, healHp, 0, -24, 0.4f, 0.93333334f, 1.0333333f);
                          }
                        }
                      }
                    } else if(this.shanaElementalShiftMode == 5) {
                      boolean addedWindMark = false;
                      boolean spArrow = false;
                      for(int i = 0; i < battleState_8006e398.alivePlayerBents_eac.size(); i++) {
                        final ScriptState<? extends BattleEntity27c> state = battleState_8006e398.alivePlayerBents_eac.get(i);
                        final PlayerBattleEntity bent = (PlayerBattleEntity)state.innerStruct_00;
                        if(bent.character.template instanceof Dart && player.item_d4.getAttackElement() == FIRE_ELEMENT.get()) { //TODO block for divine element?
                          this.addBurnStacks(bent, 1);
                        } else if((bent.character.template instanceof Lavitz || bent.character.template instanceof Albert) && player.item_d4.getAttackElement() == WIND_ELEMENT.get()) {
                          if(!addedWindMark) {
                            this.windMark[monster.typeBentSlot_276] = Math.min(4, this.windMark[monster.typeBentSlot_276] + 1);
                            addedWindMark = true;
                          }
                        } else if((bent.character.template instanceof Shana || bent.character.template instanceof Miranda) && player.item_d4.getAttackElement() == LIGHT_ELEMENT.get()) {
                          if(!spArrow) {
                            bent.stats.getStat(SP_STAT.get()).setCurrent(Math.min(bent.stats.getStat(SP_STAT.get()).getMax(), (bent.stats.getStat(SP_STAT.get()).getCurrent() + 100)));
                            this.displayNumbers(6 + bent.typeBentSlot_276, 100, 0, -12, 0.3f, 1.0333333f, 0.3f);
                            spArrow = true;
                          }
                        } else if(bent.character.template instanceof Rose && player.item_d4.getAttackElement() == DARK_ELEMENT.get()) {
                          if(attack.damage > 0) {
                            final double roseSiphonMultiplier = 0.20d;
                            roseSiphon[bent.typeBentSlot_276] = Math.min(roseSiphonMax[bent.typeBentSlot_276], (int)Math.min(Math.round(roseSiphonMax[bent.typeBentSlot_276] * roseSiphonMultiplier + roseSiphon[bent.typeBentSlot_276]), Math.round(roseSiphon[bent.typeBentSlot_276] + attack.damage * roseSiphonMultiplier)));
                            this.displayNumbers(6 + bent.typeBentSlot_276, roseSiphon[bent.typeBentSlot_276], 0, 0, 0f, 0.5f, 1.0f);
                          }
                        } else if(bent.character.template instanceof Haschel && player.item_d4.getAttackElement() == THUNDER_ELEMENT.get() && this.haschelInPartyWithDragoon) {
                          if(attack.damage > 0) {
                            if(new Random().nextBoolean()) {
                              thunderCharge[monster.typeBentSlot_276] = Math.min(10, thunderCharge[monster.typeBentSlot_276] + 1);
                              this.displayNumbers(11 + monster.typeBentSlot_276, 1, 10, -12, 0.5f, 0f, 1.0f);
                            }
                          }
                        } else if(bent.character.template instanceof Meru && this.meruInPartyWithDragoon && player.item_d4.getAttackElement() == WATER_ELEMENT.get()) {
                          this.meruIceShield[player.typeBentSlot_276] = (int)Math.min(this.meruIceShieldMax[player.typeBentSlot_276], Math.min(this.meruIceShieldMax[player.typeBentSlot_276] * 0.25 + this.meruIceShield[player.typeBentSlot_276], this.meruIceShield[player.typeBentSlot_276] + attack.damage * 0.1));
                          this.displayNumbers(6 + bent.typeBentSlot_276, this.meruIceShield[player.typeBentSlot_276], 0, -12, 1, 1, 1);
                        } else if(bent.character.template instanceof Kongol && player.item_d4.getAttackElement() == EARTH_ELEMENT.get()) {
                          this.protectionShield[bent.typeBentSlot_276] = (int)Math.min(player.stats.getStat(HP_STAT.get()).getMax() * 0.15 + this.protectionShield[bent.typeBentSlot_276], this.protectionShield[bent.typeBentSlot_276] + attack.damage * 0.1);
                          this.displayNumbers(6 + bent.typeBentSlot_276, this.protectionShield[player.typeBentSlot_276], 0, -12, 1.0333333f, 0.6666667f, 0.0f);
                        }
                      }
                    }
                  }
                }

                if(player.item_d4 != null && !player.isDragoon()) {
                  final int sp = player.stats.getStat(SP_STAT.get()).getCurrent();
                  int gain = !this.shanaDeffArrow[player.typeBentSlot_276] ? 50 : player.dlevel_06 > 0 ? Integer.parseInt(shanaSpGain.getFirst()[player.dlevel_06 - 1]) : 0;
                  if(this.shanaDeffArrow[player.typeBentSlot_276]) {
                    gain *= (1d + (player.spMultiplier_128 / 100d));
                  }
                  spGained_800bc950.mergeInt(player.character, gain, Integer::sum);
                  player.stats.getStat(SP_STAT.get()).setCurrent(Math.min(player.stats.getStat(SP_STAT.get()).getMax(), (player.stats.getStat(SP_STAT.get()).getCurrent() + gain)));
                  this.recaluteBentDragoonTurns(player);
                }
              }

              if(player.character.template instanceof Rose) {
                if(roseSiphonActivated[player.typeBentSlot_276]) {
                  attack.damage += roseSiphon[player.typeBentSlot_276];
                  roseSiphon[player.typeBentSlot_276] = 0;
                }

                if(player.isDragoon()) {
                  if(attack.attackType == AttackType.DRAGOON_MAGIC_STATUS_ITEMS) {
                    if("astral_drain".equals(player.spell_94.getRegistryId().entryId())) {
                      for(int i = 0; i < battleState_8006e398.alivePlayerBents_eac.size(); i++) {
                        final ScriptState<? extends BattleEntity27c> state = battleState_8006e398.alivePlayerBents_eac.get(i);
                        final BattleEntity27c bent = state.innerStruct_00;
                        final int playerHealedHP = bent.stats.getStat(HP_STAT.get()).getCurrent();
                        final int roseMaxHP = player.stats.getStat(HP_STAT.get()).getMax();
                        final int heal = (int)Math.round(roseMaxHP * player.dlevel_06 * 0.0425d);
                        bent.stats.getStat(HP_STAT.get()).setCurrent(Math.min(bent.stats.getStat(HP_STAT.get()).getMax(), (playerHealedHP + heal)));
                        this.displayNumbers(6 + bent.typeBentSlot_276, heal, 0, -12, 1.0f, 0f, 1.0f);
                      }
                    } else if("dark_dragon".equals(player.spell_94.getRegistryId().entryId())) {
                      final int heal = (int)Math.round(attack.damage * 0.15d);
                      player.stats.getStat(HP_STAT.get()).setCurrent(Math.min(player.stats.getStat(HP_STAT.get()).getMax(), player.stats.getStat(HP_STAT.get()).getCurrent() + heal));
                      this.displayNumbers(6 + player.typeBentSlot_276, heal, 0, -12, 1.0f, 0f, 1.0f);
                    }
                  } else if(attack.attackType == AttackType.PHYSICAL) {
                    final int heal = (int)Math.round(attack.damage * 0.08d);
                    player.stats.getStat(HP_STAT.get()).setCurrent(Math.min(player.stats.getStat(HP_STAT.get()).getMax(), player.stats.getStat(HP_STAT.get()).getCurrent() + heal));
                    this.displayNumbers(6 + player.typeBentSlot_276, heal, 0, -12, 1.0f, 0f, 1.0f);

                    if(this.getEquipment("dragoon_modifier:dragon_buster", player, EquipmentSlot.WEAPON)) {
                      final int spHeal = (int)Math.ceil(attack.damage * 0.01d);
                      player.stats.getStat(SP_STAT.get()).setCurrent(spHeal + Math.min(50, heal));
                      this.displayNumbers(6 + player.typeBentSlot_276, spHeal, 0, -24, 0.3f, 1.0333333f, 0.3f);
                    }
                  }

                  if(attack.damage > 0) {
                    final double roseSiphonMultiplier = roseSiphonActivated[player.typeBentSlot_276] ? 0.15d : 0.1d;
                    roseSiphon[player.typeBentSlot_276] = Math.min(roseSiphonMax[player.typeBentSlot_276], (int)Math.min(Math.round(roseSiphonMax[player.typeBentSlot_276] * roseSiphonMultiplier + roseSiphon[player.typeBentSlot_276]), Math.round(roseSiphon[player.typeBentSlot_276] + attack.damage * roseSiphonMultiplier)));
                    this.displayNumbers(6 + player.typeBentSlot_276, roseSiphon[player.typeBentSlot_276], 0, 0, 0f, 0.5f, 1.0f);
                  }
                } else {
                  if(attack.attackType == AttackType.PHYSICAL) {
                    if(attack.damage > 0) {
                      final int heal = (int)Math.round(attack.damage * 0.04d);
                      player.stats.getStat(HP_STAT.get()).setCurrent(Math.min(player.stats.getStat(HP_STAT.get()).getMax(), player.stats.getStat(HP_STAT.get()).getCurrent() + heal));
                      this.displayNumbers(6 + player.typeBentSlot_276, heal, 0, -12, 1.0f, 0f, 1.0f);

                      final double roseSiphonMultiplier = roseSiphonActivated[player.typeBentSlot_276] ? 0.1d : 0.05d;
                      roseSiphon[player.typeBentSlot_276] = Math.min(roseSiphonMax[player.typeBentSlot_276], (int)Math.min(Math.round(roseSiphonMax[player.typeBentSlot_276] * roseSiphonMultiplier + roseSiphon[player.typeBentSlot_276]), Math.round(roseSiphon[player.typeBentSlot_276] + attack.damage * roseSiphonMultiplier)));
                      this.displayNumbers(6 + player.typeBentSlot_276, roseSiphon[player.typeBentSlot_276], 0, 0, 0f, 0.5f, 1.0f);

                      if(this.getEquipment("dragoon_modifier:dragon_buster", player, EquipmentSlot.WEAPON)) {
                        final int spHeal = (int)Math.ceil(attack.damage * 0.01d);
                        player.stats.getStat(SP_STAT.get()).setCurrent(spHeal + Math.min(50, heal));
                        this.displayNumbers(6 + player.typeBentSlot_276, spHeal, 0, -24, 0.3f, 1.0333333f, 0.3f);
                      }
                    }
                  }
                }
              }

              roseSiphonActivated[player.typeBentSlot_276] = false;

              if(this.haschelInPartyWithDragoon) {  //Haschel in party thunder charge
                try {
                  if(attack.attacker.spell_94.element_08.get() == THUNDER_ELEMENT.get()) {
                    if(overchargedTurns[monster.typeBentSlot_276] > 0) {
                      attack.damage = (int)Math.floor(monster.getElement() == THUNDER_ELEMENT.get() ? attack.damage * 2.6 : attack.damage * 1.15d);
                    }

                    if(new Random().nextBoolean()) {
                      if(staticCharge[player.typeBentSlot_276] == 0) {
                        thunderCharge[monster.typeBentSlot_276] = Math.min(10, thunderCharge[monster.typeBentSlot_276] + 1);
                        this.displayNumbers(11 + monster.typeBentSlot_276, 1, 10, -12, 0.5f, 0f, 1.0f);
                      }
                    }
                  }
                } catch(final Exception ignored) {
                }

                try {
                  if(attack.attacker.item_d4.getAttackElement() == THUNDER_ELEMENT.get()) {
                    if(overchargedTurns[monster.typeBentSlot_276] > 0) {
                      attack.damage = (int)Math.floor(monster.getElement() == THUNDER_ELEMENT.get() ? attack.damage * 2.6 : attack.damage * 1.15d);
                    }

                    if(new Random().nextBoolean()) {
                      if(staticCharge[player.typeBentSlot_276] == 0) {
                        thunderCharge[monster.typeBentSlot_276] = Math.min(10, thunderCharge[monster.typeBentSlot_276] + 1);
                        this.displayNumbers(11 + monster.typeBentSlot_276, 1, 10, -12, 0.5f, 0f, 1.0f);
                      }
                    }
                  }
                } catch(final Exception ignored) {
                }

                if(attack.attackType.isPhysical() && player.equipmentAttackElements_1c.contains(THUNDER_ELEMENT.get())) {
                  if(overchargedTurns[monster.typeBentSlot_276] > 0) {
                    attack.damage = (int)Math.floor(monster.getElement() == THUNDER_ELEMENT.get() ? attack.damage * 2.6 : attack.damage * 1.15d);
                  }

                  if(new Random().nextBoolean()) {
                    if(staticCharge[player.typeBentSlot_276] == 0) {
                      thunderCharge[monster.typeBentSlot_276] = Math.min(10, thunderCharge[monster.typeBentSlot_276] + 1);
                      this.displayNumbers(11 + monster.typeBentSlot_276, 1, 10, -12, 0.5f, 0f, 1.0f);
                    }
                  }
                }
              }

              if(player.character.template instanceof Haschel) {
                if(player.dlevel_06 > 0) {
                  if(attack.attackType.isPhysical()) {
                    if(player.isDragoon()) {
                      if(new Random().nextBoolean() || new Random().nextBoolean()) {
                        if(staticCharge[player.typeBentSlot_276] == 0) {
                          thunderCharge[monster.typeBentSlot_276] = Math.min(10, thunderCharge[monster.typeBentSlot_276] + 1);
                          this.displayNumbers(11 + monster.typeBentSlot_276, 1, 10, -12, 0.5f, 0f, 1.0f);
                        }
                      }
                    } else {
                      if(new Random().nextBoolean()) {
                        if(staticCharge[player.typeBentSlot_276] == 0) {
                          thunderCharge[monster.typeBentSlot_276] = Math.min(10, thunderCharge[monster.typeBentSlot_276] + 1);
                          this.displayNumbers(11 + monster.typeBentSlot_276, 1, 10, -12, 0.5f, 0f, 1.0f);
                        }
                      }
                    }

                    if(this.getEquipment("dragoon_modifier:overcharge_glove", player, EquipmentSlot.WEAPON)) {
                      if(new Random().nextInt(1, 10) <= 2) {
                        if(this.infusedChargedMonsterTurns[monster.typeBentSlot_276] == 0) {
                          this.infusedChargedMonsterPreviousElement[monster.typeBentSlot_276] = monster.element;
                          this.infusedChargedMonsterTurns[monster.typeBentSlot_276] = 5;
                          monster.element = THUNDER_ELEMENT.get();
                        }
                      }
                    }
                  } else {
                    if(player.isDragoon() && attack.attackType == AttackType.ITEM_MAGIC) {
                      if("spark_net".equals(player.item_d4.getRegistryId().entryId()) && thunderCharge[monster.typeBentSlot_276] == 10) {
                        final int thunderDamage = 13 + player.dlevel_06;
                        thunderCharge[monster.typeBentSlot_276] = 0;
                        overchargedTurns[monster.typeBentSlot_276] = 7;
                        attack.damage = (int)Math.floor(monster.getElement() == THUNDER_ELEMENT.get() ? attack.damage * thunderDamage : attack.damage * 3.5d);
                      }
                    }
                  }
                }

                if(staticCharge[player.typeBentSlot_276] > 0) {
                  final int transferrableCharges = 10 - thunderCharge[monster.typeBentSlot_276];
                  if(transferrableCharges > 0) {
                    if(staticCharge[player.typeBentSlot_276] >= transferrableCharges) {
                      staticCharge[player.typeBentSlot_276] -= transferrableCharges;
                      thunderCharge[monster.typeBentSlot_276] += transferrableCharges;
                    } else {
                      thunderCharge[monster.typeBentSlot_276] += staticCharge[player.typeBentSlot_276];
                      staticCharge[player.typeBentSlot_276] = 0;
                    }
                    this.displayNumbers(11 + monster.typeBentSlot_276, thunderCharge[monster.typeBentSlot_276], 10, -12, 0.5f, 0f, 1.0f);
                  }
                }
              }

              if(player.character.template instanceof Meru) {
                if(attack.damage > 0) {
                  if(meruWinglyMagic[player.typeBentSlot_276]) {
                    this.meruIceShield[player.typeBentSlot_276] = (int)Math.min(this.meruIceShieldMax[player.typeBentSlot_276], Math.min(this.meruIceShieldMax[player.typeBentSlot_276] * 0.1 + this.meruIceShield[player.typeBentSlot_276], this.meruIceShield[player.typeBentSlot_276] + attack.damage * 0.1));
                    this.displayNumbers(6 + player.typeBentSlot_276, this.meruIceShield[player.typeBentSlot_276], 0, -12, 1, 1, 1);
                  }

                  if(attack.attackType.isPhysical()) {
                    final int healMp = player.isDragoon() ? 20 : 5;
                    player.stats.getStat(MP_STAT.get()).setCurrent(Math.min(player.stats.getStat(MP_STAT.get()).getMax(), (player.stats.getStat(MP_STAT.get()).getCurrent() + healMp)));
                    this.displayNumbers(6 + player.typeBentSlot_276, healMp, 0, -24, 0.56666666f, 0.53333336f, 1.0333333f);

                    if(this.getEquipment("dragoon_modifier:magic_hammer", player, EquipmentSlot.WEAPON)) {
                      attack.damage = 0;
                    }
                  }
                }
              }

              if(player.character.template instanceof Kongol) {
                if(this.getEquipment("dragoon_modifier:giant_axe", player, EquipmentSlot.WEAPON) && attack.attackType == AttackType.PHYSICAL) {
                  if(new Random().nextInt(0, 99) < 20) {
                    player.guard_54 = 1;
                  }
                }
              }
            }

            if(this.dragonBlockStaff) {
              if(player.isDragoon() && attack.attackType != AttackType.ITEM_MAGIC) {
                attack.damage *= 8;
              }
            }
          }
        }


        //MONSTER ATTACKING PLAYER
        if(attack.defender instanceof final PlayerBattleEntity player) {
          if(attack.attacker instanceof final MonsterBattleEntity monster) {
            if(this.preventDeathCount[player.typeBentSlot_276] > 0 && player.stats.getStat(HP_STAT.get()).getCurrent() - attack.damage < 0) {
              attack.damage = 0;
              this.preventDeathCount[player.typeBentSlot_276] -= 1;
            }

            if(attack.damage > 0) {
              if(dartBurnStackMode[player.typeBentSlot_276]) {
                boolean damaged = false;

                if(attack.attackType == AttackType.DRAGOON_MAGIC_STATUS_ITEMS) {
                  if(monster.spell_94.element_08.get() == FIRE_ELEMENT.get()) {
                    final int currentHP = player.stats.getStat(HP_STAT.get()).getCurrent();
                    player.setStat(BattleEntityStat.CURRENT_HP, currentHP + attack.damage);
                    this.displayNumbers(6 + player.typeBentSlot_276, attack.damage, 0, -12, 1f, 0.5f, 1.0f);
                    attack.damage = 0;
                    damaged = true;
                  } else if(monster.spell_94.element_08.get() == WATER_ELEMENT.get()) {
                    attack.damage *= 1.15;
                    damaged = true;
                  }
                }

                if(attack.attackType == AttackType.ITEM_MAGIC) {
                  if(monster.item_d4.getItem().getAttackElement(monster.item_d4) == FIRE_ELEMENT.get()) {
                    final int currentHP = player.stats.getStat(HP_STAT.get()).getCurrent();
                    player.setStat(BattleEntityStat.CURRENT_HP, currentHP + attack.damage);
                    this.displayNumbers(6 + player.typeBentSlot_276, attack.damage, 0, -12, 1f, 0.5f, 1.0f);
                    attack.damage = 0;
                    damaged = true;
                  } else if(monster.item_d4.getItem().getAttackElement(monster.item_d4) == WATER_ELEMENT.get()) {
                    attack.damage *= 1.15;
                    damaged = true;
                  }
                }

                if(!damaged) {
                  attack.damage *= 0.85;
                }
              }

              if(windBarrier[player.typeBentSlot_276]) {
                final int currentMP = player.stats.getStat(MP_STAT.get()).getCurrent();
                player.setStat(BattleEntityStat.CURRENT_MP, currentMP + 5);
                this.windMark[monster.typeBentSlot_276] = Math.min(4, this.windMark[monster.typeBentSlot_276] + 1);
              }

              if(this.haschelInPartyWithDragoon) {
                try {
                  if(monster.spell_94.element_08.get() == THUNDER_ELEMENT.get() && new Random().nextBoolean()) {
                    thunderCharge[monster.typeBentSlot_276] = Math.min(10, thunderCharge[monster.typeBentSlot_276] + 1);
                    this.displayNumbers(11 + monster.typeBentSlot_276, 1, 10, -12, 0.5f, 0f, 1.0f);
                  }
                } catch(final Exception ignored) {
                }

                try {
                  if(monster.item_d4.getAttackElement() == THUNDER_ELEMENT.get() && new Random().nextBoolean()) {
                    thunderCharge[monster.typeBentSlot_276] = Math.min(10, thunderCharge[monster.typeBentSlot_276] + 1);
                    this.displayNumbers(11 + monster.typeBentSlot_276, 1, 10, -12, 0.5f, 0f, 1.0f);
                  }
                } catch(final Exception ignored) {
                }
              }

              if(player.character.template instanceof Meru) {
                if(this.meruIceShield[player.typeBentSlot_276] > 0) {
                  if(attack.damage <= this.meruIceShield[player.typeBentSlot_276]) {
                    this.meruIceShield[player.typeBentSlot_276] -= attack.damage;
                    this.displayNumbers(6 + player.typeBentSlot_276, attack.damage, 0, -12, 1, 1, 1);
                    attack.damage = 0;
                  } else {
                    attack.damage -= this.meruIceShield[player.typeBentSlot_276];
                    this.displayNumbers(6 + player.typeBentSlot_276, this.meruIceShield[player.typeBentSlot_276], 0, -12, 1, 1, 1);
                    this.meruIceShield[player.typeBentSlot_276] = 0;
                  }
                }
              }

              Element attackElement = null;
              try {
                attackElement = attack.attacker.item_d4.getAttackElement();
              } catch(final Exception ignored) {
              }

              try {
                if(attackElement == null) {
                  attackElement = attack.attacker.spell_94.element_08.get();
                }
              } catch(final Exception ignored) {
              }

              if(attackElement != null && attack.damage > 0) {
                final double elementalReduction = this.isHardMode() ? 1.40d : 1.20d;
                if(attackElement == FIRE_ELEMENT.get() && this.getEquipment("dragoon_modifier:red_dg_armor", player, EquipmentSlot.ARMOUR)) {
                  attack.damage = (int)Math.round(attack.damage / elementalReduction);
                } else if(attackElement == WIND_ELEMENT.get() && this.getEquipment("dragoon_modifier:jade_dg_armor", player, EquipmentSlot.ARMOUR)) {
                  attack.damage = (int)Math.round(attack.damage / elementalReduction);
                } else if(attackElement == EARTH_ELEMENT.get() && this.getEquipment("dragoon_modifier:gold_dg_armor", player, EquipmentSlot.ARMOUR)) {
                  attack.damage = (int)Math.round(attack.damage / elementalReduction);
                } else if(attackElement == THUNDER_ELEMENT.get() && this.getEquipment("dragoon_modifier:violet_dg_armor", player, EquipmentSlot.ARMOUR)) {
                  attack.damage = (int)Math.round(attack.damage / elementalReduction);
                } else if(attackElement == LIGHT_ELEMENT.get() && this.getEquipment("dragoon_modifier:silver_dg_armor", player, EquipmentSlot.ARMOUR)) {
                  attack.damage = (int)Math.round(attack.damage / elementalReduction);
                } else if(attackElement == DARK_ELEMENT.get() && this.getEquipment("dragoon_modifier:dark_dg_armor", player, EquipmentSlot.ARMOUR)) {
                  attack.damage = (int)Math.round(attack.damage / elementalReduction);
                } else if(attackElement == WATER_ELEMENT.get() && this.getEquipment("dragoon_modifier:blue_dg_armor", player, EquipmentSlot.ARMOUR)) {
                  attack.damage = (int)Math.round(attack.damage / elementalReduction);
                }
              }


              if(player.character.template instanceof Kongol) {
                if(kongolCounterStance[player.typeBentSlot_276]) {
                  player.guard_54 = 1;
                  player.turnValue_4c += 218;
                  attack.damage *= 1.7;
                  this.kongolHitInCounterStance[player.typeBentSlot_276] = true;
                }
              }

              if(this.dragonBlockStaff) {
                if(player.isDragoon()) {
                  attack.damage /= 8;
                }
              }

              if(this.protectionShield[player.typeBentSlot_276] > 0) {
                if(attack.damage <= this.protectionShield[player.typeBentSlot_276]) {
                  this.displayNumbers(6 + player.typeBentSlot_276, attack.damage, 0, -12, 1.0333333f, 0.6666667f, 0.0f);
                  this.protectionShield[player.typeBentSlot_276] -= attack.damage;
                  attack.damage = 0;
                } else {
                  this.displayNumbers(6 + player.typeBentSlot_276, this.protectionShield[player.typeBentSlot_276], 0, -12, 1.0333333f, 0.6666667f, 0.0f);
                  attack.damage -= this.protectionShield[player.typeBentSlot_276];
                  this.protectionShield[player.typeBentSlot_276] = 0;
                }
              }
            }
          }
        }

        //PLAYER ATTACKING PLAYER
        if(attack.defender instanceof final PlayerBattleEntity defender) {
          if(attack.attacker instanceof final PlayerBattleEntity player) {
            if(player.isDragoon() && attack.attackType == AttackType.DRAGOON_MAGIC_STATUS_ITEMS && ("rose_storm".equals(player.spell_94.getRegistryId().entryId()) || "blossom_storm".equals(player.spell_94.getRegistryId().entryId()))) {
              for(int i = 0; i < battleState_8006e398.alivePlayerBents_eac.size(); i++) {
                final ScriptState<? extends BattleEntity27c> state = battleState_8006e398.alivePlayerBents_eac.get(i);
                final BattleEntity27c bent = state.innerStruct_00;
                final int turnCount = bent != player ? 4 : 5;
                bent.stats.getStat(HP_STAT.get()).addMod(id("storm_max_hp"), FRACTIONAL_STAT_MOD_TYPE.get().make(new FractionalStatModConfig().percent(30).turns(turnCount)));
                bent.stats.getStat(STORM_HP_REGEN.get()).addMod(id("storm_regen"), UNARY_STAT_MOD_TYPE.get().make(new UnaryStatModConfig().flat(10).turns(turnCount)));

                if(!this.stormHPRegenActive[bent.typeBentSlot_276]) {
                  ((PlayerBattleEntity)bent).setStat(BattleEntityStat.HP_REGEN, ((PlayerBattleEntity)bent).hpRegen_134 + STORM_REGEN_AMOUNT);
                }

                this.stormHPRegenActive[bent.typeBentSlot_276] = true;
              }
            }

            if(this.dragonBlockStaff) {
              if(player.isDragoon()) {
                attack.damage *= 8;
              }
            }
          }
        }

        if(attack.defender instanceof final PlayerBattleEntity player) { //Any attack to player
        }
      }

      this.updateDamageTracker(attack);
    }

    //System.out.println("AFTER ATTACK EVENT: " + attack.damage);
  }

  @EventListener
  public void updateDamageTracker(final AttackEvent attack) {
    if(CONFIG.getConfig(DAMAGE_TRACKER.get())) {
      if(attack.attacker instanceof final PlayerBattleEntity player && attack.defender instanceof final MonsterBattleEntity monster) {
        this.damageTrackerPreviousCharacter = player;

        if(player.isDragoon()) {
          if(attack.attackType.isPhysical()) {
            this.damageTrackerPreviousAttackType = 0;
            this.damageTracker[player.typeBentSlot_276][0] += attack.damage;
            this.damageTrackerLog.add(player.getName() + " - D.Physical - " + attack.damage);
          } else {
            this.damageTrackerPreviousAttackType = 1;
            this.damageTracker[player.typeBentSlot_276][1] += attack.damage;
            this.damageTrackerLog.add(player.getName() + " - D.Magical - " + attack.damage);
          }
        } else {
          if(attack.attackType.isPhysical()) {
            this.damageTrackerPreviousAttackType = 2;
            this.damageTracker[player.typeBentSlot_276][2] += attack.damage;
            this.damageTrackerLog.add(player.getName() + " - Physical - " + attack.damage);
          } else {
            this.damageTrackerPreviousAttackType = 3;
            this.damageTracker[player.typeBentSlot_276][3] += attack.damage;
            this.damageTrackerLog.add(player.getName() + " - Magical - " + attack.damage);
          }
        }

        final int hp = monster.stats.getStat(HP_STAT.get()).getCurrent();
        if(attack.damage > hp && hp > 0 && hp != this.damageTrackerPreviousHP[monster.typeBentSlot_276]) {
          this.damageTracker[player.typeBentSlot_276][4] = attack.damage - hp;
        }

        this.damageTrackerPreviousHP[monster.typeBentSlot_276] = hp - attack.damage;
      }
    }
  }

  public void updateItemMagicDamageTracker() {
    if(CONFIG.getConfig(DAMAGE_TRACKER.get())) {
      for(int i = 0; i < battleState_8006e398.getMonsterCount(); i++) {
        final MonsterBattleEntity monster = battleState_8006e398.monsterBents_e50[i].innerStruct_00;
        final int hp = monster.stats.getStat(HP_STAT.get()).getCurrent();
        if(hp < this.damageTrackerPreviousHP[monster.typeBentSlot_276]) {
          final int difference = this.damageTrackerPreviousHP[monster.typeBentSlot_276] - hp;
          this.damageTracker[this.damageTrackerPreviousCharacter.typeBentSlot_276][this.damageTrackerPreviousAttackType] += difference;
          this.damageTrackerLog.add(this.damageTrackerPreviousCharacter.getName() + " - Multiplier - " + difference);
          this.damageTrackerPreviousHP[monster.typeBentSlot_276] = hp;
        }
      }
    }
  }

  @EventListener
  public void battleEnded(final BattleEndedEvent event) {
    if(CONFIG.getConfig(DAMAGE_TRACKER.get()) && !this.damageTrackerPrinted && gameState_800babc8.charIds_88.size() == 3) {
      try {
        final double total = IntStream.of(this.damageTracker[0]).sum() + IntStream.of(this.damageTracker[1]).sum() + IntStream.of(this.damageTracker[2]).sum();
        final PrintWriter pw = new PrintWriter("./mods/dragoon_modifier/Damage Tracker/" + new SimpleDateFormat("yyyy-MMdd--hh-mm-ss").format(new Date()) + " E-" + String.join(" ", this.damageTrackerMonsterNames) + ".txt");
        pw.printf("======================================================================%n");
        pw.printf("=                           Damage Tracker                           =%n");
        pw.printf("======================================================================%n");
        pw.printf("| %-20s | %-20s | %-20s |%n", gameState_800babc8.charData_32c.get(gameState_800babc8.charIds_88.get(0)).getName(), gameState_800babc8.charData_32c.get(gameState_800babc8.charIds_88.get(1)).getName(), gameState_800babc8.charData_32c.get(gameState_800babc8.charIds_88.get(2)).getName());
        pw.printf("----------------------------------------------------------------------%n");
        pw.printf("| %-10s %-9s | %-10s %-9s | %-10s %-9s |%n", "D.Physical", this.damageTracker[0][0], "D.Physical", this.damageTracker[1][0], "D.Physical", this.damageTracker[2][0]);
        pw.printf("| %-10s %-9s | %-10s %-9s | %-10s %-9s |%n", "D.Magical", this.damageTracker[0][1], "D.Magical", this.damageTracker[1][1], "D.Magical", this.damageTracker[2][1]);
        pw.printf("| %-10s %-9s | %-10s %-9s | %-10s %-9s |%n", "Physical", this.damageTracker[0][2], "Physical", this.damageTracker[1][2], "Physical", this.damageTracker[2][2]);
        pw.printf("| %-10s %-9s | %-10s %-9s | %-10s %-9s |%n", "Magical", this.damageTracker[0][3], "Magical", this.damageTracker[1][3], "Magical", this.damageTracker[2][3]);
        pw.printf("| %-10s %-9s | %-10s %-9s | %-10s %-9s |%n", "Total", IntStream.of(this.damageTracker[0]).sum(), "Total", IntStream.of(this.damageTracker[1]).sum(), "Total", IntStream.of(this.damageTracker[2]).sum());
        pw.printf("----------------------------------------------------------------------%n");
        pw.printf("%-13s %.2f%%%n", gameState_800babc8.charData_32c.get(gameState_800babc8.charIds_88.get(0)).getName(), (IntStream.of(this.damageTracker[0]).sum() - this.damageTracker[0][4] * 2) / total * 100);
        pw.printf("%-13s %.2f%%%n", gameState_800babc8.charData_32c.get(gameState_800babc8.charIds_88.get(1)).getName(), (IntStream.of(this.damageTracker[1]).sum() - this.damageTracker[1][4] * 2) / total * 100);
        pw.printf("%-13s %.2f%%%n", gameState_800babc8.charData_32c.get(gameState_800babc8.charIds_88.get(2)).getName(), (IntStream.of(this.damageTracker[2]).sum() - this.damageTracker[2][4] * 2) / total * 100);
        pw.printf("Grand Total   " + total + "%n");
        pw.printf("Encounter     " + encounterId_800bb0f8 + "%n%n");
        pw.printf("===========================================================================================================%n");
        pw.printf("=                                                Equipment                                                =%n");
        pw.printf("===========================================================================================================%n");
        pw.printf("| Name     | Weapon           | Helmet           | Armor            | Shoes            | Accessory        |%n");
        pw.printf("-----------------------------------------------------------------------------------------------------------%n");
        for(int i = 0; i < this.damageTrackerEquips.length; i++) {
          pw.printf("| %-8s | %-16s | %-16s | %-16s | %-16s | %-16s |%n", gameState_800babc8.charData_32c.get(gameState_800babc8.charIds_88.get(i)).getName(), I18n.translate(this.damageTrackerEquips[i][0]), I18n.translate(this.damageTrackerEquips[i][1]), I18n.translate(this.damageTrackerEquips[i][2]), I18n.translate(this.damageTrackerEquips[i][3]), I18n.translate(this.damageTrackerEquips[i][4]));
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

  public Equipment getEquipFromRegistry(final RegistryId id) {
    if(REGISTRIES.equipment.hasEntry(id)) {
      return REGISTRIES.equipment.getEntry(id).get();
    }
    return null;
  }

  public Equipment getEquipFromRegistry(final String id) {
    return REGISTRIES.equipment.getEntry(id).get();
  }

  public Item getItemFromRegistry(final RegistryId id) {
    if(REGISTRIES.items.hasEntry(id)) {
      return REGISTRIES.items.getEntry(id).get();
    }
    return null;
  }

  public Item getItemFromRegistry(final String id) {
    return REGISTRIES.items.getEntry(id).get();
  }

  public void recaluteBentDragoonTurns(final PlayerBattleEntity bent) {
    if(bent.isDragoon()) {
      final int sp = bent.stats.getStat(SP_STAT.get()).getCurrent();
      battleState_8006e398.dragoonTurnsRemaining_294[bent.typeBentSlot_276] = (int)Math.floor(sp / 100d);
    }
  }

  @EventListener
  public void dragonBlockStaffOn(final DragonBlockStaffOnEvent event) {
    if(this.isHardMode() || this.isHellMode() && !this.dragonBlockStaff) {
      if(!this.dragonBlockStaff) {
        GameOverlay.addNotification(3, new RawText("DRAGON BLOCK STAFF REDUCES DRAGOON STATS BY 20%"));
      }
      this.dragonBlockStaff = true;
    }
  }

  @EventListener
  public void dragonBlockStaffOff(final DragonBlockStaffOffEvent event) {
    if(this.isHardMode() || this.isHellMode()  && this.dragonBlockStaff) {
      this.dragonBlockStaff = false;
    }
  }

  @EventListener
  public void drgnFile(final DrgnFileEvent event) {
    if(this.isHardMode()) { //TODO fix lol
      if(event.path.toString().contains("DRGN1.BIN\\285")) {
        event.path = Path.of(".", "mods", "dragoon_modifier", "Hard Mode", "scripts", "monsters", "285");
      } else if(event.path.toString().contains("DRGN1.BIN\\286")) {
        event.path = Path.of(".", "mods", "dragoon_modifier", "Hard Mode", "scripts", "monsters", "286");
      } else if(event.path.toString().contains("DRGN1.BIN\\344")) {
        event.path = Path.of(".", "mods", "dragoon_modifier", "Hard Mode", "scripts", "monsters", "344");
      } else if(event.path.toString().contains("DRGN1.BIN\\388")) {
        event.path = Path.of(".", "mods", "dragoon_modifier", "Hard Mode", "scripts", "monsters", "388");
      } else if(event.path.toString().contains("DRGN0.BIN\\5002")) {
        event.path = Path.of(".", "mods", "dragoon_modifier", "Hard Mode", "scripts", "attacks", "5002");
      } else if(event.path.toString().contains("DRGN0.BIN\\5004")) {
        event.path = Path.of(".", "mods", "dragoon_modifier", "Hard Mode", "scripts", "attacks", "5004");
      }
    }
  }
  //endregion

  //region Character Enhancements
  public void addBurnStacks(final PlayerBattleEntity dart, final int stacks) {
    if(!dartBurnStackMode[dart.typeBentSlot_276]) {
      this.dartPreviousBurnStacks[dart.typeBentSlot_276] = dartBurnStacks[dart.typeBentSlot_276];
      dartBurnStacks[dart.typeBentSlot_276] = Math.min(DART_BURN_STACKS_MAX, dartBurnStacks[dart.typeBentSlot_276] + stacks);

      if(dartBurnStacks[dart.typeBentSlot_276] >= 4 && this.dartPreviousBurnStacks[dart.typeBentSlot_276] < 4) {
        dart.stats.getStat(MP_STAT.get()).setCurrent(dart.stats.getStat(MP_STAT.get()).getCurrent() + 10);
      } else if(dartBurnStacks[dart.typeBentSlot_276] >= 8 && this.dartPreviousBurnStacks[dart.typeBentSlot_276] < 8) {
        dart.stats.getStat(MP_STAT.get()).setCurrent(dart.stats.getStat(MP_STAT.get()).getCurrent() + 20);
      } else if(dartBurnStacks[dart.typeBentSlot_276] >= 12 && this.dartPreviousBurnStacks[dart.typeBentSlot_276] < 12) {
        dart.stats.getStat(MP_STAT.get()).setCurrent(dart.stats.getStat(MP_STAT.get()).getCurrent() + 30);
      }

      this.dartBurnAdded[dart.typeBentSlot_276] = true;
      this.displayNumbers(6 + dart.typeBentSlot_276, stacks, 0, 0, 1.0f, 0f, 0f);
    }
  }

  @EventListener
  public void statDisplay(final StatDisplayEvent event) {
    if(this.isHardMode() || this.isHellMode()) {
      final PlayerBattleEntity player = event.player;

      if(!this.recalcDragoonTurns[player.typeBentSlot_276] && player.isDragoon()) {
        this.recaluteBentDragoonTurns(player);
        this.recalcDragoonTurns[player.typeBentSlot_276] = true;
      }


      if(player.character.template instanceof Dart && dartBurnStacks[player.typeBentSlot_276] > 0) {
        final float burnPercent = (float)dartBurnStacks[player.typeBentSlot_276] / DART_BURN_STACKS_MAX;
        this.renderCharacterBar(event.player, 1.0f, 0.0f, 0.0f, burnPercent, false);
        if(dartBurnStackMode[player.typeBentSlot_276]) {
          this.renderCharacterBar(event.player, 1.0f, 0.0f, 0.0f, 1.0f, true);
        }
      } else if(player.character.template instanceof Lavitz || player.character.template instanceof Albert) {
        if(windBarrier[event.player.typeBentSlot_276]) {
          this.renderCharacterBar(event.player, 0.0f, 1.0f, 0.0f, 1.0f, true);
        }
      } else if(player.character.template instanceof Rose && roseSiphon[player.typeBentSlot_276] > 0) {
        final float siphonPercent = (float)roseSiphon[event.player.typeBentSlot_276] / roseSiphonMax[event.player.typeBentSlot_276];
        this.renderCharacterBar(event.player, 0.0f, 0.5f, 1.0f, siphonPercent, false);

        if(roseSiphonActivated[event.player.typeBentSlot_276]) {
          this.renderCharacterBar(event.player, 0.0f, 0.5f, 1.0f, 1.0f, true);
        }
      } else if(player.character.template instanceof Meru) {
        if(this.meruIceShield[event.player.typeBentSlot_276] > 0) {
          final float iceShieldPercent = (float)this.meruIceShield[event.player.typeBentSlot_276] / this.meruIceShieldMax[event.player.typeBentSlot_276];
          this.renderCharacterBar(event.player, 0.0f, 0.5f, 1.0f, iceShieldPercent, false);
        }
        if(meruWinglyMagic[event.player.typeBentSlot_276]) {
          this.renderCharacterBar(event.player, 0.0f, 0.5f, 1.0f, 1.0f, true);
        }
      } else if(player.character.template instanceof Kongol) {
        if(kongolCounterStance[event.player.typeBentSlot_276]) {
          final float counterMax = this.getEquipment("dragoon_modifier:giant_axe", player, EquipmentSlot.WEAPON) ? 4 : 3;
          this.renderCharacterBar(event.player, 0.5f, 0.35f, 0.25f, kongolCounterStanceTurns[event.player.typeBentSlot_276] / counterMax, false);
        }
      }

      if(currentEngineState_8004dd04 instanceof final Battle battle) {
        if(!battle.hud.battleMenu_800c6c34.displayTargetArrowAndName_4c && this.protectionShield[event.player.typeBentSlot_276] > 0) {
          final MV transforms = new MV();
          transforms.transfer.set(8 + player.typeBentSlot_276 * 94, 224, 1.0f);
          RENDERER.queueOrthoModel(battle.hud.battleMenu_800c6c34.menuObj, transforms, QueuedModelStandard.class)
            .vertices(battle.hud.battleMenu_800c6c34.actionIconObjOffset + 4, 4)
            .translucency(Translucency.of(battleMenuIconMetrics_800fb674[1].translucencyMode_06));

          final MV bar = new MV();
          bar.transfer.set(event.player.typeBentSlot_276 * 94 + 30, 226.0, 999.0f);
          bar.scaling(41.0f, 13.0f, 999.0f);
          RENDERER
            .queueOrthoModel(RENDERER.opaqueQuad, bar, QueuedModelStandard.class)
            .monochrome(0.0f)
            .translucency(Translucency.HALF_B_PLUS_HALF_F);

          renderText(String.valueOf(this.protectionShield[event.player.typeBentSlot_276]), event.player.typeBentSlot_276 * 94 + 30, 227.0f, this.fontOptions);
        }
      }
    }
  }

  public void renderCharacterBar(final PlayerBattleEntity player, final float r, final float g, final float b, final float percent, final boolean top) {
    final MV transforms = new MV();
    final Battle battle = ((Battle)currentEngineState_8004dd04);
    final int x = 17 + player.typeBentSlot_276 * 94;
    final int y = top ? 184 : 218;
    transforms.transfer.set(x, y, 1);
    transforms.scaling(28.0f, 2.0f, 999.0f);
    RENDERER
      .queueOrthoModel(RENDERER.opaqueQuad, transforms, QueuedModelStandard.class)
      .monochrome(0.0f);

    final MV bar = new MV();
    bar.scaling(28.0f * percent, 2.0f, 999.0f);
    bar.transfer.set(x, y, 1);
    RENDERER.queueOrthoModel(RENDERER.opaqueQuad, bar, QueuedModelStandard.class)
      .colour(r, g, b);
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

    if(this.getEquipment("dragoon_modifier:elemental_arrow", this.currentPlayer, EquipmentSlot.WEAPON)) {
      event.arrowCount = 99;
    } else {
      event.arrowCount = amount;
    }
  }


  @EventListener
  public void shanaSwapArrow(final ShanaSwapArrowEvent event) {
    this.shanaArrowCount[this.currentPlayer.typeBentSlot_276] = EVENTS.postEvent(new ShanaGetArrowCountEvent(event.arrowIndex)).arrowCount;
    if(this.shanaArrowCount[this.currentPlayer.typeBentSlot_276] > 0) {
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
        this.shanaArrowCount[this.currentPlayer.typeBentSlot_276] = 0;
        this.shanaMaxArrowCount[this.currentPlayer.typeBentSlot_276] = 0;
        this.currentPlayer.equipment_11e.put(EquipmentSlot.WEAPON, REGISTRIES.equipment.getEntry(this.shanaPreviousArrow[this.currentPlayer.typeBentSlot_276]).get());
        return;
      }

      this.shanaMaxArrowCount[this.currentPlayer.typeBentSlot_276] = this.shanaArrowCount[this.currentPlayer.typeBentSlot_276];
      this.currentPlayer.equipment_11e.put(EquipmentSlot.WEAPON, this.getEquipFromRegistry(arrow));
    } else {
      this.shanaDeffArrow[this.currentPlayer.typeBentSlot_276] = false;
      this.shanaArrowCount[this.currentPlayer.typeBentSlot_276] = 0;
      this.shanaMaxArrowCount[this.currentPlayer.typeBentSlot_276] = 0;
      this.currentPlayer.equipment_11e.put(EquipmentSlot.WEAPON, REGISTRIES.equipment.getEntry(this.shanaPreviousArrow[this.currentPlayer.typeBentSlot_276]).get());
    }
  }

  public void removeArrow() {
    if(this.getEquipment("dragoon_modifier:elemental_arrow", this.currentPlayer, EquipmentSlot.WEAPON)) {
      return;
    }

    if(this.shanaElementalShift && this.shanaElementalShiftMode == 6) {
      return;
    }

    for(int i = 0; i < gameState_800babc8.equipment_1e8.size(); i++) {
      final Equipment equipment = gameState_800babc8.equipment_1e8.get(i);
      if(equipment.getRegistryId().equals(this.currentPlayer.equipment_11e.get(EquipmentSlot.WEAPON).getRegistryId())) {
        gameState_800babc8.equipment_1e8.remove(i);
        this.shanaArrowCount[this.currentPlayer.typeBentSlot_276]--;
        if(this.shanaArrowCount[this.currentPlayer.typeBentSlot_276] == 0) {
          this.shanaMaxArrowCount[this.currentPlayer.typeBentSlot_276] = 0;
          this.currentPlayer.equipment_11e.put(EquipmentSlot.WEAPON, REGISTRIES.equipment.getEntry(this.shanaPreviousArrow[this.currentPlayer.typeBentSlot_276]).get());
          this.shanaDeffArrow[this.currentPlayer.typeBentSlot_276] = false;
        }
        return;
      }
    }
  }

  @EventListener
  public void setElementArrowAttack(final ShanaElementArrowAttackEvent event) {
    this.shanaDeffArrow[this.currentPlayer.typeBentSlot_276] = true;

    if(this.shanaElementalCooldownTurns > 0) {
      return;
    }

    if(this.shanaElementalShift && this.shanaElementalShiftMode == 1 && this.currentPlayer.typeBentSlot_276 == this.shanaElementalShiftActivatorSlot) {
      event.attackEquip = "dragoon_modifier:magic_arrow";
      event.attackItem = "dragoon_modifier:psyche_bomb";
      event.deffIndex = 241;
      return;
    }

    if(this.shanaUsedElementalArrowsField[this.currentPlayer.typeBentSlot_276].size() < 3) {
      this.shanaUsedElementalArrowsField[this.currentPlayer.typeBentSlot_276].add(event.attackEquip);
      if(this.shanaUsedElementalArrowsField[this.currentPlayer.typeBentSlot_276].size() == 3 && !this.shanaElementalField) {
        this.shanaElementalFieldActivatorSlot = this.currentPlayer.typeBentSlot_276;
        this.shanaElementalFieldTurns = 4;
        this.shanaElementalField = true;

        final Set<String> uniqueArrowCount = new HashSet<>(this.shanaUsedElementalArrowsField[this.shanaElementalFieldActivatorSlot]);
        this.shanaElementalFieldMode = uniqueArrowCount.size();

        if(uniqueArrowCount.size() == 1) {
          this.shanaElementalFieldElement1 = this.getElementFromArrow(uniqueArrowCount.toArray()[0].toString());
        } else if(uniqueArrowCount.size() == 2) {
          final Map<String, Integer> count = new HashMap<>();
          for(final String arrow : this.shanaUsedElementalArrowsField[this.shanaElementalFieldActivatorSlot]) {
            count.merge(arrow, 1, Integer::sum);
          }
          count.forEach((arrow, times) -> {
            if(times == 1) {
              this.shanaElementalFieldElement1 = this.getElementFromArrow(arrow);
            } else {
              this.shanaElementalFieldElement2 = this.getElementFromArrow(arrow);
            }
          });
        }
      }
    } else {
      if(this.shanaUsedElementalArrowsShift[this.currentPlayer.typeBentSlot_276].size() < 3) {
        this.shanaUsedElementalArrowsShift[this.currentPlayer.typeBentSlot_276].add(event.attackEquip);
        if(this.shanaUsedElementalArrowsShift[this.currentPlayer.typeBentSlot_276].size() == 3 && !this.shanaElementalShift) {
          this.shanaElementalShiftActivatorSlot = this.currentPlayer.typeBentSlot_276;
          this.shanaElementalShiftTurns = 4;
          this.shanaElementalShift = true;

          final Set<String> uniqueArrowCountField = new HashSet<>(this.shanaUsedElementalArrowsField[this.shanaElementalShiftActivatorSlot]);
          final Set<String> uniqueArrowCountShift = new HashSet<>(this.shanaUsedElementalArrowsShift[this.shanaElementalShiftActivatorSlot]);
          final List<String> allArrows = new ArrayList<>();
          final List<Element> allElements = new ArrayList<>();

          allArrows.addAll(this.shanaUsedElementalArrowsField[this.shanaElementalShiftActivatorSlot]);
          allArrows.addAll(this.shanaUsedElementalArrowsShift[this.shanaElementalShiftActivatorSlot]);


          final Set<String> allArrowsUniqueArrowCount = new HashSet<>(allArrows);

          for(final String arrow : allArrows) {
            allElements.add(this.getElementFromArrow(arrow));
          }

          if(allArrowsUniqueArrowCount.size() == 6) {
            this.shanaElementalShiftMode = 1;
            return;
          } else if(allArrowsUniqueArrowCount.size() == 1) {
            boolean matchesElement = false;

            for(int i = 0; i < battleState_8006e398.getMonsterCount(); i++) {
              final MonsterBattleEntity monster = battleState_8006e398.monsterBents_e50[i].innerStruct_00;
              if(allElements.contains(monster.element)) {
                matchesElement = true;
                this.shanaElementalShiftElement1 = monster.element;
              }
            }

            if(matchesElement) {
              this.shanaElementalShiftMode = 2;
              return;
            } else {
              this.shanaElementalShiftMode = 3;
              return;
            }
          } else if(allArrowsUniqueArrowCount.size() == 2) {
            this.shanaElementalShiftElement1 = this.getElementFromArrow(this.shanaUsedElementalArrowsShift[this.shanaElementalShiftActivatorSlot].getFirst());
            if(this.shanaElementalFieldElement1.isWeakAgainst(this.shanaElementalShiftElement1)) {
              this.shanaElementalShiftMode = 4;
            } else {
              this.shanaElementalShiftMode = 5;
            }
            return;
          }


          this.shanaElementalShiftMode = 6;
        }
      }
    }
  }

  public Element getElementFromArrow(final String registryId) {
    return switch(registryId) {
      case "dragoon_modifier:fire_arrow" -> FIRE_ELEMENT.get();
      case "dragoon_modifier:water_arrow" -> WATER_ELEMENT.get();
      case "dragoon_modifier:wind_arrow" -> WIND_ELEMENT.get();
      case "dragoon_modifier:earth_arrow" -> EARTH_ELEMENT.get();
      case "dragoon_modifier:dark_arrow" -> DARK_ELEMENT.get();
      case "dragoon_modifier:light_arrow" -> LIGHT_ELEMENT.get();
      case "dragoon_modifier:thunder_arrow" -> THUNDER_ELEMENT.get();
      default -> NO_ELEMENT.get();
    };
  }

  public void renderElementalArrows(final PlayerBattleEntity player) {
    final MV transforms = new MV();
    final Battle battle = ((Battle)currentEngineState_8004dd04);
    final int x = 16 + player.typeBentSlot_276 * 94;
    final int y =  224;
    //transforms.scaling(14.5f, 14.5f, 999.0f);

    for(int i = 0; i < this.shanaUsedElementalArrowsField[this.currentPlayer.typeBentSlot_276].size(); i++) {
      transforms.transfer.set(x + (16 * i), y, 1);
      RENDERER
        .queueOrthoModel(ELEMENTAL_OBJ, transforms, QueuedModelStandard.class)
        .translucency(Translucency.HALF_B_PLUS_HALF_F)
        .useTextureAlpha()
        .uvOffset(0, 0)
        .texture(ELEMENTAL_ICON_TEXTURE.get(this.shanaUsedElementalArrowsField[this.currentPlayer.typeBentSlot_276].get(i)));
    }

    for(int i = 0; i < this.shanaUsedElementalArrowsShift[this.currentPlayer.typeBentSlot_276].size(); i++) {
      transforms.transfer.set(x + (16 * i) + 48, y, 1);
      RENDERER
        .queueOrthoModel(ELEMENTAL_OBJ, transforms, QueuedModelStandard.class)
        .translucency(Translucency.HALF_B_PLUS_HALF_F)
        .useTextureAlpha()
        .uvOffset(0, 0)
        .texture(ELEMENTAL_ICON_TEXTURE.get(this.shanaUsedElementalArrowsShift[this.currentPlayer.typeBentSlot_276].get(i)));
    }
  }

  public void selectedTarget(final MonsterBattleEntity monster) {
    if(CONFIG.getConfig(MONSTER_HP_BAR.get())) {
      final MV transforms = new MV();
      final VitalsStat stat = monster.stats.getStat(HP_STAT.get());
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

      /*if(this.enrageModeProtection[event.monster.charSlot_276] > 0) {
        text = stat.getCurrent() + '(' +  this.enrageModeProtection[event.monster.charSlot_276] + ") AT" + event.monster.stats.getStat(LodMod.ATTACK_STAT.get()).get() + " MAT" + event.monster.stats.getStat(LodMod.MAGIC_ATTACK_STAT.get()).get()  + " DF" + event.monster.stats.getStat(LodMod.DEFENSE_STAT.get()).get()  + " MDF" + event.monster.stats.getStat(LodMod.MAGIC_DEFENSE_STAT.get()).get()  + ']';
      } else {
        text = stat.getCurrent() + " AT" + event.monster.stats.getStat(LodMod.ATTACK_STAT.get()).get()  + " MAT" + event.monster.stats.getStat(LodMod.MAGIC_ATTACK_STAT.get()).get()  + " DF" + event.monster.stats.getStat(LodMod.DEFENSE_STAT.get()).get()  + " MDF" + event.monster.stats.getStat(LodMod.MAGIC_DEFENSE_STAT.get()).get()  + ']';;
      }*/

      text = stat.getCurrent() + "/" + stat.getMax();

      RENDERER
        .queueOrthoModel(RENDERER.opaqueQuad, transforms, QueuedModelStandard.class)
        .monochrome(0.0f)
        .translucency(Translucency.HALF_B_PLUS_HALF_F);

      transforms.scaling(238.0f * hp, 20.0f, 1.0f);
      RENDERER
        .queueOrthoModel(RENDERER.opaqueQuad, transforms, QueuedModelStandard.class)
        .colour(r, g, b)
        .translucency(Translucency.B_PLUS_QUARTER_F);
      renderText(text, 41, 6, this.hpFont);
    }

    for(int i = 0; i < battleState_8006e398.getAllBentCount(); i++) {
      final ScriptState<? extends BattleEntity27c> state = battleState_8006e398.allBents_e0c.get(i);
      final BattleEntity27c bent = state.innerStruct_00;
      if(bent instanceof final PlayerBattleEntity player) {
        final Battle battle = ((Battle)currentEngineState_8004dd04);
        if(player.character.template instanceof Lavitz || player.character.template instanceof Albert) {
          this.renderCharacterBar(player, 0.0f, 1.0f, 0.0f, this.windMark[monster.typeBentSlot_276] == 4 ? 1.0f : this.windMark[monster.typeBentSlot_276] / 4.0f, false);
        } else if(player.character.template instanceof Shana || player.character.template instanceof Miranda) {
          final float r, g, b;
          if(this.getEquipment("dragoon_modifier:fire_arrow", player, EquipmentSlot.WEAPON)) {
            r = 1.0f;
            g = 0.0f;
            b = 0.0f;
          } else if(this.getEquipment("dragoon_modifier:water_arrow", player, EquipmentSlot.WEAPON)) {
            r = 0.0f;
            g = 0.5f;
            b = 1.0f;
          } else if(this.getEquipment("dragoon_modifier:wind_arrow", player, EquipmentSlot.WEAPON)) {
            r = 0.0f;
            g = 1.0f;
            b = 0.0f;
          } else if(this.getEquipment("dragoon_modifier:earth_arrow", player, EquipmentSlot.WEAPON)) {
            r = 0.5f;
            g = 0.35f;
            b = 0.25f;
          } else if(this.getEquipment("dragoon_modifier:dark_arrow", player, EquipmentSlot.WEAPON)) {
            r = 0.0f;
            g = 0.0f;
            b = 0.5f;
          } else if(this.getEquipment("dragoon_modifier:light_arrow", player, EquipmentSlot.WEAPON)) {
            r = 1.0f;
            g = 1.0f;
            b = 0.0f;
          } else if(this.getEquipment("dragoon_modifier:thunder_arrow", player, EquipmentSlot.WEAPON)) {
            r = 0.5f;
            g = 0.0f;
            b = 1.0f;
          } else {
            r = 0.0f;
            g = 0.0f;
            b = 0.0f;
          }

          if(r + g + b != 0.0f) {
            final float arrowPercent = (float)this.shanaArrowCount[player.typeBentSlot_276] / this.shanaMaxArrowCount[player.typeBentSlot_276];
            this.renderCharacterBar(player, r, g, b, 1.0f, true);
            this.renderCharacterBar(player, r, g, b, arrowPercent, false);
            this.shanaDeffArrow[player.typeBentSlot_276] = true;
          }

          this.renderElementalArrows(player);
        } else if(player.character.template instanceof Haschel && this.haschelInPartyWithDragoon) {
          this.renderCharacterBar(player, 0.5f, 0.0f, 1.0f, thunderCharge[monster.typeBentSlot_276] == 10 ? 1.0f : thunderCharge[monster.typeBentSlot_276] / 10.0f, false);
          if(staticCharge[player.typeBentSlot_276] > 0) {
            this.renderCharacterBar(player, 0.0f, 0.8f, 1.0f, staticCharge[player.typeBentSlot_276] == 20 ? 1.0f : staticCharge[player.typeBentSlot_276] / 20.0f, true);
          }
        }
      }
    }
  }
  //endregion

  //region Battle Monster
  @EventListener(priority = Priority.HIGHEST)
  public void monsterStats(final MonsterStatsEvent event) {
    /*if(ultimateBattle) {
      final int ovrId = event.enemyId;
      for(int x = 0; x < ultimateData.size(); x++) {
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
    } else {*/
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

      if(this.isHardMode()) {
        if(ovrId == 283) {
          event.maxHp = Integer.parseInt(monsterStats.get(ovrId)[1]) * 2;
        } else if(ovrId == 388) {
          gameState_800babc8.items_2e9.setMaxSize(64);
          for(int i = 0; i < gameState_800babc8.items_2e9.getSize(); i++) {
            gameState_800babc8.items_2e9.give(gameState_800babc8.items_2e9.get(i));
          }
        }
      }
    //}
  }

  @EventListener(priority = Priority.LOWEST)
  public void enemyRewards(final EnemyRewardsEvent event) {
    final int enemyId = event.enemyId;
    final String difficulty = CONFIG.getConfig(DIFFICULTY.get());

    event.clear();
    /*if(ultimateBattle) {
      event.xp = 0;
      event.gold = 0;

      if(ultimateBossSelected == draModSave.ultimateBossStage + 1) {
        for(int i = 0; i < ultimateData.size(); i++) {
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
                event.add(new CombatantStruct1a8.ItemDrop(Integer.parseInt(ultimateData.get(i)[28]), new ItemStack(REGISTRIES.items.getEntry(item).get(), 1)));
              } catch(final Exception ignored) {
              }
            }
            break;
          }
        }
      }
    } else {*/
      final String item = monstersRewardsStats.get(enemyId)[3];
      final int exp = Integer.parseInt(monstersRewardsStats.get(enemyId)[0]);
      event.xp = exp;
      /*if("Hell Mode".equals(difficulty) || "Hard + Hell Bosses".equals(difficulty)) {
        if(ArrayUtils.contains(this.bossEncounters, encounterId_800bb0f8)) {
          int activePartyMembers = 0;
          for(int x = 0; x < 3; x++) {
            if(gameState_800babc8.charIds_88.get(x) != -1) {
              activePartyMembers++;
            }
          }

          if(activePartyMembers == 3) {
            event.xp = (int)(exp * 1.5); //TODO: increase or decrease this
          }
        }
      }*/
      event.gold = Integer.parseInt(monstersRewardsStats.get(enemyId)[1]);
      if(!item.startsWith("lod:_") && !item.startsWith("lod:None")) {
        try {
          event.add(new CombatantStruct1a8.ItemDrop(Integer.parseInt(monstersRewardsStats.get(enemyId)[2]), REGISTRIES.equipment.getEntry(item).get()));
        } catch(final Exception ignored) {
        }

        try {
          event.add(new CombatantStruct1a8.ItemDrop(Integer.parseInt(monstersRewardsStats.get(enemyId)[2]), new ItemStack(REGISTRIES.items.getEntry(item).get(), 1)));
        } catch(final Exception ignored) {
        }
      }

      if(this.isHardMode() || this.isHellMode()) {
        if(enemyId < 253) {
          if(submapCut_80052c30 >= 114 && submapCut_80052c30 <= 123) { //Volcano Villude
            event.add(new CombatantStruct1a8.ItemDrop(36, REGISTRIES.equipment.getEntry("dragoon_modifier:fire_arrow").get()));
          } else if((submapCut_80052c30 >= 301 && submapCut_80052c30 <= 305)) { //Undersea Cavern
            event.add(new CombatantStruct1a8.ItemDrop(24, REGISTRIES.equipment.getEntry("dragoon_modifier:water_arrow").get()));
          } else if((submapCut_80052c30 >= 433 && submapCut_80052c30 <= 438)) { //Kashua Glacier
            event.add(new CombatantStruct1a8.ItemDrop(24, REGISTRIES.equipment.getEntry("dragoon_modifier:water_arrow").get()));
          } else if((submapCut_80052c30 >= 130 && submapCut_80052c30 <= 139)) { //Nest of Dragon
            event.add(new CombatantStruct1a8.ItemDrop(24, REGISTRIES.equipment.getEntry("dragoon_modifier:wind_arrow").get()));
          } else if((submapCut_80052c30 >= 339 && submapCut_80052c30 <= 346)) { //Evergreen Forest
            event.add(new CombatantStruct1a8.ItemDrop(24, REGISTRIES.equipment.getEntry("dragoon_modifier:wind_arrow").get()));
          } else if((submapCut_80052c30 >= 261 && submapCut_80052c30 <= 268)) { //Home of Giganto
            event.add(new CombatantStruct1a8.ItemDrop(24, REGISTRIES.equipment.getEntry("dragoon_modifier:earth_arrow").get()));
          } else if((submapCut_80052c30 >= 393 && submapCut_80052c30 <= 405)) { //Kadessa
            event.add(new CombatantStruct1a8.ItemDrop(24, REGISTRIES.equipment.getEntry("dragoon_modifier:earth_arrow").get()));
          } else if((submapCut_80052c30 >= 261 && submapCut_80052c30 <= 296)) { //Phantom Ship
            event.add(new CombatantStruct1a8.ItemDrop(24, REGISTRIES.equipment.getEntry("dragoon_modifier:dark_arrow").get()));
          } else if((submapCut_80052c30 >= 539 && submapCut_80052c30 <= 553)) { //Death City Mayfil
            event.add(new CombatantStruct1a8.ItemDrop(24, REGISTRIES.equipment.getEntry("dragoon_modifier:dark_arrow").get()));
          } else if((submapCut_80052c30 >= 153 && submapCut_80052c30 <= 166)) { //Shrine of Shirley
            event.add(new CombatantStruct1a8.ItemDrop(24, REGISTRIES.equipment.getEntry("dragoon_modifier:light_arrow").get()));
          } else if((submapCut_80052c30 >= 597 && submapCut_80052c30 <= 622)) { //Moon
            event.add(new CombatantStruct1a8.ItemDrop(24, REGISTRIES.equipment.getEntry("dragoon_modifier:light_arrow").get()));
          } else if((submapCut_80052c30 >= 477 && submapCut_80052c30 <= 502)) { //Vellweb
            event.add(new CombatantStruct1a8.ItemDrop(24, REGISTRIES.equipment.getEntry("dragoon_modifier:thunder_arrow").get()));
          } else if((submapCut_80052c30 >= 441 && submapCut_80052c30 <= 452)) { //Flanvel Tower
            event.add(new CombatantStruct1a8.ItemDrop(24, REGISTRIES.equipment.getEntry("dragoon_modifier:thunder_arrow").get()));
          } else {
            event.add(new CombatantStruct1a8.ItemDrop(24, this.getRandomElementArrow()));
          }
        }

        /*if("Hell Mode".equals(difficulty) || "Hard + Hell Bosses".equals(difficulty)) {
          if(encounterId_800bb0f8 == 403 && enemyId == 301) {
            event.add(new CombatantStruct1a8.ItemDrop(100, new ItemStack(REGISTRIES.items.getEntry("dragoon_modifier:weak_shield").get(), 1)));
            event.add(new CombatantStruct1a8.ItemDrop(100, new ItemStack(REGISTRIES.items.getEntry("dragoon_modifier:weak_shield").get(), 1)));
            event.add(new CombatantStruct1a8.ItemDrop(100, new ItemStack(REGISTRIES.items.getEntry("dragoon_modifier:super_spirit_pot").get(), 1)));
          }
        }*/
      }

      /*if(faustBattle && event.enemyId == 344) {
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
    }*/
  }

  @EventListener(priority = Priority.HIGHEST)
  public void repeatItemReturn(final RepeatItemReturnEvent event) {
    final String difficulty = CONFIG.getConfig(DIFFICULTY.get());

    if("Japan Demo".equals(difficulty)) {
      event.returnItem = "psyche_bomb_x".equals(event.stack.getItem().getRegistryId().entryId());
    } else {
      if("dragoon_modifier:archangels_prayer".equals(event.stack.getItem().getRegistryId().toString())) {
        event.returnItem = true;
      }
    }
  }

  public Equipment getRandomElementArrow() {
    final String[] arrows = {"dragoon_modifier:fire_arrow", "dragoon_modifier:water_arrow", "dragoon_modifier:wind_arrow", "dragoon_modifier:earth_arrow", "dragoon_modifier:dark_arrow", "dragoon_modifier:light_arrow", "dragoon_modifier:thunder_arrow"};
    return REGISTRIES.equipment.getEntry(arrows[new Random().nextInt(arrows.length)]).get();
  }

  public void displayNumbers(final int scriptIndex, final int damage, final int xOffset, final int yOffset, final float r, final float g, final float b) {
    ((Battle)currentEngineState_8004dd04).hud.addFloatingNumberForBent(scriptIndex, damage, xOffset, yOffset, r, g, b);
  }
  //endregion
}