package nl.ngti.androscope.utils

import android.webkit.MimeTypeMap
import org.apache.commons.io.FilenameUtils
import java.io.File

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
