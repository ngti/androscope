package nl.ngti.androscope.server;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import java.util.Map;

import fi.iki.elonen.NanoHTTPD;

public final class ResponseFactory {

    private final Context mContext;
//    private final HashMap<String, BaseResponse> mResponses = new HashMap<>();

    private final EmptyResponse mEmptyResponse = new EmptyResponse();
    private final AssetResponse mAssetResponse = new AssetResponse();

    public ResponseFactory(Context context) {
        mContext = context;

        mEmptyResponse.init(context);
        mAssetResponse.init(context);

//        addResponses(mResponses);
//        mResponses.put(null, mEmptyResponse);

//        for (BaseResponse response : mResponses.values()) {
//            response.init(mContext);
//        }
    }

    private static void addResponses(Map<String, BaseResponse> responses) {
        // HTML responses
        responses.put("", new AssetResponse());

        // System responses
        responses.put("asset", new AssetResponse());
    }

    @NonNull
    public BaseResponse getResponse(NanoHTTPD.IHTTPSession session) {
        final String uri = session.getUri();
        Log.d("ResponseFactory", "getResponse " + uri);
        if (uri.startsWith("/")) {
            return mAssetResponse;
        }
        Log.w("ResponseFactory", "No response to handle " + uri);
        return mEmptyResponse;
    }

//    @NonNull
//    public BaseResponse getResponse(String path) {
//        Log.d("ResponseFactory", "getResponse " + path);
//        BaseResponse response = mResponses.get(path);
//        if (response == null) {
//            response = mEmptyResponse;
//        }
//        return response;
//    }
}
