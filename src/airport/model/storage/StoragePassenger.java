/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package airport.model.storage;

import airport.model.Passenger;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 *
 * @author Juan Sebastian
 */
public class StoragePassenger {

    private static StoragePassenger instance;
    private ArrayList<Passenger> passengers;

    public static StoragePassenger getInstance() {
        if (instance == null) {
            instance = new StoragePassenger();
        }
        return instance;
    }

    private StoragePassenger() {
        this.passengers = new ArrayList<>();
    }

    public boolean addPassenger(Passenger passenger) {
        for (Passenger p : this.passengers) {
            if (p.getId() == passenger.getId()) {
                return false;
            }
        }
        this.passengers.add(passenger);
        return true;
    }

    public Passenger getPassenger(long id) {
        for (Passenger passenger : this.passengers) {
            if (passenger.getId() == id) {
                return passenger;
            }
        }
        return null;
    }

    public List<Passenger> getAllPassengers() {
        // Sort by ID as per requirements: "Los pasajeros se deben obtener de manera ordenada (respecto a su id)."
        // Create a temporary list to sort, or sort a copy
        ArrayList<Passenger> sortedPassengers = new ArrayList<>(this.passengers);
        sortedPassengers.sort(Comparator.comparingLong(Passenger::getId));
        return sortedPassengers; // Return the sorted copy
    }
}
