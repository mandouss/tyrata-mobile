package edu.duke.ece651.tyrata;

import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.NavigationView;
import android.support.v4.app.NotificationCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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
import android.widget.Toast;

import edu.duke.ece651.tyrata.calibration.Report_accident;
import edu.duke.ece651.tyrata.communication.BluetoothAPI;
import edu.duke.ece651.tyrata.communication.BluetoothDeviceListActivity;
import edu.duke.ece651.tyrata.communication.EmptyActivity;
import edu.duke.ece651.tyrata.datamanagement.Database;
import edu.duke.ece651.tyrata.display.TireInfo;
import edu.duke.ece651.tyrata.display.Vehicle_Info;
import edu.duke.ece651.tyrata.user.Edit_user_information;
import edu.duke.ece651.tyrata.user.User;
import edu.duke.ece651.tyrata.vehicle.TireSnapshot;
import edu.duke.ece651.tyrata.vehicle.Vehicle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MainActivity extends EmptyActivity {
    private ListView vehicle_list;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;
    private List<Map<String, Object>> list;
    private NavigationView navigationView;
    private int user_ID;
    private StringBuilder mXmlStream;


    int notificationID = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer);
        mToggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar, R.string.open, R.string.close);
        mXmlStream = new StringBuilder();

        // Enable Bluetooth
        Log.v(Common.LOG_TAG_MAIN_ACTIVITY, "Enabling Bluetooth...");
        BluetoothAPI.enableBt(this, mHandler);

        mDrawerLayout.addDrawerListener(mToggle);
        mToggle.syncState();
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_drawer);

        navigationView = (NavigationView) findViewById(R.id.drawer_navigation) ;

        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        // set item as selected to persist highlight
                        menuItem.setChecked(true);
                        // close drawer when item is tapped
                        mDrawerLayout.closeDrawers();

                        // Add code here to update the UI based on the item selected
                        // For example, swap UI fragments here

                        switch (menuItem.getItemId()) {
                            case R.id.edit_account:
                                main_to_edit();
                                return true;
                            case R.id.log_out:
                                main_to_login();
                                return true;
                        }
                        return true;
                    }
                });

        //set the user information in the drawer menu
        Menu menu = navigationView.getMenu();
        MenuItem menu_username = menu.findItem(R.id.d_name);
        MenuItem menu_email = menu.findItem(R.id.d_email);
        MenuItem menu_phone = menu.findItem(R.id.d_phone);

        Intent intent = getIntent();
        user_ID = intent.getIntExtra("USER_ID", 0);
        if(user_ID == 0){
            SharedPreferences editor = getSharedPreferences("user_data",MODE_PRIVATE);
            user_ID = editor.getInt("USER_ID",0);
        }
        SharedPreferences.Editor editor= getSharedPreferences("user_data",MODE_PRIVATE).edit();
        editor.putInt("USER_ID",user_ID);
        editor.commit();
        Log.i("In main, user", String.valueOf(user_ID));
        Database.myDatabase = openOrCreateDatabase("TyrataData", MODE_PRIVATE, null);
        // test functions
        Database.testUserTable();
        Database.testVehicleTable();
        Database.testTireTable();
        Database.testSnapTable();
//        Database.testTraceTable();

        User curr_user = Database.getUser(user_ID);
        Database.myDatabase.close();

        TextView textView_username = findViewById(R.id.textView_user);
        textView_username.setText(curr_user.username);
        menu_username.setTitle(curr_user.username);
        menu_email.setTitle(curr_user.email);
        menu_phone.setTitle(curr_user.phone);
//        TextView textView_username_d = findViewById(R.id.d_name);
//        textView_username_d.setText(curr_user.username);

//        TextView textView_email = findViewById(R.id.textView_email);
//        textView_email.setText(curr_user.email);


//        TextView textView_phonenum = findViewById(R.id.textView_phone);
//        textView_phonenum.setText(curr_user.phone);

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
        //setIconEnable(menu, true);
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        BluetoothAPI.disableBt();
    }

    private void initDataList(ArrayList<Vehicle> vehicles) {
        int number = vehicles.size();
        //图片资源
        int img[] ;
        img = new int[number];
        for(int i = 0;i < number; i++) {
            if(i%2==0) {
                img[i] = R.drawable.vehicle_list2;
            }
            else{
                img[i] = R.drawable.vehicle_list3;
            }
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
        //EmptyActivity emptyActivity = new EmptyActivity();
        switch (item.getItemId()) {
            case R.id.n_menu_addCar:
                //main_to_addcar();
                return true;
            case R.id.n_menu_reportAccident:
                //main_to_report();
                return true;
            case R.id.n_menu_signOut:
                main_to_login();
                return true;
            case R.id.n_submenu_Bluetooth:
                goToBluetooth();
                return true;
            case R.id.n_submenu_Database:
                getDatabaseFromXml();
                Intent intent = new Intent(MainActivity.this, MainActivity.class);
                startActivity(intent);
                return true;
            case R.id.n_submenu_GPS:
                getGPS();
                return true;
            case R.id.n_submenu_Http:
                goToHTTP();
                return true;
            case R.id.n_submenu_tireSnapshot:
                //getTireSnapshotListFromXml();
                return true;
            case R.id.n_submenu_XML:
                testParseXml();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void main_to_edit() {
        Intent intent = new Intent(MainActivity.this, Edit_user_information.class);
        //intent.putExtra("userID", user_ID);

        startActivity(intent);
        // Do something in response to button
    }

    public void main_to_addcar(View view) {
        Intent intent = new Intent(MainActivity.this, edu.duke.ece651.tyrata.calibration.Input_Vehicle_Info.class);
        intent.putExtra("userID", user_ID);

        startActivity(intent);
        // Do something in response to button
    }
    public void main_to_report(View view) {
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
    public void main_to_communication(View view) {
        Log.d(Common.LOG_TAG_MAIN_ACTIVITY, "discoverBluetooth()");

        if (BluetoothAPI.isBtReady(this)) {
            // Launch the DeviceListActivity to see devices and do scan
            Intent discoverIntent = new Intent(this, BluetoothDeviceListActivity.class);
            startActivityForResult(discoverIntent, Common.REQUEST_CONNECT_BT_DEVICE);
        }
    }

    private void handleReceivedSnapshots(ArrayList<TireSnapshot> snapshots) {
        // close bluetooth connection
        BluetoothAPI.disableBt();

        // @TODO write code to store snapshots received from simulator
        Toast.makeText(getApplicationContext(), "Received "
                + snapshots.size() + " tire snapshots", Toast.LENGTH_SHORT).show();
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

        String notification_content = "Your tire1 of vehicle "+vin+" need to be replaced.";
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
        String notification_vin = "vin1-1";
        int notification_axis_row = 1;
        int notification_axis_index = 1;
        char notification_axis_side = 'L';
        displayNotification(notification_vin,notification_axis_row,notification_axis_side,notification_axis_index);
    }

    /*private void setIconEnable(Menu menu, boolean enable)
    {
        try
        {
            Class<?> clazz = Class.forName("com.android.internal.view.menu.MenuBuilder");
            Method m = clazz.getDeclaredMethod("setOptionalIconsVisible", boolean.class);
            m.setAccessible(true);

            //MenuBuilder实现Menu接口，创建菜单时，传进来的menu其实就是MenuBuilder对象(java的多态特征)
            m.invoke(menu, enable);

        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }*/

    /**
     * Called when startActivityForResult finishes
     * @param requestCode Constant integer representing the request that finished
     * @param resultCode The result of the finished activity (e.g. OK, FAILED)
     * @param data Data sent back from the finished activity
     */
    @Override
    protected void onActivityResult (int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case Common.REQUEST_ENABLE_BT:
                if (resultCode == RESULT_CANCELED) {
                    Log.w(Common.LOG_TAG_MAIN_ACTIVITY, "Bluetooth enable request canceled");
                    Toast.makeText(getApplicationContext(),
                            "Bluetooth request cancelled. Cannot connect to sensors...",
                            Toast.LENGTH_LONG).show();
                }
                else if (resultCode == RESULT_OK) {
                    Log.v(Common.LOG_TAG_MAIN_ACTIVITY, "Bluetooth enabled");
                }
                break;
            case Common.REQUEST_ACCESS_COARSE_LOCATION:
                if (resultCode == RESULT_CANCELED) {
                    Log.w(Common.LOG_TAG_MAIN_ACTIVITY, "Location access request cancelled");
                    Toast.makeText(getApplicationContext(),
                            "Location access request cancelled. Cannot discover Bluetooth devices...",
                            Toast.LENGTH_LONG).show();
                }
                else if (resultCode == RESULT_OK) {
                    Log.v(Common.LOG_TAG_MAIN_ACTIVITY, "Location access granted");
                    //@todo this is not tested. Might not work here
                    main_to_communication(null);
//                    BluetoothAPI.discoverBtDevices(this);
                }
                break;
            case Common.REQUEST_CONNECT_BT_DEVICE:
                if (resultCode == RESULT_OK) {
                    Toast.makeText(getApplicationContext(),
                            "Connecting...", Toast.LENGTH_SHORT).show();
                    BluetoothAPI.connectBt(data);
                } else {
                    Toast.makeText(getApplicationContext(),
                            "Bluetooth connection Failed...",
                            Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                Log.w(Common.LOG_TAG_MAIN_ACTIVITY, "Unknown REQUEST_CODE " + requestCode);
                Toast.makeText(getApplicationContext(),
                        "Something went wrong (Unknown REQUEST_CODE)...",
                        Toast.LENGTH_LONG).show();
        }
    }


    /**
     * The Handler that gets information back from the BluetoothChatService
     */
    // @todo fix "This handler class should be static or leaks might occur" warning
    @SuppressLint("HandlerLeak")
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Common.MESSAGE_READ:
                    byte[] readBuf = (byte[]) msg.obj;
                    // construct a string from the valid bytes in the msg
                    String msgStr = new String(readBuf, 0, msg.arg1);
                    mXmlStream.append(msgStr);
                    if (readBuf[msg.arg1-1] == Common.SIMULATOR_EOF) { // reached end of message/file
                        Log.d(Common.LOG_TAG_MAIN_ACTIVITY, "Message is: " + mXmlStream.length() + " Bytes");
                        ArrayList<TireSnapshot> snapshots = BluetoothAPI.processMsg(mXmlStream.toString());
                        handleReceivedSnapshots(snapshots);
                    }
                    break;
                case Common.MESSAGE_WRITE:
                    byte[] writeBuf = (byte[]) msg.obj;
                    String writeMsg = new String(writeBuf, 0, msg.arg1);
                    Toast.makeText(getApplicationContext(), "Sent " + writeMsg, Toast.LENGTH_SHORT).show();
                    break;
                case Common.MESSAGE_TOAST:
                    Toast.makeText(getApplicationContext(), "Some error occurred", Toast.LENGTH_SHORT).show();
                    break;
                case Common.MESSAGE_DEVICE_NAME:
                    // save the connected device's name
                    String deviceName = msg.getData().getString(Common.DEVICE_NAME);
                    Toast.makeText(getApplicationContext(), "Connected to "
                            + deviceName, Toast.LENGTH_SHORT).show();
                    break;
                default:
                    Log.w(Common.LOG_TAG_MAIN_ACTIVITY, "Unknown message passed to handler: " + msg.what);
            }
        }
    };
}
