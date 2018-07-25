package com.example.hossam.neversleep.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;

import com.example.hossam.neversleep.Database.ApplicationDatabase;
import com.example.hossam.neversleep.Database.Model.User;
import com.example.hossam.neversleep.R;
import com.example.hossam.neversleep.UserAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class UserSelectionActivity extends AppCompatActivity {

    @BindView(R.id.user_recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.seach_edit_text)
    EditText searchEditText;

    UserAdapter userAdapter;
    ArrayList<User> users;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_selection);
        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(UserSelectionActivity.this,NewUserActivity.class);
                startActivity(intent);
            }
        });
        ApplicationDatabase applicationDatabase = new ApplicationDatabase(this);
        users = new ArrayList<>(applicationDatabase.getAllUsers());
        userAdapter = new UserAdapter(users, new UserAdapter.RecyclerItemClickListener() {
            @Override
            public void onItemClick(User user) {
                Intent intent = new Intent(UserSelectionActivity.this,MainActivity.class);
                intent.putExtra(MainActivity.CURRENT_USER, user);
                startActivity(intent);
            }
        });

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(userAdapter);
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                search(s.toString());
            }
        });

    }

    private void search(String s) {
        if(s.equalsIgnoreCase(""))
        {
            userAdapter.setUsers(users);
            userAdapter.notifyDataSetChanged();
        }
        else
        {
            ArrayList<User> results = new ArrayList<>();
            for (User u : users)
            {
                if(u.getName().contains(s))
                {
                    results.add(u);
                }
            }
            userAdapter.setUsers(results);
            userAdapter.notifyDataSetChanged();
        }
    }
}
