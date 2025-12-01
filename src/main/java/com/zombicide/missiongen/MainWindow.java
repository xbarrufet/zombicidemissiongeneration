package com.zombicide.missiongen;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zombicide.missiongen.model.Tile;
import com.zombicide.missiongen.model.board.MissionFactoryService;
import com.zombicide.missiongen.panels.components.ZoneWorkAreaPanel;
import com.zombicide.missiongen.panels.interfaces.LayoutChangeListener;
import com.zombicide.missiongen.panels.interfaces.PanelSelectionListener;
import com.zombicide.missiongen.panels.missionLayout.ZoneWorkAreaMissionLayout;
import com.zombicide.missiongen.panels.missions.ZoneSelecionMissions;
import com.zombicide.missiongen.panels.missions.ZoneWorkAreaMissions;
import com.zombicide.missiongen.panels.tiles.ZoneSelecionTiles;
import com.zombicide.missiongen.panels.tiles.ZoneWorkAreaTiles;

public class MainWindow extends JFrame implements LayoutChangeListener {
    private static final Logger logger = LoggerFactory.getLogger(MainWindow.class);

    private ZoneSelecionTiles zoneSelecionTiles;
    private ZoneSelecionMissions zoneSelecionMissions;
    private JPanel currentSelectionPanel;
    private ZoneWorkAreaTiles tileWorkAreaPanel;
    private ZoneWorkAreaMissionLayout missionWorkAreaPanel;
    private ZoneWorkAreaMissions missionsViewerPanel;
    private ZoneWorkAreaPanel currentWorkAreaPanel;

    public MainWindow() {
        super("Mission Generation - Zombicide");
        initComponents();
        setupMenu();
        setupLayout();
        setupWindow();
    }

    private void setupMenu() {
        JMenuBar menuBar = new JMenuBar();

        JMenu navigationMenu = new JMenu("Navigation");

        JMenuItem tilesItem = new JMenuItem("Tiles");
        tilesItem.addActionListener(e -> {
            logger.info("Switching to Tiles workflow via menu");
            this.replaceSelectionPanel(zoneSelecionTiles);
            this.onTileLayoutSelected();
        });

        JMenuItem missionsItem = new JMenuItem("Missions");
        missionsItem.addActionListener(e -> {
            logger.info("Switching to Missions workflow via menu");
            this.replaceSelectionPanel(zoneSelecionMissions);
            this.onMissionLayoutSelected();
        });

        navigationMenu.add(tilesItem);
        navigationMenu.add(missionsItem);

        menuBar.add(navigationMenu);
        setJMenuBar(menuBar);
    }

    private void initComponents() {
        zoneSelecionTiles = new ZoneSelecionTiles();
        zoneSelecionMissions = new ZoneSelecionMissions(new MissionFactoryService(), this);

        tileWorkAreaPanel = new ZoneWorkAreaTiles();
        tileWorkAreaPanel.setOnSaveRequired(() -> zoneSelecionTiles.saveCurrentTile());

        missionWorkAreaPanel = new ZoneWorkAreaMissionLayout(zoneSelecionMissions);
        missionsViewerPanel = new ZoneWorkAreaMissions();

        zoneSelecionTiles.addTileSelectionListener(tileWorkAreaPanel);
        zoneSelecionMissions.addMissionSelectionListener(missionsViewerPanel);
        zoneSelecionMissions.addCollectionSelectionListener(missionWorkAreaPanel.getZoneMissionGridProperties());

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

        // Start with Tiles selection
        currentSelectionPanel = zoneSelecionTiles;
        add(currentSelectionPanel, gbc);

        // // Work Area (contains ZoneDraw + ZoneProperties) - Right side (1000x750)
        // gbc.gridx = 1;
        // gbc.gridy = 0;
        // gbc.weightx = 1.0;
        // gbc.weighty = 1.0;
        // gbc.fill = GridBagConstraints.BOTH;
        // add(tileWorkAreaPanel, gbc);
        this.replaceWorkArea(tileWorkAreaPanel);

        logger.info("Layout configured");
    }

    /**
     * Replaces the current work area panel with a new one.
     * This allows switching between different work area implementations.
     * 
     * @param newWorkAreaPanel The new panel to replace the current work area
     */
    public void replaceWorkArea(ZoneWorkAreaPanel newWorkAreaPanel) {
        if (newWorkAreaPanel == null) {
            logger.warn("Attempted to replace work area with null panel");
            return;
        }
        // Remove the current work area panel
        if (currentWorkAreaPanel != null) {
            remove(currentWorkAreaPanel);
        }
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

    public void replaceSelectionPanel(JPanel newSelectionPanel) {
        if (newSelectionPanel == null) {
            return;
        }
        if (currentSelectionPanel != null) {
            remove(currentSelectionPanel);
        }
        currentSelectionPanel = newSelectionPanel;

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        add(currentSelectionPanel, gbc);

        validate();
        repaint();
    }

    /**
     * Gets the current work area panel.
     * 
     * @return The current work area panel
     */
    public ZoneWorkAreaTiles getTileWorkAreaPanel() {
        return tileWorkAreaPanel;
    }

    public ZoneSelecionTiles getZoneSelecionTiles() {
        return zoneSelecionTiles;
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

    @Override
    public void onMissionLayoutSelected() {
        this.replaceWorkArea(this.missionWorkAreaPanel);
    }

    @Override
    public void onTileLayoutSelected() {
        this.replaceWorkArea(this.tileWorkAreaPanel);
    }

    @Override
    public void onMissionSelected() {
        logger.info("Switching to mission viewer");
        this.replaceWorkArea(this.missionsViewerPanel);
        // Force layout update to ensure panel has correct size
        this.missionsViewerPanel.revalidate();
        this.missionsViewerPanel.repaint();
    }
}
