package nl.ngti.androscope.server;

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

    //private final Set<HttpResponse> mHtmlResponses = new LinkedHashSet<>();

    private final Context mContext;
    private final Bundle mMetadata;

    private final ResponseFactory mResponseFactory;

    private AndroscopeHttpServer(Context context, int httpPort, Bundle metadata) {
        super(httpPort);

        mContext = context.getApplicationContext();
        mMetadata = metadata;

        mResponseFactory = new ResponseFactory(mContext);

//        mHtmlResponses.add(new HtmlResponseUploadDatabase(context));
//        mHtmlResponses.add(new HtmlResponseDownloadDatabase(context));
//        mHtmlResponses.add(new HtmlResponseDatabaseExplorer(context));
//        mHtmlResponses.add(new HtmlResponseFileExplorer(context));
//        mHtmlResponses.add(new HtmlResponseShowImageCache(context));
//        mHtmlResponses.add(new HtmlResponseThumbnail(context));
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
            e.printStackTrace();
            return NanoHTTPD.newFixedLengthResponse(TextUtils.htmlEncode(e.toString()));
        }
    }

    @NonNull
    public String getIpAddress() {
        final WifiManager myWifiManager = (WifiManager) mContext.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        //noinspection ConstantConditions
        final WifiInfo myWifiInfo = myWifiManager.getConnectionInfo();
        final int myIp = myWifiInfo.getIpAddress();
        return Formatter.formatIpAddress(myIp);
    }

    @NonNull
    private Response createResponse(IHTTPSession session) throws IOException {
        Log.d("Test", "createResponse " + session.getUri());

        final SessionWrapper sessionWrapper = new SessionWrapper(session);

        final BaseResponse response = mResponseFactory.getResponse(session);
        return response.getResponse(sessionWrapper);


        //velocityAsset.put("content", mResponseFactory.getResponse());

//        try {
//            return getResponseFromHtmlProcessors(session, menu);
//        } catch (final Throwable e) {
//            e.printStackTrace();
//            return NanoHTTPD.newFixedLengthResponse(htmlEncode(e.toString()));
//        }
    }

//    private Menu createMenu() {
//        Menu menu = new Menu();
//        menu.addItem("Home", "/");
//        for (HttpResponse resp : mHtmlResponses) {
//            if (resp.isEnabled(mMetadata)) {
//                MenuItem menuItem = resp.getMenuItem();
//                if (menuItem != null) {
//                    menuItem.addToMenu(menu);
//                }
//            }
//        }
//        return menu;
//    }

//    private Response getResponseFromHtmlProcessors(IHTTPSession session, Menu menu) throws IOException {
//        for (HttpResponse resp : mHtmlResponses) {
//            if (resp.isEnabled(mMetadata)) {
//                Response response = resp.getResponse(session, menu);
//                if (response != null) {
//                    return response;
//                }
//            }
//        }
//        return new EmptyResponse(mContext).getResponse(session, menu);
//    }

}
