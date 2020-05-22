package nl.ngti.androscope.common

import android.content.UriMatcher
import androidx.core.net.toUri

internal class UrlMatcher<T> {

    private val matcher = UriMatcher(UriMatcher.NO_MATCH)
    private val handlers = ArrayList<T>()

    fun build(rootPath: String, block: Builder.() -> Unit) {
        require(rootPath.isNotBlank()) { "Invalid root path: $rootPath" }
        require(!rootPath.contains('/')) { "Root path cannot contain any '/'" }

        val builder = Builder(rootPath)
        block(builder)
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

    /**
     * Adds an [handler] for the specified [path]. The same rules apply as in
     * [UriMatcher.addURI].
     */
    private fun add(rootPath: String, path: String?, handler: T) {
        val code = handlers.size
        matcher.addURI(rootPath, path, code)
        handlers += handler
    }

    internal inner class Builder(
            private val rootPath: String,
            private val parentPath: String = ""
    ) {

        fun addSubPath(path: String, block: Builder.() -> Unit) {
            require(!path.contains('/')) {
                "Path must not contain any '/'"
            }
            val subTreeBuilder = Builder(rootPath, "$parentPath$path/")
            block(subTreeBuilder)
        }

        fun add(path: String, handler: T) {
            require(!path.contains('/')) {
                "Use addSubPath method to create urls with multiple sub-paths"
            }
            add(rootPath, "$parentPath$path", handler)
        }
    }
}
