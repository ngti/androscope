package nl.ngti.androscope.responses.database

import android.content.Context
import android.database.Cursor
import android.database.DataSetObserver
import android.database.sqlite.SQLiteDatabase
import android.net.Uri
import nl.ngti.androscope.common.log
import nl.ngti.androscope.responses.common.UriDataProvider
import java.io.File

internal class DatabaseManager(
        context: Context
) : UriDataProvider {

    private val connectionManager = ConnectionManager(context)

    fun query(
            uri: DbUri,
            tableName: String,
            projection: Array<String>? = null,
            selection: String? = null,
            selectionArgs: Array<String>? = null,
            sortOrder: String? = null
    ): Cursor? {
        return connectionManager.queryCursor(uri) {
            query(
                    tableName, projection, selection,
                    selectionArgs, null, null, sortOrder
            )
        }
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
            return connectionManager.queryCursor(dbUri) {
                rawQuery(it, null)
            }
        }
        throw IllegalArgumentException("Invalid uri: $uri")
    }

    fun executeSql(uri: DbUri, sql: String) {
        connectionManager.performOnDatabase(uri) {
            execSQL(sql)
        }
    }
}

private class ConnectionManager(
        private val context: Context
) {
    private val cachedConnections = HashMap<File, DatabaseHolder>()

    fun <R> performOnDatabase(uri: DbUri, block: SQLiteDatabase.() -> R): R {
        return getDatabase(uri.dbFile).use(block)
    }

    fun queryCursor(uri: DbUri, block: SQLiteDatabase.() -> Cursor?): Cursor? {
        val database = getDatabase(uri.dbFile)
        return block(database.use())?.let {
            UsageTrackingCursor(database, it)
        }
    }

    private fun getDatabase(dbFile: File): DatabaseHolder {
        return synchronized(cachedConnections) {
            cachedConnections.getOrPut(dbFile) {
                sweep()
                DatabaseHolder(openDb(dbFile))
            }
        }
    }

    private fun openDb(dbFile: File): SQLiteDatabase {
        with(dbFile) {
            if (!exists()) {
                throw IllegalStateException("Database $absolutePath does not exist.")
            }
            if (!isFile) {
                throw IllegalStateException("The specified path $absolutePath is not a file.")
            }
            if (isAuxiliaryDatabaseFile) {
                throw IllegalStateException("The specified file $absolutePath is an auxiliary database file. Please choose the main database.")
            }
        }

        log { "Opening new connection for $dbFile" }
        return context.openOrCreateDatabase(dbFile.absolutePath, 0, null)
    }

    private fun sweep() {
        synchronized(cachedConnections) {
            cachedConnections.iterator().run {
                while (hasNext()) {
                    val entry = next()
                    if (entry.value.cleanup()) {
                        remove()
                        this@ConnectionManager.log { "Removed cached connection for ${entry.key}" }
                    }
                }
            }
        }
    }

    private val DbUri.dbFile: File
        get() = toConfig(context).databaseFile

    private inner class UsageTrackingCursor(
            private val databaseHolder: DatabaseHolder,
            private val dbCursor: Cursor
    ) : Cursor by dbCursor, DataSetObserver() {

        init {
            dbCursor.registerDataSetObserver(this)
        }

        override fun onInvalidated() {
            databaseHolder.release()
        }
    }

    private inner class DatabaseHolder(
            private val database: SQLiteDatabase
    ) {

        @Volatile
        private var usages: Int = 0

        fun use(): SQLiteDatabase {
            usages++
            return database
        }

        fun <R> use(block: SQLiteDatabase.() -> R): R {
            return block(database)
        }

        fun release() {
            usages--
        }

        fun cleanup(): Boolean {
            if (usages <= 0) {
                database.close()
                return true
            }
            return false
        }
    }
}
