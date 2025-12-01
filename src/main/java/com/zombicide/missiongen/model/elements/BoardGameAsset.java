package com.zombicide.missiongen.model.elements;

public enum BoardGameAsset {
    GoalMarker,
    PimpWeapon;

    public String toString() {
        return this.name();
    }

    public static BoardGameAsset fromString(String name) {
        return valueOf(name);
    }

}
