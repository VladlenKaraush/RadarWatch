package com.example.vladlen.radarwatch;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private ConstraintLayout constraintLayout;
    private TextView departmentName;
    private TextView universityName;
    private AppIntro app;
    private ViewIntro  m_viewIntro;
    private int currView = -1;
    public static final int	VIEW_INTRO		= 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);


        constraintLayout = findViewById(R.id.view);
        departmentName = findViewById(R.id.departmentName);
        universityName = findViewById(R.id.universityName);
        constraintLayout.setBackgroundColor(Color.rgb(0,0, 0));

        //disable button if not connected to internet
        Button button = findViewById(R.id.onTheSite);
        if(!isNetworkAvailable()) {
            button.setBackgroundColor(Color.GRAY);
            button.setEnabled(false);
        }

//        app = new AppIntro(this, 0);
//        // Create view
//        setView(VIEW_INTRO);

    }

    public AppIntro getApp(){
        return app;
    }

    public void onTheSiteButtonClicked(View v) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://amd.spbstu.ru"));
        startActivity(intent);
    }

    public void startClicked(View v) {
        Intent intent = new Intent(this, RadarActivity.class);
        startActivity(intent);
        System.out.println("ok");
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public void setView(int viewID)
    {
        if (currView == viewID)
        {
            return;
        }
        currView = viewID;
        if (currView == VIEW_INTRO)
        {
            m_viewIntro = new ViewIntro(this);
            setContentView(m_viewIntro);
        }
    }
}