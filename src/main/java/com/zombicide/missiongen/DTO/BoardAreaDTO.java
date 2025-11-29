package com.zombicide.missiongen.DTO;

public class BoardAreaDTO {

    public int id;
    public int x;
    public int y;
    public int width;
    public int height;
    public String areaType;

    public static BoardAreaDTO fromBoardArea(com.zombicide.missiongen.model.BoardArea area) {
        BoardAreaDTO dto = new BoardAreaDTO();
        dto.id = area.getId();
        dto.x = area.getOrigin().x;
        dto.y = area.getOrigin().y;
        dto.width = area.getWidth();
        dto.height = area.getHeight();
        dto.areaType = area.getAreaType().toString();
        return dto;
    }
}
