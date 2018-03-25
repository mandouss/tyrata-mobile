//package edu.duke.ece651.tyrata.communication;
//
//import android.database.sqlite.SQLiteDatabase;
//import android.support.v7.app.AppCompatActivity;
//
//import edu.duke.ece651.tyrata.datamanagement.Database;
//import edu.duke.ece651.tyrata.user.User;
//import edu.duke.ece651.tyrata.vehicle.Tire;
//import edu.duke.ece651.tyrata.vehicle.Vehicle;
//
//import static android.content.Context.MODE_PRIVATE;
//
///**
// * Created by Naixin on 2018-03-25.
// */
//
//public class HTTPSender extends AppCompatActivity {
//
//    public String sender(String method, String table, String primaryKey){
//        MessageGenerator myMessage = new MessageGenerator(table, primaryKey);
//        myMessage.message = "<method>" + method + "</method>" + myMessage.message;
//        String messageUrl = "http://vcm-2932.vm.duke.edu:9999/hello/XMLAction?xml_data=" + myMessage.message;
//        return "";
//    }
//
//
//
//
//    private class MessageGenerator {
//        public String message = null;
//
//        public MessageGenerator(String tableName, String primaryKey) {
//            if(tableName == "user"){
//                this.message = createUserXmlMessage(primaryKey);
//            }
//            else if(tableName == "vehicle"){
//                this.message = createVehicleXmlMessage(primaryKey);
//            }
//            else if(tableName == "tire"){
//                this.message = createTireXmlMessage(primaryKey);
//            }
//            else{
//                this.message = "";
//            }
//        }
//
//        private String createUserXmlMessage(String primaryKey){
//
//            Database.myDatabase = openOrCreateDatabase("TyrataData", MODE_PRIVATE, null);
//            User user = Database.getUser(Integer.valueOf(primaryKey));
//            String ans = "<user><user_ID>";
//            ans = ans + primaryKey + "</user_ID><name>" +
//                    user.username + "</name><email>" +
//                    user.email +"</email><phone>" +
//                    user.phone + "</phone></user>";
//            return ans;
//        }
//
//        private String createVehicleXmlMessage(String primaryKey){
//            Database.myDatabase = openOrCreateDatabase("TyrataData", MODE_PRIVATE, null);
//            Vehicle vehicle = Database.getVehicle(primaryKey);
//            String ans = "<vehicle><vin>";
//            ans = ans + primaryKey + "</vin><make>" +
//                    vehicle.getMake() + "</make><model>" +
//                    vehicle.getModel() + "</model><year>" +
//                    vehicle.getYear() + "</year><axis_num>" +
//                    vehicle.getNumAxis() + "</axis_num><tire_num>" +
//                    vehicle.getNumTires() + "</tire_num></vehicle>";
//            return ans;
//        }
//
//        private String createTireXmlMessage(String primaryKey){
//            Database.myDatabase = openOrCreateDatabase("TyrataData", MODE_PRIVATE, null);
//            Tire tire = Database.getTire();
//            //int axis_row, int axis_index, char axis_side, String vin
//            String ans = "<tire><sensor_ID>";
//            ans = ans + primaryKey + "</sensor_ID><manufacturer>" +
//                    tire.getManufacturer() + "</manufacturer><model>" +
//                    tire.getModel() + "</model><sku>" +
//                    tire.getSku() + "</sku><axis_row>" +
//                    tire.getAxisRow() + "</axis_row><aixs_side>" +
//                    tire.getAxisSide() + "</axis_side><axis_index>" +
//                    tire.getAxisIndex() + "</axis_index><init_ss_ID>" +
//                    tire.get_INIT_SS() + "</init_ss_ID><cur_ss_ID>" +
//                    tire.get_CUR_SS() + "</cur_ss_ID><init_thickness>" +
//                    tire.get_INIT_THICK() + "</init_thichness></tire>";
//            return ans;
//        }
//
//    }
//}
