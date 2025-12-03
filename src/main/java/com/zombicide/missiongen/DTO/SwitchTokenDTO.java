package com.zombicide.missiongen.DTO;

import com.zombicide.missiongen.model.tokens.Token;
import com.zombicide.missiongen.model.tokens.TokenFactory;
import com.zombicide.missiongen.model.tokens.tokenType.SwitchToken;

public class SwitchTokenDTO extends TokenDTO {

    public SwitchTokenDTO() {
        super();
    }

    public static SwitchTokenDTO fromSwitchToken(SwitchToken switchToken) {
        return new SwitchTokenDTO();
    }

    @Override
    public Token toToken() {
        Token token = TokenFactory.createToken(this.type, this.subtype);
        if (token instanceof SwitchToken) {
            SwitchToken switchToken = (SwitchToken) token;
            switchToken.setLocation(this.location, this.areaId);

            // Restore rotation
            int rotations = this.rotation / 90;
            for (int i = 0; i < rotations; i++) {
                switchToken.rotate();
            }
            return switchToken;
        }
        return null;
    }
}
