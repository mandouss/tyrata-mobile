package edu.duke.ece651.tyrata.datamanagement;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

/**
 * Created by Yuei on 3/4/18.
 */

public class Database extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main)

    }
    SQLiteDatabase myDatabase = this.openOrCreateDatabase("Users", MODE_PRIVATE, null);

    public void storeUserData(String username, String password){
        myDatabase.execSQL("CREATE TABLE IF NOT EXISTS users (Name VARCHAR, Password VARCHAR)");
        myDatabase.execSQL("INSERT INTO users (Name, Password) VALUES (username, password)");
        Cursor c = myDatabase.rawQuery("SELECT * FROM users", null);
        int nameIndex = c.getColumnIndex("Name");
        int passwordIndex = c.getColumnIndex("Password");
        c.moveToFirst();
        while (c!=null){

            Log.i("name", c.getString(nameIndex));
            Log.i("password", c.getString(passwordIndex));
            c.moveToNext();
        }

    }
    public void storeTireData(String username, String carmodel, String tiremodel, String tireyear, String vin){
        myDatabase.execSQL("CREATE TABLE IF NOT EXISTS info (Name VARCHAR, Carmodel VARCHAR, Tiremodel VARCHAR, Tirehyear VARCHAR, VIN VARCHAR)");
        myDatabase.execSQL("INSERT INTO info (Name, Carmodel, Tiremodel, Tireyear, VIN) VALUES (username, carmodel, tiremodel, tireyear, vin)");
    }

    public String carModelLoaddata(String username){
        Cursor a = myDatabase.rawQuery("SELECT * FROM info WHERE Name ="+username, null);
        int carmodelIndex = a.getColumnIndex("Carmodel");
        a.moveToFirst();
        return a.getString(carmodelIndex);

    }
    public String tireModelLoaddata(String username){
        Cursor a = myDatabase.rawQuery("SELECT * FROM info WHERE Name ="+username, null);
        int tiremodelIndex = a.getColumnIndex("Tiremodel");
        a.moveToFirst();
        return a.getString(tiremodelIndex);
    }

    public String tireYearLoaddata(String username){
        Cursor a = myDatabase.rawQuery("SELECT * FROM info WHERE Name ="+username, null);
        int tireyearIndex = a.getColumnIndex("Tireyear");
        a.moveToFirst();
        return a.getString(tireyearIndex);
    }

    public String VINLoaddata(String username){
        Cursor a = myDatabase.rawQuery("SELECT * FROM info WHERE Name ="+username, null);
        int vinIndex = a.getColumnIndex("VIN");
        a.moveToFirst();
        return a.getString(vinIndex);
    }

}

