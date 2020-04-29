package nl.ngti.androscope.responses.database

import android.content.Context
import android.database.DatabaseUtils
import android.text.format.Formatter
import com.google.gson.Gson
import fi.iki.elonen.NanoHTTPD
import nl.ngti.androscope.responses.common.MultiSchemeDataProvider
import nl.ngti.androscope.responses.common.toDownloadResponse
import nl.ngti.androscope.server.SessionParams
import nl.ngti.androscope.server.dbUri
import nl.ngti.androscope.server.readSql
import nl.ngti.androscope.utils.AndroscopeMetadata

private const val TABLE_SQLITE_MASTER = "sqlite_master"

class DatabaseResponse(
        private val context: Context,
        private val metadata: AndroscopeMetadata,
        uriDataProvider: MultiSchemeDataProvider,
        private val jsonConverter: Gson
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

    fun getTitle(sessionParams: SessionParams): String {
        return sessionParams.dbUri.toConfig(context).name
    }

    fun getInfo(sessionParams: SessionParams): DatabaseInfo {
        val uri = sessionParams.dbUri
        val config = uri.toConfig(context)
        val databaseFile = config.databaseFile
        val size = Formatter.formatFileSize(context, databaseFile.length())

        return try {
            DatabaseInfo(true,
                    fullPath = databaseFile.absolutePath,
                    size = size).apply {
                fillDatabaseInfo(uri, this)
            }
        } catch (e: Throwable) {
            DatabaseInfo(false, errorMessage = e.message)
        }
    }

    private fun fillDatabaseInfo(uri: DbUri, result: DatabaseInfo) {
        databaseManager.query(uri,
                tableName = TABLE_SQLITE_MASTER,
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
        result.tables += TABLE_SQLITE_MASTER
    }

    fun getCanQuery(sessionParams: SessionParams): Boolean {
        val sql = sessionParams.readSql(jsonConverter)
        val type = DatabaseUtils.getSqlStatementType(sql)
        return type == DatabaseUtils.STATEMENT_SELECT
    }

    fun executeSql(sessionParams: SessionParams): ExecuteSqlResult {
        return try {
            val sql = sessionParams.readSql(jsonConverter)
            databaseManager.executeSql(sessionParams.dbUri, sql)
            ExecuteSqlResult(true, "Executed")
        } catch (e: Throwable) {
            return ExecuteSqlResult(false, e.message ?: "")
        }
    }

    fun getDatabaseToDownload(sessionParams: SessionParams): NanoHTTPD.Response? {
        val file = sessionParams.dbUri.toConfig(context).databaseFile
        return file.toDownloadResponse()
    }
}
