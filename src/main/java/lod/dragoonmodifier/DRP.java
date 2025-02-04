package lod.dragoonmodifier;

import de.jcm.discordgamesdk.Core;
import de.jcm.discordgamesdk.CreateParams;
import de.jcm.discordgamesdk.activity.Activity;
import legend.game.EngineStateEnum;
import legend.game.combat.bent.BattleEntity27c;
import legend.game.combat.bent.BattleEntityStat;
import legend.game.combat.bent.MonsterBattleEntity;
import legend.game.combat.bent.PlayerBattleEntity;
import legend.game.scripting.ScriptState;
import org.apache.commons.lang3.ArrayUtils;

import java.io.File;

import static legend.game.SItem.submapNames_8011c108;
import static legend.game.SItem.worldMapNames_8011c1ec;
import static legend.game.Scus94491BpeSegment_8002.getTimestampPart;
import static legend.game.Scus94491BpeSegment_8004.engineState_8004dd20;
import static legend.game.Scus94491BpeSegment_8006.battleState_8006e398;
import static legend.game.Scus94491BpeSegment_800b.continentIndex_800bf0b0;
import static legend.game.Scus94491BpeSegment_800b.encounterId_800bb0f8;
import static legend.game.Scus94491BpeSegment_800b.gameState_800babc8;
import static legend.game.Scus94491BpeSegment_800b.submapId_800bd808;

public class DRP implements Runnable {

  EngineStateEnum engineState = null;
  String gold = "";
  String battles = "";
  String stardust = "";
  String time = "";
  String chapter = "";
  String submap = "";
  String world = "";
  int lastHP = 0;

  public static final String[] charNames = {"Dart", "Lavitz", "Shana", "Rose", "Haschel", "Albert", "Meru", "Kongol", "???"};

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

  @Override
  public void run() {
    Core.init(new File("S:\\Sauce\\scdk-csv-stat-changer\\libs\\discord_game_sdk.dll"));

    try(final CreateParams params = new CreateParams()) {
      params.setClientID(1324511938138345566L);
      params.setFlags(CreateParams.getDefaultFlags());
      try(final Core core = new Core(params)) {
        while(true) {
          if(this.engineState != engineState_8004dd20) {
            try(final Activity activity = new Activity()) {
              final String state;
              switch(engineState_8004dd20) {
                case PRELOAD_00 -> state = "Loading Severed Chains";
                case UNUSED_01 -> state = "UNKNOWN";
                case TITLE_02 ->  state = "Title Screen";
                case TRANSITION_TO_NEW_GAME_03 -> state = "Starting a new game";
                case CREDITS_04 -> state = "Credits";
                case SUBMAP_05 -> state = "Exploring";
                case COMBAT_06 -> state = "Loading battle";
                case GAME_OVER_07 -> state = "Game Over";
                case WORLD_MAP_08 -> state = "Exploring the world";
                case FMV_09 -> state = "Watching FMV";
                case DISK_SWAP_10 -> state = "Finished a disk!";
                case FINAL_FMV_11 -> state = "Watching Ending FMV";
                default -> state = "";
              }
              activity.setDetails(state);
              core.activityManager().updateActivity(activity);
              this.lastHP = 0;
            }
            this.engineState = engineState_8004dd20;
          } else {
            if(this.engineState == EngineStateEnum.SUBMAP_05 || this.engineState == EngineStateEnum.WORLD_MAP_08) {
              try(final Activity activity = new Activity()) {
                try {
                  activity.setDetails("Exploring - " + (this.engineState == EngineStateEnum.SUBMAP_05 ? submapNames_8011c108[submapId_800bd808] : worldMapNames_8011c1ec[continentIndex_800bf0b0]) + " - " + getTimestampPart(gameState_800babc8.timestamp_a0, 0) + ':' + getTimestampPart(gameState_800babc8.timestamp_a0, 1) + ':' + getTimestampPart(gameState_800babc8.timestamp_a0, 2));
                  activity.setState(gameState_800babc8.gold_94 + "G " +
                    gameState_800babc8.stardust_9c + "S " +
                    gameState_800babc8._b4 + "B " +
                    gameState_800babc8._b8 + 'T');
                  core.activityManager().updateActivity(activity);
                } catch(final Exception ignored) {}
              }
            } else if(this.engineState == EngineStateEnum.COMBAT_06) {
              try {
                String battleType = "";
                String party = "";
                String partyLevel = "";
                int currentHP = 0;
                int maxHP = 0;
                if(ArrayUtils.contains(this.bossEncounters, encounterId_800bb0f8)) {
                  battleType = "Boss - HP: ";
                } else {
                  battleType = "Battle - HP: ";
                }
                for(int i = 0; i < battleState_8006e398.getAllBentCount(); i++) {
                  final ScriptState<? extends BattleEntity27c> state = battleState_8006e398.allBents_e0c[i];
                  final BattleEntity27c bobj = state.innerStruct_00;
                  if(bobj instanceof final PlayerBattleEntity player) {
                    party += charNames[player.charId_272] + '/';
                    partyLevel += player.getStat(BattleEntityStat.LEVEL) + "/";
                  }
                  if(bobj instanceof final MonsterBattleEntity monster) {
                    currentHP += monster.getStat(BattleEntityStat.CURRENT_HP);
                    maxHP += monster.getStat(BattleEntityStat.MAX_HP);
                  }
                }
                battleType += currentHP + "/" + maxHP;

                if(this.lastHP != currentHP) {
                  try(final Activity activity = new Activity()) {
                    try {
                      activity.setDetails(battleType);
                      activity.setState(party.substring(0, party.length() - 1) + " LV " + partyLevel.substring(0, partyLevel.length() - 1));
                      core.activityManager().updateActivity(activity);
                    } catch(final Exception ignored) {
                    }
                  }
                  this.lastHP = currentHP;
                }
              } catch(final Exception ignored) {}
            }
          }

          core.runCallbacks();
          try {
            // Sleep a bit to save CPU
            Thread.sleep(250);
          } catch(final InterruptedException e) {
            core.close();
            e.printStackTrace();
          }
        }
      }
    }
  }
}

