package airport.model.storage;

import airport.model.Location;
import airport.controller.interfaces.ILocationRepository;
import airport.utils.file.JsonDataManager; // Import
import airport.utils.observer.Observer; // Import
import airport.utils.observer.Subject;   // Import

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class StorageLocation implements ILocationRepository, Subject { // Implement Subject
    private ArrayList<Location> locations; // No longer final if reloaded
    private final String filePath = "json/locations.json"; // Define path, ensure 'data' directory exists

    private final List<Observer> observers; // For Observer pattern

    public StorageLocation() {
        this.observers = new ArrayList<>();
        this.locations = new ArrayList<>(JsonDataManager.loadLocations(filePath)); // Load on init
    }

    private void saveToDisk() {
        JsonDataManager.saveLocations(filePath, this.locations);
        notifyObservers(); // Notify after saving
    }

    @Override
    public boolean addLocation(Location locationToAdd) {
        if (locations.stream().anyMatch(loc -> loc.getAirportId().equals(locationToAdd.getAirportId()))) {
            return false; 
        }
        this.locations.add(locationToAdd);
        saveToDisk();
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

    // Subject methods
    @Override
    public void registerObserver(Observer o) {
        observers.add(o);
    }

    @Override
    public void removeObserver(Observer o) {
        observers.remove(o);
    }

    @Override
    public void notifyObservers() {
        for (Observer observer : observers) {
            observer.update();
        }
    }
}