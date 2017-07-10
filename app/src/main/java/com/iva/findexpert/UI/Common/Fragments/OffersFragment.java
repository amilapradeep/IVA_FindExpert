package com.iva.findexpert.UI.Common.Fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Request;
import com.google.gson.Gson;
import com.iva.findexpert.Common.Constant;
import com.iva.findexpert.DomainModel.User;
import com.iva.findexpert.R;
import com.iva.findexpert.Service.UserService;
import com.iva.findexpert.UI.Adapters.PromotionListAdapter;
import com.iva.findexpert.UI.Agent.Fragments.AgentHomeFragment;
import com.iva.findexpert.UI.Buyer.Fragments.BuyerHomeFragment;
import com.iva.findexpert.UI.Helpers.FragmentHelper;
import com.iva.findexpert.UI.Helpers.HttpClientHelper;
import com.iva.findexpert.UI.Helpers.IHttpResponse;
import com.iva.findexpert.Utility.Session;
import com.iva.findexpert.ViewModel.PromotionViewModel;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class OffersFragment extends BaseFragment {

    private String parentUI;

    public OffersFragment() {
        // Required empty public constructor
    }

    public  static OffersFragment newInstance()
    {
        OffersFragment fragment = new OffersFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_offeres, container, false);
        setCurrentView(view);
        loadLatestPromotions();
        loadPromotions();
        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            parentUI = getArguments().getString(Constant.SessionKeys.PARENT_UI);
        }
    }

    private void loadPromotions()
    {
        String url = Constant.BASE_URL + Constant.CONTROLLER_PROMOTION + Constant.SERVICE_GET_PROMOTIONS;
        Map<String, String> params = new HashMap<>();
        params.put("Type", "1");

        Map<String,String> headerParams = new HashMap<>();
        headerParams.put("Authorization", "bearer " + new UserService(getActivity()).GetCurrentUser().Token);

        showProgress(getActivity());
        new HttpClientHelper().getResponseStringURLEncoded(
                getActivity(), url, Request.Method.GET, params, headerParams, new IHttpResponse() {
                    @Override
                    public void onSuccess(String result) {
                        if(result != null)
                        {
                            Gson gson = new Gson();
                            List<PromotionViewModel> promotions = Arrays.asList(gson.fromJson(result, PromotionViewModel[].class ));
                            ArrayAdapter adapter = new PromotionListAdapter(getActivity(), getActivity(), promotions);
                            ((ListView)getCurrentView().findViewById(R.id.promotions)).setAdapter(adapter);
                            if(promotions != null && promotions.size() > 0)
                                getCurrentView().findViewById(R.id.empty).setVisibility(View.GONE);
                            else
                                getCurrentView().findViewById(R.id.empty).setVisibility(View.VISIBLE);
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

    private void loadLatestPromotions()
    {
        String url = Constant.BASE_URL + Constant.CONTROLLER_PROMOTION + Constant.SERVICE_GET_LATEST_PROMOTION;
        Map<String, String> params = new HashMap<>();

        long id = Session.getLong(getActivity(), Constant.SessionKeys.LAST_PROMOTION);
        if(id == 0)
            params.put("Type", "1");
        else
        {
            url = Constant.BASE_URL + Constant.CONTROLLER_PROMOTION + Constant.SERVICE_GET_PROMOTION;
            params.put("Id", String.valueOf(id));
        }

        Map<String,String> headerParams = new HashMap<>();
        headerParams.put("Authorization", "bearer " + new UserService(getActivity()).GetCurrentUser().Token);

        new HttpClientHelper().getResponseStringURLEncoded(
                getActivity(), url, Request.Method.GET, params, headerParams, new IHttpResponse() {
                    @Override
                    public void onSuccess(String result) {
                        if(result != null)
                        {
                            Gson gson = new Gson();
                            final PromotionViewModel promotion = gson.fromJson(result, PromotionViewModel.class);
                            if(promotion != null)
                            {
                                ((TextView) getCurrentView().findViewById(R.id.title)).setText(promotion.Title);
                                ((TextView) getCurrentView().findViewById(R.id.header)).setText(promotion.Header);
                                getCurrentView().findViewById(R.id.discoverButton).setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        android.app.Fragment fragment = PromotionDetailFragment.newInstance(promotion.Id);
                                        FragmentHelper.openFragement(fragment, getActivity());
                                    }
                                });
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
        if(parentUI == Constant.ParentUIName.FROM_MESSAGE_LIST)
            return true;
        else
        {
            User user = new UserService(getActivity()).GetCurrentUser();
            if(user.Type == Constant.UserType.SELLER)
                openFragement(AgentHomeFragment.newInstance(), false);
            else
                openFragement(BuyerHomeFragment.newInstance(), false);
        }

        return false;
    }

}
