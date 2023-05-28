package lod.dragoonmodifier;

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