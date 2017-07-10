package com.iva.findexpert.UI.Agent.Fragments;


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
import com.iva.findexpert.DomainModel.User;
import com.iva.findexpert.R;
import com.iva.findexpert.Service.UserService;
import com.iva.findexpert.UI.Common.Fragments.BaseFragment;
import com.iva.findexpert.UI.Helpers.HttpClientHelper;
import com.iva.findexpert.UI.Helpers.IHttpResponse;
import com.iva.findexpert.ViewModel.QuotationTemplate;

import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class TemplateEditFragment extends BaseFragment {

    private int templateId;

    public TemplateEditFragment() {
        // Required empty public constructor
    }

    public static TemplateEditFragment newInstance(int templateId)
    {
        TemplateEditFragment fragment = new TemplateEditFragment();
        Bundle args = new Bundle();
        args.putInt(Constant.SessionKeys.TEMPLATE_ID, templateId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            templateId = getArguments().getInt(Constant.SessionKeys.TEMPLATE_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_template_edit, container, false);
        setCurrentView(view);
        if(templateId > 0)
            loadTemplate();
        else
        {
            ((TextView) getCurrentView().findViewById(R.id.header)).setText("Add Template");
        }
        setButtons();
        return view;
    }

    private void loadTemplate()
    {
        String url = Constant.BASE_URL + Constant.CONTROLLER_TEMPLATE + Constant.SERVICE_TEMPLATE_GET;
        Map<String, String> params = new HashMap<>();
        params.put("Id", String.valueOf(templateId));

        Map<String,String> headerParams = new HashMap<>();
        headerParams.put("Authorization", "bearer " + new UserService(getActivity()).GetCurrentUser().Token);

        showProgress(getActivity());
        new HttpClientHelper().getResponseStringURLEncoded(getActivity(), url, Request.Method.GET, params, headerParams, new IHttpResponse() {
            @Override
            public void onSuccess(String result) {
                if(result != null)
                {
                    Gson gson = new Gson();
                    QuotationTemplate template = gson.fromJson(result, QuotationTemplate.class);
                    if(template != null)
                    {
                        ((TextView)getCurrentView().findViewById(R.id.name)).setText(template.Name);
                        ((TextView)getCurrentView().findViewById(R.id.body)).setText(template.Body);
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
        getCurrentView().findViewById(R.id.save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveTemplate();
            }
        });
    }

    private boolean validate()
    {
        String name = ((EditText) getCurrentView().findViewById(R.id.name)).getText().toString();
        String body = ((EditText) getCurrentView().findViewById(R.id.name)).getText().toString();
        if(TextUtils.isEmpty(name))
        {
            showAlert(getActivity(), "Template name cannot be empty!");
            return false;
        }
        if(TextUtils.isEmpty(body))
        {
            showAlert(getActivity(), "Template body cannot be empty!");
            return false;
        }
        return true;
    }

    private void saveTemplate()
    {
        if(validate())
        {
            final User user = new UserService(getActivity()).GetCurrentUser();
            QuotationTemplate template = new QuotationTemplate();

            String url = Constant.BASE_URL + Constant.CONTROLLER_TEMPLATE + Constant.SERVICE_TEMPLATE_UPDATE;
            if(templateId == 0)
                url = Constant.BASE_URL + Constant.CONTROLLER_TEMPLATE + Constant.SERVICE_TEMPLATE_ADD;

            template.ModifiedBy = user.RemoteId;
            template.CompanyId = user.CompanyId;
            template.Id = templateId;
            template.Name = ((EditText) getCurrentView().findViewById(R.id.name)).getText().toString();
            template.Body = ((EditText) getCurrentView().findViewById(R.id.body)).getText().toString();
            String jsonObj = new Gson().toJson(template);

            Map<String,String> headerParams = new HashMap<>();
            headerParams.put("Authorization", "bearer " + user.Token );

            showProgress(getActivity());

            new HttpClientHelper().getResponseString(getActivity(), url, jsonObj, headerParams, new IHttpResponse() {
                @Override
                public void onSuccess(String result) {
                    hideProgress();
                    showAlert(getActivity(), "Template saved successfully");
                    getActivity().onBackPressed();
                }

                @Override
                public void onError(int error) {
                    hideProgress();
                    showAlert(getActivity(), "Error", "Error occurred while saving the template. Please try again.");
                }

                @Override
                public void onNoInternetConnection(){ hideProgress();}
            });
        }
    }

}
