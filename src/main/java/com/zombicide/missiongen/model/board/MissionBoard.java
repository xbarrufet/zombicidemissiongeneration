package com.zombicide.missiongen.model.board;

import java.awt.Image;
import java.util.List;
import com.zombicide.missiongen.model.areas.BoardArea;
import com.zombicide.missiongen.model.areas.BoardAreaConnection;

public class MissionBoard extends BaseBoard {

    public MissionBoard(String boardId, Image backgroundImage, int width, int height) {
        super(boardId, backgroundImage, width, height);
    }

    // public MissionBoard(MissionGridCell[][] grid) {

    // }

    public void addAreas(List<BoardArea> areas) {
        this.getAreas().addAll(areas);
    }

    public void addConnections(List<BoardAreaConnection> connections) {
        this.getConnections().addAll(connections);
    }

}
