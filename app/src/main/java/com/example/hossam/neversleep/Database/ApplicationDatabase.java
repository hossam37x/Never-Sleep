package com.example.hossam.neversleep.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.hossam.neversleep.Database.Model.Record;
import com.example.hossam.neversleep.Database.Model.User;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class ApplicationDatabase extends SQLiteOpenHelper
{

    public static final String DATABASE_NAME = "user_database";
    public static final int DATABASE_VERSION = 2;


    public ApplicationDatabase(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public ApplicationDatabase(Context context, String name, SQLiteDatabase.CursorFactory factory, int version, DatabaseErrorHandler errorHandler) {
        super(context, name, factory, version, errorHandler);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(User.UserContract.CREATE_TABLE);
        db.execSQL(Record.RecordContract.CREATE_TABLE);
    }

    public long insertUser(User user)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(User.UserContract.COLUMN_NAME,user.getName());
        contentValues.put(User.UserContract.COLUMN_AGE,user.getAge());
        contentValues.put(User.UserContract.COLUMN_GENDER,(user.getGender())?1:0);
        contentValues.put(User.UserContract.COLUMN_BIRTH_DATE, user.getBirthDate().getTimeInMillis()/1000);
        long id = db.insert(User.UserContract.TABLE_NAME, null, contentValues);
        return id;
    }

    public void updateUser(User user)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(User.UserContract.COLUMN_NAME,user.getName());
        contentValues.put(User.UserContract.COLUMN_AGE,user.getAge());
        contentValues.put(User.UserContract.COLUMN_GENDER,(user.getGender())?1:0);
        contentValues.put(User.UserContract.COLUMN_BIRTH_DATE, user.getBirthDate().getTimeInMillis()/1000);
        db.update(User.UserContract.TABLE_NAME, contentValues,"_id="+user.getId(),null);

    }


    public long insertRecord(Record record)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(Record.RecordContract.COLUMN_USERID, record.getUser_id());
        contentValues.put(Record.RecordContract.COLUMN_AVG_HEARTRATE, record.getAvg_heart_rate());
        contentValues.put(Record.RecordContract.COLUMN_MIN_HEARTRATE,record.getMin_heart_rate());
        contentValues.put(Record.RecordContract.COLUMN_MAX_HEARTRATE,record.getMax_heart_rate());
        long id = db.insert(Record.RecordContract.TABLE_NAME, null, contentValues);

        return id;
    }

    public List<User> getAllUsers()
    {
        List<User> users = new ArrayList<>();
        SQLiteDatabase db = getWritableDatabase();
        String selectQuery = "Select * from "+ User.UserContract.TABLE_NAME
                +" ORDER BY " + User.UserContract.COLUMN_NAME + " ASC";
        Cursor cursor = db.rawQuery(selectQuery, null);
        if(cursor.moveToFirst())
            do {
                User user = new User();
                user.setId(cursor.getInt(0));
                user.setName(cursor.getString(1));
                user.setAge(cursor.getInt(cursor.getColumnIndex(User.UserContract.COLUMN_AGE)));
                user.setGender(cursor.getInt(cursor.getColumnIndex(User.UserContract.COLUMN_GENDER)));
                user.setBirthDate(cursor.getInt(4));
                users.add(user);
            }
            while (cursor.moveToNext());
        cursor.close();

        return users;
    }

    public List<Record> getAllUserRecords(User user)
    {
        List<Record> userRecords = new ArrayList<>();
        SQLiteDatabase db = getWritableDatabase();
        String selectQuery = "Select * from "+ Record.RecordContract.TABLE_NAME
                + " where "+ Record.RecordContract.COLUMN_USERID+" = "+user.getId();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if(cursor.moveToFirst())
            do {
                Record userRecord = new Record();
                userRecord.setId(cursor.getInt(0));
                userRecord.setUser_id(cursor.getInt(cursor.getColumnIndex(Record.RecordContract.COLUMN_USERID)));
                userRecord.setAvg_heart_rate(cursor.getInt(cursor.getColumnIndex(Record.RecordContract.COLUMN_AVG_HEARTRATE)));
                userRecord.setMin_heart_rate(cursor.getInt(cursor.getColumnIndex(Record.RecordContract.COLUMN_MIN_HEARTRATE)));
                userRecord.setMax_heart_rate(cursor.getInt(cursor.getColumnIndex(Record.RecordContract.COLUMN_MAX_HEARTRATE)));
                userRecord.setDate(cursor.getString(cursor.getColumnIndex(Record.RecordContract.COLUMN_DATE)));
                userRecords.add(userRecord);
            }
            while (cursor.moveToNext());
        cursor.close();

        return userRecords;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public User getUser(long id)
    {
        SQLiteDatabase db = getWritableDatabase();
        String selectQuery = "Select * from "+ User.UserContract.TABLE_NAME
                +" Where "+ User.UserContract._ID + "="+ String.valueOf(id);
        User user = null;
        Cursor cursor = db.rawQuery(selectQuery, null);
        if(cursor.moveToFirst())
            do {
                user = new User();
                user.setId(cursor.getInt(0));
                user.setName(cursor.getString(1));
                user.setAge(cursor.getInt(cursor.getColumnIndex(User.UserContract.COLUMN_AGE)));
                user.setGender(cursor.getInt(cursor.getColumnIndex(User.UserContract.COLUMN_GENDER)));
                user.setBirthDate(cursor.getInt(4));
                }
            while (cursor.moveToNext());
        cursor.close();
        db.close();
        return user;
    }

    public void deleteRecords(int userID)
    {
        SQLiteDatabase db = getWritableDatabase();
        String deleteQuery = "DELETE FROM "+ Record.RecordContract.TABLE_NAME + " WHERE "+ Record.RecordContract.COLUMN_USERID + "='"+String.valueOf(userID) + "'";
        db.execSQL(deleteQuery);

    }

    public void deleteRecord(int recordID)
    {
        SQLiteDatabase db = getWritableDatabase();
        String deleteQuery = "DELETE FROM "+ Record.RecordContract.TABLE_NAME + " WHERE "+ Record.RecordContract._ID + "='"+String.valueOf(recordID) + "'";
        db.execSQL(deleteQuery);

    }

    public void deleteUser(int userID)
    {
        SQLiteDatabase db = getWritableDatabase();
        deleteRecords(userID);
        String deleteQuery = "DELETE FROM "+ User.UserContract.TABLE_NAME + " WHERE "+ User.UserContract._ID + " = '"+String.valueOf(userID) + "'";
        db.execSQL(deleteQuery);

    }

    public static String getDateCurrentTimeZone(long timestamp) {
        try{
            Calendar calendar = Calendar.getInstance();
            TimeZone tz = TimeZone.getDefault();
            calendar.setTimeInMillis(timestamp * 1000);
            calendar.add(Calendar.MILLISECOND, tz.getOffset(calendar.getTimeInMillis()));
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date currenTimeZone = (Date) calendar.getTime();
            return sdf.format(currenTimeZone);
        }catch (Exception e) {
        }
        return "";
    }

    public static String getCurrentTimeStamp()
    {
        Long TimeStamp = System.currentTimeMillis()/1000;
        return TimeStamp.toString();
    }


}
