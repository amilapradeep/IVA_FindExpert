package com.iva.findexpert.UI.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.iva.findexpert.R;
import com.iva.findexpert.ViewModel.ServiceProvider;

import java.util.List;

/**
 * Created by LENOVO on 1/1/2017.
 */

public class ServiceProviderListAdapter extends ArrayAdapter<ServiceProvider> {

    private final Context context;
    private final Activity activity;
    List<ServiceProvider> providers;

    public ServiceProviderListAdapter(Context context, Activity activity, List<ServiceProvider> providers)
    {
        super(context, R.layout.service_providers_list_item, providers);
        this.context = context;
        this.activity = activity;
        this.providers = providers;
    }

    @Override
    public View getView(int position, View convertView, final ViewGroup parent)
    {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.service_providers_list_item, parent, false);

        ServiceProvider provider = providers.get(position);
        ((TextView)rowView.findViewById(R.id.company)).setText(provider.Company);
        ((TextView)rowView.findViewById(R.id.distance)).setText(provider.DistanceKM);
        ((TextView)rowView.findViewById(R.id.category)).setText(provider.Category);
        ((TextView)rowView.findViewById(R.id.address)).setText(provider.Address);
        ((TextView)rowView.findViewById(R.id.phone)).setText(provider.Phone);
        rowView.setTag(provider.Phone);

        if(!TextUtils.isEmpty(provider.Phone))
        {
            rowView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    String mobileNo = v.getTag().toString();
                    Intent intent = new Intent(Intent.ACTION_DIAL);
                    intent.setData(Uri.parse("tel:" + mobileNo ));
                    context.startActivity(intent);

                }
            });
        }
        else
        {
            rowView.findViewById(R.id.phoneButton).setVisibility(View.INVISIBLE);
        }



        return rowView;
    }
}
