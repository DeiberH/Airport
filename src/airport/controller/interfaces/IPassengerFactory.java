/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package airport.controller.interfaces;

/**
 *
 * @author Juan Sebastian
 */
import airport.model.Passenger;
import java.time.LocalDate;
public interface IPassengerFactory {
    Passenger build(long id, String firstname, String lastname, LocalDate birthDate, int phoneCode, long phone, String country);
}
