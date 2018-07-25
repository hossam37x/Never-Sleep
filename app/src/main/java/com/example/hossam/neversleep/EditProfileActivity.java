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
import java.util.Date;

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
    User user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        ButterKnife.bind(this);
        user = (User)this.getIntent().getExtras().getSerializable("user");

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
        firstName_EditText.setText(user.getName().substring(0, user.getName().indexOf(" ")));
        lastName_EditText.setText(user.getName().substring(user.getName().indexOf(" ")+1,user.getName().length()));
        if(user.getGender()) {
            genderImage.setImageDrawable(getResources().getDrawable(R.drawable.boy));
        } else {
            genderImage.setImageDrawable(getResources().getDrawable(R.drawable.nav_bar_girl));
        }

        if(user.getGender())
            male_Radio.setChecked(true);
        else
            female_Radio.setChecked(true);
        dateOfBirth = user.getBirthDate();
        Date date = dateOfBirth.getTime();

        textView.setText(String.valueOf(dateOfBirth.get(Calendar.DAY_OF_MONTH))+"/"+
                String.valueOf(dateOfBirth.get(Calendar.MONTH)+1)+"/"
                +String.valueOf(dateOfBirth.get(Calendar.YEAR)));

        datePickerDialog = new DatePickerDialog(this, this,dateOfBirth.get(Calendar.YEAR)
                ,dateOfBirth.get(Calendar.MONTH)
                ,dateOfBirth.get(Calendar.DAY_OF_MONTH));

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                save();
            }
        });

    }

    private void save()
    {
        if(!validate())
            return;
        String name = firstName_EditText.getText().toString() +" "+ lastName_EditText.getText().toString();
        user.setName(name);
        user.setAge(getAge(dateOfBirth));
        user.setGender((male_Radio.isChecked())?User.MALE:User.FEMALE);
        user.setBirthDate(dateOfBirth);
        ApplicationDatabase database = new ApplicationDatabase(this);
        database.updateUser(user);
        finish();
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

        if (today.get(Calendar.DAY_OF_YEAR) < dob.get(Calendar.DAY_OF_YEAR))
        {
            age--;
        }

        Integer ageInt = new Integer(age);
        return ageInt;
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth)
    {
        Calendar calendar = Calendar.getInstance();
        //calendar.set(year,month,dayOfMonth);
        calendar.set(Calendar.YEAR,year );
        calendar.set(Calendar.MONTH , month);
        calendar.set(Calendar.DAY_OF_MONTH,dayOfMonth );
        calendar.set(Calendar.HOUR,0 );
        calendar.set(Calendar.MINUTE,0 );
        calendar.set(Calendar.MILLISECOND, 0);
        this.dateOfBirth = calendar;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/mm/dd");
        simpleDateFormat.format(calendar.getTime());
        textView.setText(String.valueOf(dayOfMonth)+"/"+String.valueOf(month+1)+"/"+String.valueOf(year));
        Log.i("Day", String.valueOf(month));
    }
}
