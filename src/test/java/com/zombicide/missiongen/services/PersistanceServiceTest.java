package com.zombicide.missiongen.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class PersistanceServiceTest {

    private PersistanceService service;
    private File testDataDir;

    @Before
    public void setUp() throws IOException {
        service = new PersistanceService();

        // Use a temporary test directory instead of the real assets folder
        testDataDir = new File("test-data-temp/editions");

        // Temporarily override the config for testing
        System.setProperty("folders.editions", "test-data-temp/editions");

        createTestDirectoryStructure();
    }

    @After
    public void tearDown() {
        // Clean up test directories
        File testRoot = new File("test-data-temp");
        deleteDirectory(testRoot);

        // Clear system property
        System.clearProperty("folders.editions");
    }

    private void createTestDirectoryStructure() throws IOException {
        // Create structure: assets/editions/Edition1/Collection1/{tileImages,tiles}
        File edition1 = new File(testDataDir, "Edition1");
        File edition2 = new File(testDataDir, "Edition2");

        File collection1 = new File(edition1, "Collection1");
        File collection2 = new File(edition1, "Collection2");

        File imagesDir1 = new File(collection1, "tileImages");
        File tilesDir1 = new File(collection1, "tiles");

        File imagesDir2 = new File(collection2, "tileImages");
        File tilesDir2 = new File(collection2, "tiles");

        // Create all directories
        imagesDir1.mkdirs();
        tilesDir1.mkdirs();
        imagesDir2.mkdirs();
        tilesDir2.mkdirs();
        edition2.mkdirs();

        // Create some test files
        new File(imagesDir1, "tile1.png").createNewFile();
        new File(imagesDir1, "tile2.jpg").createNewFile();
        new File(imagesDir1, "tile3.png").createNewFile();

        new File(tilesDir1, "tile1.json").createNewFile();
        new File(tilesDir1, "tile2.json").createNewFile();

        new File(imagesDir2, "tile4.png").createNewFile();
        new File(tilesDir2, "tile3.json").createNewFile();
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
    public void testGetEditions() {
        List<String> editions = service.getEditions();

        assertNotNull("Editions list should not be null", editions);
        assertEquals("Should find 2 editions", 2, editions.size());
        assertTrue("Should contain Edition1", editions.contains("Edition1"));
        assertTrue("Should contain Edition2", editions.contains("Edition2"));
    }

    @Test
    public void testGetEditionsWhenDirectoryDoesNotExist() {
        // Delete the test directory
        deleteDirectory(testDataDir);

        List<String> editions = service.getEditions();

        assertNotNull("Editions list should not be null", editions);
        assertTrue("Should return empty list when directory does not exist", editions.isEmpty());
    }

    @Test
    public void testGetCollections() {
        List<String> collections = service.getCollections("Edition1");

        assertNotNull("Collections list should not be null", collections);
        assertEquals("Should find 2 collections", 2, collections.size());
        assertTrue("Should contain Collection1", collections.contains("Collection1"));
        assertTrue("Should contain Collection2", collections.contains("Collection2"));
    }

    @Test
    public void testGetCollectionsWithNullEdition() {
        List<String> collections = service.getCollections(null);

        assertNotNull("Collections list should not be null", collections);
        assertTrue("Should return empty list for null edition", collections.isEmpty());
    }

    @Test
    public void testGetCollectionsWithEmptyEdition() {
        List<String> collections = service.getCollections("");

        assertNotNull("Collections list should not be null", collections);
        assertTrue("Should return empty list for empty edition", collections.isEmpty());
    }

    @Test
    public void testGetCollectionsWithNonExistentEdition() {
        List<String> collections = service.getCollections("NonExistentEdition");

        assertNotNull("Collections list should not be null", collections);
        assertTrue("Should return empty list for non-existent edition", collections.isEmpty());
    }

    @Test
    public void testGetTileImages() {
        List<String> images = service.getTileImages("Edition1", "Collection1");

        assertNotNull("Images list should not be null", images);
        assertEquals("Should find 3 images", 3, images.size());
        assertTrue("Should contain tile1.png", images.contains("tile1.png"));
        assertTrue("Should contain tile2.jpg", images.contains("tile2.jpg"));
        assertTrue("Should contain tile3.png", images.contains("tile3.png"));
    }

    @Test
    public void testGetTileImagesWithNullParameters() {
        List<String> images = service.getTileImages(null, "Collection1");
        assertTrue("Should return empty list for null edition", images.isEmpty());

        images = service.getTileImages("Edition1", null);
        assertTrue("Should return empty list for null collection", images.isEmpty());
    }

    @Test
    public void testGetTileImagesWithEmptyParameters() {
        List<String> images = service.getTileImages("", "Collection1");
        assertTrue("Should return empty list for empty edition", images.isEmpty());

        images = service.getTileImages("Edition1", "");
        assertTrue("Should return empty list for empty collection", images.isEmpty());
    }

    @Test
    public void testGetTileImagesWithNonExistentPath() {
        List<String> images = service.getTileImages("NonExistent", "Collection");

        assertNotNull("Images list should not be null", images);
        assertTrue("Should return empty list for non-existent path", images.isEmpty());
    }

    @Test
    public void testGetTiles() {
        List<String> tiles = service.getTiles("Edition1", "Collection1");

        assertNotNull("Tiles list should not be null", tiles);
        assertEquals("Should find 2 tiles", 2, tiles.size());
        assertTrue("Should contain tile1", tiles.contains("tile1"));
        assertTrue("Should contain tile2", tiles.contains("tile2"));
    }

    @Test
    public void testGetTilesWithNullParameters() {
        List<String> tiles = service.getTiles(null, "Collection1");
        assertTrue("Should return empty list for null edition", tiles.isEmpty());

        tiles = service.getTiles("Edition1", null);
        assertTrue("Should return empty list for null collection", tiles.isEmpty());
    }

    @Test
    public void testGetTilesWithEmptyParameters() {
        List<String> tiles = service.getTiles("", "Collection1");
        assertTrue("Should return empty list for empty edition", tiles.isEmpty());

        tiles = service.getTiles("Edition1", "");
        assertTrue("Should return empty list for empty collection", tiles.isEmpty());
    }

    @Test
    public void testGetTilesWithNonExistentPath() {
        List<String> tiles = service.getTiles("NonExistent", "Collection");

        assertNotNull("Tiles list should not be null", tiles);
        assertTrue("Should return empty list for non-existent path", tiles.isEmpty());
    }

    @Test
    public void testResultsAreSorted() {
        List<String> images = service.getTileImages("Edition1", "Collection1");

        // Verify the list is sorted alphabetically
        for (int i = 0; i < images.size() - 1; i++) {
            assertTrue("Results should be sorted alphabetically",
                    images.get(i).compareTo(images.get(i + 1)) <= 0);
        }
    }

    @Test
    public void testTilesToGenerate() {
        int result = service.getTilesToGenerate("Edition1", "Collection1");
        assertTrue("Should return count > 0 if there are tiles to generate", result > 0);
    }

    @Test
    public void testTilesToGenerateWithNullParameters() {
        int result = service.getTilesToGenerate(null, "Collection1");
        assertEquals("Should return 0 for null edition", 0, result);

        result = service.getTilesToGenerate("Edition1", null);
        assertEquals("Should return 0 for null collection", 0, result);
    }

    @Test
    public void testTilesToGenerateWithEmptyParameters() {
        int result = service.getTilesToGenerate("", "Collection1");
        assertEquals("Should return 0 for empty edition", 0, result);

        result = service.getTilesToGenerate("Edition1", "");
        assertEquals("Should return 0 for empty collection", 0, result);
    }

    @Test
    public void testTilesToGenerateWithNonExistentPath() {
        int result = service.getTilesToGenerate("NonExistent", "Collection");
        assertEquals("Should return 0 for non-existent path", 0, result);
    }

    @Test
    public void testTilesToGenerateWithEmptyCollections() {
        // Edition2 has no collections, so there are no tiles to generate
        int result = service.getTilesToGenerate("Edition2", "NonExistent");
        assertEquals("Should return 0 for empty collections", 0, result);
    }

    @Test
    public void testTilesToGenerateWithNonExistentCollections() {
        int result = service.getTilesToGenerate("Edition1", "NonExistent");
        assertEquals("Should return 0 for non-existent collections", 0, result);
    }

    @Test
    public void testTilesToGenerateWithEmptyTiles() {
        // Collection2 has 1 image (tile4.png) and 1 tile (tile3.json)
        // They don't match, so there are tiles to generate
        // To test "no tiles to generate", we need matching image and tile
        int result = service.getTilesToGenerate("Edition1", "Collection2");
        assertEquals("Should return 1 when image and tile names don't match", 1, result);
    }

    @Test
    public void testPersistTile() throws IOException {
        // Create a test image first
        File imagesDir = new File(testDataDir, "Edition1/Collection1/tileImages");
        File testImage = new File(imagesDir, "test-persist.png");
        java.awt.image.BufferedImage bufferedImage = new java.awt.image.BufferedImage(100, 100,
                java.awt.image.BufferedImage.TYPE_INT_RGB);
        javax.imageio.ImageIO.write(bufferedImage, "png", testImage);

        // Create a tile
        com.zombicide.missiongen.model.Tile tile = new com.zombicide.missiongen.model.Tile(
                "Edition1", "Collection1", testImage.getAbsolutePath(), "test-persist");

        // Persist the tile
        service.persistTile(tile);

        // Verify the JSON file was created
        File tilesDir = new File(testDataDir, "Edition1/Collection1/tiles");
        File jsonFile = new File(tilesDir, "test-persist.json");

        assertTrue("JSON file should be created", jsonFile.exists());
        assertTrue("JSON file should not be empty", jsonFile.length() > 0);

        // Verify the tile is now in the list of tiles
        List<String> tiles = service.getTiles("Edition1", "Collection1");
        assertTrue("Tiles list should contain test-persist", tiles.contains("test-persist"));
    }

    @Test
    public void testPersistTileContent() throws IOException {
        // Create a test image
        File imagesDir = new File(testDataDir, "Edition1/Collection1/tileImages");
        File testImage = new File(imagesDir, "test-content.png");
        java.awt.image.BufferedImage bufferedImage = new java.awt.image.BufferedImage(100, 100,
                java.awt.image.BufferedImage.TYPE_INT_RGB);
        javax.imageio.ImageIO.write(bufferedImage, "png", testImage);

        // Create and persist a tile
        com.zombicide.missiongen.model.Tile tile = new com.zombicide.missiongen.model.Tile(
                "Edition1", "Collection1", testImage.getAbsolutePath(), "test-content");
        service.persistTile(tile);

        // Read and verify JSON content
        File jsonFile = new File(testDataDir, "Edition1/Collection1/tiles/test-content.json");
        com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
        com.zombicide.missiongen.DTO.TileDTO loadedDTO = mapper.readValue(jsonFile,
                com.zombicide.missiongen.DTO.TileDTO.class);

        assertEquals("Edition should match", "Edition1", loadedDTO.edition);
        assertEquals("Collection should match", "Collection1", loadedDTO.collection);
        assertEquals("Tile name should match", "test-content", loadedDTO.tileName);
        assertNotNull("Area list should not be null", loadedDTO.areas);
        assertNotNull("Connections list should not be null", loadedDTO.connections);
    }

    @Test
    public void testGenerateTiles() throws IOException {
        // Create some test images without corresponding tiles
        File imagesDir = new File(testDataDir, "Edition1/Collection1/tileImages");
        File testImage1 = new File(imagesDir, "generate-test-1.png");
        File testImage2 = new File(imagesDir, "generate-test-2.png");

        java.awt.image.BufferedImage bufferedImage = new java.awt.image.BufferedImage(100, 100,
                java.awt.image.BufferedImage.TYPE_INT_RGB);
        javax.imageio.ImageIO.write(bufferedImage, "png", testImage1);
        javax.imageio.ImageIO.write(bufferedImage, "png", testImage2);

        // Generate tiles
        service.generateTiles("Edition1", "Collection1");

        // Verify tiles were created
        List<String> tiles = service.getTiles("Edition1", "Collection1");
        assertTrue("Should contain generate-test-1", tiles.contains("generate-test-1"));
        assertTrue("Should contain generate-test-2", tiles.contains("generate-test-2"));

        // Verify JSON files exist
        File tilesDir = new File(testDataDir, "Edition1/Collection1/tiles");
        File json1 = new File(tilesDir, "generate-test-1.json");
        File json2 = new File(tilesDir, "generate-test-2.json");

        assertTrue("generate-test-1.json should exist", json1.exists());
        assertTrue("generate-test-2.json should exist", json2.exists());
    }

    @Test
    public void testGenerateTilesDoesNotOverwriteExisting() throws IOException {
        // Create a test image and tile
        File imagesDir = new File(testDataDir, "Edition1/Collection1/tileImages");
        File testImage = new File(imagesDir, "existing-tile.png");
        java.awt.image.BufferedImage bufferedImage = new java.awt.image.BufferedImage(100, 100,
                java.awt.image.BufferedImage.TYPE_INT_RGB);
        javax.imageio.ImageIO.write(bufferedImage, "png", testImage);

        // Create existing tile
        File tilesDir = new File(testDataDir, "Edition1/Collection1/tiles");
        File existingJson = new File(tilesDir, "existing-tile.json");
        existingJson.createNewFile();
        long originalModified = existingJson.lastModified();

        // Wait a bit to ensure timestamp would change if file is modified
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            // Ignore
        }

        // Generate tiles - should not overwrite existing
        service.generateTiles("Edition1", "Collection1");

        // Verify the existing file was not modified
        assertEquals("Existing tile should not be overwritten", originalModified, existingJson.lastModified());
    }
}
