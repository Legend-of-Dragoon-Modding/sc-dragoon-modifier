package lod.dragoonmodifier.events;

import org.legendofdragoon.modloader.events.Event;

public class ShanaSwapArrowEvent extends Event {
  public final int arrowIndex;

  public ShanaSwapArrowEvent(final int arrowIndex) {
    this.arrowIndex = arrowIndex;
  }
}