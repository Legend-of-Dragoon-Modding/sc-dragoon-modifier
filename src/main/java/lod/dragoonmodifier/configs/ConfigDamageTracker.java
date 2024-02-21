package lod.dragoonmodifier.configs;

import legend.game.saves.ConfigStorageLocation;
import legend.game.saves.EnumConfigEntry;
import lod.dragoonmodifier.configs.values.DamageTracker;
import lod.dragoonmodifier.configs.values.MonsterHPNames;

public class ConfigDamageTracker extends EnumConfigEntry<DamageTracker> {
    public ConfigDamageTracker() {
        super(DamageTracker.class, DamageTracker.OFF, ConfigStorageLocation.CAMPAIGN);
    }
}
