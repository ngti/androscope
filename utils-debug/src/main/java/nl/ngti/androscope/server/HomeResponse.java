package nl.ngti.androscope.server;

import androidx.annotation.Nullable;

import nl.ngti.androscope.menu.MenuItem;

public class HomeResponse extends BaseHtmlResponse {

    @Nullable
    @Override
    protected MenuItem getMenuItem() {
        return new MenuItem("Home", "/");
    }

    @Override
    protected void onProvideContent(StringBuilder content) {
        content.append("Home");
    }
}
