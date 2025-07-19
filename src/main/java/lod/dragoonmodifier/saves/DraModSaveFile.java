package lod.dragoonmodifier.saves;

import java.io.Serializable;

public class DraModSaveFile implements Serializable {
  public int monsterArenaStage = 0;
  public int huntQuestStage = 0;
  public int ultimateBossStage = 0;
  public int faustDefeated = 0;
  public boolean[] baseGameAchievements = new boolean[255];
  public boolean[] enhancedAchievements = new boolean[255];
}
