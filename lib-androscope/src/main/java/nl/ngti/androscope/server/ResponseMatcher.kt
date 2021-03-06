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
) {

    private val urlMatcher = UrlMatcher<Response>()

    private val jsonConverter = Gson()

    private val assetResponse = AssetResponse(context)

    operator fun get(session: SessionParams) =
            urlMatcher["http:/${session.path}"] ?: assetResponse

    init {
        urlMatcher.build("rest") {
            addSubPath("file-system") {
                FileSystemResponse(context).run {
                    addJson("list", ::getFileList)
                    addJson("count", ::getFileCount)
                    addJson("breadcrumbs", ::getBreadcrumbs)
                    addJson("delete", ::delete)
                    add("view", ::getFileToView)
                    add("download", ::getFileToDownload)
                }
            }

            addSubPath("provider") {
                ProviderResponse(uriDataProvider).run {
                    addJson("info", ::getInfo)
                    addJson("data", ::getData)
                }
            }

            addSubPath("database") {
                DatabaseResponse(context, metadata, uriDataProvider, jsonConverter).run {
                    addJson("list") { getList() }
                    addJson("title", ::getTitle)
                    addJson("info", ::getInfo)
                    addJson("can-query", ::getCanQuery)
                    addJson("execute-sql", ::executeSql)
                    add("download", ::getDatabaseToDownload)
                    addJson("upload", ::uploadDatabase)
                    addJson("sql", ::getSql)
                }
            }

            addSubPath("image-cache") {
                ImageCacheResponse(context, metadata).run {
                    addJson("list") { getList() }
                    addJson("info", ::getInfo)
                    addJson("data", ::getData)
                    add("thumbnail", ::getThumbnail)
                }
            }

            addJson("app-name") { context.applicationName }
        }
    }

    private fun UrlMatcher<Response>.Builder.addJson(path: String, handler: (SessionParams) -> Any?) {
        add(path) {
            val data = handler(it)
            val json = jsonConverter.toJson(data)

//            this@ResponseMatcher.log { "Response: $json" }

            NanoHTTPD.newFixedLengthResponse(json)
        }
    }
}
