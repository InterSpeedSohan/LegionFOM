package com.example.legionfom;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class SalesPanel extends AppCompatActivity {

    ImageButton homeBtn;

    Button dailySalesBtn, saleByRTBtn, saleByPrBtn;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sales);

        homeBtn = findViewById(R.id.homeBtn);
        dailySalesBtn = findViewById(R.id.dailySalesBtn);
        saleByRTBtn = findViewById(R.id.salesByRetailersBtn);
        saleByPrBtn = findViewById(R.id.salesByProductBtn);

        dailySalesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SalesPanel.this,ActivityDailySales.class));
            }
        });
/*
        saleByRTBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SalesPanel.this, ActivitySalesByRT.class));
            }
        });

        saleByPrBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SalesPanel.this,ActivitySalesByProduct.class));
            }
        });

        homeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

 */
    }
}
