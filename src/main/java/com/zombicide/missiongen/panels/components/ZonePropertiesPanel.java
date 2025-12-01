package com.zombicide.missiongen.panels.components;

import java.awt.Color;
import java.awt.Dimension;

public class ZonePropertiesPanel extends BoardPanel {

    public ZonePropertiesPanel() {
        super();
        this.setBackground(new Color(255, 250, 205));
        Dimension fixedSize = new Dimension(250, 750);
        this.setPreferredSize(fixedSize);
        this.setMinimumSize(fixedSize);
        this.setMaximumSize(fixedSize);
    }

    public static ZonePropertiesPanel EMPTY_PROPERTIES_PANEL = new ZonePropertiesPanel();
}
