package nl.ngti.androscope.server

import fi.iki.elonen.NanoHTTPD

class SessionParams(
        private val nanoHTTPDSession: NanoHTTPD.IHTTPSession,
        private val basePath: String
) {

    val path: String
        get() = nanoHTTPDSession.uri

    val relativePath: String
        get() = path.substringAfter(basePath)

    operator fun get(key: String): String? =
            nanoHTTPDSession.parameters[key]?.get(0)
}