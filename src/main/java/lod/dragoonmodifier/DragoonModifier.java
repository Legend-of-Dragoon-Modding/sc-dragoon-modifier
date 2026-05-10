package lod.dragoonmodifier;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import legend.core.GameEngine;
import legend.game.characters.Element;
import legend.game.combat.types.CombatantStruct1a8;
import legend.game.inventory.ItemStack;
import legend.game.modding.events.battle.EnemyRewardsEvent;
import legend.game.modding.events.battle.MonsterStatsEvent;
import legend.game.modding.events.config.ConfigLoadedEvent;
import legend.game.saves.ConfigEntry;
import legend.game.saves.ConfigRegistryEvent;
import legend.lodmod.LodEngineStateTypes;
import lod.dragoonmodifier.configs.DifficultyEntryConfig;
import org.apache.commons.lang3.ArrayUtils;
import org.legendofdragoon.modloader.Mod;
import org.legendofdragoon.modloader.events.EventListener;
import org.legendofdragoon.modloader.registries.Registrar;
import org.legendofdragoon.modloader.registries.RegistryDelegate;
import org.legendofdragoon.modloader.registries.RegistryId;

import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static legend.core.GameEngine.REGISTRIES;
import static legend.game.EngineStates.currentEngineState_8004dd04;
import static legend.game.Scus94491BpeSegment_800b.encounterId_800bb0f8;
import static legend.game.Scus94491BpeSegment_800b.gameState_800babc8;
import static legend.game.Scus94491BpeSegment_800b.pregameLoadingStage_800bb10c;

@Mod(id = DragoonModifier.MOD_ID, version = "^3.0.0")
public class DragoonModifier {
  //region Vars
  public static final String MOD_ID = "dragoon_modifier";

  //CSVs
  public static final List<String[]> monsterStats = new ArrayList<>();
  public static final List<String[]> monstersRewardsStats = new ArrayList<>();

  //Configs
  public static final Registrar<ConfigEntry<?>, ConfigRegistryEvent> DRAMOD_CONFIG_REGISTRAR = new Registrar<>(REGISTRIES.config, MOD_ID);
  public static final RegistryDelegate<DifficultyEntryConfig> DIFFICULTY = DRAMOD_CONFIG_REGISTRAR.register("difficulty", DifficultyEntryConfig::new);

  //Constants
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
  //endregion

  //region Mod ID
  public DragoonModifier() {
    GameEngine.EVENTS.register(this);
  }

  public RegistryId id(final String entryId) {
    return new RegistryId(MOD_ID, entryId);
  }

  public RegistryId idCore(final String entryId) {
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
  //endregion

  @EventListener
  public void eventConfigRegistry(final ConfigRegistryEvent event) {
    DRAMOD_CONFIG_REGISTRAR.registryEvent(event);
  }

  @EventListener
  public void eventConfigLoaded(final ConfigLoadedEvent event) {
    this.loadAllCsvs(event.configCollection.getConfig(DIFFICULTY.get()));
  }

  private void loadAllCsvs(final String difficulty) {
    this.loadCsvIntoList(difficulty, monsterStats, "monster_stats.csv");
    this.loadCsvIntoList(difficulty, monstersRewardsStats, "monster_rewards.csv");
  }

  //region Battle Monster
  @EventListener
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
    //}
  }

  @EventListener
  public void enemyRewards(final EnemyRewardsEvent event) {
    final int enemyId = event.enemyId;
    final String difficulty = GameEngine.CONFIG.getConfig(DIFFICULTY.get());

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

      /*if("Hard Mode".equals(difficulty) || "US + Hard Bosses".equals(difficulty) || "Hell Mode".equals(difficulty) || "Hard + Hell Bosses".equals(difficulty)) {
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
        }

        if("Hell Mode".equals(difficulty) || "Hard + Hell Bosses".equals(difficulty)) {
          if(encounterId_800bb0f8 == 403 && enemyId == 301) {
            event.add(new CombatantStruct1a8.ItemDrop(100, new ItemStack(REGISTRIES.items.getEntry("dragoon_modifier:weak_shield").get(), 1)));
            event.add(new CombatantStruct1a8.ItemDrop(100, new ItemStack(REGISTRIES.items.getEntry("dragoon_modifier:weak_shield").get(), 1)));
            event.add(new CombatantStruct1a8.ItemDrop(100, new ItemStack(REGISTRIES.items.getEntry("dragoon_modifier:super_spirit_pot").get(), 1)));
          }
        }
      }*/

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
  //endregion
}