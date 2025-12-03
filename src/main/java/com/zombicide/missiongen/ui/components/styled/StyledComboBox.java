package com.zombicide.missiongen.ui.components.styled;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JComboBox;

import com.zombicide.missiongen.ui.theme.UIConstants;
import com.zombicide.missiongen.ui.theme.UIUtils;

/**
 * Styled combo box with modern appearance.
 */
public class StyledComboBox<E> extends JComboBox<E> {
    
    public StyledComboBox() {
        super();
        setupComponent();
    }
    
    public StyledComboBox(E[] items) {
        super(items);
        setupComponent();
    }
    
    private void setupComponent() {
        setFont(UIConstants.FONT_BODY);
        setForeground(UIConstants.TEXT_PRIMARY);
        setBackground(UIConstants.PANEL_BACKGROUND);
        
        setBorder(javax.swing.BorderFactory.createCompoundBorder(
            javax.swing.BorderFactory.createLineBorder(UIConstants.BORDER, 1),
            javax.swing.BorderFactory.createEmptyBorder(
                UIConstants.SPACING_XS,
                UIConstants.SPACING_SM,
                UIConstants.SPACING_XS,
                UIConstants.SPACING_SM
            )
        ));
        
        setPreferredSize(new Dimension(getPreferredSize().width, UIConstants.INPUT_HEIGHT));
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
        
        g2d.dispose();
        super.paintComponent(g);
    }
}
