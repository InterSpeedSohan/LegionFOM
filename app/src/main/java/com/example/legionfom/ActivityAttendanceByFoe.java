package com.example.legionfom;

import android.app.DatePickerDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.legionfom.dataModel.FoeAttendanceDataModel;
import com.example.legionfom.dataModel.SecDailyAttendanceDataModel;
import com.example.legionfom.helper.CustomUtility;
import com.example.legionfom.helper.MySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class ActivityAttendanceByFoe extends AppCompatActivity {
    RecyclerView recyclerView;
    DataAdapter mAdapter;
    private ArrayList<FoeAttendanceDataModel> dataList = new ArrayList<FoeAttendanceDataModel>();

    ImageButton homeBtn;


    public static String code = "", message = "";

    TextView txtFromdate, txtTodate;
    Button fromBtn, toBtn;

    String toDate="", fromDate="";

    final Calendar myCalendar = Calendar.getInstance();
    final Calendar myCalendar2 = Calendar.getInstance();
    String myFormat = "yyyy-MM-dd";
    SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

    SweetAlertDialog sweetAlertDialog,progressDialog,pDialog;
    JSONObject jsonObject;
    JSONArray jsonArray;
    JSONObject jo;
    String Json_String;
    boolean networkAvailable = false;

    SharedPreferences sharedPreferences;

    String secName = "", secId = "";
    ArrayAdapter<String> arrayAdapter;
    ArrayList secList = new ArrayList<String>();
    Map<Integer,String> secIdMap = new HashMap<>();
    String  s, sid;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_attendance_by_foe);

        sharedPreferences = getSharedPreferences("fom_user",MODE_PRIVATE);

        homeBtn = findViewById(R.id.homeBtn);


        txtFromdate = findViewById(R.id.fromdate);
        txtTodate = findViewById(R.id.todate);
        fromBtn = findViewById(R.id.frombtn);
        toBtn = findViewById(R.id.tobtn);

        recyclerView = (RecyclerView)findViewById(R.id.recycler_view);
        mAdapter = new DataAdapter(dataList);
        recyclerView.setItemViewCacheSize(20);
        recyclerView.setDrawingCacheEnabled(true);
        recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);


        getFoeList();



        homeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // making from date as first date of current month
        myCalendar.set(Calendar.DAY_OF_MONTH,1);
        fromDate = sdf.format(myCalendar.getTime());
        txtFromdate.setText(fromDate);
        toDate = sdf.format(myCalendar2.getTime());
        txtTodate.setText(toDate);


        final DatePickerDialog.OnDateSetListener fromdate = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateFromDate();
                getDetails();
            }

        };

        fromBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(ActivityAttendanceByFoe.this, fromdate, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });


        final DatePickerDialog.OnDateSetListener todate = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar2.set(Calendar.YEAR, year);
                myCalendar2.set(Calendar.MONTH, monthOfYear);
                myCalendar2.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                if(myCalendar.compareTo(myCalendar2) == 1)
                {
                    txtTodate.setText("");
                    CustomUtility.showWarning(ActivityAttendanceByFoe.this,"Select correct date","Failed");
                }
                else{
                    updateToDate();

                    networkAvailable = CustomUtility.haveNetworkConnection(ActivityAttendanceByFoe.this);
                    if (networkAvailable) getDetails();
                    else CustomUtility.showWarning(ActivityAttendanceByFoe.this,"Please turn on internet connection","No inernet");
                }

            }

        };

        toBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(fromDate.equals(""))
                {
                    CustomUtility.showWarning(ActivityAttendanceByFoe.this,"Select from date first","Failed");
                }
                else
                {
                    new DatePickerDialog(ActivityAttendanceByFoe.this, todate, myCalendar2
                            .get(Calendar.YEAR), myCalendar2.get(Calendar.MONTH),
                            myCalendar2.get(Calendar.DAY_OF_MONTH)).show();
                }

            }
        });

    }


    private void updateFromDate() {
        fromDate = sdf.format(myCalendar.getTime());
        txtFromdate.setText(sdf.format(myCalendar.getTime()));
    }

    private void updateToDate() {
        toDate = sdf.format(myCalendar2.getTime());
        txtTodate.setText(sdf.format(myCalendar2.getTime()));
    }



    public void getFoeList()
    {
        dataList.clear();
        progressDialog = new SweetAlertDialog(ActivityAttendanceByFoe.this, SweetAlertDialog.PROGRESS_TYPE);
        progressDialog.setTitle("Please wait....");
        progressDialog.setCancelable(false);
        progressDialog.show();

        String upLoadServerUri="https://sec.imslpro.com/api/android/get_sec_list.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, upLoadServerUri,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            progressDialog.dismiss();
                            Log.e("response",response);
                            jsonObject = new JSONObject(response);
                            code = jsonObject.getString("success");
                            message = jsonObject.getString("message");
                            if(code.equals("true"))
                            {
                                jsonArray = jsonObject.getJSONArray("employeeList");
                                for (int i = 0;i<jsonArray.length();i++)
                                {
                                    jo = jsonArray.getJSONObject(i);
                                    if(!jo.getString("designation_name").equals("FOE")) continue;
                                    FoeAttendanceDataModel foeAttendanceDataModel = new FoeAttendanceDataModel("",jo.getString("name"),jo.getString("user_name"),"0",
                                            "0","0","0","0","0","0","0","0","0");
                                    dataList.add(foeAttendanceDataModel);
                                }
                                mAdapter.notifyDataSetChanged();
                                getDetails();
                            }
                            else
                            {
                                CustomUtility.showError(ActivityAttendanceByFoe.this,message,"Failed");
                            }

                        } catch (JSONException e) {
                            CustomUtility.showError(ActivityAttendanceByFoe.this, "Failed to get data", "Failed");
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                pDialog = new SweetAlertDialog(ActivityAttendanceByFoe.this, SweetAlertDialog.ERROR_TYPE);
                pDialog.setTitleText("Network Error");
                pDialog.setConfirmText("Ok");
                pDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        pDialog.dismiss();
                        startActivity(getIntent());
                        finish();
                    }
                });
                pDialog.show();
                //CustomUtility.showError(AddCustomerSales.this, "Try again", "Network Error");
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("UserId",sharedPreferences.getString("id",null));
                return params;
            }
        };

        MySingleton.getInstance(ActivityAttendanceByFoe.this).addToRequestQue(stringRequest);
    }


    public void getDetails() {

        networkAvailable = CustomUtility.haveNetworkConnection(ActivityAttendanceByFoe.this);
        if(networkAvailable)
        {
            progressDialog = new SweetAlertDialog(ActivityAttendanceByFoe.this, SweetAlertDialog.PROGRESS_TYPE);
            progressDialog.setTitle("Please wait...");
            progressDialog.setCancelable(false);
            progressDialog.show();

            String upLoadServerUri="https://sec.imslpro.com/api/android/attendance_report_summary_by_foe.php";
            StringRequest stringRequest = new StringRequest(Request.Method.POST, upLoadServerUri,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {

                            try {
                                progressDialog.dismiss();
                                Log.e("response",response);
                                jsonObject = new JSONObject(response);
                                String code = jsonObject.getString("success");
                                String message = jsonObject.getString("message");
                                String foeId,present, dayoff, late, casual, sick, halfday, absent, meeting = "0", training ;
                                int total_leave, total, total_present;
                                if (code.equals("true")) {
                                    jsonArray = jsonObject.getJSONArray("resultList");
                                    for (int i = 0; i< jsonArray.length(); i++)
                                    {
                                        jo = jsonArray.getJSONObject(i);

                                        foeId = jo.getString("secId");     // here using secId for getting the foeId
                                        for(int j = 0; j<dataList.size();j++)
                                        {
                                            if(foeId.equals(dataList.get(j).getFoeId()))
                                            {
                                                Log.e("found","match");
                                                present = jo.getString("total_present");
                                                dayoff = jo.getString("total_dayOff");
                                                late = jo.getString("total_late");
                                                casual = jo.getString("total_casulLeave");
                                                sick = jo.getString("total_sickLeave");
                                                halfday = jo.getString("total_halfDayLeave");
                                                absent = jo.getString("total_absent");
                                                meeting = jo.getString("total_meeting");
                                                training = jo.getString("total_training");

                                                total_present = Integer.parseInt(present) + Integer.parseInt(late);
                                                total_leave = Integer.parseInt(casual) + Integer.parseInt(sick) + Integer.parseInt(halfday);
                                                total = total_present + Integer.parseInt(dayoff) + Integer.parseInt(absent) + Integer.parseInt(meeting) + Integer.parseInt(training);
                                                dataList.get(j).setPresent(String.valueOf(total_present));
                                                dataList.get(j).setDayOff(dayoff);
                                                dataList.get(j).setTraining(training);
                                                dataList.get(j).setCasualLeave(casual);
                                                dataList.get(j).setSickLeave(sick);
                                                dataList.get(j).setHalfDayLeave(halfday);
                                                dataList.get(j).setLeaveTotal(String.valueOf(total_leave));
                                                dataList.get(j).setAbsent(absent);
                                                dataList.get(j).setTotal(String.valueOf(total));
                                                mAdapter.notifyItemChanged(j);
                                            }
                                        }
                                    }

                                }
                                else{
                                    Log.e("mess",message);
                                    CustomUtility.showError(ActivityAttendanceByFoe.this,message,"Failed");
                                    mAdapter.notifyDataSetChanged();
                                    return;
                                }

                            } catch (JSONException e) {
                                CustomUtility.showWarning(ActivityAttendanceByFoe.this,
                                        e.getMessage() +". Parsing Failed..",
                                        "Response");
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    progressDialog.dismiss();
                    Log.e("res",error.toString());
                    CustomUtility.showError(ActivityAttendanceByFoe.this, "Network Error, try again!", "Failed");
                }
            }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    params.put("UserId",sharedPreferences.getString("id",null));
                    params.put("DateStart",fromDate);
                    params.put("DateEnd",toDate);
                    params.put("IsGroupByFoe","1");
                    return params;
                }
            };

            MySingleton.getInstance(ActivityAttendanceByFoe.this).addToRequestQue(stringRequest);

        }
        else
        {
            CustomUtility.showWarning(ActivityAttendanceByFoe.this,"Please turn on internet connection","No Internet");
        }
    }



    // data adapter class for showing the list
    public class DataAdapter extends RecyclerView.Adapter<DataAdapter.MyViewHolder> {

        private List<FoeAttendanceDataModel> dataList;

        public DataAdapter(List<FoeAttendanceDataModel> dataList) {
            this.dataList = dataList;
        }

        @NonNull
        @Override
        public DataAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.attendance_by_foe_row_layout, parent, false);
            return new DataAdapter.MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull DataAdapter.MyViewHolder holder, int position) {
            final FoeAttendanceDataModel data = dataList.get(position);
            holder.foe.setText(data.getFoe());
            holder.present.setText(data.getPresent());
            holder.dayOff.setText(data.getDayOff());
            holder.casualLeave.setText(data.getCasualLeave());
            holder.training.setText(data.getTraining());
            holder.sickLeave.setText(data.getSickLeave());
            holder.halfDayLeave.setText(data.getHalfDayLeave());
            holder.totalLeave.setText(data.getLeaveTotal());
            holder.meeting.setText(data.getMeeting());
            holder.absent.setText(data.getAbsent());
            holder.total.setText(data.getTotal());
            if(position%2 == 0)
            {
                holder.rowLayout.setBackgroundResource(R.color.even);
            }
            else
            {
                holder.rowLayout.setBackgroundResource(R.color.odd);
            }
        }

        @Override
        public int getItemCount() {
            return dataList.size();
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {
            TextView foe, present, dayOff, training, casualLeave, sickLeave, halfDayLeave, totalLeave, meeting, absent, total;
            ConstraintLayout rowLayout;
            public MyViewHolder(View convertView) {
                super(convertView);
                rowLayout = convertView.findViewById(R.id.rowLayout);
                foe = convertView.findViewById(R.id.foe);
                present = convertView.findViewById(R.id.present);
                dayOff = convertView.findViewById(R.id.dayOff);
                training = convertView.findViewById(R.id.training);
                casualLeave = convertView.findViewById(R.id.casualLeave);
                sickLeave = convertView.findViewById(R.id.sickLeave);
                halfDayLeave = convertView.findViewById(R.id.halfDayLeave);
                totalLeave = convertView.findViewById(R.id.totalLeave);
                meeting = convertView.findViewById(R.id.meeting);
                absent = convertView.findViewById(R.id.absent);
                total = convertView.findViewById(R.id.total);
            }
        }
    }
}
