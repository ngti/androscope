package nl.ngti.androscope.responses.database

import android.content.Context
import android.os.Build
import java.io.File

private const val PATH_SEPARATOR = "://"

class DbConfig(
        private val context: Context,
        val databaseName: String
) {
    private val customPath: String?
    private val databaseFileName: String

    val databasePath: String
    val databaseFile: File
        get() = context.getDatabasePath(databasePath)
    val name get() = customPath?.let { "$databaseFileName ($customPath)" } ?: databaseFileName

    var errorMessage: String? = null
        private set

    init {
        val separatorIndex = databaseName.indexOf(PATH_SEPARATOR)
        if (separatorIndex >= 0) {
            customPath = databaseName.substring(0, separatorIndex)
            databaseFileName = databaseName.substring(separatorIndex + PATH_SEPARATOR.length)

            if (customPath == "no_backup" && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                databasePath = File(context.noBackupFilesDir, databaseFileName).absolutePath
            } else {
                databasePath = ""
                errorMessage = if (customPath.isBlank())
                    "Custom path should not be empty"
                else "Unsupported custom path: $customPath"

            }
        } else {
            customPath = null
            databaseFileName = databaseName
            databasePath = databaseName
        }

    }
}
