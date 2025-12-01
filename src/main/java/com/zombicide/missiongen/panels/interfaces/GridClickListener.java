package com.zombicide.missiongen.panels.interfaces;

public interface GridClickListener {
    void onGridClick(int row, int col);

    void onGridRightClick(int row, int col);

    void onGridDoubleClick(int row, int col);
}
