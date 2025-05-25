/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package airport.controller.validation;

import java.time.DateTimeException;
import java.time.LocalDate;

/**
 *
 * @author Derby42
 */
public class PassengerValidation {

    public static String validatePassengerData(String id, String firstname, String lastname, String year, String month, String day, String phoneCode, String phone, String country) {
        if (id == null || id.trim().isEmpty()) {
            return "Id must not be empty";
        }
        if (id.length() > 15) {
            return "Id must be less than 15 digits";
        }
        try {
            long idLong = Long.parseLong(id);
            if (idLong < 0) {
                return "Id must be positive";
            }
        } catch (NumberFormatException e) {
            return "Id must be numeric";
        }

        if (firstname == null || firstname.trim().isEmpty()) {
            return "Firstname must not be empty";
        }
        if (lastname == null || lastname.trim().isEmpty()) {
            return "Lastname must not be empty";
        }

        if (year == null || year.trim().isEmpty()) {
            return "Year must not be empty";
        }
        try {
            int y = Integer.parseInt(year);
            if (y < 1900) {
                return "Year must be valid [>=1900]";
            }
        } catch (NumberFormatException e) {
            return "Year must be numeric";
        }

        try {
            int y = Integer.parseInt(year);
            int m = Integer.parseInt(month);
            int d = Integer.parseInt(day);
            LocalDate birthDate = LocalDate.of(y, m, d);
            if (birthDate.isAfter(LocalDate.now())) {
                return "Date must be before today";
            }
        } catch (DateTimeException | NumberFormatException e) {
            return "Invalid date format";
        }

        if (phoneCode == null || phoneCode.trim().isEmpty()) {
            return "PhoneCode must not be empty";
        }
        if (phoneCode.length() > 3) {
            return "Phonecode must be 3 digits or less";
        }
        try {
            int pc = Integer.parseInt(phoneCode);
            if (pc < 0) {
                return "Phonecode must be positive";
            }
        } catch (NumberFormatException e) {
            return "Phonecode must be numeric";
        }

        if (phone == null || phone.trim().isEmpty()) {
            return "Phone must not be empty";
        }
        if (phone.length() > 11) {
            return "Phone must be 11 digits or less";
        }
        try {
            long ph = Long.parseLong(phone);
            if (ph < 0) {
                return "Phone must be positive";
            }
        } catch (NumberFormatException e) {
            return "Phone must be numeric";
        }

        if (country == null || country.trim().isEmpty()) {
            return "Country must not be empty";
        }

        return null;
    }
}
