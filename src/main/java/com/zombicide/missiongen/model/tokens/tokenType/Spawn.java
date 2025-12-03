package com.zombicide.missiongen.model.tokens.tokenType;

import java.awt.Image;

import com.zombicide.missiongen.model.tokens.Token;
import com.zombicide.missiongen.model.tokens.TokenOrientation;
import com.zombicide.missiongen.model.tokens.TokenShape;
import com.zombicide.missiongen.model.tokens.TokenType;

public class Spawn extends Token {

    public Spawn(String subtype, Image image, int width, int height) {
        super(TokenType.SPAWN, TokenShape.createSquare(width, height), TokenOrientation.Horizontal, subtype, image);
    }

}
