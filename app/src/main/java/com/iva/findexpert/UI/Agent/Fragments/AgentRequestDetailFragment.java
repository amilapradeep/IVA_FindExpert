package com.iva.findexpert.UI.Agent.Fragments;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.android.volley.Request;
import com.google.gson.Gson;
import com.iva.findexpert.Common.Constant;
import com.iva.findexpert.DomainModel.Quotation;
import com.iva.findexpert.DomainModel.QuotationRequest;
import com.iva.findexpert.DomainModel.User;
import com.iva.findexpert.R;
import com.iva.findexpert.Service.UserService;
import com.iva.findexpert.Service.VehicleCategoryService;
import com.iva.findexpert.UI.Common.Fragments.MessageCreateFragment;
import com.iva.findexpert.UI.Common.Fragments.BaseFragment;
import com.iva.findexpert.UI.Helpers.HttpClientHelper;
import com.iva.findexpert.UI.Helpers.IHttpResponse;
import com.iva.findexpert.UI.Helpers.Insurance;
import com.iva.findexpert.UI.Helpers.NotificationsHelper;
import com.iva.findexpert.UI.Helpers.ViewAnimation;
import com.iva.findexpert.Utility.Common;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class AgentRequestDetailFragment extends BaseFragment {

    private long requestId;
    private long quotationId;
    private String mobileNo;
    private QuotationRequest request;
    private String parentUI;
    private User user;

    public AgentRequestDetailFragment() {
        // Required empty public constructor
    }

    public static AgentRequestDetailFragment newInstance(long requestId, String from) {
        AgentRequestDetailFragment fragment = new AgentRequestDetailFragment();
        Bundle args = new Bundle();
        args.putLong(Constant.SessionKeys.REQUEST_ID, requestId);
        args.putString(Constant.SessionKeys.PARENT_UI, from);
        fragment.setArguments(args);
        return fragment;
    }

    public static AgentRequestDetailFragment newInstanceQuotaitonId(long quotationId, String from) {
        AgentRequestDetailFragment fragment = new AgentRequestDetailFragment();
        Bundle args = new Bundle();
        args.putLong(Constant.SessionKeys.QUOTATION_ID, quotationId);
        args.putString(Constant.SessionKeys.PARENT_UI, from);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        user = new UserService(getActivity()).GetCurrentUser();
        if (getArguments() != null) {
            requestId = getArguments().getLong(Constant.SessionKeys.REQUEST_ID);
            quotationId = getArguments().getLong(Constant.SessionKeys.QUOTATION_ID);
            parentUI = getArguments().getString(Constant.SessionKeys.PARENT_UI);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_agent_request_detail, container, false);
        setCurrentView(view);
        loadRequest();
        NotificationsHelper.cancelNotification(getActivity(), Constant.NotificationType.REQUEST);
        NotificationsHelper.cancelNotification(getActivity(), Constant.NotificationType.ACCEPT);
        return view;
    }

    private void loadRequest()
    {
        User user = new UserService(getActivity()).GetCurrentUser();
        String url = Constant.BASE_URL + Constant.CONTROLLER_SERVICE_REQUEST + Constant.SERVICE_SR_GET;
        Map<String, String> params = new HashMap<>();
        if(quotationId == 0) {
            params.put("Id", String.valueOf(requestId));
        }
        else
        {
            url = Constant.BASE_URL + Constant.CONTROLLER_SERVICE_REQUEST + Constant.SERVICE_SR_GET_BY_QUOTE_ID;
            params.put("QuotationId", String.valueOf(quotationId));
        }
        params.put("UserId", String.valueOf(user.Id));

        Map<String,String> headerParams = new HashMap<>();
        headerParams.put("Authorization", "bearer " + user.Token);

        showProgress(getActivity());
        new HttpClientHelper().getResponseStringURLEncoded(getActivity(), url, Request.Method.GET, params, headerParams, new IHttpResponse() {
            @Override
            public void onSuccess(String result) {
                if(result != null)
                {
                    Gson gson = new Gson();
                    request = gson.fromJson(result, QuotationRequest.class);
                    if(request != null)
                    {
                        ((TextView)getCurrentView().findViewById(R.id.code)).setText(request.Code);
                        ((TextView)getCurrentView().findViewById(R.id.buyerName)).setText(request.BuyerName);
                        ((TextView)getCurrentView().findViewById(R.id.vehicleNo)).setText(request.VehicleNo);
                        ((TextView)getCurrentView().findViewById(R.id.expiryDate)).setText(request.ExpiryDate);
                        ((TextView)getCurrentView().findViewById(R.id.occuredTime)).setText(request.CreatedDate);
                        ((TextView)getCurrentView().findViewById(R.id.claimType)).setText(Insurance.getClaimTypeName(request.ClaimType));
                        String category = new VehicleCategoryService(getActivity()).GetById(request.RegistrationCategory).Name;
                        ((TextView)getCurrentView().findViewById(R.id.category)).setText(category);
                        ((TextView)getCurrentView().findViewById(R.id.usage)).setText(Insurance.getUsageName(request.UsageType));
                        ((TextView)getCurrentView().findViewById(R.id.value)).setText(Common.formatDecimal(request.VehicleValue, true));
                        ((TextView)getCurrentView().findViewById(R.id.year)).setText(String.valueOf(request.VehicleYear));
                        ((TextView)getCurrentView().findViewById(R.id.city)).setText(String.valueOf(request.Location));
                        ((TextView)getCurrentView().findViewById(R.id.financed)).setText(request.IsFinanced ? "Yes" : "No");
                        switch (request.FuelType)
                        {
                            case Constant.FuelType.GAS:
                                ((TextView)getCurrentView().findViewById(R.id.fuelType)).setText(R.string.fuel_type_gas);
                                break;
                            case Constant.FuelType.HYBRID:
                                ((TextView)getCurrentView().findViewById(R.id.fuelType)).setText(R.string.fuel_type_hybrid);
                                break;
                            case Constant.FuelType.ELECTRIC:
                                ((TextView)getCurrentView().findViewById(R.id.fuelType)).setText(R.string.fuel_type_electric);
                                break;
                            default:
                                break;
                        }
                        mobileNo = request.BuyerMobile;

                        if(!request.IsAllowPhone)
                            getCurrentView().findViewById(R.id.phoneButton).setVisibility(View.GONE);
                        else
                        {
                            getCurrentView().findViewById(R.id.phoneButton).setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    OpenDialer();
                                }
                            });
                        }

                        showQuotation(request);
                    }

                    setButtons();
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

    private void OpenDialer()
    {
        if(!TextUtils.isEmpty(mobileNo)) {
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:" + "0" + mobileNo.substring(2)));
            startActivity(intent);
        }
    }

    private void setButtons()
    {
        if(request.Status == Constant.QuotaionRequestStatus.EXPIRED || request.Status == Constant.QuotaionRequestStatus.CLOSED )
        {
            getCurrentView().findViewById(R.id.quoteButton).setVisibility(View.GONE);
            getCurrentView().findViewById(R.id.messageButton).setVisibility(View.GONE);
            ScrollView scrollView = (ScrollView) getCurrentView().findViewById(R.id.pageScroll);
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) scrollView
                    .getLayoutParams();

            layoutParams.bottomMargin = 10;
            scrollView.setLayoutParams(layoutParams);
        }
        else
        {
            getCurrentView().findViewById(R.id.messageButton).setVisibility(View.VISIBLE);
            getCurrentView().findViewById(R.id.messageButton).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MessageCreateFragment fragment =
                            MessageCreateFragment.newInstance(0, request.UserId, request.BuyerName, requestId);
                    openFragement(fragment, true);
                }
            });

            getCurrentView().findViewById(R.id.quoteButton).setVisibility(View.VISIBLE);
            getCurrentView().findViewById(R.id.quoteButton).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AddQuotationFragment fragment = AddQuotationFragment.newInstance(requestId);
                    openFragement(fragment, true);
                }
            });
        }

    }

    @Override
    public boolean onBackPressed() {
        if(user != null)
        {
            if(parentUI.equals(Constant.ParentUIName.FROM_HOME)) {
                openFragement(AgentHomeFragment.newInstance(), false);
                return false;
            }
        }

        return true;
    }

    private void showQuotation(QuotationRequest request)
    {
        List<Quotation> quotes = request.QuotationList;
        Quotation agentQuote = null;
        if(quotes != null && quotes.size() > 0)
        {
            for(int i = 0; i< quotes.size(); i++)
            {
                if(quotes.get(i).AgentId == user.Id) {
                    agentQuote = quotes.get(i);
                    break;
                }
            }
        }

        if(agentQuote != null)
        {
            getCurrentView().findViewById(R.id.quotationContainer).setVisibility(View.VISIBLE);
            ((TextView) getCurrentView().findViewById(R.id.premium)).setText(agentQuote.Premimum + " Rs");
            ((TextView) getCurrentView().findViewById(R.id.coverage)).setText(agentQuote.Cover + " Rs");
            ((TextView) getCurrentView().findViewById(R.id.quoteDescription)).setText(agentQuote.QuotationText);


            getCurrentView().findViewById(R.id.quoteHeader).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setCollapsible();
                }
            });
        }
    }

    private View expandedView = null;
    private void setCollapsible()
    {
        ImageView imageView = (ImageView)getCurrentView().findViewById(R.id.overflowButtonImage);
        View detailView = getCurrentView().findViewById(R.id.quoteDetails);
        if(detailView.getVisibility() == View.VISIBLE) {
            ViewAnimation.collapse(detailView);
            imageView.setImageResource(R.mipmap.ic_expand_more_black);
        }
        else
        {
            if(expandedView != null)
                ViewAnimation.collapse(expandedView);
            ViewAnimation.expand(detailView);
            expandedView = detailView;
            imageView.setImageResource(R.mipmap.ic_expand_less_black);

            final ScrollView scrollView = (ScrollView) getCurrentView().findViewById(R.id.pageScroll);
            scrollView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    scrollView.post(new Runnable() {
                        public void run() {
                            scrollView.fullScroll(View.FOCUS_DOWN);
                        }
                    });
                }
            });
        }

    }

}
