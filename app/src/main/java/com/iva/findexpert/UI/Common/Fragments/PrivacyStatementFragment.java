package com.iva.findexpert.UI.Common.Fragments;


import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import com.iva.findexpert.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class PrivacyStatementFragment extends BaseFragment {


    public PrivacyStatementFragment() {
        // Required empty public constructor
    }

    public static PrivacyStatementFragment newInstance()
    {
        return new PrivacyStatementFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_privacy_statement, container, false);
        setCurrentView(view);
        setWebView();
        return view;
    }

    private void setWebView()
    {
        WebView webView = ((WebView) getCurrentView().findViewById(R.id.webview));
        if (Build.VERSION.SDK_INT >= 19) {
            webView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        }
        else {
            webView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }
        webView.loadUrl("file:///android_asset/PrivacyStatement.html");

    }

}
