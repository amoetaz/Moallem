package com.moallem.stu;

import android.app.Application;
import android.content.Context;
import androidx.multidex.MultiDex;

import com.firebase.client.Firebase;

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Firebase.setAndroidContext(getApplicationContext());
    }


    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

}
