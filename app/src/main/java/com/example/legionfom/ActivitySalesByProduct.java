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
import com.example.legionfom.dataModel.SalesByProductDataModel;
import com.example.legionfom.helper.CustomUtility;
import com.example.legionfom.helper.MySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class ActivitySalesByProduct extends AppCompatActivity {

    RecyclerView recyclerView;
    DataAdapter mAdapter;
    private ArrayList<SalesByProductDataModel> dataList = new ArrayList<SalesByProductDataModel>();

    ImageButton homeBtn;

    Button retailBtn, delRetailBtn, territoryBtn, delTerritoryBtn;

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

    String retailName = "", retailId = "", territoryName = "", territoryId = "";
    ArrayAdapter<String> arrayAdapter;
    ArrayAdapter<String> arrayAdapter1;
    ArrayList retailList = new ArrayList<String>();
    ArrayList territoryList = new ArrayList<String>();
    Map<Integer,String> territoryMap = new HashMap<>();
    Map<Integer,String> retailIdMap = new HashMap<>();
    Map<Integer,String> retailTerritoryMap = new HashMap<>();
    String  r, rid, t, tid;
    DecimalFormat df = new DecimalFormat("#.##");
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sales_by_products);

        sharedPreferences = getSharedPreferences("fom_user",MODE_PRIVATE);

        homeBtn = findViewById(R.id.homeBtn);

        retailBtn = findViewById(R.id.retailBtn);
        delRetailBtn = findViewById(R.id.delRetailBtn);
        territoryBtn = findViewById(R.id.territoryBtn);
        delTerritoryBtn = findViewById(R.id.delTerritoryBtn);

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



        // making from date as first date of current month
        myCalendar.set(Calendar.DAY_OF_MONTH,1);
        fromDate = sdf.format(myCalendar.getTime());
        txtFromdate.setText(fromDate);
        toDate = sdf.format(myCalendar2.getTime());
        txtTodate.setText(toDate);

        homeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        //for getting the retail list
        getRetailList();
        delRetailBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetRetailSelect();
                if(!toDate.equals(""))
                    getDetails();
            }
        });

        retailBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                androidx.appcompat.app.AlertDialog.Builder builderSingle = new androidx.appcompat.app.AlertDialog.Builder(ActivitySalesByProduct.this);
                builderSingle.setIcon(R.drawable.logo);
                builderSingle.setTitle("Select retail");
                arrayAdapter = new ArrayAdapter<String>(ActivitySalesByProduct.this, R.layout.custom_list_item,retailList);

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
                            CustomUtility.showWarning(ActivitySalesByProduct.this,"This retail is not under your selected territory","Selection Error");
                        }
                    }
                });
                builderSingle.show();
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
                androidx.appcompat.app.AlertDialog.Builder builderSingle = new androidx.appcompat.app.AlertDialog.Builder(ActivitySalesByProduct.this);
                builderSingle.setIcon(R.drawable.logo);
                builderSingle.setTitle("Select territory");
                arrayAdapter1 = new ArrayAdapter<String>(ActivitySalesByProduct.this, R.layout.custom_list_item,territoryList);

                builderSingle.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                builderSingle.setAdapter(arrayAdapter1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        territoryName = arrayAdapter1.getItem(which);
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
                new DatePickerDialog(ActivitySalesByProduct.this, fromdate, myCalendar
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
                    CustomUtility.showWarning(ActivitySalesByProduct.this,"Select correct date","Failed");
                }
                else{
                    updateToDate();

                    networkAvailable = CustomUtility.haveNetworkConnection(ActivitySalesByProduct.this);
                    if (networkAvailable) getDetails();
                    else CustomUtility.showWarning(ActivitySalesByProduct.this,"Please turn on internet connection","No inernet");
                }

            }

        };

        toBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(fromDate.equals(""))
                {
                    CustomUtility.showWarning(ActivitySalesByProduct.this,"Select from date first","Failed");
                }
                else
                {
                    new DatePickerDialog(ActivitySalesByProduct.this, todate, myCalendar2
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
    private void resetRetailSelect()
    {
        retailId = "";
        retailName = "";
        retailBtn.setText("Select retail");
    }

    // for getting all retail list
    private void getRetailList() {


        progressDialog = new SweetAlertDialog(ActivitySalesByProduct.this, SweetAlertDialog.PROGRESS_TYPE);
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
                                CustomUtility.showError(ActivitySalesByProduct.this,message,"Failed");
                            }

                        } catch (JSONException e) {
                            CustomUtility.showError(ActivitySalesByProduct.this, "Failed to get data", "Failed");
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                pDialog = new SweetAlertDialog(ActivitySalesByProduct.this, SweetAlertDialog.ERROR_TYPE);
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

        MySingleton.getInstance(ActivitySalesByProduct.this).addToRequestQue(stringRequest);

    }

    // for getting all territories list
    private void getTerrirotyList() {


        progressDialog = new SweetAlertDialog(ActivitySalesByProduct.this, SweetAlertDialog.PROGRESS_TYPE);
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
                                getDetails();
                            }
                            else
                            {
                                CustomUtility.showError(ActivitySalesByProduct.this,message,"Failed");
                            }

                        } catch (JSONException e) {
                            CustomUtility.showError(ActivitySalesByProduct.this, "Failed to get data", "Failed");
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                pDialog = new SweetAlertDialog(ActivitySalesByProduct.this, SweetAlertDialog.ERROR_TYPE);
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

        MySingleton.getInstance(ActivitySalesByProduct.this).addToRequestQue(stringRequest);

    }



    public void getDetails() {

        dataList.clear();


        sweetAlertDialog = new SweetAlertDialog(ActivitySalesByProduct.this, SweetAlertDialog.PROGRESS_TYPE);
        sweetAlertDialog.setTitleText("Loading");
        sweetAlertDialog.show();
        String upLoadServerUri = "https://sec.imslpro.com/api/android/get_sales_group_summary.php";
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
                            SalesByProductDataModel salesByProductDataModel;
                            Integer trgtQuantity=0, saleQuantity=0, trgtAmount=0, saleAmount=0;
                            if (code.equals("true")) {
                                int sum = 0;
                                int total = 0;
                                jsonArray = jsonObject.getJSONArray("saleResult");
                                for (int i = 0; i< jsonArray.length(); i++)
                                {
                                    jo = jsonArray.getJSONObject(i);
                                    trgtQuantity += Integer.parseInt(jo.getString("target_quantity"));
                                    saleQuantity += Integer.parseInt(jo.getString("sale_quantity"));
                                    trgtAmount += Integer.parseInt(jo.getString("target_amount"));
                                    saleAmount += Integer.parseInt(jo.getString("sale_amount"));
                                    salesByProductDataModel = new SalesByProductDataModel(jo.getString("name"), jo.getString("unit_price"),
                                            jo.getString("target_quantity"),jo.getString("sale_quantity"),jo.getString("target_amount"),jo.getString("sale_amount"));
                                    dataList.add(salesByProductDataModel);
                                }
                                salesByProductDataModel = new SalesByProductDataModel("Total("+String.valueOf(dataList.size())+")","",String.valueOf(trgtQuantity),
                                        String.valueOf(saleQuantity),String.valueOf(trgtAmount),String.valueOf(saleAmount));
                                dataList.add(salesByProductDataModel);
                                mAdapter.notifyDataSetChanged();

                            }
                            else{
                                Log.e("mess",message);
                                CustomUtility.showError(ActivitySalesByProduct.this,message,"Failed");
                                dataList.clear();
                                mAdapter.notifyDataSetChanged();
                                return;
                            }
                        } catch (JSONException e) {
                            CustomUtility.showError(ActivitySalesByProduct.this, e.getMessage(), "Getting Response");
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                sweetAlertDialog.dismiss();
                Log.e("res",error.toString());
                CustomUtility.showError(ActivitySalesByProduct.this, "Network Error, try again!", "Failed");
                dataList.clear();
                mAdapter.notifyDataSetChanged();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("UserId",sharedPreferences.getString("id",null));
                params.put("RetailId",retailId);
                params.put("TerritoryId",territoryId);
                params.put("GroupStyle","product");
                params.put("DateStart",fromDate);
                params.put("DateEnd",toDate);
                return params;
            }
        };

        MySingleton.getInstance(ActivitySalesByProduct.this).addToRequestQue(stringRequest);

    }


    // data adapter class for showing the list
    public class DataAdapter extends RecyclerView.Adapter<DataAdapter.MyViewHolder> {

        private List<SalesByProductDataModel> dataList;

        public DataAdapter(List<SalesByProductDataModel> dataList) {
            this.dataList = dataList;
        }

        @NonNull
        @Override
        public DataAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.sales_by_products_row_layout, parent, false);
            return new DataAdapter.MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull DataAdapter.MyViewHolder holder, int position) {
            final SalesByProductDataModel data = dataList.get(position);
            holder.name.setText(data.getName());
            holder.price.setText(data.getPrice());
            holder.tgtVolume.setText(data.getTgtVolume());
            holder.achVolume.setText(data.getAchVolume());
            holder.tgtValue.setText(data.getTgtValue());
            holder.achValue.setText(data.getAchValue());


            if(Long.parseLong(data.getTgtVolume()) > 0)
            {
                float perc = (float) (Float.parseFloat(data.getAchVolume())/Float.parseFloat(data.getTgtVolume())*100.0);
                holder.achVolPerc.setText(df.format(perc)+"%");
                Log.e("ach vol per",String.valueOf(perc));
                holder.achVolPerc.getBackground().setLevel((int) (perc*100));
            }
            else
            {
                holder.achVolPerc.setText("0%");
            }
            if(Long.parseLong(data.getTgtValue()) > 0)
            {
                float perc = (float) (Float.parseFloat(data.getAchValue())/Float.parseFloat(data.getTgtValue())*100.0);
                holder.achValPerc.setText(df.format(perc)+"%");
                holder.achValPerc.getBackground().setLevel((int) (perc*100));
            }
            else
            {
                holder.achValPerc.setText("0%");
            }

            if(Integer.parseInt(data.getAchVolume())<=0)
            {
                holder.rowLayout.setBackgroundResource(R.color.light_red);
            }
            else  if((position == dataList.size()-1))
            {
                holder.rowLayout.setBackgroundResource(R.color.white);
            }
            else
            {
                if(position%2 == 0)
                {
                    holder.rowLayout.setBackgroundResource(R.color.even);
                }
                else
                {
                    holder.rowLayout.setBackgroundResource(R.color.odd);
                }
            }
        }

        @Override
        public int getItemCount() {
            return dataList.size();
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {
            TextView name, price, tgtVolume, achVolume, tgtValue, achValue,achVolPerc,achValPerc;
            ConstraintLayout rowLayout;
            public MyViewHolder(View convertView) {
                super(convertView);
                rowLayout = convertView.findViewById(R.id.rowLayout);
                name =  convertView.findViewById(R.id.product);
                price =  convertView.findViewById(R.id.price);
                tgtVolume =  convertView.findViewById(R.id.tgtVolume);
                achVolume = convertView.findViewById(R.id.achVolume);
                tgtValue = convertView.findViewById(R.id.tgtValue);
                achValue = convertView.findViewById(R.id.achValue);
                achVolPerc = convertView.findViewById(R.id.achVolPercentage);
                achValPerc = convertView.findViewById(R.id.achValPercentage);
            }
        }
    }

}
