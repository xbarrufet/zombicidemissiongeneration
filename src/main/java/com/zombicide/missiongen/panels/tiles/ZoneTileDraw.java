package com.zombicide.missiongen.panels.tiles;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zombicide.missiongen.model.areas.AreaLocation;
import com.zombicide.missiongen.model.areas.BoardArea;
import com.zombicide.missiongen.model.board.BaseBoard;
import com.zombicide.missiongen.panels.components.BoardBackgroundPanel;

public class ZoneTileDraw extends BoardBackgroundPanel {
    private static final Logger logger = LoggerFactory.getLogger(ZoneTileDraw.class);

    public enum DrawMode {
        NORMAL, SPLIT_HORIZONTAL, SPLIT_VERTICAL
    }

    private DrawMode currentMode = DrawMode.NORMAL;
    private BoardArea areaToSplit = null;
    private boolean isShiftPressed = false;

    // Snapping and Guides
    private Point currentMousePosition = null;
    private Integer snappedGuide = null;
    private static final int SNAP_DISTANCE = 10;

    public ZoneTileDraw() {
        super();

        // Add KeyListener for keyboard events
        setupKeyListener();
        setupKeyReleasedListener();

        // Make panel focusable to receive key events
        setFocusable(true);

        // Request focus when clicked to ensure keyboard events work
        addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mousePressed(java.awt.event.MouseEvent e) {
                requestFocusInWindow();

                // Handle split mode clicks
                if (currentMode != DrawMode.NORMAL) {
                    handleSplitClick(e.getPoint());
                    e.consume(); // Prevent other listeners from processing this event
                }
            }
        });

        // Add MouseMotionListener for guides and snapping
        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                handleMouseMove(e);
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                handleMouseMove(e);
            }
        });
    }

    private void setupKeyListener() {
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                handleKeyPress(e);
            }
        });
    }

    private void setupKeyReleasedListener() {
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                handleKeyReleased(e);
            }

            private void handleKeyReleased(KeyEvent e) {
                logger.info("Key released: {} (code: {})", KeyEvent.getKeyText(e.getKeyCode()), e.getKeyCode());
                if (e.getKeyCode() == KeyEvent.VK_SHIFT) {
                    isShiftPressed = false;
                }
            }
        });
    }

    private void handleKeyPress(KeyEvent e) {
        logger.info("Key pressed: {} (code: {})", KeyEvent.getKeyText(e.getKeyCode()), e.getKeyCode());

        // Track SHIFT key state
        if (e.getKeyCode() == KeyEvent.VK_SHIFT) {
            isShiftPressed = true;
        }

        // Example: Handle specific keys
        switch (e.getKeyCode()) {
            case KeyEvent.VK_DELETE:
                logger.info("Delete key pressed");
                this.deleteSelectedAreas();
                break;
            case KeyEvent.VK_BACK_SPACE:
                logger.info("Delete key pressed");
                this.deleteSelectedAreas();
                break;
            case KeyEvent.VK_Q:
                addNewLocatedArea(AreaLocation.TOP_LEFT_STREET);
                break;
            case KeyEvent.VK_W:
                addNewLocatedArea(AreaLocation.TOP_MIDDLE_STREET);
                break;
            case KeyEvent.VK_E:
                addNewLocatedArea(AreaLocation.TOP_RIGHT_STREET);
                break;
            case KeyEvent.VK_A:
                addNewLocatedArea(AreaLocation.MIDDLE_LEFT_STREET);
                break;
            case KeyEvent.VK_D:
                addNewLocatedArea(AreaLocation.MIDDLE_RIGHT_STREET);
                break;
            case KeyEvent.VK_Z:
                addNewLocatedArea(AreaLocation.BOTTOM_LEFT_STREET);
                break;
            case KeyEvent.VK_X:
                addNewLocatedArea(AreaLocation.BOTTOM_MIDDLE_STREET);
                break;
            case KeyEvent.VK_C:
                addNewLocatedArea(AreaLocation.BOTTOM_RIGHT_STREET);
                break;
            case KeyEvent.VK_S:
                logger.info("S key pressed - filling available areas");
                fillAvailableAreas();
                break;
            case KeyEvent.VK_ESCAPE:
                if (currentMode != DrawMode.NORMAL) {
                    logger.info("ESC key pressed - canceling split mode");
                    exitSplitMode();
                }
                break;
            case KeyEvent.VK_SHIFT:
                logger.info("SHIFT key pressed - merging street areas");
                this.isShiftPressed = true;
                break;
            default:
                // Handle other keys or specific characters
                if (e.getKeyChar() != KeyEvent.CHAR_UNDEFINED) {
                    logger.info("Character typed: {}", e.getKeyChar());
                }
                break;
        }
    }

    private void addNewLocatedArea(AreaLocation areaLocation) {
        logger.info("Adding new located area: {}", areaLocation);
        this.getBoard().addAreaLocation(areaLocation);
        this.repaint();
        this.notifyAreasChanged();
    }

    private void fillAvailableAreas() {
        if (this.getBoard() == null) {
            logger.warn("Cannot fill available areas: no board loaded");
            return;
        }

        logger.info("Filling available areas");
        this.getBoard().addAvailableArea();
        this.repaint();
        this.notifyAreasChanged();
    }

    public void enterSplitMode(DrawMode mode, BoardArea area) {
        this.currentMode = mode;
        this.areaToSplit = area;
        this.snappedGuide = null;
        this.currentMousePosition = null;
        logger.info("Entered split mode: {}, area: {}", mode, area.getAreaId());

        // Change cursor to indicate split mode
        updateCursor();
    }

    private void updateCursor() {
        if (currentMode == DrawMode.NORMAL) {
            setCursor(Cursor.getDefaultCursor());
            return;
        }
        if (snappedGuide != null) {
            // Indicate snapping
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            return;
        }
        if (currentMode == DrawMode.SPLIT_HORIZONTAL) {
            setCursor(Cursor.getPredefinedCursor(Cursor.N_RESIZE_CURSOR));
        } else if (currentMode == DrawMode.SPLIT_VERTICAL) {
            setCursor(Cursor.getPredefinedCursor(Cursor.E_RESIZE_CURSOR));
        }
    }

    private void handleMouseMove(MouseEvent e) {
        if (currentMode == DrawMode.NORMAL || areaToSplit == null || getBoard() == null) {
            return;
        }

        Point panelPoint = e.getPoint();
        this.currentMousePosition = panelPoint;

        // Check SHIFT key state from the mouse event
        isShiftPressed = e.isShiftDown();

        // Convert to tile coordinates
        int tilePos = 0;
        if (currentMode == DrawMode.SPLIT_HORIZONTAL) {
            tilePos = (int) (panelPoint.y * scalePanelToTile);
        } else {
            tilePos = (int) (panelPoint.x * scalePanelToTile);
        }

        // Check for guides
        List<Integer> guides = getGuides();
        Integer closestGuide = null;
        int minDistance = Integer.MAX_VALUE;

        for (int guide : guides) {
            int distance = Math.abs(guide - tilePos);
            if (distance < minDistance) {
                minDistance = distance;
                closestGuide = guide;
            }
        }

        // Snap if close enough (but not if SHIFT is pressed)
        if (!isShiftPressed && closestGuide != null && minDistance <= SNAP_DISTANCE) {
            if (snappedGuide == null || !snappedGuide.equals(closestGuide)) {
                snappedGuide = closestGuide;
                updateCursor();
            }
        } else {
            if (snappedGuide != null) {
                snappedGuide = null;
                updateCursor();
            }
        }

        repaint();
    }

    private List<Integer> getGuides() {
        if (getBoard() == null)
            return Collections.emptyList();

        List<Integer> guides = new ArrayList<>();

        // Get guides from ALL areas (including indoor ones created by splits)
        List<BoardArea> allAreas = getBoard().getAreas();

        for (BoardArea area : allAreas) {
            // Skip the area currently being split to avoid snapping to itself (optional,
            // but
            // good for UX)
            if (areaToSplit != null && area.getAreaId() == areaToSplit.getAreaId()) {
                continue;
            }

            // Skip fixed street areas (border areas), only snap to other splits (internal
            // areas)
            if (area.getAreaLocation() != AreaLocation.OTHER) {
                continue;
            }

            if (currentMode == DrawMode.SPLIT_HORIZONTAL) {
                guides.add(area.getTopLeft().y);
                guides.add(area.getBottomRight().y);
            } else {
                guides.add(area.getTopLeft().x);
                guides.add(area.getBottomRight().x);
            }
        }

        return guides.stream().distinct().sorted().collect(Collectors.toList());
    }

    private void handleSplitClick(Point panelPoint) {
        if (this.getBoard() == null || areaToSplit == null) {
            logger.warn("Cannot handle split click: board or area is null");
            exitSplitMode();
            return;
        }

        // Convert panel coordinates to tile coordinates
        Point tilePoint = new Point(
                (int) (panelPoint.x * scalePanelToTile),
                (int) (panelPoint.y * scalePanelToTile));

        // Validate that the point is inside the area
        if (!areaToSplit.isPointInside(tilePoint)) {
            logger.warn("Split point is outside the selected area");
            exitSplitMode();
            return;
        }

        int minMargin = 20; // Minimum margin from edges

        if (currentMode == DrawMode.SPLIT_HORIZONTAL) {
            // Use snapped value if available, otherwise mouse position
            int splitY = (snappedGuide != null) ? snappedGuide : tilePoint.y;

            // Validate margins
            if (splitY - areaToSplit.getTopLeft().y < minMargin ||
                    areaToSplit.getBottomRight().y - splitY < minMargin) {
                logger.warn("Split point too close to edge (margin: {}px)", minMargin);
                exitSplitMode();
                return;
            }

            logger.info("Splitting area {} horizontally at Y={}", areaToSplit.getAreaId(), splitY);
            this.getBoard().splitAreaHorizontally(areaToSplit.getAreaId(), splitY);

        } else if (currentMode == DrawMode.SPLIT_VERTICAL) {
            // Use snapped value if available, otherwise mouse position
            int splitX = (snappedGuide != null) ? snappedGuide : tilePoint.x;

            // Validate margins
            if (splitX - areaToSplit.getTopLeft().x < minMargin ||
                    areaToSplit.getBottomRight().x - splitX < minMargin) {
                logger.warn("Split point too close to edge (margin: {}px)", minMargin);
                exitSplitMode();
                return;
            }

            logger.info("Splitting area {} vertically at X={}", areaToSplit.getAreaId(), splitX);
            this.getBoard().splitAreaVertically(areaToSplit.getAreaId(), splitX);
        }

        exitSplitMode();
        this.repaint();
        this.notifyAreasChanged();
    }

    private void exitSplitMode() {
        this.currentMode = DrawMode.NORMAL;
        this.areaToSplit = null;
        this.snappedGuide = null;
        this.currentMousePosition = null;
        setCursor(Cursor.getDefaultCursor());
        logger.info("Exited split mode");
        repaint();
    }

    @Override
    public void setBoard(BaseBoard board) {
        super.setBoard(board);
        // Calculate scale for coordinate conversion
        if (board != null) {
            this.scalePanelToTile = (double) board.getWidth() / this.getWidth();
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Draw split line guide
        if (currentMode != DrawMode.NORMAL && areaToSplit != null
                && (currentMousePosition != null || snappedGuide != null)) {
            Graphics2D g2d = (Graphics2D) g;

            if (currentMode == DrawMode.SPLIT_HORIZONTAL) {
                int y = (snappedGuide != null) ? (int) (snappedGuide * scaleTileToPanel) : currentMousePosition.y;

                // Draw line across the area
                int startX = (int) (areaToSplit.getTopLeft().x * scaleTileToPanel);
                int endX = (int) (areaToSplit.getBottomRight().x * scaleTileToPanel);

                g2d.setColor(snappedGuide != null ? Color.GREEN : Color.RED);
                g2d.setStroke(new BasicStroke(2, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f,
                        new float[] { 10.0f }, 0.0f));
                g2d.drawLine(startX, y, endX, y);

            } else {
                int x = (snappedGuide != null) ? (int) (snappedGuide * scaleTileToPanel) : currentMousePosition.x;

                // Draw line across the area
                int startY = (int) (areaToSplit.getTopLeft().y * scaleTileToPanel);
                int endY = (int) (areaToSplit.getBottomRight().y * scaleTileToPanel);

                g2d.setColor(snappedGuide != null ? Color.GREEN : Color.RED);
                g2d.setStroke(new BasicStroke(2, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f,
                        new float[] { 10.0f }, 0.0f));
                g2d.drawLine(x, startY, x, endY);
            }
        }
    }
}
