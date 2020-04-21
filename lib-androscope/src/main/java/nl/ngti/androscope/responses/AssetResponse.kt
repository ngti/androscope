package nl.ngti.androscope.responses

import android.content.Context
import fi.iki.elonen.NanoHTTPD
import nl.ngti.androscope.common.log
import nl.ngti.androscope.server.SessionParams
import nl.ngti.androscope.server.path
import nl.ngti.androscope.utils.AppUtils
import java.io.FileNotFoundException
import java.io.InputStream

private const val WEB_CONTENT_ROOT = "www"
private const val HOME_PAGE = "$WEB_CONTENT_ROOT/index.html"

class AssetResponse(
        private val context: Context
) : Response {

    override fun invoke(sessionParams: SessionParams): NanoHTTPD.Response? {
        val assetToOpen = WEB_CONTENT_ROOT + sessionParams.path
        var inputStream: InputStream
        var mimeType: String?
        try {
            inputStream = context.assets.open(assetToOpen)
            mimeType = AppUtils.getMimeType(assetToOpen)
        } catch (ignore: FileNotFoundException) {
            inputStream = context.assets.open(HOME_PAGE)
            mimeType = "text/html"
        }
        log { "assetToOpen = $assetToOpen, mimeType = $mimeType" }
        return NanoHTTPD.newChunkedResponse(
                NanoHTTPD.Response.Status.OK, mimeType,
                inputStream)
    }
}
