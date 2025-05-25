package airport.model.storage;

import airport.model.Flight;
import airport.model.Plane;
import airport.model.Location;
import airport.controller.interfaces.IFlightRepository;
import airport.controller.interfaces.IPlaneRepository; // For loading
import airport.controller.interfaces.ILocationRepository; // For loading
import airport.utils.file.JsonDataManager;
import airport.utils.observer.Observer;
import airport.utils.observer.Subject;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class StorageFlight implements IFlightRepository, Subject {
    private ArrayList<Flight> flights;
    private final String filePath = "json/flights.json"; // Ensure this path is correct
    private final List<Observer> observers;
    private final IPlaneRepository planeRepository; // Needed for resolving plane objects during load
    private final ILocationRepository locationRepository; // Needed for resolving location objects during load

    public StorageFlight(IPlaneRepository planeRepository, ILocationRepository locationRepository) {
        this.observers = new ArrayList<>();
        this.planeRepository = planeRepository;
        this.locationRepository = locationRepository;
        loadInitialData();
    }
    
    private void loadInitialData(){
        List<Plane> allPlanes = this.planeRepository.getAllPlanes();
        List<Location> allLocations = this.locationRepository.getAllLocations();
        this.flights = new ArrayList<>(JsonDataManager.loadFlights(filePath, allPlanes, allLocations)); //
        JsonDataManager.rebuildFlightPlaneAssociations(this.flights, allPlanes);
        // Potentially save planes again if their flight lists were modified and need to be persisted
        // Or ensure Plane's addFlight doesn't trigger a save if it's part of initial load
    }

    private void saveToDisk() {
        JsonDataManager.saveFlights(filePath, this.flights);
        notifyObservers();
    }

    @Override
    public boolean addFlight(Flight flight) {
        if (this.flights.stream().anyMatch(f -> f.getId().equals(flight.getId()))) {
            return false;
        }
        this.flights.add(flight);
        // The association plane.addFlight(flight) is handled by FlightController after this method succeeds.
        saveToDisk();
        return true;
    }

    @Override
    public Flight getFlight(String id) {
        Optional<Flight> flightOpt = this.flights.stream()
                .filter(f -> f.getId().equals(id))
                .findFirst();
        return flightOpt.orElse(null);
    }

    @Override
    public List<Flight> getAllFlights() {
        ArrayList<Flight> sortedFlights = new ArrayList<>(this.flights);
        sortedFlights.sort(Comparator.comparing(Flight::getDepartureDate));
        return sortedFlights;
    }

    @Override
    public void registerObserver(Observer o) {
        this.observers.add(o);
    }

    @Override
    public void removeObserver(Observer o) {
        this.observers.remove(o);
    }

    @Override
    public void notifyObservers() {
        for (Observer observer : this.observers) {
            observer.update();
        }
    }
}