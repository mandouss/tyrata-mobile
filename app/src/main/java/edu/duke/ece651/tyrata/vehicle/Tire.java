package edu.duke.ece651.tyrata.vehicle;

/**
 * This class is a Tire object
 * used to standardize the use of tire across the app
 * @author Saeed Alrahma
 * Created by Saeed on 3/10/2018.
 */

public class Tire {
    String mManufacturer;
    String mModel;
    String mSku;
    String mLocation;
    double mS11Reference;
    int mOdometerMileageReference;
    String mSensorId;

    /** Constructor
     *
     * @param manufacturer Manufacturer of the tire
     * @param model Model of the tire
     * @param sku SKU (Stock Keeping Unit) of the tire)
     * @param location Location of the tire on the vehicle
     * @param s11Ref S11 measurement reference/baseline
     * @param odometerRef Odometer mileage reference/baseline
     * @param sensorId Paired sensor ID
     */
    public Tire(String manufacturer, String model, String sku, String location,
                double s11Ref, int odometerRef, String sensorId) {
        this.mManufacturer = manufacturer;
        this.mModel = model;
        this.mSku = sku;
        this.mLocation = location;
        this.mS11Reference = s11Ref;
        this.mOdometerMileageReference = odometerRef;
        this.mSensorId = sensorId;
    }

    /** Constructor
     *
     * @param tire Tire object to copy/clone
     */
    public Tire(Tire tire) {
        this(tire.getManufacturer(), tire.getModel(), tire.getSku(), tire.getLocation(),
                tire.getS11Reference(), tire.getOdometerMileageReference(), tire.getSensorId());
    }

    /** Default constructor
     *
     */
    public Tire() {
        this("", "", "", "", 0, 0, "");
    }

    /* Getters and Setters */
    public String getManufacturer() {
        return mManufacturer;
    }

    public void setManufacturer(String mManufacturer) {
        this.mManufacturer = mManufacturer;
    }

    public String getModel() {
        return mModel;
    }

    public void setModel(String mModel) {
        this.mModel = mModel;
    }

    public String getSku() {
        return mSku;
    }

    public void setSku(String mSku) {
        this.mSku = mSku;
    }

    public String getLocation() {
        return mLocation;
    }

    public void setLocation(String mLocation) {
        this.mLocation = mLocation;
    }

    public double getS11Reference() {
        return mS11Reference;
    }

    public void setS11Reference(double mS11Reference) {
        this.mS11Reference = mS11Reference;
    }

    public int getOdometerMileageReference() {
        return mOdometerMileageReference;
    }

    public void setOdometerMileageReference(int mOdometerMileageReference) {
        this.mOdometerMileageReference = mOdometerMileageReference;
    }

    public String getSensorId() {
        return mSensorId;
    }

    public void setSensorId(String mSensorId) {
        this.mSensorId = mSensorId;
    }
}
