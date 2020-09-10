package com.example.legionfom;

import android.app.DatePickerDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.legionfom.helper.CustomUtility;
import com.example.legionfom.helper.MySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class MyAttendanceSummaryActivity extends AppCompatActivity {

    ImageButton homeBtn;

    public static String code = "", message = "";
    TextView txtPresent, txtLatePresent, txtDayOff, txtTraining, txtRtClose, txtCasualLeave, txtHalfDayLeave, txtSickLeave,
        txtTotalLeave, txtAbsent, txtTotal;

    TextView txtFromdate, txtTodate;
    Button fromBtn, toBtn;

    String toDate="", fromDate="";

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
        setContentView(R.layout.activity_my_attendance_summary);

        sharedPreferences = getSharedPreferences("fom_user",MODE_PRIVATE);

        homeBtn = findViewById(R.id.homeBtn);

        txtPresent = findViewById(R.id.present);
        txtLatePresent = findViewById(R.id.latePresent);
        txtDayOff = findViewById(R.id.dayOff);
        txtTraining = findViewById(R.id.training);
        txtRtClose = findViewById(R.id.rtClose);
        txtCasualLeave = findViewById(R.id.casualLeave);
        txtHalfDayLeave = findViewById(R.id.halfDayLeave);
        txtSickLeave = findViewById(R.id.sickLeave);
        txtTotalLeave = findViewById(R.id.totalLeave);
        txtAbsent = findViewById(R.id.absent);
        txtTotal = findViewById(R.id.total);

        txtFromdate = findViewById(R.id.fromdate);
        txtTodate = findViewById(R.id.todate);
        fromBtn = findViewById(R.id.frombtn);
        toBtn = findViewById(R.id.tobtn);


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
                new DatePickerDialog(MyAttendanceSummaryActivity.this, fromdate, myCalendar
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
                    CustomUtility.showWarning(MyAttendanceSummaryActivity.this,"Select correct date","Failed");
                }
                else{
                    updateToDate();
                    getDetails();
                }

            }

        };

        toBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(fromDate.equals(""))
                {
                    CustomUtility.showWarning(MyAttendanceSummaryActivity.this,"Select from date first","Failed");
                }
                else
                {
                    new DatePickerDialog(MyAttendanceSummaryActivity.this, todate, myCalendar2
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


    public void getDetails()
    {
        sweetAlertDialog = new SweetAlertDialog(MyAttendanceSummaryActivity.this, SweetAlertDialog.PROGRESS_TYPE);
        sweetAlertDialog.setTitleText("Loading");
        sweetAlertDialog.show();
        String upLoadServerUri = "https://sec.imslpro.com/api/android/get_my_attendance_summary.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, upLoadServerUri,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            sweetAlertDialog.dismiss();
                            Log.e("response",response);
                            jsonObject = new JSONObject(response);
                            code = jsonObject.getString("success");
                            message = jsonObject.getString("message");
                            if (code.equals("true")) {
                                int sum = 0;
                                int total = 0;
                                jsonArray = jsonObject.getJSONArray("attendanceSummary");
                                for (int i = 0; i< jsonArray.length(); i++)
                                {
                                    jo = jsonArray.getJSONObject(i);
                                    total += Integer.parseInt(jo.getString("Total"));
                                    if(jo.getString("StatusName").equals("Present"))
                                    {
                                        txtPresent.setText(jo.getString("Total"));
                                    }
                                    else if(jo.getString("StatusName").equals("Late"))
                                    {
                                        txtLatePresent.setText(jo.getString("Total"));
                                    }
                                    else if(jo.getString("StatusName").equals("Casual Leave"))
                                    {
                                        txtCasualLeave.setText(jo.getString("Total"));
                                        sum += Integer.parseInt(jo.getString("Total"));
                                    }
                                    else if(jo.getString("StatusName").equals("Sick Leave"))
                                    {
                                        txtSickLeave.setText(jo.getString("Total"));
                                        sum += Integer.parseInt(jo.getString("Total"));
                                    }
                                    else if(jo.getString("StatusName").equals("Half Day Leave"))
                                    {
                                        txtHalfDayLeave.setText(jo.getString("Total"));
                                        sum += Integer.parseInt(jo.getString("Total"));
                                    }
                                    else if(jo.getString("StatusName").equals("Absent"))
                                    {
                                        txtAbsent.setText(jo.getString("Total"));
                                    }
                                    else if(jo.getString("StatusName").equals("Day-Off"))
                                    {
                                        txtDayOff.setText(jo.getString("Total"));
                                    }
                                    else if(jo.getString("StatusName").equals("RT Close"))
                                    {
                                        txtRtClose.setText(jo.getString("Total"));
                                    }
                                    else if(jo.getString("StatusName").equals("Trainning"))
                                    {
                                        txtTraining.setText(jo.getString("Total"));
                                    }

                                }

                                txtTotalLeave.setText(String.valueOf(sum));
                                txtTotal.setText(String.valueOf(total));

                            }
                            else{
                                Log.e("mess",message);
                                CustomUtility.showError(MyAttendanceSummaryActivity.this,message,"Failed");
                                return;
                            }
                        } catch (JSONException e) {
                            CustomUtility.showError(MyAttendanceSummaryActivity.this, e.getMessage(), "Getting Response");
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                sweetAlertDialog.dismiss();
                Log.e("res",error.toString());
                CustomUtility.showError(MyAttendanceSummaryActivity.this, "Network Error, try again!", "Failed");
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

        MySingleton.getInstance(MyAttendanceSummaryActivity.this).addToRequestQue(stringRequest);
    }







}
