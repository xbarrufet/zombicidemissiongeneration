package com.zombicide.missiongen.model.elements;

import java.awt.Image;
import java.awt.Point;

public class BoardAsset {

    public enum BoardAssetType {
        Exit,
        PimpCar,
        PoliceCar,
        Noise,
        GoalBlue,
        GoalRed,
        GoalGreen,
        PimpWeaponBox,
        SpawnBlue,
        SpawnRed,
        SpawnGreen,
        StartingSpawn,
        StartingSurvivorZone,
        GoalMarker,
        DoorClosedBlue,
        DoorClosedRed,
        DoorClosedGreen,
        DoorOpenBlue,
        DoorOpenRed,
        DoorOpenGreen,
        ;
    }

    Point location;
    String type;
    Image image;

    public BoardAsset(String type, Image image) {
        this.type = type;
        this.image = image;
    }

    public void setLocation(Point location) {
        this.location = location;
    }

    public Point getLocation() {
        return location;
    }

    public String getType() {
        return type;
    }

    public Image getImage() {
        return image;
    }

}
