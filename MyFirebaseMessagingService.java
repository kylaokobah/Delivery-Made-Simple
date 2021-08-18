package com.example.dmsimpledriver;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    FirebaseAuth mAuth;
    FirebaseFirestore db;
    String Prefs = "myPrefs";

    private static final String TAG = "MyFirebaseMsgService";
    /**
     * There are two scenarios when onNewToken is called:
     * 1) When a new token is generated on initial app startup
     * 2) Whenever an existing token is changed
     * Under #2, there are three scenarios when the existing token is changed:
     * A) App is restored to a new device
     * B) User uninstalls/reinstalls the app
     * C) User clears app data
     */
    @Override
    public void onNewToken(String token) {
        Log.d(TAG, "Refreshed token: " + token);

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // FCM registration token to your app server.
        sendRegistrationToServer(token);
    }

    /**
     * Persist token to third-party servers.
     *
     * Modify this method to associate the user's FCM registration token with any
     * server-side account maintained by your application.
     *
     * @param token The new token.
     */
    private void sendRegistrationToServer(String token) {
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null){
            // TODO: Implement this method to send token to your app server.
            Task<QuerySnapshot> user = db.collection("users").whereEqualTo("email", currentUser.getEmail()).get();
            user.addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Log.d(TAG, document.getId() + " => " + document.getData());
                            db.collection("users").document(document.getId()).update("notification_token",token);
                        }

                    } else {

                        Log.d(TAG, "Error getting documents: ", task.getException());
                    }
                }
            });
        }


    }


    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // ...

        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d(TAG, "From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());

            if (/* Check if data needs to be processed by long running job */ true) {
                // For long-running tasks (10 seconds or more) use WorkManager.
//                scheduleJob();
                if(remoteMessage.getNotification().getTitle().toString().compareToIgnoreCase("New Ride!") == 0){

//                    SharedPreferences sharedPref = getApplicationContext().getSharedPreferences(
//                            Prefs, Context.MODE_PRIVATE);

                    Intent  i = new Intent(this, Dashboard.class);
                    i.putExtra("ride_id",remoteMessage.getData().get("ride_id"));

                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(i);



//                    String title=remoteMessage.getNotification().getTitle();
//                    String message=remoteMessage.getNotification().getBody();
////                    String click_action=remoteMessage.getNotification().getClickAction();
//                    Intent intent= new Intent(this, Dashboard.class);
//                    intent.putExtra("ride_id",remoteMessage.getData().get("ride_id"));
//                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                    PendingIntent pendingIntent= PendingIntent.getActivity(this,0,intent,PendingIntent.FLAG_ONE_SHOT);
//                    NotificationCompat.Builder notificationBuilder=new NotificationCompat.Builder(this);
//                    notificationBuilder.setContentTitle(title);
//                    notificationBuilder.setContentText(message);
//                    notificationBuilder.setSmallIcon(R.mipmap.ic_launcher_round);
//                    notificationBuilder.setAutoCancel(true);
//                    notificationBuilder.setContentIntent(pendingIntent);
//                    NotificationManager notificationManager=(NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
//                    notificationManager.notify(0,notificationBuilder.build());


                }

            } else {
                // Handle message within 10 seconds
//                handleNow();
            }

        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
    }




}
