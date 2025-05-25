package airport.utils.file;

import airport.model.*;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class JsonDataManager {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;
    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    private static String readFileAsString(String filePath) throws IOException {
        return new String(Files.readAllBytes(Paths.get(filePath)));
    }

    // --- Passenger Methods ---
    public static List<Passenger> loadPassengers(String filePath) {
        List<Passenger> passengers = new ArrayList<>();
        try {
            String jsonText = readFileAsString(filePath);
            JSONArray jsonArray = new JSONArray(jsonText);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObj = jsonArray.getJSONObject(i);
                passengers.add(new Passenger(
                        jsonObj.getLong("id"),
                        jsonObj.getString("firstname"),
                        jsonObj.getString("lastname"),
                        LocalDate.parse(jsonObj.getString("birthDate"), DATE_FORMATTER),
                        jsonObj.getInt("countryPhoneCode"),
                        jsonObj.getLong("phone"),
                        jsonObj.getString("country")
                ));
            }
        } catch (IOException | JSONException e) {
            System.err.println("Error loading passengers from " + filePath + ": " + e.getMessage());
            // Return empty list or handle more gracefully (e.g., create file if not exists)
        }
        return passengers;
    }

    public static void savePassengers(String filePath, List<Passenger> passengers) {
        JSONArray jsonArray = new JSONArray();
        for (Passenger p : passengers) {
            JSONObject jsonObj = new JSONObject();
            jsonObj.put("id", p.getId());
            jsonObj.put("firstname", p.getFirstname());
            jsonObj.put("lastname", p.getLastname());
            jsonObj.put("birthDate", p.getBirthDate().format(DATE_FORMATTER));
            jsonObj.put("countryPhoneCode", p.getCountryPhoneCode());
            jsonObj.put("phone", p.getPhone());
            jsonObj.put("country", p.getCountry());
            jsonArray.put(jsonObj);
        }
        try (FileWriter file = new FileWriter(filePath)) {
            file.write(jsonArray.toString(4)); // Indent with 4 spaces
        } catch (IOException | JSONException e) {
            System.err.println("Error saving passengers to " + filePath + ": " + e.getMessage());
        }
    }

    // --- Location Methods ---
    public static List<Location> loadLocations(String filePath) {
        List<Location> locations = new ArrayList<>();
        try {
            String jsonText = readFileAsString(filePath);
            JSONArray jsonArray = new JSONArray(jsonText);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObj = jsonArray.getJSONObject(i);
                locations.add(new Location(
                        jsonObj.getString("airportId"),
                        jsonObj.getString("airportName"),
                        jsonObj.getString("airportCity"),
                        jsonObj.getString("airportCountry"),
                        jsonObj.getDouble("airportLatitude"),
                        jsonObj.getDouble("airportLongitude")
                ));
            }
        } catch (IOException | JSONException e) {
            System.err.println("Error loading locations from " + filePath + ": " + e.getMessage());
        }
        return locations;
    }

    public static void saveLocations(String filePath, List<Location> locations) {
        JSONArray jsonArray = new JSONArray();
        for (Location loc : locations) {
            JSONObject jsonObj = new JSONObject();
            jsonObj.put("airportId", loc.getAirportId());
            jsonObj.put("airportName", loc.getAirportName());
            jsonObj.put("airportCity", loc.getAirportCity());
            jsonObj.put("airportCountry", loc.getAirportCountry());
            jsonObj.put("airportLatitude", loc.getAirportLatitude());
            jsonObj.put("airportLongitude", loc.getAirportLongitude());
            jsonArray.put(jsonObj);
        }
        try (FileWriter file = new FileWriter(filePath)) {
            file.write(jsonArray.toString(4));
        } catch (IOException | JSONException e) {
            System.err.println("Error saving locations to " + filePath + ": " + e.getMessage());
        }
    }

    // --- Plane Methods ---
    public static List<Plane> loadPlanes(String filePath) {
        List<Plane> planes = new ArrayList<>();
        try {
            String jsonText = readFileAsString(filePath);
            JSONArray jsonArray = new JSONArray(jsonText);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObj = jsonArray.getJSONObject(i);
                planes.add(new Plane(
                        jsonObj.getString("id"),
                        jsonObj.getString("brand"),
                        jsonObj.getString("model"),
                        jsonObj.getInt("maxCapacity"),
                        jsonObj.getString("airline")
                ));
            }
        } catch (IOException | JSONException e) {
            System.err.println("Error loading planes from " + filePath + ": " + e.getMessage());
        }
        return planes;
    }

    public static void savePlanes(String filePath, List<Plane> planes) {
        JSONArray jsonArray = new JSONArray();
        for (Plane plane : planes) {
            JSONObject jsonObj = new JSONObject();
            jsonObj.put("id", plane.getId());
            jsonObj.put("brand", plane.getBrand());
            jsonObj.put("model", plane.getModel());
            jsonObj.put("maxCapacity", plane.getMaxCapacity());
            jsonObj.put("airline", plane.getAirline());
            jsonArray.put(jsonObj);
        }
        try (FileWriter file = new FileWriter(filePath)) {
            file.write(jsonArray.toString(4));
        } catch (IOException | JSONException e) {
            System.err.println("Error saving planes to " + filePath + ": " + e.getMessage());
        }
    }

    // --- Flight Methods ---
    public static List<Flight> loadFlights(String filePath, List<Plane> allPlanes, List<Location> allLocations) {
        List<Flight> flights = new ArrayList<>();
        Map<String, Plane> planeMap = allPlanes.stream().collect(Collectors.toMap(Plane::getId, Function.identity()));
        Map<String, Location> locationMap = allLocations.stream().collect(Collectors.toMap(Location::getAirportId, Function.identity()));

        try {
            String jsonText = readFileAsString(filePath);
            JSONArray jsonArray = new JSONArray(jsonText);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObj = jsonArray.getJSONObject(i);
                
                Plane plane = planeMap.get(jsonObj.getString("plane"));
                Location depLoc = locationMap.get(jsonObj.getString("departureLocation"));
                Location arrLoc = locationMap.get(jsonObj.getString("arrivalLocation"));
                Location scaleLoc = null;
                if (jsonObj.has("scaleLocation") && !jsonObj.isNull("scaleLocation")) {
                    scaleLoc = locationMap.get(jsonObj.getString("scaleLocation"));
                }

                if (plane == null || depLoc == null || arrLoc == null) {
                    System.err.println("Skipping flight " + jsonObj.getString("id") + " due to missing plane or location references.");
                    continue;
                }
                if (jsonObj.has("scaleLocation") && !jsonObj.isNull("scaleLocation") && scaleLoc == null) {
                     System.err.println("Skipping flight " + jsonObj.getString("id") + " due to missing scale location reference: " + jsonObj.getString("scaleLocation"));
                    continue;
                }


                Flight flight;
                if (scaleLoc != null) {
                    flight = new Flight(
                            jsonObj.getString("id"),
                            plane,
                            depLoc,
                            scaleLoc,
                            arrLoc,
                            LocalDateTime.parse(jsonObj.getString("departureDate"), DATETIME_FORMATTER),
                            jsonObj.getInt("hoursDurationArrival"),
                            jsonObj.getInt("minutesDurationArrival"),
                            jsonObj.getInt("hoursDurationScale"),
                            jsonObj.getInt("minutesDurationScale")
                    );
                } else {
                    flight = new Flight(
                            jsonObj.getString("id"),
                            plane,
                            depLoc,
                            arrLoc,
                            LocalDateTime.parse(jsonObj.getString("departureDate"), DATETIME_FORMATTER),
                            jsonObj.getInt("hoursDurationArrival"),
                            jsonObj.getInt("minutesDurationArrival")
                    );
                }
                flights.add(flight);
                // The Flight constructor should NOT call plane.addFlight(this) to avoid issues here.
                // The association should be rebuilt after all flights are loaded, or handled carefully.
                // For now, assuming Flight constructor is clean.
            }
        } catch (IOException | JSONException e) {
            System.err.println("Error loading flights from " + filePath + ": " + e.getMessage());
        }
        return flights;
    }

    public static void saveFlights(String filePath, List<Flight> flights) {
        JSONArray jsonArray = new JSONArray();
        for (Flight flight : flights) {
            JSONObject jsonObj = new JSONObject();
            jsonObj.put("id", flight.getId());
            jsonObj.put("plane", flight.getPlane() != null ? flight.getPlane().getId() : null);
            jsonObj.put("departureLocation", flight.getDepartureLocation() != null ? flight.getDepartureLocation().getAirportId() : null);
            jsonObj.put("arrivalLocation", flight.getArrivalLocation() != null ? flight.getArrivalLocation().getAirportId() : null);
            jsonObj.put("scaleLocation", flight.getScaleLocation() != null ? flight.getScaleLocation().getAirportId() : JSONObject.NULL);
            jsonObj.put("departureDate", flight.getDepartureDate().format(DATETIME_FORMATTER));
            jsonObj.put("hoursDurationArrival", flight.getHoursDurationArrival());
            jsonObj.put("minutesDurationArrival", flight.getMinutesDurationArrival());
            jsonObj.put("hoursDurationScale", flight.getHoursDurationScale());
            jsonObj.put("minutesDurationScale", flight.getMinutesDurationScale());
            // Note: Saving passengers per flight is not done here, assumes passengers are managed separately.
            jsonArray.put(jsonObj);
        }
        try (FileWriter file = new FileWriter(filePath)) {
            file.write(jsonArray.toString(4));
        } catch (IOException | JSONException e) {
            System.err.println("Error saving flights to " + filePath + ": " + e.getMessage());
        }
    }
    
    /**
    * After loading all flights and passengers, this method rebuilds the associations.
    * It ensures that passenger.addFlight() and flight.addPassenger() are correctly called
    * based on some criteria (e.g., if you had a passenger_flights join table/JSON structure).
    * For this example, since flights.json does not list passengers and passengers.json does not list flights,
    * this method would be called AFTER manual association in the UI or if you have a separate JSON for flight_passenger relations.
    * * If you are creating associations via the UI (e.g., AddToFlightPanel), those operations
    * within the controller/storage should handle the bi-directional linking and then save
    * the respective JSONs (passengers.json, flights.json if flight state changes, and potentially planes.json if numFlights changes).
    *
    * The `plane.addFlight(flight)` should happen in FlightController after a flight is successfully created and added to storage.
    * The initial loading of planes and then flights implies planes need to be aware of their flights.
    * We can call plane.addFlight(flight) for each flight associated with a plane during/after flight loading.
    */
    public static void rebuildFlightPlaneAssociations(List<Flight> allFlights, List<Plane> allPlanes) {
        // Clear existing flight lists from planes to avoid duplicates if this is called multiple times
        for (Plane plane : allPlanes) {
            plane.getFlights().clear(); // Assuming Plane.getFlights() returns the modifiable list
        }
        // Re-associate
        for (Flight flight : allFlights) {
            if (flight.getPlane() != null) {
                flight.getPlane().addFlight(flight); // Assumes Plane.addFlight handles duplicates or is fine
            }
        }
    }
}