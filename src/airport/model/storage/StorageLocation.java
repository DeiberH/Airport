package airport.model.storage;

import airport.model.Location;
import airport.controller.interfaces.ILocationRepository; // Corrected package for interface
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class StorageLocation implements ILocationRepository {
    private final ArrayList<Location> locations;

    public StorageLocation() {
        this.locations = new ArrayList<>();
    }

    @Override
    public boolean addLocation(Location locationToAdd) {
        if (locations.stream().anyMatch(loc -> loc.getAirportId().equals(locationToAdd.getAirportId()))) {
            return false; 
        }
        this.locations.add(locationToAdd);
        return true;
    }

    @Override
    public Location getLocation(String id) {
        Optional<Location> locationOpt = locations.stream()
                                              .filter(loc -> loc.getAirportId().equals(id))
                                              .findFirst();
        return locationOpt.orElse(null);
    }

    @Override
    public List<Location> getAllLocations() {
        ArrayList<Location> sortedLocations = new ArrayList<>(this.locations);
        sortedLocations.sort(Comparator.comparing(Location::getAirportId));
        return sortedLocations;
    }
}