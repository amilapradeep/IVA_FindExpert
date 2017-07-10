package com.iva.findexpert.UI.Adapters;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.iva.findexpert.DomainModel.QuotationRequest;
import com.iva.findexpert.R;
import com.iva.findexpert.Service.InsuranceTypesService;
import com.iva.findexpert.UI.Agent.Fragments.AgentRequestDetailFragment;
import com.iva.findexpert.UI.Helpers.FragmentHelper;

import java.util.List;

/**
 * Created by LENOVO on 12/12/2016.
 */

public class AgentRequestListAdapter extends ArrayAdapter<QuotationRequest> {

    private final Context context;
    private final Activity activity;
    private List<QuotationRequest> requestList;

    public AgentRequestListAdapter(Context context, Activity activity, List<QuotationRequest> requestList)
    {
        super(context, R.layout.request_list_item, requestList);
        this.context = context;
        this.activity = activity;
        this.requestList = requestList;
    }

    private void openDetails(long requestId)
    {
        Fragment fragment = AgentRequestDetailFragment.newInstance(requestId, "");
        FragmentHelper.openFragement(fragment, activity);
    }

    @Override
    public View getView(int position, View convertView, final ViewGroup parent)
    {
        final ViewHolder viewHolder;
        if(convertView == null)
        {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.request_list_item, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.vehicleRegNo = (TextView) convertView.findViewById(R.id.vehicleRegNo);
            viewHolder.insuranceType = (TextView) convertView.findViewById(R.id.insuranceType);
            viewHolder.createdDate = (TextView) convertView.findViewById(R.id.createdDate);
            convertView.setTag(viewHolder);
        }
        else {
            viewHolder = (ViewHolder)convertView.getTag();
        }


        final QuotationRequest request = requestList.get(position);
        viewHolder.vehicleRegNo.setText(request.VehicleNo);

        if(request.InsuranceTypeId == 0)
            request.InsuranceTypeId = 1;
        String insuranceType = new InsuranceTypesService(context).GetById(request.InsuranceTypeId).Name;
        viewHolder.insuranceType.setText(insuranceType);
        viewHolder.createdDate.setText(String.valueOf(request.CreatedDate));
        if(!TextUtils.isEmpty(request.TimeToExpire))
            viewHolder.createdDate.setText(request.TimeToExpire);

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                long id = request.Id;
                openDetails(id);
            }
        });

        return convertView;
    }

    static class ViewHolder {
        public TextView vehicleRegNo;
        public TextView insuranceType;
        public TextView createdDate;
    }

}
