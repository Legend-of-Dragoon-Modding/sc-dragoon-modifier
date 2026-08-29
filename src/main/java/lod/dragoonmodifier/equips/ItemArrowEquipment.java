package lod.dragoonmodifier.equips;

import legend.game.characters.ElementSet;
import legend.game.combat.Battle;
import legend.game.combat.bent.PlayerBattleEntity;
import legend.game.combat.effects.ScriptDeffManualLoadingEffect;
import legend.game.inventory.Equipment;
import legend.game.inventory.EquipmentAttackType;
import legend.game.inventory.Item;
import legend.game.inventory.ItemIcon;
import legend.game.inventory.ItemStack;
import legend.game.scripting.ScriptState;
import legend.game.types.EquipmentSlot;
import legend.lodmod.LodItems;
import legend.lodmod.LodMod;
import lod.dragoonmodifier.events.ShanaElementArrowAttackEvent;

import static legend.core.GameEngine.EVENTS;
import static legend.core.GameEngine.REGISTRIES;
import static legend.game.EngineStates.currentEngineState_8004dd04;
import static legend.game.Scus94491BpeSegment_8006.battleState_8006e398;

public class ItemArrowEquipment extends Equipment {
  final String attackItem;
  final int deffIndex;

  public ItemArrowEquipment(final Equipment equip, final String item, final int deffIndex) {
    super(equip.price, equip.flags_00, equip.slot, equip.attackElement_04.iterator().next(), equip.elementalResistance_06, equip.elementalImmunity_07, equip.statusResist_08, equip.mpPerPhysicalHit, equip.spPerPhysicalHit, equip.mpPerMagicalHit, equip.spPerMagicalHit, equip.hpMultiplier, equip.mpMultiplier, equip.spMultiplier, equip.magicalResistance, equip.physicalResistance, equip.magicalImmunity, equip.physicalImmunity, equip.revive, equip.hpRegen, equip.mpRegen, equip.spRegen, equip.escapeBonus, equip.icon_0e, equip.speed_0f, equip.attack_10, equip.magicAttack_11, equip.defence_12, equip.magicDefence_13, equip.attackHit_14, equip.magicHit_15, equip.attackAvoid_16, equip.magicAvoid_17, equip.onHitStatusChance_18, equip.onHitStatus_1b);
    this.attackItem = item;
    this.deffIndex = deffIndex;
  }

  @Override
  public void prepareAttack(final ScriptState<PlayerBattleEntity> player) {
    final ShanaElementArrowAttackEvent event = EVENTS.postEvent(new ShanaElementArrowAttackEvent(this.getRegistryId().toString(), this.attackItem, this.deffIndex));
    battleState_8006e398._560 = 0;
    battleState_8006e398._564 = 0;

    player.innerStruct_00.item_d4 = new ItemStack(REGISTRIES.items.getEntry(event.attackItem).get());
    player.innerStruct_00.battle.loadSpellItemDeff(player, event.deffIndex, 0, player.index, player.innerStruct_00.item_d4.canTarget(Item.TargetType.ALL) ? -1 : ((Battle)currentEngineState_8004dd04).hud.battleMenu_800c6c34.target_48, 0, new ScriptDeffManualLoadingEffect());
  }

  @Override
  public EquipmentAttackType attack(final ScriptState<PlayerBattleEntity> player) {
    return EquipmentAttackType.DEFF;
  }
}
