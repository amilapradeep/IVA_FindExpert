package com.iva.findexpert.UI.Common;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;

import com.iva.findexpert.Common.Constant;
import com.iva.findexpert.R;
import com.iva.findexpert.UI.Buyer.BuyerHomeActivity;
import com.iva.findexpert.UI.Buyer.ValidateCodeActivity;
import com.iva.findexpert.Utility.Network;

public class WelcomeTwoActivity extends BaseActivity {

    CustomProgress progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome_two);
        setToolbar();
        setButtons();
        //setWebView();
    }

    private void setButtons()
    {
        ((ImageButton)findViewById(R.id.LoginButton)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent page = new Intent(WelcomeTwoActivity.this, LoginActivity.class);
                startActivity(page);
            }
        });

        ((ImageButton) findViewById(R.id.StartButton)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent page = new Intent(WelcomeTwoActivity.this, BuyerHomeActivity.class);
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
            String url = Constant.BASE_URL + "/home/WelcomeTwo";
            WebSettings webSettings = webView.getSettings();
            webSettings.setJavaScriptEnabled(true);
            webView.setWebViewClient(new WebViewClient(){
                @Override
                public void onPageStarted(WebView view, String url, Bitmap favicon) {
                    super.onPageStarted(view, url, favicon);
                    progress = new CustomProgress(WelcomeTwoActivity.this);
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
}
