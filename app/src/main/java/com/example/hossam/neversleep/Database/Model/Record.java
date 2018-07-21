package com.example.hossam.neversleep.Database.Model;

import android.provider.BaseColumns;

import java.util.Date;


public class Record
{
    private int id;

    private  int user_id;

    private int heart_rate;
    public static final class RecordContract implements BaseColumns
    {
        public static final String TABLE_NAME = "record";
        public static final String COLUMN_USERID = "user_id";
        public static final String COLUMN_HEARTRATE = "heart_rate";
        public static final String COLUMN_DATE = "date";


        public static final String CREATE_TABLE =
                "CREATE TABLE " + TABLE_NAME + "("
                        + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                        + COLUMN_USERID + " INTEGER,"
                        + COLUMN_HEARTRATE + " INTEGER,"
                        + COLUMN_DATE + " DATETIME DEFAULT CURRENT_TIMESTAMP,"
                        +" FOREIGN KEY ("+COLUMN_USERID+") REFERENCES "
                        + User.UserContract.TABLE_NAME+"("+ User.UserContract._ID+"));";
    }
    private String date;

    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public int getUser_id()
    {
        return user_id;
    }

    public void setUser_id(int user_id)
    {
        this.user_id = user_id;
    }

    public int getHeart_rate()
    {
        return heart_rate;
    }

    public void setHeart_rate(int heart_rate)
    {
        this.heart_rate = heart_rate;
    }

    public String getDate()
    {
        return date;
    }

    public void setDate(String date)
    {
        this.date = date;
    }
}
