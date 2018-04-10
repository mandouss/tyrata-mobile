package edu.duke.ece651.tyrata.communication;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import edu.duke.ece651.tyrata.datamanagement.Database;
import edu.duke.ece651.tyrata.user.User;
import edu.duke.ece651.tyrata.vehicle.Tire;
import edu.duke.ece651.tyrata.vehicle.TireSnapshot;
import edu.duke.ece651.tyrata.vehicle.Vehicle;

import static edu.duke.ece651.tyrata.vehicle.TireSnapshot.convertCalendarToString;

/**
 * Created by zhanglian1 on 2018-03-29.
 */

public class HTTPsender extends AppCompatActivity {



    public String send_and_receive(String message, Context context){
        String myUrl = "http://vcm-2932.vm.duke.edu:9999/tyrata-team/XmlAction?xml_data=" + message;
        HttpActivity httpActivity = new HttpActivity();
        //httpActivity.mNetworkFragment = NetworkFragment.getInstance(getSupportFragmentManager(), myUrl, context);
        SharedPreferences.Editor editor= context.getSharedPreferences("msg_from_server",MODE_PRIVATE).edit();
        editor.putString("msg","");
        editor.commit();
        Log.i("send",myUrl);
        httpActivity.send(myUrl,context);
        httpActivity.startDownload(context);

        SharedPreferences editor_get = getSharedPreferences("msg_from_server",MODE_PRIVATE);
        String message_get = "";
        do{
            message_get= editor_get.getString("msg","");
        }while (message_get == "");
        Log.i("receive",message_get);
        return message_get;
    }

    public String send_to_cloud(Context context){
        Database.myDatabase = context.openOrCreateDatabase("TyrataData", MODE_PRIVATE, null);
        Trace_item trace_item = Database.getTrace();
        Database.myDatabase.close();
        if(trace_item != null) {
            String message = "<message>";
            message = message + "<id>" + String.valueOf(trace_item.getId()) + "</id><method>" + trace_item.getMethod() + "</method>";
            switch (trace_item.getTable_name()) {
                case "USER":
                    message = message + userMessage(trace_item.getTarget_id(), context);
                    break;
                case "VEHICLE":
                    message = message + vehicleMessage(trace_item.getTarget_id(), context);
                    break;
                case "TIRE":
                    message = message + tireMessage(trace_item.getTarget_id(), context);
                    break;
                case "SNAPSHOT":
                    message = message + snapshotMessage(trace_item.getTarget_id(), context);
                    break;
                case "ACCIDENT":
                    message = message + accidentMessage(trace_item.getTarget_id(), context);
                    break;
                default:
                    break;
            }
            message = message + "<original_info>" + trace_item.getOrigin_info() + "</original_info>";

            message = message + "</message>";
            //HttpActivity httpActivity = new HttpActivity();
            String myUrl = "http://vcm-2932.vm.duke.edu:9999/tyrata-team/XmlAction?xml_data=" + message;
            return myUrl;
        }
        //httpActivity.startDownload(myUrl);
        else{
            return null;
        }
    }

    private String userMessage(int id,Context context){
        String m = "";
        Database.myDatabase = context.openOrCreateDatabase("TyrataData", MODE_PRIVATE, null);
        User user = Database.getUser(id);
        Database.myDatabase.close();
        m = m + "<user><name>" + user.username +
                "</name><email>" + user.email +
                "</email><phone_num>" + user.phone +
                "</phone_num></user>";
        return  m;
    }

    private String vehicleMessage(int id,Context context){
        String m = "";
        Database.myDatabase = context.openOrCreateDatabase("TyrataData", MODE_PRIVATE, null);
        String vin = Database.getVin(id);
        String email = Database.getEmail(id);
        Vehicle vehicle = Database.getVehicle(vin);
        Database.myDatabase.close();
        m = m + "<vehicle><make>" + vehicle.getMake() +
                "</make><model>" + vehicle.getModel() +
                "</model><year>" + String.valueOf(vehicle.getYear()) +
                "</year><vin>" + vehicle.getVin() +
                "</vin><numaxis>" + String.valueOf(vehicle.getNumAxis()) +
                "</numaxis><numtires>" + String.valueOf(vehicle.getNumTires()) +
                "</numtires><email>" + email +
                "</email></vehicle>";
        return m;
    }

    private String tireMessage(int id,Context context){
        String m = "";
        Database.myDatabase = context.openOrCreateDatabase("TyrataData", MODE_PRIVATE, null);
        Tire tire = Database.getTire(id);
        String vin = Database.getVinFromTire(id);
        Database.myDatabase.close();
        m = m + "<tire><sensorid>" + tire.getSensorId() +
                "</sensorid><manufacturer>" + tire.getManufacturer() +
                "</manufacturer><model>" + tire.getModel() +
                "</model><sku>" + tire.getSku() +
                "</sku><vin>" + vin +
                "</vin><axisrow>" + String.valueOf(tire.getAxisRow()) +
                "</axisrow><axisside>" + tire.getAxisSide() +
                "</axisside><axisindex>" + String.valueOf(tire.getAxisIndex()) +
                "</axisindex><initthickness>" + String.valueOf(tire.get_INIT_THICK()) +
                "</initthickness></tire>";
        return m;
    }

    private String snapshotMessage(int id,Context context){
        String m = "";
        Database.myDatabase = context.openOrCreateDatabase("TyrataData", MODE_PRIVATE, null);
        TireSnapshot snapshot = Database.getSnapshot(id);
        String sensor_id = Database.getTireFromSSid(id);
        Database.myDatabase.close();
        m = m + "<snapshot><s11>" + String.valueOf(snapshot.getS11()) +
                "</s11><timestamp>" + convertCalendarToString(snapshot.getTimestamp()) +
                "</timestamp><mileage>" + String.valueOf(snapshot.getOdometer()) +
                "</mileage><pressure>" + String.valueOf(snapshot.getPressure()) +
                "</pressure><sensorid>" + sensor_id +
                "</sensorid><thickness>" + String.valueOf(snapshot.get_CURR_THCK()) +
                "</thickness><eol>" + snapshot.getEOL() +
                "</eol><replacetime>" + snapshot.getRepTime() +
                "</replacetime></snapshot>";
        return m;
    }

    private String accidentMessage(int id,Context context){
        String m = "";
        Database.myDatabase = context.openOrCreateDatabase("TyrataData", MODE_PRIVATE, null);
        String description = Database.getAccident(id);
        String email = Database.getEmailFromAccident(id);
        Database.myDatabase.close();
        m = m + "<accident><description>" + description +
                "</description><email>" + email +
                "</email></accident>";
        return m;
    }







}
