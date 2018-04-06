package edu.duke.ece651.tyrata.communication;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import edu.duke.ece651.tyrata.R;

public class AuthenticationActivity extends AppCompatActivity {

    TextView mEmailField;
    TextView mPasswordField;
    Button mSubmitBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authentication);

        mEmailField = findViewById(R.id.editText_s0_email);
        mPasswordField = findViewById(R.id.editText_s1_password);
        mSubmitBtn = findViewById(R.id.button_s2_submit);
    }

    public void submit(View view) {
        String email = mEmailField.getText().toString();
        String pw = mPasswordField.getText().toString();

        hashPass(pw);
    }

    private void hashPass(String pw) {
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("SHA-256");

            // Generate salt (secure random number generator)
            SecureRandom random = new SecureRandom();
            byte saltBytes[] = new byte[8];
            random.nextBytes(saltBytes);

            // Combine password and salt
            byte pwBytes[] = pw.getBytes();
            byte rawBytes[] = new byte[pwBytes.length + saltBytes.length];
            for (int i=0; i<rawBytes.length; i++) {
                rawBytes[i] = i < pwBytes.length ? pwBytes[i] : saltBytes[i - pwBytes.length];
            }

            // Hash password + salt
            md.update(rawBytes);
            byte hashedBytes[] = md.digest();


            StringBuffer sb = new StringBuffer();
            for (int i=0; i<hashedBytes.length; i++) {
                sb.append(Integer.toString((hashedBytes[i] & 0xff) + 0x100, 16).substring(1));
            }

            Log.d("TEEEEEST", "Hex format: " + sb.toString());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

    }
}


