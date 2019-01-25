package com.moallem.stu.ui.fragments;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.moallem.stu.R;
import com.moallem.stu.ui.activities.MainActivity;
import com.moallem.stu.utilities.FirebaseUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

import static com.moallem.stu.utilities.FirebaseConstants.EMAIL_NODE;
import static com.moallem.stu.utilities.FirebaseConstants.ISTHEREUNFINISHEDSESSION;
import static com.moallem.stu.utilities.FirebaseConstants.USERINFO_NODE;
import static com.moallem.stu.utilities.FirebaseConstants.USERNAME_NODE;

/**
 * A simple {@link Fragment} subclass.
 */
public class SignupFragment extends Fragment {

    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    @BindView(R.id.signup_username)
    EditText username;
    @BindView(R.id.signup_email)
    EditText email;
    @BindView(R.id.signup_password)
    EditText password;
    @BindView(R.id.signup_edu)
    EditText Edu;
    @BindView(R.id.signup_buttonsignup)
    Button signupButton;
    Unbinder unbinder;

    private FirebaseAuth firebaseAuth;
    private DatabaseReference mDatabase;

    public SignupFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firebaseAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        FirebaseUser user = firebaseAuth.getCurrentUser();

        if ( user!= null ) {
            user.reload().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful() && getActivity() != null){
                        if (user.isEmailVerified()) {
                            startActivity(new Intent(getActivity(), MainActivity.class));
                            getActivity().finish();
                        }
                    }
                }
            });

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_signup, container, false);
        unbinder = ButterKnife.bind(this, view);
        progressBar.setVisibility(View.GONE);
        return view;
    }



    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick(R.id.signup_buttonsignup)
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.signup_buttonsignup:
                hideKeyboard();
                try {
                    progressBar.setVisibility(View.VISIBLE);
                    regiterUser();
                } catch (Exception e) {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(getActivity(), R.string.wrong_message, Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }


    private void regiterUser() {
        final String emailStr = email.getText().toString().trim();
        String passwordStr = password.getText().toString().trim();

        if (isFieldsNotEmpty() && getActivity() != null) {
            if (!isPasswordlessthan8() ) {
                FirebaseUser currentUser = firebaseAuth.getCurrentUser();
                if (currentUser == null) {
                    firebaseAuth.createUserWithEmailAndPassword(emailStr, passwordStr)
                            .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                        if (user != null) {
                                            if (user.isEmailVerified()) {
                                                initializeFirebasedatabaseVariables(emailStr);
                                                updateProfileAndLaunch(user);
                                                progressBar.setVisibility(View.GONE);
                                            }else {
                                                user.sendEmailVerification()
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                progressBar.setVisibility(View.GONE);
                                                                if (task.isSuccessful()) {
                                                                    Toast.makeText(getContext(), R.string.verify_email_sent, Toast.LENGTH_LONG).show();
                                                                }
                                                                else {
                                                                    Toast.makeText(getContext(), R.string.wrong_message, Toast.LENGTH_SHORT).show();
                                                                }
                                                            }
                                                        });
                                            }
                                        }
                                    } else {
                                        Toast.makeText(getContext(), R.string.wrong_message, Toast.LENGTH_SHORT).show();

                                    }
                                }
                            });
                }else {
                    currentUser.reload().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                progressBar.setVisibility(View.GONE);
                                if (currentUser.isEmailVerified()){
                                    initializeFirebasedatabaseVariables(emailStr);
                                    updateProfileAndLaunch(currentUser);
                                }else {
                                    Toast.makeText(getContext(), R.string.verify_email_toast, Toast.LENGTH_LONG).show();
                                }
                            }
                        }
                    });

                }
            }else {
                password.setText("");
            }
        }else {
            progressBar.setVisibility(View.GONE);
            Toast.makeText(getActivity(), R.string.enter_valid_data, Toast.LENGTH_SHORT).show();
        }

    }


    private void updateProfileAndLaunch(FirebaseUser user) {
        UserProfileChangeRequest userProfileChangeRequest = new UserProfileChangeRequest.Builder()
                .setDisplayName(username.getText().toString())
                .build();

        user.updateProfile(userProfileChangeRequest)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (getActivity() != null){
                            FirebaseUtils.setUerType(getContext(), firebaseAuth.getCurrentUser().getUid(),
                                    new FirebaseUtils.Action() {
                                        @Override
                                        public void onCompleteListener() {
                                            getActivity().finish();
                                            startActivity(new Intent(getContext(), MainActivity.class));
                                        }
                                    });

                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(), R.string.wrong_message, Toast.LENGTH_SHORT).show();

            }
        });

    }

    private void initializeFirebasedatabaseVariables(String emailStr) {


        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

            mDatabase.child(USERINFO_NODE).child(userId)
                        .child(EMAIL_NODE).setValue(emailStr);
            mDatabase.child(USERINFO_NODE).child(userId)
                        .child(USERNAME_NODE).setValue(username.getText().toString());

            mDatabase.child(USERINFO_NODE).child(userId)
                    .child("education").setValue(Edu.getText().toString());

            mDatabase.child(USERINFO_NODE).child(userId)
                    .child(ISTHEREUNFINISHEDSESSION).setValue(false);

        }

    }



    private boolean isFieldsNotEmpty(){
        return !TextUtils.isEmpty(email.getText()) && !TextUtils.isEmpty(password.getText())
                && !TextUtils.isEmpty(username.getText());
    }

    private boolean isPasswordlessthan8(){
        return password.getText().length() <8;
    }


    public void hideKeyboard(){
        View view = getActivity().getCurrentFocus();
        if (view != null){
            InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(),0);
        }
    }

}
