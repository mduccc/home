package com.indieteam.home.modules

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.support.v4.app.NotificationCompat
import android.support.v4.app.NotificationManagerCompat
import com.indieteam.home.activity.MainActivity
import com.example.duc25.activity.R

class HomeNotification(val context: Context) {

    fun createNotificationChannel(){
        if(Build.VERSION.SDK_INT >= 26){
            val name  = "home"
            val description = "description"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val chanel = NotificationChannel("home", name, importance)
            chanel.description = description
            val notificationManager = context.getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(chanel)
        }
    }

    fun notification(data: String){
        //touch
        val intent = Intent(context, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        val pendingIntent = PendingIntent.getActivity(context, 0, intent, 0)

        //build notication
        val mBuilder = NotificationCompat.Builder(context, "home")
                .setSmallIcon(R.drawable.ic_app_ico)
                //.setContentTitle("Trạng thái")
                //.setContentText(data)
                .setStyle(NotificationCompat.BigTextStyle().bigText(data))
                .setColor(Color.parseColor("#fdc51162"))
                .setPriority(NotificationCompat.DEFAULT_VIBRATE)
                .setContentIntent(pendingIntent)
                .setShowWhen(true)
                .setOngoing(true)

        NotificationManagerCompat.from(context)
                .notify(1, mBuilder.build())
    }
}