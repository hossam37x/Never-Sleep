package com.example.hossam.neversleep;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.hossam.neversleep.Database.Model.Record;

import java.util.ArrayList;

public class RecordsAdapter extends RecyclerView.Adapter<RecordsAdapter.RecordViewHolder>
{

    ArrayList<Record> records;

    public int getPosition() {
        return position;
    }

    int position = 0;
    public RecordsAdapter(ArrayList<Record> records) {
        this.records = records;
    }

    @NonNull
    @Override
    public RecordViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.record_recyclerview_item,parent,false );
        RecordViewHolder recordViewHolder = new RecordViewHolder(view);
        return recordViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final RecordViewHolder holder, final int position) {
        holder.onBind(records.get(position));
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                RecordsAdapter.this.position = holder.getAdapterPosition();
                return false;
            }
        });
    }


    @Override
    public int getItemCount() {
        return records.size();
    }

    public class RecordViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener
    {

        TextView datetime, avg_bpm, min_bpm, max_bpm;

        public RecordViewHolder(View itemView)
        {
            super(itemView);
            datetime = itemView.findViewById(R.id.record_item_datetime);
            avg_bpm = itemView.findViewById(R.id.record_item_average_bpm);
            min_bpm = itemView.findViewById(R.id.record_item_min_bpm);
            max_bpm = itemView.findViewById(R.id.record_item_max_bpm);
            itemView.setOnCreateContextMenuListener(this);
        }

        void onBind(Record record)
        {
            datetime.setText(record.getDate());
            avg_bpm.setText(String.valueOf(record.getAvg_heart_rate()));
            min_bpm.setText(String.valueOf(record.getMin_heart_rate()));
            max_bpm.setText(String.valueOf(record.getMax_heart_rate()));
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo)
        {
            MenuInflater inflater = new MenuInflater(v.getContext());
            inflater.inflate(R.menu.record_recycler_context_menu, menu);
        }

    }
}
