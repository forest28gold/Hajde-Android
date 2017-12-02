package com.azizinetwork.hajde.settings;


import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBService extends SQLiteOpenHelper {

    private final static int DATABASE_VERSION = 3;
    private final static String DATABASE_NAME = Global.LOCAL_DB_NAME;

    public void onCreate(SQLiteDatabase db)
    {
        String sql_user = "CREATE TABLE [" + Global.LOCAL_TABLE_USER + "] ("
                + "[id] AUTOINC,"
                + "[" + Global.LOCAL_FIELD_USER_ID + "] TEXT NOT NULL ON CONFLICT FAIL,"
                + "[" + Global.LOCAL_FIELD_DEVICE_UUID + "] TEXT ,"
                + "[" + Global.LOCAL_FIELD_PASSWORD + "] TEXT ,"
                + "[" + Global.LOCAL_FIELD_SIGNUP + "] TEXT ,"
                + "[" + Global.LOCAL_FIELD_LAST_LOGIN + "] TEXT ,"
                + "[" + Global.LOCAL_FIELD_LANGUAGE + "] TEXT ,"
                + "[" + Global.LOCAL_FIELD_COUNTRY + "] TEXT ,"
                + "[" + Global.LOCAL_FIELD_SPENT_TIME + "] TEXT ,"
                + "[" + Global.LOCAL_FIELD_POST_COUNT + "] TEXT ,"
                + "[" + Global.LOCAL_FIELD_COMMENT_COUNT + "] TEXT ,"
                + "[" + Global.LOCAL_FIELD_VOTE_COUNT + "] TEXT ,"
                + "[" + Global.LOCAL_FIELD_KARMA_SCORE + "] TEXT ,"
                + "[" + Global.LOCAL_FIELD_LOGIN_COUNT + "] TEXT )";

        db.execSQL(sql_user);

    }

    public DBService(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        String sql_user = "drop table if exists [" + Global.LOCAL_TABLE_USER + "]";
        db.execSQL(sql_user);

        onCreate(db);
    }

    public void execSQL(String sql, Object[] args)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(sql, args);
    }

    public Cursor query(String sql, String[] args)
    {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(sql, args);
        return cursor;
    }


}
