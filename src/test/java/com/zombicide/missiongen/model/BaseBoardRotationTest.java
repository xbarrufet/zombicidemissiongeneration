package com.zombicide.missiongen.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import com.zombicide.missiongen.model.areas.AreaLocation;
import com.zombicide.missiongen.model.areas.BoardArea;
import com.zombicide.missiongen.model.areas.BoardAreaConnection;
import com.zombicide.missiongen.model.areas.Direction;
import com.zombicide.missiongen.model.areas.DoorDirection;
import com.zombicide.missiongen.model.board.TileBoard;
import com.zombicide.missiongen.services.PersistanceService;

/**
 * Test suite for BaseBoard rotation functionality.
 * Uses real tile 1V to validate rotation of areas, connections, and image.
 */
public class BaseBoardRotationTest {

    private PersistanceService persistanceService;
    private Tile tile1V;
    private TileBoard originalBoard;

    @Before
    public void setUp() {
        persistanceService = new PersistanceService();
        tile1V = persistanceService.getTile("2ndEdition", "0_original", "1V");
        assertNotNull("Tile 1V should be loaded", tile1V);

        // Create a copy of the board for testing
        originalBoard = new TileBoard(tile1V.getBoard());
    }

    @Test
    public void testRotateStreetLocations() {
        // Get original street locations
        boolean hasTopRight = originalBoard.hasAreaLocation(AreaLocation.TOP_RIGHT_STREET);
        boolean hasMiddleRight = originalBoard.hasAreaLocation(AreaLocation.MIDDLE_RIGHT_STREET);
        boolean hasBottomRight = originalBoard.hasAreaLocation(AreaLocation.BOTTOM_RIGHT_STREET);

        // Rotate the board
        originalBoard.rotate();

        // After rotation, street locations should have rotated 90° clockwise:
        // TOP_RIGHT -> BOTTOM_RIGHT
        // MIDDLE_RIGHT -> BOTTOM_MIDDLE
        // BOTTOM_RIGHT -> BOTTOM_LEFT

        if (hasTopRight) {
            assertTrue("TOP_RIGHT should become BOTTOM_RIGHT after rotation",
                    originalBoard.hasAreaLocation(AreaLocation.BOTTOM_RIGHT_STREET));
        }

        if (hasMiddleRight) {
            assertTrue("MIDDLE_RIGHT should become BOTTOM_MIDDLE after rotation",
                    originalBoard.hasAreaLocation(AreaLocation.BOTTOM_MIDDLE_STREET));
        }

        if (hasBottomRight) {
            assertTrue("BOTTOM_RIGHT should become BOTTOM_LEFT after rotation",
                    originalBoard.hasAreaLocation(AreaLocation.BOTTOM_LEFT_STREET));
        }
    }

    @Test
    public void testRotateAreaCoordinates() {
        // Get an area before rotation
        BoardArea originalArea = originalBoard.getAreas().get(0);
        int originalX = originalArea.getTopLeft().x;
        int originalY = originalArea.getTopLeft().y;
        int originalWidth = originalArea.getWidth();
        int originalHeight = originalArea.getHeight();

        int boardWidth = originalBoard.getWidth();

        // Rotate the board
        originalBoard.rotate();

        // Find the same area after rotation (same index)
        BoardArea rotatedArea = originalBoard.getAreas().get(0);

        // After 90° clockwise rotation:
        // new_x = boardHeight - (original_y + original_height)
        // new_y = original_x
        // width and height swap

        int expectedX = originalBoard.getHeight() - (originalY + originalHeight);
        int expectedY = originalX;

        assertEquals("X coordinate should be rotated correctly", expectedX, rotatedArea.getTopLeft().x);
        assertEquals("Y coordinate should be rotated correctly", expectedY, rotatedArea.getTopLeft().y);
        assertEquals("Width and height should swap", originalHeight, rotatedArea.getWidth());
        assertEquals("Width and height should swap", originalWidth, rotatedArea.getHeight());
    }

    @Test
    public void testRotateEdgeConnections() {
        // Count edge connections before rotation and their directions
        int northConnections = 0;
        int westConnections = 0;

        for (BoardAreaConnection conn : originalBoard.getConnections()) {
            if (conn.isEdgeConnection()) {
                if (conn.getDirection() == DoorDirection.NORTH_CENTER) {
                    northConnections++;
                } else if (conn.getDirection() == DoorDirection.WEST_CENTER) {
                    westConnections++;
                }
            }
        }

        // Rotate the board
        originalBoard.rotate();

        // After rotation, directions should rotate 90° clockwise:
        // NORTH_CENTER -> EAST_CENTER
        // WEST_CENTER -> NORTH_CENTER

        int eastConnections = 0;
        int northConnectionsAfter = 0;

        for (BoardAreaConnection conn : originalBoard.getConnections()) {
            if (conn.isEdgeConnection()) {
                if (conn.getDirection() == DoorDirection.EAST_CENTER) {
                    eastConnections++;
                } else if (conn.getDirection() == DoorDirection.NORTH_CENTER) {
                    northConnectionsAfter++;
                }
            }
        }

        assertEquals("NORTH connections should become EAST", northConnections, eastConnections);
        assertEquals("WEST connections should become NORTH", westConnections, northConnectionsAfter);
    }

    @Test
    public void testRotateImageDimensions() {
        int originalWidth = originalBoard.getWidth();
        int originalHeight = originalBoard.getHeight();

        // For square tiles, width should equal height
        assertEquals("Tile 1V should be square", originalWidth, originalHeight);

        // Rotate the board
        originalBoard.rotate();

        // After rotation, dimensions should remain the same for square tiles
        assertEquals("Width should remain the same for square tile", originalWidth, originalBoard.getWidth());
        assertEquals("Height should remain the same for square tile", originalHeight, originalBoard.getHeight());

        // Image should not be null
        assertNotNull("Image should not be null after rotation", originalBoard.getImage());
    }

    @Test
    public void testMultipleRotations() {
        // Store original state
        int originalAreaCount = originalBoard.getAreas().size();
        int originalConnectionCount = originalBoard.getConnections().size();

        // Rotate 4 times (should return to original orientation)
        for (int i = 0; i < 4; i++) {
            originalBoard.rotate();
        }

        // Area and connection counts should remain the same
        assertEquals("Area count should remain the same after 4 rotations",
                originalAreaCount, originalBoard.getAreas().size());
        assertEquals("Connection count should remain the same after 4 rotations",
                originalConnectionCount, originalBoard.getConnections().size());
    }

    @Test
    public void testRotatePreservesNonEdgeConnections() {
        // Count non-edge connections before rotation
        int nonEdgeConnectionsBefore = 0;
        for (BoardAreaConnection conn : originalBoard.getConnections()) {
            if (!conn.isEdgeConnection()) {
                nonEdgeConnectionsBefore++;
            }
        }

        // Rotate the board
        originalBoard.rotate();

        // Count non-edge connections after rotation
        int nonEdgeConnectionsAfter = 0;
        for (BoardAreaConnection conn : originalBoard.getConnections()) {
            if (!conn.isEdgeConnection()) {
                nonEdgeConnectionsAfter++;
            }
        }

        // Non-edge connections should remain the same (only their area positions
        // change)
        assertEquals("Non-edge connection count should remain the same",
                nonEdgeConnectionsBefore, nonEdgeConnectionsAfter);
    }
}
