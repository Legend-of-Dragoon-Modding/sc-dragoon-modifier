package lod.dragoonmodifier.items;

import legend.core.GameEngine;
import legend.game.combat.Battle;
import legend.game.combat.deff.DeffPackage;
import lod.dragoonmodifier.DragoonModifier;

import java.nio.file.Path;

import static legend.game.EngineStates.currentEngineState_8004dd04;
import static lod.dragoonmodifier.DragoonModifier.DIFFICULTY;

public class DraModItemDeffPackage extends DeffPackage {
  private final String location;

  public DraModItemDeffPackage(final String location) {
    this.location = location;
  }

  @Override
  public void load() {
    ((Battle)currentEngineState_8004dd04).loadDeff(
      Path.of("mods", DragoonModifier.MOD_ID, GameEngine.CONFIG.getConfig(DIFFICULTY.get()), "scripts", "items", this.location + '0'),
      Path.of("mods", DragoonModifier.MOD_ID, GameEngine.CONFIG.getConfig(DIFFICULTY.get()), "scripts", "items", this.location + '1')
    );
  }
}
