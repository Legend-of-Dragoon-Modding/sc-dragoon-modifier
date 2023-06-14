package lod.dragoonmodifier.configs.values;

public enum TurnBattleMode {
    OFF("Off"),
    EXTRA("Extra"),
    ACTION("Action"),
    QUICK("Quick"),
    ;

    public final String name;

    TurnBattleMode(final String name) {
        this.name = name;
    }
}