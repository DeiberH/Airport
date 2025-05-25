/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package airport.controller.interfaces;
public interface ILocationValidator {
    String validateLocationData(String id, String name, String city, String country, String latitude, String longitude);
}