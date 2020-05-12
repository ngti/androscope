package nl.ngti.androscope.common

import android.content.UriMatcher
import androidx.core.net.toUri

internal open class UrlMatcher<T> {

    private val matcher = UriMatcher(UriMatcher.NO_MATCH)
    private val handlers = ArrayList<T>()

    /**
     * Adds an [handler] for the specified [path]. The same rules apply as in
     * [UriMatcher.addURI].
     */
    fun add(rootPath: String, path: String?, handler: T) {
        require(rootPath.isNotBlank()) { "Invalid root path: $rootPath" }

        val code = handlers.size
        matcher.addURI(rootPath, path, code)
        handlers += handler
    }

    /**
     * Returns a handler associated with this uri (if available).
     */
    operator fun get(url: String): T? {
        val code = matcher.match(url.toUri())
        if (code == -1) {
            return null
        }
        return handlers[code]
    }
}
