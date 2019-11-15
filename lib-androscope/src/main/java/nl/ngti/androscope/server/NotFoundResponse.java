package nl.ngti.androscope.server;

import fi.iki.elonen.NanoHTTPD;

final class NotFoundResponse extends BaseAndroscopeResponse {

    @Override
    protected NanoHTTPD.Response getResponse(NanoHTTPD.IHTTPSession session) {
        return NanoHTTPD.newFixedLengthResponse(NanoHTTPD.Response.Status.NOT_FOUND, NanoHTTPD.MIME_PLAINTEXT, "");
    }
}
