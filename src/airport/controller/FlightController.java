/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package airport.controller;

import airport.controller.builder.FlightBuilder;
import airport.controller.utils.Response;
import airport.controller.utils.Status;
import airport.controller.validation.FlightValidation;
import airport.model.Flight;
import airport.model.Location;
import airport.model.Passenger;
import airport.model.Plane;
import airport.model.storage.StorageFlight;
import airport.model.storage.StorageLocation;
import airport.model.storage.StoragePassenger;
import airport.model.storage.StoragePlane;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Derby42
 */
public class FlightController {

    public static Response createFlight(String id, String planeId, String departureLocationId, String arrivalLocationId, String scaleLocationId, String year, String month, String day, String hour, String minutes, String hoursDurationsArrival, String minutesDurationsArrival, String hoursDurationsScale, String minutesDurationsScale) {
        try {            
            String validationError = FlightValidation.validateFlightData(id, planeId, departureLocationId, arrivalLocationId, scaleLocationId, year, month, day, hour, minutes, hoursDurationsArrival, minutesDurationsArrival, hoursDurationsScale, minutesDurationsScale);
            if (validationError != null) {
                return new Response(validationError, Status.BAD_REQUEST);
            }

            Plane plane = StoragePlane.getInstance().getPlane(planeId.trim());
            if (plane == null) {
                return new Response("Selected Plane ('" + planeId.trim() + "') not found.", Status.BAD_REQUEST);
            }

            LocalDateTime departureDate;
            int yearInt = Integer.parseInt(year.trim());
            int monthInt = Integer.parseInt(month.trim());
            int dayInt = Integer.parseInt(day.trim());
            int hoursInt = Integer.parseInt(hour.trim());
            int minutesInt = Integer.parseInt(minutes.trim());
            departureDate = LocalDateTime.of(yearInt, monthInt, dayInt, hoursInt, minutesInt);

            Location dLocation = StorageLocation.getInstance().getLocation(departureLocationId.trim());
            if (dLocation == null) {
                return new Response("Departure Location ('" + departureLocationId.trim() + "') not found.", Status.BAD_REQUEST);
            }
            Location aLocation = StorageLocation.getInstance().getLocation(arrivalLocationId.trim());
            if (aLocation == null) {
                return new Response("Arrival Location ('" + arrivalLocationId.trim() + "') not found.", Status.BAD_REQUEST);
            }

            if (dLocation.getAirportId().equals(aLocation.getAirportId())) {
                return new Response("Departure and Arrival locations cannot be the same.", Status.BAD_REQUEST);
            }

            int hourdaInt = Integer.parseInt(hoursDurationsArrival.trim());
            int minutesdaInt = Integer.parseInt(minutesDurationsArrival.trim());

            Location sLocation = null;
            int hourdsInt = 0;
            int minutesdsInt = 0;

            boolean isScaleLocationSelected = !(scaleLocationId == null || scaleLocationId.trim().isEmpty() || scaleLocationId.trim().equals("Location"));

            if (isScaleLocationSelected) {
                sLocation = StorageLocation.getInstance().getLocation(scaleLocationId.trim());
                if (sLocation == null) {
                    return new Response("Selected Scale Location ('" + scaleLocationId.trim() + "') not found.", Status.BAD_REQUEST);
                }
                if (sLocation.getAirportId().equals(dLocation.getAirportId()) || sLocation.getAirportId().equals(aLocation.getAirportId())) {
                    return new Response("Scale location cannot be the same as departure or arrival location.", Status.BAD_REQUEST);
                }
                boolean isScaleDurationHourProvided = !(hoursDurationsScale == null || hoursDurationsScale.trim().isEmpty() || hoursDurationsScale.equals("Hour"));
                boolean isScaleDurationMinuteProvided = !(minutesDurationsScale == null || minutesDurationsScale.trim().isEmpty() || minutesDurationsScale.equals("Minute"));

                if (isScaleDurationHourProvided && isScaleDurationMinuteProvided) {
                    hourdsInt = Integer.parseInt(hoursDurationsScale.trim());
                    minutesdsInt = Integer.parseInt(minutesDurationsScale.trim());
                }
                if (hourdsInt == 0 && minutesdsInt == 0) {
                    return new Response("If a scale location is selected, its duration time must be greater than 00:00.", Status.BAD_REQUEST);
                }

            } else {
                boolean isScaleDurationHourProvided = !(hoursDurationsScale == null || hoursDurationsScale.trim().isEmpty() || hoursDurationsScale.equals("Hour"));
                boolean isScaleDurationMinuteProvided = !(minutesDurationsScale == null || minutesDurationsScale.trim().isEmpty() || minutesDurationsScale.equals("Minute"));
                if (isScaleDurationHourProvided || isScaleDurationMinuteProvided) {
                    hourdsInt = Integer.parseInt(hoursDurationsScale.trim());
                    minutesdsInt = Integer.parseInt(minutesDurationsScale.trim());
                    if (hourdsInt != 0 || minutesdsInt != 0) {
                        return new Response("If no scale location is selected, duration time of scale must be 00:00.", Status.BAD_REQUEST);
                    }
                }
            }

            Flight newFlight;
            if (sLocation == null) {
                newFlight = FlightBuilder.build(id, plane, dLocation, aLocation, departureDate, hourdaInt, minutesdaInt);
            } else {
                newFlight = FlightBuilder.build(id, plane, dLocation, sLocation, aLocation, departureDate, hourdaInt, minutesdaInt, hourdsInt, minutesdsInt);
            }

            StorageFlight storage = StorageFlight.getInstance();
            if (!storage.addFlight(newFlight)) {
                return new Response("A Flight with id '" + id + "' already exists in global storage.", Status.BAD_REQUEST);
            }
            plane.addFlight(newFlight);

            Flight flightCopy;
            if (sLocation == null) {
                flightCopy = FlightBuilder.build(id, plane, dLocation, aLocation, departureDate, hourdaInt, minutesdaInt);
            } else {
                flightCopy = FlightBuilder.build(id, plane, dLocation, sLocation, aLocation, departureDate, hourdaInt, minutesdaInt, hourdsInt, minutesdsInt);
            }

            return new Response("Flight created successfully.", Status.CREATED, flightCopy);

        } catch (NumberFormatException ex) {
            return new Response("Invalid numeric format for one of the inputs: " + ex.getMessage(), Status.BAD_REQUEST);
        } catch (Exception ex) {
            return new Response("An unexpected error occurred during flight creation: " + ex.getMessage(), Status.INTERNAL_SERVER_ERROR);
        }
    }

    public static Response addPassengertoFlight(String passengerId, String flightId) {
        try {
            String validationError = FlightValidation.validateAddPassengerToFlightData(passengerId, flightId);
            if (validationError != null) {
                return new Response(validationError, Status.BAD_REQUEST);
            }

            long passengerIdLong = Long.parseLong(passengerId.trim());

            StoragePassenger storagePassenger = StoragePassenger.getInstance();
            Passenger passenger = storagePassenger.getPassenger(passengerIdLong);
            if (passenger == null) {
                return new Response("Passenger with ID '" + passengerIdLong + "' not found.", Status.NOT_FOUND);
            }

            StorageFlight storageFlight = StorageFlight.getInstance();
            Flight flight = storageFlight.getFlight(flightId.trim());
            if (flight == null) {
                return new Response("Flight with ID '" + flightId.trim() + "' not found.", Status.NOT_FOUND);
            }

            for (Passenger p : flight.getPassengers()) {
                if (p.getId() == passenger.getId()) {
                    return new Response("Passenger already added to this flight.", Status.BAD_REQUEST);
                }
            }

            if (flight.getPlane() != null && flight.getNumPassengers() >= flight.getPlane().getMaxCapacity()) {
                return new Response("Flight is full. Cannot add more passengers.", Status.BAD_REQUEST);
            }

            flight.addPassenger(passenger);
            passenger.addFlight(flight);

            return new Response("Passenger added to Flight successfully.", Status.OK);

        } catch (NumberFormatException e) {
            return new Response("Invalid numeric format for Passenger ID.", Status.BAD_REQUEST);
        } catch (Exception ex) {
            return new Response("An unexpected error occurred while adding passenger to flight: " + ex.getMessage(), Status.INTERNAL_SERVER_ERROR);
        }
    }

    public static Response delayFlight(String flightId, String hour, String minutes) {
        try {
            int hourInt, minutesInt;
            Flight flight = null;

            StorageFlight storage = StorageFlight.getInstance();

            for (Flight f : storage.getFlights()) {
                if (f.getId().equals(flightId)) {
                    flight = f;
                }
            }

            if (flight == null) {
                return new Response("Flight not found", Status.NOT_FOUND);
            }

            try {
                hourInt = Integer.parseInt(hour);
                minutesInt = Integer.parseInt(minutes);
                if (hourInt == 0 && minutesInt == 0) {
                    return new Response("Delay must be longer than 00:00", Status.BAD_REQUEST);
                }

            } catch (NumberFormatException e) {
                return new Response("Hour or Minutes were not selected", Status.BAD_REQUEST);
            }

            flight.delay(hourInt, minutesInt);
            return new Response("Passenger added to Flight successfully", Status.OK);
        } catch (Exception ex) {
            return new Response("Unexpected error", Status.INTERNAL_SERVER_ERROR);
        }
    }

    public static Response getAllFlightsForTable() {
        try {
            List<Flight> flights = StorageFlight.getInstance().getAllFlights();
            if (flights.isEmpty()) {
                return new Response("No flights found.", Status.OK, new ArrayList<Flight>());
            }
            return new Response("Flights retrieved successfully.", Status.OK, flights);
        } catch (Exception ex) {
            return new Response("Error retrieving flights: " + ex.getMessage(), Status.INTERNAL_SERVER_ERROR);
        }
    }
}
