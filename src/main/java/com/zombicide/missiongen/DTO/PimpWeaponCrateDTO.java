package com.zombicide.missiongen.DTO;

import com.zombicide.missiongen.model.tokens.Token;
import com.zombicide.missiongen.model.tokens.TokenFactory;
import com.zombicide.missiongen.model.tokens.tokenType.PimpWeaponCrate;

public class PimpWeaponCrateDTO extends TokenDTO {

    public PimpWeaponCrateDTO() {
        super();
    }

    public static PimpWeaponCrateDTO fromPimpWeaponCrate(PimpWeaponCrate pimpWeaponCrate) {
        return new PimpWeaponCrateDTO();
    }

    @Override
    public Token toToken() {
        Token token = TokenFactory.createToken(this.type, this.subtype);
        if (token instanceof PimpWeaponCrate) {
            PimpWeaponCrate pimpWeaponCrate = (PimpWeaponCrate) token;
            pimpWeaponCrate.setLocation(this.location, this.areaId);

            // Restore rotation
            int rotations = this.rotation / 90;
            for (int i = 0; i < rotations; i++) {
                pimpWeaponCrate.rotate();
            }
            return pimpWeaponCrate;
        }
        return null;
    }
}
