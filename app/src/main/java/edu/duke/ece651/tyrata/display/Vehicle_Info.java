package edu.duke.ece651.tyrata.display;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.duke.ece651.tyrata.MainActivity;
import edu.duke.ece651.tyrata.R;
import edu.duke.ece651.tyrata.calibration.Input_Vehicle_Info;
import edu.duke.ece651.tyrata.datamanagement.Database;
import edu.duke.ece651.tyrata.vehicle.Vehicle;

public class Vehicle_Info extends Activity {
    private Integer buttonnumber = 0;
    private Vehicle curr_vehicle;
    private String vin;
    private int user_id;
    private ListView tire_list;
    private List<Map<String, Object>> list;
    private int axis_row;
    private char axis_side;
    private int axis_index;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vehicle__info);
        //getVehicle
        // Get the Intent that started this activity and extract the string
        Intent intent = getIntent();
        vin = intent.getStringExtra("VIN");

        Database.myDatabase = openOrCreateDatabase("TyrataData", MODE_PRIVATE, null);

        user_id = Database.getVinUserID(vin);
        curr_vehicle = Database.getVehicle(vin);
        Database.myDatabase.close();

        Log.i("In vehicle info, VIN:", vin);
        Log.i("In vehicle info, user:", String.valueOf(user_id));

        String message_make = curr_vehicle.getMake();
        TextView textView_make = findViewById(R.id.textView_make);
        textView_make.setText(message_make);


        String message_model = curr_vehicle.getModel();
        TextView textView_model = findViewById(R.id.textView_model);
        textView_model.setText(message_model);


        String message_year = String.valueOf(curr_vehicle.getYear());
        TextView textView_year = findViewById(R.id.textView_year);
        textView_year.setText(message_year);


        TextView textView_vin = findViewById(R.id.textView_vin);
        textView_vin.setText(vin);

        String message_tirenumber = String.valueOf(curr_vehicle.getNumTires()) ;
        if(message_tirenumber.equals("")){
            message_tirenumber = "4";
        }
        TextView textView_tirenumber = findViewById(R.id.textView_tirenumber);
        textView_tirenumber.setText(message_tirenumber);

        ImageView imageView= (ImageView) findViewById(R.id.image_vehicle);
        if(curr_vehicle.getNumTires() == 4){
            imageView.setImageResource(R.drawable.four_wheel);
        }
        else if(curr_vehicle.getNumTires() == 10){
            imageView.setImageResource(R.drawable.ten_wheel);
        }
        else if(curr_vehicle.getNumTires() == 18){
            imageView.setImageResource(R.drawable.eighteen_wheel);
        }
        else{
            imageView.setImageResource(R.drawable.four_wheel);
        }


        int axis_num = curr_vehicle.getNumAxis();

        buttonnumber=Integer.parseInt(message_tirenumber);

        tire_list = (ListView) findViewById(R.id.tire_list);
        initDataList(buttonnumber);

        String[] from = { "img", "tire number", "content", "percent" };
        // 列表项组件Id 数组
        int[] to = { R.id.item_img, R.id.item_tire, R.id.item_location,
                R.id.item_percent };
        final SimpleAdapter adapter = new SimpleAdapter(this, list,
                R.layout.list_view_layout, from, to);

        tire_list.setAdapter(adapter);
        /**
         * 单击
         */
        tire_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                Map<String, Object> map = list.get(arg2);

                String str = "";
                String str2="";
                str += map.get("tire number");
                for(int i=0;i<str.length();i++) {
                    if (str.charAt(i) >= 48 && str.charAt(i) <= 57) {
                        str2 += str.charAt(i);
                    }
                }
                int tire_num = Integer.valueOf(str2);
                calculate_location(buttonnumber,tire_num);
                vehicle_to_tire();
            }
        });
    }

    private void calculate_location(int tirenum, int index){
        int side = -1;   //left-1,right-0
        if(tirenum == 4){
            axis_row = (index-1)/2+1;
            axis_index = 1;
            side = index%2;
        }
        if(tirenum == 10 || tirenum == 18) {
            axis_row = (index + 1) / 4 + 1;
            if (index == 1) {
                axis_index = 1;
                side = 1;
            } else if (index == 2) {
                axis_index = 1;
                side = 0;
            } else {
                if (index % 4 == 0 || index % 4 == 1) {
                    axis_index = 1;
                } else {
                    axis_index = 2;
                }
                if (index % 4 == 0 || index % 4 == 3) {
                    side = 1;
                } else {
                    side = 0;
                }
            }
        }
        if(side == 1){
            axis_side = 'L';
        }
        else {
            axis_side = 'R';
        }
        Log.i("axis", Character.toString(axis_side));
        Log.i("index", Integer.toString(axis_index));
        Log.i("row", Integer.toString(axis_row));
    }


    private void initDataList(int number) {
        //图片资源
        int img[] = null;
        img = new int[number];
        for(int i = 0;i < number; i++) {
            img[i] = R.drawable.tire;
        }
        list = new ArrayList<Map<String, Object>>();
        for (int i = 0; i < number; i++) {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("img", img[i]);
            map.put("tire number", "tire" + (i+1));
            map.put("content", "location:" );
            map.put("percent", "96%");
            list.add(map);
        }
    }

    public void BackToMain(View view) {
        Intent intent = new Intent(Vehicle_Info.this, MainActivity.class);
        intent.putExtra("USER_ID", user_id);
        startActivity(intent);
    }

    public void switchToEdit(View view) {
        Intent intent = new Intent(Vehicle_Info.this, Input_Vehicle_Info.class);
        intent.putExtra("userID", user_id);
        intent.putExtra("VIN", vin);
        startActivity(intent);
        // Do something in response to button
    }
    public void vehicle_to_tire () {
        Intent intent = new Intent(Vehicle_Info.this, TireInfo.class);
        intent.putExtra("AXIS_ROW", axis_row);
        intent.putExtra("AXIS_INDEX",axis_index);
        intent.putExtra("AXIS_SIDE", axis_side);
        intent.putExtra("VIN", vin);

        startActivity(intent);
        // Do something in response to button
    }

    /* Added by De Lan on 3/25/2018 */
    public void delete_vehicle(final View view){
        new AlertDialog.Builder(this)
                .setTitle("NOTIFICATION")
                .setMessage("Are you sure to delete this vehicle from your account?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Database.myDatabase = openOrCreateDatabase("TyrataData", MODE_PRIVATE, null);
                                Database.deleteVehicle(vin);
                                Database.myDatabase.close();
                                BackToMain(view);
                            }
                        }
                        )
                .setNegativeButton("No", null)
                .show();
    }

}
