package com.zombicide.missiongen.ui.components.notification;

import java.awt.Component;
import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import com.zombicide.missiongen.ui.theme.UIConstants;

/**
 * Manager for displaying toast notifications.
 * Toasts appear in the top-right corner and auto-dismiss after a delay.
 */
public class ToastManager {
    
    private static ToastManager instance;
    private JPanel toastContainer;
    private List<ToastNotification> activeToasts;
    
    private ToastManager() {
        activeToasts = new ArrayList<>();
    }
    
    public static ToastManager getInstance() {
        if (instance == null) {
            instance = new ToastManager();
        }
        return instance;
    }
    
    /**
     * Initializes the toast container in the given parent component.
     * Should be called once during application startup.
     * 
     * @param parent The parent component (typically the main window)
     */
    public void initialize(Container parent) {
        if (toastContainer != null) {
            return; // Already initialized
        }
        
        toastContainer = new JPanel();
        toastContainer.setOpaque(false);
        toastContainer.setLayout(new GridBagLayout());
        
        // Add to layered pane if available
        if (parent instanceof javax.swing.JFrame) {
            JLayeredPane layeredPane = ((javax.swing.JFrame) parent).getLayeredPane();
            layeredPane.add(toastContainer, JLayeredPane.POPUP_LAYER);
            
            // Position in top-right corner
            parent.addComponentListener(new java.awt.event.ComponentAdapter() {
                @Override
                public void componentResized(java.awt.event.ComponentEvent e) {
                    positionContainer(parent);
                }
            });
            positionContainer(parent);
        }
    }
    
    private void positionContainer(Container parent) {
        if (toastContainer == null) {
            return;
        }
        
        int x = parent.getWidth() - 320; // 300 toast width + 20 margin
        int y = 20;
        toastContainer.setBounds(x, y, 320, parent.getHeight() - 40);
    }
    
    /**
     * Shows a success toast notification.
     * 
     * @param message The message to display
     */
    public void showSuccess(String message) {
        showToast(message, ToastNotification.ToastType.SUCCESS);
    }
    
    /**
     * Shows an info toast notification.
     * 
     * @param message The message to display
     */
    public void showInfo(String message) {
        showToast(message, ToastNotification.ToastType.INFO);
    }
    
    /**
     * Shows a warning toast notification.
     * 
     * @param message The message to display
     */
    public void showWarning(String message) {
        showToast(message, ToastNotification.ToastType.WARNING);
    }
    
    /**
     * Shows an error toast notification.
     * 
     * @param message The message to display
     */
    public void showError(String message) {
        showToast(message, ToastNotification.ToastType.ERROR);
    }
    
    private void showToast(String message, ToastNotification.ToastType type) {
        if (toastContainer == null) {
            System.err.println("ToastManager not initialized. Call initialize() first.");
            return;
        }
        
        SwingUtilities.invokeLater(() -> {
            ToastNotification toast = new ToastNotification(message, type);
            activeToasts.add(toast);
            
            // Add to container
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.gridx = 0;
            gbc.gridy = activeToasts.size() - 1;
            gbc.anchor = GridBagConstraints.NORTHEAST;
            gbc.insets = new Insets(0, 0, UIConstants.SPACING_SM, 0);
            
            toastContainer.add(toast, gbc);
            toastContainer.revalidate();
            toastContainer.repaint();
            
            // Remove from list after animation completes
            javax.swing.Timer cleanupTimer = new javax.swing.Timer(4000, e -> {
                activeToasts.remove(toast);
            });
            cleanupTimer.setRepeats(false);
            cleanupTimer.start();
        });
    }
    
    /**
     * Dismisses all active toasts.
     */
    public void dismissAll() {
        for (ToastNotification toast : new ArrayList<>(activeToasts)) {
            toast.dismiss();
        }
        activeToasts.clear();
    }
}
