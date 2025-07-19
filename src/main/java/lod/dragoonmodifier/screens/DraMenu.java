package lod.dragoonmodifier.screens;

import legend.core.GameEngine;
import legend.core.QueuedModelStandard;
import legend.core.gte.MV;
import legend.game.inventory.screens.*;
import legend.game.inventory.screens.controls.Button;
import legend.game.types.Translucency;
import lod.dragoonmodifier.DragoonModifier;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import static legend.core.GameEngine.RENDERER;
import static legend.game.Scus94491BpeSegment_8002.playMenuSound;
import static legend.game.Scus94491BpeSegment_8002.renderText;
import static legend.game.Scus94491BpeSegment_8005.submapCut_80052c30;
import static legend.game.Scus94491BpeSegment_800b.gameState_800babc8;
import static legend.game.modding.coremod.CoreMod.INPUT_ACTION_MENU_DOWN;
import static legend.game.modding.coremod.CoreMod.INPUT_ACTION_MENU_UP;
import static lod.dragoonmodifier.DragoonModifier.DIFFICULTY;
import static lod.dragoonmodifier.DragoonModifier.draMenuMessage;

public class DraMenu extends MenuScreen {
  final List<Button> menuButtons = new ArrayList<>();
  final FontOptions textOptions;
  final MV transforms = new MV();
  int ultimateBossStage = 1;
  int ultimateBossDefeated = 1;

  public DraMenu() {
    this.textOptions = new FontOptions().horizontalAlign(HorizontalAlign.CENTRE).colour(TextColour.WHITE).shadowColour(TextColour.BLACK);
    addButton("Level Sync", DragoonModifier::levelSync);
    addButton("Remove Second Slot", DragoonModifier::removeSecondSlot);
    addButton("Remove Third Slot", DragoonModifier::removeThirdSlot);
    addButton("Add Everyone", DragoonModifier::addAllPartyMembers);

    if(gameState_800babc8.scriptFlags2_bc.get(0, 7)) {
      addButton("Swap Lavitz", DragoonModifier::addLavitz);
    }

    if(gameState_800babc8.scriptFlags2_bc.get(0, 10)) {
      addButton("Swap Shana", DragoonModifier::addShana);
    }

    if(submapCut_80052c30 == 10) {
      addButton("Swap all Dragoons", DragoonModifier::addDragoons);
      addButton("Add Lv1 Party", DragoonModifier::addLv1Characters);
    } else if(submapCut_80052c30 == 232) {
      addButton("Add Dart Dragoon", DragoonModifier::addDartDragoon);
    } else if(submapCut_80052c30 == 424 || submapCut_80052c30 == 736) {
      addButton("Dart Dragoon Swap", DragoonModifier::swapRedEyedAndDivineSpirit);
    } else if(submapCut_80052c30 >= 393 && submapCut_80052c30 <= 405) {
      addButton("Wingly Shop", DragoonModifier::forbiddenLandShop);
      if(DragoonModifier.isBitSet(8, 14)) {
        addButton("Next Ultimate Boss", DragoonModifier::nextUltimateBoss);
        addButton("Ultimate Boss", DragoonModifier::startUltimateBoss);
      }
    }

    //Black Castle Accessway
    //Warp out of Moon
    //Warp to Moon
    //Faust Battle

    if(gameState_800babc8.charData_32c[8].partyFlags_04 > 0) {
      addButton("???", DragoonModifier::who);
    }
  }

  @Override
  protected void render() {
    renderText("DraMod Menu", 184, 2, this.textOptions);
    this.transforms.transfer.set(0, 0, 0.0f);
    this.transforms.scaling(368.0f, 240.0f, 0.0f);
    renderText("Battles", 276, 45, this.textOptions);
    renderText(String.valueOf(gameState_800babc8._b4), 276, 60, this.textOptions);
    renderText("Turns Taken", 276, 80, this.textOptions);
    renderText(String.valueOf(gameState_800babc8._b8), 276, 95, this.textOptions);
    renderText("Ultimate Boss Defeated", 276, 135, this.textOptions);
    renderText(String.valueOf(this.ultimateBossDefeated), 276, 150, this.textOptions);
    renderText("Ultimate Boss Selected", 276, 170, this.textOptions);
    renderText(String.valueOf(this.ultimateBossStage), 276, 185, this.textOptions);
    renderText(draMenuMessage, 184, 226, this.textOptions);

    final MV draMenuTransforms = new MV();
    draMenuTransforms.transfer.set(0, 0, 999.0f);
    draMenuTransforms.scaling(368.0f, 240.0f, 999.0f);

    RENDERER
      .queueOrthoModel(RENDERER.opaqueQuad, draMenuTransforms, QueuedModelStandard.class)
      .monochrome(0.0f)
      .translucency(Translucency.HALF_B_PLUS_HALF_F);
  }

  private void addButton(final String text, final Runnable onClick) {
    final int index = this.menuButtons.size();

    final Button button = this.addControl(new Button(text));
    button.setPos(0, 30 + index * 15);
    button.setWidth(160);

    button.onHoverIn(() -> {
      playMenuSound(1);
      this.setFocus(button);
    });
    button.setTextColour(TextColour.GREY);
    button.onLostFocus(() -> {
      button.setTextColour(TextColour.GREY);
      button.setShadowColour(TextColour.BLACK);
    });
    button.onGotFocus(() -> {
      button.setTextColour(TextColour.WHITE);
    });

    button.onPressed(onClick::run);

    button.onInputActionPressed((action, repeat) -> {
      if(action == INPUT_ACTION_MENU_DOWN.get()) {
        for(int i = 1; i < this.menuButtons.size(); i++) {
          final Button otherButton = this.menuButtons.get(Math.floorMod(index + i, this.menuButtons.size()));

          if(!otherButton.isDisabled() && otherButton.isVisible()) {
            playMenuSound(1);
            this.setFocus(otherButton);
            break;
          }
        }
      } else if(action == INPUT_ACTION_MENU_UP.get()) {
        for(int i = 1; i < this.menuButtons.size(); i++) {
          final Button otherButton = this.menuButtons.get(Math.floorMod(index - i, this.menuButtons.size()));

          if(!otherButton.isDisabled() && otherButton.isVisible()) {
            playMenuSound(1);
            this.setFocus(otherButton);
            break;
          }
        }
      }

      return InputPropagation.HANDLED;
    });

    this.menuButtons.add(button);
  }

  private void showScreen(final Function<Runnable, MenuScreen> screen) {
    this.getStack().pushScreen(screen.apply(() -> {
      this.getStack().popScreen();
    }));
  }

  public void setUltimateBossStage(final int stage) {
    this.ultimateBossStage = stage;
  }

  public void setUltimateBossDefeated(final int defeated) {
    this.ultimateBossDefeated = defeated;
  }
}
