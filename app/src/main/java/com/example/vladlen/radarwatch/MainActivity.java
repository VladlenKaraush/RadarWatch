package com.example.vladlen.radarwatch;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Point;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import java.util.Locale;

public class MainActivity extends AppCompatActivity implements View.OnTouchListener {

    private ConstraintLayout constraintLayout;
    private TextView departmentName;
    private TextView universityName;
    private AppIntro app;

    private int currView = -1;

    public static final int	VIEW_INTRO		= 0;
    public static final int MODE_SOURCE_SHAPE	= 0;

    // *************************************************
    // DATA
    // *************************************************
    int						    m_viewCur = -1;
    int               m_modeCur = -1;

    private AppIntro   m_appIntro;
    private ViewIntro  m_viewIntro;

    // screen dim
    private int        m_screenW;
    private int        m_screenH;
    private String  m_log = "KP2D";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final Window win = getWindow();
        win.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        // Application is never sleeps
        win.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.activity_main);

        constraintLayout = findViewById(R.id.main_view);
        departmentName = findViewById(R.id.departmentName);
        universityName = findViewById(R.id.universityName);
        constraintLayout.setBackgroundColor(Color.rgb(0,0, 0));

        //disable button if not connected to internet
        Button button = findViewById(R.id.onTheSite);
        if(!isNetworkAvailable()) {
            button.setBackgroundColor(Color.GRAY);
            button.setEnabled(false);
        }

        //super.onCreate(savedInstanceState);
        //overridePendingTransition(0, 0);
        // No Status bar


        Display display = getWindowManager().getDefaultDisplay();
        Point point = new Point();
        display.getSize(point);
        m_screenW = point.x;
        m_screenH = point.y;


        // Detect language
        String strLang = Locale.getDefault().getDisplayLanguage();
        int language;
        if (strLang.equalsIgnoreCase("english"))
        {
            language = AppIntro.LANGUAGE_ENG;
        }
        else if (strLang.equalsIgnoreCase("русский"))
        {
            language = AppIntro.LANGUAGE_RUS;
        }
        else
        {
            language = AppIntro.LANGUAGE_UNKNOWN;
        }
        // Create application intro
        m_appIntro = new AppIntro(this, language);
        // Create view
        setView(VIEW_INTRO);
    }

//    public AppIntro getApp(){
//        return app;
//    }
//
//    public void onTheSiteButtonClicked(View v) {
//        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://amd.spbstu.ru"));
//        startActivity(intent);
//    }
//
//    public void startClicked(View v) {
//        Intent intent = new Intent(this, RadarActivity.class);
//        startActivity(intent);
//        System.out.println("ok");
//    }
//
//    public AppIntro getAppIntro()
//    {
//        return m_appIntro;
//    }
//
    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null;
//        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
//
//    public void setView(int viewID)
//    {
//        if (currView == viewID)
//        {
//            return;
//        }
//        currView = viewID;
//        if (currView == VIEW_INTRO)
//        {
//            m_viewIntro = new ViewIntro(this);
//            setContentView(m_viewIntro);
//        }
//    }
//
//
//    public boolean onTouch(View v, MotionEvent evt)
//    {
//        int x = (int)evt.getX();
//        int y = (int)evt.getY();
//        int touchType = AppIntro.TOUCH_DOWN;
//
//        if (evt.getAction() == MotionEvent.ACTION_MOVE)
//            touchType = AppIntro.TOUCH_MOVE;
//        if (evt.getAction() == MotionEvent.ACTION_UP)
//            touchType = AppIntro.TOUCH_UP;
//
//        if (m_viewCur == VIEW_INTRO) {
//            //setContentView(R.layout.activity_main);
//            return m_viewIntro.onTouch(x, y, touchType);
//        }
//        return true;
//    }
//    public boolean onKeyDown(int keyCode, KeyEvent evt)
//    {
//        if (keyCode == KeyEvent.KEYCODE_BACK)
//        {
//        }
//        boolean ret = super.onKeyDown(keyCode, evt);
//        return ret;
//    }
//
//    public void setMode(int mode)
//    {
//        m_modeCur = mode;
//    }

    //end on my code



    public AppIntro getAppIntro()
    {
        return m_appIntro;
    }
    public int getScreenWidth() { return m_screenW; }
    public int getScreenHeight() { return m_screenH; }

    public void setMode(int mode)
    {
        m_modeCur = mode;
    }

    public int getView()
    {
        return m_viewCur;
    }
    public void setView(int viewID)
    {
        if (m_viewCur == viewID)
        {
            Log.d(m_log, "setView: already set");
            return;
        }

        m_viewCur = viewID;
        if (m_viewCur == VIEW_INTRO)
        {
            m_viewIntro = new ViewIntro(this);
            setContentView(m_viewIntro);
        }


    }

    protected void onPostCreate(Bundle savedInstanceState)
    {
        super.onPostCreate(savedInstanceState);

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.

        // delayedHide(100);
    }
    public void onCompletion(MediaPlayer mp)
    {
        Log.d(m_log, "onCompletion: Video play is completed");
    }


    public boolean onTouch(View v, MotionEvent evt)
    {
        int x = (int)evt.getX();
        int y = (int)evt.getY();
        int touchType = AppIntro.TOUCH_DOWN;

        if (evt.getAction() == MotionEvent.ACTION_MOVE)
            touchType = AppIntro.TOUCH_MOVE;
        if (evt.getAction() == MotionEvent.ACTION_UP)
            touchType = AppIntro.TOUCH_UP;

        if (m_viewCur == VIEW_INTRO) {
            //setContentView(R.layout.activity_main);
            return m_viewIntro.onTouch(x, y, touchType);
        }
        return true;
    }
    public boolean onKeyDown(int keyCode, KeyEvent evt)
    {
        if (keyCode == KeyEvent.KEYCODE_BACK)
        {
        }
        boolean ret = super.onKeyDown(keyCode, evt);
        return ret;
    }

    protected void onResume()
    {
        super.onResume();
        if (m_viewCur == VIEW_INTRO)
            m_viewIntro.start();

    }
    protected void onPause()
    {
        // stop anims
        if (m_viewCur == VIEW_INTRO)
            m_viewIntro.stop();
//    if (m_viewCur == VIEW_MENU)
//      m_viewMenu.stop();

        // complete system
        super.onPause();
        //Log.d(m_log, "App onPause");
    }
    protected void onDestroy()
    {

        super.onDestroy();
        //Log.d("DCT", "App onDestroy");
    }
    public void onConfigurationChanged(Configuration confNew)
    {
        super.onConfigurationChanged(confNew);
        m_viewIntro.onConfigurationChanged(confNew);
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
}