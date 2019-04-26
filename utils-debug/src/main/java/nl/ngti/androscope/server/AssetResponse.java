package nl.ngti.androscope.server;

import java.io.IOException;

import fi.iki.elonen.NanoHTTPD;

public class AssetResponse extends BaseResponse {

    @Override
    protected NanoHTTPD.Response getResponse(SessionWrapper session) throws IOException {
        return NanoHTTPD.newChunkedResponse(
                NanoHTTPD.Response.Status.OK, null,
                getContext().getAssets().open(session.getSecondaryPath()));
    }
}
