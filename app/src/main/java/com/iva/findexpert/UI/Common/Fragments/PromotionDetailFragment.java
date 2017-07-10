package com.iva.findexpert.UI.Common.Fragments;


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
import com.iva.findexpert.UI.Agent.Fragments.AgentHomeFragment;
import com.iva.findexpert.UI.Buyer.Fragments.BuyerHomeFragment;
import com.iva.findexpert.UI.Helpers.HttpClientHelper;
import com.iva.findexpert.UI.Helpers.IHttpResponse;
import com.iva.findexpert.ViewModel.PromotionViewModel;

import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class PromotionDetailFragment extends BaseFragment {

    private long promotionId;

    public PromotionDetailFragment() {
        // Required empty public constructor
    }

    public static PromotionDetailFragment newInstance(long promotionId)
    {
        PromotionDetailFragment fragment = new PromotionDetailFragment();
        Bundle args = new Bundle();
        args.putLong(Constant.SessionKeys.PROMOTION_ID, promotionId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_promotion_detail, container, false);
        setCurrentView(view);
        loadPromotion();
        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            promotionId = getArguments().getLong(Constant.SessionKeys.PROMOTION_ID);
        }
    }

    private void loadPromotion()
    {
        String url = Constant.BASE_URL + Constant.CONTROLLER_PROMOTION + Constant.SERVICE_GET_PROMOTION;
        Map<String, String> params = new HashMap<>();
        params.put("Id", String.valueOf(promotionId));

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
                            PromotionViewModel promotion = gson.fromJson(result, PromotionViewModel.class);
                            if(promotion != null)
                            {
                                ((TextView)getCurrentView().findViewById(R.id.title)).setText(promotion.Title);
                                ((TextView)getCurrentView().findViewById(R.id.header)).setText(promotion.Header);
                                ((TextView)getCurrentView().findViewById(R.id.description)).setText(promotion.Description);
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

}
