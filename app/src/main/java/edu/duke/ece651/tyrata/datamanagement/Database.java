package edu.duke.ece651.tyrata.datamanagement;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import edu.duke.ece651.tyrata.user.User;
import edu.duke.ece651.tyrata.vehicle.Tire;
import edu.duke.ece651.tyrata.vehicle.Vehicle;

/**
 * Created by Yuei on 3/4/18.
 * Updated by De Lan on 3/18/2018: getUser(), getVehicle(), getTire()
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
        myDatabase.execSQL("CREATE TABLE IF NOT EXISTS TIRE(SENSOR_ID VARCHAR, MANUFACTURER VARCHAR, MODEL VARCHAR, SKU VARCHAR, VEHICLE_ID VARCHAR, AXIS_ROW INT, AXIS_SIDE CHAR, AXIS_INDEX INT, INIT_THICKNESS DOUBLE, INIT_SS_ID INT, CUR_SS_ID INT, PRIMARY KEY(SENSOR_ID), FOREIGN KEY(VEHICLE_ID)REFERENCES VEHICLE(VIN), FOREIGN KEY(INIT_SS_ID)REFERENCES SNAPSHOT(ID), FOREIGN KEY(CUR_SS_ID)REFERENCES SNAPSHOT(ID))");
        myDatabase.execSQL("CREATE TABLE IF NOT EXISTS SNAPSHOT(ID INT, S11 DOUBLE, TIMESTAMP VARCHAR, MILEAGE DOUBLE, PRESSURE DOUBLE, TIRE_ID VARCHAR, OUTLIER BOOL, THICKNESS DOUBLE, EOL VARCHAR, TIME_TO_REPLACEMENT VARCHAR, LONG DOUBLE, LAT DOUBLE, PRIMARY KEY(ID), FOREIGN KEY(TIRE_ID)REFERENCES TIRE(SENSOR_ID))");
    }

    public static void dropAllTable(){
        myDatabase.execSQL("DROP TABLE IF EXISTS TIRE");
        myDatabase.execSQL("DROP TABLE IF EXISTS SNAPSHOT");
        myDatabase.execSQL("DROP TABLE IF EXISTS VEHICLE");
        myDatabase.execSQL("DROP TABLE IF EXISTS USER");
    }

    public static void storeUserData(String name, String email, String phone){
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
        myDatabase.insertOrThrow("USER", null, contentValues);

    }

    // Updated by Yue Li and De Lan on 3/22/2018
    public static void storeVehicleData(String vin, String carmodel, String carmake, int tireyear, int axisnum, int tirenum, int userid){
        myDatabase.execSQL("CREATE TABLE IF NOT EXISTS VEHICLE (VIN VARCHAR, MAKE VARCHAR, MODEL VARCHAR, YEAR INT, AXIS_NUM INT, TIRE_NUM INT, USER_ID INT, PRIMARY KEY(VIN), FOREIGN KEY(USER_ID)REFERENCES USER(USER_ID))");
        ContentValues contentValues = new ContentValues();
        contentValues.put("MAKE", carmake);
        contentValues.put("MODEL", carmodel);
        contentValues.put("YEAR", tireyear);
        contentValues.put("AXIS_NUM", axisnum);
        contentValues.put("TIRE_NUM", tirenum);
        // Update or insert
        Cursor c = myDatabase.rawQuery("SELECT * FROM VEHICLE WHERE VIN = '"+vin+"'", null);
        if(c != null && c.moveToFirst()){
            Log.i("In database","update vehicle");
            c.close();
            myDatabase.update("VEHICLE", contentValues, "USER_ID = ? and VIN = ?", new String[] {Integer.toString(userid), vin});
        }else{
            contentValues.put("USER_ID", userid);
            contentValues.put("VIN", vin);
            Log.i("In database","insert vehicle");
            c.close();
            myDatabase.insertOrThrow("VEHICLE", null, contentValues);
        }
    }

    // Updated by Yue Li and De Lan on 3/22/2018
    public static void storeTireData(String sensor_id, String manufacturer, String model, String sku, String vehicle_id, int axis_row, String axis_side, int axis_index, double init_thickness, int init_ss_id, int cur_ss_id ){
        myDatabase.execSQL("CREATE TABLE IF NOT EXISTS TIRE(SENSOR_ID VARCHAR, MANUFACTURER VARCHAR, MODEL VARCHAR, SKU VARCHAR, VEHICLE_ID VARCHAR, AXIS_ROW INT, AXIS_SIDE CHAR, AXIS_INDEX INT, INIT_THICKNESS DOUBLE, INIT_SS_ID INT, CUR_SS_ID INT, PRIMARY KEY(SENSOR_ID), FOREIGN KEY(VEHICLE_ID)REFERENCES VEHICLE(VIN), FOREIGN KEY(INIT_SS_ID)REFERENCES SNAPSHOT(ID), FOREIGN KEY(CUR_SS_ID)REFERENCES SNAPSHOT(ID))");
        ContentValues contentValues = new ContentValues();
        contentValues.put("MANUFACTURER", manufacturer);
        contentValues.put("MODEL", model);
        contentValues.put("SKU", sku);
        contentValues.put("AXIS_ROW", axis_row);
        contentValues.put("AXIS_SIDE", axis_side);
        contentValues.put("AXIS_INDEX", axis_index);
        contentValues.put("INIT_THICKNESS", init_thickness);
        contentValues.put("INIT_SS_ID", init_ss_id);
        contentValues.put("CUR_SS_ID", cur_ss_id);
        // Update or insert
        Cursor c = myDatabase.rawQuery("SELECT * FROM TIRE WHERE SENSOR_ID = '"+sensor_id+"'", null);
        if(c != null && c.moveToFirst()){
            Log.i("In database","update tire");
            c.close();
            myDatabase.update("TIRE", contentValues, "SENSOR_ID = ? and VEHICLE_ID = ?", new String[] {sensor_id, vehicle_id});
        }else{
            contentValues.put("SENSOR_ID", sensor_id);
            contentValues.put("VEHICLE_ID", vehicle_id);
            Log.i("In database","insert tire");
            c.close();
            myDatabase.insertOrThrow("TIRE", null, contentValues);
        }

    }

    public static void storeSnapshot(int id, double s11, String timestamp, double mileage, double pressure, String tire_id, boolean outlier, double thickness, String eol, String time_to_replacement, double longitutde, double lat ){
        myDatabase.execSQL("CREATE TABLE IF NOT EXISTS SNAPSHOT(ID INT, S11 DOUBLE, TIMESTAMP VARCHAR, MILEAGE DOUBLE, PRESSURE DOUBLE, TIRE_ID VARCHAR, OUTLIER BOOL, THICKNESS DOUBLE, EOL VARCHAR, TIME_TO_REPLACEMENT VARCHAR, LONG DOUBLE, LAT DOUBLE, PRIMARY KEY(ID), FOREIGN KEY(TIRE_ID)REFERENCES TIRE(SENSOR_ID))");
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
    // TODO: SQL injection
    public static int Userid(String email){
        Cursor c = myDatabase.rawQuery("SELECT * FROM USER WHERE EMAIL = '"+email+"'", null);
        c.moveToFirst();
        int res = c.getInt(c.getColumnIndex("USER_ID"));
        return res;
    }

    public static boolean Userexist(String email){
        Cursor c = myDatabase.rawQuery("SELECT * FROM USER WHERE EMAIL = '"+email+"'", null);
        if(c.getCount() <=0 ){
            c.close();
            return false;
        }
        c.close();
        return true;
    }

    /* Created by De Lan on 3/18/2018.*/
    public static User getUser(int user_id){
        Cursor c = myDatabase.rawQuery("SELECT * FROM USER WHERE USER_ID = "+user_id+"", null);
        if(!c.moveToFirst()) {
            return null;
        }
        c.moveToFirst();
        String name = c.getString(c.getColumnIndex("NAME"));
        String email = c.getString(c.getColumnIndex("EMAIL"));
        String phonenum = c.getString(c.getColumnIndex("PHONE_NUMBER"));
        User curr_user = new User(name, email, phonenum);
        c.close();
        Cursor vehicle_cursor = myDatabase.rawQuery("SELECT * FROM VEHICLE WHERE USER_ID = "+user_id+"", null);

        if (vehicle_cursor.moveToFirst()){
            do{
                String vin = vehicle_cursor.getString(vehicle_cursor.getColumnIndex("VIN"));
                String make = vehicle_cursor.getString(vehicle_cursor.getColumnIndex("MAKE"));
                String model = vehicle_cursor.getString(vehicle_cursor.getColumnIndex("MODEL"));
                int year = vehicle_cursor.getInt(vehicle_cursor.getColumnIndex("YEAR"));
                int axis_num = vehicle_cursor.getInt(vehicle_cursor.getColumnIndex("AXIS_NUM"));
                int tire_num = vehicle_cursor.getInt(vehicle_cursor.getColumnIndex("TIRE_NUM"));
                Vehicle curr_vehicle = new Vehicle(vin, make, model, year, axis_num, tire_num);
                curr_user.mVehicles.add(curr_vehicle);
            }while(vehicle_cursor.moveToNext());
        }
        vehicle_cursor.close();
        return curr_user;
    }

    /* Created by De Lan on 3/18/2018.*/
    //TODO: from tireinfo page to vehicleinfo page, exception
    public static Vehicle getVehicle(String vin){
        Cursor c = myDatabase.rawQuery("SELECT * FROM VEHICLE WHERE VIN = '"+vin+"'", null);
        if(c == null) {
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
        if (tire_cursor.moveToFirst()){
            do{
                String t_sensorId = tire_cursor.getString(tire_cursor.getColumnIndex("SENSOR_ID"));
                String t_manufacturer = tire_cursor.getString(tire_cursor.getColumnIndex("MANUFACTURER"));
                String t_model = tire_cursor.getString(tire_cursor.getColumnIndex("MODEL"));
                String t_sku = tire_cursor.getString(tire_cursor.getColumnIndex("SKU"));
                int t_row = tire_cursor.getInt(tire_cursor.getColumnIndex("AXIS_ROW"));
                char t_side = tire_cursor.getString(tire_cursor.getColumnIndex("AXIS_SIDE")).charAt(0);
                int t_index = tire_cursor.getInt(tire_cursor.getColumnIndex("AXIS_INDEX"));
                double t_init_thickness = tire_cursor.getDouble(tire_cursor.getColumnIndex("INIT_THICKNESS"));
                int t_init = tire_cursor.getInt(tire_cursor.getColumnIndex("INIT_SS_ID"));
                int t_curr = tire_cursor.getInt(tire_cursor.getColumnIndex("CUR_SS_ID"));

                Tire curr_tire = new Tire(t_sensorId, t_manufacturer, t_model, t_sku, t_row, t_side, t_index, t_init_thickness, t_init, t_curr);
                curr_vehicle.mTires.add(curr_tire);
            }while(tire_cursor.moveToNext());
        }
        tire_cursor.close();
        return curr_vehicle;
    }

    /* Created by De Lan on 3/18/2018.*/
    public static Tire getTire(int axis_row, int axis_index, char axis_side, String vin){
        Cursor c = myDatabase.rawQuery("SELECT * FROM TIRE WHERE VEHICLE_ID = '"+vin+"' and AXIS_ROW = "+axis_row+" and AXIS_INDEX = "+axis_index+" and AXIS_SIDE = '"+axis_side+"'", null);
        if(c.moveToFirst()){
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
            c.close();
            Tire curr_tire = new Tire(t_sensorId, t_manufacturer, t_model, t_sku, t_row, t_side, t_index, t_init_thickness, t_init, t_curr);
            return curr_tire;
        }
        else{
            Log.i("getTire", "the tire not found!!!");
            return null;
        }
    }

    public static void testUserTable(){
        Cursor c = myDatabase.rawQuery("SELECT * FROM USER", null);
        if(c.moveToFirst()) {
            do {
                Log.i("id", c.getString(0));
                Log.i("name", c.getString(1));
                Log.i("email", c.getString(2));
                Log.i("phone", c.getString(3));
            }while(c.moveToNext());
        }
        else{
            Log.i("testUserTable", "There is nothing in testUserTable");
        }
        c.close();
    }

    /* Created by De Lan on 3/18/2018.*/
    public static void testTireTable(){
        Cursor c = myDatabase.rawQuery("SELECT * FROM TIRE", null);
        if(c.moveToFirst()) {
            do {
                Log.i("sensor_id", c.getString(0));
                Log.i("manufacturer", c.getString(1));
                Log.i("model", c.getString(2));
                Log.i("sku", c.getString(3));
                Log.i("vehicle_id", c.getString(4));
                Log.i("axis_row", c.getString(5));
                Log.i("axis_side", c.getString(6));
                Log.i("axis_index", c.getString(7));
            }while(c.moveToNext());
        }
        else{
            Log.i("testTireTable", "There is nothing in testTireTable");
        }
        c.close();
    }



}

