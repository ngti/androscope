package nl.ngti.androscope.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.*
import android.util.Log
import android.widget.Toast
import androidx.annotation.GuardedBy
import androidx.annotation.StringRes
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import nl.ngti.androscope.AndroscopeActivity
import nl.ngti.androscope.R
import nl.ngti.androscope.server.AndroscopeHttpServer
import java.io.IOException

internal class AndroscopeService : Service() {

    // For extension functions
    private inline val context get() = this

    private val localBinder = LocalBinder()

    private val statusLiveData = MutableLiveData<AndroscopeServiceStatus>()

    private val uiThreadHandler = Handler(Looper.getMainLooper())

    @Volatile
    private lateinit var serviceLooper: Looper

    @Volatile
    private lateinit var serviceHandler: ServiceHandler

    @GuardedBy("this")
    private var serverHelper: AndroscopeServerHelper? = null

    private val notificationBuilder: NotificationCompat.Builder
        get() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = getString(R.string.androscope_channel_id)
            val channelName = getString(R.string.androscope_channel_name)
            val channel = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_MIN)
            notificationManager.createNotificationChannel(channel)
            NotificationCompat.Builder(this, channelId)
        } else {
            @Suppress("DEPRECATION")
            NotificationCompat.Builder(this)
        }

    private val notificationManager: NotificationManager
        get() = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    override fun onCreate() {
        super.onCreate()

        serviceLooper = HandlerThread(AndroscopeService::class.java.simpleName).apply {
            start()
        }.looper
        serviceHandler = ServiceHandler(serviceLooper)
    }

    override fun onBind(intent: Intent): IBinder {
        return localBinder
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent != null) {
            val msg = serviceHandler.obtainMessage()
            msg.obj = intent
            serviceHandler.sendMessage(msg)
        }
        return START_NOT_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()

        serviceLooper.quit()
        stopServer()

        uiThreadHandler.removeCallbacksAndMessages(null)
    }

    private fun onHandleIntent(intent: Intent) {
        val action = intent.action
        if (action != null) {
            when (action) {
                ACTION_START_WEB_SERVER -> handleServerStart()
                ACTION_STOP_WEB_SERVER -> handleServerStop()
            }
        }
    }

    @Synchronized
    private fun stopServer() {
        serverHelper?.stop()
    }

    @Synchronized
    private fun handleServerStart() {
        val serverHelper = serverHelper
                ?: AndroscopeServerHelper.newInstance(this, ServerStartCallback()).also {
                    serverHelper = it
                }
        serverHelper.start()
    }

    private fun handleServerStop() {
        stopServer()
        stopSelf()
    }

    private fun NotificationCompat.Builder.addRestartAction() {
        addAction(ACTION_START_WEB_SERVER,
                R.id.androscope_notification_request_code_restart,
                R.string.androscope_restart)
    }

    private fun NotificationCompat.Builder.addStopAction() {
        addAction(ACTION_STOP_WEB_SERVER,
                R.id.androscope_notification_request_code_stop_server,
                R.string.androscope_stop_server)
    }

    private fun NotificationCompat.Builder.addAction(
            serviceAction: String, requestCode: Int, @StringRes textResId: Int) {

        val intent = Intent(context, AndroscopeService::class.java).apply {
            action = serviceAction
        }
        val pendingIntent = PendingIntent.getService(context,
                requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        val action = NotificationCompat.Action.Builder(0,
                getString(textResId), pendingIntent)
                .build()
        addAction(action)
    }

    private fun NotificationCompat.Builder.showNotification() {
        setSmallIcon(R.drawable.androscope_notification_icon)
        setContentIntent(PendingIntent.getActivity(context,
                R.id.androscope_notification_request_code_open_androscope_activity,
                Intent(context, AndroscopeActivity::class.java), PendingIntent.FLAG_UPDATE_CURRENT))
        startForeground(R.id.androscope_notification_id, build())
    }

    private fun showToast(message: String) {
        uiThreadHandler.post {
            Toast.makeText(this, message, Toast.LENGTH_LONG).show()
        }
    }

    private fun performLogging(message: String, exception: Throwable? = null) {
        val tag = "Androscope"
        val delayedLogging = exception?.let {
            Runnable { Log.e(tag, message, exception) }
        } ?: Runnable { Log.d(tag, message) }

        // Logs on some devices are polluted when activity is started, here we add some delay,
        // so the IP address will be logged in the end and user will not need to scroll up.
        uiThreadHandler.postDelayed(delayedLogging, 200)
    }

    inner class LocalBinder : Binder() {
        val statusLiveData: LiveData<AndroscopeServiceStatus>
            get() = this@AndroscopeService.statusLiveData
    }

    private inner class ServiceHandler internal constructor(looper: Looper) : Handler(looper) {
        override fun handleMessage(msg: Message) {
            onHandleIntent(msg.obj as Intent)
        }
    }

    private inner class ServerStartCallback : AndroscopeServerHelper.Callback {

        override fun onStarting() {
            notificationBuilder.run {
                setContentText("Androscope: attempting to start the web server")
                showNotification()
            }

            statusLiveData.postValue(AndroscopeServiceStatus.success("Starting Androscope..."))
        }

        override fun onStarted(server: AndroscopeHttpServer) {
            notificationBuilder.run {
                setContentText("Androscope is running")
                addStopAction()
                showNotification()
            }
            showToast("Androscope was started")
            reportServerInfo(server)
        }

        override fun onAlreadyRunning(server: AndroscopeHttpServer) {
            reportServerInfo(server)
        }

        override fun onError(e: IOException) {
            notificationBuilder.run {
                setContentText("Error starting Androscope")
                addRestartAction()
                addStopAction()
                showNotification()
            }

            showToast("Error starting Androscope")
            statusLiveData.postValue(
                    AndroscopeServiceStatus.error("""
There was an error when starting Androscope:

${e.message}


Check Logcat for more details.
"""))
            performLogging("Error when starting Androscope", e)
        }

        override fun onStopped() {
            stopForeground(true)
            val message = "Androscope was stopped"
            statusLiveData.postValue(AndroscopeServiceStatus.stopped(message))
            performLogging(message)
        }

        private fun reportServerInfo(server: AndroscopeHttpServer) {
            val ip = server.ipAddress
            val port = server.listeningPort
            val message = """
Androscope is running!

Address: [ http://$ip:$port ]

Local server at [ http://127.0.0.1:$port ]

For emulator you need to forward the port on the host machine:
adb forward tcp:0 tcp:$port

After that you can access Androscope on your localhost with the port returned by adb.
"""
            statusLiveData.postValue(
                    AndroscopeServiceStatus.success("""
$message
    
You can also find this information in Logcat for “:androidscope” process of your app.
"""))
            performLogging(message)
        }
    }

    companion object {

        private const val ACTION_START_WEB_SERVER = "nl.ngti.androscope.action.START_WEB_SERVER"
        private const val ACTION_STOP_WEB_SERVER = "nl.ngti.androscope.action.STOP_WEB_SERVER"

        fun startServer(context: Context) {
            val intent = Intent(context, AndroscopeService::class.java).apply {
                action = ACTION_START_WEB_SERVER
            }
            ContextCompat.startForegroundService(context, intent)
        }
    }
}
