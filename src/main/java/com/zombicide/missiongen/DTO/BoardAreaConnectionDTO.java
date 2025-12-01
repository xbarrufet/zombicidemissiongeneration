package com.zombicide.missiongen.DTO;

/**
 * Data Transfer Object for BoardAreaConnection. Supports both normal
 * connections (areaA ↔ areaB)
 * and edge connections (areaA → direction).
 */
public class BoardAreaConnectionDTO {
    public String areaA;
    // For normal connections this holds the target area id; for edge connections
    // set to null.
    public String areaB = null;
    // Optional direction for edge connections (NORTH, SOUTH, EAST, WEST).
    public String direction;

    public static BoardAreaConnectionDTO fromBoardAreaConnection(
            com.zombicide.missiongen.model.areas.BoardAreaConnection connection) {
        BoardAreaConnectionDTO dto = new BoardAreaConnectionDTO();
        dto.areaA = connection.getAreaAId().toString();
        if (connection.getAreaBId() != null) {
            dto.areaB = connection.getAreaBId().toString();
        }
        if (connection.getDirection() != null) {
            dto.direction = connection.getDirection().name();
        }
        return dto;
    }
}
