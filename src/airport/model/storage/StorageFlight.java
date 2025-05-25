package airport.model.storage;

import airport.model.Flight;
import airport.model.Plane;
import airport.model.Location;
import airport.controller.interfaces.IFlightRepository;
import airport.controller.interfaces.IPlaneRepository;
import airport.controller.interfaces.ILocationRepository;
import airport.utils.file.JsonDataManager;
import airport.utils.observer.Observer;
import airport.utils.observer.Subject;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class StorageFlight implements IFlightRepository, Subject {
    private ArrayList<Flight> flights;
    private final String filePath = "json/flights.json";
    private final List<Observer> observers;
    private final IPlaneRepository planeRepository; 
    private final ILocationRepository locationRepository; 

    public StorageFlight(IPlaneRepository planeRepository, ILocationRepository locationRepository) {
        this.observers = new ArrayList<>();
        this.planeRepository = planeRepository;
        this.locationRepository = locationRepository;
        loadInitialData();
    }
    
    private void loadInitialData(){
        List<Plane> allPlanes = this.planeRepository.getAllPlanes();
        List<Location> allLocations = this.locationRepository.getAllLocations();
        this.flights = new ArrayList<>(JsonDataManager.loadFlights(filePath, allPlanes, allLocations));
        JsonDataManager.rebuildFlightPlaneAssociations(this.flights, allPlanes);
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