/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package airport.controller.interfaces;

import airport.model.Location;

public interface ILocationFactory {
    Location build(String id, String name, String city, String country, double latitude, double longitude);
}
