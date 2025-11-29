package com.zombicide.missiongen.model;

import java.awt.Image;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

public class Board extends BoardImageElement {

    private List<BoardArea> areas;
    private List<BoardAreaConnection> connections;

    public Board(Image backgroundImage, int width, int height) {
        super(0, new Point(0, 0), "board", backgroundImage, width, height);
        this.areas = new ArrayList<>();
        this.connections = new ArrayList<>();
    }

    public void addArea(BoardArea area) {
        areas.add(area);
    }

    public void addConnection(BoardAreaConnection connection) {
        connections.add(connection);
    }

    public List<BoardArea> getAreas() {
        return areas;
    }

    public List<BoardAreaConnection> getConnections() {
        return connections;
    }

    public void removeArea(int id) {
        areas.removeIf(area -> area.getId() == id);
    }

    public int getNewAreaId() {
        int maxId = 0;
        for (BoardArea area : areas) {
            if (area.getId() > maxId) {
                maxId = area.getId();
            }
        }
        return maxId + 1;
    }

    public void removeConnection(int idA, int idB) {
        connections
                .removeIf(connection -> connection.getAreaAId() == idA && connection.getAreaBId() == idB);
    }

    public boolean isOverlap(BoardArea newArea) {
        for (BoardArea area : areas) {
            if (area.isAreaOverlap(newArea)) {
                return true;
            }
        }
        return false;
    }

}
