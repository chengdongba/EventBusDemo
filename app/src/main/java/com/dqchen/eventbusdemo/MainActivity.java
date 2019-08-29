package com.dqchen.eventbusdemo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.dqchen.eventbusdemo.core.DnEventBus;
import com.dqchen.eventbusdemo.core.DnSubscriber;
import com.dqchen.eventbusdemo.core.DnThreadMode;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class MainActivity extends AppCompatActivity {

    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView = findViewById(R.id.tv);

//        EventBus.getDefault().register(this);
        DnEventBus.getDefault().register(this);
    }

//    @Subscribe(threadMode = ThreadMode.MAIN)
//    public void receive(Friend friend){
//        Toast.makeText(this, friend.toString(), Toast.LENGTH_SHORT).show();
//    }

    @DnSubscriber(threadMode = DnThreadMode.MAIN)
    public void receive(Friend friend){
        Toast.makeText(this, friend.toString(), Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        EventBus.getDefault().unregister(this);
        EventBus.getDefault().unregister(this);
    }

    public void jump(View view) {
        startActivity(new Intent(this,SeconedActivity.class));
    }
}
