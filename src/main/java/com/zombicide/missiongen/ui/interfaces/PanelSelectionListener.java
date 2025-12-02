package com.zombicide.missiongen.ui.interfaces;

import com.zombicide.missiongen.model.Tile;

public interface PanelSelectionListener {

    /**
     * Called when a tile is selected
     * 
     * @param tile The selected tile
     */
    void onTileSelected(Tile tile);

    void onEditionCollectionSelected(String edition, String collection);
}
