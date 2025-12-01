package com.zombicide.missiongen.model.areas;

import java.util.UUID;

import com.zombicide.missiongen.DTO.BoardAreaConnectionDTO;

/**
 * Represents a connection between two board areas, or a potential edge
 * connection from an area to the tile border.
 */
public class BoardAreaConnection {

    private UUID areaA;
    /**
     * If this connection is to another area, areaB holds the target area id.
     * If this is an edge connection, areaB is set to null.
     */
    private UUID areaB = null;
    /**
     * Direction for edge connections. Null for normal area-to-area connections.
     */
    private Direction direction;

    /** Normal connection constructor */
    public BoardAreaConnection(UUID areaA, UUID areaB) {
        this.areaA = areaA;
        this.areaB = areaB;
        this.direction = null;
    }

    /** Edge connection constructor */
    public BoardAreaConnection(UUID areaA, Direction direction) {
        this.areaA = areaA;
        this.areaB = null; // null for edge connections
        this.direction = direction;
    }

    public BoardAreaConnection(BoardAreaConnection connectionToCopy) {
        this.areaA = connectionToCopy.areaA;
        this.areaB = connectionToCopy.areaB;
        this.direction = connectionToCopy.direction;
    }

    public UUID getAreaAId() {
        return areaA;
    }

    public UUID getAreaBId() {
        return areaB;
    }

    public Direction getDirection() {
        return direction;
    }

    public void setAreaAId(UUID areaA) {
        this.areaA = areaA;
    }

    public void setAreaBId(UUID areaB) {
        this.areaB = areaB;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    public boolean isEdgeConnection() {
        return areaB == null;
    }

    public static BoardAreaConnection fromAreaConnectionDTO(BoardAreaConnectionDTO dto) {
        if (dto.direction != null && !dto.direction.isEmpty()) {
            return new BoardAreaConnection(UUID.fromString(dto.areaA), Direction.valueOf(dto.direction));
        }
        return new BoardAreaConnection(UUID.fromString(dto.areaA), UUID.fromString(dto.areaB));
    }

}
