package lod.dragoonmodifier.screens;

import legend.game.combat.ui.UiBox;
import legend.game.inventory.screens.FontOptions;
import legend.game.inventory.screens.HorizontalAlign;
import legend.game.inventory.screens.MenuScreen;
import legend.game.inventory.screens.TextColour;

import static legend.game.Scus94491BpeSegment_8002.renderText;

public class DraModAchievements extends MenuScreen {

  final UiBox ui;
  final int trophyType;
  final String message;
  final boolean show;
  final FontOptions font = new FontOptions().colour(TextColour.WHITE).shadowColour(TextColour.BLACK).horizontalAlign(HorizontalAlign.LEFT).size(0.5f);

  public DraModAchievements(final int trophyType, final String message, final boolean show) {
    this.trophyType = trophyType;
    this.message = message;
    this.show = show;
    this.ui = new UiBox("DraMod Achievement", 0, 232, 368, 8);
  }

  @Override
  protected void render() {
    renderText(this.message, 0, 234, this.font);
    this.ui.render(255, 255, 0);
  }
}
