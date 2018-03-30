package edu.duke.ece651.tyrata.communication;

import android.content.Context;
import android.support.constraint.solver.widgets.Snapshot;
import android.support.v7.app.AppCompatActivity;

import java.util.ArrayList;

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

    public String send_to_cloud(Context context){
        Database.myDatabase = context.openOrCreateDatabase("TyrataData", MODE_PRIVATE, null);
        ArrayList<Trace_item> trace_list = Database.getTrace();
        Database.myDatabase.close();
        String message = "<message>";

        for(int i = 0; i < trace_list.size(); i++){
            message = message + "<id>" + String.valueOf(trace_list.get(i).getId()) + "</id><method>" + trace_list.get(i).getMethod() + "</method>";
            switch (trace_list.get(i).getTable_name()){
                case "USER":
                    message = message + userMessage(trace_list.get(i).getTarget_id(),context);
                    break;
                case "VEHICLE":
                    message = message + vehicleMessage(trace_list.get(i).getTarget_id(),context);
                    break;
                case "TIRE":
                    message = message + tireMessage(trace_list.get(i).getTarget_id(),context);
                    break;
                case "SNAPSHOT":
                    message = message + snapshotMessage(trace_list.get(i).getTarget_id(),context);
                    break;
                case "ACCIDENT":
                    message = message + accidentMessage(trace_list.get(i).getTarget_id(),context);
                    break;
                default:
                    break;
            }
            message = message + "<original_info>" + trace_list.get(i).getOrigin_info() + "</original_info>";
        }
        
        message = message + "</message>";
        //HttpActivity httpActivity = new HttpActivity();
        String myUrl = "http://vcm-2932.vm.duke.edu:9999/hello/XMLAction?xml_data=" + message;
        //httpActivity.startDownload(myUrl);
        return myUrl;
    }

    private String userMessage(int id,Context context){
        String m = "";
        Database.myDatabase = context.openOrCreateDatabase("TyrataData", MODE_PRIVATE, null);
        User user = Database.getUser(id);
        Database.myDatabase.close();
        m = m + "<user><username>" + user.username +
                "</username><email>" + user.email +
                "</email><phone>" + user.phone +
                "</phone></user>";
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
