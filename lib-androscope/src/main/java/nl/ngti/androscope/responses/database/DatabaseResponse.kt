package nl.ngti.androscope.responses.database

import android.content.Context
import android.database.DatabaseUtils
import android.text.format.Formatter
import com.google.gson.Gson
import fi.iki.elonen.NanoHTTPD
import nl.ngti.androscope.responses.common.MultiSchemeDataProvider
import nl.ngti.androscope.responses.common.RequestResult
import nl.ngti.androscope.responses.common.toDownloadResponse
import nl.ngti.androscope.server.SessionParams
import nl.ngti.androscope.server.dbUri
import nl.ngti.androscope.server.get
import nl.ngti.androscope.server.readSql
import nl.ngti.androscope.utils.AndroscopeMetadata
import java.io.File

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
                            description = "Configured in manifest",
                            error = errorMessage
                    )
                }
            }
        }

        context.databaseList()
                .mapTo(HashSet()) {
                    getMainDatabaseFile(context.getDatabasePath(it))
                }
                .filter {
                    it.exists() && it.isFile
                }
                .sorted()
                .mapTo(result) {
                    Database(it.name)
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
            DatabaseInfo(
                    true,
                    fullPath = databaseFile.absolutePath,
                    size = size
            ).apply {
                fillDatabaseInfo(uri, this)
            }
        } catch (e: Throwable) {
            DatabaseInfo(false, errorMessage = e.message)
        }
    }

    private fun fillDatabaseInfo(uri: DbUri, result: DatabaseInfo) {
        databaseManager.query(
                uri,
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

    fun executeSql(sessionParams: SessionParams): RequestResult {
        return try {
            val sql = sessionParams.readSql(jsonConverter)
            databaseManager.executeSql(sessionParams.dbUri, sql)
            RequestResult.success("Executed")
        } catch (e: Throwable) {
            return RequestResult.error(e.message ?: e.javaClass.name)
        }
    }

    fun getDatabaseToDownload(sessionParams: SessionParams): NanoHTTPD.Response? {
        val file = sessionParams.dbUri.toConfig(context).databaseFile
        return file.toDownloadResponse()
    }

    fun uploadDatabase(sessionParams: SessionParams): RequestResult? {
        val databaseFile = sessionParams.dbUri.toConfig(context).databaseFile
        val body = HashMap<String, String>()
        sessionParams.parseBody(body)
        body.forEach { (key, value) ->
            val bodyFile = File(value)

            val replaceStrategy = if (databaseFile.exists())
                BackupOriginalStrategy() else NoBackupStrategy()

            return replaceStrategy.replace(bodyFile, databaseFile, key)
        }
        return RequestResult.error("No input file")
    }

    fun getSql(sessionParams: SessionParams): String {
        val name = sessionParams["name"]!!
        return databaseManager.query(
                sessionParams.dbUri,
                tableName = TABLE_SQLITE_MASTER,
                projection = arrayOf(
                        /* 0 */ "sql"
                ),
                selection = "name=?",
                selectionArgs = arrayOf(name)
        )?.use {
            if (it.moveToFirst()) {
                it.getString(0)
            } else null
        } ?: "Cannot find '$name' sql"
    }
}

private abstract class ReplaceStrategy {

    fun replace(sourceFile: File, destFile: File, sourceName: String): RequestResult {
        onBeforeReplace(sourceFile, destFile)?.let {
            return it
        }
        if (!sourceFile.renameTo(destFile)) {
            onReplaceFailed(destFile)
            return RequestResult.error("Failed to replace database with: $sourceName")
        }
        onReplaceSuccess()
        return RequestResult.success()
    }

    open fun onBeforeReplace(sourceFile: File, destFile: File): RequestResult? {
        // Override in ancestors if needed
        return null
    }

    open fun onReplaceSuccess() {
        // Override in ancestors if needed
    }

    open fun onReplaceFailed(destFile: File) {
        // Override in ancestors if needed
    }
}

private class NoBackupStrategy : ReplaceStrategy()

private class BackupOriginalStrategy : ReplaceStrategy() {

    private lateinit var backup: FileBatch

    override fun onBeforeReplace(sourceFile: File, destFile: File): RequestResult? {
        val tempDir = createTempDir(directory = destFile.parentFile)

        backup = collectAllDatabaseFiles(destFile).moveTo(tempDir)
                ?: return RequestResult.error(
                        "Cannot backup original database into: ${tempDir.absolutePath}." +
                                " There might be active writes into it."
                )
        return null
    }

    override fun onReplaceFailed(destFile: File) {
        // Attempt to restore old destination database
        backup.moveTo(destFile.parentFile!!, revertIfFailed = false)
    }

    override fun onReplaceSuccess() {
        backup.parentDirectory.deleteRecursively()
    }
}
