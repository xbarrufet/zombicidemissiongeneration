package com.zombicide.missiongen.panels.components;

import javax.swing.JPanel;

import com.zombicide.missiongen.model.Board;

/**
 * Base panel class that holds a Board reference
 */
public class BoardPanel extends JPanel {

    private Board board;

    public BoardPanel() {
        super();
        this.board = null;
    }

    public void setBoard(Board board) {
        this.board = board;
    }

    public Board getBoard() {
        return this.board;
    }
}
