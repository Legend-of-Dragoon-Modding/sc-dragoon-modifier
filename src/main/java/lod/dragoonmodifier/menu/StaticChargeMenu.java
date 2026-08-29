package lod.dragoonmodifier.menu;

import legend.game.combat.bent.BattleEntity27c;
import legend.game.combat.bent.MonsterBattleEntity;
import legend.game.combat.bent.PlayerBattleEntity;
import legend.game.combat.ui.BattleHud;
import legend.game.combat.ui.ListMenu;
import legend.game.combat.ui.ListPosition;
import legend.game.inventory.screens.FontOptions;
import legend.game.inventory.screens.HorizontalAlign;
import legend.game.inventory.screens.TextColour;
import legend.game.scripting.RunningScript;
import legend.game.scripting.ScriptState;
import legend.game.ui.UiBox;
import lod.dragoonmodifier.DragoonModifier;

import static legend.core.GameEngine.CONFIG;
import static legend.game.Scus94491BpeSegment_8006.battleState_8006e398;
import static legend.game.Text.renderText;
import static legend.lodmod.LodConfig.UI_BACKGROUND_COLOUR;

public class StaticChargeMenu extends ListMenu {
  private final FontOptions fontOptions = new FontOptions().colour(TextColour.WHITE);
  private UiBox description;
  private final PlayerBattleEntity player;

  public StaticChargeMenu(final BattleHud hud, final PlayerBattleEntity activePlayer, final ListPosition lastPosition, final Runnable onClose) {
    super(hud, activePlayer, 186, modifyLastPosition(lastPosition), onClose);
    this.player = activePlayer;
  }

  private static ListPosition modifyLastPosition(final ListPosition lastPosition) {
    lastPosition.lastListIndex_26 = 0;
    lastPosition.lastListScroll_28 = 0;
    return lastPosition;
  }

  @Override
  protected int getListCount() {
    return 1;
  }

  @Override
  protected void drawListEntry(final int index, final int x, final int y, final int trim) {
    final TextColour textColour = TextColour.WHITE;

    this.fontOptions.trim(trim);
    this.fontOptions.horizontalAlign(HorizontalAlign.LEFT);
    this.fontOptions.colour(textColour);

    renderText("Static Charge: " + DragoonModifier.staticCharge[this.player.typeBentSlot_276], x, y, this.fontOptions);
  }


  @Override
  protected boolean canUse() {
    return true;
  }

  @Override
  protected void onUse(final int index) {
    if(DragoonModifier.staticCharge[this.player.typeBentSlot_276] == 0) {
      int allStaticCharges = 0;
      for(int i = 0; i < battleState_8006e398.getAllBentCount(); i++) {
        final ScriptState<? extends BattleEntity27c> state = battleState_8006e398.allBents_e0c.get(i);
        final BattleEntity27c bent = state.innerStruct_00;
        if(bent instanceof final MonsterBattleEntity monster) {
          if(DragoonModifier.thunderCharge[monster.typeBentSlot_276] > 0) {
            allStaticCharges += DragoonModifier.thunderCharge[monster.typeBentSlot_276];
            DragoonModifier.thunderCharge[monster.typeBentSlot_276] = 0;
          }
        }
      }

      if(allStaticCharges > 20) {
        allStaticCharges = 20;
      }

      DragoonModifier.staticCharge[this.player.typeBentSlot_276] = allStaticCharges;
    }

    this.flags_02 &= ~0x8;
    this.menuState_00 = 8;
  }

  @Override
  protected void onSelection(final int index) {
  }

  @Override
  protected void onClose() {
  }

  @Override
  protected int handleTargeting() {
    return 2;
  }

  @Override
  public void getTargetingInfo(final RunningScript<?> script) {
  }

  @Override
  public void draw() {
    super.draw();

    if(this.description == null) {
      this.description = new UiBox(44, 156, 232, 14);
    }

    this.fontOptions.trim(0);
    this.fontOptions.horizontalAlign(HorizontalAlign.CENTRE);
    this.description.render(CONFIG.getConfig(UI_BACKGROUND_COLOUR.get()));
    renderText(DragoonModifier.staticCharge[this.player.typeBentSlot_276] > 0 ? "Activated" : "Deactivated", 160, 157, this.fontOptions);
  }
}
