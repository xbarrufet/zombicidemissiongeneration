package com.zombicide.missiongen.DTO;

import java.util.List;
import java.util.stream.Collectors;

public class BoardAreaDTO {

    public String id;
    public int x;
    public int y;
    public int width;
    public int height;
    public String areaType;
    public String areaLocation;
    public List<String> boardGameAssets;

    public static BoardAreaDTO fromBoardArea(com.zombicide.missiongen.model.areas.BoardArea area) {
        BoardAreaDTO dto = new BoardAreaDTO();
        dto.areaLocation = area.getAreaLocation().toString();
        dto.id = area.getAreaId().toString();
        dto.x = area.getTopLeft().x;
        dto.y = area.getTopLeft().y;
        dto.width = area.getWidth();
        dto.height = area.getHeight();
        dto.areaType = area.getAreaType().toString();
        return dto;
    }
}
