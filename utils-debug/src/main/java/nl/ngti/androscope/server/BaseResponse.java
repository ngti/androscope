package nl.ngti.androscope.server;

import android.content.Context;

import androidx.annotation.NonNull;

import java.io.IOException;

import fi.iki.elonen.NanoHTTPD;

public abstract class BaseResponse {

    private Context mContext;

    final void init(Context context) {
        mContext = context;
    }

    @NonNull
    protected final Context getContext() {
        return mContext;
    }

    protected abstract NanoHTTPD.Response getResponse(SessionWrapper session) throws IOException;
}
