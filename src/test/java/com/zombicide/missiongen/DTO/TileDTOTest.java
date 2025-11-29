package com.zombicide.missiongen.DTO;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.junit.Before;
import org.junit.Test;

import com.zombicide.missiongen.model.Tile;

public class TileDTOTest {

    private TileDTO tileDTO;
    private static final String TEST_EDITION = "TestEdition";
    private static final String TEST_COLLECTION = "TestCollection";
    private static final String TEST_IMAGE_PATH = "test-data-temp/test-tile.png";
    private static final String TEST_TILE_NAME = "test-tile";

    @Before
    public void setUp() throws IOException {
        tileDTO = new TileDTO();

        // Create a test image file
        File testImageDir = new File("test-data-temp");
        testImageDir.mkdirs();
        BufferedImage testImage = new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);
        ImageIO.write(testImage, "png", new File(TEST_IMAGE_PATH));
    }

    @Test
    public void testDefaultConstructor() {
        TileDTO dto = new TileDTO();
        assertNotNull("Area list should not be null", dto.areas);
        assertNotNull("Connections list should not be null", dto.connections);
        assertTrue("Area list should be empty", dto.areas.isEmpty());
        assertTrue("Connections list should be empty", dto.connections.isEmpty());
    }

    @Test
    public void testFromTile() {
        Tile tile = new Tile(TEST_EDITION, TEST_COLLECTION, TEST_IMAGE_PATH, TEST_TILE_NAME);
        TileDTO dto = TileDTO.fromTile(tile);

        assertNotNull("TileDTO should not be null", dto);
        assertEquals("Edition should match", TEST_EDITION, dto.edition);
        assertEquals("Collection should match", TEST_COLLECTION, dto.collection);
        assertEquals("Image path should match", TEST_IMAGE_PATH, dto.imagePath);
        assertEquals("Tile name should match", TEST_TILE_NAME, dto.tileName);
        assertNotNull("Area list should not be null", dto.areas);
        assertNotNull("Connections list should not be null", dto.connections);
    }

    @Test
    public void testSetFields() {
        tileDTO.edition = TEST_EDITION;
        tileDTO.collection = TEST_COLLECTION;
        tileDTO.imagePath = TEST_IMAGE_PATH;
        tileDTO.tileName = TEST_TILE_NAME;

        assertEquals("Edition should be set", TEST_EDITION, tileDTO.edition);
        assertEquals("Collection should be set", TEST_COLLECTION, tileDTO.collection);
        assertEquals("Image path should be set", TEST_IMAGE_PATH, tileDTO.imagePath);
        assertEquals("Tile name should be set", TEST_TILE_NAME, tileDTO.tileName);
    }

    @Test
    public void testAddArea() {
        BoardAreaDTO area = new BoardAreaDTO();
        area.id = 1;
        area.x = 10;
        area.y = 20;
        area.width = 100;
        area.height = 150;
        area.areaType = "indoor";

        tileDTO.areas.add(area);

        assertEquals("Should have 1 area", 1, tileDTO.areas.size());
        assertEquals("Area id should match", 1, tileDTO.areas.get(0).id);
        assertEquals("Area type should match", "indoor", tileDTO.areas.get(0).areaType);
    }

    @Test
    public void testAddConnection() {
        BoardAreaConnectionDTO connection = new BoardAreaConnectionDTO();
        connection.areaA = 1;
        connection.areaB = 2;

        tileDTO.connections.add(connection);

        assertEquals("Should have 1 connection", 1, tileDTO.connections.size());
        assertEquals("AreaA should match", 1, tileDTO.connections.get(0).areaA);
        assertEquals("AreaB should match", 2, tileDTO.connections.get(0).areaB);
    }
}
