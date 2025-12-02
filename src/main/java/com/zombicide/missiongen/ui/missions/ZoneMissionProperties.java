package com.zombicide.missiongen.ui.missions;

import com.zombicide.missiongen.ui.components.ZonePropertiesPanel;
import com.zombicide.missiongen.ui.interfaces.MissionPropertiesListener;

public class ZoneMissionProperties extends ZonePropertiesPanel {

    private javax.swing.JLabel missionNameLabel;
    private javax.swing.JLabel dimensionsLabel;
    private javax.swing.JLabel areasCountLabel;

    private javax.swing.JRadioButton showAreasRadio;
    private javax.swing.JRadioButton hideAreasRadio;

    private MissionPropertiesListener listener;

    public ZoneMissionProperties(MissionPropertiesListener listener) {
        super();
        this.listener = listener;
        initComponents();
        setupLayout();
    }

    private void initComponents() {
        missionNameLabel = new javax.swing.JLabel("No mission loaded");
        dimensionsLabel = new javax.swing.JLabel("");
        areasCountLabel = new javax.swing.JLabel("");

        showAreasRadio = new javax.swing.JRadioButton("Show Areas", true);
        hideAreasRadio = new javax.swing.JRadioButton("Hide Areas", false);

        javax.swing.ButtonGroup group = new javax.swing.ButtonGroup();
        group.add(showAreasRadio);
        group.add(hideAreasRadio);

        showAreasRadio.addActionListener(e -> fireOnBoardAreasVisibilityUpdated(true));
        hideAreasRadio.addActionListener(e -> fireOnBoardAreasVisibilityUpdated(false));
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

        // Area Visibility Toggle
        gbc.gridy++;
        gbc.insets = new java.awt.Insets(15, 5, 5, 5);
        add(new javax.swing.JLabel("Area Visibility:"), gbc);

        javax.swing.JPanel radioPanel = new javax.swing.JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 0, 0));
        radioPanel.add(showAreasRadio);
        radioPanel.add(hideAreasRadio);

        gbc.gridy++;
        gbc.insets = new java.awt.Insets(5, 5, 5, 5);
        add(radioPanel, gbc);

        // Push everything to the top
        gbc.gridy++;
        gbc.weighty = 1.0;
        add(new javax.swing.JLabel(), gbc);
    }

    private void fireOnBoardAreasVisibilityUpdated(boolean visible) {
        if (listener != null) {
            listener.onBoardAreasVisibilityUpdated(visible);
        }
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
