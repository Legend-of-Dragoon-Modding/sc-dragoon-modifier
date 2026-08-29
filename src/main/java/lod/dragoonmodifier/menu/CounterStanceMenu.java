package lod.dragoonmodifier.menu;

import legend.game.characters.UnaryStatModConfig;
import legend.game.combat.bent.PlayerBattleEntity;
import legend.game.combat.ui.BattleHud;
import legend.game.combat.ui.ListMenu;
import legend.game.combat.ui.ListPosition;
import legend.game.inventory.screens.FontOptions;
import legend.game.inventory.screens.HorizontalAlign;
import legend.game.inventory.screens.TextColour;
import legend.game.scripting.RunningScript;
import legend.game.ui.UiBox;
import legend.lodmod.LodMod;
import lod.dragoonmodifier.DragoonModifier;

import static legend.core.GameEngine.CONFIG;
import static legend.game.Text.renderText;
import static legend.lodmod.LodConfig.UI_BACKGROUND_COLOUR;
import static legend.lodmod.LodMod.GUARD_HEAL_STAT;
import static legend.lodmod.LodMod.MP_STAT;

public class CounterStanceMenu extends ListMenu {
  private final FontOptions fontOptions = new FontOptions().colour(TextColour.WHITE);
  private UiBox description;
  private final PlayerBattleEntity player;

  public CounterStanceMenu(final BattleHud hud, final PlayerBattleEntity activePlayer, final ListPosition lastPosition, final Runnable onClose) {
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

    renderText("Kongol Counter Stance", x, y, this.fontOptions);
  }


  @Override
  protected boolean canUse() {
    return true;
  }

  @Override
  protected void onUse(final int index) {
    if(DragoonModifier.kongolCounterStance[this.player.typeBentSlot_276]) {
      DragoonModifier.kongolCounterStance[this.player.typeBentSlot_276] = false;
      DragoonModifier.kongolCounterStanceTurns[this.player.typeBentSlot_276] = 0;
      this.player.stats.getStat(LodMod.SPEED_STAT.get()).removeMod(DragoonModifier.COUNTER_STANCE_SLOWDOWN.getId());
    } else {
      DragoonModifier.kongolCounterStance[this.player.typeBentSlot_276] = true;
      DragoonModifier.kongolCounterStanceTurns[this.player.typeBentSlot_276] = 3;
      this.player.stats.getStat(LodMod.SPEED_STAT.get()).addMod(DragoonModifier.COUNTER_STANCE_SLOWDOWN.getId(), LodMod.UNARY_STAT_MOD_TYPE.get().make(new UnaryStatModConfig().flat((int)Math.floor(this.player.stats.getStat(LodMod.SPEED_STAT.get()).getRawWithEquipment() * (-66 / 100.0))).turns(3)));
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
    renderText(DragoonModifier.kongolCounterStance[this.player.typeBentSlot_276] ? "Activated" : "Deactivated", 160, 157, this.fontOptions);
  }
}
