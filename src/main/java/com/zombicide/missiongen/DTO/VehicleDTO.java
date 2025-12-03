package com.zombicide.missiongen.DTO;

import com.zombicide.missiongen.model.tokens.Token;
import com.zombicide.missiongen.model.tokens.TokenFactory;
import com.zombicide.missiongen.model.tokens.tokenType.Vehicle;

public class VehicleDTO extends TokenDTO {

    public VehicleDTO() {
        super();
    }

    public static VehicleDTO fromVehicle(Vehicle vehicle) {
        return new VehicleDTO();
    }

    @Override
    public Token toToken() {
        Token token = TokenFactory.createToken(this.type, this.subtype);
        if (token instanceof Vehicle) {
            Vehicle vehicle = (Vehicle) token;
            vehicle.setLocation(this.location, this.areaId);

            // Restore rotation
            int rotations = this.rotation / 90;
            for (int i = 0; i < rotations; i++) {
                vehicle.rotate();
            }
            return vehicle;
        }
        return null;
    }
}
