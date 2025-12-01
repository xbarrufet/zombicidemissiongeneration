package com.zombicide.missiongen.panels.tiles;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zombicide.missiongen.model.Tile;
import com.zombicide.missiongen.panels.interfaces.PanelSelectionListener;
import com.zombicide.missiongen.services.PersistanceService;

public class ZoneSelecionTiles extends JPanel {
    private static final Logger logger = LoggerFactory.getLogger(ZoneSelecionTiles.class);

    private PersistanceService persistanceService;

    private JComboBox<String> editionsChoice;
    private JComboBox<String> collectionsChoice;
    private JButton generateButton;

    private JList<String> tilesList;
    private DefaultListModel<String> tilesListModel;
    private JButton saveTileButton;

    private String selectedEdition;
    private String selectedCollection;
    private Tile currentTile;
    private final java.util.List<PanelSelectionListener> tileSelectionListeners;

    public ZoneSelecionTiles() {
        persistanceService = new PersistanceService();
        tileSelectionListeners = new java.util.ArrayList<>();

        setBackground(new Color(230, 230, 250));
        Dimension fixedSize = new Dimension(250, 750);
        setPreferredSize(fixedSize);
        setMinimumSize(fixedSize);
        setMaximumSize(fixedSize);

        // Add border
        setBorder(javax.swing.BorderFactory.createCompoundBorder(
                javax.swing.BorderFactory.createMatteBorder(0, 0, 0, 1, Color.GRAY),
                javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10)));

        initComponents();
        setupLayout();
        loadEditions();
    }

    private void initComponents() {
        // Editions dropdown
        editionsChoice = new JComboBox<>();
        editionsChoice.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onEditionSelected();
            }
        });

        // Collections dropdown
        collectionsChoice = new JComboBox<>();
        collectionsChoice.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onCollectionSelected();
            }
        });

        // Generate button
        generateButton = new JButton("Generar Tiles");
        generateButton.setEnabled(false);
        generateButton.addActionListener(e -> onGenerateTiles());

        // Tiles list
        tilesListModel = new DefaultListModel<>();
        tilesList = new JList<>(tilesListModel);
        tilesList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tilesList.setVisibleRowCount(20);
        tilesList.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    onTileSelected();
                }
            }
        });

        // Save Tile button
        saveTileButton = new JButton("Save Tile");
        saveTileButton.setEnabled(false);
        saveTileButton.addActionListener(e -> onSaveTile());
    }

    private void setupLayout() {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.weightx = 1.0;

        // Editions label
        gbc.gridx = 0;
        gbc.gridy = 0;
        add(new JLabel("Ediciones:"), gbc);

        // Editions dropdown
        gbc.gridy = 1;
        add(editionsChoice, gbc);

        // Collections label
        gbc.gridy = 2;
        add(new JLabel("Colecciones:"), gbc);

        // Collections dropdown
        gbc.gridy = 3;
        add(collectionsChoice, gbc);

        // Generate button
        gbc.gridy = 4;
        gbc.insets = new Insets(15, 5, 5, 5);
        add(generateButton, gbc);

        // Tiles label
        gbc.gridy = 5;
        gbc.insets = new Insets(15, 5, 5, 5);
        add(new JLabel("Tiles:"), gbc);

        // Tiles list (wrapped in scroll pane)
        gbc.gridy = 6;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(5, 5, 5, 5);
        add(new JScrollPane(tilesList), gbc);

        // Save Tile button - aligned to bottom
        gbc.gridy = 7;
        gbc.weighty = 0.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(15, 5, 5, 5);
        add(saveTileButton, gbc);
    }

    private void loadEditions() {
        editionsChoice.removeAllItems();
        java.util.List<String> editions = persistanceService.getEditions();

        if (editions.isEmpty()) {
            editionsChoice.addItem("(No editions found)");
            editionsChoice.setEnabled(false);
            logger.info("No editions found");
        } else {
            for (String edition : editions) {
                editionsChoice.addItem(edition);
            }
            editionsChoice.setEnabled(true);
            logger.info("Loaded {} editions", editions.size());

            // Auto-select first edition
            if (editionsChoice.getItemCount() > 0) {
                editionsChoice.setSelectedIndex(0);
                // Listener will trigger loadCollections
            }
        }
    }

    private void loadCollections() {
        collectionsChoice.removeAllItems();
        tilesListModel.clear();

        if (selectedEdition == null || selectedEdition.equals("(No editions found)")) {
            collectionsChoice.addItem("(Select edition first)");
            collectionsChoice.setEnabled(false);
            return;
        }

        java.util.List<String> collections = persistanceService.getCollections(selectedEdition);

        if (collections.isEmpty()) {
            collectionsChoice.addItem("(No collections found)");
            collectionsChoice.setEnabled(false);
            logger.info("No collections found for edition: {}", selectedEdition);
        } else {
            for (String collection : collections) {
                collectionsChoice.addItem(collection);
            }
            collectionsChoice.setEnabled(true);
            logger.info("Loaded {} collections for edition: {}", collections.size(), selectedEdition);

            // Auto-select first collection
            if (collectionsChoice.getItemCount() > 0) {
                collectionsChoice.setSelectedIndex(0);
                // Listener will trigger loadTiles
            }
        }
    }

    private void loadTiles() {
        tilesListModel.clear();

        if (selectedEdition == null || selectedCollection == null) {
            logger.warn("Cannot load tiles: edition or collection not selected");
            return;
        }

        java.util.List<String> tiles = persistanceService.getTiles(selectedEdition, selectedCollection);

        if (tiles.isEmpty()) {
            tilesListModel.addElement("(No tiles found)");
            logger.info("No tiles found for edition: {}, collection: {}", selectedEdition, selectedCollection);
        } else {
            for (String tile : tiles) {
                tilesListModel.addElement(tile);
            }
            logger.info("Loaded {} tiles for edition: {}, collection: {}",
                    tiles.size(), selectedEdition, selectedCollection);
        }
    }

    private void updateGenerateButton() {
        int tilesToGenerate = persistanceService.getTilesToGenerate(selectedEdition, selectedCollection);
        generateButton.setEnabled(tilesToGenerate > 0);
        generateButton.setText(tilesToGenerate > 0 ? "Generate " + tilesToGenerate + " Tiles" : "No tiles to generate");
    }

    private void onEditionSelected() {
        Object selected = editionsChoice.getSelectedItem();
        if (selected != null) {
            selectedEdition = (String) selected;
            logger.info("Edition selected: {}", selectedEdition);
            loadCollections();
        }
    }

    private void onCollectionSelected() {
        Object selected = collectionsChoice.getSelectedItem();
        if (selected != null) {
            selectedCollection = (String) selected;
            logger.info("Collection selected: {}", selectedCollection);
            loadTiles();
            updateGenerateButton();
        }
    }

    private void onGenerateTiles() {
        logger.info("Generate Tiles button clicked");
        persistanceService.generateTiles(selectedEdition, selectedCollection);
        loadTiles();
        updateGenerateButton();
    }

    private void onTileSelected() {
        String tileName = tilesList.getSelectedValue();
        if (tileName != null && !tileName.equals("(No tiles found)")) {
            logger.info("Tile selected: {}", tileName);

            Tile tile = persistanceService.getTile(selectedEdition, selectedCollection, tileName);
            this.currentTile = tile;
            saveTileButton.setEnabled(true);

            if (tile != null) {
                notifyTileSelected(tile);
            }
        } else {
            saveTileButton.setEnabled(false);
        }
    }

    public void addTileSelectionListener(PanelSelectionListener listener) {
        if (listener != null && !tileSelectionListeners.contains(listener)) {
            tileSelectionListeners.add(listener);
        }
    }

    public void removeTileSelectionListener(PanelSelectionListener listener) {
        tileSelectionListeners.remove(listener);
    }

    private void notifyTileSelected(Tile tile) {
        for (PanelSelectionListener listener : tileSelectionListeners) {
            listener.onTileSelected(tile);
        }
    }

    private void onSaveTile() {
        saveCurrentTile();
    }

    public void saveCurrentTile() {
        if (currentTile == null) {
            logger.warn("No tile to save");
            return;
        }

        logger.info("Saving tile: {}", currentTile.getTileName());
        persistanceService.persistTile(currentTile);
        logger.info("Tile saved successfully: {}", currentTile.getTileName());
    }

    // Getters for external access
    public String getSelectedEdition() {
        return selectedEdition;
    }

    public String getSelectedCollection() {
        return selectedCollection;
    }

    public String getSelectedTile() {
        return tilesList.getSelectedValue();
    }
}
