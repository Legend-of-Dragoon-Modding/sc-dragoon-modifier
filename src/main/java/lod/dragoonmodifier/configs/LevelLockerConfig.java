package lod.dragoonmodifier.configs;

import legend.game.saves.BoolConfigEntry;
import legend.game.saves.ConfigCategory;
import legend.game.saves.ConfigStorageLocation;

public class LevelLockerConfig extends BoolConfigEntry {
  public LevelLockerConfig() {
    super(false, ConfigStorageLocation.CAMPAIGN, ConfigCategory.GAMEPLAY);
  }

  @Override
  public boolean hasHelp() {
    return true;
  }
}