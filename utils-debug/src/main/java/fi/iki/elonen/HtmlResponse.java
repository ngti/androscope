package fi.iki.elonen;

/**
 * Creates a http response and adds a link in the header of the main page.
 */
public interface HtmlResponse {

    /**
     * Add here the link to generate this http response.
     *
     * @param session the http session
     * @param html the html of the main page
     */
    void showHtmlHeader(NanoHTTPD.IHTTPSession session, StringBuilder html);

    /**
     * Create a http response to show when accessing the link added in {@link #showHtmlHeader(NanoHTTPD.IHTTPSession, StringBuilder)}
     *
     * @param session the http session
     * @return the response to show in the browser, if null nothing is done.
     */
    NanoHTTPD.Response getResponse(NanoHTTPD.IHTTPSession session);

}
