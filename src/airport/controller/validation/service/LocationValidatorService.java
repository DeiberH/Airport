package airport.controller.validation.service;

import airport.controller.interfaces.ILocationValidator;
import airport.controller.validation.LocationValidation;

public class LocationValidatorService implements ILocationValidator {
    @Override
    public String validateLocationData(String id, String name, String city, String country, String latitude, String longitude) {
        return LocationValidation.validateLocationData(id, name, city, country, latitude, longitude); //
    }
}