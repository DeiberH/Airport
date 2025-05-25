/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package airport.controller.interfaces;
import airport.model.Passenger;
import airport.model.Flight; // If getFlightsForPassenger is a repo concern
import java.util.List;

public interface IPassengerRepository {
    boolean addPassenger(Passenger passenger);
    Passenger getPassenger(long id);
    boolean updatePassenger(Passenger passenger);
    List<Passenger> getAllPassengers();
}