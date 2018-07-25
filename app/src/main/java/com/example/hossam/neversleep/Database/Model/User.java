package com.example.hossam.neversleep.Database.Model;

import android.provider.BaseColumns;
import android.util.Log;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class User implements Serializable
{
    private int id;
    private String name;
    private int age;
    private boolean gender;
    private Calendar birthDate;

    private Date dateObject;
    public User()
    {
        birthDate = Calendar.getInstance();
    }

    public Calendar getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(int stamp)
    {
        birthDate.setTime(ConvertUnixToTime(stamp));
        Log.i("timestamp",String.valueOf(stamp));
        Log.i("date", String.valueOf(birthDate.get(Calendar.YEAR)));
        ConvertUnixToTime(stamp);
        dateObject = new Date((long)stamp);
    }
    public void setBirthDate(Calendar birthDate)
    {
        this.birthDate = birthDate;
        Log.d("calendar",birthDate.toString());
    }

    public Date getDateObject()
    {
        return dateObject;
    }

    public static Date ConvertUnixToTime(long unix_seconds)
    {
        Date date = new Date(unix_seconds*1000L);
        // format of the date
        SimpleDateFormat Sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z");
        Sdf.setTimeZone(TimeZone.getTimeZone("GMT-2"));
        String java_date = Sdf.format(date);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        Log.d("response: ", "ConvertTimeTounix: "+java_date);
        Log.d("response2", calendar.toString());
        return date;
    }

    public static final int MALE = 1;
    public static final int FEMALE = 0;

    public static final class UserContract implements BaseColumns
    {
        public static final String TABLE_NAME = "user";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_AGE = "age";
        public static final String COLUMN_GENDER = "gender";
        public static final String COLUMN_BIRTH_DATE = "birth_date";

        public static final String CREATE_TABLE =
                "CREATE TABLE " + TABLE_NAME + "("
                        + UserContract._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                        + COLUMN_NAME + " TEXT,"
                        + COLUMN_AGE + " INTEGER,"
                        +COLUMN_GENDER + " INTEGER,"
                        +COLUMN_BIRTH_DATE + " INTEGER"
                        + ")";
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public boolean getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = (gender>0)?true:false;
    }
}
