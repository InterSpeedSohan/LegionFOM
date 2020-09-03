package com.example.legionfom;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class StockPanelActivity extends AppCompatActivity {

    ImageButton homeBtn;

    Button stockByRtBtn, stockByPrdctBtn;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.stock_panel_activity);

        homeBtn = findViewById(R.id.homeBtn);
        stockByPrdctBtn = findViewById(R.id.stockByProduct);
        stockByRtBtn = findViewById(R.id.stockByRT);

        homeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
/*
        stockByRtBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(StockPanelActivity.this, AcivityStockByRT.class));
            }
        });

        stockByPrdctBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(StockPanelActivity.this,StockByProductActivity.class));
            }
        });

 */
    }
}
