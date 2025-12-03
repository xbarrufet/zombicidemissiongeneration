package com.zombicide.missiongen.model.tokens.tokenType;

import java.awt.Image;

import com.zombicide.missiongen.model.areas.BoardAreaConnection;
import com.zombicide.missiongen.model.tokens.Token;
import com.zombicide.missiongen.model.tokens.TokenOrientation;
import com.zombicide.missiongen.model.tokens.TokenShape;
import com.zombicide.missiongen.model.tokens.TokenType;

public class Door extends Token {

    private BoardAreaConnection boardAreaConnection;

    public Door(String subtype, Image image, int width, int height) {
        super(TokenType.DOOR, TokenShape.createSquare(width, height), TokenOrientation.Horizontal, subtype, image);
    }

    public BoardAreaConnection getBoardAreaConnection() {
        return boardAreaConnection;
    }

    public void setBoardAreaConnection(BoardAreaConnection boardAreaConnection) {
        this.boardAreaConnection = boardAreaConnection;
    }

}
