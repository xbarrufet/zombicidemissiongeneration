package com.zombicide.missiongen.DTO;

import com.zombicide.missiongen.model.tokens.Token;
import com.zombicide.missiongen.model.tokens.TokenFactory;
import com.zombicide.missiongen.model.tokens.tokenType.Zombie;

public class ZombieDTO extends TokenDTO {

    public ZombieDTO() {
        super();
    }

    public static ZombieDTO fromZombie(Zombie zombie) {
        return new ZombieDTO();
    }

    @Override
    public Token toToken() {
        Token token = TokenFactory.createToken(this.type, this.subtype);
        if (token instanceof Zombie) {
            Zombie zombie = (Zombie) token;
            zombie.setLocation(this.location, this.areaId);

            // Restore rotation
            int rotations = this.rotation / 90;
            for (int i = 0; i < rotations; i++) {
                zombie.rotate();
            }
            return zombie;
        }
        return null;
    }
}
