package nl.ngti.androscope.server;

public class EmptyResponse extends BaseHtmlResponse {

    @Override
    protected void onProvideContent(StringBuilder content) {
        content.append("Empty");
    }
}
