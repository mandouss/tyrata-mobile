package edu.duke.ece651.tyrata.calibration;
import android.app.AlertDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import edu.duke.ece651.tyrata.MainActivity;
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
        user_ID = intent.getIntExtra("userID", 1);
        // If this page is switched from vehicle edit
        String vin = intent.getStringExtra("VIN");
        if(vin != null) {
            Log.i("Vehicle Input edit", vin);
            EditText textView_vin = findViewById(R.id.edit_vin);
            textView_vin.setText(vin);
            textView_vin.setKeyListener(null);
        }else{
            Log.i("Vehicle Input add_car", "add_car");
        }

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.go_to_homepage, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.homepage){
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }
        return true;
    }

    public void saveMessage(View view) {

        Intent intent = new Intent(this, MainActivity.class);
        EditText edit_make = (EditText) findViewById(R.id.edit_make);
        String message_make = edit_make.getText().toString();

        EditText edit_model = (EditText) findViewById(R.id.edit_model);
        String message_model = edit_model.getText().toString();

        EditText edit_year = (EditText) findViewById(R.id.edit_year);
        String message_year = edit_year.getText().toString();

        EditText edit_vin = (EditText) findViewById(R.id.edit_vin);
        String message_vin = edit_vin.getText().toString();

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

        // Do something in response to button
        try {
            Database.myDatabase = openOrCreateDatabase("TyrataData", MODE_PRIVATE, null);
            Database.storeVehicleData(message_vin, message_make, message_model, Integer.parseInt(message_year), axis_num, Integer.parseInt(tirenumber), user_ID);
            Database.myDatabase.close();
            intent.putExtra("USER_ID", user_ID);
            startActivity(intent);
        }
        catch(Exception e){
            notification();
        }
    }

    private void notification(){
        new AlertDialog.Builder(this)
                .setTitle("NOTIFICATION")
                .setMessage("Please type in valid information!")
                .setPositiveButton("Yes", null)
                .show();
    }
}
