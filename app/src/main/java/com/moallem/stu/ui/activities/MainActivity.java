package com.moallem.stu.ui.activities;

import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.view.GravityCompat;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.appcompat.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
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

import static com.moallem.stu.utilities.FirebaseConstants.ISTHEREUNFINISHEDSESSION;
import static com.moallem.stu.utilities.FirebaseConstants.SUBJECTS_NODE;
import static com.moallem.stu.utilities.FirebaseConstants.USEDPROMOCODEIDsUSERS;
import static com.moallem.stu.utilities.FirebaseConstants.USERINFO_NODE;
import static com.moallem.stu.utilities.Utils.msg;

public class MainActivity extends AppCompatActivity implements SampleRecyclerViewAdapter.OnItemClicked {

    private static final String TAG = "MainActivityc";

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    private FirebaseAuth firebaseAuth;
    //private GoogleSignInClient mGoogleSignInClient;
    private DatabaseReference mDatabase;
    private SampleRecyclerViewAdapter rcAdapter;
    private ArrayList<Subject> sList = new ArrayList<>();
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    private Unbinder bind;
    private ChildEventListener childEventListener;
    private Drawer drawer;
    private PrimaryDrawerItem item2;
    private boolean isThereUnfinishedSession;
    private ChildEventListener checkerChildEventListener;
    AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        configTwitterSdk();
        setContentView(R.layout.activity_main);
        bind = ButterKnife.bind(this);
        firebaseAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        //checkIfUserNotLogin();
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
        } else {
            Toast.makeText(this, R.string.check_internet_msg, Toast.LENGTH_SHORT).show();
        }

        configNavigationDrawer();
    }


    private void getCountryName() {
        try {
            TelephonyManager tm = (TelephonyManager) this.getSystemService(this.TELEPHONY_SERVICE);
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
        } catch (ActivityNotFoundException anfe) {
            startActivity(webintent);
        }

    }

    private void showConfirmDialogForUpdate() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.new_version_notifai);

        builder.setPositiveButton(R.string.alert_ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                openApplink();

            }
        });
        builder.setNegativeButton(R.string.alert_cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
            }
        });

        builder.setNeutralButton(R.string.alert_dont_show_again, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(MainActivity.this, "" + which, Toast.LENGTH_SHORT).show();
                PrefsHelper.getInstance(getApplicationContext()).setShowDialog(false);
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();

    }

    private void configNavigationDrawer() {
        Typeface font = ResourcesCompat.getFont(this,R.font.roboto_regular);
        String displayName = firebaseAuth.getCurrentUser() != null ? firebaseAuth.getCurrentUser().getDisplayName()
                : "Moallem";
        PrimaryDrawerItem item1 = new PrimaryDrawerItem().withIcon(R.drawable.test_icon)
                .withIdentifier(1).withTypeface(font).withName(R.string.session_navigationdrawer);

        item2 = new PrimaryDrawerItem().withIcon(R.drawable.ic_minutes).withBadge("0")
                .withBadgeStyle(new BadgeStyle().withTextColor(Color.WHITE).withColorRes(R.color.app_title_color))
                .withIdentifier(2).withTypeface(font).withName(R.string.minutes_navigationdrawer);

        PrimaryDrawerItem item3 = new PrimaryDrawerItem().withIcon(R.drawable.ic_free_minutes)
                .withIdentifier(3).withTypeface(font).withName(R.string.free_minutes);

        PrimaryDrawerItem item4 = new PrimaryDrawerItem().withIcon(R.drawable.ic_signout)
                .withIdentifier(4).withTypeface(font).withName(R.string.signout_navigationdrawer);


        AccountHeader header = new AccountHeaderBuilder()
                .withActivity(this)
                .withHeaderBackground(R.color.app_title_color)
                .addProfiles(
                        new ProfileDrawerItem().withName(displayName)
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
                        item1, item2, item3, item4
                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        switch (position) {
                            case 1:
                                if (isUserLogged()) {
                                    drawer.closeDrawer();
                                    startActivity(new Intent(MainActivity.this
                                            , SessionsActivity.class));
                                } else {
                                    finishToLoggin();
                                }
                                break;
                            case 2:
                                if (isUserLogged()) {
                                    drawer.closeDrawer();
                                    startPaymentActivity();
                                } else {
                                    finishToLoggin();
                                }
                                break;
                            case 3:
                                if (isUserLogged()) {
                                    drawer.closeDrawer();
                                    freeMinutesDialog();
                                } else {
                                    finishToLoggin();
                                }
                                break;
                            case 4:
                                logout();
                                break;
                            default:
                                break;
                        }
                        return true;
                    }
                })
                .withDrawerGravity(GravityCompat.START)
                .build();
        if (!isUserLogged())
            drawer.updateItem(item4.withName(R.string.signup_or_login));
    }

    private void copyToclipboard(String text){
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("Copy", text);
        clipboard.setPrimaryClip(clip);
    }

    private void sharePromo(String msg){

        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Subject Here");
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, msg);
        startActivity(Intent.createChooser(sharingIntent, "dkmc"));
    }


    private void freeMinutesDialog() {
        String promocode = String.format("moallem%s", Utils.getCurrentUserId().substring(0, 8));
        AlertDialog.Builder builder = new AlertDialog.Builder(this, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        LayoutInflater inflater = MainActivity.this.getLayoutInflater();
        View mView = inflater.inflate(R.layout.free_minutes_dialog, null);

        builder.setView(mView);
        ImageView cancel = mView.findViewById(R.id.image_cancel);
        Button button = (Button) mView.findViewById(R.id.submit_button);
        TextView codeToCopy = mView.findViewById(R.id.code_to_copy);
        EditText promocodeField = mView.findViewById(R.id.promocode_field);
        ImageView share = mView.findViewById(R.id.share_img);


        codeToCopy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                copyToclipboard(promocode);
                Toast.makeText(MainActivity.this, "Promocode has been copied to clipboard", Toast.LENGTH_SHORT).show();
            }
        });

        codeToCopy.setText(promocode);

        dialog = builder.create();
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation_2;
        dialog.show();
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Utils.isNetworkConnected(MainActivity.this)) {
                    isPromocodeProcessingOpen(promocodeField.getText().toString());

                }else {
                    Toast.makeText(MainActivity.this, R.string.check_internet_msg, Toast.LENGTH_SHORT).show();

                }
            }
        });

        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sharePromo(promocode);
            }
        });


    }

    private void isPromocodeProcessingOpen(String s) {
        mDatabase.child("Utils").child("isPromocodeProcessingOpen").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    Boolean isOpen = dataSnapshot.getValue(Boolean.class);
                    if (isOpen != null && isOpen){
                        checkPromoCodeValidility(s);
                    }else{
                        Toast.makeText(MainActivity.this, "Please try again later", Toast.LENGTH_SHORT).show();
                    }
                }else {
                    mDatabase.child("Utils").child("isPromocodeProcessingOpen").setValue(true);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }



    private void checkPromoCodeValidility(String promocode) {
        String currentPromocode = String.format("moallem%s", Utils.getCurrentUserId().substring(0, 8));
        if (!promocode.trim().isEmpty() && !currentPromocode.equals(promocode)) {

            String subId = promocode.replace("moallem", "");
            Toast.makeText(this, subId, Toast.LENGTH_SHORT).show();
            Query query = mDatabase.child(USERINFO_NODE).orderByKey().startAt(subId).limitToFirst(1);

            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        //get the id which start with given substring
                        for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                            checkIfPromocodeUsedBefore(dataSnapshot1.getKey());
                        }
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }else{
            Toast.makeText(this, "Please enter valid promocode", Toast.LENGTH_SHORT).show();

        }
    }

    private void checkIfPromocodeUsedBefore(String userId) {
        mDatabase.child(USERINFO_NODE).child(Utils.getCurrentUserId())
                .child(USEDPROMOCODEIDsUSERS).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    boolean isUserFound = false;
                    String userids = dataSnapshot.getValue(String.class);
                    if (userids != null) {
                        String[] usersidsArr = userids.split(",");
                        for (String user : usersidsArr) {
                            if (user.equals(userId)) {
                                isUserFound = true;
                                break;
                            }
                        }
                        if (isUserFound) {
                            Toast.makeText(MainActivity.this, "Sorry you have used this promocode before"
                                    , Toast.LENGTH_SHORT).show();
                        } else {
                            dialog.dismiss();
                            updateFirebaseBalanceValues(Utils.getCurrentUserId(),5);
                            updateFirebaseBalanceValues(userId,5);

                            mDatabase.child(USERINFO_NODE).child(Utils.getCurrentUserId())
                                    .child(USEDPROMOCODEIDsUSERS).setValue(userids + "," + userId);
                            addUserIdToPromocodeFriendList(userId);
                            Toast.makeText(MainActivity.this,
                                    "5 minutes has been added to your account and your friend's account", Toast.LENGTH_LONG).show();
                        }
                    }
                }
                else {
                    dialog.dismiss();
                    updateFirebaseBalanceValues(Utils.getCurrentUserId(),5);
                    updateFirebaseBalanceValues(userId,5);

                    mDatabase.child(USERINFO_NODE).child(Utils.getCurrentUserId())
                            .child(USEDPROMOCODEIDsUSERS).setValue(userId);
                    addUserIdToPromocodeFriendList(userId);
                    Toast.makeText(MainActivity.this,
                            "5 minutes has been added to your account and your friend's account", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });
    }

    public void updateFirebaseBalanceValues(String userID,int value) {
        Double secs = (double) (value * 60);

        mDatabase.child(USERINFO_NODE).child(userID).child("timeBalance")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            Double balance = dataSnapshot.getValue(Double.class);
                            if (balance != null) {
                                balance += secs;
                                mDatabase.child(USERINFO_NODE).child(userID).child("timeBalance").setValue(balance);
                            }
                        } else {
                            mDatabase.child(USERINFO_NODE).child(userID).child("timeBalance").setValue(secs);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {}
                });
    }

    private void addUserIdToPromocodeFriendList(String id) {
        mDatabase.child(USERINFO_NODE).child(id)
                .child(USEDPROMOCODEIDsUSERS).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()) {

                    String userids = dataSnapshot.getValue(String.class);
                    if (userids != null) {
                        mDatabase.child(USERINFO_NODE).child(id)
                                .child(USEDPROMOCODEIDsUSERS).setValue(","+Utils.getCurrentUserId());

                    }
                }else{

                    mDatabase.child(USERINFO_NODE).child(id)
                            .child(USEDPROMOCODEIDsUSERS).setValue(Utils.getCurrentUserId());

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });


    }

    private void startPaymentActivity() {
        String countryCode = PrefsHelper.getInstance(this).getCountryCode();
        if (countryCode == null || countryCode.equals("none")) {
            Toast.makeText(this, R.string.make_sure_sim_insered, Toast.LENGTH_SHORT).show();
        } else if (fromAllowedCounteries(countryCode)) {
            startActivity(new Intent(MainActivity.this
                    , PaymentActivity.class));
        } else {
            Toast.makeText(this, R.string.this_section_not_allowed, Toast.LENGTH_SHORT).show();
        }
    }

    private boolean fromAllowedCounteries(String countryCode) {
        String country = countryCode.toLowerCase();
        return country.equals("eg");
    }

    private void getStudentBalance() {
        if (isUserLogged()) {
            mDatabase.child(USERINFO_NODE).child(Utils.getCurrentUserId())
                    .child("timeBalance").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        Double balance = dataSnapshot.getValue(Double.class);
                        if (balance != null) {
                            drawer.updateBadge(2, new StringHolder((balance / 60) + ""));
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }

    private void checkForNewVersion() {
        mDatabase.child("versionsUpdateNotify").child("studentApp")
                .child("isVersion1-0-6Updated").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Boolean isUpdated = dataSnapshot.getValue(Boolean.class);
                    if (isUpdated != null && isUpdated) {
                        if (PrefsHelper.getInstance(getApplicationContext()).getShowDialog()) {
                            if (PrefsHelper.getInstance(getApplicationContext()).getCounterToshowDialog() == 10) {
                                showConfirmDialogForUpdate();
                                PrefsHelper.getInstance(getApplicationContext()).resetCounterToshowDialog();
                            } else {
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


    private void initList() {

        rcAdapter = new SampleRecyclerViewAdapter(getApplicationContext(), sList);
        recyclerView.setAdapter(rcAdapter);
        rcAdapter.setOnClick(MainActivity.this);

        childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, String s) {

                if (checkIfNodesExists(dataSnapshot, "name", "subIcon", "arabicName")) {
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

    /*private void configereGoogleSignin() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);


    }*/

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen()) {
            drawer.closeDrawer();
        } else {
            super.onBackPressed();
        }
    }


    private void logout() {

        if (isUserLogged()) {
            /*mDatabase.child(USERINFO_NODE).child(Utils.getCurrentUserId()).child("tokenId")
                    .removeValue();*/
            firebaseAuth.signOut();
            // mGoogleSignInClient.signOut();
            LoginManager.getInstance().logOut();
            TwitterCore.getInstance().getSessionManager().clearActiveSession();
        }

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

    @Override
    public void onItemClick(int position) {
        if (isUserLogged()) {
            checkBalanceAndLaunchActivity(sList.get(position));
        } else {
            finishToLoggin();
        }

    }

    private void finishToLoggin(){
        msg(this,R.string.signup_or_login_first);
        finish();
        startActivity(new Intent(MainActivity.this,RegisteringActivity.class));
    }

    private void checkBalanceAndLaunchActivity(Subject subect) {

        mDatabase.child(USERINFO_NODE).child(Utils.getCurrentUserId())
                .child("timeBalance").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Double balance = dataSnapshot.getValue(Double.class);
                    if (balance != null && balance >= 60) {
                        checkForUnfinishedSession(subect);
                    } else {
                        startActivity(new Intent(MainActivity.this
                                , PaymentActivity.class));
                        Toast.makeText(getApplicationContext(), R.string.empty_balance_msg, Toast.LENGTH_LONG).show();
                    }
                } else {
                    startActivity(new Intent(MainActivity.this
                            , PaymentActivity.class));
                    Toast.makeText(getApplicationContext(), R.string.empty_balance_msg, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void checkForUnfinishedSession(Subject subect) {
        mDatabase.child(USERINFO_NODE).child(Utils.getCurrentUserId())
                .child(ISTHEREUNFINISHEDSESSION).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Boolean isThereUnfinishedSession = dataSnapshot.getValue(Boolean.class);
                    if (isThereUnfinishedSession != null && !isThereUnfinishedSession) {
                        startActivity(new Intent(getApplicationContext(), PostingQuestionActivity.class)
                                .putExtra("subjectInfo", subect)
                                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                    } else {
                        Toast.makeText(MainActivity.this, "Please end the unfinished question", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    mDatabase.child(USERINFO_NODE).child(Utils.getCurrentUserId())
                            .child(ISTHEREUNFINISHEDSESSION).setValue(false);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    public boolean checkIfNodesExists(DataSnapshot snapshot, String... ss) {
        for (String s : ss) {
            if (!snapshot.child(s).exists())
                return false;
        }
        return true;
    }

    private boolean isUserLogged(){
        return firebaseAuth.getCurrentUser() != null;
    }
}
