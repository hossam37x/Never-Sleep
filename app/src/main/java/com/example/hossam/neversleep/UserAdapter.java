package com.example.hossam.neversleep;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

public class UserAdapter extends RecyclerView.Adapter {

    ArrayList<Object> Users;

    public UserAdapter(ArrayList<Object> users) {
        Users = users;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.user_reccyclerview_item,parent,false );

        UserViewHolder viewHolder = new UserViewHolder(view);
    return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return Users.size();
    }
    private class UserViewHolder extends RecyclerView.ViewHolder
    {
        public UserViewHolder(View itemView) {
            super(itemView);

        }
    }
}
