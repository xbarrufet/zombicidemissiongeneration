package com.zombicide.missiongen.ui.interfaces;

import com.zombicide.missiongen.model.board.MissionGrid;

public interface MissionLayoutUpdate {
    void onMissionNameUpdated(String missionName);

    void onMissionGridUpdated(MissionGrid grid);
}
