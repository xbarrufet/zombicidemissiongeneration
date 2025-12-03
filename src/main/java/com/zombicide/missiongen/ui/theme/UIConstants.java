package com.zombicide.missiongen.ui.theme;

import java.awt.Color;
import java.awt.Font;

/**
 * Design system constants for the application.
 * Contains color palette, typography, spacing, and other UI constants.
 */
public class UIConstants {
    
    // ==================== COLOR PALETTE ====================
    
    // Primary Colors
    public static final Color BACKGROUND = new Color(248, 249, 250);
    public static final Color PANEL_BACKGROUND = Color.WHITE;
    public static final Color ACCENT = new Color(91, 110, 225);
    public static final Color ACCENT_HOVER = new Color(75, 94, 209);
    
    // Semantic Colors
    public static final Color SUCCESS = new Color(40, 199, 111);
    public static final Color WARNING = new Color(255, 159, 67);
    public static final Color DANGER = new Color(234, 84, 85);
    
    // Neutral Colors
    public static final Color BORDER = new Color(228, 231, 235);
    public static final Color TEXT_PRIMARY = new Color(33, 37, 41);
    public static final Color TEXT_SECONDARY = new Color(108, 117, 125);
    public static final Color TEXT_DISABLED = new Color(173, 181, 189);
    
    // Selection & Hover States
    public static final Color SELECTION_BACKGROUND = new Color(91, 110, 225, 25); // ACCENT with 10% opacity
    public static final Color HOVER_BACKGROUND = new Color(248, 249, 250);
    
    // Legacy Colors (for gradual migration)
    public static final Color LEGACY_LAVENDER = new Color(230, 230, 250);
    public static final Color LEGACY_LIGHT_YELLOW = new Color(255, 250, 205);
    
    // ==================== TYPOGRAPHY ====================
    
    // Font Families
    public static final String FONT_FAMILY = "SansSerif";
    
    // Font Sizes
    public static final int FONT_SIZE_HEADER = 18;
    public static final int FONT_SIZE_SUBHEADER = 16;
    public static final int FONT_SIZE_BODY = 14;
    public static final int FONT_SIZE_LABEL = 12;
    public static final int FONT_SIZE_SMALL = 11;
    
    // Font Styles
    public static final Font FONT_HEADER = new Font(FONT_FAMILY, Font.BOLD, FONT_SIZE_HEADER);
    public static final Font FONT_SUBHEADER = new Font(FONT_FAMILY, Font.BOLD, FONT_SIZE_SUBHEADER);
    public static final Font FONT_BODY = new Font(FONT_FAMILY, Font.PLAIN, FONT_SIZE_BODY);
    public static final Font FONT_LABEL = new Font(FONT_FAMILY, Font.BOLD, FONT_SIZE_LABEL);
    public static final Font FONT_SMALL = new Font(FONT_FAMILY, Font.PLAIN, FONT_SIZE_SMALL);
    
    // ==================== SPACING ====================
    
    public static final int SPACING_XS = 4;
    public static final int SPACING_SM = 8;
    public static final int SPACING_MD = 16;
    public static final int SPACING_LG = 24;
    public static final int SPACING_XL = 32;
    
    // ==================== BORDER RADIUS ====================
    
    public static final int RADIUS_SM = 4;
    public static final int RADIUS_MD = 8;
    public static final int RADIUS_LG = 12;
    
    // ==================== SHADOWS ====================
    
    // Shadow colors (with alpha)
    public static final Color SHADOW_LIGHT = new Color(0, 0, 0, 20);  // 8% opacity
    public static final Color SHADOW_MEDIUM = new Color(0, 0, 0, 31); // 12% opacity
    public static final Color SHADOW_DARK = new Color(0, 0, 0, 41);   // 16% opacity
    
    // ==================== ANIMATION ====================
    
    public static final int ANIMATION_FAST = 150;
    public static final int ANIMATION_NORMAL = 250;
    public static final int ANIMATION_SLOW = 350;
    
    // ==================== DIMENSIONS ====================
    
    // Panel dimensions
    public static final int SELECTION_PANEL_WIDTH = 250;
    public static final int PROPERTIES_PANEL_WIDTH = 250;
    public static final int DRAW_PANEL_SIZE = 750;
    
    // Component heights
    public static final int BUTTON_HEIGHT = 36;
    public static final int INPUT_HEIGHT = 32;
    public static final int MIN_TOUCH_TARGET = 44;
    
    // ==================== ICONS ====================
    
    // Icon sizes
    public static final int ICON_SIZE_SM = 16;
    public static final int ICON_SIZE_MD = 20;
    public static final int ICON_SIZE_LG = 24;
    
    // ==================== UTILITY METHODS ====================
    
    /**
     * Creates a color with specified opacity.
     * 
     * @param color The base color
     * @param alpha The alpha value (0-255)
     * @return Color with specified alpha
     */
    public static Color withAlpha(Color color, int alpha) {
        return new Color(color.getRed(), color.getGreen(), color.getBlue(), alpha);
    }
    
    /**
     * Creates a slightly darker version of the color.
     * 
     * @param color The base color
     * @return Darker color
     */
    public static Color darker(Color color) {
        return color.darker();
    }
    
    /**
     * Creates a slightly brighter version of the color.
     * 
     * @param color The base color
     * @return Brighter color
     */
    public static Color brighter(Color color) {
        return color.brighter();
    }
    
    private UIConstants() {
        // Prevent instantiation
    }
}
