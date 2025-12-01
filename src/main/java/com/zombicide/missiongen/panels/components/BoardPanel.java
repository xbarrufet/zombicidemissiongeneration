package com.zombicide.missiongen.panels.components;

import javax.swing.JPanel;

import com.zombicide.missiongen.model.board.BaseBoard;

/**
 * Base panel class that holds a Board reference
 */
public class BoardPanel extends JPanel {

    private BaseBoard board;

    public BoardPanel() {
        super();
        this.board = null;
    }

    public void setBoard(BaseBoard board) {
        this.board = board;
    }

    public BaseBoard getBoard() {
        return this.board;
    }
}
