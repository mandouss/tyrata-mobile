package edu.duke.ece651.tyrata.communication;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;

import android.util.Log;
import android.util.Xml;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;

import edu.duke.ece651.tyrata.datamanagement.Database;
//import static edu.duke.ece651.tyrata.datamanagement.Database.myDatabase;

/**
 * Created by yangm on 2018/3/24.
 */

public class ServerXmlParser extends AppCompatActivity {
    private static final String ns = null;

    public void parse_server(InputStream in, Context context) throws XmlPullParserException, IOException {
        parse(in, context);
    }

    private void parse(InputStream in, Context context) throws XmlPullParserException, IOException {
        try {
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(in, null);
            parser.nextTag();
            readFeed(parser, context);
        } finally {
            in.close();
        }
    }
    private void readFeed(XmlPullParser parser, Context context)throws XmlPullParserException, IOException{
        parser.require(XmlPullParser.START_TAG, ns, "message");
        String message;
        while (parser.next() != XmlPullParser.END_TAG){
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            switch (name) {
                case "download":
                    readDownload(parser, context);
                    break;
                case "ack":
                    int id = Integer.valueOf(readcontent(parser, "ack"));
                    if (id > 0) {
                        Database.myDatabase = context.openOrCreateDatabase("TyrataData", MODE_PRIVATE, null);
                        Database.deleteTrace(id);
                        Database.myDatabase.close();
                    }
                    break;
                case "error":
                    message = readcontent(parser,"error");
                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                    break;
                case "authentication":
                    readAuthentication(parser, context);
                    break;
                default:
                    skip(parser);
                    break;
            }
        }
    }

    private void readAuthentication(XmlPullParser parser, Context context) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, ns, "authentication");
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            SharedPreferences.Editor editor= getSharedPreferences("msg_from_server",MODE_PRIVATE).edit();
            String name = parser.getName();
            switch (name) {
                case "hash":
                    String hashed_pass = readcontent(parser, "hash");
                    editor.putString("hash",hashed_pass);
                    editor.commit();
                    break;
                case "salt":
                    String salt = readcontent(parser, "salt");
                    editor.putString("salt",salt);
                    editor.commit();
                    break;
                default:
                    skip(parser);
                    break;
            }
        }

    }

    private void readDownload(XmlPullParser parser, Context context) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, ns, "download");
            while (parser.next() != XmlPullParser.END_TAG) {
                if (parser.getEventType() != XmlPullParser.START_TAG) {
                    continue;
                }
                String name = parser.getName();
                switch (name) {
                    case "user":
                        readuser(parser, context);
                        break;
                    case "vehicle":
                        readvehicle(parser, context);
                        break;
                    case "tire":
                        readtire(parser, context);
                        break;
                    case "snapshot":
                        readsnapshot(parser, context);
                        break;
                    default:
                        skip(parser);
                        break;
                }
            }

    }

    private void readuser(XmlPullParser parser,Context context) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "user");
        String username="";
        String email="";
        String phone="";

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            switch (name) {
                case "name":
                    username = readcontent(parser,"name");
                    break;
                case "email":
                    email = readcontent(parser,"email");
                    break;
                case "phone_num":
                    phone = readcontent(parser,"phone_num");
                    break;
                default:
                    skip(parser);
                    break;
            }
        }
        Database.myDatabase = context.openOrCreateDatabase("TyrataData", MODE_PRIVATE, null);
        Database.createTable();
        Database.storeUserData(username, email, phone);
        Database.testUserTable();
        Database.myDatabase.close();
    }



    private void readvehicle(XmlPullParser parser, Context context) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "vehicle");
        String make = "";
        String model = "";
        Integer year = 0;
        String vin = "";
        Integer axis_num = 0;
        Integer tire_num = 0;
        Integer user_id = 0;

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            switch (name) {
                case "make":
                    make = readcontent(parser,"make");
                    break;
                case "model":
                    model = readcontent(parser,"model");
                    break;
                case "year":
                    year = Integer.valueOf(readcontent(parser,"year"));
                    break;
                case "vin":
                    vin = readcontent(parser,"vin");
                    break;
                case "numtires":
                    tire_num = Integer.valueOf(readcontent(parser,"numtires"));
                    break;
                case "numaxis":
                    axis_num = Integer.valueOf(readcontent(parser,"numaxis"));
                    break;
                case "user_id":
                    user_id = Integer.valueOf(readcontent(parser,"user_id"));
                    break;
                default:
                    skip(parser);
                    break;
            }
        }

        Database.myDatabase = context.openOrCreateDatabase("TyrataData", MODE_PRIVATE, null);
        Database.storeVehicleData("",-1, vin, make, model, year, axis_num, tire_num, user_id);
        Database.myDatabase.close();
    }

    private void readtire(XmlPullParser parser,Context context) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "tire");
        String sensorId = "";
        String manufacturer = "";
        String model = "";
        String sku = "";
        String vin = "";
        int axisRow = 0;
        String axisSide = "";
        int axisIndex = 0;
        double init_thickness = 0;
        int INIT_SS_ID = 0;
        int CUR_SS_ID = 0;
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            switch (name) {
                case "sensor_id":
                    sensorId = readcontent(parser,"sensor_id");
                    break;
                case "manufacturer":
                    manufacturer = readcontent(parser,"manufacturer");
                    break;
                case "model":
                    model = readcontent(parser,"model");
                    break;
                case "sku":
                    sku = readcontent(parser,"sku");
                    break;
                case "vehicle_id":
                    vin = readcontent(parser,"vehicle_id");
                    break;
                case "axis_row":
                    axisRow = Integer.valueOf(readcontent(parser,"axis_row"));
                    break;
                case "axis_side":
                    axisSide = readcontent(parser,"axis_side");
                    break;
                case "axis_index":
                    axisIndex = Integer.valueOf(readcontent(parser,"axis_index"));
                    break;
                case "init_ss_id":
                    INIT_SS_ID = Integer.valueOf(readcontent(parser,"init_ss_id"));
                    break;
                case "cur_ss_id":
                    CUR_SS_ID = Integer.valueOf(readcontent(parser,"cur_ss_id"));
                    break;
                case "init_thickness":
                    init_thickness = Integer.valueOf(readcontent(parser,"init_thickness"));
                    break;
                default:
                    skip(parser);
                    break;
            }
        }
        Database.myDatabase = context.openOrCreateDatabase("TyrataData", MODE_PRIVATE, null);
        Database.storeTireData("",-1, sensorId, manufacturer, model, sku, vin, axisRow, axisSide, axisIndex, init_thickness, INIT_SS_ID, CUR_SS_ID);
        Database.myDatabase.close();

    }

    private void readsnapshot(XmlPullParser parser,Context context) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "snapshot");
        Integer id = 0;
        Double s11 = 0.0;
        String timestamp = "";
        Double mileage = 0.0;
        Double pressure = 0.0;
        String tire_id = "";
        boolean outlier = false;
        Double thickness = 0.0;
        String eol = "";
        String time_to_replacement = "";
        Double longitutde = 0.0;
        Double lat = 0.0;
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            switch (name) {
                case "id":
                    id = Integer.valueOf(readcontent(parser,"id"));
                    break;
                case "s11":
                    s11 = Double.valueOf(readcontent(parser,"s11"));
                    break;
                case "timestamp":
                    timestamp = readcontent(parser,"timestamp");
                    break;
                case "mileage":
                    mileage = Double.valueOf(readcontent(parser,"mileage"));
                    break;
                case "pressure":
                    pressure = Double.valueOf(readcontent(parser,"pressure"));
                    break;
                case "tire_id":
                    tire_id = readcontent(parser,"tire_id");
                    break;
                case "outlier":
                    outlier = Boolean.parseBoolean(readcontent(parser,"outlier"));
                    break;
                case "thickness":
                    thickness = Double.valueOf(readcontent(parser,"thickness"));
                    break;
                case "eol":
                    eol = readcontent(parser,"eol");
                    break;
                case "time_to_replacement":
                    time_to_replacement = readcontent(parser,"time_to_replacement");
                    break;
                case "long":
                    longitutde = Double.valueOf(readcontent(parser,"long"));
                    break;
                case "lat":
                    lat = Double.valueOf(readcontent(parser,"lat"));
                    break;
                default:
                    skip(parser);
                    break;
            }
        }

        Database.myDatabase = context.openOrCreateDatabase("TyrataData", 0, null);
        Database.storeSnapshot(s11, timestamp, mileage, pressure, tire_id, outlier, thickness, eol, time_to_replacement, longitutde, lat);
        Database.myDatabase.close();

    }

    private String readcontent(XmlPullParser parser,String label) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, label);
        String result = "";
        if (parser.next() == XmlPullParser.TEXT) {
            result = parser.getText();
            parser.nextTag();
        }
        parser.require(XmlPullParser.END_TAG, ns, label);
        return result;
    }

    private void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
        if (parser.getEventType() != XmlPullParser.START_TAG) {
            throw new IllegalStateException();
        }
        int depth = 1;
        while (depth != 0) {
            switch (parser.next()) {
                case XmlPullParser.END_TAG:
                    depth--;
                    break;
                case XmlPullParser.START_TAG:
                    depth++;
                    break;
            }
        }
    }

}
