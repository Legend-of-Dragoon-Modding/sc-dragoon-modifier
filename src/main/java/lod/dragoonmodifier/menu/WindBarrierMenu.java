package lod.dragoonmodifier.menu;

import legend.game.combat.bent.PlayerBattleEntity;
import legend.game.combat.ui.BattleHud;
import legend.game.combat.ui.ListMenu;
import legend.game.combat.ui.ListPosition;
import legend.game.inventory.screens.FontOptions;
import legend.game.inventory.screens.HorizontalAlign;
import legend.game.inventory.screens.TextColour;
import legend.game.scripting.RunningScript;
import legend.game.ui.UiBox;
import lod.dragoonmodifier.DragoonModifier;

import static legend.core.GameEngine.CONFIG;
import static legend.game.Text.renderText;
import static legend.lodmod.LodConfig.UI_BACKGROUND_COLOUR;
import static legend.lodmod.LodMod.MP_STAT;

public class WindBarrierMenu extends ListMenu {
  private final FontOptions fontOptions = new FontOptions().colour(TextColour.WHITE);
  private UiBox description;
  private final PlayerBattleEntity player;

  public WindBarrierMenu(final BattleHud hud, final PlayerBattleEntity activePlayer, final ListPosition lastPosition, final Runnable onClose) {
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

    renderText("Wind Barrier", x, y, this.fontOptions);
  }


  @Override
  protected boolean canUse() {
    return true;
  }

  @Override
  protected void onUse(final int index) {
    if(DragoonModifier.windBarrier[this.player.typeBentSlot_276]) {
      DragoonModifier.windBarrier[this.player.typeBentSlot_276] = false;
      this.player.stats.getStat(MP_STAT.get()).setCurrent(this.player.stats.getStat(MP_STAT.get()).getCurrent() + 10);
    } else {
      if(this.player.stats.getStat(MP_STAT.get()).getCurrent() >= 10) {
        DragoonModifier.windBarrier[this.player.typeBentSlot_276] = true;
        this.player.stats.getStat(MP_STAT.get()).setCurrent(this.player.stats.getStat(MP_STAT.get()).getCurrent() - 10);
      }
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
    renderText(DragoonModifier.roseSiphonActivated[this.player.typeBentSlot_276] ? "Activated" : "Deactivated", 160, 157, this.fontOptions);
  }
}
