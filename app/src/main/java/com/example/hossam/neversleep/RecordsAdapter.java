package com.example.hossam.neversleep;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.hossam.neversleep.Database.Model.Record;

import java.util.ArrayList;

public class RecordsAdapter extends RecyclerView.Adapter<RecordsAdapter.RecordViewHolder>
{
    ArrayList<Record> records;

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
    public void onBindViewHolder(@NonNull RecordViewHolder holder, int position) {
        holder.onBind(records.get(position));
    }

    @Override
    public int getItemCount() {
        return records.size();
    }

    public class RecordViewHolder extends RecyclerView.ViewHolder
    {
        TextView datetime,bpm;
        public RecordViewHolder(View itemView)
        {
            super(itemView);
            datetime = (TextView)itemView.findViewById(R.id.record_item_datetime);
            bpm = (TextView)itemView.findViewById(R.id.record_item_bpm);
        }
        void onBind(Record record)
        {

        }
    }
}
