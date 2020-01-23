package nl.ngti.androscope.server

import fi.iki.elonen.NanoHTTPD

class SessionParams(
        private val nanoHTTPDSession: NanoHTTPD.IHTTPSession
) {

    val path: String
        get() = nanoHTTPDSession.uri

    val root: String
    private val rootPath: String

    init {
        val path = path
        val length = path.length
        var end = length
        for (i in 1 until length) {
            if (path[i] == '/') {
                end = i
                break
            }
        }
        rootPath = path.substring(0, end)
        root = rootPath.substring(1)
    }

    val relativePath: String
        get() =
            if (path.length == rootPath.length) rootPath
            else path.substring(rootPath.length + 1)

    operator fun get(key: String): String? =
            nanoHTTPDSession.parameters[key]?.get(0)
}