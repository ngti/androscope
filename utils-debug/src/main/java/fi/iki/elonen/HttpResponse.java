package fi.iki.elonen;

import android.os.Bundle;
import fi.iki.elonen.menu.Menu;
import fi.iki.elonen.menu.MenuItem;
import java.io.IOException;

/**
 * Creates a http response and adds a link in the header of the main page.
 */
public interface HttpResponse {

    boolean isEnabled(Bundle metadata);

    MenuItem getMenuItem();

    /**
     * Create a http response to show.
     *
     * @param session the http session
     * @return the response to show in the browser, if null nothing is done.
     */
    NanoHTTPD.Response getResponse(NanoHTTPD.IHTTPSession session, Menu menu) throws IOException;

}
