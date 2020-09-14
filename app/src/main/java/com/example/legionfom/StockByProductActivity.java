package com.example.legionfom;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
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
import com.example.legionfom.dataModel.StockShowingByProDataModel;
import com.example.legionfom.helper.CustomUtility;
import com.example.legionfom.helper.MySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;


public class StockByProductActivity extends AppCompatActivity {
    ImageButton homeBtn;
    Button retailBtn, delBtn, territoryBtn, delTerritoryBtn;
    RecyclerView recyclerView;
    DataAdapter mAdapter;
    private List<StockShowingByProDataModel> dataList = new ArrayList<>();



    SweetAlertDialog sweetAlertDialog,progressDialog,pDialog;
    JSONObject jsonObject;
    JSONArray jsonArray;
    JSONObject jo;

    boolean networkAvailable = false;

    String retailName = "",message = "", code = "", retailId = "", territoryName = "", territoryId = "";
    ArrayAdapter<String> arrayAdapter;
    ArrayList retailList = new ArrayList<String>();
    ArrayList territoryList = new ArrayList<String>();
    Map<Integer,String> retailIdMap = new HashMap<>();
    Map<Integer,String> territoryMap = new HashMap<>();
    Map<Integer,String> retailTerritoryMap = new HashMap<>();
    String  r, rid, t, tid;

    SharedPreferences sharedPreferences;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_by_products);

        sharedPreferences = getSharedPreferences("fom_user",MODE_PRIVATE);

        homeBtn = findViewById(R.id.homeBtn);
        retailBtn = findViewById(R.id.retailBtn);
        delBtn = findViewById(R.id.delBtn);

        territoryBtn = findViewById(R.id.territoryBtn);
        delTerritoryBtn = findViewById(R.id.delTerritoryBtn);

        homeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
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

        getRetailList();

        delBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetRetailSelect();
                getDetails();
            }
        });

        retailBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builderSingle = new AlertDialog.Builder(StockByProductActivity.this);
                builderSingle.setIcon(R.drawable.logo);
                builderSingle.setTitle("Select retail");
                arrayAdapter = new ArrayAdapter<String>(StockByProductActivity.this, R.layout.custom_list_item,retailList);

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
                            CustomUtility.showWarning(StockByProductActivity.this,"This retail is not under your selected territory","Selection Error");
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
                AlertDialog.Builder builderSingle = new AlertDialog.Builder(StockByProductActivity.this);
                builderSingle.setIcon(R.drawable.logo);
                builderSingle.setTitle("Select territory");
                arrayAdapter = new ArrayAdapter<String>(StockByProductActivity.this, R.layout.custom_list_item,territoryList);

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

    }



    private void resetRetailSelect()
    {
        retailId = "";
        retailName = "";
        retailBtn.setText("Select retail");
    }

    // for getting all retail list
    private void getRetailList() {


        progressDialog = new SweetAlertDialog(StockByProductActivity.this, SweetAlertDialog.PROGRESS_TYPE);
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
                                CustomUtility.showError(StockByProductActivity.this,message,"Failed");
                            }

                        } catch (JSONException e) {
                            CustomUtility.showError(StockByProductActivity.this, "Failed to get data", "Failed");
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                pDialog = new SweetAlertDialog(StockByProductActivity.this, SweetAlertDialog.ERROR_TYPE);
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

        MySingleton.getInstance(StockByProductActivity.this).addToRequestQue(stringRequest);

    }

    // for getting all territories list
    private void getTerrirotyList() {


        progressDialog = new SweetAlertDialog(StockByProductActivity.this, SweetAlertDialog.PROGRESS_TYPE);
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

                                // getting detail list
                                getDetails();
                            }
                            else
                            {
                                CustomUtility.showError(StockByProductActivity.this,message,"Failed");
                            }

                        } catch (JSONException e) {
                            CustomUtility.showError(StockByProductActivity.this, "Failed to get data", "Failed");
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                pDialog = new SweetAlertDialog(StockByProductActivity.this, SweetAlertDialog.ERROR_TYPE);
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

        MySingleton.getInstance(StockByProductActivity.this).addToRequestQue(stringRequest);

    }


    public void getDetails()
    {
        dataList.clear();

        sweetAlertDialog = new SweetAlertDialog(StockByProductActivity.this, SweetAlertDialog.PROGRESS_TYPE);
        sweetAlertDialog.setTitleText("Loading");
        sweetAlertDialog.show();
        String upLoadServerUri = "https://sec.imslpro.com/api/android/get_stock_group_summary.php";
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
                                jsonArray = jsonObject.getJSONArray("stockResult");
                                for (int i = 0; i< jsonArray.length(); i++)
                                {
                                    jo = jsonArray.getJSONObject(i);
                                    StockShowingByProDataModel stockShowingByProDataModel = new StockShowingByProDataModel(jo.getString("name"),jo.getString("unit_price"),
                                            jo.getString("total_quantity"),jo.getString("total_amount"));
                                    dataList.add(stockShowingByProDataModel);
                                    mAdapter.notifyDataSetChanged();
                                }

                            }
                            else{
                                Log.e("mess",message);
                                CustomUtility.showError(StockByProductActivity.this,message,"Failed");
                                dataList.clear();
                                mAdapter.notifyDataSetChanged();
                                return;
                            }
                        } catch (JSONException e) {
                            CustomUtility.showError(StockByProductActivity.this, e.getMessage(), "Getting Response");
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                sweetAlertDialog.dismiss();
                Log.e("res",error.toString());
                CustomUtility.showError(StockByProductActivity.this, "Network Error, try again!", "Failed");
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("UserId",sharedPreferences.getString("id",null));
                params.put("RetailId",retailId);
                params.put("TerritoryId",territoryId);
                params.put("GroupStyle","product");
                return params;
            }
        };

        MySingleton.getInstance(StockByProductActivity.this).addToRequestQue(stringRequest);
    }



    // data adapter class for showing the list
    public class DataAdapter extends RecyclerView.Adapter<DataAdapter.MyViewHolder> {

        private List<StockShowingByProDataModel> dataList;

        public DataAdapter(List<StockShowingByProDataModel> dataList) {
            this.dataList = dataList;
        }

        @NonNull
        @Override
        public DataAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.stock_showing_by_product_row, parent, false);
            return new DataAdapter.MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull DataAdapter.MyViewHolder holder, int position) {
            final StockShowingByProDataModel data = dataList.get(position);

            holder.product.setText(data.getName());
            holder.price.setText(data.getPrice());
            holder.volume.setText(data.getVolume());
            holder.value.setText(data.getValue());

        }

        @Override
        public int getItemCount() {
            return dataList.size();
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {
            TextView product, price, volume, value;

            public MyViewHolder(View convertView) {
                super(convertView);

                product =  convertView.findViewById(R.id.product);
                price = convertView.findViewById(R.id.price);
                volume = convertView.findViewById(R.id.volume);
                value = convertView.findViewById(R.id.value);

            }
        }
    }
}
