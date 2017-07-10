package com.iva.findexpert.UI.Helpers;

import com.iva.findexpert.Common.Constant;

/**
 * Created by LENOVO on 12/2/2016.
 */

public class Insurance {

    public static String getClaimTypeName(int type)
    {
        String t = "";
        switch (type)
        {
            case Constant.ClaimType.COMPREHENSIVE:
                t = Constant.ClaimTypeName.COMPREHENSIVE;
            break;

            case Constant.ClaimType.THIRD_PARTY:
                t = Constant.ClaimTypeName.THIRD_PARTY;
                break;

            default:
                t = Constant.ClaimTypeName.COMPREHENSIVE;
                break;
        }

        return t;
    }

    public static String getUsageName(int type)
    {
        String t = "";
        switch (type)
        {
            case Constant.Usage.PRIVATE:
                t = Constant.Usage.PRIVATE_NAME;
                break;

            case Constant.Usage.HIRING:
                t = Constant.Usage.HIRING_NAME;
                break;

            case Constant.Usage.RENT:
                t = Constant.Usage.HIRING_NAME;
                break;

            default:
                t = Constant.Usage.RENT_NAME;
                break;
        }

        return t;
    }

}
