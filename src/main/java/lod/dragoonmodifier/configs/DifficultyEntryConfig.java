package lod.dragoonmodifier.configs;

import legend.core.GameEngine;
import legend.core.IoHelper;
import legend.core.lang.RawText;
import legend.game.inventory.screens.controls.Dropdown;
import legend.game.saves.ConfigCategory;
import legend.game.saves.ConfigCollection;
import legend.game.saves.ConfigEntry;
import legend.game.saves.ConfigStorageLocation;
import lod.dragoonmodifier.events.DifficultyChangedEvent;

import java.io.File;
import java.nio.file.Path;
import java.util.function.Function;

import static legend.game.Scus94491BpeSegment_800b.gameState_800babc8;
import static lod.dragoonmodifier.DragoonModifier.DIFFICULTY;

public class DifficultyEntryConfig extends ConfigEntry<String> {
  public DifficultyEntryConfig() {
    super("Retail NA", ConfigStorageLocation.CAMPAIGN, ConfigCategory.GAMEPLAY, str -> IoHelper.stringToBytes(str, 1), bytes -> IoHelper.stringFromBytes(bytes, 1, ""));

    this.setEditControl((current, gameState) -> {
      final Dropdown dropdown = new Dropdown<>((i, e) -> new RawText(e.toString()));
      final File[] modFolders = Path.of("./mods/dragoon_modifier/").toFile().listFiles(File::isDirectory);
      int i = 0;
      for(final File directory : modFolders) {
        if((!"Ultimate".equals(directory.getName()) && !"Damage Tracker".equals(directory.getName()) && !"scripts".equals(directory.getName()) && !"patches".equals(directory.getName()) && !"lang".equals(directory.getName()) && !"gfx".equals(directory.getName()))) {
          if(gameState_800babc8 == null) {
            dropdown.addOption(directory.getName());
          } else {
            if(directory.getName().equals(GameEngine.CONFIG.getConfig(DIFFICULTY.get()))) {
              dropdown.addOption(directory.getName());
            }
          }

          if("Retail NA + Hard Bosses".equals(GameEngine.CONFIG.getConfig(DIFFICULTY.get())) && "Hard Mode".equals(directory.getName())) {
            dropdown.addOption(directory.getName());
          }

          if("Hard Mode".equals(GameEngine.CONFIG.getConfig(DIFFICULTY.get())) && "Retail NA + Hard Bosses".equals(directory.getName())) {
            dropdown.addOption(directory.getName());
          }

          if(directory.getName().equals(gameState.getConfig(this))) {
            dropdown.setSelectedIndex(i);
          }
          i++;
        }
      }

      dropdown.onSelection(index -> gameState.setConfig(this, dropdown.getSelectedOption().toString()));

      return dropdown;
    });
  }

  @Override
  public void onChange(final ConfigCollection configCollection, final String oldValue, final String newValue) {
    GameEngine.EVENTS.postEvent(new DifficultyChangedEvent(configCollection, newValue));
  }

  @Override
  public boolean availableInBattle() {
    return false;
  }

  @Override
  public boolean hasHelp() {
    return true;
  }
}
