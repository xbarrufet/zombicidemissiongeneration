package com.zombicide.missiongen.ui.components.styled;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JLabel;

import com.zombicide.missiongen.ui.theme.UIConstants;
import com.zombicide.missiongen.ui.theme.UIUtils;

/**
 * Styled label with predefined styles for different purposes.
 */
public class StyledLabel extends JLabel {
    
    public enum LabelStyle {
        HEADER,
        SUBHEADER,
        BODY,
        LABEL,
        SMALL,
        SECONDARY
    }
    
    private LabelStyle style;
    
    public StyledLabel(String text) {
        this(text, LabelStyle.BODY);
    }
    
    public StyledLabel(String text, LabelStyle style) {
        super(text);
        this.style = style;
        applyStyle();
    }
    
    private void applyStyle() {
        switch (style) {
            case HEADER:
                setFont(UIConstants.FONT_HEADER);
                setForeground(UIConstants.TEXT_PRIMARY);
                break;
            case SUBHEADER:
                setFont(UIConstants.FONT_SUBHEADER);
                setForeground(UIConstants.TEXT_PRIMARY);
                break;
            case LABEL:
                setFont(UIConstants.FONT_LABEL);
                setForeground(UIConstants.TEXT_PRIMARY);
                break;
            case SMALL:
                setFont(UIConstants.FONT_SMALL);
                setForeground(UIConstants.TEXT_SECONDARY);
                break;
            case SECONDARY:
                setFont(UIConstants.FONT_BODY);
                setForeground(UIConstants.TEXT_SECONDARY);
                break;
            case BODY:
            default:
                setFont(UIConstants.FONT_BODY);
                setForeground(UIConstants.TEXT_PRIMARY);
                break;
        }
    }
    
    public void setLabelStyle(LabelStyle style) {
        this.style = style;
        applyStyle();
    }
    
    public LabelStyle getLabelStyle() {
        return style;
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g.create();
        UIUtils.enableAntialiasing(g2d);
        g2d.dispose();
        
        super.paintComponent(g);
    }
}
