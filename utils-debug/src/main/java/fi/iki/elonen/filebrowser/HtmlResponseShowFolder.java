package fi.iki.elonen.filebrowser;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.NonNull;
import fi.iki.elonen.HtmlResponse;
import fi.iki.elonen.NanoHTTPD;
import java.io.File;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import static fi.iki.elonen.DeviceExplorerHttpServer.showFooter;
import static fi.iki.elonen.DeviceExplorerHttpServer.showHeader;

/**
 * Shows a file explorer to access quickly the private file storage of the app.
 */
public class HtmlResponseShowFolder implements HtmlResponse {

    public static final String FOLDER_SYMBOL = "&#128193;";
    private final Context mContext;

    public HtmlResponseShowFolder(Context context) {
        mContext = context;
    }

    @Override
    public void showHtmlHeader(NanoHTTPD.IHTTPSession session, StringBuilder html) {
        html.append("<p><a href='/filexp'>File Explorer</a></p>");
    }

    @Override
    public NanoHTTPD.Response getResponse(NanoHTTPD.IHTTPSession session) {
        return processShowFolder(session, session.getParms());
    }

    @NonNull
    private NanoHTTPD.Response processShowFolder(NanoHTTPD.IHTTPSession session, Map<String, String> parms) {
        final StringBuilder html = new StringBuilder();
        html.append("<html>");
        html.append("<link href=\"" + getAssetsExplorerLink("menu_breadcumbs.css") + "\" rel=\"stylesheet\" type=\"text/css\">");
        html.append("<link href=\"" + getAssetsExplorerLink("menu_vertical.css") + "\" rel=\"stylesheet\" type=\"text/css\">");
        html.append("<link href=\"" + getAssetsExplorerLink("buttons_outline.css") + "\" rel=\"stylesheet\" type=\"text/css\">");
        html.append("<link href=\"" + getAssetsExplorerLink("table_zebra.css") + "\" rel=\"stylesheet\" type=\"text/css\">");
        html.append("<body>");
        showHeader(session, html);


        String folderPath = parms.get("folder");
        if (folderPath != null) {
            File folder = new File(folderPath);
            showFolder(html, folder);
        } else {
            showAndroidFolders(html);
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

        // add menu here

        html.append("<ul class=\"breadcrumb\">");
        html.append("<li><a href=\"/\">Home</a></li>");
        List<File> parents = getParents(folder);


        html.append("<li><a href=\""
            + getFileExplorerLink("/")
            + "\">" + FOLDER_SYMBOL + "</a></li>");
        File f;
        for (int i = 0; i < parents.size(); i++) {
            f = parents.get(i);
            if (!f.getName().isEmpty()) {
                html.append("<li><a href=\""
                    + getFileExplorerLink(f.getAbsolutePath())
                    + "\">" + f.getName()
                    + "</a></li>");
            }
        }
        html.append("<li>" + folder.getName() + "</li>");
        html.append("</ul>");


        html.append("<div class=\"zebraborder\">");
        html.append("<table class=\"zebra\">");
        if (folder.getParentFile() != null) {
            html.append("<tr><td>");
            html.append(FOLDER_SYMBOL + " <a href=\"" + getUrlFolder(folder.getParentFile()) + "\">..</a>");
            html.append("</td><td>&nbsp;</td><td>&nbsp;</td><td>&nbsp;</td></tr>");
//            html.append("<p><a href='" + getUrlFolder(folder.getParentFile()) + "'>..</a></p>");
        }
        File[] files = folder.listFiles();
        if (files != null) {
            Arrays.sort(files, new Comparator<File>() {
                @Override
                public int compare(File o1, File o2) {
                    return Boolean.valueOf(o2.isDirectory()).compareTo(o1.isDirectory());
                }
            });
            for (File file : files) {
                html.append("<tr>");
                String absolutePath = file.getAbsolutePath();
                if (file.isDirectory()) {
                    html.append("<td>");
                    html.append(FOLDER_SYMBOL + " <a href=\"" + getFileExplorerLink(absolutePath) + "\">" + file.getName() + "</a>");
                    html.append("</td><td>&nbsp;</td><td>&nbsp;</td><td>&nbsp;</td>");
//                    html.append("<p><a href='"
//                        + getFileExplorerLink(absolutePath) + "'>" + absolutePath + "</a></p>");
                } else {

                    html.append("<td>");
                    html.append(file.getName());
                    html.append("</td>");
                    html.append("<td>");
                    html.append(file.length() + " bytes");
                    html.append("</td>");
                    html.append("<td>");
                    html.append("<button class=\"btn success\""
                        + " onclick=\"window.location.href='" + "/filexp?view=" + URLEncoder.encode(absolutePath) + "'\""
                        + ">View</button>");
                    html.append("</td>");
                    html.append("<td>");
                    html.append("<button class=\"btn info\""
                        + " onclick=\"window.location.href='" + getUrlDownload(file) + "'\""
                        + ">Download</button>");
                    html.append("</td>");


//                    html.append("<p>" + absolutePath + " - " + file.length() + " bytes" +
//                        " - <a href='" +
//                        "/filexp?view=" + URLEncoder.encode(absolutePath) + "'>View</a>" +
//                        " - <a href='" +
//                        getUrlDownload(file) + "'>Download</a>" +
//                        "</p>");
                }
                html.append("</tr>");
            }
        }
        html.append("</table>");
        html.append("</div>");
    }

    @NonNull
    private List<File> getParents(File folder) {
        List<File> parents = new ArrayList<>();
        File parent = folder.getParentFile();
        while (parent != null) {
            parents.add(0, parent);
            File parentFile = parent.getParentFile();
            if (!parent.equals(parentFile)) {
                parent = parentFile;
            } else {
                parent = null;
            }
        }
        return parents;
    }

    @NonNull
    private String getFileExplorerLink(String absolutePath) {
        return "/filexp?folder=" + URLEncoder.encode(absolutePath);
    }

    @NonNull
    private String getAssetsExplorerLink(String assetPath) {
        return "/filexp?view=asset:" + URLEncoder.encode(assetPath);
    }

    private void showAndroidFolders(StringBuilder html) {
        showAndroidFolder(html, new File(mContext.getApplicationInfo().dataDir), "Application Data");
        showAndroidFolder(html, Environment.getDownloadCacheDirectory(), "Environment.getDownloadCacheDirectory()");
        showAndroidFolder(html, Environment.getExternalStorageDirectory(), "Environment.getExternalStorageDirectory()");
        showAndroidFolder(html, Environment.getRootDirectory(), "Environment.getRootDirectory()");
    }

    private void showAndroidFolder(StringBuilder html, File folder, String label) {
        html.append("<p><a href='" + getUrlFolder(folder) + "'>" + label + ": " + folder + "</a></p>");
    }

    @NonNull
    private String getUrlFolder(File folder) {
        return "/filexp?folder=" + URLEncoder.encode(folder.getAbsolutePath()) + "";
    }

    @NonNull
    public static String getUrlDownload(File file) {
        return "/filexp?download=" + URLEncoder.encode(file.getAbsolutePath());
    }

    @NonNull
    private String getMimeType(Map<String, String> params, File file) {
        String mime = params.get("mime");
        if (mime == null) {
            Uri uri = Uri.fromFile(file);
            ContentResolver cR = mContext.getContentResolver();
            mime = cR.getType(uri);
            if (mime == null) {
                mime = "text/plain";
            }
        }
        return mime;
    }

}
