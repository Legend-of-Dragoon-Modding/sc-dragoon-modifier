package lod.dragoonmodifier.items;

import legend.core.memory.Method;
import legend.game.inventory.Item;
import legend.game.inventory.ItemIcon;
import legend.game.inventory.ItemStack;
import legend.game.inventory.UseItemResponse;

import static legend.core.GameEngine.CONFIG;
import static legend.game.SItem.addHp;
import static legend.game.SItem.addMp;
import static legend.game.Scus94491BpeSegment_800b.gameState_800babc8;
import static legend.lodmod.LodConfig.ITEM_STACK_SIZE;

public class RockFirefliesItem extends Item {
  public RockFirefliesItem() {
    super(ItemIcon.SACK, 10);
  }

  @Override
  public int getMaxStackSize(final ItemStack stack) {
    return CONFIG.getConfig(ITEM_STACK_SIZE.get());
  }

  @Override
  public boolean canBeUsed(final ItemStack stack, final UsageLocation location) {
    return location == UsageLocation.MENU;
  }

  @Override
  public boolean canTarget(ItemStack stack, TargetType type) {
    return true;
  }

  @Override
  @Method(0x80022d88L)
  public void useInMenu(final ItemStack stack, final UseItemResponse response, final int charId) {
    addHp(charId, -1);
    addMp(charId, -1);
    gameState_800babc8.charData_32c.get(charId).status_10 = 0;
    response.success();
  }
}