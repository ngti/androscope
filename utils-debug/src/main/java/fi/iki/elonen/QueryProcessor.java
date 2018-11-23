package fi.iki.elonen;

import android.database.Cursor;

import java.io.IOException;

/**
 * Processes a query started from the DB explorer.
 */
public interface QueryProcessor {

    Cursor process(NanoHTTPD.IHTTPSession session, StringBuilder html, String query) throws IOException;

    String formatHtmlData(NanoHTTPD.IHTTPSession session, String data, String columnName);

    String formatHtmlColumn(NanoHTTPD.IHTTPSession session, String data);

}
