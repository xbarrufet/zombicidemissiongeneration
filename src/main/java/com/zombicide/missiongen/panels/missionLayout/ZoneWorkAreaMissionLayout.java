package com.zombicide.missiongen.panels.missionLayout;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zombicide.missiongen.panels.components.ZoneDrawPanel;
import com.zombicide.missiongen.panels.components.ZonePropertiesPanel;
import com.zombicide.missiongen.panels.components.ZoneWorkAreaPanel;
import com.zombicide.missiongen.panels.interfaces.MissionLayoutUpdate;

/**
 * Container panel that holds ZoneDraw and ZoneProperties.
 * Can be replaced as a whole unit.
 */
public class ZoneWorkAreaMissionLayout extends ZoneWorkAreaPanel {
    private static final Logger logger = LoggerFactory.getLogger(ZoneWorkAreaMissionLayout.class);

    private ZoneMissionGrid zoneDraw;
    private ZoneMissionGridProperties zoneProperties;
    private MissionLayoutUpdate listener;

    public ZoneWorkAreaMissionLayout(MissionLayoutUpdate listener) {
        super();
        this.listener = listener;
        init();
    }

    @Override
    public void init() {
        initComponents();
        super.init();
    }

    private void initComponents() {
        zoneDraw = new ZoneMissionGrid(this.listener);
        zoneProperties = new ZoneMissionGridProperties(this.listener, zoneDraw);

    }

    @Override
    protected ZoneDrawPanel getZoneDrawPanel() {
        return this.zoneDraw;
    }

    @Override
    protected ZonePropertiesPanel getZonePropertiesPanel() {
        return this.zoneProperties;
    }

    public ZoneMissionGridProperties getZoneMissionGridProperties() {
        return this.zoneProperties;
    }

    // @Override
    // public void onTileSelected(Tile tile) {
    // if (tile != null) {
    // this.zoneDraw.setTile(tile.getBoard());
    // } else {
    // this.zoneDraw.setTile(null);
    // }
    // }

}
