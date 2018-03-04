package com.example.myfirstapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private List<Button> tires;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tires = new ArrayList<Button>();
        tires.add((Button) findViewById(R.id.button_A0));
        tires.add((Button) findViewById(R.id.button_A1));
        tires.add((Button) findViewById(R.id.button_A2));
        tires.add((Button) findViewById(R.id.button_A3));
        for(int i = 0; i < tires.size(); i++) {
            tires.get(i).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openTireAct();
                }
            });
        }
    }

    public void openTireAct(){
        Intent intent = new Intent(this, TireInfo.class);
        startActivity(intent);
    }
}
