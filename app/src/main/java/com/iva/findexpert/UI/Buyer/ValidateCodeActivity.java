package com.iva.findexpert.UI.Buyer;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.google.gson.Gson;
import com.iva.findexpert.Common.Constant;
import com.iva.findexpert.DomainModel.User;
import com.iva.findexpert.R;
import com.iva.findexpert.Service.UserService;
import com.iva.findexpert.UI.Common.BaseActivity;
import com.iva.findexpert.UI.Common.LoginActivity;
import com.iva.findexpert.UI.Helpers.HttpClientHelper;
import com.iva.findexpert.UI.Helpers.IHttpResponse;
import com.iva.findexpert.Utility.Common;
import com.iva.findexpert.ViewModel.UserViewModel;

import java.util.HashMap;
import java.util.Map;

import static com.iva.findexpert.Utility.Network.IsConnectedToInternet;

public class ValidateCodeActivity extends BaseActivity {

    String authCode = "";
    User currentUser = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_validate_code);

        setCurrentUser();
        setToolbar();
        setButtons();
        setCodeBoxes();
        setContactButton();
    }

    private void setCurrentUser()
    {
        currentUser = new UserService(getApplicationContext()).GetCurrentUser();
        if(currentUser != null)
            ((TextView) findViewById(R.id.phoneTextBox)).setText(currentUser.Phone);
        else
        {
            showAlert(this, "Not a valid user");
        }
    }

    private void setButtons()
    {
        findViewById(R.id.submitButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findViewById(R.id.charOne).clearFocus();
                findViewById(R.id.charTwo).clearFocus();
                findViewById(R.id.charThree).clearFocus();
                findViewById(R.id.charFour).clearFocus();
                Common.hideKeyboard(ValidateCodeActivity.this);

                showProgress(ValidateCodeActivity.this);
                authCode = ((EditText) findViewById(R.id.charOne)).getText().toString() +
                        ((EditText) findViewById(R.id.charTwo)).getText().toString() +
                        ((EditText) findViewById(R.id.charThree)).getText().toString() +
                        ((EditText) findViewById(R.id.charFour)).getText().toString();
                if(currentUser != null)
                {
                    if(!IsConnectedToInternet(ValidateCodeActivity.this))
                    {
                        showAlert(ValidateCodeActivity.this, "There is no internet connection available");
                        return;
                    }

                    if(validate())
                        verifyCode();
                }
            }
        });

        findViewById(R.id.newCodeButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ValidateCodeActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
    }

    private  void verifyCode()
    {
        try
        {
            String url = Constant.BASE_URL + Constant.CONTROLLER_BUYER + Constant.SERVICE_CODE_VALIDATE;
            Map<String, String> params = new HashMap<>();
            params.put("Password", authCode);
            params.put("Name", currentUser.UserName);
            params.put("Phone", currentUser.Phone);
            params.put("UserType", String.valueOf(currentUser.Type));
            params.put("ClientType", "1");

            new HttpClientHelper().getResponseString(ValidateCodeActivity.this, url, Request.Method.POST, params, null, new IHttpResponse() {
                @Override
                public void onSuccess(String result) {
                    hideProgress();

                    if(result != null)
                    {
                        Gson gson = new Gson();
                        UserViewModel userModel = gson.fromJson(result, UserViewModel.class);

                        if(userModel != null)
                        {
                            try {
                                User user = new User();
                                user.Id = userModel.Id;
                                user.IsAuthenticated = userModel.PasswordValidated;
                                user.ConnectionId = userModel.ConnectionId;
                                user.Token = userModel.Token;
                                user.Password = userModel.Password;
                                user.UserName = userModel.Name;
                                user.Phone = userModel.UserName;
                                user.Type = Constant.UserType.BUYER;
                                user.RemoteId = userModel.LoginId;
                                user.ProfileId = userModel.ProfileId;
                                user.CompanyId = userModel.CompanyId;

                                UserService userService = new UserService(getApplicationContext());
                                userService.DeleteAll();
                                userService.Insert(user);

                                Intent page = new Intent(ValidateCodeActivity.this, BuyerHomeActivity.class);
                                startActivity(page);
                            }
                            catch (Exception e)
                            {
                                Log.e("loginError", e.getMessage());
                                Toast.makeText(ValidateCodeActivity.this, "Error in login", Toast.LENGTH_LONG).show();
                            }

                        }
                    }
                }

                @Override
                public void onError(int error) {
                    hideProgress();
                    if(error == Constant.HttpStatus.UNAUTHORIZED)
                        showAlert(ValidateCodeActivity.this, "Invalid code entered. Please check the code again");
                    else
                        showAlert(ValidateCodeActivity.this, "Error communicating the server!");
                }

                @Override
                public void onNoInternetConnection(){ hideProgress();}
            });

           } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setCodeBoxes()
    {
        final EditText box1 = (EditText) findViewById(R.id.charOne);
        final EditText box2 = (EditText) findViewById(R.id.charTwo);
        final EditText box3 = (EditText) findViewById(R.id.charThree);
        final EditText box4 = (EditText) findViewById(R.id.charFour);
        box1.requestFocus();

        //InputMethodManager imm = (InputMethodManager)   getSystemService(Context.INPUT_METHOD_SERVICE);
        //imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);

        box1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(count == 0)
                    box1.requestFocus();
                if(count > 1)
                    box2.requestFocus();
            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.toString().equals(""))
                    box1.requestFocus();
                else
                    box2.requestFocus();
            }
        });
        box2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(count == 0)
                    box1.requestFocus();
            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.toString().equals(""))
                    box1.requestFocus();
                else
                    box3.requestFocus();
            }
        });
        box3.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(count == 0)
                    box2.requestFocus();
            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.toString().equals(""))
                    box2.requestFocus();
                else
                    box4.requestFocus();
            }
        });
        box4.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(count == 0)
                    box3.requestFocus();
            }

            @Override
            public void afterTextChanged(Editable s) {
               if(s.toString().equals(""))
                   box3.requestFocus();
                findViewById(R.id.newCodeButton).requestFocus();

            }
        });
    }

    private boolean validate()
    {
        if(!TextUtils.isEmpty(((EditText) findViewById(R.id.charOne)).getText().toString()) &&
                !TextUtils.isEmpty(((EditText) findViewById(R.id.charTwo)).getText().toString()) &&
                !TextUtils.isEmpty(((EditText) findViewById(R.id.charThree)).getText().toString()) &&
                !TextUtils.isEmpty(((EditText) findViewById(R.id.charFour)).getText().toString())
                )
            return true;
        else
        {
            hideProgress();
            showAlert(this, "Please enter full code.");
            return false;
        }
    }

    private void setContactButton()
    {
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
}
