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
import android.widget.TextView;

import com.iva.findexpert.Common.Constant;
import com.iva.findexpert.R;
import com.iva.findexpert.UI.Common.Fragments.PromotionDetailFragment;
import com.iva.findexpert.UI.Helpers.FragmentHelper;
import com.iva.findexpert.Utility.Session;
import com.iva.findexpert.ViewModel.PromotionViewModel;

import java.util.List;

/**
 * Created by LENOVO on 12/26/2016.
 */

public class PromotionListAdapter extends ArrayAdapter<PromotionViewModel> {

    private Context context;
    private Activity activity;
    private List<PromotionViewModel> promotions;

    public PromotionListAdapter(Context context, Activity activity, List<PromotionViewModel> promotions)
    {
        super(context, R.layout.message_thread_promotion_item, promotions);
        this.context = context;
        this.activity = activity;
        this.promotions = promotions;
    }

    @Override
    public View getView(int position, View convertView, final ViewGroup parent)
    {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.message_thread_promotion_item, parent, false);
        PromotionViewModel promotion = promotions.get(position);
        ((TextView)rowView.findViewById(R.id.type)).setText(String.valueOf(promotion.Title));
        ((TextView)rowView.findViewById(R.id.title)).setText(String.valueOf(promotion.Header));
        ((TextView)rowView.findViewById(R.id.header)).setText(String.valueOf(promotion.Type));
        rowView.setTag(promotion.Id);
        rowView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                long id = Long.parseLong(v.getTag().toString());
                Session.putLong(context, Constant.SessionKeys.LAST_PROMOTION, id);
                openDetails(id);
            }
        });

        return rowView;
    }

    private void openDetails(long Id)
    {
        Fragment fragment = PromotionDetailFragment.newInstance(Id);
        FragmentHelper.openFragement(fragment, activity);
    }


}
