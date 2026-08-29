package lod.dragoonmodifier.equips;

import legend.game.characters.ElementSet;
import legend.game.combat.bent.BattleEntity27c;
import legend.game.inventory.Equipment;
import legend.game.inventory.ItemIcon;
import legend.game.types.EquipmentSlot;
import legend.lodmod.LodMod;

import static legend.game.Scus94491BpeSegment_8006.battleState_8006e398;

public class UltimateWargodEquipment extends Equipment {
  public UltimateWargodEquipment(final Equipment equip) {
    super(equip.price, equip.flags_00, equip.slot, equip.attackElement_04.iterator().next(), equip.elementalResistance_06, equip.elementalImmunity_07, equip.statusResist_08, equip.mpPerPhysicalHit, equip.spPerPhysicalHit, equip.mpPerMagicalHit, equip.spPerMagicalHit, equip.hpMultiplier, equip.mpMultiplier, equip.spMultiplier, equip.magicalResistance, equip.physicalResistance, equip.magicalImmunity, equip.physicalImmunity, equip.revive, equip.hpRegen, equip.mpRegen, equip.spRegen, equip.escapeBonus, equip.icon_0e, equip.speed_0f, equip.attack_10, equip.magicAttack_11, equip.defence_12, equip.magicDefence_13, equip.attackHit_14, equip.magicHit_15, equip.attackAvoid_16, equip.magicAvoid_17, equip.onHitStatusChance_18, equip.onHitStatus_1b);
  }

  @Override
  public void applyEffect(final BattleEntity27c wearer) {
    battleState_8006e398.additionExtra_474[wearer.allBentSlot_274].flag_00 |= 0x6;
  }
}