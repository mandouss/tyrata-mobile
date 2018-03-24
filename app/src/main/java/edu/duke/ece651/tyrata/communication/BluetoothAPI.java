package edu.duke.ece651.tyrata.communication;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;

import edu.duke.ece651.tyrata.Common;

import static android.support.v4.app.ActivityCompat.startActivityForResult;

/**
 * This class has Bluetooth API
 * @author Saeed Alrahma
 * Created by Saeed on 2/25/2018.
 */

class BluetoothAPI {
    /* Constants */

    /* GLOBAL */
    private static BluetoothAdapter mBluetoothAdapter; // Device BluetoothAPI adapter (required for all BluetoothAPI activity)
    private static AcceptThread mAcceptThread;
    private static ConnectThread mConnectThread;
    private static ConnectedThread mConnectedThread;
    private static Handler mHandler; // handler that gets info from Bluetooth service

    /* Functions */
    static void enableBt(Activity activity, Handler handler) {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            // Device doesn't support BluetoothAPI
            Log.e(Common.LOG_TAG_BT_API, "Device doesn't support BluetoothAPI");
            // @todo disable bluetooth features
            return ;
        }

        // Check if BluetoothAPI is enabled
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(activity, enableBtIntent, Common.REQUEST_ENABLE_BT, null);
        }

        mHandler = handler;
    }

    static void disableBt() {
        // Make sure we're not doing discovery anymore
        if (mBluetoothAdapter != null) {
            mBluetoothAdapter.cancelDiscovery();
        }
    }

    static Set<BluetoothDevice> getBtPairedDevices() {
        return mBluetoothAdapter.getBondedDevices();
    }

    static void queryBtPairedDevices() {
        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();

        if (pairedDevices.size() > 0) {
            // There are paired devices
            // Get the name and address of each paired device.
            Log.v(Common.LOG_TAG_BT_API, "List of paired devices:");
            for (BluetoothDevice device : pairedDevices) {
                Log.v(Common.LOG_TAG_BT_API, "Device name: " + device.getName() + ", " +
                        "MAC address: " + device.getAddress());
            }
        }
    }

    static void discoverBtDevices(Activity activity) {
        // If we're already discovering, stop it
        if (mBluetoothAdapter.isDiscovering()) {
            Log.d(Common.LOG_TAG_BT_API, "cancelDiscovery()");
            mBluetoothAdapter.cancelDiscovery();
        }

        // Check for location permission
        if (ContextCompat.checkSelfPermission(activity,
                Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            // Request permission for location
            Log.d(Common.LOG_TAG_BT_API, "Requesting location access...");
            ActivityCompat.requestPermissions(activity,
                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                Common.REQUEST_ACCESS_COARSE_LOCATION);
        } else {
            // Permission has already been granted
            // Request discover from BluetoothAdapter
            boolean discoverySuccess = mBluetoothAdapter.startDiscovery();
            Log.d(Common.LOG_TAG_BT_API,
                    "startDiscovery() " + (discoverySuccess? "successful":"failed"));
        }

        /* //@todo if I wanted to show a message for rationale to access location
        // Should we show an explanation?
        if (ActivityCompat.shouldShowRequestPermissionRationale(thisActivity,
                Manifest.permission.READ_CONTACTS)) {

            // Show an explanation to the user *asynchronously* -- don't block
            // this thread waiting for the user's response! After the user
            // sees the explanation, try again to request the permission.

        } else {
        }
        */
    }

    static void cancelBtDiscovery() {
        // Make sure we're not doing discovery anymore
        if (mBluetoothAdapter != null) {
            mBluetoothAdapter.cancelDiscovery();
        }
    }

    static void acceptBt() {
        // Start the thread to listen on a BluetoothServerSocket
        if (mAcceptThread == null) {
            Log.d(Common.LOG_TAG_BT_API, "Starting AcceptThread");
            mAcceptThread = new AcceptThread();
            mAcceptThread.start();
        }
        Log.d(Common.LOG_TAG_BT_API, "Starting ConnectThread...");
    }

    static void connectBt(BluetoothDevice device) {
        Log.d(Common.LOG_TAG_BT_API, "ConnectThread() called");
        // Start the thread to connect with the given device
        if (mConnectThread == null) {
            mConnectThread = new ConnectThread(device);
            mConnectThread.start();
        } else {
            Log.d(Common.LOG_TAG_BT_API, "ConnectThread already exists");
        }
    }

    static void connectBt(String deviceAddress) {
        connectBt(mBluetoothAdapter.getRemoteDevice(deviceAddress));
    }

    /** Cancel all threads
     *
     */
    static void closeBtConnection() {
        if (mAcceptThread != null)
            mAcceptThread.cancel();

        if (mConnectThread != null)
            mConnectThread.cancel();

        if (mConnectedThread != null)
            mConnectedThread.cancel();

        mAcceptThread = null;
        mConnectThread = null;
        mConnectedThread = null;
    }

    /**
     * Start the ConnectedThread to begin managing a Bluetooth connection
     *
     * @param socket The BluetoothSocket on which the connection was made
     * @param device The BluetoothDevice that has been connected
     */
    private static void connectedBt(BluetoothSocket socket, BluetoothDevice device) {
        // Start the thread to manage the connection and perform transmissions
        mConnectedThread = new ConnectedThread(socket);
        mConnectedThread.start();
        // Send the name of the connected device back to the UI Activity
        Message msg = mHandler.obtainMessage(Common.MESSAGE_DEVICE_NAME);
        Bundle bundle = new Bundle();
        bundle.putString(Common.DEVICE_NAME, device.getName());
        msg.setData(bundle);
        mHandler.sendMessage(msg);
    }

    /**
     * Write to the ConnectedThread
     *
     * @param out The bytes to write
     * @see ConnectedThread#write(byte[])
     */
    static void write(byte[] out) {
        if (mConnectedThread != null) {
            Log.d(Common.LOG_TAG_BT_API, "Writing to device through Bluetooth");
            // Perform the write
            mConnectedThread.write(out);
        } else {
            Log.d(Common.LOG_TAG_BT_API, "No ConnectedThread Available");
        }
    }

    static String getDeviceName() {
        return mBluetoothAdapter.getName();
    }

    /**
     * This thread runs while listening for incoming connections. It behaves
     * like a server-side client. It runs until a connection is accepted
     * (or until cancelled).
     */
    private static class AcceptThread extends Thread {
        private final BluetoothServerSocket mmServerSocket;

        AcceptThread() {
            // Use a temporary object that is later assigned to mmServerSocket
            // because mmServerSocket is final.
            BluetoothServerSocket tmp = null;
            try {
                // MY_UUID is the app's UUID string, also used by the client code.
                tmp = mBluetoothAdapter.listenUsingRfcommWithServiceRecord(
                        "@string/app_name", Common.MY_UUID);
            } catch (IOException e) {
                Log.e(Common.LOG_TAG_BT_API, "Socket's listen() method failed", e);
            }
            mmServerSocket = tmp;
            Log.d(Common.LOG_TAG_BT_API, "Bluetooth Server Socket " + mmServerSocket);
        }

        public void run() {
            BluetoothSocket socket;
            // Keep listening until exception occurs or a socket is returned.
            while (true) {
                try {
                    socket = mmServerSocket.accept();
                } catch (IOException e) {
                    Log.e(Common.LOG_TAG_BT_API, "Socket's accept() method failed", e);
                    break;
                }

                if (socket != null) {
                    // A connection was accepted. Perform work associated with
                    // the connection in a separate thread.
                    connectedBt(socket, socket.getRemoteDevice());
                    Log.d(Common.LOG_TAG_BT_API, "AcceptThread is ConnectedThread");
                    try {
                        mmServerSocket.close();
                    } catch (IOException e) {
                        Log.e(Common.LOG_TAG_BT_API, "Socket's close() method failed", e);
                    }
                    break;
                }
            }
        }

        // Closes the connect socket and causes the thread to finish.
        void cancel() {
            try {
                mmServerSocket.close();
            } catch (IOException e) {
                Log.e(Common.LOG_TAG_BT_API, "Could not close the connect socket", e);
            }
        }
    }

    private static class ConnectThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final BluetoothDevice mmDevice;

        ConnectThread(BluetoothDevice device) {
            // Use a temporary object that is later assigned to mmSocket
            // because mmSocket is final.
            BluetoothSocket tmp = null;
            mmDevice = device;

            try {
                // Get a BluetoothSocket to connect with the given BluetoothDevice.
                // MY_UUID is the app's UUID string, also used in the server code.
                tmp = device.createRfcommSocketToServiceRecord(Common.MY_UUID);
            } catch (IOException e) {
                Log.e(Common.LOG_TAG_BT_API, "Socket's create() method failed", e);
            }
            mmSocket = tmp;
            Log.d(Common.LOG_TAG_BT_API, "Bluetooth Socket " + mmSocket);
        }

        public void run() {
            // Cancel discovery because it otherwise slows down the connection.
            mBluetoothAdapter.cancelDiscovery();

            try {
                // Connect to the remote device through the socket. This call blocks
                // until it succeeds or throws an exception.
                mmSocket.connect();
            } catch (IOException connectException) {
                // Unable to connect; close the socket and return.
                try {
                    mmSocket.close();
                } catch (IOException closeException) {
                    Log.e(Common.LOG_TAG_BT_API, "Could not close the client socket", closeException);
                }
                return;
            }

            // The connection attempt succeeded. Perform work associated with
            // the connection in a separate thread.
            connectedBt(mmSocket, mmDevice);
            Log.d(Common.LOG_TAG_BT_API, "ConnectThread is ConnectedThread");
        }

        // Closes the client socket and causes the thread to finish.
        void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                Log.e(Common.LOG_TAG_BT_API, "Could not close the client socket", e);
            }
        }
    }

    /**
     * This thread runs during a connection with a remote device.
     * It handles all incoming and outgoing transmissions.
     */
    private static class ConnectedThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;
        private byte[] mmBuffer; // mmBuffer store for the stream

        ConnectedThread(BluetoothSocket socket) {
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            // Get the input and output streams; using temp objects because
            // member streams are final.
            try {
                tmpIn = socket.getInputStream();
            } catch (IOException e) {
                Log.e(Common.LOG_TAG_BT_API, "Error occurred when creating input stream", e);
            }
            try {
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
                Log.e(Common.LOG_TAG_BT_API, "Error occurred when creating output stream", e);
            }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
            Log.d(Common.LOG_TAG_BT_API, "Connected socket and attached i/o streams");
        }

        public void run() {
            Log.i(Common.LOG_TAG_BT_API, "BEGIN ConnectedThread");
            // @TODO change byte array size to max required
            mmBuffer = new byte[1024];
            int numBytes; // bytes returned from read()

            // Keep listening to the InputStream until an exception occurs.
            while (true) {
                try {
                    // Read from the InputStream.
                    numBytes = mmInStream.read(mmBuffer);
                    // Send the obtained bytes to the UI activity.
                    Message readMsg = mHandler.obtainMessage(
                            Common.MESSAGE_READ, numBytes, -1, mmBuffer);
                    readMsg.sendToTarget();
                } catch (IOException e) {
                    Log.d(Common.LOG_TAG_BT_API, "Input stream was disconnected", e);
                    break;
                }
            }
        }

        // @todo I didn't proofread this method
        // Call this from the main activity to send data to the remote device.
        /**
         * Write to the connected OutStream.
         *
         * @param bytes The bytes to write
         */
        void write(byte[] bytes) {
            try {
                mmOutStream.write(bytes);

                // Share the sent message with the UI activity.
                Message writtenMsg = mHandler.obtainMessage(
                        Common.MESSAGE_WRITE, bytes.length, -1, bytes);
                writtenMsg.sendToTarget();
            } catch (IOException e) {
                Log.e(Common.LOG_TAG_BT_API, "Error occurred when sending data", e);

                // Send a failure message back to the activity.
                Message writeErrorMsg =
                        mHandler.obtainMessage(Common.MESSAGE_TOAST);
                Bundle bundle = new Bundle();
                bundle.putString("toast",
                        "Couldn't send data to the other device");
                writeErrorMsg.setData(bundle);
                mHandler.sendMessage(writeErrorMsg);
            }
        }

        // Call this method from the main activity to shut down the connection.
        void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                Log.e(Common.LOG_TAG_BT_API, "Could not close the connect socket", e);
            }
        }
    }
}
