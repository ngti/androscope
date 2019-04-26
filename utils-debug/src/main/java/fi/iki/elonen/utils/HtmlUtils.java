package fi.iki.elonen.utils;

import androidx.annotation.NonNull;

/**
 * Shortcuts for generating html.
 */
public class HtmlUtils {

    @NonNull
    public static String cellHeader(String html) {
        return "<th>" + html + "</th>";
    }

    @NonNull
    public static String cell(String html) {
        return "<td>" + html + "</td>";
    }

    @NonNull
    public static String startRow(String style) {
        return "<tr class='" + style + "'>";
    }

    @NonNull
    public static String endRow() {
        return "</tr>";
    }

    @NonNull
    static String bold(String columnName) {
        return "<b>" + columnName + "</b>";
    }
}
