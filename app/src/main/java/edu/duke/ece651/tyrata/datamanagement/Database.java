package edu.duke.ece651.tyrata.datamanagement;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

/**
 * Created by Yuei on 3/4/18.
 */

public class Database extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main)

    }

    public static SQLiteDatabase myDatabase;

    public static void createTable(){

        myDatabase.execSQL("CREATE TABLE IF NOT EXISTS USER (USER_ID INT, NAME VARCHAR, EMAIL VARCHAR, PHONE_NUMBER VARCHAR, PRIMARY KEY(USER_ID))");
        myDatabase.execSQL("CREATE TABLE IF NOT EXISTS VEHICLE (VIN VARCHAR, MAKE VARCHAR, MODEL VARCHAR, YEAR INT, AXIS_NUM INT, TIRE_NUM INT, USER_ID VARCHAR, PRIMARY KEY(VIN), FOREIGN KEY(USER_ID)REFERENCES USER(USER_ID))");
        myDatabase.execSQL("CREATE TABLE IF NOT EXISTS TIRE(SENSOR_ID VARCHAR, MANUFACTURER VARCHAR, MODEL VARCHAR, SKU VARCHAR, VEHICLE_ID VARCHAR, AXIS_ROW INT, AXIS_SIDE CHAR, AXIS_INDEX INT, INIT_SS_ID INT, CUR_SS_ID INT, PRIMARY KEY(SENSOR_ID), FOREIGN KEY(VEHICLE_ID)REFERENCES VEHICLE(VIN), FOREIGN KEY(INIT_SS_ID)REFERENCES SNAPSHOT(ID), FOREIGN KEY(CUR_SS_ID)REFERENCES SNAPSHOT(ID))");
        myDatabase.execSQL("CREATE TABLE IF NOT EXISTS SNAPSHOT(ID INT, S11 DOUBLE, TIMESTAMP VARCHAR, MILEAGE DOUBLE, PRESSURE DOUBLE, TIRE_ID VARCHAR, OUTLIER BOOL, THICKNESS DOUBLE, EOL VARCHAR, TIME_TO_REPLACEMENT VARCHAR, LONG DOUBLE, LAT DOUBLE, PRIMARY KEY(ID), FOREIGN KEY(TIRE_ID)REFERENCES TIRE(SENSOR_ID))");
    }

    public static void storeUserData(String name, String email, String phone){
        myDatabase.execSQL("DROP TABLE IF EXISTS USER");
        myDatabase.execSQL("CREATE TABLE IF NOT EXISTS USER (USER_ID INT, NAME VARCHAR, EMAIL VARCHAR, PHONE_NUMBER VARCHAR, PRIMARY KEY(USER_ID))");
        final String MY_QUERY = "SELECT MAX(USER_ID) FROM USER";
        Cursor c = myDatabase.rawQuery(MY_QUERY, null);
        int user_id = 0;
        try {
            if (c.getCount() > 0) {
                Log.i("Notify", "I'm in.");
                c.moveToFirst();
                user_id = c.getInt(0);
                user_id += 1;
            }
        }
        catch (Exception e) {
            Log.i("ERROR", e.getMessage());
        }
        c.close();
        ContentValues contentValues = new ContentValues();

        contentValues.put("USER_ID", user_id);
        contentValues.put("NAME", name);
        contentValues.put("EMAIL", email);
        contentValues.put("PHONE_NUMBER", phone);
        myDatabase.insert("USER", null, contentValues);

    }


    public static void storeVehicleData(String vin, String carmodel, String carmake, int tireyear, int axisnum, int tirenum, int userid){
        myDatabase.execSQL("CREATE TABLE IF NOT EXISTS VEHICLE (VIN VARCHAR, MAKE VARCHAR, MODEL VARCHAR, YEAR INT, AXIS_NUM INT, TIRE_NUM INT, USER_ID INT, PRIMARY KEY(VIN), FOREIGN KEY(USER_ID)REFERENCES USER(USER_ID))");

        //myDatabase.execSQL("INSERT INTO VEHICLE (VIN, MAKE, MODEL, YEAR, AXIS_NUM, TIRE_NUM, USER_ID) VALUES ('"+vin+"', '"+carmake+"', '"+carmodel+"', 'tireyear', 'axisnum', 'tirenum', '"+userid+"')");
        ContentValues contentValues = new ContentValues();
        contentValues.put("VIN", vin);
        contentValues.put("MAKE", carmake);
        contentValues.put("MODEL", carmodel);
        contentValues.put("YEAR", tireyear);
        contentValues.put("AXIS_NUM", axisnum);
        contentValues.put("TIRE_NUM", tirenum);
        contentValues.put("USER_ID", userid);
        myDatabase.insert("VEHICLE", null, contentValues);
    }

    public static void storeTireData(String sensor_id, String manufacturer, String model, String sku, String vehicle_id, int axis_row, String axis_side, int axis_index, int init_ss_id, int cur_ss_id ){
        myDatabase.execSQL("CREATE TABLE IF NOT EXISTS TIRE(SENSOR_ID VARCHAR, MANUFACTURER VARCHAR, MODEL VARCHAR, SKU VARCHAR, VEHICLE_ID VARCHAR, AXIS_ROW INT, AXIS_SIDE CHAR, AXIS_INDEX INT, INIT_SS_ID INT, CUR_SS_ID INT, PRIMARY KEY(SENSOR_ID), FOREIGN KEY(VEHICLE_ID)REFERENCES VEHICLE(VIN), FOREIGN KEY(INIT_SS_ID)REFERENCES SNAPSHOT(ID), FOREIGN KEY(CUR_SS_ID)REFERENCES SNAPSHOT(ID))");
        //myDatabase.execSQL("INSERT INTO TIRE (SENSOR_ID, MANUFACTURER, MODEL, SKU, VEHICLE_ID, AXIS_ROW, AXIS_SIDE, AXIS_INDEX, INIT_SS_ID, CUR_SS_ID) VALUES ('"+sensor_id+"', '"+model+"', '"+sku+"', '"+vehicle_id+"', 'axis_row', 'axis_side', 'axis_index', 'init_ss_id', 'cur_ss_id')");
        ContentValues contentValues = new ContentValues();
        contentValues.put("SENSOR_ID", sensor_id);
        contentValues.put("MANUFACTURER", manufacturer);
        contentValues.put("MODEL", model);
        contentValues.put("SKU", sku);
        contentValues.put("VEHICLE_ID", vehicle_id);
        contentValues.put("AXIS_ROW", axis_row);
        contentValues.put("AXIS_SIDE", axis_side);
        contentValues.put("AXIS_INDEX", axis_index);
        contentValues.put("INIT_SS_ID", init_ss_id);
        contentValues.put("CUR_SS_ID", cur_ss_id);
        myDatabase.insert("TIRE", null, contentValues);


    }

    public static void storeSnapshot(int id, double s11, String timestamp, double mileage, double pressure, String tire_id, boolean outlier, double thickness, String eol, String time_to_replacement, double longitutde, double lat ){
        myDatabase.execSQL("CREATE TABLE IF NOT EXISTS SNAPSHOT(ID INT, S11 DOUBLE, TIMESTAMP VARCHAR, MILEAGE DOUBLE, PRESSURE DOUBLE, TIRE_ID VARCHAR, OUTLIER BOOL, THICKNESS DOUBLE, EOL VARCHAR, TIME_TO_REPLACEMENT VARCHAR, LONG DOUBLE, LAT DOUBLE, PRIMARY KEY(ID), FOREIGN KEY(TIRE_ID)REFERENCES TIRE(SENSOR_ID))");
        //myDatabase.execSQL("INSERT INTO TIRE (ID, S11, TIMESTAMP, MILEAGE, PRESSURE, TIRE_ID, OUTLIER, THICKNESS, EOL, TIME_TO_REPLACEMENT, LONG, LAT) VALUES ('id', 's11', '"+timestamp+"', 'mileage', 'pressure', '"+tire_id+"', 'outlier', 'thickness', '"+eol+"', '"+time_to_replacement+"', 'longitude', 'lat')");
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
        myDatabase.insert("SNAPSHOT", null, contentValues);
    }

    public static int Userid(String email){
        Cursor c = myDatabase.rawQuery("SELECT * FROM USER WHERE EMAIL = '"+email+"'", null);
        c.moveToFirst();
        int res = c.getInt(c.getColumnIndex("USER_ID"));
        return res;
    }

    public static boolean Userexist(String email){
        Cursor c = myDatabase.rawQuery("SELECT * FROM USER WHERE EMAIL = '"+email+"'", null);
        if(c.getCount() <=0){
            c.close();
            return false;
        }
        c.close();
        return true;

    }

    public static void testUserTable(){

        Cursor c = myDatabase.rawQuery("SELECT * FROM USER", null);
        c.moveToFirst();
        Log.i("id", c.getString(0));
        Log.i("name", c.getString(1));
        Log.i("email", c.getString(2));
        Log.i("phone", c.getString(3));

        while(c.moveToNext()) {

            Log.i("id", c.getString(0));
            Log.i("name", c.getString(1));
            Log.i("email", c.getString(2));
            Log.i("phone", c.getString(3));
        }

    }



}

