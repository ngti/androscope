package nl.ngti.androscope.server

import android.content.Context
import android.os.Bundle
import android.os.SystemClock
import com.google.gson.Gson
import fi.iki.elonen.NanoHTTPD
import fi.iki.elonen.NanoHTTPD.IHTTPSession
import nl.ngti.androscope.common.log
import nl.ngti.androscope.responses.AssetResponse
import nl.ngti.androscope.responses.DownloadResponse
import nl.ngti.androscope.responses.Response
import nl.ngti.androscope.responses.ViewResponse
import nl.ngti.androscope.responses.database.DatabaseResponse
import nl.ngti.androscope.responses.files.FileSystemResponse
import nl.ngti.androscope.responses.provider.ProviderResponse

internal class ResponseFactory(
        private val context: Context,
        private val metadata: Bundle
) {

    private val gson = Gson()

    private val urlMatcher = UrlMatcher<Response>().apply {
        add("view", ViewResponse(context))
        add("download", DownloadResponse(context))

        FileSystemResponse(context).apply {
            addRest("file-system/list", ::getFileList)
            addRest("file-system/count", ::getFileCount)
            addRest("file-system/breadcrumbs", ::getBreadcrumbs)
            addRest("file-system/delete", ::delete)
        }

        ProviderResponse(context).apply {
            addRest("provider/metadata", ::getMetadata)
            addRest("provider/data", ::getData)
        }

        DatabaseResponse(context, metadata).apply {
            addRest("database/list") { getList() }
        }

        add("*", AssetResponse(context))
    }

    fun getResponse(session: IHTTPSession): NanoHTTPD.Response? {
        log {
            """getResponse ${session.path},
                | params ${session.parameters}""".trimMargin()
        }
        val handler = urlMatcher["http:/${session.path}"]
        if (handler == null) {
            log { "Unknown path: ${session.path}" }
            return null
        }

        val start = SystemClock.elapsedRealtimeNanos()
        try {
            return handler(session)
        } finally {
            log("Response generation time [%s]: %,d ns",
                    session.path, SystemClock.elapsedRealtimeNanos() - start)
        }
    }

    private fun UrlMatcher<Response>.addRest(path: String, handler: (SessionParams) -> Any?) {
        add("rest", path) {
            val data = handler(it)
            val json = gson.toJson(data)

//            log { "Response: $json" }

            NanoHTTPD.newFixedLengthResponse(json)
        }
    }

    private fun UrlMatcher<Response>.add(rootPath: String, handler: Response) {
        add(rootPath, null, handler)
    }
}
