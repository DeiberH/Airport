package airport.controller.interfaces;
import airport.model.Plane;
import java.util.List;

public interface IPlaneRepository {
    boolean addPlane(Plane plane);
    Plane getPlane(String id);
    List<Plane> getAllPlanes();
    boolean updatePlane(Plane plane); // Nuevo método
}