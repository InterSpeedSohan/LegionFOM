package com.example.legionfom;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class StatePanelActivity extends AppCompatActivity {

    Button foeDailyAttBtn, attendanceByFoeBtn, secDailyAttBtn, attendanceBySecBtn, myAttendanceSumBtn, myDailyAttendanceBtn, videoBtn;

    ImageButton homeBtn;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_states);

        homeBtn = findViewById(R.id.homeBtn);


        foeDailyAttBtn = findViewById(R.id.foeDailyAttenndanceBtn);
        attendanceByFoeBtn = findViewById(R.id.attendanceByFoeBtn);
        secDailyAttBtn = findViewById(R.id.secDailyAttenndanceBtn);
        attendanceBySecBtn = findViewById(R.id.attendanceBySecBtn);
        myAttendanceSumBtn = findViewById(R.id.attendanceSummaryBtn);
        myDailyAttendanceBtn = findViewById(R.id.myDailyAttendanceBtn);
        videoBtn = findViewById(R.id.videoBtn);


        homeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        foeDailyAttBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(StatePanelActivity.this, ActivityFoeDailyAttendance.class));
            }
        });
        attendanceByFoeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(StatePanelActivity.this,ActivityAttendanceByFoe.class));
            }
        });

        secDailyAttBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(StatePanelActivity.this, ActivitySecDailyAttendance.class));
            }
        });

        attendanceBySecBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(StatePanelActivity.this,ActivityAttendanceBySec.class));
            }
        });

        myAttendanceSumBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(StatePanelActivity.this,MyAttendanceSummaryActivity.class));

            }
        });

        myDailyAttendanceBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(StatePanelActivity.this,MyDailyAttendance.class));

            }
        });
/*
        myDailyRTvisitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(StatePanelActivity.this,MyDailyRTvisitActivity.class));
            }
        });

        myRTvisitByRTBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(StatePanelActivity.this, VisitByRetailerActivity.class));
            }
        });


 */

    }
}
