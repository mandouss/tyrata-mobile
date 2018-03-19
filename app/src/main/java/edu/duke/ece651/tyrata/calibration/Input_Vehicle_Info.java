package edu.duke.ece651.tyrata.calibration;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import java.util.ArrayList;
import java.util.List;
import edu.duke.ece651.tyrata.R;
import edu.duke.ece651.tyrata.datamanagement.Database;
import edu.duke.ece651.tyrata.display.Vehicle_Info;

public class Input_Vehicle_Info extends AppCompatActivity {
    private Spinner spinner_Tirenumber;
    private List<String> dataList;
    private ArrayAdapter<String> adapter;
    private int user_ID;
    String tirenumber;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input__vehicle__info);
        Intent intent = getIntent();
        user_ID = intent.getIntExtra("userID", 0);
        spinner_Tirenumber = (Spinner) findViewById(R.id.spinner_tirenumber);


        dataList = new ArrayList<String>();
        dataList.add("4");
        dataList.add("10");
        dataList.add("18");

        adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,dataList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_Tirenumber.setAdapter(adapter);
        spinner_Tirenumber.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
               adapter.getItem(position);
               tirenumber = dataList.get(position);
               parent.setVisibility(View.VISIBLE);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


    }
    public void saveMessage(View view) {

        Intent intent = new Intent(this, Vehicle_Info.class);
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

        intent.putExtra("TIRENUMBER", tirenumber);

        int num = Integer.parseInt(tirenumber);
        int axis_num = 0;
        if (num == 4) {
            axis_num = 2;
        }
        else if(num == 10) {
            axis_num = 3;
        }
        else if(num == 18){
            axis_num = 5;
        }
        intent.putExtra("AXIS_NUM",axis_num);

        // Do something in response to button
        Database.myDatabase = openOrCreateDatabase("TyrataData", MODE_PRIVATE, null);
        Database.createTable();
        Database.storeVehicleData(message_vin, message_make, message_model, Integer.parseInt(message_year), axis_num, Integer.parseInt(tirenumber), user_ID);
        startActivity(intent);
    }
}
