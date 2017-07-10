package com.iva.findexpert.Persistence.Tables;

import android.database.sqlite.SQLiteDatabase;

/**
 * Created by LENOVO on 11/1/2016.
 */

public class VehicleCategoryTable {
    public static final String TABLE_NAME = "VehicleCategory";
    public static final String ID = "Id";
    public static final String NAME = "Name";
    public static final String VALUE = "Value";
    public static final String DESCRIPTION = "Description";
    public static final String IS_ACTIVE = "IsActive";

    public static void Create(SQLiteDatabase db)
    {
        String TABLE_CREATE =
                "CREATE TABLE IF NOT EXISTS " + TABLE_NAME +
                        "(" + ID + " INTEGER PRIMARY KEY NOT NULL, "
                        + NAME + " VARCHAR NOT NULL, "
                        + VALUE + " VARCHAR NOT NULL, "
                        + DESCRIPTION + " VARCHAR NULL, "
                        + IS_ACTIVE + " INTEGER NOT NULL"
                        + " );";
        db.execSQL(TABLE_CREATE);
    }

    public static void Drop(SQLiteDatabase db)
    {
        String TABLE_DROP = "DROP TABLE IF EXISTS " + TABLE_NAME;
        db.execSQL(TABLE_DROP);
    }

}
