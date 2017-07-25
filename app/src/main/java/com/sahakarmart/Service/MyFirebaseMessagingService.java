package com.sahakarmart.Service;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.sahakarmart.Activity.HomeActivity;
import com.sahakarmart.Activity.Notification;
import com.sahakarmart.Activity.Registration;
import com.sahakarmart.Database.DBManager;
import com.sahakarmart.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

import static com.sahakarmart.Service.Utils.Register_Preference;
import static com.sahakarmart.Service.Utils.UserRegister;


public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = MyFirebaseMessagingService.class.getSimpleName();

    private NotificationUtils notificationUtils;
    private DBManager dbManager;
    public static final int NOTIFICATION_ID = 1;
    private SharedPreferences RegisterPrefences;




    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        if (remoteMessage == null)
            return;

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null)
        {

            System.out.println("Notification " + remoteMessage.getNotification().getBody());
            Log.e(TAG, "Notification Body: " + remoteMessage.getNotification().getBody());
            handleNotification(remoteMessage.getNotification().getBody());

        }

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.e(TAG, "Data Payload: " + remoteMessage.getData().toString());

            try {
                JSONObject json = new JSONObject(remoteMessage.getData());
                Log.e(TAG,"Json :"+json);
                handleDataMessage(json);
            } catch (Exception e) {
                Log.e(TAG, "Exception: " + e.getMessage());
            }
        }
    }

    private void handleNotification(String message) {
        if (!NotificationUtils.isAppIsInBackground(getApplicationContext())) {
            // app is in foreground, broadcast the push message
            Intent pushNotification = new Intent(Utils.PUSH_NOTIFICATION);
            pushNotification.putExtra("message", message);
            LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);

            // play notification sound
            NotificationUtils notificationUtils = new NotificationUtils(getApplicationContext());
            notificationUtils.playNotificationSound();
        } else {
            // If the app is in background, firebase itself handles the notification
        }
    }

    private void handleDataMessage(JSONObject json) {
        Log.e(TAG, "push json: " + json.toString());

        try {


            Log.e(TAG,"Message :"+json.getString("message"));
            String message = json.getString("message");
            String url = json.getString("url");
          //  String url = "";

            dbManager = new DBManager(this);
            dbManager.open();

//            dbManager.insert(message, null, null, null);
            //   Log.e(TAG, "title: " + title);
            Log.e(TAG, "message: " + message);
            Log.e(TAG, "url :" + url);



            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy HH:mm a");
            Date date = new Date();
            String when = dateFormat.format(date);


            dbManager.insert(message, when, url);

            sendNotification(message, url);



            System.out.println("Url in Not Null" + url);


               dbManager.close();

        } catch (JSONException e) {
            Log.e(TAG, "Json Exception: " + e.getMessage());
        } catch (Exception e) {
            Log.e(TAG, "Exception: " + e.getMessage());
        }
    }


    private void sendNotification(String msg, String url) {

        RegisterPrefences = getSharedPreferences(Register_Preference, Context.MODE_PRIVATE);


        boolean isUserRegister = RegisterPrefences.getBoolean(UserRegister, false);


        if (isUserRegister == true)
        {

            Intent resultIntent = new Intent(getApplicationContext(), Notification.class);
            /*resultIntent.putExtra("call","notification");
            resultIntent.putExtra("msg", msg);
            resultIntent.putExtra("url", url);*/

            PendingIntent resultPendingIntent = PendingIntent.getActivity(this, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            NotificationCompat.Builder mNotifyBuilder;
            NotificationManager mNotificationManager;

            mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            mNotifyBuilder = new NotificationCompat.Builder(this)
                    .setContentTitle("Sahakar Mart")
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(msg))
                    .setWhen(System.currentTimeMillis()) //
                    .setLights(0xffffffff, 0, 0)
                    .setSmallIcon(R.mipmap.ic_launcher);

            Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            mNotifyBuilder.setSound(alarmSound);
            long[] pattern = {200, 200};
            mNotifyBuilder.setVibrate(pattern);

            mNotifyBuilder.setContentIntent(resultPendingIntent);

            mNotifyBuilder.setContentText(msg);

            // Set autocancel
            mNotifyBuilder.setAutoCancel(true);
            // Post a notification
            mNotificationManager.notify(NOTIFICATION_ID, mNotifyBuilder.build());

        }

        else
        {
            Intent resultIntent = new Intent(getApplicationContext(), Registration.class);
            /*resultIntent.putExtra("msg", msg);
            resultIntent.putExtra("url", url);
*/
            PendingIntent resultPendingIntent = PendingIntent.getActivity(this, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            NotificationCompat.Builder mNotifyBuilder;
            NotificationManager mNotificationManager;

            mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            mNotifyBuilder = new NotificationCompat.Builder(this)
                    .setContentTitle("Sahakar Mart")
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(msg))
                    .setWhen(System.currentTimeMillis()) //
                    .setLights(0xffffffff, 0, 0)
                    .setSmallIcon(R.mipmap.ic_launcher);

            Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            mNotifyBuilder.setSound(alarmSound);
            long[] pattern = {200, 200};
            mNotifyBuilder.setVibrate(pattern);

            mNotifyBuilder.setContentIntent(resultPendingIntent);

            mNotifyBuilder.setContentText(msg);

            // Set autocancel
            mNotifyBuilder.setAutoCancel(true);
            // Post a notification
            mNotificationManager.notify(NOTIFICATION_ID, mNotifyBuilder.build());

        }

    }
}

