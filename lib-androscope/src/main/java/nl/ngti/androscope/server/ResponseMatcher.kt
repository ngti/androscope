package nl.ngti.androscope.server

import android.content.Context
import com.google.gson.Gson
import fi.iki.elonen.NanoHTTPD
import nl.ngti.androscope.common.UrlMatcher
import nl.ngti.androscope.responses.AssetResponse
import nl.ngti.androscope.responses.DownloadResponse
import nl.ngti.androscope.responses.Response
import nl.ngti.androscope.responses.ViewResponse
import nl.ngti.androscope.responses.common.MultiSchemeDataProvider
import nl.ngti.androscope.responses.database.DatabaseResponse
import nl.ngti.androscope.responses.files.FileSystemResponse
import nl.ngti.androscope.responses.provider.ProviderResponse
import nl.ngti.androscope.utils.AndroscopeMetadata

internal class ResponseMatcher(
        context: Context,
        metadata: AndroscopeMetadata,
        uriDataProvider: MultiSchemeDataProvider
) : UrlMatcher<Response>() {

    private val jsonConverter = Gson()

    init {
        add("view", ViewResponse(context))
        add("download", DownloadResponse(context))

        FileSystemResponse(context).apply {
            addRest("file-system/list", ::getFileList)
            addRest("file-system/count", ::getFileCount)
            addRest("file-system/breadcrumbs", ::getBreadcrumbs)
            addRest("file-system/delete", ::delete)
        }

        ProviderResponse(uriDataProvider).apply {
            addRest("provider/info", ::getInfo)
            addRest("provider/data", ::getData)
        }

        DatabaseResponse(context, metadata, uriDataProvider).apply {
            addRest("database/list") { getList() }
            addRest("database/info", ::getInfo)
            //addRest("database/download")
            //addRest("database/upload")
        }

        add("*", AssetResponse(context))
    }

    private fun addRest(path: String, handler: (SessionParams) -> Any?) {
        add("rest", path) {
            val data = handler(it)
            val json = jsonConverter.toJson(data)

//            log { "Response: $json" }

            NanoHTTPD.newFixedLengthResponse(json)
        }
    }

    private fun add(rootPath: String, handler: Response) {
        add(rootPath, null, handler)
    }
}
