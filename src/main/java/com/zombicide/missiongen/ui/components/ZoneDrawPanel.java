package com.zombicide.missiongen.ui.components;

import java.awt.Dimension;

import com.zombicide.missiongen.ui.theme.UIConstants;

public class ZoneDrawPanel extends BoardPanel {

    public ZoneDrawPanel() {
        Dimension fixedSize = new Dimension(UIConstants.DRAW_PANEL_SIZE, UIConstants.DRAW_PANEL_SIZE);
        setPreferredSize(fixedSize);
        setMinimumSize(fixedSize);
        setMaximumSize(fixedSize);
        setBackground(UIConstants.PANEL_BACKGROUND);
    }
}
