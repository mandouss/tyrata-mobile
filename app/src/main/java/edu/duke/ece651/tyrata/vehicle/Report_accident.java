package edu.duke.ece651.tyrata.vehicle;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import edu.duke.ece651.tyrata.MainActivity;
import edu.duke.ece651.tyrata.R;

public class Report_accident extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_accident);
    }
    public void report_to_main(View view) {
        Intent intent = new Intent(Report_accident.this, edu.duke.ece651.tyrata.MainActivity.class);

        startActivity(intent);
        // Do something in response to button
    }
}
