package lod.dragoonmodifier;

public enum NeverGuard {
    OFF("Off"),
    ON("On"),
    ;

    public final String name;

    NeverGuard(final String name) {
        this.name = name;
    }
}