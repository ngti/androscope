package nl.ngti.androscope.responses.database

import java.io.File

/**
 * The list of possible extensions was taken from:
 * [android.database.sqlite.SQLiteDatabase.deleteDatabase(java.io.File)]
 */
private val AUXILIARY_DB_FILES_SUFFIXES = arrayOf(
        "-journal",
        "-shm",
        "-wal"
)

internal val File.mainDatabaseFile: File
    get() {
        val fileName = name
        AUXILIARY_DB_FILES_SUFFIXES.forEach { suffix ->
            if (fileName.endsWith(suffix)) {
                val parent = parentFile
                val mainFileName = fileName.removeSuffix(suffix)
                val mainFile = File(parent, mainFileName)
                return if (mainFile.exists())
                    mainFile
                else this
            }
        }
        return this
    }

internal val File.isAuxiliaryDatabaseFile: Boolean
    get() = this != mainDatabaseFile

internal fun File.collectAllDatabaseFiles(): FileBatch {
    val parentDirectory = parentFile!!
    return FileBatch(parentDirectory).also {
        val mainDatabaseFileName = name
        it += mainDatabaseFileName
        AUXILIARY_DB_FILES_SUFFIXES.forEach { suffix ->
            val auxiliaryFileName = mainDatabaseFileName + suffix
            val file = File(parentDirectory, auxiliaryFileName)
            if (file.exists()) {
                it += auxiliaryFileName
            }
        }
    }
}
