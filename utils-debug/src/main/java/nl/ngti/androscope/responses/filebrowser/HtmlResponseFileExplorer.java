package nl.ngti.androscope.responses.filebrowser;

import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import nl.ngti.androscope.responses.HttpResponse;
import fi.iki.elonen.NanoHTTPD;
import nl.ngti.androscope.menu.Menu;
import nl.ngti.androscope.menu.MenuItem;
import java.io.File;
import java.io.IOException;
import java.util.Map;

import static android.os.Environment.getExternalStoragePublicDirectory;
import static nl.ngti.androscope.responses.filebrowser.HtmlResponseShowFolder.getUrlFolder;

/**
 * Shows a file explorer to access quickly the private file storage of the app.
 */
public class HtmlResponseFileExplorer implements HttpResponse {

    private final Context mContext;

    public HtmlResponseFileExplorer(Context context) {
        mContext = context;
    }

    @Override
    public boolean isEnabled(Bundle metadata) {
        return true;
    }

    @Override
    public MenuItem getMenuItem() {
        return new MenuItem("File Explorer", null)
            .subItem("Application Data", getUrlFolder(new File(mContext.getApplicationInfo().dataDir)))
            .subItem("External Storage", getUrlFolder(Environment.getExternalStorageDirectory()))
            .subItem("Root Directory", getUrlFolder(Environment.getRootDirectory()))
            .subItem("-", null)
            .subItem("Downloads", getUrlFolder(getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)))
            .subItem("Photos", getUrlFolder(getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)))
            .subItem("Movies", getUrlFolder(getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES)))
            .subItem("Pictures", getUrlFolder(getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)))
            .subItem("Music", getUrlFolder(getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC)));
    }

    @Override
    public NanoHTTPD.Response getResponse(NanoHTTPD.IHTTPSession session, Menu menu) throws IOException {
        if (isProcessable(session)) {
            final Map<String, String> parms = session.getParms();
            return FileResponseFactory.from(mContext, parms).getResponse(session, menu);
        }
        return null;
    }

    private boolean isProcessable(NanoHTTPD.IHTTPSession session) {
        return session.getUri().startsWith("/filexp");
    }


}
