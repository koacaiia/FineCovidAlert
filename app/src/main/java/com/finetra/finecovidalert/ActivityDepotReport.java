package com.finetra.finecovidalert;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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

public class ActivityDepotReport extends AppCompatActivity implements ListDepotResultAdapter.ListDepotReportClicked {
    String depotName,dateToday;
    TextView txtDepotName,txtToday,txtPos,txtPosEnd,txtPosTotal;
    RecyclerView recyclerView;
    Button btnReg;
    ListDepotResultAdapter adapter;
    ArrayList<ListDepotReport> list;
    @SuppressLint("SimpleDateFormat")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_depot_report);
        depotName=getIntent().getStringExtra("DepotName");
        dateToday=new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        txtDepotName=findViewById(R.id.activity_depot_report_txtDepotName);
        txtDepotName.setText(depotName);
        txtToday=findViewById(R.id.activity_depot_report_txtDateToday);
        txtToday.setText(dateToday);

        btnReg=findViewById(R.id.activity_depot_report_btnReg);
        btnReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogNewPosReg();
            }
        });

        recyclerView=findViewById(R.id.activity_depot_report_recyclerView);
        LinearLayoutManager manager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(manager);
        list=new ArrayList<>();
        adapter=new ListDepotResultAdapter(list,this, this);
        recyclerView.setAdapter(adapter);
        getDepotPosData();
        txtPos=findViewById(R.id.activity_depot_report_txtPositive);
        txtPosEnd=findViewById(R.id.activity_depot_report_txtPositiveEnd);
        txtPosTotal=findViewById(R.id.activity_depot_report_txtPositiveTotal);
    }

    private void getDepotPosData() {
        list.clear();
        DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference("CovidAlert/");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int pos=0;
                int posEnd=0;
                int posTotal=0;
                for(DataSnapshot data:snapshot.getChildren()){
                    ListDepotReport report=data.getValue(ListDepotReport.class);
                    assert report != null;
                    if(report.getDepotName().equals(depotName)){
                        if(!report.getStaffName().equals("")){
                            posTotal=posTotal+1;
                        }
                        if(!report.getPosDate().equals("")){
                            pos=pos+1;
                        }
                        if(!report.getPosEndDate().equals("")){
                            posEnd=posEnd+1;
                        }
                        list.add(report);
                    }
                }
                txtPos.setText(String.valueOf(pos));
                txtPosEnd.setText(String.valueOf(posEnd));
                txtPosTotal.setText(String.valueOf(posTotal));
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void dialogNewPosReg() {
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        View view=getLayoutInflater().inflate(R.layout.dialog_regpos,null);
        final String[] date = new String[1];
        Button btnDate=view.findViewById(R.id.dialog_regpos_btnDate);
        TextView txtDate=view.findViewById(R.id.dialog_regpos_editDate);
        txtDate.setText(dateToday);
        TextView txtName=view.findViewById(R.id.dialog_regpos_editName);
        Button btnName=view.findViewById(R.id.dialog_regpos_btnName);
        btnDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder=new AlertDialog.Builder(ActivityDepotReport.this);
                DatePicker datePicker=new DatePicker(ActivityDepotReport.this);
                datePicker.setOnDateChangedListener(new DatePicker.OnDateChangedListener() {
                    @Override
                    public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        String month,day;
                        if(monthOfYear+1<10){
                            month="0"+(monthOfYear+1);
                        }else{
                            month=String.valueOf(monthOfYear);
                        }
                        if(dayOfMonth<10){
                            day="0"+(dayOfMonth);
                        }else{
                            day=String.valueOf(dayOfMonth);
                        }
                        date[0] =year+"-"+month+"-"+day;
                        Toast.makeText(ActivityDepotReport.this,date[0]+" 으로 확진일 지정",Toast.LENGTH_SHORT).show();
                      }
                });

                builder.setTitle("확진일 변경 창")
                        .setView(datePicker)
                        .setPositiveButton("확진일 등록", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if(date[0]==null||date[0].equals("")){
                                    date[0]=dateToday;
                                }
                                dialog.dismiss();
                                txtDate.setText(date[0]);
                            }
                        })
                        .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .show();
            }
        });


        btnName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder=new AlertDialog.Builder(ActivityDepotReport.this);
                EditText editText=new EditText(ActivityDepotReport.this);
                editText.setInputType(InputType.TYPE_CLASS_TEXT);
                builder.setTitle("확진자명 등록")
                        .setView(editText)
                        .setPositiveButton("부서확진자 등록", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                txtName.setText(editText.getText().toString());
                            }
                        })
                        .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .show();
            }
        });

        builder.setTitle("신규 확진자 등록 창(" +depotName+ ")")
                .setView(view)
                .setPositiveButton(" 확진자 상황 등록", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String staffName=txtName.getText().toString();
                        String posDate=txtDate.getText().toString();
                        AlertDialog.Builder builder=new AlertDialog.Builder(ActivityDepotReport.this);
                        builder.setTitle(depotName+" 확진자 신규등록 확인창")
                                .setMessage("부서명:"+depotName+"\n"+"직원명: "+staffName+"\n"+"확진일: "+posDate+"\n"+" 로 확진자 사항 등록 진행 " +
                                        "합니다."+"\n"+"최종확인후 하단 서버등록 버튼 클릭하여 서버 업로드 진행 바랍니다.")
                                .setPositiveButton("서버등록", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        String refPath=depotName+"_"+staffName+"_"+posDate;
                                        DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference(
                                                "CovidAlert/"+refPath);
                                        ListDepotReport putDataList=new ListDepotReport(depotName,posDate,"",staffName,refPath);
                                        databaseReference.setValue(putDataList);
                                        Intent intent=new Intent(ActivityDepotReport.this,MainActivity.class);
                                        startActivity(intent);
                                    }
                                })
                                .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                    }
                                })

                                .show();
                    }
                })
                .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .show();
    }

    @Override
    public void itemClick(ListDepotResultAdapter.ListViewHolder holder, View v, int position) {
        String depotName=list.get(position).getDepotName();
        String staffName=list.get(position).getStaffName();
        String posDate=list.get(position).getPosDate();
        String posEndDate=list.get(position).getPosEndDate();
        if(posEndDate.equals("")){
            posEndDate="확진 자가격리중";
        }
        String path=depotName+"_"+staffName+"_"+posDate;
        AlertDialog.Builder builder=new AlertDialog.Builder(ActivityDepotReport.this);
        View view=getLayoutInflater().inflate(R.layout.dialog_updateposdata,null);
        TextView txtStaffName=view.findViewById(R.id.dialog_updateposdata_txtName);
        txtStaffName.setText(staffName);
        TextView txtPosDate=view.findViewById(R.id.dialog_updateposdata_txtPosDate);
        txtPosDate.setText(posDate);
        TextView txtPosEndDate=view.findViewById(R.id.dialog_updateposdata_txtPosEndDate);
        txtPosEndDate.setText(posEndDate);

        Button btnStaffName=view.findViewById(R.id.dialog_updateposdata_btnName);
        btnStaffName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder=new AlertDialog.Builder(ActivityDepotReport.this);
                EditText editText=new EditText(ActivityDepotReport.this);
                editText.setInputType(InputType.TYPE_CLASS_TEXT);
                builder.setTitle("확진자명 변경 창")
                        .setView(editText)
                        .setPositiveButton("확진자명 변경", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                txtStaffName.setText(editText.getText().toString());
                            }
                        })
                        .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        }).show();
            }
        });
        Button btnPosDate=view.findViewById(R.id.dialog_updateposdata_btnPosDate);
        btnPosDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder=new AlertDialog.Builder(ActivityDepotReport.this);
                DatePicker datePicker=new DatePicker(ActivityDepotReport.this);
                final String[] posDate = {new SimpleDateFormat("yyyy-MM-dd").format(new Date())};
                datePicker.setOnDateChangedListener(new DatePicker.OnDateChangedListener() {
                    @Override
                    public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                   String month,day;
                   if(monthOfYear+1<10){
                       month="0"+(monthOfYear+1);
                   }else{
                       month=String.valueOf(monthOfYear+1);
                   }
                   if(dayOfMonth<10){
                       day="0"+dayOfMonth;
                   }else{
                       day=String.valueOf(dayOfMonth);
                   }
                   posDate[0] =year+"-"+month+"-"+day;

                   Toast.makeText(ActivityDepotReport.this, posDate[0]+" 일자로 확진일 변경",Toast.LENGTH_SHORT).show();
                    }
                });
                builder.setTitle("확진일 변경창")
                        .setView(datePicker)
                        .setPositiveButton("확진일 변경", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                txtPosDate.setText(posDate[0]);
                            }
                        })
                        .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .show();
            }
        });
        Button btnPosEndDate=view.findViewById(R.id.dialog_updateposdata_btnPosEndDate);
        btnPosEndDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder=new AlertDialog.Builder(ActivityDepotReport.this);
                DatePicker datePicker=new DatePicker(ActivityDepotReport.this);
                final String[] posDate = {new SimpleDateFormat("yyyy-MM-dd").format(new Date())};
                datePicker.setOnDateChangedListener(new DatePicker.OnDateChangedListener() {
                    @Override
                    public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        String month,day;
                        if(monthOfYear+1<10){
                            month="0"+(monthOfYear+1);
                        }else{
                            month=String.valueOf(monthOfYear+1);
                        }
                        if(dayOfMonth<10){
                            day="0"+dayOfMonth;
                        }else{
                            day=String.valueOf(dayOfMonth);
                        }
                        posDate[0] =year+"-"+month+"-"+day;

                        Toast.makeText(ActivityDepotReport.this, posDate[0]+" 일자로 해제일 변경",Toast.LENGTH_SHORT).show();
                    }
                });
                builder.setTitle("해제일 변경창")
                        .setView(datePicker)
                        .setPositiveButton("해제일 변경", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                txtPosEndDate.setText(posDate[0]);
                            }
                        })
                        .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .show();

            }
        });
        builder.setTitle(staffName+" 직원 확진상황 변경 등록 창")
                .setView(view)
                .setPositiveButton("변경사항등록", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String name=txtStaffName.getText().toString();
                        String day=txtPosDate.getText().toString();
                        String dayEnd=txtPosEndDate.getText().toString();
                        if(dayEnd.equals("확진 자가격리중")){
                            dayEnd="";
                        }
                        String putPath=depotName+"_"+name+"_"+day;
                        AlertDialog.Builder builder=new AlertDialog.Builder(ActivityDepotReport.this);
                        String finalDayEnd = dayEnd;
                        builder.setTitle("변경사항 등록창")
                                .setMessage("확진자명:  "+name+"\n"+"확진일: "+day+"\n"+"해제일: "+dayEnd+"\n"+"로 변경사항 서버에 등록 할려면 하단 서버 " +
                                        "업로드 버튼 클릭 하세요!")
                                .setPositiveButton("서버등록", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        if(!path.equals(putPath)){
                                            DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference(
                                                    "CovidAlert/");

                                            Map<String,Object> nullValue=new HashMap<>();
                                            nullValue.put(path+"/",null);
                                            databaseReference.updateChildren(nullValue);
                                            Log.i("TestValue","path Value="+path+"////"+"putPath Value="+putPath);
                                        }
                                        DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference(
                                                "CovidAlert/"+putPath);
                                        Map<String,Object> putValue=new HashMap<>();
                                        putValue.put("depotName",depotName);
                                        putValue.put("posDate",day);
                                        putValue.put("posEndDate", finalDayEnd);
                                        putValue.put("staffName",name);
                                        putValue.put("keyValue",putPath);
                                        databaseReference.updateChildren(putValue);
                                        Intent intent=new Intent(ActivityDepotReport.this,ActivityDepotReport.class);
                                        intent.putExtra("DepotName",depotName);
                                        startActivity(intent);
                                    }
                                })
                                .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                    }
                                })

                                .show();

                    }
                })
                .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setNeutralButton("관련 데이터 삭제", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        AlertDialog.Builder builder=new AlertDialog.Builder(ActivityDepotReport.this);
                        builder.setTitle(staffName+" 직원 확진상황 삭제 창")
                                .setMessage("부서명:"+depotName+"\n"+"직원명: "+staffName+"\n"+"확진일: "+posDate+"\n"+
                                        "에 대한 내용을 삭제 합니다 " +
                                        "\n"+"최종확인후 하단 내용삭제 버튼 클릭하여 관련내용 삭제 진행 바랍니다.")
                                .setPositiveButton("내용삭제", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                    DatabaseReference deletePath=FirebaseDatabase.getInstance().getReference("CovidAlert");
                                    Map<String,Object> deleteValue=new HashMap<>();
                                    deleteValue.put(list.get(position).getKeyValue()+"/",null);
                                    deletePath.updateChildren(deleteValue);
                                        Intent intent=new Intent(ActivityDepotReport.this,ActivityDepotReport.class);
                                        intent.putExtra("DepotName",depotName);
                                        startActivity(intent);
                                    }
                                })
                                .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                    }
                                })
                                .show();
                    }
                })
                .show();


    }
}