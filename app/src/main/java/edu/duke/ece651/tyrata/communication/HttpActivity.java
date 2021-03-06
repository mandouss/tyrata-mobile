package edu.duke.ece651.tyrata.communication;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import edu.duke.ece651.tyrata.R;

import static java.lang.Thread.sleep;

public class HttpActivity extends FragmentActivity implements DownloadCallback {

    // Reference to the TextView showing fetched data, so we can clear it with a button
    // as necessary.
    private TextView mDataText;

    // Keep a reference to the NetworkFragment which owns the AsyncTask object
    // that is used to execute network ops.
    private NetworkFragment mNetworkFragment;

    // Boolean telling us whether a download is in progress, so we don't trigger overlapping
    // downloads with consecutive button clicks.
    private boolean mDownloading = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_http);
        mDataText = (TextView) findViewById(R.id.data_text);
    }


    public void startDownload(String myUrl) {

        mNetworkFragment = NetworkFragment.getInstance(getSupportFragmentManager(), myUrl,getApplicationContext());
        if (!mDownloading && mNetworkFragment != null) {
            // Execute the async download.
            mNetworkFragment.startDownload();
            mDownloading = true;
        }
    }

    @Override
    public void updateFromDownload(String result) {
        if (result != null) {
            mDataText.setText(result);

        } else {
            mDataText.setText(getString(R.string.connection_error));
        }
    }

    @Override
    public NetworkInfo getActiveNetworkInfo() {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo;
    }

    @Override
    public void finishDownloading() {
        mDownloading = false;
        if (mNetworkFragment != null) {
            mNetworkFragment.cancelDownload();
        }
    }

    @Override
    public void onProgressUpdate(int progressCode, int percentComplete) {
        switch(progressCode) {
            // You can add UI behavior for progress updates here.
            case Progress.ERROR:
                break;
            case Progress.CONNECT_SUCCESS:
                break;
            case Progress.GET_INPUT_STREAM_SUCCESS:
                break;
            case Progress.PROCESS_INPUT_STREAM_IN_PROGRESS:
                mDataText.setText("" + percentComplete + "%");
                break;
            case Progress.PROCESS_INPUT_STREAM_SUCCESS:
                break;
        }
    }

    public void startDownload(View view) throws InterruptedException {
        HTTPsender httpSender = new HTTPsender();
        String myUrl = httpSender.send_to_cloud(getApplicationContext());
        do {
            //String myUrl = "http://vcm-2932.vm.duke.edu:9999/hello/XMLAction?xml_data=12345";
            if (myUrl != null) {
                startDownload(myUrl);
            }
            else{
                Toast.makeText(getApplicationContext(), "no update needs to do", Toast.LENGTH_SHORT).show();
            }
            myUrl = httpSender.send_to_cloud(getApplicationContext());
            sleep(1000);
        }while(myUrl != null);
    }

    public void finishDownloading(View view) {
        finishDownloading();
        mDataText.setText("");
    }
}