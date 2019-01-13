package com.moallem.stu.ui.fragments;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.moallem.stu.R;
import com.moallem.stu.models.Session;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * A simple {@link Fragment} subclass.
 */
public class ViewQuestionFragment extends Fragment {

    @BindView(R.id.image)
    ImageView image;
    @BindView(R.id.text)
    TextView text;
    Unbinder unbinder;
    private Session session;
    private boolean check = false;

    public ViewQuestionFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        session = getArguments().getParcelable("session_data");
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_view_question, container, false);
        unbinder = ButterKnife.bind(this, view);
        view.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
        if (getContext() != null) {
            Glide.with(getContext()).load(session.getFirstPic())
                    .apply(new RequestOptions().fitCenter().format(DecodeFormat.PREFER_ARGB_8888)
                    .override(Target.SIZE_ORIGINAL))
                    .into(image);
        }
        text.setText(session.getFirstComment());
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!check){
                    text.setVisibility(View.INVISIBLE);
                    check = true;
                }else {
                    text.setVisibility(View.VISIBLE);
                    check = false;
                }
            }
        });
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }



}
