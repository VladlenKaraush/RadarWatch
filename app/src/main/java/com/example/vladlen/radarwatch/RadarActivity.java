package com.example.vladlen.radarwatch;

import android.graphics.Color;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextClock;

public class RadarActivity extends AppCompatActivity {

    RadarView radarView = null;
    SettingsView settingsView = null;
    boolean isSettingsView = false, isDigitalClock = false;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupRadarView();
    }

    private void setupRadarView(){
        setContentView(R.layout.activity_radar);
        ConstraintLayout constraintLayout = findViewById(R.id.view);
        //radarView = findViewById(R.id.radarView);

        constraintLayout.setBackgroundColor(RadarView.getBackgroundColor());
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

        spinner.setSelection(RadarView.getColorSchemeId(), false);

        spinner.post(() -> spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                RadarView.setColorScheme(i);

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        }));

        //spinner.setOnItemSelectedListener();

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

    public void toggleDigitalClock(View view) {

        isDigitalClock = !isDigitalClock;

        if(isDigitalClock){
            setContentView(R.layout.digital_clock);

            //colors
            ConstraintLayout constraintLayout = findViewById(R.id.clock_view);
            constraintLayout.setBackgroundColor(RadarView.getBackgroundColor());

            TextClock clock = findViewById(R.id.simpleDigitalClock);
            clock.setFormat24Hour("hh:mm:ss");
            clock.setTextColor(RadarView.getArcColor());
        }
        else{
            setupRadarView();
        }
    }

}
