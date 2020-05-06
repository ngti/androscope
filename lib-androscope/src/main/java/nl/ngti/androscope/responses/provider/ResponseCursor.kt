package nl.ngti.androscope.responses.provider

import android.database.Cursor

class ResponseCursor(
        private val sourceCursor: Cursor
) : Cursor by sourceCursor {

    operator fun get(columnIndex: Int): String {
        return when (getType(columnIndex)) {
            Cursor.FIELD_TYPE_NULL -> "null"
            Cursor.FIELD_TYPE_BLOB -> "[BLOB]"
            else -> getString(columnIndex)
        }
    }
}
