/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package airport.controller;

import airport.controller.utils.Response;
import airport.controller.utils.Status;
import airport.model.Passenger;
import airport.model.storage.Storage;
import java.time.LocalDate;
import java.time.DateTimeException;

/**
 *
 * @author Derby42
 */
public class PassengerController {

    public static Response createPassenger(String id, String firstname, String lastname, String year, String month, String day, String phoneCode, String phone, String country) {
        try {
            long idlong, phoneLong;
            int yearInt, monthInt, dayInt, pcInt;
            LocalDate birthDate;

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
                monthInt = Integer.parseInt(month);
                dayInt = Integer.parseInt(day);
                if (yearInt < 1900) {
                    return new Response("Year must be valid [>=1900]", Status.BAD_REQUEST);
                }
            } catch (NumberFormatException ex) {
                return new Response("Year must be numeric", Status.BAD_REQUEST);
            }

            try {
                birthDate = LocalDate.of(yearInt, monthInt, dayInt);
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
           
            Storage storage = Storage.getInstance();
            if (!storage.addPassenger(new Passenger(idlong, firstname, lastname, birthDate, pcInt, phoneLong, country))) {
                return new Response("A Passenger with that id already exists", Status.BAD_REQUEST);
            }
            return new Response("Passenger created successfully", Status.CREATED);
        } catch (Exception ex) {
            return new Response("Unexpected error", Status.INTERNAL_SERVER_ERROR);
        }
    }
}
