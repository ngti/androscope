package nl.ngti.androscope.server;

import android.util.Log;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import fi.iki.elonen.NanoHTTPD;
import nl.ngti.androscope.common.AndroscopeConstants;
import nl.ngti.androscope.utils.AppUtils;

public final class AssetResponse extends BaseAndroscopeResponse {

    private static final boolean LOG = AndroscopeConstants.LOG;
    private static final String TAG = AssetResponse.class.getSimpleName();

    @Override
    public NanoHTTPD.Response getResponse(SessionParams session) throws IOException {
        final String assetToOpen = AndroscopeConstants.WEB_CONTENT_ROOT + session.getPath();

        InputStream inputStream;
        String mimeType;
        try {
            inputStream = getContext().getAssets().open(assetToOpen);
            mimeType = AppUtils.getMimeType(assetToOpen);
        } catch (FileNotFoundException ignore) {
            inputStream = getContext().getAssets().open(AndroscopeConstants.HOME_PAGE);
            mimeType = "text/html";
        }

        if (LOG) Log.d(TAG, "assetToOpen = " + assetToOpen + ", mimeType = " + mimeType);

        return NanoHTTPD.newChunkedResponse(
                NanoHTTPD.Response.Status.OK, mimeType,
                inputStream);
    }
}
