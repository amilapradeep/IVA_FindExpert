package com.iva.findexpert.UI.Adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.Gson;
import com.iva.findexpert.Common.Constant;
import com.iva.findexpert.R;
import com.iva.findexpert.UI.Agent.Fragments.TemplateEditFragment;
import com.iva.findexpert.UI.Common.Fragments.HelpFragment;
import com.iva.findexpert.UI.Helpers.FragmentHelper;
import com.iva.findexpert.Utility.Session;
import com.iva.findexpert.ViewModel.QuotationTemplate;

import java.util.List;

/**
 * Created by LENOVO on 12/12/2016.
 */

public class TemplateListAdapter extends ArrayAdapter<QuotationTemplate>{

    private final Context context;
    private final Activity activity;
    private List<QuotationTemplate> templateList;

    public TemplateListAdapter(Context context,Activity activity, List<QuotationTemplate> templates)
    {
        super(context, R.layout.template_list_item, templates);
        this.context = context;
        this.activity = activity;
        this.templateList = templates;
    }

    @Override
    public View getView(int position, View convertView, final ViewGroup parent)
    {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.template_list_item, parent, false);
        final QuotationTemplate template = templateList.get(position);
        ((TextView)rowView.findViewById(R.id.name)).setText(template.Name);
        rowView.findViewById(R.id.edit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TemplateEditFragment fragment = TemplateEditFragment.newInstance(template.Id);
                FragmentHelper.openFragement(fragment, activity);
            }
        });

        rowView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String json = new Gson().toJson(template);
                Session.putString(context, Constant.SessionKeys.TEMPLATE_OBJECT, json);
                activity.onBackPressed();
            }
        });

        return rowView;
    }

}
