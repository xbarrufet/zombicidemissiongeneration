package com.zombicide.missiongen;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zombicide.missiongen.model.Tile;
import com.zombicide.missiongen.panels.ZoneSelecion;
import com.zombicide.missiongen.panels.interfaces.TileSelectionListener;
import com.zombicide.missiongen.panels.tiles.ZoneWorkAreaTiles;

public class MainWindow extends JFrame implements TileSelectionListener {
    private static final Logger logger = LoggerFactory.getLogger(MainWindow.class);

    private ZoneSelecion zoneSelecion;
    private ZoneWorkAreaTiles currentWorkAreaPanel;

    public MainWindow() {
        super("Mission Generation - Zombicide");
        initComponents();
        setupLayout();
        setupWindow();
    }

    private void initComponents() {
        zoneSelecion = new ZoneSelecion();
        currentWorkAreaPanel = new ZoneWorkAreaTiles();
        zoneSelecion.addTileSelectionListener(this);
        logger.info("Components initialized");
    }

    private void setupLayout() {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        // ZoneSelecion - Left panel (250x750)
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        add(zoneSelecion, gbc);

        // Work Area (contains ZoneDraw + ZoneProperties) - Right side (1000x750)
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        add(currentWorkAreaPanel, gbc);

        logger.info("Layout configured");
    }

    /**
     * Replaces the current work area panel with a new one.
     * This allows switching between different work area implementations.
     * 
     * @param newWorkAreaPanel The new panel to replace the current work area
     */
    public void replaceWorkArea(ZoneWorkAreaTiles newWorkAreaPanel) {
        if (newWorkAreaPanel == null) {
            logger.warn("Attempted to replace work area with null panel");
            return;
        }

        // Remove the current work area panel
        remove(currentWorkAreaPanel);

        // Update reference
        currentWorkAreaPanel = newWorkAreaPanel;

        // Add the new work area panel in the same position
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        add(currentWorkAreaPanel, gbc);

        // Refresh the layout
        validate();
        repaint();

        logger.info("Work area panel replaced: {}", newWorkAreaPanel.getClass().getSimpleName());
    }

    /**
     * Gets the current work area panel.
     * 
     * @return The current work area panel
     */
    public ZoneWorkAreaTiles getCurrentWorkAreaPanel() {
        return currentWorkAreaPanel;
    }

    public ZoneSelecion getZoneSelecion() {
        return zoneSelecion;
    }

    private void setupWindow() {
        pack(); // Size window to fit preferred sizes of components
        setLocationRelativeTo(null); // Center on screen
        setResizable(false);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        // Add window listener for close event
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                onWindowClosing();
            }
        });

        logger.info("Window setup complete");
    }

    private void onWindowClosing() {
        logger.info("Closing application");
        dispose();
        System.exit(0);
    }

    public void display() {
        setVisible(true);
        logger.info("Window displayed");
    }

    /**
     * Called when a tile is selected from ZoneSelecion
     */
    @Override
    public void onTileSelected(Tile tile) {
        logger.info("Tile selected: {}", tile.getTileName());
        this.currentWorkAreaPanel.setBoard(tile.getBoard());
    }
}
