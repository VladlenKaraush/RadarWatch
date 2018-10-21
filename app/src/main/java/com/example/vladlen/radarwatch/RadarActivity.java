package com.example.vladlen.radarwatch;

import android.graphics.Color;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

public class RadarActivity extends AppCompatActivity {

    RadarView radarView = null;
    SettingsView settingsView = null;
    boolean isSettingsView = false;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupRadarView();
    }

    private void setupRadarView(){
        setContentView(R.layout.activity_radar);
        ConstraintLayout constraintLayout = findViewById(R.id.view);
        //radarView = findViewById(R.id.radarView);

        constraintLayout.setBackgroundColor(Color.rgb(80,40, 40));
    }



    public void settingsButtonClick(View view) {

        setContentView(R.layout.settings);
        isSettingsView = true;

        Spinner spinner = findViewById(R.id.spinner2);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.color_schemes, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);

    }

    @Override
    public void onBackPressed(){
        if(isSettingsView){
            isSettingsView = false;
            setupRadarView();
            return;
        }

        super.onBackPressed();
    }
}
