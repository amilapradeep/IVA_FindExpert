package com.iva.findexpert.UI.Common.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.iva.findexpert.Common.Constant;
import com.iva.findexpert.DomainModel.User;
import com.iva.findexpert.R;
import com.iva.findexpert.Service.UserService;
import com.iva.findexpert.UI.Agent.Fragments.AgentHomeFragment;
import com.iva.findexpert.UI.Buyer.Fragments.BuyerHomeFragment;
import com.iva.findexpert.UI.Common.GetStartedActivity;

/**
 * A simple {@link Fragment} subclass.
 */
public class HelpFragment extends BaseFragment {


    public HelpFragment() {
        // Required empty public constructor
    }

    public static HelpFragment newInstance()
    {
        return new HelpFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_help, container, false);
        setCurrentView(view);
        setButtons();
        return view;
    }

    @Override
    public boolean onBackPressed() {
        User user = new UserService(getActivity()).GetCurrentUser();
        if(user != null && user.Type == Constant.UserType.SELLER)
            openFragement(AgentHomeFragment.newInstance(), false);
        else
            openFragement(BuyerHomeFragment.newInstance(), false);

        return false;
    }

    private void setButtons()
    {
        getCurrentView().findViewById(R.id.getStartedButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //GetStartedFragment fragment = GetStartedFragment.newInstance();
                //openFragement(fragment, true);
                Intent intent = new Intent(getActivity(), GetStartedActivity.class);
                startActivity(intent);
            }
        });

        getCurrentView().findViewById(R.id.termsAndConditionsButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TermsAndConditionsFragment fragment = TermsAndConditionsFragment.newInstance();
                openFragement(fragment, true);
            }
        });

        getCurrentView().findViewById(R.id.privacyStatementButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PrivacyStatementFragment fragment = PrivacyStatementFragment.newInstance();
                openFragement(fragment, true);
            }
        });

        getCurrentView().findViewById(R.id.aboutUsButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AboutUsFragment fragment = AboutUsFragment.newInstance();
                openFragement(fragment, true);
            }
        });
    }
}
