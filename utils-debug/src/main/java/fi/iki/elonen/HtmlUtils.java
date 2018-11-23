package fi.iki.elonen;

import android.support.annotation.NonNull;

/**
 * Shortcuts for generating html.
 */
public class HtmlUtils {

    @NonNull
    static String cellHeader(String html) {
        return "<th>" + html + "</th>";
    }

    @NonNull
    static String cell(String html) {
        return "<td>" + html + "</td>";
    }

    @NonNull
    static String startRow(String style) {
        return "<tr class='" + style + "'>";
    }

    @NonNull
    static String endRow() {
        return "</tr>";
    }

    @NonNull
    static String bold(String columnName) {
        return "<b>" + columnName + "</b>";
    }
}
