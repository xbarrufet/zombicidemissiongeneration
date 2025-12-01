package com.zombicide.missiongen.model.board;

import java.util.ArrayList;
import java.util.List;

import javax.swing.event.MenuKeyEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zombicide.missiongen.model.areas.AreaLocation;
import com.zombicide.missiongen.model.helpers.TileOperations;
import com.zombicide.missiongen.model.helpers.TileOperations.MirrorStreetLocation;

public class MissionGrid {

    private TileBoard[][] grid;
    private boolean[][] cellFilled;
    private int gridWidth;
    private int gridHeight;

    private static final Logger logger = LoggerFactory.getLogger(MissionGrid.class);

    public MissionGrid(TileBoard[][] grid) {
        this.grid = grid;
        this.cellFilled = new boolean[grid.length][grid[0].length];
        // fill it true
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[0].length; j++) {
                this.cellFilled[i][j] = true;
            }
        }
        this.gridHeight = grid.length; // height = rows
        this.gridWidth = grid[0].length; // width = columns
    }

    public MissionGrid(int width, int height) {
        this.grid = new TileBoard[height][width]; // grid[rows][cols]
        this.cellFilled = new boolean[height][width];
        // fill it false
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                this.cellFilled[i][j] = false;
            }
        }
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                this.grid[i][j] = null;
            }
        }
        this.gridWidth = width;
        this.gridHeight = height;
    }

    public void setBoard(int x, int y, TileBoard board) {
        grid[y][x] = board; // grid[row][col]

    }

    public TileBoard getBoard(int x, int y) {
        return grid[y][x]; // grid[row][col]
    }

    public int getGridWidth() {
        return gridWidth;
    }

    public int getGridHeight() {
        return gridHeight;
    }

    public boolean validate() {
        // check that all boards are valid due to restrictions on STREET_LOCATIONS
        // across the board
        boolean valid = true;
        for (int row = 0; row < gridHeight; row++) {
            for (int col = 0; col < gridWidth; col++) {
                TileBoard board = grid[row][col];
                if (board != null) {
                    for (AreaLocation areaLocation : AreaLocation.getStreetLocations()) {
                        if (board.hasAreaLocation(areaLocation)) {
                            valid = valid && validateStreetLocation(areaLocation, col, row);
                        }
                    }
                }
            }
        }
        return valid;
    }

    private boolean validateStreetLocation(AreaLocation streetLocation, int x, int y) {
        MirrorStreetLocation[] mirrorStreetLocations = TileOperations.getNeighbouringStreetLocation(streetLocation);
        boolean valid = true;
        for (MirrorStreetLocation mirrorStreetLocation : mirrorStreetLocations) {
            boolean mirrorStreetLocationValid = validateMirrorStreetLocation(mirrorStreetLocation, x, y);
            if (!mirrorStreetLocationValid) {
                logger.info("Mirror street location {} {} {} {} {} is not valid", x, y, streetLocation,
                        mirrorStreetLocation.location, mirrorStreetLocation.direction);
            }
            valid = valid && mirrorStreetLocationValid;
        }
        return valid;
    }

    private boolean validateMirrorStreetLocation(MirrorStreetLocation mirrorStreetLocation, int x, int y) {
        // validate that the mirror street location is valid it means is mirroring or is
        // null
        int[] offset = TileOperations.getDirectionOffset(x, y, mirrorStreetLocation.direction);
        if (offset[0] < 0 || offset[1] < 0 || offset[0] >= gridWidth || offset[1] >= gridHeight) {
            return true;
        }
        TileBoard board = grid[offset[1]][offset[0]]; // grid[row][col] = grid[y][x]
        if (board == null) {
            return true;
        } else {
            return board.hasAreaLocation(mirrorStreetLocation.location);
        }
    }

    public boolean isCompleteAndValid() {
        boolean complete = true;
        for (int row = 0; row < gridHeight; row++) {
            for (int col = 0; col < gridWidth; col++) {
                TileBoard board = grid[row][col];
                if (board == null) {
                    complete = false;
                    break;
                }
            }
        }
        return complete && validate();
    }

}
