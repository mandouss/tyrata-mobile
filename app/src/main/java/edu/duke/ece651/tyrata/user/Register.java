package edu.duke.ece651.tyrata.user;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import org.xmlpull.v1.XmlPullParserException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import edu.duke.ece651.tyrata.R;
import edu.duke.ece651.tyrata.communication.HTTPsender;
import edu.duke.ece651.tyrata.communication.ServerXmlParser;
import edu.duke.ece651.tyrata.datamanagement.Database;
import edu.duke.ece651.tyrata.communication.HttpActivity;

public class Register extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
    }
    public void Register_to_login(View view) {
        Intent intent = new Intent(Register.this, edu.duke.ece651.tyrata.user.Log_in.class);

        EditText username = (EditText) findViewById(R.id.register_edit_username);
        String message_username = username.getText().toString();

        EditText email = (EditText) findViewById(R.id.register_edit_email);
        String message_email = email.getText().toString();

        EditText phone = (EditText) findViewById(R.id.register_edit_phonenumber);
        String message_phone = phone.getText().toString();

        EditText password = findViewById(R.id.register_edit_password);
        String message_password = password.getText().toString();

        EditText confirmPassword = findViewById(R.id.register_confirm_password);
        String message_confirmPassword = confirmPassword.getText().toString();


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

        try {
            if(msg.equals("")){
                if(!registerUser(message_username, message_email, message_phone, message_password)) {
                    // @TODO register user with server failed
                    msg = "Failed to register.";
                    notification(msg);
                }
                else {
                    Database.myDatabase = openOrCreateDatabase("TyrataData", MODE_PRIVATE, null);
                    // For test, drop and create tables
                    Database.dropAllTable();
                    Database.createTable();
                    boolean emailExist = Database.storeUserData(message_username, message_email, message_phone);
                    Database.myDatabase.close();
                    if (emailExist) {
                        msg = "The email is already registered!";
                        notification(msg);
                    } else {
                        startActivity(intent);
                    }
                }
            } else{
                notification(msg);
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    private void notification(String msg){
        new AlertDialog.Builder(this)
                .setTitle("NOTIFICATION")
                .setMessage(msg)
                .setPositiveButton("Yes", null)
                .show();
    }

    private boolean registerUser(String username, String email, String phone, String password) {
        // Hash the password using salt
        byte salt[] = AuthenticationAPI.generateSalt();
        byte hashedPassword[] = AuthenticationAPI.hashPass(password, salt);

        String create_user = "<message><id>0</id><method>create</method><user><username>" + username
                + "</username><email>" + email
                + "</email><phone>" + phone
                + "</phone><hash>" + String.valueOf(hashedPassword)
                + "</hash><salt>" + String.valueOf(salt)
                + "</salt></user><original_info></original_info></message>";

        //@TODO register user with server and return success/fail
        HTTPsender send_get = new HTTPsender();
        String message = send_get.send_and_receive(create_user,getApplicationContext());
        if(message.equals("<message><ack>0</ack></message>")) {
            return true;
        }
        else{
            ServerXmlParser parser = new ServerXmlParser();
            InputStream msg = new ByteArrayInputStream(message.getBytes());
            try {
                parser.parse_server(msg, getApplicationContext());
            } catch (XmlPullParserException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return false;
        }
    }

}
