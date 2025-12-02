package com.zombicide.missiongen.model.tokens;

import java.util.List;

import com.zombicide.missiongen.config.TokenLoader;

public enum TokenType {
    // COUNTER,
    // START,
    // NUMBER,
    // OBJECTIVE,
    // OBSTACLE,
    // OTHER,
    // SPAWN,
    // SWITCH,
    // TENT,
    // VEHICLES,
    // PIMPWEAPONCRATE,
    DOOR,
    EXIT;

    public static TokenType fromString(String name) {
        return valueOf(name.toUpperCase());
    }

    public static String toString(TokenType type) {
        return type.name().toUpperCase();
    }

    public static List<String> getSubtypes(TokenType type) {
        return TokenLoader.getInstance().getTokenSubtypes(type);
    }
}