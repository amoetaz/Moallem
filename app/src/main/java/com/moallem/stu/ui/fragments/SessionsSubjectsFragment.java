package com.moallem.stu.ui.fragments;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.moallem.stu.R;
import com.moallem.stu.adapters.SessionsSubjectsAdapter;
import com.moallem.stu.models.Subject;
import com.moallem.stu.utilities.Utils;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static com.moallem.stu.utilities.FirebaseConstants.SUBJECTS_NODE;

/**
 * A simple {@link Fragment} subclass.
 */
public class SessionsSubjectsFragment extends Fragment {


    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.recycler_view_sessionssubjects)
    RecyclerView rvSubjects;
    Unbinder unbinder;
    private DatabaseReference mDatabase;
    private SessionsSubjectsAdapter adapter;
    private ArrayList<Subject> sList = new ArrayList<>();

    public SessionsSubjectsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDatabase = FirebaseDatabase.getInstance().getReference();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_sessions_subjects, container, false);
        unbinder = ButterKnife.bind(this, view);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        StaggeredGridLayoutManager staggeredGridLayoutManager =
                new StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL);
        rvSubjects.setLayoutManager(staggeredGridLayoutManager);

        initList();
        return view;
    }

    private void initList() {

        adapter = new SessionsSubjectsAdapter(getActivity(),sList);
        rvSubjects.setAdapter(adapter);

        if (sList != null && sList.size() > 0){
            sList.clear();
        }
        mDatabase.child(SUBJECTS_NODE)
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, String s) {
                        if (Utils.checkIfNodesExists(dataSnapshot ,"name","subIcon","arabicName")) {
                            String subjectName = dataSnapshot.child("name").getValue(String.class);
                            String subjectIconUrl = dataSnapshot.child("subIcon").getValue(String.class);
                            String arabicName = dataSnapshot.child("arabicName").getValue(String.class);
                            String key = dataSnapshot.getKey();
                            Subject subject = new Subject();

                            subject.setArabicName(arabicName);
                            subject.setName(subjectName);
                            subject.setImage(subjectIconUrl);
                            subject.setKey(key);
                            sList.add(subject);
                            adapter.notifyItemInserted(sList.size() - 1);
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
                });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
