package com.moallem.stu.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.moallem.stu.R;
import com.moallem.stu.utilities.Utils;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        Utils.hideAndroidUI(this);
        Thread timer = new Thread() {
            public void run() {

                try {
                    sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    startActivity(new Intent(getApplication(), RegisteringActivity.class));
                    finish();
                }
            }

        };
        timer.start();
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
    }
}
