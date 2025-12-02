package com.zombicide.missiongen.model.tokens;

import java.awt.Image;

import com.zombicide.missiongen.config.TokenLoader;
import com.zombicide.missiongen.model.tokens.tokenType.Door;
import com.zombicide.missiongen.model.tokens.tokenType.Exit;

public class TokenFactory {

    public static Token createToken(TokenType type, String subtype) {
        Image tokenImage = getTokenImage(type, subtype);
        int[] tokenDimension = getTokenDimension(type);
        switch (type) {

            case DOOR -> {
                return new Door(subtype, tokenImage, tokenDimension[0], tokenDimension[1]);
            }
            case EXIT -> {
                return new Exit(subtype, tokenImage, tokenDimension[0], tokenDimension[1]);
            }
            default -> {
                return null;
            }

        }
    }

    private static Image getTokenImage(TokenType type, String subtype) {
        return TokenLoader.getInstance().getTokenImage(type, subtype);
    }

    private static int[] getTokenDimension(TokenType type) {
        return TokenLoader.getInstance().getTokenDimension(type);
    }
}
