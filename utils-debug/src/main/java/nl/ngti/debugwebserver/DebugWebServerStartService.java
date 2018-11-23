package nl.ngti.debugwebserver;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import fi.iki.elonen.DeviceExplorerHttpServer;
import fi.iki.elonen.IoServerRunner;
import fi.iki.elonen.NanoHTTPD;
import fi.iki.elonen.NanoHttpListener;

public class DebugWebServerStartService extends Service {

    private static final String TAG = DebugWebServerStartService.class.getSimpleName();

    private static final String ACTION_START_WEB_SERVER = "nl.ngti.debugwebserver.action.START_WEB_SERVER";
    private static final String ACTION_STOP_WEB_SERVER = "nl.ngti.debugwebserver.action.STOP_WEB_SERVER";
    private static final String KEY_FORCE = "nl.ngti.debugwebserver.key.FORCE";

    private NanoHTTPD mServer;

    public static void startServer(Context context, boolean force) {
        Intent intent = new Intent(context, DebugWebServerStartService.class);
        intent.setAction(ACTION_START_WEB_SERVER);
        intent.putExtra(KEY_FORCE, force);
        ContextCompat.startForegroundService(context, intent);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            final String action = intent.getAction();
            if (action != null) {
                switch (action) {
                    case ACTION_START_WEB_SERVER:
                        boolean force = intent.getBooleanExtra(KEY_FORCE, false);
                        handleServerStart(force);
                        break;
                    case ACTION_STOP_WEB_SERVER:
                        handleServerStop();
                        break;
                }
            }
        }
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopServer();
    }

    private boolean stopServer() {
        if (mServer != null) {
            mServer.stop();
            mServer = null;
            return true;
        }
        return false;
    }

    private void handleServerStart(boolean force) {
        if (mServer != null && mServer.isAlive()) {
            showToast("Debug Web Server is already running");
            return;
        }

        mServer = DeviceExplorerHttpServer.newInstance(this, force);
        if (mServer != null) {
            showNotification();

            IoServerRunner.executeInstance(this, mServer, new NanoListener());
            showToast("Debug Web Server was started");
        }
    }

    private void handleServerStop() {
        if (stopServer()) {
            stopForeground(true);
        }
    }

    private void showNotification() {
        final Intent intent = new Intent(this, DebugWebServerStartService.class);
        intent.setAction(ACTION_STOP_WEB_SERVER);

        final PendingIntent pendingIntent = PendingIntent.getService(this,
                R.id.androscope_notification_request_code_stop_server, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        final NotificationCompat.Action action = new NotificationCompat.Action.Builder(0, getString(R.string.androscope_stop_server), pendingIntent)
                .build();

        final Notification notification = getNotificationBuilder()
                .setSmallIcon(R.drawable.androscope_notification_icon)
                .setContentText("Androscope is running")
                .addAction(action)
                .build();

        startForeground(R.id.androscope_notification_id, notification);
    }

    @NonNull
    private NotificationCompat.Builder getNotificationBuilder() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            final String channelId = getString(R.string.androscope_channel_id);
            final String channelName = getString(R.string.androscope_channel_name);
            final NotificationChannel channel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_MIN);
            getNotificationManager().createNotificationChannel(channel);

            return new NotificationCompat.Builder(this, channelId);
        } else {
            //noinspection deprecation
            return new NotificationCompat.Builder(this);
        }
    }

    @NonNull
    private NotificationManager getNotificationManager() {
        //noinspection ConstantConditions
        return (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
    }

    private void showToast(String message) {
        new Handler(Looper.getMainLooper()).post(new ShowMessageRunnable(this, message));
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private static final class NanoListener implements NanoHttpListener {

        @Override
        public void serverReady(final String ip, final String port) {
            Log.d(TAG, "Http server ready at [ http://" + ip + ":" + port + " ]  Local server at [ http://127.0.0.1:" + port + " ]" +
                    " For GENYMOTION this ip doesnt work, use the ip of the emulator returned by 'adb devices'");
        }
    }

    private static final class ShowMessageRunnable implements Runnable {

        private final Context mContext;
        private final String mMessage;

        private ShowMessageRunnable(Context context, String message) {
            mContext = context;
            mMessage = message;
        }

        @Override
        public void run() {
            Toast.makeText(mContext, mMessage, Toast.LENGTH_LONG).show();
        }
    }

}
