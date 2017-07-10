package com.iva.findexpert.DomainModel;

/**
 * Created by LENOVO on 12/2/2016.
 */

public class Quotation {

    public long Id;
    public long ServiceRequestId;
    public long QuotationTemplateId;
    public String Premimum;
    public String Cover;
    public String QuotationText;
    public long AgentId;
    public String AgentName;
    public String AgentContact;
    public int Status;
    public int CompanyId;
    public String CompanyName;
    public String QuotationTemplateName;
    public String CreatedTime;
    public String ModifiedTime;
    public long ThreadId;
    public boolean IsExpired;

    public String ServiceRequestCode;
    public int ClaimType;
    public String VehicleNo;
    public double VehicleValue;
}
