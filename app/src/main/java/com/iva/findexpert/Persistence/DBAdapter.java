package com.iva.findexpert.Persistence;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.iva.findexpert.DomainModel.VehicleCategory;
import com.iva.findexpert.Persistence.Tables.InsuranceTypesTable;
import com.iva.findexpert.Persistence.Tables.UserProfileTable;
import com.iva.findexpert.Persistence.Tables.UserTable;
import com.iva.findexpert.Persistence.Tables.VehicleCategoryTable;

/**
 * Created by jayan on 8/20/2014.
 */
public class DBAdapter extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "IVAFindExpert.db";
    private static final int DATABASE_VERSION = 1;

    public DBAdapter(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database)
    {
        UserTable.Create(database);
        InsuranceTypesTable.Create(database);
        VehicleCategoryTable.Create(database);
        UserProfileTable.Create(database);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.i(DBAdapter.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data"
        );
        DropDataBaseTables(db);
        onCreate(db);
    }

    private void DropDataBaseTables(SQLiteDatabase database)
    {
        UserTable.Drop(database);
        InsuranceTypesTable.Drop(database);
        VehicleCategoryTable.Drop(database);
        UserProfileTable.Drop(database);
    }
}
