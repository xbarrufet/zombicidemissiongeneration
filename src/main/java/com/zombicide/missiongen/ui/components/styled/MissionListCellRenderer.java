package com.zombicide.missiongen.ui.components.styled;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;

import com.zombicide.missiongen.ui.theme.UIConstants;
import com.zombicide.missiongen.ui.theme.UIUtils;

/**
 * Custom list cell renderer for mission list with thumbnail and metadata.
 */
public class MissionListCellRenderer extends JPanel implements ListCellRenderer<String> {
    
    private JLabel nameLabel;
    private JLabel metadataLabel;
    private JLabel thumbnailLabel;
    private boolean isSelected;
    
    public MissionListCellRenderer() {
        setLayout(new java.awt.BorderLayout(UIConstants.SPACING_SM, UIConstants.SPACING_SM));
        setOpaque(false);
        setBorder(BorderFactory.createEmptyBorder(
            UIConstants.SPACING_SM,
            UIConstants.SPACING_SM,
            UIConstants.SPACING_SM,
            UIConstants.SPACING_SM
        ));
        
        // Thumbnail (left side)
        thumbnailLabel = new JLabel();
        thumbnailLabel.setPreferredSize(new Dimension(48, 48));
        thumbnailLabel.setOpaque(false);
        add(thumbnailLabel, java.awt.BorderLayout.WEST);
        
        // Text panel (center)
        JPanel textPanel = new JPanel();
        textPanel.setLayout(new javax.swing.BoxLayout(textPanel, javax.swing.BoxLayout.Y_AXIS));
        textPanel.setOpaque(false);
        
        nameLabel = new JLabel();
        nameLabel.setFont(UIConstants.FONT_BODY);
        nameLabel.setForeground(UIConstants.TEXT_PRIMARY);
        
        metadataLabel = new JLabel();
        metadataLabel.setFont(UIConstants.FONT_SMALL);
        metadataLabel.setForeground(UIConstants.TEXT_SECONDARY);
        
        textPanel.add(nameLabel);
        textPanel.add(javax.swing.Box.createVerticalStrut(UIConstants.SPACING_XS));
        textPanel.add(metadataLabel);
        
        add(textPanel, java.awt.BorderLayout.CENTER);
    }
    
    @Override
    public Component getListCellRendererComponent(JList<? extends String> list, String value,
                                                 int index, boolean isSelected, boolean cellHasFocus) {
        this.isSelected = isSelected;
        
        // Set mission name
        nameLabel.setText(value);
        
        // Set metadata (could be loaded from mission data)
        // For now, just show index as placeholder
        metadataLabel.setText("Mission #" + (index + 1));
        
        // Set colors based on selection state
        if (isSelected) {
            nameLabel.setForeground(UIConstants.ACCENT);
        } else {
            nameLabel.setForeground(UIConstants.TEXT_PRIMARY);
        }
        
        setPreferredSize(new Dimension(list.getWidth() - 10, 64));
        
        return this;
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g.create();
        UIUtils.enableAntialiasing(g2d);
        
        // Paint background based on selection state
        if (isSelected) {
            g2d.setColor(UIConstants.SELECTION_BACKGROUND);
            g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 
                            UIConstants.RADIUS_SM, UIConstants.RADIUS_SM);
            
            // Paint accent border
            g2d.setColor(UIConstants.ACCENT);
            g2d.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 
                            UIConstants.RADIUS_SM, UIConstants.RADIUS_SM);
        }
        
        g2d.dispose();
        super.paintComponent(g);
    }
    
    /**
     * Sets a thumbnail image for the mission.
     * 
     * @param image The thumbnail image
     */
    public void setThumbnail(Image image) {
        if (image != null) {
            Image scaled = image.getScaledInstance(48, 48, Image.SCALE_SMOOTH);
            thumbnailLabel.setIcon(new javax.swing.ImageIcon(scaled));
        }
    }
}
