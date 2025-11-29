package com.zombicide.missiongen.model;

import java.awt.Point;

import com.zombicide.missiongen.DTO.BoardAreaDTO;

public class BoardArea extends BoardElement {

    public static String BOARD_AREA_TYPE = "area";

    private AreaType areaType;

    public BoardArea(int id, Point origin, int width, int height) {
        super(id, origin, BOARD_AREA_TYPE, width, height);
        this.areaType = AreaType.INDOOR;
    }

    public BoardArea(int id, Point origin, int width, int height, AreaType areaType) {
        super(id, origin, BOARD_AREA_TYPE, width, height);
        this.areaType = areaType;
    }

    public BoardArea(int id, Point origin, int width, int height, String areaType) {
        super(id, origin, BOARD_AREA_TYPE, width, height);
        this.areaType = AreaType.fromString(areaType);
    }

    public boolean isAreaOverlap(BoardElement element) {
        return element.isPointInside(getOrigin())
                || element.isPointInside(new Point(getOrigin().x + getWidth(), getOrigin().y + getHeight()));
    }

    public void setAreaType(AreaType areaType) {
        this.areaType = areaType;
    }

    public AreaType getAreaType() {
        return areaType;
    }

    public enum AreaType {
        BASIC, INDOOR, OUTDOOR;

        public String toString() {
            return this.name().toUpperCase();
        }

        public static AreaType fromString(String text) {
            return AreaType.valueOf(text.toUpperCase());
        }

    }

    public static BoardArea fromBoardAreaDTO(BoardAreaDTO boardAreaDTO) {
        return new BoardArea(boardAreaDTO.id, new Point(boardAreaDTO.x, boardAreaDTO.y), boardAreaDTO.width,
                boardAreaDTO.height, boardAreaDTO.areaType);
    }

}
