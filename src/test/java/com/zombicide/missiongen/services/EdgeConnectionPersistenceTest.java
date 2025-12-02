package com.zombicide.missiongen.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zombicide.missiongen.DTO.BoardAreaConnectionDTO;
import com.zombicide.missiongen.DTO.TileDTO;
import com.zombicide.missiongen.model.Tile;
import com.zombicide.missiongen.model.areas.BoardAreaConnection;
import com.zombicide.missiongen.model.areas.Direction;
import com.zombicide.missiongen.model.areas.DoorDirection;

public class EdgeConnectionPersistenceTest {

    private PersistanceService service;
    private File testDataDir;

    @Before
    public void setUp() throws IOException {
        service = new PersistanceService();
        testDataDir = new File("test-data-temp-edge");
        System.setProperty("folders.editions", "test-data-temp-edge");
        createTestDirectoryStructure();
    }

    @After
    public void tearDown() {
        deleteDirectory(new File("test-data-temp-edge"));
        System.clearProperty("folders.editions");
    }

    private void createTestDirectoryStructure() throws IOException {
        File edition1 = new File(testDataDir, "Edition1");
        File collection1 = new File(edition1, "Collection1");
        File imagesDir = new File(collection1, "tileImages");
        File tilesDir = new File(collection1, "tiles");

        imagesDir.mkdirs();
        tilesDir.mkdirs();
    }

    private void deleteDirectory(File directory) {
        if (directory.exists()) {
            File[] files = directory.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isDirectory()) {
                        deleteDirectory(file);
                    } else {
                        file.delete();
                    }
                }
            }
            directory.delete();
        }
    }

    @Test
    public void testPersistEdgeConnection() throws IOException {
        // 1. Create a Tile
        File imagesDir = new File(testDataDir, "Edition1/Collection1/tileImages");
        File testImage = new File(imagesDir, "test-edge.png");
        testImage.createNewFile();

        Tile tile = new Tile("Edition1", "Collection1", testImage.getAbsolutePath(), "test-edge");

        // 2. Add an edge connection (Area UUID -> NORTH)
        java.util.UUID areaId = java.util.UUID.randomUUID();
        BoardAreaConnection edgeConnection = new BoardAreaConnection(areaId, DoorDirection.NORTH_LEFT);
        tile.getBoard().addConnection(edgeConnection);

        // 3. Persist the tile
        service.persistTile(tile);

        // 4. Read back the JSON
        File tilesDir = new File(testDataDir, "Edition1/Collection1/tiles");
        File jsonFile = new File(tilesDir, "test-edge.json");
        assertTrue("JSON file should exist", jsonFile.exists());

        ObjectMapper mapper = new ObjectMapper();
        TileDTO loadedDTO = mapper.readValue(jsonFile, TileDTO.class);

        // 5. Verify the connection
        assertNotNull("Connections should not be null", loadedDTO.connections);
        assertEquals("Should have 1 connection", 1, loadedDTO.connections.size());

        BoardAreaConnectionDTO connDTO = loadedDTO.connections.get(0);
        assertEquals("AreaA should match", areaId.toString(), connDTO.areaA);
        assertEquals("AreaB should be null", null, connDTO.areaB);
        assertEquals("Direction should be NORTH_LEFT", "NORTH_LEFT", connDTO.direction);
    }
}
