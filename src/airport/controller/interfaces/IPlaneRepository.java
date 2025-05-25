/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package airport.controller.interfaces;
import airport.model.Plane;
import java.util.List;

public interface IPlaneRepository {
    boolean addPlane(Plane plane);
    Plane getPlane(String id);
    List<Plane> getAllPlanes();
}