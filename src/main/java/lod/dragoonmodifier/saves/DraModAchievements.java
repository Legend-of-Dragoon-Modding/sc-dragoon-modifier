package lod.dragoonmodifier.saves;

public class DraModAchievements {
  public enum TrophyType {
    BRONZE(1),
    SILVER(2),
    GOLD(3);

    private final int type;

    TrophyType(final int type) {
      this.type = type;
    }
  }

  public enum Enhanced {
    OPEN_DRAMOD_ACHIEVEMENTS(0, TrophyType.BRONZE, "Achievements?", "Achievements.", false),
    OPEN_DRAMOD_MENU(1, TrophyType.BRONZE, "DragooooonModifier", "Open the DraMod Menu for the first time.", false),
    SELES_COMMANDER(2, TrophyType.BRONZE, "Sandora Commander", "Defeat the commander at Seles.", true),
    FRUEGEL_PHASE_1(3, TrophyType.BRONZE, "Warden Thwarted", "Defeat Fruegel, the prison warden.", true),
    UROBOLUS(4, TrophyType.BRONZE, "Big Snake", "Defeaut Urobolus, Guardian of the Cave.", true),
    KONGOL(5, TrophyType.BRONZE, "Tall and Handsome", "Defeat the dashing Kongol.", true),
    VIRAGE(6, TrophyType.BRONZE, "Ancient Enemy", "Defeat the reawakened Virage.", true),
    FIREBIRD(7, TrophyType.BRONZE, "FIREBIRB", "Defeat the Fire Bird.", true),
    MARSHLAND_COMMANDER(8, TrophyType.SILVER, "Battleground", "Defeat Sandora Commander at Marshland.", true),
    GREHAM_FEYRBRAND(9, TrophyType.BRONZE, "Dragon and Dragoon", "Defeat Greham and Feyrbrand.", true),
    DRAKE_THE_BANDIT(10, TrophyType.BRONZE, "Temple Keeper", "Defeat the trickster.", true),
    FRUEGEL_PHASE_2(11, TrophyType.BRONZE, "Paint it Red", "Defeat Fruegel... again.", true),
    GIGANTO_ARMOR_KONGOL(12, TrophyType.BRONZE, "A game?", "Defeat the even more dashing Kongol.", true),
    DOEL(13, TrophyType.GOLD, "Country Saved", "Defeat Doel.", true),
    MAPPI(14, TrophyType.BRONZE, "Grin", "Defeat Mappi.", true),
    VIRAGE_2(15, TrophyType.BRONZE, "Ancient Enemy 2", "Defeat Virage. These things just keep appearing.", true),
    GOON_GANG(16, TrophyType.BRONZE, "Defeat the goons", "Defeat the goons, not the good kind of goon either.", true),
    LENUS(17, TrophyType.BRONZE, "What's on her back?", "This battle against Lenus was easy.", true),
    GHOST_COMMANDER(18, TrophyType.BRONZE, "Spooky Scary Skeletons", "Defeat the Ghost Commander.", true),
    LENUS_AND_REGOLE(19, TrophyType.GOLD, "Hot Prize", "Lenus defeated again.", true),
    SUPER_VIRAGE(20, TrophyType.SILVER, "Ancient Weapon 1", "Defeat the Super Virage.", true),
    GRAND_JEWEL(21, TrophyType.BRONZE, "Rock Stick", "Defeat the Grand Jewel.", true),
    DIVINE_DRAGON(22, TrophyType.BRONZE, "Winged Giraffe", "Defeat Divine Dragon.", true),
    WINDIGO(23, TrophyType.BRONZE, "Windigo", "Defeat Windigo.", true),
    KAMUY(24, TrophyType.SILVER, "Save the Doggo", "Save Kamuy.", true),
    LLOYD(25, TrophyType.GOLD, "Revenge?", "Defeat Lloyd.", true),
    ULTIMATE_START(26, TrophyType.BRONZE, "Ultimate Start", "Defeat the first Ultimate Boss.", false),
    ULTIMATE_BOSS(27, TrophyType.SILVER, "Ultimate Urobolosussy", "Same snake but different.", false),
    BLACK_CASTLE(28, TrophyType.BRONZE, "Black Castle", "Something special is being researched.", true),
    LAST_KRAKEN(29, TrophyType.BRONZE, "Off Center", "Why is Last Kraken off center?", true),
    POLTER_ARMOR(30, TrophyType.SILVER, "Possessed Weaponry", "Defeat Polter Armor", true),
    EXECUTIONERS(31, TrophyType.BRONZE, "Executioners", "GUILTY GUILTY GUILTY", true),
    ULTIMATE_BIRB(32, TrophyType.SILVER, "Ultimate Birb", "A battle of attrition.", false),
    GHOST_FEYRBRAND(33, TrophyType.SILVER, "Ghostly Dragon 1", "Defeat Ghost Feyrbrand.", true),
    GHOST_REGOLE(34, TrophyType.SILVER, "Ghostly Dragon 2", "Defeat Ghost Regole.", true),
    GHOST_DIVINE_DRAGON(35, TrophyType.SILVER, "Ghostly Dragon 3", "Defeat Ghost Divine Dragon.", true),
    ZACKWELL(36, TrophyType.BRONZE, "Soul Keeper","Defeat Zackwell", true),
    IMAGO(37, TrophyType.BRONZE, "Metamorphosis", "Defeat the trilogy.", true),
    SUPER_VIRAGE_2(38, TrophyType.BRONZE, "Ancient Weapon 2", "Defeat Super Virage 2.", true),
    ZIEG(39, TrophyType.BRONZE, "Weak", "Defeat Zieg.", true),
    MOON_FAUST(40, TrophyType.BRONZE, "Special Faust", "Faust with a slight EXP boost.", false),
    POINT_OF_RETURN(41, TrophyType.BRONZE, "Point of Return", "Use DraMod menu to warp off Moon.", true),
    BELZAC(42, TrophyType.SILVER, "Golden Dragoon", "Defeat the Golden Dragoon.", true),
    DAMIA(43, TrophyType.SILVER, "Blue-Sea Dragoon", "Defeat the Blue-Sea Dragoon.", true),
    SYUVEIL(44, TrophyType.SILVER, "Jade Dragoon", "Defeat the Jade Dragoon.", true),
    KAZAS(45, TrophyType.SILVER, "Violet Dragoon", "Defeat the Violet Dragoon.", true),
    OLD_GEN(46, TrophyType.GOLD, "Freed Souls", "Defeat all old Dragoons.", true),
    BURN_STACK(47, TrophyType.BRONZE, "Stacked", "Gain one burn stack.", false),
    WIND_MARK(48, TrophyType.BRONZE, "Marked", "Gain one wind mark.", false),
    ARROW_EXCHANGE(49, TrophyType.BRONZE, "Swapped", "Swap to an elemental arrow.", false),
    OVERFLOW(50, TrophyType.BRONZE, "Overflowed", "Use overflow to deal.", false),
    CHARGED(51, TrophyType.BRONZE, "Overchaged", "Use an enhanced Spark Net.", false),
    ICE_SHIELD(52, TrophyType.BRONZE, "Shielded", "Gain some Ice Shield.", false),
    COUNTER_STANCE(53, TrophyType.BRONZE, "Countered", "Counter attack for the first time.", false),
    DRAGOON_LV_4(54, TrophyType.BRONZE, "D.Lv 4", "Get your first Dragoon Level 4.", false),
    DRAGOON_LV_6(55, TrophyType.BRONZE, "D.Lv 6", "Get your first Dragoon Level 6.", false),
    DART_MAXED(56, TrophyType.SILVER, "Dart", "Max Dart's Dragoon and Additions.", true),
    LAVITZ_MAXED(57, TrophyType.SILVER, "Lavitz", "Max Lavitz's Dragoon and Additions.", true),
    SHANA_MAXED(58, TrophyType.SILVER, "Shana", "Max Shana's Dragoon.", true),
    ROSE_MAXED(59, TrophyType.SILVER, "Rose", "Max Rose's Dragoon and Additions.", true),
    HASCHEL_MAXED(60, TrophyType.SILVER, "Haschel", "Max Haschel's Dragoon and Additions.", true),
    ALBERT_MAXED(61, TrophyType.SILVER, "Albert", "Max Albert's Dragoon and Additions.", true),
    MERU_MAXED(62, TrophyType.SILVER, "Meru", "Max Meru's Dragoon and Additions.", true),
    KONGOL_MAXED(63, TrophyType.SILVER, "Kongol", "Max Kongol's Dragoon and Additions.", true),
    WHO_MAXED(64, TrophyType.SILVER, "???", "Max ???'s Dragoon.", true),
    OVERCAPPED(65, TrophyType.BRONZE, "Overcapped", "Deal over 9999 damage.", false),
    RICH(66, TrophyType.BRONZE, "Rich", "That's a lot of Gold but not enough!", false),
    BATTLE_50(67, TrophyType.BRONZE, "Battle 50", "Complete 50 battles.", false),
    BATTLE_150(68, TrophyType.SILVER, "Battle 150", "Complete 150 battles.", false),
    BATTLE_400(69, TrophyType.GOLD, "Battle 400", "Complete 400 battles.", false),
    ULTIMATE_ARMORY(70, TrophyType.SILVER, "Ultimate Armory", "Buy an Ultimate equip.", false),
    STARDUST_10(71, TrophyType.BRONZE, "Stardust 10", "Hand in 10 Stardusts.", false),
    STARDUST_20(72, TrophyType.BRONZE, "Stardust 20", "Hand in 20 Stardusts.", false),
    STARDUST_30(73, TrophyType.BRONZE, "Stardust 30", "Hand in 30 Stardusts.", false),
    STARDUST_40(74, TrophyType.BRONZE, "Stardust 40", "Hand in 40 Stardusts.", false),
    STARDUST_50(75, TrophyType.BRONZE, "Make a Wish", "Hand in 50 Stardusts.", false),
    LOOP(76, TrophyType.BRONZE, "Loop", "Obtain all Repeat Items.", false),
    ;

    private final int id;
    private final TrophyType trophy;
    private final String name;
    private final String desc;
    private final boolean hidden;

    Enhanced(final int id, final TrophyType trophy, final String name, final String desc, final boolean hidden) {
      this.id = id;
      this.trophy = trophy;
      this.name = name;
      this.desc = desc;
      this.hidden = hidden;
    }

    public int getId() {
      return this.id;
    }

    public String getName() {
      return this.name;
    }

    public String getDesc() {
      return this.desc;
    }

    public TrophyType getType() { return this.trophy; }

    public boolean isHidden() { return this.hidden; }

    public static Enhanced getById(final int id) {
      for(final Enhanced e : values()) {
        if(e.getId() == id)
          return e;
      }
      return null;
    }
  }

  public enum BaseGame {
    TEST(1, TrophyType.BRONZE, "", "")

    ;

    private final int id;
    private final TrophyType trophy;
    private final String name;
    private final String desc;

    BaseGame(final int id, final TrophyType trophy, final String name, final String desc) {
      this.id = id;
      this.trophy = trophy;
      this.name = name;
      this.desc = desc;
    }

    public int getId() {
      return this.id;
    }

    public String getName() {
      return this.name;
    }

    public String getDesc() {
      return this.desc;
    }

    public static BaseGame getById(final int id) {
      for(final BaseGame b : values()) {
        if(b.getId() == id)
          return b;
      }
      return null;
    }
  }
}
