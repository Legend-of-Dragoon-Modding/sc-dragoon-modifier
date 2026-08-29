package lod.dragoonmodifier.configs;

import legend.game.saves.BoolConfigEntry;
import legend.game.saves.ConfigCategory;
import legend.game.saves.ConfigStorageLocation;

public class DamageTrackerConfig extends BoolConfigEntry {
  public DamageTrackerConfig() {
    super(false, ConfigStorageLocation.CAMPAIGN, ConfigCategory.USER_INTERFACE);
  }

  @Override
  public boolean hasHelp() {
    return true;
  }
}