package airport.controller.factory.service;

import airport.controller.builder.FlightBuilder; // Your static builder
import airport.controller.interfaces.IFlightFactory; // Interface from correct package
import airport.model.Flight;
import airport.model.Location;
import airport.model.Plane;
import java.time.LocalDateTime;

public class FlightFactoryService implements IFlightFactory {
    @Override
    public Flight build(String id, Plane plane, Location departureLocation, Location arrivalLocation, LocalDateTime departureDate, int hoursDurationArrival, int minutesDurationArrival) {
        return FlightBuilder.build(id, plane, departureLocation, arrivalLocation, departureDate, hoursDurationArrival, minutesDurationArrival); //
    }

    @Override
    public Flight build(String id, Plane plane, Location departureLocation, Location scaleLocation, Location arrivalLocation, LocalDateTime departureDate, int hoursDurationArrival, int minutesDurationArrival, int hoursDurationScale, int minutesDurationScale) {
        return FlightBuilder.build(id, plane, departureLocation, scaleLocation, arrivalLocation, departureDate, hoursDurationArrival, minutesDurationArrival, hoursDurationScale, minutesDurationScale); //
    }
}