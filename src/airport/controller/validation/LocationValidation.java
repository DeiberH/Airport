/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package airport.controller.validation;

import airport.controller.utils.Response;
import airport.controller.utils.Status;
import java.math.BigDecimal;

/**
 *
 * @author Derby42
 */
public class LocationValidation {
    public static String validateLocationData(String id, String name, String city, String country, String latitude, String longitude) {

        if (id.trim().isEmpty() || id == null) {
            return "Id must not be empty";
        }

        if (id.length() != 3) {
            return "Id must contain 3 uppercase letters";
        }

        for (int i = 0; i < 3; i++) {
            char c = id.charAt(i);
            if (!Character.isUpperCase(c)) {
                return "Id must contain only uppercase letters";
            }
        }

        if (name.trim().isEmpty() || name == null) {
            return "Name must not be empty";
        }

        if (city.trim().isEmpty() || city == null) {
            return "City must not be empty";
        }

        if (country.trim().isEmpty() || country == null) {
            return "Country must not be empty";
        }

        if (latitude.trim().isEmpty() || latitude == null) {
            return "Country must not be empty";
        }

        try {
            BigDecimal lat = new BigDecimal(latitude);
            double latDob = Double.parseDouble(latitude);
            if (lat.scale() > 4) {
                return "Latitude must have less than 4 decimals";
            }
            if (latDob > 90 || latDob < -90) {
                return "Latitude must be beetween [-90,90]";
            }
        } catch (NumberFormatException ex) {
            return "Latitude must be numeric";
        }

        if (longitude.trim().isEmpty() || longitude == null) {
            return "Longitude must not be empty";
        }

        try {
            BigDecimal lon = new BigDecimal(longitude);
            double lonDob = Double.parseDouble(longitude);
            if (lon.scale() > 4) {
                return "Longitude must have less than 4 decimals";
            }
            if (lonDob > 180 && lonDob < -180) {
                return "Longitude must be beetween [-180,180]";
            }
        } catch (NumberFormatException ex) {
            return "Longitude must be numeric";
        }

        return null;
    }
}
