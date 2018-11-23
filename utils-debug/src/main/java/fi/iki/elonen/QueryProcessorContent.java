package fi.iki.elonen;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import java.io.IOException;

/**
 * Process a query for a content provider.
 */
@SuppressLint("DefaultLocale")
public class QueryProcessorContent implements QueryProcessor {
    private final Context mContext;

    public QueryProcessorContent(Context context) {
        mContext = context;
    }

    @Override
    public Cursor process(NanoHTTPD.IHTTPSession session, StringBuilder html, String query) throws IOException {
        if (isProcessable(session, query)) {
            return mContext.getContentResolver().query(Uri.parse(query), null, null, null, null);
        }
        return null;
    }

    private boolean isProcessable(NanoHTTPD.IHTTPSession session, String query) {
        return "/dbxp/injectQuery".equals(session.getUri()) && query.length() > 0 && query.toLowerCase().startsWith("content:");
    }

    @Override
    public String formatHtmlData(NanoHTTPD.IHTTPSession session, String data, String columnName) {
        return null;
    }

    @Override
    public String formatHtmlColumn(NanoHTTPD.IHTTPSession session, String data) {
        return null;
    }

}
