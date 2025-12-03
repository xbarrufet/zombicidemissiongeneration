package com.zombicide.missiongen.DTO;

import com.zombicide.missiongen.model.tokens.Token;
import com.zombicide.missiongen.model.tokens.TokenFactory;
import com.zombicide.missiongen.model.tokens.tokenType.Objective;

public class ObjectiveDTO extends TokenDTO {

    public ObjectiveDTO() {
        super();
    }

    public static ObjectiveDTO fromObjective(Objective objective) {
        return new ObjectiveDTO();
    }

    @Override
    public Token toToken() {
        Token token = TokenFactory.createToken(this.type, this.subtype);
        if (token instanceof Objective) {
            Objective objective = (Objective) token;
            objective.setLocation(this.location, this.areaId);

            // Restore rotation
            int rotations = this.rotation / 90;
            for (int i = 0; i < rotations; i++) {
                objective.rotate();
            }
            return objective;
        }
        return null;
    }
}
