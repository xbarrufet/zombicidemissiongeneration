package com.zombicide.missiongen.DTO;

import java.awt.Point;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
import com.zombicide.missiongen.model.tokens.Token;
import com.zombicide.missiongen.model.tokens.TokenType;
import com.zombicide.missiongen.model.tokens.tokenType.Door;
import com.zombicide.missiongen.model.tokens.tokenType.Exit;

@JsonTypeInfo(use = Id.NAME, include = As.PROPERTY, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = DoorDTO.class, name = "DOOR"),
        @JsonSubTypes.Type(value = ExitDTO.class, name = "EXIT")
})
public abstract class TokenDTO {
    public UUID id;
    public Point location;
    public TokenType type;
    public String subtype;
    public int rotation;
    public UUID areaId;

    public TokenDTO() {
    }

    public static TokenDTO fromToken(Token token) {
        TokenDTO dto = null;
        if (token instanceof Door) {
            dto = DoorDTO.fromDoor((Door) token);
        } else if (token instanceof Exit) {
            dto = ExitDTO.fromExit((Exit) token);
        }

        if (dto != null) {
            dto.id = token.getId();
            dto.location = token.getLocation();
            dto.type = token.getType();
            dto.subtype = token.getSubtype();
            dto.rotation = token.getRotation();
            dto.areaId = token.getAreaId();
        }
        return dto;
    }

    public abstract Token toToken();
}
