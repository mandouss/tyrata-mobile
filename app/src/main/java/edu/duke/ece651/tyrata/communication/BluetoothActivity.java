package edu.duke.ece651.tyrata.communication;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import edu.duke.ece651.tyrata.Common;
import edu.duke.ece651.tyrata.R;

/**
 * Example Activity using Bluetooth API
 * @author Saeed Alrahma
 * Created by Saeed on 2/25/2018.
 */

public class BluetoothActivity extends AppCompatActivity {

    /* GLOBAL VARIABLES */
    private BluetoothDevice mBluetoothDevice; // device to connect to

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth);

        // Register for broadcasts when discovery is started/finish or device is discovered.
        Log.v(Common.LOG_TAG_BT_ACTIVITY, "Registering receiver...");
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        registerReceiver(mReceiver, filter);

        // Enable Bluetooth
        Log.v(Common.LOG_TAG_BT_ACTIVITY, "Enabling Bluetooth...");
        BluetoothAPI.enableBt(this, mHandler);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        BluetoothAPI.disableBt();
        BluetoothAPI.closeBtConnection();

        unregisterReceiver(mReceiver);
    }

    public void deviceList(View view) {
        Log.d(Common.LOG_TAG_BT_ACTIVITY, "deviceList()");
        // Launch the DeviceListActivity to see devices and do scan
        Intent serverIntent = new Intent(this, BtDeviceListActivity.class);
        startActivityForResult(serverIntent, Common.REQUEST_CONNECT_BT_DEVICE);
    }

    public void pairedBluetooth(View view) {
        Log.d(Common.LOG_TAG_BT_ACTIVITY, "pairedBluetooth()");
        BluetoothAPI.queryPairedDevicesBt();
    }

    public void discoverBluetooth(View view) {
        Log.d(Common.LOG_TAG_BT_ACTIVITY, "discoverBluetooth()");

        // Indicate scanning in the title
        setProgressBarIndeterminateVisibility(true);
        setTitle("Discovering Bluetooth Devices");

        BluetoothAPI.discoverDevicesBt(this);
    }

    public void acceptBluetooth(View view) {
        Log.d(Common.LOG_TAG_BT_ACTIVITY, "acceptBluetooth()");

        BluetoothAPI.acceptBt();

    }

    public void connectBluetooth(View view) {
        Log.d(Common.LOG_TAG_BT_ACTIVITY, "connectBluetooth()");

        if(mBluetoothDevice != null) {
            Log.d(Common.LOG_TAG_BT_ACTIVITY, "Connecting to device " + mBluetoothDevice);
            BluetoothAPI.connectBt(mBluetoothDevice);
        }
        else
            Log.d(Common.LOG_TAG_BT_ACTIVITY, "No device to connect to...");

    }

    public void sendMsg(View view) {
        BluetoothAPI.write("Test".getBytes());
    }

    @Override
    protected void onActivityResult (int requestCode, int resultCode, Intent data) {
        Log.v(Common.LOG_TAG_BT_ACTIVITY, "onActivityResult with code " + requestCode);
        switch (requestCode) {
            case Common.REQUEST_ENABLE_BT:
                if (resultCode == RESULT_CANCELED) {
                    Log.w(Common.LOG_TAG_BT_ACTIVITY, "Bluetooth enable request canceled");
                    // @todo Disable bluetooth features
                }
                else if (resultCode == RESULT_OK) {
                    // @todo connect to sensor/simulator
                    Log.v(Common.LOG_TAG_BT_ACTIVITY, "Bluetooth enabled");
                }
                break;
            case Common.REQUEST_ACCESS_COARSE_LOCATION:
                if (resultCode == RESULT_CANCELED) {
                    Log.w(Common.LOG_TAG_BT_ACTIVITY, "Location access request canceled");
                    // @todo Cannot discover devices
                }
                else if (resultCode == RESULT_OK) {
                    Log.v(Common.LOG_TAG_BT_ACTIVITY, "Location access granted");
                    //@todo this is not tested. Might not work here
                    BluetoothAPI.discoverDevicesBt(this);
                }
                break;
            default:
                Log.w(Common.LOG_TAG_BT_ACTIVITY, "Unknown REQUEST_CODE " + requestCode);
        }
    }

    // Create a BroadcastReceiver for ACTION_FOUND.
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            Log.v(Common.LOG_TAG_BT_ACTIVITY, "BroadcastReceiver onReceive");
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Discovery has found a device
                // Get the BluetoothDevice object and its info from the Intent.
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
                    Log.v(Common.LOG_TAG_BT_ACTIVITY, "New Device (not paired)");
                }
                Log.v(Common.LOG_TAG_BT_ACTIVITY, "Device name: " + device.getName() + ", " +
                        "MAC address: " + device.getAddress());
                if(device.getName() != null && device.getName().equalsIgnoreCase("GT-N7100")) {
                    Log.d(Common.LOG_TAG_BT_ACTIVITY, "Found device " + device.getName());
                    mBluetoothDevice = device;
                }
            }
            else if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
                Log.v(Common.LOG_TAG_BT_ACTIVITY, "BroadcastReceiver onReceive DISCOVERY_STARTED");
                setProgressBarIndeterminateVisibility(true);
            }
            else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                Log.v(Common.LOG_TAG_BT_ACTIVITY, "BroadcastReceiver onReceive DISCOVERY_FINISHED");
                setProgressBarIndeterminateVisibility(false);
            }
        }
    };

    /**
     * The Handler that gets information back from the BluetoothChatService
     */
    // @todo fix "This handler class should be static or leaks might occur" warning
    @SuppressLint("HandlerLeak")
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Log.i(Common.LOG_TAG_BT_ACTIVITY, "mHandler with type " + msg.what);
            switch (msg.what) {
                case Common.MESSAGE_READ:
                    byte[] readBuf = (byte[]) msg.obj;
                    // construct a string from the valid bytes in the buffer
                    String readMessage = new String(readBuf, 0, msg.arg1);
                    Log.d(Common.LOG_TAG_BT_ACTIVITY, readMessage);
                    Toast.makeText(getApplicationContext(), "Msg read: "
                            + readMessage, Toast.LENGTH_LONG).show();
                    break;
                case Common.MESSAGE_WRITE:
                    byte[] writeBuf = (byte[]) msg.obj;
                    // construct a string from the buffer
                    String writeMessage = new String(writeBuf, 0, msg.arg1);
                    Log.d(Common.LOG_TAG_BT_ACTIVITY, writeMessage);
                    Toast.makeText(getApplicationContext(), "Msg written: "
                            + writeMessage, Toast.LENGTH_LONG).show();
                    break;
                case Common.MESSAGE_TOAST:
                    Toast.makeText(getApplicationContext(), "Toast Msg", Toast.LENGTH_SHORT).show();
                    break;
                case Common.MESSAGE_DEVICE_NAME:
                    // save the connected device's name
                    String deviceName = msg.getData().getString(Common.DEVICE_NAME);
                    Toast.makeText(getApplicationContext(), "Connected to "
                            + deviceName, Toast.LENGTH_SHORT).show();
                    break;
                default:
                    Log.w(Common.LOG_TAG_BT_ACTIVITY, "Unknown message passed to handler: " + msg.what);
            }
        }
    };

}
