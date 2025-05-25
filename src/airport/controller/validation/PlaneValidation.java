/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package airport.controller.validation;

/**
 *
 * @author Derby42
 */
public class PlaneValidation {

    public static String validatePlaneData(String id, String brand, String model, String maxCapacity, String airline) {
        if (id == null || id.trim().isEmpty()) {
            return "Id must not be empty";
        }
        if (id.length() != 7) {
            return "Id must have a length of 7 characters XXYYYYY";
        }

        char l1 = id.charAt(0), l2 = id.charAt(1);
        if (!Character.isUpperCase(l1) || !Character.isUpperCase(l2)) {
            return "First 2 characters of Id must be uppercase letters";
        }

        for (int i = 2; i < 7; i++) {
            if (!Character.isDigit(id.charAt(i))) {
                return "Last 5 characters of Id must be digits";
            }
        }

        if (brand == null || brand.trim().isEmpty()) {
            return "Brand must not be empty";
        }
        if (model == null || model.trim().isEmpty()) {
            return "Model must not be empty";
        }
        if (maxCapacity == null || maxCapacity.trim().isEmpty()) {
            return "Max capacity must not be empty";
        }
        if (airline == null || airline.trim().isEmpty()) {
            return "Airline must not be empty";
        }

        try {
            int cap = Integer.parseInt(maxCapacity);
            {
                if (cap <= 0) {
                    return "Capacity must be a positive number";
                }
            }
        } catch (NumberFormatException e) {
            return "Capacity must be numeric";
        }

        return null;
    }
}
