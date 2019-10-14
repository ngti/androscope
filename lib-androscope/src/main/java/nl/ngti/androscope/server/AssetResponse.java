package nl.ngti.androscope.server;

import android.util.Log;

import java.io.IOException;

import fi.iki.elonen.NanoHTTPD;
import nl.ngti.androscope.common.AndroscopeConstants;
import nl.ngti.androscope.utils.AppUtils;

public final class AssetResponse extends BaseAndroscopeResponse {

    private static final boolean LOG = AndroscopeConstants.LOG;
    private static final String TAG = AssetResponse.class.getSimpleName();

    @Override
    protected NanoHTTPD.Response getResponse(SessionWrapper session) throws IOException {
        String assetToOpen;

        if (session.getRootPath().isEmpty()) {
            assetToOpen = AndroscopeConstants.HOME_PAGE;
        } else {
            assetToOpen = session.getRootPath();
        }

        final String mimeType = AppUtils.getMimeType(assetToOpen);

        if (LOG) Log.d(TAG, "assetToOpen = " + assetToOpen + ", mimeType = " + mimeType);

        assetToOpen = AndroscopeConstants.WEB_CONTENT_ROOT + assetToOpen;

        return NanoHTTPD.newChunkedResponse(
                NanoHTTPD.Response.Status.OK, mimeType,
                getContext().getAssets().open(assetToOpen));
    }
}
