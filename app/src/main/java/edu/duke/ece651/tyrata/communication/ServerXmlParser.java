package edu.duke.ece651.tyrata.communication;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;

import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;

import edu.duke.ece651.tyrata.datamanagement.Database;
import static android.content.Context.MODE_PRIVATE;
import static android.database.sqlite.SQLiteDatabase.openOrCreateDatabase;
import static edu.duke.ece651.tyrata.datamanagement.Database.myDatabase;

/**
 * Created by yangm on 2018/3/24.
 */

public class ServerXmlParser {
    private static final String ns = null;

    public void parse_server(InputStream in) throws XmlPullParserException, IOException {
        parse(in);
    }

    private void parse(InputStream in) throws XmlPullParserException, IOException {
        try {
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(in, null);
            parser.nextTag();
            readFeed(parser);
        } finally {
            in.close();
        }
    }

    private void readFeed(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, ns, "download");
        while(parser.next() != XmlPullParser.END_TAG){
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            switch(name){
                case "user":
                    readuser(parser);
                    break;
                case"vehicle":
                    readvehicle(parser);
                    break;
                case "tire":
                    readtire(parser);
                    break;
                case "sanpshot":
                    readsnapshot(parser);
                    break;
                default:
                    skip(parser);
                    break;
            }
        }
    }

    private void readuser(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "user");
        int user_id = 0;
        String username="";
        String email="";
        String phone="";

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            switch (name) {
                case "user_id":
                    user_id = Integer.valueOf(readcontent(parser,"user_id"));
                    break;
                case "name":
                    username = readcontent(parser,"name");
                    break;
                case "email":
                    email = readcontent(parser,"email");
                    break;
                case "phone_number":
                    phone = readcontent(parser,"phone_number");
                    break;
                default:
                    skip(parser);
                    break;
            }
        }
        myDatabase = SQLiteDatabase.openOrCreateDatabase("TyrataData", null);
        // For test, drop and create tables
        Database.createTable();
        Database.storeUserData(username, email, phone);
        Database.testUserTable();
        myDatabase.close();
    }



    private void readvehicle(XmlPullParser parser) throws IOException, XmlPullParserException {
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
                case "tire_num":
                    tire_num = Integer.valueOf(readcontent(parser,"tire_num"));
                    break;
                case "axis_num":
                    axis_num = Integer.valueOf(readcontent(parser,"axis_num"));
                    break;
                case "user_id":
                    user_id = Integer.valueOf(readcontent(parser,"user_id"));
                    break;
                default:
                    skip(parser);
                    break;
            }
        }
        Database.myDatabase = SQLiteDatabase.openOrCreateDatabase("TyrataData",  null);
        Database.storeVehicleData(vin, make, model, year, axis_num,tire_num,user_id );
        myDatabase.close();
    }

    private void readtire(XmlPullParser parser) throws IOException, XmlPullParserException {
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
        myDatabase = SQLiteDatabase.openOrCreateDatabase("TyrataData",   null);
        Database.storeTireData(sensorId, manufacturer, model, sku, vin, axisRow, axisSide, axisIndex, init_thickness,INIT_SS_ID, CUR_SS_ID );
        Database.myDatabase.close();
    }

    private void readsnapshot(XmlPullParser parser) throws IOException, XmlPullParserException {
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
        myDatabase = SQLiteDatabase.openOrCreateDatabase("TyrataData",   null);
        Database.storeSnapshot(id,s11,timestamp,mileage,pressure,tire_id,outlier,thickness,eol,time_to_replacement,longitutde,lat );
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
