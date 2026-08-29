package lod.dragoonmodifier.menu;

import legend.core.MathHelper;
import legend.game.combat.Battle;
import legend.game.combat.bent.DetransformationMode;
import legend.game.combat.bent.PlayerBattleEntity;
import legend.game.combat.ui.BattleActionUseFlowControl;
import legend.game.combat.ui.BattleMenuStruct58;
import legend.game.i18n.I18n;
import legend.game.inventory.screens.FontOptions;
import legend.game.inventory.screens.HorizontalAlign;
import legend.game.inventory.screens.TextColour;
import legend.lodmod.battleactions.RetailBattleAction;

import static legend.game.Scus94491BpeSegment_8006.battleState_8006e398;
import static legend.game.Text.renderText;
import static legend.game.Text.textZ_800bdf00;
import static legend.game.combat.ui.BattleHud.ICON_SIZE;

public class DetransformAction extends RetailBattleAction {
  private static final FontOptions FONT = new FontOptions().size(0.67f).colour(TextColour.WHITE).shadowColour(TextColour.BLACK).horizontalAlign(HorizontalAlign.CENTRE);

  public DetransformAction() {
    super(2);
  }

  @Override
  public void draw(final Battle battle, final int index, final boolean selected) {
    final BattleMenuStruct58 menu = battle.hud.battleMenu_800c6c34;

    final int iconState;
    if(selected) {
      iconState = battleMenuIconStates_800c71e4[menu.iconStateIndex_26];
    } else {
      //LAB_800f6c88
      iconState = 0;
    }

    //LAB_800f6c90
    final int menuElementBaseX = menu.x_06 - menu.xShiftOffset_0a + index * 19;
    final int menuElementBaseY = menu.y_08 - 16;

    if(selected && menu.renderSelectedIconText_40) {
      menu.player_04.detransformationMode = DetransformationMode.AFTER_TURN;

      FONT.colour(menu.player_04.getElement().colour);

      final float brightness = MathHelper.brightness(menu.player_04.getElement().colour);
      if(brightness > 0.5f) {
        FONT.shadowColour(TextColour.BLACK);
      } else {
        FONT.shadowColour(TextColour.WHITE);
      }

      final String translationKey = this.getTranslationKey() ;

      menu.transforms.scaling(16.0f, 16.0f, 1.0f);
      menu.transforms.transfer.set(menuElementBaseX, menuElementBaseY, 10.0f);

      final int oldZ = textZ_800bdf00;
      textZ_800bdf00 = 124;
      renderText(I18n.translate(translationKey), menuElementBaseX + ICON_SIZE / 2.0f, menu.y_08 - 24.0f, FONT);
      textZ_800bdf00 = oldZ;
    }

    // Combat menu icons
    //LAB_800f6d70
    menu.transforms.scaling(16.0f, 16.0f, 1.0f);
    menu.transforms.transfer.set(menuElementBaseX, menuElementBaseY, 123.8f);
    menu.player_04.character.template.renderTransformIcon(menu.player_04.character, menu.player_04, menu.transforms, iconState);
  }

  @Override
  public BattleActionUseFlowControl use(final Battle battle, final PlayerBattleEntity player) {
    battleState_8006e398.dragoonTurnsRemaining_294[player.typeBentSlot_276] = 1;
    return BattleActionUseFlowControl.PAUSE_SCRIPT;
  }
}
