package edu.duke.ece651.tyrata.calibration;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import edu.duke.ece651.tyrata.MainActivity;
import edu.duke.ece651.tyrata.R;

public class Report_accident extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_accident);
    }


    public void sendReportToMain(View view) {

        Intent intent = new Intent(this, edu.duke.ece651.tyrata.MainActivity.class);
        EditText edit_report = (EditText) findViewById(R.id.report_editText);
        String message_report = edit_report.getText().toString();
        intent.putExtra("REPORT", message_report);

        startActivity(intent);

        // Do something in response to button
    }

}
