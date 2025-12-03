package com.zombicide.missiongen.model.board;

public class MissionTileEntry {
    
    private String tileName;
    private String imagePath;
    private int rotation;
    private int row;
    private int col;

    // Default constructor for Jackson deserialization
    public MissionTileEntry() {
    }

    public MissionTileEntry(String tileName, String imagePath, int rotation, int row, int col) {
        this.tileName = tileName;
        this.imagePath = imagePath;
        this.rotation = rotation;
        this.row=row;
        this.col=col;
    }

    public String getTileName() {
        return tileName;
    }

    public void setTileName(String tileName) {
        this.tileName = tileName;
    }

    public int getRotation() {
        return rotation;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public void setCol(int col) {
        this.col = col;
    }

    public int getRow() {
        return row;
    }
    public int getCol() {
        return col;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }
    
    public MissionTileEntry setRotation(int rotation) {
        this.rotation = rotation;
        return new MissionTileEntry(tileName, imagePath, rotation, row, col);
    }

    public boolean isNull() {
        return tileName == null || tileName.isEmpty();
    }
}
