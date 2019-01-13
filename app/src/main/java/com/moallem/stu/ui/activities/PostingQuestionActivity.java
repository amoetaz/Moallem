package com.moallem.stu.ui.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.moallem.stu.R;
import com.moallem.stu.ui.fragments.TakingPhotoFragment;

public class PostingQuestionActivity extends AppCompatActivity {

    private String currentFragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_posting_question);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().add(R.id.fpostquestion, new TakingPhotoFragment())
                    .commit();
        }
    }


}
