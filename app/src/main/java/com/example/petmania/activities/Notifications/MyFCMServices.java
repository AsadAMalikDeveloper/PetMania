package com.example.petmania.activities.Notifications;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.petmania.activities.MessageActivity;
import com.example.petmania.utils.Common;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;
import java.util.Random;

public class MyFCMServices extends FirebaseMessagingService {

    private Intent intent;
    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Map<String,String> dataRecv = remoteMessage.getData();
        int user_id = Integer.parseInt(dataRecv.get("user_id"));
        int ad_id = Integer.parseInt(dataRecv.get("ad_id"));

        SharedPreferences sharedPreferences = getSharedPreferences("PREFS",MODE_PRIVATE);
        String chatUser = sharedPreferences.getString("currentChatUser","none");


        intent = new Intent(getApplicationContext(),MessageActivity.class);
        intent.putExtra("user_id",user_id);
        intent.putExtra("ad_id",ad_id);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        if (dataRecv!=null){
            if (!chatUser.equals(String.valueOf(user_id))) {
                Common.showNotification(this, new Random().nextInt(),
                        dataRecv.get(Common.NOTI_TITLE),
                        dataRecv.get(Common.NOTI_CONTENT),
                        intent);
            }
        }
    }

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
        Common.updateToken(this,s);

    }
}
