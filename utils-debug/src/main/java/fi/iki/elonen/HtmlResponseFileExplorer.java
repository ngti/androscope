package fi.iki.elonen;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.URLEncoder;
import java.util.Map;

import static fi.iki.elonen.DeviceExplorerHttpServer.showFooter;
import static fi.iki.elonen.DeviceExplorerHttpServer.showHeader;

/**
 * Shows a file explorer to access quickly the private file storage of the app.
 */
public class HtmlResponseFileExplorer implements HtmlResponse {

    private final Context mContext;

    public HtmlResponseFileExplorer(Context context) {
        mContext = context;
    }

    @Override
    public void showHtmlHeader(NanoHTTPD.IHTTPSession session, StringBuilder html) {
        html.append("<p><a href='/filexp'>File Explorer</a></p>");
    }

    @Override
    public NanoHTTPD.Response getResponse(NanoHTTPD.IHTTPSession session) {
        if (isProcessable(session)) {
            final Map<String, String> parms = session.getParms();
            if (isView(parms)) {
                return processViewFile(parms);
            } else if (isDownload(parms)) {
                return processDownloadFile(parms);
            } else {
                return processShowFolder(session, parms);
            }

        }
        return null;
    }

    private boolean isDownload(Map<String, String> parms) {
        return parms.containsKey("download");
    }

    private boolean isView(Map<String, String> parms) {
        return parms.containsKey("view");
    }

    @NonNull
    private NanoHTTPD.Response processShowFolder(NanoHTTPD.IHTTPSession session, Map<String, String> parms) {
        final StringBuilder html = new StringBuilder();
        html.append("<html><body>");
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
        html.append("<p>" + folder.getAbsolutePath() + "</p>");
        if (folder.getParentFile() != null) {
            html.append("<p><a href='" + getUrlFolder(folder.getParentFile()) + "'>..</a></p>");
        }
        File[] files = folder.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    html.append("<p><a href='/filexp?folder=" + URLEncoder.encode(file.getAbsolutePath()) + "'>" + file.getAbsolutePath() + "</a></p>");
                } else {
                    html.append("<p>" + file.getAbsolutePath() + " - " + file.length() + " bytes" +
                            " - <a href='" +
                            "/filexp?view=" + URLEncoder.encode(file.getAbsolutePath()) + "'>View</a>" +
                            " - <a href='" +
                            getUrlDownload(file) + "'>Download</a>" +
                            "</p>");
                }
            }
        }
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
    static String getUrlDownload(File file) {
        return "/filexp?download=" + URLEncoder.encode(file.getAbsolutePath());
    }

    @Nullable
    private NanoHTTPD.Response processViewFile(Map<String, String> params) {
        String viewPath = params.get("view");
        File file = new File(viewPath);
        String mime = getMimeType(params, file);
        try {
            NanoHTTPD.Response response = new NanoHTTPD.Response(NanoHTTPD.Response.Status.OK, mime, new FileInputStream(file));
            response.addHeader("Content-Disposition", "filename=\"" + file.getName() + "\"");
            return response;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
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

    @Nullable
    private NanoHTTPD.Response processDownloadFile(Map<String, String> parms) {
        String viewPath = parms.get("download");
        File file = new File(viewPath);
        String mime = "application/octet-stream";
        try {
            NanoHTTPD.Response response = new NanoHTTPD.Response(NanoHTTPD.Response.Status.OK, mime, new FileInputStream(file));
            response.addHeader("Content-Disposition", "attachment; filename=\"" + file.getName() + "\"");
            return response;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    private boolean isProcessable(NanoHTTPD.IHTTPSession session) {
        return session.getUri().startsWith("/filexp");
    }


}
