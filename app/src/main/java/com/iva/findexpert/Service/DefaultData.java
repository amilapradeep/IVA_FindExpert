package com.iva.findexpert.Service;

import android.content.Context;

/**
 * Created by LENOVO on 11/1/2016.
 */

public class DefaultData extends BaseService {

    public DefaultData(Context context) { super(context);}

    public void Add()
    {
        InsuranceTypesService its = new InsuranceTypesService(context);
        its.DeleteAll();
        its.DefaultData();
        VehicleCategoryService cs = new VehicleCategoryService(context);
        cs.DeleteAll();
        cs.DefaultData();
    }

}
