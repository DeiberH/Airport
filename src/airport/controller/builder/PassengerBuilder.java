/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package airport.controller.builder;

import airport.model.Passenger;
import java.time.LocalDate;

/**
 *
 * @author Derby42
 */
public class PassengerBuilder {
    public static Passenger build(long id, String firstname, String lastname, LocalDate birthDate, int phoneCode, long phone, String country) {
        return new Passenger(id, firstname, lastname, birthDate, phoneCode, phone, country);
    }
}
