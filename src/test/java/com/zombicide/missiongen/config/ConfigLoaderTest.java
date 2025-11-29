package com.zombicide.missiongen.config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;

import org.junit.Test;

public class ConfigLoaderTest {

    @Test
    public void testSingletonInstance() {
        ConfigLoader instance1 = ConfigLoader.getInstance();
        ConfigLoader instance2 = ConfigLoader.getInstance();

        assertNotNull("Instance should not be null", instance1);
        assertSame("Should return the same instance", instance1, instance2);
    }

    @Test
    public void testGetEditionsFolder() {
        ConfigLoader config = ConfigLoader.getInstance();
        String editionsFolder = config.getEditionsFolder();

        assertNotNull("Editions folder should not be null", editionsFolder);
        assertEquals("Should return correct editions folder", "assets/editions", editionsFolder);
    }

    @Test
    public void testGetTileImagesFolder() {
        ConfigLoader config = ConfigLoader.getInstance();
        String tileImagesFolder = config.getTileImagesFolder();

        assertNotNull("Tile images folder should not be null", tileImagesFolder);
        assertEquals("Should return correct tile images folder", "tileImages", tileImagesFolder);
    }

    @Test
    public void testGetTilesFolder() {
        ConfigLoader config = ConfigLoader.getInstance();
        String tilesFolder = config.getTilesFolder();

        assertNotNull("Tiles folder should not be null", tilesFolder);
        assertEquals("Should return correct tiles folder", "tiles", tilesFolder);
    }

    @Test
    public void testGetPropertyWithDefault() {
        ConfigLoader config = ConfigLoader.getInstance();
        String value = config.getProperty("non.existent.property", "defaultValue");

        assertEquals("Should return default value for non-existent property", "defaultValue", value);
    }

    @Test
    public void testGetProperty() {
        ConfigLoader config = ConfigLoader.getInstance();
        String value = config.getProperty("folders.editions");

        assertNotNull("Property should exist", value);
        assertEquals("Should return correct property value", "assets/editions", value);
    }
}
