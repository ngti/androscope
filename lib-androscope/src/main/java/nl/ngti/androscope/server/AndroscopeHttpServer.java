package nl.ngti.androscope.server;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.text.TextUtils;
import android.text.format.Formatter;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.IOException;

import fi.iki.elonen.NanoHTTPD;
import nl.ngti.androscope.utils.AndroscopeMetadata;

/**
 * Custom HTTP server. Displays the structure of the database.
 */
public class AndroscopeHttpServer extends NanoHTTPD {

    private static final String TAG = AndroscopeHttpServer.class.getSimpleName();

    private final Context mContext;

    private final ResponseFactory mResponseFactory;

    private AndroscopeHttpServer(Context context, int httpPort, AndroscopeMetadata metadata) {
        super(httpPort);

        mContext = context.getApplicationContext();

        mResponseFactory = new ResponseFactory(mContext, metadata);
    }

    @NonNull
    public static AndroscopeHttpServer newInstance(Context context) {
        final AndroscopeMetadata metadata = AndroscopeMetadata.fromContext(context);
        final int httpPort = metadata.getHttpPort();
        return new AndroscopeHttpServer(context, httpPort, metadata);
    }

    @Override
    public Response serve(final IHTTPSession session) {
        try {
            Response response = createResponse(session);
            if (response == null) {
                response = super.serve(session);
            } else {
                response.addHeader("Access-Control-Allow-Origin", "*");
            }
            return response;
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

    @Nullable
    private Response createResponse(IHTTPSession session) throws IOException {
        return mResponseFactory.getResponse(session);
    }
}
