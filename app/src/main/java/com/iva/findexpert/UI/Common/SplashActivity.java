package com.iva.findexpert.UI.Common;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.iva.findexpert.Common.Constant;
import com.iva.findexpert.DomainModel.User;
import com.iva.findexpert.R;
import com.iva.findexpert.Service.UserService;
import com.iva.findexpert.UI.Buyer.BuyerHomeActivity;
import com.iva.findexpert.UI.Buyer.ValidateCodeActivity;
import com.iva.findexpert.UI.Agent.AgentHomeActivity;
import com.iva.findexpert.Utility.Session;

public class SplashActivity extends Activity {

    // Splash screen timer
    private static int SPLASH_TIME_OUT = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //getActionBar().hide();
        setContentView(R.layout.activity_splash);

        new Handler().postDelayed(new Runnable() {

            /*
             * Showing splash screen with a timer. This will be useful when you
             * want to show case your app logo / company
             */

            @Override
            public void run() {
                // This method will be executed once the timer is over
                // Start your app main activity
                Intent intent = new Intent(SplashActivity.this, WelcomeOneActivity.class);
                User currentUser = new UserService(getApplicationContext()).GetCurrentUser();
                if(currentUser == null)
                {
                    int  i = Session.getInt(getApplicationContext(), Constant.SessionKeys.APP_FIRST_RUN);
                    if(i == 0) {
                        startActivity(intent);
                        Session.putInt(getApplicationContext(), Constant.SessionKeys.APP_FIRST_RUN, 1);
                    }
                    else
                    {
                        intent = new Intent(SplashActivity.this, BuyerHomeActivity.class);
                        startActivity(intent);
                    }
                }
                else
                {
                    if(currentUser.IsAuthenticated)
                    {
                        if(currentUser.Type == Constant.UserType.BUYER)
                            intent = new Intent(SplashActivity.this, BuyerHomeActivity.class);
                        else
                            intent = new Intent(SplashActivity.this, AgentHomeActivity.class);

                        /*HttpClientHelper helper = new HttpClientHelper();
                        Map<String, String> params = new Hashtable<String, String>();
                        params.put("username", currentUser.Phone);
                        params.put("password", currentUser.Password);
                        params.put("grant_type", "password");

                        helper.getResponseStringURLEncoded(getApplicationContext(), Constant.TOKEN_URL, params, null, new IHttpResponse() {
                            @Override
                            public void onSuccess(String result) {
                                if(!TextUtils.isEmpty(result))
                                {
                                    Gson gson = new Gson();
                                    AccessTokenViewModel token = gson.fromJson(result, AccessTokenViewModel.class);
                                    if(token != null)
                                    {
                                        User user = new UserService(getApplicationContext()).GetCurrentUser();
                                        user.Token = token.access_token;
                                        new UserService(getApplicationContext()).Update(user);
                                    }
                                }
                            }
                        });*/

                        startActivity(intent);
                    }
                    else
                    {
                        intent = new Intent(SplashActivity.this, ValidateCodeActivity.class);
                        startActivity(intent);
                    }

                }
                // close this activity
                finish();
            }
        }, SPLASH_TIME_OUT);
    }
}
