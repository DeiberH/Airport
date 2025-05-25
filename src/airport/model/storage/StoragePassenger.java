package airport.model.storage;

import airport.model.Passenger;
import airport.controller.interfaces.IPassengerRepository; // Corrected package for interface
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class StoragePassenger implements IPassengerRepository {
    private final ArrayList<Passenger> passengers;

    public StoragePassenger() {
        this.passengers = new ArrayList<>();
    }

    @Override
    public boolean addPassenger(Passenger passenger) {
        if (passengers.stream().anyMatch(p -> p.getId() == passenger.getId())) {
            return false;
        }
        this.passengers.add(passenger);
        return true;
    }

    @Override
    public Passenger getPassenger(long id) {
        Optional<Passenger> passengerOpt = passengers.stream()
                                                  .filter(p -> p.getId() == id)
                                                  .findFirst();
        return passengerOpt.orElse(null);
    }

    @Override
    public boolean updatePassenger(Passenger passengerToUpdate) {
        for (int i = 0; i < passengers.size(); i++) {
            if (passengers.get(i).getId() == passengerToUpdate.getId()) {
                passengers.set(i, passengerToUpdate); // Replace with the updated instance
                return true;
            }
        }
        return false; // Passenger not found
    }

    @Override
    public List<Passenger> getAllPassengers() {
        ArrayList<Passenger> sortedPassengers = new ArrayList<>(this.passengers);
        sortedPassengers.sort(Comparator.comparingLong(Passenger::getId));
        return sortedPassengers;
    }
}