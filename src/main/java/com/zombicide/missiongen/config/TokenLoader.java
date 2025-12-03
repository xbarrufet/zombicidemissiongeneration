package com.zombicide.missiongen.config;

import java.awt.Color;
import java.awt.Image;
import java.util.ArrayList;
import java.util.List;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.Set;

import javax.imageio.ImageIO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zombicide.missiongen.model.board.BaseBoard;
import com.zombicide.missiongen.model.tokens.TokenType;

/**
 * Singleton class to load and manage application configuration from properties
 * file.
 */
public class TokenLoader {
    private static final Logger logger = LoggerFactory.getLogger(TokenLoader.class);
    private static final String PROPERTIES_FILE = "application.properties";

    private static TokenLoader instance;
    private Properties properties;

    private final String TOKENS_ENTRY = "image.token.";
    private final String TOKEN_DIMENSION_ENTRY = "image.tokendimension.";

    private TokenLoader() {
        properties = new Properties();
        loadProperties();
    }

    /**
     * Gets the singleton instance of ConfigLoader.
     * 
     * @return The ConfigLoader instance
     */
    public static synchronized TokenLoader getInstance() {
        if (instance == null) {
            instance = new TokenLoader();
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

    public List<String> getTokenSubtypes(TokenType type) {
        // list all entries that start with image.token.type
        String key = TOKENS_ENTRY + type.name() + ".";
        Set<String> propertyNames = properties.stringPropertyNames();
        List<String> subtypes = new ArrayList<>();
        for (String propertyName : propertyNames) {
            if (propertyName.startsWith(key)) {
                subtypes.add(propertyName.substring(key.length()));
            }
        }
        return subtypes;
    }

    public String getTokenImagePath(TokenType type, String subtype) {
        return properties.getProperty(TOKENS_ENTRY + type.name() + "." + subtype);
    }

    public Image getTokenImage(TokenType type, String subtype) {
        try {
            String imagePath = getTokenImagePath(type, subtype);
            if (!imagePath.startsWith("/")) {
                imagePath = "/" + imagePath;
            }
            return ImageIO.read(this.getClass().getResource(imagePath));
        } catch (IOException e) {
            logger.error("Error loading token image for type {} subtype {}", type.name(), subtype, e);
            return null;
        }
    }

    public int[] getTokenDimension(TokenType type) {
        String key = TOKEN_DIMENSION_ENTRY + type.name();
        String value = properties.getProperty(key);
        if (value == null) {
            return null;
        }
        String[] dimensions = value.split("x");
        return new int[] { Integer.parseInt(dimensions[0]), Integer.parseInt(dimensions[1]) };
    }

}