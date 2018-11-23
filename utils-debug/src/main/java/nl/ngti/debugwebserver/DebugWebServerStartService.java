package nl.ngti.debugwebserver;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
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
    private static final String KEY_FORCE = "nl.ngti.debugwebserver.key.FORCE";

    private static boolean sServerStarted;

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
            if (ACTION_START_WEB_SERVER.equals(action)) {
                boolean force = intent.getBooleanExtra(KEY_FORCE, false);
                handleServerStart(force);
            }
        }
        return START_NOT_STICKY;
    }

    private void handleServerStart(boolean force) {
        if (sServerStarted) {
            showToast("Debug Web Server is already running");
            return;
        }

        NanoHTTPD server = DeviceExplorerHttpServer.newInstance(this, force);
        if (server != null) {
            showNotification();

            IoServerRunner.executeInstance(this, server, new NanoListener());
            sServerStarted = true;
            showToast("Debug Web Server was started");
        }
    }

    private void showNotification() {
        final Notification notification = getNotificationBuilder()
                .setContentText("Androscope is running")
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
