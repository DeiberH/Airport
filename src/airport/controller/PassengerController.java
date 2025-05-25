package airport.controller;

import airport.controller.utils.Response;
import airport.controller.utils.Status;
import airport.model.Flight;
import airport.model.Passenger;
import airport.controller.interfaces.IPassengerRepository;
import airport.controller.interfaces.IPassengerValidator;
import airport.controller.interfaces.IPassengerFactory;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class PassengerController {
    private final IPassengerRepository passengerRepository;
    private final IPassengerValidator passengerValidator;
    private final IPassengerFactory passengerFactory;

    public PassengerController(IPassengerRepository passengerRepository,
                               IPassengerValidator passengerValidator,
                               IPassengerFactory passengerFactory) {
        this.passengerRepository = passengerRepository;
        this.passengerValidator = passengerValidator;
        this.passengerFactory = passengerFactory;
    }

    public Response createPassenger(String idStr, String firstnameStr, String lastnameStr, String yearStr, String monthStr, String dayStr, String phoneCodeStr, String phoneStr, String countryStr) {
        try {
            String error = passengerValidator.validatePassengerData(idStr, firstnameStr, lastnameStr, yearStr, monthStr, dayStr, phoneCodeStr, phoneStr, countryStr);
            if (error != null) {
                return new Response(error, Status.BAD_REQUEST);
            }

            long idLong = Long.parseLong(idStr.trim());
            String firstname = firstnameStr.trim();
            String lastname = lastnameStr.trim();
            int yearInt = Integer.parseInt(yearStr.trim());
            int monthInt = Integer.parseInt(monthStr.trim());
            int dayInt = Integer.parseInt(dayStr.trim());
            LocalDate birthDate = LocalDate.of(yearInt, monthInt, dayInt);
            int phoneCodeInt = Integer.parseInt(phoneCodeStr.trim());
            long phoneLong = Long.parseLong(phoneStr.trim());
            String country = countryStr.trim();

            Passenger newPassenger = passengerFactory.build(idLong, firstname, lastname, birthDate, phoneCodeInt, phoneLong, country);

            if (!passengerRepository.addPassenger(newPassenger)) {
                return new Response("A Passenger with ID '" + idLong + "' already exists", Status.BAD_REQUEST);
            }
            
            Passenger passengerCopy = passengerFactory.build(newPassenger.getId(), newPassenger.getFirstname(), newPassenger.getLastname(),
                                                              newPassenger.getBirthDate(), newPassenger.getCountryPhoneCode(),
                                                              newPassenger.getPhone(), newPassenger.getCountry());
            return new Response("Passenger created successfully", Status.CREATED, passengerCopy);

        } catch (NumberFormatException | java.time.DateTimeException ex) {
            return new Response("Invalid format for numeric or date fields.", Status.BAD_REQUEST);
        } catch (Exception ex) {
            return new Response("Unexpected error creating passenger: " + ex.getMessage(), Status.INTERNAL_SERVER_ERROR);
        }
    }

    public Response updatePassenger(String idStr, String firstnameStr, String lastnameStr, String yearStr, String monthStr, String dayStr, String phoneCodeStr, String phoneStr, String countryStr) {
        try {
            if (idStr == null || idStr.trim().isEmpty()) { // Basic ID check before parsing
                return new Response("Passenger ID for update must not be empty.", Status.BAD_REQUEST);
            }
            long idLong = Long.parseLong(idStr.trim());
            
            Passenger passenger = passengerRepository.getPassenger(idLong);
            if (passenger == null) {
                return new Response("Passenger with ID '" + idLong + "' not found for update.", Status.NOT_FOUND);
            }

            // Validate *new* data. Note: validatePassengerData might re-check ID, which is fine.
            String error = passengerValidator.validatePassengerData(idStr, firstnameStr, lastnameStr, yearStr, monthStr, dayStr, phoneCodeStr, phoneStr, countryStr);
            if (error != null) {
                return new Response(error, Status.BAD_REQUEST);
            }

            String firstname = firstnameStr.trim();
            String lastname = lastnameStr.trim();
            int yearInt = Integer.parseInt(yearStr.trim());
            int monthInt = Integer.parseInt(monthStr.trim());
            int dayInt = Integer.parseInt(dayStr.trim());
            LocalDate birthDate = LocalDate.of(yearInt, monthInt, dayInt);
            int phoneCodeInt = Integer.parseInt(phoneCodeStr.trim());
            long phoneLong = Long.parseLong(phoneStr.trim());
            String country = countryStr.trim();

            passenger.setFirstname(firstname);
            passenger.setLastname(lastname);
            passenger.setBirthDate(birthDate);
            passenger.setCountryPhoneCode(phoneCodeInt);
            passenger.setPhone(phoneLong);
            passenger.setCountry(country);

            passengerRepository.updatePassenger(passenger); // Explicit update call

            Passenger passengerCopy = passengerFactory.build(passenger.getId(), passenger.getFirstname(), passenger.getLastname(),
                                                              passenger.getBirthDate(), passenger.getCountryPhoneCode(),
                                                              passenger.getPhone(), passenger.getCountry());
            return new Response("Passenger updated successfully", Status.OK, passengerCopy);

        } catch (NumberFormatException | java.time.DateTimeException ex) {
            return new Response("Invalid format for numeric or date fields during update.", Status.BAD_REQUEST);
        } catch (Exception ex) {
            return new Response("Unexpected error updating passenger: " + ex.getMessage(), Status.INTERNAL_SERVER_ERROR);
        }
    }

    public Response getAllPassengersForTable() {
        try {
            List<Passenger> passengers = passengerRepository.getAllPassengers();
            if (passengers.isEmpty()) {
                return new Response("No passengers found.", Status.OK, new ArrayList<>());
            }
            return new Response("Passengers retrieved successfully.", Status.OK, new ArrayList<>(passengers));
        } catch (Exception ex) {
            return new Response("Error retrieving passengers: " + ex.getMessage(), Status.INTERNAL_SERVER_ERROR);
        }
    }

    public Response getFlightsForPassengerTable(String passengerIdStr) {
        try {
            if (passengerIdStr == null || passengerIdStr.trim().isEmpty()) {
                return new Response("Passenger ID must not be empty.", Status.BAD_REQUEST);
            }
            long idLong = Long.parseLong(passengerIdStr.trim());

            Passenger passenger = passengerRepository.getPassenger(idLong);
            if (passenger == null) {
                return new Response("Passenger not found with ID: " + idLong, Status.NOT_FOUND);
            }
            List<Flight> flights = passenger.getFlights2(); // Using getFlights2 as per your controller
            if (flights.isEmpty()) {
                return new Response("No flights found for this passenger.", Status.OK, new ArrayList<>());
            }
            return new Response("Passenger flights retrieved successfully.", Status.OK, new ArrayList<>(flights));
        } catch (NumberFormatException e) {
            return new Response("Passenger ID must be a numeric value.", Status.BAD_REQUEST);
        } catch (Exception ex) {
            return new Response("Error retrieving passenger flights: " + ex.getMessage(), Status.INTERNAL_SERVER_ERROR);
        }
    }
}