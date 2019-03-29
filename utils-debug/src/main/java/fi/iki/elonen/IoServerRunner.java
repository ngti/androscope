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

    public static boolean executeInstance(@NonNull final NanoHTTPD server) {
        try {
            server.start();
            waitForServer(server);
            return true;
        } catch (final IOException ioe) {
            ioe.printStackTrace();
            return false;
        }
    }

    public static void notifyServerStarted(final Context context, final NanoHTTPD server, final NanoHttpListener nanoHttpListener) {
        if (server.isAlive()) {
            final WifiManager myWifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            //noinspection ConstantConditions
            final WifiInfo myWifiInfo = myWifiManager.getConnectionInfo();
            final int myIp = myWifiInfo.getIpAddress();
            final String ip = Formatter.formatIpAddress(myIp);
            nanoHttpListener.serverReady(ip, "" + server.getListeningPort());
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
