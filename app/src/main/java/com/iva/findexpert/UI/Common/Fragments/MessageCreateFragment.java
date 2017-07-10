package com.iva.findexpert.UI.Common.Fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.iva.findexpert.Common.Constant;
import com.iva.findexpert.DomainModel.User;
import com.iva.findexpert.R;
import com.iva.findexpert.Service.UserService;
import com.iva.findexpert.UI.Helpers.HttpClientHelper;
import com.iva.findexpert.UI.Helpers.IHttpResponse;
import com.iva.findexpert.UI.Helpers.NotificationsHelper;
import com.iva.findexpert.ViewModel.Message;

import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class MessageCreateFragment extends BaseFragment {

    private long threadId;
    private long recipientId;
    private String recipientName;
    private long requestId;

    public MessageCreateFragment() {
        // Required empty public constructor
    }

    public static MessageCreateFragment newInstance(long threadId, long recipientId, String recipientName, long requestId)
    {
        MessageCreateFragment fragment = new MessageCreateFragment();
        Bundle args = new Bundle();
        args.putLong(Constant.SessionKeys.MESSAGE_THREAD_ID, threadId);
        args.putLong(Constant.SessionKeys.RECIPIENT_ID, recipientId);
        args.putString(Constant.SessionKeys.RECIPIENT_NAME, recipientName);
        args.putLong(Constant.SessionKeys.REQUEST_ID, requestId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            threadId = getArguments().getLong(Constant.SessionKeys.MESSAGE_THREAD_ID);
            recipientId = getArguments().getLong(Constant.SessionKeys.RECIPIENT_ID);
            recipientName = getArguments().getString(Constant.SessionKeys.RECIPIENT_NAME);
            requestId = getArguments().getLong(Constant.SessionKeys.REQUEST_ID);
        }
        setHasOptionsMenu(true);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_message_create, container, false);
        setCurrentView(view);
        setLabels();
        setButtons();
        NotificationsHelper.cancelNotification(getActivity(), Constant.NotificationType.MESSAGE);
        return view;
    }

    private void setLabels()
    {
        ((TextView) getCurrentView().findViewById(R.id.recipient)).setText(recipientName);
    }

    private void setButtons()
    {
        ((Button) getCurrentView().findViewById(R.id.send)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                message();
            }
        });
    }

    private boolean validate()
    {
        String message = ((EditText) getCurrentView().findViewById(R.id.messageText)).getText().toString();
        if(TextUtils.isEmpty(message))
        {
            showAlert(getActivity(), "Please add your message to send.");

            return false;
        }
        return true;
    }

    private void message()
    {
        if(validate())
        {
            final User user = new UserService(getActivity()).GetCurrentUser();
            Message message = new Message();
            message.MessageText = ((EditText) getCurrentView().findViewById(R.id.messageText)).getText().toString();
            message.SenderId =  user.Id;
            message.RecieverId = recipientId;
            message.RequestId = requestId;
            message.ThreadId = threadId;
            String jsonObj = new Gson().toJson(message);

            Map<String,String> headerParams = new HashMap<>();
            headerParams.put("Authorization", "bearer " + user.Token );
            String url = Constant.BASE_URL + Constant.CONTROLLER_MESSAGE + Constant.SERVICE_ADD_MESSAGE;
            showProgress(getActivity());

            new HttpClientHelper().getResponseString(getActivity(), url, jsonObj, headerParams, new IHttpResponse() {
                @Override
                public void onSuccess(String result) {
                    hideProgress();
                    Toast.makeText(getActivity(), "Message was sent successfully", Toast.LENGTH_LONG).show();
                    getActivity().onBackPressed();
                }

                @Override
                public void onError(int error) {
                    hideProgress();
                    showAlert(getActivity(), "Error communicating the server!");
                }

                @Override
                public void onNoInternetConnection(){ hideProgress();}
            });
        }
    }

}
