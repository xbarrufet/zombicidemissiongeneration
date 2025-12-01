package com.zombicide.missiongen.model.board;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.junit.Test;

import com.zombicide.missiongen.model.areas.AreaLocation;
import com.zombicide.missiongen.model.areas.BoardArea;
import com.zombicide.missiongen.model.areas.BoardAreaConnection;
import com.zombicide.missiongen.model.areas.DoorDirection;

import java.lang.reflect.Method;

public class MissionGridEdgeConnectionTest {

    @Test
    public void testEdgeConnectionToStreet() throws Exception {
        // Setup:
        // Tile 1 (Left): Indoor area with EAST_CENTER connection
        // Tile 2 (Right): Street area at MIDDLE_LEFT_STREET

        // --- Tile 1 ---
        TileBoard board1 = new TileBoard("board1", new BufferedImage(250, 250, BufferedImage.TYPE_INT_ARGB), 250);
        UUID indoorAreaId = UUID.randomUUID();
        BoardArea indoorArea = new BoardArea(indoorAreaId, new Point(100, 100), 50, 50); // Indoor
        board1.addArea(indoorArea);

        // Add edge connection: Indoor -> EAST_CENTER
        BoardAreaConnection edgeConn = new BoardAreaConnection(indoorAreaId, DoorDirection.EAST_CENTER);
        board1.addConnection(edgeConn);

        // --- Tile 2 ---
        TileBoard board2 = new TileBoard("board2", new BufferedImage(250, 250, BufferedImage.TYPE_INT_ARGB), 250);
        UUID streetAreaId = UUID.randomUUID();
        BoardArea streetArea = new BoardArea(streetAreaId, new Point(0, 100), 50, 50, AreaLocation.MIDDLE_LEFT_STREET);
        board2.addArea(streetArea);

        // --- Grid ---
        MissionGrid grid = new MissionGrid(2, 1);
        grid.setBoard(0, 0, board1);
        grid.setBoard(1, 0, board2);

        // --- Invoke getMergedConnections via Reflection ---
        Method method = MissionFactoryService.class.getDeclaredMethod("getMergedConnections", MissionGrid.class,
                Map.class);
        method.setAccessible(true);

        Map<UUID, Set<UUID>> emptyMergeMap = new HashMap<>();
        @SuppressWarnings("unchecked")
        List<BoardAreaConnection> connections = (List<BoardAreaConnection>) method.invoke(null, grid, emptyMergeMap);

        // --- Verify ---
        // Should have 1 connection: Indoor (Tile 1) <-> Street (Tile 2)
        assertEquals("Should have 1 connection", 1, connections.size());

        BoardAreaConnection resultConn = connections.get(0);
        assertEquals("Area A should be indoor area", indoorAreaId, resultConn.getAreaAId());
        assertEquals("Area B should be street area", streetAreaId, resultConn.getAreaBId());
    }

    @Test
    public void testEdgeConnectionToIndoor() throws Exception {
        // Setup:
        // Tile 1 (Top): Indoor area 1 with SOUTH_CENTER connection
        // Tile 2 (Bottom): Indoor area 2 with NORTH_CENTER connection

        // --- Tile 1 ---
        TileBoard board1 = new TileBoard("board1", new BufferedImage(250, 250, BufferedImage.TYPE_INT_ARGB), 250);
        UUID indoor1Id = UUID.randomUUID();
        BoardArea indoor1 = new BoardArea(indoor1Id, new Point(100, 200), 50, 50);
        board1.addArea(indoor1);

        // Edge connection: Indoor 1 -> SOUTH_CENTER
        board1.addConnection(new BoardAreaConnection(indoor1Id, DoorDirection.SOUTH_CENTER));

        // --- Tile 2 ---
        TileBoard board2 = new TileBoard("board2", new BufferedImage(250, 250, BufferedImage.TYPE_INT_ARGB), 250);
        UUID indoor2Id = UUID.randomUUID();
        BoardArea indoor2 = new BoardArea(indoor2Id, new Point(100, 0), 50, 50);
        board2.addArea(indoor2);

        // Edge connection: Indoor 2 -> NORTH_CENTER (Matches SOUTH_CENTER)
        board2.addConnection(new BoardAreaConnection(indoor2Id, DoorDirection.NORTH_CENTER));

        // --- Grid ---
        MissionGrid grid = new MissionGrid(1, 2);
        grid.setBoard(0, 0, board1); // Top
        grid.setBoard(0, 1, board2); // Bottom

        // --- Invoke getMergedConnections via Reflection ---
        Method method = MissionFactoryService.class.getDeclaredMethod("getMergedConnections", MissionGrid.class,
                Map.class);
        method.setAccessible(true);

        Map<UUID, Set<UUID>> emptyMergeMap = new HashMap<>();
        @SuppressWarnings("unchecked")
        List<BoardAreaConnection> connections = (List<BoardAreaConnection>) method.invoke(null, grid, emptyMergeMap);

        // --- Verify ---
        // Should have 2 connections:
        // 1. Indoor 1 -> Indoor 2 (processed from Board 1)
        // 2. Indoor 2 -> Indoor 1 (processed from Board 2)
        // Note: The current logic processes connections from each board independently.
        // Ideally we might want to deduplicate, but for now let's check if both are
        // created.

        assertEquals("Should have 2 connections (one from each side)", 2, connections.size());

        boolean found1to2 = false;
        boolean found2to1 = false;

        for (BoardAreaConnection conn : connections) {
            if (conn.getAreaAId().equals(indoor1Id) && conn.getAreaBId().equals(indoor2Id)) {
                found1to2 = true;
            } else if (conn.getAreaAId().equals(indoor2Id) && conn.getAreaBId().equals(indoor1Id)) {
                found2to1 = true;
            }
        }

        assertTrue("Should find connection from Indoor 1 to Indoor 2", found1to2);
        assertTrue("Should find connection from Indoor 2 to Indoor 1", found2to1);
    }
}
