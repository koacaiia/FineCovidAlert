package com.finetra.finecovidalert;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements ListDepotResultAdapter.ListDepotReportClicked {
    FirebaseDatabase database;
    DatabaseReference databaseReference;
    ArrayList<String> arrayListDepotName;
    RecyclerView recyclerViewDepotResult;
    ArrayList<ListDepotReport> arrayListDepotResult;
    ListDepotResultAdapter adapter;
    String dateToday;
    TextView txtPos,txtPosEnd,txtPosTotal;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    @SuppressLint("SimpleDateFormat")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sharedPreferences=getSharedPreferences("FineCovidAlert",MODE_PRIVATE);
        editor= sharedPreferences.edit();
        dateToday=new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        if(sharedPreferences.getString("Date",null)==null||!sharedPreferences.getString("Date",null).equals(dateToday)){
            usingAlertMessaging();
        }

        database=FirebaseDatabase.getInstance();


        TextView txtDateToday=findViewById(R.id.covid_txtDateToday);
        txtDateToday.setText(dateToday);
        recyclerViewDepotResult=findViewById(R.id.covid_recyclerViewDepotResult);
        LinearLayoutManager manager=new LinearLayoutManager(this);
        recyclerViewDepotResult.setLayoutManager(manager);
        arrayListDepotResult=new ArrayList<>();
        adapter=new ListDepotResultAdapter(arrayListDepotResult,this,this);
        recyclerViewDepotResult.setAdapter(adapter);
        getReportData();

        txtPos=findViewById(R.id.covid_txtPositive);
        txtPosEnd=findViewById(R.id.covid_txtPositiveEnd);
        txtPosTotal=findViewById(R.id.covid_txtPositiveTotal);

//        resetBasicDataBase();

    }

    private void usingAlertMessaging() {
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setTitle("????????? ????????????")
                .setMessage("??????????????? ?????? Application??? (???)??????????????? ????????? ??????????????? ???????????? Application?????? ????????? ?????? ?????????."+"\n"+"?????? Application??? " +
                        "???????????? ????????? ???????????? ????????? ????????? ?????? ????????????.")
                .setPositiveButton("?????? ??????", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setNegativeButton("?????? ??????", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                       finishAffinity();

                    }
                })
                .setNeutralButton("?????? ????????? ????????? ?????? ??????", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        editor.putString("Date",dateToday);
                        editor.apply();
                    }
                })
                .show();
    }

    private void getReportData() {
        DatabaseReference databaseReference=database.getReference("CovidAlert/");
        ArrayList<String> depotList=new ArrayList<>();
        ArrayList<ListDepotReport> arrReportList=new ArrayList<>();
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot data : snapshot.getChildren()) {
                    ListDepotReport list = data.getValue(ListDepotReport.class);

                    if (!depotList.contains(list.getDepotName())) {
                        depotList.add(list.getDepotName());}
                        arrReportList.add(list);
                    }
                    int posResultTotal = 0;
                    int posEndTotalResult = 0;
                    int posTotalResultTotal = 0;
                    for (int i = 0; i < depotList.size(); i++) {
                        int posResult = 0;
                        int posEndResult = 0;
                        int posTotalResult = 0;
                        for (int j = 0; j < arrReportList.size(); j++) {
                            if (arrReportList.get(j).getDepotName().equals(depotList.get(i))) {
                                if (!arrReportList.get(j).getPosDate().equals("")) {
                                    posResult++;
                                }
                                if (!arrReportList.get(j).getPosEndDate().equals("")) {
                                    posEndResult++;
                                }
                                if (!arrReportList.get(j).getStaffName().equals("")) {
                                    posTotalResult++;
                                }
                            }
                        }
                        ListDepotReport report = new ListDepotReport(depotList.get(i), String.valueOf(posResult),
                                String.valueOf(posEndResult), String.valueOf(posTotalResult),"");
                        arrayListDepotResult.add(report);
                        posResultTotal = posResultTotal + posResult;
                        posEndTotalResult = posEndTotalResult + posEndResult;
                        posTotalResultTotal = posTotalResultTotal + posTotalResult;
                    }
                    txtPos.setText(String.valueOf(posResultTotal));
                    txtPosEnd.setText(String.valueOf(posEndTotalResult));
                    txtPosTotal.setText(String.valueOf(posTotalResultTotal));
                    adapter.notifyDataSetChanged();
                }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void resetBasicDataBase() {
        databaseReference=database.getReference("BasicDepotInformation/");
        Map<String,Object> depotValue=new HashMap<>();
        depotValue.put("?????? ?????????","?????? ?????????");
        depotValue.put("?????? ?????????","?????? ?????????");
        depotValue.put("R&A ?????????","R&A ?????????");
        depotValue.put("?????? R&A","?????? R&A");
        depotValue.put("?????????","?????????");
        depotValue.put("???????????????","???????????????");
        depotValue.put("???????????? ?????????","???????????? ?????????");
        depotValue.put("??????????????? ???????????????","??????????????? ???????????????");
        databaseReference.updateChildren(depotValue);
        arrayListDepotName=new ArrayList<>();
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot:snapshot.getChildren()){
                    String depotName=dataSnapshot.getKey();
                    arrayListDepotName.add(depotName);
                }
                for(int i=0;i<arrayListDepotName.size();i++ ){
                    databaseReference=database.getReference("CovidAlert/"+arrayListDepotName.get(i));
                    ListDepotReport report=new ListDepotReport(arrayListDepotName.get(i),"","","",arrayListDepotName.get(i));
                    databaseReference.setValue(report);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void itemClick(ListDepotResultAdapter.ListViewHolder holder, View v, int position) {
        String depotName=arrayListDepotResult.get(position).getDepotName();
        Intent intent=new Intent(this,ActivityDepotReport.class);
        intent.putExtra("DepotName",depotName);
        startActivity(intent);
    }
}