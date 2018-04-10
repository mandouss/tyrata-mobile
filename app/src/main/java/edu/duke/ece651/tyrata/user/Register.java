package edu.duke.ece651.tyrata.user;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParserException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Scanner;

import edu.duke.ece651.tyrata.R;
import edu.duke.ece651.tyrata.communication.ServerXmlParser;
import edu.duke.ece651.tyrata.datamanagement.Database;

public class Register extends AppCompatActivity {

    String message_username;
    String message_email;
    String message_phone;
    String message_password;
    String message_confirmPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        checkInternetConenction();
    }
    public void Register_to_login(View view) {

        EditText username = (EditText) findViewById(R.id.register_edit_username);
        message_username = username.getText().toString();

        EditText email = (EditText) findViewById(R.id.register_edit_email);
        message_email = email.getText().toString();

        EditText phone = (EditText) findViewById(R.id.register_edit_phonenumber);
        message_phone = phone.getText().toString();

        EditText password = findViewById(R.id.register_edit_password);
        message_password = password.getText().toString();

        EditText confirmPassword = findViewById(R.id.register_confirm_password);
        message_confirmPassword = confirmPassword.getText().toString();


        String msg = "";
        if(message_username.equals("")){
            msg = "The username cannot be empty!";
        }
        if(message_email.equals("")){
            msg = "The email cannot be empty!";
        }
        if(message_phone.equals("")){
            msg = "The phone number cannot be empty!";
        }
        if(message_password.equals("")){
            msg = "The password cannot be empty!";
        }
        if(!message_password.equals(message_confirmPassword)){
            msg = "The two passwords you typed do not match!";
        }
        registerUser(message_username, message_email, message_phone, message_password);
    }


    private void notification(String msg){
        new AlertDialog.Builder(this)
                .setTitle("NOTIFICATION")
                .setMessage(msg)
                .setPositiveButton("Yes", null)
                .show();
    }

    private void registerUser(String username, String email, String phone, String password) {
        // Hash the password using salt
        byte salt[] = AuthenticationAPI.generateSalt();
        byte hashedPassword[] = AuthenticationAPI.hashPass(password, salt);

        String create_user = "<message><id>0</id><method>create</method><user><name>" + username
                + "</name><email>" + email
                + "</email><phone_num>" + phone
                + "</phone_num><hash>" + String.valueOf(hashedPassword)
                + "</hash><salt>" + String.valueOf(salt)
                + "</salt></user><original_info></original_info></message>";

        //@TODO register user with server and return success/fail
//        HTTPsender send_get = new HTTPsender();
//        String message = send_get.send_and_receive(create_user,getApplicationContext());

        String myUrl = "http://vcm-2932.vm.duke.edu:9999/tyrata-team/XmlAction?xml_data=" + create_user;
        Log.i("myUrl",myUrl);

        //send(myUrl, getApplicationContext());
        //Log.i("send","success");

        send_message(myUrl);
        Log.i("send_new_method","success");

    }



    private boolean checkInternetConenction() {
        // get Connectivity Manager object to check connection
        ConnectivityManager connec
                =(ConnectivityManager)getSystemService(getBaseContext().CONNECTIVITY_SERVICE);

        // Check for network connections
        if ( connec.getNetworkInfo(0).getState() ==
                android.net.NetworkInfo.State.CONNECTED ||
                connec.getNetworkInfo(0).getState() ==
                        android.net.NetworkInfo.State.CONNECTING ||
                connec.getNetworkInfo(1).getState() ==
                        android.net.NetworkInfo.State.CONNECTING ||
                connec.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.CONNECTED ) {
            Toast.makeText(this, " Connected ", Toast.LENGTH_LONG).show();
            return true;
        }else if (
                connec.getNetworkInfo(0).getState() ==
                        android.net.NetworkInfo.State.DISCONNECTED ||
                        connec.getNetworkInfo(1).getState() ==
                                android.net.NetworkInfo.State.DISCONNECTED  ) {
            Toast.makeText(this, " Not Connected ", Toast.LENGTH_LONG).show();
            return false;
        }
        return false;
    }

    private void send_message(String urlStr) {
        final String url = urlStr;
        String resource;
        new Thread() {
            public void run() {
                InputStream in = null;
                Message msg = Message.obtain();
                msg.what = 1;
                try {
                    in = openHttpConnection(url);
                    String resource = new Scanner(in).useDelimiter("\\Z").next();
                    Log.i("test_new_method",resource);
                    Bundle b = new Bundle();
                    b.putString("get_message", resource);
                    msg.setData(b);
                    in.close();
                }catch (IOException e1) {
                    e1.printStackTrace();
                }
                messageHandler.sendMessage(msg);
            }
        }.start();
    }

    private InputStream openHttpConnection(String urlStr) {
        InputStream in = null;
        int resCode = -1;

        try {
            URL url = new URL(urlStr);
            URLConnection urlConn = url.openConnection();

            if (!(urlConn instanceof HttpURLConnection)) {
                Log.i("new_method","wrong");
                throw new IOException("URL is not an Http URL");
            }

            HttpURLConnection httpConn = (HttpURLConnection) urlConn;
            httpConn.setAllowUserInteraction(false);
            httpConn.setInstanceFollowRedirects(true);
            httpConn.setRequestMethod("GET");
            httpConn.connect();
            resCode = httpConn.getResponseCode();

            if (resCode == HttpURLConnection.HTTP_OK) {
                Log.i("new_method","get");
                in = httpConn.getInputStream();
            }
        }catch (MalformedURLException e) {
            e.printStackTrace();
        }catch (IOException e) {
            e.printStackTrace();
        }
        return in;
    }

    private Handler messageHandler = new Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            String message = msg.getData().getString("get_message");

            if(message.equals("<message><ack>0</ack></message>")) {
                Intent intent = new Intent(Register.this, edu.duke.ece651.tyrata.user.Log_in.class);
                Database.myDatabase = openOrCreateDatabase("TyrataData", MODE_PRIVATE, null);
                // For test, drop and create tables
                Database.dropAllTable();
                Database.createTable();
                Database.storeUserData(message_username, message_email, message_phone);
                Database.myDatabase.close();
                startActivity(intent);
            }
            else{
                ServerXmlParser parser = new ServerXmlParser();
                InputStream message_error = new ByteArrayInputStream(message.getBytes());
                try {
                    parser.parse_server(message_error, getApplicationContext());
                } catch (XmlPullParserException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
    };

}
