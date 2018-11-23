package fi.iki.elonen.filebrowser;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.webkit.MimeTypeMap;
import fi.iki.elonen.HtmlResponse;
import fi.iki.elonen.NanoHTTPD;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;

/**
 * Shows a file explorer to access quickly the private file storage of the app.
 */
public class HtmlResponseViewFile implements HtmlResponse {

    private final Context mContext;

    public HtmlResponseViewFile(Context context) {
        mContext = context;
    }

    @Override
    public void showHtmlHeader(NanoHTTPD.IHTTPSession session, StringBuilder html) {
//        html.append("<p><a href='/filexp'>File Explorer</a></p>");
    }

    @Override
    public NanoHTTPD.Response getResponse(NanoHTTPD.IHTTPSession session) {
        final Map<String, String> parms = session.getParms();
        return processViewFile(parms);
    }

    @Nullable
    private NanoHTTPD.Response processViewFile(Map<String, String> params) {
        String viewPath = params.get("view");
        if (viewPath != null && viewPath.startsWith("asset:")) {
            String assetPath = viewPath.substring("asset:".length());
            NanoHTTPD.Response response = null;
            try {
                response = new NanoHTTPD.Response(NanoHTTPD.Response.Status.OK, null, mContext.getAssets().open(assetPath));
//                response.addHeader("Content-Disposition", "filename=\"" + file.getName() + "\"");
                return response;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
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
                mime = getMimeType2(uri.toString());
            }
            if (mime == null) {
                mime = "text/plain";
            }
        }
        return mime;
    }

    public static String getMimeType2(String url) {
        String type = null;
        String extension = MimeTypeMap.getFileExtensionFromUrl(url);
        if (extension != null) {
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        }
        return type;
    }

}
