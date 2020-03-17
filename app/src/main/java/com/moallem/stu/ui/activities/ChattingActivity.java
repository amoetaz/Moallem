package com.moallem.stu.ui.activities;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import com.moallem.stu.R;
import com.moallem.stu.ui.fragments.ChattingFragment;

public class ChattingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatting);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().add(R.id.fchat, new ChattingFragment())
                    .commit();
        }

    }


}
