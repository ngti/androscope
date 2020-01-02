package nl.ngti.androscope.server;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;

import java.io.IOException;

import fi.iki.elonen.NanoHTTPD;
import nl.ngti.androscope.common.AndroscopeConstants;

final class ResponseFactory {

    private static final boolean LOG = AndroscopeConstants.LOG;
    private static final String TAG = ResponseFactory.class.getSimpleName();

    private final NotFoundResponse mNotFoundResponse = new NotFoundResponse();
    private final AssetResponse mAssetResponse = new AssetResponse();
    private final RestResponse mRestResponse = new RestResponse();

    ResponseFactory(Context context, Bundle metadata) {
        mNotFoundResponse.init(context, metadata);
        mAssetResponse.init(context, metadata);
        mRestResponse.init(context, metadata);
    }

    @Nullable
    NanoHTTPD.Response getResponse(NanoHTTPD.IHTTPSession session) throws IOException {
        final String uri = session.getUri();
        if (LOG) Log.d(TAG, "getResponse " + uri + ", params " + session.getParameters());

        if (uri.startsWith(AndroscopeConstants.PATH_REST)) {
            return mRestResponse.getResponse(new SessionParams(session, AndroscopeConstants.PATH_REST));
        }

        if (uri.startsWith("/")) {
            return mAssetResponse.getResponse(new SessionParams(session, "/"));
        }

        if (LOG) Log.w(TAG, "No response to handle " + uri);
        return mNotFoundResponse.getResponse(new SessionParams(session, "/"));
    }
}
