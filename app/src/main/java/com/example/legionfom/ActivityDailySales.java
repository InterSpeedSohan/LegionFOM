package com.example.legionfom;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
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
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.legionfom.dataModel.DailySalesDataModel;
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

public class ActivityDailySales extends AppCompatActivity {

    RecyclerView recyclerView;
    DataAdapter mAdapter;
    private ArrayList<DailySalesDataModel> dataList = new ArrayList<DailySalesDataModel>();

    ImageButton homeBtn;

    Button retailBtn, modelBtn, delRetailBtn, delModelBtn, territoryBtn, delTerritoryBtn;

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

    String modelName = "", modelId = "", retailName = "", retailId = "", territoryName = "", territoryId = "";
    ArrayAdapter<String> arrayAdapter;
    ArrayAdapter<String> arrayAdapter1;
    ArrayAdapter<String> arrayAdapter2;
    ArrayList retailList = new ArrayList<String>();
    ArrayList modelList = new ArrayList<String>();
    ArrayList territoryList = new ArrayList<String>();
    Map<Integer,String> territoryMap = new HashMap<>();
    Map<Integer,String> retailIdMap = new HashMap<>();
    Map<Integer,String> modelIdMap = new HashMap<>();
    Map<Integer,String> retailTerritoryMap = new HashMap<>();
    String  r, rid, m, mid,t,tid;

    TextView txtTotalVal, txtTotalVol;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_sales_report);

        sharedPreferences = getSharedPreferences("fom_user",MODE_PRIVATE);

        homeBtn = findViewById(R.id.homeBtn);

        retailBtn = findViewById(R.id.retailBtn);
        modelBtn = findViewById(R.id.modelBtn);
        delModelBtn = findViewById(R.id.delModelBtn);
        delRetailBtn = findViewById(R.id.delRetailBtn);
        territoryBtn = findViewById(R.id.territoryBtn);
        delTerritoryBtn = findViewById(R.id.delTerritoryBtn);

        txtTotalVal = findViewById(R.id.totalVal);
        txtTotalVol = findViewById(R.id.totalVol);


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


        delRetailBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetRetailSelect();
                if(!toDate.equals(""))
                    getDetails();
            }
        });

        delModelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                modelId = "";
                modelBtn.setText("Select model");
                modelName = "";
                if(!toDate.equals(""))
                    getDetails();
            }
        });

        //for getting the retail list
        getRetailList();

        retailBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                androidx.appcompat.app.AlertDialog.Builder builderSingle = new androidx.appcompat.app.AlertDialog.Builder(ActivityDailySales.this);
                builderSingle.setIcon(R.drawable.logo);
                builderSingle.setTitle("Select retail");
                arrayAdapter = new ArrayAdapter<String>(ActivityDailySales.this, R.layout.custom_list_item,retailList);

                builderSingle.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        tid = retailTerritoryMap.get(which);
                        if(territoryId.equals("") | tid.equals(territoryId))
                        {
                            retailName = arrayAdapter.getItem(which);
                            retailId = retailIdMap.get(which);
                            retailBtn.setText(retailName);
                            getDetails();
                        }
                        else
                        {
                            CustomUtility.showWarning(ActivityDailySales.this,"This retail is not under your selected territory","Selection Error");
                        }
                    }
                });
                builderSingle.show();
            }
        });

        modelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!toDate.equals(""))
                {
                    AlertDialog.Builder modelDialog = new AlertDialog.Builder(ActivityDailySales.this);
                    modelDialog.setIcon(R.drawable.logo);
                    modelDialog.setTitle("Select a model");
                    arrayAdapter1 = new ArrayAdapter<>(ActivityDailySales.this, R.layout.custom_list_item, modelList);

                    modelDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });

                    modelDialog.setAdapter(arrayAdapter1, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            modelName = arrayAdapter1.getItem(which);
                            modelId = modelIdMap.get(which);
                            modelBtn.setText(modelName);
                            getDetails();
                        }
                    });
                    modelDialog.show();
                }
                else
                {
                    CustomUtility.showWarning(ActivityDailySales.this,"Please select date range first","Required Fields");
                }
            }
        });


        delTerritoryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                territoryId = "";
                territoryName = "";
                territoryBtn.setText("Select territory");
                getDetails();


            }
        });

        territoryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                androidx.appcompat.app.AlertDialog.Builder builderSingle = new androidx.appcompat.app.AlertDialog.Builder(ActivityDailySales.this);
                builderSingle.setIcon(R.drawable.logo);
                builderSingle.setTitle("Select territory");
                arrayAdapter = new ArrayAdapter<String>(ActivityDailySales.this, R.layout.custom_list_item,territoryList);

                builderSingle.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        territoryName = arrayAdapter.getItem(which);
                        territoryId = territoryMap.get(which);
                        territoryBtn.setText(territoryName);
                        resetRetailSelect();
                        getDetails();

                    }
                });
                builderSingle.show();
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
                updateFromDate();
                getDetails();
            }

        };

        fromBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(ActivityDailySales.this, fromdate, myCalendar
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
                    CustomUtility.showWarning(ActivityDailySales.this,"Select correct date","Failed");
                }
                else{
                    updateToDate();

                    networkAvailable = CustomUtility.haveNetworkConnection(ActivityDailySales.this);
                    if (networkAvailable) getDetails();
                    else CustomUtility.showWarning(ActivityDailySales.this,"Please turn on internet connection","No inernet");
                }

            }

        };

        toBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(fromDate.equals(""))
                {
                    CustomUtility.showWarning(ActivityDailySales.this,"Select from date first","Failed");
                }
                else
                {
                    new DatePickerDialog(ActivityDailySales.this, todate, myCalendar2
                            .get(Calendar.YEAR), myCalendar2.get(Calendar.MONTH),
                            myCalendar2.get(Calendar.DAY_OF_MONTH)).show();
                }

            }
        });
    }

    private void resetRetailSelect()
    {
        retailId = "";
        retailName = "";
        retailBtn.setText("Select retail");
    }
    private void updateFromDate() {
        fromDate = sdf.format(myCalendar.getTime());
        txtFromdate.setText(sdf.format(myCalendar.getTime()));
    }

    private void updateToDate() {

        toDate = sdf.format(myCalendar2.getTime());
        txtTodate.setText(sdf.format(myCalendar2.getTime()));
    }


    // for getting all retail list
    private void getRetailList() {


        progressDialog = new SweetAlertDialog(ActivityDailySales.this, SweetAlertDialog.PROGRESS_TYPE);
        progressDialog.setTitle("Please wait...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        String upLoadServerUri="https://sec.imslpro.com/api/android/get_retail_list.php";
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
                                jsonArray = jsonObject.getJSONArray("retailList");
                                for (int i = 0;i<jsonArray.length();i++)
                                {
                                    r = (jsonArray.getJSONObject(i).getString("name")+"("+jsonArray.getJSONObject(i).getString("territory_name")+")");
                                    rid = (jsonArray.getJSONObject(i).getString("id"));

                                    // adding to the list to show in dialog box with the count
                                    retailList.add(r);

                                    // mapping id to index serial of the retail
                                    retailIdMap.put(i,rid);
                                    retailTerritoryMap.put(i,jsonArray.getJSONObject(i).getString("territory_id"));
                                }

                                getTerrirotyList();
                            }
                            else
                            {
                                CustomUtility.showError(ActivityDailySales.this,message,"Failed");
                            }

                        } catch (JSONException e) {
                            CustomUtility.showError(ActivityDailySales.this, "Failed to get data", "Failed");
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                pDialog = new SweetAlertDialog(ActivityDailySales.this, SweetAlertDialog.ERROR_TYPE);
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

        MySingleton.getInstance(ActivityDailySales.this).addToRequestQue(stringRequest);

    }


    // for getting all territories list
    private void getTerrirotyList() {


        progressDialog = new SweetAlertDialog(ActivityDailySales.this, SweetAlertDialog.PROGRESS_TYPE);
        progressDialog.setTitle("Please wait...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        String upLoadServerUri="https://sec.imslpro.com/api/android/get_territory_list.php";
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
                                jsonArray = jsonObject.getJSONArray("territoryList");
                                for (int i = 0;i<jsonArray.length();i++)
                                {
                                    t = (jsonArray.getJSONObject(i).getString("name"));
                                    tid = (jsonArray.getJSONObject(i).getString("id"));

                                    // adding to the list to show in dialog box with the count
                                    territoryList.add(t);

                                    // mapping id to index serial of the retail
                                    territoryMap.put(i,tid);
                                }

                                // getting model list
                                getModelList();
                            }
                            else
                            {
                                CustomUtility.showError(ActivityDailySales.this,message,"Failed");
                            }

                        } catch (JSONException e) {
                            CustomUtility.showError(ActivityDailySales.this, "Failed to get data", "Failed");
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                pDialog = new SweetAlertDialog(ActivityDailySales.this, SweetAlertDialog.ERROR_TYPE);
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

        MySingleton.getInstance(ActivityDailySales.this).addToRequestQue(stringRequest);

    }

    public void getModelList()
    {
        progressDialog = new SweetAlertDialog(ActivityDailySales.this, SweetAlertDialog.PROGRESS_TYPE);
        progressDialog.setTitle("Please wait....");
        progressDialog.setCancelable(false);
        progressDialog.show();

        String upLoadServerUri="https://sec.imslpro.com/api/android/get_product_list.php";
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
                                jsonArray = jsonObject.getJSONArray("productList");
                                for (int i = 0;i<jsonArray.length();i++)
                                {
                                    m = (jsonArray.getJSONObject(i).getString("name"));
                                    mid = (jsonArray.getJSONObject(i).getString("id"));

                                    // adding to the list to show in dialog box with the count
                                    modelList.add(m);

                                    // mapping id to index serial of the retail
                                    modelIdMap.put(i,mid);
                                }
                                getDetails();
                            }
                            else
                            {
                                CustomUtility.showError(ActivityDailySales.this,message,"Failed");
                            }

                        } catch (JSONException e) {
                            CustomUtility.showError(ActivityDailySales.this, "Failed to get data", "Failed");
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                pDialog = new SweetAlertDialog(ActivityDailySales.this, SweetAlertDialog.ERROR_TYPE);
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
                //params.put("UserId",sharedPreferences.getString("id",null));
                return params;
            }
        };

        MySingleton.getInstance(ActivityDailySales.this).addToRequestQue(stringRequest);
    }

    public void getDetails() {

        dataList.clear();


        sweetAlertDialog = new SweetAlertDialog(ActivityDailySales.this, SweetAlertDialog.PROGRESS_TYPE);
        sweetAlertDialog.setTitleText("Loading");
        sweetAlertDialog.show();
        String upLoadServerUri = "https://sec.imslpro.com/api/android/get_sales_by_day.php";
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
                                Integer totalVol = 0, totalVal = 0;
                                DailySalesDataModel dailySalesDataModel;
                                jsonArray = jsonObject.getJSONArray("saleResult");
                                for (int i = 0; i< jsonArray.length(); i++)
                                {
                                    totalVol = totalVol + Integer.valueOf(jsonArray.getJSONObject(i).getString("sale_volume"));
                                    totalVal = totalVal + Integer.valueOf(jsonArray.getJSONObject(i).getString("sale_value"));
                                    dailySalesDataModel = new DailySalesDataModel(jsonArray.getJSONObject(i).getString("sale_date"),
                                            jsonArray.getJSONObject(i).getString("sale_volume"),jsonArray.getJSONObject(i).getString("sale_value"));
                                    dataList.add(dailySalesDataModel);
                                    mAdapter.notifyDataSetChanged();
                                }

                                txtTotalVal.setText(String.valueOf(totalVal));
                                txtTotalVol.setText(String.valueOf(totalVol));

                            }
                            else{
                                Log.e("mess",message);
                                CustomUtility.showError(ActivityDailySales.this,message,"Failed");
                                dataList.clear();
                                mAdapter.notifyDataSetChanged();
                                txtTotalVol.setText("0");
                                txtTotalVal.setText("0");
                                return;
                            }
                        } catch (JSONException e) {
                            CustomUtility.showError(ActivityDailySales.this, e.getMessage(), "Getting Response");
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                sweetAlertDialog.dismiss();
                Log.e("res",error.toString());
                CustomUtility.showError(ActivityDailySales.this, "Network Error, try again!", "Failed");
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("UserId",sharedPreferences.getString("id",null));
                params.put("SaleDateStart",fromDate);
                params.put("SaleDateEnd",toDate);
                if (!retailId.equals("")) params.put("RetailId",retailId);
                if(!modelId.equals("")) params.put("ProductId",modelId);
                params.put("TerritoryId",territoryId);
                return params;
            }
        };

        MySingleton.getInstance(ActivityDailySales.this).addToRequestQue(stringRequest);
    }



    // data adapter class for showing the list
    public class DataAdapter extends RecyclerView.Adapter<DataAdapter.MyViewHolder> {

        private List<DailySalesDataModel> dataList;

        public DataAdapter(List<DailySalesDataModel> dataList) {
            this.dataList = dataList;
        }

        @NonNull
        @Override
        public DataAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.daily_sales_row_layout, parent, false);
            return new DataAdapter.MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull DataAdapter.MyViewHolder holder, int position) {
            final DailySalesDataModel data = dataList.get(position);
            holder.date.setText(data.getDate());
            holder.saleVolume.setText(data.getSaleVolume());
            holder.saleValue.setText(data.getSaleValue());

        }

        @Override
        public int getItemCount() {
            return dataList.size();
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {
            TextView date, saleVolume, saleValue;

            public MyViewHolder(View convertView) {
                super(convertView);

                date =  convertView.findViewById(R.id.date);
                saleValue =  convertView.findViewById(R.id.saleValue);
                saleVolume =  convertView.findViewById(R.id.saleVolume);

            }
        }
    }


}
