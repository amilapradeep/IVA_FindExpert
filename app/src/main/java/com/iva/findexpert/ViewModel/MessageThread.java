package com.iva.findexpert.ViewModel;

import java.util.List;

/**
 * Created by LENOVO on 12/10/2016.
 */

public class MessageThread {

    public long Id;
    public long RequestId;
    public long BuyerId;
    public String BuyerName;
    public long AgentId;
    public String AgentName;
    public String CompanyName;
    public String VehicleNo;
    public String Description;
    public String Date;
    public List<Message> Messages;
    public PromotionViewModel Promotion;
    public boolean HasQuotation;
    public int RequestStatus;
    public int UnreadMessageCount;
}
