package com.dqchen.eventbusdemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.dqchen.eventbusdemo.core.DnEventBus;

import org.greenrobot.eventbus.EventBus;

public class SeconedActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seconed);
    }

//    public void send(View view) {
//        EventBus.getDefault().post(new Friend("alan",18));
//    }

    public void send(View view) {
        DnEventBus.getDefault().post(new Friend("alan", 18));
    }
}
