package nl.ngti.androscope.responses.database

import android.net.Uri

class DbUri(
        private val uri: Uri
) {
    val databaseName: String
        get() = uri.host!!

    val tableName: String?
        get() = uri.getQueryParameter("table")

    val query: String?
        get() = uri.getQueryParameter("query")

    init {
        check(uri.scheme == SCHEME) {
            "Invalid ${javaClass.simpleName} scheme: ${uri.scheme}"
        }
    }

    companion object {
        const val SCHEME = "database"
    }
}
