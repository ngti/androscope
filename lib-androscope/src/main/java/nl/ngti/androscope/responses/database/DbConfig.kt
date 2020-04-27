package nl.ngti.androscope.responses.database

import android.content.Context
import java.io.File

private const val PATH_SEPARATOR = "://"

class DbConfig(
        context: Context,
        private val databaseName: String
) {
    private val customPath: String?
    private val databaseFileName: String
    val databasePath: String
    val name get() = customPath?.let { "$databaseFileName ($customPath)" } ?: databaseFileName
    private var error: String? = null

    init {
        val separatorIndex = databaseName.indexOf(PATH_SEPARATOR)
        if (separatorIndex >= 0) {
            customPath = databaseName.substring(0, separatorIndex)
            databaseFileName = databaseName.substring(separatorIndex + PATH_SEPARATOR.length)

            if (customPath == "no_backup") {
                databasePath = File(context.noBackupFilesDir, databaseFileName).absolutePath
            } else {
                databasePath = ""
                error = if (customPath.isBlank())
                    "Custom path should not be empty"
                else "Unsupported custom path: $customPath"

            }
        } else {
            customPath = null
            databaseFileName = databaseName
            databasePath = databaseName
        }

    }

    fun toJsonDatabase() = Database(
            databaseName,
            title = name,
            description = "Set in manifest",
            error = error
    )
}
