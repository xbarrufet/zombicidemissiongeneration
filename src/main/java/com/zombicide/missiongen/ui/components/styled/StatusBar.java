package com.zombicide.missiongen.ui.components.styled;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import com.zombicide.missiongen.ui.theme.UIConstants;
import com.zombicide.missiongen.ui.theme.UIUtils;

/**
 * Status bar component for displaying status messages at the bottom of the window.
 */
public class StatusBar extends JPanel {
    
    private JLabel statusLabel;
    private JLabel detailLabel;
    
    public StatusBar() {
        setLayout(new BorderLayout(UIConstants.SPACING_MD, 0));
        setBackground(UIConstants.PANEL_BACKGROUND);
        setPreferredSize(new Dimension(0, 32));
        
        setBorder(javax.swing.BorderFactory.createCompoundBorder(
            javax.swing.BorderFactory.createMatteBorder(1, 0, 0, 0, UIConstants.BORDER),
            javax.swing.BorderFactory.createEmptyBorder(
                UIConstants.SPACING_SM,
                UIConstants.SPACING_MD,
                UIConstants.SPACING_SM,
                UIConstants.SPACING_MD
            )
        ));
        
        // Status label (left)
        statusLabel = new JLabel("Ready");
        statusLabel.setFont(UIConstants.FONT_SMALL);
        statusLabel.setForeground(UIConstants.TEXT_SECONDARY);
        add(statusLabel, BorderLayout.WEST);
        
        // Detail label (right)
        detailLabel = new JLabel("");
        detailLabel.setFont(UIConstants.FONT_SMALL);
        detailLabel.setForeground(UIConstants.TEXT_SECONDARY);
        detailLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        add(detailLabel, BorderLayout.EAST);
    }
    
    /**
     * Sets the main status message.
     * 
     * @param message The status message
     */
    public void setStatus(String message) {
        statusLabel.setText(message);
    }
    
    /**
     * Sets the detail message (shown on right side).
     * 
     * @param detail The detail message
     */
    public void setDetail(String detail) {
        detailLabel.setText(detail);
    }
    
    /**
     * Shows a temporary status message that reverts to "Ready" after a delay.
     * 
     * @param message The temporary message
     * @param durationMs Duration in milliseconds
     */
    public void showTemporaryStatus(String message, int durationMs) {
        setStatus(message);
        
        javax.swing.Timer timer = new javax.swing.Timer(durationMs, e -> {
            setStatus("Ready");
        });
        timer.setRepeats(false);
        timer.start();
    }
    
    /**
     * Clears both status and detail messages.
     */
    public void clear() {
        statusLabel.setText("Ready");
        detailLabel.setText("");
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g.create();
        UIUtils.enableAntialiasing(g2d);
        
        // Paint background
        g2d.setColor(getBackground());
        g2d.fillRect(0, 0, getWidth(), getHeight());
        
        g2d.dispose();
        super.paintComponent(g);
    }
}
