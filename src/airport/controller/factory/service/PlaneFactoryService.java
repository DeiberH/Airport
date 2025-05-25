package airport.controller.factory.service;

import airport.controller.builder.PlaneBuilder; // Your static builder
import airport.controller.interfaces.IPlaneFactory; // Interface from correct package
import airport.model.Plane;

public class PlaneFactoryService implements IPlaneFactory {
    @Override
    public Plane createPlane(String id, String brand, String model, int capacity, String airline) {
        return PlaneBuilder.createPlane(id, brand, model, capacity, airline); //
    }
}