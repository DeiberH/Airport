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

/**
 *
 * @author Derby42
 */
// CORREGIR LOS ERRORES DE LOS PARSEINT DEL TIEMPO
public class FlightController {

    public static Response createPassenger(String id, String planeId, String departureLocationId, String arrivalLocationId, String scaleLocationId, String year, String month, String day, String hour, String minutes, String hoursDurationsArrival, String minutesDurationsArrival, String hoursDurationsScale, String minutesDurationsScale) {
        try {
            int yearInt, monthInt, dayInt, hoursInt, minutesInt, hourdaInt, minutesdaInt, hourdsInt, minutesdsInt;
            LocalDateTime departureDate, now;
            if (id.trim().isEmpty() || id == null) {
                return new Response("Id must not be empty", Status.BAD_REQUEST);
            }

            if (id.length() != 6) {
                return new Response("Id must have a length of 6 digits", Status.BAD_REQUEST);
            }

            char c1 = id.charAt(0);
            char c2 = id.charAt(1);
            char c3 = id.charAt(2);
            if (!Character.isUpperCase(c1) || !Character.isUpperCase(c2) || !Character.isUpperCase(c3)) {
                return new Response("Id must not contain LowerCase letters or digits on the first 3 characters XXXYYY", Status.BAD_REQUEST);
            }

            for (int i = 3; i < 6; i++) {
                if (!Character.isDigit(id.charAt(i))) {
                    return new Response("Id must have 3 numbers XXXYYY", Status.BAD_REQUEST);
                }
            }

            StoragePlane storagep = StoragePlane.getInstance();
            Plane plane = storagep.getPlane(planeId);
            if (plane == null) {
                return new Response("The plane Inserted is not valid", Status.BAD_REQUEST);
            }

            if (year.trim().isEmpty() || year == null) {
                return new Response("Year must not be empty", Status.BAD_REQUEST);
            }

            try {
                yearInt = Integer.parseInt(year);
                if (yearInt < LocalDateTime.now().getYear()) {
                    return new Response("Year must be valid [>=2025]", Status.BAD_REQUEST);
                }
            } catch (NumberFormatException ex) {
                return new Response("Year must be numeric", Status.BAD_REQUEST);
            }

            try {
                monthInt = Integer.parseInt(month);
                dayInt = Integer.parseInt(day);
                hoursInt = Integer.parseInt(hour);
                minutesInt = Integer.parseInt(minutes);
                now = LocalDateTime.now();
                departureDate = LocalDateTime.of(yearInt, monthInt, dayInt, hoursInt, minutesInt);
                if (departureDate.isBefore(now)) {
                    return new Response("Date must be After Today", Status.BAD_REQUEST);
                }
            } catch (DateTimeException ex) {
                return new Response("Date Format is not Valid", Status.BAD_REQUEST);
            }

            StorageLocation storagel = StorageLocation.getInstance();
            Location dLocation = storagel.getLocation(departureLocationId);
            Location aLocation = storagel.getLocation(arrivalLocationId);
            Location sLocation = storagel.getLocation(scaleLocationId);
            if (dLocation == null) {
                return new Response("There must be a Departure Location", Status.BAD_REQUEST);
            }

            if (aLocation == null) {
                return new Response("There must be an Arrival Location", Status.BAD_REQUEST);
            }

            hourdaInt = Integer.parseInt(hoursDurationsArrival);
            minutesdaInt = Integer.parseInt(minutesDurationsArrival);
            if (hourdaInt == 0 && minutesdaInt == 0) {
                return new Response("Duration time must not be 0", Status.BAD_REQUEST);
            }

            StorageFlight storage = StorageFlight.getInstance();

            if (sLocation == null) {
                hourdsInt = Integer.parseInt(hoursDurationsScale);
                minutesdsInt = Integer.parseInt(minutesDurationsScale);
                if (hourdsInt != 0 || minutesdsInt != 0) {
                    return new Response("If scale isn't selected duration time of scale must be 0", Status.BAD_REQUEST);
                }
                if (!storage.addFlight(new Flight(id, plane, dLocation, sLocation, aLocation, departureDate, hourdaInt, minutesdaInt, hourdsInt, minutesdsInt))) {
                    return new Response("A Flight with that id already exists", Status.BAD_REQUEST);
                }
                return new Response("Flight created successfully", Status.CREATED);
            }
            if (!storage.addFlight(new Flight(id, plane, aLocation, aLocation, departureDate, hourdaInt, minutesdaInt))) {
                return new Response("A Flight with that id already exists", Status.BAD_REQUEST);
            }
            return new Response("Flight created successfully", Status.CREATED);
        } catch (Exception ex) {
            return new Response("Unexpected error", Status.INTERNAL_SERVER_ERROR);
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
}
