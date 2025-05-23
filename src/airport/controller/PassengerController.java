/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package airport.controller;

import airport.controller.utils.Response;
import airport.controller.utils.Status;
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
            long idlong, phoneLong;
            int yearInt, monthInt, dayInt, pcInt;
            LocalDate birthDate, now;

            if (id.trim().isEmpty() || id == null) {
                return new Response("Id must not be empty", Status.BAD_REQUEST);
            }

            if (id.length() > 15) {
                return new Response("Id must less than 15 digits", Status.BAD_REQUEST);
            }

            try {
                idlong = Long.parseLong(id);
                if (idlong < 0) {
                    return new Response("Id must be positive", Status.BAD_REQUEST);
                }
            } catch (NumberFormatException ex) {
                return new Response("Id must be numeric", Status.BAD_REQUEST);
            }

            if (firstname.trim().isEmpty() || firstname == null) {
                return new Response("Firstname must not be empty", Status.BAD_REQUEST);
            }

            if (lastname.trim().isEmpty() || lastname == null) {
                return new Response("Lastname must not be empty", Status.BAD_REQUEST);
            }

            if (year.trim().isEmpty() || year == null) {
                return new Response("Lastname must not be empty", Status.BAD_REQUEST);
            }

            try {
                yearInt = Integer.parseInt(year);
                if (yearInt < 1900) {
                    return new Response("Year must be valid [>=1900]", Status.BAD_REQUEST);
                }
            } catch (NumberFormatException ex) {
                return new Response("Year must be numeric", Status.BAD_REQUEST);
            }

            try {
                monthInt = Integer.parseInt(month);
                dayInt = Integer.parseInt(day);
                now = LocalDate.now();
                birthDate = LocalDate.of(yearInt, monthInt, dayInt);
                if (birthDate.isAfter(now)) {
                    return new Response("Date must be Before Today", Status.BAD_REQUEST);
                }
            } catch (DateTimeException ex) {
                return new Response("Date Format is not Valid", Status.BAD_REQUEST);
            }

            if (phoneCode.trim().isEmpty() || phoneCode == null) {
                return new Response("PhoneCode must not be empty", Status.BAD_REQUEST);
            }

            if (phoneCode.length() > 3) {
                return new Response("Phonecode must be 3 digits or less", Status.BAD_REQUEST);
            }

            try {
                pcInt = Integer.parseInt(phoneCode);
                if (pcInt < 0) {
                    return new Response("Phonecode must be positive", Status.BAD_REQUEST);
                }
            } catch (NumberFormatException ex) {
                return new Response("Phonecode must be numeric", Status.BAD_REQUEST);
            }

            if (phone.trim().isEmpty() || phone == null) {
                return new Response("Phone must not be empty", Status.BAD_REQUEST);
            }

            if (phone.length() > 11) {
                return new Response("Phone must be 11 digits or less", Status.BAD_REQUEST);
            }

            try {
                phoneLong = Long.parseLong(phoneCode);
                if (phoneLong < 0) {
                    return new Response("Phone must be positive", Status.BAD_REQUEST);
                }
            } catch (NumberFormatException ex) {
                return new Response("Phone must be numeric", Status.BAD_REQUEST);
            }

            if (country.trim().isEmpty() || country == null) {
                return new Response("Country must not be empty", Status.BAD_REQUEST);
            }

            StoragePassenger storage = StoragePassenger.getInstance();
            if (!storage.addPassenger(new Passenger(idlong, firstname, lastname, birthDate, pcInt, phoneLong, country))) {
                return new Response("A Passenger with that id already exists", Status.BAD_REQUEST);
            }
            return new Response("Passenger created successfully", Status.CREATED);
        } catch (Exception ex) {
            return new Response("Unexpected error", Status.INTERNAL_SERVER_ERROR);
        }
    }

    public static Response updatePassenger(String id, String firstname, String lastname, String year, String month, String day, String phoneCode, String phone, String country) {
        try {
            long idlong;
            int yearInt, monthInt, dayInt, pcInt, phoneInt;
            LocalDate birthDate, now;

            if (id.trim().isEmpty() || id == null) {
                return new Response("User Hasn't been selected yet", Status.NOT_FOUND);
            }

            StoragePassenger storage = StoragePassenger.getInstance();
            idlong = Long.parseLong(id);
            Passenger passenger = storage.getPassenger(idlong);
            if (passenger == null) {
                return new Response("Person not found", Status.NOT_FOUND);
            }

            if (firstname.trim().isEmpty() || firstname == null) {
                return new Response("Firstname must not be empty", Status.BAD_REQUEST);
            }

            if (lastname.trim().isEmpty() || lastname == null) {
                return new Response("Lastname must not be empty", Status.BAD_REQUEST);
            }

            if (year.trim().isEmpty() || year == null) {
                return new Response("Lastname must not be empty", Status.BAD_REQUEST);
            }

            try {
                yearInt = Integer.parseInt(year);
                if (yearInt < 1900) {
                    return new Response("Year must be valid [>=1900]", Status.BAD_REQUEST);
                }
            } catch (NumberFormatException ex) {
                return new Response("Year must be numeric", Status.BAD_REQUEST);
            }

            try {
                monthInt = Integer.parseInt(month);
                dayInt = Integer.parseInt(day);
                now = LocalDate.now();
                birthDate = LocalDate.of(yearInt, monthInt, dayInt);
                if (birthDate.isAfter(now)) {
                    return new Response("Date must be Before Today", Status.BAD_REQUEST);
                }
            } catch (DateTimeException ex) {
                return new Response("Date Format is not Valid", Status.BAD_REQUEST);
            }

            if (phoneCode.trim().isEmpty() || phoneCode == null) {
                return new Response("PhoneCode must not be empty", Status.BAD_REQUEST);
            }

            if (phoneCode.length() > 3) {
                return new Response("Phonecode must be 3 digits or less", Status.BAD_REQUEST);
            }

            try {
                pcInt = Integer.parseInt(phoneCode);
                if (pcInt < 0) {
                    return new Response("Phonecode must be positive", Status.BAD_REQUEST);
                }
            } catch (NumberFormatException ex) {
                return new Response("Phonecode must be numeric", Status.BAD_REQUEST);
            }

            if (phone.trim().isEmpty() || phone == null) {
                return new Response("Phone must not be empty", Status.BAD_REQUEST);
            }

            if (phone.length() > 11) {
                return new Response("Phone must be 11 digits or less", Status.BAD_REQUEST);
            }

            try {
                phoneInt = Integer.parseInt(phoneCode);
                if (phoneInt < 0) {
                    return new Response("Phone must be positive", Status.BAD_REQUEST);
                }
            } catch (NumberFormatException ex) {
                return new Response("Phone must be numeric", Status.BAD_REQUEST);
            }

            if (country.trim().isEmpty() || country == null) {
                return new Response("Country must not be empty", Status.BAD_REQUEST);
            }
            //  id,  firstname,  lastname,  year,  month,  day,  phoneCode,  phone,  country
            passenger.setFirstname(firstname);
            passenger.setLastname(lastname);
            passenger.setBirthDate(birthDate);
            passenger.setCountryPhoneCode(phoneInt);
            passenger.setPhone(idlong);
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
