package com.moallem.stu.ui.fragments;


import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.moallem.stu.R;
import com.moallem.stu.models.Subject;
import com.moallem.stu.utilities.RandomString;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static com.moallem.stu.utilities.FirebaseConstants.DATE_NODE;
import static com.moallem.stu.utilities.FirebaseConstants.ISFINISHED_NODE;
import static com.moallem.stu.utilities.FirebaseConstants.ISREPLYED_NODE;

/**
 * A simple {@link Fragment} subclass.
 */
public class SendingQuestionFragment extends Fragment {

    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    private Uri imageUri;
    private ImageView imageView;
    private Button done;
    private EditText commnetFeild;
    private Toolbar toolbar;
    private DatabaseReference mDatabase;
    private StorageReference mStorage;
    private FirebaseAuth firebaseAuth;
    private Subject subject;
    Unbinder unbinder;
    private String storageDataID;

    public SendingQuestionFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        imageUri = Uri.parse(getArguments().getString("imageUri"));
        subject = getActivity().getIntent().getParcelableExtra("subjectInfo");
        storageDataID = new RandomString(18,new Random()).nextString();
        initFirebaseInstances();
    }

    private void initFirebaseInstances() {
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mStorage = FirebaseStorage.getInstance().getReference();
        firebaseAuth = FirebaseAuth.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_sending_question, container, false);
        unbinder = ButterKnife.bind(this, view);
        imageView = view.findViewById(R.id.question_image);
        commnetFeild = view.findViewById(R.id.edittext_image_comment);
        toolbar = view.findViewById(R.id.toolbar);
        done = view.findViewById(R.id.buSendImage);
        imageView.setImageURI(imageUri);
        if (getActivity() != null) {
            ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);

        }

        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.INVISIBLE);
                hideKeyboard();
                toolbar.setVisibility(View.VISIBLE);
                commnetFeild.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_comment, 0, 0, 0);
                imageView.setVisibility(View.VISIBLE);
                done.setVisibility(View.GONE);
            }
        });

        toolbar.findViewById(R.id.crop_appbar_Connect).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                initFirebaseNodes();

            }
        });

        checkifEdittextisFocused();
        return view;
    }

    private void initFirebaseNodes() {
        String uid = firebaseAuth.getCurrentUser().getUid();
        DatabaseReference node = mDatabase.child("subjects").child(subject.getKey()).child("questionids").push();
        uploadImage(uid,node);

    }



    private void uploadImage(String uid, DatabaseReference node) {
        final StorageReference filepath = mStorage.child("images/chatImage/"+storageDataID+"/"+imageUri.getLastPathSegment());
        UploadTask uploadTask = filepath.putFile(imageUri);

        uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (task.getException() != null && !task.isSuccessful()) {
                    throw task.getException();
                }
                return filepath.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    Uri downloadUri = task.getResult();
                    if (downloadUri != null) {

                        Map<String, Object> newSession = new HashMap<>();
                        newSession.put("questionType", subject.getName());
                        newSession.put("firstPic", downloadUri.toString());
                        newSession.put(ISREPLYED_NODE, false);
                        newSession.put("studentId", uid);
                        newSession.put("studentName", firebaseAuth.getCurrentUser().getDisplayName());
                        newSession.put("firstComment", commnetFeild.getText().toString());
                        newSession.put("storageDataID",storageDataID);
                        newSession.put(ISFINISHED_NODE,false);
                        newSession.put(DATE_NODE,getCurrentDate());
                        newSession.put("nodeKey",node.getKey());

                        node.setValue(newSession);
                        progressBar.setVisibility(View.INVISIBLE);
                        Toast.makeText(getActivity(), R.string.question_posted, Toast.LENGTH_SHORT).show();

                        WaitingATeacherFragment waitingATeacherFragment = new WaitingATeacherFragment();
                        Bundle bundle = new Bundle();
                        bundle.putParcelable("subjectInfo",subject);
                        bundle.putString("questionId",node.getKey());
                        bundle.putString("storageDataID",storageDataID);
                        bundle.putString("firstPicUrl",downloadUri.toString());
                        waitingATeacherFragment.setArguments(bundle);
                        getActivity().getSupportFragmentManager()
                                .beginTransaction().replace(R.id.fpostquestion, waitingATeacherFragment)
                                .commit();

                    }

                }
            }
        });

    }

    private void checkifEdittextisFocused() {
        commnetFeild.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.GONE);
                toolbar.setVisibility(View.GONE);
                commnetFeild.setCompoundDrawablesWithIntrinsicBounds(0,0,0,0);
                imageView.setVisibility(View.GONE);
                done.setVisibility(View.VISIBLE);
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

    public String getCurrentDate() {
        SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
        Date now = new Date();
        return sdfDate.format(now);

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }


}
