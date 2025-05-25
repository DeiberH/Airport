package airport.controller.interfaces; //
import airport.model.Flight;
import java.util.List;

public interface IFlightRepository {
    boolean addFlight(Flight flight);
    Flight getFlight(String id);
    List<Flight> getAllFlights();
    boolean updateFlight(Flight flight); // Nuevo método
}