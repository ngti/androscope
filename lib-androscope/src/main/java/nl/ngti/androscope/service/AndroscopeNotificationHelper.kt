package nl.ngti.androscope.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Handler
import android.os.SystemClock
import androidx.core.app.NotificationCompat
import nl.ngti.androscope.AndroscopeActivity
import nl.ngti.androscope.R

internal class AndroscopeNotificationHelper(
        private val service: Service,
        private val handler: Handler
) {

    private val notificationManager: NotificationManager
        get() = service.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    private val showPendingNotification = Runnable {
        pendingNotification?.run {
            showNotificationNow(this)
        }
    }

    private var lastNotificationShowTime: Long = 0
    private var pendingNotification: NotificationCompat.Builder? = null

    fun showNotification(setupBlock: NotificationCompat.Builder.() -> Unit) {
        val notificationBuilder = newNotificationBuilder()
        setupBlock(notificationBuilder)
        notificationBuilder.run {
            setSmallIcon(R.drawable.androscope_notification_icon)
            setContentIntent(PendingIntent.getActivity(service,
                    R.id.androscope_notification_request_code_open_androscope_activity,
                    Intent(service, AndroscopeActivity::class.java), PendingIntent.FLAG_UPDATE_CURRENT))
        }

        handler.post {
            queueNotification(notificationBuilder)
        }
    }

    private fun newNotificationBuilder(): NotificationCompat.Builder {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = service.getString(R.string.androscope_channel_id)
            val channelName = service.getString(R.string.androscope_channel_name)
            val channel = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_MIN)
            notificationManager.createNotificationChannel(channel)
            NotificationCompat.Builder(service, channelId)
        } else {
            @Suppress("DEPRECATION")
            NotificationCompat.Builder(service)
        }
    }

    private fun queueNotification(builder: NotificationCompat.Builder) {
        val timeTillNextNotification = NOTIFICATION_THROTTLE - (currentTime - lastNotificationShowTime)
        handler.removeCallbacks(showPendingNotification)
        pendingNotification = builder
        if (timeTillNextNotification <= 0) {
            handler.post(showPendingNotification)
        } else {
            handler.postDelayed(showPendingNotification, timeTillNextNotification)
        }
    }

    private fun showNotificationNow(builder: NotificationCompat.Builder) {
        service.startForeground(R.id.androscope_notification_id, builder.build())
        lastNotificationShowTime = currentTime
    }

    private val currentTime get() = SystemClock.elapsedRealtime()

    companion object {
        /**
         * Minimum interval between notifications
         */
        private const val NOTIFICATION_THROTTLE = 1000L
    }
}
