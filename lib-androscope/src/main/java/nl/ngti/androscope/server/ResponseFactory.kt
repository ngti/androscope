package nl.ngti.androscope.server

import android.content.Context
import android.os.Bundle
import android.util.Log
import fi.iki.elonen.NanoHTTPD
import fi.iki.elonen.NanoHTTPD.IHTTPSession
import nl.ngti.androscope.common.LOG
import nl.ngti.androscope.common.PATH_DOWNLOAD
import nl.ngti.androscope.common.PATH_REST
import nl.ngti.androscope.common.PATH_VIEW

private val TAG = ResponseFactory::class.java.simpleName

internal class ResponseFactory(
        private val context: Context,
        private val metadata: Bundle
) {

    private val cachedResponses = HashMap<String, BaseAndroscopeResponse>()

    fun getResponse(session: IHTTPSession): NanoHTTPD.Response? {
        val sessionParams = SessionParams(session)
        if (LOG) Log.d(TAG, "getResponse " + sessionParams.path + ", params " + session.parameters + ", root " + sessionParams.root)
        return getCachedResponse(sessionParams.root).getResponse(sessionParams)
    }

    private fun getCachedResponse(root: String) =
            cachedResponses.getOrPut(root) {
                getResponse(root).also {
                    it.init(context, metadata)
                }
            }

    private fun getResponse(root: String): BaseAndroscopeResponse =
            when (root) {
                PATH_REST -> RestResponse()
                PATH_VIEW -> ViewResponse()
                PATH_DOWNLOAD -> DownloadResponse()
                else -> AssetResponse()
            }
}