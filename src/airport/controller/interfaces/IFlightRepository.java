/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package airport.controller.interfaces;
import airport.model.Flight;
import java.util.List;

public interface IFlightRepository {
    boolean addFlight(Flight flight);
    Flight getFlight(String id);
    List<Flight> getAllFlights();
}