package com.zombicide.missiongen.model.areas;

/**
 * Cardinal directions used for potential edge connections from an area to the
 * tile border.
 */
public enum DoorDirection {
    NORTH_LEFT,
    NORTH_RIGHT,
    NORTH_CENTER,
    SOUTH_LEFT,
    SOUTH_RIGHT,
    SOUTH_CENTER,
    EAST_TOP,
    EAST_BOTTOM,
    EAST_CENTER,
    WEST_TOP,
    WEST_BOTTOM,
    WEST_CENTER;

    public String toString() {
        return this.name().toLowerCase();
    }

    public static DoorDirection fromString(String direction) {
        return DoorDirection.valueOf(direction.toUpperCase());
    }

    public DoorDirection rotate() {
        switch (this) {
            case NORTH_LEFT:
                return DoorDirection.EAST_TOP;
            case NORTH_RIGHT:
                return DoorDirection.EAST_BOTTOM;
            case NORTH_CENTER:
                return DoorDirection.EAST_CENTER;
            case SOUTH_LEFT:
                return DoorDirection.WEST_TOP;
            case SOUTH_RIGHT:
                return DoorDirection.WEST_BOTTOM;
            case SOUTH_CENTER:
                return DoorDirection.WEST_CENTER;
            case EAST_TOP:
                return DoorDirection.SOUTH_LEFT;
            case EAST_BOTTOM:
                return DoorDirection.SOUTH_RIGHT;
            case EAST_CENTER:
                return DoorDirection.SOUTH_CENTER;
            case WEST_TOP:
                return DoorDirection.NORTH_RIGHT;
            case WEST_BOTTOM:
                return DoorDirection.NORTH_LEFT;
            case WEST_CENTER:
                return DoorDirection.NORTH_CENTER;
            default:
                return this;
        }
    }

    public DoorDirection getOpposite() {
        switch (this) {
            case NORTH_LEFT:
                return SOUTH_LEFT;
            case NORTH_RIGHT:
                return SOUTH_RIGHT;
            case NORTH_CENTER:
                return SOUTH_CENTER;
            case SOUTH_LEFT:
                return NORTH_LEFT;
            case SOUTH_RIGHT:
                return NORTH_RIGHT;
            case SOUTH_CENTER:
                return NORTH_CENTER;
            case EAST_TOP:
                return WEST_TOP;
            case EAST_BOTTOM:
                return WEST_BOTTOM;
            case EAST_CENTER:
                return WEST_CENTER;
            case WEST_TOP:
                return EAST_TOP;
            case WEST_BOTTOM:
                return EAST_BOTTOM;
            case WEST_CENTER:
                return EAST_CENTER;
            default:
                return null;
        }
    }

    public AreaLocation toStreetLocation() {
        switch (this) {
            case NORTH_LEFT:
                return AreaLocation.BOTTOM_LEFT_STREET;
            case NORTH_RIGHT:
                return AreaLocation.BOTTOM_RIGHT_STREET;
            case NORTH_CENTER:
                return AreaLocation.BOTTOM_MIDDLE_STREET;
            case SOUTH_LEFT:
                return AreaLocation.TOP_LEFT_STREET;
            case SOUTH_RIGHT:
                return AreaLocation.TOP_RIGHT_STREET;
            case SOUTH_CENTER:
                return AreaLocation.TOP_MIDDLE_STREET;
            case EAST_TOP:
                return AreaLocation.MIDDLE_LEFT_STREET; // Assuming simplified mapping for now, adjust if needed
            case EAST_BOTTOM:
                return AreaLocation.MIDDLE_LEFT_STREET;
            case EAST_CENTER:
                return AreaLocation.MIDDLE_LEFT_STREET;
            case WEST_TOP:
                return AreaLocation.MIDDLE_RIGHT_STREET;
            case WEST_BOTTOM:
                return AreaLocation.MIDDLE_RIGHT_STREET;
            case WEST_CENTER:
                return AreaLocation.MIDDLE_RIGHT_STREET;
            default:
                return null;
        }
    }
}
