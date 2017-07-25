package com.sahakarmart.Activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.sahakarmart.R;

public class Web extends AppCompatActivity {


    private WebView web;
    private String WebUrl,site,test,contact,order;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);

        toolbar = (Toolbar)findViewById(R.id.WebToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        WebUrl = getIntent().getStringExtra("Web");


        site ="http://www.sahakarmart.com/";
        test="file:///android_asset/prescription.html";
        contact="file:///android_asset/about.html";
        order= "file:///android_asset/order.html";


        web =(WebView)findViewById(R.id.webView);
        WebSettings webSettings = web.getSettings();
        webSettings.setJavaScriptEnabled(true);
        web.getSettings().setLoadsImagesAutomatically(true);
        web.getSettings().setSupportZoom(true);
        web.getSettings().setBuiltInZoomControls(true);
        web.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
        web.setScrollbarFadingEnabled(true);
        web.getSettings().setPluginState(WebSettings.PluginState.ON);
        web.getSettings().setAllowFileAccess(true);
        web.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        web.getSettings().getDomStorageEnabled();



            if(WebUrl.equals("web"))
            {

                web.loadUrl(site);
            }

            if(WebUrl.equals("test"))
            {
                web.loadUrl(test);
            }

            if(WebUrl.equals("order"))
            {
                web.loadUrl(order);
            }

            if (WebUrl.equals("contact"))
            {
                web.loadUrl(contact);
            }


        web.setWebViewClient(new MyBrowser());
    }


    public class MyBrowser extends WebViewClient
    {

        public boolean shouldOverrideUrlLoading(WebView web, String url)
        {

            web.loadUrl(url);

            return true;

        }


    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            onBackPressed(); // close this activity and return to preview activity (if there is any)
        }

        return super.onOptionsItemSelected(item);
    }
}
