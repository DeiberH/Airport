package airport.controller;

import airport.controller.utils.Response;
import airport.controller.utils.Status;
import airport.model.Flight;
import airport.model.Location;
import airport.model.Passenger;
import airport.model.Plane;
import airport.controller.interfaces.IFlightRepository;
import airport.controller.interfaces.IPlaneRepository;
import airport.controller.interfaces.ILocationRepository;
import airport.controller.interfaces.IPassengerRepository;
import airport.controller.interfaces.IFlightValidator;
import airport.controller.interfaces.IFlightFactory;
import airport.model.storage.StorageFlight;
import java.time.DateTimeException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class FlightController {

    private final IFlightRepository flightRepository;
    private final IFlightValidator flightValidator;
    private final IFlightFactory flightFactory;
    private final IPlaneRepository planeRepository;
    private final ILocationRepository locationRepository;
    private final IPassengerRepository passengerRepository;

    public FlightController(IFlightRepository flightRepository, IFlightValidator flightValidator, IFlightFactory flightFactory,
            IPlaneRepository planeRepository, ILocationRepository locationRepository, IPassengerRepository passengerRepository) {
        this.flightRepository = flightRepository;
        this.flightValidator = flightValidator;
        this.flightFactory = flightFactory;
        this.planeRepository = planeRepository;
        this.locationRepository = locationRepository;
        this.passengerRepository = passengerRepository;
    }

    public Response createFlight(String idStr, String planeIdStr,
            String departureLocationIdStr, String arrivalLocationIdStr, String scaleLocationIdStr,
            String yearStr, String monthStr, String dayStr, String hourStr, String minutesStr,
            String hoursDurationsArrivalStr, String minutesDurationsArrivalStr,
            String hoursDurationsScaleStr, String minutesDurationsScaleStr) {
        try {
            String validationError = flightValidator.validateFlightData(idStr, planeIdStr,
                    departureLocationIdStr, arrivalLocationIdStr, scaleLocationIdStr,
                    yearStr, monthStr, dayStr, hourStr, minutesStr,
                    hoursDurationsArrivalStr, minutesDurationsArrivalStr,
                    hoursDurationsScaleStr, minutesDurationsScaleStr);

            if (validationError != null) {
                return new Response(validationError, Status.BAD_REQUEST);
            }

            String id = idStr.trim();
            Plane plane = this.planeRepository.getPlane(planeIdStr.trim());
            if (plane == null) {
                return new Response("Selected Plane ('" + planeIdStr.trim() + "') not found.", Status.BAD_REQUEST);
            }

            LocalDateTime departureDate = LocalDateTime.of(
                    Integer.parseInt(yearStr.trim()), Integer.parseInt(monthStr.trim()), Integer.parseInt(dayStr.trim()),
                    Integer.parseInt(hourStr.trim()), Integer.parseInt(minutesStr.trim()));

            Location dLocation = this.locationRepository.getLocation(departureLocationIdStr.trim());
            if (dLocation == null) {
                return new Response("Departure Location ('" + departureLocationIdStr.trim() + "') not found.", Status.BAD_REQUEST);
            }
            Location aLocation = this.locationRepository.getLocation(arrivalLocationIdStr.trim());
            if (aLocation == null) {
                return new Response("Arrival Location ('" + arrivalLocationIdStr.trim() + "') not found.", Status.BAD_REQUEST);
            }

            if (dLocation.getAirportId().equals(aLocation.getAirportId())) {
                return new Response("Departure and Arrival locations cannot be the same.", Status.BAD_REQUEST);
            }

            int hourdaInt = Integer.parseInt(hoursDurationsArrivalStr.trim());
            int minutesdaInt = Integer.parseInt(minutesDurationsArrivalStr.trim());

            Location sLocation = null;
            int hourdsInt = 0;
            int minutesdsInt = 0;
            boolean isScaleLocationSelected = !(scaleLocationIdStr == null || scaleLocationIdStr.trim().isEmpty() || scaleLocationIdStr.trim().equals("Location"));

            if (isScaleLocationSelected) {
                sLocation = this.locationRepository.getLocation(scaleLocationIdStr.trim());
                if (sLocation == null) {
                    return new Response("Selected Scale Location ('" + scaleLocationIdStr.trim() + "') not found.", Status.BAD_REQUEST);
                }
                if (sLocation.getAirportId().equals(dLocation.getAirportId()) || sLocation.getAirportId().equals(aLocation.getAirportId())) {
                    return new Response("Scale location cannot be the same as departure or arrival location.", Status.BAD_REQUEST);
                }
                boolean isScaleDurationHourProvided = !(hoursDurationsScaleStr == null || hoursDurationsScaleStr.trim().isEmpty() || hoursDurationsScaleStr.equals("Hour"));
                boolean isScaleDurationMinuteProvided = !(minutesDurationsScaleStr == null || minutesDurationsScaleStr.trim().isEmpty() || minutesDurationsScaleStr.equals("Minute"));

                if (isScaleDurationHourProvided && isScaleDurationMinuteProvided) {
                    hourdsInt = Integer.parseInt(hoursDurationsScaleStr.trim());
                    minutesdsInt = Integer.parseInt(minutesDurationsScaleStr.trim());
                }
                if (hourdsInt == 0 && minutesdsInt == 0) {
                    return new Response("If a scale location is selected, its duration time must be greater than 00:00.", Status.BAD_REQUEST);
                }
            } else {
                boolean isScaleDurationHourProvided = !(hoursDurationsScaleStr == null || hoursDurationsScaleStr.trim().isEmpty() || hoursDurationsScaleStr.equals("Hour"));
                boolean isScaleDurationMinuteProvided = !(minutesDurationsScaleStr == null || minutesDurationsScaleStr.trim().isEmpty() || minutesDurationsScaleStr.equals("Minute"));
                if (isScaleDurationHourProvided || isScaleDurationMinuteProvided) {
                    hourdsInt = Integer.parseInt(hoursDurationsScaleStr.trim());
                    minutesdsInt = Integer.parseInt(minutesDurationsScaleStr.trim());
                    if (hourdsInt != 0 || minutesdsInt != 0) {
                        return new Response("If no scale location is selected, duration time of scale must be 00:00.", Status.BAD_REQUEST);
                    }
                }
            }

            Flight newFlight;
            if (sLocation == null) {
                newFlight = flightFactory.build(id, plane, dLocation, aLocation, departureDate, hourdaInt, minutesdaInt);
            } else {
                newFlight = flightFactory.build(id, plane, dLocation, sLocation, aLocation, departureDate, hourdaInt, minutesdaInt, hourdsInt, minutesdsInt);
            }

            if (!flightRepository.addFlight(newFlight)) {
                return new Response("A Flight with ID '" + id + "' already exists.", Status.BAD_REQUEST);
            }
            plane.addFlight(newFlight); //
            this.planeRepository.updatePlane(plane); // Persiste el cambio en el avión y notifica

            Flight flightCopy;
            if (sLocation == null) {
                flightCopy = flightFactory.build(id, plane, dLocation, aLocation, departureDate, hourdaInt, minutesdaInt);
            } else {
                flightCopy = flightFactory.build(id, plane, dLocation, sLocation, aLocation, departureDate, hourdaInt, minutesdaInt, hourdsInt, minutesdsInt);
            }
            return new Response("Flight created successfully.", Status.CREATED, flightCopy);

        } catch (NumberFormatException | java.time.DateTimeException ex) {
            return new Response("Invalid format for numeric or date inputs: " + ex.getMessage(), Status.BAD_REQUEST);
        } catch (Exception ex) {
            return new Response("An unexpected error occurred during flight creation: " + ex.getMessage(), Status.INTERNAL_SERVER_ERROR);
        }
    }

    public Response addPassengertoFlight(String passengerIdStr, String flightIdStr) {
        try {
            String validationError = flightValidator.validateAddPassengerToFlightData(passengerIdStr, flightIdStr);
            if (validationError != null) {
                return new Response(validationError, Status.BAD_REQUEST);
            }
            long passengerIdLong = Long.parseLong(passengerIdStr.trim());

            Passenger passenger = passengerRepository.getPassenger(passengerIdLong);
            if (passenger == null) {
                return new Response("Passenger with ID '" + passengerIdLong + "' not found.", Status.NOT_FOUND);
            }

            Flight flight = flightRepository.getFlight(flightIdStr.trim());
            if (flight == null) {
                return new Response("Flight with ID '" + flightIdStr.trim() + "' not found.", Status.NOT_FOUND);
            }

            for (Passenger p : flight.getPassengers()) { //
                if (p.getId() == passenger.getId()) {
                    return new Response("Passenger already added to this flight.", Status.BAD_REQUEST);
                }
            }

            if (flight.getPlane() != null && flight.getNumPassengers() >= flight.getPlane().getMaxCapacity()) { //
                return new Response("Flight is full. Cannot add more passengers.", Status.BAD_REQUEST);
            }

            flight.addPassenger(passenger); //
            passenger.addFlight(flight); //

            passengerRepository.updatePassenger(passenger);
            flightRepository.updateFlight(flight);

            return new Response("Passenger added to Flight successfully.", Status.OK);

        } catch (NumberFormatException e) {
            return new Response("Invalid numeric format for Passenger ID.", Status.BAD_REQUEST);
        } catch (Exception ex) {
            return new Response("Unexpected error adding passenger: " + ex.getMessage(), Status.INTERNAL_SERVER_ERROR);
        }
    }

    public Response delayFlight(String flightIdStr, String hourStr, String minutesStr) { //
        try {
            String validationError = flightValidator.validateDelayFlightData(flightIdStr, hourStr, minutesStr);
            if (validationError != null) {
                return new Response(validationError, Status.BAD_REQUEST);
            }

            int hourInt = Integer.parseInt(hourStr.trim());
            int minutesInt = Integer.parseInt(minutesStr.trim());

            Flight flight = flightRepository.getFlight(flightIdStr.trim());
            if (flight == null) {
                return new Response("Flight with ID '" + flightIdStr.trim() + "' not found.", Status.NOT_FOUND);
            }

            flight.delay(hourInt, minutesInt); //

            flightRepository.updateFlight(flight);

            Flight flightCopy = flightFactory.build(flight.getId(), flight.getPlane(),
                    flight.getDepartureLocation(), flight.getScaleLocation(), flight.getArrivalLocation(),
                    flight.getDepartureDate(), flight.getHoursDurationArrival(), flight.getMinutesDurationArrival(),
                    flight.getHoursDurationScale(), flight.getMinutesDurationScale());

            return new Response("Flight delayed successfully.", Status.OK, flightCopy);

        } catch (NumberFormatException e) {
            return new Response("Invalid numeric format for delay hours/minutes.", Status.BAD_REQUEST);
        } catch (Exception ex) {
            return new Response("Unexpected error delaying flight: " + ex.getMessage(), Status.INTERNAL_SERVER_ERROR);
        }
    }

    public Response getAllFlightsForTable() { //
        try {
            List<Flight> flights = flightRepository.getAllFlights();
            if (flights.isEmpty()) {
                return new Response("No flights found.", Status.OK, new ArrayList<>());
            }
            return new Response("Flights retrieved successfully.", Status.OK, new ArrayList<>(flights));
        } catch (Exception ex) {
            return new Response("Error retrieving flights: " + ex.getMessage(), Status.INTERNAL_SERVER_ERROR);
        }
    }
}
