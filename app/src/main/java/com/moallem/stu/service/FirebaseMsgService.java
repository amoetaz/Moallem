package com.moallem.stu.service;

import android.app.PendingIntent;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.widget.Toast;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.moallem.stu.R;

public class FirebaseMsgService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        String title = remoteMessage.getNotification().getTitle();
        String body = remoteMessage.getNotification().getBody();
        String clickAction = remoteMessage.getNotification().getClickAction();

        String teacherPic = remoteMessage.getData().get("teacherPic");
        String teachername = remoteMessage.getData().get("teachername");
        String teacherId = remoteMessage.getData().get("teacherId");
        String questionType = remoteMessage.getData().get("questionType");
        String nodeKey = remoteMessage.getData().get("nodeKey");
        boolean isFinished = Boolean.parseBoolean(remoteMessage.getData().get("isFinished"));
        boolean isStudentReachedZeroMins = Boolean.parseBoolean(remoteMessage.getData().get("isStudentReachedZeroMins"));
        String storageDataID = remoteMessage.getData().get("storageDataID");

        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this,
                getString(R.string.notification_channel_id))
                .setSmallIcon(R.drawable.ic_notifa)
                .setContentTitle(title)
                .setContentText(body)
                .setSound(alarmSound)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(body))
                .setPriority(NotificationCompat.PRIORITY_MAX);

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
    }
}
