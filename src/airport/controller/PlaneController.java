/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package airport.controller;

import airport.controller.utils.Response;
import airport.controller.utils.Status;
import airport.controller.validation.PlaneValidation;
import airport.controller.builder.PlaneBuilder;
import airport.model.Plane;
import airport.model.storage.StoragePlane;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Juan Sebastian
 */
public class PlaneController {

    public static Response CreatePlane(String id, String brand, String model, String maxCapacity, String airline) {
        try {
            String Error = PlaneValidation.validatePlaneData(id, brand, model, maxCapacity, airline);
            if (Error != null) {
                return new Response(Error, Status.BAD_REQUEST);
            }

            int capacity = Integer.parseInt(maxCapacity);
            Plane plane = PlaneBuilder.createPlane(id, brand, model, capacity, airline);
            StoragePlane storage = StoragePlane.getInstance();
            
            if (!storage.addPlane(plane)) {
                return new Response("A plane with that id already exists", Status.BAD_REQUEST);
            }
            return new Response("Airplane created successfully", Status.CREATED);
        } catch (Exception ex) {
            return new Response("Unexpected error", Status.INTERNAL_SERVER_ERROR);
        }
    }

    public static Response getAllPlanesForTable() {
        try {
            List<Plane> planes = StoragePlane.getInstance().getAllPlanes();
            // Storage should return sorted copy
            if (planes.isEmpty()) {
                return new Response("No planes found.", Status.OK, new ArrayList<Plane>());
            }
            return new Response("Planes retrieved successfully.", Status.OK, planes);
        } catch (Exception ex) {
            // ex.printStackTrace();
            return new Response("Error retrieving planes: " + ex.getMessage(), Status.INTERNAL_SERVER_ERROR);
        }
    }
}
