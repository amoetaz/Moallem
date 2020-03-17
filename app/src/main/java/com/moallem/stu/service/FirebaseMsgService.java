package com.moallem.stu.service;

import android.app.PendingIntent;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.moallem.stu.R;

public class FirebaseMsgService extends FirebaseMessagingService {


   /* @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

        if (firebaseAuth.getCurrentUser() != null) {
            databaseReference.child(USERINFO_NODE).child(firebaseAuth.getCurrentUser().getUid()).child("tokenId")
                    .setValue(s);
        }
    }
*/
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        String title = null;
        String body = null;
        String clickAction = null;
        if (remoteMessage.getNotification() != null) {
            title = remoteMessage.getNotification().getTitle();
            body = remoteMessage.getNotification().getBody();
            clickAction = remoteMessage.getNotification().getClickAction();
        }

        String teacherPic = null;
        String teachername = null;
        String teacherId = null;
        String questionType = null;
        String nodeKey = null;
        String isFinished = null;
        String isStudentReachedZeroMins = null;
        String storageDataID = null;
        if (remoteMessage.getData() != null) {
            teacherPic = remoteMessage.getData().get("teacherPic");
            teachername = remoteMessage.getData().get("teachername");
            teacherId = remoteMessage.getData().get("teacherId");
            questionType = remoteMessage.getData().get("questionType");
            nodeKey = remoteMessage.getData().get("nodeKey");
            isFinished =  remoteMessage.getData().get("isFinished");
            isStudentReachedZeroMins = remoteMessage.getData().get("isStudentReachedZeroMins");
            storageDataID = remoteMessage.getData().get("storageDataID");
        }

        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this,
                getString(R.string.notification_channel_id))
                .setSmallIcon(R.drawable.ic_notifa)
                .setContentTitle(title)
                .setContentText(body)
                .setSound(alarmSound)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(body))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        Intent intent = new Intent(clickAction);
        intent.putExtra("teacherPic",teacherPic);
        intent.putExtra("teachername",teachername);
        intent.putExtra("teacherId",teacherId);
        intent.putExtra("questionType",questionType);
        intent.putExtra("nodeKey",nodeKey);
        intent.putExtra("isFinished",isFinished);
        intent.putExtra("isStudentReachedZeroMins",isStudentReachedZeroMins);
        intent.putExtra("storageDataID",storageDataID);



        PendingIntent pendingIntent = PendingIntent.getActivity(this,0,intent
                ,PendingIntent.FLAG_UPDATE_CURRENT);

        mBuilder.setContentIntent(pendingIntent);

        int notificationId = (int)System.currentTimeMillis();

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

        notificationManager.notify(notificationId, mBuilder.build());
/*
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        manager.notify(notificationId , mBuilder.build());*/
    }
}
