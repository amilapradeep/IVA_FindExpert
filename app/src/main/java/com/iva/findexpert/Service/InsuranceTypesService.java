package com.iva.findexpert.Service;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.iva.findexpert.DomainModel.InsuranceType;
import com.iva.findexpert.Persistence.Tables.InsuranceTypesTable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by LENOVO on 11/1/2016.
 */

public class InsuranceTypesService extends BaseService {

    public InsuranceTypesService( Context context)
    {
        super(context);
    }

    String[] columns = {
            InsuranceTypesTable.ID,
            InsuranceTypesTable.NAME,
            InsuranceTypesTable.DESCRIPTION,
            InsuranceTypesTable.IS_ACTIVE};

    public  void Insert(InsuranceType type)
    {
        ContentValues values = new ContentValues();
        values.put(InsuranceTypesTable.ID, type.Id);
        values.put(InsuranceTypesTable.NAME, type.Name);
        values.put(InsuranceTypesTable.DESCRIPTION, type.Description);
        values.put(InsuranceTypesTable.IS_ACTIVE, type.IsActive ? 1 : 0);

        dbaccess.openDBForWrite().insert(InsuranceTypesTable.TABLE_NAME, null, values);
        dbaccess.close();
    }

    public List<InsuranceType> GetAll()
    {
        List<InsuranceType> types = new ArrayList<>();
        InsuranceType intype = null;
        Cursor mCursor =  dbaccess.openDBForRead().query(InsuranceTypesTable.TABLE_NAME, columns,
                null, null, null, null, null);

        if(mCursor != null)
        {
            mCursor.moveToFirst();
            while (!mCursor.isAfterLast()) {
                intype = this.getFromCursor(mCursor);
                types.add(intype);
                mCursor.moveToNext();
            }
            mCursor.close();
        }
        dbaccess.close();
        return types;
    }

    public InsuranceType GetById(int Id)
    {
        InsuranceType intype = null;
        Cursor mCursor =  dbaccess.openDBForRead().query(InsuranceTypesTable.TABLE_NAME, columns,
                InsuranceTypesTable.ID + " = " + String.valueOf(Id), null, null, null, null);

        if(mCursor != null && mCursor.getCount() > 0)
        {
            mCursor.moveToFirst();
            intype = this.getFromCursor(mCursor);
            mCursor.close();
        }
        dbaccess.close();
        return intype;
    }

    private InsuranceType getFromCursor(Cursor cursor)
    {
        InsuranceType intype = new InsuranceType();
        intype.Id = cursor.getInt(cursor.getColumnIndex("Id"));
        intype.Name = cursor.getString(cursor.getColumnIndex("Name"));
        //intype.Description = cursor.getString(cursor.getColumnIndex(InsuranceTypesTable.DESCRIPTION));
        //intype.IsActive = cursor.getInt(cursor.getColumnIndex(InsuranceTypesTable.IS_ACTIVE)) == 1 ? true : false;
        return intype;
    }

    public void DeleteAll()
    {
        dbaccess.openDBForWrite().delete(InsuranceTypesTable.TABLE_NAME, null, null);
        dbaccess.close();
    }

    public void DefaultData()
    {
        InsuranceType t = new InsuranceType();
        t.Id = 1;
        t.Name = "Vehicle Insurance";
        t.Description = "Vehicle Insurance";
        t.IsActive = true;
        this.Insert(t);

        t = new InsuranceType();
        t.Id = 2;
        t.Name = "Travel Insurance";
        t.Description = "Travel Insurance";
        t.IsActive = true;
        this.Insert(t);

        t = new InsuranceType();
        t.Id = 3;
        t.Name = "Life Insurance";
        t.Description = "Life Insurance";
        t.IsActive = true;
        this.Insert(t);

    }



}
