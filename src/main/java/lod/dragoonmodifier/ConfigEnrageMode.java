package lod.dragoonmodifier;

import legend.game.saves.ConfigStorageLocation;
import legend.game.saves.EnumConfigEntry;

public class ConfigEnrageMode extends EnumConfigEntry<EnrageMode> {
    public ConfigEnrageMode() {
        super(EnrageMode.class, EnrageMode.OFF, ConfigStorageLocation.CAMPAIGN);
    }
}