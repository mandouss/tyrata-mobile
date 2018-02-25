package edu.duke.ece651.tyrata.vehicle;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import edu.duke.ece651.tyrata.R;
import edu.duke.ece651.tyrata.calibration.Input_Vehicle_Info;

public class Vehicle_Info extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vehicle_info);

        // Get the Intent that started this activity and extract the string
        Intent intent = getIntent();
        String message_make = intent.getStringExtra("MAKE");
        TextView textView_make = findViewById(R.id.textView_make);
        textView_make.setText(message_make);


        String message_model = intent.getStringExtra("MODEL");
        TextView textView_model = findViewById(R.id.textView_model);
        textView_model.setText(message_model);


        String message_year = intent.getStringExtra("YEAR");
        TextView textView_year = findViewById(R.id.textView_year);
        textView_year.setText(message_year);


        String message_vin = intent.getStringExtra("VIN");
        TextView textView_vin = findViewById(R.id.textView_vin);
        textView_vin.setText(message_vin);
    }
    public void switchToEdit(View view) {
        Intent intent = new Intent(Vehicle_Info.this, Input_Vehicle_Info.class);

        startActivity(intent);
        // Do something in response to button
    }
}
