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

internal fun getMainDatabaseFile(file: File): File {
    val fileName = file.name
    AUXILIARY_DB_FILES_SUFFIXES.forEach { suffix ->
        if (fileName.endsWith(suffix)) {
            val parent = file.parentFile
            val mainFileName = file.name.removeSuffix(suffix)
            val mainFile = File(parent, mainFileName)
            return if (mainFile.exists())
                mainFile
            else file
        }
    }
    return file
}

internal fun isAuxiliaryDatabaseFile(file: File): Boolean {
    return file != getMainDatabaseFile(file)
}

internal fun collectAllDatabaseFiles(databaseFile: File): FileBatch {
    val parentDirectory = databaseFile.parentFile!!
    return FileBatch(parentDirectory).also {
        val mainDatabaseFileName = databaseFile.name
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
