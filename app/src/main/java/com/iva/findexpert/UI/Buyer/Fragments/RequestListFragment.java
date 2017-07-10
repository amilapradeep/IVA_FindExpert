package com.iva.findexpert.UI.Buyer.Fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.ListPopupWindow;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.google.gson.Gson;
import com.iva.findexpert.Common.Constant;
import com.iva.findexpert.Common.Enum;
import com.iva.findexpert.DomainModel.QuotationRequest;
import com.iva.findexpert.DomainModel.User;
import com.iva.findexpert.R;
import com.iva.findexpert.Service.UserService;
import com.iva.findexpert.UI.Adapters.BuyerRequestListAdapter;
import com.iva.findexpert.UI.Adapters.EndlessScrollListener;
import com.iva.findexpert.UI.Adapters.MenuListAdapter;
import com.iva.findexpert.UI.Agent.Fragments.AgentHomeFragment;
import com.iva.findexpert.UI.Common.Fragments.BaseFragment;
import com.iva.findexpert.UI.Helpers.HttpClientHelper;
import com.iva.findexpert.UI.Helpers.IHttpResponse;
import com.iva.findexpert.Utility.Common;
import com.iva.findexpert.Utility.Session;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class RequestListFragment extends BaseFragment {

    private long userId;
    private int page = 1;
    private int status = Constant.RequestStatus.ALL;
    private List<QuotationRequest> globalList;
    private ArrayAdapter adapter;

    public RequestListFragment() {
        // Required empty public constructor
    }

    public static RequestListFragment newInstance(long userId) {
        RequestListFragment fragment = new RequestListFragment();
        Bundle args = new Bundle();
        args.putLong(Constant.SessionKeys.USER_ID, userId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            userId = getArguments().getLong(Constant.SessionKeys.USER_ID);
        }
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_request_list, container, false);
        setCurrentView(view);
        SetScrollPaging();

        int key = Session.getInt(getActivity(), Constant.SessionKeys.REQUEST_LIST_FOCUS);
        if(key == 0) {
            key = Constant.RequestStatus.ALL;
            Session.putInt(getActivity(), Constant.SessionKeys.REQUEST_LIST_FOCUS, key);
        }
        loadRequests(key, page);
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_item_request_list, menu);
    }

    private ListPopupWindow listPopupWindow;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.action_new){
            //Do whatever you want to do
            RequestCreateFragment fragment = RequestCreateFragment.newInstance(Constant.ParentUIName.FROM_INQUIRY_LIST);
            openFragement(fragment, false);
        }

        if(id == R.id.action_overflow){
            //Do whatever you want to do
            listPopupWindow = new ListPopupWindow(getActivity());
            listPopupWindow.setBackgroundDrawable(getActivity().getResources().getDrawable(R.drawable.main_view_background));
            String[] items = getResources().getStringArray(R.array.buyer_sr_list_menus);
            MenuListAdapter listAdapter = new MenuListAdapter(getActivity().getBaseContext(), items, Enum.Fragment.BUYER_REQUEST_LIST);
            listPopupWindow.setAdapter(listAdapter);
            listPopupWindow.setAnchorView(getActivity().findViewById(R.id.action_overflow));
            listPopupWindow.setWidth(Common.ConvertDPToPixels(getActivity(), 200));
            listPopupWindow.setModal(true);
            listPopupWindow.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    switch (position) {
                        case 0:
                            Session.putInt(getActivity(), Constant.SessionKeys.REQUEST_LIST_FOCUS, Constant.RequestStatus.ALL);
                            ((TextView) getCurrentView().findViewById(R.id.title)).setText("All Enquiries");
                            loadRequests(Constant.RequestStatus.ALL, page);
                            break;

                        case 1:
                            Session.putInt(getActivity(), Constant.SessionKeys.REQUEST_LIST_FOCUS, Constant.RequestStatus.CLOSED);
                            ((TextView) getCurrentView().findViewById(R.id.title)).setText("Accepted Enquiries");
                            loadRequests(Constant.RequestStatus.CLOSED, page);
                            break;

                        case 2:
                            Session.putInt(getActivity(), Constant.SessionKeys.REQUEST_LIST_FOCUS, Constant.RequestStatus.EXPIRED);
                            ((TextView) getCurrentView().findViewById(R.id.title)).setText("Closed Enquiries");
                            loadRequests(Constant.RequestStatus.EXPIRED, page);
                            break;

                        default:
                            Session.putInt(getActivity(), Constant.SessionKeys.REQUEST_LIST_FOCUS, Constant.RequestStatus.ALL);
                            ((TextView) getCurrentView().findViewById(R.id.title)).setText("All Enquiries");
                            loadRequests(Constant.RequestStatus.ALL, page);
                            break;
                    }
                    listPopupWindow.dismiss();
                }
            });
            listPopupWindow.show();
        }

        return super.onOptionsItemSelected(item);
    }

    private void loadRequests(int status, final int page)
    {
        this.status = status;
        getCurrentView().findViewById(R.id.empty).setVisibility(View.GONE);
        String url = Constant.BASE_URL + Constant.CONTROLLER_SERVICE_REQUEST + Constant.SERVICE_SR_BUYER;
        Map<String, String> params = new HashMap<>();
        params.put("BuyerId", String.valueOf(userId));
        params.put("Status", String.valueOf(status));
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
                    List<QuotationRequest> requestList = Arrays.asList(gson.fromJson(result, QuotationRequest[].class ));
                    if(page == 1)
                    {
                        globalList = new ArrayList<>();
                        globalList.addAll(requestList);
                        adapter = new BuyerRequestListAdapter(getActivity(), getActivity(), globalList);
                        ((ListView)getCurrentView().findViewById(R.id.requestlist)).setAdapter(adapter);
                        if(requestList.size() > 0)
                            getCurrentView().findViewById(R.id.empty).setVisibility(View.GONE);
                        else
                            getCurrentView().findViewById(R.id.empty).setVisibility(View.VISIBLE);
                    }
                    else
                    {
                        globalList.addAll(requestList);
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
        ((ListView) getCurrentView().findViewById(R.id.requestlist)).setOnScrollListener(new EndlessScrollListener() {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                if(page > 1)
                    loadRequests(status, page);
            }
        });
    }

    @Override
    public boolean onBackPressed() {
        Session.putInt(getActivity(), Constant.SessionKeys.REQUEST_LIST_FOCUS, Constant.RequestStatus.ALL);
        User user = new UserService(getActivity()).GetCurrentUser();
        if(user.Type == Constant.UserType.SELLER)
            openFragement(AgentHomeFragment.newInstance(), false);
        else
            openFragement(BuyerHomeFragment.newInstance(), false);

        return false;
    }


}
