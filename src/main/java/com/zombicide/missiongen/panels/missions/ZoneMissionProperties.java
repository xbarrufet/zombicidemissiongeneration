package com.zombicide.missiongen.panels.missions;

import com.zombicide.missiongen.panels.components.ZonePropertiesPanel;

public class ZoneMissionProperties extends ZonePropertiesPanel {

    private javax.swing.JLabel missionNameLabel;
    private javax.swing.JLabel dimensionsLabel;
    private javax.swing.JLabel areasCountLabel;

    public ZoneMissionProperties() {
        super();
        initComponents();
        setupLayout();
    }

    private void initComponents() {
        missionNameLabel = new javax.swing.JLabel("No mission loaded");
        dimensionsLabel = new javax.swing.JLabel("");
        areasCountLabel = new javax.swing.JLabel("");
    }

    private void setupLayout() {
        setLayout(new java.awt.GridBagLayout());
        java.awt.GridBagConstraints gbc = new java.awt.GridBagConstraints();

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = java.awt.GridBagConstraints.WEST;
        gbc.insets = new java.awt.Insets(5, 5, 5, 5);
        gbc.fill = java.awt.GridBagConstraints.HORIZONTAL;

        // Mission Name
        add(new javax.swing.JLabel("Mission:"), gbc);
        gbc.gridy++;
        add(missionNameLabel, gbc);

        // Dimensions
        gbc.gridy++;
        gbc.insets = new java.awt.Insets(15, 5, 5, 5);
        add(new javax.swing.JLabel("Dimensions:"), gbc);
        gbc.gridy++;
        gbc.insets = new java.awt.Insets(5, 5, 5, 5);
        add(dimensionsLabel, gbc);

        // Areas Count
        gbc.gridy++;
        gbc.insets = new java.awt.Insets(15, 5, 5, 5);
        add(new javax.swing.JLabel("Areas:"), gbc);
        gbc.gridy++;
        gbc.insets = new java.awt.Insets(5, 5, 5, 5);
        add(areasCountLabel, gbc);

        // Push everything to the top
        gbc.gridy++;
        gbc.weighty = 1.0;
        add(new javax.swing.JLabel(), gbc);
    }

    public void setMissionInfo(String missionName, int rows, int cols, int areasCount) {
        missionNameLabel.setText(missionName);
        dimensionsLabel.setText(rows + " x " + cols);
        areasCountLabel.setText(String.valueOf(areasCount));
    }

    public void clearMissionInfo() {
        missionNameLabel.setText("No mission loaded");
        dimensionsLabel.setText("");
        areasCountLabel.setText("");
    }
}
