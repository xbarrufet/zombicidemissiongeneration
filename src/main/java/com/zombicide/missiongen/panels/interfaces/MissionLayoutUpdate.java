package com.zombicide.missiongen.panels.interfaces;

import com.zombicide.missiongen.model.board.MissionGrid;

public interface MissionLayoutUpdate {
    void onMissionNameUpdated(String missionName);

    void onMissionGridUpdated(MissionGrid grid);
}
