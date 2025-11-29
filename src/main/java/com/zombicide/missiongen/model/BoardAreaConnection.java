package com.zombicide.missiongen.model;

import com.zombicide.missiongen.DTO.BoardAreaConnectionDTO;

public class BoardAreaConnection {

    int areaA;
    int areaB;

    public BoardAreaConnection(int areaA, int areaB) {
        this.areaA = areaA;
        this.areaB = areaB;
    }

    public int getAreaAId() {
        return areaA;
    }

    public int getAreaBId() {
        return areaB;
    }

    public static BoardAreaConnection fromAreaConnectionDTO(BoardAreaConnectionDTO areaConnectionDTO) {
        return new BoardAreaConnection(areaConnectionDTO.areaA, areaConnectionDTO.areaB);
    }
}
