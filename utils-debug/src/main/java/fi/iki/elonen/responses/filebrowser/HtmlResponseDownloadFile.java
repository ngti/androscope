package fi.iki.elonen.responses.filebrowser;

import android.os.Bundle;
import android.support.annotation.Nullable;
import fi.iki.elonen.HttpResponse;
import fi.iki.elonen.NanoHTTPD;
import fi.iki.elonen.menu.Menu;
import fi.iki.elonen.menu.MenuItem;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Map;

/**
 * Shows a file explorer to access quickly the private file storage of the app.
 */
public class HtmlResponseDownloadFile implements HttpResponse {

    @Override
    public boolean isEnabled(Bundle metadata) {
        return true;
    }

    @Override
    public MenuItem getMenuItem() {
        return null;
    }

    @Override
    public NanoHTTPD.Response getResponse(NanoHTTPD.IHTTPSession session, Menu menu) {
        return processDownloadFile(session.getParms());
    }

    @Nullable
    private NanoHTTPD.Response processDownloadFile(Map<String, String> parms) {
        String viewPath = parms.get("download");
        File file = new File(viewPath);
        String mime = "application/octet-stream";
        try {
            NanoHTTPD.Response response = NanoHTTPD.newChunkedResponse(NanoHTTPD.Response.Status.OK, mime, new FileInputStream(file));
            response.addHeader("Content-Disposition", "attachment; filename=\"" + file.getName() + "\"");
            return response;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

}
