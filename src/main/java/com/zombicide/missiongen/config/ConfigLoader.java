package com.zombicide.missiongen.config;

import java.awt.Color;
import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.imageio.ImageIO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Singleton class to load and manage application configuration from properties
 * file.
 */
public class ConfigLoader {
    private static final Logger logger = LoggerFactory.getLogger(ConfigLoader.class);
    private static final String PROPERTIES_FILE = "application.properties";

    private static ConfigLoader instance;
    private Properties properties;

    private ConfigLoader() {
        properties = new Properties();
        loadProperties();
    }

    /**
     * Gets the singleton instance of ConfigLoader.
     * 
     * @return The ConfigLoader instance
     */
    public static synchronized ConfigLoader getInstance() {
        if (instance == null) {
            instance = new ConfigLoader();
        }
        return instance;
    }

    /**
     * Loads properties from the application.properties file.
     */
    private void loadProperties() {
        try (InputStream input = getClass().getClassLoader().getResourceAsStream(PROPERTIES_FILE)) {
            if (input == null) {
                logger.error("Unable to find {}", PROPERTIES_FILE);
                return;
            }

            properties.load(input);
            logger.info("Properties loaded successfully from {}", PROPERTIES_FILE);

        } catch (IOException e) {
            logger.error("Error loading properties file", e);
        }
    }

    /**
     * Gets a property value by key.
     * 
     * @param key The property key
     * @return The property value, or null if not found
     */
    public String getProperty(String key) {
        return properties.getProperty(key);
    }

    public int getPropertyAsInt(String key) {
        return Integer.parseInt(properties.getProperty(key));
    }

    /**
     * Gets a property value by key with a default value.
     * 
     * @param key          The property key
     * @param defaultValue The default value if key is not found
     * @return The property value, or defaultValue if not found
     */
    public String getProperty(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }

    /**
     * Gets the editions folder path.
     * 
     * @return The editions folder path
     */
    public String getEditionsFolder() {
        // Check system property first (for testing)
        String systemProperty = System.getProperty("folders.editions");
        if (systemProperty != null && !systemProperty.trim().isEmpty()) {
            return systemProperty;
        }
        return getProperty("folders.editions", "assets/editions");
    }

    /**
     * Gets the tile images folder name.
     * 
     * @return The tile images folder name
     */
    public String getTileImagesFolder() {
        return getProperty("folders.tileImages", "images");
    }

    /**
     * Gets the tiles folder name.
     * 
     * @return The tiles folder name
     */
    public String getTilesFolder() {
        return getProperty("folders.tiles", "tiles");
    }

    /**
     * Gets the missions folder name.
     * 
     * @return The missions folder name
     */
    public String getMissionsFolder() {
        return getProperty("folders.missions", "missions");
    }

     public String getMissionImagesFolder() {
        return getProperty("folders.missionImages", "missionImages");
    }

    /**
     * Gets the mission layout file prefix.
     * 
     * @return The mission layout file prefix
     */
    public String getMissionLayoutPrefix() {
        return getProperty("file.mission_layout.prefix", "mission_");
    }

    /**
     * Gets the mission layout file suffix.
     * 
     * @return The mission layout file suffix
     */
    public String getMissionLayoutSuffix() {
        return getProperty("file.mission_layout.suffix", ".json");
    }

    /**
     * Gets the mission image file prefix.
     * 
     * @return The mission image file prefix
     */
    public String getMissionImagePrefix() {
        return getProperty("file.mission_image.prefix", "mission_");
    }

    /**
     * Gets the mission image file suffix.
     * 
     * @return The mission image file suffix
     */
    public String getMissionImageSuffix() {
        return getProperty("file.mission_image.suffix", ".png");
    }


    public Color getPropertyAsColor(String string, Color defaultValue) {
        String colorString = getProperty(string);
        if (colorString == null) {
            return defaultValue;
        }
        return Color.decode(colorString);
    }

    public Image getNoTileYetImage() {
        // devuelve la imagen no_tile_yet.png de la carpete resources/images
        // debe cargar la imagen desde el classpath
        Image image = null;
        try {
            image = ImageIO.read(this.getClass().getResource("/images/no_tile.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return image;
    }
}
