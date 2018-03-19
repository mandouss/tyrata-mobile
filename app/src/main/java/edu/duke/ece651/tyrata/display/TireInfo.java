package edu.duke.ece651.tyrata.display;

/**
 * This class has Bluetooth API
 * @author De Lan
 * Created by Alan on 2/27/2018.
 */
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import edu.duke.ece651.tyrata.R;
import edu.duke.ece651.tyrata.calibration.TireInfoInput;
import edu.duke.ece651.tyrata.datamanagement.Database;
import edu.duke.ece651.tyrata.vehicle.Tire;

public class TireInfo extends AppCompatActivity {
    int axis_row;
    int axis_index;
    char axis_side;
    String vin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tire_info);
        Database.myDatabase = openOrCreateDatabase("TyrataData", MODE_PRIVATE, null);
        Tire curr_tire = Database.getTire("sensorID");

        // Get the Intent that started this activity and extract the string
        Intent intent = getIntent();
        String message_manufacturer = curr_tire.getManufacturer();
        if(message_manufacturer == null)
            message_manufacturer = "Default MAKE";
        TextView textView_manufacturer = findViewById(R.id.textView_manufacturer);
        textView_manufacturer.setText(message_manufacturer);


        String message_model = curr_tire.getModel();
        if(message_model == null)
            message_model = "Default MODEL";
        TextView textView_model = findViewById(R.id.textView_model);
        textView_model.setText(message_model);


        String message_SKU = curr_tire.getSku();
        if(message_SKU == null)
            message_SKU = "Default SKU";
        TextView textView_SKU = findViewById(R.id.textView_SKU);
        textView_SKU.setText(message_SKU);

        //TODO: calculate thickness
        String message_Thickness = intent.getStringExtra("tire_thickness");
        if(message_Thickness == null)
            message_Thickness = "Default THICKNESS";
        TextView textView_Thickness = findViewById(R.id.textView_thickness);
        textView_Thickness.setText(message_Thickness);


        axis_row = intent.getIntExtra("AXIS_ROW",0);
        axis_index = intent.getIntExtra("AXIS_INDEX",0);
        axis_side = intent.getCharExtra("AXIS_SIDE",'a');
        vin = intent.getStringExtra("VIN");

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

        intent.putExtra("axis_IDX", axis_index);
        intent.putExtra("axis_ROW",axis_row);
        intent.putExtra("axis_SIDE", axis_side);
        intent.putExtra("VIN",vin);

        startActivity(intent);
    }
    //TODO: call BT to for S11 and ODM ref
    public void switchToS11ODM(View view) {
        Intent intent = new Intent(TireInfo.this, TireInfoInput.class);

        startActivity(intent);
    }
}
