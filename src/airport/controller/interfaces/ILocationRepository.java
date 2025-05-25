/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package airport.controller.interfaces;

import airport.model.Location;
import java.util.List;

public interface ILocationRepository {
    boolean addLocation(Location location);
    Location getLocation(String id); // Useful for updates or checks
    List<Location> getAllLocations();
}