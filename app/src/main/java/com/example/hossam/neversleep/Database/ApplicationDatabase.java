package com.example.hossam.neversleep.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.ContactsContract;

import com.example.hossam.neversleep.Database.Model.Record;
import com.example.hossam.neversleep.Database.Model.User;

import java.util.ArrayList;
import java.util.List;

public class ApplicationDatabase extends SQLiteOpenHelper
{

    public static final String DATABASE_NAME = "user_database";
    public static final int DATABASE_VERSION = 1;


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
        long id = db.insert(User.UserContract.TABLE_NAME, null, contentValues);
        db.close();
        return id;
    }

    public long insertRecord(Record record)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(Record.RecordContract.COLUMN_USERID, record.getUser_id());
        contentValues.put(Record.RecordContract.COLUMN_HEARTRATE, record.getHeart_rate());
        long id = db.insert(Record.RecordContract.TABLE_NAME, null, contentValues);
        db.close();
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
                users.add(user);
            }
            while (cursor.moveToNext());
        cursor.close();
        db.close();
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
                userRecord.setHeart_rate(cursor.getInt(cursor.getColumnIndex(Record.RecordContract.COLUMN_HEARTRATE)));
                userRecord.setDate(cursor.getString(cursor.getColumnIndex(Record.RecordContract.COLUMN_DATE)));
                userRecords.add(userRecord);
            }
            while (cursor.moveToNext());
        cursor.close();
        db.close();
        return userRecords;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public User getUser(long id) {
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
            }
            while (cursor.moveToNext());
        cursor.close();
        db.close();
        return user;
    }

    public void deleteRecords(int userID)
    {
        SQLiteDatabase db = getWritableDatabase();
        String deleteQuery = "Delete from "+ Record.RecordContract.TABLE_NAME
                + " where "+ Record.RecordContract.COLUMN_USERID + " = "+String.valueOf(userID);
        db.execSQL(deleteQuery, null);
        db.close();
    }

    public void deleteUser(int userID)
    {
        SQLiteDatabase db = getWritableDatabase();
        deleteRecords(userID);
        String deleteQuery = "Delete from "+ User.UserContract.TABLE_NAME
                + " where "+ User.UserContract._ID + " = "+String.valueOf(userID);
        db.execSQL(deleteQuery, null);
        db.close();
    }

}
