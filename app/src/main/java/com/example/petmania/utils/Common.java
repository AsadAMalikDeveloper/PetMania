package com.example.petmania.utils;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.example.petmania.R;
import com.example.petmania.activities.MessageActivity;
import com.example.petmania.activities.Notifications.TokenModel;
import com.example.petmania.database.datasource.FavouriteRespository;
import com.example.petmania.database.local.PetManiaRoomDatabase;
import com.example.petmania.model.Addresses;
import com.example.petmania.model.Adds;
import com.example.petmania.model.Category;
import com.example.petmania.model.Doctors;
import com.example.petmania.model.User;
import com.example.petmania.retrofit.IPetManiaAPI;
import com.example.petmania.retrofit.RetrofitClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.database.FirebaseDatabase;
import com.luseen.spacenavigation.SpaceNavigationView;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Common {
    private static final String BASE_URL = "https://petsmaniapk.000webhostapp.com/functions/";

    public static User currentUser = null;
    public static Category currentCategory = null;
    public static Addresses currentAddres = null;
    public static Adds lastUploadedAdd =null;

    public static PetManiaRoomDatabase roomDatabase;
    public static FavouriteRespository favouriteRespository;
    public static Adds SelectedAdd;
    public static User SelectedAddUser =  null;
    public static Adds EditSelectedAd = null;
    public static final String NOTI_TITLE = "title";
    public static final String NOTI_CONTENT = "content";
    private static final String TOKEN_REF = "Tokens";
    private static final String TOKEN_DR_REF = "TokensDr";
    public static SpaceNavigationView bottomNavigationView = null;
    public static String SelectedClinicName = "";
    public static String SelectedBranchCode="";
    public static int SelectedBranchId = -1;
    public static Doctors currentDoctor =null;

    public static IPetManiaAPI getAPI(){
        return RetrofitClient.getClient(BASE_URL).create(IPetManiaAPI.class);
    }

    public static final String md5(final String s) {
        final String MD5 = "MD5";
        try {
            // Create MD5 Hash
            MessageDigest digest = java.security.MessageDigest
                    .getInstance(MD5);
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();

            // Create Hex String
            StringBuilder hexString = new StringBuilder();
            for (byte aMessageDigest : messageDigest) {
                String h = Integer.toHexString(0xFF & aMessageDigest);
                while (h.length() < 2)
                    h = "0" + h;
                hexString.append(h);
            }
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }
    public static void showNotification(Context context, int id, String title, String content, Intent intent) {
        PendingIntent pendingIntent = null;
        if (intent!=null){
            pendingIntent = PendingIntent.getActivity(context,id,intent,PendingIntent.FLAG_UPDATE_CURRENT);
        }
        String notificationChannelId = "pet_mania";
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context. NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            NotificationChannel notificationChannel = new NotificationChannel(notificationChannelId,
                    "Pet Mania",NotificationManager.IMPORTANCE_DEFAULT);
            notificationChannel.setDescription("Pet Mania");
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.BLUE);
            notificationChannel.setVibrationPattern(new long[]{0,1000,500,1000});
            notificationChannel.enableVibration(true);

            notificationManager.createNotificationChannel(notificationChannel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context,notificationChannelId);
        builder.setContentTitle(title)
                .setContentText(content)
                .setAutoCancel(true)
                .setSmallIcon(R.drawable.app_icon_foreground)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(),R.drawable.app_icon_foreground));
        if (pendingIntent!=null){
            builder.setContentIntent(pendingIntent);
        }
        Notification notification = builder.build();
        notificationManager.notify(id,notification);
    }

    public static void updateToken(Context context, String myToken) {

        if (!TextUtils.isEmpty(String.valueOf(Common.currentUser.getUser_id()))){

            FirebaseDatabase.getInstance()
                    .getReference(Common.TOKEN_REF)
                    .child(String.valueOf(Common.currentUser.getUser_id()))
                    .setValue(new TokenModel(Common.currentUser.getName(),myToken))
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(context, "[TOKEN]  "+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }
    public static void updateDrToken(Context context, String myToken) {

        FirebaseDatabase.getInstance()
                .getReference(Common.TOKEN_DR_REF)
                .child(String.valueOf(Common.currentDoctor.getId()))
                .setValue(new TokenModel(Common.currentDoctor.getDr_name(),myToken))
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context, "[TOKEN]  "+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public static String createTopicOrder() {
        return new StringBuilder("/topics/new_message").toString();
    }
}
