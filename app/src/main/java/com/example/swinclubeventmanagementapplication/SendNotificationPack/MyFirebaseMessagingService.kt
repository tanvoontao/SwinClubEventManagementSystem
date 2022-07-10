package com.example.swinclubeventmanagementapplication.SendNotificationPack

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.annotation.NonNull
import androidx.core.app.NotificationCompat
import com.example.swinclubeventmanagementapplication.DisplayEventActivity
import com.example.swinclubeventmanagementapplication.MainActivity
import com.example.swinclubeventmanagementapplication.R
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFireBaseMessagingService: FirebaseMessagingService(){
    private lateinit var title:String;
    private lateinit var message:String;
    private val CHANNEL_ID = "my_channel_01"
    private val name = "my_channel"

    override fun onMessageReceived(@NonNull remoteMessage: RemoteMessage){
        super.onMessageReceived(remoteMessage)
        title = remoteMessage.getData().get("Title").toString()
        message = remoteMessage.getData().get("Message").toString()
        Log.e("notification message","Received ...");
        showNotification(title,message)
    }

    private fun showNotification(title: String, message: String) {
        val intent = Intent(this, DisplayEventActivity::class.java)
        intent.putExtra("eventTitle", title)

        // Here FLAG_ACTIVITY_CLEAR_TOP flag is set to clear the activities present in the activity stack,
        // on the top of the Activity that is to be launched
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

        // Pass the intent to PendingIntent to start the next Activity
        val pendingIntent = PendingIntent.getActivity(this,0,intent,PendingIntent.FLAG_ONE_SHOT)

        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val builder = NotificationCompat.Builder(applicationContext,CHANNEL_ID)
            .setSmallIcon(R.drawable.swinburnelogo)
            .setContentTitle(title)
            .setContentText(message)
            .setContentIntent(pendingIntent)
            .setVibrate(
                longArrayOf(
                    1000, 1000, 1000,
                    1000, 1000
                )
            )
            .setAutoCancel(true)
            .setOnlyAlertOnce(true)

        manager.notify(0, builder.build())
    }
}