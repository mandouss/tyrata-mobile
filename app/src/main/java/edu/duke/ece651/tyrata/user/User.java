package edu.duke.ece651.tyrata.user;

import java.util.ArrayList;

import edu.duke.ece651.tyrata.vehicle.Vehicle;

/**
 * This is a User object for all user-related information and functions
 * Created by Saeed on 3/10/2018.
 */

public class User {
    private String mUserName;
    private String mEmail;
    private String mPhone;
    private ArrayList<Vehicle> mVehicles;

    /** Constructor
     *
     * @param name User  name
     * @param email User email
     * @param number User phone number
     */
    public User(String name, String email, String number) {
        this.mUserName = name;
        this.mEmail = email;
        this.mPhone = number;
        this.mVehicles = new ArrayList<>();
    }

    /** Default constructor
     *
     */
    public User() {
        this("", "", "");
    }

    /** Add new vehicle to user
     *
     * @param vehicle The vehicle to add
     */
    public void addVehicle(Vehicle vehicle) {
        mVehicles.add(vehicle);
    }

    /** Remove vehicle from user
     *
     * @param vehicle The vehicle to remove
     * @return true if vehicle removed (list modified), otherwise false
     */
    public boolean removeVehicle(Vehicle vehicle) {
        return mVehicles.remove(vehicle);
    }

    public boolean register() {
        // @TODO

        return false;
    }

    public boolean login() {
        // @TODO

        return false;
    }

    public boolean logout() {
        // @TODO

        return false;
    }

    public boolean changePassword() {
        // @TODO

        return false;
    }

    public void sendFeedback() {
        // @TODO

    }

    public void pushNotification() {
        // @TODO

    }

    /* GETTERS and SETTERS */
    public String getUserName() {
        return mUserName;
    }

    public void setUserName(String username) {
        this.mUserName = username;
    }

    public String getEmail() {
        return mEmail;
    }

    public void setEmail(String email) {
        this.mEmail = email;
    }

    public String getPhone() {
        return mPhone;
    }

    public void setPhone(String phone) {
        this.mPhone = phone;
    }

    public ArrayList<Vehicle> getVehicles() {
        return mVehicles;
    }
}
