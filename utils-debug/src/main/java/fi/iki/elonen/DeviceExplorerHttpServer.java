package fi.iki.elonen;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import fi.iki.elonen.database.HtmlResponseDatabaseExplorer;
import fi.iki.elonen.database.HtmlResponseDownloadDatabase;
import fi.iki.elonen.database.HtmlResponseUploadDatabase;
import fi.iki.elonen.filebrowser.HtmlResponseFileExplorer;
import java.io.File;
import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.Set;

import static android.text.TextUtils.htmlEncode;
import static fi.iki.elonen.filebrowser.HtmlResponseShowFolder.getAssetsExplorerLink;
import static fi.iki.elonen.filebrowser.HtmlResponseShowFolder.getUrlFolder;

/**
 * Custom HTTP server. Displays the structure of the database.
 */
public class DeviceExplorerHttpServer extends NanoHTTPD {

    private static final String TAG = DeviceExplorerHttpServer.class.getSimpleName();

    private static final String KEY_DATABASE_NAME = "nl.ngti.debugwebserver.DATABASE_NAME";
    private static final String KEY_IMAGE_CACHE = "nl.ngti.debugwebserver.IMAGE_CACHE";
    private static final String KEY_IMAGE_CACHE_FILTER = "nl.ngti.debugwebserver.IMAGE_CACHE.filter";
    private static final String KEY_HTTP_PORT = "nl.ngti.debugwebserver.HTTP_PORT";
    private static final String KEY_AUTO_START = "nl.ngti.debugwebserver.AUTO_START";
    private static final int HTTP_PORT = 8787;

    private final Set<HtmlResponse> mHtmlResponses = new LinkedHashSet<>();
    private final Context mContext;

    private DeviceExplorerHttpServer(Context context, String databaseName, String imageCache, String imageFilter, int httpPort) {
        super(httpPort);
        mContext = context;

        if (!TextUtils.isEmpty(databaseName)) {
            mHtmlResponses.add(new HtmlResponseUploadDatabase(context, databaseName));
            mHtmlResponses.add(new HtmlResponseDownloadDatabase(context, databaseName));
            mHtmlResponses.add(new HtmlResponseDatabaseExplorer(context, databaseName));
        } else {
            Log.w(TAG, KEY_DATABASE_NAME + " metadata was not specified. Not showing database-related items");
        }
        mHtmlResponses.add(new HtmlResponseFileExplorer(context));
        if (!TextUtils.isEmpty(imageCache) && !TextUtils.isEmpty(imageFilter)) {
            mHtmlResponses.add(new HtmlResponseShowImageCache(context, imageCache, imageFilter));
        } else {
            Log.w(TAG, KEY_IMAGE_CACHE + " and/or " + KEY_IMAGE_CACHE_FILTER + " metadata were not specified. Not showing image cache item");
        }
    }

    @Nullable
    public static DeviceExplorerHttpServer newInstance(Context context, boolean force) {
        ApplicationInfo applicationInfo;
        try {
            applicationInfo = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
        } catch (PackageManager.NameNotFoundException e) {
            throw new RuntimeException(e);
        }
        Bundle metadata = applicationInfo.metaData;
        if (!force && !metadata.getBoolean(KEY_AUTO_START, false)) {
            return null;
        }
        String databaseName = metadata.getString(KEY_DATABASE_NAME);
        String imageCache = metadata.getString(KEY_IMAGE_CACHE);
        String imageFilter = metadata.getString(KEY_IMAGE_CACHE_FILTER);
        int httpPort = metadata.getInt(KEY_HTTP_PORT, HTTP_PORT);
        return new DeviceExplorerHttpServer(context, databaseName, imageCache, imageFilter, httpPort);
    }

    @Override
    public Response serve(final IHTTPSession session) {
        try {
            Response response = getResponseFromHtmlProcessors(session);
            if (response != null) {
                return response;
            } else {
                return getDefaultHtmlResponse(session);
            }
        } catch (final Throwable e) {
            e.printStackTrace();
            return new NanoHTTPD.Response(htmlEncode(e.toString()));
        }
    }

    @NonNull
    private Response getDefaultHtmlResponse(IHTTPSession session) throws IOException {
        final StringBuilder html = new StringBuilder();
        html.append("<html><body>");
        showHeader(mContext, session, html);
        showHeaderFromHtmlProcessors(session, html);

        showFooter(html);
        html.append("</body></html>");
        return new Response(html.toString());
    }

    private Response getResponseFromHtmlProcessors(IHTTPSession session) {
        Response response = null;
        for (HtmlResponse resp : mHtmlResponses) {
            response = resp.getResponse(session);
            if (response != null) {
                break;
            }
        }
        return response;
    }


    public static void showFooter(StringBuilder html) {
        html.append("<p>NGTI</p>");
    }

    public static void showHeader(Context context, IHTTPSession session, StringBuilder html) {

        html.append("<link href=\"" + getAssetsExplorerLink("navbar.css") + "\" rel=\"stylesheet\" type=\"text/css\">");
        //            + "  <a href=\"#news\">File Explorer</a>\n"
        StringBuilder builder = html.append("<div class=\"navbar\">\n")
            .append("  <a href=\"/\">Home</a>\n")
            .append("  <div class=\"dropdown\">\n")
            .append(
                "    <button class=\"dropbtn\">File Explorer \n")
            .append("      <i class=\"fa fa-caret-down\"></i>\n")
            .append("    </button>\n")
            .append("    <div class=\"dropdown-content\">\n");

        builder.append("      <a href=\"" + getUrlFolder(new File(context.getApplicationInfo().dataDir)) + "\">Application Data</a>\n");
        builder.append("      <a href=\"" + getUrlFolder(Environment.getExternalStorageDirectory()) + "\">External Storage</a>\n");
        builder.append("      <a href=\"" + getUrlFolder(Environment.getRootDirectory()) + "\">Root Directory</a>\n");

        builder
            .append("    </div>\n")
            .append("  </div> \n")
            .append("</div>");
//        html.append("<h1><a href='/'>Device Explorer</a></h1>");
    }

    private void showHeaderFromHtmlProcessors(IHTTPSession session, StringBuilder html) {
        for (HtmlResponse proc : mHtmlResponses) {
            proc.showHtmlHeader(session, html);
        }
    }


}
