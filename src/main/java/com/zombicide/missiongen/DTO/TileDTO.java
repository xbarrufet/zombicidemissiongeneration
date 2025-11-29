package com.zombicide.missiongen.DTO;

import java.util.ArrayList;
import java.util.List;

import com.zombicide.missiongen.model.Board;
import com.zombicide.missiongen.model.Tile;

public class TileDTO {
    public String edition;
    public String collection;
    public String imagePath;
    public String tileName;

    public List<BoardAreaDTO> areas;
    public List<BoardAreaConnectionDTO> connections;

    public TileDTO() {
        this.areas = new ArrayList<>();
        this.connections = new ArrayList<>();
    }

    public static TileDTO fromTile(Tile tile) {
        TileDTO tileDTO = new TileDTO();
        tileDTO.edition = tile.getEdition();
        tileDTO.collection = tile.getCollection();
        tileDTO.imagePath = tile.getImagePath();
        tileDTO.tileName = tile.getTileName();

        // Add all areas from the board
        Board board = tile.getBoard();
        if (board != null) {
            for (com.zombicide.missiongen.model.BoardArea area : board.getAreas()) {
                tileDTO.areas.add(BoardAreaDTO.fromBoardArea(area));
            }

            // Add all connections from the board
            for (com.zombicide.missiongen.model.BoardAreaConnection connection : board.getConnections()) {
                tileDTO.connections.add(BoardAreaConnectionDTO.fromBoardAreaConnection(connection));
            }
        }

        return tileDTO;
    }

}
