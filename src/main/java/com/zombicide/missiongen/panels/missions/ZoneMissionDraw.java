package com.zombicide.missiongen.panels.missions;

import java.awt.Graphics;

import com.zombicide.missiongen.model.board.MissionBoard;
import com.zombicide.missiongen.panels.components.BoardBackgroundPanel;

public class ZoneMissionDraw extends BoardBackgroundPanel {

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
}
