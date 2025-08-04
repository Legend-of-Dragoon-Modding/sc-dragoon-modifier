package lod.dragoonmodifier.screens;

import legend.game.Scus94491BpeSegment_800b;
import legend.game.combat.ui.UiBox;
import legend.game.inventory.screens.FontOptions;
import legend.game.inventory.screens.HorizontalAlign;
import legend.game.inventory.screens.MenuScreen;
import legend.game.inventory.screens.TextColour;

import static legend.game.Scus94491BpeSegment_8002.renderText;

public class DraModNotification extends MenuScreen {

  final UiBox ui;
  final int trophyType;
  final String message;
  final FontOptions font = new FontOptions().colour(TextColour.WHITE).shadowColour(TextColour.BLACK).horizontalAlign(HorizontalAlign.LEFT).size(0.5f);

  public DraModNotification(final int trophyType, final String message) {
    this.trophyType = trophyType;
    this.message = message;
    this.ui = new UiBox("DraMod Notification", 0, 232, 368, 8);
  }

  @Override
  protected void render() {
    int oldZ = Scus94491BpeSegment_800b.textZ_800bdf00;
    Scus94491BpeSegment_800b.textZ_800bdf00 = 0;
    renderText(this.message, 0, 234, this.font);
    Scus94491BpeSegment_800b.textZ_800bdf00 = oldZ;

    if(this.trophyType == 0) { //Notification
      this.ui.render(1.0f, 0, 0);
    } else if(this.trophyType == 1) { //Bronze
      this.ui.render(206f /255f, 137f / 255f, 70f / 255f);
    } else if(this.trophyType == 2) { //Silver
      this.ui.render(192f /255f, 192f / 255f, 192f / 255f);
    } else if(this.trophyType == 3) { //Gold
      this.ui.render(1.0f, 215f / 255f, 0);
    }
  }
}
