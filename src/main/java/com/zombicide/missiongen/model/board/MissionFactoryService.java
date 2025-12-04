package com.zombicide.missiongen.model.board;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javax.imageio.ImageIO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zombicide.missiongen.config.ConfigLoader;
import com.zombicide.missiongen.model.Mission;
import com.zombicide.missiongen.model.areas.BoardArea;
import com.zombicide.missiongen.model.areas.BoardAreaConnection;
import com.zombicide.missiongen.model.helpers.ImageOperations;
import com.zombicide.missiongen.ui.missionLayout.ZoneMissionGridCell;

public class MissionFactoryService {

    private static final Logger logger = LoggerFactory.getLogger(MissionFactoryService.class);

    public static Mission createMission(String missionId, String edition, String collection, String missionName,
            MissionGrid grid) {

            ConfigLoader config = ConfigLoader.getInstance();
            String imagePath = config.getProperty("folders.editions") + "/"
        + edition + "/" + collection + "/"
        + config.getProperty("folders.missionImages") + "/" 
        + config.getMissionImagePrefix() + missionName + config.getMissionImageSuffix();
        MissionBoard missionBoard = createMissionBoard(grid,  imagePath);

      

        return new Mission(grid.getGridWidth(), grid.getGridHeight(),
                missionBoard.getWidth(),
                missionBoard.getHeight(), edition, collection,
                imagePath,
                missionName, missionBoard);
    }

    // public static MissionBoard createMissionBoard(int width, int height) {
    //     MissionGrid grid = new MissionGrid(width, height);
    //     return createMissionBoard(grid,null);
    // }

    public static MissionBoard createMissionBoard(MissionGrid grid,String imagePath) {
        //Image image = createMissionBoardImage(grid);
        Image image = createMissionBoardImageComplete(grid);
        int tileWidth = Integer.parseInt(ConfigLoader.getInstance().getProperty("tile.width"));
        int tileHeight = Integer.parseInt(ConfigLoader.getInstance().getProperty("tile.height"));
        int missionBoardWidth = grid.getGridWidth() * tileWidth;
        int missionBoardHeight = grid.getGridHeight() * tileHeight;
        String missionBoardId = UUID.randomUUID().toString();
        MissionBoard missionBoard = new MissionBoard(missionBoardId, image,imagePath, missionBoardWidth, missionBoardHeight,grid.toMissionTileEntries());
        missionBoard = addAreas(missionBoard, grid,
                tileWidth,
                tileHeight);

        return missionBoard;
    }

    private static MissionBoard addAreas(MissionBoard missionBoard, MissionGrid grid, int tileWidth,
            int tileHeight) {

        List<Set<UUID>> areasToMerge = grid.getAreasToMerge();

        // Create merge map: assign a new UUID to each set of areas to merge
        Map<UUID, Set<UUID>> mergeMap = new HashMap<>();
        for (Set<UUID> areasId : areasToMerge) {
            mergeMap.put(UUID.randomUUID(), areasId);
        }

        // Get all areas (merged and shifted)
        List<BoardArea> allAreas = getAllAreasMergedAndShifted(grid, mergeMap, tileWidth, tileHeight);

        // Get all connections with remapped UUIDs
        List<BoardAreaConnection> connections = getMergedConnections(grid, mergeMap);

        logger.info("Total areas after merge and shift: " + allAreas.size());
        logger.info("Total connections after merge: " + connections.size());

        // Add all areas to the mission board
        for (BoardArea area : allAreas) {
            missionBoard.addArea(area);
        }

        // Add all connections to the mission board
        for (BoardAreaConnection connection : connections) {
            missionBoard.addConnection(connection);
        }

        return missionBoard;
    }

    /**
     * Devuelve todas las conexiones entre areas modificando los uuid de las areas
     * que van a mergear por el nuevo uuid que tendran.
     * De momento no tratamos las open connections, lo haremos mas adelante.
     * 
     * @param grid     The mission grid containing all tiles
     * @param mergeMap Map of new UUID -> Set of old UUIDs to be merged
     * @return List of connections with remapped UUIDs
     */
    private static List<BoardAreaConnection> getMergedConnections(MissionGrid grid,
            Map<UUID, Set<UUID>> mergeMap) {

        List<BoardAreaConnection> allConnections = new ArrayList<>();

        // Create reverse map: old UUID -> new UUID for quick lookup
        Map<UUID, UUID> oldToNewMap = new HashMap<>();
        for (Map.Entry<UUID, Set<UUID>> entry : mergeMap.entrySet()) {
            UUID newUuid = entry.getKey();
            for (UUID oldUuid : entry.getValue()) {
                oldToNewMap.put(oldUuid, newUuid);
            }
        }

        // Collect all connections from all boards in the grid
        for (int row = 0; row < grid.getGridHeight(); row++) {
            for (int col = 0; col < grid.getGridWidth(); col++) {
                TileBoard board = grid.getBoard(col, row);
                if (board != null) {
                    for (BoardAreaConnection connection : board.getConnections()) {
                        // Remap the connection UUIDs if they are being merged
                        UUID areaAId = connection.getAreaAId();
                        UUID areaBId = connection.getAreaBId();

                        // Skip open connections (where areaB is null)
                        if (areaBId == null) {
                            processEdgeConnections(connection, col, row, grid, oldToNewMap, allConnections);
                            continue;
                        }

                        // Remap to new UUIDs if they are being merged
                        UUID newAreaAId = oldToNewMap.getOrDefault(areaAId, areaAId);
                        UUID newAreaBId = oldToNewMap.getOrDefault(areaBId, areaBId);

                        // Skip self-connections (areas merged together)
                        if (newAreaAId.equals(newAreaBId)) {
                            continue;
                        }

                        // Create new connection with remapped UUIDs
                        // Note: We only handle normal area-to-area connections here
                        // Edge connections (with direction) are skipped above
                        BoardAreaConnection remappedConnection = new BoardAreaConnection(
                                newAreaAId,
                                newAreaBId);

                        allConnections.add(remappedConnection);
                    }
                }
            }
        }

        return allConnections;
    }

    private static void processEdgeConnections(BoardAreaConnection connection, int col, int row, MissionGrid grid,
            Map<UUID, UUID> oldToNewMap, List<BoardAreaConnection> allConnections) {

        com.zombicide.missiongen.model.areas.DoorDirection direction = connection.getDirection();
        if (direction == null) {
            return;
        }

        // Calculate target tile coordinates
        int targetCol = col;
        int targetRow = row;

        if (direction.name().contains("NORTH")) {
            targetRow--;
        } else if (direction.name().contains("SOUTH")) {
            targetRow++;
        } else if (direction.name().contains("EAST")) {
            targetCol++;
        } else if (direction.name().contains("WEST")) {
            targetCol--;
        }

        // Check bounds
        if (targetCol < 0 || targetCol >= grid.getGridWidth() || targetRow < 0 || targetRow >= grid.getGridHeight()) {
            return;
        }

        TileBoard targetBoard = grid.getBoard(targetCol, targetRow);
        if (targetBoard == null) {
            return;
        }

        UUID areaAId = connection.getAreaAId();
        UUID newAreaAId = oldToNewMap.getOrDefault(areaAId, areaAId);

        // Case 1: Target is a Street Location
        com.zombicide.missiongen.model.areas.AreaLocation targetStreetLocation = direction.toStreetLocation();
        if (targetStreetLocation != null && targetBoard.hasAreaLocation(targetStreetLocation)) {
            BoardArea targetArea = targetBoard.getAreaByAreaLocation(targetStreetLocation);
            UUID targetAreaId = targetArea.getAreaId();
            UUID newTargetAreaId = oldToNewMap.getOrDefault(targetAreaId, targetAreaId);

            // Create connection
            allConnections.add(new BoardAreaConnection(newAreaAId, newTargetAreaId));
            return;
        }

        // Case 2: Target is an Indoor Area with matching edge connection
        com.zombicide.missiongen.model.areas.DoorDirection oppositeDirection = direction.getOpposite();
        if (oppositeDirection != null) {
            for (BoardAreaConnection targetConn : targetBoard.getConnections()) {
                if (targetConn.isEdgeConnection() && targetConn.getDirection() == oppositeDirection) {
                    UUID targetAreaId = targetConn.getAreaAId();
                    UUID newTargetAreaId = oldToNewMap.getOrDefault(targetAreaId, targetAreaId);

                    // Create connection
                    allConnections.add(new BoardAreaConnection(newAreaAId, newTargetAreaId));
                    // We found the matching connection, we can stop searching in this board
                    // Note: This assumes only one connection per direction per board edge, which is
                    // generally true
                    return;
                }
            }
        }
    }

    /**
     * Devuelve todas las areas de la mision, hace un shift de las coordenadas de
     * las areas dependiendo de en que zona de la grid esta.
     * Crea las areas de merge creando un top-left y un bottom-right que seran los
     * limites de la area nueva.
     * 
     * @param grid     The mission grid containing all tiles
     * @param mergeMap Map of new UUID -> Set of old UUIDs to be merged
     * @return List of all areas with shifted coordinates and merged areas
     */
    private static List<BoardArea> getAllAreasMergedAndShifted(MissionGrid grid,
            Map<UUID, Set<UUID>> mergeMap,
            int tileWidth,
            int tileHeight) {
        List<BoardArea> allAreas = new ArrayList<>();

        // Create reverse map: old UUID -> new UUID for quick lookup
        Map<UUID, UUID> oldToNewMap = new HashMap<>();
        for (Map.Entry<UUID, Set<UUID>> entry : mergeMap.entrySet()) {
            UUID newUuid = entry.getKey();
            for (UUID oldUuid : entry.getValue()) {
                oldToNewMap.put(oldUuid, newUuid);
            }
        }

        // Track which areas have been processed (to avoid duplicates for merged areas)
        Set<UUID> processedMergedAreas = new HashSet<>();

        // Collect all areas from the grid
        for (int row = 0; row < grid.getGridHeight(); row++) {
            for (int col = 0; col < grid.getGridWidth(); col++) {
                TileBoard board = grid.getBoard(col, row);
                if (board != null) {
                    // Calculate shift offset for this tile position
                    int shiftX = col * tileWidth;
                    int shiftY = row * tileHeight;

                    for (BoardArea area : board.getAreas()) {
                        UUID areaId = area.getAreaId();

                        // Check if this area is part of a merge
                        if (oldToNewMap.containsKey(areaId)) {
                            UUID newMergedId = oldToNewMap.get(areaId);

                            // Only process each merged area once
                            if (!processedMergedAreas.contains(newMergedId)) {
                                processedMergedAreas.add(newMergedId);

                                // Create merged area by finding bounding box of all areas to merge
                                BoardArea mergedArea = createMergedArea(grid, mergeMap.get(newMergedId),
                                        newMergedId, tileWidth, tileHeight);
                                allAreas.add(mergedArea);
                            }
                        } else {
                            // Not a merged area - just shift coordinates and add
                            BoardArea shiftedArea = createShiftedArea(area, shiftX, shiftY);
                            allAreas.add(shiftedArea);
                        }
                    }
                }
            }
        }

        return allAreas;
    }

    /**
     * Creates a merged area by calculating the bounding box of all areas to merge.
     */
    private static BoardArea createMergedArea(MissionGrid grid, Set<UUID> areasToMerge,
            UUID newMergedId, int tileWidth, int tileHeight) {
        int minX = Integer.MAX_VALUE;
        int minY = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE;
        int maxY = Integer.MIN_VALUE;

        BoardArea firstArea = null;

        // Find bounding box across all tiles
        for (int row = 0; row < grid.getGridHeight(); row++) {
            for (int col = 0; col < grid.getGridWidth(); col++) {
                TileBoard board = grid.getBoard(col, row);
                if (board != null) {
                    int shiftX = col * tileWidth;
                    int shiftY = row * tileHeight;

                    for (BoardArea area : board.getAreas()) {
                        if (areasToMerge.contains(area.getAreaId())) {
                            if (firstArea == null) {
                                firstArea = area;
                            }

                            // Calculate shifted coordinates
                            int areaX = area.getTopLeft().x + shiftX;
                            int areaY = area.getTopLeft().y + shiftY;

                            minX = Math.min(minX, areaX);
                            minY = Math.min(minY, areaY);
                            maxX = Math.max(maxX, areaX + area.getWidth());
                            maxY = Math.max(maxY, areaY + area.getHeight());
                        }
                    }
                }
            }
        }

        // Create new merged area with bounding box
        int width = maxX - minX;
        int height = maxY - minY;

        BoardArea mergedArea = new BoardArea(newMergedId, new java.awt.Point(minX, minY), width, height);

        // Copy properties from first area (type, location if not street, assets)
        if (firstArea != null) {
            mergedArea.setAreaType(firstArea.getAreaType());
            // For merged street areas, we might want to set location to OTHER or keep it
            // For now, keep the location from first area
            mergedArea.setAreaLocation(firstArea.getAreaLocation());
        }

        return mergedArea;
    }

    /**
     * Creates a shifted copy of an area.
     */
    private static BoardArea createShiftedArea(BoardArea original, int shiftX, int shiftY) {
        BoardArea shifted = new BoardArea(
                original.getAreaId(),
                new java.awt.Point(original.getTopLeft().x + shiftX, original.getTopLeft().y + shiftY),
                original.getWidth(),
                original.getHeight());

        shifted.setAreaType(original.getAreaType());
        shifted.setAreaLocation(original.getAreaLocation());

        return shifted;
    }

    // // ***** Private Methods ***** */

    // *** createMissionBoardImage *** */
    /**
     * Creates an image of the mission board by concatenating the different tiles.
     *
     * @param rows      The number of rows in the mission board
     * @param cols      The number of columns in the mission board
     * @param tiles     The tiles to be used in the mission board
     * @param rotations The rotations of the tiles
     */
    private static Image createMissionBoardImage(MissionGrid grid) {
        if (grid == null || grid.getGridWidth() == 0 || grid.getGridHeight() == 0) {
            return null;
        }

        int tileWidth = Integer.parseInt(ConfigLoader.getInstance().getProperty("tile.width"));
        int tileHeight = Integer.parseInt(ConfigLoader.getInstance().getProperty("tile.height"));
        int totalWidth = grid.getGridWidth() * tileWidth;
        int totalHeight = grid.getGridHeight() * tileHeight;

        BufferedImage missionImage = new BufferedImage(totalWidth, totalHeight,
                BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = missionImage.createGraphics();

        for (int i = 0; i < grid.getGridHeight(); i++) { // i = row index
            for (int j = 0; j < grid.getGridWidth(); j++) { // j = col index
                TileBoard board = grid.getBoard(j, i);
                if (board != null) {
                    Image img = board.getImage();

                    int x = j * tileWidth; // column determines x position
                    int y = i * tileHeight; // row determines y position

                    g2d.drawImage(img, x, y, tileWidth, tileHeight, null);
                }
            }
        }
        g2d.dispose();
        return missionImage;
    }

     private static Image createMissionBoardImageComplete(MissionGrid grid) {
        if (grid == null || grid.getGridWidth() == 0 || grid.getGridHeight() == 0) {
            return null;
        }
        //load all iamges in a grid
        Image[][] images = new Image[grid.getGridHeight()][grid.getGridWidth()];
        MissionTileEntry[][] entries = grid.toMissionTileEntries();
        for(int i=0;i<grid.getGridHeight();i++){
            for(int j=0;j<grid.getGridWidth();j++){
                MissionTileEntry entry = entries[i][j];
                if(entry!=null){
                    try {
                        Image img = ImageIO.read(new File(entry.getImagePath()));
                        images[i][j] = ImageOperations.rotateImage(img, entry.getRotation());
                    } catch (IOException e) {
                       logger.error("Error loading tile image for tile: {}", entry.getTileName(), e);
                        return null;
                    }
                }
            }
        }
        //calculate total width and height
        int totalWidth = 0;
        for(int j=0;j<grid.getGridWidth();j++){
            if(images[0][j]!=null){
                totalWidth += images[0][j].getWidth(null);
            }
        }
        //calculate total height
        int totalHeight = 0;
        for(int i=0;i<grid.getGridHeight();i++){
            if(images[i][0]!=null){
                totalHeight+= images[i][0].getHeight(null);
            }
        }


        BufferedImage missionImage = new BufferedImage(totalWidth, totalHeight,
                BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = missionImage.createGraphics();
        for(int i=0;i<grid.getGridHeight();i++){
            for(int j=0;j<grid.getGridWidth();j++){
                int tileWidth = images[i][j].getWidth(null);
                int tileHeight = images[i][j].getHeight(null);
                g2d.drawImage(images[i][j], j * tileWidth, i * tileHeight, tileWidth, tileHeight, null);
                }
            }
        g2d.dispose();
        return missionImage;
    }

    
}
