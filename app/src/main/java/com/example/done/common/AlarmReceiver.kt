package com.example.done.common

import android.app.NotificationManager
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import androidx.core.app.NotificationCompat
import com.example.done.R
import com.example.done.data.task.Task
import com.example.done.feature.task.detail.TaskDetailActivity
import kotlin.random.Random

class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {

        val task = intent?.getBundleExtra(EXTRA_KEY_DATA)?.getParcelable<Task>(EXTRA_KEY_DATA)
        if (task?.deadlineUtc == "1") {
            val i = Intent(context, TaskDetailActivity::class.java).apply {
                putExtra(EXTRA_KEY_DATA, task)
            }
            i.flags = FLAG_ACTIVITY_NEW_TASK or FLAG_ACTIVITY_NEW_TASK
            val pendingIntent = PendingIntent.getActivity(context, 0, i, FLAG_UPDATE_CURRENT)
            val notification = NotificationCompat.Builder(context!!, "doneChannel")
                .setSmallIcon(R.drawable.ic_outline_check_circle_24)
                .setContentTitle("یادآوری")
                .setContentText(task?.title)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .build()

            val notificationManager =
                context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.notify(Random.nextInt(), notification)
        }
    }
}