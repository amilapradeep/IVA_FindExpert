package com.iva.findexpert.UI.Buyer.Fragments;


import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Request;
import com.google.gson.Gson;
import com.iva.findexpert.Common.Constant;
import com.iva.findexpert.DomainModel.QuotationRequest;
import com.iva.findexpert.DomainModel.User;
import com.iva.findexpert.R;
import com.iva.findexpert.Service.UserService;
import com.iva.findexpert.UI.Adapters.BuyerQuotationListAdapter;
import com.iva.findexpert.UI.Common.Fragments.BaseFragment;
import com.iva.findexpert.UI.Helpers.HttpClientHelper;
import com.iva.findexpert.UI.Helpers.IHttpResponse;
import com.iva.findexpert.UI.Helpers.Insurance;
import com.iva.findexpert.UI.Helpers.NotificationsHelper;
import com.iva.findexpert.UI.Helpers.ViewAnimation;
import com.iva.findexpert.Utility.Common;

import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class RequestDetailFragment extends BaseFragment {

    private User user;
    private long requestId;
    private String parentUI;

    public RequestDetailFragment() {
        // Required empty public constructor
    }

    public static RequestDetailFragment newInstance(long requestId, String from) {
        RequestDetailFragment fragment = new RequestDetailFragment();
        Bundle args = new Bundle();
        args.putLong(Constant.SessionKeys.REQUEST_ID, requestId);
        args.putString(Constant.SessionKeys.PARENT_UI, from);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_request_detail, container, false);
        setCurrentView(view);
        setOverflow();
        user = new UserService(getActivity()).GetCurrentUser();
        loadRequest();
        NotificationsHelper.cancelNotification(getActivity(), Constant.NotificationType.QUOTATION);
        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            requestId = getArguments().getLong(Constant.SessionKeys.REQUEST_ID);
            parentUI = getArguments().getString(Constant.SessionKeys.PARENT_UI);
        }
    }

    private void loadRequest()
    {
        String url = Constant.BASE_URL + Constant.CONTROLLER_SERVICE_REQUEST + Constant.SERVICE_SR_GET;
        Map<String, String> params = new HashMap<>();
        params.put("Id", String.valueOf(requestId));

        Map<String,String> headerParams = new HashMap<>();
        headerParams.put("Authorization", "bearer " + new UserService(getActivity()).GetCurrentUser().Token);

        showProgress(getActivity());
        new HttpClientHelper().getResponseStringURLEncoded(getActivity(), url, Request.Method.GET, params, headerParams, new IHttpResponse() {
            @Override
            public void onSuccess(String result) {
                if(result != null)
                {
                    Gson gson = new Gson();
                    QuotationRequest request = gson.fromJson(result, QuotationRequest.class);
                    if(request != null)
                    {
                        ((TextView)getCurrentView().findViewById(R.id.requestCode)).setText(request.Code);
                        ((TextView)getCurrentView().findViewById(R.id.vehicleNo)).setText(request.VehicleNo);
                        ((TextView)getCurrentView().findViewById(R.id.expiryDate)).setText(request.ExpiryDate);
                        ((TextView)getCurrentView().findViewById(R.id.claimType)).setText(Insurance.getClaimTypeName(request.ClaimType));
                        ((TextView) getCurrentView().findViewById(R.id.vehicleValue)).setText(Common.formatDecimal(request.VehicleValue, true));
                        ((TextView) getCurrentView().findViewById(R.id.requestDate)).setText(request.CreatedDate);

                        switch (request.RegistrationCategory)
                        {
                            case 1:
                                ((TextView) getCurrentView().findViewById(R.id.registrationCategory)).setText(R.string.cat_motor_car);
                                break;
                            case 2:
                                ((TextView) getCurrentView().findViewById(R.id.registrationCategory)).setText(R.string.cat_dual_purpose);
                                break;
                            case 3:
                                ((TextView) getCurrentView().findViewById(R.id.registrationCategory)).setText(R.string.cat_motor_lorry);
                                break;
                            case 4:
                                ((TextView) getCurrentView().findViewById(R.id.registrationCategory)).setText(R.string.cat_motor_cycle);
                                break;
                            case 5:
                                ((TextView) getCurrentView().findViewById(R.id.registrationCategory)).setText(R.string.cat_motor_three_wheel);
                                break;
                            case 6:
                                ((TextView) getCurrentView().findViewById(R.id.registrationCategory)).setText(R.string.cat_motor_bus);
                                break;
                            case 7:
                                ((TextView) getCurrentView().findViewById(R.id.registrationCategory)).setText(R.string.cat_prime_mover);
                                break;
                            case 8:
                                ((TextView) getCurrentView().findViewById(R.id.registrationCategory)).setText(R.string.cat_land_vehicle);
                                break;
                            default:
                                break;
                        }

                        switch (request.UsageType)
                        {
                            case Constant.Usage.PRIVATE:
                                ((TextView) getCurrentView().findViewById(R.id.vehicleUsage)).setText(Constant.Usage.PRIVATE_NAME);
                                break;
                            case Constant.Usage.HIRING:
                                ((TextView) getCurrentView().findViewById(R.id.vehicleUsage)).setText(Constant.Usage.HIRING_NAME);
                                break;
                            case Constant.Usage.RENT:
                                ((TextView) getCurrentView().findViewById(R.id.vehicleUsage)).setText(Constant.Usage.RENT_NAME);
                                break;
                            default:
                                break;
                        }

                        ((TextView) getCurrentView().findViewById(R.id.vehicleYear)).setText(String.valueOf(request.VehicleYear));

                        if(request.IsFinanced)
                            ((TextView) getCurrentView().findViewById(R.id.vehicleFinanced)).setText("Yes");
                        else
                            ((TextView) getCurrentView().findViewById(R.id.vehicleFinanced)).setText("No");

                        ((TextView) getCurrentView().findViewById(R.id.location)).setText(request.Location);

                        BuyerQuotationListAdapter adapter =
                                new BuyerQuotationListAdapter(getActivity(), getActivity(), request.QuotationList);
                        ((ListView) getCurrentView().findViewById(R.id.quotationList)).setAdapter(adapter);
                        if(request.QuotationList.size() > 0)
                            getCurrentView().findViewById(R.id.empty).setVisibility(View.GONE);
                        else
                        {
                            getCurrentView().findViewById(R.id.empty).setVisibility(View.VISIBLE);
                            if(request.Status == Constant.RequestStatus.CLOSED ||
                                    request.Status == Constant.RequestStatus.EXPIRED)
                                ((TextView) getCurrentView().findViewById(R.id.empty)).setText("No quotations received.");
                        }


                        if(request.QuotationList.size() > 0) {
                            getCurrentView().findViewById(R.id.additionalContainer).setVisibility(View.GONE);
                            getCurrentView().findViewById(R.id.overflowButton).setVisibility(View.VISIBLE);
                        }
                        else {
                            getCurrentView().findViewById(R.id.additionalContainer).setVisibility(View.VISIBLE);
                            getCurrentView().findViewById(R.id.overflowButton).setVisibility(View.GONE);
                        }

                        TextView countView = (TextView) getCurrentView().findViewById(R.id.quotationCount);
                        countView.setText("Quotations Received (" + String.valueOf(request.QuotationList.size()) + ")");

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
        User user = new UserService(getActivity()).GetCurrentUser();
        if(user != null)
        {
            if(parentUI.equals(Constant.ParentUIName.FROM_HOME)) {
                openFragement(BuyerHomeFragment.newInstance(), false);
                return false;
            }
        }

        return true;
    }

    private void setOverflow()
    {
        LinearLayout button =  (LinearLayout)getCurrentView().findViewById(R.id.overflowButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View container = getCurrentView().findViewById(R.id.additionalContainer);
                setExpandCollapse(container);

            }
        });

        getCurrentView().findViewById(R.id.codeArea).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View container = getCurrentView().findViewById(R.id.additionalContainer);
                setExpandCollapse(container);

            }
        });

        getCurrentView().findViewById(R.id.vehicleNoArea).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View container = getCurrentView().findViewById(R.id.additionalContainer);
                setExpandCollapse(container);

            }
        });

        getCurrentView().findViewById(R.id.additionalContainer).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View container = getCurrentView().findViewById(R.id.additionalContainer);
                setExpandCollapse(container);

            }
        });
    }

    private View expandedView = null;
    private void setExpandCollapse(View detailView)
    {
        ImageView imageView = (ImageView)getCurrentView().findViewById(R.id.overflowButtonImage);
        if(detailView.getVisibility() == View.VISIBLE) {
            ViewAnimation.collapse(detailView);
            imageView.setImageResource(R.mipmap.ic_expand_more);
        }
        else
        {
            if(expandedView != null)
                ViewAnimation.collapse(expandedView);
            ViewAnimation.expand(detailView);
            expandedView = detailView;
            imageView.setImageResource(R.mipmap.ic_expand_less);
        }
        Common.hideKeyboard(getActivity());
    }

}
