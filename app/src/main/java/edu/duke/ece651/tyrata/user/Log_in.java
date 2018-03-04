package edu.duke.ece651.tyrata.user;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import edu.duke.ece651.tyrata.MainActivity;
import edu.duke.ece651.tyrata.R;

public class Log_in extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);
    }
    public void switchto_main(View view) {
        Intent intent = new Intent(Log_in.this, MainActivity.class);

        startActivity(intent);
        // Do something in response to button
    }
    public void switchto_register(View view) {
        Intent intent = new Intent(Log_in.this, edu.duke.ece651.tyrata.user.Register.class);

        startActivity(intent);
        // Do something in response to button
    }
}
