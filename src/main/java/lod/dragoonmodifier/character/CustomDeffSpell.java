package lod.dragoonmodifier.character;

import legend.game.characters.Element;
import legend.game.combat.Battle;
import legend.game.combat.bent.PlayerBattleEntity;
import legend.game.combat.deff.LoadedDeff24;
import legend.game.combat.effects.ScriptDeffEffect;
import legend.game.combat.types.BattleObject;
import legend.game.scripting.ScriptState;
import legend.game.unpacker.Loader;
import legend.lodmod.spells.RetailSpell;
import lod.dragoonmodifier.DragoonModifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;
import org.legendofdragoon.modloader.registries.RegistryDelegate;

import java.io.IOException;
import java.nio.file.LinkOption;

import static legend.core.GameEngine.CONFIG;
import static legend.game.FullScreenEffects.fullScreenEffect_800bb140;
import static legend.game.combat.Battle.deffManager_800c693c;
import static lod.dragoonmodifier.DragoonModifier.DIFFICULTY;

public class CustomDeffSpell extends RetailSpell {
  private static final Logger LOGGER = LogManager.getFormatterLogger(RetailSpell.class);
  private static final Marker DEFF = MarkerManager.getMarker("DEFF");

  private final String deff;
  private final boolean forceElement;
  private final boolean forceTarget;

  public CustomDeffSpell(final int targetType, final int flags, final int specialEffect, final int damage, final int multi, final int accuracy, final int mp, final int statusChance, final RegistryDelegate<Element> element, final int statusType, final int buffType, final int _0b, final String deff, final boolean forceElement, final boolean forceTarget) {
    super(targetType, flags, specialEffect, damage, multi, accuracy, mp, statusChance, element, statusType, buffType, _0b, 0);
    this.deff = deff;
    this.forceElement = forceElement;
    this.forceTarget = forceTarget;
  }

  @Override
  public void loadDeff(final Battle battle, final ScriptState<? extends BattleObject> parent, final ScriptDeffEffect effect, final int flags, final int bentIndex, final int deffParam, final int entrypoint) {
    try {
      final PlayerBattleEntity player = ((PlayerBattleEntity)parent.innerStruct_00);
      final ScriptState<PlayerBattleEntity> state = player.getState();

      LOGGER.info(DEFF, "Loading dragoon DEFF (ID: %d, flags: %x)", this.deff, flags);

      deffManager_800c693c.flags_20 |= 0x40_0000;
      battle.allocateDeffEffectManager(parent, flags, bentIndex, deffParam, entrypoint, effect);

      battle.loadDeff(
        Loader.resolve("..").normalize().toRealPath(LinkOption.NOFOLLOW_LINKS).resolve("mods/" + DragoonModifier.MOD_ID + '/' + CONFIG.getConfig(DIFFICULTY.get()) + "/scripts/attacks/" + this.deff),
        Loader.resolve("..").normalize().toRealPath(LinkOption.NOFOLLOW_LINKS).resolve("mods/" + DragoonModifier.MOD_ID + '/' + CONFIG.getConfig(DIFFICULTY.get()) + "/scripts/attacks/" + this.deff + '1')
      );

      if(this.forceTarget) {
        final LoadedDeff24 struct24 = battle.loadedDeff_800c6938;
        struct24.param_08 = -1;
      }

      if(this.forceElement) {
        player.getAttackElements().clear();
        player.getAttackElements().add(this.element_08.get());
      }

      new Thread(() -> {
        for(int i = 0; i < 40; i++) {
          try {
            fullScreenEffect_800bb140.type_00 = 0;
            Thread.sleep(125);
          } catch(final InterruptedException e) {
            throw new RuntimeException(e);
          }
        }
      }).start();
    } catch(final IOException e) {
      throw new RuntimeException(e);
    }
  }
}