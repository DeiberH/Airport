package airport.model.storage;

import airport.model.Flight;
import airport.controller.interfaces.IFlightRepository; // Corrected package for interface
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class StorageFlight implements IFlightRepository {
    private final ArrayList<Flight> flights;

    public StorageFlight() {
        this.flights = new ArrayList<>();
    }

    @Override
    public boolean addFlight(Flight flight) {
        if (flights.stream().anyMatch(f -> f.getId().equals(flight.getId()))) {
            return false;
        }
        this.flights.add(flight);
        return true;
    }

    @Override
    public Flight getFlight(String id) {
        Optional<Flight> flightOpt = flights.stream()
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
}