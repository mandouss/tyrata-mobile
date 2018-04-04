package edu.duke.ece651.tyrata.communication;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import edu.duke.ece651.tyrata.R;

public class AuthenticationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authentication);

        WebView myWebView = findViewById(R.id.webview_s0);
        myWebView.setWebViewClient(new myWebViewClient());
        myWebView.loadUrl("http://www.google.com");
    }
}

class myWebViewClient extends WebViewClient {
    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        view.loadUrl(url);
        return true;
    }
}
