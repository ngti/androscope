package nl.ngti.androscope.responses.common

import android.webkit.MimeTypeMap
import fi.iki.elonen.NanoHTTPD
import java.io.File
import java.io.FileInputStream

private const val EXTENSION_SEPARATOR = '.'
private const val EMPTY_EXTENSION = ""

internal fun getExtension(fileName: String) = fileName.substringAfterLast(EXTENSION_SEPARATOR, EMPTY_EXTENSION)

internal fun indexOfExtension(fileName: String) = fileName.lastIndexOf(EXTENSION_SEPARATOR)

internal fun getMimeType(fileName: String): String? {
    val extension = getExtension(fileName)
    var mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension)
    if (mimeType == null) {
        // Might not be available in MimeTypeMap
        if ("js" == extension) {
            mimeType = "application/javascript"
        }
    }
    return mimeType
}

internal val File.mimeType: String?
    get() = getMimeType(name)

internal fun File.toDownloadResponse(): NanoHTTPD.Response =
        NanoHTTPD.newChunkedResponse(
                NanoHTTPD.Response.Status.OK,
                "application/octet-stream",
                FileInputStream(this)).apply {
            addHeader("Content-Disposition", "filename=\"$name\"")
        }
