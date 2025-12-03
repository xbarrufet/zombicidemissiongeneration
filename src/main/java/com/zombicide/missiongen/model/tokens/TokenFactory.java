package com.zombicide.missiongen.model.tokens;

import java.awt.Image;

import com.zombicide.missiongen.config.TokenLoader;
import com.zombicide.missiongen.model.tokens.tokenType.Door;
import com.zombicide.missiongen.model.tokens.tokenType.Exit;
import com.zombicide.missiongen.model.tokens.tokenType.NumberToken;
import com.zombicide.missiongen.model.tokens.tokenType.Objective;
import com.zombicide.missiongen.model.tokens.tokenType.PimpWeaponCrate;
import com.zombicide.missiongen.model.tokens.tokenType.Spawn;
import com.zombicide.missiongen.model.tokens.tokenType.Start;
import com.zombicide.missiongen.model.tokens.tokenType.SwitchToken;
import com.zombicide.missiongen.model.tokens.tokenType.Vehicle;
import com.zombicide.missiongen.model.tokens.tokenType.Zombie;

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
            case START -> {
                return new Start(subtype, tokenImage, tokenDimension[0], tokenDimension[1]);
            }
            case NUMBER -> {
                return new NumberToken(subtype, tokenImage, tokenDimension[0]);
            }
            case OBJECTIVE -> {
                return new Objective(subtype, tokenImage, tokenDimension[0], tokenDimension[1]);
            }
            case SPAWN -> {
                return new Spawn(subtype, tokenImage, tokenDimension[0], tokenDimension[1]);
            }
            case SWITCH -> {
                return new SwitchToken(subtype, tokenImage, tokenDimension[0], tokenDimension[1]);
            }
            case VEHICLES -> {
                return new Vehicle(subtype, tokenImage, tokenDimension[0], tokenDimension[1]);
            }
            case PIMPWEAPONCRATE -> {
                return new PimpWeaponCrate(subtype, tokenImage, tokenDimension[0], tokenDimension[1]);
            }
            case ZOMBIE -> {
                return new Zombie(subtype, tokenImage, tokenDimension[0]);
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
