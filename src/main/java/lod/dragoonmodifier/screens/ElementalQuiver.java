package lod.dragoonmodifier.screens;

import legend.core.GameEngine;
import legend.game.combat.bent.PlayerBattleEntity;
import legend.game.combat.ui.BattleHud;
import legend.game.combat.ui.ListMenu;
import legend.game.combat.ui.ListPosition;
import legend.game.inventory.screens.FontOptions;
import legend.game.inventory.screens.HorizontalAlign;
import legend.game.inventory.screens.TextColour;
import legend.game.scripting.RunningScript;
import lod.dragoonmodifier.events.ShanaGetArrowCountEvent;
import lod.dragoonmodifier.events.ShanaSwapArrowEvent;

import static legend.core.GameEngine.EVENTS;
import static legend.game.Scus94491BpeSegment_8002.renderText;

public class ElementalQuiver extends ListMenu {
  private final FontOptions fontOptions = new FontOptions().colour(TextColour.WHITE);
  private final String[] displayList = {"Fire Arrow", "Water Arrow", "Wind Arrow", "Earth Arrow", "Dark Arrow", "Light Arrow", "Thunder Arrow"};

  public ElementalQuiver(final BattleHud hud, final PlayerBattleEntity activePlayer, final ListPosition lastPosition, final Runnable onClose) {
    super(hud, activePlayer, 186, getLastPosition(lastPosition), onClose);
  }

  private static ListPosition getLastPosition(final ListPosition lastPosition) {
    lastPosition.lastListIndex_26 = 0;
    lastPosition.lastListScroll_28 = 0;
    return lastPosition;
  }

  @Override
  protected int getListCount() {
    return this.displayList.length;
  }

  @Override
  protected void drawListEntry(final int index, final int x, final int y, final int trim) {
    this.fontOptions.trim(trim);
    this.fontOptions.horizontalAlign(HorizontalAlign.LEFT);
    renderText(this.displayList[index], x, y, this.fontOptions);
    this.fontOptions.horizontalAlign(HorizontalAlign.RIGHT);
    renderText("x", x + 146, y, this.fontOptions);
    renderText(String.valueOf(this.getArrows(index)), x + 166, y, this.fontOptions);
  }

  @Override
  protected void onSelection(final int i) {

  }

  @Override
  protected void onUse(final int index) {
    GameEngine.EVENTS.postEvent(new ShanaSwapArrowEvent(index));
  }

  @Override
  protected void onClose() {

  }

  @Override
  protected int handleTargeting() {
    return 2;
  }

  @Override
  public void getTargetingInfo(final RunningScript<?> runningScript) {

  }

  private int getArrows(final int index) {
    return GameEngine.EVENTS.postEvent(new ShanaGetArrowCountEvent(index)).arrowCount;
  }

  @Override
  public void draw() {
    super.draw();
  }

  @Override
  public void delete() {
    super.delete();
  }
}
