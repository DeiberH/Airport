package airport.model.storage;

import airport.model.Plane;
import airport.controller.interfaces.IPlaneRepository;
import airport.utils.file.JsonDataManager;
import airport.utils.observer.Observer;
import airport.utils.observer.Subject;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class StoragePlane implements IPlaneRepository, Subject {

    private ArrayList<Plane> planes;
    private final String filePath = "json/planes.json";
    private final List<Observer> observers;

    public StoragePlane() {
        this.observers = new ArrayList<>();
        this.planes = new ArrayList<>(JsonDataManager.loadPlanes(filePath));
    }

    private void saveToDisk() {
        JsonDataManager.savePlanes(filePath, this.planes);
        notifyObservers();
    }

    @Override
    public boolean addPlane(Plane plane) {
        if (this.planes.stream().anyMatch(p -> p.getId().equals(plane.getId()))) {
            return false;
        }
        this.planes.add(plane);
        saveToDisk();
        return true;
    }

    @Override
    public Plane getPlane(String id) {
        Optional<Plane> planeOpt = this.planes.stream()
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

    @Override
    public void registerObserver(Observer o) {
        this.observers.add(o);
    }

    @Override
    public void removeObserver(Observer o) {
        this.observers.remove(o);
    }

    @Override
    public void notifyObservers() {
        for (Observer observer : this.observers) {
            observer.update();
        }
    }

    @Override
    public boolean updatePlane(Plane planeToUpdate) {
        for (int i = 0; i < this.planes.size(); i++) {
            if (this.planes.get(i).getId().equals(planeToUpdate.getId())) {
                this.planes.set(i, planeToUpdate); // Reemplaza con la instancia actualizada
                saveToDisk(); // Guarda y notifica
                return true;
            }
        }
        return false; // Avión no encontrado para actualizar
    }
}
