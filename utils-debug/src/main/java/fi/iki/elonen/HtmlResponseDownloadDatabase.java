package fi.iki.elonen;

import android.content.Context;
import java.io.File;

import static fi.iki.elonen.filebrowser.HtmlResponseShowFolder.getUrlDownload;

/**
 * Shows a link to download the database.
 */
public class HtmlResponseDownloadDatabase extends BaseDatabaseHtmlResponse {

    public HtmlResponseDownloadDatabase(Context context, String databaseName) {
        super(context, databaseName);
    }

    @Override
    public void showHtmlHeader(NanoHTTPD.IHTTPSession session, StringBuilder html) {
        File dbFile = mContext.getDatabasePath(mDatabaseName).getAbsoluteFile();
        html.append("<p><a href='" +
            getUrlDownload(dbFile) +
            "'>Download Database Here</a></p>");
    }

    @Override
    public NanoHTTPD.Response getResponse(NanoHTTPD.IHTTPSession session) {
        return null;
    }

}
