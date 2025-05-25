package airport.controller.validation.service;

import airport.controller.interfaces.IPassengerValidator; // Interface from correct package
import airport.controller.validation.PassengerValidation; // Your static validation class

public class PassengerValidatorService implements IPassengerValidator {
    @Override
    public String validatePassengerData(String id, String firstname, String lastname, String year, String month, String day, String phoneCode, String phone, String country) {
        return PassengerValidation.validatePassengerData(id, firstname, lastname, year, month, day, phoneCode, phone, country); //
    }
}