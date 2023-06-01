package lod.dragoonmodifier;

import legend.game.modding.events.Event;

public class DifficultyChangedEvent extends Event {
  public final String difficulty;

  public DifficultyChangedEvent(String difficulty) {
    this.difficulty = difficulty;
  }
}
