package com.zombicide.missiongen.model.tokens;

import java.awt.Point;

public class TokenShape {
    // SQUARE, RECTANGLE, TRIANGLE, CIRCLE;

    public enum Shape {
        SQUARE,
        TRIANGLE,
        CIRCLE
    }

    private int width;
    private int height;
    private int Radius;
    private Shape shape;
    private int rotation;

    private TokenShape(int width, int height, int radius, Shape shape) {
        this.width = width;
        this.height = height;
        this.shape = shape;
        this.Radius = radius;
        this.rotation = 0;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getRadius() {
        return Radius;
    }

    public Shape getShape() {
        return shape;
    }

    public Point getTopLeftFromCenter(Point offset) {

        if (shape == Shape.CIRCLE) {
            return new Point(offset.x - Radius, offset.y - Radius);
        } else {
            return new Point(offset.x - width / 2, offset.y - height / 2);
        }
    }

    public static TokenShape createSquare(int width, int height) {
        return new TokenShape(width, height, 0, Shape.SQUARE);
    }

    public static TokenShape createCircle(int radius) {
        return new TokenShape(0, 0, radius, Shape.CIRCLE);
    }

    public static TokenShape createTriangle(int width, int height) {
        return new TokenShape(width, height, 0, Shape.TRIANGLE);
    }

    public void rotate() {
        if (this.shape == Shape.CIRCLE) {
            // No change in shape for circle
            return;
        } else {
            // For square/rectangle and triangle, swap width and height
            int temp = this.width;
            this.width = this.height;
            this.height = temp;
            this.rotation += 90 % 360;
        }
    }

    public int getRotation() {
        return rotation;
    }

    public boolean isPointInShape(Point tokenLocation, Point point) {
        if (shape == Shape.CIRCLE) {
            return point.distance(tokenLocation) <= Radius;
        } else {
            return point.x >= tokenLocation.x && point.x <= tokenLocation.x + width && point.y >= tokenLocation.y
                    && point.y <= tokenLocation.y + height;
        }
    }
}
