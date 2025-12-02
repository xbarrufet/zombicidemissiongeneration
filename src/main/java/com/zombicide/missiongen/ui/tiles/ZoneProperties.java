package com.zombicide.missiongen.ui.tiles;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JOptionPane;

import com.zombicide.missiongen.model.areas.Direction;
import com.zombicide.missiongen.model.areas.DoorDirection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zombicide.missiongen.model.areas.BoardArea;
import com.zombicide.missiongen.model.areas.BoardAreaConnection;
import com.zombicide.missiongen.model.areas.BoardArea.AreaType;
import com.zombicide.missiongen.ui.components.ZonePropertiesPanel;
import com.zombicide.missiongen.ui.interfaces.BoardSelectionListener;

public class ZoneProperties extends ZonePropertiesPanel implements BoardSelectionListener {
    private static final Logger logger = LoggerFactory.getLogger(ZoneProperties.class);

    private java.util.List<BoardArea> selectedAreas;
    private ButtonGroup areaTypeGroup;
    private JRadioButton streetCheckbox;
    private JRadioButton indoorLightCheckbox;
    private JRadioButton indoorDarkCheckbox;
    private JRadioButton outdoorCheckbox;
    private JLabel titleLabel;
    private JLabel areaIdLabel;
    private JPanel connectionsPanel;
    private JButton createConnectionButton;
    private JButton addEdgeConnectionButton;
    private JButton deleteAreasButton;
    private JButton splitHorizontalButton;
    private JButton splitVerticalButton;
    private ZoneTileDraw zoneTileDraw; // Direct reference instead of listeners

    public ZoneProperties(ZoneTileDraw zoneTileDraw) {
        super();
        this.zoneTileDraw = zoneTileDraw;
        this.selectedAreas = new java.util.ArrayList<>();

        // Register as selection listener directly
        if (this.zoneTileDraw != null) {
            this.zoneTileDraw.addSelectionListener(this);
        }

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

        addEdgeConnectionButton = new JButton("Add Edge Connection");
        addEdgeConnectionButton.setEnabled(false);
        addEdgeConnectionButton.addActionListener(e -> onAddEdgeConnection());

        deleteAreasButton = new JButton("Delete Areas");
        deleteAreasButton.setEnabled(false);
        deleteAreasButton.addActionListener(e -> onDeleteAreas());

        splitHorizontalButton = new JButton("Split Horizontal");
        splitHorizontalButton.setEnabled(false);
        splitHorizontalButton.addActionListener(e -> onSplitHorizontal());

        splitVerticalButton = new JButton("Split Vertical");
        splitVerticalButton.setEnabled(false);
        splitVerticalButton.addActionListener(e -> onSplitVertical());

        areaTypeGroup = new ButtonGroup();

        streetCheckbox = new JRadioButton("STREET");
        areaTypeGroup.add(streetCheckbox);
        streetCheckbox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (streetCheckbox.isSelected()) {
                    onAreaTypeChanged(AreaType.STREET);
                }
            }
        });

        indoorLightCheckbox = new JRadioButton("INDOOR LIGHT");
        areaTypeGroup.add(indoorLightCheckbox);
        indoorLightCheckbox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (indoorLightCheckbox.isSelected()) {
                    onAreaTypeChanged(AreaType.INDOOR_LIGHT);
                }
            }
        });

        indoorDarkCheckbox = new JRadioButton("INDOOR DARK");
        areaTypeGroup.add(indoorDarkCheckbox);
        indoorDarkCheckbox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (indoorDarkCheckbox.isSelected()) {
                    onAreaTypeChanged(AreaType.INDOOR_DARK);
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
            logger.info("Deleting area: {}", area.getAreaId());
            this.getBoard().removeArea(area.getAreaId());
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

    private void notifyAreasChanged() {
        if (zoneTileDraw != null) {
            zoneTileDraw.notifyAreasChanged();
        }
    }

    private void notifyConnectionsChanged() {
        if (zoneTileDraw != null) {
            zoneTileDraw.notifyConnectionsChanged();
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
        int currentRow = 0;
        gbc.gridy = currentRow++;
        gbc.insets = new Insets(10, 10, 15, 10);
        add(titleLabel, gbc);

        // Area ID label
        gbc.gridy = currentRow++;
        gbc.insets = new Insets(5, 10, 15, 10);
        add(areaIdLabel, gbc);

        // Area Type label
        gbc.gridy = currentRow++;
        gbc.insets = new Insets(10, 10, 5, 10);
        add(new JLabel("Area Type:"), gbc);

        // Radio buttons
        gbc.insets = new Insets(2, 20, 2, 10);

        gbc.gridy = currentRow++;
        add(streetCheckbox, gbc);

        gbc.gridy = currentRow++;
        add(indoorLightCheckbox, gbc);

        gbc.gridy = currentRow++;
        add(indoorDarkCheckbox, gbc);

        gbc.gridy = currentRow++;
        add(outdoorCheckbox, gbc);

        // Board Game Assets section
        gbc.gridy = currentRow++;
        gbc.insets = new Insets(15, 10, 5, 10);
        add(new JLabel("Board Game Assets:"), gbc);

        // Delete Areas button
        gbc.gridy = currentRow++;
        gbc.insets = new Insets(15, 10, 5, 10);
        add(deleteAreasButton, gbc);

        // Split Horizontal button
        gbc.gridy = currentRow++;
        gbc.insets = new Insets(5, 10, 2, 10);
        add(splitHorizontalButton, gbc);

        // Split Vertical button
        gbc.gridy = currentRow++;
        gbc.insets = new Insets(2, 10, 5, 10);
        add(splitVerticalButton, gbc);

        // Create Connection button
        gbc.gridy = currentRow++;
        gbc.insets = new Insets(5, 10, 5, 10);
        add(createConnectionButton, gbc);

        // Add Edge Connection button
        gbc.gridy = currentRow++;
        gbc.insets = new Insets(5, 10, 5, 10);
        add(addEdgeConnectionButton, gbc);

        // Connections section
        gbc.gridy = currentRow++;
        gbc.insets = new Insets(10, 10, 5, 10);
        add(new JLabel("Connections:"), gbc);

        gbc.gridy = currentRow++;
        gbc.insets = new Insets(5, 10, 5, 10);
        add(connectionsPanel, gbc);

        // Add empty panel at bottom to push everything to top
        gbc.gridy = currentRow;
        gbc.weighty = 1.0; // This will expand to fill remaining space
        gbc.fill = GridBagConstraints.BOTH;
        add(new JPanel(), gbc);
    }

    @Override
    public void onSelectionChanged(java.util.List<BoardArea> selectedAreas) {
        updateAreasSelection(selectedAreas);
    }

    public void updateAreasSelection(java.util.List<BoardArea> areas) {
        this.selectedAreas = new java.util.ArrayList<>(areas);
        updateAreaIDLabel();
        updateAreaTypeSelectionGroup();
        updateConnectionsPanel();
        updateDeleteAreasButton();
        updateSplitButtons();
        this.revalidate();
        this.repaint();
    }

    private void updateAreaIDLabel() {
        if (this.selectedAreas.isEmpty()) {
            areaIdLabel.setText("No Areas Selected");
            return;
        }
        String areaIds = selectedAreas.stream().map(x -> String.valueOf(x.getAreaId()))
                .collect(Collectors.joining(", "));
        areaIdLabel.setText("Area ID: " + areaIds);
    }

    public void updateAreaTypeSelectionGroup() {

        // Enable radio buttons
        streetCheckbox.setEnabled(this.selectedAreas.size() > 0);
        indoorLightCheckbox.setEnabled(this.selectedAreas.size() > 0);
        indoorDarkCheckbox.setEnabled(this.selectedAreas.size() > 0);
        outdoorCheckbox.setEnabled(this.selectedAreas.size() > 0);

        // Set the current selection based on area type
        if (this.selectedAreas.size() > 0) {
            AreaType currentType = this.selectedAreas.get(0).getAreaType();
            switch (currentType) {
                case STREET:
                    streetCheckbox.setSelected(true);
                    break;
                case INDOOR_LIGHT:
                    indoorLightCheckbox.setSelected(true);
                    break;
                case INDOOR_DARK:
                    indoorDarkCheckbox.setSelected(true);
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
            createConnectionButton.setText("Con:" + this.getAreaIdString(this.selectedAreas.get(0).getAreaId()) + "->"
                    + this.selectedAreas.stream().skip(1).map(x -> this.getAreaIdString(x.getAreaId()))
                            .collect(Collectors.joining(", ")));
        }

        // Enable/disable edge connection button
        if (this.selectedAreas.size() == 1 && (this.selectedAreas.get(0).getAreaType() == AreaType.INDOOR_LIGHT
                || this.selectedAreas.get(0).getAreaType() == AreaType.INDOOR_DARK)) {
            addEdgeConnectionButton.setEnabled(true);
        } else {
            addEdgeConnectionButton.setEnabled(false);
        }
        // Clear connections panel
        connectionsPanel.removeAll();
        connectionsPanel.revalidate();
        connectionsPanel.repaint();

        if (this.selectedAreas.size() == 0) {
            return;
        }
        // Find all connections for the 1st area in selectedAreas
        java.util.List<BoardAreaConnection> areaConnections = this
                .findConnectionsForArea(this.selectedAreas.get(0).getAreaId());

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
    private java.util.List<BoardAreaConnection> findConnectionsForArea(UUID areaId) {
        java.util.List<BoardAreaConnection> result = this.getBoard().getConnections().stream()
                .filter(connection -> connection.getAreaAId().equals(areaId)
                        || (connection.isEdgeConnection() && connection.getAreaAId().equals(areaId))
                        || (!connection.isEdgeConnection() && connection.getAreaBId().equals(areaId)))

                .collect(Collectors.toList());
        return result;
    }

    private void onAreaTypeChanged(AreaType newType) {
        if (selectedAreas != null && selectedAreas.size() > 0) {
            for (BoardArea area : selectedAreas) {
                AreaType oldType = area.getAreaType();
                area.setAreaType(newType);
                logger.info("Area ID {} type changed: {} -> {}", area.getAreaId(), oldType, newType);
            }
            notifyAreasChanged();
        }
    }

    private JPanel createConnectionRow(BoardAreaConnection connection) {
        JPanel row = new JPanel();
        row.setLayout(new BorderLayout(5, 0));

        String labelText;
        if (connection.getDirection() != null) {
            labelText = this.getAreaIdString(connection.getAreaAId()) + " -> " + connection.getDirection();
        } else {
            labelText = this.getAreaIdString(connection.getAreaAId()) + " <-> "
                    + this.getAreaIdString(connection.getAreaBId());
        }
        JLabel connectionLabel = new JLabel(labelText);
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
            if (connection.getDirection() != null) {
                this.getBoard().removeEdgeConnection(connection.getAreaAId(), connection.getDirection());
                logger.info("Deleted edge connection: Area {} -> {}", connection.getAreaAId(),
                        connection.getDirection());
            } else {
                this.getBoard().removeConnection(connection.getAreaAId(), connection.getAreaBId());
                logger.info("Deleted connection: Area {} <-> Area {}", connection.getAreaAId(),
                        connection.getAreaBId());
            }
            updateConnectionsPanel();
            notifyConnectionsChanged();
        }
    }

    private void onAddEdgeConnection() {
        if (selectedAreas == null || selectedAreas.size() != 1 || this.getBoard() == null) {
            return;
        }
        BoardArea area = selectedAreas.get(0);
        DoorDirection[] directions = DoorDirection.values();
        DoorDirection selectedDirection = (DoorDirection) JOptionPane.showInputDialog(this,
                "Select Direction", "Add Edge Connection",
                JOptionPane.QUESTION_MESSAGE, null, directions, directions[0]);

        if (selectedDirection != null) {
            try {
                BoardAreaConnection connection = new BoardAreaConnection(area.getAreaId(), selectedDirection);
                this.getBoard().addConnection(connection);
                logger.info("Added edge connection: Area {} -> {}", area.getAreaId(), selectedDirection);
                updateConnectionsPanel();
                notifyConnectionsChanged();
            } catch (IllegalArgumentException e) {
                JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private String getAreaIdString(UUID areaId) {
        return areaId.toString().substring(areaId.toString().length() - 3);
    }

    // creates connections from the first area to the rest
    private void onCreateConnection() {
        if (selectedAreas == null || selectedAreas.size() < 2 || this.getBoard() == null) {
            return;
        }

        UUID areaId1 = selectedAreas.get(0).getAreaId();

        for (int i = 1; i < selectedAreas.size(); i++) {
            UUID areaId2 = selectedAreas.get(i).getAreaId();
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

    private boolean connectionExists(UUID areaId1, UUID areaId2) {
        if (this.getBoard() == null) {
            return false;
        }

        for (BoardAreaConnection connection : this.getBoard().getConnections()) {
            if ((connection.getAreaAId().equals(areaId1) && connection.getAreaBId() != null
                    && connection.getAreaBId().equals(areaId2)) ||
                    (connection.getAreaAId().equals(areaId2) && connection.getAreaBId() != null
                            && connection.getAreaBId().equals(areaId1))) {
                return true;
            }
        }

        return false;
    }

    private void updateSplitButtons() {
        // Enable split buttons only if exactly 1 non-border area is selected
        boolean canSplit = this.selectedAreas.size() == 1 &&
                this.selectedAreas.get(0).getAreaLocation() == com.zombicide.missiongen.model.areas.AreaLocation.OTHER;

        splitHorizontalButton.setEnabled(canSplit);
        splitVerticalButton.setEnabled(canSplit);
    }

    private void onSplitHorizontal() {
        if (this.selectedAreas.size() != 1) {
            logger.warn("Cannot split: exactly 1 area must be selected");
            return;
        }

        BoardArea areaToSplit = this.selectedAreas.get(0);
        logger.info("Entering horizontal split mode for area {}", areaToSplit.getAreaId());

        if (zoneTileDraw != null) {
            zoneTileDraw.enterSplitMode(
                    com.zombicide.missiongen.ui.tiles.ZoneTileDraw.DrawMode.SPLIT_HORIZONTAL,
                    areaToSplit);
        }
    }

    private void onSplitVertical() {
        if (this.selectedAreas.size() != 1) {
            logger.warn("Cannot split: exactly 1 area must be selected");
            return;
        }

        BoardArea areaToSplit = this.selectedAreas.get(0);
        logger.info("Entering vertical split mode for area {}", areaToSplit.getAreaId());

        if (zoneTileDraw != null) {
            zoneTileDraw.enterSplitMode(
                    com.zombicide.missiongen.ui.tiles.ZoneTileDraw.DrawMode.SPLIT_VERTICAL,
                    areaToSplit);
        }
    }
}
