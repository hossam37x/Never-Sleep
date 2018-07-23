package com.example.hossam.neversleep;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.example.hossam.neversleep.Activities.MainActivity;
import com.example.hossam.neversleep.Database.ApplicationDatabase;
import com.example.hossam.neversleep.Database.Model.User;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;

public class EditProfileActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {
    DatePickerDialog datePickerDialog;
    @BindView(R.id.datetime_textview)
    TextView textView;
    @BindView(R.id.first_name_textview)
    EditText firstName_EditText;
    @BindView(R.id.last_name_textview)
    EditText lastName_EditText;
    @BindView(R.id.male_radio)
    RadioButton male_Radio;
    @BindView(R.id.female_radio)
    RadioButton female_Radio;
    @BindView(R.id.radiogroup)
    RadioGroup radioGroup;
    @BindView(R.id.save_button)
    Button button;
    @BindView(R.id.gender_pic)
    ImageView genderImage;

    Calendar dateOfBirth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        ButterKnife.bind(this);

        datePickerDialog = new DatePickerDialog(this, this,1980,1,1);
        firstName_EditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
                s.subSequence(start, s.length());
            }

            @Override
            public void afterTextChanged(Editable s) {
                if(firstName_EditText.getText().toString().trim().equalsIgnoreCase("")) {
                    firstName_EditText.setError("Please enter first name");
                }
                else {
                    firstName_EditText.setError(null);
                }
            }
        });
        lastName_EditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (lastName_EditText.getText().toString().trim().equalsIgnoreCase("")) {
                    lastName_EditText.setError("Please enter last name");
                }
                else
                    lastName_EditText.setError(null);
            }
        });

        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                datePickerDialog.show();
            }
        });
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                save();
            }
        });
    }

    private void save()
    {

    }

    private boolean validate()
    {
        boolean valid = true;
        if(firstName_EditText.getText().toString().trim().equalsIgnoreCase(""))
        {
            valid = false;
        }
        if (lastName_EditText.getText().toString().trim().equalsIgnoreCase(""))
        {
            valid = false;
        }
        return valid;
    }

    private int getAge(Calendar dob)
    {
        Calendar today = Calendar.getInstance();

        int age = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR);

        if (today.get(Calendar.DAY_OF_YEAR) < dob.get(Calendar.DAY_OF_YEAR)){
            age--;
        }

        Integer ageInt = new Integer(age);
        return ageInt;
    }


    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth)
    {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year,month,dayOfMonth);
        this.dateOfBirth = calendar;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/mm/dd");
        simpleDateFormat.format(calendar.getTime());
        textView.setText(String.valueOf(dayOfMonth)+"/"+String.valueOf(month+1)+"/"+String.valueOf(year));
        Log.i("Day", String.valueOf(month));
    }
}
