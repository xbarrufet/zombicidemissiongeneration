package com.zombicide.missiongen.model.board;

import java.awt.Image;
import java.util.List;
import com.zombicide.missiongen.model.areas.BoardArea;
import com.zombicide.missiongen.model.areas.BoardAreaConnection;
import com.zombicide.missiongen.model.helpers.ImageOperations;

public class MissionBoard extends BaseBoard {

    private Image originalImage;
    MissionTileEntry[][] gridTiles;
    public MissionBoard(String boardId, Image backgroundImage, String imagePath, int width, int height, MissionTileEntry[][] gridTiles) {
        super(boardId, backgroundImage, width, height, imagePath);
        this.originalImage = ImageOperations.copyImage(backgroundImage);
        this.gridTiles = gridTiles;
    }

    // public MissionBoard(MissionGridCell[][] grid) {

    // }

    public void addAreas(List<BoardArea> areas) {
        this.getAreas().addAll(areas);
    }

    public void addConnections(List<BoardAreaConnection> connections) {
        this.getConnections().addAll(connections);
    }

    public MissionTileEntry[][] getGridTiles() {
        return gridTiles;
    }

    @Override
    public Image getImage() {
        return this.originalImage;
    }

}
