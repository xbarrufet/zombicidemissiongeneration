package com.zombicide.missiongen.panels.interfaces;

import java.util.List;

import com.zombicide.missiongen.model.areas.BoardArea;

/**
 * Listener interface for board area selection changes.
 */
public interface BoardSelectionListener {
    /**
     * Called when the selection of board areas changes.
     * 
     * @param selectedAreas The current list of selected areas (copy, safe to store)
     */
    void onSelectionChanged(List<BoardArea> selectedAreas);
}
