/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package airport.controller;

import airport.controller.builder.LocationBuilder;
import airport.controller.utils.Response;
import airport.controller.utils.Status;
import airport.controller.validation.LocationValidation;
import airport.model.Location;
import airport.model.storage.StorageLocation;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Derby42
 */
public class LocationController {

    public static Response createLocation(String id, String name, String city, String country, String latitude, String longitude) {
        try {
            String Error = LocationValidation.validateLocationData(id, name, city, country, latitude, longitude);
            if (Error != null) {
                return new Response(Error, Status.BAD_REQUEST);
            }
            
            double latDob = Double.parseDouble(latitude);
            double lonDob = Double.parseDouble(longitude);
            
            Location location = LocationBuilder.build(id, name, city, country, latDob, lonDob);
            
            StorageLocation storage = StorageLocation.getInstance();
            if (!storage.addLocation(location)) {
                return new Response("A location with that id already exists", Status.BAD_REQUEST);
            }
            return new Response("Location created successfully", Status.CREATED);
        } catch (Exception ex) {
            return new Response("Unexpected error", Status.INTERNAL_SERVER_ERROR);
        }
    }

    public static Response getAllLocationsForTable() {
        try {
            List<Location> locations = StorageLocation.getInstance().getAllLocations();
            // Storage should return sorted copy
            if (locations.isEmpty()) {
                return new Response("No locations found.", Status.OK, new ArrayList<Location>());
            }
            return new Response("Locations retrieved successfully.", Status.OK, locations);
        } catch (Exception ex) {
            // ex.printStackTrace();
            return new Response("Error retrieving locations: " + ex.getMessage(), Status.INTERNAL_SERVER_ERROR);
        }
    }
}
