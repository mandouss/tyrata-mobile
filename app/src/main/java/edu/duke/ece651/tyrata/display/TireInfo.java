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
        if(message_make == null)
            message_make = "Default MAKE";
        TextView textView_manufacturer = findViewById(R.id.textView_manufacturer);
        textView_manufacturer.setText(message_make);


        String message_model = intent.getStringExtra("tire_model");
        if(message_model == null)
            message_model = "Default MODEL";
        TextView textView_model = findViewById(R.id.textView_model);
        textView_model.setText(message_model);


        String message_SKU = intent.getStringExtra("tire_SKU");
        if(message_SKU == null)
            message_SKU = "Default SKU";
        TextView textView_SKU = findViewById(R.id.textView_SKU);
        textView_SKU.setText(message_SKU);


        String message_Thickness = intent.getStringExtra("tire_thickness");
        if(message_Thickness == null)
            message_Thickness = "Default THICKNESS";
        TextView textView_Thickness = findViewById(R.id.textView_thickness);
        textView_Thickness.setText(message_Thickness);


        int axis_row = intent.getIntExtra("AXIS_ROW",0);
        int axis_index = intent.getIntExtra("AXIS_INDEX",0);
        char axis_side = intent.getCharExtra("AXIS_SIDE",'a');


        /* @TODO: read S11 and odometer from database, sync with BT*/
        String message_Odometer = "odometer from BT";
        TextView textView_Odometer = findViewById(R.id.textView_odometer);
        textView_Odometer.setText(message_Odometer);

        String message_S11 = "S11 from BT";
        TextView textView_S11 = findViewById(R.id.textView_S11);
        textView_S11.setText(message_S11);

        String message_EOL = "EOL from calculation";
        TextView textView_EOL = findViewById(R.id.textView_EOL);
        textView_EOL.setText(message_EOL);

        String message_rep = "time to rep from calculation";
        TextView textView_rep = findViewById(R.id.textView_replace);
        textView_rep.setText(message_rep);

    }
    public void switchToEdit(View view) {
        Intent intent = new Intent(TireInfo.this, TireInfoInput.class);

        startActivity(intent);
    }
    //TODO: call BT to for S11 and ODM ref
    public void switchToS11ODM(View view) {
        Intent intent = new Intent(TireInfo.this, TireInfoInput.class);

        startActivity(intent);
    }
}
