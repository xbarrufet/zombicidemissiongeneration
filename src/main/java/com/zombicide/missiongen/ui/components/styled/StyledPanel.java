package com.zombicide.missiongen.ui.components.styled;

import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JPanel;

import com.zombicide.missiongen.ui.theme.UIConstants;
import com.zombicide.missiongen.ui.theme.UIUtils;

/**
 * Modern styled panel with rounded corners and optional shadow effect.
 */
public class StyledPanel extends JPanel {
    
    private boolean withShadow;
    private boolean withRoundedCorners;
    private int cornerRadius;
    
    public StyledPanel() {
        this(true, true);
    }
    
    public StyledPanel(boolean withShadow, boolean withRoundedCorners) {
        this.withShadow = withShadow;
        this.withRoundedCorners = withRoundedCorners;
        this.cornerRadius = UIConstants.RADIUS_MD;
        
        setOpaque(false);
        setBackground(UIConstants.PANEL_BACKGROUND);
        setBorder(UIUtils.createCardBorder());
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g.create();
        UIUtils.enableAntialiasing(g2d);
        
        int width = getWidth();
        int height = getHeight();
        
        if (withShadow) {
            // Paint shadow
            g2d.setColor(UIConstants.SHADOW_LIGHT);
            if (withRoundedCorners) {
                g2d.fillRoundRect(3, 3, width - 6, height - 6, cornerRadius, cornerRadius);
            } else {
                g2d.fillRect(3, 3, width - 6, height - 6);
            }
        }
        
        // Paint background
        g2d.setColor(getBackground());
        if (withRoundedCorners) {
            g2d.fillRoundRect(0, 0, width, height, cornerRadius, cornerRadius);
        } else {
            g2d.fillRect(0, 0, width, height);
        }
        
        // Paint border
        if (withRoundedCorners) {
            g2d.setColor(UIConstants.BORDER);
            g2d.drawRoundRect(0, 0, width - 1, height - 1, cornerRadius, cornerRadius);
        }
        
        g2d.dispose();
        
        super.paintComponent(g);
    }
    
    public void setWithShadow(boolean withShadow) {
        this.withShadow = withShadow;
        repaint();
    }
    
    public void setWithRoundedCorners(boolean withRoundedCorners) {
        this.withRoundedCorners = withRoundedCorners;
        repaint();
    }
    
    public void setCornerRadius(int radius) {
        this.cornerRadius = radius;
        repaint();
    }
}
