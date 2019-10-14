package nl.ngti.androscope.server;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;

import fi.iki.elonen.NanoHTTPD;
import nl.ngti.androscope.common.AndroscopeConstants;

final class ResponseFactory {

    private static final boolean LOG = AndroscopeConstants.LOG;
    private static final String TAG = ResponseFactory.class.getSimpleName();

    private final NotFoundResponse mNotFoundResponse = new NotFoundResponse();
    private final AssetResponse mAssetResponse = new AssetResponse();

    ResponseFactory(Context context, Bundle metadata) {
        mNotFoundResponse.init(context, metadata);
        mAssetResponse.init(context, metadata);
    }

    @NonNull
    BaseAndroscopeResponse getResponse(NanoHTTPD.IHTTPSession session) {
        final String uri = session.getUri();
        if (LOG) Log.d(TAG, "getResponse " + uri);

        if (uri.startsWith("/")) {
            return mAssetResponse;
        }

        if (LOG) Log.w(TAG, "No response to handle " + uri);
        return mNotFoundResponse;
    }
}
