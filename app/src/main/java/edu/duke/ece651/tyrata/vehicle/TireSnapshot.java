package edu.duke.ece651.tyrata.vehicle;

import java.util.Calendar;

/**
 * This class is a TireSnapshot object
 * used to standardize the use of tire snapshots across the app
 * Tire snapshot is the tire's data in an instant/moment as received from the sensor
 * @author Saeed Alrahma
 * Created by Saeed on 3/10/2018.
 */

public class TireSnapshot extends Tire {
    double mS11;
    int mOdometerMileage;
    Calendar mTimestamp; /* @TODO DateFormat might be a better type */
    int mPressure;

    /** Constructor
     *
     * @param tire Tire (parent object)
     * @param s11 Tire S11 measurement from sensor
     * @param odoMileage Vehicle odometer mileage
     * @param timestamp Timestamp of snapshot
     * @param pressure Tire pressure
     */
    public TireSnapshot(Tire tire, double s11, int odoMileage, Calendar timestamp, int pressure) {
        super(tire);
        this.mS11 = s11;
        this.mOdometerMileage = odoMileage;
        this.mTimestamp = timestamp;
        this.mPressure = pressure;
    }

    /** Constructor
     *
     * @param s11 Tire S11 measurement from sensor
     * @param odoMileage Vehicle odometer mileage
     * @param timestamp Timestamp of snapshot
     * @param pressure Tire pressure
     */
    public TireSnapshot(double s11, int odoMileage, Calendar timestamp, int pressure) {
        super();
        this.mS11 = s11;
        this.mOdometerMileage = odoMileage;
        this.mTimestamp = timestamp;
        this.mPressure = pressure;
    }

    /** Default constructor
     *
     */
    public TireSnapshot() {
        this(0, 0, null, 0);
    }

    /* @TODO implement processing and calcualtion methods here */

    public float calculateTreadThickness() {
        /* @TODO implement tread thickness calcualtion/formaul */
        return 0;
    }

    /* Getters and Setters */
    public double getS11() {
        return mS11;
    }

    public void setS11(double mS11) {
        this.mS11 = mS11;
    }

    public int getOdometerMileage() {
        return mOdometerMileage;
    }

    public void setOdometerMileage(int mOdometerMileage) {
        this.mOdometerMileage = mOdometerMileage;
    }

    public Calendar getTimestamp() {
        return mTimestamp;
    }

    public void setTimestamp(Calendar mTimestamp) {
        this.mTimestamp = mTimestamp;
    }

    public int getPressure() {
        return mPressure;
    }

    public void setPressure(int mPressure) {
        this.mPressure = mPressure;
    }
}
