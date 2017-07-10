package com.iva.findexpert.UI.Common;


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
import android.widget.Toast;

import com.android.volley.Request;
import com.google.gson.Gson;
import com.iva.findexpert.Common.Constant;
import com.iva.findexpert.Common.Enum;
import com.iva.findexpert.DomainModel.User;
import com.iva.findexpert.R;
import com.iva.findexpert.Service.UserService;
import com.iva.findexpert.UI.Adapters.EndlessScrollListener;
import com.iva.findexpert.UI.Adapters.MenuListAdapter;
import com.iva.findexpert.UI.Adapters.NotificationListAdapter;
import com.iva.findexpert.UI.Agent.Fragments.AgentHomeFragment;
import com.iva.findexpert.UI.Buyer.Fragments.BuyerHomeFragment;
import com.iva.findexpert.UI.Common.Fragments.BaseFragment;
import com.iva.findexpert.UI.Helpers.HttpClientHelper;
import com.iva.findexpert.UI.Helpers.IHttpResponse;
import com.iva.findexpert.Utility.Common;
import com.iva.findexpert.ViewModel.NotificationViewModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class NotificationListFragment extends BaseFragment {

    private long userId;
    private List<NotificationViewModel> globalList;
    private ArrayAdapter adapter;
    private ListPopupWindow listPopupWindow;
    private User user;

    public NotificationListFragment() {
        // Required empty public constructor
    }

    public static NotificationListFragment newInstance()
    {
        NotificationListFragment fragment = new NotificationListFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        user = new UserService(getActivity()).GetCurrentUser();
        userId = user.Id;
        setHasOptionsMenu(true);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_notification_list, container, false);

        setCurrentView(view);
        SetScrollPaging();
        loadNotifications(1);
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_notification_list, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.action_overflow)
        {
            listPopupWindow = new ListPopupWindow(getActivity());
            listPopupWindow.setBackgroundDrawable(getActivity().getResources().getDrawable(R.drawable.main_view_background));
            String[] items = getResources().getStringArray(R.array.notification_list_menus);
            MenuListAdapter listAdapter = new MenuListAdapter(getActivity().getBaseContext(), items, Enum.Fragment.NOTIFICATION_LIST);
            listPopupWindow.setAdapter(listAdapter);
            listPopupWindow.setAnchorView(getActivity().findViewById(R.id.action_overflow));
            listPopupWindow.setWidth(Common.ConvertDPToPixels(getActivity(), 200));
            listPopupWindow.setModal(true);
            listPopupWindow.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    switch(position)
                    {
                        case 0:
                            deleteSelected();
                            break;
                        case 1:
                            deleteAll();
                            break;
                        default:
                            break;
                    }
                    listPopupWindow.dismiss();
                }
            });

            listPopupWindow.show();

        }

        return super.onOptionsItemSelected(item);
    }



    private void SetScrollPaging()
    {
        ((ListView) getCurrentView().findViewById(R.id.notificationList)).setOnScrollListener(
                new EndlessScrollListener() {
                    @Override
                    public void onLoadMore(int page, int totalItemsCount) {
                        if(page > 1)
                            loadNotifications(page);
            }
        });
    }

    private void loadNotifications(final int page)
    {
        getCurrentView().findViewById(R.id.empty).setVisibility(View.GONE);
        String url = Constant.BASE_URL + Constant.CONTROLLER_NOTIFICATION + Constant.SERVICE_GET_NOTIFICATION_LIST;
        Map<String, String> params = new HashMap<>();
        params.put("UserId", String.valueOf(userId));
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
                    List<NotificationViewModel> notificationList = Arrays.asList(gson.fromJson(result, NotificationViewModel[].class ));
                    if(page == 1)
                    {
                        globalList = new ArrayList<>();
                        globalList.addAll(notificationList);
                        adapter = new NotificationListAdapter(getActivity(), getActivity(), globalList, user.Type);
                        ((ListView)getCurrentView().findViewById(R.id.notificationList)).setAdapter(adapter);
                        if(notificationList.size() > 0)
                            getCurrentView().findViewById(R.id.empty).setVisibility(View.GONE);
                        else
                            getCurrentView().findViewById(R.id.empty).setVisibility(View.VISIBLE);
                    }
                    else
                    {
                        globalList.addAll(notificationList);
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

    @Override
    public boolean onBackPressed() {
        if(user.Type == Constant.UserType.SELLER)
            openFragement(AgentHomeFragment.newInstance(), false);
        else
            openFragement(BuyerHomeFragment.newInstance(), false);

        return false;
    }

    private void deleteSelected()
    {
        List<Long> selectedList = new ArrayList<>();
        for (int i = 0; i < globalList.size(); i++)
        {
            if(globalList.get(i).IsSelected)
                selectedList.add(globalList.get(i).Id);
        }
        if(selectedList.size() == 0)
        {
            Toast.makeText(getActivity(), "No items selected!", Toast.LENGTH_SHORT).show();
            return;
        }

        SelectedIds selectedIds = new SelectedIds();
        selectedIds.Ids = selectedList;

        String url = Constant.BASE_URL + Constant.CONTROLLER_NOTIFICATION + Constant.SERVICE_DELETE_SELECTED_NOTIFICATIONS;
        Map<String,String> headerParams = new HashMap<>();
        headerParams.put("Authorization", "bearer " + new UserService(getActivity()).GetCurrentUser().Token);

        new HttpClientHelper().getResponseString(getActivity(), url, new Gson().toJson(selectedIds), headerParams, new IHttpResponse() {
            @Override
            public void onSuccess(String result) {
                loadNotifications(1);
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

    private void deleteAll()
    {
        String url = Constant.BASE_URL + Constant.CONTROLLER_NOTIFICATION + Constant.SERVICE_DELETE_ALL_NOTIFICATIONS;
        Map<String, String> params = new HashMap<>();
        long userId = new UserService(getActivity()).GetCurrentUser().Id;
        params.put("UserId", String.valueOf(userId));

        Map<String,String> headerParams = new HashMap<>();
        headerParams.put("Authorization", "bearer " + new UserService(getActivity()).GetCurrentUser().Token);

        new HttpClientHelper().getResponseStringURLEncoded(getActivity(), url, Request.Method.GET, params , headerParams, new IHttpResponse() {
            @Override
            public void onSuccess(String result) {
                loadNotifications(1);
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

    class SelectedIds
    {
        public List<Long> Ids;
    }


}
