package com.example.hossam.neversleep;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.example.hossam.neversleep.Database.ApplicationDatabase;
import com.example.hossam.neversleep.Database.Model.Record;
import com.example.hossam.neversleep.Database.Model.User;

import java.util.ArrayList;
import java.util.List;

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
    }
}
