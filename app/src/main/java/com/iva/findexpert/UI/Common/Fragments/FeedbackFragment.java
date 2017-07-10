package com.iva.findexpert.UI.Common.Fragments;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;
import com.iva.findexpert.Common.Constant;
import com.iva.findexpert.DomainModel.User;
import com.iva.findexpert.R;
import com.iva.findexpert.Service.UserService;
import com.iva.findexpert.UI.Agent.Fragments.AgentHomeFragment;
import com.iva.findexpert.UI.Buyer.Fragments.BuyerHomeFragment;
import com.iva.findexpert.UI.Helpers.HttpClientHelper;
import com.iva.findexpert.UI.Helpers.IHttpResponse;
import com.iva.findexpert.Utility.Session;
import com.iva.findexpert.ViewModel.UserFeedbackViewModel;

import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class FeedbackFragment extends BaseFragment{


    public FeedbackFragment() {
        // Required empty public constructor
    }

    public static FeedbackFragment newInstance()
    {
        return new FeedbackFragment();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_feedback, container, false);
        setCurrentView(view);
        setButton();
        setContactButton();
        return view;
    }

    private void setButton()
    {
        ((Button) getCurrentView().findViewById(R.id.submitButton)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveFeedback();
            }
        });
    }

    private boolean validate()
    {
        String comment = ((EditText) getCurrentView().findViewById(R.id.comment)).getText().toString();
        if(TextUtils.isEmpty(comment))
        {
            showAlert(getActivity(), "Please add your comment.");
            return false;
        }
        return true;
    }

    private void saveFeedback()
    {
        if(validate())
        {
            User user = new UserService(getActivity()).GetCurrentUser();
            UserFeedbackViewModel feedback = new UserFeedbackViewModel();
            feedback.UserId = user.Id;
            feedback.Description = ((EditText) getCurrentView().findViewById(R.id.comment)).getText().toString();
            feedback.Rating = 0;

            String jsonObj = new Gson().toJson(feedback);
            Map<String,String> headerParams = new HashMap<>();
            headerParams.put("Authorization", "bearer " + user.Token );
            String url = Constant.BASE_URL + Constant.CONTROLLER_USER + Constant.SERVICE_FEEDBACK;
            showProgress(getActivity());

            new HttpClientHelper().getResponseString(getActivity(), url, jsonObj, headerParams, new IHttpResponse() {
                @Override
                public void onSuccess(String result) {
                    hideProgress();
                    Toast.makeText(getActivity(), "Saved successfully, Thank you for the feedback.", Toast.LENGTH_LONG).show();
                }

                @Override
                public void onError(int error) {
                    hideProgress();
                    showAlert(getActivity(), "Error", "Error occured while saving the profile. \n Please try again.");
                }

                @Override
                public void onNoInternetConnection(){ hideProgress();}
            });
        }
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

    private void setContactButton()
    {
        getCurrentView().findViewById(R.id.contactPhone).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //String mobileNo = ((TextView) v).getText().toString();
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + "0771234567" ));
                startActivity(intent);
            }
        });
    }

}
