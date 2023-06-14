package lod.dragoonmodifier.configs;

import legend.game.saves.ConfigStorageLocation;
import legend.game.saves.EnumConfigEntry;
import lod.dragoonmodifier.configs.values.TurnBattleMode;

public class ConfigTurnBattleMode extends EnumConfigEntry<TurnBattleMode> {
    public ConfigTurnBattleMode() {
        super(TurnBattleMode.class, TurnBattleMode.OFF, ConfigStorageLocation.CAMPAIGN);
    }
}