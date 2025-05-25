package airport.utils.observer; // Example package

public interface Observer {
    void update(); // Called by the Subject to notify of a change
    // You could also have void update(Object arg); if you want to pass specific data
}