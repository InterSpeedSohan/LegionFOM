package com.example.legionfom;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.legionfom.dataModel.FoeAttendanceDataModel;
import com.example.legionfom.helper.CustomUtility;
import com.example.legionfom.helper.MySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class ActivityLeaveAuthorization extends AppCompatActivity {
    String code, message;
    JSONObject jsonObject,jo;
    JSONArray jsonArray, ja, markArray;
    SweetAlertDialog progressDialog,pDialog;
    boolean networkAvailable = false;

    ArrayAdapter<String> foeAdapter;
    ArrayAdapter<String> leaveAdapter;
    Spinner foeSpinner, leaveSpinner;
    Button dateBtn,submitBtn;
    EditText edtRemark;

    ArrayList<String> foeList = new ArrayList<>();
    ArrayList<String> leaveList = new ArrayList<>();
    Map<Integer,String> foeMap = new HashMap<>();
    Map<Integer,String> leaveMap = new HashMap<>();


    String foeId = "", leaveTypeId = "", uploadDate = "", remark = "", leaveDate = "";

    final Calendar myCalendar = Calendar.getInstance();

    SharedPreferences sharedPreferences;

    ImageButton homeBtn;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leave_authorization);

        homeBtn = findViewById(R.id.homeBtn);

        sharedPreferences = getSharedPreferences("fom_user",MODE_PRIVATE);


        foeSpinner = findViewById(R.id.foeSpinner);
        leaveSpinner = findViewById(R.id.leaveTypeSpinner);
        dateBtn = findViewById(R.id.leaveDate);
        edtRemark = findViewById(R.id.remark);
        submitBtn = findViewById(R.id.btnSubmit);

        homeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        foeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String foeName = foeSpinner.getSelectedItem().toString();
                foeId = foeMap.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        leaveSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String leaveTyp = leaveSpinner.getSelectedItem().toString();
                leaveTypeId = leaveMap.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        final DatePickerDialog.OnDateSetListener fromdate = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateDate();
            }

        };

        dateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(ActivityLeaveAuthorization.this, fromdate, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });


        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                networkAvailable = CustomUtility.haveNetworkConnection(ActivityLeaveAuthorization.this);
                remark = edtRemark.getText().toString();
                int f = checkfields();
                if(f==1)
                {
                    if (ContextCompat.checkSelfPermission(ActivityLeaveAuthorization.this, Manifest.permission.INTERNET)
                            != PackageManager.PERMISSION_GRANTED) {
                        // Permission is not granted
                        Log.e("DXXXXXXXXXX", "Not Granted");
                        CustomUtility.showWarning(ActivityLeaveAuthorization.this, "Permission not granted", "Permission");
                    } else {


                        final SweetAlertDialog d = new SweetAlertDialog(ActivityLeaveAuthorization.this, SweetAlertDialog.WARNING_TYPE);
                        d.setTitleText("Confirm Submission?");
                        d.setConfirmText("Yes");
                        d.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {
                                d.dismiss();
                                upload();
                            }
                        });
                        d.setCancelText("No");
                        d.show();
                    }
                }
            }
        });


        // for getting foe list
        getFoeList();

    }

    private void updateDate() {
        String myFormat = "dd-MM-yyyy"; //In which you need put here
        String uploadFormat = "yyyy-MM-dd";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        SimpleDateFormat uploadSdf = new SimpleDateFormat(uploadFormat, Locale.US);
        Log.e("FromdDate",sdf.format(myCalendar.getTime()));
        uploadDate = uploadSdf.format(myCalendar.getTime());
        leaveDate = sdf.format(myCalendar.getTime());
        dateBtn.setText(sdf.format(myCalendar.getTime()));
    }
    public int checkfields()
    {
        if(!networkAvailable)
        {
            CustomUtility.showWarning(ActivityLeaveAuthorization.this, "Please turn on your internet connection","No Internet!");
            return -1;
        }
        else if(foeId.equals(""))
        {
            CustomUtility.showWarning(ActivityLeaveAuthorization.this, "Please select an SEC","Required feilds!");
            return -1;
        }
        else if(leaveDate.equals(""))
        {
            CustomUtility.showWarning(ActivityLeaveAuthorization.this, "Please select a leave date","Required feilds!");
            return -1;
        }
        else if(leaveTypeId.equals(""))
        {
            CustomUtility.showWarning(ActivityLeaveAuthorization.this, "Please select a leave type","Required feilds!");
            return -1;
        }
        return 1;
    }

    public void getFoeList()
    {
        foeList.clear();
        progressDialog = new SweetAlertDialog(ActivityLeaveAuthorization.this, SweetAlertDialog.PROGRESS_TYPE);
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
                                String fname,fid;
                                int index = 0;
                                jsonArray = jsonObject.getJSONArray("employeeList");
                                for (int i = 0;i<jsonArray.length();i++)
                                {
                                    jo = jsonArray.getJSONObject(i);
                                    if(!jo.getString("designation_name").equals("FOE")) continue;
                                    fname = jo.getString("name");
                                    fid = jo.getString("id");
                                    foeList.add(fname);
                                    foeMap.put(index++,fid);
                                }
                                foeAdapter = new ArrayAdapter<String>(ActivityLeaveAuthorization.this,R.layout.spinner_item,foeList);
                                foeSpinner.setAdapter(foeAdapter);

                                getLeaveTypes();
                            }
                            else
                            {
                                new SweetAlertDialog(ActivityLeaveAuthorization.this, SweetAlertDialog.ERROR_TYPE)
                                        .setTitleText("Failed")
                                        .setContentText(message)
                                        .setConfirmText("Ok")
                                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                            @Override
                                            public void onClick(SweetAlertDialog sDialog) {
                                                sDialog.dismissWithAnimation();
                                                startActivity(getIntent());
                                                finish();
                                            }
                                        })
                                        .show();
                                //CustomUtility.showError(ActivityLeaveAuthorization.this,message,"Failed");
                            }

                        } catch (JSONException e) {
                            CustomUtility.showError(ActivityLeaveAuthorization.this, "Failed to get data", "Failed");
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                pDialog = new SweetAlertDialog(ActivityLeaveAuthorization.this, SweetAlertDialog.ERROR_TYPE);
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

        MySingleton.getInstance(ActivityLeaveAuthorization.this).addToRequestQue(stringRequest);
    }



    // for getting leave types
    private void getLeaveTypes() {

        leaveList.clear();

        progressDialog = new SweetAlertDialog(ActivityLeaveAuthorization.this,SweetAlertDialog.PROGRESS_TYPE);
        progressDialog.setTitle("Please wait...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        String upLoadServerUri="https://sec.imslpro.com/api/android/get_leave_types.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, upLoadServerUri,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            String rname, rid;
                            progressDialog.dismiss();
                            Log.e("response",response);
                            jsonObject = new JSONObject(response);
                            code = jsonObject.getString("success");
                            message = jsonObject.getString("message");
                            if(code.equals("true"))
                            {
                                jsonArray = jsonObject.getJSONArray("leaveTypeList");
                                for (int i = 0;i<jsonArray.length();i++)
                                {
                                    rname = jsonArray.getJSONObject(i).getString("attendance_status_name");
                                    rid = jsonArray.getJSONObject(i).getString("attendance_status_id");
                                    leaveList.add(rname);
                                    leaveMap.put(i,rid);
                                }
                                leaveAdapter = new ArrayAdapter<String>(ActivityLeaveAuthorization.this,R.layout.spinner_item,leaveList);
                                leaveSpinner.setAdapter(leaveAdapter);
                            }
                            else
                            {
                                new SweetAlertDialog(ActivityLeaveAuthorization.this, SweetAlertDialog.ERROR_TYPE)
                                        .setTitleText("Failed")
                                        .setContentText(message)
                                        .setConfirmText("Ok")
                                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                            @Override
                                            public void onClick(SweetAlertDialog sDialog) {
                                                sDialog.dismissWithAnimation();
                                                startActivity(getIntent());
                                                finish();
                                            }
                                        })
                                        .show();
                                //CustomUtility.showError(ActivityLeaveAuthorization.this,message,"Failed");
                            }

                        } catch (JSONException e) {
                            CustomUtility.showError(ActivityLeaveAuthorization.this, "Failed to get data", "Failed");
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                pDialog = new SweetAlertDialog(ActivityLeaveAuthorization.this, SweetAlertDialog.ERROR_TYPE);
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
                params.put("UserId", sharedPreferences.getString("id","-1"));
                return params;
            }
        };

        MySingleton.getInstance(ActivityLeaveAuthorization.this).addToRequestQue(stringRequest);
    }


    // for submiting leave authorization
    private void upload() {


        progressDialog = new SweetAlertDialog(ActivityLeaveAuthorization.this,SweetAlertDialog.PROGRESS_TYPE);
        progressDialog.setTitle("Please wait...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        String upLoadServerUri="https://sec.imslpro.com/api/android/insert_leave.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, upLoadServerUri,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            String rname, rid;
                            progressDialog.dismiss();
                            Log.e("response",response);
                            jsonObject = new JSONObject(response);
                            code = jsonObject.getString("success");
                            message = jsonObject.getString("message");
                            if(code.equals("true"))
                            {
                                CustomUtility.showSuccess(ActivityLeaveAuthorization.this,message,"Success");
                            }
                            else
                            {
                                CustomUtility.showError(ActivityLeaveAuthorization.this,message,"Failed");
                            }

                        } catch (JSONException e) {
                            CustomUtility.showError(ActivityLeaveAuthorization.this, "Failed to get data", "Failed");
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                pDialog = new SweetAlertDialog(ActivityLeaveAuthorization.this, SweetAlertDialog.ERROR_TYPE);
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
                params.put("UserId", sharedPreferences.getString("id",null));
                params.put("LeaveEmployeeId",foeId);
                params.put("LeaveDate",uploadDate);
                params.put("LeaveTypeId",leaveTypeId);

                return params;
            }
        };

        MySingleton.getInstance(ActivityLeaveAuthorization.this).addToRequestQue(stringRequest);

    }

}
