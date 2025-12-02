package com.zombicide.missiongen.ui.interfaces;

import com.zombicide.missiongen.model.board.TileBoard;

public interface TileGridListener {
    void onTilePlaced(int row, int col, TileBoard tileBoard);

    void onTileRemoved(int row, int col, TileBoard tileBoard);
}
