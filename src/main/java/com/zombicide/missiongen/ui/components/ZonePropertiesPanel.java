package com.zombicide.missiongen.ui.components;

import java.awt.Dimension;

import com.zombicide.missiongen.ui.theme.UIConstants;

public class ZonePropertiesPanel extends BoardPanel {

    public ZonePropertiesPanel() {
        super();
        this.setBackground(UIConstants.BACKGROUND);
        Dimension fixedSize = new Dimension(UIConstants.PROPERTIES_PANEL_WIDTH, UIConstants.DRAW_PANEL_SIZE);
        this.setPreferredSize(fixedSize);
        this.setMinimumSize(fixedSize);
        this.setMaximumSize(fixedSize);
    }

    public static ZonePropertiesPanel EMPTY_PROPERTIES_PANEL = new ZonePropertiesPanel();
}
