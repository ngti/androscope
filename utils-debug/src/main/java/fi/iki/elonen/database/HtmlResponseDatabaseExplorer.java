package fi.iki.elonen.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import fi.iki.elonen.NanoHTTPD;
import java.io.IOException;
import java.net.URLDecoder;
import java.util.HashSet;
import java.util.Set;

import static fi.iki.elonen.DeviceExplorerHttpServer.showFooter;
import static fi.iki.elonen.DeviceExplorerHttpServer.showHeader;

/**
 * Shows a link to a database explorer.
 */
public class HtmlResponseDatabaseExplorer extends BaseDatabaseHtmlResponse {

    private Set<QueryProcessor> mQueryProcessors;

    public HtmlResponseDatabaseExplorer(Context context, String databaseName) {
        super(context, databaseName);
    }

    private synchronized Set<QueryProcessor> getQueryProcessors() {
        if (mQueryProcessors == null) {
            SQLiteOpenHelper sqLiteOpenHelper = new SQLiteOpenHelperImpl(mContext, mDatabaseName);
            mQueryProcessors = new HashSet<>();
            mQueryProcessors.add(new QueryProcessorTable(sqLiteOpenHelper));
            mQueryProcessors.add(new QueryProcessorSelect(sqLiteOpenHelper));
            mQueryProcessors.add(new QueryProcessorContent(mContext));
            mQueryProcessors.add(new QueryProcessorInject(sqLiteOpenHelper));
        }
        return mQueryProcessors;
    }

    @Override
    public void showHtmlHeader(NanoHTTPD.IHTTPSession session, StringBuilder html) {
        html.append("<p><a href='/dbxp'>Explore Database</a></p>");
    }

    @Override
    public NanoHTTPD.Response getResponse(NanoHTTPD.IHTTPSession session) {
        try {
            if (isProcessable(session)) {
                final String query = getQuery(session);

                final StringBuilder html = new StringBuilder();
                html.append("<html><head><meta charset=\"UTF-8\"></head><body>");
                showCSS(html);
                showHeader(session, html);

                showQueryForm(html, query);
                try {
                    QueryToHtml.showQuery(session, query, html, getQueryProcessors());
                } catch (SQLiteException e) {
                    showError(html, e);
                }

                showFooter(html);
                html.append("</body></html>");
                return new NanoHTTPD.Response(html.toString());

            }
        } catch (IOException exc) {
            exc.printStackTrace();
        }
        return null;
    }

    private void showError(StringBuilder html, SQLiteException e) {
        html.append("Error:\n");
        html.append(e.getMessage());
    }

    private boolean isProcessable(NanoHTTPD.IHTTPSession session) {
        return session.getUri().startsWith("/dbxp");
    }

    private void showQueryForm(StringBuilder html, String query) throws IOException {
        html.append("<form action='/dbxp/injectQuery' method=\"post\">")
            .append("<textarea name=\"query\" rows=\"10\" cols=\"300\" placeholder=\"");
        html.append("Maximum 500 lines returned.\n\nWrite here your SQL query\n\nOr your PROVIDER url, e.g. content://com.android.contacts/data");
        html.append("\n\nThe provider can be explored also using: adb shell content query --uri content://com.android.contacts/data");
        html.append("\">");
        if (!TextUtils.isEmpty(query)) {
            html.append(query);
        }
        html.append("</textarea>")
            .append("<br/>")
            .append("<button type='submit'>Execute Query</button>")
            .append("</form>");
    }

    @NonNull
    private String getQuery(NanoHTTPD.IHTTPSession session) throws IOException {
        int size = session.getInputStream().available();
        if (size > 0) {
            byte[] data = new byte[size];
            session.getInputStream().read(data);
            String sql = URLDecoder.decode(new String(data), "UTF-8");
            return sql.substring("query=".length()).trim();
        }
        return "";
    }

    private void showCSS(StringBuilder html) {

        // style created with http://www.csstablegenerator.com/

        html.append("<style>");
        html.append(".cursor {\n" +
            "\tmargin:0px;padding:0px;\n" +
            "\twidth:100%;\n" +
            "\tbox-shadow: 10px 10px 5px #888888;\n" +
            "\tborder:1px solid #000000;\n" +
            "\t\n" +
            "\t-moz-border-radius-bottomleft:0px;\n" +
            "\t-webkit-border-bottom-left-radius:0px;\n" +
            "\tborder-bottom-left-radius:0px;\n" +
            "\t\n" +
            "\t-moz-border-radius-bottomright:0px;\n" +
            "\t-webkit-border-bottom-right-radius:0px;\n" +
            "\tborder-bottom-right-radius:0px;\n" +
            "\t\n" +
            "\t-moz-border-radius-topright:0px;\n" +
            "\t-webkit-border-top-right-radius:0px;\n" +
            "\tborder-top-right-radius:0px;\n" +
            "\t\n" +
            "\t-moz-border-radius-topleft:0px;\n" +
            "\t-webkit-border-top-left-radius:0px;\n" +
            "\tborder-top-left-radius:0px;\n" +
            "}.cursor table{\n" +
            "    border-collapse: collapse;\n" +
            "        border-spacing: 0;\n" +
            "\twidth:100%;\n" +
            //"\theight:100%;\n" +
            "\tmargin:0px;padding:0px;\n" +
            "}.cursor tr:last-child td:last-child {\n" +
            "\t-moz-border-radius-bottomright:0px;\n" +
            "\t-webkit-border-bottom-right-radius:0px;\n" +
            "\tborder-bottom-right-radius:0px;\n" +
            "}\n" +
            ".cursor table tr:first-child td:first-child {\n" +
            "\t-moz-border-radius-topleft:0px;\n" +
            "\t-webkit-border-top-left-radius:0px;\n" +
            "\tborder-top-left-radius:0px;\n" +
            "}\n" +
            ".cursor table tr:first-child td:last-child {\n" +
            "\t-moz-border-radius-topright:0px;\n" +
            "\t-webkit-border-top-right-radius:0px;\n" +
            "\tborder-top-right-radius:0px;\n" +
            "}.cursor tr:last-child td:first-child{\n" +
            "\t-moz-border-radius-bottomleft:0px;\n" +
            "\t-webkit-border-bottom-left-radius:0px;\n" +
            "\tborder-bottom-left-radius:0px;\n" +
            "}.cursor tr:hover td{\n" +
            "\t\n" +
            "}\n" +
            ".cursor tr:nth-child(odd){ background-color:#e5e5e5; }\n" +
            ".cursor tr:nth-child(even)    { background-color:#ffffff; }.cursor td{\n" +
            "\tvertical-align:middle;\n" +
            "\t\n" +
            "\t\n" +
            "\tborder:1px solid #000000;\n" +
            "\tborder-width:0px 1px 1px 0px;\n" +
            "\ttext-align:left;\n" +
            "\tpadding:7px;\n" +
            "\tfont-size:14px;\n" +
            "\tfont-family:Arial;\n" +
            "\tfont-weight:normal;\n" +
            "\tcolor:#000000;\n" +
            "}.cursor tr:last-child td{\n" +
            "\tborder-width:0px 1px 0px 0px;\n" +
            "}.cursor tr td:last-child{\n" +
            "\tborder-width:0px 0px 1px 0px;\n" +
            "}.cursor tr:last-child td:last-child{\n" +
            "\tborder-width:0px 0px 0px 0px;\n" +
            "}\n" +
            ".cursor tr:first-child td{\n" +
            "\t\tbackground:-o-linear-gradient(bottom, #cccccc 5%, #b2b2b2 100%);\tbackground:-webkit-gradient( linear, left top, left bottom, "
            + "color-stop(0.05, #cccccc), color-stop(1, #b2b2b2) );\n"
            +
            "\tbackground:-moz-linear-gradient( center top, #cccccc 5%, #b2b2b2 100% );\n" +
            "\tfilter:progid:DXImageTransform.Microsoft.gradient(startColorstr=\"#cccccc\", endColorstr=\"#b2b2b2\");\tbackground: "
            + "-o-linear-gradient(top,#cccccc,b2b2b2);\n"
            +
            "\n" +
            "\tbackground-color:#cccccc;\n" +
            "\tborder:0px solid #000000;\n" +
            "\ttext-align:center;\n" +
            "\tborder-width:0px 0px 1px 1px;\n" +
            "\tfont-size:19px;\n" +
            "\tfont-family:Arial;\n" +
            "\tfont-weight:bold;\n" +
            "\tcolor:#000000;\n" +
            "}\n" +
            ".cursor tr:first-child:hover td{\n" +
            "\tbackground:-o-linear-gradient(bottom, #cccccc 5%, #b2b2b2 100%);\tbackground:-webkit-gradient( linear, left top, left bottom, "
            + "color-stop(0.05, #cccccc), color-stop(1, #b2b2b2) );\n"
            +
            "\tbackground:-moz-linear-gradient( center top, #cccccc 5%, #b2b2b2 100% );\n" +
            "\tfilter:progid:DXImageTransform.Microsoft.gradient(startColorstr=\"#cccccc\", endColorstr=\"#b2b2b2\");\tbackground: "
            + "-o-linear-gradient(top,#cccccc,b2b2b2);\n"
            +
            "\n" +
            "\tbackground-color:#cccccc;\n" +
            "}\n" +
            ".cursor tr:first-child td:first-child{\n" +
            "\tborder-width:0px 0px 1px 0px;\n" +
            "}\n" +
            ".cursor tr:first-child td:last-child{\n" +
            "\tborder-width:0px 0px 1px 1px;\n" +
            "}");
        html.append("</style>");
    }

    private static class SQLiteOpenHelperImpl extends SQLiteOpenHelper {

        public SQLiteOpenHelperImpl(Context context, String name) {
            super(context, name, null, 1);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            // Do nothing
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            // Do nothing
        }
    }
}
