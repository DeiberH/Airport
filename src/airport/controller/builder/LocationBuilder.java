/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package airport.controller.builder;

import airport.model.Location;

/**
 *
 * @author Derby42
 */
public class LocationBuilder {
    public static Location build(String id, String name, String city, String country, double latitude, double longitude) {
        return new Location(id, name, city, country, latitude, longitude);
    }
}
