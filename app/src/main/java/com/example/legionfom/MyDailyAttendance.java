package com.example.legionfom;

import android.app.DatePickerDialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.example.legionfom.dataModel.MyDailyAttendanceDataModel;
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

public class MyDailyAttendance extends AppCompatActivity {


    ImageButton homeBtn;

    RecyclerView recyclerView;
    DataAdapter mAdapter;
    private List<MyDailyAttendanceDataModel> dataList = new ArrayList<>();

    public static String code = "", message = "";
    TextView txtFromdate, txtTodate;
    Button fromBtn, toBtn;

    String toDate = "", fromDate = "";

    final Calendar myCalendar = Calendar.getInstance();
    final Calendar myCalendar2 = Calendar.getInstance();
    String myFormat = "yyyy-MM-dd";
    SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

    SweetAlertDialog sweetAlertDialog;
    JSONObject jsonObject;
    JSONArray jsonArray;
    JSONObject jo;
    String Json_String;
    boolean networkAvailable = false;

    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_daily_attendance);

        homeBtn = findViewById(R.id.homeBtn);

        homeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        sharedPreferences = getSharedPreferences("fom_user", MODE_PRIVATE);
        txtFromdate = findViewById(R.id.fromdate);
        txtTodate = findViewById(R.id.todate);
        fromBtn = findViewById(R.id.frombtn);
        toBtn = findViewById(R.id.tobtn);

        recyclerView = (RecyclerView)findViewById(R.id.recycler_view);
        mAdapter = new MyDailyAttendance.DataAdapter(dataList);
        recyclerView.setItemViewCacheSize(20);
        recyclerView.setDrawingCacheEnabled(true);
        recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);



        // making from date as first date of current month
        myCalendar.set(Calendar.DAY_OF_MONTH,1);
        fromDate = sdf.format(myCalendar.getTime());
        txtFromdate.setText(fromDate);
        toDate = sdf.format(myCalendar2.getTime());
        txtTodate.setText(toDate);
        getDetails();

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
                new DatePickerDialog(MyDailyAttendance.this, fromdate, myCalendar
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
                if (myCalendar.compareTo(myCalendar2) == 1) {
                    txtTodate.setText("");
                    CustomUtility.showWarning(MyDailyAttendance.this, "Select correct date", "Failed");
                } else {
                    updateToDate();
                    networkAvailable = CustomUtility.haveNetworkConnection(MyDailyAttendance.this);
                    if(networkAvailable)
                        getDetails();
                    else
                        CustomUtility.showWarning(MyDailyAttendance.this,"Please on internet connection","No internet!");
                }

            }

        };

        toBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (fromDate.equals("")) {
                    CustomUtility.showWarning(MyDailyAttendance.this, "Select from date first", "Failed");
                } else {
                    new DatePickerDialog(MyDailyAttendance.this, todate, myCalendar2
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


    // data adapter class for showing the list
    public class DataAdapter extends RecyclerView.Adapter<DataAdapter.MyViewHolder> {

        private List<MyDailyAttendanceDataModel> dataList;

        public DataAdapter(List<MyDailyAttendanceDataModel> dataList) {
            this.dataList = dataList;
        }

        @NonNull
        @Override
        public DataAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.my_daily_attendance_summary_list_row, parent, false);
            return new DataAdapter.MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull DataAdapter.MyViewHolder holder, int position) {
            final MyDailyAttendanceDataModel data = dataList.get(position);

            holder.date.setText(data.getDate());
            holder.status.setText(data.getStatus());
            holder.time.setText(data.getTime());
            if(data.getStatus().equals("Late"))
            {
                holder.status.setTextColor(Color.parseColor("#FF9800"));
            }
            else if(data.getStatus().equals("Absent"))
            {
                holder.status.setTextColor(Color.parseColor("#E33109"));
            }
            else if(data.getStatus().equals("Present"))
            {
                holder.status.setTextColor(Color.parseColor("#148026"));
            }
            if (position % 2 == 0) {
                holder.rowLayout.setBackgroundResource(R.color.even);
            } else {
                holder.rowLayout.setBackgroundResource(R.color.odd);
            }


        }

        @Override
        public int getItemCount() {
            return dataList.size();
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {
            TextView date,status,time;
            ConstraintLayout rowLayout;
            public MyViewHolder(View convertView) {
                super(convertView);
                rowLayout = convertView.findViewById(R.id.rowLayout);
                date =  convertView.findViewById(R.id.date);
                status =  convertView.findViewById(R.id.status);
                time = convertView.findViewById(R.id.time);
            }
        }
    }

    public void getDetails()
    {
        dataList.clear();

        sweetAlertDialog = new SweetAlertDialog(MyDailyAttendance.this, SweetAlertDialog.PROGRESS_TYPE);
        sweetAlertDialog.setTitleText("Loading");
        sweetAlertDialog.show();
        String upLoadServerUri = "https://sec.imslpro.com/api/android/get_my_attendance_detail.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, upLoadServerUri,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            sweetAlertDialog.dismiss();
                            Log.e("response",response);
                            jsonObject = new JSONObject(response);
                            String code = jsonObject.getString("success");
                            String message = jsonObject.getString("message");
                            if (code.equals("true")) {
                                int sum = 0;
                                int total = 0;
                                jsonArray = jsonObject.getJSONArray("attendanceSummary");
                                for (int i = 0; i< jsonArray.length(); i++)
                                {
                                    jo = jsonArray.getJSONObject(i);
                                    MyDailyAttendanceDataModel myDailyAttendanceDataModel = new MyDailyAttendanceDataModel(jo.getString("attendance_date"),
                                            jo.getString("attendance_status_name"),jo.getString("in_time"));
                                    dataList.add(myDailyAttendanceDataModel);
                                    mAdapter.notifyDataSetChanged();
                                }

                            }
                            else{
                                Log.e("mess",message);
                                CustomUtility.showError(MyDailyAttendance.this,message,"Failed");
                                return;
                            }
                        } catch (JSONException e) {
                            CustomUtility.showError(MyDailyAttendance.this, e.getMessage(), "Getting Response");
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                sweetAlertDialog.dismiss();
                Log.e("res",error.toString());
                CustomUtility.showError(MyDailyAttendance.this, "Network Error, try again!", "Failed");
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("UserId",sharedPreferences.getString("id",null));
                params.put("DateStart",fromDate);
                params.put("DateEnd",toDate);
                return params;
            }
        };

        MySingleton.getInstance(MyDailyAttendance.this).addToRequestQue(stringRequest);

    }
}
