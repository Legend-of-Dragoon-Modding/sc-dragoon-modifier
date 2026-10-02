package lod.dragoonmodifier.screens;

import legend.core.lang.RawText;
import legend.core.platform.input.InputAction;
import legend.game.characters.CharacterData2c;
import legend.game.inventory.GoodsSource;
import legend.game.inventory.screens.InputPropagation;
import legend.game.inventory.screens.VerticalLayoutScreen;
import legend.game.inventory.screens.controls.Background;
import legend.game.inventory.screens.controls.Button;
import legend.game.inventory.screens.controls.Label;
import legend.game.modding.coremod.CoreMod;
import legend.game.types.EquipmentSlot;
import legend.game.ui.GameOverlay;
import legend.lodmod.LodGoods;
import org.jetbrains.annotations.NotNull;

import static legend.core.GameEngine.REGISTRIES;
import static legend.game.FullScreenEffects.startFadeEffect;
import static legend.game.Menus.deallocateRenderables;
import static legend.game.Scus94491BpeSegment_800b.battleLoaded_800bc94c;
import static legend.game.Scus94491BpeSegment_800b.gameState_800babc8;
import static legend.game.characters.CharacterData2c.CAN_BE_IN_PARTY;
import static legend.game.characters.CharacterData2c.IN_PARTY;
import static legend.game.sound.Audio.playMenuSound;
import static lod.dragoonmodifier.DragoonModifier.ALBERT;
import static lod.dragoonmodifier.DragoonModifier.DART;
import static lod.dragoonmodifier.DragoonModifier.HASCHEL;
import static lod.dragoonmodifier.DragoonModifier.KONGOL;
import static lod.dragoonmodifier.DragoonModifier.LAVITZ;
import static lod.dragoonmodifier.DragoonModifier.MERU;
import static lod.dragoonmodifier.DragoonModifier.MIRANDA;
import static lod.dragoonmodifier.DragoonModifier.MOD_ID;
import static lod.dragoonmodifier.DragoonModifier.ROSE;
import static lod.dragoonmodifier.DragoonModifier.SHANA;

public class DraMenu extends VerticalLayoutScreen {
  private final Runnable unload;

  public DraMenu(final Runnable unload) {
    this.unload = unload;
    this.addControl(new Background());

    deallocateRenderables(0xff);
    startFadeEffect(2, 10);

    final Button levelSync = new Button(new RawText("Sync"));
    final Button addAllCharacters = new Button(new RawText("Add"));
    this.addRow(new RawText("Level Sync Party"), levelSync);
    this.addRow(new RawText("Add All Characters"), addAllCharacters);

    levelSync.onPressed(() -> {
      int highestInPartyEXP = 0;
      for(int i = 0; i < gameState_800babc8.charData_32c.size(); i++) {
        final CharacterData2c currentChar = gameState_800babc8.charData_32c.get(i);
        if(currentChar.partyFlags_04 > 0 && currentChar.xp_00 > highestInPartyEXP) {
          highestInPartyEXP = currentChar.xp_00;
        }
      }


      for(int i = 0; i < gameState_800babc8.charData_32c.size(); i++) {
        final CharacterData2c currentChar = gameState_800babc8.charData_32c.get(i);
        if(currentChar.partyFlags_04 > 0) {
          while(highestInPartyEXP > currentChar.getXpToNextLevel()) {
            currentChar.applyLevelUp(null);
          }
        }
      }

      GameOverlay.addNotification(3, (new RawText("Levels synced to " + highestInPartyEXP + " EXP.")));
    });

    addAllCharacters.onPressed(() -> {
      for(int i = 0; i < gameState_800babc8.charData_32c.size(); i++) {
        final CharacterData2c currentChar = gameState_800babc8.charData_32c.get(i);
        currentChar.partyFlags_04 = (IN_PARTY) | (CAN_BE_IN_PARTY);
      }

      GameOverlay.addNotification(3, (new RawText("All characters in party.")));
    });

    if(!gameState_800babc8.scriptFlags2_bc.get(20, 0)) {
      final Button level1Start = new Button(new RawText("Set Level"));
      final Button allDragoons = new Button(new RawText("Give Dragoons"));
      this.addRow(new RawText("All Chars Level 1"), level1Start);
      this.addRow(new RawText("All Dragoons"), allDragoons);
      
      level1Start.onPressed(() -> {
        final CharacterData2c dart = gameState_800babc8.addCharacter(DART.get().make(gameState_800babc8));
        final CharacterData2c lavitz = gameState_800babc8.addCharacter(LAVITZ.get().make(gameState_800babc8));
        final CharacterData2c shana = gameState_800babc8.addCharacter(SHANA.get().make(gameState_800babc8));
        final CharacterData2c rose = gameState_800babc8.addCharacter(ROSE.get().make(gameState_800babc8));
        final CharacterData2c haschel = gameState_800babc8.addCharacter(HASCHEL.get().make(gameState_800babc8));
        final CharacterData2c albert = gameState_800babc8.addCharacter(ALBERT.get().make(gameState_800babc8));
        final CharacterData2c meru = gameState_800babc8.addCharacter(MERU.get().make(gameState_800babc8));
        final CharacterData2c kongol = gameState_800babc8.addCharacter(KONGOL.get().make(gameState_800babc8));
        final CharacterData2c miranda = gameState_800babc8.addCharacter(MIRANDA.get().make(gameState_800babc8));

        gameState_800babc8.charData_32c.clear();
        gameState_800babc8.charData_32c.add(dart);
        gameState_800babc8.charData_32c.add(lavitz);
        gameState_800babc8.charData_32c.add(shana);
        gameState_800babc8.charData_32c.add(rose);
        gameState_800babc8.charData_32c.add(haschel);
        gameState_800babc8.charData_32c.add(albert);
        gameState_800babc8.charData_32c.add(meru);
        gameState_800babc8.charData_32c.add(kongol);
        gameState_800babc8.charData_32c.add(miranda);

        for(int i = 0; i < gameState_800babc8.charData_32c.size(); i++) {
          final CharacterData2c currentChar = gameState_800babc8.charData_32c.get(i);

          currentChar.level_12 = 1;
          currentChar.dlevel_13 = 1;
          currentChar.xp_00 = 0;
          currentChar.partyFlags_04 = 0x3;
          currentChar.equip(EquipmentSlot.WEAPON, REGISTRIES.equipment.getEntry(MOD_ID, "broad_sword").get());
          currentChar.equip(EquipmentSlot.HELMET, REGISTRIES.equipment.getEntry(MOD_ID, "bandana").get());
          currentChar.equip(EquipmentSlot.ARMOUR, REGISTRIES.equipment.getEntry(MOD_ID, "leather_armor").get());
          currentChar.equip(EquipmentSlot.BOOTS, REGISTRIES.equipment.getEntry(MOD_ID, "leather_boots").get());
          currentChar.equip(EquipmentSlot.ACCESSORY, REGISTRIES.equipment.getEntry(MOD_ID, "bracelet").get());
        }

        GameOverlay.addNotification(3, (new RawText("All characters set to level 1 in party.")));
      });

      allDragoons.onPressed(() -> {
        gameState_800babc8.goods_19c.give(LodGoods.RED_DRAGOON_SPIRIT, GoodsSource.DEBUGGER);
        gameState_800babc8.goods_19c.give(LodGoods.JADE_DRAGOON_SPIRIT, GoodsSource.DEBUGGER);
        gameState_800babc8.goods_19c.give(LodGoods.SILVER_DRAGOON_SPIRIT, GoodsSource.DEBUGGER);
        gameState_800babc8.goods_19c.give(LodGoods.DARK_DRAGOON_SPIRIT, GoodsSource.DEBUGGER);
        gameState_800babc8.goods_19c.give(LodGoods.VIOLET_DRAGOON_SPIRIT, GoodsSource.DEBUGGER);
        gameState_800babc8.goods_19c.give(LodGoods.BLUE_DRAGOON_SPIRIT, GoodsSource.DEBUGGER);
        gameState_800babc8.goods_19c.give(LodGoods.GOLD_DRAGOON_SPIRIT, GoodsSource.DEBUGGER);

        GameOverlay.addNotification(3, (new RawText("Dragoons added.")));
      });
    }
  }

  @Override
  protected InputPropagation inputActionPressed(@NotNull final InputAction action, final boolean repeat) {
    if(super.inputActionPressed(action, repeat) == InputPropagation.HANDLED) {
      return InputPropagation.HANDLED;
    }

    if(action == CoreMod.INPUT_ACTION_MENU_BACK.get()) {
      playMenuSound(3);
      this.unload.run();
      return InputPropagation.HANDLED;
    }

    return InputPropagation.PROPAGATE;
  }

  @Override
  protected float getSizeScale() {
    if(battleLoaded_800bc94c) {
      return this.getWidth() / 320.0f;
    } else {
      return super.getWidth() / 368.0f;
    }
  }

  @Override
  public int getWidth() {
    if(battleLoaded_800bc94c) {
      return 320;
    } else {
      return super.getWidth();
    }
  }
}
