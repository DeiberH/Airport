package airport.controller.factory.service;

import airport.controller.builder.PassengerBuilder; // Your static builder
import airport.controller.interfaces.IPassengerFactory; // Interface from correct package
import airport.model.Passenger;
import java.time.LocalDate;

public class PassengerFactoryService implements IPassengerFactory {
    @Override
    public Passenger build(long id, String firstname, String lastname, LocalDate birthDate, int phoneCode, long phone, String country) {
        return PassengerBuilder.build(id, firstname, lastname, birthDate, phoneCode, phone, country); //
    }
}