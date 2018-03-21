package edu.duke.ece651.tyrata.calibration;
/**
 * This class is Tireinfo input page
 * @author De Lan
 * Created by De Lan on 2/27/2018.
 */
import android.content.Intent;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import edu.duke.ece651.tyrata.R;
import edu.duke.ece651.tyrata.datamanagement.Database;
import edu.duke.ece651.tyrata.display.TireInfo;

public class TireInfoInput extends AppCompatActivity {
    int axis_row;
    int axis_index;
    char axis_side;
    String vin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tire_info_input);
        Intent intent = getIntent();

        axis_row = intent.getIntExtra("axis_ROW",0);
        axis_index = intent.getIntExtra("axis_IDX",0);
        axis_side = intent.getCharExtra("axis_SIDE",'a');
        vin = intent.getStringExtra("VIN");
    }
    /** Called when the user taps the Submit button */
    public void saveMessage(View view) {
        Intent intent = new Intent(this, TireInfo.class);
        EditText edit_manufacturer = (EditText) findViewById(R.id.edit_manufacturer);
        String message_manufacturer = edit_manufacturer.getText().toString();
//        intent.putExtra("tire_manufacturer", message_manufacturer);

        EditText edit_model = (EditText) findViewById(R.id.edit_model);
        String message_model = edit_model.getText().toString();
//        intent.putExtra("tire_model", message_model);

        EditText edit_SKU = (EditText) findViewById(R.id.edit_SKU);
        String message_SKU = edit_SKU.getText().toString();
//        intent.putExtra("tire_SKU", message_SKU);

        //TODO: calculate thickness from init_ss_id, store initial thickness
        EditText edit_thickness = (EditText) findViewById(R.id.edit_thickness);
        String message_thickness = edit_thickness.getText().toString();
//        intent.putExtra("tire_thickness", message_thickness);

        Database.myDatabase = openOrCreateDatabase("TyrataData", MODE_PRIVATE, null);
        Log.i("NOTIFICATION","new store!!!");
        Log.i("axis_ROW",String.valueOf(axis_row));
        Log.i("axis_IDX", String.valueOf(axis_index));
        Log.i("axis_SIDE", String.valueOf(axis_side));
        Log.i("VIN", vin);

        Database.storeTireData("sensor1", message_manufacturer, message_model, message_SKU, vin, axis_row, String.valueOf(axis_side), axis_index, Double.parseDouble(message_thickness),0, 0 );
        startActivity(intent);
    }
}
