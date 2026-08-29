package lod.dragoonmodifier.events;

import legend.game.saves.ConfigCollection;
import org.legendofdragoon.modloader.events.Event;

public class DifficultyChangedEvent extends Event {
  public final ConfigCollection configCollection;
  public final String difficulty;

  public DifficultyChangedEvent(ConfigCollection configCollection, String difficulty) {
    this.configCollection = configCollection;
    this.difficulty = difficulty;
  }
}
