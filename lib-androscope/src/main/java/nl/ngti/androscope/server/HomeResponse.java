package nl.ngti.androscope.server;

public class HomeResponse extends BaseHtmlResponse {

    @Override
    protected void onProvideContent(StringBuilder content) {
        content.append("Home");
    }
}
