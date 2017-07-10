package com.iva.findexpert.Persistence.Tables;

import android.database.sqlite.SQLiteDatabase;

/**
 * Created by jayan on 07/12/2016.
 */

public class UserProfileTable  {

    public static final String TABLE_NAME = "UserProfile";
    public static final String ID = "Id";
    public static final String USER_ID = "UserId";
    public static final String FIRST_NAME = "FirstName";
    public static final String LAST_NAME = "LastName";
    public static final String GENDER = "Gender";
    public static final String EMAIL = "Email";
    public static final String PHONE = "Phone";
    public static final String MOBILE = "Mobile";
    public static final String STREET = "Street";
    public static final String CITY = "Location";
    public static final String IMAGE = "Image";
    public static final String CONTACT_METHOD = "ContactMethod";
    public static final String BANK_ID = "BankId";
    public static final String ACCOUNT_NAME = "AccountName";
    public static final String ACCOUNT_NO = "AccountNo";
    public static final String BANK_BRANCH = "BankBranch";

    public UserProfileTable(){}

    public static void Create(SQLiteDatabase db)
    {
        String TABLE_CREATE =
                "CREATE TABLE IF NOT EXISTS " + TABLE_NAME +
                        "(" + ID + " INTEGER PRIMARY KEY NOT NULL, "
                        + USER_ID + " NOT NULL, "
                        + FIRST_NAME + " VARCHAR NULL, "
                        + LAST_NAME + " VARCHAR NULL, "
                        + GENDER + " INTEGER NULL, "
                        + EMAIL + " VARCHAR NULL, "
                        + PHONE + " VARCHAR NULL, "
                        + MOBILE + " VARCHAR NULL, "
                        + STREET + " VARCHAR NULL, "
                        + CITY + " VARCHAR NULL, "
                        + IMAGE + " BLOB NULL, "
                        + CONTACT_METHOD + " INTEGER NULL, "
                        + BANK_ID + " INTEGER NULL, "
                        + ACCOUNT_NAME + " VARCHAR NULL, "
                        + ACCOUNT_NO + " VARCHAR NULL, "
                        + BANK_BRANCH + " VARCHAR NULL"
                        + " );";
        db.execSQL(TABLE_CREATE);
    }

    public static void Drop(SQLiteDatabase db)
    {
        String TABLE_DROP = "DROP TABLE IF EXISTS " + TABLE_NAME;
        db.execSQL(TABLE_DROP);
    }


}
