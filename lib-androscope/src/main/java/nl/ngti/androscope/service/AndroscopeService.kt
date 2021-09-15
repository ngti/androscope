package nl.ngti.androscope.service

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.*
import androidx.annotation.GuardedBy
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

internal class AndroscopeService : Service() {

    private val localBinder = LocalBinder()

    private val statusLiveData = MutableLiveData<AndroscopeServiceStatus>()

    private val uiThreadHandler = Handler(Looper.getMainLooper())

    @Volatile
    private lateinit var serviceLooper: Looper

    @Volatile
    private lateinit var serviceHandler: ServiceHandler

    @GuardedBy("this")
    private var serverHelper: AndroscopeServerHelper? = null

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

        serviceHandler.removeCallbacksAndMessages(null)
        serviceLooper.quit()
        stopServer()

        uiThreadHandler.removeCallbacksAndMessages(null)
    }

    private fun onHandleIntent(intent: Intent) {
        val action = intent.action ?: return
        when (action) {
            ACTION_START_WEB_SERVER -> handleServerStart()
            ACTION_STOP_WEB_SERVER -> handleServerStop()
        }
    }

    @Synchronized
    private fun stopServer() {
        serverHelper?.stop()
    }

    @Synchronized
    private fun handleServerStart() {
        val serverHelper = serverHelper
                ?: AndroscopeServerHelper(
                        this,
                        AndroscopeServerCallback(this, uiThreadHandler, statusLiveData)
                ).also {
                    serverHelper = it
                }
        serverHelper.start()
    }

    private fun handleServerStop() {
        stopServer()
        stopSelf()
    }

    inner class LocalBinder : Binder() {
        val statusLiveData: LiveData<AndroscopeServiceStatus>
            get() = this@AndroscopeService.statusLiveData
    }

    private inner class ServiceHandler(looper: Looper) : Handler(looper) {
        override fun handleMessage(msg: Message) {
            onHandleIntent(msg.obj as Intent)
        }
    }

    companion object {

        const val ACTION_START_WEB_SERVER = "nl.ngti.androscope.action.START_WEB_SERVER"
        const val ACTION_STOP_WEB_SERVER = "nl.ngti.androscope.action.STOP_WEB_SERVER"

        fun startServer(context: Context) {
            val intent = Intent(context, AndroscopeService::class.java).apply {
                action = ACTION_START_WEB_SERVER
            }
            ContextCompat.startForegroundService(context, intent)
        }
    }
}
