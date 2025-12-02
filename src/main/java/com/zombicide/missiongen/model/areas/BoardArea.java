package com.zombicide.missiongen.model.areas;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.awt.Point;

import com.zombicide.missiongen.DTO.BoardAreaDTO;
import com.zombicide.missiongen.config.ConfigLoader;

public class BoardArea {

    public static String BOARD_AREA_TYPE = "area";

    private AreaType areaType;
    private UUID areaId;
    private Point topLeft;
    private int width;
    private int height;
    private AreaLocation areaLocation;
    private ConfigLoader configLoader;

    public BoardArea(UUID id, Point origin, int width, int height, AreaLocation areaLocation) {
        this.areaId = id;
        this.width = width;
        this.height = height;
        this.areaType = AreaType.OUTDOOR;
        this.topLeft = origin;
        this.areaLocation = areaLocation;
        this.configLoader = ConfigLoader.getInstance();
    }

    public BoardArea(UUID id, Point origin, int width, int height) {
        this.areaId = id;
        this.width = width;
        this.height = height;
        this.areaType = AreaType.INDOOR_LIGHT;
        this.topLeft = origin;
        this.areaLocation = AreaLocation.OTHER;
    }

    public BoardArea(UUID id, Point origin, int width, int height, AreaType areaType) {
        this.areaId = id;
        this.width = width;
        this.height = height;
        this.areaType = areaType;
        this.topLeft = origin;
        this.areaLocation = AreaLocation.OTHER;
    }

    public BoardArea(UUID id, Point origin, int width, int height, String areaType) {
        this.areaId = id;
        this.width = width;
        this.height = height;
        this.areaType = AreaType.fromString(areaType);
        this.topLeft = origin;
        this.areaLocation = AreaLocation.OTHER;
    }

    public BoardArea(UUID id, Point origin, int width, int height, String areaType,
            AreaLocation areaLocation) {
        this.areaId = id;
        this.width = width;
        this.height = height;
        this.areaType = AreaType.fromString(areaType);
        this.topLeft = origin;
        this.areaLocation = areaLocation;
    }

    public BoardArea(BoardArea areaToCopy) {
        this.areaId = areaToCopy.areaId;
        this.width = areaToCopy.width;
        this.height = areaToCopy.height;
        this.areaType = areaToCopy.areaType;
        this.topLeft = areaToCopy.topLeft;
        this.areaLocation = areaToCopy.areaLocation;
    }

    public boolean isAreaOverlap(BoardArea area) {
        return area.isPointInside(getTopLeft())
                || area.isPointInside(new Point(getTopLeft().x + getWidth(), getTopLeft().y + getHeight()));
    }

    public boolean isPointInside(Point point) {
        return point.x > topLeft.x && point.x < topLeft.x + width && point.y > topLeft.y
                && point.y < topLeft.y + height;
    }

    public void setAreaType(AreaType areaType) {
        this.areaType = areaType;
    }

    public AreaType getAreaType() {
        return areaType;
    }

    public enum AreaType {
        STREET, INDOOR_LIGHT, INDOOR_DARK, OUTDOOR, STREET_MERGE;

        public String toString() {
            return this.name().toUpperCase();
        }

        public static AreaType fromString(String text) {
            return AreaType.valueOf(text.toUpperCase());
        }

    }

    public static BoardArea fromBoardAreaDTO(BoardAreaDTO boardAreaDTO) {
        return new BoardArea(UUID.fromString(boardAreaDTO.id), new Point(boardAreaDTO.x, boardAreaDTO.y),
                boardAreaDTO.width,
                boardAreaDTO.height, boardAreaDTO.areaType, AreaLocation.fromString(boardAreaDTO.areaLocation));
    }

    public UUID getAreaId() {
        return areaId;
    }

    public void setAreaId(UUID areaId) {
        this.areaId = areaId;
    }

    public Point getTopLeft() {
        return topLeft;
    }

    public void setTopLeft(Point topLeft) {
        this.topLeft = topLeft;
    }

    public Point getBottomRight() {
        return new Point(topLeft.x + width, topLeft.y + height);
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public void shiftArea(int x, int y) {
        this.topLeft.x += x;
        this.topLeft.y += y;
    }

    public AreaLocation getAreaLocation() {
        return areaLocation;
    }

    public void setAreaLocation(AreaLocation areaLocation) {
        this.areaLocation = areaLocation;
    }

    public Point getNeighbourPoint(Direction direction) {
        switch (direction) {
            case NORTH:
                return new Point(getTopLeft().x, getTopLeft().y - 1);
            case SOUTH:
                return new Point(getTopLeft().x, getBottomRight().y + 1);
            case EAST:
                return new Point(getBottomRight().x + 1, getTopLeft().y);
            case WEST:
                return new Point(getTopLeft().x - 1, getTopLeft().y);
            case NORTH_EAST:
                return new Point(getBottomRight().x + 1, getTopLeft().y - 1);
            case NORTH_WEST:
                return new Point(getTopLeft().x - 1, getTopLeft().y - 1);
            case SOUTH_EAST:
                return new Point(getBottomRight().x + 1, getBottomRight().y + 1);
            case SOUTH_WEST:
                return new Point(getTopLeft().x - 1, getBottomRight().y + 1);
            default:
                return null;
        }
    }
}
