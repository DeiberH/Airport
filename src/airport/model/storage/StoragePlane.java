/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package airport.model.storage;

import airport.model.Plane;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 *
 * @author Juan Sebastian
 */
public class StoragePlane {

    private static StoragePlane instance;
    private ArrayList<Plane> planes;

    public static StoragePlane getInstance() {
        if (instance == null) {
            instance = new StoragePlane();
        }
        return instance;
    }

    private StoragePlane() {
        this.planes = new ArrayList<>();
    }

    public boolean addPlane(Plane plane) {
        for (Plane p : this.planes) {
            if (p.getId().equals(plane.getId())) {
                return false;
            }
        }
        this.planes.add(plane);
        return true;
    }

    public Plane getPlane(String id) {
        for (Plane plane : this.planes) {
            if (plane.getId().equals(id)) {
                return plane;
            }
        }
        return null;
    }

    public List<Plane> getAllPlanes() {
        // Sort by ID: "Los aviones se deben obtener de manera ordenada (respecto a su id)."
        ArrayList<Plane> sortedPlanes = new ArrayList<>(this.planes);
        sortedPlanes.sort(Comparator.comparing(Plane::getId));
        return sortedPlanes; // Return the sorted copy
    }
}
