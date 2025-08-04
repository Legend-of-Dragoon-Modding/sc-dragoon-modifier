package lod.dragoonmodifier.screens;

import legend.core.platform.input.InputAction;
import legend.core.platform.input.InputAxis;
import legend.core.platform.input.InputAxisDirection;
import legend.core.platform.input.InputButton;
import legend.core.platform.input.InputKey;
import legend.core.platform.input.InputMod;
import legend.game.Scus94491BpeSegment_800b;
import legend.game.combat.ui.UiBox;
import legend.game.inventory.screens.FontOptions;
import legend.game.inventory.screens.HorizontalAlign;
import legend.game.inventory.screens.InputPropagation;
import legend.game.inventory.screens.MenuScreen;
import legend.game.inventory.screens.TextColour;
import legend.game.modding.coremod.CoreMod;

import java.util.Set;

import static legend.core.GameEngine.CONFIG;
import static legend.core.GameEngine.PLATFORM;
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

  @Override
  protected InputPropagation mouseMove(final int x, final int y) {
    return InputPropagation.PROPAGATE;
  }

  @Override
  protected InputPropagation mouseClick(final int x, final int y, final int button, final Set<InputMod> mods) {
    return InputPropagation.PROPAGATE;
  }


  @Override
  protected InputPropagation keyPress(final InputKey key, final InputKey scancode, final Set<InputMod> mods, final boolean repeat) {
    return InputPropagation.PROPAGATE;
  }

  @Override
  protected InputPropagation keyRelease(final InputKey key, final InputKey scancode, final Set<InputMod> mods) {
    return InputPropagation.PROPAGATE;
  }

  @Override
  protected InputPropagation buttonPress(final InputButton button, final boolean repeat) {
    return InputPropagation.PROPAGATE;
  }

  @Override
  protected InputPropagation buttonRelease(final InputButton button) {
    return InputPropagation.PROPAGATE;
  }

  @Override
  protected InputPropagation axis(final InputAxis axis, final InputAxisDirection direction, final float menuValue, final float movementValue) {
    return InputPropagation.PROPAGATE;
  }

  @Override
  protected InputPropagation charPress(final int codepoint) {
    return InputPropagation.PROPAGATE;
  }

  @Override
  protected InputPropagation inputActionPressed(final InputAction action, final boolean repeat) {
    return InputPropagation.PROPAGATE;
  }

  @Override
  protected InputPropagation inputActionReleased(final InputAction action) {
    return InputPropagation.PROPAGATE;
  }
}
