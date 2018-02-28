package edu.duke.ece651.tyrata;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import edu.duke.ece651.tyrata.user.Log_in;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
    public void main_to_addcar(View view) {
        Intent intent = new Intent(MainActivity.this, edu.duke.ece651.tyrata.calibration.Input_Vehicle_Info.class);

        startActivity(intent);
        // Do something in response to button
    }
    public void main_to_report(View view) {
        Intent intent = new Intent(MainActivity.this, edu.duke.ece651.tyrata.vehicle.Report_accident.class);

        startActivity(intent);
        // Do something in response to button
    }

    public void main_to_vehicleinfo(View view) {
        Intent intent = new Intent(MainActivity.this, edu.duke.ece651.tyrata.vehicle.Vehicle_Info.class);

        startActivity(intent);
        // Do something in response to button
    }
}
