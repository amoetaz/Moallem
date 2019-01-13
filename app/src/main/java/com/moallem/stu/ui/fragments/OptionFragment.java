package com.moallem.stu.ui.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.transition.ChangeBounds;
import android.transition.Slide;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.moallem.stu.R;
import com.moallem.stu.ui.activities.MainActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;


/**
 * A simple {@link Fragment} subclass.
 */
public class OptionFragment extends Fragment {

    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    @BindView(R.id.option_button_option1)
    Button option1;
    @BindView(R.id.option_textlogin)
    TextView loginOption;
    Unbinder unbinder;
    private FirebaseAuth firebaseAuth;

    public OptionFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firebaseAuth = FirebaseAuth.getInstance();

        if (getActivity() != null) {
            getActivity().getSupportFragmentManager()
                    .addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
                @Override
                public void onBackStackChanged() {
                    if (getActivity().getSupportFragmentManager().getBackStackEntryCount() <1){
                        enableclicklisteners();
                    }
                }
            });
        }

    }

    private void enableclicklisteners() {
        loginOption.setEnabled(true);
        option1.setEnabled(true);
    }

    private void disableclicklisteners() {
        loginOption.setEnabled(false);
        option1.setEnabled(false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_option, container, false);
        unbinder = ButterKnife.bind(this, view);
        checkUser();
        return view;
    }

    private void checkUser() {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if ( user!= null && getActivity() != null ) {
            if (user.getProviders().get(0).equals("facebook.com") ||
                    user.getProviders().get(0).equals("twitter.com")
                    || user.isEmailVerified()) {
                startActivity(new Intent(getActivity(), MainActivity.class));
                getActivity().finish();
            }
            }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick({R.id.option_button_option1,  R.id.option_textlogin})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.option_button_option1:
                signup();
                break;
            case R.id.option_textlogin:
                login();
                break;
        }
    }

    private void login() {
        disableclicklisteners();
        LoginFragment loginFragment = new LoginFragment();

        slideAnimation(loginFragment);
        getActivity().getSupportFragmentManager()
                .beginTransaction().addToBackStack(null).add(R.id.fregister, loginFragment)
                .commit();
    }

    private void signup() {
        disableclicklisteners();
        SignupFragment signupFragment = new SignupFragment();
        slideAnimation(signupFragment);
        if (getActivity() != null) {
            getActivity().getSupportFragmentManager()
                    .beginTransaction().addToBackStack(null).add(R.id.fregister, signupFragment)
                    .commit();
        }
    }

    private void slideAnimation(Fragment fragment){
        Slide slideTransition = null ;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            slideTransition = new Slide(GravityCompat.getAbsoluteGravity(GravityCompat.END
                    , getResources().getConfiguration().getLayoutDirection()));
            slideTransition.setDuration(getResources().getInteger(R.integer.anim_duration_medium));

            ChangeBounds changeBoundsTransition = new ChangeBounds();
            changeBoundsTransition.setDuration(getResources().getInteger(R.integer.anim_duration_medium));

            fragment.setEnterTransition(slideTransition);
            fragment.setAllowEnterTransitionOverlap(false);
            fragment.setAllowReturnTransitionOverlap(false);
            fragment.setSharedElementEnterTransition(changeBoundsTransition);
        }
    }


}
