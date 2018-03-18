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
    int mAxisRow;
    char mAxisSide;
    int mAxisIndex;
    String mLocation;
    double mS11Reference;
    int mOdometerMileageReference;
    String mSensorId;

    /** Constructor
     *
     * @param manufacturer Manufacturer of the tire
     * @param model Model of the tire
     * @param sku SKU (Stock Keeping Unit) of the tire)
     * @param axisRow The axis/axle row number of the tire
     * @param axisSide The axis/axle side of the tires )('L' for left, 'R' for right)
     * @param axisIndex The index of the tire on the axis/axle (inner-most is 0)
     * @param s11Ref S11 measurement reference/baseline
     * @param odometerRef Odometer mileage reference/baseline
     * @param sensorId Paired sensor ID
     */
    public Tire(String manufacturer, String model, String sku, int axisRow, char axisSide,
                int axisIndex, double s11Ref, int odometerRef, String sensorId) {
        this.mManufacturer = manufacturer;
        this.mModel = model;
        this.mSku = sku;
        this.mAxisRow = axisRow;
        this.mAxisSide = axisSide;
        this.mAxisIndex = axisIndex;
        this.mS11Reference = s11Ref;
        this.mOdometerMileageReference = odometerRef;
        this.mSensorId = sensorId;
    }

    /** Constructor
     *
     * @param tire Tire object to copy/clone
     */
    public Tire(Tire tire) {
        this(tire.getManufacturer(), tire.getModel(), tire.getSku(), tire.getAxisRow(),
                tire.getAxisSide(), tire.getAxisIndex(), tire.getS11Reference(),
                tire.getOdometerMileageReference(), tire.getSensorId());
    }

    /** Default constructor
     *
     */
    public Tire() {
        this("", "", "", 0, 'S', 0, 0, 0, "");
    }

    public void reportAccident() {
        // @TODO

    }

    public void calibrate() {
        // @TODO

    }

    /** Return the location of the tire (e.g. Front Left)
     *
     * @return String describing the location of the tire on the car
     */
    public String getTireLocation() {
        // @TODO use axis info to find location on car
        return "";
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

    public int getAxisRow() {
        return mAxisRow;
    }

    public void setAxisRow(int mAxisRow) {
        this.mAxisRow = mAxisRow;
    }

    public char getAxisSide() {
        return mAxisSide;
    }

    public void setAxisSide(char mAxisSide) {
        this.mAxisSide = mAxisSide;
    }

    public int getAxisIndex() {
        return mAxisIndex;
    }

    public void setAxisIndex(int mAxisIndex) {
        this.mAxisIndex = mAxisIndex;
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
