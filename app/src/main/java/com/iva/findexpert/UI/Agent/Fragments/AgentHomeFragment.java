package com.iva.findexpert.UI.Agent.Fragments;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
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
import com.iva.findexpert.UI.Common.Fragments.MessageSummaryFragment;
import com.iva.findexpert.UI.Common.Fragments.BaseFragment;
import com.iva.findexpert.UI.Common.Fragments.UserProfileFragment;
import com.iva.findexpert.UI.Helpers.HttpClientHelper;
import com.iva.findexpert.UI.Helpers.IHttpResponse;

import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class AgentHomeFragment extends BaseFragment {

    private BroadcastReceiver broadcastReceiver;
    private boolean doubleBackToExitPressedOnce = false;

    public AgentHomeFragment() {
        // Required empty public constructor
    }

    public static AgentHomeFragment newInstance() {
        AgentHomeFragment fragment = new AgentHomeFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_agent_home, container, false);
        setCurrentView(view);
        setButtons();
        setActionButtons();
        showMessageCount();
        showPendingCount();
        showFollowUpCount();

        setReciever();
        return view;
    }

    private void setButtons()
    {
        getCurrentView().findViewById(R.id.InsuranceButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openRequestList();
            }
        });
    }

    private void showMessageCount()
    {
        User user = new UserService(getActivity()).GetCurrentUser();
        if(user == null || !user.IsAuthenticated)
            return;

        String url = Constant.BASE_URL + Constant.CONTROLLER_MESSAGE + Constant.SERVICE_GET_MESSAGE_COUNT;
        Map<String, String> params = new HashMap<>();
        params.put("UserId", String.valueOf(user.Id));

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
            public void onError(int error) { }

            @Override
            public void onNoInternetConnection(){ hideProgress();}
        });
    }

    private void showPendingCount()
    {
        User user = new UserService(getActivity()).GetCurrentUser();
        if(user == null || !user.IsAuthenticated)
            return;

        String url = Constant.BASE_URL + Constant.CONTROLLER_SERVICE_REQUEST + Constant.SERVICE_GET_PENDING_COUNT;
        Map<String, String> params = new HashMap<>();
        params.put("AgentId", String.valueOf(user.Id));

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
                        TextView countView = ((TextView) getCurrentView().findViewById(R.id.pendingCount));
                        countView.setText(String.valueOf(count));
                        countView.setVisibility(View.VISIBLE);
                    }
                    else
                    {
                        getCurrentView().findViewById(R.id.pendingCount).setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onError(int error) { }

            @Override
            public void onNoInternetConnection(){ hideProgress();}
        });
    }

    private void showFollowUpCount()
    {
        User user = new UserService(getActivity()).GetCurrentUser();
        if(user == null || !user.IsAuthenticated)
            return;

        String url = Constant.BASE_URL + Constant.CONTROLLER_SERVICE_REQUEST + Constant.SERVICE_GET_FOLLOWUP_COUNT;
        Map<String, String> params = new HashMap<>();
        params.put("AgentId", String.valueOf(user.Id));

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
                        TextView countView = ((TextView) getCurrentView().findViewById(R.id.followUpCount));
                        countView.setText(String.valueOf(count));
                        countView.setVisibility(View.VISIBLE);
                    }
                    else
                    {
                        getCurrentView().findViewById(R.id.followUpCount).setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onError(int error) { }

            @Override
            public void onNoInternetConnection(){ hideProgress();}
        });
    }

    private void openRequestList()
    {
        openFragement(AgentRequestListFragment.newInstance(Constant.AgentRequestListFocus.ALL), false);
    }

    private void setActionButtons()
    {
        getCurrentView().findViewById(R.id.requestListButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                User user = new UserService(getActivity()).GetCurrentUser();
                if(user != null)
                {
                    openFragement(AgentRequestListFragment.newInstance(Constant.AgentRequestListFocus.ALL), false);
                }
            }
        });

        getCurrentView().findViewById(R.id.followUpRequestListButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                User user = new UserService(getActivity()).GetCurrentUser();
                if(user != null)
                {
                    openFragement(AgentRequestListFragment.newInstance(Constant.AgentRequestListFocus.FOLLOWUP), false);
                }
            }
        });

        getCurrentView().findViewById(R.id.pendingRequestListButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                User user = new UserService(getActivity()).GetCurrentUser();
                if(user != null)
                {
                    openFragement(AgentRequestListFragment.newInstance(Constant.AgentRequestListFocus.PENDING), false);
                }
            }
        });

        getCurrentView().findViewById(R.id.messageListButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                User user = new UserService(getActivity()).GetCurrentUser();
                if(user != null)
                {
                    openFragement(MessageSummaryFragment.newInstance(), false);
                }
            }
        });

        getCurrentView().findViewById(R.id.profileButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                User user = new UserService(getActivity()).GetCurrentUser();
                if(user != null)
                {
                    openFragement(UserProfileFragment.newInstance(), false);
                }
            }
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

    private void setReciever()
    {
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                showMessageCount();
                showPendingCount();
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
