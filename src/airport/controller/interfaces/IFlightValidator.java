/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package airport.controller.interfaces;

/**
 *
 * @author Juan Sebastian
 */
public interface IFlightValidator {
    String validateFlightData(String id, String planeId, String departureLocationId, String arrivalLocationId, String scaleLocationId, String year, String month, String day, String hour, String minutes, String hoursDurationsArrival, String minutesDurationsArrival, String hoursDurationsScale, String minutesDurationsScale);
    String validateAddPassengerToFlightData(String passengerId, String flightId);
    String validateDelayFlightData(String flightId, String hourStr, String minutesStr);
}
