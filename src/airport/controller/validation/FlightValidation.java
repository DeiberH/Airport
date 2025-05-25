/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package airport.controller.validation;

import java.time.DateTimeException;
import java.time.LocalDateTime;

/**
 *
 * @author Derby42
 */
public class FlightValidation {

    public static String validateFlightData(String id, String planeId, String departureLocationId, String arrivalLocationId, String scaleLocationId, String year, String month, String day, String hour, String minutes, String hoursDurationsArrival, String minutesDurationsArrival, String hoursDurationsScale, String minutesDurationsScale) {

        if (id == null || id.trim().isEmpty()) {
            return "Flight ID must not be empty.";
        }
        id = id.trim();
        if (id.length() != 6) {
            return "Flight ID must have a length of 6 characters (e.g., XXXYYY).";
        }
        for (int i = 0; i < 3; i++) {
            if (!Character.isUpperCase(id.charAt(i))) {
                return "First 3 characters of Flight ID must be uppercase letters.";
            }
        }
        for (int i = 3; i < 6; i++) {
            if (!Character.isDigit(id.charAt(i))) {
                return "Last 3 characters of Flight ID must be digits.";
            }
        }

        if (planeId == null || planeId.trim().isEmpty() || planeId.equals("Plane")) {
            return "A valid Plane ID must be provided/selected.";
        }
        if (departureLocationId == null || departureLocationId.trim().isEmpty() || departureLocationId.equals("Location")) {
            return "A valid Departure Location ID must be provided/selected.";
        }
        if (arrivalLocationId == null || arrivalLocationId.trim().isEmpty() || arrivalLocationId.equals("Location")) {
            return "A valid Arrival Location ID must be provided/selected.";
        }

        if (year == null || year.trim().isEmpty()) {
            return "Departure year must not be empty.";
        }
        if (month == null || month.trim().isEmpty() || month.equals("Month")) {
            return "Departure month must not be empty/selected.";
        }
        if (day == null || day.trim().isEmpty() || day.equals("Day")) {
            return "Departure day must not be empty/selected.";
        }
        if (hour == null || hour.trim().isEmpty() || hour.equals("Hour")) {
            return "Departure hour must not be empty/selected.";
        }
        if (minutes == null || minutes.trim().isEmpty() || minutes.equals("Minute")) {
            return "Departure minute must not be empty/selected.";
        }

        int yearInt, monthInt, dayInt, hoursInt, minutesInt;
        try {
            yearInt = Integer.parseInt(year.trim());
            if (yearInt < 1900) {
                return "Departure year must be valid (e.g., >= 1900).";
            }
        } catch (NumberFormatException ex) {
            return "Departure year must be a numeric value.";
        }
        try {
            monthInt = Integer.parseInt(month.trim());
            dayInt = Integer.parseInt(day.trim());
            hoursInt = Integer.parseInt(hour.trim());
            minutesInt = Integer.parseInt(minutes.trim());

            LocalDateTime departureAttemptDate = LocalDateTime.of(yearInt, monthInt, dayInt, hoursInt, minutesInt);
            if (departureAttemptDate.isBefore(LocalDateTime.now())) {
                return "Departure date must be in the future.";
            }
        } catch (NumberFormatException ex) {
            return "Departure date/time components (month, day, hour, minute) must be numeric.";
        } catch (DateTimeException ex) {
            return "Departure Date/Time is not valid (e.g., invalid day for month, or hour/minute out of range).";
        }

        if (hoursDurationsArrival == null || hoursDurationsArrival.trim().isEmpty() || hoursDurationsArrival.equals("Hour")) {
            return "Arrival duration hours must not be empty/selected.";
        }
        if (minutesDurationsArrival == null || minutesDurationsArrival.trim().isEmpty() || minutesDurationsArrival.equals("Minute")) {
            return "Arrival duration minutes must not be empty/selected.";
        }
        try {
            int hda = Integer.parseInt(hoursDurationsArrival.trim());
            int mda = Integer.parseInt(minutesDurationsArrival.trim());
            if (hda < 0 || mda < 0) {
                return "Arrival duration components (hours, minutes) must be positive or zero.";
            }
            if (hda == 0 && mda == 0) {
                return "Total arrival duration time must be greater than 00:00.";
            }
            if (mda >= 60) {
                return "Arrival duration minutes must be less than 60.";
            }
        } catch (NumberFormatException ex) {
            return "Arrival duration hours and minutes must be numeric.";
        }

        boolean isScaleDurationHourProvided = !(hoursDurationsScale == null || hoursDurationsScale.trim().isEmpty() || hoursDurationsScale.equals("Hour"));
        boolean isScaleDurationMinuteProvided = !(minutesDurationsScale == null || minutesDurationsScale.trim().isEmpty() || minutesDurationsScale.equals("Minute"));

        if (isScaleDurationHourProvided || isScaleDurationMinuteProvided) {
            if (!isScaleDurationHourProvided) {
                return "Scale duration hours must be provided if minutes are set.";
            }
            if (!isScaleDurationMinuteProvided) {
                return "Scale duration minutes must be provided if hours are set.";
            }
            try {
                int hds = Integer.parseInt(hoursDurationsScale.trim());
                int mds = Integer.parseInt(minutesDurationsScale.trim());
                if (hds < 0 || mds < 0) {
                    return "Scale duration components (hours, minutes) must be positive or zero.";
                }
                if (mds >= 60) {
                    return "Scale duration minutes must be less than 60.";
                }
            } catch (NumberFormatException ex) {
                return "Scale duration hours and minutes must be numeric.";
            }
        }

        return null;
    }

    public static String validateAddPassengerToFlightData(String passengerId, String flightId) {
        if (passengerId == null || passengerId.trim().isEmpty()) {
            return "Passenger ID must not be empty and must be selected.";
        }
        try {
            Long.parseLong(passengerId.trim());
        } catch (NumberFormatException e) {
            return "Passenger ID must be a numeric value.";
        }

        if (flightId == null || flightId.trim().isEmpty() || flightId.equals("Flight")) {
            return "Flight ID must not be empty and a valid flight must be selected.";
        }

        return null;
    }

    public static String validateDelayFlightData(String flightId, String hourStr, String minutesStr) {
        if (flightId == null || flightId.trim().isEmpty() || flightId.equals("ID")) {
            return "Flight ID must not be empty and a valid flight must be selected.";
        }

        if (hourStr == null || hourStr.trim().isEmpty() || hourStr.equals("Hour")) {
            return "Delay hours must be selected/provided.";
        }
        if (minutesStr == null || minutesStr.trim().isEmpty() || minutesStr.equals("Minute")) {
            return "Delay minutes must be selected/provided.";
        }

        int hourInt, minutesInt;
        try {
            hourInt = Integer.parseInt(hourStr.trim());
            minutesInt = Integer.parseInt(minutesStr.trim());
        } catch (NumberFormatException e) {
            return "Delay hours and minutes must be numeric values.";
        }

        if (hourInt < 0 || minutesInt < 0) {
            return "Delay time components (hours, minutes) must not be negative.";
        }
        if (minutesInt >= 60) {
            return "Delay minutes must be less than 60.";
        }
        if (hourInt == 0 && minutesInt == 0) {
            return "Total delay time must be greater than 00:00.";
        }
        return null;
    }
}
