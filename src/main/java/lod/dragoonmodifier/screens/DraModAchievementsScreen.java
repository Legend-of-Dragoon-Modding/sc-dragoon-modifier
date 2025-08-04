package lod.dragoonmodifier.screens;

import legend.core.QueuedModelStandard;
import legend.core.gte.MV;
import legend.core.platform.input.InputAction;
import legend.game.inventory.screens.FontOptions;
import legend.game.inventory.screens.HorizontalAlign;
import legend.game.inventory.screens.InputPropagation;
import legend.game.inventory.screens.MenuScreen;
import legend.game.inventory.screens.TextColour;
import legend.game.types.Translucency;
import lod.dragoonmodifier.DragoonModifier;
import lod.dragoonmodifier.saves.DraModAchievements;

import static legend.core.GameEngine.CONFIG;
import static legend.core.GameEngine.RENDERER;
import static legend.game.Scus94491BpeSegment_8002.renderText;
import static legend.game.modding.coremod.CoreMod.INPUT_ACTION_MENU_BACK;
import static legend.game.modding.coremod.CoreMod.INPUT_ACTION_MENU_DELETE;
import static legend.game.modding.coremod.CoreMod.INPUT_ACTION_MENU_DOWN;
import static legend.game.modding.coremod.CoreMod.INPUT_ACTION_MENU_SORT;
import static legend.game.modding.coremod.CoreMod.INPUT_ACTION_MENU_UP;
import static lod.dragoonmodifier.DragoonModifier.SHOW_ACHIEVEMENTS;

public class DraModAchievementsScreen extends MenuScreen {

  final boolean enhanced;
  final FontOptions textOptions;
  final FontOptions notAchievedText;
  final FontOptions bronzeText;
  final FontOptions silverText;
  final FontOptions goldText;

  int bronze = 0;
  int bronzeMax = 0;
  int silver = 0;
  int silverMax = 0;
  int gold = 0;
  int goldMax = 0;

  int currentAchievement = 0;

  String[] name;
  String[] desc;

  public DraModAchievementsScreen(final boolean enhanced) {
    this.enhanced = enhanced;
    this.textOptions = new FontOptions().horizontalAlign(HorizontalAlign.LEFT).colour(TextColour.WHITE).shadowColour(TextColour.PURPLE);
    this.notAchievedText = new FontOptions().horizontalAlign(HorizontalAlign.LEFT).colour(TextColour.WHITE).shadowColour(TextColour.PURPLE).size(0.5f);;
    this.bronzeText = new FontOptions().horizontalAlign(HorizontalAlign.LEFT).colour(TextColour.WHITE).shadowColour(TextColour.BROWN).size(0.5f);;
    this.silverText = new FontOptions().horizontalAlign(HorizontalAlign.LEFT).colour(TextColour.WHITE).shadowColour(TextColour.GREY).size(0.5f);;
    this.goldText = new FontOptions().horizontalAlign(HorizontalAlign.LEFT).colour(TextColour.WHITE).shadowColour(TextColour.YELLOW).size(0.5f);;

    if(enhanced) {
      this.name = new String[DraModAchievements.Enhanced.values().length];
      this.desc = new String[DraModAchievements.Enhanced.values().length];

      for(int i = 0; i < DraModAchievements.Enhanced.values().length - 1; i++) {
        final DraModAchievements.Enhanced achievement = DraModAchievements.Enhanced.getById(i);
        if(achievement.getType() == DraModAchievements.TrophyType.BRONZE) {
          this.bronzeMax++;
          if(DragoonModifier.draModSave.enhancedAchievements.containsKey(i)) {
            this.bronze++;
          }
        } else if(achievement.getType() == DraModAchievements.TrophyType.SILVER) {
          this.silverMax++;
          if(DragoonModifier.draModSave.enhancedAchievements.containsKey(i)) {
            this.silver++;
          }
        } else if(achievement.getType() == DraModAchievements.TrophyType.GOLD) {
          this.goldMax++;
          if(DragoonModifier.draModSave.enhancedAchievements.containsKey(i)) {
            this.gold++;
          }
        }

        if(achievement.isHidden() && !DragoonModifier.draModSave.enhancedAchievements.containsKey(i) && !CONFIG.getConfig(SHOW_ACHIEVEMENTS.get())) {
          this.name[i] = "???";
          this.desc[i] = "???";
        } else {
          this.name[i] = achievement.getName();
          this.desc[i] = achievement.getDesc();
        }
      }
    }
  }

  @Override
  protected void render() {

    final MV draMenuTransforms = new MV();
    draMenuTransforms.transfer.set(0, 0, 60.0f);
    draMenuTransforms.scaling(368.0f, 240.0f, 999.0f);

    RENDERER
      .queueOrthoModel(RENDERER.opaqueQuad, draMenuTransforms, QueuedModelStandard.class)
      .monochrome(0.0f);


    renderText("DraMod Achievements", 112, 2, this.textOptions);
    renderText("Bronze: " + this.bronze + "/" + this.bronzeMax + " - Silver: " + this.silver + "/" + this.silverMax + " - Gold: " + this.gold + "/" + this.goldMax, 4, 18, this.textOptions);


    int index = 0;
    for(int i = this.currentAchievement; i < this.currentAchievement + 10; i++) {
      if(!DragoonModifier.draModSave.enhancedAchievements.containsKey(i)) {
        renderText(name[i], 4, 40 + ((index * 2) * 10), this.notAchievedText);
        renderText(desc[i], 4, 50 + ((index * 2) * 10), this.notAchievedText);
      } else {
        final DraModAchievements.Enhanced achievement = DraModAchievements.Enhanced.getById(this.currentAchievement + i);
        if(achievement.getType() == DraModAchievements.TrophyType.BRONZE) {
          renderText(name[i] + "[X]", 4, 40 + ((index * 2) * 10), this.bronzeText);
          renderText(desc[i], 4, 50 + ((index * 2) * 10), this.bronzeText);
        } else if(achievement.getType() == DraModAchievements.TrophyType.SILVER) {
          renderText(name[i] + "[X]", 4, 40 + ((index * 2) * 10), this.silverText);
          renderText(desc[i], 4, 50 + ((index * 2)  * 10), this.silverText);
        } else if(achievement.getType() == DraModAchievements.TrophyType.GOLD) {
          renderText(name[i] + "[X]", 4, 40 + ((index * 2) * 10), this.goldText);
          renderText(desc[i], 4, 50 + ((index * 2) * 10), this.goldText);
        }
      }

      draMenuTransforms.transfer.set(0, 57 + ((index * 2) * 10), 1.0f);
      draMenuTransforms.scaling(368.0f, 1.0f, 999.0f);

      RENDERER
        .queueOrthoModel(RENDERER.opaqueQuad, draMenuTransforms, QueuedModelStandard.class)
        .monochrome(1.0f);

      index++;
    }
  }

  @Override
  protected InputPropagation inputActionPressed(final InputAction action, final boolean repeat) {
    if(super.inputActionPressed(action, repeat) == InputPropagation.HANDLED) {
      return InputPropagation.HANDLED;
    }

    if(action == INPUT_ACTION_MENU_UP.get()) {
      this.currentAchievement -= 1;
      if(this.currentAchievement < 0) {
        this.currentAchievement = 0;
      }
      return InputPropagation.HANDLED;
    }

    if(action == INPUT_ACTION_MENU_DOWN.get()) {
      this.currentAchievement += 1;
      if(this.currentAchievement + 11 > DraModAchievements.Enhanced.values().length - 1) {
        this.currentAchievement = DraModAchievements.Enhanced.values().length - 11;
      }
      return InputPropagation.HANDLED;
    }

    return InputPropagation.PROPAGATE;
  }
}
