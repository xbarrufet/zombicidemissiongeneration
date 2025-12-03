package com.zombicide.missiongen.ui.components.styled;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

import javax.swing.JTextField;

import com.zombicide.missiongen.ui.theme.UIConstants;
import com.zombicide.missiongen.ui.theme.UIUtils;

/**
 * Styled text field with modern appearance and focus effects.
 */
public class StyledTextField extends JTextField {
    
    private boolean isFocused = false;
    
    public StyledTextField() {
        this("");
    }
    
    public StyledTextField(String text) {
        super(text);
        setupComponent();
        setupListeners();
    }
    
    public StyledTextField(int columns) {
        super(columns);
        setupComponent();
        setupListeners();
    }
    
    private void setupComponent() {
        setFont(UIConstants.FONT_BODY);
        setForeground(UIConstants.TEXT_PRIMARY);
        setBackground(UIConstants.PANEL_BACKGROUND);
        setCaretColor(UIConstants.ACCENT);
        
        setBorder(javax.swing.BorderFactory.createCompoundBorder(
            javax.swing.BorderFactory.createLineBorder(UIConstants.BORDER, 1),
            javax.swing.BorderFactory.createEmptyBorder(
                UIConstants.SPACING_SM,
                UIConstants.SPACING_SM,
                UIConstants.SPACING_SM,
                UIConstants.SPACING_SM
            )
        ));
        
        setPreferredSize(new Dimension(getPreferredSize().width, UIConstants.INPUT_HEIGHT));
        setOpaque(false);
    }
    
    private void setupListeners() {
        addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                isFocused = true;
                repaint();
            }
            
            @Override
            public void focusLost(FocusEvent e) {
                isFocused = false;
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
        
        // Paint background
        g2d.setColor(getBackground());
        g2d.fillRoundRect(0, 0, width, height, UIConstants.RADIUS_SM, UIConstants.RADIUS_SM);
        
        // Paint border
        if (isFocused) {
            g2d.setColor(UIConstants.ACCENT);
            g2d.drawRoundRect(0, 0, width - 1, height - 1, UIConstants.RADIUS_SM, UIConstants.RADIUS_SM);
        } else {
            g2d.setColor(UIConstants.BORDER);
            g2d.drawRoundRect(0, 0, width - 1, height - 1, UIConstants.RADIUS_SM, UIConstants.RADIUS_SM);
        }
        
        g2d.dispose();
        super.paintComponent(g);
    }
    
    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        if (!enabled) {
            setForeground(UIConstants.TEXT_DISABLED);
        } else {
            setForeground(UIConstants.TEXT_PRIMARY);
        }
    }
}
