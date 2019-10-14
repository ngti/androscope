package nl.ngti.androscope.server;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;

import java.io.IOException;

import fi.iki.elonen.NanoHTTPD;

abstract class BaseAndroscopeResponse {

    private Context mContext;

    final void init(Context context, Bundle metadata) {
        mContext = context;

        onInit(metadata);
    }

    @NonNull
    protected final Context getContext() {
        return mContext;
    }

    protected void onInit(Bundle metadata) {
        // Override in ancestors if needed
    }

    protected abstract NanoHTTPD.Response getResponse(SessionWrapper session) throws IOException;
}
