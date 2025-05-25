/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package airport.controller.interfaces;

/**
 *
 * @author Juan Sebastian
 */
import airport.model.Plane;
public interface IPlaneFactory {
    Plane createPlane(String id, String brand, String model, int capacity, String airline);
}
