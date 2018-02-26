package edu.duke.ece651.tyrata.calibration;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import edu.duke.ece651.tyrata.R;

public class Input_Vehicle_Info extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input__vehicle__info);
    }
    public void saveMessage(View view) {

        Intent intent = new Intent(this, edu.duke.ece651.tyrata.vehicle.Vehicle_Info.class);
        EditText edit_make = (EditText) findViewById(R.id.edit_make);
        String message_make = edit_make.getText().toString();
        intent.putExtra("MAKE", message_make);


        EditText edit_model = (EditText) findViewById(R.id.edit_model);
        String message_model = edit_model.getText().toString();
        intent.putExtra("MODEL", message_model);


        EditText edit_year = (EditText) findViewById(R.id.edit_year);
        String message_year = edit_year.getText().toString();
        intent.putExtra("YEAR", message_year);


        EditText edit_vin = (EditText) findViewById(R.id.edit_vin);
        String message_vin = edit_vin.getText().toString();
        intent.putExtra("VIN", message_vin);
        startActivity(intent);

        // Do something in response to button
    }
}
