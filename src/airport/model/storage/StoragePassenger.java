package airport.model.storage;

import airport.model.Passenger;
import airport.controller.interfaces.IPassengerRepository;
import airport.utils.file.JsonDataManager;
import airport.utils.observer.Observer;
import airport.utils.observer.Subject;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class StoragePassenger implements IPassengerRepository, Subject {
    private ArrayList<Passenger> passengers;
    private final String filePath = "json/passengers.json"; // Ensure this path is correct
    private final List<Observer> observers;

    public StoragePassenger() {
        this.observers = new ArrayList<>();
        this.passengers = new ArrayList<>(JsonDataManager.loadPassengers(filePath)); //
    }

    private void saveToDisk() {
        JsonDataManager.savePassengers(filePath, this.passengers);
        notifyObservers();
    }

    @Override
    public boolean addPassenger(Passenger passenger) {
        if (this.passengers.stream().anyMatch(p -> p.getId() == passenger.getId())) {
            return false;
        }
        this.passengers.add(passenger);
        saveToDisk();
        return true;
    }

    @Override
    public Passenger getPassenger(long id) {
        Optional<Passenger> passengerOpt = this.passengers.stream()
                .filter(p -> p.getId() == id)
                .findFirst();
        return passengerOpt.orElse(null);
    }

    @Override
    public boolean updatePassenger(Passenger passengerToUpdate) {
        for (int i = 0; i < this.passengers.size(); i++) {
            if (this.passengers.get(i).getId() == passengerToUpdate.getId()) {
                this.passengers.set(i, passengerToUpdate);
                saveToDisk();
                return true;
            }
        }
        return false;
    }

    @Override
    public List<Passenger> getAllPassengers() {
        ArrayList<Passenger> sortedPassengers = new ArrayList<>(this.passengers);
        sortedPassengers.sort(Comparator.comparingLong(Passenger::getId));
        return sortedPassengers;
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
}