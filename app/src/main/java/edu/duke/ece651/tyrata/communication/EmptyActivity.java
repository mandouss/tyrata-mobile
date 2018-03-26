package edu.duke.ece651.tyrata.communication;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;

import edu.duke.ece651.tyrata.R;
import edu.duke.ece651.tyrata.processing.GPStracker;
import edu.duke.ece651.tyrata.vehicle.TireSnapshot;

public class EmptyActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_empty);
    }

    public void getGPS(View view) {
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
            double lat = l.getLatitude();
            double lon = l.getLongitude();
            Toast.makeText(getApplicationContext(), "LAT: " + lat + " \n LON : " + lon, Toast.LENGTH_LONG).show();
        }
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

    public void getTireSnapshotFromXml(View view) {
        BluetoothXmlParser xmlParser = new BluetoothXmlParser();
        try {
            TireSnapshot tireSnapshot = xmlParser.parseToTireSnapshot(getResources().openRawResource(R.raw.xml_bluetooth_sample));
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
    }
}
