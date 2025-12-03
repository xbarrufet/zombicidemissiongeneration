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
import com.zombicide.missiongen.model.tokens.tokenType.NumberToken;
import com.zombicide.missiongen.model.tokens.tokenType.Objective;
import com.zombicide.missiongen.model.tokens.tokenType.PimpWeaponCrate;
import com.zombicide.missiongen.model.tokens.tokenType.Spawn;
import com.zombicide.missiongen.model.tokens.tokenType.Start;
import com.zombicide.missiongen.model.tokens.tokenType.SwitchToken;
import com.zombicide.missiongen.model.tokens.tokenType.Vehicle;
import com.zombicide.missiongen.model.tokens.tokenType.Zombie;

@JsonTypeInfo(use = Id.NAME, include = As.PROPERTY, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = DoorDTO.class, name = "DOOR"),
        @JsonSubTypes.Type(value = ExitDTO.class, name = "EXIT"),
        @JsonSubTypes.Type(value = StartDTO.class, name = "START"),
        @JsonSubTypes.Type(value = ZombieDTO.class, name = "ZOMBIE"),
        @JsonSubTypes.Type(value = ObjectiveDTO.class, name = "OBJECTIVE"),
        @JsonSubTypes.Type(value = SpawnDTO.class, name = "SPAWN"),
        @JsonSubTypes.Type(value = NumberTokenDTO.class, name = "NUMBER"),
        @JsonSubTypes.Type(value = SwitchTokenDTO.class, name = "SWITCH"),
        @JsonSubTypes.Type(value = VehicleDTO.class, name = "VEHICLES"),
        @JsonSubTypes.Type(value = PimpWeaponCrateDTO.class, name = "PIMPWEAPONCRATE")
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
        } else if (token instanceof Start) {
            dto = StartDTO.fromStart((Start) token);
        } else if (token instanceof Zombie) {
            dto = ZombieDTO.fromZombie((Zombie) token);
        } else if (token instanceof Objective) {
            dto = ObjectiveDTO.fromObjective((Objective) token);
        } else if (token instanceof Spawn) {
            dto = SpawnDTO.fromSpawn((Spawn) token);
        } else if (token instanceof NumberToken) {
            dto = NumberTokenDTO.fromNumberToken((NumberToken) token);
        } else if (token instanceof SwitchToken) {
            dto = SwitchTokenDTO.fromSwitchToken((SwitchToken) token);
        } else if (token instanceof Vehicle) {
            dto = VehicleDTO.fromVehicle((Vehicle) token);
        } else if (token instanceof PimpWeaponCrate) {
            dto = PimpWeaponCrateDTO.fromPimpWeaponCrate((PimpWeaponCrate) token);
        } else {
            // Log warning for unsupported token types
            System.err.println("Warning: Unsupported token type for serialization: " + token.getClass().getSimpleName());
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
