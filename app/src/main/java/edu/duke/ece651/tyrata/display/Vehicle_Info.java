package edu.duke.ece651.tyrata.display;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import edu.duke.ece651.tyrata.R;
import edu.duke.ece651.tyrata.calibration.Input_Vehicle_Info;

public class Vehicle_Info extends AppCompatActivity {
    private Integer buttonnumber = 0;
    private Button left3;
    private Button right3;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vehicle__info);

        // Get the Intent that started this activity and extract the string
        Intent intent = getIntent();
        String message_make = intent.getStringExtra("MAKE");
        if(message_make == null){
            message_make = "";
        }
        TextView textView_make = findViewById(R.id.textView_make);
        textView_make.setText(message_make);


        String message_model = intent.getStringExtra("MODEL");
        if(message_model == null){
            message_model = "";
        }
        TextView textView_model = findViewById(R.id.textView_model);
        textView_model.setText(message_model);


        String message_year = intent.getStringExtra("YEAR");
        if(message_year == null){
            message_year = "";
        }
        TextView textView_year = findViewById(R.id.textView_year);
        textView_year.setText(message_year);


        String message_vin = intent.getStringExtra("VIN");
        if(message_vin == null){
            message_vin = "";
        }
        TextView textView_vin = findViewById(R.id.textView_vin);
        textView_vin.setText(message_vin);

        String message_tirenumber = intent.getStringExtra("TIRENUMBER");
        if(message_tirenumber == null){
            message_tirenumber = "4";
        }
        TextView textView_tirenumber = findViewById(R.id.textView_tirenumber);
        textView_tirenumber.setText(message_tirenumber);

        buttonnumber=Integer.parseInt(message_tirenumber);
        left3 = (Button)findViewById(R.id.button_letf3);
        right3 = (Button)findViewById(R.id.button_right3);
        if( buttonnumber==6){
            left3.setVisibility(View.VISIBLE);
            right3.setVisibility(View.VISIBLE);
        }
    }

    public void switchToEdit(View view) {
        Intent intent = new Intent(Vehicle_Info.this, Input_Vehicle_Info.class);

        startActivity(intent);
        // Do something in response to button
    }
    public void vehicle_to_tire (View view) {
        Intent intent = new Intent(Vehicle_Info.this, TireInfo.class);

        startActivity(intent);
        // Do something in response to button
    }


}
