package com.finetra.finecovidalert;

import android.app.Activity;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ListDepotResultAdapter extends RecyclerView.Adapter<ListDepotResultAdapter.ListViewHolder> {
    ArrayList<ListDepotReport> list;
    ListDepotReportClicked itemClicked;
    Activity activity;
    public interface ListDepotReportClicked{
        void itemClick(ListDepotResultAdapter.ListViewHolder holder,View v,int position);

    }
    public ListDepotResultAdapter(ArrayList<ListDepotReport> list, ListDepotReportClicked itemClicked, Activity activity){
        this.list=list;
        this.itemClicked=itemClicked;
        this.activity=activity;
    }

    @NonNull
    @Override
    public ListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.list_depot_result,parent,false);
        return new ListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ListViewHolder holder, int position) {
        String activityName=activity.getLocalClassName();
        Date formatStart = new Date(),formatEnd = new Date();
        try {
            formatStart=new SimpleDateFormat("yyyy-MM-dd").parse(list.get(position).getPosDate());
            formatEnd=new SimpleDateFormat("yyyy-MM-dd").parse(list.get(position).getPosEndDate());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        long diffDate=(formatEnd.getTime()-formatStart.getTime())/1000/(24*60*60);
        if(activityName.equals("MainActivity")){
            holder.txtDepotName.setText(list.get(position).getDepotName());
            holder.txtPosTotal.setText(list.get(position).getStaffName());
        }else{
            holder.txtDepotName.setText(list.get(position).getStaffName());
            holder.txtPosTotal.setText(diffDate+"일 경과");
        }

        holder.txtPos.setText(list.get(position).getPosDate());
        holder.txtPosEnd.setText(list.get(position).getPosEndDate());
        if(!list.get(position).getPosDate().equals("0")){
        holder.txtPos.setTextColor(Color.RED);
        holder.txtPosTotal.setTextColor(Color.RED);
        }
        if(!list.get(position).getPosEndDate().equals("0")){
            holder.txtPosEnd.setTextColor(Color.BLUE);
        }



    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ListViewHolder extends RecyclerView.ViewHolder {
        TextView txtDepotName;
        TextView txtPos;
        TextView txtPosEnd;
        TextView txtPosTotal;
        public ListViewHolder(@NonNull View itemView) {
            super(itemView);
            txtDepotName=itemView.findViewById(R.id.list_depot_result_txtDepotName);
            txtPos=itemView.findViewById(R.id.list_depot_result_txtPositive);
            txtPosEnd=itemView.findViewById(R.id.list_depot_result_txtPositiveEnd);
            txtPosTotal=itemView.findViewById(R.id.list_depot_result_txtPositiveTotal);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    itemClicked.itemClick(ListViewHolder.this,itemView,getAdapterPosition());
                }
            });
        }
    }
}
