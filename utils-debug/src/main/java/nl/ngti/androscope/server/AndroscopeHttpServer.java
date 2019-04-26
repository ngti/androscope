package nl.ngti.androscope.server;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.text.format.Formatter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import nl.ngti.androscope.responses.HttpResponse;
import fi.iki.elonen.NanoHTTPD;
import nl.ngti.androscope.menu.Menu;
import nl.ngti.androscope.menu.MenuItem;
import nl.ngti.androscope.responses.EmptyResponse;
import nl.ngti.androscope.responses.HtmlResponseShowImageCache;
import nl.ngti.androscope.responses.HtmlResponseThumbnail;
import nl.ngti.androscope.responses.database.HtmlResponseDatabaseExplorer;
import nl.ngti.androscope.responses.database.HtmlResponseDownloadDatabase;
import nl.ngti.androscope.responses.database.HtmlResponseUploadDatabase;
import nl.ngti.androscope.responses.filebrowser.HtmlResponseFileExplorer;
import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.Set;

import static android.text.TextUtils.htmlEncode;
import static nl.ngti.androscope.utils.AppUtils.getMetadata;

/**
 * Custom HTTP server. Displays the structure of the database.
 */
public class AndroscopeHttpServer extends NanoHTTPD {

    private static final String KEY_HTTP_PORT = "nl.ngti.androscope.HTTP_PORT";
    private static final String KEY_AUTO_START = "nl.ngti.androscope.AUTO_START";
    private static final int HTTP_PORT = 8787;

    private final Set<HttpResponse> mHtmlResponses = new LinkedHashSet<>();

    private final Context mContext;
    private final Bundle mMetadata;

    @Nullable
    public static AndroscopeHttpServer newInstance(Context context, boolean force) {
        Bundle metadata = getMetadata(context);
        if (!force && !metadata.getBoolean(KEY_AUTO_START, false)) {
            return null;
        }
        int httpPort = metadata.getInt(KEY_HTTP_PORT, HTTP_PORT);
        return new AndroscopeHttpServer(context, httpPort, metadata);
    }

    private AndroscopeHttpServer(Context context, int httpPort, Bundle metadata) {
        super(httpPort);

        mContext = context.getApplicationContext();
        mMetadata = metadata;

        mHtmlResponses.add(new HtmlResponseUploadDatabase(context));
        mHtmlResponses.add(new HtmlResponseDownloadDatabase(context));
        mHtmlResponses.add(new HtmlResponseDatabaseExplorer(context));
        mHtmlResponses.add(new HtmlResponseFileExplorer(context));
        mHtmlResponses.add(new HtmlResponseShowImageCache(context));
        mHtmlResponses.add(new HtmlResponseThumbnail(context));
    }

    @Override
    public Response serve(final IHTTPSession session) {
        Menu menu = createMenu();
        return createResponse(session, menu);
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
    private Response createResponse(IHTTPSession session, Menu menu) {
        try {
            return getResponseFromHtmlProcessors(session, menu);
        } catch (final Throwable e) {
            e.printStackTrace();
            return NanoHTTPD.newFixedLengthResponse(htmlEncode(e.toString()));
        }
    }

    private Menu createMenu() {
        Menu menu = new Menu();
        menu.addItem("Home", "/");
        for (HttpResponse resp : mHtmlResponses) {
            if (resp.isEnabled(mMetadata)) {
                MenuItem menuItem = resp.getMenuItem();
                if (menuItem != null) {
                    menuItem.addToMenu(menu);
                }
            }
        }
        return menu;
    }

    private Response getResponseFromHtmlProcessors(IHTTPSession session, Menu menu) throws IOException {
        for (HttpResponse resp : mHtmlResponses) {
            if (resp.isEnabled(mMetadata)) {
                Response response = resp.getResponse(session, menu);
                if (response != null) {
                    return response;
                }
            }
        }
        return new EmptyResponse(mContext).getResponse(session, menu);
    }

}
