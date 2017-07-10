package com.iva.findexpert.DomainModel;

import com.iva.findexpert.ViewModel.QuotationTemplate;

import java.util.List;

/**
 * Created by LENOVO on 11/14/2016.
 */

public class QuotationRequest {

    public QuotationRequest(){}
    public long Id;
    public String Code;
    public int InsuranceTypeId;
    public String VehicleNo;
    public int ClaimType;
    public int RegistrationCategory;
    public int UsageType;
    public double VehicleValue;
    public int VehicleYear;
    public boolean IsFinanced;
    public long UserId;
    public String CreatedDate;
    public String ExpiryDate;
    public int Status;
    public String BuyerName;
    public String BuyerMobile;
    public boolean IsAllowPhone;
    public String Location;
    public String TimeToExpire;
    public boolean IsFollowUp;

    public List<Quotation> QuotationList;
}
