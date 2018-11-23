package fi.iki.elonen.filebrowser;

import android.support.annotation.Nullable;
import fi.iki.elonen.HtmlResponse;
import fi.iki.elonen.NanoHTTPD;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Map;

/**
 * Shows a file explorer to access quickly the private file storage of the app.
 */
public class HtmlResponseDownloadFile implements HtmlResponse {

    @Override
    public void showHtmlHeader(NanoHTTPD.IHTTPSession session, StringBuilder html) {
        html.append("<p><a href='/filexp'>File Explorer</a></p>");
    }

    @Override
    public NanoHTTPD.Response getResponse(NanoHTTPD.IHTTPSession session) {
        return processDownloadFile(session.getParms());
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

}
