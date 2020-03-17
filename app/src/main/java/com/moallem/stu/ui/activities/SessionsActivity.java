package com.moallem.stu.ui.activities;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import com.moallem.stu.R;
import com.moallem.stu.ui.fragments.SessionsFragment;
import com.moallem.stu.ui.fragments.SessionsSubjectsFragment;

public class SessionsActivity extends AppCompatActivity {

    boolean goToSessions = false;

    private static final String TAG = "SessionsActivityclass";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sessions);

        String subjectkey = getIntent().getExtras().getString("subjectkey");
        String isGoToSession = getIntent().getExtras().getString("gotoSession");
        if (isGoToSession != null){
            goToSessions = Boolean.valueOf(isGoToSession);
        }


        if (savedInstanceState == null){

            if (subjectkey == null && !goToSessions) {
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.fsessios,new SessionsSubjectsFragment()).commit();
            }else {
                SessionsFragment sessionsFragment = new SessionsFragment();
                Bundle bundle = new Bundle();
                bundle.putString("subjectKey",subjectkey);
                bundle.putBoolean("isTeacherHome",true);
                sessionsFragment.setArguments(bundle);

                getSupportFragmentManager().beginTransaction()
                        .add(R.id.fsessios,sessionsFragment).commit();
            }
        }
    }
}
