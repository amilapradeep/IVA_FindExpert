package com.iva.findexpert.UI.Adapters;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.iva.findexpert.Common.Constant;
import com.iva.findexpert.R;
import com.iva.findexpert.UI.Buyer.Fragments.QuotationDetailFragment;
import com.iva.findexpert.UI.Helpers.FragmentHelper;
import com.iva.findexpert.ViewModel.Message;
import com.iva.findexpert.ViewModel.MessageThread;

import java.util.List;

/**
 * Created by LENOVO on 12/10/2016.
 */

public class MessageListAdapter extends ArrayAdapter<Message> {

    private final Context context;
    private final Activity activity;
    private List<Message> messages;
    private long userId;

    public MessageListAdapter(Context context, Activity activity, List<Message> messages, long userId)
    {
        super(context, R.layout.message_list_item, messages);
        this.context = context;
        this.activity = activity;
        this.messages = messages;
        this.userId = userId;
    }

    @Override
    public View getView(int position, View convertView, final ViewGroup parent)
    {
        View rowView;
        final Message message = messages.get(position);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if(message.SenderId == userId) {
            rowView = inflater.inflate(R.layout.message_list_item_me, parent, false);
            ((TextView)rowView.findViewById(R.id.sender)).setText("Me");
        }
        else {
            rowView = inflater.inflate(R.layout.message_list_item, parent, false);
            ((TextView)rowView.findViewById(R.id.sender)).setText(message.SenderName);
        }

        ((TextView)rowView.findViewById(R.id.time)).setText(String.valueOf(message.Time));
        ((TextView)rowView.findViewById(R.id.message)).setText(message.MessageText);
        ImageView quote = ((ImageView) rowView.findViewById(R.id.quote));

        if(message.Status == 1 && message.RecieverId == userId)
        {
            ((TextView)rowView.findViewById(R.id.sender)).setTypeface(null, Typeface.BOLD);
            ((TextView)rowView.findViewById(R.id.time)).setTypeface(null, Typeface.BOLD);
            ((TextView)rowView.findViewById(R.id.message)).setTypeface(null, Typeface.BOLD);
        }

        if(message.QuotationId > 0)
        {
            quote.setVisibility(View.VISIBLE);
            rowView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openQuoteDetails(message.QuotationId);
                }
            });

            rowView.findViewById(R.id.container).setBackground(
                    context.getResources().getDrawable(R.drawable.bg_message_list_item));
        }

        return rowView;
    }

    private void openQuoteDetails(long quoteid)
    {
        Fragment fragment = QuotationDetailFragment.newInstance(quoteid , "");
        FragmentHelper.openFragement(fragment, activity);
    }

}

