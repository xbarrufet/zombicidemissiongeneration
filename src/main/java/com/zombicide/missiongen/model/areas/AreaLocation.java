package com.zombicide.missiongen.model.areas;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum AreaLocation {
    TOP_LEFT_STREET, TOP_MIDDLE_STREET, TOP_RIGHT_STREET,
    MIDDLE_LEFT_STREET, MIDDLE_RIGHT_STREET,
    BOTTOM_LEFT_STREET, BOTTOM_MIDDLE_STREET, BOTTOM_RIGHT_STREET, OTHER;

    public static AreaLocation fromString(String text) {
        return AreaLocation.valueOf(text.toUpperCase());
    }

    public String toString() {
        return this.name().toUpperCase();
    }

    public static List<AreaLocation> getAll() {
        return Arrays.stream(AreaLocation.values()).collect(Collectors.toList());
    }

    public static List<AreaLocation> getStreetLocations() {
        return Arrays.stream(AreaLocation.values()).filter(areaLocation -> areaLocation.name().contains("STREET"))
                .collect(Collectors.toList());
    }

    public AreaLocation rotate() {
        switch (this) {
            case TOP_LEFT_STREET -> {
                return AreaLocation.TOP_RIGHT_STREET;
            }
            case TOP_MIDDLE_STREET -> {
                return AreaLocation.MIDDLE_RIGHT_STREET;
            }
            case TOP_RIGHT_STREET -> {
                return AreaLocation.BOTTOM_RIGHT_STREET;
            }
            case MIDDLE_LEFT_STREET -> {
                return AreaLocation.TOP_MIDDLE_STREET;
            }
            case MIDDLE_RIGHT_STREET -> {
                return AreaLocation.BOTTOM_MIDDLE_STREET;
            }
            case BOTTOM_LEFT_STREET -> {
                return AreaLocation.TOP_LEFT_STREET;
            }
            case BOTTOM_MIDDLE_STREET -> {
                return AreaLocation.MIDDLE_LEFT_STREET;
            }
            case BOTTOM_RIGHT_STREET -> {
                return AreaLocation.BOTTOM_LEFT_STREET;
            }
            default -> {
                return AreaLocation.OTHER;
            }
        }
    }

}
