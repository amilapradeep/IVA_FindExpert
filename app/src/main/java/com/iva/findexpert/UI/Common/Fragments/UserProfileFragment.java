package com.iva.findexpert.UI.Common.Fragments;


import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListPopupWindow;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.google.gson.Gson;
import com.iva.findexpert.Common.Constant;
import com.iva.findexpert.DomainModel.User;
import com.iva.findexpert.DomainModel.UserProfile;
import com.iva.findexpert.R;
import com.iva.findexpert.Service.UserProfileService;
import com.iva.findexpert.Service.UserService;
import com.iva.findexpert.UI.Agent.Fragments.AgentHomeFragment;
import com.iva.findexpert.UI.Buyer.Fragments.BuyerHomeFragment;
import com.iva.findexpert.UI.Common.CustomDialog;
import com.iva.findexpert.UI.Helpers.HttpClientHelper;
import com.iva.findexpert.UI.Helpers.IHttpResponse;
import com.iva.findexpert.Utility.Common;
import com.iva.findexpert.ViewModel.LocationViewModel;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class UserProfileFragment extends BaseFragment {

    private User user;
    private UserProfile profile;
    private ListPopupWindow listPopup;
    private boolean listenForChanges = false;

    public UserProfileFragment() {
        // Required empty public constructor
    }

    public static UserProfileFragment newInstance()
    {
        return new UserProfileFragment();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view  = inflater.inflate(R.layout.fragment_user_profile, container, false);
        setCurrentView(view);
        setButtons();
        setUser();
        setUserProfile();
        getLocations();
        return view;
    }

    private void setButtons()
    {
        ((Button) getCurrentView().findViewById(R.id.saveProfileButton)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveProfile();
            }
        });
    }

    private void setUser()
    {
        user = new UserService(getActivity()).GetCurrentUser();
        ((EditText)getCurrentView().findViewById(R.id.firstName)).setText(user.UserName);
        ((EditText)getCurrentView().findViewById(R.id.mobile)).setText(user.Phone);
        displayUserProfile();
    }

    private void setUserProfile()
    {
        String url = Constant.BASE_URL + Constant.CONTROLLER_USER + Constant.SERVICE_GET_PROFILE;
        Map<String, String> params = new HashMap<>();
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
                    profile = gson.fromJson(result, UserProfile.class );

                    if(profile != null)
                    {
                        UserProfileService service = new UserProfileService(getActivity());
                        service.DeleteAll();
                        service.Insert(profile);
                        displayUserProfile();
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

    private boolean validateFields()
    {
        if(profile == null)
            profile = new UserProfile();
        profile.FirstName = ((TextView) getCurrentView().findViewById(R.id.firstName)).getText().toString().trim();
        profile.LastName = ((TextView) getCurrentView().findViewById(R.id.lastName)).getText().toString().trim();
        profile.Email = ((TextView) getCurrentView().findViewById(R.id.email)).getText().toString().trim();
        profile.Phone = ((TextView) getCurrentView().findViewById(R.id.telephone)).getText().toString().trim();
        profile.Mobile = ((TextView) getCurrentView().findViewById(R.id.mobile)).getText().toString().trim();
        profile.Street = ((TextView) getCurrentView().findViewById(R.id.street)).getText().toString().trim();
        profile.City = ((TextView) getCurrentView().findViewById(R.id.city)).getText().toString().trim();

        if(((RadioGroup) getCurrentView().findViewById(R.id.genderRadioGroup)).getCheckedRadioButtonId() == R.id.male)
            profile.Gender = 0;
        else
            profile.Gender = 1;

        int checkedId = ((RadioGroup) getCurrentView().findViewById(R.id.contactMethodRadioGroup)).getCheckedRadioButtonId();
        switch (checkedId)
        {
            case R.id.optBoth:
                profile.ContactMethod = 0;
                break;
            case R.id.optPhone:
                profile.ContactMethod = 1;
                break;
            case R.id.optMessage:
                profile.ContactMethod = 2;
                break;
            default:
                profile.ContactMethod = 0;
                break;
        }

        if(TextUtils.isEmpty(profile.FirstName))
        {
            showAlert(getActivity(), "First name is required.");
            return false;
        }
        if(!Common.ValidName(profile.FirstName))
        {
            showAlert(getActivity(), "Invalid characters in first name");
            return false;
        }


        if(TextUtils.isEmpty(profile.LastName))
        {
            showAlert(getActivity(), "Last name is required.");
            return false;
        }

        if(!Common.ValidName(profile.LastName))
        {
            showAlert(getActivity(), "Invalid characters in last name");
            return false;
        }

        if(!Common.ValidEmail(profile.Email, getActivity()))
        {
            getCurrentView().findViewById(R.id.email).requestFocus();
            return false;
        }

        if(!Common.ValidName(profile.City))
        {
            showAlert(getActivity(), "Invalid characters in city name");
            return false;
        }

        if(!Common.ValidAddress(profile.Street, getActivity()))
            return false;

        return true;
    }

    private void saveProfile()
    {
        if(validateFields())
        {
            profile.UserId = user.Id;
            String jsonObj = new Gson().toJson(profile);
            Map<String,String> headerParams = new HashMap<>();
            headerParams.put("Authorization", "bearer " + user.Token );
            String url = Constant.BASE_URL + Constant.CONTROLLER_USER + Constant.SERVICE_UPDATE_PROFILE;
            showProgress(getActivity());

            new HttpClientHelper().getResponseString(getActivity(), url, jsonObj, headerParams, new IHttpResponse() {
                @Override
                public void onSuccess(String result) {
                    hideProgress();
                    ((TextView) getActivity().findViewById(R.id.profileName)).setText(profile.FirstName + " " + profile.LastName);
                    Toast.makeText(getActivity(), "Profile saved successfully", Toast.LENGTH_LONG).show();
                }

                @Override
                public void onError(int error) {
                    hideProgress();
                    showAlert(getActivity(), "Error", "Error occurred while saving the profile. \n Please try again.");
                }

                @Override
                public void onNoInternetConnection(){ hideProgress();}
            });
        }
    }

    private void displayUserProfile()
    {
        if(profile == null)
            return;

        ((TextView) getCurrentView().findViewById(R.id.firstName)).setText(profile.FirstName);
        ((TextView) getCurrentView().findViewById(R.id.lastName)).setText(profile.LastName);
        ((TextView) getCurrentView().findViewById(R.id.telephone)).setText(profile.Phone);
        ((TextView) getCurrentView().findViewById(R.id.mobile)).setText(user.Phone);
        ((TextView) getCurrentView().findViewById(R.id.email)).setText(profile.Email);
        ((TextView) getCurrentView().findViewById(R.id.street)).setText(profile.Street);
        ((TextView) getCurrentView().findViewById(R.id.city)).setText(profile.City);
        listenForChanges = true;

        if(profile.Gender == 0)
            ((RadioGroup) getCurrentView().findViewById(R.id.genderRadioGroup)).check(R.id.male);
        else
            ((RadioGroup) getCurrentView().findViewById(R.id.genderRadioGroup)).check(R.id.female);

        switch (profile.ContactMethod)
        {
            case 1:
                ((RadioGroup) getCurrentView().findViewById(R.id.contactMethodRadioGroup)).check(R.id.optPhone);
                break;
            case 2:
                ((RadioGroup) getCurrentView().findViewById(R.id.contactMethodRadioGroup)).check(R.id.optMessage);
                break;
            case 3:
                ((RadioGroup) getCurrentView().findViewById(R.id.contactMethodRadioGroup)).check(R.id.optBoth);
                break;
            default:
                ((RadioGroup) getCurrentView().findViewById(R.id.contactMethodRadioGroup)).check(R.id.optBoth);
                break;

        }
    }

    @Override
   public boolean onBackPressed()
   {
       if(profile == null) {
           gotoParent();
           return true;
       }

       if(!checkDirty())
       {
           final CustomDialog confirm = showAlert(getActivity(), "Profile Update", "Profile changed. \nPlease save the profile using save button before exiting the form");
           confirm.OKButton.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {
                   confirm.dismiss();
               }
           });
           confirm.CancelButton.setVisibility(View.VISIBLE);
           confirm.CancelButton.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {
                   gotoParent();
                   confirm.dismiss();
               }
           });
           return false;
       }
       gotoParent();
       return false;
   }

    private void gotoParent()
    {
        User user = new UserService(getActivity()).GetCurrentUser();
        if(user.Type == Constant.UserType.SELLER)
            openFragement(AgentHomeFragment.newInstance(), false);
        else
            openFragement(BuyerHomeFragment.newInstance(), false);
    }

    private boolean checkDirty()
    {
        if(!profile.FirstName.equals(((TextView) getCurrentView().findViewById(R.id.firstName)).getText().toString().trim()))
            return false;
        if(!profile.LastName.equals(((TextView) getCurrentView().findViewById(R.id.lastName)).getText().toString().trim()))
            return false;
        if(!profile.Phone.equals(((TextView) getCurrentView().findViewById(R.id.telephone)).getText().toString().trim()))
            return false;
        if(!profile.Mobile.equals(((TextView) getCurrentView().findViewById(R.id.mobile)).getText().toString().trim()))
            return false;
        if(!profile.Email.equals(((TextView) getCurrentView().findViewById(R.id.email)).getText().toString().trim()))
            return false;
        if(!profile.Street.equals(((TextView) getCurrentView().findViewById(R.id.street)).getText().toString().trim()))
            return false;
        if(!profile.City.equals(((TextView) getCurrentView().findViewById(R.id.city)).getText().toString().trim()))
            return false;

        int gender = 0;
        if(((RadioGroup) getCurrentView().findViewById(R.id.genderRadioGroup)).getCheckedRadioButtonId() == R.id.male)
           gender = 0;
        else
            gender = 1;
        if(profile.Gender != gender)
            return false;

        int checkedId = ((RadioGroup) getCurrentView().findViewById(R.id.contactMethodRadioGroup)).getCheckedRadioButtonId();
        int contactMethod = 0;
        switch (checkedId)
        {
            case R.id.optBoth:
                contactMethod = 0;
                break;
            case R.id.optPhone:
                contactMethod = 1;
                break;
            case R.id.optMessage:
                contactMethod = 2;
                break;
            default:
                contactMethod = 0;
                break;
        }

        if(profile.ContactMethod != contactMethod)
            return false;

        return true;
    }

    private void getLocations()
    {
        String url = Constant.BASE_URL + "/api/default/GetCities";
        Map<String, String> params = new HashMap<>();
        params.put("Text", Common.urlEncode(""));

        new HttpClientHelper().getResponseStringURLEncoded(getActivity(), url, Request.Method.GET, params, null, new IHttpResponse() {
            @Override
            public void onSuccess(String result) {
                if(result != null)
                {
                    Gson gson = new Gson();
                    List<LocationViewModel> locations = Arrays.asList(gson.fromJson(result, LocationViewModel[].class ));
                    if(locations != null && locations.size() > 0)
                    {
                        final String[] locArray = new String[locations.size()];
                        for(int i = 0; i < locations.size(); i++)
                        {
                            locArray[i] = locations.get(i).Name;
                        }
                        ArrayAdapter<String> adapter = new ArrayAdapter(getActivity(), R.layout.list_popup_item, locArray);
                        AutoCompleteTextView actv = (AutoCompleteTextView) getCurrentView().findViewById(R.id.city);
                        actv.setThreshold(1);
                        actv.setAdapter(adapter);
                    }
                }

            }

            @Override
            public void onError(int error) {
                //showAlert(getActivity(), "Error communicating the server!");
            }

            @Override
            public void onNoInternetConnection(){}
        });
    }

}
