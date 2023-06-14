package lod.dragoonmodifier.configs;

import legend.game.saves.ConfigStorageLocation;
import legend.game.saves.EnumConfigEntry;
import lod.dragoonmodifier.configs.values.ElementalBomb;

public class ConfigElementalBomb extends EnumConfigEntry<ElementalBomb> {
    public ConfigElementalBomb() {
        super(ElementalBomb.class, ElementalBomb.OFF, ConfigStorageLocation.CAMPAIGN);
    }
}