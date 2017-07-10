package com.iva.findexpert.UI.Common.Fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.google.gson.Gson;
import com.iva.findexpert.Common.Constant;
import com.iva.findexpert.DomainModel.User;
import com.iva.findexpert.R;
import com.iva.findexpert.Service.UserService;
import com.iva.findexpert.UI.Adapters.MessageListAdapter;
import com.iva.findexpert.UI.Buyer.Fragments.BuyerHomeFragment;
import com.iva.findexpert.UI.Common.CustomDialog;
import com.iva.findexpert.UI.Helpers.HttpClientHelper;
import com.iva.findexpert.UI.Helpers.IHttpResponse;
import com.iva.findexpert.UI.Helpers.NotificationsHelper;
import com.iva.findexpert.ViewModel.MessageThread;

import java.util.HashMap;
import java.util.Map;

import static android.view.View.GONE;

/**
 * A simple {@link Fragment} subclass.
 */
public class MessageFragment extends BaseFragment {

    private long threadId;
    private MessageThread thread;
    private String parentUI;

    public MessageFragment() {
        // Required empty public constructor
    }

    public static MessageFragment newInstance(long threadId, String from)
    {
        MessageFragment fragment = new MessageFragment();
        Bundle args = new Bundle();
        args.putLong(Constant.SessionKeys.MESSAGE_THREAD_ID, threadId);
        args.putString(Constant.SessionKeys.PARENT_UI, from);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            threadId = getArguments().getLong(Constant.SessionKeys.MESSAGE_THREAD_ID);
            parentUI = getArguments().getString(Constant.SessionKeys.PARENT_UI);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_message, container, false);
        setCurrentView(view);
        loadMessageThread();
        setButtons();
        NotificationsHelper.cancelNotification(getActivity(), Constant.NotificationType.MESSAGE);
        return  view;
    }

    private void loadMessageThread()
    {
        final User user = new UserService(getActivity()).GetCurrentUser();
        String url = Constant.BASE_URL + Constant.CONTROLLER_MESSAGE + Constant.SERVICE_GET_THREAD;
        Map<String, String> params = new HashMap<>();
        params.put("ThreadId", String.valueOf(threadId));
        params.put("UserId", String.valueOf(user.Id));

        Map<String,String> headerParams = new HashMap<>();
        headerParams.put("Authorization", "bearer " + new UserService(getActivity()).GetCurrentUser().Token);

        showProgress(getActivity());
        new HttpClientHelper().getResponseStringURLEncoded(getActivity(), url, Request.Method.GET, params, headerParams, new IHttpResponse() {
            @Override
            public void onSuccess(String result) {
                if(result != null)
                {
                    Gson gson = new Gson();
                    thread = gson.fromJson(result, MessageThread.class );
                    ArrayAdapter adapter = new MessageListAdapter(getActivity(), getActivity(), thread.Messages, user.Id);
                    ((ListView)getCurrentView().findViewById(R.id.messageList)).setAdapter(adapter);
                    if(thread.Messages.size() > 0)
                        getCurrentView().findViewById(R.id.empty).setVisibility(GONE);
                    else
                        getCurrentView().findViewById(R.id.empty).setVisibility(View.VISIBLE);

                    if(thread.RequestStatus == Constant.RequestStatus.CLOSED ||
                            thread.RequestStatus == Constant.RequestStatus.EXPIRED)
                    {
                        getCurrentView().findViewById(R.id.acceptButton).setVisibility(GONE);
                        getCurrentView().findViewById(R.id.messageButton).setVisibility(GONE);
                    }
                    else
                    {
                        if(thread.HasQuotation)
                            getCurrentView().findViewById(R.id.acceptButton).setVisibility(View.VISIBLE);
                        else
                            getCurrentView().findViewById(R.id.acceptButton).setVisibility(GONE);
                        getCurrentView().findViewById(R.id.messageButton).setVisibility(View.VISIBLE);
                    }

                    User user = new UserService(getActivity()).GetCurrentUser();
                    if(user.Type == Constant.UserType.SELLER)
                    {
                        getCurrentView().findViewById(R.id.acceptButton).setVisibility(GONE);
                    }

                    if(getCurrentView().findViewById(R.id.acceptButton).getVisibility() == GONE &&
                            getCurrentView().findViewById(R.id.messageButton).getVisibility() == GONE)
                    {
                        ViewGroup.MarginLayoutParams params =
                                (ViewGroup.MarginLayoutParams)getCurrentView().findViewById(R.id.listContainer).getLayoutParams();
                        params.bottomMargin = 0;
                        getCurrentView().findViewById(R.id.listContainer).setLayoutParams(params);
                    }
                }
                hideProgress();
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

    private void setButtons()
    {
        User user = new UserService(getActivity()).GetCurrentUser();
        if(user.Type == Constant.UserType.BUYER)
        {
            getCurrentView().findViewById(R.id.acceptButton).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    final CustomDialog dialog = showAlert(getActivity(), "Confirm", "Are you sure you need to accept the offer?");
                    dialog.OKButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                            acceptQuotation();
                        }
                    });
                    dialog.CancelButton.setVisibility(View.VISIBLE);
                    dialog.CancelButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });

                }
            });


            getCurrentView().findViewById(R.id.messageButton).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(thread != null) {
                        MessageCreateFragment fragment = MessageCreateFragment.newInstance(
                                thread.Id, thread.AgentId, thread.AgentName, thread.RequestId);
                        openFragement(fragment, true);
                    }
                }
            });
        }
        else
        {
            getCurrentView().findViewById(R.id.messageButton).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(thread != null) {
                        MessageCreateFragment fragment = MessageCreateFragment.newInstance(
                                thread.Id, thread.BuyerId, thread.BuyerName, thread.RequestId);
                        openFragement(fragment, true);
                    }
                }
            });
        }
    }

    private void acceptQuotation()
    {
        String url = Constant.BASE_URL + Constant.CONTROLLER_QUOTATION + Constant.SERVICE_QUOTATION_ACCEPT_MTHREAD;
        Map<String, String> params = new HashMap<>();
        params.put("ThreadId", String.valueOf(threadId));

        Map<String,String> headerParams = new HashMap<>();
        headerParams.put("Authorization", "bearer " + new UserService(getActivity()).GetCurrentUser().Token);

        showProgress(getActivity());
        new HttpClientHelper().getResponseStringURLEncoded(getActivity(), url, Request.Method.POST, params, headerParams, new IHttpResponse() {
            @Override
            public void onSuccess(String result) {
                hideProgress();
                Toast.makeText(getActivity(), "Successfully saved and the offer is accepted.", Toast.LENGTH_LONG).show();
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

    @Override
    public boolean onBackPressed() {
        User user = new UserService(getActivity()).GetCurrentUser();
        if(user != null)
        {
            if(parentUI.equals(Constant.ParentUIName.FROM_HOME)) {
                openFragement(BuyerHomeFragment.newInstance(), false);
                return false;
            }
        }

        return true;
    }

}
