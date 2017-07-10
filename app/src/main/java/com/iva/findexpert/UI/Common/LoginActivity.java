package com.iva.findexpert.UI.Common;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

import com.android.volley.Request;
import com.google.firebase.FirebaseApp;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.Gson;
import com.iva.findexpert.Common.Constant;
import com.iva.findexpert.DomainModel.User;
import com.iva.findexpert.R;
import com.iva.findexpert.Service.UserService;
import com.iva.findexpert.UI.Buyer.ValidateCodeActivity;
import com.iva.findexpert.UI.Helpers.HttpClientHelper;
import com.iva.findexpert.UI.Helpers.IHttpResponse;
import com.iva.findexpert.UI.Agent.AgentHomeActivity;
import com.iva.findexpert.Utility.Common;
import com.iva.findexpert.Utility.Session;
import com.iva.findexpert.ViewModel.UserViewModel;

import java.util.HashMap;
import java.util.Map;

import static com.iva.findexpert.Utility.Network.IsConnectedToInternet;

public class LoginActivity extends BaseActivity {

    private View clientContainer;
    private View agentContainer;
    private TextView selectorText;
    private Switch selector;

    private String clientName;
    private String clientPhone;
    private String agentPhone;
    private String agentPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setToolbar();

        clientContainer = findViewById(R.id.clientContainer);
        agentContainer = findViewById(R.id.agentContainer);
        selectorText = (TextView) findViewById(R.id.selectorText);
        setSwitch();
        setButton();
        setContactButton();
        setUserPhoneFromSession();
        setTermsAndConditionsButton();
    }

    private void setSwitch()
    {
        selector = (Switch) findViewById(R.id.clientAgentSelector);
        selector.setChecked(false);
        selector.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setViews(!selector.isChecked());
            }
        });

        selector.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                setViews(!isChecked);
            }
        });
    }

    private void setViews(boolean isClient)
    {
        if(isClient)
        {
            selectorText.setText("Client");
            clientContainer.setVisibility(View.VISIBLE);
            agentContainer.setVisibility(View.GONE);
        }
        else
        {
            selectorText.setText("Agent");
            clientContainer.setVisibility(View.GONE);
            agentContainer.setVisibility(View.VISIBLE);
        }
    }

    private void setButton()
    {
        Button submit = (Button) findViewById(R.id.submitButton);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showProgress(LoginActivity.this);
                if(!selector.isChecked())
                {
                    if(validateClient())
                    {
                        if(!IsConnectedToInternet(LoginActivity.this))
                        {
                            showAlert(LoginActivity.this, "There is no internet connection available");
                            hideProgress();
                            return;
                        }
                        verifyClientPhone();
                    }
                    else
                        hideProgress();
                }
                else
                {
                    if(validateAgent())
                    {
                        if(!IsConnectedToInternet(LoginActivity.this))
                        {
                            showAlert(LoginActivity.this, "There is no internet connection available");
                            hideProgress();
                            return;
                        }
                        verifyAgent();
                    }
                    else
                        hideProgress();
                }
            }
        });
    }

    private boolean validateClient()
    {
        clientName = ((EditText)findViewById(R.id.clientNameTextBox)).getText().toString();
        clientPhone = ((EditText) findViewById(R.id.clientPhoneTextBox)).getText().toString();

        if(TextUtils.isEmpty(clientName))
        {
            showAlert(this, "Please enter a valid name.");
            return false;
        }

        if(!Common.ValidName(clientName))
        {
            showAlert(this, "Invalid characters in name");
            return false;
        }

        if(TextUtils.isEmpty(clientPhone))
        {
            showAlert(this, "Please enter a valid phone number.");
            return false;
        }
        else
        {
            if(clientPhone.substring(0, 1).equals("0"))
            {
                if(clientPhone.length() < Constant.PHONE_NUMBER_LENGTH)
                {
                    showAlert(this, "Please enter the phone number without leading '0'");
                    return false;
                }
                clientPhone = clientPhone.substring(1);
            }
            else if (clientPhone.length() != (Constant.PHONE_NUMBER_LENGTH - 1))
            {
                if(clientPhone.length() != (Constant.PHONE_NUMBER_LENGTH - 1))
                {
                    showAlert(this, "Please enter a valid phone number.");
                    return false;
                }
            }
        }
        return true;
    }

    private  void verifyClientPhone()
    {
        try {
            //clientName = ((EditText)findViewById(R.id.clientNameTextBox)).getText().toString();
            //clientPhone = ((EditText) findViewById(R.id.clientPhoneTextBox)).getText().toString();
            Session.putString(this, Constant.SessionKeys.USER_PHONE, clientPhone);
            clientPhone = "94" + clientPhone;

            Map<String, String> params = new HashMap<>();
            params.put("Name", clientName);
            params.put("Phone", clientPhone);
            String url = Constant.BASE_URL + Constant.CONTROLLER_BUYER + Constant.SERVICE_PHONE_VALIDATE;

            HttpClientHelper httpClientHelper = new HttpClientHelper();
            httpClientHelper.getResponseString(this, url,  Request.Method.POST, params, null, new IHttpResponse() {
                @Override
                public void onSuccess(String result) {
                    hideProgress();

                    UserService userService = new UserService(getApplicationContext());
                    userService.DeleteAll();
                    User user = new User();
                    user.UserName = clientName;
                    user.Phone = clientPhone;
                    user.Type = Constant.UserType.BUYER;
                    user.IsAuthenticated = false;
                    userService.Insert(user);

                    Intent page = new Intent(LoginActivity.this, ValidateCodeActivity.class);
                    startActivity(page);
                }

                @Override
                public void onError(int error) {
                    hideProgress();
                    showAlert(LoginActivity.this, "Error communicating the server!");
                }

                @Override
                public void onNoInternetConnection(){ hideProgress();}
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean validateAgent()
    {
        agentPhone = ((EditText)findViewById(R.id.agentPhoneTextBox)).getText().toString();
        agentPassword = ((EditText) findViewById(R.id.agentPasswordTextBox)).getText().toString();

        if(TextUtils.isEmpty(agentPhone))
        {
            showAlert(this, "Please enter a valid phone number.");
            return false;
        }

        if(TextUtils.isEmpty(agentPassword))
        {
            showAlert(this, "Please enter the password.");
            return false;
        }
        else
        {
            if(agentPhone.substring(0, 1).equals("0"))
            {
                if(agentPhone.length() < Constant.PHONE_NUMBER_LENGTH)
                {
                    showAlert(this, "Please enter the phone number without leading '0'");
                    return false;
                }
                agentPhone = agentPhone.substring(1);
            }
            else if (agentPhone.length() != (Constant.PHONE_NUMBER_LENGTH - 1))
            {
                if(agentPhone.length() != (Constant.PHONE_NUMBER_LENGTH - 1))
                {
                    showAlert(this, "Please enter a valid phone number.");
                    return false;
                }
            }
        }
        return true;
    }

    private  void verifyAgent()
    {
        try
        {
            String url = Constant.BASE_URL + Constant.CONTROLLER_SELLER + Constant.SERVICE_AGENT_VALIDATE;
            agentPassword = ((EditText)findViewById(R.id.agentPasswordTextBox)).getText().toString();
            agentPhone = ((EditText) findViewById(R.id.agentPhoneTextBox)).getText().toString();
            Session.putString(this, Constant.SessionKeys.AGENT_USER_PHONE, agentPhone);
            agentPhone = "94" + agentPhone;

            Map<String, String> params = new HashMap<>();
            params.put("Password", agentPassword);
            params.put("Name", "Agent");
            params.put("Phone", agentPhone);
            params.put("UserType", String.valueOf(Constant.UserType.SELLER));

            new HttpClientHelper().getResponseString(LoginActivity.this, url,  Request.Method.POST, params, null, new IHttpResponse() {
                @Override
                public void onSuccess(String result) {
                    hideProgress();
                    if(result != null)
                    {
                        Gson gson = new Gson();
                        UserViewModel userModel = gson.fromJson(result, UserViewModel.class);
                        if(userModel != null)
                        {
                            User user = new User();
                            user.Id = userModel.Id;
                            user.IsAuthenticated = userModel.PasswordValidated;
                            user.ConnectionId = userModel.ConnectionId;
                            user.Token = userModel.Token;
                            user.Password = userModel.Password;
                            user.UserName = userModel.Name;
                            user.Phone = agentPhone;
                            user.Type = Constant.UserType.SELLER;
                            user.RemoteId = userModel.LoginId;
                            user.ProfileId = userModel.ProfileId;
                            user.CompanyId = userModel.CompanyId;

                            UserService userService = new UserService(getApplicationContext());
                            userService.DeleteAll();
                            userService.Insert(user);
                            Intent intent = new Intent(LoginActivity.this, AgentHomeActivity.class);
                            startActivity(intent);

                            //String token = FirebaseInstanceId.getInstance().getToken();
                            //new CloudMessagingHelper(LoginActivity.this).sendRegistrationToServer(token);
                        }
                    }
                }

                @Override
                public void onError(int error) {
                    hideProgress();
                    showAlert(LoginActivity.this, "Error communicating the server. Check your credentials!");
                }

                @Override
                public void onNoInternetConnection(){ hideProgress();}
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setUserPhoneFromSession()
    {
        String phone = Session.getString(this, Constant.SessionKeys.USER_PHONE);
        String aphone = Session.getString(this, Constant.SessionKeys.AGENT_USER_PHONE);
        if(!TextUtils.isEmpty(phone))
            ((EditText) findViewById(R.id.clientPhoneTextBox)).setText(phone);
        if(!TextUtils.isEmpty(aphone))
            ((EditText) findViewById(R.id.agentPhoneTextBox)).setText(aphone);
    }

    private void setContactButton()
    {
        ((TextView) findViewById(R.id.contactPhone)).setText(Html.fromHtml(getString(R.string.login_help)));
        findViewById(R.id.contactPhone).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //String mobileNo = ((TextView) v).getText().toString();
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + "0771234567" ));
                startActivity(intent);
            }
        });
    }

    private void setTermsAndConditionsButton()
    {
        TextView terms = (TextView) findViewById(R.id.termsnCondtions);
        terms.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
        terms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent termsnConditions = new Intent(LoginActivity.this, TermsAndConditionsActivity.class);
                startActivity(termsnConditions);
            }
        });

    }

}
