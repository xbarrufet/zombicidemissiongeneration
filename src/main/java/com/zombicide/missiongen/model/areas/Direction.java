package com.zombicide.missiongen.model.areas;

/**
 * Cardinal directions used for potential edge connections from an area to the
 * tile border.
 */
public enum Direction {
    NORTH,
    SOUTH,
    EAST,
    WEST,
    NORTH_EAST,
    NORTH_WEST,
    SOUTH_EAST,
    SOUTH_WEST;

    public String toString() {
        return this.name().toLowerCase();
    }

    public static Direction fromString(String direction) {
        return Direction.valueOf(direction.toUpperCase());
    }
}
