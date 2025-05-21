/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package airport.model.storage;

import airport.model.Flight;
import airport.model.Location;
import airport.model.Passenger;
import airport.model.Plane;
import java.util.ArrayList;

/**
 *
 * @author Juan Sebastian
 */
public class Storage {

    private static Storage instance;
    
    private ArrayList<Passenger> passengers;
    private ArrayList<Plane> planes;
    private ArrayList<Location> locations;
    private ArrayList<Flight> flights;
    
    public static Storage getInstance() {
        if (instance == null) {
            instance = new Storage();
        }
        return instance;
    }
    
    public Storage() {
        this.passengers = new ArrayList<>();
        this.planes = new ArrayList<>();
        this.locations = new ArrayList<>();
        this.flights = new ArrayList<>();
    }
    
    public boolean addPlane(Plane plane) {
        for (Plane p : this.planes) {
            if (Integer.parseInt(p.getId()) == Integer.parseInt(plane.getId())) {
                return false;
            }
        }
        this.planes.add(plane);
        return true;
    }
    
    public boolean addLocation(Location local) {
        for (Location l : this.locations) {
            if (Integer.parseInt(l.getAirportId()) == Integer.parseInt(local.getAirportId())) {
                return false;
            }
        }
        this.locations.add(local);
        return true;
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
}
