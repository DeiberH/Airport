package airport.controller;

import airport.controller.utils.Response;
import airport.controller.utils.Status;
import airport.model.Location;
import airport.controller.interfaces.ILocationRepository;
import airport.controller.interfaces.ILocationValidator;
import airport.controller.interfaces.ILocationFactory;

import java.util.ArrayList;
import java.util.List;

public class LocationController {
    private final ILocationRepository locationRepository;
    private final ILocationValidator locationValidator;
    private final ILocationFactory locationFactory;

    public LocationController(ILocationRepository locationRepository,
                              ILocationValidator locationValidator,
                              ILocationFactory locationFactory) {
        this.locationRepository = locationRepository;
        this.locationValidator = locationValidator;
        this.locationFactory = locationFactory;
    }

    public Response createLocation(String idStr, String nameStr, String cityStr, String countryStr, String latitudeStr, String longitudeStr) {
        try {
            String error = locationValidator.validateLocationData(idStr, nameStr, cityStr, countryStr, latitudeStr, longitudeStr);
            if (error != null) {
                return new Response(error, Status.BAD_REQUEST);
            }
            
            String id = idStr.trim(); // Use trimmed id
            String name = nameStr.trim();
            String city = cityStr.trim();
            String country = countryStr.trim();
            double latDob = Double.parseDouble(latitudeStr.trim());
            double lonDob = Double.parseDouble(longitudeStr.trim());
            
            Location newLocation = locationFactory.build(id, name, city, country, latDob, lonDob);
            
            if (!locationRepository.addLocation(newLocation)) {
                return new Response("A location with ID '" + id + "' already exists", Status.BAD_REQUEST);
            }
            
            Location locationCopy = locationFactory.build(newLocation.getAirportId(), newLocation.getAirportName(),
                                                          newLocation.getAirportCity(), newLocation.getAirportCountry(),
                                                          newLocation.getAirportLatitude(), newLocation.getAirportLongitude());
            return new Response("Location created successfully", Status.CREATED, locationCopy);

        } catch (NumberFormatException ex) {
            return new Response("Invalid numeric format for latitude or longitude.", Status.BAD_REQUEST);
        } catch (Exception ex) {
            return new Response("Unexpected error creating location: " + ex.getMessage(), Status.INTERNAL_SERVER_ERROR);
        }
    }

    public Response getAllLocationsForTable() {
        try {
            List<Location> locations = locationRepository.getAllLocations();
            if (locations.isEmpty()) {
                return new Response("No locations found.", Status.OK, new ArrayList<>());
            }
            return new Response("Locations retrieved successfully.", Status.OK, new ArrayList<>(locations));
        } catch (Exception ex) {
            return new Response("Error retrieving locations: " + ex.getMessage(), Status.INTERNAL_SERVER_ERROR);
        }
    }
}