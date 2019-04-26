package nl.ngti.androscope.server;

import androidx.annotation.Nullable;

import java.io.IOException;

import fi.iki.elonen.NanoHTTPD;
import nl.ngti.androscope.menu.Menu;
import nl.ngti.androscope.menu.MenuItem;
import nl.ngti.androscope.velocity.VelocityAsset;

public abstract class BaseHtmlResponse extends BaseResponse {

    private Menu mMenu;

    final void setMenu(Menu menu) {
        mMenu = menu;
    }

    @Override
    protected final NanoHTTPD.Response getResponse(SessionWrapper session) throws IOException {
        final VelocityAsset velocityAsset = new VelocityAsset();
        velocityAsset.initAsset(getContext(), "main.html");

        velocityAsset.put("header", mMenu.render());

        final StringBuilder content = new StringBuilder();
        onProvideContent(content);
        velocityAsset.put("content", content.toString());

        return NanoHTTPD.newFixedLengthResponse(velocityAsset.html());
    }

    @Nullable
    protected abstract MenuItem getMenuItem();

    protected abstract void onProvideContent(StringBuilder content);
}
