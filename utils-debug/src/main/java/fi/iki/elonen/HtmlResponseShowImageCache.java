package fi.iki.elonen;

import android.content.Context;
import android.support.annotation.NonNull;
import java.io.File;
import java.net.URLEncoder;
import java.util.Map;
import java.util.regex.Pattern;

import static fi.iki.elonen.DeviceExplorerHttpServer.showFooter;
import static fi.iki.elonen.DeviceExplorerHttpServer.showHeader;

/**
 * Shows a file explorer to access quickly the private file storage of the app.
 */
public class HtmlResponseShowImageCache implements HtmlResponse {

    private final Context mContext;
    private final String mImageCache;
    private final Pattern mImageMatcher;

    public HtmlResponseShowImageCache(Context context, String imageCache, String imageFilter) {
        mContext = context;
        mImageCache = imageCache;
        mImageMatcher = Pattern.compile(imageFilter);
    }

    @Override
    public void showHtmlHeader(NanoHTTPD.IHTTPSession session, StringBuilder html) {
        String packageName = mContext.getPackageName();
        html.append("<p><a href='/imageCache?folder=%2Fdata%2Fuser%2F0%2F" + packageName
            + "%2Fcache%2F" + mImageCache + "'>Show Image cache</a></p>");
    }

    @Override
    public NanoHTTPD.Response getResponse(NanoHTTPD.IHTTPSession session) {
        if (isProcessable(session)) {
            final Map<String, String> parms = session.getParms();
            return processShowFolder(session, parms);
        }
        return null;
    }

    @NonNull
    private NanoHTTPD.Response processShowFolder(NanoHTTPD.IHTTPSession session, Map<String, String> parms) {
        final StringBuilder html = new StringBuilder();
        html.append("<html><body>");
        showHeader(mContext, session, html);


        String folderPath = parms.get("folder");
        if (folderPath != null) {
            File folder = new File(folderPath);
            showFolder(html, folder);
        }

        showFooter(html);
        html.append("</body></html>");
        return new NanoHTTPD.Response(html.toString());
    }

    private void showFolder(StringBuilder html, File folder) {
        if (!folder.exists()) {
            html.append("Folder doesnt exist! " + folder);
            return;
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
    }

    @NonNull
    private String getUrlFolder(File folder) {
        return "/filexp?folder=" + URLEncoder.encode(folder.getAbsolutePath()) + "";
    }

    private boolean isProcessable(NanoHTTPD.IHTTPSession session) {
        return session.getUri().startsWith("/imageCache");
    }


}
