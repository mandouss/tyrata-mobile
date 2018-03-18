package edu.duke.ece651.tyrata.user;

import java.util.ArrayList;

import edu.duke.ece651.tyrata.vehicle.Vehicle;

/**
 * Created by Saeed on 3/10/2018.
 */

public class User {
    public String mFirstName;
    public String mLastName;
    public String mID;
    /* @TODO add more user attributes (e.g. login token/certification) */
    public ArrayList<Vehicle> mVehicles;

    /** Constructor
     *
     * @param first User first name
     * @param last User last name
     */
    public User(String first, String last) {
        this.mFirstName = first;
        this.mLastName = last;
        /* @TODO include the rest of the attributes here */
        this.mVehicles = new ArrayList<>();
    }

    /** Default constructor
     *
     */
    public User() {
        this("", "");
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
}
