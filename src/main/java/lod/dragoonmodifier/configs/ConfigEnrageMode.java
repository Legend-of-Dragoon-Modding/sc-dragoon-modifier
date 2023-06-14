package lod.dragoonmodifier.configs;

import legend.game.saves.ConfigStorageLocation;
import legend.game.saves.EnumConfigEntry;
import lod.dragoonmodifier.configs.values.EnrageMode;

public class ConfigEnrageMode extends EnumConfigEntry<EnrageMode> {
    public ConfigEnrageMode() {
        super(EnrageMode.class, EnrageMode.OFF, ConfigStorageLocation.CAMPAIGN);
    }
}