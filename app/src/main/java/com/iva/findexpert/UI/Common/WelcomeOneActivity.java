package com.iva.findexpert.UI.Common;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.ProgressBar;

import com.iva.findexpert.Common.Constant;
import com.iva.findexpert.R;
import com.iva.findexpert.UI.Buyer.BuyerHomeActivity;
import com.iva.findexpert.Utility.Network;
import com.iva.findexpert.Utility.Session;

public class WelcomeOneActivity extends BaseActivity {

    CustomProgress progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome_one);
        this.setToolbar();
        this.setButtons();
        setStatusbarColor();
    }

    private void setButtons()
    {
        findViewById(R.id.NextButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(WelcomeOneActivity.this, WelcomeTwoActivity.class);
                startActivity(intent);
            }
        });

        findViewById(R.id.skipButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent page = new Intent(WelcomeOneActivity.this, BuyerHomeActivity.class);
                startActivity(page);
            }
        });
    }

    private void setWebView()
    {
        WebView webView = ((WebView) findViewById(R.id.webview));
        webView.loadUrl("file:///android_asset/background.html");
        if (Build.VERSION.SDK_INT >= 19) {
            webView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        }
        else {
            webView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }

        if(Network.IsConnectedToInternet(this))
        {
            String url = Constant.BASE_URL + "/home/WelcomeOne";
            WebSettings webSettings = webView.getSettings();
            webSettings.setJavaScriptEnabled(true);
            webView.setWebViewClient(new WebViewClient(){
                @Override
                public void onPageStarted(WebView view, String url, Bitmap favicon) {
                    super.onPageStarted(view, url, favicon);
                    progress = new CustomProgress(WelcomeOneActivity.this);
                    progress.show();
                }
                @Override
                public void onPageFinished(WebView view, String url) {
                    try{
                        if(progress != null)
                            progress.dismiss();

                    }catch(Exception exception){
                        exception.printStackTrace();
                        if(progress != null)
                            progress.dismiss();
                    }
                }
            });
            webView.loadUrl(url);
        }
    }

    private void setStatusbarColor()
    {
        Window window = this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimaryDarkWelcomeOne));
    }
}
