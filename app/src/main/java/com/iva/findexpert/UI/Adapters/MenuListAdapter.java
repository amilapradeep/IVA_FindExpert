package com.iva.findexpert.UI.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.iva.findexpert.Common.Enum;
import com.iva.findexpert.R;


/**
 * Created by jayan on 9/1/2014.
 */
public class MenuListAdapter extends ArrayAdapter<String> {

    private final Context context;
    private final String[] values;
    private final Enum.Fragment fragment;

    public MenuListAdapter(Context context, String[] values, Enum.Fragment fragmentInView){
        super(context, R.layout.menu_list_item, values);
        this.context = context;
        this.values = values;
        this.fragment = fragmentInView;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.menu_list_item, parent, false);

        TextView textView = (TextView) rowView.findViewById(R.id.lblMenuText);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.imgMenuIcon);

        switch (fragment)
        {
            case BUYER_REQUEST_LIST:
                menuBuyerSRList(position, imageView, textView);
                break;

            case NOTIFICATION_LIST:
                notifcationList(position, imageView, textView);
                break;

            default:
                break;
        }

        return rowView;
    }

    private void menuBuyerSRList(int position, ImageView imageView, TextView textView)
    {
        switch (position)
        {
            case 0: //Sync
                imageView.setImageResource(R.mipmap.ic_right_arrow_white);
                textView.setText("All");
                break;
            case 1: //Logoff
                imageView.setImageResource(R.mipmap.ic_right_arrow_white);
                textView.setText("Accepted");
                break;
            case 2: //Logoff
                imageView.setImageResource(R.mipmap.ic_right_arrow_white);
                textView.setText("Closed");
                break;
            default:
                break;
        }
    }

    private void notifcationList(int position, ImageView imageView, TextView textView)
    {
        switch (position)
        {
            case 0: //Sync
                imageView.setImageResource(R.mipmap.ic_right_arrow_white);
                textView.setText(values[position]);
                break;
            case 1: //Logoff
                imageView.setImageResource(R.mipmap.ic_right_arrow_white);
                textView.setText(values[position]);
                break;
            default:
                break;
        }
    }

}
