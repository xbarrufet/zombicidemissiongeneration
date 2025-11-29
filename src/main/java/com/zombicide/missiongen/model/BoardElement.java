package com.zombicide.missiongen.model;

import java.awt.Point;

public class BoardElement {

    int id;
    Point origin;
    String type;
    int width;
    int height;

    public BoardElement(int id, Point origin, String type, int width, int height) {
        this.id = id;
        this.origin = origin;
        this.type = type;
        this.width = width;
        this.height = height;
    }

    public int getId() {
        return id;
    }

    public Point getOrigin() {
        return origin;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public String getType() {
        return type;
    }

    public boolean isPointInside(Point point) {
        return point.x >= origin.x && point.x <= origin.x + width && point.y >= origin.y
                && point.y <= origin.y + height;
    }

}
