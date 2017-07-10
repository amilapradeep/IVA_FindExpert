package com.iva.findexpert.Service;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.iva.findexpert.DomainModel.UserProfile;
import com.iva.findexpert.Persistence.Tables.UserProfileTable;

/**
 * Created by jayan on 09/12/2016.
 */

public class UserProfileService extends BaseService {

    String[] columns = {
            UserProfileTable.ID,
            UserProfileTable.USER_ID,
            UserProfileTable.FIRST_NAME,
            UserProfileTable.LAST_NAME,
            UserProfileTable.GENDER,
            UserProfileTable.EMAIL,
            UserProfileTable.PHONE,
            UserProfileTable.MOBILE,
            UserProfileTable.STREET,
            UserProfileTable.CITY,
            UserProfileTable.IMAGE,
            UserProfileTable.CONTACT_METHOD,
            UserProfileTable.BANK_ID,
            UserProfileTable.ACCOUNT_NAME,
            UserProfileTable.ACCOUNT_NO,
            UserProfileTable.BANK_BRANCH
    };

    public UserProfileService(Context context)
    {
        super(context);
    }

    public long Insert(UserProfile profile)
    {
        ContentValues values = new ContentValues();
        values.put( UserProfileTable.ID, profile.Id);
        values.put( UserProfileTable.USER_ID, profile.UserId);
        values.put( UserProfileTable.FIRST_NAME, profile.FirstName);
        values.put( UserProfileTable.LAST_NAME, profile.LastName);
        values.put( UserProfileTable.GENDER, profile.Gender);
        values.put( UserProfileTable.EMAIL, profile.Email);
        values.put( UserProfileTable.PHONE, profile.Phone);
        values.put( UserProfileTable.MOBILE, profile.Mobile);
        values.put( UserProfileTable.STREET, profile.Street);
        values.put( UserProfileTable.CITY, profile.City);
        values.put( UserProfileTable.IMAGE, profile.Image);
        values.put( UserProfileTable.CONTACT_METHOD, profile.ContactMethod);
        values.put( UserProfileTable.BANK_ID, profile.BankId);
        values.put( UserProfileTable.ACCOUNT_NAME, profile.AccountName);
        values.put( UserProfileTable.ACCOUNT_NO, profile.AccountNo);
        values.put( UserProfileTable.BANK_BRANCH, profile.BankBranch);

        long id = dbaccess.openDBForWrite().insert(UserProfileTable.TABLE_NAME, null, values);
        dbaccess.close();
        return id;
    }

    public long Update(UserProfile profile)
    {
        ContentValues values = new ContentValues();
        values.put( UserProfileTable.ID, profile.Id);
        values.put( UserProfileTable.USER_ID, profile.UserId);
        values.put( UserProfileTable.FIRST_NAME, profile.FirstName);
        values.put( UserProfileTable.LAST_NAME, profile.LastName);
        values.put( UserProfileTable.GENDER, profile.Gender);
        values.put( UserProfileTable.EMAIL, profile.Email);
        values.put( UserProfileTable.PHONE, profile.Phone);
        values.put( UserProfileTable.MOBILE, profile.Mobile);
        values.put( UserProfileTable.STREET, profile.Street);
        values.put( UserProfileTable.CITY, profile.City);
        values.put( UserProfileTable.IMAGE, profile.Image);
        values.put( UserProfileTable.CONTACT_METHOD, profile.ContactMethod);
        values.put( UserProfileTable.BANK_ID, profile.BankId);
        values.put( UserProfileTable.ACCOUNT_NAME, profile.AccountName);
        values.put( UserProfileTable.ACCOUNT_NO, profile.AccountNo);
        values.put( UserProfileTable.BANK_BRANCH, profile.BankBranch);

        long id = dbaccess.openDBForWrite().update(UserProfileTable.TABLE_NAME, values, UserProfileTable.ID + " = " + profile.Id, null);
        dbaccess.close();
        return id;
    }

    public UserProfile GetCurrentUserProfile()
    {
        UserProfile profile = null;
        Cursor mCursor =  dbaccess.openDBForRead().query(UserProfileTable.TABLE_NAME, columns,
                null, null, null, null, null);

        if(mCursor != null)
        {
            if(mCursor.getCount() > 0) {
                mCursor.moveToFirst();
                profile = getUserFromCursor(mCursor);
            }
            mCursor.close();
        }
        dbaccess.close();
        return profile;
    }

    private UserProfile getUserFromCursor(Cursor mCursor)
    {
        UserProfile profile = new UserProfile();
        profile.Id = mCursor.getLong(mCursor.getColumnIndex(UserProfileTable.ID));
        profile.UserId = mCursor.getLong(mCursor.getColumnIndex(UserProfileTable.USER_ID));
        profile.FirstName = mCursor.getString(mCursor.getColumnIndex(UserProfileTable.FIRST_NAME));
        profile.LastName = mCursor.getString(mCursor.getColumnIndex(UserProfileTable.LAST_NAME));
        profile.Gender = mCursor.getInt(mCursor.getColumnIndex(UserProfileTable.GENDER));
        profile.Email = mCursor.getString(mCursor.getColumnIndex(UserProfileTable.EMAIL));
        profile.Phone = mCursor.getString(mCursor.getColumnIndex(UserProfileTable.PHONE));
        profile.Mobile = mCursor.getString(mCursor.getColumnIndex(UserProfileTable.MOBILE));
        profile.Street = mCursor.getString(mCursor.getColumnIndex(UserProfileTable.STREET));
        profile.City = mCursor.getString(mCursor.getColumnIndex(UserProfileTable.CITY));
        profile.Image = mCursor.getBlob(mCursor.getColumnIndex(UserProfileTable.IMAGE));
        profile.ContactMethod = mCursor.getInt(mCursor.getColumnIndex(UserProfileTable.CONTACT_METHOD));
        profile.BankId = mCursor.getInt(mCursor.getColumnIndex(UserProfileTable.BANK_ID));
        profile.BankBranch = mCursor.getString(mCursor.getColumnIndex(UserProfileTable.BANK_BRANCH));
        profile.AccountName = mCursor.getString(mCursor.getColumnIndex(UserProfileTable.ACCOUNT_NAME));
        profile.AccountNo = mCursor.getString(mCursor.getColumnIndex(UserProfileTable.ACCOUNT_NO));

        return profile;
    }

    public void DeleteAll()
    {
        dbaccess.openDBForWrite().delete(UserProfileTable.TABLE_NAME, null, null);
        dbaccess.close();
    }

}
