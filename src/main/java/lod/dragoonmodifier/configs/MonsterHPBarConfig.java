package lod.dragoonmodifier.configs;

import legend.game.saves.BoolConfigEntry;
import legend.game.saves.ConfigCategory;
import legend.game.saves.ConfigStorageLocation;
import legend.game.saves.EnumConfigEntry;

public class MonsterHPBarConfig extends BoolConfigEntry {
  public MonsterHPBarConfig() {
    super(false, ConfigStorageLocation.CAMPAIGN, ConfigCategory.USER_INTERFACE);
  }

  @Override
  public boolean hasHelp() {
    return true;
  }
}