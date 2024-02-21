package lod.dragoonmodifier.configs;

import legend.core.IoHelper;
import legend.core.MathHelper;
import legend.game.inventory.screens.controls.Label;
import legend.game.inventory.screens.controls.NumberSpinner;
import legend.game.saves.ConfigCollection;
import legend.game.saves.ConfigEntry;
import legend.game.saves.ConfigStorageLocation;

import static legend.core.GameEngine.CONFIG;

public class ConfigUltimateBossDefeated extends ConfigEntry<String> {
    public ConfigUltimateBossDefeated() {
        super(
                "0",
                ConfigStorageLocation.SAVE,
                str -> IoHelper.stringToBytes(str, 4),
                bytes -> IoHelper.stringFromBytes(bytes, 4, "0")
        );

        this.setEditControl((current, gameState) -> {
            return new Label(CONFIG.getConfig(this).toString());
        });
    }

    @Override
    public void onChange(final ConfigCollection configCollection, final String oldValue, final String newValue) {
        super.onChange(configCollection, oldValue, newValue);
    }
}
