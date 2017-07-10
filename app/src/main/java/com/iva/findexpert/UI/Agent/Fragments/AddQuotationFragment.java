package com.iva.findexpert.UI.Agent.Fragments;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.google.gson.Gson;
import com.iva.findexpert.Common.Constant;
import com.iva.findexpert.DomainModel.Quotation;
import com.iva.findexpert.DomainModel.QuotationRequest;
import com.iva.findexpert.DomainModel.User;
import com.iva.findexpert.R;
import com.iva.findexpert.Service.UserService;
import com.iva.findexpert.Service.VehicleCategoryService;
import com.iva.findexpert.UI.Common.Fragments.BaseFragment;
import com.iva.findexpert.UI.Helpers.HttpClientHelper;
import com.iva.findexpert.UI.Helpers.IHttpResponse;
import com.iva.findexpert.UI.Helpers.Insurance;
import com.iva.findexpert.UI.Helpers.NumberTextWatcherForThousand;
import com.iva.findexpert.Utility.Session;
import com.iva.findexpert.ViewModel.QuotationTemplate;

import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class AddQuotationFragment extends BaseFragment {

    private long requestId;
    private String mobileNo;
    private QuotationRequest request;
    private QuotationTemplate template;

    public AddQuotationFragment() {
        // Required empty public constructor
    }

    public static AddQuotationFragment newInstance(long requestId)
    {
        AddQuotationFragment fragment = new AddQuotationFragment();
        Bundle args = new Bundle();
        args.putLong(Constant.SessionKeys.REQUEST_ID, requestId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            requestId = getArguments().getLong(Constant.SessionKeys.REQUEST_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_add_quotation, container, false);
        setCurrentView(view);
        loadRequest();
        setButtons();
        setEditText();
        return view;
    }

    @Override
    public void onResume()
    {
        super.onResume();
        String json = Session.getString(getActivity(), Constant.SessionKeys.TEMPLATE_OBJECT);
        if(!TextUtils.isEmpty(json))
        {
            template = new Gson().fromJson(json, QuotationTemplate.class);
            if(template != null)
            {
                ((TextView) getCurrentView().findViewById(R.id.templateName)).setText(template.Name);
                ((TextView) getCurrentView().findViewById(R.id.templateBody)).setText(template.Body);
                getCurrentView().findViewById(R.id.templateContainer).setVisibility(View.VISIBLE);
            }
        }
        Session.remove(getActivity(), Constant.SessionKeys.TEMPLATE_OBJECT);
    }

    private void loadRequest()
    {
        String url = Constant.BASE_URL + Constant.CONTROLLER_SERVICE_REQUEST + Constant.SERVICE_SR_GET;
        Map<String, String> params = new HashMap<>();
        params.put("Id", String.valueOf(requestId));

        Map<String,String> headerParams = new HashMap<>();
        headerParams.put("Authorization", "bearer " + new UserService(getActivity()).GetCurrentUser().Token);

        showProgress(getActivity());
        new HttpClientHelper().getResponseStringURLEncoded(getContext(), url, Request.Method.GET, params, headerParams, new IHttpResponse() {
            @Override
            public void onSuccess(String result) {
                if(result != null)
                {
                    Gson gson = new Gson();
                    request = gson.fromJson(result, QuotationRequest.class);
                    if(request != null)
                    {
                        ((TextView)getCurrentView().findViewById(R.id.buyerName)).setText(request.BuyerName);
                        ((TextView)getCurrentView().findViewById(R.id.vehicleNo)).setText(request.VehicleNo);
                        ((TextView)getCurrentView().findViewById(R.id.expiryDate)).setText(request.ExpiryDate);
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
        getCurrentView().findViewById(R.id.selectTemplate).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TemplateListFragment fragment = TemplateListFragment.newInstance();
                openFragement(fragment, true);
            }
        });

        getCurrentView().findViewById(R.id.sendQuotation).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendQuotation();
            }
        });
    }

    private boolean validate()
    {
        String premium = ((EditText) getCurrentView().findViewById(R.id.premium)).getText().toString();
        String cover = ((EditText) getCurrentView().findViewById(R.id.cover)).getText().toString();
        if(TextUtils.isEmpty(premium))
        {
            showAlert(getActivity(), "Premium amount cannot be empty!");
            return false;
        }
        if(TextUtils.isEmpty(cover))
        {
            showAlert(getActivity(), "Damage cover cannot be empty!");
            return false;
        }
        if(template == null)
        {
            showAlert(getActivity(), "Please select a template for the quotation!");
            return false;
        }
        return true;
    }

    private void sendQuotation()
    {
        if(validate())
        {
            final User user = new UserService(getActivity()).GetCurrentUser();
            Quotation quotation = new Quotation();
            quotation.ServiceRequestId = requestId;
            quotation.AgentId = user.Id;
            quotation.QuotationTemplateId = template.Id;
            quotation.QuotationText = template.Body;
            quotation.Premimum = ((EditText) getCurrentView().findViewById(R.id.premium)).getText().toString();
            quotation.Cover = ((EditText) getCurrentView().findViewById(R.id.cover)).getText().toString();
            String jsonObj = new Gson().toJson(quotation);

            Map<String,String> headerParams = new HashMap<>();
            headerParams.put("Authorization", "bearer " + user.Token );
            String url = Constant.BASE_URL + Constant.CONTROLLER_QUOTATION + Constant.SERVICE_QUOTATION_SEND;
            showProgress(getActivity());

            new HttpClientHelper().getResponseString(getActivity(), url, jsonObj, headerParams, new IHttpResponse() {
                @Override
                public void onSuccess(String result) {
                    hideProgress();
                    showAlert(getActivity(), "Quotation sent successfully");
                    //getActivity().onBackPressed();
                    openFragement(AgentRequestListFragment.newInstance(Constant.AgentRequestListFocus.PENDING), false);
                }

                @Override
                public void onError(int error) {
                    hideProgress();
                    showAlert(getActivity(), "Error occurred while sending the quotation. Please try again.");
                }

                @Override
                public void onNoInternetConnection(){ hideProgress();}
            });
        }
    }

    private void setEditText()
    {
        EditText premium = ((EditText) getCurrentView().findViewById(R.id.premium));
        premium.addTextChangedListener(new NumberTextWatcherForThousand(premium));
        EditText cover = ((EditText) getCurrentView().findViewById(R.id.cover));
        cover.addTextChangedListener(new NumberTextWatcherForThousand(cover));
    }


}
