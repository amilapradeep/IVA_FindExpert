package com.iva.findexpert.UI.Common.Fragments;


import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ViewFlipper;

import com.iva.findexpert.Common.Constant;
import com.iva.findexpert.DomainModel.User;
import com.iva.findexpert.R;
import com.iva.findexpert.Service.UserService;
import com.iva.findexpert.UI.Agent.Fragments.AgentHomeFragment;
import com.iva.findexpert.UI.Buyer.Fragments.BuyerHomeFragment;

/**
 * A simple {@link Fragment} subclass.
 */
public class GetStartedFragment extends BaseFragment {


    public GetStartedFragment() {
        // Required empty public constructor
    }

    public static GetStartedFragment newInstance()
    {
        return new GetStartedFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_get_started, container, false);
        setCurrentView(view);
        return view;
    }
}
