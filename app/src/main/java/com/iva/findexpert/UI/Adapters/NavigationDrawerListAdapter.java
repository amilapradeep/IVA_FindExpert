package com.iva.findexpert.UI.Adapters;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.iva.findexpert.DomainModel.User;
import com.iva.findexpert.R;
import com.iva.findexpert.Service.UserService;

/**
 * Created by jayan on 8/25/2015.
 */
public class NavigationDrawerListAdapter extends ArrayAdapter<String> {

    private Context context;
    String[] modules;

    public NavigationDrawerListAdapter(Context context, String[] modules)
    {
        super(context, R.layout.navigation_list_item, modules );
        this.context = context;
        this.modules = modules;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = null;
        LayoutInflater inflator = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        String text = modules[position];
        if(TextUtils.isEmpty(text))
            view = inflator.inflate(R.layout.list_group_seperator, null);
        else
        {
            view = inflator.inflate(R.layout.navigation_list_item, null);

            ((TextView) view.findViewById(R.id.lblModuleName)).setText(modules[position]);
            ImageView image = ((ImageView) view.findViewById(R.id.imgModuleImage));

            switch (position)
            {
                case 0:
                    image.setImageResource(R.mipmap.ic_home_white);
                    break;
                case 1:
                    image.setImageResource(R.mipmap.ic_person_outline);
                    break;
                case 2:
                    image.setImageResource(R.mipmap.ic_local_offer);
                    break;
                case 3:
                    image.setImageResource(R.mipmap.ic_info_outline_white);
                    break;
                case 4:
                    image.setImageResource(R.mipmap.ic_feedback);
                    break;
                case 5:
                    image.setImageResource(R.mipmap.ic_notifications);
                    break;
                case 7:
                    User user = new UserService(context).GetCurrentUser();
                    if(user != null)
                        image.setImageResource(R.mipmap.ic_power_settings_new);
                    else
                    {
                        image.setImageResource(R.mipmap.ic_input_white);
                        ((TextView) view.findViewById(R.id.lblModuleName)).setText("Login");
                    }
                    break;
                default:
                    break;
            }
        }

        return view;
    }


}
