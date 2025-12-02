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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zombicide.missiongen.model.areas.BoardArea;
import com.zombicide.missiongen.model.areas.Direction;
import com.zombicide.missiongen.model.board.MissionBoard;
import com.zombicide.missiongen.model.helpers.Rect;
import com.zombicide.missiongen.model.tokens.Token;
import com.zombicide.missiongen.model.tokens.TokenFactory;
import com.zombicide.missiongen.model.tokens.TokenType;
import com.zombicide.missiongen.ui.components.BoardBackgroundPanel;
import com.zombicide.missiongen.ui.interfaces.MissionPropertiesListener;

public class ZoneMissionDraw extends BoardBackgroundPanel implements MissionPropertiesListener {

    private static final Logger logger = LoggerFactory.getLogger(ZoneMissionDraw.class);

    private Token selectedToken;
    private Point cursorPosition;
    float opacity = 0.5f;
    private BoardArea hoveredArea;
    private Point tokenPosition;

    private final int SNAP_DISTANCE = 20;

    private Point[] horizontalSnapp;
    private Point[] verticalSnapp;

    public ZoneMissionDraw() {
        super();
        setupMouseListeners();
        setupKeyListeners();
        setAreaIdsVisible(false);
    }

    private void setupMouseListeners() {
        // Track cursor position when in selection mode
        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                if (inSelectedTokenState()) {
                    cursorPosition = e.getPoint();
                    hoveredArea = getBoardAreaAtPoint(cursorPosition);
                    tokenPosition = convertPanelMouseToBaordCoordinates(cursorPosition);
                    if (hoveredArea != null) {
                        horizontalSnapp = getHorizontalSnappLine(tokenPosition, hoveredArea);
                        verticalSnapp = getVerticalSnappLine(tokenPosition, hoveredArea);
                    } else {
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
                if (inSelectedTokenState() && e.getButton() == MouseEvent.BUTTON1) {
                    placeToken(e.getX(), e.getY());
                }
            }
        });
    }

    private void setupKeyListeners() {
        // Handle ESC to cancel token selection
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE && inSelectedTokenState()) {
                    selectedToken = null;
                    cursorPosition = null;
                    repaint();
                    logger.info("Token selection cancelled");
                }
                  if (e.getKeyCode() == KeyEvent.VK_R && inSelectedTokenState()) {
                    selectedToken.rotate();
                    repaint();
                    logger.info("Token rotated {} {} ",selectedToken.getShape().getWidth(),selectedToken.getShape().getHeight());
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
        if (inSelectedTokenState() && cursorPosition != null && selectedToken != null) {
            drawTokenPlaceHolder(g);
            drawSnappLines(g);
        }
    }

    private void drawSnappLines(Graphics g) {
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setColor(java.awt.Color.RED);
        g2d.drawLine(horizontalSnapp[0].x, horizontalSnapp[0].y, horizontalSnapp[1].x, horizontalSnapp[1].y);
        g2d.drawLine(verticalSnapp[0].x, verticalSnapp[0].y, verticalSnapp[1].x, verticalSnapp[1].y);
        g2d.dispose();
    }

    private void drawTokens(Graphics g) {
        for (com.zombicide.missiongen.model.tokens.Token token : getBoard().getTokens()) {
            java.awt.Point location = token.getLocation();
            if (location != null) {
                // int x = (int) (location.x * getCurrentRenderScale()) + getImgXOffset();
                // int y = (int) (location.y * getCurrentRenderScale()) + getImgYOffset();
                // int width = (int) (token.getWidth() * getCurrentRenderScale());
                // int height = (int) (token.getHeight() * getCurrentRenderScale());

                // Draw centered on the point
                //g.drawImage(token.getImage(), x - width / 2, y - height / 2, width, height, null);
            }
        }
    }

   

    private boolean inSelectedTokenState() {
        return this.selectedToken != null;
    }

    private void drawTokenPlaceHolder(Graphics g) {
        // Convert cursor position from panel coordinates to board coordinates
        Point boardCursorPosition = convertPanelMouseToBaordCoordinates(cursorPosition);
        
        // Get top-left position in board coordinates
        Point boardTopLeft = this.selectedToken.getShape().getTopLeftFromCenter(boardCursorPosition);
        
        // Convert back to panel coordinates for drawing
        int panelX = (int)(boardTopLeft.x * getCurrentRenderScale()) + getImgXOffset();
        int panelY = (int)(boardTopLeft.y * getCurrentRenderScale()) + getImgYOffset();
        
        Graphics2D g2d = (Graphics2D) g.create();
        Composite originalComposite = g2d.getComposite();
        
        // Set transparency
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, opacity));
        
        // Draw based on shape type
        double scale = getCurrentRenderScale();
        switch (selectedToken.getShape().getShape()) {
            case SQUARE:
                g2d.drawImage(selectedToken.getImage(), panelX, panelY, 
                        (int)(selectedToken.getShape().getWidth() * scale), 
                        (int)(selectedToken.getShape().getHeight() * scale), null);
                break;
            case CIRCLE:
                int diameter = (int)(selectedToken.getShape().getRadius() * 2 * scale);
                g2d.fillOval(panelX, panelY, diameter, diameter);
                break;
            case TRIANGLE:
                int width = (int)(selectedToken.getShape().getWidth() * scale);
                int height = (int)(selectedToken.getShape().getHeight() * scale);
                int[] xPoints = {panelX, panelX + width / 2, panelX + width};
                int[] yPoints = {panelY + height, panelY, panelY + height};
                g2d.fillPolygon(xPoints, yPoints, 3);
                break;
        }
        g2d.setComposite(originalComposite);
        g2d.dispose();
    }

    private void placeToken(int x, int y) {
        // TODO: Implementar colocación de token
        logger.info("placeToken called at ({}, {})", x, y);
    }

    @Override
    public void onTokenSelected(String type, String subtype) {
        TokenType tokenType = TokenType.fromString(type);
        this.selectedToken = TokenFactory.createToken(tokenType, subtype);
        this.cursorPosition = null;
        requestFocusInWindow();
        logger.info("Token selected: {}", selectedToken.getType());
    }


    private Point[] getHorizontalSnappLine(Point tokenCenter, BoardArea area) {
       //chek if line is NORTH, SOUTH or center horizontal
       //NORTH
       if(tokenCenter.y >= area.getTopLeft().y - SNAP_DISTANCE && tokenCenter.y <= area.getTopLeft().y + SNAP_DISTANCE) {
           return new Point[] {
               transformBoardPointsToPanel(new Point(area.getTopLeft().x, area.getTopLeft().y)),
               transformBoardPointsToPanel(new Point(area.getTopLeft().x + area.getWidth(), area.getTopLeft().y))
           };
       }
        else if(tokenCenter.y >= area.getTopLeft().y + area.getHeight() - SNAP_DISTANCE && tokenCenter.y <= area.getTopLeft().y + area.getHeight() + SNAP_DISTANCE) {
           //SOUTH
           return new Point[] {
               transformBoardPointsToPanel(new Point(area.getTopLeft().x, area.getTopLeft().y + area.getHeight())),
               transformBoardPointsToPanel(new Point(area.getTopLeft().x + area.getWidth(), area.getTopLeft().y + area.getHeight()))
           };
         } else if(tokenCenter.y >= area.getTopLeft().y + area.getHeight()/2 - SNAP_DISTANCE && tokenCenter.y <= area.getTopLeft().y + area.getHeight()/2 + SNAP_DISTANCE) {
           //CENTER HORIZONTAL
           return new Point[] {
               transformBoardPointsToPanel(new Point(area.getTopLeft().x, area.getTopLeft().y + area.getHeight()/2)),
               transformBoardPointsToPanel(new Point(area.getTopLeft().x + area.getWidth(), area.getTopLeft().y + area.getHeight()/2))
           };
        } else {
            //return line based on current token position
            return new Point[] {
                transformBoardPointsToPanel(new Point(area.getTopLeft().x, tokenCenter.y)),
                transformBoardPointsToPanel(new Point(area.getBottomRight().x , tokenCenter.y))
            };   
        }
    }

     private Point[] getVerticalSnappLine(Point tokenCenter, BoardArea area) {
        //chek if line is EAST, WEST or center vertical
        //WEST
        if(tokenCenter.x >= area.getTopLeft().x - SNAP_DISTANCE && tokenCenter.x <= area.getTopLeft().x + SNAP_DISTANCE) {
            return new Point[] {
                transformBoardPointsToPanel(new Point(area.getTopLeft().x, area.getTopLeft().y)),
                transformBoardPointsToPanel(new Point(area.getTopLeft().x, area.getTopLeft().y + area.getHeight()))
            };
        }
         else if(tokenCenter.x >= area.getTopLeft().x + area.getWidth() - SNAP_DISTANCE && tokenCenter.x <= area.getTopLeft().x + area.getWidth() + SNAP_DISTANCE) {
            //EAST
            return new Point[] {
                transformBoardPointsToPanel(new Point(area.getTopLeft().x + area.getWidth(), area.getTopLeft().y)),
                transformBoardPointsToPanel(new Point(area.getTopLeft().x + area.getWidth(), area.getTopLeft().y + area.getHeight()))
            };
          } else if(tokenCenter.x >= area.getTopLeft().x + area.getWidth()/2 - SNAP_DISTANCE && tokenCenter.x <= area.getTopLeft().x + area.getWidth()/2 + SNAP_DISTANCE) {
            //CENTER VERTICAL
            return new Point[] {
                transformBoardPointsToPanel(new Point(area.getTopLeft().x + area.getWidth()/2, area.getTopLeft().y)),
                transformBoardPointsToPanel(new Point(area.getTopLeft().x + area.getWidth()/2, area.getTopLeft().y + area.getHeight()))
            };
         } else {
             //return line based on current token position
             return new Point[] {
                 transformBoardPointsToPanel(new Point(tokenCenter.x, area.getTopLeft().y)),
                 transformBoardPointsToPanel(new Point(tokenCenter.x , area.getBottomRight().y))
             };   
         }
     }

     private Point transformBoardPointsToPanel(Point boardPoint) {
         double scale = getCurrentRenderScale();
         int panelX = (int)(boardPoint.x * scale) + getImgXOffset();
         int panelY = (int)(boardPoint.y * scale) + getImgYOffset();
         return new Point(panelX, panelY);
     }
      

}