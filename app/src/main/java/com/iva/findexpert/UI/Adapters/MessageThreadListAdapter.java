package com.iva.findexpert.UI.Adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.iva.findexpert.Common.Constant;
import com.iva.findexpert.R;
import com.iva.findexpert.UI.Common.Fragments.MessageFragment;
import com.iva.findexpert.UI.Common.Fragments.OffersFragment;
import com.iva.findexpert.UI.Helpers.FragmentHelper;
import com.iva.findexpert.Utility.Common;
import com.iva.findexpert.ViewModel.MessageThread;

import java.util.List;

/**
 * Created by LENOVO on 12/10/2016.
 */

public class MessageThreadListAdapter extends ArrayAdapter<MessageThread> {

    private final Context context;
    private final Activity activity;
    private List<MessageThread> threads;
    private boolean isBuyer;

    public MessageThreadListAdapter(Context context, Activity activity, List<MessageThread> threads, boolean isBuyer)
    {
        super(context, R.layout.message_thread_list_item, threads);
        this.context = context;
        this.activity = activity;
        this.threads = threads;
        this.isBuyer = isBuyer;
    }

    private void openDetails(long threadId)
    {
        MessageFragment fragment = MessageFragment.newInstance(threadId, "");
        FragmentHelper.openFragement(fragment, activity);
    }

    @Override
    public View getView(int position, View convertView, final ViewGroup parent)
    {
        final ViewHolder viewHolder;
        if(convertView == null)
        {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.message_thread_list_item, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.sender = (TextView) convertView.findViewById(R.id.sender);
            viewHolder.messageFor = (TextView) convertView.findViewById(R.id.messageFor);
            viewHolder.date = (TextView) convertView.findViewById(R.id.date);
            viewHolder.description = (TextView) convertView.findViewById(R.id.description);
            viewHolder.msgCount = (TextView) convertView.findViewById(R.id.msgCount);
            viewHolder.image = (ImageView) convertView.findViewById(R.id.imageView);
            convertView.setTag(viewHolder);
        }
        else {
            viewHolder = (ViewHolder)convertView.getTag();
        }

        final MessageThread thread = threads.get(position);

        if(thread.Id != 0)
        {
            if(isBuyer)
            {
                viewHolder.sender.setText(thread.AgentName);
                viewHolder.messageFor.setText(thread.CompanyName);
            }
            else
            {
                viewHolder.sender.setText(thread.BuyerName);
                viewHolder.messageFor.setText(thread.VehicleNo);
            }

            viewHolder.date.setText(String.valueOf(thread.Date));
            viewHolder.description.setText(String.valueOf(thread.Description));
            if(thread.UnreadMessageCount > 0)
            {
                viewHolder.msgCount.setText(String.valueOf(thread.UnreadMessageCount));
                viewHolder.msgCount.setVisibility(View.VISIBLE);
            }
            else
            {
                viewHolder.msgCount.setVisibility(View.GONE);
            }
            viewHolder.image.setBackground(context.getResources().getDrawable(R.drawable.bg_circle_thread_icon));
            viewHolder.image.setImageResource(R.mipmap.ic_forum_white);

            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    long id = thread.Id;
                    openDetails(id);
                }
            });
        }
        else {

            if(thread.Promotion != null)
            {
                viewHolder.sender.setText(String.valueOf(thread.Promotion.Type));
                viewHolder.messageFor.setText(String.valueOf(thread.Promotion.Title));
                viewHolder.description.setText(String.valueOf(thread.Promotion.Header));
                viewHolder.date.setText("");
                viewHolder.msgCount.setVisibility(View.GONE);
                viewHolder.image.setBackground(context.getResources().getDrawable(R.drawable.bg_circle_orange));
                viewHolder.image.setImageResource(R.mipmap.ic_local_offer);

                convertView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        OffersFragment fragment = OffersFragment.newInstance();
                        Bundle args = new Bundle();
                        args.putString(Constant.SessionKeys.PARENT_UI, Constant.ParentUIName.FROM_MESSAGE_LIST);
                        fragment.setArguments(args);
                        FragmentHelper.openFragement(fragment, activity);
                    }
                });
            }
        }

        return convertView;
    }

    static class ViewHolder {
        public TextView sender;
        public TextView messageFor;
        public TextView date;
        public TextView description;
        public TextView msgCount;
        public ImageView image;
    }

}

