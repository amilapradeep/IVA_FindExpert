package com.iva.findexpert.UI.Common.Fragments;


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
import com.iva.findexpert.DomainModel.User;
import com.iva.findexpert.R;
import com.iva.findexpert.Service.UserService;
import com.iva.findexpert.UI.Adapters.EndlessScrollListener;
import com.iva.findexpert.UI.Adapters.MessageThreadListAdapter;
import com.iva.findexpert.UI.Agent.Fragments.AgentHomeFragment;
import com.iva.findexpert.UI.Buyer.Fragments.BuyerHomeFragment;
import com.iva.findexpert.UI.Helpers.HttpClientHelper;
import com.iva.findexpert.UI.Helpers.IHttpResponse;
import com.iva.findexpert.UI.Helpers.NotificationsHelper;
import com.iva.findexpert.ViewModel.MessageThread;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class MessageSummaryFragment extends BaseFragment {

    private boolean isBuyer = true;
    private ArrayAdapter adapter;
    private List<MessageThread> globalList;

    public MessageSummaryFragment() {
        // Required empty public constructor
    }

    public static MessageSummaryFragment newInstance()
    {
        return new MessageSummaryFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_message_summary, container, false);
        setCurrentView(view);
        SetScrollPaging();
        getMessageThreads(1);
        NotificationsHelper.cancelNotification(getActivity(), Constant.NotificationType.MESSAGE);
        return  view;
    }

    private void getMessageThreads(final int page)
    {
        User user = new UserService(getActivity()).GetCurrentUser();
        String url = Constant.BASE_URL + Constant.CONTROLLER_MESSAGE + Constant.SERVICE_GET_BUYER_THREADS;
        if(user.Type == Constant.UserType.SELLER) {
            isBuyer = false;
            url = Constant.BASE_URL + Constant.CONTROLLER_MESSAGE + Constant.SERVICE_GET_AGENT_THREADS;
        }
        Map<String, String> params = new HashMap<>();
        params.put("UserId", String.valueOf(user.Id));
        params.put("Page", String.valueOf(page));

        Map<String,String> headerParams = new HashMap<>();
        headerParams.put("Authorization", "bearer " + new UserService(getActivity()).GetCurrentUser().Token);

        showProgress(getActivity());
        new HttpClientHelper().getResponseStringURLEncoded(getActivity(), url, Request.Method.GET, params, headerParams, new IHttpResponse() {
            @Override
            public void onSuccess(String result) {
                if(result != null)
                {
                    Gson gson = new Gson();
                    List<MessageThread> threads = Arrays.asList(gson.fromJson(result, MessageThread[].class ));

                    if(page == 1)
                    {
                        if(threads.size() > 0)
                            getCurrentView().findViewById(R.id.empty).setVisibility(View.GONE);
                        else
                            getCurrentView().findViewById(R.id.empty).setVisibility(View.VISIBLE);

                        globalList = new ArrayList<>();
                        globalList.addAll(threads);
                        adapter = new MessageThreadListAdapter(getActivity(), getActivity(), globalList, isBuyer);
                        ((ListView)getCurrentView().findViewById(R.id.threadList)).setAdapter(adapter);
                    }
                    else
                    {
                        globalList.addAll(threads);
                        adapter.notifyDataSetChanged();
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
        ((ListView) getCurrentView().findViewById(R.id.threadList)).setOnScrollListener(new EndlessScrollListener() {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                if(page > 1)
                    getMessageThreads(page);
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
