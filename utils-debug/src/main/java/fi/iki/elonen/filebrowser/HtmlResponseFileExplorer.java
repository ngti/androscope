package fi.iki.elonen.filebrowser;

import android.content.Context;
import fi.iki.elonen.HtmlResponse;
import fi.iki.elonen.NanoHTTPD;
import java.util.Map;

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
            return FileResponseFactory.from(mContext, parms).getResponse(session);
        }
        return null;
    }

    private boolean isProcessable(NanoHTTPD.IHTTPSession session) {
        return session.getUri().startsWith("/filexp");
    }


}
