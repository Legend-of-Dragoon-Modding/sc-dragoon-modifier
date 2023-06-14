package lod.dragoonmodifier.configs;

import legend.game.saves.ConfigStorageLocation;
import legend.game.saves.EnumConfigEntry;
import lod.dragoonmodifier.configs.values.MonsterHPNames;

public class ConfigMonsterHPNames extends EnumConfigEntry<MonsterHPNames> {
    public ConfigMonsterHPNames() {
        super(MonsterHPNames.class, MonsterHPNames.OFF, ConfigStorageLocation.CAMPAIGN);
    }
}
