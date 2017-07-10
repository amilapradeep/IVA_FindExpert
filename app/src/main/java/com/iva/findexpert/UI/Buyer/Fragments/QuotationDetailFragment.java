package com.iva.findexpert.UI.Buyer.Fragments;


import android.content.Intent;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.google.gson.Gson;
import com.iva.findexpert.Common.Constant;
import com.iva.findexpert.DomainModel.Quotation;
import com.iva.findexpert.DomainModel.User;
import com.iva.findexpert.R;
import com.iva.findexpert.Service.UserService;
import com.iva.findexpert.UI.Agent.Fragments.AgentRequestDetailFragment;
import com.iva.findexpert.UI.Common.Fragments.BaseFragment;
import com.iva.findexpert.UI.Common.CustomDialog;
import com.iva.findexpert.UI.Common.Fragments.MessageFragment;
import com.iva.findexpert.UI.Helpers.FragmentHelper;
import com.iva.findexpert.UI.Helpers.HttpClientHelper;
import com.iva.findexpert.UI.Helpers.IHttpResponse;
import com.iva.findexpert.UI.Helpers.NotificationsHelper;
import com.iva.findexpert.Utility.Common;

import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class QuotationDetailFragment extends BaseFragment {

    private long quoteId;
    private long threadId;
    private int status;
    private String parentUI;
    private User user;

    public QuotationDetailFragment() {
        // Required empty public constructor
    }

    public static QuotationDetailFragment newInstance(long quotationId, String from)
    {
        QuotationDetailFragment fragment = new QuotationDetailFragment();
        Bundle args = new Bundle();
        args.putLong(Constant.SessionKeys.QUOTATION_ID, quotationId);
        args.putString(Constant.SessionKeys.PARENT_UI, from);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_quote_detail, container, false);
        setCurrentView(view);
        loadQuotation();
        setButtons();
        NotificationsHelper.cancelNotification(getActivity(), Constant.NotificationType.QUOTATION);
        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            quoteId = getArguments().getLong(Constant.SessionKeys.QUOTATION_ID);
            parentUI = getArguments().getString(Constant.SessionKeys.PARENT_UI);
        }
    }

    private void loadQuotation()
    {
        user = new UserService(getActivity()).GetCurrentUser();
        String url = Constant.BASE_URL + Constant.CONTROLLER_QUOTATION + Constant.SERVICE_GET_QUOTATION_FROM_BUYER;
        if(user.Type == Constant.UserType.SELLER)
            url = Constant.BASE_URL + Constant.CONTROLLER_QUOTATION + Constant.SERVICE_GET_QUOTATION_BY_ID;

        Map<String, String> params = new HashMap<>();
        params.put("Id", String.valueOf(quoteId));

        Map<String,String> headerParams = new HashMap<>();
        headerParams.put("Authorization", "bearer " + new UserService(getActivity()).GetCurrentUser().Token);

        showProgress(getActivity());
        new HttpClientHelper().getResponseStringURLEncoded(getActivity(), url, Request.Method.GET, params, headerParams, new IHttpResponse() {
            @Override
            public void onSuccess(String result) {
                if(result != null)
                {
                    Gson gson = new Gson();
                        final Quotation quotation = gson.fromJson(result, Quotation.class);

                    if(quotation != null)
                    {
                        ((TextView)getCurrentView().findViewById(R.id.companyName)).setText(quotation.CompanyName);
                        ((TextView)getCurrentView().findViewById(R.id.agentName)).setText(quotation.AgentName);
                        ((TextView)getCurrentView().findViewById(R.id.agentContact)).setText("0" + quotation.AgentContact.substring(2));
                        ((TextView)getCurrentView().findViewById(R.id.premium)).setText(quotation.Premimum + " Rs");
                        ((TextView)getCurrentView().findViewById(R.id.cover)).setText(quotation.Cover + " Rs");
                        ((TextView)getCurrentView().findViewById(R.id.body)).setText(quotation.QuotationText);
                        TextView requestCodeText = (TextView)getCurrentView().findViewById(R.id.requestCode);
                        requestCodeText.setText(quotation.ServiceRequestCode);
                        requestCodeText.setVisibility(View.VISIBLE);
                        requestCodeText.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                android.app.Fragment fragment = null;
                                if(user.Type == Constant.UserType.BUYER)
                                    fragment = RequestDetailFragment.newInstance(quotation.ServiceRequestId, "");
                                if(user.Type == Constant.UserType.SELLER)
                                    fragment = AgentRequestDetailFragment.newInstance(quotation.ServiceRequestId, "");
                                FragmentHelper.openFragement(fragment,  getActivity());
                            }
                        });

                        ((TextView)getCurrentView().findViewById(R.id.vehicleNo)).setText(quotation.VehicleNo);
                        ((TextView)getCurrentView().findViewById(R.id.vehicleValue)).setText(Common.formatDecimal(quotation.VehicleValue, true));
                        if(quotation.ClaimType == 1)
                        {
                            ((TextView) getCurrentView().findViewById(R.id.claimType)).setText(R.string.str_comprehensive);
                        }
                        else
                        {
                            ((TextView) getCurrentView().findViewById(R.id.claimType)).setText(R.string.str_third_party);
                        }

                        threadId = quotation.ThreadId;
                        status = quotation.Status;

                        if(status == Constant.QuotationStatus.INITIAL ||  status == Constant.QuotationStatus.NONE || status == Constant.QuotationStatus.CHECKED)
                        {
                            if(!quotation.IsExpired)
                            {
                                getCurrentView().findViewById(R.id.messageButton).setVisibility(View.VISIBLE);
                                if(new UserService(getActivity()).GetCurrentUser().Type == Constant.UserType.BUYER)
                                    getCurrentView().findViewById(R.id.acceptButton).setVisibility(View.VISIBLE);
                                setScrollHeight(true);
                            }
                        }
                        else
                        {
                            setScrollHeight(false);
                            getCurrentView().findViewById(R.id.messageButton).setVisibility(View.GONE);
                            getCurrentView().findViewById(R.id.acceptButton).setVisibility(View.GONE);
                        }


                        getCurrentView().findViewById(R.id.agentContact).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                String mobileNo = ((TextView) v).getText().toString();
                                Intent intent = new Intent(Intent.ACTION_DIAL);
                                intent.setData(Uri.parse("tel:" + mobileNo ));
                                startActivity(intent);
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

    private void setButtons()
    {
        getCurrentView().findViewById(R.id.messageButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(threadId > 0) {
                    MessageFragment fragment = MessageFragment.newInstance(threadId, "");
                    openFragement(fragment, true);
                }
            }
        });

        getCurrentView().findViewById(R.id.acceptButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final CustomDialog dialog = showAlert(getActivity(), "Confirm", "Are you sure you need to accept the offer?");
                dialog.OKButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        acceptQuotation();
                    }
                });
                dialog.CancelButton.setVisibility(View.VISIBLE);
                dialog.CancelButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

            }
        });
    }

    private void acceptQuotation()
    {
        String url = Constant.BASE_URL + Constant.CONTROLLER_QUOTATION + Constant.SERVICE_QUOTATION_ACCEPT;
        Map<String, String> params = new HashMap<>();
        params.put("QuotationId", String.valueOf(quoteId));

        Map<String,String> headerParams = new HashMap<>();
        headerParams.put("Authorization", "bearer " + new UserService(getActivity()).GetCurrentUser().Token);

        showProgress(getActivity());
        new HttpClientHelper().getResponseStringURLEncoded(getActivity(), url, Request.Method.POST, params, headerParams, new IHttpResponse() {
            @Override
            public void onSuccess(String result) {
                hideProgress();
                Toast.makeText(getActivity(), "Successfully saved and the offer is accepted.", Toast.LENGTH_LONG).show();
                getActivity().onBackPressed();
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

    private void setScrollHeight(boolean isButtonsVisible)
    {
        ScrollView scroll_view = (ScrollView) getCurrentView().findViewById(R.id.scrollView);
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) scroll_view.getLayoutParams();
        if(isButtonsVisible)
            layoutParams.bottomMargin = Common.ConvertDPToPixels(getActivity(), 60);
        else
            layoutParams.bottomMargin = Common.ConvertDPToPixels(getActivity(), 10);
        scroll_view.setLayoutParams(layoutParams);

    }

}
