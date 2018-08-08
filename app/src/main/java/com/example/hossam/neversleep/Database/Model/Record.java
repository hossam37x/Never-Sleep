package com.example.hossam.neversleep.Database.Model;

import android.provider.BaseColumns;


public class Record
{
    private int id;

    private  int user_id;

    private int avg_heart_rate;

    private int min_heart_rate;

    private int max_heart_rate;

    public static final class RecordContract implements BaseColumns
    {
        public static final String TABLE_NAME = "record";
        public static final String COLUMN_USERID = "user_id";
        public static final String COLUMN_AVG_HEARTRATE = "avg_heart_rate";
        public static final String COLUMN_MIN_HEARTRATE = "min_heart_rate";
        public static final String COLUMN_MAX_HEARTRATE = "max_heart_rate";
        public static final String COLUMN_DATE = "date";



        public static final String CREATE_TABLE =
                "CREATE TABLE " + TABLE_NAME + "("
                        + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                        + COLUMN_USERID + " INTEGER,"
                        + COLUMN_AVG_HEARTRATE + " INTEGER,"
                        + COLUMN_MIN_HEARTRATE + " INTEGER,"
                        + COLUMN_MAX_HEARTRATE + " INTEGER,"
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

    public int getAvg_heart_rate()
    {
        return avg_heart_rate;
    }

    public void setAvg_heart_rate(int avg_heart_rate)
    {
        this.avg_heart_rate = avg_heart_rate;
    }

    public int getMin_heart_rate() {
        return min_heart_rate;
    }

    public void setMin_heart_rate(int min_heart_rate) {
        this.min_heart_rate = min_heart_rate;
    }

    public int getMax_heart_rate() {
        return max_heart_rate;
    }

    public void setMax_heart_rate(int max_heart_rate) {
        this.max_heart_rate = max_heart_rate;
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
