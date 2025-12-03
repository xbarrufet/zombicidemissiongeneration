package com.zombicide.missiongen.DTO;

import com.zombicide.missiongen.model.tokens.Token;
import com.zombicide.missiongen.model.tokens.TokenFactory;
import com.zombicide.missiongen.model.tokens.tokenType.Spawn;

public class SpawnDTO extends TokenDTO {

    public SpawnDTO() {
        super();
    }

    public static SpawnDTO fromSpawn(Spawn spawn) {
        return new SpawnDTO();
    }

    @Override
    public Token toToken() {
        Token token = TokenFactory.createToken(this.type, this.subtype);
        if (token instanceof Spawn) {
            Spawn spawn = (Spawn) token;
            spawn.setLocation(this.location, this.areaId);

            // Restore rotation
            int rotations = this.rotation / 90;
            for (int i = 0; i < rotations; i++) {
                spawn.rotate();
            }
            return spawn;
        }
        return null;
    }
}
