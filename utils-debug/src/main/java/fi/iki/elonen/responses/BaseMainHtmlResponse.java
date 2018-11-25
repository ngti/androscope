package fi.iki.elonen.responses;

import android.content.Context;
import android.os.Bundle;
import fi.iki.elonen.NanoHTTPD;
import fi.iki.elonen.menu.Menu;
import fi.iki.elonen.menu.MenuItem;
import fi.iki.elonen.velocity.VelocityAsset;
import java.io.IOException;

public abstract class BaseMainHtmlResponse extends BaseVelocityResponse {

    protected BaseMainHtmlResponse(Context context) {
        super(context);
    }

    public static NanoHTTPD.Response emptyResponse(Context context, NanoHTTPD.IHTTPSession session, Menu menu) throws IOException {
        return new EmptyResponse(context).getResponse(session, menu);
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


    private static class EmptyResponse extends BaseMainHtmlResponse {
        public EmptyResponse(Context context) {
            super(context);
        }

        @Override
        public boolean isEnabled(Bundle metadata) {
            return true;
        }

        @Override
        public MenuItem getMenuItem() {
            return null;
        }

        @Override
        protected String getContent(NanoHTTPD.IHTTPSession session) {
            return "";
        }
    }
}
