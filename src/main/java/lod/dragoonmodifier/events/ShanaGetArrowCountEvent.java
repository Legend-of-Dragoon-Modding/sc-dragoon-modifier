package lod.dragoonmodifier.events;

import org.legendofdragoon.modloader.events.Event;

public class ShanaGetArrowCountEvent extends Event {
  public final int arrowIndex;
  public int arrowCount = 0;

  public ShanaGetArrowCountEvent(final int arrowIndex) {
    this.arrowIndex = arrowIndex;
  }
}
