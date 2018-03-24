package edu.duke.ece651.tyrata.communication;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.renderscript.ScriptGroup;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;
import org.xmlpull.v1.XmlPullParserException;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import edu.duke.ece651.tyrata.Common;
import edu.duke.ece651.tyrata.R;
import edu.duke.ece651.tyrata.vehicle.TireSnapshot;

import static java.lang.System.in;

/**
 * Example Activity using Bluetooth API
 * @author Saeed Alrahma
 * Created by Saeed on 2/25/2018.
 */

public class BluetoothActivity extends AppCompatActivity {

    /* GLOBAL VARIABLES */
    private BluetoothDevice mBluetoothDevice; // device to connect to
    private TextView mTextView;
    private String mXmlStream;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth);

        mTextView = findViewById(R.id.textView_s0_bt);
        mTextView.setMovementMethod(new ScrollingMovementMethod());

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
        Intent serverIntent = new Intent(this, BluetoothDeviceListActivity.class);
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
            String address = extras.getString(BluetoothDeviceListActivity.EXTRA_DEVICE_ADDRESS);
            Log.d(Common.LOG_TAG_BT_ACTIVITY, "Connecting to " + address);
            Toast.makeText(getApplicationContext(),
                    "Connecting to " + address, Toast.LENGTH_SHORT).show();
            BluetoothAPI.connectBt(address);
        }
        else {
            Log.w(Common.LOG_TAG_BT_ACTIVITY, "No device selected to connect to");
            Toast.makeText(getApplicationContext(),
                    "No device selected to connect to", Toast.LENGTH_LONG).show();
        }
    }

    public void sendMsg(View view) {
        Log.d(Common.LOG_TAG_BT_ACTIVITY, "sendMsg()");
        InputStream in = getResources().openRawResource(R.raw.xml_bluetooth_sample);
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();

        int nRead;
        byte[] data = new byte[16384];

        try {
            while ((nRead = in.read(data, 0, data.length)) != -1) {
                buffer.write(data, 0, nRead);
            }
            buffer.flush();
            Log.d(Common.LOG_TAG_BT_ACTIVITY, "Sending message...");
            BluetoothAPI.write(buffer.toByteArray());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void displayMsg(String msg) {
        mTextView.setText(msg);
    }

    public void processMsg(String msg) {
        // Create/Update XML string
        if (msg.startsWith("<?xml")) {
            // beginning of XML data
            mXmlStream = msg;

        } else {
            // middle of XML data
            mXmlStream += msg;
        }

        displayMsg(mXmlStream);
        try {
            // construct an InputStream from XML string
            InputStream in = new ByteArrayInputStream(mXmlStream.getBytes("UTF-8"));
            // Validate XML format
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            dBuilder.parse(in);
            // No exceptions/errors --> valid XML format
            Toast.makeText(getApplicationContext(), "Received XML data! Parsing...",
                    Toast.LENGTH_SHORT).show();
            displayMsg(mXmlStream);
            // parse the message
            BluetoothXmlParser btXmlParser = new BluetoothXmlParser();
            TireSnapshot tireSnapshot = btXmlParser.parseToTireSnapshot(in);
            String info;
            if (tireSnapshot == null)
                info = "Failed to parse message received...";
            else {
                info = "Tire/Sensor ID: " + tireSnapshot.getSensorId();
                info += ", S11: " + tireSnapshot.getS11();
                info += " Pressure: " + tireSnapshot.getPressure();
                info += ", Mileage: " + tireSnapshot.getOdometerMileage();
                info += ", Timestamp: " + TireSnapshot.convertCalendarToString(tireSnapshot.getTimestamp());
            }
            Toast.makeText(getApplicationContext(), info, Toast.LENGTH_LONG).show();
        } catch (SAXException e) {
            // Parsing XML failed (invalid XML file)
            // Invalid --> assume incomplete and wait for rest of data
            Toast.makeText(getApplicationContext(), "Received part XML data. Waiting for more...",
                    Toast.LENGTH_SHORT).show();
            return;
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        }
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
                    // construct a string from the valid bytes in the msg
                    String msgStr = new String(readBuf, 0, msg.arg1);
                    Toast.makeText(getApplicationContext(), msgStr.length()
                            + " Bytes read ", Toast.LENGTH_SHORT).show();
                    displayMsg(msgStr);
                    processMsg(msgStr);
                    break;

                case Common.MESSAGE_WRITE:
//                    byte[] writeBuf = (byte[]) msg.obj;
//                    // construct a string from the buffer
//                    String writeMessage = new String(writeBuf, 0, msg.arg1);
//                    Log.d(Common.LOG_TAG_BT_ACTIVITY, writeMessage);
//                    Toast.makeText(getApplicationContext(), "Msg written: "
//                            + writeMessage, Toast.LENGTH_LONG).show();
                    Toast.makeText(getApplicationContext(), "Sent message with " + msg.arg1
                                    + " Bytes!", Toast.LENGTH_LONG).show();
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
                    Log.w(Common.LOG_TAG_BT_ACTIVITY, "Unknown message passed to handler: " + msg.what);
            }
        }
    };

}
