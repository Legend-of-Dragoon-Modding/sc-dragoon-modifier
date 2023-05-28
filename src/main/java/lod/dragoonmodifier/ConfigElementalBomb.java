package lod.dragoonmodifier;

import legend.game.saves.ConfigStorageLocation;
import legend.game.saves.EnumConfigEntry;

public class ConfigElementalBomb extends EnumConfigEntry<ElementalBomb> {
    public ConfigElementalBomb() {
        super(ElementalBomb.class, ElementalBomb.OFF, ConfigStorageLocation.CAMPAIGN);
    }
}