package com.zombicide.missiongen.DTO;

import com.zombicide.missiongen.model.tokens.Token;
import com.zombicide.missiongen.model.tokens.TokenFactory;
import com.zombicide.missiongen.model.tokens.tokenType.Start;

public class StartDTO extends TokenDTO {

    public StartDTO() {
        super();
    }

    public static StartDTO fromStart(Start start) {
        return new StartDTO();
    }

    @Override
    public Token toToken() {
        Token token = TokenFactory.createToken(this.type, this.subtype);
        if (token instanceof Start) {
            Start start = (Start) token;
            start.setLocation(this.location, this.areaId);

            // Restore rotation
            int rotations = this.rotation / 90;
            for (int i = 0; i < rotations; i++) {
                start.rotate();
            }
            return start;
        }
        return null;
    }
}
