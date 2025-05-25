/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package airport.controller.builder;

import airport.model.Flight;
import airport.model.Location;
import airport.model.Plane;
import java.time.LocalDateTime;

/**
 *
 * @author Derby42
 */
public class FlightBuilder {

    public static Flight build(String id, Plane plane,Location departureLocation, Location arrivalLocation,LocalDateTime departureDate,int hoursDurationArrival, int minutesDurationArrival) {
        return new Flight(id, plane, departureLocation, arrivalLocation, departureDate,hoursDurationArrival, minutesDurationArrival);
    }

    public static Flight build(String id, Plane plane,Location departureLocation, Location scaleLocation, Location arrivalLocation,LocalDateTime departureDate,int hoursDurationArrival, int minutesDurationArrival,int hoursDurationScale, int minutesDurationScale) {
        return new Flight(id, plane, departureLocation, scaleLocation, arrivalLocation, departureDate,hoursDurationArrival, minutesDurationArrival,hoursDurationScale, minutesDurationScale);
    }
}
