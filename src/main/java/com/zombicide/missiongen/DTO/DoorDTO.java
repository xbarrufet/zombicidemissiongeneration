package com.zombicide.missiongen.DTO;

import com.zombicide.missiongen.model.tokens.Token;
import com.zombicide.missiongen.model.tokens.TokenFactory;
import com.zombicide.missiongen.model.tokens.tokenType.Door;

public class DoorDTO extends TokenDTO {

    public DoorDTO() {
        super();
    }

    public static DoorDTO fromDoor(Door door) {
        DoorDTO dto = new DoorDTO();
        // Store area IDs in properties
        if (door.getAreaA() != null) {
            dto.properties.put("areaA", door.getAreaA().toString());
        }
        if (door.getAreaB() != null) {
            dto.properties.put("areaB", door.getAreaB().toString());
        }
        return dto;
    }

    @Override
    public Token toToken() {
        Token token = TokenFactory.createToken(this.type, this.subtype);
        if (token instanceof Door) {
            Door door = (Door) token;
            door.setLocation(this.location, this.areaId);

            // Restore rotation
            int rotations = this.rotation / 90;
            for (int i = 0; i < rotations; i++) {
                door.rotate();
            }

            // Restore area connections from properties
            if (this.properties != null) {
                if (this.properties.containsKey("areaA")) {
                    door.setAreaA(java.util.UUID.fromString(this.properties.get("areaA")));
                }
                if (this.properties.containsKey("areaB")) {
                    door.setAreaB(java.util.UUID.fromString(this.properties.get("areaB")));
                }
            }
            return door;
        }
        return null;
    }
}
