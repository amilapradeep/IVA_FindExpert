package com.iva.findexpert.Service;

import android.content.Context;

import com.iva.findexpert.Persistence.DBAccess;

public abstract class BaseService {

    public Context context;
    public DBAccess dbaccess;

    public BaseService(Context context){
        this.context = context;
        dbaccess = new DBAccess(context);
    }
}
