package lod.dragoonmodifier.events;

import legend.game.modding.events.Event;
import legend.game.saves.ConfigCollection;

public class DifficultyChangedEvent extends Event {
  public final ConfigCollection configCollection;
  public final String difficulty;

  public DifficultyChangedEvent(ConfigCollection configCollection, String difficulty) {
    this.configCollection = configCollection;
    this.difficulty = difficulty;
  }
}
