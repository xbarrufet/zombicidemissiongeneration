package com.zombicide.missiongen.ui.components;

import java.awt.Graphics;

import javax.swing.JPanel;

import com.zombicide.missiongen.model.tokens.TokenShape;

public class TokenPlaceHolder extends JPanel {

    TokenShape shape;
    public TokenPlaceHolder(TokenShape shape) {
       super();
         this.shape = shape;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        // paint a placeholder based on the shape with a almost transparent green lime color
        g.setColor(new java.awt.Color(0, 255, 0, 80));
        if (shape != null) {
            switch (shape.getShape()) {
                case SQUARE:
                    g.fillRect(0, 0, shape.getWidth(), shape.getHeight());
                    break;
                case CIRCLE:
                    g.fillOval(0, 0, shape.getRadius() * 2, shape.getRadius() * 2);
                    break;
                case TRIANGLE:
                    // Draw triangle placeholder
                    int[] xPoints = {0, shape.getWidth() / 2, shape.getWidth()};
                    int[] yPoints = {shape.getHeight(), 0, shape.getHeight()};
                    g.fillPolygon(xPoints, yPoints, 3);
                    break;
            }
        }
    }

}   
