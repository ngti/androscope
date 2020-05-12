package nl.ngti.androscope.responses.provider

import android.database.Cursor

internal class ResponseCursor(
        private val sourceCursor: Cursor
) : Cursor by sourceCursor {

    operator fun get(columnIndex: Int): String? {
        return when (sourceCursor.getType(columnIndex)) {
            Cursor.FIELD_TYPE_BLOB -> "[BLOB]"
            else -> sourceCursor.getString(columnIndex)
        }
    }
}
