package lod.dragoonmodifier.configs;

import legend.core.GameEngine;
import legend.core.IoHelper;
import legend.game.inventory.screens.controls.Dropdown;
import legend.game.saves.ConfigCollection;
import legend.game.saves.ConfigEntry;
import legend.game.saves.ConfigStorageLocation;
import lod.dragoonmodifier.events.DifficultyChangedEvent;

import java.io.File;
import java.nio.file.Path;

public class ConfigDifficultyEntry extends ConfigEntry<String> {
    public ConfigDifficultyEntry() {
        super(
            "US",
            ConfigStorageLocation.CAMPAIGN,
            str -> IoHelper.stringToBytes(str, 1),
            bytes -> IoHelper.stringFromBytes(bytes, 1, "")
        );

        this.setEditControl((current, gameState) -> {
            final Dropdown dropdown = new Dropdown();
            final File[] modFolders = Path.of("./mods/csvstat/").toFile().listFiles(File::isDirectory);
            int i = 0;
            for (File directory : modFolders) {
                dropdown.addOption(directory.getName());
                if (directory.getName().equals(gameState.getConfig(this))) {
                    dropdown.setSelectedIndex(i);
                }
                i++;
            }

            dropdown.onSelection(index -> gameState.setConfig(this, dropdown.getSelectedOption()));

            return dropdown;
        });
    }

    @Override
    public void onChange(final ConfigCollection configCollection, final String oldValue, final String newValue) {
        System.out.println("[Dragoon Modifier] Mod Changed: " + oldValue + " -> " + newValue);
        super.onChange(configCollection, oldValue, newValue);

        GameEngine.EVENTS.postEvent(new DifficultyChangedEvent(configCollection, newValue));
    }
}
