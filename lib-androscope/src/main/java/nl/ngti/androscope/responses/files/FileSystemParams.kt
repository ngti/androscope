package nl.ngti.androscope.responses.files

import android.content.Context
import android.net.Uri
import android.os.Environment
import nl.ngti.androscope.server.SessionParams
import nl.ngti.androscope.server.get
import nl.ngti.androscope.server.timestamp
import java.io.File

data class FileSystemParams(
        private val fileSystemType: String,
        val path: String?,
        private val timestamp: Long
) {

    constructor(session: SessionParams) : this(
            fileSystemType = session["type"]!!,
            path = Uri.decode(session["path"]),
            timestamp = session.timestamp
    )

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

    fun getFile(context: Context): File {
        val dataDir = resolveFileSystemType(context)
        return if (path.isNullOrEmpty()) dataDir else File(dataDir, path)
    }
}
