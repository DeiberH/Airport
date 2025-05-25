/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package airport.controller;

import airport.controller.utils.Response;
import airport.controller.utils.Status;
import airport.controller.validation.PassengerValidation;
import airport.controller.builder.PassengerBuilder;
import airport.model.Flight;
import airport.model.Passenger;
import airport.model.storage.StoragePassenger;
import java.time.LocalDate;
import java.time.DateTimeException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Derby42
 */
public class PassengerController {

    public static Response createPassenger(String id, String firstname, String lastname, String year, String month, String day, String phoneCode, String phone, String country) {
        try {
            String Error = PassengerValidation.validatePassengerData(id, firstname, lastname, year, month, day, phoneCode, phone, country);
            if (Error != null) {
                return new Response(Error, Status.BAD_REQUEST);
            }

            long idLong = Long.parseLong(id);
            int phoneCodeInt = Integer.parseInt(phoneCode);
            long phoneLong = Long.parseLong(phone);
            int yearInt = Integer.parseInt(year);
            int monthInt = Integer.parseInt(month);
            int dayInt = Integer.parseInt(day);
            LocalDate birthDate = LocalDate.of(yearInt, monthInt, dayInt);

            Passenger passenger = PassengerBuilder.build(idLong, firstname, lastname, birthDate, phoneCodeInt, phoneLong, country);

            StoragePassenger storage = StoragePassenger.getInstance();
            if (!storage.addPassenger(passenger)) {
                return new Response("A Passenger with that id already exists", Status.BAD_REQUEST);
            }
            return new Response("Passenger created successfully", Status.CREATED);
        } catch (Exception ex) {
            return new Response("Unexpected error", Status.INTERNAL_SERVER_ERROR);
        }
    }

    public static Response updatePassenger(String id, String firstname, String lastname, String year, String month, String day, String phoneCode, String phone, String country) {
        try {
            if (id == null || id.trim().isEmpty()) {
                return new Response("User hasn't been selected yet", Status.NOT_FOUND);
            }

            long idLong = Long.parseLong(id);
            StoragePassenger storage = StoragePassenger.getInstance();
            Passenger passenger = storage.getPassenger(idLong);
            if (passenger == null) {
                return new Response("Passenger not found", Status.NOT_FOUND);
            }

            // Validación delegada
            String Error = PassengerValidation.validatePassengerData(id, firstname, lastname, year, month, day, phoneCode, phone, country);
            if (Error != null) {
                return new Response(Error, Status.BAD_REQUEST);
            }

            // Conversión de datos
            int yearInt = Integer.parseInt(year);
            int monthInt = Integer.parseInt(month);
            int dayInt = Integer.parseInt(day);
            LocalDate birthDate = LocalDate.of(yearInt, monthInt, dayInt);
            int phoneCodeInt = Integer.parseInt(phoneCode);
            long phoneLong = Long.parseLong(phone);

            // Actualización
            passenger.setFirstname(firstname);
            passenger.setLastname(lastname);
            passenger.setBirthDate(birthDate);
            passenger.setCountryPhoneCode(phoneCodeInt);
            passenger.setPhone(phoneLong);
            passenger.setCountry(country);

            return new Response("Passenger updated successfully", Status.CREATED);

        } catch (Exception ex) {
            return new Response("Unexpected error", Status.INTERNAL_SERVER_ERROR);
        }
    }

    public static Response getAllPassengersForTable() {
        try {
            List<Passenger> passengers = StoragePassenger.getInstance().getAllPassengers();
            // The getAllPassengers method in storage should already return a sorted copy.
            if (passengers.isEmpty()) {
                // It's okay to return an empty list; the view can handle it.
                return new Response("No passengers found.", Status.OK, new ArrayList<Passenger>());
            }
            return new Response("Passengers retrieved successfully.", Status.OK, passengers);
        } catch (Exception ex) {
            // ex.printStackTrace();
            return new Response("Error retrieving passengers: " + ex.getMessage(), Status.INTERNAL_SERVER_ERROR);
        }
    }

    public static Response getFlightsForPassengerTable(String passengerId) {
        try {
            if (passengerId == null || passengerId.trim().isEmpty()) {
                return new Response("Passenger ID must not be empty.", Status.BAD_REQUEST);
            }
            long idLong;
            try {
                idLong = Long.parseLong(passengerId);
            } catch (NumberFormatException e) {
                return new Response("Passenger ID must be a numeric value.", Status.BAD_REQUEST);
            }

            Passenger passenger = StoragePassenger.getInstance().getPassenger(idLong);
            if (passenger == null) {
                return new Response("Passenger not found with ID: " + passengerId, Status.NOT_FOUND);
            }
            List<Flight> flights = passenger.getFlights2(); // This method in Passenger should return a sorted copy.
            if (flights.isEmpty()) {
                return new Response("No flights found for this passenger.", Status.OK, new ArrayList<Flight>());
            }
            return new Response("Passenger flights retrieved successfully.", Status.OK, flights);
        } catch (Exception ex) {
            // ex.printStackTrace();
            return new Response("Error retrieving passenger flights: " + ex.getMessage(), Status.INTERNAL_SERVER_ERROR);
        }
    }
}
