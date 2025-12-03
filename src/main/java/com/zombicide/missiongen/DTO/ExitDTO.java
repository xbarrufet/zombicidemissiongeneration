package com.zombicide.missiongen.DTO;

import com.zombicide.missiongen.model.tokens.Token;
import com.zombicide.missiongen.model.tokens.TokenFactory;
import com.zombicide.missiongen.model.tokens.tokenType.Exit;

public class ExitDTO extends TokenDTO {

    public ExitDTO() {
        super();
    }

    public static ExitDTO fromExit(Exit exit) {
        return new ExitDTO();
    }

    @Override
    public Token toToken() {
        Token token = TokenFactory.createToken(this.type, this.subtype);
        if (token instanceof Exit) {
            Exit exit = (Exit) token;
            exit.setLocation(this.location, this.areaId);

            // Restore rotation
            int rotations = this.rotation / 90;
            for (int i = 0; i < rotations; i++) {
                exit.rotate();
            }
            return exit;
        }
        return null;
    }
}
