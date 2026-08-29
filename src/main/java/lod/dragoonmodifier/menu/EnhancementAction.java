package lod.dragoonmodifier.menu;

import legend.core.gpu.Bpp;
import legend.core.renderer.QuadBuilder;
import legend.core.renderer.QueuedModelStandard;
import legend.core.renderer.Texture;
import legend.core.renderer.Translucency;
import legend.game.combat.Battle;
import legend.game.combat.bent.PlayerBattleEntity;
import legend.game.combat.ui.BattleActionTickFlowControl;
import legend.game.combat.ui.BattleActionUseFlowControl;
import legend.game.combat.ui.BattleMenuStruct58;
import legend.game.combat.ui.ListPosition;
import legend.game.i18n.I18n;
import legend.game.inventory.screens.FontOptions;
import legend.game.inventory.screens.HorizontalAlign;
import legend.game.inventory.screens.TextColour;
import legend.lodmod.battleactions.RetailBattleAction;
import lod.dragoonmodifier.DragoonModifier;
import lod.dragoonmodifier.character.Albert;
import lod.dragoonmodifier.character.Dart;
import lod.dragoonmodifier.character.Haschel;
import lod.dragoonmodifier.character.Kongol;
import lod.dragoonmodifier.character.Lavitz;
import lod.dragoonmodifier.character.Meru;
import lod.dragoonmodifier.character.Miranda;
import lod.dragoonmodifier.character.Rose;
import lod.dragoonmodifier.character.Shana;
import org.legendofdragoon.modloader.registries.RegistryId;

import java.nio.file.Path;

import static legend.core.GameEngine.RENDERER;
import static legend.game.Text.renderText;
import static legend.game.Text.textZ_800bdf00;
import static legend.game.combat.ui.BattleHud.ICON_SIZE;

public class EnhancementAction extends RetailBattleAction {
  private static final FontOptions FONT = new FontOptions().size(0.67f).colour(TextColour.WHITE).shadowColour(TextColour.BLACK).horizontalAlign(HorizontalAlign.CENTRE);
  private Battle battle;
  private RegistryId character;

  public EnhancementAction() {
    super(0);
    this.initTexture();
  }

  public EnhancementAction(final RegistryId character) {
    super(0);
    this.character = character;
    this.initTexture();
  }

  public void initTexture() {
    if(DragoonModifier.ENHANCEMENT_OBJ == null) {
      DragoonModifier.ENHANCEMENT_OBJ = new QuadBuilder("Enhancement Icon")
        .bpp(Bpp.BITS_24)
        .posSize(16.0f, 16.0f)
        .uvSize(1.0f, 1.0f)
        .build();
      DragoonModifier.ENHANCEMENT_OBJ.persistent = true;
    }

    if(DragoonModifier.ENHANCEMENT_TEXTURE == null) {
      DragoonModifier.ENHANCEMENT_TEXTURE = Texture.png("enhancement_action", Path.of("mods", "dragoon_modifier", "gfx", "enhancement.png"));
      DragoonModifier.ENHANCEMENT_TEXTURE.persistent = true;
    }

    if(DragoonModifier.ELEMENTAL_OBJ == null) {
      DragoonModifier.ELEMENTAL_OBJ = new QuadBuilder("Elemental Icon")
        .bpp(Bpp.BITS_24)
        .posSize(16.0f, 16.0f)
        .uvSize(1.0f, 1.0f)
        .build();
      DragoonModifier.ELEMENTAL_OBJ.persistent = true;

      DragoonModifier.ELEMENTAL_ICON_TEXTURE.put("dragoon_modifier:fire_arrow", Texture.png("element_icon_fire_arrow", Path.of("gfx", "ui", "fire.png")));
      DragoonModifier.ELEMENTAL_ICON_TEXTURE.put("dragoon_modifier:water_arrow", Texture.png("element_icon_water_arrow",Path.of("gfx", "ui", "water.png")));
      DragoonModifier.ELEMENTAL_ICON_TEXTURE.put("dragoon_modifier:wind_arrow", Texture.png("element_icon_wind_arrow",Path.of("gfx", "ui", "wind.png")));
      DragoonModifier.ELEMENTAL_ICON_TEXTURE.put("dragoon_modifier:earth_arrow", Texture.png("element_icon_earth_arrow",Path.of("gfx", "ui", "earth.png")));
      DragoonModifier.ELEMENTAL_ICON_TEXTURE.put("dragoon_modifier:dark_arrow", Texture.png("element_icon_dark_arrow",Path.of("gfx", "ui", "dark.png")));
      DragoonModifier.ELEMENTAL_ICON_TEXTURE.put("dragoon_modifier:light_arrow", Texture.png("element_icon_light_arrow",Path.of("gfx", "ui", "light.png")));
      DragoonModifier.ELEMENTAL_ICON_TEXTURE.put("dragoon_modifier:thunder_arrow", Texture.png("element_icon_thunder_arrow",Path.of("gfx", "ui", "thunder.png")));

      for(final Texture texture : DragoonModifier.ELEMENTAL_ICON_TEXTURE.values()) {
        texture.persistent = true;
      }
    }
  }

  @Override
  public BattleActionUseFlowControl use(final Battle battle, final PlayerBattleEntity player) {
    this.battle = battle;
    final ListPosition lastPosition = new ListPosition();
    lastPosition.lastListIndex_26 = 0;
    lastPosition.lastListScroll_28 = 0;

    if(player.character.template instanceof Dart) {
      battle.hud.listMenu_800c6b60 = new BurnStacksMenu(battle.hud, player, lastPosition, this::onListClose);
    } else if(player.character.template instanceof Lavitz || player.character.template instanceof Albert) {
      battle.hud.listMenu_800c6b60 = new WindBarrierMenu(battle.hud, player, lastPosition, this::onListClose);
    } else if(player.character.template instanceof Shana || player.character.template instanceof Miranda) {
      battle.hud.listMenu_800c6b60 = new ElementalQuiver(battle.hud, player, lastPosition, this::onListClose);
    } else if(player.character.template instanceof Rose) {
      battle.hud.listMenu_800c6b60 = new SiphonMenu(battle.hud, player, lastPosition, this::onListClose);
    } else if(player.character.template instanceof Haschel) {
      battle.hud.listMenu_800c6b60 = new StaticChargeMenu(battle.hud, player, lastPosition, this::onListClose);
    } else if(player.character.template instanceof Meru) {
      battle.hud.listMenu_800c6b60 = new WinglyMagicMenu(battle.hud, player, lastPosition, this::onListClose);
    } else if(player.character.template instanceof Kongol) {
      battle.hud.listMenu_800c6b60 = new CounterStanceMenu(battle.hud, player, lastPosition, this::onListClose);
    }

    return BattleActionUseFlowControl.PAUSE_SCRIPT;
  }

  @Override
  public void draw(final Battle battle, final int index, final boolean selected) {
    final BattleMenuStruct58 menu = battle.hud.battleMenu_800c6c34;
    final int menuElementBaseX = menu.x_06 - menu.xShiftOffset_0a + index * 19;
    final int menuElementBaseY = menu.y_08 - 16;

    menu.transforms.identity();
    menu.transforms.transfer.set(menuElementBaseX, menuElementBaseY, 123.8f);

    RENDERER
      .queueOrthoModel(DragoonModifier.ENHANCEMENT_OBJ, menu.transforms, QueuedModelStandard.class)
      .translucency(Translucency.HALF_B_PLUS_HALF_F)
      .alpha(1.0f)
      .useTextureAlpha()
      .uvOffset(0, 0)
      .texture(DragoonModifier.ENHANCEMENT_TEXTURE);

    if(selected && menu.renderSelectedIconText_40) {
      final int oldZ = textZ_800bdf00;
      textZ_800bdf00 = 124;

      if(this.character == DragoonModifier.DART.getId()) {
        renderText(I18n.translate(DragoonModifier.BURN_STACK_ACTION), menuElementBaseX + ICON_SIZE / 2.0f, menu.y_08 - 24.0f, FONT);
      } else if(this.character == DragoonModifier.LAVITZ.getId() || this.character == DragoonModifier.ALBERT.getId()) {
        renderText(I18n.translate(DragoonModifier.WIND_BARRIER_ACTION), menuElementBaseX + ICON_SIZE / 2.0f, menu.y_08 - 24.0f, FONT);
      } else if(this.character == DragoonModifier.SHANA.getId() || this.character == DragoonModifier.MIRANDA.getId()) {
        renderText(I18n.translate(DragoonModifier.ELEMENTAL_QUIVER_ACTION), menuElementBaseX + ICON_SIZE / 2.0f, menu.y_08 - 24.0f, FONT);
      } else if(this.character == DragoonModifier.ROSE.getId()) {
        renderText(I18n.translate(DragoonModifier.SIPHON_ACTION), menuElementBaseX + ICON_SIZE / 2.0f, menu.y_08 - 24.0f, FONT);
      } else if(this.character == DragoonModifier.HASCHEL.getId()) {
        renderText(I18n.translate(DragoonModifier.STATIC_CHARGE), menuElementBaseX + ICON_SIZE / 2.0f, menu.y_08 - 24.0f, FONT);
      } else if(this.character == DragoonModifier.MERU.getId()) {
        renderText(I18n.translate(DragoonModifier.WINGLY_MAGIC), menuElementBaseX + ICON_SIZE / 2.0f, menu.y_08 - 24.0f, FONT);
      } else if(this.character == DragoonModifier.KONGOL.getId()) {
        renderText(I18n.translate(DragoonModifier.COUNTER_STANCE), menuElementBaseX + ICON_SIZE / 2.0f, menu.y_08 - 24.0f, FONT);
      }

      textZ_800bdf00 = oldZ;
    }
  }

  @Override
  public BattleActionTickFlowControl tick(final Battle battle, final PlayerBattleEntity player) {
    if(this.battle.hud.listMenu_800c6b60 != null) {
      return BattleActionTickFlowControl.PAUSE_SCRIPT;
    }

    return BattleActionTickFlowControl.REPEAT_TURN;
  }

  private void onListClose() {
    if(this.battle != null) {
      this.battle.hud.listMenu_800c6b60 = null;
    }
  }
}
