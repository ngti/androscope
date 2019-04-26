package nl.ngti.androscope.service;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.GuardedBy;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.io.IOException;

import nl.ngti.androscope.AndroscopeActivity;
import nl.ngti.androscope.R;
import nl.ngti.androscope.server.AndroscopeHttpServer;

public final class AndroscopeService extends Service {

    private static final String TAG = AndroscopeService.class.getSimpleName();

    private static final String ACTION_START_WEB_SERVER = "nl.ngti.androscope.action.START_WEB_SERVER";
    private static final String ACTION_STOP_WEB_SERVER = "nl.ngti.androscope.action.STOP_WEB_SERVER";

    private static final String KEY_FORCE = "nl.ngti.androscope.key.FORCE";

    private final LocalBinder mLocalBinder = new LocalBinder();

    private final MutableLiveData<AndroscopeServiceStatus> mStatusLiveData = new MutableLiveData<>();

    private volatile Looper mServiceLooper;
    private volatile ServiceHandler mServiceHandler;

    @GuardedBy("this")
    @Nullable
    private AndroscopeServerHelper mServerHelper;

    public static void startServer(Context context, boolean force) {
        final Intent intent = new Intent(context, AndroscopeService.class);
        intent.setAction(ACTION_START_WEB_SERVER);
        intent.putExtra(KEY_FORCE, force);

        ContextCompat.startForegroundService(context, intent);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        final HandlerThread thread = new HandlerThread(AndroscopeService.class.getSimpleName());
        thread.start();

        mServiceLooper = thread.getLooper();
        mServiceHandler = new ServiceHandler(mServiceLooper);
    }

    @NonNull
    @Override
    public IBinder onBind(Intent intent) {
        return mLocalBinder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            final Message msg = mServiceHandler.obtainMessage();
            msg.obj = intent;
            mServiceHandler.sendMessage(msg);
        }
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        mServiceLooper.quit();
        stopServer();
    }

    @NonNull
    public LiveData<AndroscopeServiceStatus> getStatusLiveData() {
        return mStatusLiveData;
    }

    private void onHandleIntent(@NonNull Intent intent) {
        final String action = intent.getAction();
        if (action != null) {
            switch (action) {
                case ACTION_START_WEB_SERVER:
                    final boolean force = intent.getBooleanExtra(KEY_FORCE, false);
                    handleServerStart(force);
                    break;
                case ACTION_STOP_WEB_SERVER:
                    handleServerStop();
                    break;
            }
        }
    }

    private synchronized void stopServer() {
        if (mServerHelper != null) {
            mServerHelper.stop();
        }
    }

    private synchronized void handleServerStart(boolean force) {
        if (mServerHelper == null) {
            mServerHelper = AndroscopeServerHelper.newInstance(this, force, new ServerStartCallback());
        }
        if (mServerHelper != null) {
            mServerHelper.start();
        }
    }

    private void handleServerStop() {
        stopServer();
        stopSelf();
    }

    private void addRestartAction(NotificationCompat.Builder notificationBuilder) {
        addAction(notificationBuilder, ACTION_START_WEB_SERVER,
                R.id.androscope_notification_request_code_restart,
                R.string.androscope_restart);
    }

    private void addStopAction(NotificationCompat.Builder notificationBuilder) {
        addAction(notificationBuilder, ACTION_STOP_WEB_SERVER,
                R.id.androscope_notification_request_code_stop_server,
                R.string.androscope_stop_server);
    }

    private void addAction(NotificationCompat.Builder notificationBuilder, String serviceAction,
                           int requestCode, @StringRes int textResId) {

        final Intent intent = new Intent(this, AndroscopeService.class);
        intent.setAction(serviceAction);

        final PendingIntent pendingIntent = PendingIntent.getService(this,
                requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        final NotificationCompat.Action action = new NotificationCompat.Action.Builder(0,
                getString(textResId), pendingIntent)
                .build();

        notificationBuilder.addAction(action);
    }

    private void showNotification(NotificationCompat.Builder notificationBuilder) {
        notificationBuilder
                .setSmallIcon(R.drawable.androscope_notification_icon)
                .setContentIntent(PendingIntent.getActivity(this,
                        R.id.androscope_notification_request_code_open_androscope_activity,
                        new Intent(this, AndroscopeActivity.class), PendingIntent.FLAG_UPDATE_CURRENT))
                .build();

        startForeground(R.id.androscope_notification_id, notificationBuilder.build());
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

    private void performLogging(@NonNull Runnable runnable) {
        // Logs on some devices are polluted when activity is started, here we add some delay,
        // so the IP address will be logged in the end and user will not need to scroll up.
        new Handler(Looper.getMainLooper()).postDelayed(runnable, 200);
    }

    private static final class LogSuccess implements Runnable {

        private final String mInfo;

        private LogSuccess(String info) {
            mInfo = info;
        }

        @Override
        public void run() {
            Log.d(TAG, mInfo);
        }
    }

    private static final class LogError implements Runnable {

        private final IOException mException;

        private LogError(IOException exception) {
            mException = exception;
        }

        @Override
        public void run() {
            Log.e(TAG, "Error when starting Androscope", mException);
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

    public final class LocalBinder extends Binder {

        @NonNull
        public AndroscopeService getService() {
            return AndroscopeService.this;
        }
    }

    private final class ServiceHandler extends Handler {

        ServiceHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            onHandleIntent((Intent) msg.obj);
        }
    }

    private final class ServerStartCallback implements AndroscopeServerHelper.Callback {

        @Override
        public void onStarting() {
            final NotificationCompat.Builder notificationBuilder = getNotificationBuilder()
                    .setContentText("Androscope: attempting to start the web server");
            showNotification(notificationBuilder);

            mStatusLiveData.postValue(AndroscopeServiceStatus.success("Starting Androscope..."));
        }

        @Override
        public void onStarted(AndroscopeHttpServer server) {
            final NotificationCompat.Builder notificationBuilder = getNotificationBuilder()
                    .setContentText("Androscope is running");
            addStopAction(notificationBuilder);
            showNotification(notificationBuilder);

            showToast("Androscope was started");
            reportServerInfo(server);
        }

        @Override
        public void onAlreadyRunning(AndroscopeHttpServer server) {
            reportServerInfo(server);
        }

        @Override
        public void onError(IOException e) {
            final NotificationCompat.Builder notificationBuilder = getNotificationBuilder()
                    .setContentText("Error starting Androscope");
            addRestartAction(notificationBuilder);
            addStopAction(notificationBuilder);
            showNotification(notificationBuilder);

            showToast("Error starting Androscope");

            mStatusLiveData.postValue(
                    AndroscopeServiceStatus.error("There was an error when starting Androscope:\n\n" +
                            e.getMessage() + "\n\n" +
                            "\nCheck Logcat for more details"));

            performLogging(new LogError(e));
        }

        @Override
        public void onStopped() {
            stopForeground(true);

            final String message = "Androscope was stopped";

            mStatusLiveData.postValue(AndroscopeServiceStatus.stopped(message));

            performLogging(new LogSuccess(message));
        }

        private void reportServerInfo(@NonNull AndroscopeHttpServer server) {
            final String ip = server.getIpAddress();
            final int port = server.getListeningPort();
            final String message =
                    "Androscope is running!\n" +
                            "Address: [ http://" + ip + ":" + port + " ]\n\n" +
                            "Local server at [ http://127.0.0.1:" + port + " ]\n\n" +
                            "For GENYMOTION this ip doesn't work, use the ip of the emulator returned by 'adb devices'\n";

            mStatusLiveData.postValue(
                    AndroscopeServiceStatus.success(message +
                            "\nYou can find this information also in Logcat for “:androidscope” process"));

            performLogging(new LogSuccess(message));
        }
    }

}
