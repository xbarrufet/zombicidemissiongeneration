package com.zombicide.missiongen.ui.missions;

import java.awt.Graphics;

import com.zombicide.missiongen.model.board.MissionBoard;
import com.zombicide.missiongen.ui.components.BoardBackgroundPanel;
import com.zombicide.missiongen.ui.interfaces.MissionPropertiesListener;

public class ZoneMissionDraw extends BoardBackgroundPanel implements MissionPropertiesListener {

    public ZoneMissionDraw() {
        super();
    }

    public void setMissionBoard(MissionBoard board) {
        super.setBoard(board);

    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        // Mission board is read-only, so no additional drawing needed
    }

    @Override
    public void onBoardAreasVisibilityUpdated(boolean visible) {
        this.setAreaDrawingVisible(visible);
    }
}
