/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package airport.controller.builder;
import airport.model.Plane;

/**
 *
 * @author Derby42
 */
public class PlaneBuilder {
    public static Plane createPlane(String id, String brand, String model, int capacity, String airline) {
        return new Plane(id, brand, model, capacity, airline);
    }
}
