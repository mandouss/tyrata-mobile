package edu.duke.ece651.tyrata.communication;

import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import edu.duke.ece651.tyrata.vehicle.TireSnapshot;

/**
 * XML Parser for Bluetooth data
 * @author Saeed Alrahma
 * Created by Saeed on 3/17/2018.
 */

class BluetoothXmlParser {
    // We don't use namespaces
    private static final String ns = null;

    TireSnapshot parseToTireSnapshot(InputStream in) throws XmlPullParserException, IOException {
        TireSnapshot tireSnapshot = new TireSnapshot();
        ArrayList<DailyS11> dailyS11List = parse(in);
        if (!dailyS11List.isEmpty()) {
            DailyS11 dailyS11 = dailyS11List.get(0);
            ArrayList<TireSnapshot> tires = dailyS11.mTires;
            if (!tires.isEmpty()) {
                tireSnapshot = tires.get(0);
                tireSnapshot.setTimestamp(TireSnapshot.convertStringToCalendar(dailyS11.mTimestamp));
                tireSnapshot.setOdometerMileage(dailyS11.mMileage);
            }
        }

        return tireSnapshot;
    }

    /**
     * Parse XML-format data received through Bluetooth
     * @param in InputStream containing XML-format data
     * @return List of entries (dailyS11) received
     * @throws IOException IO Exception
     * @throws XmlPullParserException XmlPullParser Exception
     */
    ArrayList<DailyS11> parse(InputStream in) throws XmlPullParserException, IOException {
        try {
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(in, null);
            parser.nextTag();
            return readFeed(parser);
        } finally {
            in.close();
        }
    }

    /**
     * Read the XML stream to parse
     * @param parser XMLPullParser object
     * @return List of entries (dailyS11) received
     * @throws IOException IO Exception
     * @throws XmlPullParserException XmlPullParser Exception
     */
    private ArrayList<DailyS11> readFeed(XmlPullParser parser) throws XmlPullParserException, IOException {
        ArrayList<DailyS11> dailyS11List = new ArrayList<>();

        parser.require(XmlPullParser.START_TAG, ns, "dailyS11List");
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            // Starts by looking for the entry tag
            if (name.equals("dailyS11")) {
                dailyS11List.add(readDailyS11(parser));
            } else {
                skip(parser);
            }
        }
        return dailyS11List;
    }

    /**
     * Object to store XML entries (dailyS11)
     */
    static class DailyS11 {
        String mTimestamp;
        int mMileage; // value is -1 if error
        ArrayList<TireSnapshot> mTires;

        private DailyS11(String timestamp, int mileage) {
            this.mTimestamp = timestamp;
            this.mMileage = mileage;
            this.mTires = new ArrayList<>();
        }

        private DailyS11() {
            this(null,-1);
        }
    }

    /**
     * Read adn parse the dailyS11 tag
     * @param parser XmlPullParser object
     * @return DailyS11 object containing all relevant data
     * @throws IOException IO Exception
     * @throws XmlPullParserException XmlPullParser Exception
     */
    private DailyS11 readDailyS11(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, ns, "dailyS11");
        DailyS11 dailyS11 = new DailyS11();

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            switch (name) {
                case "timestamp":
                    dailyS11.mTimestamp = readTimestamp(parser);
                    break;
                case "mileage":
                    dailyS11.mMileage  = readMileage(parser);
                    break;
                case "tire":
                    dailyS11.mTires.add(readTire(parser));
                    break;
                default:
                    skip(parser);
                    break;
            }
        }
        return dailyS11;
    }

    /**
     * Process timestamp tags in the feed
     * @param parser XmlPullParser object
     * @return Timestamp as a String
     * @throws IOException IO Exception
     * @throws XmlPullParserException XmlPullParser Exception
     */
    private String readTimestamp(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "timestamp");
        String timestamp = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "timestamp");
        return timestamp;
    }

    /**
     * Process mileage tags in the feed
     * @param parser XmlPullParser object
     * @return The mileage as an int
     * @throws IOException IO Exception
     * @throws XmlPullParserException XmlPullParser Exception
     */
    private int readMileage(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "mileage");
        String mileage = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "mileage");
        return Integer.parseInt(mileage);
    }

    /**
     * Process Tire tags in the feed
     * @param parser XmlPullParser object
     * @return Tire as a TireSnapshot object
     * @throws IOException IO Exception
     * @throws XmlPullParserException XmlPullParser Exception
     */
    private TireSnapshot readTire(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "tire");
        TireSnapshot tire = new TireSnapshot();

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            switch (name) {
                case "sensorID":
                    tire.setSensorId(readSensorID(parser));
                    break;
                case "s11":
                    tire.setS11(readS11(parser));
                    break;
                case "pressure":
                    tire.setPressure(readPressure(parser));
                    break;
                default:
                    skip(parser);
                    break;
            }
        }
        return tire;
    }

    /**
     * Process SensorID tags in the feed
     * @param parser XmlPullParser object
     * @return SensorID as a String
     * @throws IOException IO Exception
     * @throws XmlPullParserException XmlPullParser Exception
     */
    private String readSensorID(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "sensorID");
        String sensorID = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "sensorID");
        return sensorID;
    }

    /**
     * Process Pressure tags in the feed
     * @param parser XmlPullParser object
     * @return Presshure as a double
     * @throws IOException IO Exception
     * @throws XmlPullParserException XmlPullParser Exception
     */
    private double readPressure(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "pressure");
        String pressure = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "pressure");
        return Double.parseDouble(pressure);
    }

    /**
     * Process S11 tags in the feed
     * @param parser XmlPullParser object
     * @return S11 as a double
     * @throws IOException IO Exception
     * @throws XmlPullParserException XmlPullParser Exception
     */
    private double readS11(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "s11");
        String s11 = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "s11");
        return Double.parseDouble(s11);
    }

    /**
     * Read the text contained in a tag
     * @param parser XmlPullParser object
     * @return The text read (tag text value)
     * @throws IOException IO Exception
     * @throws XmlPullParserException XmlPullParser Exception
     */
    private String readText(XmlPullParser parser) throws IOException, XmlPullParserException {
        String result = "";
        if (parser.next() == XmlPullParser.TEXT) {
            result = parser.getText();
            parser.nextTag();
        }
        return result;
    }

    /**
     * Skip irrelevant tags
     * @param parser XmlPullParser object
     * @throws IOException IO Exception
     * @throws XmlPullParserException XmlPullParser Exception
     */
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
