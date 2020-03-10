package nl.ngti.androscope.utils

import android.content.Context
import android.os.Environment
import android.webkit.MimeTypeMap
import nl.ngti.androscope.server.SessionParams
import org.apache.commons.io.FilenameUtils
import java.io.File

@Suppress("DEPRECATION")
fun resolveFileSystemType(context: Context, session: SessionParams): File {
    val fileSystemType = session["type"]
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

fun getRootFile(context: Context, session: SessionParams): File {
    val dataDir = resolveFileSystemType(context, session)
    return session["path"]?.let {
        File(dataDir, it)
    } ?: dataDir
}

fun getMimeType(file: File): String {
    var type: String? = null
    val extension = FilenameUtils.getExtension(file.name)
    if (extension != null) {
        type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension)
    }
    if (type == null) {
        type = "text/plain"
    }
    return type
}
