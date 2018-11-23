package fi.iki.elonen;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;

import java.io.IOException;

/**
 * Process a SELECT sql query.
 */
@SuppressLint("DefaultLocale")
public class QueryProcessorSelect implements QueryProcessor {

    private final SQLiteOpenHelper mSql;

    public QueryProcessorSelect(SQLiteOpenHelper sql) {
        mSql = sql;
    }

    @Override
    public Cursor process(NanoHTTPD.IHTTPSession session, StringBuilder html, String query) throws IOException {
        if (isProcessable(session, query)) {
            html.append("Table: ").append(TextUtils.htmlEncode(query)).append("<br>");
            return mSql.getReadableDatabase().rawQuery(query, null);
        }
        return null;
    }

    private boolean isProcessable(NanoHTTPD.IHTTPSession session, String query) {
        return "/dbxp/injectQuery".equals(session.getUri()) && query.length() > 0 && query.toLowerCase().startsWith("select");
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


