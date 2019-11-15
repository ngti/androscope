package nl.ngti.androscope.server;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.format.Formatter;
import android.util.Log;

import androidx.annotation.NonNull;

import java.io.IOException;

import fi.iki.elonen.NanoHTTPD;

import static nl.ngti.androscope.utils.AppUtils.getMetadata;

/**
 * Custom HTTP server. Displays the structure of the database.
 */
public class AndroscopeHttpServer extends NanoHTTPD {

    public static final String KEY_AUTO_START = "nl.ngti.androscope.AUTO_START";

    private static final String KEY_HTTP_PORT = "nl.ngti.androscope.HTTP_PORT";
    private static final int HTTP_PORT = 8787;

    private static final String TAG = AndroscopeHttpServer.class.getSimpleName();

    private final Context mContext;

    private final ResponseFactory mResponseFactory;

    private AndroscopeHttpServer(Context context, int httpPort, Bundle metadata) {
        super(httpPort);

        mContext = context.getApplicationContext();

        mResponseFactory = new ResponseFactory(mContext, metadata);
    }

    @NonNull
    public static AndroscopeHttpServer newInstance(Context context) {
        final Bundle metadata = getMetadata(context);
        final int httpPort = metadata.getInt(KEY_HTTP_PORT, HTTP_PORT);
        return new AndroscopeHttpServer(context, httpPort, metadata);
    }

    @Override
    public Response serve(final IHTTPSession session) {
        try {
            return createResponse(session);
        } catch (IOException e) {
            Log.e(TAG, "Error creating response", e);
            return NanoHTTPD.newFixedLengthResponse(TextUtils.htmlEncode(e.toString()));
        }
    }

    @NonNull
    public String getIpAddress() {
        @SuppressLint("WifiManagerPotentialLeak") final WifiManager myWifiManager =
                (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
        //noinspection ConstantConditions
        final WifiInfo myWifiInfo = myWifiManager.getConnectionInfo();
        final int myIp = myWifiInfo.getIpAddress();
        return Formatter.formatIpAddress(myIp);
    }

    @NonNull
    private Response createResponse(IHTTPSession session) throws IOException {
        final BaseAndroscopeResponse response = mResponseFactory.getResponse(session);
        return response.getResponse(session);
    }
}
