package com.example.myfirstapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class TireInfoInput extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tire_info_input);
    }
    /** Called when the user taps the Submit button */
    public void saveMessage(View view) {

        Intent intent = new Intent(this, TireInfo.class);
        EditText edit_make = (EditText) findViewById(R.id.edit_manufacturer);
        String message_make = edit_make.getText().toString();
        intent.putExtra("tire_manufacturer", message_make);


        EditText edit_model = (EditText) findViewById(R.id.edit_model);
        String message_model = edit_model.getText().toString();
        intent.putExtra("tire_model", message_model);


        EditText edit_SKU = (EditText) findViewById(R.id.edit_SKU);
        String message_SKU = edit_SKU.getText().toString();
        intent.putExtra("tire_SKU", message_SKU);

        EditText edit_odometer = (EditText) findViewById(R.id.edit_odometer);
        String message_odometer = edit_odometer.getText().toString();
        intent.putExtra("tire_odometer", message_odometer);

        EditText edit_thickness = (EditText) findViewById(R.id.edit_thickness);
        String message_thickness = edit_thickness.getText().toString();
        intent.putExtra("tire_thickness", message_thickness);

        startActivity(intent);
    }
}
