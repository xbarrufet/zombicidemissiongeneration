package com.zombicide.missiongen.ui.theme;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.border.Border;

/**
 * Utility class for common UI operations and styling.
 */
public class UIUtils {
    
    /**
     * Enables antialiasing for smoother graphics rendering.
     * 
     * @param g2d The Graphics2D context
     */
    public static void enableAntialiasing(Graphics2D g2d) {
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
    }
    
    /**
     * Creates a rounded border with specified radius and color.
     * 
     * @param radius The corner radius
     * @param color The border color
     * @return Border instance
     */
    public static Border createRoundedBorder(int radius, Color color) {
        return BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(color, 1),
            BorderFactory.createEmptyBorder(
                UIConstants.SPACING_SM,
                UIConstants.SPACING_SM,
                UIConstants.SPACING_SM,
                UIConstants.SPACING_SM
            )
        );
    }
    
    /**
     * Creates a card-style border with shadow effect.
     * 
     * @return Border instance
     */
    public static Border createCardBorder() {
        return BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(UIConstants.BORDER, 1),
            BorderFactory.createEmptyBorder(
                UIConstants.SPACING_MD,
                UIConstants.SPACING_MD,
                UIConstants.SPACING_MD,
                UIConstants.SPACING_MD
            )
        );
    }
    
    /**
     * Creates an empty border with standard padding.
     * 
     * @return Border instance
     */
    public static Border createPaddingBorder() {
        return BorderFactory.createEmptyBorder(
            UIConstants.SPACING_MD,
            UIConstants.SPACING_MD,
            UIConstants.SPACING_MD,
            UIConstants.SPACING_MD
        );
    }
    
    /**
     * Creates a section separator border.
     * 
     * @return Border instance
     */
    public static Border createSectionBorder() {
        return BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, UIConstants.BORDER),
            BorderFactory.createEmptyBorder(
                UIConstants.SPACING_SM,
                0,
                UIConstants.SPACING_SM,
                0
            )
        );
    }
    
    /**
     * Paints a rounded rectangle with shadow effect.
     * 
     * @param g The graphics context
     * @param x X coordinate
     * @param y Y coordinate
     * @param width Width
     * @param height Height
     * @param radius Corner radius
     * @param bgColor Background color
     */
    public static void paintRoundedRect(Graphics g, int x, int y, int width, int height, 
                                        int radius, Color bgColor) {
        Graphics2D g2d = (Graphics2D) g.create();
        enableAntialiasing(g2d);
        
        // Paint shadow
        g2d.setColor(UIConstants.SHADOW_LIGHT);
        g2d.fillRoundRect(x + 2, y + 2, width, height, radius, radius);
        
        // Paint background
        g2d.setColor(bgColor);
        g2d.fillRoundRect(x, y, width, height, radius, radius);
        
        g2d.dispose();
    }
    
    /**
     * Paints a shadow effect below a component.
     * 
     * @param g The graphics context
     * @param width Width
     * @param height Height
     * @param shadowSize Shadow blur size
     */
    public static void paintShadow(Graphics g, int width, int height, int shadowSize) {
        Graphics2D g2d = (Graphics2D) g.create();
        enableAntialiasing(g2d);
        
        for (int i = 0; i < shadowSize; i++) {
            int alpha = (int) (UIConstants.SHADOW_LIGHT.getAlpha() * (1.0 - (double) i / shadowSize));
            g2d.setColor(new Color(0, 0, 0, alpha));
            g2d.drawRoundRect(i, i, width - 2 * i, height - 2 * i, 
                             UIConstants.RADIUS_MD, UIConstants.RADIUS_MD);
        }
        
        g2d.dispose();
    }
    
    /**
     * Sets up a component with modern styling.
     * 
     * @param component The component to style
     */
    public static void applyModernStyle(JComponent component) {
        component.setBackground(UIConstants.PANEL_BACKGROUND);
        component.setFont(UIConstants.FONT_BODY);
        component.setBorder(createCardBorder());
    }
    
    /**
     * Interpolates between two colors.
     * 
     * @param color1 Start color
     * @param color2 End color
     * @param ratio Interpolation ratio (0.0 to 1.0)
     * @return Interpolated color
     */
    public static Color interpolateColor(Color color1, Color color2, float ratio) {
        ratio = Math.max(0, Math.min(1, ratio));
        
        int r = (int) (color1.getRed() + ratio * (color2.getRed() - color1.getRed()));
        int g = (int) (color1.getGreen() + ratio * (color2.getGreen() - color1.getGreen()));
        int b = (int) (color1.getBlue() + ratio * (color2.getBlue() - color1.getBlue()));
        int a = (int) (color1.getAlpha() + ratio * (color2.getAlpha() - color1.getAlpha()));
        
        return new Color(r, g, b, a);
    }
    
    /**
     * Creates a gradient paint for backgrounds.
     * 
     * @param component The component to paint
     * @param startColor Start color
     * @param endColor End color
     * @return Graphics2D with gradient configured
     */
    public static void paintGradientBackground(Graphics g, Component component, 
                                               Color startColor, Color endColor) {
        Graphics2D g2d = (Graphics2D) g.create();
        enableAntialiasing(g2d);
        
        java.awt.GradientPaint gradient = new java.awt.GradientPaint(
            0, 0, startColor,
            0, component.getHeight(), endColor
        );
        
        g2d.setPaint(gradient);
        g2d.fillRect(0, 0, component.getWidth(), component.getHeight());
        
        g2d.dispose();
    }
    
    private UIUtils() {
        // Prevent instantiation
    }
}
