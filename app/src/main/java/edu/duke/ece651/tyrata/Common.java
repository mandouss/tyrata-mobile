package edu.duke.ece651.tyrata;

import java.util.UUID;

/**
 * Class containing all common constants
 * @author Saeed Alrahma
 * Created by Saeed on 2/25/2018.
 */

public class Common {
    /* CONSTANTS */

    // Defines constants for logger keywords/tags
    public static final String LOG_TAG_BT_API = "BluetoothAPI";
    public static final String LOG_TAG_BT_ACTIVITY = "BluetoothActivity";
    public static final String LOG_TAG_BT_DEVICE_LIST_ACTIVITY = "BtDeviceListActivity";
    public static final String LOG_TAG_REGISTER_ACTIVITY = "RegisterActivity";
    public static final String LOG_TAG_LOGIN_ACTIVITY = "LoginActivity";
    public static final String LOG_TAG_AUTHENTICATION_API = "AuthenticationAPI";

    // Defines constants for bundle keywords/tags
    public static final String DEVICE_NAME = "device_name";

    //@todo change UUID
    public static final UUID MY_UUID = UUID.fromString("fa87c0d0-afac-11de-8a39-0800200c9a66");

    // Defines several constants used when transmitting messages between the
    // Bluetooth service and the UI.
    public static final int MESSAGE_READ = 0;
    public static final int MESSAGE_WRITE = 1;
    public static final int MESSAGE_TOAST = 2;
    public static final int MESSAGE_DEVICE_NAME = 3;


    // Defines constants for Activity result return
    public static final int REQUEST_ENABLE_BT = 1;
    public static final int REQUEST_ACCESS_COARSE_LOCATION = 2;
    public static final int REQUEST_CONNECT_BT_DEVICE = 3;


    /* GLOBAL VARIABLES */
}
