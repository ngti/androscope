package nl.ngti.androscope.responses.database

import android.content.Context
import nl.ngti.androscope.utils.AndroscopeMetadata
import java.io.File

class DatabaseResponse(
        private val context: Context,
        private val metadata: AndroscopeMetadata
) {

    fun getList(): List<Database> {
        val result = ArrayList<Database>()

        metadata.databaseName?.run {
            if (isNotBlank()) {
                val manifestDbConfig = ManifestDbConfig(context, this)
                result += manifestDbConfig.toJsonDatabase()
            }
        }

        context.databaseList().forEach {
            result += Database(it)
        }
        return result
    }
}

private const val PATH_SEPARATOR = "://"

private class ManifestDbConfig(
        context: Context,
        manifestDatabaseName: String
) {
    val customPath: String?
    val databaseName: String
    val databasePath: String
    var error: String? = null

    init {
        val separatorIndex = manifestDatabaseName.indexOf(PATH_SEPARATOR)
        if (separatorIndex >= 0) {
            customPath = manifestDatabaseName.substring(0, separatorIndex)
            databaseName = manifestDatabaseName.substring(separatorIndex + PATH_SEPARATOR.length)

            if (customPath == "no_backup") {
                databasePath = File(context.noBackupFilesDir, databaseName).absolutePath
            } else {
                databasePath = ""
                error = if (customPath.isBlank())
                    "Custom path should not be empty"
                else "Unsupported custom path: $customPath"

            }
        } else {
            customPath = null
            databaseName = manifestDatabaseName
            databasePath = manifestDatabaseName
        }

    }

    fun toJsonDatabase() = Database(databasePath, title = databaseName, description = "Set in manifest", error = error)
}
