package com.zombicide.missiongen.panels.tiles;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.BasicStroke;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zombicide.missiongen.model.Board;
import com.zombicide.missiongen.model.BoardArea;
import com.zombicide.missiongen.panels.components.AreaDrawer;
import com.zombicide.missiongen.panels.components.BoardBackgroundPanel;
import com.zombicide.missiongen.panels.interfaces.AreaDrawerListener;

public class ZoneTileDraw extends BoardBackgroundPanel implements AreaDrawerListener {
    private static final Logger logger = LoggerFactory.getLogger(ZoneTileDraw.class);

    private AreaDrawer areaDrawer;
    private double scalePanelToTile;

    public ZoneTileDraw() {
        super();

        // Initialize AreaDrawer for drawing rectangles
        areaDrawer = new AreaDrawer(this, this);

        // Add AreaDrawer as mouse listener (for drag operations)
        addMouseListener(areaDrawer);
        addMouseMotionListener(areaDrawer);
    }

    @Override
    public void setBoard(Board board) {
        super.setBoard(board);
        // Calculate scale for coordinate conversion
        if (board != null) {
            this.scalePanelToTile = (double) board.getWidth() / this.getWidth();
        }
    }

    @Override
    public void onAreaDrawn(int topLeftX, int topLeftY, int bottomRightX, int bottomRightY) {
        if (this.getBoard() == null) {
            logger.warn("Cannot create area: no board loaded");
            return;
        }

        // Convert panel coordinates to tile coordinates
        int tileX = (int) (topLeftX * scalePanelToTile);
        int tileY = (int) (topLeftY * scalePanelToTile);
        int tileWidth = (int) ((bottomRightX - topLeftX) * scalePanelToTile);
        int tileHeight = (int) ((bottomRightY - topLeftY) * scalePanelToTile);

        // Get new area ID
        int newAreaId = this.getBoard().getNewAreaId();

        // Create new BoardArea
        Point origin = new Point(tileX, tileY);
        BoardArea newArea = new BoardArea(newAreaId, origin, tileWidth, tileHeight, BoardArea.AreaType.BASIC);

        // Add to board if there is no overlap with other areas
        if (!this.getBoard().isOverlap(newArea)) {
            this.getBoard().addArea(newArea);

            logger.info("Created new area: ID={}, origin=({},{}), size={}x{}",
                    newAreaId, tileX, tileY, tileWidth, tileHeight);
        } else {
            logger.warn("Cannot create area: overlaps with existing area at origin=({},{}), size={}x{}",
                    tileX, tileY, tileWidth, tileHeight);
        }

        // Repaint to show the new area
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Draw the current drawing rectangle if user is dragging
        if (areaDrawer != null && areaDrawer.isDrawing()) {
            Rectangle drawingArea = areaDrawer.getCurrentDrawingArea();
            if (drawingArea != null) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setColor(new Color(0, 255, 0, 100)); // Semi-transparent green
                g2d.fillRect(drawingArea.x, drawingArea.y, drawingArea.width, drawingArea.height);

                g2d.setColor(Color.GREEN);
                g2d.setStroke(new BasicStroke(2, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER,
                        10.0f, new float[] { 5.0f }, 0.0f)); // Dashed line
                g2d.drawRect(drawingArea.x, drawingArea.y, drawingArea.width, drawingArea.height);
            }
        }
    }
}
