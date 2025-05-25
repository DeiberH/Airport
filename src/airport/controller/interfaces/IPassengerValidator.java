/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package airport.controller.interfaces;
public interface IPassengerValidator {
    String validatePassengerData(String id, String firstname, String lastname, String year, String month, String day, String phoneCode, String phone, String country);
}
