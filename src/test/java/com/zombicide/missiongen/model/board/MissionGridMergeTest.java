package com.zombicide.missiongen.model.board;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.junit.Before;
import org.junit.Test;

import com.zombicide.missiongen.model.Tile;
import com.zombicide.missiongen.model.areas.BoardAreaConnection;
import com.zombicide.missiongen.services.PersistanceService;

/**
 * Test suite for MissionGrid merge connections functionality.
 * Tests the logic that remaps area UUIDs when areas are merged.
 */
public class MissionGridMergeTest {

    private PersistanceService persistanceService;
    private Tile tile1V;
    private Tile tile3V;
    private MissionGrid grid;

    @Before
    public void setUp() {
        persistanceService = new PersistanceService();

        // Load real tiles
        tile1V = persistanceService.getTile("2ndEdition", "0_original", "1V");
        tile3V = persistanceService.getTile("2ndEdition", "0_original", "3V");

        assertNotNull("Tile 1V should be loaded", tile1V);
        assertNotNull("Tile 3V should be loaded", tile3V);

        // Create 2x1 grid (2 columns, 1 row)
        grid = new MissionGrid(2, 1);
        grid.setBoard(0, 0, tile1V.getBoard()); // 1V on the left
        grid.setBoard(1, 0, tile3V.getBoard()); // 3V on the right
    }

    @Test
    public void testGetAreasToMerge() {
        // Verify we get 3 merge groups (TOP, MIDDLE, BOTTOM street pairs)
        List<Set<UUID>> areasToMerge = grid.getAreasToMerge();

        assertEquals("Should have 3 merge groups", 3, areasToMerge.size());

        for (Set<UUID> mergeGroup : areasToMerge) {
            assertEquals("Each merge group should have 2 areas", 2, mergeGroup.size());
        }
    }

    @Test
    public void testMergedConnectionsCount() {
        // Get areas to merge
        List<Set<UUID>> areasToMerge = grid.getAreasToMerge();

        // Create merge map (new UUID -> old UUIDs)
        Map<UUID, Set<UUID>> mergeMap = new HashMap<>();
        for (Set<UUID> areas : areasToMerge) {
            mergeMap.put(UUID.randomUUID(), areas);
        }

        // Count original connections (excluding open connections)
        int originalConnectionsCount = countNonOpenConnections(tile1V.getBoard()) +
                countNonOpenConnections(tile3V.getBoard());

        // Get merged connections using reflection to access private method
        List<BoardAreaConnection> mergedConnections = getMergedConnectionsViaReflection(grid, mergeMap);

        assertNotNull("Merged connections should not be null", mergedConnections);

        System.out.println("Original connections (non-open): " + originalConnectionsCount);
        System.out.println("Merged connections: " + mergedConnections.size());

        // In 1V-3V case:
        // - Tile 1V has 4 internal connections (non-open)
        // - Tile 3V has 6 internal connections (non-open)
        // - Total: 10 connections
        // - The street areas (TOP, MIDDLE, BOTTOM) get merged, but they don't have
        // connections between them
        // - However, internal areas connected to street areas will have their
        // connections remapped
        // - The count should remain the same (10), but UUIDs change for connections to
        // merged areas

        assertEquals("Connection count should remain the same after merge",
                originalConnectionsCount, mergedConnections.size());
    }

    @Test
    public void testSpecificConnectionRemapping() {
        // This test validates that the connection from indoor area to
        // MIDDLE_LEFT_STREET
        // gets remapped to the new merged UUID

        // Get areas to merge
        List<Set<UUID>> areasToMerge = grid.getAreasToMerge();

        // Find the MIDDLE street merge group
        UUID middleLeftId = UUID.fromString("73e81441-7c2f-4986-a98f-8d36e2457d9c"); // MIDDLE_LEFT from 3V
        UUID middleRightId = UUID.fromString("9fcee129-316e-4aee-89b7-fe2648bfc1b6"); // MIDDLE_RIGHT from 1V

        Set<UUID> middleMergeGroup = null;
        for (Set<UUID> group : areasToMerge) {
            if (group.contains(middleLeftId) && group.contains(middleRightId)) {
                middleMergeGroup = group;
                break;
            }
        }

        assertNotNull("Should find MIDDLE street merge group", middleMergeGroup);
        assertEquals("MIDDLE merge group should have 2 areas", 2, middleMergeGroup.size());

        // Create merge map with known UUID for middle group
        UUID newMiddleUuid = UUID.randomUUID();
        Map<UUID, Set<UUID>> mergeMap = new HashMap<>();
        mergeMap.put(newMiddleUuid, middleMergeGroup);

        // Add other merge groups with random UUIDs
        for (Set<UUID> group : areasToMerge) {
            if (group != middleMergeGroup) {
                mergeMap.put(UUID.randomUUID(), group);
            }
        }

        // Get merged connections
        List<BoardAreaConnection> mergedConnections = getMergedConnectionsViaReflection(grid, mergeMap);

        // Find the connection from indoor area to MIDDLE_LEFT (should now point to
        // newMiddleUuid)
        UUID indoorAreaId = UUID.fromString("89f1201a-bd16-4c4e-abc8-24ed512f38bc"); // INDOOR_DARK from 3V

        boolean foundRemappedConnection = false;
        for (BoardAreaConnection conn : mergedConnections) {
            if (conn.getAreaAId().equals(indoorAreaId) && conn.getAreaBId().equals(newMiddleUuid)) {
                foundRemappedConnection = true;
                System.out.println("Found remapped connection: " + indoorAreaId + " -> " + newMiddleUuid);
                break;
            }
        }

        assertTrue("Should find connection from indoor area to merged MIDDLE street UUID",
                foundRemappedConnection);
    }

    @Test
    public void testMergedConnectionsRemapping() {
        // Get areas to merge
        List<Set<UUID>> areasToMerge = grid.getAreasToMerge();

        // Create merge map
        Map<UUID, Set<UUID>> mergeMap = new HashMap<>();
        Map<UUID, UUID> oldToNewMap = new HashMap<>();

        for (Set<UUID> areas : areasToMerge) {
            UUID newUuid = UUID.randomUUID();
            mergeMap.put(newUuid, areas);
            for (UUID oldUuid : areas) {
                oldToNewMap.put(oldUuid, newUuid);
            }
        }

        // Get merged connections
        List<BoardAreaConnection> mergedConnections = getMergedConnectionsViaReflection(grid, mergeMap);

        // Verify that all connections use either:
        // 1. New merged UUIDs (if the area was merged)
        // 2. Original UUIDs (if the area was not merged)
        Set<UUID> allValidUuids = new HashSet<>();
        allValidUuids.addAll(mergeMap.keySet()); // New merged UUIDs

        // Add original UUIDs that are NOT being merged
        for (int row = 0; row < grid.getGridHeight(); row++) {
            for (int col = 0; col < grid.getGridWidth(); col++) {
                TileBoard board = grid.getBoard(col, row);
                if (board != null) {
                    board.getAreas().forEach(area -> {
                        if (!oldToNewMap.containsKey(area.getAreaId())) {
                            allValidUuids.add(area.getAreaId());
                        }
                    });
                }
            }
        }

        // Verify all connection UUIDs are valid
        for (BoardAreaConnection conn : mergedConnections) {
            assertTrue("Connection areaA should be valid UUID",
                    allValidUuids.contains(conn.getAreaAId()));
            assertTrue("Connection areaB should be valid UUID",
                    allValidUuids.contains(conn.getAreaBId()));
        }
    }

    /**
     * Helper method to count non-open connections in a board
     */
    private int countNonOpenConnections(TileBoard board) {
        int count = 0;
        for (BoardAreaConnection conn : board.getConnections()) {
            if (conn.getAreaBId() != null) { // Not an open connection
                count++;
            }
        }
        return count;
    }

    /**
     * Helper method to access private getMergedConnections method via reflection
     */
    private List<BoardAreaConnection> getMergedConnectionsViaReflection(MissionGrid grid,
            Map<UUID, Set<UUID>> mergeMap) {
        try {
            java.lang.reflect.Method method = MissionFactoryService.class.getDeclaredMethod(
                    "getMergedConnections", MissionGrid.class, Map.class);
            method.setAccessible(true);

            @SuppressWarnings("unchecked")
            List<BoardAreaConnection> result = (List<BoardAreaConnection>) method.invoke(null, grid, mergeMap);
            return result;
        } catch (Exception e) {
            throw new RuntimeException("Failed to invoke getMergedConnections", e);
        }
    }
}
