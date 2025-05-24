/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package airport.model.storage;

import airport.model.Location;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 *
 * @author Juan Sebastian
 */
public class StorageLocation {

    private static StorageLocation instance;
    private ArrayList<Location> locations;

    public static StorageLocation getInstance() {
        if (instance == null) {
            instance = new StorageLocation();
        }
        return instance;
    }

    private StorageLocation() {
        this.locations = new ArrayList<>();
    }

    public boolean addLocation(Location local) {
        for (Location l : this.locations) {
            if (l.getAirportId().equals(local.getAirportId())) {
                return false;
            }
        }
        this.locations.add(local);
        return true;
    }

    public Location getLocation(String id) {
        for (Location location : this.locations) {
            if (location.getAirportId().equals(id)) {
                return location;
            }
        }
        return null;
    }

    public List<Location> getAllLocations() {
        // Sort by ID: "Los aeropuertos (localizaciones) se deben obtener de manera ordenada (respecto a su id)."
        ArrayList<Location> sortedLocations = new ArrayList<>(this.locations);
        sortedLocations.sort(Comparator.comparing(Location::getAirportId));
        return sortedLocations; // Return the sorted copy
    }
}
