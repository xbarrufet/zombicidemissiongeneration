package com.zombicide.missiongen.ui.components.styled;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JButton;

import com.zombicide.missiongen.ui.theme.UIConstants;
import com.zombicide.missiongen.ui.theme.UIUtils;

/**
 * Modern styled button with rounded corners, hover effects, and icon support.
 */
public class StyledButton extends JButton {
    
    public enum ButtonStyle {
        PRIMARY,
        SECONDARY,
        SUCCESS,
        WARNING,
        DANGER
    }
    
    private ButtonStyle style;
    private boolean isHovered = false;
    private boolean isPressed = false;
    private Color baseColor;
    private Color hoverColor;
    private Color pressedColor;
    
    public StyledButton(String text) {
        this(text, ButtonStyle.SECONDARY);
    }
    
    public StyledButton(String text, ButtonStyle style) {
        super(text);
        this.style = style;
        initializeColors();
        setupComponent();
        setupListeners();
    }
    
    private void initializeColors() {
        switch (style) {
            case PRIMARY:
                baseColor = UIConstants.ACCENT;
                hoverColor = UIConstants.ACCENT_HOVER;
                pressedColor = UIConstants.darker(UIConstants.ACCENT_HOVER);
                setForeground(Color.WHITE);
                break;
            case SUCCESS:
                baseColor = UIConstants.SUCCESS;
                hoverColor = UIConstants.darker(UIConstants.SUCCESS);
                pressedColor = UIConstants.darker(hoverColor);
                setForeground(Color.WHITE);
                break;
            case WARNING:
                baseColor = UIConstants.WARNING;
                hoverColor = UIConstants.darker(UIConstants.WARNING);
                pressedColor = UIConstants.darker(hoverColor);
                setForeground(Color.WHITE);
                break;
            case DANGER:
                baseColor = UIConstants.DANGER;
                hoverColor = UIConstants.darker(UIConstants.DANGER);
                pressedColor = UIConstants.darker(hoverColor);
                setForeground(Color.WHITE);
                break;
            case SECONDARY:
            default:
                baseColor = UIConstants.PANEL_BACKGROUND;
                hoverColor = UIConstants.HOVER_BACKGROUND;
                pressedColor = UIConstants.BORDER;
                setForeground(UIConstants.TEXT_PRIMARY);
                break;
        }
    }
    
    private void setupComponent() {
        setFont(UIConstants.FONT_BODY);
        setFocusPainted(false);
        setBorderPainted(false);
        setContentAreaFilled(false);
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Set preferred size with padding
        Dimension size = getPreferredSize();
        size.height = Math.max(UIConstants.BUTTON_HEIGHT, size.height);
        size.width = size.width + UIConstants.SPACING_LG * 2;
        setPreferredSize(size);
        setMinimumSize(new Dimension(80, UIConstants.BUTTON_HEIGHT));
    }
    
    private void setupListeners() {
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (isEnabled()) {
                    isHovered = true;
                    repaint();
                }
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                isHovered = false;
                repaint();
            }
            
            @Override
            public void mousePressed(MouseEvent e) {
                if (isEnabled()) {
                    isPressed = true;
                    repaint();
                }
            }
            
            @Override
            public void mouseReleased(MouseEvent e) {
                isPressed = false;
                repaint();
            }
        });
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g.create();
        UIUtils.enableAntialiasing(g2d);
        
        int width = getWidth();
        int height = getHeight();
        
        // Determine current color based on state
        Color currentColor;
        if (!isEnabled()) {
            currentColor = UIConstants.BORDER;
            setForeground(UIConstants.TEXT_DISABLED);
        } else if (isPressed) {
            currentColor = pressedColor;
        } else if (isHovered) {
            currentColor = hoverColor;
        } else {
            currentColor = baseColor;
        }
        
        // Draw shadow for non-secondary buttons
        if (style != ButtonStyle.SECONDARY && isEnabled()) {
            g2d.setColor(UIConstants.SHADOW_LIGHT);
            g2d.fillRoundRect(2, 2, width - 4, height - 4, 
                            UIConstants.RADIUS_MD, UIConstants.RADIUS_MD);
        }
        
        // Draw button background
        g2d.setColor(currentColor);
        g2d.fillRoundRect(0, 0, width, height, 
                         UIConstants.RADIUS_MD, UIConstants.RADIUS_MD);
        
        // Draw border for secondary style
        if (style == ButtonStyle.SECONDARY) {
            g2d.setColor(UIConstants.BORDER);
            g2d.drawRoundRect(0, 0, width - 1, height - 1, 
                            UIConstants.RADIUS_MD, UIConstants.RADIUS_MD);
        }
        
        // Draw text
        g2d.setColor(getForeground());
        g2d.setFont(getFont());
        
        FontMetrics fm = g2d.getFontMetrics();
        String text = getText();
        int textWidth = fm.stringWidth(text);
        int textHeight = fm.getAscent();
        
        int x = (width - textWidth) / 2;
        int y = (height + textHeight) / 2 - 2;
        
        g2d.drawString(text, x, y);
        
        g2d.dispose();
    }
    
    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        if (!enabled) {
            setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        } else {
            setCursor(new Cursor(Cursor.HAND_CURSOR));
        }
        repaint();
    }
    
    public void setButtonStyle(ButtonStyle style) {
        this.style = style;
        initializeColors();
        repaint();
    }
    
    public ButtonStyle getButtonStyle() {
        return style;
    }
}
