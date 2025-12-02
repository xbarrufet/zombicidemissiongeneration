package com.zombicide.missiongen.model.tokens.tokenType;

import java.awt.Image;

import com.zombicide.missiongen.model.tokens.Token;
import com.zombicide.missiongen.model.tokens.TokenOrientation;
import com.zombicide.missiongen.model.tokens.TokenShape;
import com.zombicide.missiongen.model.tokens.TokenType;

public class Door extends Token {

    public Door(String subtype, Image image, int width, int height) {
        super(TokenType.DOOR, TokenShape.createSquare(width, height), TokenOrientation.Horizontal,  subtype, image);
    }

}
