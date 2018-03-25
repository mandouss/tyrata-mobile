package edu.duke.ece651.tyrata.user;

import android.app.AlertDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import edu.duke.ece651.tyrata.MainActivity;
import edu.duke.ece651.tyrata.R;
import edu.duke.ece651.tyrata.datamanagement.Database;

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


        try {
            // Do something in response to button
            Database.myDatabase = openOrCreateDatabase("TyrataData", MODE_PRIVATE, null);
            // For test, drop and create tables
            Database.dropAllTable();
            Database.createTable();
            boolean emailExist = Database.storeUserData(message_username, message_email, message_phone);
            Database.testUserTable();
            Database.myDatabase.close();
            if (!emailExist) {
                startActivity(intent);
            }
            else {
                notification();
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }
    private void notification(){
        new AlertDialog.Builder(this)
                .setTitle("NOTIFICATION")
                .setMessage("The email is already registered!")
                .setPositiveButton("Yes", null)
                .show();
    }
}
