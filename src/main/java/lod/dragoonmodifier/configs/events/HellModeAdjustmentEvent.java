package lod.dragoonmodifier.configs.events;
import legend.core.GameEngine;
import legend.game.modding.coremod.CoreMod;
import legend.game.modding.events.Event;
import lod.dragoonmodifier.DragoonModifier;
import lod.dragoonmodifier.configs.values.ElementalBomb;
import lod.dragoonmodifier.configs.values.EnrageMode;

public class HellModeAdjustmentEvent extends Event {
    public HellModeAdjustmentEvent() {
        int ultimateBossesDefeated = Integer.parseInt(GameEngine.CONFIG.getConfig(DragoonModifier.ULTIMATE_BOSS_DEFEATED.get()));
        if (ultimateBossesDefeated < 3) {
            GameEngine.CONFIG.setConfig(CoreMod.INVENTORY_SIZE_CONFIG.get(), 32);
        } else if (ultimateBossesDefeated < 8) {
            GameEngine.CONFIG.setConfig(CoreMod.INVENTORY_SIZE_CONFIG.get(), 36);
        } else if (ultimateBossesDefeated < 22) {
            GameEngine.CONFIG.setConfig(CoreMod.INVENTORY_SIZE_CONFIG.get(), 40);
        } else if (ultimateBossesDefeated < 34) {
            GameEngine.CONFIG.setConfig(CoreMod.INVENTORY_SIZE_CONFIG.get(), 48);
        } else if (ultimateBossesDefeated >= 34) {
            GameEngine.CONFIG.setConfig(CoreMod.INVENTORY_SIZE_CONFIG.get(), 64);
        }
        GameEngine.CONFIG.setConfig(DragoonModifier.ENRAGE_MODE.get(), EnrageMode.ON);
        GameEngine.CONFIG.setConfig(DragoonModifier.ELEMENTAL_BOMB.get(), ElementalBomb.ON);
    }
}
