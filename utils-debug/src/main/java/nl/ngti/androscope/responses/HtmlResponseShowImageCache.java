package nl.ngti.androscope.responses;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import fi.iki.elonen.NanoHTTPD;
import nl.ngti.androscope.menu.MenuItem;
import java.io.File;
import java.net.URLEncoder;
import java.util.regex.Pattern;

/**
 * Shows a file explorer to access quickly the private file storage of the app.
 */
public class HtmlResponseShowImageCache extends BaseMainHtmlResponse {

    private static final String KEY_IMAGE_CACHE = "nl.ngti.androscope.IMAGE_CACHE";
    private static final String KEY_IMAGE_CACHE_FILTER = "nl.ngti.androscope.IMAGE_CACHE.filter";

    private String mImageCache;
    private Pattern mImageMatcher;

    public HtmlResponseShowImageCache(Context context) {
        super(context);
    }

    @Override
    public boolean isEnabled(Bundle metadata) {
        mImageCache = metadata.getString(KEY_IMAGE_CACHE);
        String imageFilter = metadata.getString(KEY_IMAGE_CACHE_FILTER);
        boolean enabled = !TextUtils.isEmpty(mImageCache) && !TextUtils.isEmpty(imageFilter);
        if (enabled) {
            mImageMatcher = Pattern.compile(imageFilter);
        } else {
            Log.w("androscope", KEY_IMAGE_CACHE + " and/or " + KEY_IMAGE_CACHE_FILTER + " metadata were not specified. Not showing image cache item");
        }
        return enabled;
    }

    @Override
    public MenuItem getMenuItem() {
        String packageName = getContext().getPackageName();
        return new MenuItem("Show Image cache", "/imageCache?folder=%2Fdata%2Fuser%2F0%2F" + packageName + "%2Fcache%2F" + mImageCache);
    }

    @Override
    protected String getContent(NanoHTTPD.IHTTPSession session) {

        String folderPath = session.getParms().get("folder");
        if (folderPath != null) {
            File folder = new File(folderPath);
            return showFolder(folder);
        }
        return null;
    }

    private String showFolder(File folder) {
        StringBuilder html = new StringBuilder();
        if (!folder.exists()) {
            html.append("Folder doesnt exist! " + folder);
            return html.toString();
        }
        html.append("<p>" + folder.getAbsolutePath() + "</p>");
        if (folder.getParentFile() != null) {
            html.append("<p><a href='" + getUrlFolder(folder.getParentFile()) + "'>..</a></p>");
        }
        File[] files = folder.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    html.append(
                        "<p><a href='/filexp?folder=" + URLEncoder.encode(file.getAbsolutePath()) + "'>" + file.getAbsolutePath() + "</a></p>");
                } else {
                    html.append("<div style='display: table-cell; vertical-align: middle;padding: 5px; bordeR: 1px solid #f00;'>").append(
                        file.getAbsolutePath()).append(" - ").append(file.length()).append(" bytes");
                    if (mImageMatcher.matcher(file.getName()).matches()) {
                        html.append("<br/><img src='/filexp?view=").append(URLEncoder.encode(file.getAbsolutePath())).append("' />");
                    }
                    html.append("</div>");
                    // br added due to the style
                    html.append("&nbsp;<br/>");
                }
            }
        }
        return html.toString();
    }

    @NonNull
    private String getUrlFolder(File folder) {
        return "/filexp?folder=" + URLEncoder.encode(folder.getAbsolutePath()) + "";
    }

    private boolean isProcessable(NanoHTTPD.IHTTPSession session) {
        return session.getUri().startsWith("/imageCache");
    }


}
