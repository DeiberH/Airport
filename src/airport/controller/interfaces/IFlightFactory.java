/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package airport.controller.interfaces;

/**
 *
 * @author Juan Sebastian
 */
import airport.model.Flight;
import airport.model.Location;
import airport.model.Plane;
import java.time.LocalDateTime;
public interface IFlightFactory {
    Flight build(String id, Plane plane, Location departureLocation, Location arrivalLocation, LocalDateTime departureDate, int hoursDurationArrival, int minutesDurationArrival);
    Flight build(String id, Plane plane, Location departureLocation, Location scaleLocation, Location arrivalLocation, LocalDateTime departureDate, int hoursDurationArrival, int minutesDurationArrival, int hoursDurationScale, int minutesDurationScale);
}
