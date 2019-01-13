package com.moallem.stu.ui.fragments;


import android.Manifest;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.moallem.stu.R;
import com.moallem.stu.adapters.ChatAdapter;
import com.moallem.stu.models.Message;
import com.moallem.stu.models.Session;
import com.moallem.stu.utilities.RandomString;
import com.moallem.stu.utilities.Utils;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

import static com.moallem.stu.utilities.FirebaseConstants.ISFINISHED_NODE;
import static com.moallem.stu.utilities.FirebaseConstants.ISSTUDENTREACHERZERO_NODE;
import static com.moallem.stu.utilities.FirebaseConstants.MESSAGES_INFO;
import static com.moallem.stu.utilities.FirebaseConstants.MONEYBALANCE_NODE;
import static com.moallem.stu.utilities.FirebaseConstants.QUESTIONIDS_NODES;
import static com.moallem.stu.utilities.FirebaseConstants.SUBJECTS_NODE;
import static com.moallem.stu.utilities.FirebaseConstants.USERINFO_NODE;

/**
 * A simple {@link Fragment} subclass.
 */
public class ChattingFragment extends Fragment {

    private static final String TAG = "ChattingFragmentclass";
    @BindView(R.id.speakorder)
    TextView speakOrder;
    @BindView(R.id.timer)
    TextView timer;
    @BindView(R.id.session_end_text)
    TextView sessionEndedText;
    @BindView(R.id.msg_text)
    EditText msText;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.chat_list_recylerview)
    RecyclerView rvChatList;
    @BindView(R.id.send_photo)
    ImageView sendPhoto;
    @BindView(R.id.send_audio)
    ImageView sendAudio;
    @BindView(R.id.send_data)
    ImageView sendData;
    @BindView(R.id.teacher_availability_textview)
    TextView teacherAvaibility;
    private Session session;
    private ArrayList<Message> messages = new ArrayList<>();
    private ChatAdapter adapter;
    private DatabaseReference reference;
    private StorageReference mStorage;
    private LinearLayoutManager linearLayoutManager;
    private CountDownTimer countDownTimer;
    private CountDownTimer chatTimer ;
    public static long chatLength = 0;
    private MediaRecorder mRecorder;
    private String mFileName = null;
    private ValueEventListener valueEventListener;
    // Requesting permission to RECORD_AUDIO
    private boolean permissionToRecordAccepted = false;
    private String [] permissions = {Manifest.permission.RECORD_AUDIO,Manifest.permission.WRITE_EXTERNAL_STORAGE};
    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;
    private String fileNameEndPoint;
    private ValueEventListener teacherStatusEventListener;
    private final long maxNumber = 3000000;


    public ChattingFragment() {
        // Required empty public constructor
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case REQUEST_RECORD_AUDIO_PERMISSION:
                for (int grandresult :grantResults){
                    permissionToRecordAccepted  = grandresult == PackageManager.PERMISSION_GRANTED;
                    if (!permissionToRecordAccepted){
                        break;
                    }
                }

                break;
        }

        if (!permissionToRecordAccepted){
            sendAudio.setOnTouchListener(null);
            sendAudio.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(getActivity(), "permissions denied", Toast.LENGTH_SHORT).show();
                }
            });
        }


    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //TODO : setHasOptionsMenu(true);
        reference = FirebaseDatabase.getInstance().getReference();
        mStorage = FirebaseStorage.getInstance().getReference();
        fileNameEndPoint = new RandomString(12,new Random()).nextString();
        session = getActivity().getIntent().getParcelableExtra("session_extra");
        mFileName = Environment.getExternalStorageDirectory().getAbsolutePath();
        mFileName += "/"+fileNameEndPoint+".3gp";
        requestPermissions( permissions, REQUEST_RECORD_AUDIO_PERMISSION);

    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_chatting, container, false);
        ButterKnife.bind(this, view);
        linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setStackFromEnd(true);
        rvChatList.setLayoutManager(linearLayoutManager);
        CircleImageView imageTeacher = toolbar.findViewById(R.id.user_image1_photo);
        TextView imageName = toolbar.findViewById(R.id.teacher_name_textview);
        Glide.with(getContext()).load(session.getTeacherPic()).into(imageTeacher);
        imageName.setText(session.getTeacherName());
        checkIFSessionEdned();
        checkIfStudentReachedZeroMins();
        listenToTeacherStatus();

        toolbar.findViewById(R.id.end_session_textview).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (((TextView)v).getText().equals(getString(R.string.end_session_text))){
                    showConfirmDialog();
                }else {
                    if (getActivity() != null) {
                        getActivity().finish();
                    }
                }

            }
        });

        initList();

        sendAudio.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                long downTime = event.getEventTime()-event.getDownTime();

                if (event.getAction() == MotionEvent.ACTION_DOWN){
                    hideViews(sendPhoto,msText,sendData);
                    showViews(timer,speakOrder);
                    startTimer();
                    startRecording();
                }else if(event.getAction() == MotionEvent.ACTION_UP) {
                    showViews(sendPhoto,msText,sendData);
                    hideViews(timer,speakOrder);
                    stopTimer();
                    stopRecording();

                    if (downTime > 1500){
                        uploadAudio();
                        Toast.makeText(getContext(), "sending ...", Toast.LENGTH_SHORT).show();
                    } else
                        Toast.makeText(getContext(), "Press mic icon and release when finish", Toast.LENGTH_SHORT).show();
                }
                return false;
            }
        });

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        listenToSessionEnded();
        listenToIfStudentReachedZeroMins();
    }


    private void showConfirmDialog(){
        if (getActivity() != null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("Are you sure to end this session?");
            builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    reference.child("subjects").child(session.getQuestionType().toLowerCase()).child(QUESTIONIDS_NODES)
                            .child(session.getKey()).child(ISFINISHED_NODE)
                            .setValue(true);
                    hideViews(sendPhoto,msText,sendData,timer,speakOrder,sendAudio);
                    showViews(sessionEndedText);
                    stopChatTimer();
                }
            });
            builder.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    // User cancelled the dialog
                }
            });

            AlertDialog dialog = builder.create();
            dialog.show();
        }
    }

    private void listenToSessionEnded() {
        valueEventListener = reference.child("subjects").child(session.getQuestionType().toLowerCase())
                .child(QUESTIONIDS_NODES)
                .child(session.getKey()).child(ISFINISHED_NODE)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Boolean isFinished = dataSnapshot.getValue(Boolean.class);
                        if (isFinished != null && isFinished) {
                            session.setFinished(true);
                            ((TextView) toolbar.findViewById(R.id.end_session_textview)).setText(R.string.exit_session_text);
                            hideViews(sendPhoto, msText, sendData, timer, speakOrder, sendAudio);
                            showViews(sessionEndedText);
                            stopChatTimer();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    private void listenToIfStudentReachedZeroMins() {
        valueEventListener = reference.child("subjects").child(session.getQuestionType().toLowerCase())
                .child(QUESTIONIDS_NODES)
                .child(session.getKey()).child(ISSTUDENTREACHERZERO_NODE)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Boolean isReached = dataSnapshot.getValue(Boolean.class);

                        if (isReached != null && isReached) {
                            session.setStudentReachedZeroMins(true);
                            ((TextView) toolbar.findViewById(R.id.end_session_textview))
                                    .setText(R.string.exit_session_text);
                            hideViews(sendPhoto, msText, sendData, timer, speakOrder, sendAudio);
                            showViews(sessionEndedText);
                            stopChatTimer();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    private void checkIFSessionEdned() {
        if (session.getFinished()){
            ((TextView)toolbar.findViewById(R.id.end_session_textview)).setText(R.string.exit_session_text);
            hideViews(sendPhoto,msText,sendData,timer,speakOrder,sendAudio);
            showViews(sessionEndedText);
        }
    }

    private void checkIfStudentReachedZeroMins() {
        if (session.getStudentReachedZeroMins()){
            ((TextView)toolbar.findViewById(R.id.end_session_textview)).setText(R.string.exit_session_text);
            hideViews(sendPhoto,msText,sendData,timer,speakOrder,sendAudio);
            showViews(sessionEndedText);
        }
    }

    private void hideViews(View ...views){
        for (View view : views){
            view.setVisibility(View.GONE);
        }
    }

    private void showViews(View ...views){
        for (View view : views){
            view.setVisibility(View.VISIBLE);
        }
    }

    private void startTimer(){
        countDownTimer = new CountDownTimer(maxNumber,1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timer.setText(counvertToTime((maxNumber-millisUntilFinished)/1000));
            }

            @Override
            public void onFinish() {

            }
        };

        countDownTimer.start();
    }

    private void stopTimer(){
        if (countDownTimer != null){
            countDownTimer.cancel();
            countDownTimer = null;
        }
    }

    private void startChatTimer(){
        if (chatTimer == null) {
            chatTimer = new CountDownTimer(300000,1000) {
                @Override
                public void onTick(long millisUntilFinished) {

                    if (chatLength % 60 == 0){
                        changeTeahcerMoneyBalance();
                        changeStudentTimeBalance();

                    }
                    chatLength++;
                    Log.d(TAG, "onTick: "+chatLength);
                }

                @Override
                public void onFinish() {

                }
            };

            chatTimer.start();
        }
    }

    private void changeStudentTimeBalance() {

        reference.child(USERINFO_NODE).child(Utils.getCurrentUserId()).child("timeBalance")
                .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    Double balance = dataSnapshot.getValue(Double.class);
                    if (balance != null && balance >= 60) {
                        balance -= 60;
                        if (balance < 60){
                            reference.child("subjects").child(session.getQuestionType().toLowerCase())
                                    .child(QUESTIONIDS_NODES)
                                    .child(session.getKey()).child(ISSTUDENTREACHERZERO_NODE).setValue(true);
                        }
                        reference.child(USERINFO_NODE).child(Utils.getCurrentUserId()).child("timeBalance").setValue(balance);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void changeTeahcerMoneyBalance() {
        reference.child("teachers").child(session.getTeacherId())
                .child(MONEYBALANCE_NODE)
                .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    Double balance = dataSnapshot.getValue(Double.class);
                    balance += 1.00;
                    reference.child("teachers").child(session.getTeacherId()).child("moneyBalance").setValue(balance);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void stopChatTimer(){
        if (chatTimer != null){
            chatTimer.cancel();
            chatTimer = null;
        }
    }

    private void initList() {

        adapter = new ChatAdapter(getContext(), messages,session);
        rvChatList.setAdapter(adapter);

        reference.child(SUBJECTS_NODE).child(session.getQuestionType().toLowerCase()).child(QUESTIONIDS_NODES)
                .child(session.getKey()).child(MESSAGES_INFO)
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, String s) {
                        Message message = new Message();

                        String msg = dataSnapshot.child("msg").getValue(String.class);
                        String msgType = dataSnapshot.child("msgType").getValue(String.class);
                        String senderId = dataSnapshot.child("senderId").getValue(String.class);
                        String senderType = dataSnapshot.child("senderType").getValue(String.class);

                        message.setMsg(msg);
                        message.setMsgType(msgType);
                        message.setSenderId(senderId);
                        message.setSenderType(senderType);

                        messages.add(message);

                        adapter.notifyItemInserted(messages.size() - 1);
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


    @OnClick({R.id.send_photo, R.id.send_audio, R.id.send_data})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.send_photo:takePhoto();
                break;
            case R.id.send_audio:
                break;
            case R.id.send_data:
                sendText();
                break;
        }
    }

    private void sendText() {

        if (!TextUtils.isEmpty(msText.getText().toString())) {
            DatabaseReference messageChild = reference.child(SUBJECTS_NODE)
                    .child(session.getQuestionType().toLowerCase())
                    .child(QUESTIONIDS_NODES).child(session.getKey())
                    .child(MESSAGES_INFO).push();

            Map<String ,String> newMessage = new HashMap<>();
            newMessage.put("msg",msText.getText().toString());
            newMessage.put("msgType","text");
            newMessage.put("senderId",Utils.getCurrentUserId());
            newMessage.put("senderType",Utils.getUserType(getContext()));

            messageChild.setValue(newMessage).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                     linearLayoutManager.scrollToPosition(messages.size()-1);
                     msText.setText("");

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getContext(), "Failed", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    public void takePhoto() {
        if (getContext() != null) {
            CropImage.activity()
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setActivityTitle(getString(R.string.edit_your_image_text))
                    .setCropShape(CropImageView.CropShape.RECTANGLE)
                    .setCropMenuCropButtonTitle("Done")
                    .setRequestedSize(400, 400)
                    .setCropMenuCropButtonIcon(R.drawable.ic_done)
                    .start(getContext(),this);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        // handle result of CropImageActivity
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == getActivity().RESULT_OK) {
                uploadImage(result.getUri());
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Toast.makeText(getActivity(), "Cropping failed Please try again ", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void uploadImage(Uri imageUri) {
        String currentTime = Utils.getStringCurrentTime();

        DatabaseReference messageChild = reference.child(SUBJECTS_NODE)
                .child(session.getQuestionType().toLowerCase())
                .child(QUESTIONIDS_NODES).child(session.getKey())
                .child(MESSAGES_INFO).push();

        final StorageReference filepath = mStorage.child("images/chatImage/"+session.getStorageDataID()
                +"/"+imageUri.getLastPathSegment());
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
                        Map<String ,String> newMessage = new HashMap<>();
                        newMessage.put("msg",downloadUri.toString());
                        newMessage.put("msgType","photo");
                        newMessage.put("senderId",Utils.getCurrentUserId());
                        newMessage.put("senderType",Utils.getUserType(getContext()));

                        messageChild.setValue(newMessage).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                linearLayoutManager.scrollToPosition(messages.size()-1);

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                }
            }
        });

    }

    private String counvertToTime(long number){
        long mod = number % 60;
        int min =(int) (number/60);

        if (String.valueOf(min).length() < 2){
            if (String.valueOf(mod).length() < 2){
                 return "0"+min+":"+"0"+mod;
            }else {
                return "0"+min+":"+mod;
            }
        }else {
            if (String.valueOf(mod).length() < 2){
                return min+":"+"0"+mod;
            }else {
                return min+":"+mod;
            }
        }
    }


    private void startRecording() {
        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mRecorder.setOutputFile(mFileName);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);

        try {
            mRecorder.prepare();
        } catch (IOException e) { }

        mRecorder.start();

    }

    private void stopRecording() {
        if (mRecorder != null) {
            try {
                mRecorder.stop();
            } catch (Exception e) {
                e.printStackTrace();
            }
            mRecorder.release();
            mRecorder = null;
        }

    }

    private void uploadAudio() {

        String audioId = new RandomString(12,new Random()).nextString();

        DatabaseReference messageChild = reference.child(SUBJECTS_NODE)
                .child(session.getQuestionType().toLowerCase())
                .child(QUESTIONIDS_NODES).child(session.getKey())
                .child(MESSAGES_INFO).push();

        final StorageReference filepath = mStorage.child("audio/chatAudio/"+session.getStorageDataID()
                +"/"+audioId+".3gp");

        Uri audUri = Uri.fromFile(new File(mFileName));


        UploadTask uploadTask = filepath.putFile(audUri);

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
                        Map<String ,String> newMessage = new HashMap<>();
                        newMessage.put("msg",downloadUri.toString());
                        newMessage.put("msgType","audio");
                        newMessage.put("senderId",Utils.getCurrentUserId());
                        newMessage.put("senderType",Utils.getUserType(getContext()));

                        messageChild.setValue(newMessage).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                linearLayoutManager.scrollToPosition(messages.size()-1);
                                deleteLocalFileAudio();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getContext(), "Failed", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                }
            }
        });
    }

    private void deleteLocalFileAudio(){
        File file = new File(mFileName);
        if (file.exists()) {
            boolean delete = file.delete();
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (valueEventListener != null) {
            reference.child("subjects").child(session.getQuestionType().toLowerCase())
                    .child(QUESTIONIDS_NODES)
                    .child(session.getKey()).child(ISFINISHED_NODE)
                    .removeEventListener(valueEventListener);
        }
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.chat_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();

        if(itemId == R.id.report)
        {
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onPause() {
        super.onPause();
        reference.child("subjects").child(session.getQuestionType().toLowerCase()).child(QUESTIONIDS_NODES)
                .child(session.getKey()).child("isStudentOnline").setValue(false);
        session.setStudentOnline(false);
        stopChatTimer();
    }

    @Override
    public void onResume() {
        super.onResume();
        reference.child("subjects").child(session.getQuestionType().toLowerCase()).child(QUESTIONIDS_NODES)
                .child(session.getKey()).child("isStudentOnline").setValue(true);
        session.setStudentOnline(true);

        if (!session.getFinished() && !session.getStudentReachedZeroMins()) {
            reference.child("subjects").child(session.getQuestionType().toLowerCase()).child(QUESTIONIDS_NODES)
                    .child(session.getKey()).child("isTeacherOnline")
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Boolean isTeacherOnline = dataSnapshot.getValue(Boolean.class);
                    if (isTeacherOnline != null && isTeacherOnline) {
                        startChatTimer();
                    } else {
                        stopChatTimer();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

    }

    private void listenToTeacherStatus(){
        teacherStatusEventListener = reference.child("subjects").child(session.getQuestionType().toLowerCase()).child(QUESTIONIDS_NODES)
                .child(session.getKey()).child("isTeacherOnline").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Boolean isTeacherOnline = dataSnapshot.getValue(Boolean.class);
                        Log.d(TAG, "onDataChange: " + "isTeacherOnline "+isTeacherOnline);
                        if (isTeacherOnline != null && isTeacherOnline && session.getStudentOnline()) {
                            session.setTeacherOnline(true);
                            teacherAvaibility.setText("Online");
                            startChatTimer();
                        } else {
                            session.setTeacherOnline(false);
                            teacherAvaibility.setText("Offline");
                            stopChatTimer();
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
        if (teacherStatusEventListener != null){
            reference.child("subjects").child(session.getQuestionType().toLowerCase()).child(QUESTIONIDS_NODES)
                    .child(session.getKey()).child("isTeacherOnline").removeEventListener(teacherStatusEventListener);
        }

    }
}
