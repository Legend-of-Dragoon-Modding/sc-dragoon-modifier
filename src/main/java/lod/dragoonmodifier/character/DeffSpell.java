package lod.dragoonmodifier.character;

import legend.game.characters.Element;
import legend.game.combat.Battle;
import legend.game.combat.effects.ScriptDeffEffect;
import legend.game.combat.types.BattleObject;
import legend.game.scripting.ScriptState;
import legend.game.unpacker.Loader;
import legend.lodmod.spells.RetailSpell;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;
import org.legendofdragoon.modloader.registries.RegistryDelegate;

import static legend.game.DrgnFiles.loadDrgnDir;
import static legend.game.FullScreenEffects.fullScreenEffect_800bb140;
import static legend.game.combat.Battle.deffManager_800c693c;
import static legend.game.combat.Battle.dragoonDeffFlags_800fafec;
import static legend.game.combat.Battle.dragoonDeffsWithExtraTims_800fb040;

public class DeffSpell extends RetailSpell {
  private static final Logger LOGGER = LogManager.getFormatterLogger(RetailSpell.class);
  private static final Marker DEFF = MarkerManager.getMarker("DEFF");

  private final int index;

  public DeffSpell(final int targetType, final int flags, final int specialEffect, final int damage, final int multi, final int accuracy, final int mp, final int statusChance, final RegistryDelegate<Element> element, final int statusType, final int buffType, final int _0b, final int index) {
    super(targetType, flags, specialEffect, damage, multi, accuracy, mp, statusChance, element, statusType, buffType, _0b, index);
    this.index = index;
  }

  @Override
  public void loadDeff(final Battle battle, final ScriptState<? extends BattleObject> parent, final ScriptDeffEffect effect, final int flags, final int bentIndex, final int deffParam, final int entrypoint) {
    LOGGER.info(DEFF, "Loading dragoon DEFF (ID: %d, flags: %x)", this.index, flags);

    deffManager_800c693c.flags_20 |= 0x40_0000;
    battle.allocateDeffEffectManager(parent, flags, bentIndex, deffParam, entrypoint, effect);

    battle.loadDeff(
      Loader.resolve("SECT/DRGN0.BIN/" + this.index),
      Loader.resolve("SECT/DRGN0.BIN/" + (this.index + 1))
    );

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
  }
}
