package fi.iki.elonen;

import android.database.Cursor;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;

import java.net.URLEncoder;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Process a query to show a table.
 */
public class QueryProcessorTable implements QueryProcessor {

    private static final String TYPE_COLUMN_NAME = "type";
    private static final String TBL_NAME_COLUMN_NAME = "tbl_name";
    private static final String NAME_COLUMN_NAME = "name";
    private static final Set<String> SUPPORTED_DB_TYPES;
    static {
        final HashSet<String> supportedDbTypes = new HashSet<>(2);
        supportedDbTypes.add("table");
        supportedDbTypes.add("view");

        SUPPORTED_DB_TYPES = Collections.unmodifiableSet(supportedDbTypes);
    }

    private final SQLiteOpenHelper mSql;
    private String mTable;
    private String mSort;
    private String mType;

    QueryProcessorTable(SQLiteOpenHelper sql) {
        mSql = sql;
    }

    @Override
    public Cursor process(NanoHTTPD.IHTTPSession session, StringBuilder html, String query) {
        if (isProcessable(session)) {
            final Map<String, String> parms = session.getParms();
            mTable = parms.get("table");
            if (mTable == null) {
                mTable = "sqlite_master";
            }
            if (parms.get("sort") != null) {
                mSort = parms.get("sort");
            } else {
                mSort = "rowid DESC";
            }
            html.append("Table: ").append(TextUtils.htmlEncode(mTable)).append("<br>");
            return mSql.getReadableDatabase().rawQuery("SELECT rowid, * FROM " + mTable + " order by " + mSort, null);
        }
        return null;
    }

    private boolean isProcessable(NanoHTTPD.IHTTPSession session) {
        return !"/dbxp/injectQuery".equals(session.getUri());
    }

    @Override
    public String formatHtmlData(NanoHTTPD.IHTTPSession session, String data, String columnName) {
        if (isProcessable(session) && data != null) {
            if ("sqlite_master".equals(mTable)) {
                if (TBL_NAME_COLUMN_NAME.equals(columnName)
                        || (NAME_COLUMN_NAME.equals(columnName) && mType != null && SUPPORTED_DB_TYPES.contains(mType))) {
                    return "<a href='/dbxp/?table=" + URLEncoder.encode(data)
                            + "'>" + TextUtils.htmlEncode(data) + "</a>";
                }
                if (TYPE_COLUMN_NAME.equals(columnName)) {
                    mType = data;
                } else {
                    mType = null;
                }
            }
        }
        return null;
    }

    @Override
    public String formatHtmlColumn(NanoHTTPD.IHTTPSession session, String columnName) {
        if (isProcessable(session)) {
            return
                    "<a href='/dbxp/?table=" + URLEncoder.encode(mTable)
                            + "&sort=" + columnName + " " + (mSort.equals(columnName + " ASC") ? "DESC" : "ASC")
                            + "'>"
                            + "<b>"
                            + TextUtils.htmlEncode(columnName)
                            + "</b>"
                            + "</a>"
                            + "</td>";
        }
        return null;
    }
}
