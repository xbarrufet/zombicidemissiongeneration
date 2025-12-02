package com.zombicide.missiongen.ui.components;

import java.awt.Dimension;

public class ZoneDrawPanel extends BoardPanel {

    public ZoneDrawPanel() {
        Dimension fixedSize = new Dimension(750, 750);
        setPreferredSize(fixedSize);
        setMinimumSize(fixedSize);
        setMaximumSize(fixedSize);
    }
}
