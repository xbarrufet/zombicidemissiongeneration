package com.zombicide.missiongen.ui.missions;

import java.awt.AlphaComposite;
import java.awt.Composite;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zombicide.missiongen.model.areas.BoardArea;
import com.zombicide.missiongen.model.areas.BoardAreaConnection;
import com.zombicide.missiongen.model.areas.Direction;
import com.zombicide.missiongen.model.board.MissionBoard;
import com.zombicide.missiongen.model.helpers.Rect;
import com.zombicide.missiongen.model.tokens.Token;
import com.zombicide.missiongen.model.tokens.TokenFactory;
import com.zombicide.missiongen.model.tokens.TokenType;
import com.zombicide.missiongen.model.tokens.tokenType.Door;
import com.zombicide.missiongen.ui.components.BoardBackgroundPanel;
import com.zombicide.missiongen.ui.interfaces.MissionPropertiesListener;

public class ZoneMissionDraw extends BoardBackgroundPanel implements MissionPropertiesListener {

    private static final Logger logger = LoggerFactory.getLogger(ZoneMissionDraw.class);

    private Token tokenToBeAdded;
    private Token selectedToken;
    private Point cursorPosition;
    float opacity = 0.5f;
    private BoardArea hoveredArea;
    private Point tokenPosition;

    private final int SNAP_DISTANCE = 5;

    private Point[] horizontalSnapp;
    private Point[] verticalSnapp;

    private Point snappedTokenPosition;

    enum SNAPPING_DIRECTION {
        UP, DOWN, LEFT, RIGHT, CENTER, CENTER_H, CENTER_V, NONE
    }

    private SNAPPING_DIRECTION[] snappingDirection;

    public ZoneMissionDraw() {
        super();
        setupMouseListeners();
        setupKeyListeners();
        setAreaIdsVisible(false);
        setAreaSelectionAllowed(false);
        this.snappingDirection = new SNAPPING_DIRECTION[2];
    }

    private void setupMouseListeners() {
        // Track cursor position when in selection mode
        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                if (inTokenToBeAdddedState()) {
                    cursorPosition = e.getPoint();
                    hoveredArea = getBoardAreaAtPoint(cursorPosition);
                    tokenPosition = convertPanelMouseToBaordCoordinates(cursorPosition);

                    if (hoveredArea != null) {
                        snappedTokenPosition = calculateSnappedPosition(tokenPosition, hoveredArea);
                        horizontalSnapp = getHorizontalSnappLine(snappedTokenPosition, hoveredArea);
                        verticalSnapp = getVerticalSnappLine(snappedTokenPosition, hoveredArea);
                    } else {
                        snappedTokenPosition = tokenPosition;
                        horizontalSnapp = null;
                        verticalSnapp = null;
                    }
                    repaint();
                }
            }
        });

        // Handle left click to place token
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (inTokenToBeAdddedState() && e.getButton() == MouseEvent.BUTTON1) {
                    if (snappedTokenPosition != null) {
                        placeToken(snappedTokenPosition.x, snappedTokenPosition.y);
                    } else {
                        placeToken(tokenPosition.x, tokenPosition.y);
                    }
                } else {
                    // will be used to select a token
                    cursorPosition = e.getPoint();
                    tokenPosition = convertPanelMouseToBaordCoordinates(cursorPosition);
                    selectedToken = getBoard().getTokenAtPoint(tokenPosition);
                    repaint();
                }
            }
        });
    }

    private Point calculateSnappedPosition(Point rawBoardPos, BoardArea area) {
        int newX = rawBoardPos.x;
        int newY = rawBoardPos.y;
        int tokenWidth = tokenToBeAdded.getShape().getWidth();
        int tokenHeight = tokenToBeAdded.getShape().getHeight();
        // Horizontal Snapping (Y-axis)
        this.snappingDirection[0] = SNAPPING_DIRECTION.NONE;
        this.snappingDirection[1] = SNAPPING_DIRECTION.NONE;
        // North (Top)
        if (Math.abs(rawBoardPos.y - (area.getTopLeft().y + tokenHeight / 2)) <= SNAP_DISTANCE) {
            newY = area.getTopLeft().y + tokenHeight / 2;
            snappingDirection[0] = SNAPPING_DIRECTION.UP;
        }
        // South (Bottom)
        else if (Math
                .abs(rawBoardPos.y - (area.getTopLeft().y + area.getHeight() - tokenHeight / 2)) <= SNAP_DISTANCE) {
            newY = area.getTopLeft().y + area.getHeight() - tokenHeight / 2;
            snappingDirection[0] = SNAPPING_DIRECTION.DOWN;
        }
        // Center Horizontal
        else if (Math.abs(rawBoardPos.y - (area.getTopLeft().y + area.getHeight() / 2)) <= SNAP_DISTANCE) {
            newY = area.getTopLeft().y + area.getHeight() / 2;
            snappingDirection[0] = SNAPPING_DIRECTION.CENTER_V;
        }

        // Vertical Snapping (X-axis)
        // West (Left)
        if (Math.abs(rawBoardPos.x - (area.getTopLeft().x + tokenWidth / 2)) <= SNAP_DISTANCE) {
            newX = area.getTopLeft().x + tokenWidth / 2;
            snappingDirection[1] = SNAPPING_DIRECTION.LEFT;
        }
        // East (Right)
        else if (Math.abs(rawBoardPos.x - (area.getTopLeft().x + area.getWidth() - tokenWidth / 2)) <= SNAP_DISTANCE) {
            newX = area.getTopLeft().x + area.getWidth() - tokenWidth / 2;
            snappingDirection[1] = SNAPPING_DIRECTION.RIGHT;
        }
        // Center Vertical
        else if (Math.abs(rawBoardPos.x - (area.getTopLeft().x + area.getWidth() / 2)) <= SNAP_DISTANCE) {
            newX = area.getTopLeft().x + area.getWidth() / 2;
            snappingDirection[1] = SNAPPING_DIRECTION.CENTER_H;
        }

        return new Point(newX, newY);
    }

    private void setupKeyListeners() {
        // Handle ESC to cancel token selection
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE && inTokenToBeAdddedState()) {
                    tokenToBeAdded = null;
                    cursorPosition = null;
                    snappedTokenPosition = null;
                    repaint();
                    logger.info("Token selection cancelled");
                }
                if (e.getKeyCode() == KeyEvent.VK_R && inTokenToBeAdddedState()) {
                    tokenToBeAdded.rotate();
                    // Recalculate snap after rotation if we are hovering
                    if (hoveredArea != null && tokenPosition != null) {
                        snappedTokenPosition = calculateSnappedPosition(tokenPosition, hoveredArea);
                        horizontalSnapp = getHorizontalSnappLine(snappedTokenPosition, hoveredArea);
                        verticalSnapp = getVerticalSnappLine(snappedTokenPosition, hoveredArea);
                    }
                    repaint();
                    logger.info("Token rotated {} {} ", tokenToBeAdded.getShape().getWidth(),
                            tokenToBeAdded.getShape().getHeight());
                }
            }
        });

        // Make panel focusable to receive key events
        setFocusable(true);
    }

    @Override
    public void onBoardAreasVisibilityUpdated(boolean visible) {
        this.setAreaDrawingVisible(visible);
    }

    public void setMissionBoard(MissionBoard board) {
        super.setBoard(board);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (getBoard() != null) {
            drawTokens(g);
        }

        // Draw token placeholder if in selection mode
        if (inTokenToBeAdddedState() && cursorPosition != null && tokenToBeAdded != null) {
            drawTokenPlaceHolder(g);
            drawSnappLines(g);
        }
    }

    private void drawSnappLines(Graphics g) {
        if (horizontalSnapp != null) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setColor(java.awt.Color.RED);
            g2d.drawLine(horizontalSnapp[0].x, horizontalSnapp[0].y, horizontalSnapp[1].x, horizontalSnapp[1].y);
            g2d.dispose();
        }
        if (verticalSnapp != null) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setColor(java.awt.Color.RED);
            g2d.drawLine(verticalSnapp[0].x, verticalSnapp[0].y, verticalSnapp[1].x, verticalSnapp[1].y);
            g2d.dispose();
        }
    }

    private void drawTokens(Graphics g) {
        if (getBoard() == null)
            return;
        for (com.zombicide.missiongen.model.tokens.Token token : getBoard().getTokens()) {
            java.awt.Point location = token.getLocation();
            if (location != null) {
                Point panelPoint = transformBoardPointsToPanel(location);

                double scale = getCurrentRenderScale();
                int width = (int) (token.getShape().getWidth() * scale);
                int height = (int) (token.getShape().getHeight() * scale);

                // Draw centered
                int x = panelPoint.x - width / 2;
                int y = panelPoint.y - height / 2;
                if (this.selectedToken != null && selectedToken.getId().equals(token.getId())) {
                    g.setColor(java.awt.Color.YELLOW);
                    g.drawRect(x, y, width, height);
                }

                g.drawImage(token.getImage(), x, y, width, height, null);
            }
        }
    }

    private boolean inTokenToBeAdddedState() {
        return this.tokenToBeAdded != null;
    }

    private void drawTokenPlaceHolder(Graphics g) {
        // Use snapped position if available, otherwise use raw cursor position
        Point drawPosition = (snappedTokenPosition != null) ? snappedTokenPosition
                : convertPanelMouseToBaordCoordinates(cursorPosition);

        // Get top-left position in board coordinates
        Point boardTopLeft = this.tokenToBeAdded.getShape().getTopLeftFromCenter(drawPosition);

        // Convert back to panel coordinates for drawing
        int panelX = (int) (boardTopLeft.x * getCurrentRenderScale()) + getImgXOffset();
        int panelY = (int) (boardTopLeft.y * getCurrentRenderScale()) + getImgYOffset();

        Graphics2D g2d = (Graphics2D) g.create();
        Composite originalComposite = g2d.getComposite();

        // Set transparency
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, opacity));

        // Draw based on shape type
        double scale = getCurrentRenderScale();
        switch (tokenToBeAdded.getShape().getShape()) {
            case SQUARE:
                g2d.drawImage(tokenToBeAdded.getImage(), panelX, panelY,
                        (int) (tokenToBeAdded.getShape().getWidth() * scale),
                        (int) (tokenToBeAdded.getShape().getHeight() * scale), null);
                break;
            case CIRCLE:
                int diameter = (int) (tokenToBeAdded.getShape().getRadius() * 2 * scale);
                g2d.fillOval(panelX, panelY, diameter, diameter);
                break;
            case TRIANGLE:
                int width = (int) (tokenToBeAdded.getShape().getWidth() * scale);
                int height = (int) (tokenToBeAdded.getShape().getHeight() * scale);
                int[] xPoints = { panelX, panelX + width / 2, panelX + width };
                int[] yPoints = { panelY + height, panelY, panelY + height };
                g2d.fillPolygon(xPoints, yPoints, 3);
                break;
        }
        g2d.setComposite(originalComposite);
        g2d.dispose();
    }

    private void placeToken(int x, int y) {
        if (getBoard() == null || tokenToBeAdded == null)
            return;
        logger.info("Placing token at ({}, {} snapp {} {} rotation {})", x, y, this.snappingDirection[0],
                this.snappingDirection[1], this.tokenToBeAdded.getShape().getRotation());
        BoardArea area = getBoard().getAreaAtPoint(new Point(x, y));
        if (!validateTokenPlacement(new Point(x, y), this.snappingDirection, area)) {
            logger.warn("Cannot place token at ({}, {}): Invalid placement", x, y);
            return;
        }
        if (area != null) {
            tokenToBeAdded = setTokenLocationAndProperties(new Point(x, y), this.snappingDirection, area);
            tokenToBeAdded.setLocation(new Point(x, y), area.getAreaId());
            getBoard().addToken(tokenToBeAdded);
            logger.info("Placed token {} at ({}, {}) in area {}", tokenToBeAdded.getType(), x, y, area.getAreaId());

            // Reset selection
            tokenToBeAdded = null;
            cursorPosition = null;
            snappedTokenPosition = null;
            repaint();
        } else {
            logger.warn("Cannot place token at ({}, {}): No area found", x, y);
        }
    }

    @Override
    public void onTokenSelected(String type, String subtype) {
        TokenType tokenType = TokenType.fromString(type);
        this.tokenToBeAdded = TokenFactory.createToken(tokenType, subtype);
        this.cursorPosition = null;
        this.snappedTokenPosition = null;
        requestFocusInWindow();
        logger.info("Token selected: {}", tokenToBeAdded.getType());
    }

    private Point[] getHorizontalSnappLine(Point tokenCenter, BoardArea area) {
        // chek if line is NORTH, SOUTH or center horizontal
        int tokenHeight = tokenToBeAdded.getShape().getHeight();

        // NORTH
        if (Math.abs(tokenCenter.y - (area.getTopLeft().y + tokenHeight / 2)) <= 1) { // Use small epsilon for float/int
                                                                                      // comparison if needed, but here
                                                                                      // we set exact int
            return new Point[] {
                    transformBoardPointsToPanel(new Point(area.getTopLeft().x, area.getTopLeft().y)),
                    transformBoardPointsToPanel(new Point(area.getTopLeft().x + area.getWidth(), area.getTopLeft().y))
            };
        } else if (Math.abs(tokenCenter.y - (area.getTopLeft().y + area.getHeight() - tokenHeight / 2)) <= 1) {
            // SOUTH
            return new Point[] {
                    transformBoardPointsToPanel(new Point(area.getTopLeft().x, area.getTopLeft().y + area.getHeight())),
                    transformBoardPointsToPanel(
                            new Point(area.getTopLeft().x + area.getWidth(), area.getTopLeft().y + area.getHeight()))
            };
        } else if (Math.abs(tokenCenter.y - (area.getTopLeft().y + area.getHeight() / 2)) <= 1) {
            // CENTER HORIZONTAL
            return new Point[] {
                    transformBoardPointsToPanel(
                            new Point(area.getTopLeft().x, area.getTopLeft().y + area.getHeight() / 2)),
                    transformBoardPointsToPanel(new Point(area.getTopLeft().x + area.getWidth(),
                            area.getTopLeft().y + area.getHeight() / 2))
            };
        } else {
            // return line based on current token position
            return new Point[] {
                    transformBoardPointsToPanel(new Point(area.getTopLeft().x, tokenCenter.y)),
                    transformBoardPointsToPanel(new Point(area.getBottomRight().x, tokenCenter.y))
            };
        }
    }

    private Point[] getVerticalSnappLine(Point tokenCenter, BoardArea area) {
        // chek if line is EAST, WEST or center vertical
        int tokenWidth = tokenToBeAdded.getShape().getWidth();

        // WEST
        if (Math.abs(tokenCenter.x - (area.getTopLeft().x + tokenWidth / 2)) <= 1) {
            return new Point[] {
                    transformBoardPointsToPanel(new Point(area.getTopLeft().x, area.getTopLeft().y)),
                    transformBoardPointsToPanel(new Point(area.getTopLeft().x, area.getTopLeft().y + area.getHeight()))
            };
        } else if (Math.abs(tokenCenter.x - (area.getTopLeft().x + area.getWidth() - tokenWidth / 2)) <= 1) {
            // EAST
            return new Point[] {
                    transformBoardPointsToPanel(new Point(area.getTopLeft().x + area.getWidth(), area.getTopLeft().y)),
                    transformBoardPointsToPanel(
                            new Point(area.getTopLeft().x + area.getWidth(), area.getTopLeft().y + area.getHeight()))
            };
        } else if (Math.abs(tokenCenter.x - (area.getTopLeft().x + area.getWidth() / 2)) <= 1) {
            // CENTER VERTICAL
            return new Point[] {
                    transformBoardPointsToPanel(
                            new Point(area.getTopLeft().x + area.getWidth() / 2, area.getTopLeft().y)),
                    transformBoardPointsToPanel(new Point(area.getTopLeft().x + area.getWidth() / 2,
                            area.getTopLeft().y + area.getHeight()))
            };
        } else {
            // return line based on current token position
            return new Point[] {
                    transformBoardPointsToPanel(new Point(tokenCenter.x, area.getTopLeft().y)),
                    transformBoardPointsToPanel(new Point(tokenCenter.x, area.getBottomRight().y))
            };
        }
    }

    private Point transformBoardPointsToPanel(Point boardPoint) {
        double scale = getCurrentRenderScale();
        int panelX = (int) (boardPoint.x * scale) + getImgXOffset();
        int panelY = (int) (boardPoint.y * scale) + getImgYOffset();
        return new Point(panelX, panelY);
    }

    private Token setTokenLocationAndProperties(Point tokenPosition, SNAPPING_DIRECTION[] snappingDirection,
            BoardArea area) {
        Token token = this.tokenToBeAdded;
        token.setLocation(tokenPosition, area.getAreaId());
        if (token.getType() == TokenType.DOOR) {
            UUID connectedAreaId = getDoorBoardAreaConnection(tokenPosition, area.getAreaId());
            BoardAreaConnection connection = getBoard().getBoardAreaConnection(area.getAreaId(), connectedAreaId);
            ((Door) token).setBoardAreaConnection(connection);
        }
        return token;
    }

    private boolean validateTokenPlacement(Point tokenPosition, SNAPPING_DIRECTION[] snappingDirection,
            BoardArea area) {

        if (this.tokenToBeAdded.getType() == TokenType.DOOR) {
            return validateDoorPlacement(tokenPosition, snappingDirection, area);
        }

        return true;
    }

    private boolean validateDoorPlacement(Point tokenPosition, SNAPPING_DIRECTION[] snappingDirection, BoardArea area) {
        // Door must be snapped to a wall
        if (snappingDirection[0] != SNAPPING_DIRECTION.UP && snappingDirection[0] != SNAPPING_DIRECTION.DOWN
                && (snappingDirection[1] != SNAPPING_DIRECTION.LEFT
                        && snappingDirection[1] != SNAPPING_DIRECTION.RIGHT)) {
            logger.info("{} {} door must be snapped to a wall", snappingDirection[0], snappingDirection[1]);
            return false;
        }
        // check rotation matches the direction
        int rotation = tokenToBeAdded.getRotation();
        if (rotation == 0 && snappingDirection[0] != SNAPPING_DIRECTION.UP
                || rotation == 90 && snappingDirection[1] != SNAPPING_DIRECTION.RIGHT
                || rotation == 180 && snappingDirection[0] != SNAPPING_DIRECTION.DOWN
                || rotation == 270 && snappingDirection[1] != SNAPPING_DIRECTION.LEFT) {
            logger.info("Door rotation does not match the direction");
            return false;
        }
        // check if there is a connection to the area
        UUID connectedAreaId = getDoorBoardAreaConnection(tokenPosition, area.getAreaId());
        if (connectedAreaId == null) {
            logger.info("Door has no connection to the area");
            return false;
        }
        return true;
    }

    private UUID getDoorBoardAreaConnection(Point doorPosition, UUID areaPlacementId) {
        // return the BoardAreaCoonection linked to the door
        // we get it by the rotation of the door
        int rotation = tokenToBeAdded.getRotation();
        Point checkPoint = new Point(doorPosition.x, doorPosition.y);

        switch (rotation) {
            case 0: // UP
                checkPoint = new Point(doorPosition.x, doorPosition.y - tokenToBeAdded.getShape().getHeight());
                break;
            case 90: // RIGHT
                checkPoint = new Point(doorPosition.x + tokenToBeAdded.getShape().getWidth(), doorPosition.y);
                break;
            case 180: // DOWN
                checkPoint = new Point(doorPosition.x, doorPosition.y + tokenToBeAdded.getShape().getHeight());
                break;
            case 270: // LEFT
                checkPoint = new Point(doorPosition.x - tokenToBeAdded.getShape().getWidth(), doorPosition.x);
                break;
        }
        BoardArea connectedArea = getBoard().getAreaAtPoint(checkPoint);
        if (connectedArea == null) {
            return null;
        }
        // check both area are connected
        logger.info("Checking connection between {} and {}", areaPlacementId, connectedArea.getAreaId());
        if (!getBoard().connectionExists(areaPlacementId, connectedArea.getAreaId())) {
            return null;
        }
        return connectedArea.getAreaId();
    }

}