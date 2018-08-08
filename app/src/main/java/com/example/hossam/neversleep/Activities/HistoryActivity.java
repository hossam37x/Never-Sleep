package com.example.hossam.neversleep.Activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;

import com.example.hossam.neversleep.Database.ApplicationDatabase;
import com.example.hossam.neversleep.Database.Model.Record;
import com.example.hossam.neversleep.Database.Model.User;
import com.example.hossam.neversleep.R;
import com.example.hossam.neversleep.RecordsAdapter;

import java.util.ArrayList;
import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;

public class HistoryActivity extends AppCompatActivity
{
    @BindView(R.id.record_recycler_view)
    RecyclerView recordRecyclerView;
    ArrayList<Record> records;
    User user;
    RecordsAdapter recordsAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        ButterKnife.bind(this);
        user = (User)getIntent().getExtras().getSerializable("user");
        ApplicationDatabase applicationDatabase = new ApplicationDatabase(this);
        records = new ArrayList<>(applicationDatabase.getAllUserRecords(user));
        recordsAdapter = new RecordsAdapter(records);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recordRecyclerView.setLayoutManager(linearLayoutManager);
        recordRecyclerView.setAdapter(recordsAdapter);
        registerForContextMenu(recordRecyclerView);
        getSupportActionBar().setTitle("History");
    }

    @Override
    public boolean onContextItemSelected(MenuItem item)
    {
        int id = item.getItemId();
        switch (id)
        {
            case R.id.delete:
                    ApplicationDatabase database = new ApplicationDatabase(this);
                    database.deleteRecord(records.get(recordsAdapter.getPosition()).getId());
                    records.remove(recordsAdapter.getPosition());
                    recordsAdapter.notifyDataSetChanged();
                break;
        }
        return super.onContextItemSelected(item);
    }
}
