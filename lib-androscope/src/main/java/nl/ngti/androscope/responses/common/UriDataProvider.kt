package nl.ngti.androscope.responses.common

import android.database.Cursor
import android.net.Uri

internal interface UriDataProvider {

    fun query(
            uri: Uri,
            projection: Array<String>? = null,
            selection: String? = null,
            selectionArgs: Array<String>? = null,
            sortOrder: String? = null
    ): Cursor?
}
