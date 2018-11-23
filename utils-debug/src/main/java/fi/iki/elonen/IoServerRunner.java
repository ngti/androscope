package fi.iki.elonen;


import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.text.format.Formatter;

import java.io.IOException;

public class IoServerRunner {

    private static final int MAX_WATING_TIME = 5000;
    private static final int WAITING_STEP = 100;

    public static void executeInstance(final Context context, @NonNull final NanoHTTPD server, final NanoHttpListener nanoHttpListener) {
        try {
            server.start();
            waitForServer(server);
            notifyServerStarted(context, server, nanoHttpListener);
        } catch (final IOException ioe) {
            ioe.printStackTrace();
            nanoHttpListener.serverReady("COULD NOT START SERVER! " + ioe, "");
        }
    }

    private static void notifyServerStarted(final Context context, final NanoHTTPD server, final NanoHttpListener nanoHttpListener) {
        if (server.isAlive()) {
            final WifiManager myWifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            final WifiInfo myWifiInfo = myWifiManager.getConnectionInfo();
            final int myIp = myWifiInfo.getIpAddress();
            final String ip = Formatter.formatIpAddress(myIp);
            nanoHttpListener.serverReady(ip, "" + server.getListeningPort());
        } else {
            nanoHttpListener.serverReady("COULD NOT START SERVER", "");
        }
    }

    private static void waitForServer(NanoHTTPD server) {
        long waiting = 0;
        while (!server.isAlive()) {
            SystemClock.sleep(WAITING_STEP);
            waiting += WAITING_STEP;
            if (waiting > MAX_WATING_TIME) {
                break;
            }
        }
    }

}
