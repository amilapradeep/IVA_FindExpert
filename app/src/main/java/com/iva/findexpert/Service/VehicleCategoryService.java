package com.iva.findexpert.Service;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.iva.findexpert.DomainModel.VehicleCategory;
import com.iva.findexpert.Persistence.Tables.VehicleCategoryTable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by LENOVO on 11/1/2016.
 */

public class VehicleCategoryService extends BaseService {

    public VehicleCategoryService(Context context) { super(context);}

    String[] columns = {
            VehicleCategoryTable.ID,
            VehicleCategoryTable.NAME,
            VehicleCategoryTable.VALUE,
            VehicleCategoryTable.DESCRIPTION,
            VehicleCategoryTable.IS_ACTIVE};

    public  void Insert(VehicleCategory category)
    {
        ContentValues values = new ContentValues();
        values.put(VehicleCategoryTable.ID, category.Id);
        values.put(VehicleCategoryTable.VALUE, category.Value);
        values.put(VehicleCategoryTable.NAME, category.Name);
        values.put(VehicleCategoryTable.DESCRIPTION, category.Description);
        values.put(VehicleCategoryTable.IS_ACTIVE, category.IsActive ? 1 : 0);

        dbaccess.openDBForWrite().insert(VehicleCategoryTable.TABLE_NAME, null, values);
        dbaccess.close();
    }

    public VehicleCategory GetById(int Id)
    {
        VehicleCategory category = null;
        Cursor mCursor =  dbaccess.openDBForRead().query(VehicleCategoryTable.TABLE_NAME, columns,
                VehicleCategoryTable.ID + " = " + String.valueOf(Id), null, null, null, null);

        if(mCursor != null && mCursor.getCount() > 0)
        {
            mCursor.moveToFirst();
            category = this.getFromCursor(mCursor);
            mCursor.close();
        }
        dbaccess.close();
        return category;
    }

    public List<VehicleCategory> GetAll()
    {
        List<VehicleCategory> categories = new ArrayList<>();
        VehicleCategory category = null;
        Cursor mCursor =  dbaccess.openDBForRead().query(VehicleCategoryTable.TABLE_NAME, columns,
                VehicleCategoryTable.IS_ACTIVE + " = 1", null, null, null, null);

        if(mCursor != null)
        {
            mCursor.moveToFirst();
            while (!mCursor.isAfterLast()) {
                category = this.getFromCursor(mCursor);
                categories.add(category);
                mCursor.moveToNext();
            }
            mCursor.close();
        }
        dbaccess.close();
        return categories;
    }

    private VehicleCategory getFromCursor(Cursor cursor)
    {
        VehicleCategory category = new VehicleCategory();
        category.Id = cursor.getInt(cursor.getColumnIndex(VehicleCategoryTable.ID));
        category.Name = cursor.getString(cursor.getColumnIndex(VehicleCategoryTable.NAME));
        category.Value = cursor.getString(cursor.getColumnIndex(VehicleCategoryTable.VALUE));
        category.Description = cursor.getString(cursor.getColumnIndex(VehicleCategoryTable.DESCRIPTION));
        category.IsActive = cursor.getInt(cursor.getColumnIndex(VehicleCategoryTable.IS_ACTIVE)) == 1 ? true : false;
        return category;
    }

    public void DeleteAll()
    {
        dbaccess.openDBForWrite().delete(VehicleCategoryTable.TABLE_NAME, null, null);
        dbaccess.close();
    }

    public void DefaultData()
    {
        VehicleCategory cat = new VehicleCategory();
        cat.Id = 1;
        cat.Name = "Motor Car";
        cat.Value = "1";
        cat.Description = "";
        cat.IsActive = true;
        Insert(cat);

        cat = new VehicleCategory();
        cat.Id = 2;
        cat.Name = "Dual Purpose Vehicle";
        cat.Value = "2";
        cat.Description = "";
        cat.IsActive = true;
        Insert(cat);

        cat = new VehicleCategory();
        cat.Id = 3;
        cat.Name = "Motor Lorry";
        cat.Value = "3";
        cat.Description = "";
        cat.IsActive = true;
        Insert(cat);

        cat = new VehicleCategory();
        cat.Id = 4;
        cat.Name = "Motor Cycle";
        cat.Value = "4";
        cat.Description = "";
        cat.IsActive = true;
        Insert(cat);

        cat = new VehicleCategory();
        cat.Id = 5;
        cat.Name = "Motor Three Wheelers";
        cat.Value = "5";
        cat.Description = "";
        cat.IsActive = true;
        Insert(cat);

        cat = new VehicleCategory();
        cat.Id = 6;
        cat.Name = "Motor Bus";
        cat.Value = "6";
        cat.Description = "";
        cat.IsActive = true;
        Insert(cat);

        cat = new VehicleCategory();
        cat.Id = 7;
        cat.Name = "Prime Movers";
        cat.Value = "7";
        cat.Description = "";
        cat.IsActive = true;
        Insert(cat);

        cat = new VehicleCategory();
        cat.Id = 8;
        cat.Name = "Land Vehicles";
        cat.Value = "8";
        cat.Description = "";
        cat.IsActive = true;
        Insert(cat);

    }
}
