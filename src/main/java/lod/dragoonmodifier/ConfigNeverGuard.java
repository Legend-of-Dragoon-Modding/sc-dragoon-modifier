package lod.dragoonmodifier;

import legend.game.saves.ConfigStorageLocation;
import legend.game.saves.EnumConfigEntry;

public class ConfigNeverGuard extends EnumConfigEntry<NeverGuard> {
    public ConfigNeverGuard() {
        super(NeverGuard.class, NeverGuard.OFF, ConfigStorageLocation.CAMPAIGN);
    }
}