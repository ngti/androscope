package nl.ngti.androscope.responses;

import android.content.Context;

import java.io.IOException;

import fi.iki.elonen.NanoHTTPD;
import nl.ngti.androscope.menu.Menu;
import nl.ngti.androscope.velocity.VelocityAsset;

public abstract class BaseVelocityResponse implements HttpResponse {

    private final Context mContext;

    protected BaseVelocityResponse(Context context) {
        mContext = context;
    }

    @Override
    public final NanoHTTPD.Response getResponse(NanoHTTPD.IHTTPSession session, Menu menu) throws IOException {
        VelocityAsset v = new VelocityAsset();
        v.initAsset(mContext, getVelocityAsset());

        prepareVelocityVariables(v, session, menu);

        return NanoHTTPD.newFixedLengthResponse(v.html());
    }

    protected void prepareVelocityVariables(VelocityAsset v, NanoHTTPD.IHTTPSession session, Menu menu) {

    }

    protected abstract String getVelocityAsset();

    protected Context getContext() {
        return mContext;
    }
}
