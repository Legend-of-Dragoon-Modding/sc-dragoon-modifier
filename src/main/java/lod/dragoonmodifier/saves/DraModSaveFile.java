package lod.dragoonmodifier.saves;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class DraModSaveFile implements Serializable {
  public int monsterArenaStage = 0;
  public int huntQuestStage = 0;
  public int ultimateBossStage = 0;
  public int faustDefeated = 0;
  public Map<Integer, Boolean> baseGameAchievements = new HashMap<>();
  public Map<Integer, Boolean> enhancedAchievements = new HashMap<>();
}
