package fi.iki.elonen;

import android.database.Cursor;

import java.io.IOException;
import java.util.Set;

import static android.text.TextUtils.htmlEncode;
import static fi.iki.elonen.HtmlUtils.cell;
import static fi.iki.elonen.HtmlUtils.cellHeader;
import static fi.iki.elonen.HtmlUtils.endRow;

/**
 * Dumps a Cursor to html.
 */
public class QueryToHtml {
    static void showQuery(NanoHTTPD.IHTTPSession session, String query, StringBuilder html, Set<QueryProcessor> queryProcessors) throws IOException {
        final Cursor cursor = getCursorFromProcessors(session, html, query, queryProcessors);

        if (cursor != null) {
            html.append("Rows: " + cursor.getCount());
            html.append("<div class='cursor'> <table border='1'>");
            showCursor(session, html, cursor, queryProcessors);
            html.append("</table></div>");
        }
    }

    static Cursor getCursorFromProcessors(NanoHTTPD.IHTTPSession session, StringBuilder html, String query, Set<QueryProcessor> queryProcessors) throws IOException {
        for (QueryProcessor processor : queryProcessors) {
            Cursor cursor = processor.process(session, html, query);
            if (cursor != null) {
                return cursor;
            }
        }
        return null;
    }

    static void showCursor(NanoHTTPD.IHTTPSession session, final StringBuilder html, final Cursor cursor, Set<QueryProcessor> queryProcessors) throws IOException {
        int line = 0;
        int columns = 0;
        while (cursor.moveToNext()) {
            if (line == 0) {
                columns = cursor.getColumnCount();
                showCursorHeader(session, html, cursor, columns, queryProcessors);
            }
            showCursorRow(session, html, cursor, columns, queryProcessors, line);
            line++;
            if(line > 500){
                break;
            }
        }
        cursor.close();
    }

    static void showCursorHeader(NanoHTTPD.IHTTPSession session, final StringBuilder html, final Cursor cursor, final int columns, Set<QueryProcessor> queryProcessors) throws IOException {
        html.append(HtmlUtils.startRow(""));
        for (int i = 0; i < columns; i++) {
            String name = cursor.getColumnName(i);
            String htmlName = getHtmlColumnNameFromProcessors(session, name, i, queryProcessors);
            if (htmlName != null) {
                html.append(cellHeader(htmlName));
            } else {
                html.append(cellHeader(htmlEncode(name)));
            }
        }
        html.append(endRow());
    }

    static void showCursorRow(NanoHTTPD.IHTTPSession session, final StringBuilder html, final Cursor cursor, final int columns, Set<QueryProcessor> queryProcessors, int line) throws IOException {
        html.append(HtmlUtils.startRow(line % 2 != 0 ? "alt" : ""));
        for (int i = 0; i < columns; i++) {
            final String data = getData(cursor, i);
            final String columnName = cursor.getColumnName(i);
            String htmlData = getHtmlRowDataFromProcessors(session, data, columnName, queryProcessors);
            if (htmlData != null) {
                html.append(cell(htmlData));
            } else {
                html.append(cell(htmlEncode("" + data)));
            }
        }
        html.append(endRow());
    }

    static String getData(Cursor cursor, int i) {
        final String data;
        if (cursor.getType(i) != Cursor.FIELD_TYPE_BLOB) {
            data = cursor.getString(i);
        } else {
            data = "[BLOB]";
        }
        return data;
    }

    static String getHtmlRowDataFromProcessors(NanoHTTPD.IHTTPSession session, String data, String columnName, Set<QueryProcessor> queryProcessors) throws IOException {
        for (QueryProcessor processor : queryProcessors) {
            String htmlData = processor.formatHtmlData(session, data, columnName);
            if (htmlData != null) {
                return htmlData;
            }
        }
        return null;
    }

    static String getHtmlColumnNameFromProcessors(NanoHTTPD.IHTTPSession session, String columnName, int column, Set<QueryProcessor> queryProcessors) throws IOException {
        for (QueryProcessor processor : queryProcessors) {
            String htmlData = processor.formatHtmlColumn(session, columnName);
            if (htmlData != null) {
                return htmlData;
            }
        }
        return null;
    }
}
