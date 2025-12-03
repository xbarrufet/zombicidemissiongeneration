package com.zombicide.missiongen.model.tokens.tokenType;

import java.awt.Image;

import com.zombicide.missiongen.model.tokens.Token;
import com.zombicide.missiongen.model.tokens.TokenOrientation;
import com.zombicide.missiongen.model.tokens.TokenShape;
import com.zombicide.missiongen.model.tokens.TokenType;

public class Objective extends Token {

    public Objective(String subtype, Image image, int width, int height) {
        super(TokenType.OBJECTIVE, TokenShape.createSquare(width, height), TokenOrientation.Horizontal, subtype, image);
    }

}
