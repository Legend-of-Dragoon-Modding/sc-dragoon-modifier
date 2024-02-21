package lod.dragoonmodifier.configs.events;

import legend.core.GameEngine;
import legend.game.modding.coremod.CoreMod;
import legend.game.modding.events.Event;
import legend.game.saves.ConfigCollection;

public class DifficultyChangedEvent extends Event {
  public final ConfigCollection configCollection;
  public final String difficulty;

  public DifficultyChangedEvent(ConfigCollection configCollection, String difficulty) {
    this.configCollection = configCollection;
    this.difficulty = difficulty;

    if (difficulty.equals("Hell Mode") || difficulty.equals("Hard + Hell Bosses")) {
      GameEngine.EVENTS.postEvent(new HellModeAdjustmentEvent());
    }
  }
}
