package com.zombicide.missiongen.model.tokens.tokenType;

import java.awt.Image;
import java.util.UUID;

import com.zombicide.missiongen.model.tokens.Token;
import com.zombicide.missiongen.model.tokens.TokenOrientation;
import com.zombicide.missiongen.model.tokens.TokenShape;
import com.zombicide.missiongen.model.tokens.TokenType;

public class Door extends Token {

    private static final String AREA_A_KEY = "areaA";
    private static final String AREA_B_KEY = "areaB";

    public Door(String subtype, Image image, int width, int height) {
        super(TokenType.DOOR, TokenShape.createSquare(width, height), TokenOrientation.Horizontal, subtype, image);
    }

    public void setAreaA(UUID areaId) {
        this.setProperty(AREA_A_KEY, areaId != null ? areaId.toString() : null);
    }

    public UUID getAreaA() {
        String areaAStr = this.getProperty(AREA_A_KEY);
        return areaAStr != null ? UUID.fromString(areaAStr) : null;
    }

    public void setAreaB(UUID areaId) {
        this.setProperty(AREA_B_KEY, areaId != null ? areaId.toString() : null);
    }

    public UUID getAreaB() {
        String areaBStr = this.getProperty(AREA_B_KEY);
        return areaBStr != null ? UUID.fromString(areaBStr) : null;
    }

}
