package com.zombicide.missiongen.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.awt.image.BufferedImage;

import org.junit.Before;
import org.junit.Test;

import com.zombicide.missiongen.model.board.MissionFactoryService;
import com.zombicide.missiongen.model.board.MissionGrid;

public class MissionFactoryServiceTest {

    @Before
    public void setUp() {
        // MissionFactoryService now uses static methods, no need to instantiate
    }

    @Test
    public void testCreateMissionBoard() {
        // Create a simple MissionGrid with some tiles
        int width = 2; // 2 columns
        int height = 2; // 2 rows

        MissionGrid grid = new MissionGrid(width, height);

        // Create some test TileBoards
        java.awt.image.BufferedImage testImage = new BufferedImage(250, 250, BufferedImage.TYPE_INT_ARGB);

        com.zombicide.missiongen.model.board.TileBoard board1 = new com.zombicide.missiongen.model.board.TileBoard(
                "board-1", testImage, 250);
        com.zombicide.missiongen.model.board.TileBoard board2 = new com.zombicide.missiongen.model.board.TileBoard(
                "board-2", testImage, 250);

        // Place boards in grid
        grid.setBoard(0, 0, board1); // x=0, y=0
        grid.setBoard(1, 1, board2); // x=1, y=1

        // Execute - create mission board from grid
        com.zombicide.missiongen.model.board.MissionBoard missionBoard = MissionFactoryService.createMissionBoard(grid);

        // Verify
        assertNotNull("Mission board should not be null", missionBoard);
        assertNotNull("Mission board image should not be null", missionBoard.getImage());

        // Verify dimensions (2x2 grid of 250x250 tiles)
        // Note: ConfigLoader needs to be properly configured for this test
        // For now, we just verify the board was created
    }

    @Test
    public void testCreateMissionBoardWithWidthHeight() {
        // Test the convenience method that creates an empty grid
        int width = 3;
        int height = 2;

        com.zombicide.missiongen.model.board.MissionBoard missionBoard = MissionFactoryService.createMissionBoard(width,
                height);

        assertNotNull("Mission board should not be null", missionBoard);
        assertNotNull("Mission board image should not be null", missionBoard.getImage());
    }

    @Test
    public void testCreateMissionBoardWith1Vand3VTiles() {
        // Create a 2x1 grid (2 columns, 1 row) with real tiles 1V and 3V
        int width = 2; // 2 columns
        int height = 1; // 1 row

        MissionGrid grid = new MissionGrid(width, height);

        // Load real tiles using PersistanceService
        com.zombicide.missiongen.services.PersistanceService persistanceService = new com.zombicide.missiongen.services.PersistanceService();

        com.zombicide.missiongen.model.Tile tile1V = persistanceService.getTile("2ndEdition", "0_original", "1V");
        com.zombicide.missiongen.model.Tile tile3V = persistanceService.getTile("2ndEdition", "0_original", "3V");

        assertNotNull("Tile 1V should be loaded", tile1V);
        assertNotNull("Tile 3V should be loaded", tile3V);

        // Get TileBoards from tiles
        com.zombicide.missiongen.model.board.TileBoard board1V = tile1V.getBoard();
        com.zombicide.missiongen.model.board.TileBoard board3V = tile3V.getBoard();

        assertNotNull("TileBoard 1V should not be null", board1V);
        assertNotNull("TileBoard 3V should not be null", board3V);

        // Place tiles in grid: 1V at (0,0), 3V at (1,0)
        grid.setBoard(0, 0, board1V); // x=0 (col 0), y=0 (row 0)
        grid.setBoard(1, 0, board3V); // x=1 (col 1), y=0 (row 0)

        // Verify grid is complete and valid
        assertTrue("Grid should be complete and valid", grid.isCompleteAndValid());

        // Verify areas to merge
        java.util.List<java.util.Set<java.util.UUID>> areasToMerge = grid.getAreasToMerge();

        assertNotNull("Areas to merge should not be null", areasToMerge);
        // Should have 3 groups: TOP_RIGHT↔TOP_LEFT, MIDDLE_RIGHT↔MIDDLE_LEFT,
        // BOTTOM_RIGHT↔BOTTOM_LEFT
        assertEquals("Should have 3 groups of areas to merge", 3, areasToMerge.size());

        // Each group should have exactly 2 areas (one from each adjacent tile)
        int groupIndex = 0;
        for (java.util.Set<java.util.UUID> mergeGroup : areasToMerge) {
            System.out.println("Merge group " + groupIndex + " has " + mergeGroup.size() + " areas");
            assertEquals("Each merge group should have exactly 2 areas", 2, mergeGroup.size());
            groupIndex++;
        }

        // Create mission board from grid
        com.zombicide.missiongen.model.board.MissionBoard missionBoard = MissionFactoryService.createMissionBoard(grid);

        // Verify mission board was created
        assertNotNull("Mission board should not be null", missionBoard);
        assertNotNull("Mission board image should not be null", missionBoard.getImage());

        // Verify dimensions (2 tiles of 250x250 each = 500x250)
        int expectedWidth = 2 * 250; // 2 columns * tile width
        int expectedHeight = 1 * 250; // 1 row * tile height

        assertEquals("Mission board width should be 500", expectedWidth, missionBoard.getWidth());
        assertEquals("Mission board height should be 250", expectedHeight, missionBoard.getHeight());

        // Verify the image dimensions match
        assertEquals("Image width should match board width", expectedWidth, missionBoard.getImage().getWidth(null));
        assertEquals("Image height should match board height", expectedHeight, missionBoard.getImage().getHeight(null));
    }
}
