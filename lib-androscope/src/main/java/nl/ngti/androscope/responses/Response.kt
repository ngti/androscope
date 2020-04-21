package nl.ngti.androscope.responses

import fi.iki.elonen.NanoHTTPD
import nl.ngti.androscope.server.SessionParams

typealias Response = (session: SessionParams) -> NanoHTTPD.Response?
