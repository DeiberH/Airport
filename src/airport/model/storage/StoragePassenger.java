/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package airport.model.storage;

import airport.model.Passenger;
import java.util.ArrayList;

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

    public ArrayList<Passenger> getPassengers() {
        return passengers;
    }
    
}
