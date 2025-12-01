package com.zombicide.missiongen.model.board;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.awt.Point;
import java.awt.image.BufferedImage;

import org.junit.Before;
import org.junit.Test;

import com.zombicide.missiongen.model.areas.AreaLocation;
import com.zombicide.missiongen.model.areas.BoardArea;

/**
 * Comprehensive test suite for MissionGrid class.
 * Tests constructor initialization, coordinate system alignment,
 * validation logic, and edge cases.
 */
public class MissionGridTest {

    private BufferedImage testImage;
    private TileBoard testBoard1;
    private TileBoard testBoard2;
    private TileBoard testBoard3;

    @Before
    public void setUp() {
        // Create test image and boards for use in tests
        testImage = new BufferedImage(250, 250, BufferedImage.TYPE_INT_ARGB);
        testBoard1 = new TileBoard("board-1", testImage, 250);
        testBoard2 = new TileBoard("board-2", testImage, 250);
        testBoard3 = new TileBoard("board-3", testImage, 250);
    }

    // ========== Constructor Tests ==========

    @Test
    public void testConstructorWithWidthHeight() {
        int width = 3; // 3 columns
        int height = 2; // 2 rows

        MissionGrid grid = new MissionGrid(width, height);

        assertNotNull("Grid should not be null", grid);
        assertEquals("Grid width should match", width, grid.getGridWidth());
        assertEquals("Grid height should match", height, grid.getGridHeight());
    }

    @Test
    public void testConstructorWithExistingGrid() {
        // Create a 2x3 grid (2 rows, 3 columns)
        TileBoard[][] boardGrid = new TileBoard[2][3];
        boardGrid[0][0] = testBoard1;
        boardGrid[0][1] = testBoard2;
        boardGrid[1][2] = testBoard3;

        MissionGrid grid = new MissionGrid(boardGrid);

        assertNotNull("Grid should not be null", grid);
        assertEquals("Grid width should be 3 (columns)", 3, grid.getGridWidth());
        assertEquals("Grid height should be 2 (rows)", 2, grid.getGridHeight());
    }

    @Test
    public void testConstructorInitializesEmptyGrid() {
        MissionGrid grid = new MissionGrid(2, 2);

        // All cells should be null initially
        assertNull("Cell (0,0) should be null", grid.getBoard(0, 0));
        assertNull("Cell (1,0) should be null", grid.getBoard(1, 0));
        assertNull("Cell (0,1) should be null", grid.getBoard(0, 1));
        assertNull("Cell (1,1) should be null", grid.getBoard(1, 1));
    }

    // ========== Coordinate System Tests ==========

    @Test
    public void testSetAndGetBoard() {
        MissionGrid grid = new MissionGrid(3, 2);

        // Set boards at different positions
        grid.setBoard(0, 0, testBoard1); // x=0 (col 0), y=0 (row 0)
        grid.setBoard(2, 1, testBoard2); // x=2 (col 2), y=1 (row 1)

        // Verify retrieval
        assertEquals("Board at (0,0) should match", testBoard1, grid.getBoard(0, 0));
        assertEquals("Board at (2,1) should match", testBoard2, grid.getBoard(2, 1));
        assertNull("Board at (1,0) should be null", grid.getBoard(1, 0));
    }

    @Test
    public void testCoordinateAlignment() {
        // Verify that x maps to columns and y maps to rows
        MissionGrid grid = new MissionGrid(4, 3); // 4 cols, 3 rows

        // Place boards in corners
        grid.setBoard(0, 0, testBoard1); // Top-left
        grid.setBoard(3, 0, testBoard2); // Top-right
        grid.setBoard(0, 2, testBoard3); // Bottom-left

        // Verify correct placement
        assertEquals("Top-left corner", testBoard1, grid.getBoard(0, 0));
        assertEquals("Top-right corner", testBoard2, grid.getBoard(3, 0));
        assertEquals("Bottom-left corner", testBoard3, grid.getBoard(0, 2));
    }

    @Test
    public void testMultipleBoardPlacements() {
        MissionGrid grid = new MissionGrid(2, 2);

        // Fill entire grid
        grid.setBoard(0, 0, testBoard1);
        grid.setBoard(1, 0, testBoard2);
        grid.setBoard(0, 1, testBoard3);
        grid.setBoard(1, 1, testBoard1);

        // Verify all placements
        assertEquals("Cell (0,0)", testBoard1, grid.getBoard(0, 0));
        assertEquals("Cell (1,0)", testBoard2, grid.getBoard(1, 0));
        assertEquals("Cell (0,1)", testBoard3, grid.getBoard(0, 1));
        assertEquals("Cell (1,1)", testBoard1, grid.getBoard(1, 1));
    }

    @Test
    public void testOverwriteBoard() {
        MissionGrid grid = new MissionGrid(2, 2);

        // Place a board
        grid.setBoard(1, 1, testBoard1);
        assertEquals("Initial board", testBoard1, grid.getBoard(1, 1));

        // Overwrite with different board
        grid.setBoard(1, 1, testBoard2);
        assertEquals("Overwritten board", testBoard2, grid.getBoard(1, 1));
    }

    // ========== Validation Tests ==========

    @Test
    public void testValidateEmptyGrid() {
        MissionGrid grid = new MissionGrid(3, 3);

        // Empty grid should be valid
        assertTrue("Empty grid should be valid", grid.validate());
    }

    @Test
    public void testValidateWithValidBoards() {
        MissionGrid grid = new MissionGrid(2, 2);

        // Create boards without street locations (should be valid)
        grid.setBoard(0, 0, testBoard1);
        grid.setBoard(1, 1, testBoard2);

        assertTrue("Grid with non-connecting boards should be valid", grid.validate());
    }

    @Test
    public void testValidateWithMatchingStreetConnections() {
        MissionGrid grid = new MissionGrid(2, 1);

        // Create two boards with matching street connections
        TileBoard leftBoard = new TileBoard("left-board", testImage, 250);
        TileBoard rightBoard = new TileBoard("right-board", testImage, 250);

        // Add MIDDLE_RIGHT street to left board
        BoardArea leftStreet = new BoardArea(
                java.util.UUID.randomUUID(),
                new Point(200, 100),
                50, 50);
        leftStreet.setAreaLocation(AreaLocation.MIDDLE_RIGHT_STREET);
        leftBoard.addArea(leftStreet);

        // Add MIDDLE_LEFT street to right board (should match)
        BoardArea rightStreet = new BoardArea(
                java.util.UUID.randomUUID(),
                new Point(0, 100),
                50, 50);
        rightStreet.setAreaLocation(AreaLocation.MIDDLE_LEFT_STREET);
        rightBoard.addArea(rightStreet);

        grid.setBoard(0, 0, leftBoard);
        grid.setBoard(1, 0, rightBoard);

        assertTrue("Grid with matching street connections should be valid", grid.validate());
    }

    // ========== Edge Cases ==========

    @Test
    public void testSetBoardNull() {
        MissionGrid grid = new MissionGrid(2, 2);

        // Place a board then remove it
        grid.setBoard(0, 0, testBoard1);
        grid.setBoard(0, 0, null);

        assertNull("Board should be null after removal", grid.getBoard(0, 0));
    }

    @Test
    public void testLargeGrid() {
        int width = 5;
        int height = 5;
        MissionGrid grid = new MissionGrid(width, height);

        assertNotNull("Large grid should be created", grid);
        assertEquals("Width should match", width, grid.getGridWidth());
        assertEquals("Height should match", height, grid.getGridHeight());

        // Test corners of large grid
        grid.setBoard(0, 0, testBoard1);
        grid.setBoard(4, 4, testBoard2);

        assertEquals("Top-left of large grid", testBoard1, grid.getBoard(0, 0));
        assertEquals("Bottom-right of large grid", testBoard2, grid.getBoard(4, 4));
    }

    @Test
    public void testSingleCellGrid() {
        MissionGrid grid = new MissionGrid(1, 1);

        assertEquals("Width should be 1", 1, grid.getGridWidth());
        assertEquals("Height should be 1", 1, grid.getGridHeight());

        grid.setBoard(0, 0, testBoard1);
        assertEquals("Single cell should contain board", testBoard1, grid.getBoard(0, 0));
    }

    @Test
    public void testRectangularGrids() {
        // Test various rectangular configurations
        MissionGrid grid1 = new MissionGrid(4, 2); // Wide
        assertEquals("Wide grid width", 4, grid1.getGridWidth());
        assertEquals("Wide grid height", 2, grid1.getGridHeight());

        MissionGrid grid2 = new MissionGrid(2, 4); // Tall
        assertEquals("Tall grid width", 2, grid2.getGridWidth());
        assertEquals("Tall grid height", 4, grid2.getGridHeight());
    }

    @Test
    public void testValidateAfterBoardRemoval() {
        MissionGrid grid = new MissionGrid(2, 2);

        grid.setBoard(0, 0, testBoard1);
        assertTrue("Grid should be valid with one board", grid.validate());

        grid.setBoard(0, 0, null);
        assertTrue("Grid should be valid after board removal", grid.validate());
    }
}
