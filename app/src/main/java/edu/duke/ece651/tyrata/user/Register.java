package edu.duke.ece651.tyrata.user;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import edu.duke.ece651.tyrata.MainActivity;
import edu.duke.ece651.tyrata.R;
import edu.duke.ece651.tyrata.datamanagement.Database;

public class Register extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
    }

    public void Register_to_main(View view) {
        Intent intent = new Intent(Register.this, edu.duke.ece651.tyrata.MainActivity.class);

        startActivity(intent);
        // Do something in response to button
        Database.myDatabase = openOrCreateDatabase("Users", MODE_PRIVATE, null);
        Database.storeUserData("abc", "pass");

    }
}
