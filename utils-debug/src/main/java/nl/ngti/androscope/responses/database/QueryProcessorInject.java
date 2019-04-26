package nl.ngti.androscope.responses.database;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import fi.iki.elonen.NanoHTTPD;
import java.io.IOException;

/**
 * Process a sql query that is not a SELECT.
 */
@SuppressLint("DefaultLocale")
public class QueryProcessorInject implements QueryProcessor {
    private final SQLiteDatabase mDatabase;

    public QueryProcessorInject(SQLiteDatabase database) {
        mDatabase = database;
    }

    @Override
    public Cursor process(NanoHTTPD.IHTTPSession session, StringBuilder html, String query) throws IOException {
        if (isProcessable(session, query)) {
            mDatabase.execSQL(query);
            html.append("<b>Executed query: </b>" + TextUtils.htmlEncode(query));
        }
        return null;
    }

    private boolean isProcessable(NanoHTTPD.IHTTPSession session, String query) {
        return "/dbxp/injectQuery".equals(session.getUri()) && query.length() > 0 && !query.toLowerCase().startsWith("select") && !query.toLowerCase().startsWith("content:");
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
