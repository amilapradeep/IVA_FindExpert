package com.iva.findexpert.UI.Adapters;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.iva.findexpert.Common.Constant;
import com.iva.findexpert.DomainModel.Quotation;
import com.iva.findexpert.DomainModel.QuotationRequest;
import com.iva.findexpert.R;
import com.iva.findexpert.Service.InsuranceTypesService;
import com.iva.findexpert.UI.Buyer.Fragments.QuotationDetailFragment;
import com.iva.findexpert.UI.Helpers.FragmentHelper;

import java.util.List;

/**
 * Created by LENOVO on 12/2/2016.
 */

public class BuyerQuotationListAdapter extends ArrayAdapter<Quotation> {

    private final Context context;
    private final Activity activity;
    private List<Quotation> quotationList;

    public BuyerQuotationListAdapter(Context context, Activity activity, List<Quotation> quotationList)
    {
        super(context, R.layout.quotation_list_item, quotationList);
        this.context = context;
        this.activity = activity;
        this.quotationList = quotationList;
    }

    private void openDetails(long quoteid)
    {
        Fragment fragment = QuotationDetailFragment.newInstance(quoteid, "");
        FragmentHelper.openFragement(fragment, activity);
    }

    @Override
    public View getView(int position, View convertView, final ViewGroup parent)
    {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.quotation_list_item, parent, false);
        Quotation quotation = quotationList.get(position);
        ((TextView)rowView.findViewById(R.id.companyName)).setText(quotation.CompanyName);
        ((TextView)rowView.findViewById(R.id.premium)).setText(quotation.Premimum+ " Rs");
        ((TextView)rowView.findViewById(R.id.cover)).setText(String.valueOf(quotation.Cover + " Rs"));
        rowView.setTag(quotation.Id);

        rowView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                long id = Long.parseLong(v.getTag().toString());
                openDetails(id);
            }
        });

        if(quotation.Status == Constant.QuotationStatus.ACCEPTED)
        {
            rowView.findViewById(R.id.acceptedIcon).setVisibility(View.VISIBLE);
        }
        return rowView;
    }

}
