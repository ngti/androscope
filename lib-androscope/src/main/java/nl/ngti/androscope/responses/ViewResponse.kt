package nl.ngti.androscope.responses

import android.content.Context
import fi.iki.elonen.NanoHTTPD
import nl.ngti.androscope.responses.files.FileSystemParams
import nl.ngti.androscope.server.SessionParams
import nl.ngti.androscope.utils.getMimeType
import java.io.FileInputStream

class ViewResponse(
        private val context: Context
) : Response {

    override fun invoke(sessionParams: SessionParams): NanoHTTPD.Response? {
        val file = FileSystemParams(sessionParams).getRootFile(context)

        val mime = getMimeType(file)
        return NanoHTTPD.newChunkedResponse(NanoHTTPD.Response.Status.OK, mime, FileInputStream(file)).apply {
            addHeader("Content-Disposition", "filename=\"" + file.name + "\"")
        }
    }
}
