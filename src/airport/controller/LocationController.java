/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package airport.controller;

import airport.controller.utils.Response;
import airport.controller.utils.Status;
import airport.model.Location;
import airport.model.storage.Storage;
import java.math.BigDecimal;

/**
 *
 * @author Derby42
 */
public class LocationController {

    public static Response createLocation(String id, String name, String city, String country, String latitude, String longitude) {
        try {
            double latDob, lonDob;

            if (id.trim().isEmpty() || id == null) {
                return new Response("Id must not be empty", Status.BAD_REQUEST);
            }

            if (id.length() != 3) {
                return new Response("Id must contain 3 uppercase letters", Status.BAD_REQUEST);
            }
                       
            for (int i = 0; i < 3; i++) {
                char c = id.charAt(i);
                if (!Character.isUpperCase(c)) {
                    return new Response("Id must contain only uppercase letters", Status.BAD_REQUEST);
                }
            }

            if (name.trim().isEmpty() || name == null) {
                return new Response("Name must not be empty", Status.BAD_REQUEST);
            }

            if (city.trim().isEmpty() || city == null) {
                return new Response("City must not be empty", Status.BAD_REQUEST);
            }
            
            if (country.trim().isEmpty() || country == null) {
                return new Response("Country must not be empty", Status.BAD_REQUEST);
            }
            
            if (latitude.trim().isEmpty() || latitude == null) {
                return new Response("Country must not be empty", Status.BAD_REQUEST);
            }

            try {
                BigDecimal lat = new BigDecimal(latitude);
                latDob = Double.parseDouble(latitude);
                if (lat.scale() > 4){
                    return new Response("Latitude must have less than 4 decimals", Status.BAD_REQUEST);
                }
                if (latDob > 90 || latDob < -90) {
                    return new Response("Latitude must be beetween [-90,90]", Status.BAD_REQUEST);
                }
            } catch (NumberFormatException ex) {
                return new Response("Latitude must be numeric", Status.BAD_REQUEST);
            }
            
            if (longitude.trim().isEmpty() || longitude == null) {
                return new Response("Longitude must not be empty", Status.BAD_REQUEST);
            }
            
            try {
                BigDecimal lon = new BigDecimal(longitude);
                lonDob = Double.parseDouble(longitude);
                if (lon.scale() > 4){
                    return new Response("Longitude must have less than 4 decimals", Status.BAD_REQUEST);
                }
                if (lonDob > 180 && lonDob < -180) {
                    return new Response("Longitude must be beetween [-180,180]", Status.BAD_REQUEST);
                }
            } catch (NumberFormatException ex) {
                return new Response("Longitude must be numeric", Status.BAD_REQUEST);
            }

            Storage storage = Storage.getInstance();
            if (!storage.addLocation(new Location(id, name, city, country, latDob, lonDob))) {
                return new Response("A location with that id already exists", Status.BAD_REQUEST);
            }
            return new Response("Location created successfully", Status.CREATED);
        } catch (Exception ex) {
            return new Response("Unexpected error", Status.INTERNAL_SERVER_ERROR);
        }
    }
}
