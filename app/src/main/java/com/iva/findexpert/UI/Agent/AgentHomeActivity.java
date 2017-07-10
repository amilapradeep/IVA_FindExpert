package com.iva.findexpert.UI.Agent;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Request;
import com.google.gson.Gson;
import com.iva.findexpert.Background.BackgroundService;
import com.iva.findexpert.Common.Constant;
import com.iva.findexpert.DomainModel.User;
import com.iva.findexpert.DomainModel.UserProfile;
import com.iva.findexpert.R;
import com.iva.findexpert.Service.DefaultData;
import com.iva.findexpert.Service.UserProfileService;
import com.iva.findexpert.Service.UserService;
import com.iva.findexpert.UI.Adapters.AgentNavigationDrawerAdapter;
import com.iva.findexpert.UI.Agent.Fragments.AgentHomeFragment;
import com.iva.findexpert.UI.Agent.Fragments.AgentRequestDetailFragment;
import com.iva.findexpert.UI.Common.BaseActivity;

import com.iva.findexpert.UI.Common.CustomDialog;
import com.iva.findexpert.UI.Common.Fragments.FeedbackFragment;
import com.iva.findexpert.UI.Common.Fragments.HelpFragment;
import com.iva.findexpert.UI.Common.Fragments.MessageFragment;
import com.iva.findexpert.UI.Common.Fragments.OffersFragment;
import com.iva.findexpert.UI.Common.Fragments.UserProfileFragment;
import com.iva.findexpert.UI.Common.Fragments.BaseFragment;
import com.iva.findexpert.UI.Common.LoginActivity;
import com.iva.findexpert.UI.Common.Fragments.OnFragmentBackPressEventHandler;
import com.iva.findexpert.UI.Common.NotificationListFragment;
import com.iva.findexpert.UI.Helpers.HttpClientHelper;
import com.iva.findexpert.UI.Helpers.IHttpResponse;
import com.iva.findexpert.UI.Helpers.NotificationsHelper;
import com.iva.findexpert.Utility.Session;

import java.util.HashMap;
import java.util.Map;

public class AgentHomeActivity extends AppCompatActivity {

    private DrawerLayout mDrawerLayout;
    private Toolbar toolbar;
    private ListView mDrawerList;
    private LinearLayout mDrawerListContainer;
    ActionBarDrawerToggle mDrawerToggle;
    private int moduleIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agent_home);

        this.setNavigationDrawer();
        checkOpenFromNotification();

        DefaultData df = new DefaultData(this);
        df.Add();
        setUserProfile();

        Intent bgService = new Intent(getBaseContext(), BackgroundService.class);
        startService(bgService);
    }

    private  void setNavigationDrawer()
    {
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerLayout.setScrimColor(Color.TRANSPARENT);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);
        mDrawerListContainer = (LinearLayout) findViewById(R.id.mDrawerListContainer);

        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //getSupportActionBar().setHomeButtonEnabled(true);

        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout ,toolbar, R.string.open_drawer, R.string.close_drawer){

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                // code here will execute once the drawer is opened( As I dont want anything happened whe drawer is
                // open I am not going to put anything here)
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                // Code here will execute once drawer is closed
                //setModuleView(moduleIndex);
            }

        }; // Drawer Toggle Object Made
        mDrawerLayout.addDrawerListener(mDrawerToggle); // Drawer Listener set to the Drawer toggle
        mDrawerToggle.syncState();               // Finally we set the drawer toggle sync State

        AgentNavigationDrawerAdapter adaptor = new AgentNavigationDrawerAdapter(
                getBaseContext(), getResources().getStringArray(R.array.arr_agent_modules));
        mDrawerList.setAdapter(adaptor);
        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                moduleIndex = position;
                setModuleView(position);
                mDrawerLayout.closeDrawer(mDrawerListContainer);
            }
        });
    }

    private void setModuleView(int Index)
    {
        BaseFragment fragment = null;

        //Add the logic to open from history if the fragments already exists in backstack

        switch (Index)
        {
            case Constant.MODULE_SELLER_HOME:
                fragment = AgentHomeFragment.newInstance();
                Session.putInt(this, Constant.SessionKeys.INSURANCE_TYPE, 1);
                break;

            case Constant.MODULE_SELLER_PROFILE_:
                fragment = UserProfileFragment.newInstance();
                break;

            case Constant.MODULE_SELLER_HELP:
                fragment = HelpFragment.newInstance();
                break;

            case Constant.MODULE_SELLER_FEEDBACK:
                fragment = FeedbackFragment.newInstance();
                break;

            case Constant.MODULE_SELLER_PRODUCTS_OFFERS:
                fragment = OffersFragment.newInstance();
                break;

            case Constant.MODULE_SELLER_NOTIFICATIONS:
                fragment = NotificationListFragment.newInstance();
                break;

            case Constant.MODULE_SELLER_LOG_OFF:
                this.confirmLogoff();
                break;

            default:
                fragment = AgentHomeFragment.newInstance();
                break;
        }

        if(fragment != null)
        {
            openFragment(fragment);
        }
    }

    private void openFragment(Fragment fragment)
    {
        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.content_frame, fragment);
        //ft.addToBackStack(fragment.getClass().getName());
        ft.commit();
        fm.executePendingTransactions();
    }

    private void checkOpenFromNotification()
    {
        Bundle args = getIntent().getExtras();

        if (args != null)
        {
            int type = args.getInt(Constant.NotificationType.NAME);
            long id = args.getLong(Constant.NotificationType.ID);
            BaseFragment fragment = null;
            switch (type)
            {
                case Constant.NotificationType.REQUEST:
                case Constant.NotificationType.FOLLOWUP_SELLER:
                    fragment = AgentRequestDetailFragment.newInstance(id, Constant.ParentUIName.FROM_HOME);
                    openFragment(fragment);
                    break;
                case Constant.NotificationType.ACCEPT:
                    fragment = AgentRequestDetailFragment.newInstanceQuotaitonId(id, Constant.ParentUIName.FROM_HOME);
                    openFragment(fragment);
                    break;
                case Constant.NotificationType.MESSAGE:
                    fragment = MessageFragment.newInstance(id, "");
                    openFragment(fragment);
                    break;
                default:
                    break;
            }
        }
        else
            setModuleView(moduleIndex);
    }

    @Override
    public void onBackPressed() {

        if(mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(Gravity.LEFT); //CLOSE Nav Drawer!
            return;
        }

        /*if (getFragmentManager().getBackStackEntryCount() == 0)
        {
            *//*final CustomDialog alert = Window.showAlert(this, "Exit Application", "Are you sure you want to exit app?");
            alert.OKButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });

            alert.CancelButton.setVisibility(View.VISIBLE);
            alert.CancelButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    alert.dismiss();
                }
            });*//*

            finishAffinity();
        } else
        {
            OnFragmentBackPressEventHandler e = (OnFragmentBackPressEventHandler)getFragmentManager().findFragmentById(R.id.content_frame);
            if(e.onBackPressed())
                getFragmentManager().popBackStackImmediate();
        }*/

        OnFragmentBackPressEventHandler e = (OnFragmentBackPressEventHandler)getFragmentManager().findFragmentById(R.id.content_frame);
        if(e != null)
        {
            boolean loadFromBS = e.onBackPressed();
            if(loadFromBS)
            {
                if(getFragmentManager().getBackStackEntryCount() > 0)
                    getFragmentManager().popBackStackImmediate();
                else
                    finishAffinity();
            }
        }
    }

    private void setUserProfile()
    {
        User user = new UserService(this).GetCurrentUser();
        ((TextView) findViewById(R.id.profileName)).setText(user.UserName);
        String url = Constant.BASE_URL + Constant.CONTROLLER_USER + Constant.SERVICE_GET_PROFILE;
        Map<String, String> params = new HashMap<>();
        params.put("UserId", String.valueOf(user.Id));

        Map<String,String> headerParams = new HashMap<>();
        headerParams.put("Authorization", "bearer " + user.Token);

        new HttpClientHelper().getResponseStringURLEncoded(this, url, Request.Method.GET, params, headerParams, new IHttpResponse() {
            @Override
            public void onSuccess(String result) {
                if(result != null)
                {
                    Gson gson = new Gson();
                    UserProfile profile = gson.fromJson(result, UserProfile.class );

                    if(profile != null)
                    {
                        UserProfileService service = new UserProfileService(AgentHomeActivity.this);
                        service.DeleteAll();
                        service.Insert(profile);
                        ((TextView) findViewById(R.id.profileName)).setText(profile.FirstName + " " + profile.LastName);
                    }
                }
            }

            @Override
            public void onError(int error) {
            }

            @Override
            public void onNoInternetConnection(){}
        });
    }

    private  void confirmLogoff()
    {
        final CustomDialog dialog = showAlert(this, "Confirm Logoff", "Are you sure you need to log off?");
        dialog.OKButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                logoff();
            }
        });
        dialog.CancelButton.setVisibility(View.VISIBLE);
        dialog.CancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        //logoff();
    }

    private void logoff()
    {
        new UserService(this).DeleteAll();
        new UserProfileService(this).DeleteAll();
        NotificationsHelper.cancelAllNotification(this);
        finishAffinity();
        Intent page = new Intent(this, LoginActivity.class);
        startActivity(page);
    }

    private CustomDialog showAlert(Context context, String title, String message)
    {
        CustomDialog alert = new CustomDialog(context);
        alert.setTitle(title);
        alert.setMessage(message);
        alert.show();
        return alert;
    }
}
