package com.example.hossam.neversleep.Activities;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.example.hossam.neversleep.Database.ApplicationDatabase;
import com.example.hossam.neversleep.Database.Model.Record;
import com.example.hossam.neversleep.Database.Model.User;
import com.example.hossam.neversleep.R;
import com.example.hossam.neversleep.RecordsAdapter;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class HistoryActivity extends AppCompatActivity {
    @BindView(R.id.record_recycler_view)
    RecyclerView recordRecyclerView;

    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        ButterKnife.bind(this);
        user = (User)getIntent().getExtras().getSerializable("user");
        ApplicationDatabase applicationDatabase = new ApplicationDatabase(this);
        ArrayList<Record> records = new ArrayList<>(applicationDatabase.getAllUserRecords(user));
        RecordsAdapter recordsAdapter = new RecordsAdapter(records);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recordRecyclerView.setLayoutManager(linearLayoutManager);
        recordRecyclerView.setAdapter(recordsAdapter);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

    }

    @Override
    public boolean onSupportNavigateUp() {
        this.finish();
        return true;
    }
}
