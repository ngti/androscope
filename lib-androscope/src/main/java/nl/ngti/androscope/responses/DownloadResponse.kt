package nl.ngti.androscope.responses

import android.content.Context
import fi.iki.elonen.NanoHTTPD
import nl.ngti.androscope.responses.files.FileSystemParams
import nl.ngti.androscope.server.SessionParams
import java.io.FileInputStream

class DownloadResponse(
        private val context: Context
) : Response {

    override fun invoke(session: SessionParams): NanoHTTPD.Response? {
        val file = FileSystemParams(session).getRootFile(context)

        val mime = "application/octet-stream"
        val response = NanoHTTPD.newChunkedResponse(NanoHTTPD.Response.Status.OK, mime, FileInputStream(file))
        response.addHeader("Content-Disposition", "filename=\"" + file.name + "\"")
        return response
    }
}
