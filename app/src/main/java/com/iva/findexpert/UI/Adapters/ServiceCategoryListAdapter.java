package com.iva.findexpert.UI.Adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.iva.findexpert.R;
import com.iva.findexpert.UI.Common.Fragments.ServiceProvidersFragment;
import com.iva.findexpert.UI.Helpers.FragmentHelper;
import com.iva.findexpert.ViewModel.ServiceCategory;

import java.util.List;

/**
 * Created by LENOVO on 1/1/2017.
 */

public class ServiceCategoryListAdapter extends ArrayAdapter<ServiceCategory> {

    private final Context context;
    private final Activity activity;
    private List<ServiceCategory> categories;

    public ServiceCategoryListAdapter(Context context, Activity activity, List<ServiceCategory> categories)
    {
        super(context, R.layout.service_category_list_item, categories);
        this.context = context;
        this.activity = activity;
        this.categories = categories;
    }

    @Override
    public View getView(int position, View convertView, final ViewGroup parent)
    {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.service_category_list_item, parent, false);

        ServiceCategory category = categories.get(position);
        ((TextView)rowView.findViewById(R.id.name)).setText(category.Name);
        rowView.setTag(category.Id);

        rowView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int catid = (int)v.getTag();
                String name = ((TextView) v.findViewById(R.id.name)).getText().toString();
                ServiceProvidersFragment fragment = ServiceProvidersFragment.newInstance(catid, name);
                FragmentHelper.openFragement(fragment, activity);
            }
        });

        return rowView;
    }

}
