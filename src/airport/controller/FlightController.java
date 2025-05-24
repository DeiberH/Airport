/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package airport.controller;

import airport.controller.utils.Response;
import airport.controller.utils.Status;
import airport.model.Flight;
import airport.model.Location;
import airport.model.Passenger;
import airport.model.Plane;
import airport.model.storage.StorageFlight;
import airport.model.storage.StorageLocation;
import airport.model.storage.StoragePassenger;
import airport.model.storage.StoragePlane;
import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Derby42
 */
// CORREGIR LOS ERRORES DE LOS PARSEINT DEL TIEMPO
public class FlightController {

    public static Response createFlight(String id, String planeId, String departureLocationId, String arrivalLocationId, String scaleLocationId, String year, String month, String day, String hour, String minutes, String hoursDurationsArrival, String minutesDurationsArrival, String hoursDurationsScale, String minutesDurationsScale) {
        try {
            // ... (all previous validations for ID, plane, date, locations, durations remain here)
            int yearInt, monthInt, dayInt, hoursInt, minutesInt, hourdaInt, minutesdaInt, hourdsInt, minutesdsInt;
            LocalDateTime departureDate;
            
            // ID Validation (XXXYYY format)
            if (id.trim().isEmpty()) {
                return new Response("Flight Id must not be empty", Status.BAD_REQUEST);
            }
            if (id.length() != 6) {
                return new Response("Flight Id must have a length of 6 characters (XXXYYY)", Status.BAD_REQUEST);
            }
            for (int i = 0; i < 3; i++) {
                if (!Character.isUpperCase(id.charAt(i))) {
                    return new Response("First 3 characters of Flight Id must be uppercase letters (XXXYYY)", Status.BAD_REQUEST);
                }
            }
            for (int i = 3; i < 6; i++) {
                if (!Character.isDigit(id.charAt(i))) {
                    return new Response("Last 3 characters of Flight Id must be digits (XXXYYY)", Status.BAD_REQUEST);
                }
            }

            // Plane Validation
            StoragePlane storagep = StoragePlane.getInstance();
            Plane plane = storagep.getPlane(planeId);
            if (plane == null) {
                return new Response("The selected Plane ('" + planeId + "') is not valid or does not exist.", Status.BAD_REQUEST);
            }

            // Date Validation
            if (year.trim().isEmpty()) { return new Response("Departure year must not be empty", Status.BAD_REQUEST); }
            try {
                yearInt = Integer.parseInt(year);
                if (yearInt < 1900) { return new Response("Departure year must be valid [>=1900]", Status.BAD_REQUEST); }
            } catch (NumberFormatException ex) { return new Response("Departure year must be numeric", Status.BAD_REQUEST); }
            try {
                monthInt = Integer.parseInt(month); dayInt = Integer.parseInt(day); hoursInt = Integer.parseInt(hour); minutesInt = Integer.parseInt(minutes);
                departureDate = LocalDateTime.of(yearInt, monthInt, dayInt, hoursInt, minutesInt);
                if (departureDate.isBefore(LocalDateTime.now())) { return new Response("Departure date must be in the future.", Status.BAD_REQUEST); }
            } catch (DateTimeException ex) { return new Response("Departure Date/Time format is not valid or realistic (e.g., Feb 30).", Status.BAD_REQUEST);
            } catch (NumberFormatException ex) { return new Response("Departure date/time components (month, day, hour, minute) must be numeric.", Status.BAD_REQUEST); }

            // Location Validations
            StorageLocation storagel = StorageLocation.getInstance();
            Location dLocation = storagel.getLocation(departureLocationId);
            Location aLocation = storagel.getLocation(arrivalLocationId);
            if (dLocation == null) { return new Response("A valid Departure Location ('" + departureLocationId + "') must be selected/exist.", Status.BAD_REQUEST); }
            if (aLocation == null) { return new Response("A valid Arrival Location ('" + arrivalLocationId + "') must be selected/exist.", Status.BAD_REQUEST); }
            if (dLocation.getAirportId().equals(aLocation.getAirportId())) { return new Response("Departure and Arrival locations cannot be the same.", Status.BAD_REQUEST); }

            // Arrival Duration Validation
            try {
                hourdaInt = Integer.parseInt(hoursDurationsArrival); minutesdaInt = Integer.parseInt(minutesDurationsArrival);
                if (hourdaInt < 0 || minutesdaInt < 0 || (hourdaInt == 0 && minutesdaInt == 0)) { return new Response("Arrival duration time must be greater than 00:00 and positive.", Status.BAD_REQUEST); }
                if (minutesdaInt >= 60) { return new Response("Arrival duration minutes must be less than 60.", Status.BAD_REQUEST); }
            } catch (NumberFormatException ex) { return new Response("Arrival duration hours and minutes must be numeric.", Status.BAD_REQUEST); }

            // Scale Location and Duration Validation
            Location sLocation = null;
            boolean isPlaceholderScale = scaleLocationId == null || scaleLocationId.trim().isEmpty() || scaleLocationId.equals("Location"); 
            if (!isPlaceholderScale) {
                sLocation = storagel.getLocation(scaleLocationId);
                if (sLocation == null) { return new Response("The selected Scale Location ('" + scaleLocationId + "') is not valid or does not exist.", Status.BAD_REQUEST); }
                if (sLocation.getAirportId().equals(dLocation.getAirportId()) || sLocation.getAirportId().equals(aLocation.getAirportId())) { return new Response("Scale location cannot be the same as departure or arrival location.", Status.BAD_REQUEST); }
            }
            try {
                hourdsInt = Integer.parseInt(hoursDurationsScale); minutesdsInt = Integer.parseInt(minutesDurationsScale);
                if (hourdsInt < 0 || minutesdsInt < 0) { return new Response("Scale duration time must be positive or zero.", Status.BAD_REQUEST); }
                if (minutesdsInt >= 60) { return new Response("Scale duration minutes must be less than 60.", Status.BAD_REQUEST); }
            } catch (NumberFormatException ex) { return new Response("Scale duration hours and minutes must be numeric.", Status.BAD_REQUEST); }
            if (sLocation == null) {
                if (hourdsInt != 0 || minutesdsInt != 0) { return new Response("If no scale location is selected, duration time of scale must be 00:00.", Status.BAD_REQUEST); }
            } else {
                if (hourdsInt == 0 && minutesdsInt == 0) { return new Response("If a scale location is selected, its duration time must be greater than 00:00.", Status.BAD_REQUEST); }
            }
            // End of Validations

            StorageFlight storage = StorageFlight.getInstance();
            Flight newFlight; // This is the canonical flight object

            if (sLocation == null) {
                newFlight = new Flight(id, plane, dLocation, aLocation, departureDate, hourdaInt, minutesdaInt);
            } else {
                newFlight = new Flight(id, plane, dLocation, sLocation, aLocation, departureDate, hourdaInt, minutesdaInt, hourdsInt, minutesdsInt);
            }

            // Attempt to add the canonical flight to global storage
            if (!storage.addFlight(newFlight)) {
                return new Response("A Flight with id '" + id + "' already exists in global storage.", Status.BAD_REQUEST);
            }

            // **FIX APPLIED HERE**: 
            // If successfully added to global storage, then associate this flight with the plane.
            // The Plane.addFlight() method should just add it to its internal list.
            plane.addFlight(newFlight); 

            // Create the copy for the response. Its constructor (now modified) will NOT call plane.addFlight().
            Flight flightCopy; 
            if (sLocation == null) {
                flightCopy = new Flight(id, plane, dLocation, aLocation, departureDate, hourdaInt, minutesdaInt);
            } else {
                flightCopy = new Flight(id, plane, dLocation, sLocation, aLocation, departureDate, hourdaInt, minutesdaInt, hourdsInt, minutesdsInt);
            }
            // Note: The flightCopy's passengers list will be empty. If the response needs passengers, they'd have to be copied.
            // For a "created" response, the core flight data (without passengers yet) is usually sufficient.
            
            return new Response("Flight created successfully.", Status.CREATED, flightCopy);

        } catch (Exception ex) {
            // ex.printStackTrace(); 
            return new Response("Unexpected error during flight creation: " + ex.getMessage(), Status.INTERNAL_SERVER_ERROR);
        }
    }

    public static Response addPassengertoFlight(String passengerId, String FlightId) {
        try {
            Passenger passenger = null;

            if (passengerId.trim().isEmpty() || passengerId == null) {
                return new Response("User Hasn't been selected", Status.BAD_REQUEST);
            }

            StoragePassenger storagep = StoragePassenger.getInstance();

            long idlong = Long.parseLong(passengerId);
            for (Passenger p : storagep.getPassengers()) {
                if (p.getId() == idlong) {
                    passenger = p;
                }
            }

            if (passenger == null) {
                return new Response("Passenger was not found", Status.BAD_REQUEST);
            }

            StorageFlight storagef = StorageFlight.getInstance();

            for (Flight f : storagef.getFlights()) {
                if (f.getId().equals(FlightId)) {
                    for (Passenger p : f.getPassangers()) {
                        if (p.getId() == passenger.getId()) {
                            return new Response("Passenger alredy added to flight", Status.BAD_REQUEST);
                        }

                    }
                    f.addPassenger(passenger);
                    return new Response("Passenger added to Flight successfully", Status.OK);
                }
            }
            return new Response("Flight not found", Status.NOT_FOUND);
        } catch (Exception ex) {
            return new Response("Unexpected error", Status.INTERNAL_SERVER_ERROR);
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
            // Storage should return sorted copy
            if (flights.isEmpty()) {
                return new Response("No flights found.", Status.OK, new ArrayList<Flight>());
            }
            return new Response("Flights retrieved successfully.", Status.OK, flights);
        } catch (Exception ex) {
            // ex.printStackTrace();
            return new Response("Error retrieving flights: " + ex.getMessage(), Status.INTERNAL_SERVER_ERROR);
        }
    }
}
