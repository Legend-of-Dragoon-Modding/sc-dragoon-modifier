package lod.dragoonmodifier.configs;

import legend.game.saves.ConfigStorageLocation;
import legend.game.saves.EnumConfigEntry;
import lod.dragoonmodifier.configs.values.NeverGuard;

public class ConfigNeverGuard extends EnumConfigEntry<NeverGuard> {
    public ConfigNeverGuard() {
        super(NeverGuard.class, NeverGuard.OFF, ConfigStorageLocation.CAMPAIGN);
    }
}