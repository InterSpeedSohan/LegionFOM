package com.example.legionfom;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
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
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.legionfom.dataModel.TeamAttendanceDataModel;
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

public class ActivityTeamAttendance extends AppCompatActivity {


    ImageButton homeBtn;

    RecyclerView recyclerView;
    DataAdapter mAdapter;
    private ArrayList<TeamAttendanceDataModel> dataList = new ArrayList<TeamAttendanceDataModel>();

    SweetAlertDialog progressDialog,sweetAlertDialog;

    String code, message;
    JSONObject jsonObject, jo;
    JSONArray jsonArray, ja;

    boolean networkAvailable = false;

    TextView txtFromdate;
    Button fromBtn;

    String toDate="", fromDate="";

    final Calendar myCalendar = Calendar.getInstance();
    final Calendar myCalendar2 = Calendar.getInstance();
    String myFormat = "yyyy-MM-dd"; //In which you need put here
    SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

    SharedPreferences sharedPreferences;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team_attendance);

        sharedPreferences = getSharedPreferences("fom_user",MODE_PRIVATE);

        homeBtn = findViewById(R.id.homeBtn);
        fromBtn = findViewById(R.id.frombtn);
        txtFromdate = findViewById(R.id.date);



        homeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

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


        getSecList();


        fromDate = sdf.format(myCalendar.getTime());
        txtFromdate.setText(fromDate);

        final DatePickerDialog.OnDateSetListener fromdate = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateFromDate();
                getList();
            }

        };

        fromBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(ActivityTeamAttendance.this, fromdate, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });


    }


    private void updateFromDate() {

        fromDate = sdf.format(myCalendar.getTime());
        txtFromdate.setText(sdf.format(myCalendar.getTime()));
    }




    private void getSecList() {


        dataList.clear();

        networkAvailable = CustomUtility.haveNetworkConnection(ActivityTeamAttendance.this);
        if(networkAvailable)
        {
            progressDialog = new SweetAlertDialog(ActivityTeamAttendance.this, SweetAlertDialog.PROGRESS_TYPE);
            progressDialog.setTitle("Please wait...");
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
                                String code = jsonObject.getString("success");
                                String message = jsonObject.getString("message");
                                if (code.equals("true")) {
                                    TeamAttendanceDataModel teamAttendanceDataModel;
                                    jsonArray = jsonObject.getJSONArray("employeeList");
                                    for (int i = 0; i< jsonArray.length(); i++)
                                    {
                                        jo = jsonArray.getJSONObject(i);
                                        if(jo.getString("designation_name").equals("FOE"))
                                        {
                                            teamAttendanceDataModel = new TeamAttendanceDataModel(jo.getString("user_name"),jo.getString("name"),
                                                    jo.getString("territory_name"), "null","null","null","null");
                                            dataList.add(teamAttendanceDataModel);
                                            mAdapter.notifyDataSetChanged();
                                        }

                                    }

                                    getList();
                                }
                                else{
                                    Log.e("mess",message);
                                    CustomUtility.showError(ActivityTeamAttendance.this,message,"Failed");
                                    return;
                                }

                            } catch (JSONException e) {
                                CustomUtility.showWarning(ActivityTeamAttendance.this,
                                        e.getMessage() +". Parsing Failed..",
                                        "Response");
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    progressDialog.dismiss();
                    Log.e("res",error.toString());
                    CustomUtility.showError(ActivityTeamAttendance.this, "Network Error, try again!", "Failed");
                }
            }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    params.put("UserId",sharedPreferences.getString("id",null));
                    return params;
                }
            };

            MySingleton.getInstance(ActivityTeamAttendance.this).addToRequestQue(stringRequest);

        }
        else
        {
            CustomUtility.showWarning(ActivityTeamAttendance.this,"Please turn on internet connection","No Internet");
        }

    }




    // data adapter class for showing the list
    public class DataAdapter extends RecyclerView.Adapter<DataAdapter.MyViewHolder> {

        private List<TeamAttendanceDataModel> dataList;

        public DataAdapter(List<TeamAttendanceDataModel> dataList) {
            this.dataList = dataList;
        }

        @NonNull
        @Override
        public DataAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.team_attendance_row_layout, parent, false);
            return new MyViewHolder(itemView);
        }

        @SuppressLint("ResourceAsColor")
        @Override
        public void onBindViewHolder(@NonNull DataAdapter.MyViewHolder holder, int position) {
            final TeamAttendanceDataModel data = dataList.get(position);

            holder.id.setText(data.getId());
            holder.name.setText(data.getName());
            holder.territory.setText(data.getTerritory());
            if(!data.getPhoto().equals(""))
                holder.image.setImageBitmap(CustomUtility.stringToImage(data.getPhoto()));
            holder.status.setText(data.getStatus());
            if(data.getStatus().equals("Present"))
            {
                holder.status.setTextColor(Color.parseColor("#148026"));
            }
            else if (data.getStatus().equals("Absent"))
            {
                holder.status.setTextColor(Color.parseColor("#E33109"));
            }
            else if(data.getStatus().equals("Late"))
            {
                holder.status.setTextColor(Color.parseColor("#FF9800"));
            }
            holder.time.setText(data.getTime());
            holder.gps.setText(data.getGps());

            holder.image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(!data.getPhoto().equals(""))
                    {
                        ImageView image = new ImageView(ActivityTeamAttendance.this);
                        image.setMaxHeight(200);
                        image.setMaxWidth(230);
                        image.setImageBitmap(CustomUtility.stringToImage(data.getPhoto()));

                        AlertDialog.Builder builder =
                                new AlertDialog.Builder(ActivityTeamAttendance.this).
                                        //setMessage("Message above the image").
                                        setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                            }
                                        }).
                                        setView(image);
                        builder.create().show();
                    }

                }
            });
        }

        @Override
        public int getItemCount() {
            return dataList.size();
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {
            TextView id, name, territory,status, time, gps;
            ImageView image;

            public MyViewHolder(View convertView) {
                super(convertView);

                id =  convertView.findViewById(R.id.id);
                name =  convertView.findViewById(R.id.name);
                territory =  convertView.findViewById(R.id.territory);
                image = convertView.findViewById(R.id.image);
                status = convertView.findViewById(R.id.status);
                time = convertView.findViewById(R.id.time);
                gps = convertView.findViewById(R.id.gpsAccuracy);
            }
        }
    }

    private void getList() {


        //dataList.clear();

        /*
        // for creating dummy data to show
        for (int i = 1; i<50; i++)
        {
            Bitmap bm = BitmapFactory.decodeResource(getResources(), R.drawable.logo);
            TeamAttendanceDataModel stockDataModel = new TeamAttendanceDataModel(String.valueOf(i), "name", "234343", CustomUtility.imageToString(bm),"present",
                    "9.18am","25");
            dataList.add(stockDataModel);
            mAdapter.notifyDataSetChanged();
        }

         */

        networkAvailable = CustomUtility.haveNetworkConnection(ActivityTeamAttendance.this);
        if(networkAvailable)
        {
            progressDialog = new SweetAlertDialog(ActivityTeamAttendance.this, SweetAlertDialog.PROGRESS_TYPE);
            progressDialog.setTitle("Please wait...");
            progressDialog.setCancelable(false);
            progressDialog.show();

            String upLoadServerUri="https://sec.imslpro.com/api/android/get_team_attendance_detail.php";
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
                                if (code.equals("true")) {
                                    TeamAttendanceDataModel teamAttendanceDataModel;
                                    jsonArray = jsonObject.getJSONArray("attendanceSummary");
                                    for(int j = 0;j<dataList.size();j++)
                                    {
                                        for (int i = 0; i< jsonArray.length(); i++)
                                        {
                                            jo = jsonArray.getJSONObject(i);
                                            Log.e("id: ",dataList.get(j).getId());
                                            if(dataList.get(j).getId().equals(jo.getString("EmployeeCode")))
                                            {
                                                Log.e("match","found");
                                                dataList.get(j).setPhoto(jo.getString("AttendancePicture"));
                                                dataList.get(j).setStatus(jo.getString("AttendanceStatusName"));
                                                dataList.get(j).setTime(jo.getString("AttendanceTime"));
                                                dataList.get(j).setGps(jo.getString("InTimeAccuracy"));
                                               // dataList.set(j,dataList.get(j));
                                                //mAdapter.notifyDataSetChanged();

                                            }
                                            else if(dataList.get(j).getStatus().equals("null")) {
                                                dataList.get(j).setStatus("Absent");
                                            }
                                            mAdapter.notifyItemChanged(j);
                                        }

                                    }


                                }
                                else{
                                    Log.e("mess",message);
                                    CustomUtility.showError(ActivityTeamAttendance.this,message,"Failed");
                                    mAdapter.notifyDataSetChanged();
                                    return;
                                }

                            } catch (JSONException e) {
                                CustomUtility.showWarning(ActivityTeamAttendance.this,
                                        e.getMessage() +". Parsing Failed..",
                                        "Response");
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    progressDialog.dismiss();
                    Log.e("res",error.toString());
                    CustomUtility.showError(ActivityTeamAttendance.this, "Network Error, try again!", "Failed");
                }
            }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    params.put("UserId",sharedPreferences.getString("id",null));
                    params.put("DateStart",fromDate);
                    params.put("DateEnd",fromDate);
                    return params;
                }
            };

            MySingleton.getInstance(ActivityTeamAttendance.this).addToRequestQue(stringRequest);

        }
        else
        {
            CustomUtility.showWarning(ActivityTeamAttendance.this,"Please turn on internet connection","No Internet");
        }

    }


}
