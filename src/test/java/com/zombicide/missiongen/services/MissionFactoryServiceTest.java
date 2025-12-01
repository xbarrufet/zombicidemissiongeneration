package com.zombicide.missiongen.services;

import static org.junit.Assert.assertNotNull;

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
}
