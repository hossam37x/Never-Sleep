package com.example.hossam.neversleep.Activities;

import android.animation.Animator;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.hossam.neversleep.Database.ApplicationDatabase;
import com.example.hossam.neversleep.Database.Model.Record;
import com.example.hossam.neversleep.Database.Model.User;
import com.example.hossam.neversleep.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class StartActivity extends AppCompatActivity {
    @BindView(R.id.start_logo_image)
    ImageView logo_image;
    @BindView(R.id.start_sentence_image)
    TextView sentence_image;
    @BindView(R.id.start_heart_sentence)
    TextView heart_sentence;
    @BindView(R.id.start_button)
    Button start_button;

    SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        ButterKnife.bind(this);
        sharedPreferences = this.getSharedPreferences("prefs", MODE_PRIVATE);
        if(!sharedPreferences.contains("first"))
            sharedPreferences.edit().putBoolean("first", true).commit();
        start_button.setVisibility(View.INVISIBLE);
        sentence_image.setVisibility(View.INVISIBLE);
        heart_sentence.setVisibility(View.INVISIBLE);
        logo_image.animate().translationY(-100).setDuration(1000).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                start_button.setVisibility(View.VISIBLE);
                sentence_image.setVisibility(View.VISIBLE);
                heart_sentence.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        }).start();
        start_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(sharedPreferences.getBoolean("first", true))
                {
                    Intent intent = new Intent(StartActivity.this,NewUserActivity.class);
                    startActivity(intent);
                }
                else
                {
                    if(sharedPreferences.contains("id"))
                    {
                        long id = sharedPreferences.getLong("id", 0);
                        ApplicationDatabase db = new ApplicationDatabase(StartActivity.this);
                        User user = db.getUser(id);
                        Intent intent = new Intent(StartActivity.this,MainActivity.class);
                        intent.putExtra(MainActivity.CURRENT_USER, user);
                        startActivity(intent);
                        finish();
                    }
                    else
                    {
                        Intent intent = new Intent(StartActivity.this,NewUserActivity.class);
                        startActivity(intent);
                    }
                }
            }
        });
        initializeDefaultSettings();
    }
    private void initializeDefaultSettings()
    {
        if(sharedPreferences.getBoolean("first",true ))
        {
            sharedPreferences.edit().putBoolean(SettingsActivity.SOUND_SETTING, true).commit();
            sharedPreferences.edit().putBoolean(SettingsActivity.VIBRATION_SETTING, true).commit();
        }
    }
}
