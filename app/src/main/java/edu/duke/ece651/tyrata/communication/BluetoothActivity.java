package edu.duke.ece651.tyrata.communication;

import android.annotation.SuppressLint;
import android.app.Activity;
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

        // Enable Bluetooth
        Log.v(Common.LOG_TAG_BT_ACTIVITY, "Enabling Bluetooth...");
        BluetoothAPI.enableBt(this, mHandler);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        BluetoothAPI.disableBt();
        BluetoothAPI.closeBtConnection();
    }

    public void pairedBluetooth(View view) {
        Log.d(Common.LOG_TAG_BT_ACTIVITY, "pairedBluetooth()");
        BluetoothAPI.queryBtPairedDevices();
    }

    public void discoverBluetooth(View view) {
        Log.d(Common.LOG_TAG_BT_ACTIVITY, "discoverBluetooth()");

        // Launch the DeviceListActivity to see devices and do scan
        Intent serverIntent = new Intent(this, BtDeviceListActivity.class);
        startActivityForResult(serverIntent, Common.REQUEST_CONNECT_BT_DEVICE);
    }

    public void acceptBluetooth(View view) {
        Log.d(Common.LOG_TAG_BT_ACTIVITY, "acceptBluetooth()");

        BluetoothAPI.acceptBt();

    }

    public void connectBluetooth(Intent data) {
        Log.d(Common.LOG_TAG_BT_ACTIVITY, "connectBluetooth()");
        Bundle extras = data.getExtras();
        if (extras != null) {
            String address = extras.getString(BtDeviceListActivity.EXTRA_DEVICE_ADDRESS);
            Log.d(Common.LOG_TAG_BT_ACTIVITY, "Connecting to " + address);
            Toast.makeText(getApplicationContext(),
                    "Connecting to " + address, Toast.LENGTH_LONG).show();
            BluetoothAPI.connectBt(address);
        }
        else {
            Log.w(Common.LOG_TAG_BT_ACTIVITY, "No device selected to connect to");
            Toast.makeText(getApplicationContext(),
                    "No device selected to connect to", Toast.LENGTH_LONG).show();
        }
    }

    public void sendMsg(View view) {
        String msg = "Message sent from " + BluetoothAPI.getDeviceName();
        BluetoothAPI.write(msg.getBytes());
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
                    BluetoothAPI.discoverBtDevices(this);
                }
                break;
            case Common.REQUEST_CONNECT_BT_DEVICE:
                if (resultCode == Activity.RESULT_OK) {
                    connectBluetooth(data);
                }
                break;
            default:
                Log.w(Common.LOG_TAG_BT_ACTIVITY, "Unknown REQUEST_CODE " + requestCode);
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
