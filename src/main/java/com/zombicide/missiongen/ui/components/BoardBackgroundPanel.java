package com.zombicide.missiongen.ui.components;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.LoggerFactory;

import com.zombicide.missiongen.config.ConfigLoader;
import com.zombicide.missiongen.model.areas.BoardArea;
import com.zombicide.missiongen.model.board.BaseBoard;
import com.zombicide.missiongen.ui.interfaces.BoardSelectionListener;

public class BoardBackgroundPanel extends ZoneDrawPanel {
    protected double scaleTileToPanel;
    protected double scalePanelToTile;
    private int imgXOffset = 0;
    private int imgYOffset = 0;
    private double currentRenderScale = 1.0;
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(BoardBackgroundPanel.class);

    public int getImgXOffset() {
        return imgXOffset;
    }

    public int getImgYOffset() {
        return imgYOffset;
    }

    public double getCurrentRenderScale() {
        return currentRenderScale;
    }

    private final ConfigLoader config;
    private final List<BoardArea> selectedAreas;
    private final List<BoardSelectionListener> selectionListeners;
    private final List<com.zombicide.missiongen.ui.interfaces.BoardChangeListener> boardChangeListeners;

    private boolean areaDrawingVisible = true;
    private boolean areaIdsVisible = true;

    public BoardBackgroundPanel() {
        this.config = ConfigLoader.getInstance();
        this.selectedAreas = new ArrayList<>();
        this.selectionListeners = new ArrayList<>();
        this.boardChangeListeners = new ArrayList<>();
        setupMouseListeners();

    }

    private void setupMouseListeners() {
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1) {
                    Point clickPoint = e.getPoint();
                    if (e.isShiftDown()) {
                        onAlternativeLeftClick(clickPoint);
                    } else {
                        onLeftClick(clickPoint);
                    }
                }
            }
        });
    }

    protected void onLeftClick(Point point) {
        BoardArea clickedArea = getBoardAreaAtPoint(point);
        if (clickedArea != null) {
            // Normal click: clear selection and select only this area
            selectedAreas.clear();
            selectedAreas.add(clickedArea);
            logger.info("Left click on area ID: {} (selected)", clickedArea.getAreaId());
            notifySelectionChanged();
        } else {
            // Click on empty space: clear selection
            selectedAreas.clear();
            logger.info("Left click at: {} (selection cleared)", point);
            notifySelectionChanged();
        }
        repaint(); // Repaint to show selection changes
    }

    protected void onAlternativeLeftClick(Point point) {
        BoardArea clickedArea = getBoardAreaAtPoint(point);
        if (clickedArea != null) {
            // SHIFT + click: toggle area in selection
            if (selectedAreas.contains(clickedArea)) {
                selectedAreas.remove(clickedArea);
                logger.info("SHIFT + Left click on area ID: {} (deselected)", clickedArea.getAreaId());
            } else {
                selectedAreas.add(clickedArea);
                logger.info("SHIFT + Left click on area ID: {} (selected)", clickedArea.getAreaId());
            }
            notifySelectionChanged();
        } else {
            // SHIFT + click on empty space: clear selection
            selectedAreas.clear();
            logger.info("SHIFT + Left click at: {} (selection cleared)", point);
            notifySelectionChanged();
        }
        repaint(); // Repaint to show selection changes
    }

    protected BoardArea getBoardAreaAtPoint(Point panelPoint) {
        if (this.getBoard() == null) {
            return null;
        }

        // Convert panel coordinates to tile coordinates
        // panelX = tileX * scale + offsetX
        // tileX = (panelX - offsetX) / scale

        int tileX = (int) ((panelPoint.x - imgXOffset) / currentRenderScale);
        int tileY = (int) ((panelPoint.y - imgYOffset) / currentRenderScale);
        Point tilePoint = new Point(tileX, tileY);

        //Point tilePoint = convertPanelToTileCoordinates(panelPoint);

        return this.getBoard().getAreaAtPoint(tilePoint);
    }


    public Point convertPanelMouseToBaordCoordinates(Point panelPoint) {
        // Convert panel coordinates to board coordinates
        // panelX = boardX * scale + offsetX
        // boardX = (panelX - offsetX) / scale
        
        int boardX = (int) ((panelPoint.x - imgXOffset) / currentRenderScale);
        int boardY = (int) ((panelPoint.y - imgYOffset) / currentRenderScale);
        
        return new Point(boardX, boardY);
    }

    public boolean isAreaDrawingVisible() {
        return areaDrawingVisible;
    }

    protected void setAreaDrawingVisible(boolean visible) {
        this.areaDrawingVisible = visible;
        this.repaint();
    }

    protected void setAreaIdsVisible(boolean visible) {
        this.areaIdsVisible = visible;
        this.repaint();
    }

    public boolean isAreaIdsVisible() {
        return areaIdsVisible;
    }

    @Override
    public void setBoard(BaseBoard board) {
        super.setBoard(board);
        this.selectedAreas.clear(); // Clear selection when board changes
        // Scale and offsets will be calculated in paintComponent
        this.repaint();
    }

    public List<BoardArea> getSelectedAreas() {
        return new ArrayList<>(selectedAreas); // Return a copy to prevent external modification
    }

    public void addSelectionListener(BoardSelectionListener listener) {
        if (listener != null && !selectionListeners.contains(listener)) {
            selectionListeners.add(listener);
        }
    }

    public void removeSelectionListener(BoardSelectionListener listener) {
        selectionListeners.remove(listener);
    }

    private void notifySelectionChanged() {
        List<BoardArea> selectedAreasCopy = new ArrayList<>(selectedAreas);
        for (BoardSelectionListener listener : selectionListeners) {
            listener.onSelectionChanged(selectedAreasCopy);
        }
    }

    public void addBoardChangeListener(com.zombicide.missiongen.ui.interfaces.BoardChangeListener listener) {
        if (listener != null && !boardChangeListeners.contains(listener)) {
            boardChangeListeners.add(listener);
        }
    }

    public void removeBoardChangeListener(com.zombicide.missiongen.ui.interfaces.BoardChangeListener listener) {
        boardChangeListeners.remove(listener);
    }

    public void notifyAreasChanged() {
        for (com.zombicide.missiongen.ui.interfaces.BoardChangeListener listener : boardChangeListeners) {
            listener.onAreasChanged();
        }
    }

    public void notifyConnectionsChanged() {
        for (com.zombicide.missiongen.ui.interfaces.BoardChangeListener listener : boardChangeListeners) {
            listener.onConnectionsChanged();
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (this.getBoard() == null || this.getBoard().getImage() == null) {
            return;
        }

        Image image = this.getBoard().getImage();

        // --- 1. Get Dimensions ---
        int panelWidth = getWidth();
        int panelHeight = getHeight();
        int boardWidth = this.getBoard().getWidth();
        int boardHeight = this.getBoard().getHeight();

        // --- 2. Calculate the Scaling Factor ---
        // Find the ratio for both dimensions
        double widthRatio = (double) panelWidth / boardWidth;
        double heightRatio = (double) panelHeight / boardHeight;

        // Use the smaller ratio to ensure the entire image fits within the panel
        double scale = Math.min(widthRatio, heightRatio);

        // --- 3. Calculate New Dimensions ---
        int scaledWidth = (int) (boardWidth * scale);
        int scaledHeight = (int) (boardHeight * scale);

        // --- 4. Center the Image (Optional) ---
        // Calculate the offsets to center the image
        int x = (panelWidth - scaledWidth) / 2;
        int y = (panelHeight - scaledHeight) / 2;

        // Store render parameters for mouse interaction and area drawing
        this.currentRenderScale = scale;
        this.imgXOffset = x;
        this.imgYOffset = y;

        // --- 5. Draw the Scaled Image ---
        // Use the drawImage method with target dimensions:
        // g.drawImage(Image img, int x, int y, int width, int height, ImageObserver
        // observer)
        g.drawImage(image, x, y, scaledWidth, scaledHeight, this);

        if (this.isAreaDrawingVisible()) {
            drawBoardAreas(g);
        }
    }

    private void drawBoardAreas(Graphics g) {
        if (this.getBoard() == null) {
            return;
        }
        for (BoardArea boardArea : this.getBoard().getAreas()) {
            drawArea(g, boardArea);
        }
    }

    public void drawArea(Graphics g, BoardArea boardArea) {
        Point topLeft = new Point(
                (int) (boardArea.getTopLeft().x * currentRenderScale) + imgXOffset,
                (int) (boardArea.getTopLeft().y * currentRenderScale) + imgYOffset);

        Graphics2D g2d = (Graphics2D) g;
        g2d.setStroke(new BasicStroke(2));

        // Check if this area is selected
        boolean isSelected = selectedAreas.contains(boardArea);

        Color areaColor = isSelected
                ? config.getPropertyAsColor("tile.area.selected.color", Color.YELLOW)
                : config.getPropertyAsColor("tile.area.color", Color.BLUE);

        g.setColor(areaColor);
        g.drawRect(topLeft.x, topLeft.y,
                (int) (boardArea.getWidth() * currentRenderScale),
                (int) (boardArea.getHeight() * currentRenderScale));

        // Draw ID, the last 3 characters
        if (!isAreaIdsVisible()) {
            return;
        }
        String areaId = boardArea.getAreaId().toString();
        String idText = areaId.substring(areaId.length() - 3);
        g.setFont(new Font("Arial", Font.BOLD, 56));
        FontMetrics fm = g.getFontMetrics();
        int textWidth = fm.stringWidth(idText);
        int textHeight = fm.getAscent();

        int centerX = (int) (topLeft.x + (boardArea.getWidth() * currentRenderScale - textWidth) / 2);
        int centerY = (int) (topLeft.y + (boardArea.getHeight() * currentRenderScale + textHeight) / 2);

        g.drawString(idText, centerX, centerY);
    }

    public void deleteSelectedAreas() {
        for (BoardArea area : this.getSelectedAreas()) {
            this.getBoard().removeArea(area.getAreaId());
        }
        this.selectedAreas.clear();
        this.repaint();
        this.notifyAreasChanged();
    }

   
}
