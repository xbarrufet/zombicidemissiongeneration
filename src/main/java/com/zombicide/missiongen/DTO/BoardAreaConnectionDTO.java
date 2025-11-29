package com.zombicide.missiongen.DTO;

public class BoardAreaConnectionDTO {
    public int areaA;
    public int areaB;

    public static BoardAreaConnectionDTO fromBoardAreaConnection(
            com.zombicide.missiongen.model.BoardAreaConnection connection) {
        BoardAreaConnectionDTO dto = new BoardAreaConnectionDTO();
        dto.areaA = connection.getAreaAId();
        dto.areaB = connection.getAreaBId();
        return dto;
    }
}
