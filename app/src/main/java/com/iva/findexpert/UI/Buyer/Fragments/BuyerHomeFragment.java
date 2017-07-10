package com.iva.findexpert.UI.Buyer.Fragments;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.Request;
import com.google.gson.Gson;
import com.iva.findexpert.Common.Constant;
import com.iva.findexpert.DomainModel.User;
import com.iva.findexpert.R;
import com.iva.findexpert.Service.UserService;
import com.iva.findexpert.UI.Common.Fragments.AroundMeFragment;
import com.iva.findexpert.UI.Common.Fragments.BaseFragment;
import com.iva.findexpert.UI.Common.Fragments.MessageSummaryFragment;
import com.iva.findexpert.UI.Common.Fragments.UserProfileFragment;
import com.iva.findexpert.UI.Helpers.HttpClientHelper;
import com.iva.findexpert.UI.Helpers.IHttpResponse;
import com.iva.findexpert.Utility.Session;

import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class BuyerHomeFragment extends BaseFragment {

    private User user;
    private BroadcastReceiver broadcastReceiver;
    private boolean doubleBackToExitPressedOnce = false;

    public BuyerHomeFragment() {
        // Required empty public constructor
    }

    public static BuyerHomeFragment newInstance() {
        BuyerHomeFragment fragment = new BuyerHomeFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_buyer_home, container, false);
        setCurrentView(view);
        user = new UserService(getActivity()).GetCurrentUser();
        setButtons();
        setLastSelectedService();
        setActionButtons();
        showMessageCount();
        showFollowUpCount();
        setReceiver();
        return view;
    }

    private void setButtons()
    {
        getCurrentView().findViewById(R.id.InsuranceButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Session.putInt(getActivity(), Constant.SessionKeys.LAST_SELECTED_SERVICE, 1);
                openRequestCreate();
            }
        });

        getCurrentView().findViewById(R.id.cleaningServiceButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Session.putInt(getActivity(), Constant.SessionKeys.LAST_SELECTED_SERVICE, 2);
                setLastSelectedService();
            }
        });

        getCurrentView().findViewById(R.id.tellMeMoreButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                TellMeMoreFragment fragment = TellMeMoreFragment.newInstance();
                openFragement(fragment, true);
            }
        });

        /*getCurrentView().findViewById(R.id.getQuoteButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openRequestCreate();
            }
        });*/
    }

    private void openRequestCreate()
    {
        openFragement(RequestCreateFragment.newInstance(Constant.ParentUIName.FROM_HOME), false);
    }

    private void setActionButtons()
    {
        getCurrentView().findViewById(R.id.requestListButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(user != null && user.IsAuthenticated)
                {
                    openFragement(RequestListFragment.newInstance(user.Id), false);
                }
                else
                    showNotSignedInMessage();
            }
        });

        getCurrentView().findViewById(R.id.aroundMeButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(user != null && user.IsAuthenticated)
                {
                    openFragement(AroundMeFragment.newInstance(), false);
                }
                else
                    showNotSignedInMessage();
            }
        });

        getCurrentView().findViewById(R.id.messageListButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(user != null && user.IsAuthenticated)
                {
                    openFragement(MessageSummaryFragment.newInstance(), false);
                }
                else
                    showNotSignedInMessage();
            }
        });

        getCurrentView().findViewById(R.id.profileButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(user != null && user.IsAuthenticated)
                {
                    openFragement(UserProfileFragment.newInstance(), false);
                }
                else
                    showNotSignedInMessage();
            }
        });
    }

    private void showNotSignedInMessage()
    {
        showAlert(getActivity(), "User not logged in. Please register or login to get this service.");
    }

    private void showMessageCount()
    {
        if(user == null || !user.IsAuthenticated)
            return;

        String url = Constant.BASE_URL + Constant.CONTROLLER_MESSAGE + Constant.SERVICE_GET_MESSAGE_COUNT;
        Map<String, String> params = new HashMap<>();
        params.put("UserId", String.valueOf(user.Id));

        Map<String,String> headerParams = new HashMap<>();
        headerParams.put("Authorization", "bearer " + new UserService(getActivity()).GetCurrentUser().Token);

        new HttpClientHelper().getResponseStringURLEncoded(getActivity(), url, Request.Method.GET, params, headerParams, new IHttpResponse() {
            @Override
            public void onSuccess(String result) {
                if(result != null)
                {
                    Gson gson = new Gson();
                    int count = gson.fromJson(result, Integer.class );
                    if(count > 0)
                    {
                        TextView countView = ((TextView) getCurrentView().findViewById(R.id.msgCount));
                        countView.setText(String.valueOf(count));
                        countView.setVisibility(View.VISIBLE);
                    }
                    else
                    {
                        getCurrentView().findViewById(R.id.msgCount).setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onError(int error) {}

            @Override
            public void onNoInternetConnection(){ hideProgress();}
        });
    }

    private void showFollowUpCount()
    {
        User user = new UserService(getActivity()).GetCurrentUser();
        if(user == null || !user.IsAuthenticated)
            return;

        String url = Constant.BASE_URL + Constant.CONTROLLER_SERVICE_REQUEST + Constant.SERVICE_GET_BUYER_FOLLOWUP_COUNT;
        Map<String, String> params = new HashMap<>();
        params.put("BuyerId", String.valueOf(user.Id));

        Map<String,String> headerParams = new HashMap<>();
        headerParams.put("Authorization", "bearer " + user.Token);

        new HttpClientHelper().getResponseStringURLEncoded(getActivity(), url, Request.Method.GET, params, headerParams, new IHttpResponse() {
            @Override
            public void onSuccess(String result) {
                if(result != null)
                {
                    Gson gson = new Gson();
                    int count = gson.fromJson(result, Integer.class );
                    if(count > 0)
                    {
                        TextView countView = ((TextView) getCurrentView().findViewById(R.id.followupCount));
                        countView.setText(String.valueOf(count));
                        countView.setVisibility(View.VISIBLE);
                    }
                    else
                    {
                        getCurrentView().findViewById(R.id.followupCount).setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onError(int error) { }

            @Override
            public void onNoInternetConnection(){ hideProgress();}
        });
    }

    @Override
    public boolean onBackPressed() {
        if(getFragmentManager().getBackStackEntryCount() == 0)
        {
            if(!doubleBackToExitPressedOnce)
            {
                doubleBackToExitPressedOnce = true;
                showAlert(getActivity(), "Press BACK button again to exit the application");
                return false;
            }
            else
            {
                getActivity().finishAffinity();
            }
        }
        return false;
    }

    private void setLastSelectedService()
    {
        int selected = Session.getInt(getActivity(), Constant.SessionKeys.LAST_SELECTED_SERVICE);
        if(selected == 2)
        {
            ((TextView)getCurrentView().findViewById(R.id.title)).setText("Cleaning Services");
            ((TextView)getCurrentView().findViewById(R.id.titleDescription)).setText("Coming soon!");
            getCurrentView().findViewById(R.id.getQuoteButton).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Snackbar.make(getView(), "Coming soon!", Snackbar.LENGTH_LONG).show();
                }
            });
            getCurrentView().findViewById(R.id.tellMeMoreButton).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Snackbar.make(getView(), "Coming soon!", Snackbar.LENGTH_LONG).show();
                }
            });
        }
        else{
            ((TextView)getCurrentView().findViewById(R.id.title)).setText("Car Insurance");
            ((TextView)getCurrentView().findViewById(R.id.titleDescription)).setText("An easier way to compare insurance in place.");
            getCurrentView().findViewById(R.id.getQuoteButton).setOnClickListener(new View.OnClickListener() {
            @Override
                public void onClick(View v) {
                    openRequestCreate();
                }
            });
        }
    }

    private void setReceiver()
    {
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                showMessageCount();
                showFollowUpCount();
            }
        };

    }

    @Override
    public void onStart() {
        super.onStart();
        getActivity().registerReceiver((broadcastReceiver),
                new IntentFilter(Constant.Notification.REFRESH_MESSAGE_COUNT)
        );
    }

    @Override
    public void onStop() {
        getActivity().unregisterReceiver(broadcastReceiver);
        super.onStop();
    }

}
