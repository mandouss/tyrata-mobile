package edu.duke.ece651.tyrata;

import java.util.UUID;

/**
 * Created by Saeed on 2/25/2018.
 */

public class Common {
    /* CONSTANTS */
    public static final String LOG_TAG_BT_API = "BluetoothAPI";
    public static final String LOG_TAG_BT_ACTIVITY = "BluetoothActivity";
    public static final UUID MY_UUID = UUID.fromString("@string/app_name");

    // Defines several constants used when transmitting messages between the
    // Bluetooth service and the UI.
    public static final int MESSAGE_READ = 0;
    public static final int MESSAGE_WRITE = 1;
    public static final int MESSAGE_TOAST = 2;



    public static final int REQUEST_ENABLE_BT = 1;


    /* GLOBAL VARIABLES */
}
