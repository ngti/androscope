package nl.ngti.androscope.responses.database

import android.content.Context
import android.text.format.Formatter
import nl.ngti.androscope.responses.common.MultiSchemeDataProvider
import nl.ngti.androscope.server.SessionParams
import nl.ngti.androscope.server.dbUri
import nl.ngti.androscope.utils.AndroscopeMetadata

class DatabaseResponse(
        private val context: Context,
        private val metadata: AndroscopeMetadata,
        uriDataProvider: MultiSchemeDataProvider
) {

    private val databaseManager = DatabaseManager(context).also {
        uriDataProvider.addProvider(DbUri.SCHEME, it)
    }

    fun getList(): List<Database> {
        val result = ArrayList<Database>()

        metadata.databaseName?.run {
            if (isNotBlank()) {
                result += DbConfig(context, this).run {
                    Database(
                            databaseName,
                            title = name,
                            description = "Set in manifest",
                            error = errorMessage
                    )
                }
            }
        }

        context.databaseList().forEach {
            result += Database(it)
        }
        return result
    }

    fun getInfo(sessionParams: SessionParams): DatabaseInfo {
        val uri = sessionParams.dbUri
        val config = DbConfig(context, uri.databaseName)
        val databaseFile = context.getDatabasePath(config.databasePath)
        val size = Formatter.formatFileSize(context, databaseFile.length())
        val result = DatabaseInfo(config.name, databaseFile.absolutePath, size)

        databaseManager.query(uri,
                tableName = "sqlite_master",
                projection = arrayOf(
                        /* 0 */ "name",
                        /* 1 */ "type"
                ),
                sortOrder = "type ASC, name ASC"
        )?.use { cursor ->
            while (cursor.moveToNext()) {
                val list = when (cursor.getString(1)) {
                    "table" -> result.tables
                    "view" -> result.views
                    "trigger" -> result.triggers
                    "index" -> result.indexes
                    else -> null
                }
                list?.apply {
                    this += cursor.getString(0)
                }
            }
        }

        return result
    }
}
