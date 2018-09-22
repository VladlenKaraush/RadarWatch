package com.example.vladlen.radarwatch;

import android.graphics.Color;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class RadarActivity extends AppCompatActivity {

    RadarView radarView = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_radar);
        ConstraintLayout constraintLayout = findViewById(R.id.view);
        radarView = findViewById(R.id.radarView);

        constraintLayout.setBackgroundColor(Color.rgb(80,40, 40));
    }
}
