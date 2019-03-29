package fi.iki.elonen.responses.filebrowser;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.format.Formatter;

import java.io.File;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import fi.iki.elonen.NanoHTTPD;
import fi.iki.elonen.menu.MenuItem;
import fi.iki.elonen.responses.BaseMainHtmlResponse;

import static fi.iki.elonen.responses.filebrowser.HtmlResponseViewFile.getMimeType;

/**
 * Shows a file explorer to access quickly the private file storage of the app.
 */
public class HtmlResponseShowFolder extends BaseMainHtmlResponse {

    public static final String FOLDER_SYMBOL = "&#128193;";

    public HtmlResponseShowFolder(Context context) {
        super(context);
    }

    @Override
    public boolean isEnabled(Bundle metadata) {
        return true;
    }

    @Override
    public MenuItem getMenuItem() {
        return null;
    }

    @Override
    protected String getContent(NanoHTTPD.IHTTPSession session) {
        Map<String, String> parms = session.getParms();
        String folderPath = parms.get("folder");
        if (folderPath != null) {
            File folder = new File(folderPath);
            return showFolder(folder, parms);
        }
        return null;
    }

    private String showFolder(File folder, Map<String, String> parms) {
        StringBuilder html = new StringBuilder();
        if (!folder.exists()) {
            html.append("Folder doesnt exist! " + folder);
            return html.toString();
        }

        // add menu here

        addBreadcumbs(html, folder);

        addTable(html, folder, parms);
        return html.toString();
    }

    private void addTable(StringBuilder html, File folder, Map<String, String> parms) {
        html.append("<div class=\"zebraborder\">");
        html.append("<table class=\"zebra\">");
        if (folder.getParentFile() != null) {
            html.append("<tr><td>");
            html.append(FOLDER_SYMBOL + " <a href=\"" + getUrlFolder(folder.getParentFile()) + "\">..</a>");
            html.append("</td><td>&nbsp;</td><td>&nbsp;</td><td>&nbsp;</td><td>&nbsp;</td></tr>");
//            html.append("<p><a href='" + getUrlFolder(folder.getParentFile()) + "'>..</a></p>");
        }
        final Context context = getContext();
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
                    html.append("</td><td>&nbsp;</td><td>&nbsp;</td><td>&nbsp;</td><td>&nbsp;</td>");
                } else {

                    html.append("<td>");
                    html.append(file.getName());
                    html.append("</td>");
                    html.append("<td align=\"right\">");
                    html.append(Formatter.formatFileSize(context, file.length()));
                    html.append("</td>");
                    html.append("<td>");
                    String mime = "" + getMimeType(context, parms, file);
                    if (mime.startsWith("image/") || mime.startsWith("video/")) {
                        html.append("<img width='100' heigth='100' src=\"/thumbnail?file=" + URLEncoder.encode(absolutePath) + "\" />");
                    } else {
                        html.append("&nbsp;");
                    }
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


                }
                html.append("</tr>");
            }
        }
        html.append("</table>");
        html.append("</div>");
    }

    private void addBreadcumbs(StringBuilder html, File folder) {
        List<File> parents = getParents(folder);
        html.append("<ul class=\"breadcrumb\">");
        html.append("<li><a href=\"" + getFileExplorerLink("/") + "\">" + FOLDER_SYMBOL + "</a></li>");
        File f;
        for (int i = 0; i < parents.size(); i++) {
            f = parents.get(i);
            if (!f.getName().isEmpty()) {
                html.append("<li><a href=\"" + getFileExplorerLink(f.getAbsolutePath()) + "\">" + f.getName() + "</a></li>");
            }
        }
        html.append("<li>" + folder.getName() + "</li>");
        html.append("</ul>");
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
    public static String getAssetsExplorerLink(String assetPath) {
        return "/filexp?view=asset:" + URLEncoder.encode(assetPath);
    }

    @NonNull
    public static String getUrlFolder(File folder) {
        return "/filexp?folder=" + URLEncoder.encode(folder.getAbsolutePath()) + "";
    }

    @NonNull
    public static String getUrlDownload(File file) {
        return "/filexp?download=" + URLEncoder.encode(file.getAbsolutePath());
    }

}
