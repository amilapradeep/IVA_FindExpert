package com.iva.findexpert.Persistence.Tables;

import android.database.sqlite.SQLiteDatabase;

/**
 * Created by jayan on 8/20/2014.
 */
public class UserTable {

    public static final String TABLE_NAME = "AppUser";
    public static final String USER_ID = "UserID";
    public static final String REMOTE_ID = "RemoteID";
    public static final String USER_NAME = "UserName";
    public static final String PHONE = "Phone";
    public static final String PASSWORD = "Password";
    public static final String USER_TYPE = "Type";
    public static final String COMPANY_ID = "CompanyId";
    public static final String TOKEN = "Token";
    public static final String CONNECTION_ID = "ConnectionId";
    public static final String IS_AUTHENTICATED = "IsAuthenticated";
    public static final String PROFILE_ID = "ProfileId";


    public UserTable(){}

    public static void Create(SQLiteDatabase db)
    {
        String TABLE_CREATE =
                "CREATE TABLE IF NOT EXISTS " + TABLE_NAME +
                        "(" + USER_ID + " INTEGER PRIMARY KEY NOT NULL, "
                        + REMOTE_ID + " INTEGER NOT NULL, "
                        + USER_NAME + " VARCHAR NOT NULL, "
                        + PHONE + " VARCHAR NOT NULL, "
                        + PASSWORD + " VARCHAR NULL, "
                        + USER_TYPE + " INTEGER NOT NULL, "
                        + COMPANY_ID + " INTEGER NULL, "
                        + TOKEN + " VARCHAR NULL, "
                        + CONNECTION_ID + " VARCHAR NULL, "
                        + IS_AUTHENTICATED + " INTEGER NOT NULL, "
                        + PROFILE_ID + " INTEGER NULL"
                        + " );";
        db.execSQL(TABLE_CREATE);
    }

    public static void Drop(SQLiteDatabase db)
    {
        String TABLE_DROP = "DROP TABLE IF EXISTS " + TABLE_NAME;
        db.execSQL(TABLE_DROP);
    }
}
