package com.zombicide.missiongen.ui.components.notification;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;

import com.zombicide.missiongen.ui.theme.UIConstants;
import com.zombicide.missiongen.ui.theme.UIUtils;

/**
 * Toast notification component for showing temporary messages.
 */
public class ToastNotification extends JPanel {
    
    public enum ToastType {
        SUCCESS,
        INFO,
        WARNING,
        ERROR
    }
    
    private ToastType type;
    private JLabel messageLabel;
    private Timer hideTimer;
    private float opacity = 0.0f;
    private Timer fadeTimer;
    
    public ToastNotification(String message, ToastType type) {
        this.type = type;
        setOpaque(false);
        setLayout(new BorderLayout(UIConstants.SPACING_SM, UIConstants.SPACING_SM));
        
        // Message label
        messageLabel = new JLabel(message);
        messageLabel.setFont(UIConstants.FONT_BODY);
        messageLabel.setForeground(type == ToastType.WARNING || type == ToastType.INFO 
            ? UIConstants.TEXT_PRIMARY : UIConstants.PANEL_BACKGROUND);
        
        setBorder(javax.swing.BorderFactory.createEmptyBorder(
            UIConstants.SPACING_MD,
            UIConstants.SPACING_MD,
            UIConstants.SPACING_MD,
            UIConstants.SPACING_MD
        ));
        
        add(messageLabel, BorderLayout.CENTER);
        
        // Set preferred size
        setPreferredSize(new Dimension(300, 60));
        
        // Start fade-in animation
        fadeIn();
    }
    
    private void fadeIn() {
        fadeTimer = new Timer(20, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                opacity += 0.1f;
                if (opacity >= 1.0f) {
                    opacity = 1.0f;
                    fadeTimer.stop();
                    scheduleHide();
                }
                repaint();
            }
        });
        fadeTimer.start();
    }
    
    private void scheduleHide() {
        hideTimer = new Timer(3000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                fadeOut();
            }
        });
        hideTimer.setRepeats(false);
        hideTimer.start();
    }
    
    private void fadeOut() {
        if (fadeTimer != null) {
            fadeTimer.stop();
        }
        
        fadeTimer = new Timer(20, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                opacity -= 0.1f;
                if (opacity <= 0.0f) {
                    opacity = 0.0f;
                    fadeTimer.stop();
                    // Notify parent to remove this component
                    if (getParent() != null) {
                        getParent().remove(ToastNotification.this);
                        getParent().revalidate();
                        getParent().repaint();
                    }
                }
                repaint();
            }
        });
        fadeTimer.start();
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g.create();
        UIUtils.enableAntialiasing(g2d);
        
        // Set composite for transparency
        g2d.setComposite(java.awt.AlphaComposite.getInstance(
            java.awt.AlphaComposite.SRC_OVER, opacity));
        
        int width = getWidth();
        int height = getHeight();
        
        // Paint shadow
        g2d.setColor(UIConstants.SHADOW_MEDIUM);
        g2d.fillRoundRect(3, 3, width - 6, height - 6, 
                         UIConstants.RADIUS_MD, UIConstants.RADIUS_MD);
        
        // Paint background based on type
        switch (type) {
            case SUCCESS:
                g2d.setColor(UIConstants.SUCCESS);
                break;
            case WARNING:
                g2d.setColor(UIConstants.WARNING);
                break;
            case ERROR:
                g2d.setColor(UIConstants.DANGER);
                break;
            case INFO:
            default:
                g2d.setColor(UIConstants.ACCENT);
                break;
        }
        g2d.fillRoundRect(0, 0, width, height, UIConstants.RADIUS_MD, UIConstants.RADIUS_MD);
        
        g2d.dispose();
        super.paintComponent(g);
    }
    
    public void dismiss() {
        if (hideTimer != null) {
            hideTimer.stop();
        }
        fadeOut();
    }
}
