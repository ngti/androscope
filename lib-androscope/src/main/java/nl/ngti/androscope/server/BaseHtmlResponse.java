package nl.ngti.androscope.server;

import java.io.IOException;

import fi.iki.elonen.NanoHTTPD;

public abstract class BaseHtmlResponse extends BaseResponse {

    @Override
    protected final NanoHTTPD.Response getResponse(SessionWrapper session) throws IOException {
        final StringBuilder content = new StringBuilder();
        onProvideContent(content);

        return NanoHTTPD.newFixedLengthResponse(content.toString());
    }

    protected abstract void onProvideContent(StringBuilder content);
}
