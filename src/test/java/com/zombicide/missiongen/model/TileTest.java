package com.zombicide.missiongen.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.zombicide.missiongen.DTO.TileDTO;
import com.zombicide.missiongen.model.board.BaseBoard;

public class TileTest {

    private static final String TEST_EDITION = "TestEdition";
    private static final String TEST_COLLECTION = "TestCollection";
    private static final String TEST_IMAGE_PATH = "test-data-temp/test-tile.png";
    private static final String TEST_TILE_NAME = "test-tile";
    private Tile tile;

    @Before
    public void setUp() throws IOException {
        // Create test directory and image
        File testImageDir = new File("test-data-temp");
        testImageDir.mkdirs();

        BufferedImage testImage = new BufferedImage(200, 300, BufferedImage.TYPE_INT_RGB);
        ImageIO.write(testImage, "png", new File(TEST_IMAGE_PATH));

        tile = new Tile(TEST_EDITION, TEST_COLLECTION, TEST_IMAGE_PATH, TEST_TILE_NAME);
    }

    @After
    public void tearDown() {
        // Clean up test files
        File testImage = new File(TEST_IMAGE_PATH);
        if (testImage.exists()) {
            testImage.delete();
        }
        File testDir = new File("test-data-temp");
        if (testDir.exists()) {
            testDir.delete();
        }
    }

    @Test
    public void testConstructor() {
        assertNotNull("Tile should not be null", tile);
        assertEquals("Edition should match", TEST_EDITION, tile.getEdition());
        assertEquals("Collection should match", TEST_COLLECTION, tile.getCollection());
        assertEquals("Image path should match", TEST_IMAGE_PATH, tile.getImagePath());
        assertEquals("Tile name should match", TEST_TILE_NAME, tile.getTileName());
    }

    @Test
    public void testGetBoard() {
        BaseBoard board = tile.getBoard();
        assertNotNull("Board should not be null", board);
        assertNotNull("Board image should not be null", board.getImage());
        assertEquals("Board width should match image width", 200, board.getWidth());
        assertEquals("Board height should match image height", 300, board.getHeight());
    }

    @Test
    public void testGetEdition() {
        assertEquals("Edition should be " + TEST_EDITION, TEST_EDITION, tile.getEdition());
    }

    @Test
    public void testGetCollection() {
        assertEquals("Collection should be " + TEST_COLLECTION, TEST_COLLECTION, tile.getCollection());
    }

    @Test
    public void testGetImagePath() {
        assertEquals("Image path should be " + TEST_IMAGE_PATH, TEST_IMAGE_PATH, tile.getImagePath());
    }

    @Test
    public void testGetTileName() {
        assertEquals("Tile name should be " + TEST_TILE_NAME, TEST_TILE_NAME, tile.getTileName());
    }

    @Test
    public void testFromTileDTO() {
        TileDTO tileDTO = new TileDTO();
        tileDTO.edition = "Edition2";
        tileDTO.collection = "Collection2";
        tileDTO.imagePath = TEST_IMAGE_PATH;
        tileDTO.tileName = "tile2";

        Tile newTile = Tile.fromTileDTO(tileDTO);

        assertNotNull("Tile should not be null", newTile);
        assertEquals("Edition should match", "Edition2", newTile.getEdition());
        assertEquals("Collection should match", "Collection2", newTile.getCollection());
        assertEquals("Image path should match", TEST_IMAGE_PATH, newTile.getImagePath());
        assertEquals("Tile name should match", "tile2", newTile.getTileName());
    }

    @Test
    public void testConstructorWithInvalidImagePath() {
        // Test that constructor handles invalid image paths gracefully
        Tile tileWithInvalidPath = new Tile(TEST_EDITION, TEST_COLLECTION,
                "invalid/path/image.png", "invalid-tile");

        assertNotNull("Tile should still be created", tileWithInvalidPath);
        assertEquals("Edition should match", TEST_EDITION, tileWithInvalidPath.getEdition());
        assertEquals("Collection should match", TEST_COLLECTION, tileWithInvalidPath.getCollection());
        assertEquals("Tile name should match", "invalid-tile", tileWithInvalidPath.getTileName());
        // Board might be null or error handled
    }
}
