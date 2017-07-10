package com.iva.findexpert.Service;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.iva.findexpert.DomainModel.User;
import com.iva.findexpert.Persistence.Tables.UserTable;

/**
 * Created by LENOVO on 10/29/2016.
 */

public class UserService extends BaseService {

    String[] columns = {
            UserTable.USER_ID,
            UserTable.REMOTE_ID,
            UserTable.USER_NAME,
            UserTable.PASSWORD,
            UserTable.PHONE,
            UserTable.USER_TYPE,
            UserTable.COMPANY_ID,
            UserTable.TOKEN,
            UserTable.CONNECTION_ID,
            UserTable.IS_AUTHENTICATED,
            UserTable.PROFILE_ID
            };

    public UserService( Context context)
    {
        super(context);
    }

    public long Insert(User user)
    {
        ContentValues values = new ContentValues();
        values.put(UserTable.USER_ID, user.Id);
        values.put(UserTable.REMOTE_ID, user.RemoteId);
        values.put(UserTable.USER_NAME, user.UserName);
        values.put(UserTable.PASSWORD, user.Password);
        values.put(UserTable.PHONE, user.Phone);
        values.put(UserTable.USER_TYPE, user.Type);
        values.put(UserTable.COMPANY_ID, user.CompanyId);
        values.put(UserTable.TOKEN, user.Token);
        values.put(UserTable.CONNECTION_ID, user.ConnectionId);
        values.put(UserTable.PROFILE_ID, user.ProfileId);
        values.put(UserTable.IS_AUTHENTICATED, user.IsAuthenticated ? 1 : 0);

        long id = dbaccess.openDBForWrite().insert(UserTable.TABLE_NAME, null, values);
        dbaccess.close();
        return id;
    }

    public long Update(User user)
    {
        ContentValues values = new ContentValues();
        values.put(UserTable.REMOTE_ID, user.RemoteId);
        values.put(UserTable.USER_ID, user.Id);
        values.put(UserTable.USER_NAME, user.UserName);
        values.put(UserTable.PASSWORD, user.Password);
        values.put(UserTable.PHONE, user.Phone);
        values.put(UserTable.USER_TYPE, user.Type);
        values.put(UserTable.COMPANY_ID, user.CompanyId);
        values.put(UserTable.TOKEN, user.Token);
        values.put(UserTable.CONNECTION_ID, user.ConnectionId);
        values.put(UserTable.PROFILE_ID, user.ProfileId);
        values.put(UserTable.IS_AUTHENTICATED, user.IsAuthenticated ? 1 : 0);

        long id = dbaccess.openDBForWrite().update(UserTable.TABLE_NAME, values, UserTable.USER_ID + " = " + user.Id, null);
        dbaccess.close();
        return id;
    }

    public User GetCurrentUser()
    {
        User user = null;
        Cursor mCursor =  dbaccess.openDBForRead().query(UserTable.TABLE_NAME, columns,
                 null, null, null, null, null);

        if(mCursor != null)
        {
            if(mCursor.getCount() > 0) {
                mCursor.moveToFirst();
                user = getUserFromCursor(mCursor);
            }
            mCursor.close();
        }
        dbaccess.close();

        return user;
    }

    private User getUserFromCursor(Cursor mCursor)
    {
        User user = new User();
        user.Id = mCursor.getLong(mCursor.getColumnIndex(UserTable.USER_ID));
        user.RemoteId = mCursor.getLong(mCursor.getColumnIndex(UserTable.REMOTE_ID));
        user.UserName = mCursor.getString(mCursor.getColumnIndex(UserTable.USER_NAME));
        user.Password = mCursor.getString(mCursor.getColumnIndex(UserTable.PASSWORD));
        user.Phone = mCursor.getString(mCursor.getColumnIndex(UserTable.PHONE));
        user.Token = mCursor.getString(mCursor.getColumnIndex(UserTable.TOKEN));
        user.ConnectionId = mCursor.getString(mCursor.getColumnIndex(UserTable.CONNECTION_ID));
        user.Type = mCursor.getInt(mCursor.getColumnIndex(UserTable.USER_TYPE));
        user.CompanyId = mCursor.getInt(mCursor.getColumnIndex(UserTable.COMPANY_ID));
        user.ProfileId = mCursor.getLong(mCursor.getColumnIndex(UserTable.PROFILE_ID));
        user.IsAuthenticated = mCursor.getInt(mCursor.getColumnIndex(UserTable.IS_AUTHENTICATED)) == 1 ? true : false;

        return user;
    }

    public void DeleteAll()
    {
        dbaccess.openDBForWrite().delete(UserTable.TABLE_NAME, null, null);
        dbaccess.close();
    }

}
