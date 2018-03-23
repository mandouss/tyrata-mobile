package edu.duke.ece651.tyrata.calibration;
/**
 * This class is Tireinfo input page
 * @author De Lan
 * Created by De Lan on 2/27/2018.
 */
import android.app.AlertDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import java.io.IOException;
import edu.duke.ece651.tyrata.R;
import edu.duke.ece651.tyrata.datamanagement.Database;
import edu.duke.ece651.tyrata.display.TireInfo;

public class TireInfoInput extends AppCompatActivity {
    int axis_row;
    int axis_index;
    char axis_side;
    String vin;
    String msg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tire_info_input);
        Intent intent = getIntent();

        axis_row = intent.getIntExtra("axis_ROW",0);
        axis_index = intent.getIntExtra("axis_IDX",0);
        axis_side = intent.getCharExtra("axis_SIDE",'a');
        vin = intent.getStringExtra("VIN");
        // switched from tire edit
        // @TODO add deletion button
        String sensor_id = intent.getStringExtra("SENSOR_ID");
        if(sensor_id != null && !sensor_id.equals("Default sensorID")) {
            Log.i("Tire Input edit", sensor_id);
            EditText textView_sensor = findViewById(R.id.edit_sensor_ID);
            textView_sensor.setText(sensor_id);
            textView_sensor.setKeyListener(null);
        }else{
            Log.i("Vehicle Input add_car", "add_car");
        }
    }
    /** Called when the user taps the Submit button */
    public void saveMessage(View view) {
        Intent intent = new Intent(this, TireInfo.class);
        EditText edit_manufacturer = (EditText) findViewById(R.id.edit_manufacturer);
        String message_manufacturer = edit_manufacturer.getText().toString();

        EditText edit_model = (EditText) findViewById(R.id.edit_model);
        String message_model = edit_model.getText().toString();

        EditText edit_SKU = (EditText) findViewById(R.id.edit_SKU);
        String message_SKU = edit_SKU.getText().toString();

        //TODO: calculate thickness from init_ss_id, store initial thickness
        EditText edit_thickness = (EditText) findViewById(R.id.edit_thickness);
        String message_thickness = edit_thickness.getText().toString();

        EditText edit_sensorID = (EditText) findViewById(R.id.edit_sensor_ID);
        String message_sensorID = edit_sensorID.getText().toString();

        Database.myDatabase = openOrCreateDatabase("TyrataData", MODE_PRIVATE, null);
        Log.i("tire input","new store!!!");
        Log.i("axis_ROW",String.valueOf(axis_row));
        Log.i("axis_IDX", String.valueOf(axis_index));
        Log.i("axis_SIDE", String.valueOf(axis_side));
        Log.i("sensor_ID", message_sensorID);
        Log.i("VIN", vin);

        try {
            Double thickness = Double.parseDouble(message_thickness);
            if(thickness < 5.0 || thickness > 15.0){
                msg = "The initial tire thickness need to between 5mm and 15mm!";
                throw new IOException();
            }
            Database.storeTireData(message_sensorID, message_manufacturer, message_model, message_SKU, vin, axis_row, String.valueOf(axis_side), axis_index, Double.parseDouble(message_thickness), 0, 0);
            Database.myDatabase.close();
            startActivity(intent);
        }
        catch (Exception e){
            if(msg == null) {
                msg = "Please type in valid information!";
            }
            storeexception(msg);
        }
    }

    private void storeexception(String msg){
        new AlertDialog.Builder(this)
                .setTitle("NOTIFICATION")
                .setMessage(msg)
                .setPositiveButton("Yes", null)
                .show();
    }
}
