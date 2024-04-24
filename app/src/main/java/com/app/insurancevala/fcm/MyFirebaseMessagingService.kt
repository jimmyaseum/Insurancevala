package com.app.insurancevala.fcm

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.app.insurancevala.R
import com.app.insurancevala.activity.Lead.CallsDetailsActivity
import com.app.insurancevala.activity.Lead.MeetingsDetailsActivity
import com.app.insurancevala.activity.Lead.TasksDetailsActivity
import com.app.insurancevala.utils.convertDateToString
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import java.util.*

class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {

        Log.e("Remote data","==>"+remoteMessage.data)
        Log.e("Remote notification","==>"+remoteMessage.notification)

        var title = ""
        var msg = ""
        var ID = ""
        var GUID = ""
        var NotificationType = ""

        if (remoteMessage!!.getNotification() == null) {
            return
        }
        if(remoteMessage!!.getNotification() != null) {

            title = remoteMessage!!.notification!!.title.toString()
            msg = remoteMessage!!.notification!!.body.toString()
        }

        if(remoteMessage.data.isEmpty()) {
            sendNotificationFCM(title, msg,"clickAction")
        } else {
            remoteMessage.data.isNotEmpty().let {
                ID = remoteMessage.data["ID"]!!
                GUID = remoteMessage.data["GUID"]!!
                NotificationType = remoteMessage.data["NotificationType"]!!
            }
            sendNotification(title, msg,NotificationType, ID.toInt(), GUID, "clickAction")
        }
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.e("token","==>"+token)
        sendRegistrationToServer(token)
    }

    private fun sendRegistrationToServer(token: String?) {
    }

    @SuppressLint("WrongConstant")
    private fun sendNotification(title: String, messageBody: String, NotificationType:String, ID: Int, GUID: String, clickAction: String) {

        var intent: Intent? = null

       if(!NotificationType.equals("")) {
           when(NotificationType) {
               "Meeting" -> {
                   intent = Intent(this, MeetingsDetailsActivity::class.java)
                   intent.putExtra("ID",ID)
                   intent.putExtra("MeetingGUID",GUID)
                   intent.putExtra("Type", NotificationType)
               }
               "Call" -> {
                   intent = Intent(this, CallsDetailsActivity::class.java)
                   intent.putExtra("ID",ID)
                   intent.putExtra("CallGUID",GUID)
                   intent.putExtra("Type", NotificationType)
               }
               "Task" -> {
                   intent = Intent(this, TasksDetailsActivity::class.java)
                   intent.putExtra("ID",ID)
                   intent.putExtra("TaskGUID",GUID)
                   intent.putExtra("Type", NotificationType)
               }
           }
       }

        intent!!.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)

        val pendingIntent = PendingIntent.getActivity(this, 0 , intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

        var smallIcon = R.mipmap.icon
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            smallIcon = R.mipmap.icon
        } else {
            smallIcon = R.mipmap.icon
        }

        val channelId = getString(R.string.notification_channel_id)
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(smallIcon)
            .setContentTitle(title) // getString(R.string.app_name)
            .setContentText(messageBody)
            .setAutoCancel(true)
            .setSound(defaultSoundUri)
            .setContentIntent(pendingIntent)
            .setStyle(NotificationCompat.BigTextStyle().bigText(messageBody))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Channel human readable title",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }

        notificationManager.notify(createID() /* ID of notification */, notificationBuilder.build())
    }

    companion object {
        private const val TAG = "MyFirebaseMsgService"
    }

    private fun createID(): Int {
        val dateNow = Date()
        val id = convertDateToString(dateNow, "ddHHmmsss")
        Log.i(TAG, "-------messageBody - createID==>${id.toInt()}")
        return id.toInt()
    }

    private fun sendNotificationFCM(title: String, messageBody: String, clickAction: String) {

        var smallIcon = R.mipmap.icon
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            smallIcon = R.mipmap.icon
        } else {
            smallIcon = R.mipmap.icon
        }

        val channelId = getString(R.string.notification_channel_id)
//        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)


        val uri = Uri.parse("android.resource://$packageName/raw/noti2")
        val defaults = Notification.DEFAULT_LIGHTS or Notification.DEFAULT_VIBRATE

        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(smallIcon)
            .setContentTitle(title) // getString(R.string.app_name)
            .setContentText(messageBody)
            .setAutoCancel(true)
            .setSound(uri, AudioManager.STREAM_NOTIFICATION)
            .setDefaults(defaults)
            .setContentIntent(null)
            .setStyle(NotificationCompat.BigTextStyle().bigText(messageBody))
            .setPriority(NotificationCompat.PRIORITY_LOW)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Channel human readable title",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }

        notificationManager.notify(createID() /* ID of notification */, notificationBuilder.build())
    }

    override fun handleIntent(intent: Intent) {
        try {
            if (intent.extras != null) {
                val builder = RemoteMessage.Builder("MessagingService")
                for (key in intent.extras!!.keySet()) {
                    builder.addData(key!!, intent.extras!![key].toString())
                }
                onMessageReceived(builder.build())
            } else {
                super.handleIntent(intent)
            }
        } catch (e: Exception) {
            super.handleIntent(intent)
        }
    }
}
