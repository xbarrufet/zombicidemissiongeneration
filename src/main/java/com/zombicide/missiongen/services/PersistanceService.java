package com.zombicide.missiongen.services;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.zombicide.missiongen.DTO.TileDTO;
import com.zombicide.missiongen.config.ConfigLoader;
import com.zombicide.missiongen.model.Tile;

public class PersistanceService {
    private static final Logger logger = LoggerFactory.getLogger(PersistanceService.class);
    private final ConfigLoader config;
    private final ObjectMapper objectMapper;

    public PersistanceService() {
        this.config = ConfigLoader.getInstance();
        this.objectMapper = new ObjectMapper();
        this.objectMapper.enable(SerializationFeature.INDENT_OUTPUT); // JSON formateado
        this.objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    /**
     * Lee los folders debajo del path definido en la property folders.editions y
     * devuelve una lista de strings con los nombres de los folders
     * 
     * @return Lista de nombres de ediciones
     */
    public List<String> getEditions() {
        String editionsPath = config.getEditionsFolder();
        File editionsDir = new File(editionsPath);

        if (!editionsDir.exists() || !editionsDir.isDirectory()) {
            logger.warn("Editions directory does not exist: {}", editionsPath);
            return Collections.emptyList();
        }

        File[] directories = editionsDir.listFiles(File::isDirectory);
        if (directories == null || directories.length == 0) {
            logger.info("No editions found in: {}", editionsPath);
            return Collections.emptyList();
        }

        List<String> editions = Arrays.stream(directories)
                .map(File::getName)
                .sorted()
                .collect(Collectors.toList());

        logger.info("Found {} editions", editions.size());
        return editions;
    }

    /**
     * Lee los folders debajo del path definido en la property folders.editions +
     * edition y
     * devuelve una lista de strings con los nombres de los folders
     * 
     * @param edition Nombre de la edición
     * @return Lista de nombres de colecciones
     */
    public List<String> getCollections(String edition) {
        if (edition == null || edition.trim().isEmpty()) {
            logger.warn("Edition parameter is null or empty");
            return Collections.emptyList();
        }

        String collectionsPath = config.getEditionsFolder() + File.separator + edition;
        File collectionsDir = new File(collectionsPath);

        if (!collectionsDir.exists() || !collectionsDir.isDirectory()) {
            logger.warn("Collections directory does not exist: {}", collectionsPath);
            return Collections.emptyList();
        }

        File[] directories = collectionsDir.listFiles(File::isDirectory);
        if (directories == null || directories.length == 0) {
            logger.info("No collections found for edition: {}", edition);
            return Collections.emptyList();
        }

        List<String> collections = Arrays.stream(directories)
                .map(File::getName)
                .sorted()
                .collect(Collectors.toList());

        logger.info("Found {} collections for edition '{}'", collections.size(), edition);
        return collections;
    }

    /**
     * Lee los nombres de los ficheros de la carpeta
     * edition + collection + property folder.tileImages
     * devuelve una lista de strings con los nombres de los ficheros
     * 
     * @param edition    Nombre de la edición
     * @param collection Nombre de la colección
     * @return Lista de nombres de archivos de imágenes de tiles
     */
    public List<String> getTileImages(String edition, String collection) {
        if (edition == null || edition.trim().isEmpty() ||
                collection == null || collection.trim().isEmpty()) {
            logger.warn("Edition or collection parameter is null or empty");
            return Collections.emptyList();
        }

        String imagesPath = config.getEditionsFolder() + File.separator +
                edition + File.separator +
                collection + File.separator +
                config.getTileImagesFolder();

        return getFilesInDirectory(imagesPath, "tile images", edition, collection, false);
    }

    /**
     * Lee los nombres de los ficheros de la carpeta
     * edition + collection + property folder.tiles
     * devuelve una lista de strings con los nombres de los ficheros
     * 
     * @param edition    Nombre de la edición
     * @param collection Nombre de la colección
     * @return Lista de nombres de archivos de tiles
     */
    public List<String> getTiles(String edition, String collection) {
        if (edition == null || edition.trim().isEmpty() ||
                collection == null || collection.trim().isEmpty()) {
            logger.warn("Edition or collection parameter is null or empty");
            return Collections.emptyList();
        }

        String tilesPath = config.getEditionsFolder() + File.separator +
                edition + File.separator +
                collection + File.separator +
                config.getTilesFolder();
        return getFilesInDirectory(tilesPath, "tiles", edition, collection, true);
    }

    /**
     * Lee los nombres de los ficheros de la carpeta
     * edition + collection + property folder.missions
     * devuelve una lista de strings con los nombres de los ficheros
     * 
     * @param edition    Nombre de la edición
     * @param collection Nombre de la colección
     * @return Lista de nombres de archivos de misiones
     */
    public List<String> getMissions(String edition, String collection) {
        if (edition == null || edition.trim().isEmpty() ||
                collection == null || collection.trim().isEmpty()) {
            logger.warn("Edition or collection parameter is null or empty");
            return Collections.emptyList();
        }

        String missionsPath = config.getEditionsFolder() + File.separator +
                edition + File.separator +
                collection + File.separator +
                config.getMissionsFolder();
        return getFilesInDirectory(missionsPath, "missions", edition, collection, true);
    }

    /**
     * Helper method to get files in a directory.
     * 
     * @param directoryPath Path to the directory
     * @param fileType      Type of files (for logging)
     * @param edition       Edition name (for logging)
     * @param collection    Collection name (for logging)
     * @return Lista de nombres de archivos
     */
    private List<String> getFilesInDirectory(String directoryPath, String fileType,
            String edition, String collection, boolean removeExtension) {
        File directory = new File(directoryPath);

        if (!directory.exists() || !directory.isDirectory()) {
            logger.warn("{} directory does not exist: {}", fileType, directoryPath);
            return Collections.emptyList();
        }

        File[] files = directory.listFiles(File::isFile);
        if (files == null || files.length == 0) {
            logger.info("No {} found for edition '{}', collection '{}'",
                    fileType, edition, collection);
            return Collections.emptyList();
        }

        List<String> fileNames = Arrays.stream(files)
                .map(File::getName)
                .sorted()
                .map(removeExtension ? this::removeExtension : Function.identity())
                .collect(Collectors.toList());

        logger.info("Found {} {} for edition '{}', collection '{}'",
                fileNames.size(), fileType, edition, collection);
        return fileNames;
    }

    // ** Mira si hay alguna imageTile que no tenga un correspondiente tile cread */
    public int getTilesToGenerate(String edition, String collection) {
        List<String> tiles = getTiles(edition, collection);
        List<String> tileImages = getTileImages(edition, collection);
        int tilesToGenerate = 0;
        for (String tileImage : tileImages) {
            if (!tiles.contains(removeExtension(tileImage))) {
                tilesToGenerate++;
            }
        }
        return tilesToGenerate;
    }

    public void generateTiles(String edition, String collection) {
        List<String> tileImages = getTileImages(edition, collection);
        for (String tileImage : tileImages) {
            String tileName = removeExtension(tileImage);
            if (!isTileGenerated(edition, collection, tileName)) {
                generateEmptyTile(edition, collection, tileImage);
            }
        }
    }

    public void persistTile(Tile tile) {
        TileDTO tileDTO = TileDTO.fromTile(tile);
        String tileJsonPath = getTileJsonPath(tileDTO.edition, tileDTO.collection, tileDTO.tileName);
        try {
            File jsonFile = new File(tileJsonPath);
            // Crear directorio si no existe
            File parentDir = jsonFile.getParentFile();
            if (parentDir != null && !parentDir.exists()) {
                parentDir.mkdirs();
            }
            // Guardar JSON
            objectMapper.writeValue(jsonFile, tileDTO);
            logger.info("Tile {} saved to {}", tileDTO.tileName, tileJsonPath);
        } catch (IOException e) {
            logger.error("Error writing tile json file: {}", tileJsonPath, e);
        }
    }

    private void generateEmptyTile(String edition, String collection, String tileImage) {
        String tileName = removeExtension(tileImage);
        String tileImagePath = getTileImagePath(edition, collection, tileImage);
        Tile tile = new Tile(edition, collection, tileImagePath, tileName);
        persistTile(tile);
    }

    public Tile getTile(String edition, String collection, String tile) {
        String json = getTileJsonPath(edition, collection, tile);
        try {
            File jsonFile = new File(json);
            TileDTO tileDTO = objectMapper.readValue(jsonFile, TileDTO.class);
            logger.info("Tile {} loaded from {}", tileDTO.tileName, json);
            return Tile.fromTileDTO(tileDTO);
        } catch (JsonMappingException e) {
            logger.error("Error mapping tile json file: {}", json, e);
        } catch (JsonProcessingException e) {
            logger.error("Error processing tile json file: {}", json, e);
        } catch (IOException e) {
            logger.error("Error reading tile json file: {}", json, e);
        }
        return null;
    }

    /**
     * Checks if a tile exists in the persistence layer.
     * 
     * @param edition    Edition name
     * @param collection Collection name
     * @param tileName   Tile name
     * @return true if the tile exists, false otherwise
     */
    private boolean isTileGenerated(String edition, String collection, String tileName) {
        return getTiles(edition, collection).contains(tileName);
    }

    private String removeExtension(String fileName) {
        return fileName.substring(0, fileName.lastIndexOf('.'));
    }

    private String getTileImagePath(String edition, String collection, String fileName) {
        return config.getEditionsFolder() + File.separator +
                edition + File.separator +
                collection + File.separator +
                config.getTileImagesFolder() + File.separator +
                fileName;
    }

    private String getTileJsonPath(String edition, String collection, String fileName) {
        return config.getEditionsFolder() + File.separator +
                edition + File.separator +
                collection + File.separator +
                config.getTilesFolder() + File.separator +
                fileName + ".json";
    }

    public void persistMission(com.zombicide.missiongen.model.Mission mission) {
        com.zombicide.missiongen.DTO.MissionDTO missionDTO = com.zombicide.missiongen.DTO.MissionDTO
                .fromMission(mission);
        String missionJsonPath = getMissionJsonPath(missionDTO.edition, missionDTO.collection, missionDTO.missionName);
        try {
            File jsonFile = new File(missionJsonPath);
            // Crear directorio si no existe
            File parentDir = jsonFile.getParentFile();
            if (parentDir != null && !parentDir.exists()) {
                parentDir.mkdirs();
            }
            // Guardar JSON
            objectMapper.writeValue(jsonFile, missionDTO);
            logger.info("Mission {} saved to {}", missionDTO.missionName, missionJsonPath);
            persistMissionBoardImage(mission.getMissionBoard().getImage(), mission.getImagePath());
        } catch (IOException e) {
            logger.error("Error writing mission json file: {}", missionJsonPath, e);
        }
    }

    private void persistMissionBoardImage(Image image, String imagePath) throws IOException {
        File imageFile = new File(imagePath);
        // Crear directorio si no existe
        File parentDir = imageFile.getParentFile();
        if (parentDir != null && !parentDir.exists()) {
            parentDir.mkdirs();
        }
        // Guardar imagen
        ImageIO.write((BufferedImage) image, "png", imageFile);
        logger.info("Mission board image saved to {}", imagePath);
    }

    public com.zombicide.missiongen.model.Mission getMission(String edition, String collection, String missionName) {
        String json = getMissionJsonPath(edition, collection, missionName);
        try {
            File jsonFile = new File(json);
            com.zombicide.missiongen.DTO.MissionDTO missionDTO = objectMapper.readValue(jsonFile,
                    com.zombicide.missiongen.DTO.MissionDTO.class);
            logger.info("Mission {} loaded from {}", missionDTO.missionName, json);
            return com.zombicide.missiongen.model.Mission.fromMissionDTO(missionDTO);
        } catch (JsonMappingException e) {
            logger.error("Error mapping mission json file: {}", json, e);
        } catch (JsonProcessingException e) {
            logger.error("Error processing mission json file: {}", json, e);
        } catch (IOException e) {
            logger.error("Error reading mission json file: {}", json, e);
        }
        return null;
    }

    private String getMissionJsonPath(String edition, String collection, String fileName) {
        return config.getEditionsFolder() + File.separator +
                edition + File.separator +
                collection + File.separator +
                config.getMissionsFolder() + File.separator +
                fileName + ".json";
    }
}
