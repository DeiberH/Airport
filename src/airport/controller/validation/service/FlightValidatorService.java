package airport.controller.validation.service;

import airport.controller.interfaces.IFlightValidator; // Interface from correct package
import airport.controller.validation.FlightValidation; // Your static validation class

public class FlightValidatorService implements IFlightValidator {
    @Override
    public String validateFlightData(String id, String planeId, String departureLocationId, String arrivalLocationId, String scaleLocationId, String year, String month, String day, String hour, String minutes, String hoursDurationsArrival, String minutesDurationsArrival, String hoursDurationsScale, String minutesDurationsScale) {
        return FlightValidation.validateFlightData(id, planeId, departureLocationId, arrivalLocationId, scaleLocationId, year, month, day, hour, minutes, hoursDurationsArrival, minutesDurationsArrival, hoursDurationsScale, minutesDurationsScale); //
    }

    @Override
    public String validateAddPassengerToFlightData(String passengerId, String flightId) {
        return FlightValidation.validateAddPassengerToFlightData(passengerId, flightId); //
    }

    @Override
    public String validateDelayFlightData(String flightId, String hourStr, String minutesStr) {
        return FlightValidation.validateDelayFlightData(flightId, hourStr, minutesStr); //
    }
}