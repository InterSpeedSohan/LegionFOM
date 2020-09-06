package com.example.legionfom;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.legionfom.helper.CustomUtility;

public class ActivityAttendancePanel extends AppCompatActivity {
    Button yourAttendanceBtn, teamAttendanceBtn, leaveBtn, evaluationBtn;

    ImageButton homeBtn;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance_panel);

        homeBtn = findViewById(R.id.homeBtn);

        yourAttendanceBtn = findViewById(R.id.yourAttendanceBtn);
        teamAttendanceBtn = findViewById(R.id.teamAttendanceBtn);
        leaveBtn = findViewById(R.id.leaveAuthorizationBtn);

        homeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ActivityAttendancePanel.this,ActivityDockPanel.class));
                finish();
            }
        });

        yourAttendanceBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!ActivityDockPanel.presentFlag)
                {
                    startActivity(new Intent(ActivityAttendancePanel.this,YourAttendancePanel.class));
                    finish();
                }
                else
                {
                    CustomUtility.showWarning(ActivityAttendancePanel.this,"","You already gave today attendance");
                }
            }
        });
        /*
        teamAttendanceBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ActivityAttendancePanel.this,ActivityTeamAttendance.class));
            }
        });
        evaluationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ActivityAttendancePanel.this, SecEvaluationPanel.class));
            }
        });
        leaveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ActivityAttendancePanel.this,ActivityLeaveAuthorization.class));
            }
        });

 */
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(ActivityAttendancePanel.this,ActivityDockPanel.class));
        finish();
    }
}
