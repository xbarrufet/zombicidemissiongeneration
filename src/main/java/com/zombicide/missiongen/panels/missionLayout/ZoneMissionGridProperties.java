package com.zombicide.missiongen.panels.missionLayout;

import java.util.Collections;
import java.util.Vector;

import com.zombicide.missiongen.model.Tile;
import com.zombicide.missiongen.panels.components.ZonePropertiesPanel;
import com.zombicide.missiongen.panels.interfaces.MissionLayoutUpdate;

public class ZoneMissionGridProperties extends ZonePropertiesPanel
        implements com.zombicide.missiongen.panels.interfaces.TileGridListener,
        com.zombicide.missiongen.panels.interfaces.PanelSelectionListener {

    private javax.swing.JTextField missionNameField;
    private javax.swing.JRadioButton rb2x1;
    private javax.swing.JRadioButton rb2x2;
    private javax.swing.JRadioButton rb2x3;
    private javax.swing.JRadioButton rb3x2;
    private javax.swing.JRadioButton rb3x3;
    private javax.swing.JRadioButton rb4x3;
    private javax.swing.JRadioButton rb4x4;
    private javax.swing.ButtonGroup bgGridSize;

    private javax.swing.JList<String> tilesList;
    private javax.swing.DefaultListModel<String> tilesListModel;
    private javax.swing.JScrollPane tilesScrollPane;

    private MissionLayoutUpdate listener;
    private ZoneMissionGrid zoneMissionGrid;
    private final java.util.List<com.zombicide.missiongen.panels.interfaces.PanelSelectionListener> tileSelectionListeners = new java.util.ArrayList<>();

    private com.zombicide.missiongen.services.PersistanceService persistanceService;
    private String currentEdition;
    private String currentCollection;

    public ZoneMissionGridProperties(MissionLayoutUpdate listener, ZoneMissionGrid zoneMissionGrid) {
        super();
        this.listener = listener;
        this.zoneMissionGrid = zoneMissionGrid;
        this.persistanceService = new com.zombicide.missiongen.services.PersistanceService();
        this.zoneMissionGrid.addTileGridListener(this);
        initComponents();
        setupLayout();
        setupListeners();
    }

    private void initComponents() {
        missionNameField = new javax.swing.JTextField(15);
        missionNameField.setText("mission1");
        listener.onMissionNameUpdated(missionNameField.getText());

        rb2x1 = new javax.swing.JRadioButton("2x1");
        rb2x2 = new javax.swing.JRadioButton("2x2");
        rb2x3 = new javax.swing.JRadioButton("2x3");
        rb3x2 = new javax.swing.JRadioButton("3x2");
        rb3x3 = new javax.swing.JRadioButton("3x3");
        rb4x3 = new javax.swing.JRadioButton("4x3");
        rb4x4 = new javax.swing.JRadioButton("4x4");

        bgGridSize = new javax.swing.ButtonGroup();
        bgGridSize.add(rb2x1);
        bgGridSize.add(rb2x2);
        bgGridSize.add(rb2x3);
        bgGridSize.add(rb3x2);
        bgGridSize.add(rb3x3);
        bgGridSize.add(rb4x3);
        bgGridSize.add(rb4x4);

        rb2x2.setSelected(true);

        // Tiles List
        tilesListModel = new javax.swing.DefaultListModel<>();
        tilesList = new javax.swing.JList<>(tilesListModel);
        tilesList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        tilesScrollPane = new javax.swing.JScrollPane(tilesList);
        tilesScrollPane.setPreferredSize(new java.awt.Dimension(180, 200));
    }

    private void setupListeners() {
        // Grid size listeners
        java.awt.event.ActionListener actionListener = e -> fireGridSizeChanged();
        rb2x1.addActionListener(actionListener);
        rb2x2.addActionListener(actionListener);
        rb2x3.addActionListener(actionListener);
        rb3x2.addActionListener(actionListener);
        rb3x3.addActionListener(actionListener);
        rb4x3.addActionListener(actionListener);
        rb4x4.addActionListener(actionListener);

        // Mission name listener
        missionNameField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            @Override
            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                fireMissionNameChanged();
            }

            @Override
            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                fireMissionNameChanged();
            }

            @Override
            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                fireMissionNameChanged();
            }
        });

        // Tile selection listener
        tilesList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                String selectedTileName = tilesList.getSelectedValue();
                if (selectedTileName != null && currentEdition != null && currentCollection != null) {
                    // Load the tile from persistence service
                    com.zombicide.missiongen.model.Tile tile = persistanceService.getTile(currentEdition,
                            currentCollection, selectedTileName);

                    if (tile != null) {
                        com.zombicide.missiongen.model.board.TileBoard tileBoard = tile.getBoard();
                        // Notify the grid that a tile has been selected
                        zoneMissionGrid.setTile(tileBoard);
                        // Optionally notify other listeners
                        fireTileSelected(tile);
                    }
                }
            }
        });
    }

    public void addTileSelectionListener(com.zombicide.missiongen.panels.interfaces.PanelSelectionListener listener) {
        tileSelectionListeners.add(listener);
    }

    private void fireTileSelected(com.zombicide.missiongen.model.Tile tile) {
        for (com.zombicide.missiongen.panels.interfaces.PanelSelectionListener listener : tileSelectionListeners) {
            listener.onTileSelected(tile);
        }
    }

    private void fireMissionNameChanged() {
        if (listener != null) {
            listener.onMissionNameUpdated(missionNameField.getText());
        }
    }

    private void fireGridSizeChanged() {
        int rows = 2;
        int cols = 2;

        if (rb2x1.isSelected()) {
            cols = 2;
            rows = 1;
        } else if (rb2x2.isSelected()) {
            rows = 2;
            cols = 2;
        } else if (rb2x3.isSelected()) {
            cols = 2;
            rows = 3;
        } else if (rb3x2.isSelected()) {
            cols = 3;
            rows = 2;
        } else if (rb3x3.isSelected()) {
            cols = 3;
            rows = 3;
        } else if (rb4x3.isSelected()) {
            cols = 4;
            rows = 3;
        } else if (rb4x4.isSelected()) {
            cols = 4;
            rows = 4;
        }

        this.zoneMissionGrid.reset(cols, rows); // reset(width, height)
    }

    private void setupLayout() {
        setLayout(new java.awt.GridBagLayout());
        java.awt.GridBagConstraints gbc = new java.awt.GridBagConstraints();

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = java.awt.GridBagConstraints.WEST;
        gbc.insets = new java.awt.Insets(5, 5, 5, 5);

        // Mission Name
        add(new javax.swing.JLabel("Mission Name:"), gbc);
        gbc.gridy++;
        gbc.fill = java.awt.GridBagConstraints.HORIZONTAL;
        add(missionNameField, gbc);
        gbc.fill = java.awt.GridBagConstraints.NONE;

        // Grid Size
        gbc.gridy++;
        gbc.insets = new java.awt.Insets(15, 5, 5, 5);
        add(new javax.swing.JLabel("Grid Size:"), gbc);
        gbc.insets = new java.awt.Insets(5, 5, 5, 5);

        gbc.gridy++;
        add(rb2x1, gbc);

        gbc.gridy++;
        add(rb2x2, gbc);

        gbc.gridy++;
        add(rb2x3, gbc);

        gbc.gridy++;
        add(rb3x2, gbc);

        gbc.gridy++;
        add(rb3x3, gbc);

        gbc.gridy++;
        add(rb4x3, gbc);

        gbc.gridy++;
        add(rb4x4, gbc);

        // Push everything to the top
        gbc.gridy++;
        gbc.insets = new java.awt.Insets(15, 5, 5, 5);
        add(new javax.swing.JLabel("Available Tiles:"), gbc);

        gbc.gridy++;
        gbc.insets = new java.awt.Insets(5, 5, 5, 5);
        gbc.fill = java.awt.GridBagConstraints.BOTH;
        gbc.weighty = 1.0;
        add(tilesScrollPane, gbc);
    }

    public String getMissionName() {
        return missionNameField.getText();
    }

    private void loadTiles() {
        tilesListModel.clear();
        if (currentEdition != null && currentCollection != null) {
            java.util.List<String> tiles = persistanceService.getTiles(currentEdition, currentCollection);
            for (String tile : tiles) {
                tilesListModel.addElement(tile);
            }
        }
    }

    // TileGridListener implementation
    @Override
    public void onTilePlaced(int row, int col, com.zombicide.missiongen.model.board.TileBoard tileBoard) {
        // Remove from list if present (assuming tileBoard has info to identify it, or
        // we rely on logic)
        // Since TileBoard doesn't strictly have the tile name in a simple way
        // accessible here without casting or checking
        // We might need to know WHICH tile was placed.
        // The ZoneMissionGrid knows the tile.
        // Actually, we remove the SELECTED tile from the list when it's placed.
        String selected = tilesList.getSelectedValue();
        if (selected != null) {
            tilesListModel.removeElement(selected);
            tilesList.clearSelection();
            // Notify that no tile is selected?
            fireTileSelected(null);
        }
    }

    @Override
    public void onTileRemoved(int row, int col, com.zombicide.missiongen.model.board.TileBoard tileBoard) {
        // Add back to list
        if (tileBoard != null) {
            String boardId = tileBoard.getBoardId();
            // boardId format: edition.collection.tileName
            // We want to extract tileName
            if (boardId != null && currentEdition != null && currentCollection != null) {
                String prefix = currentEdition + "." + currentCollection + ".";
                if (boardId.startsWith(prefix)) {
                    String tileName = boardId.substring(prefix.length());
                    addTileBack(tileName);
                } else {
                    // Fallback: try to split by dots and take last part, or just use boardId if it
                    // doesn't match
                    // This handles cases where maybe edition/collection changed?
                    // But if edition changed, the list would be reloaded anyway.
                    // So just logging warning if it doesn't match might be enough, or try to
                    // recover.
                    String[] parts = boardId.split("\\.");
                    if (parts.length > 0) {
                        addTileBack(parts[parts.length - 1]);
                    }
                }
            }
        }
    }

    public void addTileBack(String tileName) {
        if (tileName != null && !tilesListModel.contains(tileName)) {
            // 1. Añadir el nuevo elemento al final (temporalmente)
            tilesListModel.addElement(tileName);
            // 2. Extraer todos los elementos a un Vector (o ArrayList)
            Vector<String> listData = new Vector<>();
            for (int i = 0; i < tilesListModel.getSize(); i++) {
                listData.add(tilesListModel.getElementAt(i));
            }
            // 3. Ordenar la lista
            // Collections.sort() ordena Vector o ArrayList alfabéticamente por defecto
            Collections.sort(listData);

            // 4. Limpiar el modelo y rellenarlo con la lista ordenada
            tilesListModel.clear();
            for (String tile : listData) {
                tilesListModel.addElement(tile);
            }
        }
    }

    @Override
    public void onTileSelected(Tile tile) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'onTileSelected'");
    }

    @Override
    public void onEditionCollectionSelected(String edition, String collection) {
        // load new Tiles based on edition and collection
        this.currentEdition = edition;
        this.currentCollection = collection;
        loadTiles();

    }
}
