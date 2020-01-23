package nl.ngti.androscope.utils

import android.content.Context
import android.webkit.MimeTypeMap
import nl.ngti.androscope.server.SessionParams
import org.apache.commons.io.FilenameUtils
import java.io.File

fun getRootFile(context: Context, session: SessionParams): File {
    val dataDir = context.applicationInfo.dataDir
    return session["path"]?.let {
        File(dataDir, it)
    } ?: File(dataDir)
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