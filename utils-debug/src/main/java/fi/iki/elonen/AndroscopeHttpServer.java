package fi.iki.elonen;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import fi.iki.elonen.menu.Menu;
import fi.iki.elonen.menu.MenuItem;
import fi.iki.elonen.responses.HtmlResponseShowImageCache;
import fi.iki.elonen.responses.HtmlResponseThumbnail;
import fi.iki.elonen.responses.database.HtmlResponseDatabaseExplorer;
import fi.iki.elonen.responses.database.HtmlResponseDownloadDatabase;
import fi.iki.elonen.responses.database.HtmlResponseUploadDatabase;
import fi.iki.elonen.responses.filebrowser.HtmlResponseFileExplorer;
import fi.iki.elonen.velocity.VelocityAsset;
import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.Set;

import static android.text.TextUtils.htmlEncode;
import static fi.iki.elonen.utils.AppUtils.getMetadata;

/**
 * Custom HTTP server. Displays the structure of the database.
 */
public class AndroscopeHttpServer extends NanoHTTPD {

    private static final String TAG = AndroscopeHttpServer.class.getSimpleName();

    private static final String KEY_HTTP_PORT = "nl.ngti.debugwebserver.HTTP_PORT";
    private static final String KEY_AUTO_START = "nl.ngti.debugwebserver.AUTO_START";
    private static final int HTTP_PORT = 8787;

    private final Set<HttpResponse> mHtmlResponses = new LinkedHashSet<>();
    private final Context mContext;
    private final Bundle metadata;

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
        mContext = context;

        mHtmlResponses.add(new HtmlResponseUploadDatabase(context));
        mHtmlResponses.add(new HtmlResponseDownloadDatabase(context));
        mHtmlResponses.add(new HtmlResponseDatabaseExplorer(context));
        mHtmlResponses.add(new HtmlResponseFileExplorer(context));
        mHtmlResponses.add(new HtmlResponseShowImageCache(context));
        mHtmlResponses.add(new HtmlResponseThumbnail(context));
        this.metadata = metadata;
    }

    @Override
    public Response serve(final IHTTPSession session) {
        Menu menu = createMenu();
        return createResponse(session, menu);
    }

    @NonNull
    private Response createResponse(IHTTPSession session, Menu menu) {
        try {
            Response response = getResponseFromHtmlProcessors(session, menu);
            if (response != null) {
                return response;
            } else {
                return getDefaultHtmlResponse(session, menu);
            }
        } catch (final Throwable e) {
            e.printStackTrace();
            return new Response(htmlEncode(e.toString()));
        }
    }

    private Menu createMenu() {
        Menu menu = new Menu();

        menu.addItem("Home", "/");

        for (HttpResponse resp : mHtmlResponses) {
            if (resp.isEnabled(metadata)) {
                MenuItem menuItem = resp.getMenuItem();
                if (menuItem != null) {
                    menuItem.addToMenu(menu);
                }
            }
        }
        return menu;
    }

    @NonNull
    private Response getDefaultHtmlResponse(IHTTPSession session, Menu menu) throws IOException {
        VelocityAsset v = new VelocityAsset();
        v.initMainAsset(mContext);

        v.put("header", menu.render());
        v.put("content", "");

        return new Response(v.html());
    }

    private Response getResponseFromHtmlProcessors(IHTTPSession session, Menu menu) throws IOException {
        Response response = null;
        for (HttpResponse resp : mHtmlResponses) {
            if (resp.isEnabled(metadata)) {
                response = resp.getResponse(session, menu);
                if (response != null) {
                    break;
                }
            }
        }
        return response;
    }

}
