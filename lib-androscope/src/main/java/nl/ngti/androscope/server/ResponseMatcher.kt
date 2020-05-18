package nl.ngti.androscope.server

import android.content.Context
import com.google.gson.Gson
import fi.iki.elonen.NanoHTTPD
import nl.ngti.androscope.common.UrlMatcher
import nl.ngti.androscope.responses.AssetResponse
import nl.ngti.androscope.responses.Response
import nl.ngti.androscope.responses.common.MultiSchemeDataProvider
import nl.ngti.androscope.responses.database.DatabaseResponse
import nl.ngti.androscope.responses.files.FileSystemResponse
import nl.ngti.androscope.responses.image_cache.ImageCacheResponse
import nl.ngti.androscope.responses.provider.ProviderResponse
import nl.ngti.androscope.utils.AndroscopeMetadata
import nl.ngti.androscope.utils.applicationName

internal class ResponseMatcher(
        context: Context,
        metadata: AndroscopeMetadata,
        uriDataProvider: MultiSchemeDataProvider
) : UrlMatcher<Response>() {

    private val jsonConverter = Gson()

    val assetResponse = AssetResponse(context)

    init {
        FileSystemResponse(context).apply {
            addJson("file-system/list", ::getFileList)
            addJson("file-system/count", ::getFileCount)
            addJson("file-system/breadcrumbs", ::getBreadcrumbs)
            addJson("file-system/delete", ::delete)
            add("file-system/view", ::getFileToView)
            add("file-system/download", ::getFileToDownload)
        }

        ProviderResponse(uriDataProvider).apply {
            addJson("provider/info", ::getInfo)
            addJson("provider/data", ::getData)
        }

        DatabaseResponse(context, metadata, uriDataProvider, jsonConverter).apply {
            addJson("database/list") { getList() }
            addJson("database/title", ::getTitle)
            addJson("database/info", ::getInfo)
            addJson("database/can-query", ::getCanQuery)
            addJson("database/execute-sql", ::executeSql)
            add("database/download", ::getDatabaseToDownload)
            addJson("database/upload", ::uploadDatabase)
            addJson("database/sql", ::getSql)
        }

        ImageCacheResponse(context, metadata).apply {
            addJson("image-cache/list") { getList() }
            addJson("image-cache/info", ::getInfo)
            addJson("image-cache/data", ::getData)
            add("image-cache/thumbnail", ::getThumbnail)
        }

        addJson("app-name") { context.applicationName }
    }

    private fun add(path: String, handler: Response) {
        add("rest", path, handler)
    }

    private fun addJson(path: String, handler: (SessionParams) -> Any?) {
        add("rest", path) {
            val data = handler(it)
            val json = jsonConverter.toJson(data)

//            log { "Response: $json" }

            NanoHTTPD.newFixedLengthResponse(json)
        }
    }
}
