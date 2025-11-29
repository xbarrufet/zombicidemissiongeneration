package com.zombicide.missiongen.panels.tiles;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.stream.Collectors;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zombicide.missiongen.model.BoardArea;
import com.zombicide.missiongen.model.BoardArea.AreaType;
import com.zombicide.missiongen.model.BoardAreaConnection;
import com.zombicide.missiongen.panels.components.ZonePropertiesPanel;
import com.zombicide.missiongen.panels.interfaces.BoardChangeListener;

public class ZoneProperties extends ZonePropertiesPanel {
    private static final Logger logger = LoggerFactory.getLogger(ZoneProperties.class);

    private java.util.List<BoardArea> selectedAreas;
    private ButtonGroup areaTypeGroup;
    private JRadioButton basicCheckbox;
    private JRadioButton indoorCheckbox;
    private JRadioButton outdoorCheckbox;
    private JLabel titleLabel;
    private JLabel areaIdLabel;
    private JPanel connectionsPanel;
    private JButton createConnectionButton;
    private JButton deleteAreasButton;
    private final java.util.List<BoardChangeListener> boardChangeListeners;

    public ZoneProperties() {
        super();
        this.selectedAreas = new java.util.ArrayList<>();
        this.boardChangeListeners = new java.util.ArrayList<>();

        // Add border
        setBorder(javax.swing.BorderFactory.createCompoundBorder(
                javax.swing.BorderFactory.createMatteBorder(0, 1, 0, 0, Color.GRAY),
                javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10)));

        initComponents();
        setupLayout();
        updateAreaIDLabel();
        updateAreaTypeSelectionGroup();
        updateConnectionsPanel();
    }

    private void initComponents() {
        titleLabel = new JLabel("Area Properties");
        areaIdLabel = new JLabel("Area ID: -");
        connectionsPanel = new JPanel();
        connectionsPanel.setLayout(new GridBagLayout());

        createConnectionButton = new JButton("Create Connection");
        createConnectionButton.setEnabled(false);
        createConnectionButton.addActionListener(e -> onCreateConnection());

        deleteAreasButton = new JButton("Delete Areas");
        deleteAreasButton.setEnabled(false);
        deleteAreasButton.addActionListener(e -> onDeleteAreas());

        areaTypeGroup = new ButtonGroup();

        basicCheckbox = new JRadioButton("BASIC");
        areaTypeGroup.add(basicCheckbox);
        basicCheckbox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (basicCheckbox.isSelected()) {
                    onAreaTypeChanged(AreaType.BASIC);
                }
            }
        });

        indoorCheckbox = new JRadioButton("INDOOR");
        areaTypeGroup.add(indoorCheckbox);
        indoorCheckbox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (indoorCheckbox.isSelected()) {
                    onAreaTypeChanged(AreaType.INDOOR);
                }
            }
        });

        outdoorCheckbox = new JRadioButton("OUTDOOR");
        areaTypeGroup.add(outdoorCheckbox);
        outdoorCheckbox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (outdoorCheckbox.isSelected()) {
                    onAreaTypeChanged(AreaType.OUTDOOR);
                }
            }
        });
    }

    private void onDeleteAreas() {
        for (BoardArea area : this.selectedAreas) {
            logger.info("Deleting area: {}", area.getId());
            this.getBoard().removeArea(area.getId());
        }
        this.selectedAreas.clear();
        this.updateAreaIDLabel();
        this.updateAreaTypeSelectionGroup();
        this.updateConnectionsPanel();

        // Notify listeners of board changes
        notifyAreasChanged();

        this.revalidate();
        this.repaint();
    }

    public void addBoardChangeListener(BoardChangeListener listener) {
        if (listener != null && !boardChangeListeners.contains(listener)) {
            boardChangeListeners.add(listener);
        }
    }

    public void removeBoardChangeListener(BoardChangeListener listener) {
        boardChangeListeners.remove(listener);
    }

    private void notifyAreasChanged() {
        for (BoardChangeListener listener : boardChangeListeners) {
            listener.onAreasChanged();
        }
    }

    private void notifyConnectionsChanged() {
        for (BoardChangeListener listener : boardChangeListeners) {
            listener.onConnectionsChanged();
        }
    }

    private void setupLayout() {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.NORTH; // Align to top
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.weightx = 1.0;
        gbc.weighty = 0.0; // Don't expand vertically
        gbc.gridx = 0;

        // Title
        gbc.gridy = 0;
        gbc.insets = new Insets(10, 10, 15, 10);
        add(titleLabel, gbc);

        // Area ID label
        gbc.gridy = 1;
        gbc.insets = new Insets(5, 10, 15, 10);
        add(areaIdLabel, gbc);

        // Area Type label
        gbc.gridy = 2;
        gbc.insets = new Insets(10, 10, 5, 10);
        add(new JLabel("Area Type:"), gbc);

        // Radio buttons
        gbc.insets = new Insets(2, 20, 2, 10);

        gbc.gridy = 3;
        add(basicCheckbox, gbc);

        gbc.gridy = 4;
        add(indoorCheckbox, gbc);

        gbc.gridy = 5;
        add(outdoorCheckbox, gbc);

        // Delete Areas button
        gbc.gridy = 6;
        gbc.insets = new Insets(15, 10, 5, 10);
        add(deleteAreasButton, gbc);

        // Create Connection button
        gbc.gridy = 7;
        gbc.insets = new Insets(5, 10, 5, 10);
        add(createConnectionButton, gbc);

        // Connections section
        gbc.gridy = 8;
        gbc.insets = new Insets(10, 10, 5, 10);
        add(new JLabel("Connections:"), gbc);

        gbc.gridy = 9;
        gbc.insets = new Insets(5, 10, 5, 10);
        add(connectionsPanel, gbc);

        // Add empty panel at bottom to push everything to top
        gbc.gridy = 10;
        gbc.weighty = 1.0; // This will expand to fill remaining space
        gbc.fill = GridBagConstraints.BOTH;
        add(new JPanel(), gbc);
    }

    public void updateAreasSelection(java.util.List<BoardArea> areas) {
        this.selectedAreas = new java.util.ArrayList<>(areas);
        updateAreaIDLabel();
        updateAreaTypeSelectionGroup();
        updateConnectionsPanel();
        updateDeleteAreasButton();
        this.revalidate();
        this.repaint();
    }

    private void updateAreaIDLabel() {
        if (this.selectedAreas.isEmpty()) {
            areaIdLabel.setText("No Areas Selected");
            return;
        }
        String areaIds = selectedAreas.stream().map(x -> String.valueOf(x.getId()))
                .collect(Collectors.joining(", "));
        areaIdLabel.setText("Area ID: " + areaIds);
    }

    public void updateAreaTypeSelectionGroup() {

        // Enable radio buttons
        basicCheckbox.setEnabled(this.selectedAreas.size() > 0);
        indoorCheckbox.setEnabled(this.selectedAreas.size() > 0);
        outdoorCheckbox.setEnabled(this.selectedAreas.size() > 0);

        // Set the current selection based on area type
        if (this.selectedAreas.size() > 0) {
            AreaType currentType = this.selectedAreas.get(0).getAreaType();
            switch (currentType) {
                case BASIC:
                    basicCheckbox.setSelected(true);
                    break;
                case INDOOR:
                    indoorCheckbox.setSelected(true);
                    break;
                case OUTDOOR:
                    outdoorCheckbox.setSelected(true);
                    break;
            }
        } else {
            areaTypeGroup.clearSelection();
        }
    }

    private void updateDeleteAreasButton() {
        deleteAreasButton.setEnabled(this.selectedAreas.size() > 0);
        String deleteButtonText = "Delete Areas";
        if (this.selectedAreas.size() > 0) {
            deleteButtonText = "Delete Areas (" + this.selectedAreas.size() + ")";
        }
        deleteAreasButton.setText(deleteButtonText);
    }

    private void updateConnectionsPanel() {
        // enable disable button, only 2 areas selected allowed
        if (this.selectedAreas.size() < 2) {
            createConnectionButton.setEnabled(false);
            createConnectionButton.setText("No connections allowed");
        } else {
            createConnectionButton.setEnabled(true);
            createConnectionButton.setText("Con:" + this.selectedAreas.get(0).getId() + "->"
                    + this.selectedAreas.stream().skip(1).map(x -> String.valueOf(x.getId()))
                            .collect(Collectors.joining(", ")));
        }
        // Clear connections panel
        connectionsPanel.removeAll();
        connectionsPanel.revalidate();
        connectionsPanel.repaint();

        if (this.selectedAreas.size() == 0) {
            return;
        }
        // Find all connections for the 1st area in selectedAreas
        java.util.List<BoardAreaConnection> areaConnections = this.findConnectionsForArea(this.selectedAreas.get(0));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(2, 10, 2, 5);

        if (areaConnections.isEmpty()) {
            JLabel noConnectionsLabel = new JLabel("No connections");
            connectionsPanel.add(noConnectionsLabel, gbc);
        } else {
            for (BoardAreaConnection connection : areaConnections) {
                JPanel connectionRow = createConnectionRow(connection);
                connectionsPanel.add(connectionRow, gbc);
                gbc.gridy++;
            }
        }
        connectionsPanel.revalidate();
        connectionsPanel.repaint();
    }

    // gets the connections for the area
    private java.util.List<BoardAreaConnection> findConnectionsForArea(BoardArea area) {
        int areaIdA = area.getId();
        java.util.List<BoardAreaConnection> result = this.getBoard().getConnections().stream()
                .filter(connection -> connection.getAreaAId() == areaIdA || connection.getAreaBId() == areaIdA)
                .collect(Collectors.toList());
        return result;
    }

    private void onAreaTypeChanged(AreaType newType) {
        if (selectedAreas != null && selectedAreas.size() > 0) {
            for (BoardArea area : selectedAreas) {
                AreaType oldType = area.getAreaType();
                area.setAreaType(newType);
                logger.info("Area ID {} type changed: {} -> {}", area.getId(), oldType, newType);
            }
        }
    }

    private JPanel createConnectionRow(BoardAreaConnection connection) {
        JPanel row = new JPanel();
        row.setLayout(new BorderLayout(5, 0));

        JLabel connectionLabel = new JLabel("Area " + connection.getAreaAId() + " -> Area " + connection.getAreaBId());
        JButton deleteButton = new JButton("Delete");

        deleteButton.addActionListener(e -> {
            deleteConnection(connection);
        });

        row.add(connectionLabel, BorderLayout.CENTER);
        row.add(deleteButton, BorderLayout.EAST);

        return row;
    }

    private void deleteConnection(BoardAreaConnection connection) {
        if (this.getBoard() != null) {
            this.getBoard().removeConnection(connection.getAreaAId(), connection.getAreaBId());
            logger.info("Deleted connection: Area {} <-> Area {}", connection.getAreaAId(), connection.getAreaBId());
            updateConnectionsPanel();
            notifyConnectionsChanged();
        }
    }

    // creates connections from the first area to the rest
    private void onCreateConnection() {
        if (selectedAreas == null || selectedAreas.size() < 2 || this.getBoard() == null) {
            return;
        }

        int areaId1 = selectedAreas.get(0).getId();

        for (int i = 1; i < selectedAreas.size(); i++) {
            int areaId2 = selectedAreas.get(i).getId();
            if (connectionExists(areaId1, areaId2)) {
                logger.info("Connection between Area {} and Area {} already exists", areaId1, areaId2);
                continue;
            }
            BoardAreaConnection newConnection = new BoardAreaConnection(areaId1, areaId2);
            this.getBoard().addConnection(newConnection);
            logger.info("Created connection: Area {} <-> Area {}", areaId1, areaId2);
        }
        updateConnectionsPanel();
        notifyConnectionsChanged();
    }

    private boolean connectionExists(int areaId1, int areaId2) {
        if (this.getBoard() == null) {
            return false;
        }

        for (BoardAreaConnection connection : this.getBoard().getConnections()) {
            if ((connection.getAreaAId() == areaId1 && connection.getAreaBId() == areaId2) ||
                    (connection.getAreaAId() == areaId2 && connection.getAreaBId() == areaId1)) {
                return true;
            }
        }

        return false;
    }
}
