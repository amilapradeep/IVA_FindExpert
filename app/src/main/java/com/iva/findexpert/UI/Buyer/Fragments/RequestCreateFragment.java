package com.iva.findexpert.UI.Buyer.Fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.google.gson.Gson;
import com.iva.findexpert.Common.Constant;
import com.iva.findexpert.DomainModel.User;
import com.iva.findexpert.R;
import com.iva.findexpert.Service.UserService;
import com.iva.findexpert.UI.Common.CustomDialog;
import com.iva.findexpert.UI.Common.Fragments.BaseFragment;
import com.iva.findexpert.UI.Common.LoginActivity;
import com.iva.findexpert.UI.Helpers.HttpClientHelper;
import com.iva.findexpert.UI.Helpers.IHttpResponse;
import com.iva.findexpert.UI.Helpers.ViewAnimation;
import com.iva.findexpert.Utility.Common;
import com.iva.findexpert.Utility.Session;
import com.iva.findexpert.ViewModel.LocationViewModel;
import com.iva.findexpert.ViewModel.QuotationRequestViewModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class RequestCreateFragment extends BaseFragment {

    private View expandedView = null;
    private QuotationRequestViewModel quotationRequest = new QuotationRequestViewModel();
    private User user;
    private String parentUI;
    ScrollView scrollView;
    List<LocationViewModel> locations;
    private Boolean spinnerTouched = false;

    public RequestCreateFragment() {
        // Required empty public constructor
    }

    public static RequestCreateFragment newInstance(String from) {
        RequestCreateFragment fragment = new RequestCreateFragment();
        Bundle args = new Bundle();
        args.putString(Constant.SessionKeys.PARENT_UI, from);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_request_create, container, false);
        setCurrentView(view);
        user = new UserService(getActivity()).GetCurrentUser();
        setCollapsiblePanels();
        setYearSpinner();
        setFieldEvents();
        checkForRequestInSession();
        getLocations();
        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            parentUI = getArguments().getString(Constant.SessionKeys.PARENT_UI);
        }
    }

    private void setCollapsiblePanels()
    {
        getCurrentView().findViewById(R.id.vehicleNoHeader).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View detailView = getCurrentView().findViewById(R.id.vehicleNoDetail);
                setExpandCollapse(detailView);
            }
        });

        getCurrentView().findViewById(R.id.insuranceTypeHeader).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View detailView = getCurrentView().findViewById(R.id.insuranceTypeDetail);
                setExpandCollapse(detailView);
            }
        });

        getCurrentView().findViewById(R.id.registrationCategoryHeader).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View detailView = getCurrentView().findViewById(R.id.registrationCategoryDetail);
                setExpandCollapse(detailView);
            }
        });

        getCurrentView().findViewById(R.id.usageHeader).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View detailView = getCurrentView().findViewById(R.id.usageDetail);
                setExpandCollapse(detailView);
            }
        });

        getCurrentView().findViewById(R.id.valueHeader).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View detailView = getCurrentView().findViewById(R.id.valueDetail);
                setExpandCollapse(detailView);
            }
        });

        getCurrentView().findViewById(R.id.yearHeader).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View detailView = getCurrentView().findViewById(R.id.yearDetail);
                setExpandCollapse(detailView);
            }
        });

        getCurrentView().findViewById(R.id.locationHeader).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View detailView = getCurrentView().findViewById(R.id.locationDetail);
                setExpandCollapse(detailView);
                scrollView = (ScrollView) getCurrentView().findViewById(R.id.pageScroll);
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
        });

        getCurrentView().findViewById(R.id.isFinancedHeader).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View detailView = getCurrentView().findViewById(R.id.isFinancedDetail);
                ((TextView) getCurrentView().findViewById(R.id.financedTextView)).setVisibility(View.VISIBLE);
                setExpandCollapse(detailView);
                scrollView = (ScrollView) getCurrentView().findViewById(R.id.pageScroll);
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
        });

        getCurrentView().findViewById(R.id.fuelTypeHeader).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View detailView = getCurrentView().findViewById(R.id.fuelTypeDetail);
                setExpandCollapse(detailView);
            }
        });

    }

    private void setExpandCollapse(View detailView)
    {
        if(detailView.getVisibility() == View.VISIBLE)
            ViewAnimation.collapse(detailView);
        else
        {
            if(expandedView != null)
                ViewAnimation.collapse(expandedView);
            ViewAnimation.expand(detailView);
            expandedView = detailView;
        }
        Common.hideKeyboard(getActivity());
    }

    ArrayList<String> years = new ArrayList<>();
    private void setYearSpinner()
    {
        //final ArrayList<String> years = new ArrayList<>();
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(new Date());
        int curYear = calendar.get(Calendar.YEAR) + 2;
        years.add("--Please Select Year--");
        for(int i = 0; i <= 22; i++ )
        {
            int val = curYear - i;
            years.add(String.valueOf(val));
        }

        Spinner yearSelector = (Spinner)getCurrentView().findViewById(R.id.yearSelector);
        ArrayAdapter adapter = new ArrayAdapter(getActivity(), android.R.layout.simple_spinner_dropdown_item, years);
        yearSelector.setAdapter(adapter);
        yearSelector.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(spinnerTouched) {
                    if(position > 0) {
                        TextView tv = ((TextView) getCurrentView().findViewById(R.id.yearTextView));
                        tv.setVisibility(View.VISIBLE);
                        tv.setText(years.get(position));
                        quotationRequest.VehicleYear = Integer.valueOf(years.get(position));
                        ViewAnimation.collapse(getCurrentView().findViewById(R.id.yearDetail));
                    }
                }
                spinnerTouched = true;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        yearSelector.setSelection(0);
    }

    private void setFieldEvents()
    {
        ((EditText) getCurrentView().findViewById(R.id.vehicleNoEditText)).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                TextView tv = ((TextView) getCurrentView().findViewById(R.id.vehicleNoTextView));
                tv.setVisibility(View.VISIBLE);
                tv.setText(((EditText) getCurrentView().findViewById(R.id.vehicleNoEditText)).getText());
                quotationRequest.VehicleNo = tv.getText().toString();
            }
        });

        AutoCompleteTextView actv = (AutoCompleteTextView) getCurrentView().findViewById(R.id.locationEditText);
        actv.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                TextView tv = ((TextView) getCurrentView().findViewById(R.id.locationTextView));
                tv.setVisibility(View.VISIBLE);
                tv.setText(s.toString());
                quotationRequest.Location = s.toString();
            }
        });

        ((RadioGroup) getCurrentView().findViewById(
                R.id.insuranceTypeRadioGroup)).setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                if(checkedId == R.id.optComprehensive)
                {
                    quotationRequest.ClaimType = 1;
                }
                else
                {
                    quotationRequest.ClaimType = 2;
                }
                TextView tv = ((TextView) getCurrentView().findViewById(R.id.insuranceTypeTextView));
                tv.setVisibility(View.VISIBLE);
                tv.setText(((TextView)getCurrentView().findViewById(checkedId)).getText());
                ViewAnimation.collapse(getCurrentView().findViewById(R.id.insuranceTypeDetail));
            }
        });

        ((RadioGroup) getCurrentView().findViewById(
                R.id.registraionTypeRadioGroup)).setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                switch (checkedId)
                {
                    case R.id.optMotorCar:
                        quotationRequest.RegistrationCategory = 1;
                        break;

                    case R.id.optDualPurpose:
                        quotationRequest.RegistrationCategory = 2;
                        break;

                    case R.id.optMotorLorry:
                        quotationRequest.RegistrationCategory = 3;
                        break;

                    case R.id.optMotorCycle:
                        quotationRequest.RegistrationCategory = 4;
                        break;

                    case R.id.optMotorThreeWheel:
                        quotationRequest.RegistrationCategory = 5;
                        break;

                    case R.id.optMotorBus:
                        quotationRequest.RegistrationCategory = 6;
                        break;

                    case R.id.optPrimeMover:
                        quotationRequest.RegistrationCategory = 7;
                        break;

                    case R.id.optLandVehicle:
                        quotationRequest.RegistrationCategory = 8;
                        break;

                    default:
                        break;
                }
                TextView tv = ((TextView) getCurrentView().findViewById(R.id.registrationTypeTextView));
                tv.setVisibility(View.VISIBLE);
                tv.setText(((TextView)getCurrentView().findViewById(checkedId)).getText());
                ViewAnimation.collapse(getCurrentView().findViewById(R.id.registrationCategoryDetail));
            }
        });

        ((RadioGroup) getCurrentView().findViewById(
                R.id.usageRadioGroup)).setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                switch (checkedId)
                {
                    case R.id.optPrivate:
                        quotationRequest.UsageType = Constant.Usage.PRIVATE;
                        break;

                    case R.id.optHiring:
                        quotationRequest.UsageType = Constant.Usage.HIRING;
                        break;

                    case R.id.optRent:
                        quotationRequest.UsageType = Constant.Usage.RENT;
                        break;

                    default:
                        break;
                }
                TextView tv = ((TextView) getCurrentView().findViewById(R.id.usageTextView));
                tv.setVisibility(View.VISIBLE);
                tv.setText(((TextView)getCurrentView().findViewById(checkedId)).getText());
                ViewAnimation.collapse(getCurrentView().findViewById(R.id.usageDetail));
            }
        });

        ((RadioGroup) getCurrentView().findViewById(
                R.id.fuelTypeRadioGroup)).setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                switch (checkedId)
                {
                    case R.id.optGas:
                        quotationRequest.FuelType = Constant.FuelType.GAS;
                        break;

                    case R.id.optHybrid:
                        quotationRequest.FuelType = Constant.FuelType.HYBRID;
                        break;

                    case R.id.optElectric:
                        quotationRequest.FuelType = Constant.FuelType.ELECTRIC;
                        break;

                    default:
                        break;
                }
                TextView tv = ((TextView) getCurrentView().findViewById(R.id.fuelTypeTextView));
                tv.setVisibility(View.VISIBLE);
                tv.setText(((TextView)getCurrentView().findViewById(checkedId)).getText());
                ViewAnimation.collapse(getCurrentView().findViewById(R.id.fuelTypeDetail));
            }
        });

        ((EditText) getCurrentView().findViewById(R.id.valueEditText)).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                TextView tv = ((TextView) getCurrentView().findViewById(R.id.valueTextView));
                tv.setVisibility(View.VISIBLE);

                String value = ((EditText) getCurrentView().findViewById(R.id.valueEditText)).getText().toString();
                double valued = 0;
                if(!TextUtils.isEmpty(value))
                {
                    valued = Double.valueOf(value);
                    value = Common.formatDecimal(valued, true);
                }
                tv.setText(value);
                quotationRequest.VehicleValue = valued;
            }
        });

        ((RadioGroup) getCurrentView().findViewById(
                R.id.financedRadioGroup)).setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                if(checkedId == R.id.optYes)
                {
                    quotationRequest.IsFinanced = true;
                }
                else
                {
                    quotationRequest.IsFinanced = false;
                }
                TextView tv = ((TextView) getCurrentView().findViewById(R.id.financedTextView));
                tv.setVisibility(View.VISIBLE);
                tv.setText(((TextView)getCurrentView().findViewById(checkedId)).getText());
                ViewAnimation.collapse(getCurrentView().findViewById(R.id.isFinancedDetail));
            }
        });

        getCurrentView().findViewById(R.id.createRequestButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validateSave())
                    confirmSave();
            }
        });
    }

    private void saveRequest()
    {
        user = new UserService(getActivity()).GetCurrentUser();
        quotationRequest.UserId = user.Id;
        quotationRequest.InsuranceTypeId = Session.getInt(getActivity(), Constant.SessionKeys.INSURANCE_TYPE);
        String jsonObj = new Gson().toJson(quotationRequest);

        Map<String,String> headerParams = new HashMap<>();
        headerParams.put("Authorization", "bearer " + user.Token );
        String url = Constant.BASE_URL + Constant.CONTROLLER_SERVICE_REQUEST + Constant.SERVICE_SR_SAVE;
        showProgress(getActivity());

        new HttpClientHelper().getResponseString(getActivity(), url, jsonObj, headerParams, new IHttpResponse() {
            @Override
            public void onSuccess(String result) {
                hideProgress();
                Toast.makeText(getActivity(), "Request sent successfully", Toast.LENGTH_LONG).show();
                openFragement(RequestListFragment.newInstance(user.Id), false);
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

    private boolean validateSave()
    {
        if(TextUtils.isEmpty(quotationRequest.VehicleNo))
        {
            View detailView = getCurrentView().findViewById(R.id.vehicleNoDetail);
            setExpandCollapse(detailView);
            getCurrentView().findViewById(R.id.vehicleNoEditText).requestFocus();
            showAlert(getActivity(), "Please add the vehicle registration number!");
            return false;
        }
        else
        {
            if(!Common.ValidVehicleNumber(quotationRequest.VehicleNo, getActivity()))
            {
                View detailView = getCurrentView().findViewById(R.id.vehicleNoDetail);
                setExpandCollapse(detailView);
                getCurrentView().findViewById(R.id.vehicleNoEditText).requestFocus();
                return false;
            }
        }
        if(quotationRequest.ClaimType == 0)
        {
            View detailView = getCurrentView().findViewById(R.id.insuranceTypeDetail);
            setExpandCollapse(detailView);
            showAlert(getActivity(), "Please select Insurance type!");
            return false;
        }
        if(quotationRequest.RegistrationCategory == 0)
        {
            View detailView = getCurrentView().findViewById(R.id.registrationCategoryDetail);
            setExpandCollapse(detailView);
            showAlert(getActivity(), "Please select vehicle registration category!");
            return false;
        }
        if(quotationRequest.FuelType == 0)
        {
            View detailView = getCurrentView().findViewById(R.id.fuelTypeDetail);
            setExpandCollapse(detailView);
            showAlert(getActivity(), "Please select vehicle fuel type!");
            return false;
        }
        if(quotationRequest.UsageType == 0)
        {
            View detailView = getCurrentView().findViewById(R.id.usageDetail);
            setExpandCollapse(detailView);
            showAlert(getActivity(), "Please select vehicle usage type!");
            return false;
        }
        if(quotationRequest.VehicleValue == 0)
        {
            View detailView = getCurrentView().findViewById(R.id.valueDetail);
            setExpandCollapse(detailView);
            showAlert(getActivity(), "Please add the vehicle value!");
            return false;
        }
        if(quotationRequest.VehicleYear == 0)
        {
            View detailView = getCurrentView().findViewById(R.id.yearDetail);
            setExpandCollapse(detailView);
            getCurrentView().findViewById(R.id.valueEditText).requestFocus();
            showAlert(getActivity(), "Please select vehicle manufactured year!");
            return false;
        }
        if(TextUtils.isEmpty(
                ((TextView)getCurrentView().findViewById(R.id.financedTextView)).getText().toString()))
        {
            View detailView = getCurrentView().findViewById(R.id.isFinancedDetail);
            setExpandCollapse(detailView);
            showAlert(getActivity(), "Please select financed or not!");
            return false;
        }

        /*if(TextUtils.isEmpty(
                ((TextView)getCurrentView().findViewById(R.id.locationTextView)).getText().toString()))
        {
            View detailView = getCurrentView().findViewById(R.id.locationDetail);
            setExpandCollapse(detailView);
            showAlert(getActivity(), "Please enter your location!");
            return false;
        }*/

        if(!checkUser())
            return false;

        return true;
    }

    private void confirmSave()
    {
      /*final CustomDialog dialog = showAlert(getActivity(), "Confirm Request",
              "Confirm the quotation request. You may not be able to cancel the request once created.");*/

        final CustomDialog dialog = new CustomDialog(getActivity(), R.layout.request_confirm_popup);
        ((TextView) dialog.findViewById(R.id.vehicleNo)).setText(
                ((TextView)getCurrentView().findViewById(R.id.vehicleNoTextView)).getText());
        ((TextView) dialog.findViewById(R.id.claimType)).setText(
                ((TextView)getCurrentView().findViewById(R.id.insuranceTypeTextView)).getText());
        ((TextView) dialog.findViewById(R.id.registrationCategory)).setText(
                ((TextView)getCurrentView().findViewById(R.id.registrationTypeTextView)).getText());
        ((TextView) dialog.findViewById(R.id.fuelType)).setText(
                ((TextView)getCurrentView().findViewById(R.id.fuelTypeTextView)).getText());
        ((TextView) dialog.findViewById(R.id.vehicleUsage)).setText(
                ((TextView)getCurrentView().findViewById(R.id.usageTextView)).getText());
        ((TextView) dialog.findViewById(R.id.vehicleValue)).setText(
                ((TextView)getCurrentView().findViewById(R.id.valueTextView)).getText());
        ((TextView) dialog.findViewById(R.id.vehicleYear)).setText(
                ((TextView)getCurrentView().findViewById(R.id.yearTextView)).getText());
        ((TextView) dialog.findViewById(R.id.vehicleFinanced)).setText(
                ((TextView)getCurrentView().findViewById(R.id.financedTextView)).getText());
        if(!TextUtils.isEmpty(quotationRequest.Location))
        {
            dialog.findViewById(R.id.locationContainer).setVisibility(View.VISIBLE);
            ((TextView) dialog.findViewById(R.id.vehicleLocation)).setText(
                    ((TextView)getCurrentView().findViewById(R.id.locationTextView)).getText());
        }


        dialog.OKButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                saveRequest();
            }
        });
        //dialog.CancelButton.setVisibility(View.VISIBLE);
        dialog.CancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private boolean checkUser()
    {
        if(user != null && user.IsAuthenticated)
            return true;
        else
        {
            final CustomDialog dialog = showAlert(getActivity(), "User Login", "You are not logged in to get this service." +
                    "Please login if you already have an account or you have to register first!");
            dialog.OKButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    String json = new Gson().toJson(quotationRequest);
                    Session.putString(getActivity(), Constant.SessionKeys.SERVICE_REQUEST, json );
                    dialog.dismiss();
                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                    startActivity(intent);
                }
            });
            dialog.CancelButton.setVisibility(View.VISIBLE);
            dialog.CancelButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
            return false;
        }
    }

    @Override
    public boolean onBackPressed() {
        User user = new UserService(getActivity()).GetCurrentUser();
        if(user != null)
        {
            if(parentUI.equals(Constant.ParentUIName.FROM_HOME))
                openFragement(BuyerHomeFragment.newInstance(), false);
            else if(parentUI.equals(Constant.ParentUIName.FROM_INQUIRY_LIST))
                openFragement(RequestListFragment.newInstance(user.Id), false);
        }
        else
        {
            openFragement(BuyerHomeFragment.newInstance(), false);
        }

        return false;
    }

    private void checkForRequestInSession()
    {
        String json = Session.getString(getActivity(), Constant.SessionKeys.SERVICE_REQUEST);
        Session.remove(getActivity(), Constant.SessionKeys.SERVICE_REQUEST);
        if(!TextUtils.isEmpty(json))
        {
            quotationRequest = new Gson().fromJson(json, QuotationRequestViewModel.class);

            if(quotationRequest == null)
                return;

            ((TextView) getCurrentView().findViewById(R.id.vehicleNoTextView)).setText(quotationRequest.VehicleNo);
            ((EditText) getCurrentView().findViewById(R.id.vehicleNoEditText)).setText(quotationRequest.VehicleNo);

            if(quotationRequest.ClaimType == 1)
            {
                ((RadioButton) getCurrentView().findViewById(R.id.optComprehensive)).setChecked(true);
                ((TextView) getCurrentView().findViewById(R.id.insuranceTypeTextView)).setText(((RadioButton) getCurrentView().findViewById(R.id.optComprehensive)).getText());
            }
            else
            {
                ((RadioButton) getCurrentView().findViewById(R.id.optthirdParty)).setChecked(true);
                ((TextView) getCurrentView().findViewById(R.id.insuranceTypeTextView)).setText(((RadioButton) getCurrentView().findViewById(R.id.optthirdParty)).getText());
            }

            switch (quotationRequest.RegistrationCategory)
            {
                case 1:
                    ((RadioButton) getCurrentView().findViewById(R.id.optMotorCar)).setChecked(true);
                    ((TextView) getCurrentView().findViewById(R.id.registrationTypeTextView)).setText(((RadioButton) getCurrentView().findViewById(R.id.optMotorCar)).getText());
                    break;
                case 2:
                    ((RadioButton) getCurrentView().findViewById(R.id.optDualPurpose)).setChecked(true);
                    ((TextView) getCurrentView().findViewById(R.id.registrationTypeTextView)).setText(((RadioButton) getCurrentView().findViewById(R.id.optDualPurpose)).getText());
                    break;
                case 3:
                    ((RadioButton) getCurrentView().findViewById(R.id.optMotorLorry)).setChecked(true);
                    ((TextView) getCurrentView().findViewById(R.id.registrationTypeTextView)).setText(((RadioButton) getCurrentView().findViewById(R.id.optMotorLorry)).getText());
                    break;
                case 4:
                    ((RadioButton) getCurrentView().findViewById(R.id.optMotorCycle)).setChecked(true);
                    ((TextView) getCurrentView().findViewById(R.id.registrationTypeTextView)).setText(((RadioButton) getCurrentView().findViewById(R.id.optMotorCycle)).getText());
                    break;
                case 5:
                    ((RadioButton) getCurrentView().findViewById(R.id.optMotorThreeWheel)).setChecked(true);
                    ((TextView) getCurrentView().findViewById(R.id.registrationTypeTextView)).setText(((RadioButton) getCurrentView().findViewById(R.id.optMotorThreeWheel)).getText());
                    break;

                case 6:
                    ((RadioButton) getCurrentView().findViewById(R.id.optMotorBus)).setChecked(true);
                    ((TextView) getCurrentView().findViewById(R.id.registrationTypeTextView)).setText(((RadioButton) getCurrentView().findViewById(R.id.optMotorBus)).getText());
                    break;
                case 7:
                    ((RadioButton) getCurrentView().findViewById(R.id.optPrimeMover)).setChecked(true);
                    ((TextView) getCurrentView().findViewById(R.id.registrationTypeTextView)).setText(((RadioButton) getCurrentView().findViewById(R.id.optPrimeMover)).getText());
                    break;
                case 8:
                    ((RadioButton) getCurrentView().findViewById(R.id.optLandVehicle)).setChecked(true);
                    ((TextView) getCurrentView().findViewById(R.id.registrationTypeTextView)).setText(((RadioButton) getCurrentView().findViewById(R.id.optLandVehicle)).getText());
                    break;
                default:
                    break;
            }

            switch (quotationRequest.FuelType)
            {
                case Constant.FuelType.GAS:
                    ((RadioButton) getCurrentView().findViewById(R.id.optGas)).setChecked(true);
                    ((TextView) getCurrentView().findViewById(R.id.fuelTypeTextView)).setText(R.string.fuel_type_gas);
                    break;
                case Constant.Usage.HIRING:
                    ((RadioButton) getCurrentView().findViewById(R.id.optHybrid)).setChecked(true);
                    ((TextView) getCurrentView().findViewById(R.id.fuelTypeTextView)).setText(R.string.fuel_type_hybrid);
                    break;
                case Constant.Usage.RENT:
                    ((RadioButton) getCurrentView().findViewById(R.id.optElectric)).setChecked(true);
                    ((TextView) getCurrentView().findViewById(R.id.fuelTypeTextView)).setText(R.string.fuel_type_electric);
                    break;
                default:
                    break;
            }

            switch (quotationRequest.UsageType)
            {
                case Constant.Usage.PRIVATE:
                    ((RadioButton) getCurrentView().findViewById(R.id.optPrivate)).setChecked(true);
                    ((TextView) getCurrentView().findViewById(R.id.usageTextView)).setText(((RadioButton) getCurrentView().findViewById(R.id.optPrivate)).getText());
                    break;
                case Constant.Usage.HIRING:
                    ((RadioButton) getCurrentView().findViewById(R.id.optHiring)).setChecked(true);
                    ((TextView) getCurrentView().findViewById(R.id.usageTextView)).setText(((RadioButton) getCurrentView().findViewById(R.id.optHiring)).getText());
                    break;
                case Constant.Usage.RENT:
                    ((RadioButton) getCurrentView().findViewById(R.id.optRent)).setChecked(true);
                    ((TextView) getCurrentView().findViewById(R.id.usageTextView)).setText(((RadioButton) getCurrentView().findViewById(R.id.optRent)).getText());
                    break;
                default:
                    break;
            }
            ((EditText) getCurrentView().findViewById(R.id.valueEditText)).setText(String.valueOf(quotationRequest.VehicleValue));
            ((TextView) getCurrentView().findViewById(R.id.valueTextView)).setText(Common.formatDecimal(quotationRequest.VehicleValue, true));

            int pos = 0;
            for(int i = 1; i < years.size(); i++)
            {
                int year = Integer.valueOf(years.get(i));
                if(year == quotationRequest.VehicleYear)
                    pos = i;
            }
            ((Spinner) getCurrentView().findViewById(R.id.yearSelector)).setSelection(pos);
            ((TextView) getCurrentView().findViewById(R.id.yearTextView)).setText(String.valueOf(quotationRequest.VehicleYear));
            getCurrentView().findViewById(R.id.yearTextView).setVisibility(View.VISIBLE);

            if(quotationRequest.IsFinanced)
            {
                ((RadioButton) getCurrentView().findViewById(R.id.optYes)).setChecked(true);
                ((TextView) getCurrentView().findViewById(R.id.financedTextView)).setText(((RadioButton) getCurrentView().findViewById(R.id.optYes)).getText());
            }
            else
            {
                ((RadioButton) getCurrentView().findViewById(R.id.optNo)).setChecked(true);
                ((TextView) getCurrentView().findViewById(R.id.financedTextView)).setText(((RadioButton) getCurrentView().findViewById(R.id.optNo)).getText());
            }
        }
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
                    locations = Arrays.asList(gson.fromJson(result, LocationViewModel[].class ));
                    if(locations != null && locations.size() > 0)
                    {
                        final String[] locArray = new String[locations.size()];
                        for(int i = 0; i < locations.size(); i++)
                        {
                            locArray[i] = locations.get(i).Name;
                        }
                        ArrayAdapter<String> adapter = new ArrayAdapter(getActivity(), R.layout.list_popup_item, locArray);
                        AutoCompleteTextView actv = (AutoCompleteTextView) getCurrentView().findViewById(R.id.locationEditText);
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
