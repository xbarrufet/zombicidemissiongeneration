package com.zombicide.missiongen.model.tokens.tokenType;

import java.awt.Image;

import com.zombicide.missiongen.model.tokens.Token;
import com.zombicide.missiongen.model.tokens.TokenOrientation;
import com.zombicide.missiongen.model.tokens.TokenShape;
import com.zombicide.missiongen.model.tokens.TokenType;

public class Zombie extends Token {

    public Zombie(String subtype, Image image, int diameter) {
        super(TokenType.ZOMBIE, TokenShape.createCircle(diameter), TokenOrientation.Horizontal, subtype, image);
    }

}
