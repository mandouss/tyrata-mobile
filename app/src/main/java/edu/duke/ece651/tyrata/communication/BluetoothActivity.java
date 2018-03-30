package edu.duke.ece651.tyrata.communication;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.xml.sax.SAXException;
import org.xmlpull.v1.XmlPullParserException;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import edu.duke.ece651.tyrata.Common;
import edu.duke.ece651.tyrata.R;
import edu.duke.ece651.tyrata.vehicle.TireSnapshot;

/**
 * Example Activity using Bluetooth API
 * @author Saeed Alrahma
 * Created by Saeed on 2/25/2018.
 */

public class BluetoothActivity extends AppCompatActivity {

    /* GLOBAL VARIABLES */
    private TextView mTextViewReceived;
    private TextView mTextViewParsed;
    private String mXmlStream;
    private String mParsedMsg;

    private int mNumBytesRead;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth);

        mTextViewReceived = findViewById(R.id.textView_s0_bt);
//        mTextViewReceived.setMovementMethod(new ScrollingMovementMethod());
        mTextViewParsed = findViewById(R.id.textView_s1_bt);

        mXmlStream = "";
        mParsedMsg = "";
        mNumBytesRead = 0;

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

    public void sampleDataTest(View view) {
        try {
            InputStream in = getResources().openRawResource(R.raw.xml_bluetooth_sample);
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }
            reader.close();
            processMsg(sb.toString());
        } catch (IOException e) {
            e.printStackTrace();
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

    public void displayMsg(String msg, TextView textView) {
        textView.setText(msg);
    }

    public void processMsg(String msg) {
        Toast.makeText(getApplicationContext(), "Received XML data! Parsing...",
                Toast.LENGTH_SHORT).show();
        try {
            InputStream in = new ByteArrayInputStream(mXmlStream.getBytes("UTF-8"));
            mNumBytesRead = 0;
            // parse the message
            BluetoothXmlParser btXmlParser = new BluetoothXmlParser();
            ArrayList<TireSnapshot> tireSnapshotList = btXmlParser.parseToTireSnapshotList(in);
            if (tireSnapshotList.isEmpty()){
                Toast.makeText(getApplicationContext(),
                        "Failed to obtain TireSnapshot from message received...",
                        Toast.LENGTH_LONG).show();
            }
            for (int i=0; i<tireSnapshotList.size(); i++) {
                mParsedMsg += "Tire/Sensor ID: " + tireSnapshotList.get(i).getSensorId();
                mParsedMsg += "\nS11: " + tireSnapshotList.get(i).getS11();
                mParsedMsg += "\nPressure: " + tireSnapshotList.get(i).getPressure();
                mParsedMsg += "\nMileage: " + tireSnapshotList.get(i).getOdometerMileage();
                mParsedMsg += "\nTimestamp: " + TireSnapshot.convertCalendarToString(tireSnapshotList.get(i).getTimestamp());
                mParsedMsg += "\n\n";
                displayMsg(mParsedMsg, mTextViewParsed);
                //Toast.makeText(getApplicationContext(), info, Toast.LENGTH_SHORT).show();
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

/*
        // Create/Update XML string
        if (msg.startsWith("<?xml")) {
            // beginning of XML data
            mXmlStream = msg;

        } else {
            // middle of XML data
            mXmlStream += msg;
        }

        displayMsg(mXmlStream, mTextViewReceived);
        try {
            // construct an InputStream from XML string
            InputStream in = new ByteArrayInputStream(mXmlStream.getBytes("UTF-8"));
            in.mark(0);
            // Validate XML format
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            dBuilder.parse(in);
            in.reset();
            // No exceptions/errors --> valid XML format
            Toast.makeText(getApplicationContext(), "Received XML data! Parsing...",
                    Toast.LENGTH_SHORT).show();
            mNumBytesRead = 0;
            // parse the message
            BluetoothXmlParser btXmlParser = new BluetoothXmlParser();
            ArrayList<TireSnapshot> tireSnapshotList = btXmlParser.parseToTireSnapshotList(in);
            if (tireSnapshotList.isEmpty()){
                Toast.makeText(getApplicationContext(),
                        "Failed to obtain TireSnapshot from message received...",
                        Toast.LENGTH_LONG).show();
            }
            for (int i=0; i<tireSnapshotList.size(); i++) {
                mParsedMsg += "Tire/Sensor ID: " + tireSnapshotList.get(i).getSensorId();
                mParsedMsg += "\nS11: " + tireSnapshotList.get(i).getS11();
                mParsedMsg += "\nPressure: " + tireSnapshotList.get(i).getPressure();
                mParsedMsg += "\nMileage: " + tireSnapshotList.get(i).getOdometerMileage();
                mParsedMsg += "\nTimestamp: " + TireSnapshot.convertCalendarToString(tireSnapshotList.get(i).getTimestamp());
                mParsedMsg += "\n\n";
                displayMsg(mParsedMsg, mTextViewParsed);
                //Toast.makeText(getApplicationContext(), info, Toast.LENGTH_SHORT).show();
            }
        } catch (SAXException e) {
            // Parsing XML failed (invalid XML file)
            // Invalid --> assume incomplete and wait for rest of data
            Log.w(Common.LOG_TAG_BT_ACTIVITY, "Received incomplete XML data. Waiting for more...");
            return;
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        }
        */
    }


    @Override
    protected void onActivityResult (int requestCode, int resultCode, Intent data) {
        Log.v(Common.LOG_TAG_BT_ACTIVITY, "onActivityResult with code " + requestCode);
        switch (requestCode) {
            case Common.REQUEST_ENABLE_BT:
                if (resultCode == RESULT_CANCELED) {
                    Log.w(Common.LOG_TAG_BT_ACTIVITY, "Bluetooth enable request canceled");
                    Toast.makeText(getApplicationContext(),
                            "Bluetooth request cancelled...",
                            Toast.LENGTH_LONG).show();
                    // @todo Disable bluetooth features
                }
                else if (resultCode == RESULT_OK) {
                    // @todo connect to sensor/simulator
                    Log.v(Common.LOG_TAG_BT_ACTIVITY, "Bluetooth enabled");
                }
                break;
            case Common.REQUEST_ACCESS_COARSE_LOCATION:
                if (resultCode == RESULT_CANCELED) {
                    Log.w(Common.LOG_TAG_BT_ACTIVITY, "Location access request cancelled");
                    // @todo Cannot discover devices
                    Toast.makeText(getApplicationContext(),
                            "Location access request cancelled...",
                            Toast.LENGTH_LONG).show();
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
                } else {
                    Toast.makeText(getApplicationContext(),
                            "Connection Bluetooth Device Failed...",
                            Toast.LENGTH_LONG).show();
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
                    mNumBytesRead += msgStr.length();
                    mXmlStream += msgStr;
                    if (mNumBytesRead > 7000) {
//                        Log.d(Common.LOG_TAG_BT_ACTIVITY, "numBytes from msg: " + msg.arg1);
//                        Log.d(Common.LOG_TAG_BT_ACTIVITY, msgStr.length() + " Bytes read");
                        Log.d(Common.LOG_TAG_BT_ACTIVITY, mNumBytesRead + " Total bytes read");
                        Log.d(Common.LOG_TAG_BT_ACTIVITY, "String length: " + mXmlStream.length());
//                        Log.v(Common.LOG_TAG_BT_ACTIVITY, msgStr);
                        //Log.v(Common.LOG_TAG_BT_ACTIVITY, mXmlStream);
                        //displayMsg(msgStr, mTextViewReceived);
                        //processMsg(msgStr);
                        displayMsg(mXmlStream, mTextViewReceived);
                        processMsg(mXmlStream);
                    }
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
