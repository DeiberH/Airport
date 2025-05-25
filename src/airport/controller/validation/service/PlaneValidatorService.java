package airport.controller.validation.service;

import airport.controller.interfaces.IPlaneValidator; // Interface from correct package
import airport.controller.validation.PlaneValidation; // Your static validation class

public class PlaneValidatorService implements IPlaneValidator {
    @Override
    public String validatePlaneData(String id, String brand, String model, String maxCapacity, String airline) {
        return PlaneValidation.validatePlaneData(id, brand, model, maxCapacity, airline); //
    }
}