/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package airport.controller.interfaces;

/**
 *
 * @author Juan Sebastian
 */
public interface IPlaneValidator {
    String validatePlaneData(String id, String brand, String model, String maxCapacity, String airline);
}
