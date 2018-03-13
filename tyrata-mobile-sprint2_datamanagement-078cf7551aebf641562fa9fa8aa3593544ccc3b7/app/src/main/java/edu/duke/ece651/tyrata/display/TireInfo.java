package edu.duke.ece651.tyrata.display;


import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import edu.duke.ece651.tyrata.R;
import edu.duke.ece651.tyrata.calibration.TireInfoInput;

public class TireInfo extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tire_info);

        // Get the Intent that started this activity and extract the string
        Intent intent = getIntent();
        String message_make = intent.getStringExtra("tire_manufacturer");
        TextView textView_manufacturer = findViewById(R.id.textView_manufacturer);
        textView_manufacturer.setText(message_make);


        String message_model = intent.getStringExtra("tire_model");
        TextView textView_model = findViewById(R.id.textView_model);
        textView_model.setText(message_model);


        String message_SKU = intent.getStringExtra("tire_SKU");
        TextView textView_SKU = findViewById(R.id.textView_SKU);
        textView_SKU.setText(message_SKU);

        String message_Odometer = intent.getStringExtra("tire_odometer");
        TextView textView_Odometer = findViewById(R.id.textView_odometer);
        textView_Odometer.setText(message_Odometer);

        String message_Thickness = intent.getStringExtra("tire_thickness");
        TextView textView_Thickness = findViewById(R.id.textView_Thickness);
        textView_Thickness.setText(message_Thickness);

    }
    public void switchToEdit(View view) {
        Intent intent = new Intent(TireInfo.this, TireInfoInput.class);

        startActivity(intent);
    }
}
