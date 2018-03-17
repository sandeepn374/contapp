package com.voiceitbase.audiorecorder;

/**
 * Created by kshravi on 13/03/2018 AD.
 */
import android.app.Activity;
import android.os.Bundle;
import android.view.View;

public class NotificationActivity extends Activity implements View.OnClickListener {
    NotificationHandler nHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        nHandler = NotificationHandler.getInstance(this);
        initUI();
    }


    private void initUI () {
        setContentView(R.layout.activity_main_notification);

        findViewById(R.id.progress_notification).setOnClickListener(this);

       }


    @Override
    public void onClick (View v) {
        switch (v.getId()) {


            case R.id.progress_notification:
                nHandler.createProgressNotification(this);
                break;



        }

    }
}
