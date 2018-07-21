package com.example.hossam.neversleep.Database.Model;

import android.provider.BaseColumns;

import java.io.Serializable;

public class User implements Serializable
{
    private int id;
    private String name;
    private int age;
    private boolean gender;

    public static final int MALE = 1;
    public static final int FEMALE = 0;

    public static final class UserContract implements BaseColumns
    {
        public static final String TABLE_NAME = "user";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_AGE = "age";
        public static final String COLUMN_GENDER = "gender";

        public static final String CREATE_TABLE =
                "CREATE TABLE " + TABLE_NAME + "("
                        + UserContract._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                        + COLUMN_NAME + " TEXT,"
                        + COLUMN_AGE + " INTEGER,"
                        +COLUMN_GENDER + " INTEGER"
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
