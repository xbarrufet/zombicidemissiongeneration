package com.zombicide.missiongen.model.board;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zombicide.missiongen.config.ConfigLoader;
import com.zombicide.missiongen.model.areas.AreaLocation;
import com.zombicide.missiongen.model.areas.BoardArea;
import com.zombicide.missiongen.model.areas.BoardAreaConnection;
import com.zombicide.missiongen.model.areas.BoardAreaFactiory;
import com.zombicide.missiongen.model.areas.Direction;

public abstract class BaseBoard {

    private List<BoardArea> areas;
    private List<BoardAreaConnection> connections;
    private Image backgroundImage;
    private int width;
    private int height;

    private String boardId;

    private ConfigLoader config;
    private static final Logger logger = LoggerFactory.getLogger(BaseBoard.class);

    public BaseBoard(String boardId, Image backgroundImage, int width, int height) {
        this.boardId = boardId;
        this.backgroundImage = backgroundImage;
        this.width = width;
        this.height = height;
        this.config = ConfigLoader.getInstance();
        this.areas = new ArrayList<>();
        this.connections = new ArrayList<>();
    }

    public BaseBoard(BaseBoard boardToCopy) {
        this.boardId = boardToCopy.getBoardId();
        this.backgroundImage = clonarImage(boardToCopy.backgroundImage);
        this.width = boardToCopy.width;
        this.height = boardToCopy.height;
        this.config = boardToCopy.config;
        this.areas = new ArrayList<>();
        this.connections = new ArrayList<>();
        for (BoardArea area : boardToCopy.areas) {
            this.areas.add(new BoardArea(area));
        }
        for (BoardAreaConnection connection : boardToCopy.connections) {
            this.connections.add(new BoardAreaConnection(connection));
        }
    }

    public void addArea(BoardArea area) {
        areas.add(area);
    }

    public void addAvailableArea() {
        // explore space and creates available areas
        Point[] exploringPoints = BoardAreaFactiory.getExploringPoints();
        logger.info("Exploring points: " + exploringPoints.length);
        for (Point point : exploringPoints) {
            logger.info("Point: " + point);
            if (!isPointInArea(point)) {
                logger.info("Point not in area: " + point);
                BoardArea candidateArea = this.maximizeArea(point);
                BoardArea collisionArea = this.overlapWith(candidateArea);
                if (collisionArea != null) {
                    // we need to split the condidate area by top left
                    // due to we scan top-left to bottom-right, we need to split the candidate area
                    // by top left
                    // to avoid overlapping with other areas
                    Point bottomRight = new Point(collisionArea.getTopLeft().x, candidateArea.getBottomRight().y);
                    candidateArea = BoardAreaFactiory.createBoardIndoorArea(
                            candidateArea.getTopLeft(), bottomRight);
                }
                this.addArea(candidateArea);
            }
        }

    }

    public void addConnection(BoardAreaConnection connection) {
        if (connection.getDirection() != null) {
            BoardArea area = getAreaById(connection.getAreaAId());
            if (area != null
                    && area.getAreaType() != com.zombicide.missiongen.model.areas.BoardArea.AreaType.INDOOR_LIGHT
                    && area.getAreaType() != com.zombicide.missiongen.model.areas.BoardArea.AreaType.INDOOR_DARK) {
                throw new IllegalArgumentException("Only INDOOR areas can have edge connections");
            }
        }
        connections.add(connection);
    }

    public void removeEdgeConnection(UUID areaA, com.zombicide.missiongen.model.areas.Direction direction) {
        connections.removeIf(
                connection -> connection.getAreaAId().equals(areaA) && connection.getDirection() == direction);
    }

    public List<BoardArea> getAreas() {
        return areas;
    }

    public List<BoardAreaConnection> getConnections() {
        return connections;
    }

    public void removeArea(UUID id) {
        areas.removeIf(area -> area.getAreaId().equals(id));
    }

    public void removeConnection(UUID idA, UUID idB) {
        connections
                .removeIf(connection -> connection.getAreaAId().equals(idA) && connection.getAreaBId().equals(idB));
    }

    public boolean isOverlap(BoardArea newArea) {
        for (BoardArea area : areas) {
            if (area.isAreaOverlap(newArea)) {
                return true;
            }
        }
        return false;
    }

    public void splitAreaHorizontally(UUID areaId, int splitPoint) {
        BoardArea area = areas.stream().filter(a -> a.getAreaId().equals(areaId)).findFirst().orElse(null);
        if (area == null) {
            return;
        }

        // Remove original area first
        areas.remove(area);

        // Create new areas with new UUIDs and clear assets
        BoardArea topArea = BoardAreaFactiory.createBoardIndoorArea(area.getTopLeft(),
                new Point(area.getBottomRight().x, splitPoint));
        topArea.setBoardGameAssets(new ArrayList<>()); // Clear assets
        areas.add(topArea);

        BoardArea bottomArea = BoardAreaFactiory.createBoardIndoorArea(new Point(area.getTopLeft().x,
                splitPoint), area.getBottomRight());
        bottomArea.setBoardGameAssets(new ArrayList<>()); // Clear assets
        areas.add(bottomArea);

        logger.info("Split area {} horizontally into {} (top) and {} (bottom)", areaId, topArea.getAreaId(),
                bottomArea.getAreaId());
    }

    public void splitAreaVertically(UUID areaId, int splitPoint) {
        BoardArea area = areas.stream().filter(a -> a.getAreaId().equals(areaId)).findFirst().orElse(null);
        if (area == null) {
            return;
        }

        // Remove original area first
        areas.remove(area);

        // Create new areas with new UUIDs and clear assets
        BoardArea leftArea = BoardAreaFactiory.createBoardIndoorArea(area.getTopLeft(),
                new Point(splitPoint, area.getBottomRight().y));
        leftArea.setBoardGameAssets(new ArrayList<>()); // Clear assets
        areas.add(leftArea);

        BoardArea rightArea = BoardAreaFactiory.createBoardIndoorArea(new Point(splitPoint,
                area.getTopLeft().y), area.getBottomRight());
        rightArea.setBoardGameAssets(new ArrayList<>()); // Clear assets
        areas.add(rightArea);

        logger.info("Split area {} vertically into {} (left) and {} (right)", areaId, leftArea.getAreaId(),
                rightArea.getAreaId());
    }

    public BoardArea overlapWith(BoardArea area) {
        for (BoardArea existingArea : areas) {
            if (existingArea.isAreaOverlap(area)) {
                return existingArea;
            }
        }
        return null;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public Image getImage() {
        return backgroundImage;
    }

    public void shiftAreas(int x, int y) {
        for (BoardArea area : areas) {
            area.shiftArea(x, y);
        }
    }

    public void rotate() {
        rotateBoardAreas();
        rotateStreetAreas();
        rotateImage();
    }

    private void rotateStreetAreas() {

        List<AreaLocation> streetLocations = AreaLocation.getStreetLocations();
        List<BoardArea> streetAreas = areas.stream()
                .filter(area -> streetLocations.contains(area.getAreaLocation()))
                .collect(Collectors.toList());
        AreaLocation[] newLocations = new AreaLocation[streetAreas.size()];
        int t = 0;
        for (BoardArea streetArea : streetAreas) {
            AreaLocation location = streetArea.getAreaLocation();
            AreaLocation newLocation = location.rotate();
            newLocations[t] = newLocation;
            t++;
            streetArea.setAreaLocation(newLocation);
        }
        logger.info("New Locations are {}", Arrays.toString(newLocations));
    }

    // rotate the boardareas 90 degrees clockwise wirh the pivot point in the center
    // of the boardarea
    private void rotateBoardAreas() {
        for (BoardArea area : areas) {
            int oldX = area.getTopLeft().x;
            int oldY = area.getTopLeft().y;
            int oldWidth = area.getWidth();
            int oldHeight = area.getHeight();

            int newX = this.height - (oldY + oldHeight);
            int newY = oldX;
            int newWidth = oldHeight;
            int newHeight = oldWidth;

            area.setTopLeft(new java.awt.Point(newX, newY));
            area.setWidth(newWidth);
            area.setHeight(newHeight);
        }
        // Swap board dimensions
        int temp = this.width;
        this.width = this.height;
        this.height = temp;
    }

    // rotate the image 90 degrees clockwise
    private void rotateImage() {
        if (backgroundImage == null) {
            return;
        }

        int w = backgroundImage.getWidth(null);
        int h = backgroundImage.getHeight(null);

        java.awt.image.BufferedImage newImage = new java.awt.image.BufferedImage(h, w,
                java.awt.image.BufferedImage.TYPE_INT_ARGB);
        java.awt.Graphics2D g2d = newImage.createGraphics();

        AffineTransform at = new AffineTransform();
        at.translate((h - w) / 2.0, (w - h) / 2.0);
        at.rotate(Math.toRadians(90), w / 2.0, h / 2.0);

        g2d.setTransform(at);
        g2d.drawImage(backgroundImage, 0, 0, null);
        g2d.dispose();

        this.backgroundImage = newImage;
    }

    public void setAreas(List<BoardArea> areas) {
        this.areas = areas;
    }

    public void setConnections(List<BoardAreaConnection> connections) {
        this.connections = connections;
    }

    public boolean hasAreaLocation(AreaLocation areaLocation) {
        for (BoardArea area : areas) {
            if (area.getAreaLocation() == areaLocation) {
                return true;
            }
        }
        return false;
    }

    public BoardArea getAreaById(UUID id) {
        for (BoardArea area : areas) {
            if (area.getAreaId().equals(id)) {
                return area;
            }
        }
        return null;
    }

    public BoardArea getAreaByAreaLocation(AreaLocation areaLocation) {
        for (BoardArea area : this.areas) {
            if (area.getAreaLocation() == areaLocation) {
                return area;
            }
        }
        return null;
    }

    public void addAreaLocation(AreaLocation areaLocation) {
        if (areaLocation == null || areaLocation == AreaLocation.OTHER) {
            return;
        }

        // Remove existing border area at this location if it exists
        BoardArea existingBorderArea = getAreaByAreaLocation(areaLocation);
        if (existingBorderArea != null) {
            // delete areas, there is only one area per location
            removeArea(existingBorderArea.getAreaId());
        }

        // Remove all INDOOR areas (areas with AreaLocation.OTHER and AreaType.INDOOR)
        List<BoardArea> indoorAreas = new ArrayList<>();
        for (BoardArea area : areas) {
            if (area.getAreaLocation() == AreaLocation.OTHER
                    && (area.getAreaType() == com.zombicide.missiongen.model.areas.BoardArea.AreaType.INDOOR_LIGHT
                            || area.getAreaType() == com.zombicide.missiongen.model.areas.BoardArea.AreaType.INDOOR_DARK)) {
                indoorAreas.add(area);
            }
        }

        // Remove indoor areas and their connections
        for (BoardArea indoorArea : indoorAreas) {
            UUID areaId = indoorArea.getAreaId();
            logger.info("Removing INDOOR area {} when adding STREET area {}", areaId, areaLocation);

            // Remove all connections involving this area
            connections.removeIf(
                    connection -> connection.getAreaAId().equals(areaId) || connection.getAreaBId().equals(areaId));

            // Remove the area
            removeArea(areaId);
        }

        // Create new border area
        BoardArea area = BoardAreaFactiory.createBoardBorderArea(areaLocation);
        addArea(area);

        logger.info("Added STREET area {} at location {}, removed {} INDOOR areas", area.getAreaId(), areaLocation,
                indoorAreas.size());
    }

    public BoardArea getAreaAtPoint(Point point) {
        for (BoardArea area : areas) {
            if (area.isPointInside(point)) {
                return area;
            }
        }
        return null;
    }

    public boolean isPointInArea(Point point) {
        return getAreaAtPoint(point) != null;
    }

    private BoardArea maximizeArea(Point point) {
        int tlx = getMinTopLeftXAvailablePoint(point);
        int tly = getMinTopLeftYAvailablePoint(point);
        int brx = getMinBottomRightXAvailablePoint(point);
        int bry = getMinBottomRightYAvailablePoint(point);
        return BoardAreaFactiory.createBoardIndoorArea(new Point(tlx, tly), new Point(brx, bry));
    }

    private int getMinTopLeftXAvailablePoint(Point point) {
        Point checkPoint = new Point(point.x, point.y);
        int tressholds = this.config.getPropertyAsInt("tile.area.corner_width");
        BoardArea area = getAreaAtPoint(checkPoint);
        while (area == null && point.x > tressholds) {
            checkPoint.x = checkPoint.x - tressholds;
            area = getAreaAtPoint(checkPoint);
        }
        if (area == null)
            return 0;
        return area.getBottomRight().x;
    }

    private int getMinTopLeftYAvailablePoint(Point point) {
        Point checkPoint = new Point(point.x, point.y);
        int tressholds = this.config.getPropertyAsInt("tile.area.corner_width");
        BoardArea area = getAreaAtPoint(checkPoint);
        while (area == null && checkPoint.y > tressholds) {
            checkPoint.y = checkPoint.y - tressholds;
            area = getAreaAtPoint(checkPoint);
        }
        if (area == null)
            return 0;
        return area.getBottomRight().y;
    }

    private int getMinBottomRightXAvailablePoint(Point point) {
        Point checkPoint = new Point(point.x, point.y);
        int tressholds = this.config.getPropertyAsInt("tile.area.corner_width");
        BoardArea area = getAreaAtPoint(checkPoint);
        while (area == null && checkPoint.x < this.config.getPropertyAsInt("tile.width") - tressholds) {
            checkPoint.x = checkPoint.x + tressholds;
            area = getAreaAtPoint(checkPoint);
        }
        if (area == null)
            return this.config.getPropertyAsInt("tile.width");
        return area.getTopLeft().x;
    }

    private int getMinBottomRightYAvailablePoint(Point point) {
        Point checkPoint = new Point(point.x, point.y);
        int tressholds = this.config.getPropertyAsInt("tile.area.corner_width");
        BoardArea area = getAreaAtPoint(checkPoint);
        while (area == null && checkPoint.y < this.config.getPropertyAsInt("tile.height") - tressholds) {
            checkPoint.y = checkPoint.y + tressholds;
            area = getAreaAtPoint(checkPoint);
        }
        if (area == null)
            return this.config.getPropertyAsInt("tile.height");
        return area.getTopLeft().y;
    }

    public BoardArea getNeighbourArea(BoardArea area, Direction direction) {
        Point point = area.getNeighbourPoint(direction);
        return this.getAreaAtPoint(point);
    }

    /**
     * Método para convertir y clonar una java.awt.Image en un nuevo BufferedImage.
     * 
     * @param image La imagen original a clonar.
     * @return Una nueva copia de la imagen como BufferedImage.
     */
    public static BufferedImage clonarImage(Image image) {
        // 1. Asegurarse de que la imagen esté completamente cargada.
        // (A menudo no es necesario si la imagen ya se está utilizando, pero es una
        // buena práctica).

        // 2. Crear el nuevo BufferedImage con las mismas dimensiones y tipo.
        int width = image.getWidth(null);
        int height = image.getHeight(null);

        // Definir el tipo de imagen (por ejemplo, RGB con 8 bits por color).
        // ARGB: Transparencia (Alpha), Rojo, Verde, Azul.
        BufferedImage bufferedImage = new BufferedImage(
                width,
                height,
                BufferedImage.TYPE_INT_ARGB);

        // 3. Obtener el contexto gráfico del nuevo BufferedImage.
        Graphics2D g2d = bufferedImage.createGraphics();

        // 4. Dibujar la imagen original en el nuevo BufferedImage.
        // Esto copia los píxeles de la original a la nueva.
        g2d.drawImage(image, 0, 0, null);

        // 5. Liberar los recursos gráficos.
        g2d.dispose();

        // El BufferedImage ahora es una copia independiente (clon) de la Image
        // original.
        return bufferedImage;
    }

    public String getBoardId() {
        return this.boardId;
    }

}
