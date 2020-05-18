package nl.ngti.androscope.server

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.os.SystemClock
import fi.iki.elonen.NanoHTTPD
import fi.iki.elonen.NanoHTTPD.IHTTPSession
import nl.ngti.androscope.common.log
import nl.ngti.androscope.responses.common.MultiSchemeDataProvider
import nl.ngti.androscope.responses.common.UriDataProvider
import nl.ngti.androscope.utils.AndroscopeMetadata

internal class ResponseFactory(
        context: Context,
        metadata: AndroscopeMetadata
) {

    private val uriDataProvider = MultiSchemeDataProvider().apply {
        addProvider(ContentResolver.SCHEME_CONTENT, ContentResolverDataProvider(context.contentResolver))
    }

    private val urlMatcher = ResponseMatcher(context, metadata, uriDataProvider)

    fun getResponse(session: IHTTPSession): NanoHTTPD.Response? {
        log {
            """getResponse
                | path ${session.path},
                | params ${session.parameters}""".trimMargin()
        }
        val handler = urlMatcher[session]

        val start = SystemClock.elapsedRealtime()
        try {
            return handler(session)
        } finally {
            log("Response generation time [%s]: %,d ms",
                    session.path, SystemClock.elapsedRealtime() - start)
        }
    }
}

private class ContentResolverDataProvider(
        private val contentResolver: ContentResolver
) : UriDataProvider {

    override fun query(
            uri: Uri,
            projection: Array<String>?,
            selection: String?,
            selectionArgs: Array<String>?,
            sortOrder: String?
    ) = contentResolver.query(uri, projection, selection, selectionArgs, sortOrder)
}
