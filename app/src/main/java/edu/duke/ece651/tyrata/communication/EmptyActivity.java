package edu.duke.ece651.tyrata.communication;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Location;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import edu.duke.ece651.tyrata.datamanagement.Database;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;

import edu.duke.ece651.tyrata.R;
import edu.duke.ece651.tyrata.datamanagement.Database;
import edu.duke.ece651.tyrata.processing.GPStracker;
import edu.duke.ece651.tyrata.vehicle.TireSnapshot;

public class EmptyActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_empty);
    }

    public ArrayList<Double> getGPS(View view) {
        ArrayList<Double> ans = new ArrayList<>();
        // Check for location permission
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            // Request permission for location
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    123);
        }

        GPStracker g = new GPStracker(getApplicationContext());
        Location l = g.getLocation();
        if (l != null) {
            Double lat = l.getLatitude();
            Double lon = l.getLongitude();
            ans.add(lat);
            ans.add(lon);
            Toast.makeText(getApplicationContext(), "LAT: " + lat + " \n LON : " + lon, Toast.LENGTH_LONG).show();
        }
        return ans;
    }

    public void goToBluetooth(View view) {
        Intent intent = new Intent(this, BluetoothActivity.class);
        startActivity(intent);
    }

    public void goToHTTP(View view) {
        Intent intent = new Intent(this, HttpActivity.class);
        startActivity(intent);
    }

    public void testParseXml(View view) {
        BluetoothXmlParser xmlParser = new BluetoothXmlParser();
        ArrayList<BluetoothXmlParser.DailyS11> list;
        try {
            list = xmlParser.parse(getResources().openRawResource(R.raw.xml_bluetooth_sample));
            String msg = "";
            if (list.isEmpty()) {
                msg = "No DailyS11...";
            } else {
                BluetoothXmlParser.DailyS11 dailyS11 = list.get(0);
                ArrayList<TireSnapshot> tires = dailyS11.mTires;
                msg = "Timestamp: " + dailyS11.mTimestamp;
                msg += ", Mileage: " + dailyS11.mMileage;
                msg += ", Tire #1: " + tires.get(0).getSensorId();
                msg += " S11: " + tires.get(0).getS11();
                msg += " Pressure: " + tires.get(0).getPressure();
                msg += ", Tire #2: " + tires.get(1).getSensorId();
                msg += " S11: " + tires.get(1).getS11();
                msg += " Pressure: " + tires.get(1).getPressure();
            }
            Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /* Updated by Zijie and Yue on 3/24/2018. */
    public void getTireSnapshotFromXml(View view) {
        ArrayList<Double> GPS = getGPS(view);
        BluetoothXmlParser xmlParser = new BluetoothXmlParser();
        try {
            TireSnapshot tireSnapshot = xmlParser.parseToTireSnapshot(getResources().openRawResource(R.raw.xml_bluetooth_sample));

            double s11 = tireSnapshot.getS11();
            String timestamp = TireSnapshot.convertCalendarToString(tireSnapshot.getTimestamp());
            double mileage = tireSnapshot.getOdometerMileage();
            double pressure = tireSnapshot.getPressure();
            String tire_id = tireSnapshot.getSensorId();
            //Log.i("sensorid", tire_id);

            Database.myDatabase = openOrCreateDatabase("TyrataData", MODE_PRIVATE, null);
            double init_thickness =  Database.getInitThickness(tire_id); //init_thickness
            Cursor c = Database.myDatabase.rawQuery("SELECT * FROM SNAPSHOT WHERE TIRE_ID = '"+tire_id+"'", null);
            double thickness = init_thickness;
            String eol = Double.toString((init_thickness - 3) * 5000);
            String time_to_replacement = timestamp;
            double longitutde = GPS.get(0);
            double lat = GPS.get(1);
            if(c != null && c.moveToFirst()) {
                double init_mS11 = c.getDouble(c.getColumnIndex("S11"));
                thickness = tireSnapshot.calculateTreadThickness(init_mS11, init_thickness);
                eol = Double.toString((thickness - 3) * 5000);
                time_to_replacement = timestamp;
                c.close();
                Log.i("Check eol", eol);
            }
            //Log.i("eol", eol);

            Database.storeSnapshot(s11, timestamp, mileage, pressure, tire_id, false, thickness, eol, time_to_replacement, longitutde, lat);
            boolean sensorExist = Database.updateTireSSID(tire_id);
            if(!sensorExist){
                throw new IOException();
            }
            Database.myDatabase.close();

            String msg = "";
            if (tireSnapshot == null) {
                msg = "Empty TireSnapshot...";
            } else {
                msg = "Tire/Sensor ID: " + tireSnapshot.getSensorId();
                msg += ", S11: " + tireSnapshot.getS11();
                msg += " Pressure: " + tireSnapshot.getPressure();
                msg += ", Mileage: " + tireSnapshot.getOdometerMileage();
                msg += ", Timestamp: " + TireSnapshot.convertCalendarToString(tireSnapshot.getTimestamp());
            }
            Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
        } catch (XmlPullParserException e) {
            notification(e.getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            String msg = "The sensor ID does not exist in local database, please check and enter valid sensor ID!";
            notification(msg);
            e.printStackTrace();
        }
    }

    public void getDatabaseFromXml(View view) {
        ServerXmlParser xmlParser = new ServerXmlParser();
        try {
            xmlParser.parse_server(getResources().openRawResource(R.raw.xml_get_from_server_sample), getApplicationContext());
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void getTireSnapshotListFromXml(View view) {
        BluetoothXmlParser xmlParser = new BluetoothXmlParser();
        try {
            ArrayList<TireSnapshot> tireSnapshotList = xmlParser.parseToTireSnapshotList(
                    getResources().openRawResource(R.raw.xml_bluetooth_sample));
            if (tireSnapshotList.isEmpty()){
                Toast.makeText(getApplicationContext(),
                        "Failed to obtain TireSnapshot from message received...",
                        Toast.LENGTH_LONG).show();
            }
            for (int i=0; i<tireSnapshotList.size(); i++) {
                String info = "Tire/Sensor ID: " + tireSnapshotList.get(i).getSensorId();
                info += "\nS11: " + tireSnapshotList.get(i).getS11();
                info += "\nPressure: " + tireSnapshotList.get(i).getPressure();
                info += "\nMileage: " + tireSnapshotList.get(i).getOdometerMileage();
                info += "\nTimestamp: " + TireSnapshot.convertCalendarToString(tireSnapshotList.get(i).getTimestamp());
                Toast.makeText(getApplicationContext(), info, Toast.LENGTH_SHORT).show();
            }
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


    private void notification(String msg){
        new AlertDialog.Builder(this)
                .setTitle("NOTIFICATION")
                .setMessage(msg)
                .setPositiveButton("Yes", null)
                .show();
    }
}
