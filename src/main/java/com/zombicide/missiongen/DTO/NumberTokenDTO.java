package com.zombicide.missiongen.DTO;

import com.zombicide.missiongen.model.tokens.Token;
import com.zombicide.missiongen.model.tokens.TokenFactory;
import com.zombicide.missiongen.model.tokens.tokenType.NumberToken;

public class NumberTokenDTO extends TokenDTO {

    public NumberTokenDTO() {
        super();
    }

    public static NumberTokenDTO fromNumberToken(NumberToken numberToken) {
        return new NumberTokenDTO();
    }

    @Override
    public Token toToken() {
        Token token = TokenFactory.createToken(this.type, this.subtype);
        if (token instanceof NumberToken) {
            NumberToken numberToken = (NumberToken) token;
            numberToken.setLocation(this.location, this.areaId);

            // Restore rotation
            int rotations = this.rotation / 90;
            for (int i = 0; i < rotations; i++) {
                numberToken.rotate();
            }
            return numberToken;
        }
        return null;
    }
}
