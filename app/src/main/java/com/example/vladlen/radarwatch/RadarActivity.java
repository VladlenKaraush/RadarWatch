package com.example.vladlen.radarwatch;

import android.graphics.Color;
import android.os.Build;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextClock;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class RadarActivity extends AppCompatActivity {

    RadarView radarView = null;
    SettingsView settingsView = null;
    boolean isSettingsView = false, isDigitalClock = false;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setupRadarView();
    }

    private void setupRadarView(){
        setContentView(R.layout.activity_radar);
        ConstraintLayout constraintLayout = findViewById(R.id.view);
        constraintLayout.setBackgroundColor(RadarView.getBackgroundColor());

        Toolbar tb = findViewById(R.id.te_radar);
        tb.setBackgroundColor(RadarView.getToolbarColor());
    }



    public void settingsButtonClick(View view) {

        setContentView(R.layout.settings);

        Toolbar tb = findViewById(R.id.te_settings);
        tb.setBackgroundColor(RadarView.getToolbarColor());
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
                Toolbar tb = findViewById(R.id.te_settings);
                tb.setBackgroundColor(RadarView.getToolbarColor());

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

            Toolbar tb = findViewById(R.id.te_clock);
            tb.setBackgroundColor(RadarView.getToolbarColor());

            //colors
            ConstraintLayout constraintLayout = findViewById(R.id.clock_view);
            constraintLayout.setBackgroundColor(RadarView.getBackgroundColor());

//            TextView textClock = findViewById(R.id.text_clock);
//
//            Date date = new Date();
//            String strDateFormat = "hh:mm:ss";
//            DateFormat dateFormat = new SimpleDateFormat(strDateFormat);
//            String formattedDate= dateFormat.format(date);
//            System.out.println("Current time of the day using Date - 12 hour format: " + formattedDate);
//            textClock.setText(formattedDate);

            TextClock clock = findViewById(R.id.simpleDigitalClock);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                clock.setFormat12Hour("hh:mm:ss");
                clock.setFormat24Hour("hh:mm:ss");
            }
            clock.setTextColor(RadarView.getArcColor());
        }
        else{
            setupRadarView();
        }
    }

}
