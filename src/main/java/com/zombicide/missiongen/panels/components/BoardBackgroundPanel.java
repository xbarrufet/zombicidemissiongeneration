package com.zombicide.missiongen.panels.components;

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

import javax.swing.JPanel;

import org.slf4j.LoggerFactory;

import com.zombicide.missiongen.config.ConfigLoader;
import com.zombicide.missiongen.model.Board;
import com.zombicide.missiongen.model.BoardArea;
import com.zombicide.missiongen.panels.interfaces.BoardSelectionListener;

public class BoardBackgroundPanel extends ZoneDrawPanel {
    private double scaleTileToPanel;
    private double scalePanelToTile;
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(BoardBackgroundPanel.class);

    private final ConfigLoader config;
    private final List<BoardArea> selectedAreas;
    private final List<BoardSelectionListener> selectionListeners;

    public BoardBackgroundPanel() {
        this.config = ConfigLoader.getInstance();
        this.selectedAreas = new ArrayList<>();
        this.selectionListeners = new ArrayList<>();
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
            logger.info("Left click on area ID: {} (selected)", clickedArea.getId());
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
                logger.info("SHIFT + Left click on area ID: {} (deselected)", clickedArea.getId());
            } else {
                selectedAreas.add(clickedArea);
                logger.info("SHIFT + Left click on area ID: {} (selected)", clickedArea.getId());
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

    private BoardArea getBoardAreaAtPoint(Point panelPoint) {
        if (this.getBoard() == null) {
            return null;
        }

        // Convert panel coordinates to tile coordinates
        Point tilePoint = new Point(
                (int) (panelPoint.x * scalePanelToTile),
                (int) (panelPoint.y * scalePanelToTile));

        // Find the area that contains this point
        for (BoardArea area : this.getBoard().getAreas()) {
            if (area.isPointInside(tilePoint)) {
                return area;
            }
        }

        return null;
    }

    @Override
    public void setBoard(Board board) {
        super.setBoard(board);
        this.selectedAreas.clear(); // Clear selection when board changes
        this.scalePanelToTile = (double) this.getBoard().getWidth() / this.getWidth();
        logger.info("Board with width {} and height {}", this.getBoard().getWidth(), this.getBoard().getHeight());
        logger.info("Panel with width {} and height {}", this.getWidth(), this.getHeight());
        logger.info("Scale tile to panel: {}", this.scaleTileToPanel);
        this.scaleTileToPanel = 1 / this.scalePanelToTile;
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
        int imageWidth = image.getWidth(this);
        int imageHeight = image.getHeight(this);

        // --- 2. Calculate the Scaling Factor ---
        // Find the ratio for both dimensions
        double widthRatio = (double) panelWidth / imageWidth;
        double heightRatio = (double) panelHeight / imageHeight;

        // Use the smaller ratio to ensure the entire image fits within the panel
        double scale = Math.min(widthRatio, heightRatio);

        // --- 3. Calculate New Dimensions ---
        int scaledWidth = (int) (imageWidth * scale);
        int scaledHeight = (int) (imageHeight * scale);

        // --- 4. Center the Image (Optional) ---
        // Calculate the offsets to center the image
        int x = (panelWidth - scaledWidth) / 2;
        int y = (panelHeight - scaledHeight) / 2;

        // --- 5. Draw the Scaled Image ---
        // Use the drawImage method with target dimensions:
        // g.drawImage(Image img, int x, int y, int width, int height, ImageObserver
        // observer)
        g.drawImage(image, x, y, scaledWidth, scaledHeight, this);

        drawBoardAreas(g);
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
        Point topLeft = new Point((int) (boardArea.getOrigin().x * scaleTileToPanel),
                (int) (boardArea.getOrigin().y * scaleTileToPanel));

        Graphics2D g2d = (Graphics2D) g;
        g2d.setStroke(new BasicStroke(2));

        // Check if this area is selected
        boolean isSelected = selectedAreas.contains(boardArea);

        Color areaColor = isSelected
                ? config.getPropertyAsColor("tile.area.selected.color", Color.YELLOW)
                : config.getPropertyAsColor("tile.area.color", Color.BLUE);

        g.setColor(areaColor);
        g.drawRect(topLeft.x, topLeft.y, (int) (boardArea.getWidth() * scaleTileToPanel),
                (int) (boardArea.getHeight() * scaleTileToPanel));

        // Draw ID
        String idText = String.valueOf(boardArea.getId());
        g.setFont(new Font("Arial", Font.BOLD, 56));
        FontMetrics fm = g.getFontMetrics();
        int textWidth = fm.stringWidth(idText);
        int textHeight = fm.getAscent();

        int centerX = (int) (topLeft.x + (boardArea.getWidth() * scaleTileToPanel - textWidth) / 2);
        int centerY = (int) (topLeft.y + (boardArea.getHeight() * scaleTileToPanel + textHeight) / 2);

        g.drawString(idText, centerX, centerY);
    }
}
