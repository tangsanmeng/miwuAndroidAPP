package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class ProductionTeamActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_production_team);

        initView();
    }

    private void initView() {
        getSupportActionBar().hide();
        LoginExitActivity.addActivity(this);
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        LoginExitActivity.removeActivity(this);
    }
}