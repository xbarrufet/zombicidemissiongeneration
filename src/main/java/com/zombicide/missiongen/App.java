package com.zombicide.missiongen;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

public class App {
    private static final Logger logger = LoggerFactory.getLogger(App.class);

    public static void main(String[] args) {
        logger.info("Starting Mission Generation App...");

        // Set System Look and Feel
        try {
            javax.swing.UIManager.setLookAndFeel(javax.swing.UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            logger.error("Failed to set Look and Feel", e);
        }

        // JSON Mapper Example
        try {
            ObjectMapper mapper = new ObjectMapper();
            Map<String, Object> data = new HashMap<>();
            data.put("app", "MissionGeneration");
            data.put("version", 1.0);
            data.put("status", "active");

            String json = mapper.writeValueAsString(data);
            logger.info("JSON Configuration: {}", json);
        } catch (Exception e) {
            logger.error("Error creating JSON", e);
        }

        // Create and display main window
        MainWindow mainWindow = new MainWindow();
        mainWindow.display();

        logger.info("Main window opened.");
    }
}
