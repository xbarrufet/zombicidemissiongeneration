package com.zombicide.missiongen.model.board;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import javax.swing.event.MenuKeyEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zombicide.missiongen.model.areas.AreaLocation;
import com.zombicide.missiongen.model.areas.BoardArea;
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

    // **********************. VALIDATE SECTION

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

    // *****************************************************
    // MERGE AREAS SECTION
    // *****************************************************

    /**
     * Gets all pairs of areas that should be merged across adjacent tiles.
     * Each set contains exactly 2 UUIDs representing areas that share a street
     * connection.
     * 
     * @return List of merge groups, each containing 2 area UUIDs
     */
    public List<Set<UUID>> getAreasToMerge() {
        List<Set<UUID>> areasToMerge = new ArrayList<>();
        Set<UUID> alreadyMerged = new HashSet<>();

        for (int row = 0; row < gridHeight; row++) {
            for (int col = 0; col < gridWidth; col++) {
                TileBoard board = grid[row][col];
                if (board != null) {
                    findMergePairsForBoard(board, row, col, areasToMerge, alreadyMerged);
                }
            }
        }
        return areasToMerge;
    }

    /**
     * Finds all merge pairs for a specific board at the given position.
     */
    private void findMergePairsForBoard(TileBoard board, int row, int col,
            List<Set<UUID>> areasToMerge,
            Set<UUID> alreadyMerged) {
        for (AreaLocation streetLocation : AreaLocation.getStreetLocations()) {
            if (board.hasAreaLocation(streetLocation)) {
                BoardArea currentArea = board.getAreaByAreaLocation(streetLocation);

                if (shouldProcessArea(currentArea, alreadyMerged)) {
                    processMergePair(currentArea, streetLocation, row, col,
                            areasToMerge, alreadyMerged);
                }
            }
        }
    }

    /**
     * Checks if an area should be processed for merging.
     */
    private boolean shouldProcessArea(BoardArea area, Set<UUID> alreadyMerged) {
        return area != null && !alreadyMerged.contains(area.getAreaId());
    }

    /**
     * Processes a potential merge pair for the current area.
     */
    private void processMergePair(BoardArea currentArea, AreaLocation streetLocation,
            int row, int col,
            List<Set<UUID>> areasToMerge,
            Set<UUID> alreadyMerged) {
        MirrorStreetLocation[] mirrorStreetLocations = TileOperations
                .getNeighbouringStreetLocation(streetLocation);

        for (MirrorStreetLocation mirrorStreetLocation : mirrorStreetLocations) {
            BoardArea neighbourArea = findNeighbourArea(row, col, mirrorStreetLocation);

            if (neighbourArea != null && !alreadyMerged.contains(neighbourArea.getAreaId())) {
                createMergeGroup(currentArea, neighbourArea, areasToMerge, alreadyMerged);
            }
        }
    }

    /**
     * Finds the neighboring area at the given mirror location.
     * 
     * @return The neighboring area, or null if not found or out of bounds
     */
    private BoardArea findNeighbourArea(int row, int col,
            MirrorStreetLocation mirrorStreetLocation) {
        int[] offset = TileOperations.getDirectionOffset(col, row,
                mirrorStreetLocation.direction);

        // Check bounds
        if (!isValidPosition(offset[0], offset[1])) {
            return null;
        }

        TileBoard neighbourBoard = grid[offset[1]][offset[0]];
        if (neighbourBoard == null) {
            return null;
        }

        return neighbourBoard.getAreaByAreaLocation(mirrorStreetLocation.location);
    }

    /**
     * Checks if a position is within grid bounds.
     */
    private boolean isValidPosition(int x, int y) {
        return x >= 0 && y >= 0 && x < gridWidth && y < gridHeight;
    }

    /**
     * Creates a merge group with both areas and marks them as merged.
     */
    private void createMergeGroup(BoardArea currentArea, BoardArea neighbourArea,
            List<Set<UUID>> areasToMerge,
            Set<UUID> alreadyMerged) {
        Set<UUID> mergeGroup = new HashSet<>();
        mergeGroup.add(currentArea.getAreaId());
        mergeGroup.add(neighbourArea.getAreaId());

        areasToMerge.add(mergeGroup);

        // Mark both as merged
        alreadyMerged.add(currentArea.getAreaId());
        alreadyMerged.add(neighbourArea.getAreaId());
    }

    // *****************************************************
    // COMPLETE AND VALID SECTION
    // *****************************************************

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

    public MissionTileEntry[][] toMissionTileEntries() {
        MissionTileEntry[][] entries = new MissionTileEntry[gridHeight][gridWidth];
        for (int row = 0; row < gridHeight; row++) {
            for (int col = 0; col < gridWidth; col++) {
                TileBoard board = grid[row][col];
                if (board != null) {
                    entries[row][col] = new MissionTileEntry(board.getBoardId(),board.getImagePath(), board.getRotation(),row,col);
                } else {
                    entries[row][col] = null;
                }
            }
        }
        return entries;
    }
}
