package edu.duke.ece651.tyrata;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import edu.duke.ece651.tyrata.user.Log_in;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent intent = getIntent();
        String message_report = intent.getStringExtra("REPORT");
        TextView textView_report = findViewById(R.id.main_notification);
        textView_report.setText(message_report);
        if(message_report != ""){
            textView_report.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_view_navigation, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.n_item2:
                main_to_addcar();
                return true;
            case R.id.n_item3:
                main_to_report();
                return true;
            case R.id.n_item4:
                main_to_login();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }




    public void main_to_addcar() {
        Intent intent = new Intent(MainActivity.this, edu.duke.ece651.tyrata.calibration.Input_Vehicle_Info.class);

        startActivity(intent);
        // Do something in response to button
    }
    public void main_to_report() {
        Intent intent = new Intent(MainActivity.this, edu.duke.ece651.tyrata.vehicle.Report_accident.class);

        startActivity(intent);
        // Do something in response to button
    }
    public void main_to_login() {
        Intent intent = new Intent(MainActivity.this, edu.duke.ece651.tyrata.user.Log_in.class);

        startActivity(intent);
        // Do something in response to button
    }
    public void main_to_vehicle_info(View view) {
        Intent intent = new Intent(MainActivity.this, edu.duke.ece651.tyrata.vehicle.Vehicle_Info.class);

        startActivity(intent);
        // Do something in response to button
    }

}
