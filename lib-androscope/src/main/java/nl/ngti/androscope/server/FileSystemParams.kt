package nl.ngti.androscope.server

import android.content.Context
import android.os.Environment
import java.io.File

data class FileSystemParams(
        val fileSystemType: String,
        val path: String?
) {

    constructor(session: SessionParams) : this(session["type"]!!, session["path"])

    @Suppress("DEPRECATION")
    fun resolveFileSystemType(context: Context): File {
        return when (fileSystemType) {
            "application-data" -> File(context.applicationInfo.dataDir)
            "external-storage" -> Environment.getExternalStorageDirectory()
            "phone-root" -> Environment.getRootDirectory()
            "downloads" -> Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            "photos" -> Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)
            "movies" -> Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES)
            "pictures" -> Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
            "music" -> Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC)
            else -> throw IllegalArgumentException("Unsupported file system type: $fileSystemType")
        } ?: throw IllegalArgumentException("Null File returned for $fileSystemType")
    }

    fun getRootFile(context: Context): File {
        val dataDir = resolveFileSystemType(context)
        return path?.let {
            File(dataDir, it)
        } ?: dataDir
    }
}
