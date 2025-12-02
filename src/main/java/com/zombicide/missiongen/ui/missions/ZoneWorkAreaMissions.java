package com.zombicide.missiongen.ui.missions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zombicide.missiongen.model.Mission;
import com.zombicide.missiongen.ui.components.ZoneDrawPanel;
import com.zombicide.missiongen.ui.components.ZonePropertiesPanel;
import com.zombicide.missiongen.ui.components.ZoneWorkAreaPanel;
import com.zombicide.missiongen.ui.interfaces.MissionSelectionListener;

/**
 * Container panel that holds ZoneMissionDraw and ZoneMissionProperties.
 * Displays missions in read-only mode.
 */
public class ZoneWorkAreaMissions extends ZoneWorkAreaPanel implements MissionSelectionListener {
    private static final Logger logger = LoggerFactory.getLogger(ZoneWorkAreaMissions.class);

    private ZoneMissionDraw zoneDraw;
    private ZoneMissionProperties zoneProperties;

    public ZoneWorkAreaMissions() {
        super();
        zoneDraw = new ZoneMissionDraw();
        zoneProperties = new ZoneMissionProperties(zoneDraw);
        init(); // Initialize layout and components
    }

    @Override
    public void init() {
        super.init();
    }

    public ZoneMissionDraw getZoneDraw() {
        return zoneDraw;
    }

    public ZoneMissionProperties getZoneProperties() {
        return zoneProperties;
    }

    @Override
    protected ZonePropertiesPanel getZonePropertiesPanel() {
        return zoneProperties;
    }

    @Override
    protected ZoneDrawPanel getZoneDrawPanel() {
        return zoneDraw;
    }

    @Override
    public void onMissionSelected(Mission mission) {
        logger.info("Mission selected: {}", mission.getMissionName());

        // Update properties immediately
        zoneProperties.setMissionInfo(
                mission.getMissionName(),
                mission.getRows(),
                mission.getCols(),
                mission.getMissionBoard().getAreas().size());

        // Defer board loading until after layout is complete
        javax.swing.SwingUtilities.invokeLater(() -> {
            zoneDraw.setMissionBoard(mission.getMissionBoard());
        });
    }
}
