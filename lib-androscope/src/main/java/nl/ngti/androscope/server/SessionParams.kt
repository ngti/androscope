package nl.ngti.androscope.server

import fi.iki.elonen.NanoHTTPD

typealias SessionParams = NanoHTTPD.IHTTPSession

val SessionParams.path: String
    get() = uri

operator fun SessionParams.get(key: String): String? =
        parameters[key]?.get(0)
