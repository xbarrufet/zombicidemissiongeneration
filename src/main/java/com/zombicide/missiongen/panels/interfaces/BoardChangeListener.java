package com.zombicide.missiongen.panels.interfaces;

public interface BoardChangeListener {

    /**
     * Called when areas are added or removed from the board
     */
    void onAreasChanged();

    /**
     * Called when connections are added or removed from the board
     */
    void onConnectionsChanged();
}
