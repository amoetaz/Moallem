package com.moallem.stu.ui.fragments;


import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.moallem.stu.R;
import com.moallem.stu.adapters.SessionsAdapter;
import com.moallem.stu.data.PrefsHelper;
import com.moallem.stu.models.Session;
import com.moallem.stu.utilities.Utils;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static com.moallem.stu.utilities.FirebaseConstants.DATE_NODE;
import static com.moallem.stu.utilities.FirebaseConstants.ISFINISHED_NODE;
import static com.moallem.stu.utilities.FirebaseConstants.ISREPLYED_NODE;
import static com.moallem.stu.utilities.FirebaseConstants.ISSTUDENTREACHERZERO_NODE;
import static com.moallem.stu.utilities.FirebaseConstants.STORAGEDATAID_FOLDER;
import static com.moallem.stu.utilities.FirebaseConstants.TEACHERNAME_NODE;

/**
 * A simple {@link Fragment} subclass.
 */
public class SessionsFragment extends Fragment {

    private static final String TAG = "SessionsFragmentclass";
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.rv_sessions)
    RecyclerView rvSessions;
    @BindView(R.id.empty_list_image)
    ImageView emptyListImage;
    Unbinder unbinder;
    private SessionsAdapter adapter;
    private ArrayList<Session> sessions = new ArrayList<>();
    private DatabaseReference mDatabase;
    private String subjectKey;
    private boolean isTeacherHome ;
    private FirebaseAuth firebaseAuth;
    private ChildEventListener childEventListener;

    public SessionsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        subjectKey = getArguments().getString("subjectKey");
        isTeacherHome = getArguments().getBoolean("isTeacherHome",false);
        firebaseAuth = FirebaseAuth.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_sessions, container, false);
        unbinder = ButterKnife.bind(this, view);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        //linearLayoutManager.setReverseLayout(true);
        rvSessions.setLayoutManager(linearLayoutManager);

        return view;
    }

    private void initList() {

        adapter = new SessionsAdapter(getActivity(),sessions);
        rvSessions.setAdapter(adapter);

        if (sessions != null && sessions.size() > 0){
            sessions.clear();
        }

        childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, String s) {

                Session session = new Session();
                if (dataSnapshot.exists()) {
                    if (Utils.checkIfNodesExists(dataSnapshot,"firstComment","firstPic","questionType","studentId"
                            ,"studentName")) {
                        String firstComment = dataSnapshot.child("firstComment").getValue(String.class);
                        String firstPic = dataSnapshot.child("firstPic").getValue(String.class);
                        String questionType = dataSnapshot.child("questionType").getValue(String.class);
                        String studentId = dataSnapshot.child("studentId").getValue(String.class);
                        String studentName = dataSnapshot.child("studentName").getValue(String.class);
                        Boolean isReplyed = dataSnapshot.child(ISREPLYED_NODE).getValue(Boolean.class);
                        Boolean isFinished = dataSnapshot.child(ISFINISHED_NODE).getValue(Boolean.class);
                        String teacherId = dataSnapshot.child("teacherId").getValue(String.class);
                        String teacherName = dataSnapshot.child(TEACHERNAME_NODE).getValue(String.class);
                        String teacherPic = dataSnapshot.child("teacherPic").getValue(String.class);
                        String strorageDataId = dataSnapshot.child(STORAGEDATAID_FOLDER).getValue(String.class);
                        Boolean isStudentReachedZeroMins = dataSnapshot.child(ISSTUDENTREACHERZERO_NODE).getValue(Boolean.class);
                        String key = dataSnapshot.getKey();
                        String date = dataSnapshot.child(DATE_NODE).getValue(String.class);

                        session.setTeacherName(teacherName);
                        session.setDate(date);
                        session.setStudentReachedZeroMins(isStudentReachedZeroMins);
                        session.setTeacherPic(teacherPic);
                        session.setTeacherId(teacherId);
                        session.setFirstComment(firstComment);
                        session.setFirstPic(firstPic);
                        session.setQuestionType(questionType);
                        session.setReplyed(isReplyed);
                        session.setStudentId(studentId);
                        session.setStudentName(studentName);
                        session.setKey(key);
                        session.setStorageDataID(strorageDataId);
                        session.setFinished(isFinished);

                        if (isTeacherHome && !session.getReplyed()) {
                            sessions.add(session);
                        } else if (PrefsHelper.getInstance(getActivity()).getUserType().equals("student")
                                && firebaseAuth.getCurrentUser().getUid().equals(session.getStudentId()) && !isTeacherHome) {
                            sessions.add(session);
                        } else if (PrefsHelper.getInstance(getActivity()).getUserType().equals("teacher")
                                && firebaseAuth.getCurrentUser().getUid().equals(session.getTeacherId()) && !isTeacherHome) {
                            sessions.add(session);
                        }

                        adapter.notifyItemInserted(sessions.size() - 1);
                    }
                }
                //checkIFListisEmpty();
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

        mDatabase.child("subjects").child(subjectKey).child("questionids")
                .addChildEventListener(childEventListener);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onResume() {
        super.onResume();
        initList();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (childEventListener != null) {
            mDatabase.child("subjects").child(subjectKey).child("questionids")
                    .removeEventListener(childEventListener);
        }
    }
}
