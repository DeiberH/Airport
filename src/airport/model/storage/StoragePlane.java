package airport.model.storage;

import airport.model.Plane;
import airport.controller.interfaces.IPlaneRepository; // Corrected package for interface
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class StoragePlane implements IPlaneRepository {
    private final ArrayList<Plane> planes;

    public StoragePlane() {
        this.planes = new ArrayList<>();
    }

    @Override
    public boolean addPlane(Plane plane) {
        if (planes.stream().anyMatch(p -> p.getId().equals(plane.getId()))) {
            return false;
        }
        this.planes.add(plane);
        return true;
    }

    @Override
    public Plane getPlane(String id) {
        Optional<Plane> planeOpt = planes.stream()
                                          .filter(p -> p.getId().equals(id))
                                          .findFirst();
        return planeOpt.orElse(null);
    }

    @Override
    public List<Plane> getAllPlanes() {
        ArrayList<Plane> sortedPlanes = new ArrayList<>(this.planes);
        sortedPlanes.sort(Comparator.comparing(Plane::getId));
        return sortedPlanes;
    }
}