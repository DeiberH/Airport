package airport.controller;

import airport.controller.utils.Response;
import airport.controller.utils.Status;
import airport.model.Plane;
import airport.controller.interfaces.IPlaneRepository;
import airport.controller.interfaces.IPlaneValidator;
import airport.controller.interfaces.IPlaneFactory;

import java.util.ArrayList;
import java.util.List;

public class PlaneController {
    private final IPlaneRepository planeRepository;
    private final IPlaneValidator planeValidator;
    private final IPlaneFactory planeFactory;

    public PlaneController(IPlaneRepository planeRepository,
                           IPlaneValidator planeValidator,
                           IPlaneFactory planeFactory) {
        this.planeRepository = planeRepository;
        this.planeValidator = planeValidator;
        this.planeFactory = planeFactory;
    }

    public Response createPlane(String idStr, String brandStr, String modelStr, String maxCapacityStr, String airlineStr) { // Renamed for convention
        try {
            String error = planeValidator.validatePlaneData(idStr, brandStr, modelStr, maxCapacityStr, airlineStr);
            if (error != null) {
                return new Response(error, Status.BAD_REQUEST);
            }

            String id = idStr.trim();
            String brand = brandStr.trim();
            String model = modelStr.trim();
            int capacity = Integer.parseInt(maxCapacityStr.trim());
            String airline = airlineStr.trim();

            Plane newPlane = planeFactory.createPlane(id, brand, model, capacity, airline);

            if (!planeRepository.addPlane(newPlane)) {
                return new Response("A plane with ID '" + id + "' already exists", Status.BAD_REQUEST);
            }

            Plane planeCopy = planeFactory.createPlane(newPlane.getId(), newPlane.getBrand(), newPlane.getModel(),
                                                      newPlane.getMaxCapacity(), newPlane.getAirline());
            return new Response("Airplane created successfully", Status.CREATED, planeCopy);

        } catch (NumberFormatException ex) {
            return new Response("Invalid numeric format for capacity.", Status.BAD_REQUEST);
        } catch (Exception ex) {
            return new Response("Unexpected error creating plane: " + ex.getMessage(), Status.INTERNAL_SERVER_ERROR);
        }
    }

    public Response getAllPlanesForTable() {
        try {
            List<Plane> planes = planeRepository.getAllPlanes();
            if (planes.isEmpty()) {
                return new Response("No planes found.", Status.OK, new ArrayList<>());
            }
            return new Response("Planes retrieved successfully.", Status.OK, new ArrayList<>(planes));
        } catch (Exception ex) {
            return new Response("Error retrieving planes: " + ex.getMessage(), Status.INTERNAL_SERVER_ERROR);
        }
    }
}