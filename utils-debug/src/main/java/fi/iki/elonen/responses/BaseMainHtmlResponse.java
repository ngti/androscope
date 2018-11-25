package fi.iki.elonen.responses;

import android.content.Context;
import fi.iki.elonen.NanoHTTPD;
import fi.iki.elonen.menu.Menu;
import fi.iki.elonen.velocity.VelocityAsset;

public abstract class BaseMainHtmlResponse extends BaseVelocityResponse {

    protected BaseMainHtmlResponse(Context context) {
        super(context);
    }

    @Override
    protected String getVelocityAsset() {
        return "main.html";
    }

    @Override
    protected void prepareVelocityVariables(VelocityAsset v, NanoHTTPD.IHTTPSession session, Menu menu) {
        v.put("header", menu.render());
        v.put("content", getContent(session));
    }

    protected abstract String getContent(NanoHTTPD.IHTTPSession session);


}
