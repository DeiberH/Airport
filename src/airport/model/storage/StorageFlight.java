/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package airport.model.storage;

import airport.model.Flight;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 *
 * @author Juan Sebastian
 */
public class StorageFlight {

    private static StorageFlight instance;
    private ArrayList<Flight> flights;

    public static StorageFlight getInstance() {
        if (instance == null) {
            instance = new StorageFlight();
        }
        return instance;
    }

    private StorageFlight() {
        this.flights = new ArrayList<>();
    }

    public boolean addFlight(Flight flight) {
        for (Flight f : this.flights) {
            if ((f.getId()).equals(flight.getId())) {
                return false;
            }
        }
        this.flights.add(flight);
        return true;
    }

    public Flight getFlight(String id) {
        for (Flight flight : this.flights) {
            if (flight.getId().equals(id)) {
                return flight;
            }
        }
        return null;
    }

    public ArrayList<Flight> getFlights() {
        return flights;
    }

    public List<Flight> getAllFlights() {
        // Sort by departure date: "Los vuelos se deben obtener de manera ordenada (respecto a su fecha de salida, de los más antiguos a los más nuevos)."
        ArrayList<Flight> sortedFlights = new ArrayList<>(this.flights);
        sortedFlights.sort(Comparator.comparing(Flight::getDepartureDate));
        return sortedFlights; // Return the sorted copy
    }
}
