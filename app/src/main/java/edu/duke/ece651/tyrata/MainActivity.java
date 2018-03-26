package edu.duke.ece651.tyrata;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import edu.duke.ece651.tyrata.calibration.Report_accident;
import edu.duke.ece651.tyrata.datamanagement.Database;
import edu.duke.ece651.tyrata.display.TireInfo;
import edu.duke.ece651.tyrata.display.Vehicle_Info;
import edu.duke.ece651.tyrata.user.User;
import edu.duke.ece651.tyrata.vehicle.Vehicle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MainActivity extends AppCompatActivity {
    private ListView vehicle_list;
    private List<Map<String, Object>> list;
    private int user_ID;

    int notificationID = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent intent = getIntent();
        user_ID = intent.getIntExtra("USER_ID", 0);
        Log.i("In main", String.valueOf(user_ID));
        Database.myDatabase = openOrCreateDatabase("TyrataData", MODE_PRIVATE, null);
        // test functions
        Database.testUserTable();
        Database.testVehicleTable();
        Database.testTireTable();
        Database.testSnapTable();

        User curr_user = Database.getUser(user_ID);
        Database.myDatabase.close();

        TextView textView_username = findViewById(R.id.textView_user);
        textView_username.setText(curr_user.username);

        TextView textView_email = findViewById(R.id.textView_email);
        textView_email.setText(curr_user.email);

        TextView textView_phonenum = findViewById(R.id.textView_phone);
        textView_phonenum.setText(curr_user.phone);

        String message_report = intent.getStringExtra("REPORT");
        if(message_report != null && !message_report.equals("")){
            message_report = "User Report:\n" + message_report;
            TextView textView_report = findViewById(R.id.main_notification);
            textView_report.setText(message_report);
            textView_report.setVisibility(View.VISIBLE);
        }

        vehicle_list = (ListView) findViewById(R.id.vehicle_list);

        initDataList(curr_user.mVehicles);

        String[] from = { "img", "VIN", "make","model", "year" };
        int[] to = { R.id.item_img, R.id.item_vehicle, R.id.item_make,R.id.item_model,
                R.id.item_year };

        final SimpleAdapter adapter = new SimpleAdapter(this, list,
                R.layout.main_list_view_layout, from, to);

        vehicle_list.setAdapter(adapter);
        /**
         * click
         */
        vehicle_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                Map<String, Object> map = list.get(arg2);
                String str = "";
                str += map.get("VIN");
                main_to_vehicle_info(str);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_view_navigation, menu);
        return true;
    }

    private void initDataList(ArrayList<Vehicle> vehicles) {
        int number = vehicles.size();
        //图片资源
        int img[] ;
        img = new int[number];
        for(int i = 0;i < number; i++) {
            img[i] = R.drawable.vehicle;
        }
        list = new ArrayList<Map<String, Object>>();
        for (int i = 0; i < number; i++) {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("img", img[i]);
            map.put("VIN", vehicles.get(i).getVin());
            map.put("make", "Make:" + vehicles.get(i).getMake() );
            map.put("model", "Model:" + vehicles.get(i).getModel() );
            map.put("year", "Year:" + String.valueOf(vehicles.get(i).getYear()));
            list.add(map);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.n_item2:
                main_to_addcar();
                return true;
            case R.id.n_item3:
                main_to_report();
                return true;
            case R.id.n_item4:
                main_to_login();
                return true;
            case R.id.n_item5:
                main_to_communication();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    public void main_to_addcar() {
        Intent intent = new Intent(MainActivity.this, edu.duke.ece651.tyrata.calibration.Input_Vehicle_Info.class);
        intent.putExtra("userID", user_ID);

        startActivity(intent);
        // Do something in response to button
    }
    public void main_to_report() {
        Intent intent = new Intent(MainActivity.this, Report_accident.class);
        intent.putExtra("userID", user_ID);

        startActivity(intent);
        // Do something in response to button
    }
    public void main_to_login() {
        Intent intent = new Intent(MainActivity.this, edu.duke.ece651.tyrata.user.Log_in.class);

        startActivity(intent);
        // Do something in response to button
    }
    public void main_to_vehicle_info(String vin) {
        Intent intent = new Intent(MainActivity.this, Vehicle_Info.class);
        intent.putExtra("VIN", vin);
//        intent.putExtra("userID", user_ID);

        startActivity(intent);
        // Do something in response to button
    }
    public void main_to_communication() {
        Intent intent = new Intent(MainActivity.this, edu.duke.ece651.tyrata.communication.EmptyActivity.class);

        startActivity(intent);
        // Do something in response to button
    }

    private void displayNotification(String vin,int axis_row,char axis_side,int axis_index) {
        Intent i = new Intent(this, TireInfo.class);
        i.putExtra("notificationID", notificationID);
        i.putExtra("AXIS_ROW", axis_row);
        i.putExtra("AXIS_INDEX",axis_index);
        i.putExtra("AXIS_SIDE", axis_side);
        i.putExtra("VIN", vin);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                0, i, 0);
        NotificationManager nm = (NotificationManager) getSystemService
                (NOTIFICATION_SERVICE);
        String id = "my_channel_01";
        int importance = NotificationManager.IMPORTANCE_LOW;
        CharSequence name = "my_channel";
        NotificationChannel mChannel = new NotificationChannel(id, name,importance);
        mChannel.enableLights(true);
        nm.createNotificationChannel(mChannel);
        int tire_index;

        String notification_content = "Your tire_index of vehicle "+vin+" need to be replaced.";
        NotificationCompat.Builder notifBuilder = new NotificationCompat.Builder(this, id)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("NOTIFICATION:")
                .setContentText(notification_content)
                .addAction(R.mipmap.ic_launcher, "See the details",
                        pendingIntent);

        assert nm != null;
        nm.notify(notificationID, notifBuilder.build());
    }

    public void onClick(View view) {
        String notification_vin = "abcd";
        int notification_axis_row = 1;
        int notification_axis_index = 1;
        char notification_axis_side = 'L';
//        displayNotification(notification_vin,notification_axis_row,notification_axis_side,notification_axis_index);
    }
}
