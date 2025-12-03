package com.zombicide.missiongen.DTO;

import com.zombicide.missiongen.model.tokens.Token;
import com.zombicide.missiongen.model.tokens.TokenFactory;
import com.zombicide.missiongen.model.tokens.tokenType.Door;

public class DoorDTO extends TokenDTO {
    public BoardAreaConnectionDTO boardAreaConnection;

    public DoorDTO() {
        super();
    }

    public static DoorDTO fromDoor(Door door) {
        DoorDTO dto = new DoorDTO();
        if (door.getBoardAreaConnection() != null) {
            dto.boardAreaConnection = BoardAreaConnectionDTO.fromBoardAreaConnection(door.getBoardAreaConnection());
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

            if (this.boardAreaConnection != null) {
                door.setBoardAreaConnection(this.boardAreaConnection.toBoardAreaConnection());
            }
            return door;
        }
        return null;
    }
}
