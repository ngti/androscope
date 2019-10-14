package nl.ngti.androscope.server;

import android.util.Log;
import android.webkit.MimeTypeMap;

import org.apache.commons.io.FilenameUtils;

import java.io.IOException;

import fi.iki.elonen.NanoHTTPD;

public class AssetResponse extends BaseResponse {

    @Override
    protected NanoHTTPD.Response getResponse(SessionWrapper session) throws IOException {
        String assetToOpen;

        if (session.getRootPath().isEmpty()) {
            assetToOpen = "index.html";
        } else {
            assetToOpen = session.getRootPath();
        }

        final String extension = FilenameUtils.getExtension(assetToOpen);

        String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);

        if (mimeType == null) {
            if ("js".equals(extension)) {
                mimeType = "application/javascript";
            }
        }

        Log.d("AssetResponse", "assetToOpen = " + assetToOpen + ", mimeType = " + mimeType);

        assetToOpen = "www/" + assetToOpen;

        return NanoHTTPD.newChunkedResponse(
                NanoHTTPD.Response.Status.OK, mimeType,
                getContext().getAssets().open(assetToOpen));
    }
}
