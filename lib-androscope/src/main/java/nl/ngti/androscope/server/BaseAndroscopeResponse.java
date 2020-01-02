package nl.ngti.androscope.server;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.IOException;

import fi.iki.elonen.NanoHTTPD;

public abstract class BaseAndroscopeResponse {

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

    @Nullable
    protected abstract NanoHTTPD.Response getResponse(SessionParams session) throws IOException;
}
