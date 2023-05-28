package lod.dragoonmodifier;

import legend.game.saves.ConfigStorageLocation;
import legend.game.saves.EnumConfigEntry;

public class ConfigTurnBattleMode extends EnumConfigEntry<TurnBattleMode> {
    public ConfigTurnBattleMode() {
        super(TurnBattleMode.class, TurnBattleMode.OFF, ConfigStorageLocation.CAMPAIGN);
    }
}