package com.zombicide.missiongen.DTO;

import java.util.ArrayList;
import java.util.List;

import com.zombicide.missiongen.model.Mission;
import com.zombicide.missiongen.model.board.MissionBoard;
import com.zombicide.missiongen.model.board.MissionTileEntry;
import com.zombicide.missiongen.model.board.TileEntry;

public class MissionDTO {
    public int rows;
    public int cols;
    public int width;
    public int height;
    public String edition;
    public String collection;
    public String imagePath;
    public String missionName;

    public List<BoardAreaDTO> areas;
    public List<BoardAreaConnectionDTO> connections;
    public List<TokenDTO> tokens;

    public MissionTileEntry [][] gridTiles;


    public MissionDTO() {
        this.areas = new ArrayList<>();
        this.connections = new ArrayList<>();
        this.tokens = new ArrayList<>();
    }

    public static MissionDTO fromMission(Mission mission) {
        MissionDTO dto = new MissionDTO();
        dto.rows = mission.getRows();
        dto.cols = mission.getCols();
        dto.width = mission.getWidth();
        dto.height = mission.getHeight();
        dto.edition = mission.getEdition();
        dto.collection = mission.getCollection();
        dto.imagePath = mission.getImagePath();
        dto.missionName = mission.getMissionName();
        dto.gridTiles = mission.getGridTiles();

        MissionBoard board = mission.getMissionBoard();
        if (board != null) {
            if (board.getAreas() != null) {
                for (com.zombicide.missiongen.model.areas.BoardArea area : board.getAreas()) {
                    dto.areas.add(BoardAreaDTO.fromBoardArea(area));
                }
            }
            if (board.getConnections() != null) {
                for (com.zombicide.missiongen.model.areas.BoardAreaConnection connection : board.getConnections()) {
                    dto.connections.add(BoardAreaConnectionDTO.fromBoardAreaConnection(connection));
                }
            }
            if (board.getTokens() != null) {
                for (com.zombicide.missiongen.model.tokens.Token token : board.getTokens()) {
                    dto.tokens.add(TokenDTO.fromToken(token));
                }
            }
        }

        return dto;
    }
}
