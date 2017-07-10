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
import com.iva.findexpert.UI.Adapters.ServiceCategoryListAdapter;
import com.iva.findexpert.UI.Agent.Fragments.AgentHomeFragment;
import com.iva.findexpert.UI.Buyer.Fragments.BuyerHomeFragment;
import com.iva.findexpert.UI.Helpers.HttpClientHelper;
import com.iva.findexpert.UI.Helpers.IHttpResponse;
import com.iva.findexpert.ViewModel.ServiceCategory;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class AroundMeFragment extends BaseFragment {


    public AroundMeFragment() {
        // Required empty public constructor
    }

    public static AroundMeFragment newInstance()
    {
        return new AroundMeFragment();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_around_me, container, false);
        setCurrentView(view);
        loadCategories();
        return view;
    }

    private void loadCategories()
    {
        String url = Constant.BASE_URL + Constant.CONTROLLER_AROUND_ME + Constant.SERVICE_GET_SERVICE_CATEGORIES;

        Map<String,String> headerParams = new HashMap<>();
        headerParams.put("Authorization", "bearer " + new UserService(getActivity()).GetCurrentUser().Token);

        showProgress(getActivity());
        new HttpClientHelper().getResponseStringURLEncoded(
                getActivity(), url, Request.Method.GET, null, headerParams, new IHttpResponse() {
                    @Override
                    public void onSuccess(String result) {
                        if(result != null)
                        {
                            Gson gson = new Gson();
                            List<ServiceCategory> categories = Arrays.asList(gson.fromJson(result, ServiceCategory[].class ));
                            ArrayAdapter adapter = new ServiceCategoryListAdapter(getActivity(), getActivity(), categories);
                            ((ListView)getCurrentView().findViewById(R.id.categories)).setAdapter(adapter);
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
        User user = new UserService(getActivity()).GetCurrentUser();
        if(user.Type == Constant.UserType.SELLER)
            openFragement(AgentHomeFragment.newInstance(), false);
        else
            openFragement(BuyerHomeFragment.newInstance(), false);

        return false;
    }

}
