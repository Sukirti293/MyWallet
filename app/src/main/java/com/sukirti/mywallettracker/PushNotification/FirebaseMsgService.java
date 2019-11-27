package com.sukirti.mywallettracker.PushNotification;

import android.app.NotificationManager;
import android.content.Context;
import android.media.RingtoneManager;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.sukirti.mywallettracker.R;

import androidx.core.app.NotificationCompat;

public class FirebaseMsgService extends FirebaseMessagingService {

    Context context=null;
    private String id ="";

    public FirebaseMsgService(){

    }

    public FirebaseMsgService(Context context){
        this.context = context;
    }


    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        if(remoteMessage.getNotification() !=null){

            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, "myWallet")
                    .setContentTitle(remoteMessage.getNotification().getTitle())
                    .setContentText(remoteMessage.getNotification().getBody())
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setStyle(new NotificationCompat.BigTextStyle())
                    .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setAutoCancel(true);

            NotificationManager notificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            notificationManager.notify(0, notificationBuilder.build());
        }

    }

}
