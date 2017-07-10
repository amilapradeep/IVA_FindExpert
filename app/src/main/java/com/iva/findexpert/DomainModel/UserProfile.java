package com.iva.findexpert.DomainModel;

/**
 * Created by jayan on 07/12/2016.
 */

public class UserProfile {

    public long Id;
    public long UserId;
    public String FirstName;
    public String LastName;
    public int Gender;
    public String Email;
    public String Phone;
    public String Mobile;
    public String Street;
    public String City;
    public transient byte[] Image;
    public int ContactMethod;
    public int BankId;
    public String AccountName;
    public String BankBranch;
    public String AccountNo;
}
