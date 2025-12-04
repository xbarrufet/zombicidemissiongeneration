package com.zombicide.missiongen.DTO;

import java.awt.Point;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.zombicide.missiongen.model.tokens.Token;
import com.zombicide.missiongen.model.tokens.TokenFactory;
import com.zombicide.missiongen.model.tokens.TokenType;

public class TokenDTO {
    public UUID id;
    public Point location;
    public TokenType type;
    public String subtype;
    public int rotation;
    public UUID areaId;
    public Map<String, String> properties; // Type-specific properties

    public TokenDTO() {
        this.properties = new HashMap<>();
    }

    public static TokenDTO fromToken(Token token) {
        TokenDTO dto = new TokenDTO();
        dto.id = token.getId();
        dto.location = token.getLocation();
        dto.type = token.getType();
        dto.subtype = token.getSubtype();
        dto.rotation = token.getRotation();
        dto.areaId = token.getAreaId();
        // Copy all properties from token
        if (token.getProperties() != null) {
            dto.properties.putAll(token.getProperties());
        }
        return dto;
    }

    public Token toToken() {
        // Use TokenFactory to create the appropriate Token subclass
        Token token = TokenFactory.createToken(this.type, this.subtype);
        if (token != null) {
            token.setLocation(this.location, this.areaId);
            // Set rotation
            int rotations = this.rotation / 90;
            for (int i = 0; i < rotations; i++) {
                token.rotate();
            }
            // Restore properties
            if (this.properties != null && !this.properties.isEmpty()) {
                token.setProperties(this.properties);
            }
        }
        return token;
    }
}
