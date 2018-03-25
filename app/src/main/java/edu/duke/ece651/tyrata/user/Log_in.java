package edu.duke.ece651.tyrata.user;

import android.app.AlertDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import edu.duke.ece651.tyrata.MainActivity;
import edu.duke.ece651.tyrata.R;
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

        Database.myDatabase = openOrCreateDatabase("TyrataData", MODE_PRIVATE, null);
        int user_ID = Database.getUserID(message_email);
        Database.myDatabase.close();
        if(user_ID != -1){
            Log.i("exist", String.valueOf(user_ID));
            intent.putExtra("USER_ID",user_ID);
            startActivity(intent);
        }
        else{
            Log.i("exist", "No");
            email_not_exist();
        }

    }
    public void switchto_register(View view) {
        Intent intent = new Intent(Log_in.this, edu.duke.ece651.tyrata.user.Register.class);

        startActivity(intent);
        // Do something in response to button
    }

    public void email_not_exist(){
        new AlertDialog.Builder(this)
                .setTitle("NOTIFICATION")
                .setMessage("The email doesn't exist. Please enter the right email or register.")
                .setPositiveButton("OK", null)
                .show();
    }
}
