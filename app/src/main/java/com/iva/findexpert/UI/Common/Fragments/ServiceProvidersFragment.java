package com.iva.findexpert.UI.Common.Fragments;


import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Request;
import com.google.gson.Gson;
import com.iva.findexpert.Common.Constant;
import com.iva.findexpert.R;
import com.iva.findexpert.Service.UserService;
import com.iva.findexpert.UI.Adapters.ServiceCategoryListAdapter;
import com.iva.findexpert.UI.Adapters.ServiceProviderListAdapter;
import com.iva.findexpert.UI.Helpers.HttpClientHelper;
import com.iva.findexpert.UI.Helpers.IHttpResponse;
import com.iva.findexpert.UI.Helpers.SingleTimeLocationProvider;
import com.iva.findexpert.ViewModel.ServiceCategory;
import com.iva.findexpert.ViewModel.ServiceProvider;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.jar.Manifest;

/**
 * A simple {@link Fragment} subclass.
 */
public class ServiceProvidersFragment extends BaseFragment {

    private int category;
    private String name;
    private double longitude = 0;
    private double latitude = 0;

    public ServiceProvidersFragment() {
        // Required empty public constructor
    }

    public static ServiceProvidersFragment newInstance(int type, String name)
    {
        ServiceProvidersFragment fragment = new ServiceProvidersFragment();
        Bundle args = new Bundle();
        args.putInt(Constant.SessionKeys.CATEGORY_ID, type);
        args.putString(Constant.SessionKeys.CATEGORY_NAME, name);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            category = getArguments().getInt(Constant.SessionKeys.CATEGORY_ID);
            name = getArguments().getString(Constant.SessionKeys.CATEGORY_NAME);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_service_providers, container, false);
        setCurrentView(view);
        ((TextView) getCurrentView().findViewById(R.id.categoryName)).setText(name);
        checkLocationServicePermissions();
        return view;
    }

    private final int REQUEST_PERMISSION_FINE_LOCATION_STATE = 1;
    private void checkLocationServicePermissions()
    {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            int permFineLoc = ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION);
            int permCoarseLoc = ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION);
            if(permFineLoc != PackageManager.PERMISSION_GRANTED)
            {
                ActivityCompat.requestPermissions( getActivity(), new String[] {android.Manifest.permission.ACCESS_FINE_LOCATION},
                        REQUEST_PERMISSION_FINE_LOCATION_STATE);
            }
            else
            {
                LocationBasedSearch();
            }
        } else {
            LocationBasedSearch();
        }


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_PERMISSION_FINE_LOCATION_STATE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission Granted
                    LocationBasedSearch();
                } else {
                    showAlert(getActivity(), "LocationViewModel permissions not granted, Could not find the nearest services!");
                    loadServiceProviders(false);
                }
                break;
            default:
                break;
        }
    }

    private void LocationBasedSearch()
    {
        SingleTimeLocationProvider.requestSingleUpdate(getActivity(), new SingleTimeLocationProvider.LocationCallback() {
            @Override
            public void onNewLocationAvailable(SingleTimeLocationProvider.GPSCoordinates location) {
                longitude = location.longitude;
                latitude = location.latitude;
                loadServiceProviders(false);
            }

            @Override
            public void onNoLocationAvailable()
            {
                loadServiceProviders(true);
            }

            @Override
            public void onCacheLocationAvailable(SingleTimeLocationProvider.GPSCoordinates location) {
                longitude = location.longitude;
                latitude = location.latitude;
                loadServiceProviders(true);
            }
        });
    }

    private void loadServiceProviders(boolean fromKnownlocation)
    {
        String url = Constant.BASE_URL + Constant.CONTROLLER_AROUND_ME + Constant.SERVICE_GET_SERVICE_PROVIDERS;

        Map<String,String> params = new HashMap<>();
        params.put("CategoryId", String.valueOf(category));
        params.put("Longitude", String.valueOf(longitude));
        params.put("Latitude", String.valueOf(latitude));

        Map<String,String> headerParams = new HashMap<>();
        headerParams.put("Authorization", "bearer " + new UserService(getActivity()).GetCurrentUser().Token);

        if(!fromKnownlocation)
            showProgress(getActivity());
        new HttpClientHelper().getResponseStringURLEncoded(
                getActivity(), url, Request.Method.GET, params, headerParams, new IHttpResponse() {
                    @Override
                    public void onSuccess(String result) {
                        if(result != null)
                        {
                            Gson gson = new Gson();
                            List<ServiceProvider> providers = Arrays.asList(gson.fromJson(result, ServiceProvider[].class ));
                            ArrayAdapter adapter = new ServiceProviderListAdapter(getActivity(), getActivity(), providers);
                            ((ListView)getCurrentView().findViewById(R.id.providers)).setAdapter(adapter);
                            if(providers.size() > 0)
                                getCurrentView().findViewById(R.id.empty).setVisibility(View.GONE);
                            else
                                getCurrentView().findViewById(R.id.empty).setVisibility(View.VISIBLE);
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

}
