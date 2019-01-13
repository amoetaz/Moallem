package com.moallem.stu.ui.fragments;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.TwitterAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.moallem.stu.R;
import com.moallem.stu.ui.activities.MainActivity;
import com.moallem.stu.utilities.FirebaseUtils;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterConfig;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;

import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

import static com.moallem.stu.utilities.FirebaseConstants.USERINFO_NODE;
import static com.moallem.stu.utilities.FirebaseConstants.USERNAME_NODE;

/**
 * A simple {@link Fragment} subclass.
 */
public class LoginFragment extends Fragment {

    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    @BindView(R.id.login_email)
    EditText email;
    @BindView(R.id.login_password)
    EditText password;
    @BindView(R.id.buRegister)
    Button buLogin;
    @BindView(R.id.button_facebook_login)
    LoginButton loginButton;
    @BindView(R.id.button_twitter_login)
    TwitterLoginButton mLoginButton;
    Unbinder unbinder;
    private FirebaseAuth firebaseAuth;
    private CallbackManager mCallbackManager;
    private DatabaseReference databaseReference;


    public LoginFragment() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        configTwitterSdk();
        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        mCallbackManager = CallbackManager.Factory.create();

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        unbinder = ButterKnife.bind(this, view);
        progressBar.setVisibility(View.GONE);
        mLoginButton.setCallback(new Callback<TwitterSession>() {
            @Override
            public void success(Result<TwitterSession> result) {
                handleTwitterSession(result.data);
                progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void failure(TwitterException exception) {

            }
        });
        return view;
    }

    @Override
    public void onResume() {

        super.onResume();

        getView().setFocusableInTouchMode(true);
        getView().requestFocus();
        getView().setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK){
                    getActivity()
                            .getSupportFragmentManager()
                            .popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                    return true;

                }

                return false;
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick({R.id.buRegister, R.id.button_facebook_login})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.buRegister:
                progressBar.setVisibility(View.VISIBLE);
                hideKeyboard();
                userLogin();
                break;
            case R.id.button_facebook_login:
                initializeFacebookButton();
                break;

        }
    }

    private void userLogin() {
        String emailStr = email.getText().toString().trim();
        String passwordStr = password.getText().toString().trim();

        if (isFieldsNotEmpty()) {
            firebaseAuth.signInWithEmailAndPassword(emailStr, passwordStr)
                    .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if (task.isSuccessful()) {
                                FirebaseUtils.setUerType(getContext(), firebaseAuth.getCurrentUser().getUid()
                                        , new FirebaseUtils.Action() {
                                            @Override
                                            public void onCompleteListener() {

                                             getActivity().finish();
                                             startActivity(new Intent(getContext(), MainActivity.class));

                                            }
                                        });
                            } else {

                                Toast.makeText(getContext(), task.getException().getMessage(), Toast.LENGTH_LONG).show();
                            }
                            progressBar.setVisibility(View.INVISIBLE);

                        }
                    });
        } else {
            Toast.makeText(getActivity(), "Please enter valid data", Toast.LENGTH_SHORT).show();
        }

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //facebook
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
        //twitter
        mLoginButton.onActivityResult(requestCode, resultCode, data);
    }


    private void initializeFacebookButton() {

        LoginManager.getInstance().logInWithReadPermissions(getActivity(),
                Arrays.asList("email", "public_profile"));
        LoginManager.getInstance().registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {

                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
            }

            @Override
            public void onError(FacebookException error) {

            }
        });

    }


    private void handleFacebookAccessToken(AccessToken token) {

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        if (getActivity() != null) {
            firebaseAuth.signInWithCredential(credential)
                    .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                progressBar.setVisibility(View.VISIBLE);
                                FirebaseUtils.setUerType(getContext(), firebaseAuth.getCurrentUser().getUid()
                                        , new FirebaseUtils.Action() {
                                            @Override
                                            public void onCompleteListener() {
                                                checkuserexisting(task.getResult().getAdditionalUserInfo().isNewUser());
                                                getActivity().finish();
                                                startActivity(new Intent(getContext(), MainActivity.class));
                                            }
                                        });

                            } else {
                                Toast.makeText(getActivity(), "Something went wrong", Toast.LENGTH_SHORT).show();
                            }
                            progressBar.setVisibility(View.INVISIBLE);
                        }

                    });
        }
    }

    private void checkuserexisting(boolean newUser) {
        String uid = firebaseAuth.getCurrentUser().getUid();

        if (newUser){
            databaseReference.child(USERINFO_NODE).child(uid)
                    .child(USERNAME_NODE).setValue(firebaseAuth.getCurrentUser().getDisplayName());
        }

    }

    private void configTwitterSdk() {

        // Configure Twitter SDK
        TwitterAuthConfig authConfig = new TwitterAuthConfig(
                getString(R.string.twitter_consumer_key),
                getString(R.string.twitter_consumer_secret));

        TwitterConfig twitterConfig = new TwitterConfig.Builder(getActivity())
                .twitterAuthConfig(authConfig)
                .build();

        Twitter.initialize(twitterConfig);
    }

    private boolean isFieldsNotEmpty() {
        return !TextUtils.isEmpty(email.getText()) && !TextUtils.isEmpty(password.getText());
    }

    private void handleTwitterSession(TwitterSession session) {

        AuthCredential credential = TwitterAuthProvider.getCredential(
                session.getAuthToken().token,
                session.getAuthToken().secret);



        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            FirebaseUtils.setUerType(getContext(), firebaseAuth.getCurrentUser().getUid()
                                    , new FirebaseUtils.Action() {
                                        @Override
                                        public void onCompleteListener() {
                                            checkuserexisting(task.getResult().getAdditionalUserInfo().isNewUser());
                                            getActivity().finish();
                                            startActivity(new Intent(getContext(), MainActivity.class));
                                        }
                                    });


                        } else {
                            Toast.makeText(getActivity(), "Something went wrong", Toast.LENGTH_SHORT).show();
                        }
                        progressBar.setVisibility(View.INVISIBLE);
                    }
                });
    }


    public void hideKeyboard(){
        View view = getActivity().getCurrentFocus();
        if (view != null){
            InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(),0);
        }
    }


}
