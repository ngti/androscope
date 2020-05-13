package nl.ngti.androscope.service

import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.Handler
import android.util.Log
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.core.app.NotificationCompat
import androidx.lifecycle.MutableLiveData
import nl.ngti.androscope.R
import nl.ngti.androscope.server.AndroscopeHttpServer
import java.io.IOException

internal class AndroscopeServerCallback(
        private val service: Service,
        private val uiThreadHandler: Handler,
        private val statusLiveData: MutableLiveData<AndroscopeServiceStatus>
) : AndroscopeServerHelper.Callback {

    private val notificationHelper = AndroscopeNotificationHelper(service, uiThreadHandler)

    override fun onStarting() {
        notificationHelper.showNotification {
            setContentText("Androscope: attempting to start the web server")
        }

        statusLiveData.postValue(AndroscopeServiceStatus.success("Starting Androscope..."))
    }

    override fun onStarted(server: AndroscopeHttpServer) {
        notificationHelper.showNotification {
            setContentText("Androscope is running")
            addStopAction()
        }
        showToast("Androscope was started")
        reportServerInfo(server)
    }

    override fun onAlreadyRunning(server: AndroscopeHttpServer) {
        reportServerInfo(server)
    }

    override fun onError(e: IOException) {
        notificationHelper.showNotification {
            setContentText("Error starting Androscope")
            addRestartAction()
            addStopAction()
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
        service.stopForeground(true)
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

    private fun showToast(message: String) {
        uiThreadHandler.post {
            Toast.makeText(service, message, Toast.LENGTH_LONG).show()
        }
    }

    private fun NotificationCompat.Builder.addRestartAction() {
        addAction(AndroscopeService.ACTION_START_WEB_SERVER,
                R.id.androscope_notification_request_code_restart,
                R.string.androscope_restart)
    }

    private fun NotificationCompat.Builder.addStopAction() {
        addAction(AndroscopeService.ACTION_STOP_WEB_SERVER,
                R.id.androscope_notification_request_code_stop_server,
                R.string.androscope_stop_server)
    }

    private fun NotificationCompat.Builder.addAction(
            serviceAction: String, requestCode: Int, @StringRes textResId: Int) {

        val intent = Intent(service, AndroscopeService::class.java).apply {
            action = serviceAction
        }
        val pendingIntent = PendingIntent.getService(service,
                requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        val action = NotificationCompat.Action.Builder(0,
                service.getString(textResId), pendingIntent)
                .build()
        addAction(action)
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
}
