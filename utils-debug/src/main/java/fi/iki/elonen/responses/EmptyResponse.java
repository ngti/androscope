package fi.iki.elonen.responses;

import android.content.Context;
import android.os.Bundle;
import fi.iki.elonen.NanoHTTPD;
import fi.iki.elonen.menu.MenuItem;

public class EmptyResponse extends BaseMainHtmlResponse {
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
