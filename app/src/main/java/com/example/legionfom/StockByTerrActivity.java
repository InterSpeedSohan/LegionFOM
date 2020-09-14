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
import com.example.legionfom.dataModel.StockShowByRTDataModel;
import com.example.legionfom.dataModel.StockShowByTerrDataModel;
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

public class StockByTerrActivity extends AppCompatActivity {

    ImageButton homeBtn;
    Button productBtn, delBtn;
    RecyclerView recyclerView;
    DataAdapter mAdapter;
    private List<StockShowByTerrDataModel> dataList = new ArrayList<>();



    SweetAlertDialog sweetAlertDialog,progressDialog,pDialog;
    JSONObject jsonObject;
    JSONArray jsonArray;
    JSONObject jo;

    boolean networkAvailable = false;

    String message = "", code = "";
    String modelName = "", modelId = "";
    ArrayAdapter<String> arrayAdapter;
    ArrayList modelList = new ArrayList<String>();
    Map<Integer,String> modelIdMap = new HashMap<>();
    String  m, mid;

    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_by_territory);
        sharedPreferences = getSharedPreferences("fom_user",MODE_PRIVATE);

        homeBtn = findViewById(R.id.homeBtn);
        productBtn = findViewById(R.id.productBtn);
        delBtn = findViewById(R.id.delBtn);

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

        getProductList();

        delBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                modelId = "";
                modelName = "";
                productBtn.setText("Select model");
                getDetails();
            }
        });

        productBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builderSingle = new AlertDialog.Builder(StockByTerrActivity.this);
                builderSingle.setIcon(R.drawable.logo);
                builderSingle.setTitle("Select model");
                arrayAdapter = new ArrayAdapter<String>(StockByTerrActivity.this, R.layout.custom_list_item,modelList);

                builderSingle.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        modelName = arrayAdapter.getItem(which);
                        modelId = modelIdMap.get(which);
                        productBtn.setText(modelName);
                        getDetails();
                    }
                });
                builderSingle.show();
            }
        });
    }

    // for getting all product list
    private void getProductList() {


        progressDialog = new SweetAlertDialog(StockByTerrActivity.this, SweetAlertDialog.PROGRESS_TYPE);
        progressDialog.setTitle("Please wait...");
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
                                CustomUtility.showError(StockByTerrActivity.this,message,"Failed");
                            }

                        } catch (JSONException e) {
                            CustomUtility.showError(StockByTerrActivity.this, "Failed to get data", "Failed");
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                pDialog = new SweetAlertDialog(StockByTerrActivity.this, SweetAlertDialog.ERROR_TYPE);
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

        MySingleton.getInstance(StockByTerrActivity.this).addToRequestQue(stringRequest);

    }

    public void getDetails()
    {
        dataList.clear();

        sweetAlertDialog = new SweetAlertDialog(StockByTerrActivity.this, SweetAlertDialog.PROGRESS_TYPE);
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
                                    StockShowByTerrDataModel stockShowByTerrDataModel = new StockShowByTerrDataModel(jo.getString("name"),
                                            jo.getString("total_quantity"),jo.getString("total_amount"));
                                    dataList.add(stockShowByTerrDataModel);
                                    mAdapter.notifyDataSetChanged();
                                }

                            }
                            else{
                                Log.e("mess",message);
                                CustomUtility.showError(StockByTerrActivity.this,message,"Failed");
                                dataList.clear();
                                mAdapter.notifyDataSetChanged();
                                return;
                            }
                        } catch (JSONException e) {
                            CustomUtility.showError(StockByTerrActivity.this, e.getMessage(), "Getting Response");
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                sweetAlertDialog.dismiss();
                Log.e("res",error.toString());
                CustomUtility.showError(StockByTerrActivity.this, "Network Error, try again!", "Failed");
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("UserId",sharedPreferences.getString("id",null));
                params.put("ProductId",modelId);
                params.put("GroupStyle","territory");
                return params;
            }
        };

        MySingleton.getInstance(StockByTerrActivity.this).addToRequestQue(stringRequest);


    }



    // data adapter class for showing the list
    public class DataAdapter extends RecyclerView.Adapter<DataAdapter.MyViewHolder> {

        private List<StockShowByTerrDataModel> dataList;

        public DataAdapter(List<StockShowByTerrDataModel> dataList) {
            this.dataList = dataList;
        }

        @NonNull
        @Override
        public DataAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.stock_showing_by_territory_row, parent, false);
            return new DataAdapter.MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull DataAdapter.MyViewHolder holder, int position) {
            final StockShowByTerrDataModel data = dataList.get(position);

            holder.territory.setText(data.getTerritory());
            holder.volume.setText(data.getVolume());
            holder.value.setText(data.getValue());

        }

        @Override
        public int getItemCount() {
            return dataList.size();
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {
            TextView territory, volume, value;

            public MyViewHolder(View convertView) {
                super(convertView);

                territory =  convertView.findViewById(R.id.territory);
                volume = convertView.findViewById(R.id.volume);
                value = convertView.findViewById(R.id.value);

            }
        }
    }


}
