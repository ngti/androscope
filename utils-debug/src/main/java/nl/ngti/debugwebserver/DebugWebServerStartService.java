package nl.ngti.debugwebserver;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import fi.iki.elonen.DeviceExplorerHttpServer;
import fi.iki.elonen.IoServerRunner;
import fi.iki.elonen.NanoHTTPD;
import fi.iki.elonen.NanoHttpListener;

public class DebugWebServerStartService {

    private static final String TAG = DebugWebServerStartService.class.getSimpleName();

    private static boolean sServerStarted;

    private final Context mContext;

    private DebugWebServerStartService(Context context) {
        mContext = context.getApplicationContext();
    }

    public static void startServer(Context context) {
        final DebugWebServerStartService service = new DebugWebServerStartService(context);
        service.handleServerStart();
    }

    private void handleServerStart() {
        if (sServerStarted) {
            showToast("Debug Web Server is already running");
            return;
        }
        NanoHTTPD server = DeviceExplorerHttpServer.newInstance(mContext);
        IoServerRunner.executeInstance(mContext, server, new NanoListener());
        sServerStarted = true;
        showToast("Debug Web Server was started");
    }

    private void showToast(String message) {
        new Handler(Looper.getMainLooper()).post(new ShowMessageRunnable(mContext, message));
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
