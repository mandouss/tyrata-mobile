package edu.duke.ece651.tyrata.datamanagement;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import java.io.IOError;
import java.io.IOException;

import edu.duke.ece651.tyrata.R;
import edu.duke.ece651.tyrata.calibration.TireInfoInput;
import edu.duke.ece651.tyrata.user.User;
import edu.duke.ece651.tyrata.vehicle.Tire;
import edu.duke.ece651.tyrata.vehicle.Vehicle;

/**
 * Created by Yue Li and Zijie Wang on 3/4/18.
 * Updated by De Lan on 3/18/2018: getUser(), getVehicle(), getTire()
 */

public class Database extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main)

    }

    /* Created by Zijie Wang on 3/4/2018. */
    public static SQLiteDatabase myDatabase;

    public static void createTable() {
        myDatabase.execSQL("CREATE TABLE IF NOT EXISTS USER (USER_ID INT, NAME VARCHAR, EMAIL VARCHAR, PHONE_NUMBER VARCHAR, PRIMARY KEY(USER_ID))");
        myDatabase.execSQL("CREATE TABLE IF NOT EXISTS VEHICLE (VIN VARCHAR, MAKE VARCHAR, MODEL VARCHAR, YEAR INT, AXIS_NUM INT, TIRE_NUM INT, USER_ID VARCHAR, PRIMARY KEY(VIN), FOREIGN KEY(USER_ID)REFERENCES USER(USER_ID))");
        myDatabase.execSQL("CREATE TABLE IF NOT EXISTS TIRE(SENSOR_ID VARCHAR, MANUFACTURER VARCHAR, MODEL VARCHAR, SKU VARCHAR, VEHICLE_ID VARCHAR, AXIS_ROW INT, AXIS_SIDE CHAR, AXIS_INDEX INT, INIT_THICKNESS DOUBLE, INIT_SS_ID INT, CUR_SS_ID INT, PRIMARY KEY(SENSOR_ID), FOREIGN KEY(VEHICLE_ID)REFERENCES VEHICLE(VIN))");
        myDatabase.execSQL("CREATE TABLE IF NOT EXISTS SNAPSHOT(ID INT, S11 DOUBLE, TIMESTAMP VARCHAR, MILEAGE DOUBLE, PRESSURE DOUBLE, TIRE_ID VARCHAR, OUTLIER BOOL, THICKNESS DOUBLE, EOL VARCHAR, TIME_TO_REPLACEMENT VARCHAR, LONG DOUBLE, LAT DOUBLE, PRIMARY KEY(ID), FOREIGN KEY(TIRE_ID)REFERENCES TIRE(SENSOR_ID))");
    }

    public static void dropAllTable() {
        myDatabase.execSQL("DROP TABLE IF EXISTS TIRE");
        myDatabase.execSQL("DROP TABLE IF EXISTS SNAPSHOT");
        myDatabase.execSQL("DROP TABLE IF EXISTS VEHICLE");
        myDatabase.execSQL("DROP TABLE IF EXISTS USER");
    }

    /* Created by Yue Li and Zijie Wang on 3/4/2018. */
    /* Updated by De Lan on 3/24/2018 */
    public static boolean storeUserData(String name, String email, String phone) {
        myDatabase.execSQL("CREATE TABLE IF NOT EXISTS USER (USER_ID INT, NAME VARCHAR, EMAIL VARCHAR, PHONE_NUMBER VARCHAR, PRIMARY KEY(USER_ID))");
        Cursor emailCursor = myDatabase.rawQuery("SELECT * FROM USER WHERE EMAIL = '"+email+"'", null);
        if(emailCursor != null && emailCursor.moveToFirst()){
            emailCursor.close();
            return true;
        }
        else {
            final String MY_QUERY = "SELECT MAX(USER_ID) FROM USER";
            Cursor c = myDatabase.rawQuery(MY_QUERY, null);
            int user_id = 0;
            try {
                if (c.getCount() > 0) {
                    Log.i("Notify", "I'm in storeUserData.");
                    c.moveToFirst();
                    user_id = c.getInt(0);
                    user_id += 1;
                }
            } catch (Exception e) {
                Log.i("storeUserData", e.getMessage());
            }
            c.close();
            ContentValues contentValues = new ContentValues();
            contentValues.put("USER_ID", user_id);
            contentValues.put("NAME", name);
            contentValues.put("EMAIL", email);
            contentValues.put("PHONE_NUMBER", phone);
            myDatabase.insertOrThrow("USER", null, contentValues);
            return false;
        }
    }


    public static void storeVehicleData(String vin, String carmodel, String carmake, int tireyear, int axisnum, int tirenum, int userid) {
        myDatabase.execSQL("CREATE TABLE IF NOT EXISTS VEHICLE (VIN VARCHAR, MAKE VARCHAR, MODEL VARCHAR, YEAR INT, AXIS_NUM INT, TIRE_NUM INT, USER_ID INT, PRIMARY KEY(VIN), FOREIGN KEY(USER_ID)REFERENCES USER(USER_ID))");
        ContentValues contentValues = new ContentValues();
        contentValues.put("MAKE", carmake);
        contentValues.put("MODEL", carmodel);
        contentValues.put("YEAR", tireyear);
        contentValues.put("AXIS_NUM", axisnum);
        contentValues.put("TIRE_NUM", tirenum);
        // Update or insert
        Cursor c = myDatabase.rawQuery("SELECT * FROM VEHICLE WHERE VIN = '" + vin + "'", null);
        if (c != null && c.moveToFirst()) {
            Log.i("In database", "update vehicle");
            c.close();
            myDatabase.update("VEHICLE", contentValues, "USER_ID = ? and VIN = ?", new String[]{Integer.toString(userid), vin});
        } else {
            contentValues.put("USER_ID", userid);
            contentValues.put("VIN", vin);
            Log.i("In database", "insert vehicle");
            c.close();
            myDatabase.insertOrThrow("VEHICLE", null, contentValues);
        }
    }

    // Updated by Yue Li and De Lan on 3/22/2018
    public static boolean storeTireData(String sensor_id, String manufacturer, String model, String sku, String vehicle_id, int axis_row, String axis_side, int axis_index, double init_thickness, int init_ss_id, int cur_ss_id) {
        myDatabase.execSQL("CREATE TABLE IF NOT EXISTS TIRE(SENSOR_ID VARCHAR, MANUFACTURER VARCHAR, MODEL VARCHAR, SKU VARCHAR, VEHICLE_ID VARCHAR, AXIS_ROW INT, AXIS_SIDE CHAR, AXIS_INDEX INT, INIT_THICKNESS DOUBLE, INIT_SS_ID INT, CUR_SS_ID INT, PRIMARY KEY(SENSOR_ID), FOREIGN KEY(VEHICLE_ID)REFERENCES VEHICLE(VIN))");
        ContentValues contentValues = new ContentValues();
        contentValues.put("MANUFACTURER", manufacturer);
        contentValues.put("MODEL", model);
        contentValues.put("SKU", sku);
        contentValues.put("INIT_THICKNESS", init_thickness);
        // Update or insert
        Cursor c = myDatabase.rawQuery("SELECT * FROM TIRE WHERE SENSOR_ID = '" + sensor_id + "'", null);
        if (c != null && c.moveToFirst()) {
            Cursor posQuery = myDatabase.rawQuery("SELECT * FROM TIRE WHERE VEHICLE_ID = '" + vehicle_id + "' and AXIS_ROW = " + axis_row + " and AXIS_INDEX = " + axis_index + " and AXIS_SIDE = '" + axis_side + "'", null);
            // This is edit update
            if (posQuery != null && posQuery.moveToFirst()) {
                Log.i("In database", "update tire succeeds!");
                posQuery.close();
                c.close();
                myDatabase.update("TIRE", contentValues, "SENSOR_ID = ? and VEHICLE_ID = ?", new String[]{sensor_id, vehicle_id});
            } // The user types in a existing sensor ID
            else {
                Log.i("In database", "update tire fails, sensorID exists!");
                return false;
            }
        } else {
            contentValues.put("SENSOR_ID", sensor_id);
            contentValues.put("VEHICLE_ID", vehicle_id);
            contentValues.put("AXIS_ROW", axis_row);
            contentValues.put("AXIS_SIDE", axis_side);
            contentValues.put("AXIS_INDEX", axis_index);
            contentValues.put("INIT_SS_ID", init_ss_id);
            contentValues.put("CUR_SS_ID", cur_ss_id);
            Log.i("In database", "insert tire");
            c.close();
            myDatabase.insertOrThrow("TIRE", null, contentValues);
        }
        return true;
    }

    /* Created by Zijie Wang on 3/24/2018. */
    public static double getInitThickness(String sensor_id) {
        Cursor c = myDatabase.rawQuery("SELECT * FROM TIRE WHERE SENSOR_ID = '" + sensor_id + "'", null);
        if(c != null && c.moveToFirst()){
            Double ans = c.getDouble(c.getColumnIndex("INIT_THICKNESS"));
            c.close();
            return ans;
        }
        else {
            Log.i("In database", "Sensor id not found");
            return 0;
        }
    }

    public static double[] getThickness(String sensor_id){
        Cursor c = myDatabase.rawQuery("SELECT * FROM SNAPSHOT WHERE TIRE_ID = '" + sensor_id + "'", null);
        double[] x = new double[60];
        int i = 0;
        if (c.moveToFirst()) {
            do {
                x[i] = c.getDouble(c.getColumnIndex("THICKNESS"));
                i++;

            } while (c.moveToNext());
            x[i] = -1;
            c.close();
            return x;
        }
        else {
            Log.i("snapshotTable", "There is nothing in snapshotTable");
            return null;
        }
    }

    // Updated by De Lan on 03/23/2018
    public static void storeSnapshot(double s11, String timestamp, double mileage, double pressure, String tire_id, boolean outlier, double thickness, String eol, String time_to_replacement, double longitutde, double lat) {
        myDatabase.execSQL("CREATE TABLE IF NOT EXISTS SNAPSHOT(ID INT, S11 DOUBLE, TIMESTAMP VARCHAR, MILEAGE DOUBLE, PRESSURE DOUBLE, TIRE_ID VARCHAR, OUTLIER BOOL, THICKNESS DOUBLE, EOL VARCHAR, TIME_TO_REPLACEMENT VARCHAR, LONG DOUBLE, LAT DOUBLE, PRIMARY KEY(ID), FOREIGN KEY(TIRE_ID)REFERENCES TIRE(SENSOR_ID))");
        // avoid duplication
        Cursor c = myDatabase.rawQuery("SELECT * FROM SNAPSHOT WHERE TIMESTAMP = '"+timestamp+"' and TIRE_ID = '"+tire_id+"'", null);
        if(c != null && c.moveToFirst()){
            Log.i("Snapshot duplication", "DUP!!!");
            c.close();
            return;
        }

        Cursor c_ID = myDatabase.rawQuery("SELECT MAX(ID) FROM SNAPSHOT", null);
        int id = 0;
        try {
            if (c_ID.getCount() > 0 && c_ID.moveToFirst()) {
                Log.i("Notify", "I'm in storeSnapshot.");
                id = c_ID.getInt(0);
                id += 1;
            }
        } catch (Exception e) {
            Log.i("storeSnapshot", e.getMessage());
        }
        c_ID.close();
        ContentValues contentValues = new ContentValues();
        contentValues.put("ID", id);
        contentValues.put("S11", s11);
        contentValues.put("TIMESTAMP", timestamp);
        contentValues.put("MILEAGE", mileage);
        contentValues.put("PRESSURE", pressure);
        contentValues.put("TIRE_ID", tire_id);
        contentValues.put("OUTLIER", outlier);
        contentValues.put("THICKNESS", thickness);
        contentValues.put("EOL", eol);
        contentValues.put("TIME_TO_REPLACEMENT", time_to_replacement);
        contentValues.put("LONG", longitutde);
        contentValues.put("LAT", lat);
        myDatabase.insertOrThrow("SNAPSHOT", null, contentValues);
    }

    // Added by De Lan on 03/23/2018
    public static boolean updateTireSSID(String sensor_ID){
        Cursor sensorExist = myDatabase.rawQuery("SELECT * FROM TIRE WHERE SENSOR_ID = '"+sensor_ID+"'", null);
        if(sensorExist == null || !sensorExist.moveToFirst()){
            Log.i("In updateTireSSID", sensor_ID+"The sensor_ID is not found in TIRE!");
            return false;
        }
        sensorExist.close();

        Cursor curr = myDatabase.rawQuery("SELECT MAX(ID) FROM SNAPSHOT WHERE TIRE_ID = '"+sensor_ID+"'", null);
        Cursor init = myDatabase.rawQuery("SELECT MIN(ID) FROM SNAPSHOT WHERE TIRE_ID = '"+sensor_ID+"'", null);
        ContentValues contentValues = new ContentValues();
        if(curr != null && curr.moveToFirst() && init != null && init.moveToFirst()){
            int curr_ss_id = curr.getInt(0);
            int init_ss_id = init.getInt(0);
            contentValues.put("INIT_SS_ID", init_ss_id);
            contentValues.put("CUR_SS_ID", curr_ss_id);
            myDatabase.update("TIRE", contentValues, "SENSOR_ID = ?", new String[]{sensor_ID});
            curr.close();
            init.close();
            Log.i("In updateTireSSID", sensor_ID+"The sensor_ID is updated in SNAPSHOT!");
            return true;
        }
        else{
            Log.i("In updateTireSSID", sensor_ID+"The sensor_ID is not found in SNAPSHOT!");
            return false;
        }
    }

    // TODO: SQL injection
    /* Updated by Yue Li and De Lan on 3/24/2018 */
    public static int getUserID(String email) {
        Cursor c = myDatabase.rawQuery("SELECT * FROM USER WHERE EMAIL = '" + email + "'", null);
        int res = -1;
        if(c != null && c.moveToFirst()) {
            res = c.getInt(c.getColumnIndex("USER_ID"));
            c.close();
        }
        return res;
    }

    /* Updated by De Lan on 3/24/2018 */
    public static int getVinUserID(String vin){
        Cursor c = myDatabase.rawQuery("SELECT * FROM VEHICLE WHERE VIN = '" + vin + "'", null);
        int res = -1;
        if(c != null && c.moveToFirst()) {
            res = c.getInt(c.getColumnIndex("USER_ID"));
            c.close();
        }
        return res;

    }


    /* Created by De Lan on 3/18/2018.*/
    public static User getUser(int user_id) {
        Cursor c = myDatabase.rawQuery("SELECT * FROM USER WHERE USER_ID = " + user_id + "", null);
        if (!c.moveToFirst()) {
            return null;
        }
        c.moveToFirst();
        String name = c.getString(c.getColumnIndex("NAME"));
        String email = c.getString(c.getColumnIndex("EMAIL"));
        String phonenum = c.getString(c.getColumnIndex("PHONE_NUMBER"));
        User curr_user = new User(name, email, phonenum);
        c.close();
        Cursor vehicle_cursor = myDatabase.rawQuery("SELECT * FROM VEHICLE WHERE USER_ID = " + user_id + "", null);

        if (vehicle_cursor.moveToFirst()) {
            do {
                String vin = vehicle_cursor.getString(vehicle_cursor.getColumnIndex("VIN"));
                String make = vehicle_cursor.getString(vehicle_cursor.getColumnIndex("MAKE"));
                String model = vehicle_cursor.getString(vehicle_cursor.getColumnIndex("MODEL"));
                int year = vehicle_cursor.getInt(vehicle_cursor.getColumnIndex("YEAR"));
                int axis_num = vehicle_cursor.getInt(vehicle_cursor.getColumnIndex("AXIS_NUM"));
                int tire_num = vehicle_cursor.getInt(vehicle_cursor.getColumnIndex("TIRE_NUM"));
                Vehicle curr_vehicle = new Vehicle(vin, make, model, year, axis_num, tire_num);
                curr_user.mVehicles.add(curr_vehicle);
            } while (vehicle_cursor.moveToNext());
        }
        vehicle_cursor.close();
        return curr_user;
    }

    /* Created by De Lan on 3/18/2018.*/
    //TODO: from tireinfo page to vehicleinfo page, exception
    public static Vehicle getVehicle(String vin) {
        Cursor c = myDatabase.rawQuery("SELECT * FROM VEHICLE WHERE VIN = '" + vin + "'", null);
        if (c == null) {
            return null;
        }
        c.moveToFirst();
        String make = c.getString(c.getColumnIndex("MAKE"));
        String model = c.getString(c.getColumnIndex("MODEL"));
        int year = c.getInt(c.getColumnIndex("YEAR"));
        int axis_num = c.getInt(c.getColumnIndex("AXIS_NUM"));
        int tire_num = c.getInt(c.getColumnIndex("TIRE_NUM"));
        Vehicle curr_vehicle = new Vehicle(vin, make, model, year, axis_num, tire_num);
        c.close();

        Cursor tire_cursor = myDatabase.rawQuery("SELECT * FROM TIRE WHERE VEHICLE_ID = '"+vin+"'", null);
        if (tire_cursor.moveToFirst()) {
            do {
                Tire curr_tire = tireHelper(tire_cursor);
                curr_vehicle.mTires.add(curr_tire);
            } while (tire_cursor.moveToNext());
        }
        tire_cursor.close();
        return curr_vehicle;
    }

    /* Created by De Lan on 3/18/2018.*/
    public static Tire tireHelper(Cursor c){
        String t_sensorId = c.getString(c.getColumnIndex("SENSOR_ID"));
        String t_manufacturer = c.getString(c.getColumnIndex("MANUFACTURER"));
        String t_model = c.getString(c.getColumnIndex("MODEL"));
        String t_sku = c.getString(c.getColumnIndex("SKU"));
        int t_row = c.getInt(c.getColumnIndex("AXIS_ROW"));
        char t_side = c.getString(c.getColumnIndex("AXIS_SIDE")).charAt(0);
        int t_index = c.getInt(c.getColumnIndex("AXIS_INDEX"));
        double t_init_thickness = c.getDouble(c.getColumnIndex("INIT_THICKNESS"));
        int t_init = c.getInt(c.getColumnIndex("INIT_SS_ID"));
        int t_curr = c.getInt(c.getColumnIndex("CUR_SS_ID"));
        double s11 = 0;
        double curr_thickness = 0;
        double odometer = 0;
        String eol = "Default";
        String repTime = "Default";
        Cursor curr_snap = myDatabase.rawQuery("SELECT * FROM SNAPSHOT WHERE ID = "+t_curr,null);
        if(curr_snap.moveToFirst()){
            s11 = curr_snap.getDouble(1);
            odometer = curr_snap.getDouble(3);
            curr_thickness = curr_snap.getDouble(7);
            eol = curr_snap.getString(8);
            repTime = curr_snap.getString(9);
            curr_snap.close();
        }
        Tire curr_tire = new Tire(t_sensorId, t_manufacturer, t_model, t_sku, t_row,
                t_side, t_index, t_init_thickness, curr_thickness, t_init, t_curr, s11, odometer, eol, repTime);
        return curr_tire;
    }



    /* Updated by De Lan on 3/24/2018 */
    public static Tire getTire(int axis_row, int axis_index, char axis_side, String vin) {
        Cursor c = myDatabase.rawQuery("SELECT * FROM TIRE WHERE VEHICLE_ID = '" + vin + "' and AXIS_ROW = " + axis_row + " and AXIS_INDEX = " + axis_index + " and AXIS_SIDE = '" + axis_side + "'", null);
        if (c.moveToFirst()) {
            Tire ans = tireHelper(c);
            c.close();
            return ans;
        } else {
            Log.i("getTire", "the tire not found!!!");
            return null;
        }
    }

    /* Created by YUE LI on 3/18/2018.*/
    /* Created by ZIJIE WANG on 3/18/2018.*/
    public static void testUserTable() {
        Cursor c = myDatabase.rawQuery("SELECT * FROM USER", null);
        if (c.moveToFirst()) {
            do {
                Log.i("id", c.getString(0));
                Log.i("name", c.getString(1));
                Log.i("email", c.getString(2));
                Log.i("phone", c.getString(3));
            } while (c.moveToNext());
        } else {
            Log.i("testUserTable", "There is nothing in testUserTable");
        }
        c.close();
    }

    /* Created by De Lan on 3/18/2018.*/
    public static void testTireTable() {
        Cursor c = myDatabase.rawQuery("SELECT * FROM TIRE", null);
        if (c.moveToFirst()) {
            do {
                Log.i("sensor_id", c.getString(0));
                Log.i("manufacturer", c.getString(1));
                Log.i("model", c.getString(2));
                Log.i("sku", c.getString(3));
                Log.i("vehicle_id", c.getString(4));
                Log.i("axis_row", c.getString(5));
                Log.i("axis_side", c.getString(6));
                Log.i("axis_index", c.getString(7));
                Log.i("init thickness", c.getString(8));
                Log.i("init ss ID", c.getString(9));
                Log.i("curr ss ID", c.getString(10));
            } while (c.moveToNext());
        } else {
            Log.i("testTireTable", "There is nothing in testTireTable");
        }
        c.close();
    }

    /* Created by De Lan on 3/23/2018.*/
    public static void testSnapTable() {
        Cursor c = myDatabase.rawQuery("SELECT * FROM SNAPSHOT", null);
        if (c.moveToFirst()) {
            do {
                Log.i("SNAPSHOT_id", c.getString(0));
                Log.i("s11", c.getString(1));
                Log.i("timestamp", c.getString(2));
                Log.i("mileage", c.getString(3));
                Log.i("pressure", c.getString(4));
                Log.i("tire_id", c.getString(5));
                Log.i("thickness", c.getString(7));
                Log.i("eol", c.getString(8));
                Log.i("time_to_replacement", c.getString(9));
                Log.i("longitutde", c.getString(10));
                Log.i("lat", c.getString(11));
            } while (c.moveToNext());
        } else {
            Log.i("testSnapTable", "There is nothing in testTireTable");
        }
        c.close();
    }
}

