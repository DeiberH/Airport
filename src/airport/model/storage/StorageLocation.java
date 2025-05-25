package airport.model.storage;

import airport.model.Location;
import airport.controller.interfaces.ILocationRepository;
import airport.utils.file.JsonDataManager; // Asegúrate que este import sea correcto
import airport.utils.observer.Observer;   // Asegúrate que este import sea correcto
import airport.utils.observer.Subject;    // Asegúrate que este import sea correcto

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class StorageLocation implements ILocationRepository, Subject {
    private ArrayList<Location> locations;
    private final String filePath = "json/locations.json"; // Confirma que esta ruta es correcta
    private final List<Observer> observers;

    public StorageLocation() {
        this.observers = new ArrayList<>();
        this.locations = new ArrayList<>(JsonDataManager.loadLocations(filePath)); //
    }

    private void saveToDisk() {
        JsonDataManager.saveLocations(filePath, this.locations);
        notifyObservers();
    }

    @Override
    public boolean addLocation(Location locationToAdd) {
        if (this.locations.stream().anyMatch(loc -> loc.getAirportId().equals(locationToAdd.getAirportId()))) {
            return false; 
        }
        this.locations.add(locationToAdd);
        saveToDisk();
        return true;
    }

    @Override
    public Location getLocation(String id) {
        Optional<Location> locationOpt = this.locations.stream()
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

    @Override
    public void registerObserver(Observer o) {
        if (o != null && !this.observers.contains(o)) {
            this.observers.add(o);
        }
    }

    @Override
    public void removeObserver(Observer o) {
        this.observers.remove(o);
    }

    @Override
    public void notifyObservers() {
        List<Observer> observersCopy = new ArrayList<>(this.observers);
        for (Observer observer : observersCopy) {
            observer.update();
        }
    }
}