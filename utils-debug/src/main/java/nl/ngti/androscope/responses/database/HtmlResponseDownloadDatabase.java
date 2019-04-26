package nl.ngti.androscope.responses.database;

import android.content.Context;
import fi.iki.elonen.NanoHTTPD;
import nl.ngti.androscope.menu.Menu;
import nl.ngti.androscope.menu.MenuItem;
import java.io.File;

import static nl.ngti.androscope.responses.filebrowser.HtmlResponseShowFolder.getUrlDownload;

/**
 * Shows a link to download the database.
 */
public class HtmlResponseDownloadDatabase extends BaseDatabaseHtmlResponse {

    public HtmlResponseDownloadDatabase(Context context) {
        super(context);
    }

    @Override
    public MenuItem getMenuItem() {
        File dbFile = mContext.getDatabasePath(mDatabaseName).getAbsoluteFile();
        return new MenuItem("Download Database", getUrlDownload(dbFile));
    }

    @Override
    public NanoHTTPD.Response getResponse(NanoHTTPD.IHTTPSession session, Menu menu) {
        return null;
    }

}
