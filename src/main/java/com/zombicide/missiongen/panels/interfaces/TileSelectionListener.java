package com.zombicide.missiongen.panels.interfaces;

import com.zombicide.missiongen.model.Tile;

public interface TileSelectionListener {

    /**
     * Called when a tile is selected
     * 
     * @param tile The selected tile
     */
    void onTileSelected(Tile tile);
}
