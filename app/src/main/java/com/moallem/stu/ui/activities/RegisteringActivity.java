package com.moallem.stu.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;

import com.moallem.stu.R;
import com.moallem.stu.ui.fragments.LoginFragment;
import com.moallem.stu.ui.fragments.OptionFragment;

public class RegisteringActivity extends AppCompatActivity {

    private static final String TAG = "RegisteringAityclass";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_registering);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().add(R.id.fregister, new OptionFragment())
                    .commit();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        for (Fragment fragment : getSupportFragmentManager().getFragments()) {
            if (fragment instanceof LoginFragment) {
                fragment.onActivityResult(requestCode, resultCode, data);
            }
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        try {
            return super.dispatchTouchEvent(event);
        }
        catch (Exception ignored){
            return true;
        }
    }


}
