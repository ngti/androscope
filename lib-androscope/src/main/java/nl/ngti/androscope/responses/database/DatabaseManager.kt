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
        throw IllegalArgumentException("Invalid uri: $uri")
    }

    fun executeSql(uri: DbUri, sql: String) {
        getDatabase(uri).execSQL(sql)
    }

    private fun getDatabase(uri: DbUri): SQLiteDatabase {
        val dbConfig = uri.toConfig(context)
        val dbFile = dbConfig.databaseFile
        if (!dbFile.exists()) {
            throw IllegalStateException("Database ${dbFile.absolutePath} does not exist.")
        }
        if (!dbFile.isFile) {
            throw IllegalStateException("The specified path ${dbFile.absolutePath} is not a file.")
        }
        if (isAuxiliaryDatabaseFile(dbFile)) {
            throw IllegalStateException("The specified file ${dbFile.absolutePath} is an auxiliary database file. Please choose the main database.")
        }
        return context.openOrCreateDatabase(dbConfig.databasePath, 0, null)
    }
}
