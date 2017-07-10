package com.iva.findexpert.UI.Agent.Fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.android.volley.Request;
import com.google.gson.Gson;
import com.iva.findexpert.Common.Constant;
import com.iva.findexpert.DomainModel.QuotationRequest;
import com.iva.findexpert.DomainModel.User;
import com.iva.findexpert.R;
import com.iva.findexpert.Service.UserService;
import com.iva.findexpert.UI.Adapters.AgentRequestListAdapter;
import com.iva.findexpert.UI.Adapters.AgentRequestListAdapterWithAcceptedIcon;
import com.iva.findexpert.UI.Adapters.EndlessScrollListener;
import com.iva.findexpert.UI.Buyer.Fragments.BuyerHomeFragment;
import com.iva.findexpert.UI.Common.Fragments.BaseFragment;
import com.iva.findexpert.UI.Helpers.HttpClientHelper;
import com.iva.findexpert.UI.Helpers.IHttpResponse;
import com.iva.findexpert.UI.Helpers.NotificationsHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class AgentRequestListFragment extends BaseFragment {

   //private int curPage = 1;
    private int focus = Constant.AgentRequestListFocus.ALL;
    private List<QuotationRequest> globalList;
    private ArrayAdapter adapter;

    public AgentRequestListFragment() {
        // Required empty public constructor
    }

    public static AgentRequestListFragment newInstance(int focus)
    {
        AgentRequestListFragment fragment= new AgentRequestListFragment();
        Bundle args = new Bundle();
        args.putInt(Constant.SessionKeys.AGENT_LIST_FOCUS_KEY, focus);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            focus = getArguments().getInt(Constant.SessionKeys.AGENT_LIST_FOCUS_KEY);
            }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_agent_request_list, container, false);
        setCurrentView(view);
        SetScrollPaging();
        setTabs();
        loadRequests(1);
        NotificationsHelper.cancelNotification(getActivity(), Constant.NotificationType.REQUEST);
        NotificationsHelper.cancelNotification(getActivity(), Constant.NotificationType.ACCEPT);
        return view;
    }

    private void setTabs()
    {
        selectTabs();
        getCurrentView().findViewById(R.id.allButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                focus = Constant.AgentRequestListFocus.ALL;
                selectTabs();
                loadRequests(1);
            }
        });

        getCurrentView().findViewById(R.id.pendingButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                focus = Constant.AgentRequestListFocus.PENDING;
                selectTabs();
                loadRequests(1);
            }
        });

        getCurrentView().findViewById(R.id.followupButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                focus = Constant.AgentRequestListFocus.FOLLOWUP;
                selectTabs();
                loadRequests(1);
            }
        });
    }

    private void selectTabs()
    {
        switch (focus)
        {
            case Constant.AgentRequestListFocus.ALL:
                getCurrentView().findViewById(R.id.allButton).setSelected(true);
                getCurrentView().findViewById(R.id.pendingButton).setSelected(false);
                getCurrentView().findViewById(R.id.followupButton).setSelected(false);
                getCurrentView().findViewById(R.id.allContainer).setVisibility(View.VISIBLE);
                getCurrentView().findViewById(R.id.pendingContainer).setVisibility(View.GONE);
                getCurrentView().findViewById(R.id.followupContainer).setVisibility(View.GONE);
            break;
            case Constant.AgentRequestListFocus.PENDING:
                getCurrentView().findViewById(R.id.allButton).setSelected(false);
                getCurrentView().findViewById(R.id.pendingButton).setSelected(true);
                getCurrentView().findViewById(R.id.followupButton).setSelected(false);
                getCurrentView().findViewById(R.id.allContainer).setVisibility(View.GONE);
                getCurrentView().findViewById(R.id.pendingContainer).setVisibility(View.VISIBLE);
                getCurrentView().findViewById(R.id.followupContainer).setVisibility(View.GONE);
                break;
            case Constant.AgentRequestListFocus.FOLLOWUP:
                getCurrentView().findViewById(R.id.allButton).setSelected(false);
                getCurrentView().findViewById(R.id.pendingButton).setSelected(false);
                getCurrentView().findViewById(R.id.followupButton).setSelected(true);
                getCurrentView().findViewById(R.id.allContainer).setVisibility(View.GONE);
                getCurrentView().findViewById(R.id.pendingContainer).setVisibility(View.GONE);
                getCurrentView().findViewById(R.id.followupContainer).setVisibility(View.VISIBLE);
                break;
            default:
                getCurrentView().findViewById(R.id.allButton).setSelected(true);
                getCurrentView().findViewById(R.id.pendingButton).setSelected(false);
                getCurrentView().findViewById(R.id.followupButton).setSelected(false);
                getCurrentView().findViewById(R.id.allButton).setSelected(true);
                getCurrentView().findViewById(R.id.pendingButton).setSelected(false);
                getCurrentView().findViewById(R.id.followupButton).setSelected(false);
                break;
        }
    }

    private void loadRequests(final int curPage)
    {
        getCurrentView().findViewById(R.id.empty).setVisibility(View.GONE);
        User user = new UserService(getActivity()).GetCurrentUser();
        Map<String, String> params = new HashMap<>();

        String url;
        switch (focus)
        {
            case Constant.AgentRequestListFocus.ALL:
                url = Constant.BASE_URL + Constant.CONTROLLER_SERVICE_REQUEST + Constant.SERVICE_SR_AGENT;
                params.put("Page", String.valueOf(curPage));
                break;
            case Constant.AgentRequestListFocus.PENDING:
                url = Constant.BASE_URL + Constant.CONTROLLER_SERVICE_REQUEST + Constant.SERVICE_SR_AGENT_PENDING;
                break;
            case Constant.AgentRequestListFocus.FOLLOWUP:
                url = Constant.BASE_URL + Constant.CONTROLLER_SERVICE_REQUEST + Constant.SERVICE_SR_AGENT_FOLLOWUP;
                break;
            default:
                url = Constant.BASE_URL + Constant.CONTROLLER_SERVICE_REQUEST + Constant.SERVICE_SR_AGENT;
                break;
        }

        params.put("AgentId", String.valueOf(user.Id));

        Map<String,String> headerParams = new HashMap<>();
        headerParams.put("Authorization", "bearer " + user.Token);
        //if(focus == Constant.AgentRequestListFocus.ALL)
        showProgress(getActivity());
        new HttpClientHelper().getResponseStringURLEncoded(getActivity(), url, Request.Method.GET, params, headerParams, new IHttpResponse() {
            @Override
            public void onSuccess(String result) {
                if(result != null)
                {
                    Gson gson = new Gson();
                    //Type collectionType = new TypeToken<List<QuotationRequest>>() {}.getType();
                    List<QuotationRequest> requestList = Arrays.asList(gson.fromJson(result, QuotationRequest[].class ));

                    if(curPage == 1){
                        if(requestList != null && requestList.size() > 0)
                            getCurrentView().findViewById(R.id.empty).setVisibility(View.GONE);
                        else
                            getCurrentView().findViewById(R.id.empty).setVisibility(View.VISIBLE);
                    }

                    switch (focus)
                    {
                        case Constant.AgentRequestListFocus.ALL:
                            if(curPage == 1) {
                                globalList = new ArrayList<>();
                                globalList.addAll(requestList);
                                adapter = new AgentRequestListAdapterWithAcceptedIcon(getActivity(), getActivity(), globalList);
                                ((ListView)getCurrentView().findViewById(R.id.allList)).setAdapter(adapter);
                            }
                            else
                            {
                                globalList.addAll(requestList);
                                adapter.notifyDataSetChanged();
                            }
                            break;
                        case Constant.AgentRequestListFocus.PENDING:
                            adapter = new AgentRequestListAdapter(getActivity(), getActivity(), requestList);
                            ((ListView)getCurrentView().findViewById(R.id.pendingList)).setAdapter(adapter);
                            break;
                        case Constant.AgentRequestListFocus.FOLLOWUP:
                            adapter = new AgentRequestListAdapter(getActivity(), getActivity(), requestList);
                            ((ListView)getCurrentView().findViewById(R.id.followupList)).setAdapter(adapter);
                            break;
                        default:
                            //((ListView)getCurrentView().findViewById(R.id.allList)).setAdapter(adapter);
                            break;
                    }
                }
                hideProgress();
            }

            @Override
            public void onError(int error) {
                hideProgress();
                showAlert(getActivity(), "Error communicating the server!");
            }

            @Override
            public void onNoInternetConnection(){ hideProgress();}
        });
    }

    private void SetScrollPaging()
    {
        ((ListView) getCurrentView().findViewById(R.id.allList)).setOnScrollListener(new EndlessScrollListener() {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                if(page > 1) {
                    loadRequests(page);
                }
            }
        });
    }

    @Override
    public boolean onBackPressed() {
        User user = new UserService(getActivity()).GetCurrentUser();
        if(user.Type == Constant.UserType.SELLER)
            openFragement(AgentHomeFragment.newInstance(), false);
        else
            openFragement(BuyerHomeFragment.newInstance(), false);

        return false;
    }

}
