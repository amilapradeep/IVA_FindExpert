package com.iva.findexpert.Persistence;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by jayan on 8/20/2014.
 */
public class DBAccess {

    private DBAdapter adaptor;

    public DBAccess(Context context)
    {
        adaptor = new DBAdapter(context);
    }

    public SQLiteDatabase openDBForWrite(){
        return adaptor.getWritableDatabase();
    }

    public SQLiteDatabase openDBForRead(){
        return adaptor.getReadableDatabase();
    }

    public void close(){
        adaptor.close();
    }

}
