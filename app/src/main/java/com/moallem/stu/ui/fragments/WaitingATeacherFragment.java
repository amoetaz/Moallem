package com.moallem.stu.ui.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.moallem.stu.R;
import com.moallem.stu.models.Session;
import com.moallem.stu.models.Subject;
import com.moallem.stu.ui.activities.ChattingActivity;
import com.moallem.stu.utilities.Utils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static com.moallem.stu.utilities.FirebaseConstants.ISFINISHED_NODE;
import static com.moallem.stu.utilities.FirebaseConstants.ISREPLYED_NODE;
import static com.moallem.stu.utilities.FirebaseConstants.ISSTUDENTREACHERZERO_NODE;
import static com.moallem.stu.utilities.FirebaseConstants.ISTHEREUNFINISHEDSESSION;
import static com.moallem.stu.utilities.FirebaseConstants.QUESTIONIDS_NODES;
import static com.moallem.stu.utilities.FirebaseConstants.SUBJECTS_NODE;
import static com.moallem.stu.utilities.FirebaseConstants.TEACHERID_NODE;
import static com.moallem.stu.utilities.FirebaseConstants.TEACHERNAME_NODE;
import static com.moallem.stu.utilities.FirebaseConstants.USERINFO_NODE;

/**
 * A simple {@link Fragment} subclass.
 */
public class WaitingATeacherFragment extends Fragment {


    private static final String TAG = "WATFragmentclass";
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    Unbinder unbinder;
    private Subject subject;
    private Session session;
    private String questionId;
    private DatabaseReference mDatabase;
    private String storageDataID;
    private StorageReference mStorage;
    private FirebaseAuth firebaseAuth;
    private String firstPicUrl;
    private ValueEventListener valueEventListener;
    private boolean isAnswered = false;

    public WaitingATeacherFragment() {
        // Required empty public constructor
    }

    private void initFirebaseInstances() {
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mStorage = FirebaseStorage.getInstance().getReference();
        firebaseAuth = FirebaseAuth.getInstance();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        subject = getArguments().getParcelable("subjectInfo");
        questionId = getArguments().getString("questionId");
        storageDataID = getArguments().getString("storageDataID");
        firstPicUrl = getArguments().getString("firstPicUrl");
        initFirebaseInstances();
        session = new Session();
        session.setQuestionType(subject.getKey());
        session.setKey(questionId);
        session.setStorageDataID(storageDataID);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_waiting_ateacher, container, false);
        unbinder = ButterKnife.bind(this, view);
        progressBar.setVisibility(View.VISIBLE);
        checkIfQuestionAnswered();

        return view;
    }

    private void checkIfQuestionAnswered() {
        valueEventListener = mDatabase.child(SUBJECTS_NODE).child(subject.getKey()).child(QUESTIONIDS_NODES).child(questionId)
                .child(ISREPLYED_NODE)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Boolean isReplyed = dataSnapshot.getValue(Boolean.class);
                        if (isReplyed != null && isReplyed) {
                            isAnswered = true;
                            setIsThereUnfinishedSession();
                            try {
                                Thread.sleep(1500);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            mDatabase.child(SUBJECTS_NODE).child(subject.getKey()).child(QUESTIONIDS_NODES).child(questionId)
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            if (dataSnapshot.exists()) {

                                                String teacherid = dataSnapshot.child(TEACHERID_NODE).getValue(String.class);
                                                String teacherName = dataSnapshot.child(TEACHERNAME_NODE).getValue(String.class);
                                                String teacherPic = dataSnapshot.child("teacherPic").getValue(String.class);
                                                Boolean isFinished = dataSnapshot.child(ISFINISHED_NODE).getValue(Boolean.class);
                                                Boolean isTeacherOnline = dataSnapshot.child("isTeacherOnline").getValue(Boolean.class);
                                                Boolean isStudentOnline = dataSnapshot.child("isStudentOnline").getValue(Boolean.class);
                                                Boolean isStudentReachedZeroMins = dataSnapshot.child(ISSTUDENTREACHERZERO_NODE).getValue(Boolean.class);
                                                session.setTeacherId(teacherid);
                                                session.setTeacherName(teacherName);
                                                session.setTeacherPic(teacherPic);
                                                session.setFinished(isFinished);
                                                session.setStudentReachedZeroMins(isStudentReachedZeroMins);
                                                session.setTeacherOnline(isTeacherOnline);
                                                session.setStudentOnline(isStudentOnline);
                                                progressBar.setVisibility(View.INVISIBLE);
                                                ChattingFragment.chatLength = 0;
                                                Intent intent = new Intent(getContext(), ChattingActivity.class);
                                                intent.putExtra("session_extra", session);
                                                startActivity(intent);
                                                if (getActivity() != null) {
                                                    getActivity().finish();
                                                }
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
        if (valueEventListener != null){
            mDatabase.child(SUBJECTS_NODE).child(subject.getKey()).child(QUESTIONIDS_NODES).child(questionId)
                    .child(ISREPLYED_NODE).removeEventListener(valueEventListener);
        }
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
                    progressBar.setVisibility(View.VISIBLE);
                    deleteSession();
                    deleteSession();
                    Toast.makeText(getActivity(), "Question has been deleted", Toast.LENGTH_SHORT).show();
                    getActivity().finish();
                    return true;

                }

                return false;
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        if (!isAnswered) {
            deleteSession();
            deleteSessionData();
            Toast.makeText(getActivity(), "Question has been deleted", Toast.LENGTH_SHORT).show();
        }

        getActivity();
        if (getActivity() != null) {
            getActivity().finish();
        }

    }

    public void deleteSession(){
        mDatabase.child(SUBJECTS_NODE)
                .child(subject.getKey())
                .child(QUESTIONIDS_NODES)
                .child(questionId)
                .removeValue();
    }

    public void deleteSessionData(){
        StorageReference photoRef = FirebaseStorage.getInstance().getReferenceFromUrl(firstPicUrl);

        photoRef.delete();

    }


    public void setIsThereUnfinishedSession(){
        mDatabase.child(USERINFO_NODE).child(Utils.getCurrentUserId())
                .child(ISTHEREUNFINISHEDSESSION).setValue(true);
    }
}
