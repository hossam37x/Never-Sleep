package com.example.hossam.neversleep;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.hossam.neversleep.Database.Model.User;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

    ArrayList<User> Users;
    RecyclerItemClickListener mlistener;

    public void setUsers(ArrayList<User> users) {
        Users = users;
    }

    public UserAdapter(ArrayList<User> users, RecyclerItemClickListener listener)
    {
        Users = users;
        this.mlistener = listener;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.user_reccyclerview_item,parent,false );

        UserViewHolder viewHolder = new UserViewHolder(view);
    return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position)
    {
        holder.onBind(Users.get(position), mlistener);
    }

    @Override
    public int getItemCount() {
        return Users.size();
    }
    public class UserViewHolder extends RecyclerView.ViewHolder
    {
        User user;
        private TextView textView;
        private ImageView imageView;
        public UserViewHolder(View itemView) {
            super(itemView);
            textView = (TextView)itemView.findViewById(R.id.user_recycler_view_text);
            imageView = (ImageView)itemView.findViewById(R.id.user_recycler_view_image);
        }
        void onBind(User user, final RecyclerItemClickListener listener)
        {
            this.user = user;
            imageView.setImageResource((user.getGender())?R.drawable.boy:R.drawable.nav_bar_girl);
            textView.setText(user.getName());
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(UserViewHolder.this.user);
                }
            });
        }
    }
    public interface RecyclerItemClickListener
    {
        void onItemClick(User user);
    }
}
