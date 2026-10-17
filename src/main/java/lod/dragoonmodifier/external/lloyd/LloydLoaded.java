package lod.dragoonmodifier.external.lloyd;

import legend.game.characters.CharacterData2c;
import legend.game.characters.RegisterCharacterTemplatesEvent;
import legend.game.inventory.EquipmentTypes;
import legend.game.inventory.GatherCharacterEquipmentTypesEvent;
import legend.game.inventory.GoodsSource;
import legend.game.types.EquipmentSlot;
import lod.dragoonmodifier.DragoonModifier;
import lod.dragoonmodifier.character.Lloyd;
import lod.lloydmod.events.LloydJoinedPartyEvent;
import lod.lloydmod.events.LloydVersionEvent;
import lod.lloydmod.stats.LloydTemplate;
import org.legendofdragoon.modloader.events.EventListener;
import org.legendofdragoon.modloader.events.Priority;

import static legend.core.GameEngine.EVENTS;
import static legend.core.GameEngine.REGISTRIES;
import static legend.game.Scus94491BpeSegment_8005.submapCut_80052c30;
import static legend.game.Scus94491BpeSegment_800b.gameState_800babc8;
import static legend.game.characters.CharacterData2c.CAN_BE_IN_PARTY;
import static legend.game.characters.CharacterData2c.IN_PARTY;
import static lod.dragoonmodifier.DragoonModifier.isHardMode;
import static lod.dragoonmodifier.DragoonModifier.isHellMode;
import static lod.dragoonmodifier.DragoonModifier.levelUp;
import static lod.lloydmod.LloydMod.CHAOS_DIVINE_DRAGOON_SPIRIT;

public class LloydLoaded implements LloydInterface {

  private Lloyd lloyd;

  public LloydLoaded() {
    super();
  }

  @Override
  public void registerEvents() {
    EVENTS.register(this);
  }

  @Override
  public void registerTemplate(final RegisterCharacterTemplatesEvent event) {
    this.lloyd = new Lloyd();
    event.register(DragoonModifier.id("lloyd"), this.lloyd);
  }

  @EventListener(priority = Priority.HIGHEST)
  public void lloydDramodVersion(final LloydVersionEvent event) {
    try {
      if(isHardMode() || isHellMode()) {
        event.draModVersion = true;
      } else {
        event.draModVersion = false;
      }
      DragoonModifier.lloydDramod = event.draModVersion;
    } catch (final Exception ignored) {
    }
  }

  @EventListener(priority = Priority.HIGHEST)
  public void lloydJoinedParty(final LloydJoinedPartyEvent event) {
    try {
      for(int i = 0; i < gameState_800babc8.charData_32c.size(); i++) {
        if(gameState_800babc8.charData_32c.get(i).template instanceof LloydTemplate) {
          gameState_800babc8.charData_32c.remove(i);
        }
      }

      gameState_800babc8.goods_19c.give(CHAOS_DIVINE_DRAGOON_SPIRIT.get(), GoodsSource.GAMEPLAY);

      final CharacterData2c lloyd = this.lloyd.make(gameState_800babc8);
      lloyd.partyFlags_04 = IN_PARTY | CAN_BE_IN_PARTY;
      gameState_800babc8.addCharacter(lloyd);

      lloyd.equip(EquipmentSlot.WEAPON, REGISTRIES.equipment.getEntry("dragoon_modifier", "broad_sword").get());
      lloyd.equip(EquipmentSlot.HELMET, REGISTRIES.equipment.getEntry("dragoon_modifier", "bandana").get());
      lloyd.equip(EquipmentSlot.ARMOUR, REGISTRIES.equipment.getEntry("dragoon_modifier", "leather_armor").get());
      lloyd.equip(EquipmentSlot.BOOTS, REGISTRIES.equipment.getEntry("dragoon_modifier", "leather_boots").get());
      lloyd.equip(EquipmentSlot.ACCESSORY, REGISTRIES.equipment.getEntry("dragoon_modifier", "bracelet").get());

      if(submapCut_80052c30 == 441) {
        levelUp(lloyd, 27);
      }

      event.override = true;
    } catch(final Exception ignored) {
    }
  }

  @EventListener(priority = Priority.HIGHEST)
  public void lloydVersionEvent(final LloydVersionEvent event) {
    if(isHardMode() || isHellMode()) {
      event.draModVersion = true;
    } else {
      event.draModVersion = false;
    }
  }

  @EventListener
  public void gatherCharacterEquipmentTypes(final GatherCharacterEquipmentTypesEvent event) {
    event.add(this.lloyd, EquipmentTypes.DART, EquipmentTypes.LONGSWORD, EquipmentTypes.SHORTSWORD, EquipmentTypes.NEUTRAL, EquipmentTypes.MALE, EquipmentTypes.ADDITIONS, EquipmentTypes.HEAVY, EquipmentTypes.ARMOR_OF_YORE);
  }
}
