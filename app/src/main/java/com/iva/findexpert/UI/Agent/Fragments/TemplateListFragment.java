package com.iva.findexpert.UI.Agent.Fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.android.volley.Request;
import com.google.gson.Gson;
import com.iva.findexpert.Common.Constant;
import com.iva.findexpert.DomainModel.User;
import com.iva.findexpert.R;
import com.iva.findexpert.Service.UserService;
import com.iva.findexpert.UI.Adapters.TemplateListAdapter;
import com.iva.findexpert.UI.Common.Fragments.BaseFragment;
import com.iva.findexpert.UI.Helpers.HttpClientHelper;
import com.iva.findexpert.UI.Helpers.IHttpResponse;
import com.iva.findexpert.ViewModel.QuotationTemplate;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class TemplateListFragment extends BaseFragment {


    public TemplateListFragment() {
        // Required empty public constructor
    }

    public static TemplateListFragment newInstance()
    {
        return new TemplateListFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_template_list, container, false);
        setCurrentView(view);
        loadTemplates();
        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_template, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.action_new)
        {
            TemplateEditFragment fragment = TemplateEditFragment.newInstance(0);
            openFragement(fragment, true);
        }

        return super.onOptionsItemSelected(item);
    }

    private void loadTemplates()
    {
        User user = new UserService(getActivity()).GetCurrentUser();
        String url = Constant.BASE_URL + Constant.CONTROLLER_TEMPLATE + Constant.SERVICE_TEMPLATE_GET_BY_AGENT;
        Map<String, String> params = new HashMap<>();
        params.put("AgentId", String.valueOf(user.Id));

        Map<String,String> headerParams = new HashMap<>();
        headerParams.put("Authorization", "bearer " + user.Token);

        showProgress(getActivity());
        new HttpClientHelper().getResponseStringURLEncoded(getActivity(), url, Request.Method.GET, params, headerParams, new IHttpResponse() {
            @Override
            public void onSuccess(String result) {
                if(result != null)
                {
                    Gson gson = new Gson();
                    List<QuotationTemplate> templates = Arrays.asList(gson.fromJson(result, QuotationTemplate[].class ));
                    ArrayAdapter adapter = new TemplateListAdapter(getActivity(), getActivity(), templates);
                    ((ListView)getCurrentView().findViewById(R.id.templateList)).setAdapter(adapter);
                    if(templates.size() > 0)
                        getCurrentView().findViewById(R.id.empty).setVisibility(View.GONE);
                    else
                        getCurrentView().findViewById(R.id.empty).setVisibility(View.VISIBLE);
                }
                hideProgress();
            }

            @Override
            public void onError(int error) {
                hideProgress();
            }

            @Override
            public void onNoInternetConnection(){ hideProgress();}
        });
    }

}
