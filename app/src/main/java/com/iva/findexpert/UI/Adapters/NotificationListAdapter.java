package com.iva.findexpert.UI.Adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.iva.findexpert.Common.Constant;
import com.iva.findexpert.R;
import com.iva.findexpert.UI.Agent.Fragments.AgentRequestDetailFragment;
import com.iva.findexpert.UI.Buyer.Fragments.QuotationDetailFragment;
import com.iva.findexpert.UI.Common.Fragments.BaseFragment;
import com.iva.findexpert.UI.Common.Fragments.MessageFragment;
import com.iva.findexpert.UI.Helpers.FragmentHelper;
import com.iva.findexpert.ViewModel.NotificationViewModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by LENOVO on 5/6/2017.
 */

public class NotificationListAdapter extends ArrayAdapter<NotificationViewModel> {
    private final Context context;
    private final Activity activity;
    private List<NotificationViewModel> notifications;
    private int userType;

    public NotificationListAdapter(Context context, Activity activity, List<NotificationViewModel> notifications, int userType)
    {
        super(context, R.layout.notification_list_item, notifications);
        this.context = context;
        this.activity = activity;
        this.notifications = notifications;
        this.userType = userType;
    }

    @Override
    public View getView(int position, View convertView, final ViewGroup parent)
    {
        final notificationListViewHolder viewHolder;
        if(convertView == null)
        {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.notification_list_item, parent, false);
            viewHolder = new notificationListViewHolder();
            viewHolder.title = (TextView) convertView.findViewById(R.id.title);
            viewHolder.text = (TextView) convertView.findViewById(R.id.text);
            viewHolder.time = (TextView) convertView.findViewById(R.id.time);
            viewHolder.select = (CheckBox) convertView.findViewById(R.id.selectItem);
            convertView.setTag(viewHolder);
        }
        else
        {
            viewHolder = (notificationListViewHolder)convertView.getTag();
        }

        final NotificationViewModel notification = notifications.get(position);
        viewHolder.title.setText(notification.Title);
        viewHolder.text.setText(notification.Text);
        viewHolder.time.setText(notification.DisplayTime);
        viewHolder.select.setChecked(notification.IsSelected);

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                BaseFragment fragment = null;
                if(userType == Constant.UserType.SELLER)
                {
                    switch (notification.Type)
                    {
                        case Constant.NotificationType.REQUEST:
                        case Constant.NotificationType.FOLLOWUP_SELLER:
                            fragment = AgentRequestDetailFragment.newInstance(notification.RecordId, "");
                            FragmentHelper.openFragement(fragment, activity);
                            break;
                        case Constant.NotificationType.ACCEPT:
                            fragment = AgentRequestDetailFragment.newInstanceQuotaitonId(notification.RecordId, "");
                            FragmentHelper.openFragement(fragment, activity);
                            break;
                        case Constant.NotificationType.MESSAGE:
                            fragment = MessageFragment.newInstance(notification.RecordId, "");
                            FragmentHelper.openFragement(fragment, activity);
                            break;
                        default:
                            break;
                    }
                }
                else
                {
                    switch (notification.Type)
                    {
                        case Constant.NotificationType.QUOTATION:
                            fragment = QuotationDetailFragment.newInstance(notification.RecordId, "");
                            FragmentHelper.openFragement(fragment, activity);
                            break;
                        case Constant.NotificationType.MESSAGE:
                            fragment = MessageFragment.newInstance(notification.RecordId, "");
                            FragmentHelper.openFragement(fragment, activity);
                            break;
                        case  Constant.NotificationType.FOLLOWUP_BUYER:
                            fragment = QuotationDetailFragment.newInstance(notification.RecordId, "");
                            FragmentHelper.openFragement(fragment, activity);
                            break;
                        default:
                            break;
                    }
                }

            }
        });

        convertView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                ((CheckBox) v.findViewById(R.id.selectItem)).toggle();
                v.findViewById(R.id.selectItem).callOnClick();
                return true;
            }
        });

        convertView.findViewById(R.id.selectItem).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isChecked = ((CheckBox) v.findViewById(R.id.selectItem)).isChecked();
                notification.IsSelected = isChecked;
            }
        });
        return convertView;
    }

    public List<NotificationViewModel> getList()
    {
        return notifications;
    }

    static class notificationListViewHolder
    {
        public TextView title;
        public TextView text;
        public TextView time;
        public CheckBox select;
    }
}
