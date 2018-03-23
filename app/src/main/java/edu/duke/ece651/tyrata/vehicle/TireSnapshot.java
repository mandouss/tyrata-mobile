package edu.duke.ece651.tyrata.vehicle;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * This class is a TireSnapshot object
 * used to standardize the use of tire snapshots across the app
 * Tire snapshot is the tire's data in an instant/moment as received from the sensor
 * @author Saeed Alrahma
 * Created by Saeed on 3/10/2018.
 */

public class TireSnapshot extends Tire {
    private double mS11;
    private int mOdometerMileage;
    private Calendar mTimestamp; /* @TODO DateFormat might be a better type */
    private double mPressure;

    /** Constructor
     *
     * @param tire Tire (parent object)
     * @param s11 Tire S11 measurement from sensor
     * @param odoMileage Vehicle odometer mileage
     * @param timestamp Timestamp of snapshot
     * @param pressure Tire pressure
     */
    public TireSnapshot(Tire tire, double s11, int odoMileage, Calendar timestamp, double pressure) {
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
    public TireSnapshot(double s11, int odoMileage, Calendar timestamp, double pressure) {
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

    public float calculateEol() {
        // @TODO
        return 0;
    }

    public Calendar calculateReplaceTime() {
        // @TODO

        return null;
    }

    public boolean filterOutlier() {
        // @TODO

        return false;
    }

    public void storeSnapshot() {
        // @TODO

    }

    public void processSnapshot() {
        // @TODO

    }

    /**
     * Convert/Parse String with format "2018-03-22" (or "year-month-day") to Calendar object
     * @param date String with date formatted as "2018-03-22" (or "year-month-day")
     * @return Calendar object of parsed date
     */
    public static Calendar convertStringToCalendar(String date) {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        try {
            cal.setTime(sdf.parse(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return cal;
    }

    /**
     * Convert/Parse Calendar object to String with format "2018-03-22" (or "year-month-day")
     * @param cal Calendar object
     * @return String with date formatted as "2018-03-22" (or "year-month-day")
     */
    public static String convertCalendarToString(Calendar cal) {
        return cal.get(Calendar.YEAR) + "-" + cal.get(Calendar.MONTH) + "-" + cal.get(Calendar.DAY_OF_MONTH);
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

    public double getPressure() {
        return mPressure;
    }

    public void setPressure(double mPressure) {
        this.mPressure = mPressure;
    }
}
