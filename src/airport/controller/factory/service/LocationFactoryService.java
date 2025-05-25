package airport.controller.factory.service;

import airport.controller.builder.LocationBuilder; // Your static builder
import airport.controller.interfaces.ILocationFactory; // Interface from correct package
import airport.model.Location;

public class LocationFactoryService implements ILocationFactory {
    @Override
    public Location build(String id, String name, String city, String country, double latitude, double longitude) {
        return LocationBuilder.build(id, name, city, country, latitude, longitude); //
    }
}