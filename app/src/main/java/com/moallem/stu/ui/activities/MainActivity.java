package com.moallem.stu.ui.activities;

import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.holder.BadgeStyle;
import com.mikepenz.materialdrawer.holder.StringHolder;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;
import com.moallem.stu.R;
import com.moallem.stu.adapters.SampleRecyclerViewAdapter;
import com.moallem.stu.data.PrefsHelper;
import com.moallem.stu.models.Subject;
import com.moallem.stu.utilities.Utils;
import com.twitter.sdk.android.core.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterConfig;
import com.twitter.sdk.android.core.TwitterCore;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static com.moallem.stu.utilities.FirebaseConstants.SUBJECTS_NODE;
import static com.moallem.stu.utilities.FirebaseConstants.USERINFO_NODE;

public class MainActivity extends AppCompatActivity  {

    private static final String TAG = "MainActivity";

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    private FirebaseAuth firebaseAuth;
    private GoogleSignInClient mGoogleSignInClient;
    private DatabaseReference mDatabase;
    private SampleRecyclerViewAdapter rcAdapter;
    private ArrayList<Subject> sList = new ArrayList<>();
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    private Unbinder bind;
    private ChildEventListener childEventListener;
    private Drawer drawer;
    private PrimaryDrawerItem item2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        configTwitterSdk();
        setContentView(R.layout.activity_main);
        bind = ButterKnife.bind(this);
        firebaseAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        checkIfUserNotLogin();
        //configereGoogleSignin();

        checkForNewVersion();
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        StaggeredGridLayoutManager staggeredGridLayoutManager =
                new StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(staggeredGridLayoutManager);

        if (Utils.isNetworkConnected(getApplicationContext())) {
            initList();
        }else {
            Toast.makeText(this, "Check internet connection", Toast.LENGTH_SHORT).show();
        }

        configNavigationDrawer();
    }

    private void getCountryName() {
        try {
            TelephonyManager tm = (TelephonyManager)this.getSystemService(this.TELEPHONY_SERVICE);
            if (tm != null) {
                String countryCodeValue = tm.getNetworkCountryIso();
                PrefsHelper.getInstance(this).setCountryCode(countryCodeValue);
            }
        } catch (Exception e) {
        }
    }


    private void openApplink() {
        Intent appintent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + getPackageName()));
        Intent webintent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + getPackageName()));
        try {
            startActivity(appintent);
        } catch (ActivityNotFoundException anfe ) {
            startActivity(webintent);
        }

    }

    private void showConfirmDialogForUpdate(){

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("New version of the app was released ,update now?");

            builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    openApplink();

                }
            });
            builder.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    // User cancelled the dialog
                }
            });

            builder.setNeutralButton("don't show again?", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Toast.makeText(MainActivity.this, ""+which, Toast.LENGTH_SHORT).show();
                    PrefsHelper.getInstance(getApplicationContext()).setShowDialog(false);
                }
            });

            AlertDialog dialog = builder.create();
            dialog.show();

    }

    private void configNavigationDrawer() {
        PrimaryDrawerItem item1 = new PrimaryDrawerItem().withIcon(R.drawable.test_icon)
                .withIdentifier(1).withName("Sessions");

        item2 = new PrimaryDrawerItem().withIcon(R.drawable.ic_minutes).withBadge("0")
                .withBadgeStyle(new BadgeStyle().withTextColor(Color.WHITE).withColorRes(R.color.app_title_color))
                .withIdentifier(2).withName("Minutes");

        PrimaryDrawerItem item3 = new PrimaryDrawerItem().withIcon(R.drawable.ic_signout)
                .withIdentifier(3).withName("Sign out");


        AccountHeader header = new AccountHeaderBuilder()
                .withActivity(this)
                .withHeaderBackground(R.color.app_title_color)
                .addProfiles(
                        new ProfileDrawerItem().withName(firebaseAuth.getCurrentUser().getDisplayName())
                                .withIcon(getResources().getDrawable(R.drawable.ic_launcher))
                )
                .withOnAccountHeaderListener(new AccountHeader.OnAccountHeaderListener() {
                    @Override
                    public boolean onProfileChanged(View view, IProfile profile, boolean currentProfile) {
                        return false;
                    }
                })
                .build();

         drawer = new DrawerBuilder()
                .withActivity(this)
                .withToolbar(toolbar)
                .withAccountHeader(header)
                .addDrawerItems(
                        item1, item2,item3
                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        switch (position){
                            case 1:drawer.closeDrawer();startActivity(new Intent(MainActivity.this
                                    ,SessionsActivity.class)); break;
                            case 2:drawer.closeDrawer();
                                startPaymentActivity(); break;
                            case 3:logout();break;
                            default:break;
                        }
                         return true;
                    }
                })
                 .withDrawerGravity(GravityCompat.START)
                .build();
    }

    private void startPaymentActivity() {
        String countryCode = PrefsHelper.getInstance(this).getCountryCode();
        if (countryCode == null || countryCode.equals("none")) {
            Toast.makeText(this, "Please make sure SIM card is inserted", Toast.LENGTH_SHORT).show();
        }else if (/*fromAllowedCounteries(countryCode)*/true){
            startActivity(new Intent(MainActivity.this
                    ,PaymentActivity.class));
        }else {
            Toast.makeText(this, "this section is not allowed for your country for this version", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean fromAllowedCounteries(String countryCode) {
        String country = countryCode.toLowerCase();
        return country.equals("eg");
    }

    private void getStudentBalance() {
        mDatabase.child(USERINFO_NODE).child(Utils.getCurrentUserId())
                .child("timeBalance").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    Double balance = dataSnapshot.getValue(Double.class);
                    if (balance != null){
                        drawer.updateBadge(2,new StringHolder((balance/60)+""));
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void checkForNewVersion(){
        mDatabase.child("versionsUpdateNotify").child("studentApp")
                .child("isVersion1-0Updated").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    Boolean isUpdated = dataSnapshot.getValue(Boolean.class);
                    if (isUpdated != null && isUpdated){
                        if (PrefsHelper.getInstance(getApplicationContext()).getShowDialog()){
                            if (PrefsHelper.getInstance(getApplicationContext()).getCounterToshowDialog() == 10){
                                showConfirmDialogForUpdate();
                                PrefsHelper.getInstance(getApplicationContext()).resetCounterToshowDialog();
                            }else {
                                PrefsHelper.getInstance(getApplicationContext()).incremntCounterToshowDialog();
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void checkIfUserNotLogin() {
        if (firebaseAuth.getCurrentUser() == null){
            startActivity(new Intent(this, RegisteringActivity.class));
            finish();
        }
    }

    private void initList() {

        rcAdapter = new SampleRecyclerViewAdapter(getApplicationContext(),sList);
        recyclerView.setAdapter(rcAdapter);

        childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, String s) {
                String subjectName = dataSnapshot.child("name").getValue(String.class);
                String subjectIconUrl = dataSnapshot.child("subIcon").getValue(String.class);
                String key = dataSnapshot.getKey();
                String arabicName = dataSnapshot.child("arabicName").getValue(String.class);
                Subject subject = new Subject();

                subject.setArabicName(arabicName);
                subject.setName(subjectName);
                subject.setImage(subjectIconUrl);
                subject.setKey(key);
                sList.add(subject);
                rcAdapter.notifyItemInserted(sList.size() - 1);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        mDatabase.child(SUBJECTS_NODE)
                .addChildEventListener(childEventListener);
    }

    private void configereGoogleSignin(){
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);


    }

    @Override
    public void onBackPressed() {
            if (drawer.isDrawerOpen()){
             drawer.closeDrawer();
            }else {
                super.onBackPressed();
            }
    }



    private void logout(){

        firebaseAuth.signOut();
       // mGoogleSignInClient.signOut();
        LoginManager.getInstance().logOut();
        TwitterCore.getInstance().getSessionManager().clearActiveSession();

        startActivity(new Intent(MainActivity.this, RegisteringActivity.class));
        finish();
    }


    private void configTwitterSdk() {

        // Configure Twitter SDK
        TwitterAuthConfig authConfig = new TwitterAuthConfig(
                getString(R.string.twitter_consumer_key),
                getString(R.string.twitter_consumer_secret));

        TwitterConfig twitterConfig = new TwitterConfig.Builder(this)
                .twitterAuthConfig(authConfig)
                .build();

        Twitter.initialize(twitterConfig);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        bind.unbind();
        if (childEventListener != null) {
            mDatabase.child(SUBJECTS_NODE)
                    .removeEventListener(childEventListener);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        getStudentBalance();
        getCountryName();
    }

}
