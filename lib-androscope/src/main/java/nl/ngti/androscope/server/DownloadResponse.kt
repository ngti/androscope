package nl.ngti.androscope.server

import android.util.Log
import fi.iki.elonen.NanoHTTPD
import nl.ngti.androscope.utils.getRootFile
import java.io.FileInputStream

class DownloadResponse : BaseAndroscopeResponse() {

    override fun getResponse(session: SessionParams): NanoHTTPD.Response? {
        Log.d("DownloadResponse", "Download response " + session["path"]);

        val file = getRootFile(context, session)

        val mime = "application/octet-stream"
        val response = NanoHTTPD.newChunkedResponse(NanoHTTPD.Response.Status.OK, mime, FileInputStream(file))
        response.addHeader("Content-Disposition", "filename=\"" + file.name + "\"")
        return response
    }
}