package com.example.legionfom;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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

import java.util.HashMap;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class ActivityDockPanel extends AppCompatActivity {
    SweetAlertDialog sweetAlertDialog;
    JSONObject jsonObject;
    JSONArray jsonArray;
    String Json_String;
    boolean networkAvailable = false;
    public static String code = "", message = "";

    ImageButton attendanceBtn, salesBtn, stockBtn, statesBtn,logoutBtn;

    TextView empName,empCode, empArea,txtDaypassed, txtAchievement, txtTotalTargetNumber, txtTotalTargetValue,txtMtdTargetNumber, txtMtdTargetValue,
            txtTodayTargetNumber, txtTodayTargetValue,txtTodayAchievemenNumber,txtTodayAchievementValue;
    TextView txtPresentStatus;
    public static Boolean presentFlag = false;
    String attendanceStatus = "";
    SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dock_panel);

        sharedPreferences = getSharedPreferences("fom_user",MODE_PRIVATE);
        Log.e("id from shared",sharedPreferences.getString("id",null));
        Log.e("name from shared",sharedPreferences.getString("name",null));

        empName = findViewById(R.id.tvName);
        empCode = findViewById(R.id.tvId);
        empArea = findViewById(R.id.tvArea);
        txtPresentStatus = findViewById(R.id.presentStatus);

        txtDaypassed = findViewById(R.id.dayPassed);
        txtAchievement = findViewById(R.id.achievement);
        txtTotalTargetNumber = findViewById(R.id.totalTargetNumber);
        txtTotalTargetValue = findViewById(R.id.totalTargetValue);
        txtMtdTargetNumber = findViewById(R.id.mtdTargetNumber);
        txtMtdTargetValue = findViewById(R.id.mtdTargetValue);
        txtTodayTargetNumber = findViewById(R.id.todayTargetNumber);
        txtTodayTargetValue = findViewById(R.id.todayTargetValue);
        txtTodayAchievemenNumber = findViewById(R.id.todayAchievementNumber);
        txtTodayAchievementValue = findViewById(R.id.todayAchievementValue);


        getAttendanceStatus();

        empName.setText(sharedPreferences.getString("name",null));
        empCode.setText(sharedPreferences.getString("code",null));
        empArea.setText(sharedPreferences.getString("territoryName",null));


        attendanceBtn = findViewById(R.id.attendanceBtn);
        salesBtn = findViewById(R.id.salesBtn);
        stockBtn = findViewById(R.id.stockBtn);
        statesBtn = findViewById(R.id.stateBtn);
        logoutBtn = findViewById(R.id.logoutBtn);


        attendanceBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ActivityDockPanel.this,ActivityAttendancePanel.class));
                finish();
            }
        });
        salesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ActivityDockPanel.this,SalesPanel.class));
            }
        });
        statesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ActivityDockPanel.this, StatePanelActivity.class));
            }
        });
        stockBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ActivityDockPanel.this, StockPanelActivity.class));
            }
        });

        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final SweetAlertDialog log = new SweetAlertDialog(ActivityDockPanel.this, SweetAlertDialog.WARNING_TYPE);
                log.setTitleText("Are you sure to Sign Out?");
                log.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.clear();
                        editor.apply();
                        log.dismissWithAnimation();
                        finish();
                    }
                });
                log.setCancelText("No");
                log.setConfirmText("Ok");
                log.show();
            }
        });
    }

    public void getAttendanceStatus() {
        networkAvailable = CustomUtility.haveNetworkConnection(ActivityDockPanel.this);
        if (networkAvailable) {

            sweetAlertDialog = new SweetAlertDialog(ActivityDockPanel.this, SweetAlertDialog.PROGRESS_TYPE);
            sweetAlertDialog.setTitleText("Loading");
            sweetAlertDialog.show();
            String upLoadServerUri = "https://sec.imslpro.com/api/android/get_employee_attendance_status.php";
            StringRequest stringRequest = new StringRequest(Request.Method.POST, upLoadServerUri,
                    new Response.Listener<String>() {
                        @SuppressLint("ResourceAsColor")
                        @Override
                        public void onResponse(String response) {
                            sweetAlertDialog.dismiss();
                            try {
                                Log.e("response", response);
                                jsonObject = new JSONObject(response);
                                code = jsonObject.getString("success");
                                message = jsonObject.getString("message");
                                if (code.equals("true")) {
                                    txtPresentStatus.setVisibility(View.VISIBLE);
                                    attendanceStatus = jsonObject.getString("attendanceStatus");
                                    if (!attendanceStatus.equals("NA")) {
                                        presentFlag = true;
                                        txtPresentStatus.setVisibility(View.VISIBLE);
                                        if (attendanceStatus.equals("Late")) {
                                            txtPresentStatus.setText(R.string.late);
                                            txtPresentStatus.setTextColor(Color.parseColor("#FF9800"));
                                        } else if(attendanceStatus.equals("Present")) {
                                            txtPresentStatus.setText(R.string.present);
                                            txtPresentStatus.setTextColor(Color.parseColor("#00FF04"));
                                        }
                                        else{
                                            txtPresentStatus.setText("You are on "+attendanceStatus);
                                            txtPresentStatus.setTextColor(Color.parseColor("#FF9800"));
                                        }

                                    }
                                    getDetails();
                                } else {
                                    Log.e("mess", message);
                                    CustomUtility.showError(ActivityDockPanel.this, message, "Failed");
                                    return;
                                }
                            } catch (JSONException e) {
                                CustomUtility.showError(ActivityDockPanel.this, e.getMessage(), "Getting Response");
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("res", error.toString());
                    sweetAlertDialog.dismiss();
                    //CustomUtility.showError(Dockpanel.this, "Network Error, try again!", "Failed");
                    startActivity(getIntent());
                    finish();
                }
            }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    params.put("UserId", sharedPreferences.getString("id", null));
                    return params;
                }
            };

            MySingleton.getInstance(ActivityDockPanel.this).addToRequestQue(stringRequest);
        } else {
            new SweetAlertDialog(ActivityDockPanel.this, SweetAlertDialog.WARNING_TYPE)
                    .setTitleText("No Internet")
                    .setContentText("Please turn on internet connection")
                    .setConfirmText("Ok")
                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sDialog) {
                            sDialog.dismissWithAnimation();
                            startActivity(new Intent(ActivityDockPanel.this, ActivityDockPanel.class));
                            finish();
                        }
                    })
                    .show();
        }
    }


    // for getting the dashboard summary
    public void getDetails()
    {
        sweetAlertDialog = new SweetAlertDialog(ActivityDockPanel.this, SweetAlertDialog.PROGRESS_TYPE);
        sweetAlertDialog.setTitleText("Loading");
        sweetAlertDialog.show();
        String upLoadServerUri = "https://sec.imslpro.com/api/android/get_dashboard_mobile.php";
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
                                jsonObject = jsonObject.getJSONObject("dashboardData");
                                int actWdays = Integer.parseInt(jsonObject.getString("monthlyWDays"));
                                int actWdaysLeft = Integer.parseInt(jsonObject.getString("actualWdaysLeft"));
                                txtDaypassed.setText(String.valueOf(Math.round((actWdays-actWdaysLeft)*100.0/actWdays))+"%");
                                int monthlySale = Integer.parseInt(jsonObject.getString("monthlySaleAmount"));
                                int monthlyTargetAmount = Integer.parseInt(jsonObject.getString("monthlyTargetAmount"));
                                txtAchievement.setText(String.valueOf(Math.round(monthlySale*100.0/monthlyTargetAmount))+"%");

                                txtTotalTargetNumber.setText(jsonObject.getString("monthlyTargetQuantity"));
                                txtTotalTargetValue.setText(jsonObject.getString("monthlyTargetAmount"));

                                txtMtdTargetNumber.setText(String.valueOf(Integer.parseInt(jsonObject.getString("monthlySaleQuantity"))));
                                txtMtdTargetValue.setText(String.valueOf(Integer.parseInt(jsonObject.getString("monthlySaleAmount"))));

                                txtTodayTargetNumber.setText(String.valueOf(Math.round(Double.parseDouble(jsonObject.getString("todayTargetQuantity")))));
                                txtTodayTargetValue.setText(String.valueOf(Math.round(Double.parseDouble(jsonObject.getString("todayTargetAmount")))));

                                txtTodayAchievemenNumber.setText(String.valueOf(Math.round(Double.parseDouble(jsonObject.getString("todaySaleQuantity")))));
                                txtTodayAchievementValue.setText(String.valueOf(Math.round(Double.parseDouble(jsonObject.getString("todaySaleAmount")))));


                            }
                            else{
                                Log.e("mess",message);
                                CustomUtility.showError(ActivityDockPanel.this,message,"Loading Data Failed");
                            }
                        } catch (JSONException e) {
                            CustomUtility.showError(ActivityDockPanel.this, e.getMessage(), "Getting Response");
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                sweetAlertDialog.dismiss();
                Log.e("res",error.toString());
                final SweetAlertDialog s = new SweetAlertDialog(ActivityDockPanel.this, SweetAlertDialog.WARNING_TYPE);
                s.setTitleText("Network Error try again");
                s.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        startActivity(getIntent());
                        finish();
                    }
                });
                s.setConfirmText("Ok");
                s.show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("UserId",sharedPreferences.getString("id",null));
                return params;
            }
        };

        MySingleton.getInstance(ActivityDockPanel.this).addToRequestQue(stringRequest);
    }
}
