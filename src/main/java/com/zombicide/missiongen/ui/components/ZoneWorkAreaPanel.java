package com.zombicide.missiongen.ui.components;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JPanel;

public abstract class ZoneWorkAreaPanel extends JPanel {

    private ZoneDrawPanel zoneDrawPanel;
    private ZonePropertiesPanel zonePropertiesPanel;

    public ZoneWorkAreaPanel() {
        super();
    }

    public void init() {
        initComponents();
        setupLayout();
    }

    private void initComponents() {
        zoneDrawPanel = this.getZoneDrawPanel();
        zonePropertiesPanel = this.getZonePropertiesPanel();
    }

    protected abstract ZonePropertiesPanel getZonePropertiesPanel();

    protected abstract ZoneDrawPanel getZoneDrawPanel();

    protected void setupLayout() {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        // ZoneDraw - Left side (750x750)
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.0;
        gbc.weighty = 0.0;
        gbc.fill = GridBagConstraints.NONE;
        add(zoneDrawPanel, gbc);

        // ZoneProperties - Right side (250x750)
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 0.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        add(zonePropertiesPanel, gbc);
    }

}
