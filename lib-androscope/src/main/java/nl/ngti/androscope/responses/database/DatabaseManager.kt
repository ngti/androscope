package nl.ngti.androscope.responses.database

import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.net.Uri
import nl.ngti.androscope.responses.common.UriDataProvider

class DatabaseManager(
        private val context: Context
) : UriDataProvider {

    fun query(
            uri: DbUri,
            tableName: String,
            projection: Array<String>? = null,
            selection: String? = null,
            selectionArgs: Array<String>? = null,
            sortOrder: String? = null
    ): Cursor? {
        return getDatabase(uri).query(tableName, projection, selection,
                selectionArgs, null, null, sortOrder)
    }

    override fun query(
            uri: Uri,
            projection: Array<String>?,
            selection: String?,
            selectionArgs: Array<String>?,
            sortOrder: String?
    ): Cursor? {
        val dbUri = DbUri(uri)
        dbUri.tableName?.let {
            return query(
                    dbUri,
                    it,
                    projection = projection,
                    selection = selection,
                    selectionArgs = selectionArgs,
                    sortOrder = sortOrder
            )
        }
        dbUri.query?.let {
            return getDatabase(dbUri).rawQuery(it, null)
        }
        throw IllegalArgumentException("Invalid uri: $uri");
    }

    private fun getDatabase(uri: DbUri): SQLiteDatabase {
        val dbName = uri.databaseName
        val dbConfig = DbConfig(context, dbName)
        return context.openOrCreateDatabase(dbConfig.databasePath, 0, null)
    }
}
