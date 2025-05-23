/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package airport.controller;

import airport.controller.utils.Response;
import airport.controller.utils.Status;
import airport.model.Flight;
import airport.model.Location;
import airport.model.Plane;
import airport.model.storage.StorageFlight;
import airport.model.storage.StorageLocation;
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
            if (year.trim().isEmpty()) {
                return new Response("Departure year must not be empty", Status.BAD_REQUEST);
            }
            try {
                yearInt = Integer.parseInt(year);
                if (yearInt < 1900) {
                    return new Response("Departure year must be valid [>=1900]", Status.BAD_REQUEST);
                }
            } catch (NumberFormatException ex) {
                return new Response("Departure year must be numeric", Status.BAD_REQUEST);
            }
            try {
                monthInt = Integer.parseInt(month);
                dayInt = Integer.parseInt(day);
                hoursInt = Integer.parseInt(hour);
                minutesInt = Integer.parseInt(minutes);
                departureDate = LocalDateTime.of(yearInt, monthInt, dayInt, hoursInt, minutesInt);
                if (departureDate.isBefore(LocalDateTime.now())) {
                    return new Response("Departure date must be in the future.", Status.BAD_REQUEST);
                }
            } catch (DateTimeException ex) {
                return new Response("Departure Date/Time format is not valid or realistic (e.g., Feb 30).", Status.BAD_REQUEST);
            } catch (NumberFormatException ex) {
                return new Response("Departure date/time components (month, day, hour, minute) must be numeric.", Status.BAD_REQUEST);
            }

            // Location Validations
            StorageLocation storagel = StorageLocation.getInstance();
            Location dLocation = storagel.getLocation(departureLocationId);
            Location aLocation = storagel.getLocation(arrivalLocationId);

            if (dLocation == null) {
                return new Response("A valid Departure Location ('" + departureLocationId + "') must be selected/exist.", Status.BAD_REQUEST);
            }
            if (aLocation == null) {
                return new Response("A valid Arrival Location ('" + arrivalLocationId + "') must be selected/exist.", Status.BAD_REQUEST);
            }
            // **CRITICAL FIX 1: Prevent same departure and arrival locations**
            if (dLocation.getAirportId().equals(aLocation.getAirportId())) {
                return new Response("Departure and Arrival locations cannot be the same.", Status.BAD_REQUEST);
            }

            // Arrival Duration Validation
            try {
                hourdaInt = Integer.parseInt(hoursDurationsArrival);
                minutesdaInt = Integer.parseInt(minutesDurationsArrival);
                if (hourdaInt < 0 || minutesdaInt < 0 || (hourdaInt == 0 && minutesdaInt == 0)) {
                    return new Response("Arrival duration time must be greater than 00:00 and positive.", Status.BAD_REQUEST);
                }
                if (minutesdaInt >= 60) {
                    return new Response("Arrival duration minutes must be less than 60.", Status.BAD_REQUEST);
                }
            } catch (NumberFormatException ex) {
                return new Response("Arrival duration hours and minutes must be numeric.", Status.BAD_REQUEST);
            }

            // Scale Location and Duration Validation
            Location sLocation = null;
            // Check if scaleLocationId is a placeholder (e.g., the first item "Location" if your JComboBox has it)
            // or if it's a genuinely selected ID.
            boolean isPlaceholderScale = scaleLocationId == null || scaleLocationId.trim().isEmpty() || scaleLocationId.equals("Location"); // Adjust "Location" if your placeholder is different

            if (!isPlaceholderScale) {
                sLocation = storagel.getLocation(scaleLocationId);
                if (sLocation == null) {
                    return new Response("The selected Scale Location ('" + scaleLocationId + "') is not valid or does not exist.", Status.BAD_REQUEST);
                }
                // **CRITICAL FIX 2: Prevent scale being same as departure or arrival**
                if (sLocation.getAirportId().equals(dLocation.getAirportId()) || sLocation.getAirportId().equals(aLocation.getAirportId())) {
                    return new Response("Scale location cannot be the same as departure or arrival location.", Status.BAD_REQUEST);
                }
            }

            try {
                hourdsInt = Integer.parseInt(hoursDurationsScale);
                minutesdsInt = Integer.parseInt(minutesDurationsScale);
                if (hourdsInt < 0 || minutesdsInt < 0) {
                    return new Response("Scale duration time must be positive or zero.", Status.BAD_REQUEST);
                }
                if (minutesdsInt >= 60) {
                    return new Response("Scale duration minutes must be less than 60.", Status.BAD_REQUEST);
                }
            } catch (NumberFormatException ex) {
                return new Response("Scale duration hours and minutes must be numeric.", Status.BAD_REQUEST);
            }

            if (sLocation == null) { // No actual scale location selected
                if (hourdsInt != 0 || minutesdsInt != 0) {
                    return new Response("If no scale location is selected, duration time of scale must be 00:00.", Status.BAD_REQUEST);
                }
            } else { // Scale location is selected
                if (hourdsInt == 0 && minutesdsInt == 0) {
                    return new Response("If a scale location is selected, its duration time must be greater than 00:00.", Status.BAD_REQUEST);
                }
            }

            StorageFlight storage = StorageFlight.getInstance();
            Flight newFlight;

            if (sLocation == null) {
                newFlight = new Flight(id, plane, dLocation, aLocation, departureDate, hourdaInt, minutesdaInt);
            } else {
                newFlight = new Flight(id, plane, dLocation, sLocation, aLocation, departureDate, hourdaInt, minutesdaInt, hourdsInt, minutesdsInt);
            }

            if (!storage.addFlight(newFlight)) {
                return new Response("A Flight with id '" + id + "' already exists.", Status.BAD_REQUEST);
            }

            Flight flightCopy; // For the response, ensuring prototype principle for the new object
            if (sLocation == null) {
                flightCopy = new Flight(id, plane, dLocation, aLocation, departureDate, hourdaInt, minutesdaInt);
            } else {
                flightCopy = new Flight(id, plane, dLocation, sLocation, aLocation, departureDate, hourdaInt, minutesdaInt, hourdsInt, minutesdsInt);
            }
            // The plane and location objects within flightCopy are still references to the ones from storage.
            // If deeper cloning is needed, those models would need clone methods.

            return new Response("Flight created successfully.", Status.CREATED, flightCopy);

        } catch (Exception ex) {
            // ex.printStackTrace(); // Good for debugging, but remove for production
            return new Response("Unexpected error during flight creation: " + ex.getMessage(), Status.INTERNAL_SERVER_ERROR);
        }
    }

    public static Response addPassengertoFlight(String passengerId, String FlightId) {
        try {
            long idlong;

            if (passengerId.trim().isEmpty() || passengerId == null) {
                return new Response("Id must not be empty", Status.BAD_REQUEST);
            }

            if (passengerId.length() > 15) {
                return new Response("Id must less than 15 digits", Status.BAD_REQUEST);
            }

            try {
                idlong = Long.parseLong(passengerId);
                if (idlong < 0) {
                    return new Response("Id must be positive", Status.BAD_REQUEST);
                }
            } catch (NumberFormatException ex) {
                return new Response("Id must be numeric", Status.BAD_REQUEST);
            }
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
