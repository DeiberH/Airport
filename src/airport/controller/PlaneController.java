/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package airport.controller;

import airport.controller.utils.Response;
import airport.controller.utils.Status;
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
            int capInt;

            if (id.trim().isEmpty() || id == null) {
                return new Response("Id must not be empty", Status.BAD_REQUEST);
            }

            if (id.length() != 7) {
                return new Response("Id must have a length of 7 characters XXYYYYY", Status.BAD_REQUEST);
            }
            char l1 = id.charAt(0);
            char l2 = id.charAt(1);
            if (!Character.isUpperCase(l1) || !Character.isUpperCase(l2)) {
                return new Response("Id must not contain LowerCase letters or digits on the first 2 characters XXYYYYY", Status.BAD_REQUEST);
            }

            for (int i = 2; i < 7; i++) {
                if (!Character.isDigit(id.charAt(i))) {
                    return new Response("Id must have 5 numbers XXYYYYY", Status.BAD_REQUEST);
                }
            }

            if (brand.trim().isEmpty() || brand == null) {
                return new Response("Brand must not be empty", Status.BAD_REQUEST);
            }

            if (model.trim().isEmpty() || model == null) {
                return new Response("Model must not be empty", Status.BAD_REQUEST);
            }

            if (maxCapacity.trim().isEmpty() || maxCapacity == null) {
                return new Response("Airline must not be empty", Status.BAD_REQUEST);
            }

            try {
                capInt = Integer.parseInt(maxCapacity);
                if (capInt <= 0) {
                    return new Response("Capacity must be positive", Status.BAD_REQUEST);
                }
            } catch (NumberFormatException ex) {
                return new Response("Capacity must be numeric", Status.BAD_REQUEST);
            }

            if (airline.trim().isEmpty() || airline == null) {
                return new Response("Airline must not be empty", Status.BAD_REQUEST);
            }

            StoragePlane storage = StoragePlane.getInstance();
            if (!storage.addPlane(new Plane(id, brand, model, capInt, airline))) {
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
