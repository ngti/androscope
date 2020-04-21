package nl.ngti.androscope.responses

import fi.iki.elonen.NanoHTTPD
import nl.ngti.androscope.server.SessionParams

typealias Response = (sessionParams: SessionParams) -> NanoHTTPD.Response?
