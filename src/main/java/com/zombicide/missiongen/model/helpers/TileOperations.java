package com.zombicide.missiongen.model.helpers;

import java.util.ArrayList;
import java.util.List;

import com.zombicide.missiongen.model.areas.AreaLocation;
import com.zombicide.missiongen.model.areas.Direction;

public class TileOperations {

    public static class MirrorStreetLocation {
        public Direction direction;
        public AreaLocation location;

        public MirrorStreetLocation(Direction direction, AreaLocation location) {
            this.direction = direction;
            this.location = location;
        }
    }

    public static MirrorStreetLocation[] getNeighbouringStreetLocation(AreaLocation areaLocation) {

        List<MirrorStreetLocation> neighbours = new ArrayList<>();
        switch (areaLocation) {
            case TOP_LEFT_STREET:
                neighbours.add(new MirrorStreetLocation(Direction.NORTH, AreaLocation.BOTTOM_LEFT_STREET));
                neighbours.add(new MirrorStreetLocation(Direction.WEST, AreaLocation.TOP_RIGHT_STREET));
                neighbours.add(new MirrorStreetLocation(Direction.NORTH_WEST, AreaLocation.BOTTOM_RIGHT_STREET));
                break;
            case TOP_RIGHT_STREET:
                neighbours.add(new MirrorStreetLocation(Direction.NORTH, AreaLocation.BOTTOM_RIGHT_STREET));
                neighbours.add(new MirrorStreetLocation(Direction.EAST, AreaLocation.TOP_LEFT_STREET));
                neighbours.add(new MirrorStreetLocation(Direction.NORTH_EAST, AreaLocation.BOTTOM_LEFT_STREET));
                break;
            case BOTTOM_LEFT_STREET:
                neighbours.add(new MirrorStreetLocation(Direction.SOUTH, AreaLocation.TOP_LEFT_STREET));
                neighbours.add(new MirrorStreetLocation(Direction.WEST, AreaLocation.BOTTOM_RIGHT_STREET));
                neighbours.add(new MirrorStreetLocation(Direction.SOUTH_WEST, AreaLocation.TOP_RIGHT_STREET));
                break;
            case BOTTOM_RIGHT_STREET:
                neighbours.add(new MirrorStreetLocation(Direction.SOUTH, AreaLocation.TOP_RIGHT_STREET));
                neighbours.add(new MirrorStreetLocation(Direction.EAST, AreaLocation.BOTTOM_LEFT_STREET));
                neighbours.add(new MirrorStreetLocation(Direction.SOUTH_EAST, AreaLocation.TOP_LEFT_STREET));
                break;
            case MIDDLE_LEFT_STREET:
                neighbours.add(new MirrorStreetLocation(Direction.WEST, AreaLocation.MIDDLE_RIGHT_STREET));
                break;
            case MIDDLE_RIGHT_STREET:
                neighbours.add(new MirrorStreetLocation(Direction.EAST, AreaLocation.MIDDLE_LEFT_STREET));
                break;
            case TOP_MIDDLE_STREET:
                neighbours.add(new MirrorStreetLocation(Direction.NORTH, AreaLocation.BOTTOM_MIDDLE_STREET));
                break;
            case BOTTOM_MIDDLE_STREET:
                neighbours.add(new MirrorStreetLocation(Direction.SOUTH, AreaLocation.TOP_MIDDLE_STREET));
                break;
            default:
                break;
        }
        return neighbours.toArray(new MirrorStreetLocation[0]);
    }

    public static int[] getDirectionOffset(int x, int y, Direction direction) {
        switch (direction) {
            case NORTH:
                return new int[] { x, y - 1 };
            case SOUTH:
                return new int[] { x, y + 1 };
            case EAST:
                return new int[] { x + 1, y };
            case WEST:
                return new int[] { x - 1, y };
            case NORTH_EAST:
                return new int[] { x + 1, y - 1 };
            case NORTH_WEST:
                return new int[] { x - 1, y - 1 };
            case SOUTH_EAST:
                return new int[] { x + 1, y + 1 };
            case SOUTH_WEST:
                return new int[] { x - 1, y + 1 };
            default:
                return new int[] { x, y };
        }
    }

}
