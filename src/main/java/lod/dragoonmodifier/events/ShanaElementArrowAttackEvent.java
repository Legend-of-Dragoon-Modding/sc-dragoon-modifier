package lod.dragoonmodifier.events;

import org.legendofdragoon.modloader.events.Event;

public class ShanaElementArrowAttackEvent extends Event {
  public String attackEquip;
  public String attackItem;
  public int deffIndex;

  public ShanaElementArrowAttackEvent(final String attackEquip, final String attackItem, final int deffIndex) {
    this.attackEquip = attackEquip;
    this.attackItem = attackItem;
    this.deffIndex = deffIndex;
  }
}
