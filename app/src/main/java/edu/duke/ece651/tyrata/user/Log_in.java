package edu.duke.ece651.tyrata.user;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import org.xmlpull.v1.XmlPullParserException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

import edu.duke.ece651.tyrata.MainActivity;
import edu.duke.ece651.tyrata.R;
import edu.duke.ece651.tyrata.communication.HttpActivity;
import edu.duke.ece651.tyrata.communication.ServerXmlParser;
import edu.duke.ece651.tyrata.datamanagement.Database;

public class Log_in extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);
    }
    public void switchto_main(View view) {
        Intent intent = new Intent(Log_in.this, MainActivity.class);

        EditText input_email = (EditText) findViewById(R.id.input_email);
        String message_email = input_email.getText().toString();

        EditText inputPassword = findViewById(R.id.input_password);
        String messagePassword = inputPassword.getText().toString();

        if(!authenticateUser(message_email, messagePassword)) {
            // Authentication failed
            String msg = "Incorrect credentials. Please try again.";
            notification(msg);
        }
        else {
            Database.myDatabase = openOrCreateDatabase("TyrataData", MODE_PRIVATE, null);
            int user_ID = Database.getUserID(message_email);
            Database.myDatabase.close();
            if (user_ID != -1) {
                Log.i("exist", String.valueOf(user_ID));
                intent.putExtra("USER_ID", user_ID);
                startActivity(intent);
            } else {
                Log.i("exist", "No");
                String msg = "The email doesn't exist. Please enter the right email or register.";
                notification(msg);
            }
        }

    }
    public void switchto_register(View view) {
        Intent intent = new Intent(Log_in.this, edu.duke.ece651.tyrata.user.Register.class);

        startActivity(intent);
        // Do something in response to button
    }

    public void notification(String msg){
        new AlertDialog.Builder(this)
                .setTitle("NOTIFICATION")
                .setMessage(msg)
                .setPositiveButton("OK", null)
                .show();
    }

    private boolean authenticateUser(String email, String password) {
        //@TODO athenticate user with server
        String user = "<message><authentication><email>" + email + "</email></authentication></message>";
        String myUrl = "http://vcm-2932.vm.duke.edu:9999/hello/XMLAction?xml_data=" + user;
        HttpActivity httpActivity = new HttpActivity();
        SharedPreferences.Editor editor= getSharedPreferences("msg_from_server",MODE_PRIVATE).edit();
        editor.putString("hash","");
        editor.putString("salt","");
        editor.putString("msg","");
        editor.commit();
        httpActivity.startDownload(myUrl);

        SharedPreferences editor_get = getSharedPreferences("msg_from_server",MODE_PRIVATE);
        String message = "";
        do{
            message= editor_get.getString("msg","");
        }while (message == "");

        ServerXmlParser parser = new ServerXmlParser();
        InputStream msg = new ByteArrayInputStream(message.getBytes());
        try {
            parser.parse_server(msg, getApplicationContext());
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        String salt_get = "";
        String hash_get = "";
        do{
            salt_get = editor_get.getString("salt","");
            hash_get = editor_get.getString("hash","");
        }while (salt_get == "" || hash_get == "");


        // get user salt and hashed password from server
        byte salt[] = salt_get.getBytes(); // from server
        byte serverHash[] = hash_get.getBytes(); // from server

        // re-calculate hashed password from user input and salt
        byte hashedPassword[] = AuthenticationAPI.hashPass(password, salt);

        // confirm password
        return Arrays.equals(hashedPassword, serverHash);
    }
}
