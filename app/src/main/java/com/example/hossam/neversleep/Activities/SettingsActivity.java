package com.example.hossam.neversleep.Activities;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Switch;
import android.widget.TextView;

import com.example.hossam.neversleep.Database.ApplicationDatabase;
import com.example.hossam.neversleep.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SettingsActivity extends AppCompatActivity
{
    @BindView(R.id.SA_settings_sound_switch)
    Switch soundSwitch;
    @BindView(R.id.SA_settings_vibrate_switch)
    Switch vibrate_switch;
    @BindView(R.id.SA_clear_data_textview)
    TextView clearData;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        ButterKnife.bind(this);
        AlertDialog.OnClickListener alertClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which)
                {
                    case AlertDialog.BUTTON_POSITIVE:
                            ApplicationDatabase applicationDatabase = new ApplicationDatabase(SettingsActivity.this);
                            int id =SettingsActivity.this.getIntent().getExtras().getInt("id");
                            applicationDatabase.deleteRecords(id);
                        break;
                    case AlertDialog.BUTTON_NEGATIVE:
                        break;
                }
            }
        };
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(this)
                .setMessage("Are you sure you want to clear history?")
                .setPositiveButton("Yes", alertClickListener)
                .setNegativeButton("No", alertClickListener);
        clearData.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                alertDialog.show();
            }
        });
    }
}